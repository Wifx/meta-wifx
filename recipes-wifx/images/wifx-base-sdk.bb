require wifx-base-dbg.bb

DESCRIPTION = "Image with LORIX One support that includes everything within \
wifx-base-image plus meta-toolchain, development headers and libraries to \
form a standalone SDK."

IMAGE_FEATURES += "debug-tweaks tools-sdk tools-debug dev-pkgs  \
	 eclipse-debug tools-profile tools-testapps ssh-server-openssh"

IMAGE_INSTALL += "kernel-devsrc"

