SUMMARY = "LoRa SX1301 based gateway packet forwarder"
DESCRIPTION = "LoRa SX1301 based gateway packet forwarder."

LICENSE = "SEMTECH"
LIC_FILES_CHKSUM = "file://LICENSE;md5=22af7693d7b76ef0fc76161c4be76c45"

KTAG = "v3.0.0"
SRC_URI = "git://github.com/Wifx/packet-forwarder.git;protocol=git;tag=${KTAG}"

PR = "r0"
S = "${WORKDIR}/git"

DEPENDS = "lora-gateway"

PACKAGES =+ "update-gwid"

RPROVIDES_${PN} =+ "update-gwid"

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
	install -m 0644 ${S}/lora_pkt_fwd/*.json ${D}/opt/lorix/clouds/${BPN}	
	install -m 0755 ${S}/util_ack/util_ack ${D}/opt/lorix/clouds/${BPN}
	install -m 0755 ${S}/util_sink/util_sink ${D}/opt/lorix/clouds/${BPN}
	install -m 0755 ${S}/util_tx_test/util_tx_test ${D}/opt/lorix/clouds/${BPN}
	install -m 0755 ${S}/lora_pkt_fwd/update_gwid.sh ${D}/opt/lorix/utils
}

pkg_postinst_${PN} () {
#!/bin/bash
if [ -z "$D" ]; then
	# Update gateway ID based on the eth0 MAC address
	/opt/lorix/utils/update_gwid.sh /opt/lorix/clouds/${BPN}/global_conf.json
	/opt/lorix/utils/update_gwid.sh /opt/lorix/clouds/${BPN}/local_conf.json
fi
}

FILES_${PN} = "/opt/lorix/clouds/${BPN}/*"

FILES_update-gwid = "/opt/lorix/utils/update_gwid.sh"
