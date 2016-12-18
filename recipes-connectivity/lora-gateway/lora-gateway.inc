SUMMARY = "LoRa SX1301 based gateway"
DESCRIPTION = "LoRa SX1301 based gateway library and tools."

SRC_URI = "git://github.com/Wifx/lora_gateway.git;protocol=git;tag=${KTAG}"

S = "${WORKDIR}/git"

RDEPENDS_${PN} += "update-gwid"

EXTRA_OEMAKE = " \
	'CC=${CC}' 'CFLAGS=${CFLAGS} \
	-I${S}/util_pkt_logger/inc \
	-I${S}/libloragw -I${S}/libloragw/inc' \
	'BUILDDIR=${S}' \
	"

do_install () {
	install -d ${D}${libdir}/${BPN}/inc \
		   ${D}${includedir}/${BPN}/inc \
		   ${D}/opt/lorix/tests \
 		   ${D}/opt/lorix/utils

	install -m 0644 ${S}/libloragw/libloragw.a ${D}${libdir}/${BPN}
	install -m 0644 ${S}/libloragw/library.cfg ${D}${libdir}/${BPN}

	install -m 0644 ${S}/libloragw/inc/* ${D}${libdir}/${BPN}/inc
	install -m 0644 ${S}/libloragw/inc/* ${D}${includedir}/${BPN}/inc
	install -m 0755 ${S}/libloragw/test_loragw_* ${D}/opt/lorix/tests
	install -m 0755 ${S}/util_lbt_test/util_lbt_test ${D}/opt/lorix/utils

	install -m 0755 ${S}/util_pkt_logger/util_pkt_logger ${D}/opt/lorix/utils
	install -m 0644 ${S}/util_pkt_logger/*.json ${D}/opt/lorix/utils

	install -m 0755 ${S}/util_spi_stress/util_spi_stress ${D}/opt/lorix/utils
	install -m 0755 ${S}/util_tx_continuous/util_tx_continuous ${D}/opt/lorix/utils
	install -m 0755 ${S}/util_tx_test/util_tx_test ${D}/opt/lorix/utils
}

pkg_postinst_${PN} () {
#!/bin/sh -e
if [ x"$D" = "x" ]; then
	# Update gateway ID based on the eth0 MAC address
	/opt/lorix/utils/update_gwid.sh /opt/lorix/utils/global_conf.json
	/opt/lorix/utils/update_gwid.sh /opt/lorix/utils/local_conf.json
else
	exit 1
fi
}

FILES_${PN} =+ "/opt/lorix/tests/* /opt/lorix/utils/*"

FILES_${PN}-staticdev =+ "${libdir}/${BPN}/*.cfg"

