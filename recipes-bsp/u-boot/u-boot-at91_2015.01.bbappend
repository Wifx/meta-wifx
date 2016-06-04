FILESEXTRAPATHS_prepend := "${THISDIR}/files:"
SRC_URI += "file://000_atmel_2015.01_lorix_one_addition.patch"

COMPATIBLE_MACHINE += '|sama5d4-lorix-one|sama5d4-lorix-one-sd'
