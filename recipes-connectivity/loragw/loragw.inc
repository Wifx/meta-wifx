SUMMARY = "LoRa SX1301 based gateway"
DESCRIPTION = "LoRa SX1301 based gateway library and tools."

SRC_URI = "git://github.com/Wifx/lora_gateway.git;protocol=git;tag=${KTAG}"

S = "${WORKDIR}/git"

do_compile () {
	export CFLAGS="${CFLAGS} -Iinc -I."
	make -C ${WORKDIR}/git all
}

do_install () {
	mkdir -p ${D}/opt/lorix/test
	cp ${S}/libloragw/test_loragw_* ${D}/opt/lorix/test

	mkdir -p ${D}/opt/lorix/util
	cp ${S}/util_lbt_test/util_lbt_test ${D}/opt/lorix/util
	cp ${S}/util_pkt_logger/global_conf.json ${D}/opt/lorix/util
	cp ${S}/util_pkt_logger/local_conf.json ${D}/opt/lorix/util	
	cp ${S}/util_pkt_logger/util_pkt_logger ${D}/opt/lorix/util
	cp ${S}/util_spectral_scan/util_spectral_scan ${D}/opt/lorix/util
	cp ${S}/util_spi_stress/util_spi_stress ${D}/opt/lorix/util
	cp ${S}/util_tx_continuous/util_tx_continuous ${D}/opt/lorix/util
	cp ${S}/util_tx_test/util_tx_test ${D}/opt/lorix/util
}

FILES_${PN} += "/opt/lorix/test/test_loragw_* \
		/opt/lorix/util/util_* \
		/opt/lorix/util/global_conf.json \
		/opt/lorix/util/local_conf.json \
		"

BBCLASSEXTEND = "native nativesdk"
