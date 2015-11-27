from Vera import Vera
'''
This is abstract class, all devices inhert from this
disallow create directly

devices API is here: http://wiki.micasaverde.com/index.php/Luup_Devices
'''

class Device():
    
    def __init__(self, vera, device_id):
        if self.__class__ == Device:
            raise TypeError("Device class creation disallowed")
        self.device_id = device_id
        self.vera = vera
    