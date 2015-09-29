from Vera import Vera
'''
This is abstract class, all devices inhert from this
disallow create directly
'''

class Device():
    
    def __init__(self, vera, device_id):
        if self.__class__ == Device:
            raise TypeError("Device class creation disallowed")
        self.device_id = device_id
        self.vera = vera
    