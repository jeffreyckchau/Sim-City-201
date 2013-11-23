package gui;

import java.awt.Dimension;
import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import agent.Agent;
import astar.AStarTraversal;
import gui.Building.Building;
import gui.Building.BuildingPanel;
import gui.Building.BusStopBuilding;
import gui.Building.BusStopBuildingPanel;
import gui.MockAgents.MockBusAgent;
import gui.MockAgents.MockPerson;
import gui.agentGuis.PersonGui;
import gui.agentGuis.VehicleGui;



//This class will instantiate and setup everything.
public class SetUpWorldFactory{
	public SimCityLayout layout;// = new SimCityLayout(WINDOWX, WINDOWY/2);// <-This holds the grid information
	public CityAnimationPanel cityPanel;// = new CityAnimationPanel(layout);//<-AnimationPanel draws the layout and the GUIs
	public BuildingsPanels buildingsPanels;// = new BuildingsPanels();//<-Zoomed in view of buildings
	List<Agent> agents = new ArrayList<>();
	List<LocationInfo> locationMap = new ArrayList();//<--a map of strings to LocationInfo
	
	
	
	SetUpWorldFactory() {
	}
		
	/** Loads the default configuration
	 * 
	 */
	public void LoadDefault(){
		final int WINDOWX = 800;
		final int WINDOWY = 800;
		final int GRIDSIZEX = 25;
		final int GRIDSIZEY = 25;
				
		
		layout = new SimCityLayout(WINDOWX, WINDOWY/2, GRIDSIZEX, GRIDSIZEY);// <-This holds the grid information
		cityPanel = new CityAnimationPanel(layout);//<-AnimationPanel draws the layout and the GUIs
		buildingsPanels = new BuildingsPanels();//<-Zoomed in view of buildings
		
		//across middle
		layout.addRoad(1, 5, 34, 5);
		//crosswalks
		layout.addCrossWalk(10, 5, 2, 5);
		layout.addCrossWalk(20, 5, 2, 5);
		
		
		LocationInfo location = new LocationInfo();
		
			for(int x = 1; x < 7;x++) {
				for(int y = 1; y < 5; y++){
					addBuilding("Default", "Building " + x + y, x * 5 - 2, (y * 5)-3, 2, 2, location );
				}
			}
			
			
//BusStop 1			
			location.positionToEnterFromRoadGrid=new Dimension(16, 5);
			location.positionToEnterFromMainGrid=new Dimension(17, 4);
			location.entranceFromMainGridPosition=location.entranceFromRoadGrid = new Dimension(16, 4);
			addBuilding("Bus Stop", "Bus Stop 1", 16, 4, 1, 1, location);
//BusStop 2
			location.positionToEnterFromRoadGrid=new Dimension(5, 9);
			location.positionToEnterFromMainGrid=new Dimension(6, 10);
			location.entranceFromMainGridPosition=location.entranceFromRoadGrid = new Dimension(5, 10);
			addBuilding("Bus Stop", "Bus Stop 2", 5, 10, 1, 1,location);
//BusStop 3		
			location.positionToEnterFromMainGrid=new Dimension(17, 10);
			location.entranceFromMainGridPosition=location.entranceFromRoadGrid = new Dimension(16, 10);
			location.positionToEnterFromRoadGrid=new Dimension(16, 9);
			addBuilding("Bus Stop", "Bus Stop 3", 16, 10, 1, 1, location);
//BusStop 4
			location.positionToEnterFromRoadGrid=new Dimension(26, 9);
			location.positionToEnterFromMainGrid=new Dimension(27, 10);
			location.entranceFromMainGridPosition=location.entranceFromRoadGrid = new Dimension(26, 10);
			addBuilding("Bus Stop", "Bus Stop 4", 26, 10, 1, 1, location);
//BusStop 5
			location.positionToEnterFromRoadGrid=new Dimension(6, 5);
			location.positionToEnterFromMainGrid=new Dimension(7, 4);
			location.entranceFromMainGridPosition=location.entranceFromRoadGrid = new Dimension(6, 4);
			addBuilding("Bus Stop", "Bus Stop 5", 6, 4, 1, 1, location);
//BusStop 6
			location.positionToEnterFromRoadGrid=new Dimension(26, 5);
			location.positionToEnterFromMainGrid=new Dimension(27, 4);
			location.entranceFromMainGridPosition=location.entranceFromRoadGrid = new Dimension(26, 4);
			addBuilding("Bus Stop", "Bus Stop 6", 26, 4, 1, 1, location);
			
			
			//Set default city entrance
			location.entranceFromRoadGrid = new Dimension(1,5);
			location.entranceFromMainGridPosition = new Dimension(1, 3);
			location.positionToEnterFromMainGrid=new Dimension(0,3);
			location.positionToEnterFromRoadGrid=new Dimension(0, 5);
			location.name = "City Entrance";
			locationMap.add(location);
			
			
			addVehicle("OddMockBus");
			addVehicle("EvenMockBus");
			addVehicle("OddMockBus");
			
			addPerson("Person 1");
			addPerson("Person 2");
			addPerson("Person 3");
			
		
	} //end LoadDefault
	
