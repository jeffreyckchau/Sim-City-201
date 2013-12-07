package mikeRestaurant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Semaphore;

import mikeRestaurant.gui.WaiterGui;
import mikeRestaurant.interfaces.Cashier;
import mikeRestaurant.interfaces.Cook;
import mikeRestaurant.interfaces.Customer;
import mikeRestaurant.interfaces.Host;
import mikeRestaurant.interfaces.Waiter;
import agent.Agent;

/**
 * Restaurant Host Agent
 */
//We only have 2 types of agents in this prototype. A customer and an agent that
//does all the rest. Rather than calling the other agent a waiter, we called him
//the HostAgent. A Host is the manager of a restaurant who sees that all
//is proceeded as he wishes.
public class WaiterAgent extends Agent implements Waiter{
	//Notice that we implement waitingCustomers using ArrayList, but type it
	//with List semantics.
	
	//note that tables is typed with Collection semantics.
	//Later we will see how it is implemented
	private String name;
	
	//semaphores used for sleeping the thread for various animations
	private Semaphore atTable = new Semaphore(0,true);
	private Semaphore atStart = new Semaphore(0, true);
	private Semaphore atCook = new Semaphore(0, true);
	private Semaphore atCashier = new Semaphore(0, true);
	
	public boolean readyToSeatCustomer;

	public WaiterGui waiterGui = null;
	
	/**
	 * Returns the price of the explicit items of the menu
	 * @return
	 */
	public static Map<String, Double> MENU() {
		HashMap<String, Double> temp = new HashMap<String, Double>();
		temp.put("Steak", Double.valueOf(15.99));
		temp.put("Chicken", Double.valueOf(10.99));
		temp.put("Salad", Double.valueOf(5.99));
		temp.put("Pizza", Double.valueOf(8.99));
		return temp;
	};
	
	/**
	 * An inner class meant for the WaiterAgent to link a customer to his table choice 
	 * while also keeping track of the state of that customer
	 *
	 */
	private class MyCustomer {
		  CustomerAgent customer;
		  Table table;
		  String choice;
		  CustomerState state;
		  double check;
		  Map<String, Double> menu = MENU();
		  
		  public MyCustomer(CustomerAgent customer, Table table, String choice){
			  this.customer = customer;
			  this.table = table;
			  this.choice = choice;
			  
			  this.state = CustomerState.CustomerWaiting;
		  }
		  
		  public void editMenu(Set<String> remainingFoods){
			  menu.clear();
			  Map<String, Double> temp = MENU();
			  for(String s : temp.keySet()){
				  //menu should only have things that exist in the foods
				  if(remainingFoods.contains(s)){
					  menu.put(s, temp.get(s));
				  }
			  }
		  }
	}
	
	//these Agents are singletons, so a list of each is not needed
	private HostAgent host;
	private CookAgent cook;
	private CashierAgent cashier;

	//list of customers that this waiter is handling
	private List<MyCustomer> customers;
	//list of orders that are given by the 
	private List<Order> ordersForDelivery;
	
	public enum CustomerState {CustomerWaiting, CustomerSeated, CustomerReadyToOrder, CustomerNeedsNewMenu, CustomerGivingOrder, CustomerOrdered, CustomerWaitingForFood, 
		CustomerServed, CustomerWantsCheck, CustomerWaitingForCheck, CustomerNeedsCheckDelivered, CustomerCheckDelivered, CustomerLeft};
	
	//really the only state for the waiter -- all else is handled by semaphores
	private boolean isIdle;
	public enum BreakState {NoState, WantsToGoOnBreak, AskedToGoOnBreak, CanGoOnBreak, OnBreak};
	private BreakState breakState;
	
	/**
	 * Constructor for the Waiter
	 * @param name name of the Waiter
	 * @param host host for the waiter
	 * @param cook cook for the waiter
	 */
	public WaiterAgent(String name, Host host, Cook cook, Cashier cashier) {
		super();

		this.name = name;
		
		ordersForDelivery = Collections.synchronizedList(new ArrayList<Order>());
		customers = Collections.synchronizedList(new ArrayList<MyCustomer>());
		
		this.host = (HostAgent) host;
		this.cook = (CookAgent) cook;
		this.cashier = (CashierAgent) cashier;
		
		isIdle = false;
		breakState = BreakState.NoState;
	}

