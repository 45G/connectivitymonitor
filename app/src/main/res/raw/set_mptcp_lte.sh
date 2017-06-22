lteip=$(ip ro | grep rmnet0 | awk '{print $9}')

if [ "x${lteip}x" == "xx" ]; then 
echo "Please start LTE"
exit 1
fi

table1=$(ip rule | grep "lookup 1")

if [ "x${table1}x" != "xx" ]; then
ip rule del table 1
fi

ip rule add from $lteip table 1

ltegw=$(ip route show table rmnet0 | grep via | awk '{print $3}')
ltenet=$(ip ro | grep rmnet0 | awk '{print $1}')

# Configure the two different routing tables
ip route add $ltenet dev rmnet0 scope link table 1
ip route add default via $ltegw dev rmnet0 table 1

ip route add default scope global via $ltegw dev rmnet0

