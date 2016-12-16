FILESEXTRAPATHS_prepend := "${THISDIR}/files:"
SRC_URI += "file://0001_atmel_v3.8.7_lorix_one_addition.patch"

COMPATIBLE_MACHINE += '|sama5d4-lorix-one|sama5d4-lorix-one-sd'

inherit cml1 deploy

AT91BOOTSTRAP_CONFIG_sama5d4-lorix-one ??= "${AT91BOOTSTRAP_MACHINE}nf_uboot_secure"
AT91BOOTSTRAP_CONFIG_sama5d4-lorix-one-sd ??= "${AT91BOOTSTRAP_MACHINE}sd_uboot_secure"

AT91BOOTSTRAP_LOAD_sama5d4-lorix-one-sd ??= "sdboot-uboot"


do_configure() {
        # Copy board defconfig to .config if .config does not exist. This
	# allows recipes to manage the .config themselves in
	# do_configure_prepend().
	
	if [ "${MACHINE}" = "sama5d4-lorix-one" ] || \
	   [ "${MACHINE}" = "sama5d4-lorix-one-sd" ]; then
		if [ -f "${S}/contrib/board/wifx/${AT91BOOTSTRAP_MACHINE}/${AT91BOOTSTRAP_TARGET}" ] && [ ! -f "${B}/.config" ]; then
			cp "${S}/contrib/board/wifx/${AT91BOOTSTRAP_MACHINE}/${AT91BOOTSTRAP_TARGET}" "${B}/.config"
		fi
	else
		if [ -f "${S}/board/${AT91BOOTSTRAP_MACHINE}/${AT91BOOTSTRAP_TARGET}" ] && [ ! -f "${B}/.config" ]; then
			cp "${S}/board/${AT91BOOTSTRAP_MACHINE}/${AT91BOOTSTRAP_TARGET}" "${B}/.config"
		fi
	fi

	# Copy defconfig to .config if .config does not exist. This allows
	# recipes to manage the .config themselves in do_configure_prepend()
	# and to override default settings with a custom file.
	if [ -f "${WORKDIR}/defconfig" ] && [ ! -f "${B}/.config" ]; then
		cp "${WORKDIR}/defconfig" "${B}/.config"
	fi

	if [ ! -f "${S}/.config" ]; then
		bbfatal "No config files found"
	fi

	cml1_do_configure
}
