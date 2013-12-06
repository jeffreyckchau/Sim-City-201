package jeffreyRestaurant;

import agent.Agent;

import java.util.*;
import java.util.concurrent.Semaphore;

import jeffreyRestaurant.Gui.HostGui;
import jeffreyRestaurant.interfaces.Customer;
import jeffreyRestaurant.interfaces.Waiter;

/**
 * Restaurant Waiter Agent
 */
//We only have 2 types of agents in this prototype. A customer and an agent that
//does all the rest. Rather than calling the other agent a waiter, we called him
//the HostAgent. A Host is the manager of a restaurant who sees that all
//is proceeded as he wishes.
public class WaiterAgent extends Agent implements Waiter{
	static final int NTABLES = 3;//a global for the number of tables.
	//Notice that we implement waitingCustomers using ArrayList, but type it
	//with List semantics.
	public List<myCustomer> Customers
	= new ArrayList<myCustomer>();
	//public Collection<Table> tables;
	//note that tables is typed with Collection semantics.
	//Later we will see how it is implemented
    
	private enum CustomerState{waiting, seated, readyToOrder, AskedToOrder, OutOfOrder, Ordered, OrderReady, Eating, WaitingForCheck, Paying, GivenCheck, Leaving, Done};
	
	private class myCustomer {
		myCustomer(CustomerAgent myCust, int tab, CustomerState state) {
			c = myCust; table = tab; s = state;
		}
		CustomerAgent c;
		int table;
		Double tab = 0.0;
		String ch;
		CustomerState s;
	}
	
	public class menu {
		menu() {
			foods = new HashMap<String, Double>();
			foods.put("Steak", 15.99);
			foods.put("Chicken", 10.99);
			foods.put("Salad", 5.99);
			foods.put("Pizza", 8.99);
		}
		HashMap<String, Double> foods;
		public void removeFood(String food) {
			foods.remove(food);
		}
		public String randomFood() {
			ArrayList<String> foodSet = new ArrayList<String>(foods.keySet());
			Random rand = new Random();
			return foodSet.get(rand.nextInt(foodSet.size()));
		}
		
	}
	
	private String name; 
	private Semaphore atDestination = new Semaphore(0,true);
	private boolean wantsBreak;
	private boolean onBreak;
	
	private HostAgent h;
	private CookAgent ck;
	private CashierAgent ch;
	public HostGui hostGui = null;

	public WaiterAgent(String name) {
		super();

		this.name = name;
		wantsBreak = false;
		onBreak = false;
	}

	public String getMaitreDName() {
		return h.getName();
	}
	public void setHost(HostAgent host) {
		h = host;
	}
	public void setCook(CookAgent cook) {
		ck = cook;
	}
	public void setCashier(CashierAgent cashier) {
		ch = cashier;
	}
	public String getName() {
		return name;
	}

	public List getCustomers() {
		return Customers;
	}
	
	public boolean isOnBreak() {
		return onBreak;
	}
	public void setOnBreak() {
		//print("I am going on break" + state);
		onBreak = true;
	}
	public void setOffBreak() {
		print("Back from break");
		onBreak = false;
	}
	public boolean doesWantBreak() {
		return wantsBreak;
	}
	public void setWantsBreak(Boolean state) {
		print("I do want break" + state);
		wantsBreak = state;
	}
	// Messages

	public void msgSeatCustomer(CustomerAgent cust, int t) {
		print ("Told to seat " + cust);
		Customers.add(new myCustomer(cust, t, CustomerState.waiting));
		stateChanged();
	}

	public void msgReadyToOrder(CustomerAgent cust) {
		//print("msgReadyToOrder received");
		for (myCustomer mc : Customers) {
			if (mc.c == cust) {
				print(cust.getName()  + "is ready to order");
				mc.s = CustomerState.readyToOrder;
				stateChanged();
			}
		}
	}
	