	/**
	 * Name accessor method
	 * @return name of the waiter
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Accessor method for number of customers -- used by host in 
	 * customer assignment process.
	 * @return number of customers being handled by the customer
	 */
	public int getNumCustomers(){
		return customers.size();
	}
	
	//-------------------------MESSAGES---------------------------//
	
	/**
	 * Message sent by the HostAgent to seat a particular customer waiting
	 * to eat at the restauraunt
	 * @param customer the CustomerAgent to seat
	 * @param table the table the customer is to sit at
	 */
	public void msgSitAtTable(Customer customer, Table table){
		customers.add(new MyCustomer((CustomerAgent)customer, table, null));
		stateChanged();
	}
	
	/**
	 * Message sent by the CustomerAgent when he/she is ready for the check
	 * from his/her waiter
	 * @param sender the CustomerAgent who wants his/her check
	 */
	public void msgReadyForCheck(Customer sender){
		for(MyCustomer cust : customers){
			if(cust.customer == (CustomerAgent)sender){
				cust.state = CustomerState.CustomerWantsCheck;
				stateChanged();
				return;
			}
		}
	}
	
	/**
	 * Message sent by the CustomerAgent to signal the waiter for ordering
	 * @param sender the CustomerAgent sending the message
	 */
	public void msgImReadyToOrder(Customer sender){
		print(sender.getName()+ " is ready to order");
		for(MyCustomer customer : customers){
			if(customer.customer == (CustomerAgent) sender){
				customer.state = CustomerState.CustomerReadyToOrder;
				stateChanged();
				return;
			}
		}
	}
	
	/**
	 * Message sent by the CustomerAgent to give a waiter his/her order
	 * @param choice A String representation of the choice
	 * @param sender the CustomerAgent sending the message
	 */
	public void msgHereIsMyChoice(String choice, Customer sender){
		for(MyCustomer customer : customers){
			if(customer.customer == (CustomerAgent) sender){
				customer.choice = choice;
				customer.state = CustomerState.CustomerOrdered;
				
				/* the waiter should show the ordered food immediately upon ordering, but priority for the corresponding
				 * action prevents this from happening unless called here (priority should not be moved up because action is
				 * not important enough)
				 * 
				 * action cannot be called from CustomerAgent (sender) because table number is required
				 */
				waiterGui.showPendingOrderOnScreen(customer.choice, customer.table.tableNumber);
				stateChanged();
				return;
			}
		}
	}
	
	/**
	 * Message sent by the CookAgent that a particular order is ready for delivery
	 * @param order the order that is ready
	 */
	public void msgOrderIsReady(Waiter newWaiter, String newChoice, Table newTable, int grillPosition){
		Order o = new Order( (WaiterAgent)newWaiter,  newChoice,  newTable);
		o.orderStatus = Order.OrderStatus.OrderReady;
		o.grillPosition = grillPosition;
		ordersForDelivery.add(o);
		stateChanged();
	}
	
	/**
	 * Message sent by the CookAgent to inform the waiter that he is 
	 * out of food for a particular order
	 * @param order the order for which the cook does not have sufficient food
	 */
	public void msgOutOfFoodForOrder(Waiter waiter, String choice, Table table, Set<String> remainingFood){
		for(MyCustomer cust: customers){
			if(table.tableNumber == cust.table.tableNumber){
				cust.state = CustomerState.CustomerNeedsNewMenu;
				cust.editMenu(remainingFood);
				stateChanged();
				return;
			}
		}
	}
	
	/**
	 * Message sent by the CustomerAgent that he/she is done eating and leaving the restaurant
	 * @param sender the CustomerAgent sending the message
	 */
	public void msgDoneEatingAndPaying(Customer sender){
		for(MyCustomer customer : customers){
			if(customer.customer == (CustomerAgent) sender){
				customer.state = CustomerState.CustomerLeft;
				/* the waiter should show that the order has left immediately after the customer leaves, but priority for the 
				 * corresponding action prevents this from happening via the scheduler and such priority should not be changed
				 * because gui updates are not a releveant state for a waiter
				 * 
				 * action cannot be called from CustomerAgent (sender) because table number is required
				 */
				waiterGui.clearOrderOnScreen(customer.table.tableNumber);
				stateChanged();
				return;
			}
		}
	}

	/**
	 * Message Sent by the WaiterGui that it has arrived at the table
	 */
	public void msgGuiAtTable() {//from animation
		atTable.release();// = true;
		isIdle = false;
		stateChanged();
	}
	
