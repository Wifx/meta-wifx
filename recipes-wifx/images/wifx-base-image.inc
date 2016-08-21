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
    gdbserver \
    usbutils \
    wget \
    loragw \
    reset-lgw \
    ${CORE_IMAGE_EXTRA_INSTALL} \
    "

inherit core-image

EXTRA_USERS_PARAMS = "usermod -P lorix4u root;"
