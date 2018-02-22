#!/bin/sh

# PATH should only include /usr/* if it runs after the mountnfs.sh script
PATH=/usr/sbin:/sbin:/bin:/usr/bin:/usr/local/bin
DESC="LORIX One clouds manager"
NAME=clouds-manager.sh
SCRIPTNAME=/etc/init.d/$NAME
CONFIGFILE_NAME=clouds.conf
CONFIGFILE=/etc/lorix/$CONFIGFILE_NAME

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
          echo -n "Starting cloud $cloud... "
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
          echo "Unknown cloud $cloud, abort."
          ;;
      0)
          echo "Cloud $cloud already running, abort."
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
            echo "Cloud $cloud already stopped, abort."
            ;;
        1)
            echo "Unknown cloud $cloud, abort."
            ;;
        0)
            echo -n "Stopping cloud $cloud... "
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
        *)
            # clean cloud variable
            cloud=""
            return 1
            ;;
    esac
   
    # is the cloud running ?
    [[ -f $init_file ]] && $init_file status 2>&1 > /dev/null
    run=$?
    if [ $run -ne 0 ]; then
        run=2
    fi 
}

do_configure()
{
    echo "=========================================="
    echo "| LORIX One clouds manager configuration |"
    echo "=========================================="
    echo ""
    echo "Actual configuration:"
    echo "  autostart=$autostart"
    echo "      cloud=$cloud"
    echo ""

    if [ $run -eq 0 ]; then
        echo "Cloud $cloud actually running, please stop it before modifying parameters"
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
        echo "[loriot|packet-forwarder|ttn]"
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
            *)
                echo "Wrong value, please retry."
                ;;
        esac
    done
    
    echo ""
    echo "New configuration:"
    echo "  autostart=$autostart"
    echo "      cloud=$cloud"
    echo ""

    write_param autostart $autostart
    write_param cloud $cloud
    
    return 0
}

# retrieve params from config file
set_env

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
            echo "Cloud $cloud status:"
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

