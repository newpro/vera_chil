import requests

#check if the variable is not empty. if it is, raise exception  
def _s(var):
    if var == None: 
        raise Exception("undefined variable, throw from Comm")
    return str(var)

class Comm:
    """
    This is the communication module for all classes
    Use to extract the process of send and receive request, and parse raw data
    Each Comm class belong's to one Vera
    
    implementation of this module is based on here: http://wiki.micasaverde.com/index.php/Luup_Requests
    
    notes: sdata not working, API broken
    
    TYPES:
        1. user_data
        2. status
        3. device
        4. room
        _____lua control_____
        5. action
    """

    #command_url = "http://192.168.0.100:3480/data_request?id=lu_action&output_format=json&DeviceNum=14&serviceId=urn:upnp-org:serviceId:SwitchPower1&action=SetTarget&newTargetValue=1"
    
    def __init__(self, vera_ip="192.168.0.100", vera_port=49451):
        self.vera_ip = vera_ip
        self.vera_port = vera_port
    
    #hardcoded url generator    
    def _url_gen(self, request_type, device_id=None, action = None, new_value=None, room=None):
        result = "http://" + _s(self.vera_ip) + ":" + _s(self.vera_port) + "/data_request?"
        
        if(request_type == "user_data"):
            result += "id=user_data&output_format=json"
            
        elif (request_type == "status"):
            result +="id=status&output_format=json"
            result = result + "&DeviceNum=" + _s(device_id)
                
        elif (request_type == "device"):
            result += "id=device"
            #action have to be rename/delete
            if(action == "rename"):
                #example: &action=rename&device=5&name=Chandalier&room=Garage
                result = result + "&action=rename" + "&device=" + _s(device_id) + "&name" + _s(new_value) + "&room=" + _s(room)
            elif (action == "delete"):
                #example: action=delete&device=5
                result = result + "&action=delete&device=" + _s(device_id)
            else:
                raise Exception("CRITICAL: device request type does not match!!!")
            
        elif (request_type == "room"):
            result += "id=room"
            #action is create/rename/delete
            if(action == "create"):
                result = result + "&action=create&name=" + _s(new_value)
            elif (action == "rename"):
                result = result + "&action=rename&room=" + _s(room) + "&name=" + _s(new_value)
            elif (action == "delete"):
                result = result + "&action=delete&room=" + _s(room)
            else:
                raise Exception("CRITICAL: room request type does not match")
            
        elif (request_type == "action"):
            result += "id=action&output_format=json"            
            if (device_id=="lights"): #Aziz, light!
                result += "&Category=999"
            elif (device_id == "dimmable lights"):
                result += "&Category=2"
            elif (device_id == "binary lights"):
                result += "&Category=3"
            else:
                result = result + "&DeviceNum=" + _s(device_id)
            #support turn/dimm
            if(action == "turn"):
                #&DeviceNum=6&serviceId=urn:upnp-org:serviceId:SwitchPower1&action=SetTarget&newTargetValue=0
                result = result + "&serviceId=urn:upnp-org:serviceId:SwitchPower1&action=SetTarget&newTargetValue=" + _s(new_value)
            elif (action == "dimm"):
                #&DeviceNum=7&serviceId=urn:upnp-org:serviceId:Dimming1&action=SetLoadLevelTarget&newLoadlevelTarget=30
                result = result + "&serviceId=urn:upnp-org:serviceId:Dimming1&action=SetLoadLevelTarget&newLoadlevelTarget=" + _s(new_value)
            else:
                raise Exception("action type not supported")
            
        elif (request_type == "variableget"):
            #http://192.168.0.100:3480/data_request?id=variableget&DeviceNum=14&serviceId=urn:upnp-org:serviceId:SwitchPower1&Variable=Status
            result += "id=variableget"
            result = result + "&DeviceNum=" + _s(device_id) + "&serviceId=urn:upnp-org:serviceId:SwitchPower1&Variable=Status"
            
        return result
    
    #--------------call specific level (wapper)-------------------
    
    #Request full information, prase and return
    def poll(self):
        url = self._url_gen("user_data")
        print url
        resp = requests.get(url).json()
        
        for device in resp["devices"]:
            #print "T: " + device["device_type"]#[35:-2]
            target = device["device_type"]
            target = target.split(":")
            
            print "type:", target[3]
            print "name:", device["name"]
            print "id:", device["id"]
            try:
                print "room: ", device["room"]
            except:
                pass
            
            try:
                print "par: ", device["id_parent"]
            except:
                pass
            
            print "-----------------------"
    
    def status(self, device_id):
        """
        return the on/off status of one device
        """
        url = self._url_gen("variableget", device_id=device_id)
        print url
        resp = requests.get(url).json()
        
        if (resp == 0):
            return False
        
        else:
            return True
    
    def on(self, device_id):
        """
        Turn on the light
        """
        url = self._url_gen("action",action="turn",new_value=1,device_id=device_id)
        resp = requests.get(url).json()
        print "job", resp["u:SetTargetResponse"]["JobID"], "finished successful"
    
    def off(self, device_id):
        """
        Turn off the light
        """
        url = self._url_gen("action",action="turn",new_value=0,device_id=device_id)
        resp = requests.get(url).json()
        print "job", resp["u:SetTargetResponse"]["JobID"], "finished successful"