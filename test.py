"""
this file contain the test/usage example for the API

Warning: this requires you r in the same network with Vera
"""

from Vera import Vera as v

#find vera IP address
#vera_ip = v.find_vera()

#Create a Vera Network first for connection
myvera = v.Vera()
#print myvera.get_wave_status(update=False) #get if the wave is alive

#Add a switch to the network

from Vera import Switch as s
from Vera import Lock as l
light1 = s.Switch(myvera, 14)
lock1 = l.Lock(myvera, 11)
"""
#light1.info()
myvera.comm.poll()
#light1.poll()
"""
'''
if(not light1.status()):
    light1.on()
else:
    light1.off()
'''

#print light1.status()
#print lock1.status()

"""
from time import sleep
count = 4

while count > 0 and (lock1.status()):
    count -= 1
    sleep(1)
    if(not light1.status()):
        light1.on()
    else:
        light1.off()
"""
print lock1.status()