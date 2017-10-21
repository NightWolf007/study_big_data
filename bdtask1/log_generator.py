#!/usr/bin/env python3

import sys
import random
import socket
import struct

def generate_line():
    ip_addr = socket.inet_ntoa(struct.pack('>I', random.randint(1, 0xffffffff)))
    bytes_count = random.randint(0, 100000)
    return ip_addr + ' - - [23/Jun/2016:12:00:07 +0300] "GET ssh/resolvconf.conf/host.conf ' \
           'HTTP/1.1" 200 ' + str(bytes_count) + ' "-" "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_1) ' \
           'Gecko/20100101 Thunderbird/38.2.0 Lightning/4.0.2"'

if len(sys.argv) > 2:
    path = sys.argv[1]
    lines_count = int(sys.argv[2])
    with open(path, 'w') as f:
        for i in range(lines_count):
            f.write(generate_line() + "\n")
else:
    print("Usage: log_generator.py PATH LINES_COUNT")
