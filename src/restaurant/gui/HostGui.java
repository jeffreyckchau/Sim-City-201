package restaurant.gui;


import restaurant.CustomerAgent;
import restaurant.HostAgent;

import java.awt.*;

public class HostGui implements Gui {

    private HostAgent agent = null;

    private int xPos = -20, yPos = -20;//default waiter position
    private int xDestination = -20, yDestination = -20;//default start position

    private int xTable = -20;
    private int yTable = -20;

    public HostGui(HostAgent agent) {
        this.agent = agent;
    }

    public void updatePosition() {
        if (xPos < xDestination)
            xPos++;
        else if (xPos > xDestination)
            xPos--;

        if (yPos < yDestination)
            yPos++;
        else if (yPos > yDestination)
            yPos--;

        if (xPos == xDestination && yPos == yDestination
        		& (xDestination == xTable + 20) & (yDestination == yTable - 20)) {
           agent.msgAtTable();
        }
    }

    public void draw(Graphics2D g) {
        g.setColor(Color.MAGENTA);
        g.fillRect(xPos, yPos, 20, 20);
    }

    public boolean isPresent() {
        return true;
    }

    public void DoBringToTable(CustomerAgent customer) {
    	xDestination = agent.getTableX(customer.getTableNum()) + 20;
        yDestination = agent.getTableY(customer.getTableNum()) - 20;
        xTable = agent.getTableX(customer.getTableNum());
        yTable = agent.getTableY(customer.getTableNum());
    }

    public void DoLeaveCustomer() {
        xDestination = -20;
        yDestination = -20;
    }

    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }
}
