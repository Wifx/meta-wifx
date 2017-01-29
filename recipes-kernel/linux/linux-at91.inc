COMPATIBLE_MACHINE += '|sama5d4-lorix-one|sama5d4-lorix-one-sd'

kernel_do_configure_prepend() {

	if [ -f "${WORKDIR}/sama5d4_lorix_one_defconfig" ] && [ ! -f "${B}/.config" ]; then
		cp "${WORKDIR}/sama5d4_lorix_one_defconfig" "${B}/.config"
	fi
}

