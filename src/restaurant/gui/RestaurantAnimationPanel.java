package restaurant.gui;

import interfaces.GuiPanel;

import javax.swing.*;

import Person.Role.Role;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;

public class RestaurantAnimationPanel extends JPanel implements ActionListener, GuiPanel {
	
	static final int XCOOR = 0;
	static final int YCOOR = 0;
	
	static final int TABLEXCOOR1 = 130;
	static final int TABLEYCOOR1 = 60;
	static final int TABLEX1 = 50;
	static final int TABLEY1 = 50;
	static final int TABLEXCOOR2 = 190;
	static final int TABLEYCOOR2 = 120;
	static final int TABLEX2 = 50;
	static final int TABLEY2 = 50;
	static final int TABLEXCOOR3 = 250;
	static final int TABLEYCOOR3 = 180;
	static final int TABLEX3 = 50;
	static final int TABLEY3 = 50;
	
    private final int WINDOWX = 800;
    private final int WINDOWY = 400;
    private Image bufferImage;
    private Dimension bufferSize;

    private List<Gui> guis = new ArrayList<Gui>();

    public RestaurantAnimationPanel() {
    	setSize(WINDOWX, WINDOWY);
        setVisible(true);
        
        bufferSize = this.getSize();
 
    	Timer timer = new Timer(20, this );
    	timer.start();
    }

	public void actionPerformed(ActionEvent e) {
		repaint();  //Will have paintComponent called
	}

    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;

        //Clear the screen by painting a rectangle the size of the frame
        g2.setColor(getBackground());
        g2.fillRect(XCOOR, YCOOR, WINDOWX, WINDOWY );

        //Here is the table
        g2.setColor(Color.ORANGE);
        g2.fillRect(TABLEXCOOR1, TABLEYCOOR1, TABLEX1, TABLEY1);//200 and 250 need to be table params
        g2.fillRect(TABLEXCOOR2, TABLEYCOOR2, TABLEX2, TABLEY2);
        g2.fillRect(TABLEXCOOR3, TABLEYCOOR3, TABLEX3, TABLEY3);
        
        //Here is the waiting area
        g2.setColor(Color.white);
        g2.fillRect(40,20,70,60);
        
        //Here is the cooking area
        g2.setColor(Color.lightGray);
        g2.fillRect(370, 30, 30, 120);
        g2.setColor(Color.black);
        g2.fillRect(370, 40, 30, 20);
        g2.fillRect(370, 70, 30, 20);
        g2.fillRect(370, 100, 30, 20);
        
        //Here is the plating area
        g2.setColor(Color.magenta);
        g2.fillRect(370, 130, 30, 40);
        g2.setColor(Color.white);
        g2.fillRect(440, 30, 25, 25);
        

        for(Gui gui : guis) {
            if (gui.isPresent()) {
                gui.updatePosition();
            }
        }

        for(Gui gui : guis) {
            if (gui.isPresent()) {
                gui.draw(g2);
            }
        }
    }

    public void addGui(CustomerGui gui) {
        guis.add(gui);
    }

    public void addGui(HostGui gui) {
        guis.add(gui);
    }
    
    public void addGui(WaiterGui gui) {
    	guis.add(gui);
    }
    
    public void addGui(CashierGui gui) {
    	guis.add(gui);
    }
    
    public void addGui(CookGui gui) {
    	guis.add(gui);
    }

	@Override
	public void addGuiForRole(Role r) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeGuiForRole(Role r) {
		// TODO Auto-generated method stub
		
	}
}