	/** Loads the second configuration
	 * 
	 */
	public void LoadDefault2(){
		final int WINDOWX = 800;
		final int WINDOWY = 800;
		final int GRIDSIZEX = 25;
		final int GRIDSIZEY = 25;
				
		
		layout = new SimCityLayout(WINDOWX, WINDOWY/2, GRIDSIZEX, GRIDSIZEY);// <-This holds the grid information
		cityPanel = new CityAnimationPanel(layout);//<-AnimationPanel draws the layout and the GUIs
		buildingsPanels = new BuildingsPanels();//<-Zoomed in view of buildings
		
		//down left
		layout.addRoad(6, 4, 3, 6);
		// across top
		layout.addRoad(9, 4, 20, 3);
		 //down right
		layout.addRoad(26, 7, 3, 3);
		//down middle
		layout.addRoad(16, 1, 2, 10);
		//across middle
		layout.addRoad(6, 10, 23, 3);
		
		LocationInfo location = new LocationInfo();
		
			for(int x = 1; x < 7;x++) {
				for(int y = 1; y < 5; y++){
					addBuilding("Default", "Building " + x + y, x * 5 - 2, (y * 4)-3, 2, 2, location );
				}
			}
			
			location.positionToEnterFromRoadGrid=new Dimension(16, 2);
			addBuilding("Bus Stop", "Bus Stop 1", 15, 2, 1, 1, location);
			location.positionToEnterFromRoadGrid=new Dimension(6, 12);
			addBuilding("Bus Stop", "Bus Stop 2", 5, 12, 1, 1,location);
			location.positionToEnterFromRoadGrid=new Dimension(28, 12);
			addBuilding("Bus Stop", "Bus Stop 3", 29, 12, 1, 1, location);
			location.positionToEnterFromRoadGrid=new Dimension(14, 10);
			addBuilding("Bus Stop", "Bus Stop 4", 14, 9, 1, 1, location);
			location.positionToEnterFromRoadGrid=new Dimension(17, 12);
			addBuilding("Bus Stop", "Bus Stop 5", 17, 13, 1, 1, location);
			location.positionToEnterFromRoadGrid=new Dimension(26, 8);
			addBuilding("Bus Stop", "Bus Stop 6", 25, 8, 1, 1, location);
			
			addVehicle("OddMockBus");
			addVehicle("EvenMockBus");
		
	} //end LoadDefault2
	
	
	
	
	public void LoadGUITest1(){
		final int WINDOWX = 800;
		final int WINDOWY = 800;
		final int GRIDSIZEX = 20;
		final int GRIDSIZEY = 20;
				
		
		layout = new SimCityLayout(WINDOWX, WINDOWY/2, GRIDSIZEX, GRIDSIZEY);// <-This holds the grid information
		cityPanel = new CityAnimationPanel(layout);//<-AnimationPanel draws the layout and the GUIs
		buildingsPanels = new BuildingsPanels();//<-Zoomed in view of buildings
		
		//down left
		layout.addRoad(6, 4, 3, 14);
		// across top
		layout.addRoad(9, 4, 26, 3);
		 //down right
		layout.addRoad(32, 7, 3, 11);
		//down middle
		layout.addRoad(16, 1, 2, 14);
		//across middle
			layout.addRoad(9, 11, 23, 2);
		//across bottom
		layout.addRoad(9, 15, 23, 3);
		
		
		
		
		LocationInfo location = new LocationInfo();
		
			for(int x = 1; x < 9;x++) {
				for(int y = 1; y < 6; y++){
					addBuilding("Default", "Building " + x + y, x * 5 - 2, (y * 4)-3, 2, 2,location );
				}
			}
			
			
			
			location.positionToEnterFromRoadGrid=new Dimension(16, 2);
			addBuilding("Bus Stop", "Bus Stop 1", 15, 2, 1, 1, location);
			location.positionToEnterFromRoadGrid=new Dimension(6, 16);
			addBuilding("Bus Stop", "Bus Stop 2", 5, 16, 1, 1, location);
			location.positionToEnterFromRoadGrid=new Dimension(34, 12);
			addBuilding("Bus Stop", "Bus Stop 3", 35, 12, 1, 1,location);
			location.positionToEnterFromRoadGrid=new Dimension(15, 6);
			addBuilding("Bus Stop", "Bus Stop 4", 15, 7, 1, 1,location);
			location.positionToEnterFromRoadGrid=new Dimension(28, 17);
			addBuilding("Bus Stop", "Bus Stop 5", 28, 18, 1, 1,location);
			location.positionToEnterFromRoadGrid=new Dimension(32, 7);
			addBuilding("Bus Stop", "Bus Stop 6", 31, 7, 1, 1,location);
			
			addVehicle("OddMockBus");
			addVehicle("EvenMockBus");
		
	} //end LoadGUITest1
	
	
	
