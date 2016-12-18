require wifx-base.bb

DESCRIPTION = "Image with LORIX One support that includes everything within \
wifx-base-image plus meta-toolchain, application development and \
testing libraries, profiling and debug symbols."

IMAGE_FEATURES += "debug-tweaks tools-sdk tools-debug dev-pkgs"

