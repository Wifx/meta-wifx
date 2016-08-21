SUMMARY = "Basic init scripts which manage the SX1301 reset"
DESCRIPTION = "This package provides the init script which manage the RESET pin of the SX1301 RF gateway chip"
AUTHOR = "Wifx Sàrl"
SECTION = "base"
LICENSE = "WIFX"
LIC_FILES_CHKSUM = "file://${WORKDIR}/LICENSE;md5=323e5da3c7dfd45dac56506af122354f"
PR = "r2"

inherit update-rc.d

INITSCRIPT_NAME = "reset_lgw"
INITSCRIPT_PARAMS = "start 01 2 3 4 5 . stop 80 0 6 1 ."

SRC_URI = "file://LICENSE \
	   file://init \
	  "

S = "${WORKDIR}"

do_install () {
	install -d ${D}${sysconfdir}/init.d
	install -m 0755 ${WORKDIR}/init ${D}${sysconfdir}/init.d/reset_lgw
}
