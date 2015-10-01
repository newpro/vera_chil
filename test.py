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
light1 = s.Switch(myvera, 14)
#light1.info()
#light1.vera.comm.poll()
#light1.poll()
#if (light1.status()):
#    light1.on()

if(not light1.status()):
    light1.on()
else:
    light1.off()