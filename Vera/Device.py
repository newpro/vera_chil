from Vera import Vera
'''
This is abstract class, all devices inhert from this
disallow for create directly
'''

class Device(Vera):
    
    def __init__(self, device_id, vera_ip="192.168.0.100", vera_port=49451):
        #print self.__class__
        if self.__class__ == Device:
            raise TypeError("Device class creation disallowed")
        Vera.__init__(self, vera_ip = vera_ip, vera_port = vera_port)
        self.device_id = device_id
    