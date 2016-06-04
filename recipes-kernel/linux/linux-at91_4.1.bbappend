FILESEXTRAPATHS_prepend := "${THISDIR}/linux-at91-4.1:"
SRC_URI += "file://000_atmel_linux-4.1-at91_lorix_one_addition.patch \
	    file://defconfig \
           "

COMPATIBLE_MACHINE += '|sama5d4-lorix-one|sama5d4-lorix-one-sd'
