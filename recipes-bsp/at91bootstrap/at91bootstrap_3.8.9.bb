require at91bootstrap.inc

LIC_FILES_CHKSUM = "file://main.c;endline=27;md5=a2a70db58191379e2550cbed95449fbd"

COMPATIBLE_MACHINE = '(sama5d4-lorix-one|sama5d4-lorix-one-sd|sama5d4-lorix-one-512|sama5d4-lorix-one-512-sd)'

SRC_URI = "https://github.com/Wifx/at91bootstrap/archive/v${PV}.tar.gz;name=tarball \
"

SRC_URI[tarball.md5sum] = "91206f057d2bd6c933f255cd3e3c5891"
SRC_URI[tarball.sha256sum] = "1211a9f32dad6d2539c99833dd7a52dc844e4066d7d375133a9045a2f62908a1"

