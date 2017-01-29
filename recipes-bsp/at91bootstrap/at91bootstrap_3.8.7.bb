require at91bootstrap.inc

LIC_FILES_CHKSUM = "file://main.c;endline=27;md5=a2a70db58191379e2550cbed95449fbd"

COMPATIBLE_MACHINE = '(sama5d4-lorix-one|sama5d4-lorix-one-sd)'

SRC_URI = "https://github.com/Wifx/at91bootstrap/archive/v${PV}.tar.gz;name=tarball \
"

SRC_URI[tarball.md5sum] = "83ba78d6a412fe9157938ad8a739fed2"
SRC_URI[tarball.sha256sum] = "95ee154b766f03e796a369994bb812fba51bc4cdcd8cb0f5d8f0ac5e2197de32"
