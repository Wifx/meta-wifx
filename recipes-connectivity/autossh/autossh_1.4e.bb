SUMMARY = "Automatically restart SSH sessions and tunnels"
DESCRIPTION = "autossh is a program to start a copy of ssh and monitor it, restarting it as necessary should it die or stop passing traffic. The idea is from rstunnel (Reliable SSH Tunnel), but implemented in C."
PROVIDES = "autossh"
LICENSE = "GPLv2+"
PR = "r0"

LIC_FILES_CHKSUM = "file://autossh.c;beginline=1;endline=24;md5=755a81ffe573faf6c18d1f1061d097c4"
SRC_URI[md5sum] = "f86684b96e99d22b2e9d35dc63b0aa29"
SRC_URI[sha256sum] = "9e8e10a59d7619176f4b986e256f776097a364d1be012781ea52e08d04679156"

SRC_URI = "http://www.harding.motd.ca/autossh/${PN}-${PV}.tgz"

inherit autotools

S = "${WORKDIR}/${PN}-${PV}"

do_configure() {
    cd ${S}
    ./configure --host=arm-${TARGET_ARCH} --prefix=${D}        
}

do_compile() {
    cd ${S}
    make
}

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${S}/autossh ${D}${bindir}/autossh
}

FILES_${PN} = "${bindir}/autossh"

