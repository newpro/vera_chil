import os
from Vera import SnapComm

#os.path.exists()

comm = SnapComm.SnapComm(class_path=os.path("/Vera/MyClass.java"))
comm.write("hello there")