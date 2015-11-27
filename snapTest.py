import os
from Vera import SnapComm

sc = SnapComm.SnapComm(class_path = "./symbolicPerseusJava")
sc.write("hello there")
print "EDR:", sc.read()
sc.write("hello there2")
print "EDR:", sc.read()
sc.write("hello there3")
print "EDR:", sc.read()
sc.end_communication()