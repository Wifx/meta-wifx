#!/bin/sh

# Update gateway ID based on the eth0 MAC address
echo "TTN packet-forwarder: updating configuration files with gateway ID"
/opt/lorix/utils/update_gwid.sh /opt/lorix/clouds/ttn/global_conf.json
/opt/lorix/utils/update_gwid.sh /opt/lorix/clouds/ttn/EU_global_conf_2dBi_indoor.json
/opt/lorix/utils/update_gwid.sh /opt/lorix/clouds/ttn/EU_global_conf_4dBi_outdoor.json
/opt/lorix/utils/update_gwid.sh /opt/lorix/clouds/ttn/local_conf.json
