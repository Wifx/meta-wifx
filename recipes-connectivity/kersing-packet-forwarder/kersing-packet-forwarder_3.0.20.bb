SUMMARY = "Kersing multipacket forwarder application"

LICENSE = "KERSING"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5d85f19ed76de4ab777629791dcfd05c"

SRC_URI = " \
    file://LICENSE \
    file://README \
    file://init \
    file://logrotate \
    file://mp_pkt_fwd \
    file://libpaho-embed-mqtt3c.so.1.0 \
    file://libttn-gateway-connector.so \
    file://configs/global_conf_EU868_2dBi_indoor.json \
    file://configs/global_conf_EU868_4dBi_outdoor.json \
    file://configs/global_conf_US915_2dBi_indoor.json \
    file://configs/global_conf_US915_4dBi_outdoor.json \
    file://configs/global_conf_AU915_2dBi_indoor.json \
    file://configs/global_conf_AU915_4dBi_outdoor.json \
    file://configs/local_conf.json \        
    "

PR = "r1"
S = "${WORKDIR}"

RDEPENDS_${PN} += "openssl reset-lgw update-gwid start-stop-daemon-ext logrotate"
PACKAGES = "${PN}-dbg ${PN} ${PN}-dev"

# FIX package name bug
PKG_${PN} = "${PN}"
PKG_${PN}-dev = "${PN}-dev"
PKG_${PN}-dbg = "${PN}-dbg"

inherit update-rc.d

RUNDIR="/opt/lorix/clouds/mp-pkt-fwd"
BKPDIR="${sysconfdir}/backup.d${RUNDIR}"

INITSCRIPT_NAME = "mp-pkt-fwd-gw"
INITSCRIPT_PARAMS = "stop 70 0 1 6 ."

RUNNING_FILE = "/var/tmp/${INITSCRIPT_NAME}_is_running"

TARGET_TYPE_FILE = "/sys/bus/i2c/devices/4-0060/product_type"

