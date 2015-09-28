from Device import Device


class Switch(Device):
    
    #
    def __init__(self, device_id, vera_ip="192.168.0.100", vera_port=49451):
        Device.__init__(self, device_id, vera_ip=vera_ip, vera_port=vera_port)
        self.device_id = device_id
        self.device_type = __name__
        
    def on(self):
        pass
    
    def info(self):
        print "--------SWITCH INFO---------"
        print "VERA NETWORK: ", self.vera_ip, ":", self.vera_port
        print "device:", self.device_type, "(", self.device_id, ")"
    
    