SUMMARY = "LORIOT application"
DESCRIPTION = "LORIOT LoRaWAN cloud application"

LICENSE = "LORIOT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=63722c69679e7db558ef8786ac6de1b1"

SRC_URI = " \
	file://LICENSE \
	file://loriot_lorix_one_SPI_2.6.828-JKS-EU-9 \
	file://init \
	"

PR = "r0"
S = "${WORKDIR}"

RDEPENDS_${PN} += "libcrypto openssl reset-lgw"

inherit update-rc.d

INITSCRIPT_NAME = "loriot-gw"
INITSCRIPT_PARAMS = "stop 70 0 1 6 ."

RUNNING_FILE = "/var/tmp/${INITSCRIPT_NAME}_is_running"

do_install () {
	install -d ${D}/opt/lorix/clouds/loriot
	install -m 0755 ${WORKDIR}/loriot_lorix_one_SPI_2.6.828-JKS-EU-9 ${D}/opt/lorix/clouds/loriot/loriot-gw

	# init script
	install -d ${D}${sysconfdir}/init.d
	install -m 0755 ${WORKDIR}/init ${D}${sysconfdir}/init.d/${INITSCRIPT_NAME}
}

pkg_prerm_${PN}_prepend () {
    #!/bin/sh

    # test if service is running
    ${sysconfdir}/init.d/${INITSCRIPT_NAME} status >/dev/null 2>&1

    if [ $? -eq 0 ]; then
        touch ${RUNNING_FILE}
        echo "Stopping LORIOT cloud service"
        ${sysconfdir}/init.d/${INITSCRIPT_NAME} stop
    else
        rm ${RUNNING_FILE} >/dev/null 2>&1
    fi
}

pkg_postinst_${PN}_append () {
    #!/bin/sh
    if [ x"$D" = "x" ]; then
        # Script is executed during the runtime installation

	    if [ -f "${RUNNING_FILE}" ]; then
	        echo "Restarting LORIOT cloud service"
	        ${sysconfdir}/init.d/${INITSCRIPT_NAME} start
	        rm ${RUNNING_FILE}
	    fi
    else
        # Script is executed during the rootfs construction
	    exit 1
    fi
}

FILES_${PN} =+ " \
    /opt/lorix/clouds/loriot/* \
    ${sysconfdir}/init.d/${INITSCRIPT_NAME} \
    "
    
INHIBIT_PACKAGE_STRIP = "1"

