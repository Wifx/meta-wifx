FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI_append = " \
	file://base-feeds_lorix_one.conf \
	file://base-feeds_lorix_one_512.conf \
"

do_install_append() {
	install -d ${D}${sysconfdir}/opkg

	if   [ "${MACHINE}" = "sama5d4-lorix-one" ] || \
             [ "${MACHINE}" = "sama5d4-lorix-one-sd" ]; then
		install -m 0644 ${WORKDIR}/base-feeds_lorix_one.conf ${D}${sysconfdir}/opkg/base-feeds.conf		
	elif [ "${MACHINE}" = "sama5d4-lorix-one-512" ] || \
             [ "${MACHINE}" = "sama5d4-lorix-one-512-sd" ]; then
		install -m 0644 ${WORKDIR}/base-feeds_lorix_one_512.conf ${D}${sysconfdir}/opkg/base-feeds.conf		
	fi
}

pkg_postinst_${PN}_append () {
    #!/bin/sh
    if [ x"$D" = "x" ]; then
        # Script is executed during the runtime installation

        # Retrieve current Linux release
        KERNEL_RELEASE=$(/bin/uname -r)

        # Update base-feeds.conf file with Linux kernel release
        /bin/sed -i "s/@{FEED_ARCH_VERSION}/linux-$KERNEL_RELEASE/g" ${sysconfdir}/opkg/base-feeds.conf
    else
        # Script is executed during the rootfs construction
        exit 1
    fi
}

CONFFILES_${PN}_append = "${sysconfdir}/opkg/base-feeds.conf"
