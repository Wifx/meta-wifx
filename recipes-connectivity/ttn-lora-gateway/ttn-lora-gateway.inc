SUMMARY = "LoRa SX1301 based gateway modified for The Thing Networks"
DESCRIPTION = "LoRa SX1301 based gateway library and tools. It's doesn't provide tools as the offical liblora (see lora-gateway recipe) but only provides library for ttn-packet-forwarder."

SRC_URI = "git://github.com/Wifx/ttn_lora_gateway.git;protocol=git;tag=${KTAG}"

S = "${WORKDIR}/git"

EXTRA_OEMAKE = " \
	'CC=${CC}' 'CFLAGS=${CFLAGS} \
	-I${S}/util_pkt_logger/inc \
	-I${S}/libloragw -I${S}/libloragw/inc' \
	'BUILDDIR=${S}' \
	"

do_install () {
	install -d ${D}${libdir}/${BPN}/inc \
		   ${D}${includedir}/${BPN}/inc

	install -m 0644 ${S}/libloragw/libloragw.a ${D}${libdir}/${BPN}
	install -m 0644 ${S}/libloragw/library.cfg ${D}${libdir}/${BPN}

	install -m 0644 ${S}/libloragw/inc/* ${D}${libdir}/${BPN}/inc
	install -m 0644 ${S}/libloragw/inc/* ${D}${includedir}/${BPN}/inc
}

FILES_${PN}-staticdev =+ "${libdir}/${BPN}/*.cfg"

