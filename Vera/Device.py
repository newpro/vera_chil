from Vera import Vera
'''
This is abstract class, all devices inhert from this
disallow create directly
'''

class Device():
    
    def __init__(self, vera, device_id):
        #print self.__class__
        if self.__class__ == Device:
            raise TypeError("Device class creation disallowed")
        #Vera.__init__(self, vera_ip = vera_ip, vera_port = vera_port)
        self.device_id = device_id
        self.vera = vera