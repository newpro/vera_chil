"""
this file contain the test/usage example for the API

Warning: this requires you r in the same network with Vera
"""

from Vera import Vera as v

#find vera IP address
#vera_ip = v.find_vera()

#Usage of Vera class
myvera = v.Vera()
print myvera.get_wave_status(update=False) #get if the wave is alive

from Vera import Switch as s
light1 = s.Switch(14)
light1.info()