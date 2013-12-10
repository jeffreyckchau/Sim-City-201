package gui;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**	An animation panel for bus stops.
 * 
 * @author Team29
 *
 */
@SuppressWarnings("serial")
public class LoadGui extends JFrame implements ActionListener{

	LoadGui thisFrame = null;
	
	private JScrollPane configPane =
            new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                    JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	private JPanel view = new JPanel();
	//private List<JButton> configList = new ArrayList<>();
	
	JLabel loadTimeLabel = new JLabel();
	JLabel infoLabel = new JLabel();
	boolean load = true;
	int defaultWaitTime = 5;//<<-The default time to wait before loading config.
	int time = 0;
	Timer timer = new Timer();
	
	JButton cancelButton = new JButton("Cancel");
	
	//JButton loadDefault = new JButton("Default");
	
	//JButton loadScenario1 = new JButton("Scenario 1");
	//JButton loadScenario2 = new JButton("Scenario 2");
	
	String loadThisConfig = new String("Default");
	JPanel loadingPanel = new JPanel();
	
	List<JButton> buttonList = new ArrayList<JButton>();
	Font font = new Font(Font.SERIF, Font.BOLD, 24);
	
	
	//Default constructor
	public LoadGui() {
		thisFrame = this;
		this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.X_AXIS));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("Team 29     Sim City Gui Configuration Window");
		setBounds(50, 50, 800, 800);
		view.setLayout(new BoxLayout((Container) view, BoxLayout.Y_AXIS));
		configPane.setViewportView(view);
		
		Dimension paneSize = new Dimension(400, 800);
		configPane.setMaximumSize(paneSize);
		configPane.setMinimumSize(paneSize);
		configPane.setPreferredSize(paneSize);
		this.add(configPane);
		
		JLabel b = new JLabel("Load: ");
		b.setFont(font);
		view.add(b);


		loadTimeLabel.setFont(font);
		infoLabel.setFont(font);
		
		
		
		cancelButton.addActionListener(this);
		cancelButton.setFont(font);
		cancelButton.setPreferredSize(paneSize);
		cancelButton.setMinimumSize(paneSize);
		cancelButton.setMaximumSize(paneSize);
		
//		loadDefault.addActionListener(this);
//		loadDefault.setFont(new Font(Font.SERIF, Font.BOLD, 24));
//		loadScenario2.addActionListener(this);
//		loadScenario2.setFont(new Font(Font.DIALOG, Font.PLAIN, 24));
//		loadScenario1.addActionListener(this);
//		loadScenario1.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 24));
		
		
		Dimension size = new Dimension(paneSize.width, paneSize.height/8);
		
		buttonList.add(new JButton("Default"));
		buttonList.add(new JButton("Scenario 1"));
		buttonList.add(new JButton("Scenario 2"));
		
		for(JButton bl : buttonList){
			bl.addActionListener(this);
			bl.setFont(font);
			bl.setPreferredSize(size);
			bl.setMaximumSize(size);
			bl.setMinimumSize(size);
			view.add(bl);
		}
		
		
		loadingPanel.setMaximumSize(paneSize);
		loadingPanel.setMinimumSize(paneSize);
		loadingPanel.setPreferredSize(paneSize);
		this.add(loadingPanel);
		time = defaultWaitTime;
		timeCountDown();
		
		populateLoadingPanel();
		this.revalidate();
		this.repaint();
		this.setVisible(true);
	}

	
	
	
	void populateLoadingPanel(){
		loadingPanel.setLayout(new BoxLayout(loadingPanel, BoxLayout.Y_AXIS));
		loadingPanel.removeAll();
		
		if(loadThisConfig.length() != 0)
		{
			infoLabel.setText(loadThisConfig + " configuration");
			loadingPanel.add(infoLabel);
			loadingPanel.add(loadTimeLabel);
			loadingPanel.add(cancelButton);
		}else{
			infoLabel.setText("<<~~~ Please choose a file to load.");
			loadingPanel.add(infoLabel);
		}
		
		
		
		loadingPanel.revalidate();
		loadingPanel.repaint();
	}
	
	
	
	
	
	
	
	
	void updateDispTime(){
		loadTimeLabel.setText("Loading in " + time + " seconds!");
		loadTimeLabel.setForeground(Color.red);
		if(time == 10)
			loadTimeLabel.setForeground(Color.black);
		
		this.revalidate();
		this.repaint();
	}
	
	
	void timeCountDown(){
		
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				if(!load){
					return;
				}
				
				time--;
				thisFrame.updateDispTime();
				if(time > 0)
					timeCountDown();
				else if(time == 0 && load){
						//Timer has expired load the current loadThisConfig
					@SuppressWarnings("unused")
					SimCity201Gui gui = new SimCity201Gui(loadThisConfig);
					thisFrame.setVisible(false);
				}
			}
		}, 1000);
	}
	
	
	
	
	
	
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		load = false;
		time = defaultWaitTime;updateDispTime();
		
		if(e.getSource() == cancelButton){
			loadThisConfig = "";
			populateLoadingPanel();
			return;
		}
		else if(e.getSource() instanceof JButton){
			JButton b = (JButton)e.getSource();
			loadThisConfig = b.getText();
			SimCity201Gui gui = new SimCity201Gui(loadThisConfig);
		}
		
		this.setVisible(false);
	}
	
	
	
	
	
public static void main(String[] args) {
	LoadGui splash = new LoadGui();
	splash.setVisible(true);
}

}
