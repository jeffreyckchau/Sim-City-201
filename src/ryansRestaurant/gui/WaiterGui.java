package ryansRestaurant.gui;


import ryansRestaurant.CookAgent;
import ryansRestaurant.CustomerAgent;
import ryansRestaurant.HostAgent;
import ryansRestaurant.WaiterAgent;
import ryansRestaurant.interfaces.Customer;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.Semaphore;
import java.util.List;

import astar.*;

public class WaiterGui implements Gui {

    private WaiterAgent agent = null;
    private CookGui cook = null;
    private CustomerGui currentCustomer = null;
    
    private RestaurantGui gui;
    private RestaurantLayout restLayout = null;
    private Semaphore sem = new Semaphore(0, true);
    

    private int xPos = -20, yPos = -20;//default waiter position
    private int xDestination = -20, yDestination = -20;//default start position

    public static final int xTable = 200;
    public static final int yTable = 250;
    public static final int xCounter = -20;
    public static final int yCounter = -20;
    public static final int xCook = 250;
    public static final int yCook = 300;
    public static final int xCashier = 0;
    public static final int yCashier = 200;
    
    //default Home Coordinates
    Dimension homePosition = new Dimension(-1, -1);
    Dimension homeCoordinates = new Dimension(-20,-20);

    public enum AgentState {atCounter, goingToCustomer, atCustomer, goingToTable, atTable, goingToCook, atCook, goingToCashier, atCashier, leavingTable, none, goingToCounter, goingToHomePosition, goingToGrill, atGrill};
    private AgentState state = AgentState.atCounter;//default host state
    private String dispName;
    
    public final Map<Dimension, Dimension> positionMap;
    AStarTraversal aStar;
    Position currentPosition;
    Position originalPosition;
    enum ASTARSTATE {none, moving, atDestination};
    ASTARSTATE aStarState = ASTARSTATE.none;
    Semaphore aSem = new Semaphore(0, true);
    
    public WaiterGui(WaiterAgent agent, RestaurantGui gui, RestaurantLayout restLayout, AStarTraversal aStar) {
    	positionMap = new HashMap<Dimension, Dimension>(restLayout.positionMap);
    	this.agent = agent;
    	this.cook = agent.cook.getGui();
        this.gui = gui;
        this.restLayout = restLayout;
        
        this.aStar = aStar;
       
        //if(restLayout.addWaiterGui(this))
        //	DoBeAtHomePosition();
        
        //currentPosition = new Position(homePosition.width, homePosition.height);
        //currentPosition.moveInto(aStar.getGrid());
        //originalPosition = currentPosition;
        
        dispName = new String("" + agent.getName().charAt(0) + agent.getName().charAt(agent.getName().length() - 1));
    }

    
    //from gui to set home coordinates
    public void msgHereAreHomeCoords(Dimension d) {
    	homeCoordinates = d;
    	
    }
    public void msgHereIsHomePosition(Dimension p) {
    	homePosition = p;
    	//originalPosition = new Position(p.width, p.height + 1);
    }
    
    
    
    
    public void updatePosition() {
        {
			if (xPos < xDestination)
				xPos++;
			else if (xPos > xDestination)
				xPos--;
			if (yPos < yDestination)
				yPos++;
			else if (yPos > yDestination)
				yPos--;
        }
        
        if(aStarState == ASTARSTATE.moving && xPos == xDestination && yPos == yDestination) {
        	aStarState = ASTARSTATE.atDestination;
        	aSem.release();
        }
    }

    public void draw(Graphics2D g) {
        g.setColor(Color.MAGENTA);
        g.fillRect(xPos, yPos, 20, 20);
        g.setColor(Color.blue);
        //g.drawString(agent.activity, xPos, yPos);
        g.drawString(dispName, xPos, yPos + 10);
        g.setColor(Color.red);
        int y = yPos - g.getFontMetrics().getHeight();
        for(String line : agent.activity.split("\n")) {
			g.drawString(line, xPos, y += g.getFontMetrics().getHeight() );
		}
    }

    public boolean isPresent() {
        return true;
    }
    
    /**
     * This function assumes the waiter is not in the ryansRestaurant...
     * Will enter the ryansRestaurant and request a home position. Will then GoToHomePostition();
     */
    public void DoEnterRestaurant() {
    	
    	Position entrance = new Position(1, 1);
    	
    	
    	while( !entrance.moveInto(aStar.getGrid()) ) {
    		//System.out.println("EntranceBlocked!!!!!!! waiting 1sec");
    		try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				System.out.println("EXCEPTION!!!!!!!!!! caught while waiting for entrance to clear.");
			}    		
    	}
    	
