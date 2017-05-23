FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI += "file://0001-Added-resolvconf-delete-udhcpc-during-ifdown.patch"

PR = "r1"
