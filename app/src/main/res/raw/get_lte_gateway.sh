ltegate=$(ip ro sh table rmnet0 | grep default | awk '{print $3}')

if [ "x${ltegate}x" == "xx" ]; then
echo "Please start Mobile Data"
exit 1
fi

echo ${ltegate}