FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI_append = "file://base-feeds.conf"

do_install_append () {
	install -d ${D}${sysconfdir}/opkg
	install -m 0644 ${WORKDIR}/base-feeds.conf ${D}${sysconfdir}/opkg/base-feeds.conf
}

CONFFILES_${PN}_append = "${sysconfdir}/opkg/base-feeds.conf"