    	try{
    	move(entrance.getX(), entrance.getY());
    	
    	if(restLayout.addWaiterGui(this)) {
    		currentPosition = new Position(entrance.getX(), entrance.getY());
            //currentPosition.moveInto(aStar.getGrid());
            originalPosition = new Position(homePosition.width, homePosition.height);
    		DoGoToHomePosition();
    	}
    	}catch(Exception e) {
    		DoGoToHomePosition();//Sometime entrance can get clogged so try to find a path again
    	}
    	
    }

    public void DoBringToTable(CustomerAgent customer, int seatnumber) {
       
    	Dimension dim = new Dimension(AnimationPanel.tableMap.get(seatnumber));
    	customer.getGui().DoGoToCoords(dim);
    	
    	DoGoToTable(seatnumber);
    }
    
    public void DoGoToTable(int seatnumber) {
    	Dimension p = restLayout.tablePositionMap.get(seatnumber);
    	Position pos = new Position(p.width, p.height - 1);
    	//System.out.println("Table Position!!!" + pos);
    	guiMoveFromCurrentPostionTo(pos);
        
       // state = AgentState.goingToTable;
    }

    public void DoGoToCook() {
    	//state = AgentState.goingToCook;
    	Dimension d = restLayout.cookOrderCounterPosition;
    	Position p = new Position(d.width, d.height - 1);
    	guiMoveFromCurrentPostionTo(p);
    }
    
    
    public void DoGoToCustomer(Customer cust) {
    	currentCustomer = ((CustomerAgent)cust).getGui();
    	doGoToCustomerUtility();
//    	try {
//			sem.acquire();
//		} catch (InterruptedException e) {
//		}
    }
    private void doGoToCustomerUtility(){
    	if(currentCustomer != null) {
    		Dimension d = new Dimension(currentCustomer.waitingPosition);
//    		xDestination = d.width + 25;
//    		yDestination = d.height + 25;
    		guiMoveFromCurrentPostionTo(new Position(d.width + 1, d.height));
    		//state = AgentState.goingToCustomer;
    	}
    }
    
    
    
//    public void DoGoToGrill(int grillNumber) {
//		Dimension d = new Dimension(cook.grillPostionMap.get(grillNumber));
//		DoGoToGrillCoords(d);	
//	}
    public void DoGoToGrill(int grillNumber) {
		Dimension d = new Dimension(cook.grillPositionMap.get(grillNumber));
		guiMoveFromCurrentPostionTo(new Position(d.width, d.height - 2));
		//state = AgentState.goingToGrill;
	}
//    public void DoGoToGrillCoords(Dimension d) {
//    	if(d != null) {
//    		xDestination = d.width;
//    		yDestination = d.height - 50;
//    		state = AgentState.goingToGrill;
//    	}
//    }
    
    public void DoGoToCashier() {
    	//xDestination = restLayout.cashierXYCoords.width;
    //	state = AgentState.goingToCashier;
    	//yDestination = restLayout.cashierXYCoords.height - 25;
    	Dimension d = restLayout.cashierPosition;
    	guiMoveFromCurrentPostionTo(new Position(d.width + 1, d.height));
    }

    public void DoLeaveCustomer() {
        //xDestination = xCounter;
        //yDestination = yCounter;
    	DoGoToHomePosition();
    }
    public void DoBeAtHomePosition(){
    	xPos = xDestination = homeCoordinates.width;
    //	state = AgentState.goingToHomePosition;
    	yPos = yDestination = homeCoordinates.height;
    }
    public void DoGoToHomePosition() {
//    	xDestination = homeCoordinates.width;
//    	yDestination = homeCoordinates.height;
    	guiMoveFromCurrentPostionTo(originalPosition);
    //	state = AgentState.goingToHomePosition;
    }