	/**
	 * Message sent by the WaiterGui that it has arrived at the cook
	 */
	public void msgGuiAtCook(){
		atCook.release();
		isIdle = false;
		stateChanged();
	}
	
	/**
	 * Message sent by the WaiterGui that it has arrived at the starting location
	 * presumably to pickup a new customer
	 */
	public void msgGuiAtStart(){
		atStart.release();
		isIdle = false;
		stateChanged();
	}
	
	/**
	 * Message sent by the WaiterGui that it has arrived at the cashier
	 */
	public void msgGuiAtCashier(){
		atCashier.release();
		isIdle = false;
		stateChanged();
	}
	
	/**
	 * Message sent by the GUI to request a break;
	 */
	public void goOnBreak(){
		breakState = BreakState.WantsToGoOnBreak;
		stateChanged();
	}
	
	/**
	 * Message sent by the HostAgent telling the waiter if he/she
	 * can go on break
	 * @param canGoOnBreak true if the waiter can go on break, false otherwise
	 */
	public void msgBreakReply(boolean canGoOnBreak){
		if(canGoOnBreak){
			breakState = BreakState.CanGoOnBreak;
			stateChanged();
		}else{
			breakState = BreakState.NoState;
		}
	}
	
	/**
	 * Message received from the Cashier with the amount owed by a Customer
	 * @param sender
	 * @param price
	 */
	public void msgHereIsCheck(Customer sender, double price){
		for(MyCustomer cust : customers){
			if(cust.customer == sender){
				cust.check = price;
				cust.state = CustomerState.CustomerNeedsCheckDelivered;
				stateChanged();
				return;
			}
		}
	}
	
	//----------------SCHEDULER------------------//


	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	protected boolean pickAndExecuteAnAction() {
		/* Think of this next rule as:
            Does there exist a table and customer,
            so that table is unoccupied and customer is waiting.
            If so seat him at the table.
		 */
		
		//If the customer wants to go on break, ask to do so
		if(breakState == BreakState.WantsToGoOnBreak){
			wantToGoOnBreak();
			return true;
		}
		
		//if the waiter is out of customers and can go on break, do so
		if(customers.size() == 0 && breakState == BreakState.CanGoOnBreak){
			becomeIdle(true);
			return true;
		}

		synchronized(customers){
			//most important -- seating new customers
			for(MyCustomer cust : customers){
				if(cust.state == CustomerState.CustomerWaiting){
					seatCustomer(cust);
					return true;
				}
			}
		}
		
		synchronized(customers){
			for(MyCustomer cust : customers){
				if(cust.state == CustomerState.CustomerNeedsNewMenu){
					deliverNewMenu(cust);
					return true;
				}
			}
		}
		
		synchronized(customers){
			//collect all the orders you can
			for(MyCustomer cust : customers){
				//print(cust.customer.getName()+ " "+cust.state);
				if(cust.state == CustomerState.CustomerReadyToOrder){
					takeOrder(cust);
					return true;
				}
			}
		}
		
		synchronized(customers){
			//deliver those orders to the cook
			for(MyCustomer cust : customers){
				if(cust.state == CustomerState.CustomerOrdered){
					giveOrderToCook(cust);
					return true;
				}
			}
		}
		
		synchronized(customers){
			for(MyCustomer cust : customers){
				if(cust.state == CustomerState.CustomerWantsCheck){
					getCheckFromCashier(cust);
				}
			}
		}
		
		synchronized(customers){
			for(MyCustomer cust : customers){
				if(cust.state == CustomerState.CustomerNeedsCheckDelivered){
					deliverCheck(cust);
					return true;
				}
			}
		}
		
		synchronized(customers){
			//notify the host of open tables is least important because there may
			//be customers already there
			for(MyCustomer cust : customers){
				if(cust.state == CustomerState.CustomerLeft){
					notifyHost(cust);
					return true;
				}
			}
		}
		
		synchronized(customers){
			//pickup sitting orders at the cook last
			for(Order order : ordersForDelivery){
				if(order.orderStatus == Order.OrderStatus.OrderReady){
					deliverOrder(order);
					return true;
				}
			}
		}
		
		
		//if there is nothing to do, go to a neutral location
		if(!isIdle){
			becomeIdle(false);
			return true;
		}

		return false;
		//we have tried all our rules and found
		//nothing to do. So return false to main loop of abstract agent
		//and wait.
	}
	
