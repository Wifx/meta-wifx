SUMMARY = "Basic init scripts which detects factory reset command"
DESCRIPTION = "This package provides the init script which detects factory reset (long press on reset button) and launches reset script and copy back some files in factory state"
AUTHOR = "Wifx Sàrl"
SECTION = "base"

LICENSE = "WIFX"
LIC_FILES_CHKSUM = "file://${WORKDIR}/LICENSE;md5=323e5da3c7dfd45dac56506af122354f"

SRC_URI = " \
        file://LICENSE \
        file://init \
	    "

PR = "r0"
S = "${WORKDIR}"

inherit update-rc.d

INITSCRIPT_NAME = "factory-reset.sh"
INITSCRIPT_PARAMS = "start 60 S ."

do_install () {
	install -d -m 755 ${D}${sysconfdir}/init.d
	install -m 0755 ${WORKDIR}/init ${D}${sysconfdir}/init.d/${INITSCRIPT_NAME}
	
	install -d -m 660 ${D}${sysconfdir}/pre-reset.d
	install -d -m 660 ${D}${sysconfdir}/post-reset.d
	install -d -m 660 ${D}${sysconfdir}/backup.d
}

pkg_postinst_${PN}_append () {
    #!/bin/sh
    if [ x"$D" = "x" ]; then
        # Script is executed during the runtime installation
        
        # launch init script in copy mode
        ${sysconfdir}/init.d/${INITSCRIPT_NAME} copy        
    else
        # Script is executed during the rootfs construction
	    exit 1
    fi
}

FILES_${PN} =+ "${sysconfdir}/init.d/${INITSCRIPT_NAME}"
    
