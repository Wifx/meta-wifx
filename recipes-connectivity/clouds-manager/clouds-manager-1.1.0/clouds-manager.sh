#!/bin/sh

# PATH should only include /usr/* if it runs after the mountnfs.sh script
PATH=/usr/sbin:/sbin:/bin:/usr/bin:/usr/local/bin
DESC="LORIX One clouds manager"
NAME=clouds-manager.sh
SCRIPTNAME=/etc/init.d/$NAME
CONFIGFILE_NAME=clouds.conf
CONFIGFILE=/etc/lorix/$CONFIGFILE_NAME

# Manual cloud client
# Replace this path by your customized cloud application start script
MANUAL_INIT_FILE="/etc/init.d/manual-gw"
MANUAL_CLOUD_NAME="manual"

ask_sudo()
{
    sudo -nv 2> /dev/null
    SUDOCREDCACHED=$?
    if [ $SUDOCREDCACHED != 0 ] ; then 
	    # acquire credentials
        sudo -v
        if [ $? != 0 ] ; then 
            exit 1
        fi
    fi
}

param_exists() {
    awk '{ 
        if (match($0,"('$1')=[a-zA-Z0-9-]*$", a)) { 
            found=1
            exit; 
        }
    } END { 
        if(found==1) { print "true"; } else { print "false" };
    }' $CONFIGFILE
}

read_param() {
    awk '{ 
        if (match($0,"'$1'=([a-zA-Z0-9-]+)$", a)) { 
            print a[1]; 
            exit; 
        }
    }' $CONFIGFILE
}

replace_param () {
    sudo sed -i 's,^\('$1'[ ]*=\).*,\1'$2',g' $CONFIGFILE
}

write_param () {
    exists=$(param_exists $1)
    # if param not exists, append it at the end of the file
    if [ $exists = "false" ]; then
        sudo echo $1=$2 >> $CONFIGFILE
    else
        replace_param $1 $2
    fi
}

do_start()
{
    case $run in
      2)
          # if called from init, start only if autostart is set
          if [[ $initcall = true && $autostart = false ]]; then
              echo "Cloud manager autostart disabled"
              exit 0
          fi
          
          echo -n "Starting cloud $cloud_name... "
          
          sudo $init_file start
          ret=$?
          if [ $ret -eq 0 ]; then
              echo "done."
          else
              echo "fail."
          fi
          return $ret
          ;;
      1) 
          echo "Unknown cloud $cloud_name, abort."
          ;;
      0)
          echo "Cloud $cloud_name already running, abort."
          ;;
      *)
          echo "Unknown error, abort."
          ;;
    esac
    return $run
}

do_stop()
{
    case $run in
        2)
            echo "Cloud $cloud_name already stopped, abort."
            ;;
        1)
            echo "Unknown cloud $cloud_name, abort."
            ;;
        0)
            echo -n "Stopping cloud $cloud_name... "
            sudo $init_file stop
            ret=$?
            if [ $ret -eq 0 ]; then
                echo "done."
            else
                echo "fail."
            fi
            return $ret
            ;;
        *)
            echo "Unknown error, abort."
            ;;
    esac
    return $run
}

set_cloud_name()
{
    case $cloud in
        loriot)
            cloud_name="loriot"
            ;;
        packet-forwarder)
            cloud_name="packet-forwarder"
            ;;
        ttn)
            cloud_name="ttn"
            ;;
        manual)
            init_file=$MANUAL_INIT_FILE
            if [ -z $MANUAL_CLOUD_NAME ]; then
                cloud_name="manual"
            else
                cloud_name="manual ($MANUAL_CLOUD_NAME)"
            fi
            ;;
        *)
            ;;
    esac
}

set_env()
{
    if [[ ! -z $CALLED_FROM_INIT__ && $CALLED_FROM_INIT__ = "true" ]]; then
       initcall=true
    else
       initcall=false
    fi

    autostart=$(read_param autostart)

    cloud=$(read_param cloud)
    case $cloud in
        loriot)
            init_file="/etc/init.d/loriot-gw"
            ;;
        packet-forwarder)
            init_file="/etc/init.d/packet-forwarder-gw"
            ;;
        ttn)
            init_file="/etc/init.d/ttn-gw"
            ;;
        manual)
            init_file=$MANUAL_INIT_FILE
            ;;
        *)
            # clean cloud variable
            cloud=""
            run=1
            return 1
            ;;
    esac
    set_cloud_name

    return 0
}

