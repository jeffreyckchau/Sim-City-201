package residence.gui;

import gui.Gui;
import gui.Building.ResidenceBuilding;
import interfaces.GuiPanel;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import Person.Role.Role;

public class ApartmentAnimationPanel extends JPanel implements MouseListener, GuiPanel  {
	private List<Gui> guis = new ArrayList<Gui>();
	private List<ResidenceBuilding> apartments = new ArrayList<>();
	
	static final int XCOOR = 0;
	static final int YCOOR = 0;
	
    private final int WINDOWX = 800;
    private final int WINDOWY = 400;
	
	
	public ApartmentAnimationPanel() {
		setVisible(true);

		this.setBackground(Color.BLACK);
		addMouseListener(this);

	}
	
	public void addApartment(ResidenceBuilding b) {
		apartments.add(b);
		guis.add(b);
	}
	
	public void addGui(ResidenceBuilding gui){
		addApartment(gui);
	}

	
	public void mouseClicked(MouseEvent me) {
		for(ResidenceBuilding rb : apartments) {
			if(rb.contains(me.getX(), me.getY())){
				rb.displayBuilding();
			}
		}
	}
	
	@Override
	public void mouseEntered(MouseEvent arg0) {
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		
	}
	
	public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        
        g2.setColor(getBackground());
        g2.fillRect(XCOOR, YCOOR, WINDOWX, WINDOWY);
        
        g2.setColor(new Color(200, 50, 50));
        g2.fillRect(300,0,200,400);
        g2.setColor(Color.orange);
        g2.fillRect(310,0,180,400);
        
        
        g2.setColor(new Color(100, 180, 200));
        g2.fillOval(290, 140, 40, 40);
        g2.fillOval(470, 140, 40, 40);
        g2.fillOval(290, 340, 40, 40);
        g2.fillOval(470, 340, 40, 40);
        g2.setColor(Color.yellow);
        g2.fillOval(300, 150, 20, 20);
        g2.fillOval(480, 150, 20, 20);
        g2.fillOval(300, 350, 20, 20);
        g2.fillOval(480, 350, 20, 20);
        
        g2.setColor(Color.white);
        g2.drawString(apartments.get(0).getName(), 300, 100);
        g2.drawString(apartments.get(1).getName(), 300, 300);
        g2.drawString(apartments.get(2).getName(), 420, 100);
        g2.drawString(apartments.get(3).getName(), 420, 300);
        
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

	@Override
	public void addGuiForRole(Role r) {
		// TODO Auto-generated method stub
		
	}

	public void removeGuiForRole(Role r) {
		// TODO Auto-generated method stub
		
	}
}