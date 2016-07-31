IMAGE_FEATURES += "ssh-server-openssh package-management"

TOOLCHAIN_TARGET_TASK += "\
    openssl-dev \
    "

IMAGE_INSTALL = "\
    packagegroup-core-boot \
    packagegroup-core-full-cmdline \
    packagegroup-base-usbgadget \
    kernel-modules \
    lrzsz \
    setserial \
    opkg \
    iperf \
    \
    nbench-byte \
    lmbench \
    \
    i2c-tools \
    devmem2 \
    dosfstools \
    mtd-utils \
    dtc \
    dtc-misc \
    iproute2 \
    iptables \
    bridge-utils \
    evtest \
    \
    python-pyserial \
    python-smbus \
    python-ctypes \
    python-distribute \
    python-pycurl \
    python-flask \
    python-flask-bcrypt \
    python-flask-bootstrap \
    python-flask-login \
    python-flask-navigation \
    python-flask-pymongo \
    python-flask-sqlalchemy \
    python-flask-wtf \
    \
    gdbserver \
    usbutils \
    wget \
    ca-certificates \
    ${CORE_IMAGE_EXTRA_INSTALL} \
    "

inherit core-image
