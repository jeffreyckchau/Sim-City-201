package restaurant;

/**
 * Package visible class that represents a table
 */
public class Table {
	
	//data includes whether or not the table is occupied and a table number
	private CustomerAgent occupiedBy;
	int tableNumber; //package visible
	
	/**
	 * Constructor
	 * @param tableNumber
	 */
	Table(int tableNumber) {
		this.tableNumber = tableNumber;
	}

	/**
	 * Fills the table with the paramter customer
	 * @param cust customer to sit at the table
	 */
	void setOccupant(CustomerAgent cust) {
		occupiedBy = cust;
	}

	/**
	 * Makes the table unoccupied
	 */
	void setUnoccupied() {
		occupiedBy = null;
	}

	/**
	 * Accessor method of the occupant
	 * @return
	 */
	CustomerAgent getOccupant() {
		return occupiedBy;
	}

	/**
	 * Returns whether or not the table is occupied
	 * @return whether or not the table is occupied
	 */
	boolean isOccupied() {
		return occupiedBy != null;
	}

	/**
	 * To String method for thet able
	 * @return the string representation of the table
	 */
	public String toString() {
		return "table " + tableNumber;
	}
	
	/**
	 * Compares two tables for equality
	 * @param table the table to compare to
	 * @return true if equal, false otherwise
	 */
	public boolean equals(Table table){
		return this.tableNumber == table.tableNumber;
	}
}
