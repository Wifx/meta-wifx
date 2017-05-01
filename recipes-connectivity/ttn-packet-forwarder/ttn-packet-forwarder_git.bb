SUMMARY = "LoRa SX1301 based gateway packet forwarder for The Things Network"
DESCRIPTION = "LoRa SX1301 based gateway packet forwarder modified for The Things Network"

LICENSE = "SEMTECH"
LIC_FILES_CHKSUM = "file://LICENSE;md5=22af7693d7b76ef0fc76161c4be76c45"

SRCREV = "${AUTOREV}"
SRC_URI = " \
    git://github.com/Wifx/ttn_packet_forwarder.git;protocol=git \
    file://init \
    file://post-backup.sh \
    "

PR = "r0"
S = "${WORKDIR}/git"

DEPENDS = "ttn-lora-gateway"
RDEPENDS_${PN} += "reset-lgw update-gwid"

inherit update-rc.d

INITSCRIPT_NAME = "ttn-gw"
INITSCRIPT_PARAMS = "stop 70 0 1 6 ."

RUNDIR="/opt/lorix/clouds/ttn"
BKPDIR="${sysconfdir}/backup.d${RUNDIR}"
POSTBKPDIR="${sysconfdir}/post-backup.d"

RUNNING_FILE = "/var/tmp/${INITSCRIPT_NAME}_is_running"

EXTRA_OEMAKE = " \
	'CC=${CC}' 'CFLAGS=${CFLAGS} \
	-Iinc -I. \
	-I${STAGING_DIR_TARGET}/${includedir}/ttn-lora-gateway/inc' \
	'BUILDDIR=${S}' \
	'LGW_PATH=${STAGING_DIR_TARGET}${libdir}/ttn-lora-gateway' \
	"

do_install () {
    install -d ${D}${RUNDIR}

    install -m 0755 ${S}/poly_pkt_fwd/poly_pkt_fwd ${D}${RUNDIR}
    
    # configuration files
    install -m 0644 ${S}/poly_pkt_fwd/*.json ${D}${RUNDIR}
    
    # init script
    install -d ${D}${sysconfdir}/init.d
    install -m 0755 ${WORKDIR}/init ${D}${sysconfdir}/init.d/${INITSCRIPT_NAME}
    
    # backup management
    install -d -m 0644 ${D}/${BKPDIR}
    install -m 0644 ${S}/poly_pkt_fwd/*.json ${D}/${BKPDIR}
    install -d -m 0644 ${D}/${POSTBKPDIR}
    install -m 0755 ${WORKDIR}/post-backup.sh ${D}${POSTBKPDIR}/ttn-post-backup.sh
}

pkg_prerm_${PN}_prepend () {
    #!/bin/sh
    
    echo "pre rm script prepend"

    # test if service is running
    ${sysconfdir}/init.d/${INITSCRIPT_NAME} status >/dev/null 2>&1

    if [ $? -eq 0 ]; then
        touch ${RUNNING_FILE}
        echo "Stopping TTN packet-forwarder cloud service"
        ${sysconfdir}/init.d/${INITSCRIPT_NAME} stop
    else
        rm ${RUNNING_FILE} >/dev/null 2>&1
    fi
}

pkg_postinst_${PN}_append () {
    #!/bin/sh
    if [ x"$D" = "x" ]; then
        # Script is executed during the runtime installation

        # Update gateway ID based on the eth0 MAC address
        echo "Updating configuration files with gateway ID"
        /opt/lorix/utils/update_gwid.sh ${RUNDIR}/global_conf.json
        /opt/lorix/utils/update_gwid.sh ${RUNDIR}/EU_global_conf_2dBi_indoor.json
        /opt/lorix/utils/update_gwid.sh ${RUNDIR}/EU_global_conf_4dBi_outdoor.json
        /opt/lorix/utils/update_gwid.sh ${RUNDIR}/local_conf.json

        if [ -f "${RUNNING_FILE}" ]; then
            echo "Restarting TTN packet-forwarder cloud service"
            ${sysconfdir}/init.d/${INITSCRIPT_NAME} start
            rm ${RUNNING_FILE}
        fi
    else
        # Script is executed during the rootfs construction
        exit 1
    fi
}

FILES_${PN} = " \
    ${RUNDIR}/* \
    ${BKPDIR}/*.json \
    ${POSTBKPDIR}/* \
    ${sysconfdir}/init.d/${INITSCRIPT_NAME} \
    "

