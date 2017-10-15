import time

def read_bytes (card, direction):
    with open('/sys/class/net/'+card+'/statistics/'+direction+'_bytes', 'r') as file:
        data = file.read()
        return int(data)

rx_wlan_bytes = read_bytes('wlan0', 'rx')
rx_lte_bytes = read_bytes('rmnet0', 'rx')

tx_wlan_bytes = read_bytes('wlan0', 'tx')
tx_lte_bytes = read_bytes('rmnet0', 'tx')

print time.time(), rx_wlan_bytes, rx_lte_bytes, tx_wlan_bytes, tx_lte_bytes

