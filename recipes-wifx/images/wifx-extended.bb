DESCRIPTION = "Image with LORIX One support + extended libraries, applications and services"

LICENSE = "MIT"
PR = "r0"

require wifx-base.bb

IMAGE_FEATURES += ""

IMAGE_INSTALL += "\
    python \
    python-psutil \
    python-netifaces \
    python-ujson \
    python-cryptography \
    python-pyserial \
    python-setuptools \
    \
    python3 \
    python3-pip \
    python3-setuptools \
    \
    curl \
    \
    arptables \
    ebtables \
    dnsmasq \
    iftop \
    net-snmp \
    netcat \
    openvpn \
    traceroute \
    tunctl \
    autossh \
    tcpdump \
    wireshark \
    nginx \
    monkey \
    lighttpd \
    \
    watchdog \
    "
    
