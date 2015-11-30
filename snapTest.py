import os
from Vera import SnapComm
from Vera import Switch
from Vera import Vera 

#sc = SnapComm.SnapComm("light", 
                      #class_path="./symbolicPerseusJava", 
                      #class_name="VeraComm")
#sc.write("hello1")
#print "EDR:", sc.read(delay=0)
#sc.write("hello2")
#print "EDR:", sc.read(delay=0)
#sc.write("hello3")
#print "EDR:", sc.read(delay=0)
#sc.end_communication()

#setups:

sc = SnapComm.SnapComm(class_path = "./symbolicPerseusJava", feed = "light")
myvera = Vera.Vera()
light1 = Switch.Switch(myvera, 14)
sc.link_ability('prompt_Af_light1_on', "HINT: Light can be turned on", a_snap='turn_light', fail_action='on', 
               tolence=4)
sc.link_ability('prompt_Rn_light1_on', "HINT: Light is on your left side", a_snap='turn_light', fail_action='on', 
               tolence=4)

#establish understanding
sc.map_oa('light_switch', 'turn_light', light1, light1)# light1 is both status object and action receive object in this case

#interaction start
target_action = ""
while not (target_action == "donothing"):
    target_action = sc.read() #check on the ability
    if target_action == "donothing":
        break
    sc.follow_default()
    sc.ability_check(target_action, delay=5)
    
    ob_name = sc.read() #read observation 
    ob_state = sc.get_observation(ob_name)
    sc.write(ob_state)
    
    beh_name = sc.read()
    beh_state = raw_input('Did it? ' + beh_name + ':')
    sc.write(beh_state)

#good hit
sc.end_communication()
