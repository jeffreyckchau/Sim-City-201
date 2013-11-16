package gui;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class SimCity201Gui extends JFrame {

	private final int WINDOWX = 900;
	private final int WINDOWY = 900;
	
	SimCityLayout layout = new SimCityLayout(WINDOWX, WINDOWY);
	AnimationPanel animationPanel = new AnimationPanel(layout);
	
	
	
	/**
	 * Default Constructor Initializes gui
	*/
	public SimCity201Gui() {
		
		setBounds(50, 50, WINDOWX, WINDOWY);
		
		
		/**
		 * This stuff prolly shouldn't be here but for testing purposes
		 */
		//down left
		if(!layout.addRoad(6, 5, 3, 20))
			System.out.println("addRoad unsuccessful");
		//across bottom
		if(!layout.addRoad(6, 25, 20, 3))
			System.out.println("addRoad unsuccessful");
		// across top
		if(!layout.addRoad(9, 5, 20, 3))
			System.out.println("addRoad unsuccessful");
		 //down right
		if(!layout.addRoad(26, 8, 3, 20))
			System.out.println("addRoad unsuccessful");
		//down middle
		if(!layout.addRoad(16, 8, 2, 17))
			System.out.println("addRoad unsuccessful");
		
		
		
		add(animationPanel);
		

	}
	
	
	
	
	public static void main(String[] args) {
		SimCity201Gui gui = new SimCity201Gui();
        gui.setTitle("SimCity201 V0.5  - Team 29");
        gui.setVisible(true);
        gui.setResizable(false);
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

}
