SUMMARY = "LORIX One clouds manager"
DESCRIPTION = "Startup script which manages the various availlable clouds software of the LORIX One"

LICENSE = "WIFX"
LIC_FILES_CHKSUM = "file://LICENSE;md5=22af7693d7b76ef0fc76161c4be76c45"

SRC_URI = " \
	file://LICENSE \
	file://init \
	"

DEPENDS += ""
RDEPENDS_${PN} += "reset-lgw packet-forwarder loriot "

inherit update-rc.d

CONFIGFILE_NAME = "clouds"
CONFIGFILE = "${sysconfdir}/lorix/${CONFIGFILE_NAME}"

INITSCRIPT_NAME = "clouds-manager"
INITSCRIPT = "${sysconfdir}/init.d/${INITSCRIPT_NAME}"
INITSCRIPT_PARAMS = "start 2 3 4 5 . stop 70 0 1 6 ."

RUNNINGFILE = "/var/tmp/${INITSCRIPT_NAME}_is_running"

do_install () {
	# init script
	install -d ${D}${sysconfdir}/init.d
	install -m 0755 ${WORKDIR}/init ${D}/${INITSCRIPT}
	
	# config file
	install -d ${D}${sysconfdir}/lorix
	install -m 0644 ${WORKDIR}/config ${D}/${CONFIGFILE}
}

pkg_prerm_${PN}_prepend () {
    #!/bin/sh

    echo "pkg_prerm_${PN}_prepend"
    
    # test if config file exists
    if [ -f ${CONFIGFILE} ]; then
        echo "${PN}: config file exists, backup it"
        mv ${CONFIGFILE} ${CONFIGFILE}.bpk
    fi
    
    # test if init script file exists
    if [ -f ${INITSCRIPT} ]; then
        # stop possible running cloud service
        echo "${PN}: stop the running service"
        ${INITSCRIPT} stop
        
        if [ $? -eq 0 ]; then
            # service was running, must restart it after update
            touch ${RUNNINGFILE}
        elif [ -f ${RUNNINGFILE} ]; then
            rm ${RUNNINGFILE} >/dev/null 2>&1
        fi
    fi
}

pkg_postinst_${PN}_append () {
    #!/bin/sh
    echo "pkg_postinst_${PN}_prepend"
    if [ x"$D" = "x" ]; then
        # Script is executed during the runtime installation

        # test if config file was already existing before the update
        if [ -f ${CONFIGFILE}.bkp ]; then
            
            
            
            # delete old config file
            rm ${CONFIGGILE}.bkp >/dev/null 2>&1
        fi
        
        # test if service was running before update
	    if [ -f ${RUNNINGFILE} ]; then
            echo "${PN}: restarting the stopped service"
	        ${INITSCRIPT} start
	        rm ${RUNNINGFILE}
	    fi
    else
        # Script is executed during the rootfs construction
	    exit 1
    fi
}

FILES_${PN} = " \
    ${INITSCRIPT} \
    ${CONFIGFILE} \
    "

