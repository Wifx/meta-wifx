IMAGE_FEATURES += "ssh-server-openssh package-management"

TOOLCHAIN_HOST_TASK_append = " golang-cross-canadian-${TRANSLATED_TARGET_ARCH}"

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
    ntp \
    openssl \
    openssl-misc \
    ca-certificates \
    perl \
    resolvconf \
    reset-lgw \
    lora-gateway \
    update-gwid \
    packet-forwarder \
    loriot \
    ${CORE_IMAGE_EXTRA_INSTALL} \
    "

inherit core-image

# lorix4u encrypted password
inherit extrausers
#EXTRA_USERS_PARAMS = "\
#    usermod -P 'lorix4u' root; \
#    "
