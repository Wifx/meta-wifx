SUMMARY = "LoRa SX1301 based gateway packet forwarder for The Things Network"
DESCRIPTION = "LoRa SX1301 based gateway packet forwarder modified for The Things Network"

LICENSE = "SEMTECH"
LIC_FILES_CHKSUM = "file://LICENSE;md5=22af7693d7b76ef0fc76161c4be76c45"

SRCREV = "${AUTOREV}"
SRC_URI = " \
    git://github.com/Wifx/ttn_packet_forwarder.git;protocol=git \
    file://init \
    "

PR = "r0"
S = "${WORKDIR}/git"

DEPENDS = "ttn-lora-gateway"
RDEPENDS_${PN} += "reset-lgw update-gwid"

inherit update-rc.d

INITSCRIPT_NAME = "ttn-gw"
INITSCRIPT_PARAMS = "stop 70 0 1 6 ."

RUNNING_FILE = "/var/tmp/${INITSCRIPT_NAME}_is_running"

EXTRA_OEMAKE = " \
	'CC=${CC}' 'CFLAGS=${CFLAGS} \
	-Iinc -I. \
	-I${STAGING_DIR_TARGET}/${includedir}/ttn-lora-gateway/inc' \
	'BUILDDIR=${S}' \
	'LGW_PATH=${STAGING_DIR_TARGET}${libdir}/ttn-lora-gateway' \
	"

do_install () {
    #install -d ${D}/opt/lorix/clouds/ttn-basic-pkt-fwd
    #install -m 0755 ${S}/basic_pkt_fwd/basic_pkt_fwd ${D}/opt/lorix/clouds/ttn-basic-pkt-fwd
    #install -m 0644 ${S}/basic_pkt_fwd/*.json ${D}/opt/lorix/clouds/ttn-basic-pkt-fwd
    
    install -d ${D}/opt/lorix/clouds/ttn
    install -m 0755 ${S}/poly_pkt_fwd/poly_pkt_fwd ${D}/opt/lorix/clouds/ttn
    install -m 0644 ${S}/poly_pkt_fwd/*.json ${D}/opt/lorix/clouds/ttn
    
    # init script
    install -d ${D}${sysconfdir}/init.d
    install -m 0755 ${WORKDIR}/init ${D}${sysconfdir}/init.d/${INITSCRIPT_NAME}
}

pkg_prerm_${PN}_prepend () {
    #!/bin/sh

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
        #/opt/lorix/utils/update_gwid.sh /opt/lorix/clouds/ttn-basic-pkt-fwd/global_conf.json
        #/opt/lorix/utils/update_gwid.sh /opt/lorix/clouds/ttn-basic-pkt-fwd/EU_global_conf_2dBi_indoor.json
        #/opt/lorix/utils/update_gwid.sh /opt/lorix/clouds/ttn-basic-pkt-fwd/EU_global_conf_4dBi_outdoor.json
        #/opt/lorix/utils/update_gwid.sh /opt/lorix/clouds/ttn-basic-pkt-fwd/local_conf.json
        
        /opt/lorix/utils/update_gwid.sh /opt/lorix/clouds/ttn/global_conf.json
        /opt/lorix/utils/update_gwid.sh /opt/lorix/clouds/ttn/EU_global_conf_2dBi_indoor.json
        /opt/lorix/utils/update_gwid.sh /opt/lorix/clouds/ttn/EU_global_conf_4dBi_outdoor.json
        /opt/lorix/utils/update_gwid.sh /opt/lorix/clouds/ttn/local_conf.json

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
    /opt/lorix/clouds/ttn/* \
    ${sysconfdir}/init.d/${INITSCRIPT_NAME} \
    "
#/opt/lorix/clouds/ttn-basic-pkt-fwd/*
