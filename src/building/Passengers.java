package building;

// TODO: Auto-generated Javadoc
//CLASS MADE BY SEAN
/**
 * The Class Passengers. Represents a GROUP of passengers that are 
 * traveling together from one floor to another. Tracks information that 
 * can be used to analyze Elevator performance.
 */
public class Passengers {
	
	/**  Constant for representing direction. */
	private static final int UP = 1;
	private static final int DOWN = -1;
	
	/**  ID represents the NEXT available id for the passenger group. */
	private static int ID=0;

	/** id is the unique ID assigned to each Passenger during construction.
	 *  After assignment, static ID must be incremented.
	 */
	private int id;
	
	/** These fields will be passed into the constructor by the Building.
	 *  This data will come from the .csv file read by the SimController
	 */
	private int time;         // the time that the Passenger will call the elevator
	private int numPass;      // the number of passengers in this group
	private int onFloor;      // the floor that the Passenger will appear on
	private int destFloor;	  // the floor that the Passenger will get off on
	private boolean polite;   // will the Passenger let the doors close?
	private int waitTime;     // the amount of time that the Passenger will wait for the
	                          // Elevator
	
	/** These values will be calculated during construction.
	 */
	private int direction;      // The direction that the Passenger is going
	private int timeWillGiveUp; // The calculated time when the Passenger will give up
	
	/** These values will actually be set during execution. Initialized to -1 */
	private int boardTime=-1;
	private int timeArrived=-1;

	/**
	 * Instantiates a new passengers.
	 *
	 * @param time the time
	 * @param numPass the number of people in this Passenger
	 * @param on the floor that the Passenger calls the elevator from
	 * @param dest the floor that the Passenger is going to
	 * @param polite - are the passengers polite?
	 * @param waitTime the amount of time that the passenger will wait before giving up
	 */
	//checked by Nolan C
	public Passengers(int time, int numPass, int on, int dest, boolean polite, int waitTime) {
	// TODO: Write the constructor for this class
	//       Remember to appropriately adjust the onFloor and destFloor to account  
	//       to convert from American to European numbering...
		
		this.time = time;
		this.numPass = numPass;
		onFloor = on - 1;
		destFloor = dest - 1;
		this.polite = polite;
		this.waitTime = waitTime;
		if (destFloor > onFloor) { // Special getter to get direction
			direction = UP;
		} else {
			direction = DOWN;
		}
		setTimeWillGiveUp(getTime() + getWaitTime()); // Sets up wait time
		
		id = ID;
		ID++;
	}
	
	
	// TODO: Write any required getters/setters for this class

	// 
	/**
	 * Reset static ID. 
	 * This method MUST be called during the building constructor BEFORE
	 * reading the configuration files. This is to provide consistency in the
	 * Passenger ID's during JUnit testing.
	 */
	static void resetStaticID() {
		ID = 0;
	}

	/**
	 * toString - returns the formatted string for this class
	 *
	 * @return the 
	 */
	@Override
	public String toString() {
		return("ID="+id+"   Time="+time+"   NumPass="+numPass+"   From="+(onFloor+1)+"   To="+(destFloor+1)+"   Polite="+polite+"   Wait="+waitTime);
	}


	protected static int getID() {
		return ID;
	}


	protected static void setID(int iD) {
		ID = iD;
	}


	protected int getId() {
		return id;
	}


	protected void setId(int id) {
		this.id = id;
	}


	protected int getTime() {
		return time;
	}


	protected void setTime(int time) {
		this.time = time;
	}


	protected int getNumPass() {
		return numPass;
	}


	protected void setNumPass(int numPass) {
		this.numPass = numPass;
	}


	protected int getOnFloor() {
		return onFloor;
	}


	protected void setOnFloor(int onFloor) {
		this.onFloor = onFloor;
	}


	protected int getDestFloor() {
		return destFloor;
	}


	protected void setDestFloor(int destFloor) {
		this.destFloor = destFloor;
	}


	protected boolean isPolite() {
		return polite;
	}


	protected void setPolite(boolean polite) {
		this.polite = polite;
	}


	protected int getWaitTime() {
		return waitTime;
	}


	protected void setWaitTime(int waitTime) {
		this.waitTime = waitTime;
	}


	protected int getDirection() {
		return direction;
	}


	protected void setDirection(int direction) {
		this.direction = direction;
	}


	protected int getTimeWillGiveUp() {
		return timeWillGiveUp;
	}


	protected void setTimeWillGiveUp(int timeWillGiveUp) {
		this.timeWillGiveUp = timeWillGiveUp;
	}


	protected int getBoardTime() {
		return boardTime;
	}


	protected void setBoardTime(int boardTime) {
		this.boardTime = boardTime;
	}


	protected int getTimeArrived() {
		return timeArrived;
	}


	protected void setTimeArrived(int timeArrived) {
		this.timeArrived = timeArrived;
	}


	protected static int getUp() {
		return UP;
	}


	protected static int getDown() {
		return DOWN;
	}
	
	

}
