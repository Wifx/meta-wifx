SUMMARY = "LORIOT application"
DESCRIPTION = "LORIOT LoRaWAN cloud application"

LICENSE = "LORIOT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=63722c69679e7db558ef8786ac6de1b1"
SRC_URI = "file://LICENSE \
	file://loriot_lorix_one_SPI_${PV}"

S = "${WORKDIR}"

RDEPENDS_${PN} += "libcrypto openssl"

do_install () {
	install -d ${D}/opt/lorix/clouds/loriot
	install -m 0755 ${WORKDIR}/loriot_lorix_one_SPI_${PV} ${D}/opt/lorix/clouds/loriot
}

FILES_${PN} =+ "/opt/lorix/clouds/loriot/*"
