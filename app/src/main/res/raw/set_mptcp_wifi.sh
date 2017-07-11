wlanip=$(ip ro | grep wlan0 | awk '{print $9}')

if [ "x${wlanip}x" == "xx" ]; then 
echo "Please start WiFi"
exit 1
fi

echo ${wlanip}

table2=$(ip rule | grep "lookup 2")

if [ "x${table2}x" != "xx" ]; then
ip rule del table 2
fi

ip rule add from $wlanip table 2

wlangw=$(ip route show table wlan0 | grep via | awk '{print $3}')
wlannet=$(ip ro | grep wlan0 | awk '{print $1}')

ip route add $wlannet dev wlan0 scope link table 2
ip route add default via $wlangw dev wlan0 table 2