	public void msgHereIsChoice(String choice, CustomerAgent cust) {
		//print("msgHereIsChoice called");
		for (myCustomer mc : Customers) {
			if (mc.c == cust) {
				print("Received " + cust.getName() + "'s order of " + choice);
				mc.ch = choice;
				mc.s = CustomerState.AskedToOrder;
				stateChanged();
				//waitForOrder.release();
			}
		}
	}
	
	public void msgLeavingTable(CustomerAgent cust) {
		//print("msgLeavingTable called");
		for (myCustomer mc : Customers) {
			if (mc.c == cust) {
				mc.s = CustomerState.Leaving;
			}
		}
		stateChanged();
	}
	
	public void msgGiveFood(String Food, int myTable) {
		print ("Received " + Food + " for " + myTable + " from " + ck.getName());
		for (myCustomer mc : Customers) {
			if (mc.ch == Food) {
				if (mc.table == myTable) {
					mc.s = CustomerState.OrderReady;
					stateChanged();
				}
			}
		}
	}

	public void msgAtDestination() {//from animation
		//print("msgAtTable called");
		atDestination.release();// = true;
		stateChanged();
	}
	
	public void msgAtDesk() {
		//obsolete
		//print("msgAtDesk Called");
		atDestination.release();
		//stateChanged();
	}
	
	public void msgDone(CustomerAgent c) {
		for (myCustomer mc : Customers) {
			if (mc.c == c) {
				print(c.getName() + " is done");
				mc.s = CustomerState.Done;
				stateChanged();
			}
		}
	}
	
	public void msgOutOfFood(String food, int table) {
		for (myCustomer mc : Customers) {
			if (mc.ch == food && mc.table == table) {
				print("Told that we are out of " + food + " for " + mc.c.getName());
				mc.s = CustomerState.OutOfOrder;
				//atDestination.release();
				stateChanged();
			}
		}
	}
	
	public void msgHereIsCheck(Double tab, Customer c) {
		for (myCustomer mc : Customers) {
			if (mc.c == c) {
				print("Check received from Cashier");
				mc.tab = tab;
				mc.s = CustomerState.Paying;
				stateChanged();
			}
		}
	}
	
	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	protected boolean pickAndExecuteAnAction() {
		/* Think of this next rule as:
            Does there exist a table and customer,
            so that table is unoccupied and customer is waiting.
            If so seat him at the table.
		 */
		if (wantsBreak) {
			askForBreak();
			return true;
		}
		try {
			for (myCustomer mc : Customers) {
				//print("Dealing with " + mc.c.getName());
				if (mc.s == CustomerState.waiting) {
					seatCustomer(mc);
					return true;
				}
			}
			for (myCustomer mc : Customers) {
				//print("Dealing with " + mc.c.getName());
				if (mc.s == CustomerState.readyToOrder) {
					TakeOrder(mc);
					return true;
				}
			}
			for (myCustomer mc : Customers) {
				if (mc.s == CustomerState.AskedToOrder) {
					tellCookOrder(mc);
					return true;
				}
			}
			//print("00");
			for (myCustomer mc: Customers) {
				if (mc.s == CustomerState.OutOfOrder) {
					//print("Scheduler debug");
					pleaseOrderAgain(mc);
					return true;
				}
			}
			//print("01");
			for (myCustomer mc : Customers) {
				//print("Dealing with " + mc.c.getName());
				if (mc.s == CustomerState.OrderReady) {
					DeliverFood(mc);
					return true;
				}
			}
			for (myCustomer mc : Customers) {
				if (mc.s == CustomerState.Done){
					customerReadyToPay(mc);
					//customerLeaving(mc);
					return true;
				}
			}
			for (myCustomer mc : Customers) {
				if (mc.s == CustomerState.Paying) {
					giveCustomerCheck(mc);
					return true;
				}
			}
			
			for (myCustomer mc : Customers) {
				if (mc.s == CustomerState.Leaving) {
					customerLeaving(mc);
					return true;
				}
			}
		}
		catch (ConcurrentModificationException e) {
			return false;
		}
		
		hostGui.DoLeaveCustomer();
		return false;
		//we have tried all our rules and found
		//nothing to do. So return false to main loop of abstract agent
		//and wait.
	}

