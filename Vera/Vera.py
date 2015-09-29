'''
this is the communication unit between program and vera 

Vera class is used for communicate, e.g poll state, and inhert by other class
'''

#-----------------locate boardcast IP------------------
from subprocess import Popen, PIPE
import time
from os import setsid

#command_url = "http://192.168.0.100:3480/data_request?id=lu_action&output_format=json&DeviceNum=14&serviceId=urn:upnp-org:serviceId:SwitchPower1&action=SetTarget&newTargetValue=1"

def find_vera(vera_port = 49451):
    '''
    this function give the vera ip in the network
    !!!WARNING: this function is unstable, due to the nature of UPNP call can block for variaous reasons!!!!
    So: this function is intended to call only once! and as less as possible
    Because Vera have delay, and sometime not even responed, that is why this function is not in class
    Once find, vera IP do not change easily. unless rebot or others
    '''
    timeout_time = 1
    while (timeout_time > 0): #if not find, increase scan time
        print "--------------start scanning for " + str(timeout_time)  +" secs----------------"
        #create process group (have to be group, kill have to terminate write)
        popen_process = Popen(["gssdp-discover", "-n", str(timeout_time)], stdout=PIPE)
        result = popen_process.communicate()[0].split()
        timeout_time = timeout_time + 2
        #now i need all resources that is boradcast on my target port, then find the ip
        for i,line in enumerate(result):
            if line == 'Location:': #the next string is the location information
                #ip = result[i+1]
                target = result[i+1]
                if target.find(str(vera_port)) >= 0:#if the location is boradcast on target port, it is the one
                    print "SUCCESS!!!"
                    head_index = target.find("http://") + len("http://")
                    tail_index = target.find(":", head_index)
                    vera_ip = target[head_index:tail_index]
                    print "IP LOCATED: ", vera_ip
                    return vera_ip
        
        if timeout_time > 10: #more than 10 secs scan time, probably something goes wrong
            raise Exception("CRITICAL: timeout quota reached, no Vera find\nVera do this sometime, u might want to try later.")

import requests
from Comm import Comm

class Vera:
    """
    A Vera instance represent a Vera network (a Vera Router)
    the reason to isolate the class is to allow the user to connect to multiple Vera Network at the same time
    
    Other class passin a Vera instance on creation to link to the network
    """
    #hardcode info
    def __init__(self, vera_ip = "192.168.0.100", vera_port = 49451):
        self.vera_ip = vera_ip
        self.vera_port = vera_port
        self.resp = requests.get("http://" + str(vera_ip) + ":" + str(vera_port) + "/data_request?id=lu_status").json()
        self.comm = Comm(vera_ip, vera_port) #bind a communication channel
    
    #this function is called when there is major configuration change to cause change of IP
    #or cause the change of IP
    def refetch_ip(): 
        new_ip = ""
        try:
            new_ip = find_vera() #this function is unstable
        except:
            print "WARNING: Locate fail, retry later"
        self.vera_ip = new_ip #if u can survive the try, u r right
    
    def update_vera_status(self, vera_ip, vera_port):
        """
        get status information of all vera devices in system
        return: unparsed json file, and parse
        """
        self.resp = requests.get("http://" + str(self.vera_ip) + ":" + str(self.vera_port) + "/data_request?id=lu_status").json()    
    
    def status(self, update = True):
        '''
        check if the wave is alive
        Option: -update: if true then repoll the status
        return: 
        '''
        if(update == True):
            update_vera_status()
        return self.resp["devices"]
    
    