//    
//    public void DoGoToCounter() {
//    	xDestination = xCounter;
//        yDestination = yCounter;
//        state = AgentState.goingToCounter;
//    }
    
    /**ASTAR************************************************************
     * Will try to find a path from the currentPosition to to.
     *  The caller's Thread will be here until the gui has reached the destination.
     *  Can throw an exception if no path can be found.
     *  
     * 
     *  @param to The Position to move to. 
     *  
     */
    void guiMoveFromCurrentPostionTo(Position to){
        
    	//First check to make sure the destination is free otherwise wait
    	int waits = 0;
    	while(true){
    		if(currentPosition.equals(to) || to.open(aStar.getGrid()) ){
    			break;
    		}
    		else
    		{
    			try {
					Thread.sleep(1000);
					waits++;
					if(waits > 10){
						if(aStar.getGrid()[to.getX() + 1][to.getY()].availablePermits() > 0)
							guiMoveFromCurrentPostionTo(new Position(to,1, 0));
						else if(aStar.getGrid()[to.getX()][to.getY()-1].availablePermits() > 0)
							guiMoveFromCurrentPostionTo(new Position(to,0,-1));
						waits = 0;
					}
				} catch (InterruptedException e) {
				}
    		}
    	}
    	
    	
    	//System.out.println("[Gaut] " + this + " moving from " + currentPosition.toString() + " to " + to.toString());

    	//System.out.println("TO" + to + "CUR " + currentPosition);
        AStarNode aStarNode = (AStarNode)aStar.generalSearch(currentPosition, to);
       // System.out.println("WHY ISNMT THIS PRINTING");
        List<Position> path = aStarNode.getPath();
        Boolean firstStep = true;
        Boolean gotPermit = true;

        for (Position tmpPath: path) {
         //The first node in the path is the current node. So skip it.
         if (firstStep) {
                firstStep = false;
                continue;
         }

         //Try and get lock for the next step.
         int attempts = 1;
         gotPermit = (new Position(tmpPath.getX(), tmpPath.getY())).moveInto(aStar.getGrid());

         //Did not get lock. Lets make n attempts.
         while (!gotPermit && attempts < 3) {
                //System.out.println("[Gaut] " + guiWaiter.getName() + " got NO permit for " + tmpPath.toString() + " on attempt " + attempts);

                //Wait for 1sec and try again to get lock.
                try { Thread.sleep(1000); }
                catch (Exception e){}

                gotPermit = (new Position(tmpPath.getX(), tmpPath.getY())).moveInto(aStar.getGrid());
                attempts ++;
         }

         //Did not get lock after trying n attempts. So recalculating path.
         if (!gotPermit) {
                //System.out.println("[Gaut] " + guiWaiter.getName() + " No Luck even after " + attempts + " attempts! Lets recalculate");
             path.clear();//this will free some memory
             aStarNode=null;
        	 guiMoveFromCurrentPostionTo(to);
                break;
         }

         //Got the required lock. Lets move.
         //System.out.println("[Gaut] " + guiWaiter.getName() + " got permit for " + tmpPath.toString());
         currentPosition.release(aStar.getGrid());
         currentPosition = new Position(tmpPath.getX(), tmpPath.getY ());
         move(currentPosition.getX(), currentPosition.getY());
        }
    }//End A* guiMoveFromCurrent...

    /**ASTAR************************************************************
     * 
    /* 
    void guiMoveFromCurrentPostionTo(Position to){
        //System.out.println("[Gaut] " + this + " moving from " + currentPosition.toString() + " to " + to.toString());

    	//System.out.println("TO" + to + "CUR " + currentPosition);
        AStarNode aStarNode = (AStarNode)aStar.generalSearch(currentPosition, to);
        //System.out.println("WHY ISNMT THIS PRINTING");
        List<Position> path = aStarNode.getPath();
        Boolean firstStep = true;
        Boolean gotPermit = true;

        for (Position tmpPath: path) {
         //The first node in the path is the current node. So skip it.
         if (firstStep) {
                firstStep = false;
                continue;
         }

         //Try and get lock for the next step.
         int attempts = 1;
         gotPermit = (new Position(tmpPath.getX(), tmpPath.getY())).moveInto(aStar.getGrid());

         //Did not get lock. Lets make n attempts.
         while (!gotPermit && attempts < 3) {
                //System.out.println("[Gaut] " + guiWaiter.getName() + " got NO permit for " + tmpPath.toString() + " on attempt " + attempts);

                //Wait for 1sec and try again to get lock.
                try { Thread.sleep(1000); }
                catch (Exception e){}

                gotPermit = (new Position(tmpPath.getX(), tmpPath.getY())).moveInto(aStar.getGrid());
                attempts ++;
         }

         //Did not get lock after trying n attempts. So recalculating path.
         if (!gotPermit) {
                //System.out.println("[Gaut] " + guiWaiter.getName() + " No Luck even after " + attempts + " attempts! Lets recalculate");
             path.clear();//this will free some memory
             aStarNode=null;
        	 guiMoveFromCurrentPostionTo(to);
                break;
         }

         //Got the required lock. Lets move.
         //System.out.println("[Gaut] " + guiWaiter.getName() + " got permit for " + tmpPath.toString());
         currentPosition.release(aStar.getGrid());
         currentPosition = new Position(tmpPath.getX(), tmpPath.getY ());
         move(currentPosition.getX(), currentPosition.getY());
        }

    }
    */
    void move(int xCoord, int yCoord) {
    	Dimension p = new Dimension(xCoord, yCoord);
    	Dimension d = new Dimension(positionMap.get(p));
    	
    	xDestination = d.width;
    	aStarState = ASTARSTATE.moving;
    	yDestination = d.height;
    	try {
			aSem.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }
    
    
    
    
    
    
    
    
    
    public void prepareForBreak() {
    	gui.setWaiterBreakStatus(agent, "preparingForBreak");
    }
    
    public void backToWork() {
    	gui.setWaiterBreakStatus(agent, "none");
    }
    
    public void onBreak() {
    	gui.setWaiterBreakStatus(agent, "onbreak");
    }

    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }

	
}