	//---------------------ACTIONS-------------------//
	
	/**
	 * Private method called by the scheduler to seat the customer
	 * @param customer the Customer to seat
	 */
	private void seatCustomer(MyCustomer customer){
		DoSeatCustomer(customer.customer ,customer.table);
		customer.state = CustomerState.CustomerSeated;
		customer.customer.msgFollowMeToTable(customer.menu, this);
		//wait for the waiter to reach the table
		try {
			atTable.acquire();
		}catch (InterruptedException e){
			e.printStackTrace();
		}
		customer.customer.msgWaiterAtTable();
		//stateChanged();
	}
	
	/**
	 * Private method called by the scheduler to request the bill for a customer
	 * from the lone CashierAgent
	 * @param cust the customer whose bill is needed
	 */
	private void getCheckFromCashier(MyCustomer cust){
		DoGetCheckFromCashier(cust);
		cashier.msgComputeBill(cust.choice, cust.customer, this);
		cust.state = CustomerState.CustomerWaitingForCheck;
	}

	
	/**
	 * Private method called by the scheduler to ask the host to go on break
	 */
	private void wantToGoOnBreak(){
		DoWantToGoOnBreak();
		host.msgWantToGoOnBreak(this);
		//stateChanged();
	}
	
	/**
	 * Private method called by the scheduler to deliver an order to the cook
	 * @param customer the Customer whose order should be delivered
	 */
	private void giveOrderToCook(MyCustomer customer){
		DoGiveOrderToCook(customer);
		//wait for the gui to get to the cook
		try {
			atCook.acquire();
		}catch (InterruptedException e){
			e.printStackTrace();
		}
		cook.msgHereIsAnOrder(this, customer.choice, customer.table);
		customer.state = CustomerState.CustomerWaitingForFood;
		
		//stateChanged();
	}
	
	/**
	 * Private method called by the scheduler to deliver a new menu to a customer,
	 * presumably because the cook was out of a particular item that the customer ordered
	 * @param cust the customer to deliver a new menu
	 */
	private void deliverNewMenu(MyCustomer cust){
		DoDeliverNewMenu(cust);
		cust.customer.msgWaiterAtTable();
		cust.customer.msgHereIsNewMenu(cust.menu);
		cust.state = CustomerState.CustomerSeated;
		//stateChanged();
	}

	/**
	 * Private method called by the scheduler to deliver an order from the cook to a customer
	 * @param order the order to deliver
	 */
	private void deliverOrder(Order order){
		DoDeliverOrder(order);
		//wait for the waiter to get to the table
		try {
			atTable.acquire();
		} catch(InterruptedException e){
			e.printStackTrace();
		}
		
		order.orderStatus = Order.OrderStatus.OrderDelivered;
		waiterGui.showDeliveredOrderOnScreen(order.getChoice(), order.getTable().tableNumber);
		for(MyCustomer customer : customers){
			if(order.getTable() == customer.table){
				customer.state = CustomerState.CustomerServed;
				ordersForDelivery.remove(order);
				customer.customer.msgHereIsYourFood();
				return;
			}
		}
		
		stateChanged();
	}
	
	/**
	 * Private method called by the scheduler to take an order from a customer
	 * @param customer the customer to take an order from
	 */
	private void takeOrder(MyCustomer customer){
		DoTakeOrder(customer);
		//wait for the Waiter to get to the table
		customer.state = CustomerState.CustomerGivingOrder;
		customer.customer.msgWhatWouldYouLike();
	}
	
	/**
	 * Private method called by the scheduler so the waiter can tell the
	 * host that a table has been freed up
	 * @param customer the leaving customer
	 */
	private void notifyHost(MyCustomer customer){
		DoNotifyHost(customer);
		host.msgTableIsFree(customer.table);
		customers.remove(customer);

		//stateChanged();
	}
	
	/**
	 * Private method called by the scheduler to make the waiter Idle
	 */
	private void becomeIdle(boolean onBreak){
		DoBecomeIdle(onBreak);
		isIdle = true;
		if(onBreak)
			breakState = BreakState.OnBreak;
		//stateChanged();
	}
	
