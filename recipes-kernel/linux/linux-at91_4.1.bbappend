FILESEXTRAPATHS_prepend_sama5d4-lorix-one := "${THISDIR}/${BPN}-4.1:"

SRC_URI += "file://000_atmel_linux-4.1-at91_lorix_one_addition.patch \
	    file://sama5d4-lorix-one-defconfig \
           "

COMPATIBLE_MACHINE += '|sama5d4-lorix-one|sama5d4-lorix-one-sd'

kernel_do_configure_prepend_sama5d4-lorix-one() {
	if [ -f "${WORKDIR}/sama5d4-lorix-one-defconfig" ] && [ ! -f "${B}/.config" ]; then
		cp "${WORKDIR}/sama5d4-lorix-one-defconfig" "${B}/.config"
	fi
}