do_configure()
{
    print_header_config
    echo "Actual configuration:"
    echo "  autostart=$autostart"
    echo "      cloud=$cloud_name"
    echo ""

    if [ $run -eq 0 ]; then
        echo "Cloud $cloud_name actually running, please stop it before modifying parameters"
        echo "using the command /etc/init.d/clouds-manager.sh stop"
        return 1
    fi
    
    # configure autostart
    while true; do
        echo "Do you want to enable autostart at boot time?"
        echo "[Yes|No]"
        read -p " > " yn
        
        case $yn in
            [Yy]* )
                autostart=true
                break
                ;;
            [Nn]* )
                autostart=false
                break
                ;;
            *)
                echo "Wrong value, please retry."
                ;;
        esac
    done
    
    echo ""

    # configure cloud
    while true; do
        echo "Which cloud app. do you want to use ?"
        echo "[loriot|packet-forwarder|ttn|manual]"
        read -p " > " cloud
 
        case $cloud in 
            loriot)
                break
                ;;
            packet-forwarder)
                break
                ;;
            ttn)
                break
                ;;
            manual)
                break
                ;;
            *)
                echo "Wrong value, please retry."
                ;;
        esac
    done
    
    set_cloud_name
    echo ""
    echo "New configuration:"
    echo "  autostart=$autostart"
    echo "      cloud=$cloud_name"
    echo ""

    write_param autostart $autostart
    write_param cloud $cloud
    sync $CONFIGFILE
    
    return 0
}

print_header_config()
{
    echo "=========================================="
    echo "| LORIX One clouds manager configuration |"
    echo "=========================================="
    echo ""
}

print_header_error()
{
    echo "=========================================="
    echo "| LORIX One clouds manager manual conf   |"
    echo "=========================================="
    echo ""
}

# retrieve params from config file
set_env

if [ "$1" != "configure" ]; then
    if [ -z $cloud ]; then
        echo "No cloud actually configured, use \"$0 configure\" to configure it"
        echo ""
        exit 0
    elif [ "$cloud" == "manual" ]; then
        # test if MANUAL_INIT_FILE exists
        if [ -z "$MANUAL_INIT_FILE" ]; then
            print_header_error
            echo "MANUAL_INIT_FILE not defined, please define it by editing the script \"/etc/init.d/clouds-manager.sh\""
            echo ""
            exit 0
        fi

        # test if script by MANUAL_INIT_FILE exists
        if [ ! -f "$MANUAL_INIT_FILE" ]; then
            print_header_error
            echo "The script file defined by MANUAL_INIT_FILE variable"
            echo "\"$MANUAL_INIT_FILE\" does not exist."
            echo "Please modify it by editing the script \"/etc/init.d/clouds-manager.sh\""
            echo ""
            exit 0
        fi

        # test if script is execitable
        if [ ! -x "$MANUAL_INIT_FILE" ]; then
            print_header_error
            echo "The script file defined by MANUAL_INIT_FILE variable"
            echo "\"$MANUAL_INIT_FILE\" is not executable."
            echo ""
            exit 0
        fi
    fi
fi

# is the cloud running ?
[[ -f $init_file ]] && $init_file status 2>&1 > /dev/null
run=$?
if [ $run -ne 0 ]; then
    run=2
fi

case "$1" in
    start)
        ask_sudo
        do_start
        ;;
    stop)
        ask_sudo
        do_stop
        ;;
    restart|force-reload)
        $0 stop
        $0 start
        ;;
    status)
        if [ -z $cloud ]; then
            echo "Configured cloud parameter wrong or empty."
            exit 1
        else
            echo "Cloud $cloud_name status:"
            $init_file status
            exit $?
        fi
        ;;
    configure)
        ask_sudo
        do_configure
        exit $?
        ;;
    *)
        echo "Usage: $SCRIPTNAME {start|stop|restart|force-reload|status|configure}" >&2
        exit 3
        ;;
esac

exit 0

