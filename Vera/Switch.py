from Device import Device

class Switch(Device):
    """
    A switch instance represent a switch device
    inhert from device class, link to a vera instance
    """
    def __init__(self, vera, device_id):
        Device.__init__(self, vera, device_id)
        self.device_id = device_id
        self.device_type = __name__
        
    def on(self):
        pass
    
    #call directly
    def poll(self):
        #self.vera.comm.poll()
        raise Exception("not allow to raise the ")
    
    def status(self):
        self.vera.comm.status(self.device_id)
    
    def info(self):
        print "--------SWITCH INFO---------"
        print "VERA NETWORK: ", self.vera.vera_ip, ":", self.vera.vera_port
        print "device:", self.device_type, "(", self.device_id, ")"
    
    