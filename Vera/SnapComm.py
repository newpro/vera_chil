#!/usr/bin/python

import subprocess, os


class SnapComm():
    """
    SnapComm is a module designed for fast communication with java program snap
    """
    def __init__(self, class_name = "MyClass", class_path = os.path.dirname(os.path.abspath(__file__))):
        self.class_name = class_name
        self.class_path = class_path
        self.process = subprocess.Popen(["java", self.class_name,
                                         "-classpath", self.class_path],
                                        stdin=subprocess.PIPE)
        
    def write(self, content):
        self.process.stdin.write(content + "\r\n")
        self.process.stdin.write("ENDOFMESSAGE\r\n")
        
sc = SnapComm()
sc.write("hello there")