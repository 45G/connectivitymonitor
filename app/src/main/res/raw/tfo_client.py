#!/usr/bin/env python

import sys, socket
from datetime import datetime

HOST = "jepi.cs.pub.ro"
#HOST = "px.bot.nu"
PORT = 1888

TCP_FASTOPEN = 23
MSG_FASTOPEN = 0x20000000
BUFSIZE = 1500

before = datetime.now()
s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

# \n marks the end of the message sent
s.sendto("Hello, world!\n", MSG_FASTOPEN, (HOST, PORT))
data = s.recv(BUFSIZE)
after = datetime.now()
if data: 
    print data, ":", (after-before).seconds*1000.0 + (after-before).microseconds/1000.0, "ms"
s.close()
