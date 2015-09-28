import requests

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
    
    def __init__(self, service, device_id, vera_ip="192.168.0.100", vera_port=49451):
        self.vera_ip = vera_ip
        self.vera_port = vera_port
        self.device_id = device_id
        self.service = service
    
    #check if the variable is not empty. if it is, raise exception
    def _s(var):
        if var == None:
            raise ("CRITICAL: variable feed error, from _url_gen")
        else:
            return str(var)
    
    #hardcoded url generator    
    def _url_gen(self, request_type, device_id=None, action = None, new_value=None, room=None):
        result = "http://" + self.vera_ip + ":" + self.vera_port + "/data_request?"
        
        if(request_type == "user_data"):
            result += "id=user_data&output_format=json"
            
        else if (request_type == "status"):
            result +="id=status&output_format=json"
            result = result + "&DeviceNum=" + _s(device_id)
                
        else if (request_type == "device"):
            result += "id=device"
            #action have to be rename/delete
            if(action == "rename"):
                #example: &action=rename&device=5&name=Chandalier&room=Garage
                result = result + "&action=rename" + "&device=" + _s(device_id) + "&name" + _s(new_value) + "&room=" + _s(room)
            else if (action == "delete"):
                #example: action=delete&device=5
                result = result + "&action=delete&device=" + _s(device_id)
            else:
                raise Exception("CRITICAL: device request type does not match!!!")
            
        else if (request_type == "room"):
            result += "id=room"
            #action is create/rename/delete
            if(action == "create"):
                result = result + "&action=create&name=" + _s(new_value)
            else if (action == "rename"):
                result = result + "&action=rename&room=" + _s(room) + "&name=" + _s(new_value)
            else if (action == "delete"):
                result = result + "&action=delete&room=" + _s(room)
            else:
                raise Exception("CRITICAL: room request type does not match")
        else if (request_type == "action"):
            result += "id=action&output_format=json"            
            if (device_id=="lights"): #Aziz, light!
                result += "&Category=999"
            else if (device_id == "dimmable lights"):
                result += "&Category=2"
            else if (device_id == "binary lights"):
                result += "&Category=3"
            else:
                result = result + "&DeviceNum=" + _s(device_id)
            #support turn/dimm
            if(action == "turn"):
                #&DeviceNum=6&serviceId=urn:upnp-org:serviceId:SwitchPower1&action=SetTarget&newTargetValue=0
                result = result + "&serviceId=urn:upnp-org:serviceId:SwitchPower1&action=SetTarget&newTargetValue=" + _s(new_value)
            else if (action == "dimm"):
                #&DeviceNum=7&serviceId=urn:upnp-org:serviceId:Dimming1&action=SetLoadLevelTarget&newLoadlevelTarget=30
                result = result + "&serviceId=urn:upnp-org:serviceId:Dimming1&action=SetLoadLevelTarget&newLoadlevelTarget=" + _s(new_value)
            else:
                raise Exception("action type not supported")
        else if (request_type == "variableget"):
            #http://192.168.0.100:3480/data_request?id=variableget&DeviceNum=14&serviceId=urn:upnp-org:serviceId:SwitchPower1&Variable=Status
            result += "id=variableget"
            result = result + "DeviceNum=" + _s(device_id) + "&serviceId=urn:upnp-org:serviceId:SwitchPower1&Variable=Status"
        
                
        
    
    
                
        