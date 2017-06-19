DESCRIPTION = "Image with LORIX One support + extended libraries, applications and services"

LICENSE = "MIT"
PR = "r0"

require wifx-base.bb

IMAGE_FEATURES += ""

IMAGE_INSTALL += "\
    arptables \
    dnsmasq \
    iftop \
    net-snmp \
    openvpn \
    traceroute \
    tunctl \
    autossh \
    "
