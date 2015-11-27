#!/usr/bin/python

import subprocess, os, sys, time


class SnapComm():
    """
    SnapComm is a module designed for fast communication with java program snap
    """
    def __init__(self, class_name = "VeraComm", class_path = "../symbolicPerseusJava"):
        self.class_name = class_name
        self.class_path = class_path
        
        try:
            self.process = subprocess.Popen(["java", "-cp", 
                                             self.class_path, self.class_name],
                                            stdin=subprocess.PIPE,
                                            stdout=subprocess.PIPE)
        except:
            print "ERROR: subprocess fail, prosibily because SNAP missing, or have not compiled"
        
    def write(self, content):
        self.process.stdin.write(content + "\r\n")
        
    def read(self):
        msg = ""
        while(True):#only accept DUMP, avoid info and echo
            msg = self.process.stdout.readline()
            if (msg[:5]=="DUMP:"):
                sys.stderr.write("msg:" + msg)
                msg = msg[6:]
                break
            if (msg == ""): #block
                time.sleep(1)
                sys.stderr.write("Waiting")
        return msg
    
    def end_communication(self):
        self.process.stdin.write("ENDOPERATION\r\n")
        sys.stderr.write(self.read()) #dump the end signal
        
    def kill_proc(self):
        """
        only for testing proposes, do not recommand to use, 
        have port leak and other issues, 
        natually, the subprocess should end by itself
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
