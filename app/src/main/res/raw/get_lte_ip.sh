lteip=$(ip ro | grep rmnet0 | awk '{print $9}')

if [ "x${lteip}x" == "xx" ]; then
echo "Please start Mobile Data"
exit 1
fi

echo ${lteip}