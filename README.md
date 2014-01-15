#Sim City 201

Jeffrey Chau

======

##Overview

This was a team collaboration to create a digital simulation of a city, including buildings and autonomous persons, to demonstrate agent methodology and unit-testing. 

##Features
+ A*
+ Busses
+ Businesses (Market, bank, restaurants)
+ Residences

##Jeffrey's Duties
+ Transportation programming
+ git-integration
+ Control panel GUI
+ Single Restaurant
+ Many of the J-unit tests for other agents
+ Design for the Person and Transportation agents

#Execution instructions

**The class required that this project be built in Eclipse, so the instructions reflect this requirement**

The project is best run through the Eclipse editor by using the play button to run SimCity201Gui. If the play button does not work, the main application can be run through the src/gui/SimCity201Gui.java file. 

Loading specific scenarios is achieved through the initial GUI with labeled buttons. Once in the scenario, selecting View -> Settings -> Test View will display the names of all agents and constructs. To run another scenario, the application must be closed and reopened. 

Clicking on various buildings will show a zoomed in view of the building inside. In the food court and the apartments, clicking on buildings in the zoomed in view will zoom in on another building. 

Going into View -> Sim City Controls will display a control panel with various options. Selecting a person from the list of people on the left will display more information about them. The additional controls button creates a new panel with certain commands for the person in focus on the control panel. The zoom to agent button will change the focused view to wherever the focused agent is. The add person button will create a new panel with various options for adding another person to the scene.


#Full Disclosure (Known Issues)

We fully acknowledge that this version of Sim City is a work in progress and will have some issues and bugs present in its execution. As such

+ A* may lead to clogging of building entrances and impact machine performance
+ Of the scenarios present in the load screen, not all are fully implemented or follow specifications. Certain scenarios need to be tested manually using the control panel. 
+ Restaurant - Bank interaction is not implemented
