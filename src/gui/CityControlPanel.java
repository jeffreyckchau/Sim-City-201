package gui;

import javax.swing.*;

import gui.Building.BuildingGui;
import gui.Building.BuildingPanel;
import interfaces.GuiPanel;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;

import sun.awt.X11.Screen;
import agent.Agent;
import Person.PersonAgent;
import Person.Role.Role;
/**
 * Singleton GUI class for controlling the city.
 * @author JEFFREY
 *
 */
public class CityControlPanel extends BuildingPanel implements ActionListener{
	
	public List<JButton> peopleList = new ArrayList<JButton>();
	//Window to the GUI list of persons
	public JScrollPane pane =
            new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    private JPanel personView = new JPanel();
    private JLabel focusInfo = new JLabel();
    private JPanel moreControls = new JPanel();
    static BuildingGui defaultGui = new BuildingGui(0,0,0,0);
    
    private PersonAgent focus = null;
    private SetUpWorldFactory parent; 
    
    //Right Side Control Buttons
    JButton plusControlsB;
    JButton findAgentB;
    JButton addPersonB;
	
	
	public CityControlPanel(BuildingsPanels buildingPanels, SetUpWorldFactory parent) {
		super(defaultGui, "Controls", buildingPanels);
		this.removeAll();
		setLayout(new GridLayout(1,3));
		setBackground(Color.WHITE);
		this.parent = parent;
		
		personView.setLayout(new BoxLayout((Container) personView, BoxLayout.Y_AXIS));
		pane.setViewportView(personView);
		add(pane);
		
		focusInfo.setText("<html><pre> <u> Person Info goes here </u> </pre></html>");
		add(focusInfo);
		
		plusControlsB = new JButton("Additional Controls");
		plusControlsB.addActionListener(this);
		
		findAgentB = new JButton("Zoom to Agent");
		findAgentB.addActionListener(this);
		
		addPersonB = new JButton("Add a Person");
		addPersonB.addActionListener(this);
		
		moreControls.setLayout(new GridLayout(3,1)); //Modify number of rows to add more buttons
		moreControls.add(plusControlsB);
		moreControls.add(findAgentB);
		moreControls.add(addPersonB);
		add(moreControls);
		//Add buttons to the controls here
	}
	
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == plusControlsB) {
			//Create new control window
			showExtraControls();
		}
		if (e.getSource() == findAgentB) {
			//Gui.showAgent'sCurrentLocation
			zoomToPerson();
		}
		if (e.getSource() == addPersonB) {
			//Create person creation window
			showPersonCreation();
		}
		
		
		//Iterate through people list
		for (JButton person : peopleList) {
			if (e.getSource() == person) {
				//Display info for that person
				for (PersonAgent a : SetUpWorldFactory.agents) {
					if (a.getName().equalsIgnoreCase(person.getText())) {
						focus = a;
						showInfo(a);
					}
				}
			}
		}
		
	}
	/**
	 * GUI function to update the info panel with the current information
	 * of the agent it is passed. 
	 * @param agent Agent taken directly from SetUpWorldFactory list of agents
	 */
	private void showInfo(PersonAgent agent) {
		//Update Center text field
		/*
		String carStatus;
		if (agent.hasCar()) {
			carStatus = "Yes";
		}
		else {
			carStatus = "No";
		}*/
		String currentJob;
		try {
			currentJob = agent.getCurrentJobString();
		}
		catch (Exception e) {
			currentJob = "N/A";
		}
		
		focusInfo.setText("<html> <u> " + agent.getName() + 
				"</u> <table><tr> Current Job: " + currentJob + 
				"</tr><tr> Age: " + agent.getAge() + 
				"</tr><tr> SSN: " + agent.getSSN() +
				"</tr><tr> Owns car: " + "/*carStatus*/" + 
				"</tr><tr> Current money: " + agent.getMoney() + 
				"</tr><tr> Hunger Level: " + agent.getHungerLevel() + 
				"</tr><tr> Current Loan: " + agent.getLoan() + 
				"</tr><tr> Number of Parties: " + agent.getNumParties() +
				"</tr><tr> Current Location: " + agent.getCurrentLocation() + 
				"</tr></table></html>");
	}
	
	public void addPerson(Agent a) {
		JButton newPerson = new JButton(a.getName());
		newPerson.setBackground(Color.WHITE);
		
		Dimension buttonSize = new Dimension(240, 30);
		newPerson.setPreferredSize(buttonSize);
		newPerson.setMinimumSize(buttonSize);
		newPerson.setMaximumSize(buttonSize);
		newPerson.addActionListener(this);
		peopleList.add(newPerson);
		personView.add(newPerson);
		//Hacked so that it adds through the config file and then shows up here second. 
		//Control panel references a public list of agents in the SetUpWorldFactory construct
		
		validate();
	}
	
	//Add function to realtime update infopanel
	
	public void showExtraControls() {
		JFrame extraControls = new JFrame("Additional Controls");
		Rectangle windowLocation = new Rectangle(800, 400, 200, 400);
		extraControls.setBounds(windowLocation);
		
		JPanel testPanel = new JPanel();
		testPanel.add(new JButton("Test"));
		extraControls.add(testPanel);
		
		
		extraControls.setVisible(true);
	}
	
	public void showPersonCreation() {
		AddPersonControl addPersonControls = new AddPersonControl("Create a person");
		Rectangle windowLocation = new Rectangle(800, 100, 400, 800); //Modify this to determine where the window spawns
																	//(Xpos, Ypos, width, height)
		addPersonControls.setBounds(windowLocation);
		
		addPersonControls.setVisible(true);
	}
	
	public void zoomToPerson() {
		String location;
		try {
			location = focus.getCurrentLocation();
			//Find correct building
			//Need to format string correctly
			//Call buildingsList.correctBuilding.displayBuildingPanel();
			if (location.equals("City")) {
				parent.buildingsPanels.displayBuildingPanel(location);
			}
		}
		catch (Exception e) {
			//Catch null pointer exception
		}
	}
	
	@Override
	public GuiPanel getPanel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addPersonWithRole(Role r) {
		// TODO Auto-generated method stub
		
	}

}
