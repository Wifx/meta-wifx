SUMMARY = "Monit support files for the TTN packet-forwarder"
AUTHOR = "Wifx Sàrl"

LICENSE = "WIFX"
LIC_FILES_CHKSUM = "file://LICENSE;md5=34f36682009129c7f5c204e0dbb3e7ba"

SRC_URI = " \
    file://LICENSE \
    file://monit \
"

PR = "r0"
S = "${WORKDIR}"

INHIBIT_DEFAULT_DEPS = "1"
do_patch[noexec] = "1"
do_configure[noexec] = "1"
do_compile[noexec] = "1"

RDEPENDS_${PN} += "monit"

do_install () {
    install -d -m 0700 ${D}${sysconfdir}/monit.d
    install -m 0644 ${WORKDIR}/monit ${D}${sysconfdir}/monit.d/ttn-gw.monit
}
