SUMMARY = "LORIOT application"
DESCRIPTION = "LORIOT LoRaWAN cloud application"

LICENSE = "LORIOT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=63722c69679e7db558ef8786ac6de1b1"
SRC_URI = " \
	file://LICENSE \
	file://loriot_lorix_one_SPI_${PV} \
	file://init \
	"

S = "${WORKDIR}"

RDEPENDS_${PN} += "libcrypto openssl reset-lgw"

inherit update-rc.d

INITSCRIPT_NAME = "loriot_gw"
INITSCRIPT_PARAMS = "start 2 3 4 5 . stop 80 0 6 1 ."

do_install () {
	install -d ${D}/opt/lorix/clouds/loriot
	install -m 0755 ${WORKDIR}/loriot_lorix_one_SPI_${PV} ${D}/opt/lorix/clouds/loriot/loriot-gw

	# init script
	install -d ${D}${sysconfdir}/init.d
	install -m 0755 ${WORKDIR}/init ${D}${sysconfdir}/init.d/${INITSCRIPT_NAME}
}

FILES_${PN} =+ "/opt/lorix/clouds/loriot/*"
