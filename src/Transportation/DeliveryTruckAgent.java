package Transportation;

import java.util.LinkedList;
import java.util.Queue;

import trace.AlertLog;
import trace.AlertTag;
import agent.Agent;
import gui.agentGuis.TruckVehicleGui;
import interfaces.Car;

public class DeliveryTruckAgent extends Agent implements Car{
	
	
	/** Constructor for Delivery Truck
	 * 
	 * @param name The name of the truck Truck 1 for example.
	 */
	public DeliveryTruckAgent(String name) {
		state = CarState.parked;
		this.name = name;
		
		this.setGui(new TruckVehicleGui(name));
		this.startThread();
	}
	
	
	//Data
	String name = null;
	private TruckVehicleGui agentGui = null;
	
	private Queue<String> destination = new LinkedList<>(); 
	private CarState state;
	public enum CarState {parked, driving};
	
	
	//Messages
	
	
	/** Will add the destination to the queue of places to go.
	 * 
	 */
	public void msgNewDestination(String destination) {
		this.destination.offer(destination);
		this.state = CarState.driving;
		stateChanged();
	}
	

	//Scheduler
	@Override
	protected boolean pickAndExecuteAnAction() {

		if (state == CarState.driving) {
			goToDestination();
			return true;
		}

		return false;
	}
	
	//Actions
	private void goToDestination() {
		
		if(destination.size() == 0){
			state = CarState.parked;
			AlertLog.getInstance().logMessage(AlertTag.VEHICLE_GUI, name, "No destinations in queue. Parking.");
			return;
		}
		
		String dest;
		dest = destination.poll();
		
		AlertLog.getInstance().logMessage(AlertTag.VEHICLE_GUI, name, "Going to " + dest );
		agentGui.DoGoTo(dest);
		AlertLog.getInstance().logMessage(AlertTag.VEHICLE_GUI, name, "Arrived at " + dest);
	}
	
	
	
	
	
	
	
	
	//Utilities
	public void setGui(TruckVehicleGui truckGui){
		this.agentGui = truckGui;
	}
	
	
	
	public String toString(){
		return "" + name;
	}
	
	public String getName(){
		return name;
	}
	
}
