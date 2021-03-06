package astar;
import java.util.*;
import java.util.concurrent.*;

public class VehicleAStarTraversal extends AStarTraversal
{
    private Semaphore[][] grid;
    private Semaphore[][] roadGrid;

    public VehicleAStarTraversal(Semaphore[][] agentGrid, Semaphore[][]roadGrid ){
	super(agentGrid);
	this.roadGrid = roadGrid;
	this.grid = agentGrid;
	
    }
    
    
    
    
    public List<Node> expandFunc(Node n) {
	AStarNode node = (AStarNode) n;
	//loop computes the positions you can get to from node
	List<Node> expandedNodes = new ArrayList<Node>();
	List<Position> path = node.getPath();
	Position pos = node.getPosition();
	int x = pos.getX();
	int y = pos.getY();
	//this next pair of loops will create all the possible moves
	//from pos.
	for(int i = -1; i <= 1; i++) {//increment for x direction
	    for (int j = -1; j <= 1; j++) {//increment for y direction
		//create the potential next position
		int nextX=x+i;
		int nextY=y+j;
		//make sure next point is on the grid
		if ((nextX+1>grid.length || nextY+1>grid[0].length) ||
		      (nextX<0 || nextY<0)) continue;
		Position next = new Position(nextX,nextY);
		//System.out.println("considering"+next);
		if (inPath(next,path) || !next.open(grid) || !gridTypeOk(next) ) continue;
		//printCurrentList();
		//System.out.println("available"+next);
		AStarNode nodeTemp = new AStarNode(next);

		//update distance travelled
		nodeTemp.setDistTravelled(
                        node.getDistTravelled()+pos.distance(next));
		//update approximate total distance to destination
		//note that we are computing the straight-line
		//heuristic on the fly right here from next to endingState
		nodeTemp.setApproxTotalDist(
			nodeTemp.getDistTravelled() + next.distance((Position)endingState));	
		//update internal path
		nodeTemp.updatePath(path);
		expandedNodes.add(nodeTemp);//could have just added
					    //them directly to nodelist 
	    }
	}
	return expandedNodes;
    }//end expandFunc

    //Vehicles cannot drive where there are no roads
    public boolean gridTypeOk(Position next){
    	boolean road = false;
    	
    	if(next.open(roadGrid))
    		road = false;
    	else
    		road = true;
    			    	
    	if(!next.open(grid))
    		return false;
      	
    	return road;
    }

    public Semaphore[][] getGrid(){return grid;}
}