	/**
	 * Private method called by the scheduler to deliver a check to a customer
	 * @param cust the customer to deliver a check to
	 */
	private void deliverCheck(MyCustomer cust){
		DoDeliverCheck(cust);
		cust.customer.msgHereIsCheck(cust.check);
		cust.state = CustomerState.CustomerCheckDelivered;
	}
	
	
	//----------------------DoXYZ() methods -------------------//
	/*
	 * DoXYZ methods perform the same basic "function" as the Action methods
	 * with the difference that they don't affect Agent data, but instead implement gui 
	 * motion and also print statements to the console.
	 */
	private void DoWantToGoOnBreak(){
		print("wants to go on break");
	}
	
	private void DoGetCheckFromCashier(MyCustomer cust){
		print("Getting check for "+cust.customer.getName()+" from cashier");
		waiterGui.DoGoToCashier();
		try {
			atCashier.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void DoSeatCustomer(CustomerAgent customer, Table table) {
		//Notice how we print "customer" directly. It's toString method will do it.
		//Same with "table"

		waiterGui.DoGoToCustomer(customer);	
		try {
			atStart.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		print("Seating " + customer + " at " + table);
		waiterGui.DoBringToTable(customer, table.tableNumber);
		//hostGui.DoBringToTable(customer, table.tableNumber); 
		
	}
	
	private void DoGiveOrderToCook(MyCustomer cust){
		print("giving order for " + cust.customer.getName() + " to cook");
		waiterGui.DoGoToCook();
	}
	
	private void DoBecomeIdle(boolean onBreak){
		if(onBreak)
			print("is now on break");
		else
			print("is now idle");
		waiterGui.DoGoToIdle();
	}
	
	private void DoDeliverOrder(Order order){
		print("delivering order " + order);
		waiterGui.DoGoToCook();
		//wait until he gets to cook
		try {
			atCook.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		waiterGui.pickedUpFood(order.grillPosition);
		waiterGui.DoGoToTable(order.getTable().tableNumber);
	}
	
	private void DoDeliverNewMenu(MyCustomer cust){
		waiterGui.DoGoToTable(cust.table.tableNumber);
		try{
			atTable.acquire();
		}catch (InterruptedException e){
			e.printStackTrace();
		}
	}
	
	
	private void DoTakeOrder(MyCustomer cust){
		print("taking order for "+cust.customer.getName());
		waiterGui.DoGoToTable(cust.table.tableNumber);	
		try {
			atTable.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		cust.customer.msgWaiterAtTable();
	}
	
	private void DoNotifyHost(MyCustomer cust){
		print("notifying host that "+cust.customer.getName()+ " is leaving");
		
	}
	
	private void DoDeliverCheck(MyCustomer cust){
		print("Delivering check to "+cust.customer.getName());
		waiterGui.DoGoToTable(cust.table.tableNumber);
		try {
			atTable.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//---------------UTILITIES--------------//
	
	/**
	 * A static class to simulate an order
	 *
	 */
    private static class Order {
		   
		public enum OrderStatus {NewOrder, OrderPending, OrderCooking, OrderReady, OrderDelivered};
		
		//Order meant to link the following data
		private WaiterAgent waiter;
	    private String choice;
	    private Table table;
	    public OrderStatus orderStatus;
	    
	    int grillPosition;
		
	    /**
	     * Constructor
	     * @param newWaiter waiter handling order
	     * @param newChoice choice of customer for order
	     * @param newTable table to which the order corresponds
	     */
	    public Order(WaiterAgent newWaiter, String newChoice, Table newTable){
	    	waiter = newWaiter;
	    	choice = newChoice;
	    	table = newTable;
	    	orderStatus = OrderStatus.OrderPending;
	    }
	    
	    
	    //-----------------ACCESSORS-------------//
	    public String getChoice(){
	    	return choice;
	    }
	    
	    public Table getTable(){
	    	return table;
	    }
	    
	    public WaiterAgent getWaiter(){
	    	return waiter;
	    }
	    
	    /**
	     * toString method which changes the Order to a readable string
	     */
	    public String toString(){
	    	StringBuilder builder = new StringBuilder();
	    	builder.append("Choice = "+ choice);
	    	builder.append("\tWaiter = "+ waiter.getName());
	    	builder.append("\tTable = "+ table);
	    	return builder.toString();
	    }
	}

	/**
	 * Mutator method for the Agent's gui
	 * @param gui the WaiterGui for this Agent
	 */
	public void setGui(WaiterGui gui) {
		waiterGui = gui;
	}

	/**
	 * Accessor method for the Agent's gui
	 * @return the WaiterGui for this agent
	 */
	public WaiterGui getGui() {
		return waiterGui;
	}

}

