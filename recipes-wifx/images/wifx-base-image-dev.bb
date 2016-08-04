require wifx-base-image.bb

DESCRIPTION = "Image with LORIX One support that includes everything within \
wifx-base-image plus meta-toolchain, application development and \
testing libraries, profiling and debug symbols."

IMAGE_FEATURES += "dev-pkgs"

