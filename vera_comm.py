'''
this is the communication unit between program and vera 
'''

#-----------------locate boardcast IP------------------
from subprocess import Popen, PIPE
import time
from os import setsid

def findip(vera_port):
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
            raise Exception("CRITICAL: timeout quota reached, no Vera find")

#test       
vera_port = 49451
#vera_ip = findip(vera_port)
vera_ip = "192.168.0.100"

#_______________________parse UML data_________________________
import requests
import xml.etree.ElementTree as ET
r = requests.get("http://" + str(vera_ip) + ":" + str(vera_port) + "/data_request?id=lu_status")
print type(r.json())