	public void LoadGUITest2(){
		final int WINDOWX = 800;
		final int WINDOWY = 800;
		final int GRIDSIZEX = 20;
		final int GRIDSIZEY = 20;
				
		
		layout = new SimCityLayout(WINDOWX, WINDOWY/2, GRIDSIZEX, GRIDSIZEY);// <-This holds the grid information
		cityPanel = new CityAnimationPanel(layout);//<-AnimationPanel draws the layout and the GUIs
		buildingsPanels = new BuildingsPanels();//<-Zoomed in view of buildings
		
		//down left
		layout.addRoad(6, 4, 3, 14);
		// across top
		layout.addRoad(9, 4, 20, 3);
		 //down right
		layout.addRoad(26, 7, 3, 11);
		//down middle
		layout.addRoad(16, 1, 2, 7);
		//across middle
		layout.addRoad(9, 15, 18, 3);
		LocationInfo location = new LocationInfo();
			for(int x = 1; x < 8;x++) {
				for(int y = 1; y < 6; y++){
					addBuilding("Default", "Building " + x + y, x * 5 - 2, (y * 4)-3, 2, 2,location );
				}
			}
			
			
			
			location.positionToEnterFromRoadGrid=new Dimension(16, 2);
			addBuilding("Bus Stop", "Bus Stop 1", 15, 2, 1, 1, location);
			location.positionToEnterFromRoadGrid=new Dimension(6, 16);
			addBuilding("Bus Stop", "Bus Stop 2", 5, 16, 1, 1, location);
			location.positionToEnterFromRoadGrid=new Dimension(28, 12);
			addBuilding("Bus Stop", "Bus Stop 3", 29, 12, 1, 1,location);
			location.positionToEnterFromRoadGrid=new Dimension(15, 6);
			addBuilding("Bus Stop", "Bus Stop 4", 15, 7, 1, 1,location);
			location.positionToEnterFromRoadGrid=new Dimension(18, 17);
			addBuilding("Bus Stop", "Bus Stop 5", 18, 18, 1, 1,location);
			location.positionToEnterFromRoadGrid=new Dimension(26, 8);
			addBuilding("Bus Stop", "Bus Stop 6", 25, 8, 1, 1,location);
			
			addVehicle("OddMockBus");
			addVehicle("EvenMockBus");
		
	} //end LoadGUITest2
	
	
	
	
	
