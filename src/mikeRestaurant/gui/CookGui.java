package mikeRestaurant.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import mikeRestaurant.CookAgent;

public class CookGui implements Gui {

	private CookAgent agent;
	
	private int xPos;
	private int yPos;
	
	private int xDestination;
	private int yDestination;
	
	private final int WIDTH = 30;
	private final int HEIGHT = 30;
	
	private final int COOKING_X = 275;
	private final int COOKING_Y = 450;
	
	private final int GRILL_INITIAL_X = 275;
	private final int GRILL_INITIAL_Y = 425;
	private final int GRILL_WIDTH = 25;
	
	public final static int PICKUP_X = 130;
	public final static int PICKUP_Y = 450;
	
	public final int FRIDGE_X = 400;
	public final int FRIDGE_Y = 450;
	
	public final int LABEL_HEIGHT = 410;
	
	private boolean moving;
	private RestaurantGui gui;
	
	private List<FoodGui> foods;
	
	public CookGui(CookAgent agent, RestaurantGui gui){
		this.agent = agent;
		
		xPos = COOKING_X;
		yPos = COOKING_Y;
		
		xDestination = COOKING_X;
		yDestination = COOKING_Y;
		
		moving = false;
		
		this.gui = gui;
		foods = new ArrayList<FoodGui>();
		
	}
	
	@Override
	public void updatePosition() {
		// TODO Auto-generated method stub
		if(moving){
			if (xPos < xDestination)
				xPos++;
			else if (xPos > xDestination)
				xPos--;
	
			if (yPos < yDestination)
				yPos++;
			else if (yPos > yDestination)
				yPos--;
			
			if (xPos == xDestination && yPos == yDestination){
				if(xPos == COOKING_X){
					agent.msgAtCookingLocation();
				}else if(xPos == PICKUP_X){
					agent.msgAtPickupLocation();
				}else if(xPos == FRIDGE_X){
					agent.msgAtFridge();
				}else{
					
				}
				moving = false;
			}
		}
	}
	
	public void addFood(String type, int grillPosition){
		foods.add(new FoodGui(type, grillPosition));
	}
	
	public void foodPrepared(int grillPosition){
		for(FoodGui fg : foods){
			if(fg.grillPosition == grillPosition && fg.state == FoodGuiState.InTransit){
				fg.state = FoodGuiState.Plated;
				return;
			}
		}
	}
	
	public void foodInTransit(int grillPosition){
		for(FoodGui fg : foods){
			if(fg.grillPosition == grillPosition && fg.state == FoodGuiState.Cooking){
				fg.state = FoodGuiState.InTransit;
				return;
			}
		}
	}
	
	public void foodPickedUp(int grillPosition){
		for(FoodGui fg : foods){
			if(fg.grillPosition == grillPosition && fg.state == FoodGuiState.Plated){
				fg.state = FoodGuiState.Done;
				return;
			}
		}
	}
	
	public void DoGoToCookingLocation(){
		xDestination = COOKING_X;
		yDestination = COOKING_Y;
		
		moving = true;
	}
	
	public void DoGoToPickupLocation(){
		xDestination = PICKUP_X;
		yDestination = PICKUP_Y;
		
		moving = true;
	}
	
	public void DoGoToRefrigerator(){
		xDestination = FRIDGE_X;
		yDestination = FRIDGE_Y;
		
		moving = true;
	}

	@Override
	public void draw(Graphics2D g) {
		// TODO Auto-generated method stub
		g.setColor(Color.GREEN);
		g.fillRect(xPos, yPos, WIDTH, HEIGHT);
		g.setColor(Color.BLACK);
		g.drawString("Pickup", PICKUP_X, LABEL_HEIGHT);
        g.drawString("Cooking", COOKING_X, LABEL_HEIGHT);
		
		for(int i = foods.size()-1; i >=0; i--){
			FoodGui fg = foods.get(i);
			if(fg.state == FoodGuiState.Cooking){
				fg.icon.paintIcon(this.gui, g, GRILL_INITIAL_X+fg.grillPosition*GRILL_WIDTH, GRILL_INITIAL_Y);
			}else if(fg.state == FoodGuiState.Plated){
				fg.icon.paintIcon(this.gui, g, PICKUP_X+fg.grillPosition*GRILL_WIDTH, GRILL_INITIAL_Y);
			}else if(fg.state == FoodGuiState.InTransit){
				//do nothing
			}else if(fg.state == FoodGuiState.Done){
				foods.remove(i);
			}
			
		}
	}

	@Override
	public boolean isPresent() {
		// TODO Auto-generated method stub
		return true;
	}
	
	public enum FoodGuiState {Cooking, InTransit, Plated, Done};
	
	private class FoodGui {
		
		private String type;
		private ImageIcon icon;
		private String filepath;
		int grillPosition;
		
		FoodGuiState state = FoodGuiState.Cooking;
		
		public FoodGui(String type, int grillPosition){
			this.type = type;
			
			switch(type){
				case "Steak":
					filepath = "steak.png";
					break;
				case "Salad":
					filepath = "salad.png";
					break;
				case "Chicken":
					filepath = "chicken.png";
					break;
				case "Pizza":
					filepath = "pizza.png";
					break;
				default:
					filepath = "default.png";
			}
			
			icon = new ImageIcon(filepath);
			this.grillPosition = grillPosition;
		}
	}
}
