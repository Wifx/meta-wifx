SUMMARY = "LORIX One clouds manager"
DESCRIPTION = "Startup script which manages the various availlable clouds software of the LORIX One"

LICENSE = "WIFX"
LIC_FILES_CHKSUM = "file://${WORKDIR}/LICENSE;md5=323e5da3c7dfd45dac56506af122354f"

SRC_URI = " \
	file://LICENSE \
	file://init \
	file://clouds-manager.sh \
	file://config \
	"

DEPENDS += ""
RDEPENDS_${PN} += "reset-lgw loriot packet-forwarder ttn-packet-forwarder "

inherit update-rc.d

CONFIGDIR="${sysconfdir}/lorix"
CONFIGFILE_NAME = "clouds.conf"
CONFIGFILE = "${CONFIGDIR}/${CONFIGFILE_NAME}"

BKPDIR="${sysconfdir}/backup.d${CONFIGDIR}"
BKPFILE="${BKPDIR}/${CONFIGFILE_NAME}"

INITSCRIPT_NAME = "init-clouds-manager"
INITSCRIPT = "${sysconfdir}/init.d/${INITSCRIPT_NAME}"
INITSCRIPT_PARAMS = "start 02 2 3 4 5 . stop 71 0 1 6 ."

RUNNING_FILE = "/var/tmp/${INITSCRIPT_NAME}_is_running"

do_install () {
	# init script
	install -d ${D}${sysconfdir}/init.d
	# base script file
	install -m 0755 ${WORKDIR}/clouds-manager.sh ${D}${sysconfdir}/init.d/clouds-manager.sh \
	# init wrapper
	install -m 0744 ${WORKDIR}/init ${D}/${INITSCRIPT}
	
	# config file
	install -d ${D}${CONFIGDIR}
	install -m 0644 ${WORKDIR}/config ${D}${CONFIGFILE}
	# copy config file to backup directory
	install -d ${D}${BKPDIR}
	install -m 0644 ${WORKDIR}/config ${D}${BKPFILE}
}

pkg_prerm_${PN}_prepend () {
    #!/bin/sh
    # test if config file exists
    if [ -f ${CONFIGFILE} ]; then
        echo "${PN}: config file exists, backup it"
        cp ${CONFIGFILE} ${CONFIGFILE}.bpk
    fi
    
    # test if init script file exists
    if [ -f ${INITSCRIPT} ]; then
        # don't stop the service here, it will be stopped automatically
        
        if [ $? -eq 0 ]; then
            # service was running, must restart it after update
            touch ${RUNNING_FILE}
        elif [ -f ${RUNNING_FILE} ]; then
            rm ${RUNNING_FILE} >/dev/null 2>&1
        fi
    fi
}

pkg_preinst_${PN}_append () {
    #!/bin/sh
    if [ x"$D" = "x" ]; then
        # FIX bug with loriot init script
        if [ -f /etc/init.d/loriot-gw ]; then
            #if [[ $(sudo opkg list-installed |grep '^loriot - ') == *1.0.1-r1 ]]; then
                sudo sed -i "/$(echo '\t')exit \$?/d" /etc/init.d/loriot-gw
            #fi
        fi

        # FIX bug with packet-forwarder init script
        if [ -f /etc/init.d/packet-forwarder-gw ]; then
            #if [[ $(sudo opkg list-installed |grep '^packet-forwarder - ') == *3.1.0-r3 ]]; then
                sudo sed -i "/$(echo '\t')exit \$?/d" /etc/init.d/packet-forwarder-gw
            #fi
        fi
    fi
}

pkg_postinst_${PN}_append () {
    #!/bin/sh
    if [ x"$D" = "x" ]; then
        # Script is executed during the runtime installation

        # test if config file was already existing before the update
        if [ -f ${CONFIGFILE}.bkp ]; then
            # delete old config file
            rm ${CONFIGGILE}.bkp >/dev/null 2>&1
        fi
        
        # test if service was running before update
	    if [ -f ${RUNNING_FILE} ]; then
            echo "${PN}: restarting the stopped service"
            # use the manual script which doesn't use autostart value
            ${sysconfdir}/init.d/clouds-manager.sh start
	        rm ${RUNNING_FILE} >/dev/null 2>&1
	    fi
    else
        # Script is executed during the rootfs construction
	    exit 1
    fi
}

FILES_${PN} =+ " \
    ${CONFIGFILE} \
    ${BKPFILE} \
    ${sysconfdir}/init.d/clouds-manager.sh \
    ${INITSCRIPT} \
    "

