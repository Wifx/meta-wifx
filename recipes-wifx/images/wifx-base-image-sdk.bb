require wifx-base-image.bb

DESCRIPTION = "Image with LORIX One support that includes everything within \
wifx-base-image plus meta-toolchain, development headers and libraries to \
form a standalone SDK."

IMAGE_FEATURES += "dev-pkgs tools-sdk \
	tools-debug eclipse-debug tools-profile tools-testapps debug-tweaks ssh-server-openssh"

IMAGE_INSTALL += "kernel-devsrc"

