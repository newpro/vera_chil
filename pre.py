"""
this file contain the test/usage example for the API

Warning: this requires you r in the same network with Vera
"""

from Vera import Vera as v
myvera = v.Vera()

#Add a switch to the network
from Vera import Switch as s
from Vera import Lock as l
light1 = s.Switch(myvera, 14)
lock1 = l.Lock(myvera, 11)
from time import sleep
count = 4

while count > 0 and (lock1.status()):
    count -= 1
    sleep(1)
    if(not light1.status()):
        light1.on()
    else:
        light1.off()

