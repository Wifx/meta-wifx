DESCRIPTION = "Image with LORIX One support."

LICENSE = "MIT"
PR = "r0"

IMAGE_FEATURES += "debug-tweaks"

require wifx-base.inc

# Override users parameters for debug and allow root user without password
EXTRA_USERS_PARAMS = " \
    useradd -p '\$1\$OrJYw4GK\$z39VfXDWPb0IENfzDr7GM.' admin; \
    usermod -a -G sudo admin; \
    "
    