	private void addPerson(String name){
		MockPerson p1 = new MockPerson(name);
		PersonGui g1 = new PersonGui(p1, layout, new AStarTraversal(layout.getMainGrid()), locationMap);
		p1.setAgentGui(g1);
		cityPanel.addGui(g1);
		p1.startThread();
	}
	
	
	private void addVehicle(String type) {
		switch (type) {
		case "OddMockBus":
			//add a mockVehicle
			Queue<String> OddStopsQueue = new LinkedList<>(); //<--a list of the stops to go to
				OddStopsQueue.add("Bus Stop " + 1);
				OddStopsQueue.add("Bus Stop " + 3);
				OddStopsQueue.add("Bus Stop " + 5);
			MockBusAgent v1 = new MockBusAgent("Odd Mock Bus", OddStopsQueue);
			VehicleGui v1Gui = new VehicleGui( v1, layout, new AStarTraversal(layout.getRoadGrid()), locationMap );
			v1.agentGui = v1Gui;
			cityPanel.addGui(v1Gui);
			v1.startThread();
			//mockVehicle Added
			break;
			
		case "EvenMockBus":
			Queue<String> EvenStopsQueue1 = new LinkedList<>(); //<--a list of the stops to go to
			EvenStopsQueue1.add("Bus Stop " + 2);
			EvenStopsQueue1.add("Bus Stop " + 4);
			EvenStopsQueue1.add("Bus Stop " + 6);
			MockBusAgent v2 = new MockBusAgent("Even Mock Bus", EvenStopsQueue1);
			VehicleGui v2Gui = new VehicleGui( v2, layout, new AStarTraversal(layout.getRoadGrid()),locationMap );
			v2.agentGui = v2Gui;
			cityPanel.addGui(v2Gui);
			v2.startThread();
			//mockVehicle Added

		default:
			break;
		}

	}// end addVehicle

	
	
	/** Attempts to add a building to the world.
	 * 
	 * @param type	The type of building. {Default, Bus Stop}
	 * @param name	The unique name of the building.
	 * @param xPos	The x Grid Coordinate
	 * @param yPos	The y Grid Coordinate
	 * @param width	Number of grid positions wide.
	 * @param height	Number of grid positions high.
	 */
	private void addBuilding(String type, String name, int xPos, int yPos, int width, int height, LocationInfo info){
		if(layout == null || buildingsPanels == null){
			System.out.println("ERROR In addBuilding ALL IS NULL");
			return;
		}
		
		//If the name already exists, we can't add the building.
		if(buildingsPanels.containsName(name))
			return;
		
		Building building = layout.addBuilding( xPos, yPos, width, height );//<-this puts the building on the grid
		
		if(building == null){
			return;
		}
		
		
		switch (type) {
		case "Default":
			if(building != null){
				BuildingPanel bp = new BuildingPanel(building, name, buildingsPanels);
				building.setBuildingPanel(bp);
				cityPanel.addGui(building);
				buildingsPanels.addBuildingPanel(bp);
			}
			break;
		case "Bus Stop":
			BusStopBuilding busStop = new BusStopBuilding(building);
			if(busStop != null)
			{
				BuildingPanel bp = new BusStopBuildingPanel(busStop, name, buildingsPanels);
				
				busStop.setBuildingPanel(bp);
				cityPanel.addGui(busStop);
				buildingsPanels.addBuildingPanel(bp);
			}
			break;
		default:
			return;
		}
		info.name = name;
		locationMap.add(new LocationInfo(info));
	}
	
}
