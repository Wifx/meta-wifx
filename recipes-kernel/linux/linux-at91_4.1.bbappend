require linux-at91.inc

FILESEXTRAPATHS_prepend := "${THISDIR}/${BPN}-4.1:"

SRC_URI += "file://0001_atmel_linux-4.1-at91_lorix_one_addition.patch \
	    file://sama5d4-lorix-one-defconfig \
           "
