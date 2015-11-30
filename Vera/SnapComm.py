#!/usr/bin/python

import subprocess
from os import path as op
from time import sleep
from sys import stderr
from re import split as re_split
#from Vera import Switch #support switch


class SnapComm():
    """
    SnapComm is a module designed for fast communication with java program snap
    the class will spawn a thread as the SNAP communication end, "the other end of communication"
    """
    #global class variables
    
    #switched this to getattr, to give more control to logic unit and enhance module isolation
    #device_fun_list = { #store all support function, TODO: add more functions here to support more
        #'switch_on': Switch.on,#mypackage.mymodule.myfunction,
        #'switch_off': Switch.off,#mypackage.mymodule.myfunction,
        #'switch_status': Switch.status,#mypackage.mymodule.myfunction,
    #}
    
    def __init__(self, feed, class_name = "VeraHandler", class_path = "../symbolicPerseusJava", test_err = 0):
        """
        input args: 
        feed: the file name of the feed, have to be in the folder feed!
        class_name: the java class name, the other end of communication
        class_path: the relative path of java file
        test_err: for testing propose, the probability to report wrong for an observation
        """
        self.class_name = class_name
        
        self.class_path = op.abspath(class_path)
        if(not op.exists(self.class_path)):
            stderr.write( "ERROR: PATH DOES NOT EXISTS" )
            raise 
        
        self.feed1 = op.join(self.class_path,("./feed/" + feed + ".txt"))
        self.feed2 = op.join(self.class_path,("./feed/"+ feed +".pomdp"))
        if(not op.exists(self.feed1)) or (not op.exists(self.feed2)):
                stderr.write( "!!!!!ERROR: FEED DOES NOT EXIST\n" )
                raise         
            
        self.obj_maps = dict() #mapping for Vera observations and SNAP observations
        self.action_maps = dict() #mapping for Vera observations and SNAP observations
        self.ability_maps = dict()
        
        try:
            self.process = subprocess.Popen(["java", "-cp", 
                                             self.class_path, self.class_name,
                                             self.feed1,"-i", self.feed2,"-s","1",
                                             ],
                                            stdin=subprocess.PIPE,
                                            stdout=subprocess.PIPE,
                                            shell=False
                                            )
        except:
            stderr.write( "ERROR: subprocess fail, prosibily because SNAP missing, or have not compiled\n" )
            
    def map_oa(self, o_snap, a_snap, vera_obj, action_obj):
        """
        establish mapping between Vera and SNAP, for observation and action
        so they can understand each other
        
        input args:
        o_snap: the name of snap observation
        a_snap: the name of the snap action
        vera_obj: the object pointer to the vera device/sensor instance
        vera_action: the object pointer to the device that should take the action, when the observation changes
        """
        if o_snap in self.obj_maps:
            stderr.write( "!!!WARNING: replace key\n" )
        if a_snap in self.action_maps:
                    stderr.write( "!!!WARNING: replace key\n" )        
        self.obj_maps[o_snap] = vera_obj
        self.action_maps[a_snap] = action_obj
        
    def get_observation(self, o_snap):
        """
        find the coresponded vera observe value
        """
        if not (o_snap in self.obj_maps):
            stderr.write( "!!!!!ERROR: can not find VERA observation by giving SNAP:" + o_snap +"\n" )
            raise
        else:
            state = getattr(self.obj_maps[o_snap], "status")() #call it
            #stderr.write( "VERA: FOUND OBSERVATION:"+ o_snap + state ) 
        
            if(state): 
                return 'on'
            else: 
                return 'off'
            
    def ability_check(self, ability_name, delay = 5):
        """
        check if the user gain the ability, and perform action if necessary
        """
        stderr.write( "______________________________________________________\n" )
        stderr.write( "||||||||USER FAIL ABILITY:" +  ability_name + "||||||||||\n" )
        self._ability_fail(ability_name)
        stderr.write( "______________________________________________________\n" )
        sleep(delay)#wait for user to gain the ability
            
    def _ability_fail(self, ability_name): 
        """
        When user fail exceeded the tolence, the system will act by this function
        this function is called everytime when user fail the ability, to reduce the tolence counter
        when reduce to 0 the action will be performed
        """
        #SnapComm.device_fun_list[fun_name]
        if not (ability_name in self.ability_maps):
            stderr.write( "!!!!!ERROR: can not find ability:"+ ability_name + "\n" )
            stderr.write(str(self.ability_maps))
            stderr.write(str(ability_name in self.ability_maps))
            raise
        else:
            #vera_instance = self.action_maps[a_snap]
            #getattr(vera_instance, fn_name)() #call it
            #print "VERA: MADE ACTION:", vera_instance.__name__, fn_name
            ability = self.ability_maps[ability_name]
            ability[3] = ability[3] - 1 #tolence - 1
            stderr.write( "HHHHH:"+ ability[0] + "\n")
            if (ability[3] <= 0):#take action now
                #prepare info to make the action
                vera_instance = self.action_maps[ability[1]]
                fail_action = ability[2]
                #make the call
                getattr(vera_instance, fail_action)()
            
    def link_ability(self, ability_name, ability_hint, a_snap = None, fail_action = None, tolence = float('inf')):
        """
        link the ability, the hint for the ability, and the tolence
        
        input args:
        ability_name: the name of the ability, in the SNAP context
        ability_hint: the msg to display when the ability failed
        a_snap: snap action that should take when fail time exceeded tolence, can be None if no action takes
        fail_action: the function name of the action that should call when fail, can be None if no action takes
        ability_tolence: how many time before the system just go a head to do it
            further explain: if the user fail the ability many times,
            the system will just go ahead and do the action. 
            tolence = inf means the Vera do not have ability to change the state of device, 
                          for example: Vera can not auto open the closest
            
            tolence example: if the user fail to turn on the light three times, Vera will go ahead and do it
        """
        if ability_name in self.ability_maps:
            stderr.write( "!!!WARNING: replace key \n" )
        else:
            self.ability_maps[ability_name] = [ability_hint, a_snap, fail_action, tolence]
        
    def write(self, content):
        self.process.stdin.write(content + "\r\n")
        self.process.stdin.write("X\r\n")
        
    def follow_default(self):
        self.write("")
        
    def read(self, delay = 0):
        """
        read one command from SNAP system
        """
        sleep(delay)
        stderr.write( "___________READ___________\n" )
        line = self.process.stdout.readline()
        read = line
        while(read != "X\n"):
            stderr.write("INFO:" + read)
            line = read
            read = self.process.stdout.readline()
        #stderr.write("HERE:" + str(re_split(r"[\n ]+",line)[0]) + "/end")
        return re_split(r"[\n ]+", line)[0]
    
    def end_communication(self):
        self.write("ENDOPERATION")
        stderr.write(  "___________SNAP ENDED___________\n" )
        
    def kill_proc(self):
        """
        only for testing proposes, do not recommand to use, 
        have port leak and other issues, 
        natually, the subprocess should end by itself, call end_communication
        """
        self.process.terminate()
     
#test   
#sc = SnapComm()
#sc.write("hello there")
#print "EDR:", sc.read()
#sc.write("hello there2")
#print "EDR:", sc.read()
#sc.write("hello there3")
#print "EDR:", sc.read()
#sc.end_communication()

#sc = SnapComm(feed = "light")
#sc.write("ENDOPERATION\r\n")
#sc.read()