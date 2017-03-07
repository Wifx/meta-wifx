require u-boot-atmel.inc

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://README;beginline=1;endline=22;md5=2687c5ebfd9cb284491c3204b726ea29"

SRCREV = "${AUTOREV}"

PV = "v2015.01-at91+git${SRCPV}"

COMPATIBLE_MACHINE = '(sama5d4-lorix-one|sama5d4-lorix-one-sd)'

SRC_URI = "git://github.com/Wifx/u-boot-at91.git;branch=u-boot-2015.01-at91"

S = "${WORKDIR}/git"

do_compile () {
	if [ "${@base_contains('DISTRO_FEATURES', 'ld-is-gold', 'ld-is-gold', '', d)}" = "ld-is-gold" ] ; then
		sed -i 's/$(CROSS_COMPILE)ld$/$(CROSS_COMPILE)ld.bfd/g' config.mk
	fi

	unset LDFLAGS
	unset CFLAGS
	unset CPPFLAGS

	cd ${S}; git status; cd -

	oe_runmake ${UBOOT_MACHINE}
	oe_runmake ${UBOOT_MAKE_TARGET}
}

PACKAGE_ARCH = "${MACHINE_ARCH}"

