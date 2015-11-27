#!/usr/bin/python

import subprocess, os


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
                                            stdin=subprocess.PIPE)
        except:
            print "ERROR: subprocess fail, prosibily because SNAP missing, or have not compiled"
        
    def write(self, content):
        self.process.stdin.write(content + "\r\n")
        self.process.stdin.write("ENDOFMESSAGE\r\n")
        
sc = SnapComm()
sc.write("hello there")