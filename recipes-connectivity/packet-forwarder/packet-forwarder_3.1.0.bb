SUMMARY = "LoRa SX1301 based gateway packet forwarder"
DESCRIPTION = "LoRa SX1301 based gateway packet forwarder."

LICENSE = "SEMTECH"
LIC_FILES_CHKSUM = "file://LICENSE;md5=22af7693d7b76ef0fc76161c4be76c45"

KTAG = "v3.1.0"
SRC_URI = " \
    git://github.com/Wifx/packet_forwarder.git;protocol=git;tag=${KTAG} \
    file://init \
    "

PR = "r3"
S = "${WORKDIR}/git"

DEPENDS = "lora-gateway"
RDEPENDS_${PN} += "reset-lgw"

PACKAGES =+ "update-gwid"
RPROVIDES_${PN} =+ "update-gwid"

inherit update-rc.d

INITSCRIPT_NAME = "packet-forwarder-gw"
INITSCRIPT_PARAMS = "stop 70 0 1 6 ."

RUNNING_FILE = "/var/tmp/${INITSCRIPT_NAME}_is_running"

EXTRA_OEMAKE = " \
	'CC=${CC}' 'CFLAGS=${CFLAGS} \
	-I${S}/lora_pkt_fwd/inc \
	-I${STAGING_DIR_TARGET}/${includedir}/lora-gateway/inc' \
	'BUILDDIR=${S}' \
	'LGW_PATH=${STAGING_DIR_TARGET}${libdir}/lora-gateway' \
	"

do_install () {
    install -d ${D}/opt/lorix/clouds/${BPN} \
	       ${D}/opt/lorix/utils

    install -m 0755 ${S}/lora_pkt_fwd/lora_pkt_fwd ${D}/opt/lorix/clouds/${BPN}

    # configuration files
    install -m 0644 ${S}/lora_pkt_fwd/*.json ${D}/opt/lorix/clouds/${BPN}

    install -m 0755 ${S}/util_ack/util_ack ${D}/opt/lorix/clouds/${BPN}
    install -m 0755 ${S}/util_sink/util_sink ${D}/opt/lorix/clouds/${BPN}
    install -m 0755 ${S}/util_tx_test/util_tx_test ${D}/opt/lorix/clouds/${BPN}
    install -m 0755 ${S}/lora_pkt_fwd/update_gwid.sh ${D}/opt/lorix/utils

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
        echo "Stopping Semtech packet-forwarder cloud service"
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
        /opt/lorix/utils/update_gwid.sh /opt/lorix/clouds/${BPN}/global_conf.json
        /opt/lorix/utils/update_gwid.sh /opt/lorix/clouds/${BPN}/global_conf_2dBi_indoor.json
        /opt/lorix/utils/update_gwid.sh /opt/lorix/clouds/${BPN}/global_conf_4dBi_outdoor.json
        /opt/lorix/utils/update_gwid.sh /opt/lorix/clouds/${BPN}/local_conf.json
        
        # Copy files to backup directory
        if [ -d ${sysconfdir}/backup.d ]; then
            # Copy with hierarchy but do not override
            cp --parents -n /opt/lorix/clouds/${BPN}/*.json ${sysconfdir}/backup.d
        fi

        if [ -f "${RUNNING_FILE}" ]; then
            echo "Restarting Semtech packet-forwarder cloud service"
            ${sysconfdir}/init.d/${INITSCRIPT_NAME} start
            rm ${RUNNING_FILE}
        fi
    else
        # Script is executed during the rootfs construction
        exit 1
    fi
}

FILES_${PN} = " \
    /opt/lorix/clouds/${BPN}/* \
    ${sysconfdir}/init.d/${INITSCRIPT_NAME} \
    "

FILES_update-gwid = "/opt/lorix/utils/update_gwid.sh"

