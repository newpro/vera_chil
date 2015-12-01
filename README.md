# Vera Chil project

### About the project:

 * The project is restricted to research propose only, all rights reserved for University of Waterloo, Canada
 * Project authorized to: Prof. Jesse Hoey, University of Waterloo, member of AGE-WELL
 * Project written by: Aaron Li, research assistent, University of Waterloo
 * This project is intend to build developer environment for smart home system, and support SNAP system as POMDP engine. 

### Links:
* The description of SNAP is [here](https://cs.uwaterloo.ca/~jhoey/research/snap/index.php)
* The links for research about SNAP is [here](http://arxiv.org/pdf/1206.5698.pdf)
 
### Project Break down

 * Vera Chil, python package: the package allow direct control over VERA smart home system
 * symbolicPerseusJava, Java package: java implementation of SNAP system

### Communication
The two parts is connected with python Popen, controlled on both end:

* Vera communication end is controlled by SnapComm class in Vera packages
* SNAP communication end is controlled by VeraHanlder class
* The main control is on python side

### Requirement
* currently have to run in Wifi that within Vera system
* Vera devices/sensor install required 

### example files
* test.py: example on how to use Vera control system
* snapTest.py: example on how to combine Vera and Snap system

#### sample analysis: snapTest.py
* in this example, we are going to look at a VERA-SNAP system that 

###### setup the running environment 
* vera system, devices and sensors in the system, communication unit is represent as instances
* link all abilities in the system, set up parameters, mark down all parameters. For details please check on help for functions

```python
import os
from Vera import SnapComm
from Vera import Switch
from Vera import Vera 

sc = SnapComm.SnapComm(class_path = "./symbolicPerseusJava", feed = "light")
myvera = Vera.Vera()
light1 = Switch.Switch(myvera, 14)
sc.link_ability('prompt_Af_light1_on', "HINT: Light can be turned on", a_snap='turn_light', fail_action='on', 
               tolence=4)
sc.link_ability('prompt_Rn_light1_on', "HINT: Light is on your left side", a_snap='turn_light', fail_action='on', 
               tolence=4)
```

###### setup the running environment 
* establish the understanding between VERA and SNAP system 
```python
sc.map_oa('light_switch', 'turn_light', light1, lig### Links:
* The description of SNAP is [here](https://cs.uwaterloo.ca/~jhoey/research/snap/index.php)
* The links for research about SNAP is [here](http://arxiv.org/pdf/1206.5698.pdf)ht1)# light1 is both status object and action receive object in this case
```

###### interaction 
* loop until the target reached
```python
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
```
###### close the connection
```python
sc.end_communication()
```

### Future of the project:
###### since time spend on the project is very limited, this project is far from complete, the following is suggestion 
* Add more supports within Vera system, include more types of devicees and command support. 
* Add support system other than Vera. 
* 

### Contact information

* [Prof. Jesse Hoey](https://cs.uwaterloo.ca/~jhoey/index.php): jhoey@cs.uwaterloo.ca, 
* [Aaron Li](https://ca.linkedin.com/in/aaron-li-01215748): w89li@uwaterloo.ca, 