	// Actions

	private void seatCustomer(myCustomer myCustomer) {
		print("Seating" + myCustomer.c.getName());
		myCustomer.c.msgSitAtTable(myCustomer.table,new WaiterAgent.menu());
		DoSeatCustomer(myCustomer.c, myCustomer.table);
		try {
			atDestination.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		myCustomer.s = CustomerState.seated;
	}

	private void TakeOrder(myCustomer myCustomer) {
		print("Taking the order of " + myCustomer.c.getName());
		//myCustomer.s = CustomerState.AskedToOrder;
		DoFindCustomer(myCustomer);
		try {
			atDestination.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		myCustomer.c.msgWhatIsOrder();
	}
	
	private void tellCookOrder(myCustomer mc) {
		print("Telling cook order of " + mc.ch);
		DoGoToCook();
		try {
			atDestination.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ck.msgHereIsOrder(this, mc.ch, mc.table);
		mc.s = CustomerState.Ordered;
		hostGui.DoLeaveCustomer();
	}
	
	private void pleaseOrderAgain(myCustomer mc) {
		DoFindCustomer(mc);
		try {
			atDestination.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		print ("We are out of " + mc.c.getName() + "'s order. Please choose something else");
		menu newMenu = new menu();
		newMenu.removeFood(mc.ch);
		mc.c.msgOrderAgain(newMenu);
		//mc.s = CustomerState.AskedToOrder;
		mc.s = CustomerState.readyToOrder;
		
		hostGui.DoLeaveCustomer();
	}
	
	private void DeliverFood(myCustomer myCustomer) {
		DoGoToCook();
		try {
			atDestination.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		print ("Picking up food for " + myCustomer.c.getName());
		myCustomer.c.msgHereIsFood();
		
		DoFindCustomer(myCustomer);
		try {
			atDestination.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		print("Delivering food to " + myCustomer.c.getName());
		myCustomer.c.msgHereIsFood();
		myCustomer.s = CustomerState.Eating;
		hostGui.DoLeaveCustomer();
	}
	
	private void customerReadyToPay(myCustomer mc) {
		print(mc.c.getName() + " is ready to pay");
		mc.s = CustomerState.WaitingForCheck;
		ch.msgCustomerReadyToPay(this, mc.ch, mc.c);
	}
	
	private void giveCustomerCheck(myCustomer mc) {
		mc.c.msgHereIsCheck(mc.tab);
		mc.s = CustomerState.GivenCheck;
	}
	
	private void customerLeaving(myCustomer mc) {
		print("Telling host that " + mc.c.getName() + " is leaving table.");
		h.msgDone(mc.c);
		h.msgLeavingTable(mc.c);
		Customers.remove(mc);
	}
	
	private void askForBreak() {
		h.msgWantToGoOnBreak(this);
		wantsBreak = false;
	}
	
	// The animation DoXYZ() routines
	private void DoSeatCustomer(CustomerAgent customer, int table) {
		//Notice how we print "customer" directly. It's toString method will do it.
		//Same with "table"
		print("Seating " + customer + " at " + table);
		hostGui.DoBringToTable(customer, table); 
	}

	private void DoFindCustomer(myCustomer cust) {
		print ("Going to " + cust.c.getName());
		hostGui.DoGoToTable(cust.table);
	}
	
	private void DoGoToCook() {
		print ("Going to " + ck.getName());
		hostGui.DoGoToCook();
	}
	
	//utilities

	public void setGui(HostGui gui) {
		hostGui = gui;
	}

	public HostGui getGui() {
		return hostGui;
	}

	public Integer howManyCustomers() {
		return Customers.size();
	}
}

