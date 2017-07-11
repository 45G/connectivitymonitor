wlanip=$(ip ro | grep wlan0 | awk '{print $9}')

if [ "x${wlanip}x" == "xx" ]; then
echo "Please start WiFi"
exit 1
fi

echo ${wlanip}