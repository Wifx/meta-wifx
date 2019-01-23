SUMMARY = "LoRa SX1301 based gateway"
DESCRIPTION = "LoRa SX1301 based gateway library and tools."

SRC_URI = " \
    git://github.com/Wifx/lora_gateway.git;protocol=git;tag=${KTAG} \
    file://configs/global_conf_EU868_4dBi_outdoor.json \
    file://configs/global_conf_US915_4dBi_outdoor.json \
    file://configs/global_conf_AU915_4dBi_outdoor.json \
    file://configs/global_conf_AS2_4dBi_outdoor.json \
    file://configs/local_conf.json \        
    "

S = "${WORKDIR}/git"

RDEPENDS_${PN} += "update-gwid"

EXTRA_OEMAKE = " \
	'CC=${CC}' 'CFLAGS=${CFLAGS} \
	-I${S}/util_pkt_logger/inc \
	-I${S}/libloragw -I${S}/libloragw/inc' \
	'BUILDDIR=${S}' \
	"
	
TESTSDIR="/opt/lorix/tests"
UTILSDIR="/opt/lorix/utils"
	
TARGET_TYPE_FILE = "/sys/bus/i2c/devices/4-0060/product_type"

do_install () {
	install -d ${D}${libdir}/${BPN}/inc \
		   ${D}${includedir}/${BPN}/inc \
		   ${D}${UTILSDIR} \
 		   ${D}${UTILSDIR}

	install -m 0644 ${S}/libloragw/libloragw.a ${D}${libdir}/${BPN}
	install -m 0644 ${S}/libloragw/library.cfg ${D}${libdir}/${BPN}

	install -m 0644 ${S}/libloragw/inc/* ${D}${libdir}/${BPN}/inc
	install -m 0644 ${S}/libloragw/inc/* ${D}${includedir}/${BPN}/inc
	install -m 0755 ${S}/libloragw/test_loragw_* ${D}${UTILSDIR}
	install -m 0755 ${S}/util_lbt_test/util_lbt_test ${D}${UTILSDIR}

	install -m 0755 ${S}/util_pkt_logger/util_pkt_logger ${D}${UTILSDIR}	
	# configuration files
    install -m 0644 ${WORKDIR}/configs/*.json ${D}${UTILSDIR}

	install -m 0755 ${S}/util_spi_stress/util_spi_stress ${D}${UTILSDIR}
	install -m 0755 ${S}/util_tx_continuous/util_tx_continuous ${D}${UTILSDIR}
	install -m 0755 ${S}/util_tx_test/util_tx_test ${D}${UTILSDIR}
}

pkg_postinst_${PN}_append () {
    #!/bin/sh
    if [ x"$D" = "x" ]; then
        # Script is executed during the runtime installation

        if [ -f ${TARGET_TYPE_FILE} ]; then
            TYPE=$(cat ${TARGET_TYPE_FILE})
        else
            echo "Error while detecting gateway type, default type to EU868"
            TYPE="EU868"
        fi
        
        # Update gateway ID based on the eth0 MAC address
        echo "Updating configuration files with gateway ID, type $TYPE"

        case $TYPE in
            "EU868")
            mv -f ${UTILSDIR}/global_conf_EU868_4dBi_outdoor.json ${UTILSDIR}/global_conf.json >/dev/null 2>&1
            rm -f ${UTILSDIR}/global_conf_US915_4dBi_outdoor.json >/dev/null 2>&1
            rm -f ${UTILSDIR}/global_conf_AU915_4dBi_outdoor.json >/dev/null 2>&1
            rm -f ${UTILSDIR}/global_conf_AS2_4dBi_outdoor.json >/dev/null 2>&1
            ;;
            "US915")
            mv -f ${UTILSDIR}/global_conf_US915_4dBi_outdoor.json ${UTILSDIR}/global_conf.json >/dev/null 2>&1
            rm -f ${UTILSDIR}/global_conf_EU868_4dBi_outdoor.json >/dev/null 2>&1
            rm -f ${UTILSDIR}/global_conf_AU915_4dBi_outdoor.json >/dev/null 2>&1
            rm -f ${UTILSDIR}/global_conf_AS2_4dBi_outdoor.json >/dev/null 2>&1
            ;;
            "AU915")
            mv -f ${UTILSDIR}/global_conf_AU915_4dBi_outdoor.json ${UTILSDIR}/global_conf.json >/dev/null 2>&1
            rm -f ${UTILSDIR}/global_conf_EU868_4dBi_outdoor.json >/dev/null 2>&1
            rm -f ${UTILSDIR}/global_conf_US915_4dBi_outdoor.json >/dev/null 2>&1
            rm -f ${UTILSDIR}/global_conf_AS2_4dBi_outdoor.json >/dev/null 2>&1
            ;;
            "AS2")
            mv -f ${UTILSDIR}/global_conf_AU915_4dBi_outdoor.json ${UTILSDIR}/global_conf.json >/dev/null 2>&1
            rm -f ${UTILSDIR}/global_conf_EU868_4dBi_outdoor.json >/dev/null 2>&1
            rm -f ${UTILSDIR}/global_conf_US915_4dBi_outdoor.json >/dev/null 2>&1
            rm -f ${UTILSDIR}/global_conf_AU915_4dBi_outdoor.json >/dev/null 2>&1
            ;;
        esac
    
	    # Update gateway ID based on the eth0 MAC address
	    ${UTILSDIR}/update_gwid.sh ${UTILSDIR}/global_conf.json
	    ${UTILSDIR}/update_gwid.sh ${UTILSDIR}/local_conf.json
    else
        # Script is executed during the rootfs construction
	    exit 1
    fi
}

FILES_${PN} =+ "${UTILSDIR}/* ${UTILSDIR}/*"

FILES_${PN}-staticdev =+ "${libdir}/${BPN}/*.cfg"

