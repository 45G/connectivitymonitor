wlangate=$(ip ro sh table wlan0 | grep default | awk '{print $3}')

if [ "x${wlangate}x" == "xx" ]; then
echo "Please start WiFi"
exit 1
fi

echo ${wlangate}