do_install () {
    # binary
    install -d ${D}${RUNDIR}
    install -m 0755 ${WORKDIR}/mp_pkt_fwd ${D}${RUNDIR}
    
    # README file
    install -m 0644 ${WORKDIR}/README ${D}${RUNDIR}
    
    # library
    oe_libinstall -C ${WORKDIR} -so -s libpaho-embed-mqtt3c ${D}${libdir}
    oe_libinstall -C ${WORKDIR} -so -s libttn-gateway-connector ${D}${libdir}
    
    # configuration files
    install -m 0644 ${WORKDIR}/configs/*.json ${D}${RUNDIR}
    
    # init script
    install -d ${D}${sysconfdir}/init.d
    install -m 0755 ${WORKDIR}/init ${D}${sysconfdir}/init.d/${INITSCRIPT_NAME}
    
    # log rotation
    install -d -m 0755 ${D}/${sysconfdir}/logrotate.d
    install -m 0644 ${WORKDIR}/logrotate ${D}${sysconfdir}/logrotate.d/${INITSCRIPT_NAME}
    
    # Factory reset directory
    install -d -m 0644 ${D}/${BKPDIR}
}

pkg_prerm_${PN}_prepend () {
    #!/bin/sh

    # test if service is running
    ${sysconfdir}/init.d/${INITSCRIPT_NAME} status >/dev/null 2>&1

    if [ $? -eq 0 ]; then
        touch ${RUNNING_FILE}
        echo "Stopping Multi packet-forwarder cloud service"
        ${sysconfdir}/init.d/${INITSCRIPT_NAME} stop
    else
        rm ${RUNNING_FILE} >/dev/null 2>&1
    fi
    
    # Backuping configuration files
    if [ -f ${RUNDIR}/global_conf.json ]; then
        echo "Backuping global_conf.json file"
        mv -f ${RUNDIR}/global_conf.json ${RUNDIR}/global_conf.json.bkp >/dev/null 2>&1
    fi
    if [ -f ${RUNDIR}/local_conf.json ]; then
        echo "Backuping local_conf.json file"
        mv -f ${RUNDIR}/local_conf.json ${RUNDIR}/local_conf.json.bkp >/dev/null 2>&1
    fi
    if [ -f ${RUNDIR}/global_conf_2dBi_indoor.json ]; then
        echo "Backuping global_conf_2dBi_indoor.json file"
        mv -f ${RUNDIR}/global_conf_2dBi_indoor.json ${RUNDIR}/global_conf_2dBi_indoor.json.bkp >/dev/null 2>&1
    fi
    if [ -f ${RUNDIR}/global_conf_4dBi_outdoor.json ]; then
        echo "Backuping global_conf_4dBi_outdoor.json file"
        mv -f ${RUNDIR}/global_conf_4dBi_outdoor.json ${RUNDIR}/global_conf_4dBi_outdoor.json.bkp >/dev/null 2>&1
    fi
    
    # Delete old factory reset files
    rm -f ${BKPDIR}/global_conf.json >/dev/null 2>&1
    rm -f ${BKPDIR}/global_conf_2dBi_indoor.json >/dev/null 2>&1
    rm -f ${BKPDIR}/global_conf_4dBi_outdoor.json >/dev/null 2>&1
    rm -f ${BKPDIR}/local_conf.json >/dev/null 2>&1
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
            mv -f ${RUNDIR}/global_conf_EU868_2dBi_indoor.json ${RUNDIR}/global_conf_2dBi_indoor.json >/dev/null 2>&1
            mv -f ${RUNDIR}/global_conf_EU868_4dBi_outdoor.json ${RUNDIR}/global_conf_4dBi_outdoor.json >/dev/null 2>&1
            rm -f ${RUNDIR}/global_conf_US915_2dBi_indoor.json >/dev/null 2>&1
            rm -f ${RUNDIR}/global_conf_US915_4dBi_outdoor.json >/dev/null 2>&1
            rm -f ${RUNDIR}/global_conf_AU915_2dBi_indoor.json >/dev/null 2>&1
            rm -f ${RUNDIR}/global_conf_AU915_4dBi_outdoor.json >/dev/null 2>&1
            ;;
            "US915")
            mv -f ${RUNDIR}/global_conf_US915_2dBi_indoor.json ${RUNDIR}/global_conf_2dBi_indoor.json >/dev/null 2>&1
            mv -f ${RUNDIR}/global_conf_US915_4dBi_outdoor.json ${RUNDIR}/global_conf_4dBi_outdoor.json >/dev/null 2>&1
            rm -f ${RUNDIR}/global_conf_EU868_2dBi_indoor.json >/dev/null 2>&1
            rm -f ${RUNDIR}/global_conf_EU868_4dBi_outdoor.json >/dev/null 2>&1
            rm -f ${RUNDIR}/global_conf_AU915_2dBi_indoor.json >/dev/null 2>&1
            rm -f ${RUNDIR}/global_conf_AU915_4dBi_outdoor.json >/dev/null 2>&1            
            ;;
            "AU915")
            mv -f ${RUNDIR}/global_conf_AU915_2dBi_indoor.json ${RUNDIR}/global_conf_2dBi_indoor.json >/dev/null 2>&1
            mv -f ${RUNDIR}/global_conf_AU915_4dBi_outdoor.json ${RUNDIR}/global_conf_4dBi_outdoor.json >/dev/null 2>&1
            rm -f ${RUNDIR}/global_conf_EU868_2dBi_indoor.json >/dev/null 2>&1
            rm -f ${RUNDIR}/global_conf_EU868_4dBi_outdoor.json >/dev/null 2>&1
            rm -f ${RUNDIR}/global_conf_US915_2dBi_indoor.json >/dev/null 2>&1
            rm -f ${RUNDIR}/global_conf_US915_4dBi_outdoor.json >/dev/null 2>&1            
            ;;
        esac
        # Create default global_conf.json based on 4Bi by default
        echo "******************************************************************************************"
        echo "Default global_conf.json file created from 4dBi outdoor antenna by default,"
        echo "don't forget to chose the one corresponding to your antenna using the following command:"
        echo "sudo cp /opt/lorix/cloud/mp-pkt-fwd/global_conf_<antenna>.json global_conf.json"
        echo "******************************************************************************************"
        cp ${RUNDIR}/global_conf_4dBi_outdoor.json ${RUNDIR}/global_conf.json

        # Update gateway ID based on the eth0 MAC address
        /opt/lorix/utils/update_gwid.sh ${RUNDIR}/global_conf.json
        /opt/lorix/utils/update_gwid.sh ${RUNDIR}/global_conf_2dBi_indoor.json
        /opt/lorix/utils/update_gwid.sh ${RUNDIR}/global_conf_4dBi_outdoor.json
        /opt/lorix/utils/update_gwid.sh ${RUNDIR}/local_conf.json
        
        # Copy files for factory reset management
        cp ${RUNDIR}/global_conf.json ${BKPDIR}
        cp ${RUNDIR}/global_conf_2dBi_indoor.json ${BKPDIR}
        cp ${RUNDIR}/global_conf_4dBi_outdoor.json ${BKPDIR}
        cp ${RUNDIR}/local_conf.json ${BKPDIR}

        if [ -f "${RUNNING_FILE}" ]; then
            echo "Restarting TTN packet-forwarder cloud service"
            ${sysconfdir}/init.d/${INITSCRIPT_NAME} start
            rm ${RUNNING_FILE}
        fi
    else
        # Script is executed during the rootfs construction
        exit 1
    fi
}

FILES_${PN} = " \
    ${RUNDIR}/* \
    ${BKPDIR} \
    ${libdir}/libttn-gateway-connector.so \
    ${libdir}/libpaho-embed-mqtt3c.so.1 \
    ${libdir}/libpaho-embed-mqtt3c.so.1.0 \
    ${sysconfdir}/logrotate.d/${INITSCRIPT_NAME} \
    ${sysconfdir}/init.d/${INITSCRIPT_NAME} \
    "
    
FILES_${PN}-dev = " \
    ${libdir}/libpaho-embed-mqtt3c.so \
    "

