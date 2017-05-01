#!/bin/sh

# Update gateway ID based on the eth0 MAC address
echo "Semtech packet-forwarder: updating configuration files with gateway ID"
/opt/lorix/utils/update_gwid.sh /opt/lorix/clouds/packet-forwarder/global_conf.json
/opt/lorix/utils/update_gwid.sh /opt/lorix/clouds/packet-forwarder/global_conf_2dBi_indoor.json
/opt/lorix/utils/update_gwid.sh /opt/lorix/clouds/packet-forwarder/global_conf_4dBi_outdoor.json
/opt/lorix/utils/update_gwid.sh /opt/lorix/clouds/packet-forwarder/local_conf.json
