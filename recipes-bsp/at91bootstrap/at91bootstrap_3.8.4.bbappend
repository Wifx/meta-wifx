FILESEXTRAPATHS_prepend := "${THISDIR}/files:"
COMPATIBLE_MACHINE += '|sama5d4-lorix-one|sama5d4-lorix-one-sd'

SRC_URI += "file://000_atmel_v3.8.4_lorix_one_addition.patch"

inherit cml1 deploy

AT91BOOTSTRAP_CONFIG_sama5d4-lorix-one ??= "${AT91BOOTSTRAP_MACHINE}nf_uboot_secure"
AT91BOOTSTRAP_CONFIG_sama5d4-lorix-one-sd ??= "${AT91BOOTSTRAP_MACHINE}sd_uboot_secure"


do_configure() {
        # Copy board defconfig to .config if .config does not exist. This
	# allows recipes to manage the .config themselves in
	# do_configure_prepend().
	
	if [ "${MACHINE}" = "sama5d4-lorix-one" ] || \
	   [ "${MACHINE}" = "sama5d4-lorix-one-sd" ]; then
		echo "coucou WIFX"
		if [ -f "${S}/contrib/board/wifx/${AT91BOOTSTRAP_MACHINE}/${AT91BOOTSTRAP_TARGET}" ] && [ ! -f "${B}/.config" ]; then
			cp "${S}/contrib/board/wifx/${AT91BOOTSTRAP_MACHINE}/${AT91BOOTSTRAP_TARGET}" "${B}/.config"
		fi
	else
		echo "fuck WIFX"
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
