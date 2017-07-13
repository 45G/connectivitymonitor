#!/usr/bin/env python

import time,sys,argparse,socket,datetime
from datetime import datetime
from time import gmtime, strftime

def filter_verbose (s, v):
    if int(args.verbose) >= v:
        return s
    else:
        return ""

if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('-l', '--length', default = 1)
    parser.add_argument('-tfo', dest='tfo', action='store_true')  
    parser.add_argument('-datartt', dest='datartt', action='store_true')
    parser.add_argument('-v', dest='verbose', default = 0)
    parser.add_argument('-c', dest='count', default = 1)
    parser.add_argument('-I', dest='interface')
    parser.add_argument('-i', dest='interval', default = 1.0)
    parser.add_argument("destination")
    parser.add_argument("port")
    args = parser.parse_args()
# datartt measures only the RTT of data and echo, without connection setup
# its value should be close to ICMP ping  
  
destination = args.destination
port = int(args.port) 
psize = int(args.length) 
count = int(args.count) 
interface = args.interface
interval = float(args.interval)
datartt = args.datartt
tfo = args.tfo 

if tfo and datartt:
    print "datartt and tfo cannot be active at the same time"
    exit(1)

if datartt:
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    if args.interface != None: 
        s.setsockopt(socket.SOL_SOCKET, 25, args.interface + '\0')
    s.connect((destination, port))

while count != 0:
    count = count - 1
    before = datetime.now()
    if not datartt: 
        s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        if args.interface != None: 
            s.setsockopt(socket.SOL_SOCKET, 25, args.interface + '\0')
        if not tfo:    
            s.connect((destination, port))

    if tfo:
        MSG_FASTOPEN = 0x20000000
        sbytes = s.sendto((time.ctime() + 'X' * psize +  '\n').encode(), MSG_FASTOPEN, (destination, port))
    else:
        sbytes = s.send((time.ctime() + 'X' * psize +  '\n').encode())

    data = s.recv(sbytes)
    rbytes = len(data)
    while rbytes < sbytes:
        #print "s= ", sbytes, "r= ", rbytes
        data = s.recv(sbytes - rbytes)
        #if data: 
        #    print data.decode()
        rbytes = rbytes + len(data)
    after = datetime.now()
    print filter_verbose ("src: " +  str(s.getsockname()[0]) + " port: " + str(s.getsockname()[1]), 1), strftime("%s", gmtime()),\
                          "bytes:", sbytes, "ms:", (after-before).seconds*1000.0 + (after-before).microseconds/1000.0
    if not datartt:
        s.close()
    data = data.decode()

    if count != 0:
        time.sleep(interval)
