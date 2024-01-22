package building;
import java.util.ArrayList;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

// Written by Yuki Yanase

/**
 * The Class Elevator.
 *
 * @author This class will represent an elevator, and will contain
 * configuration information (capacity, speed, etc) as well
 * as state information - such as stopped, direction, and count
 * of passengers targeting each floor...
 */
public class Elevator {
	
	/**  Elevator State Variables - These are visible publicly. */
	public final static int STOP = 0;
	
	/** The Constant MVTOFLR. */
	public final static int MVTOFLR = 1;
	
	/** The Constant OPENDR. */
	public final static int OPENDR = 2;
	
	/** The Constant OFFLD. */
	public final static int OFFLD = 3;
	
	/** The Constant BOARD. */
	public final static int BOARD = 4;
	
	/** The Constant CLOSEDR. */
	public final static int CLOSEDR = 5;
	
	/** The Constant MV1FLR. */
	public final static int MV1FLR = 6;

	/** Default configuration parameters for the elevator. These should be
	 *  updated in the constructor.
	 */
	private int capacity = 15;				// The number of PEOPLE the elevator can hold
	
	/** The ticks per floor. */
	private int ticksPerFloor = 5;			// The time it takes the elevator to move between floors
	
	/** The ticks door open close. */
	private int ticksDoorOpenClose = 2;  	// The time it takes for doors to go from OPEN <=> CLOSED
	
	/** The pass per tick. */
	private int passPerTick = 3;            // The number of PEOPLE that can enter/exit the elevator per tick
	
	/**  Finite State Machine State Variables. */
	private int currState;		// current state
	
	/** The prev state. */
	private int prevState;      // prior state
	
	/** The prev floor. */
	private int prevFloor;      // prior floor
	
	/** The curr floor. */
	private int currFloor;      // current floor
	
	/** The direction. */
	private int direction;      // direction the Elevator is traveling in.

	/** The time in state. */
	private int timeInState;    // represents the time in a given state
	                            // reset on state entry, used to determine if
	                            // state has completed or if floor has changed
	                            // *not* used in all states 

	/** The door state. */
    private int doorState;      // used to model the state of the doors - OPEN, CLOSED
	                            // or moving
								// fully closed = 0, half open = 1, fully open = ticksDoorOpenClose (2)

	
	/** The passengers. */
    private int passengers;  	// the number of people in the elevator
	
	/** The num passengers boarding. */
	private int numPassengersBoarding;	// the number of people boarding the elevator in a given boarding period
	
	/** The pass by floor. */
	private ArrayList<Passengers>[] passByFloor;  // Passengers to exit on the corresponding floor

	/** The move to floor. */
	private int moveToFloor;	// When exiting the STOP state, this is the floor to move to without
	                            // stopping.
	
	/** The post move to floor dir. */
    private int postMoveToFloorDir; // This is the direction that the elevator will travel AFTER reaching
	                                // the moveToFloor in MVTOFLR state.

	/** The offload delay. */
    private int offloadDelay;	// The required time to offload passengers
	
	/** The board delay. */
	private int boardDelay;		// The required time to board passengers
	
	/** The offloading. */
	private boolean offloading;		// true if in the process of offloading; false if not
	
	/** The boarding. */
	private boolean boarding;		// true if in the process of boarding; false if not
	
	/**
	 * Instantiates a new elevator.
	 *
	 * @param numFloors the num floors
	 * @param capacity the capacity
	 * @param floorTicks the floor ticks
	 * @param doorTicks the door ticks
	 * @param passPerTick the pass per tick
	 */
	@SuppressWarnings("unchecked")
	public Elevator(int numFloors,int capacity, int floorTicks, int doorTicks, int passPerTick) {		
		this.prevState = STOP;
		this.currState = STOP;
		this.timeInState = 0;
		this.currFloor = 0;
		passByFloor = new ArrayList[numFloors];
		
		for (int i = 0; i < numFloors; i++) 
			passByFloor[i] = new ArrayList<Passengers>(); 

		//TODO: Finish this constructor, adding configuration initialiation and
		//      initialization of any other private fields, etc.
		direction = 1;
		passengers = 0;
		
		this.capacity = capacity;
		this.ticksPerFloor = floorTicks;
		this.ticksDoorOpenClose = doorTicks;
		this.passPerTick = passPerTick;
	}


	//METHODS CHECKED BY SEAN AMINOV
	
	/**
	 * Updates the current state of the elevator.
	 * Resets timeInState if state changes.
	 *
	 * @param currState the current state of the elevator
	 */
	void updateCurrState(int currState) {
		this.prevState = this.currState;
		this.currState = currState;
		if (this.prevState != this.currState) {
			timeInState = 0;
		}
	}
	
	
	/**
	 * Moves the elevator in appropriate direction.
	 * Updates the current floor if in a new floor.
	 */
	void moveElevator() {
		timeInState++;
		prevFloor = currFloor;
		if ((timeInState % ticksPerFloor) == 0) {	
			currFloor = currFloor + direction;	
		}
	}
	
	/**
	 * Determines if the elevator is moving in-between floors.
	 *
	 * @return true, if the elevator is still moving; else false.
	 */
	boolean stillMoving() {
		return ((timeInState % ticksPerFloor) != 0);
	}
	
	
	/**
	 * Offload passengers who need to get off on the current floor.
	 * When called for the first time, offload delay is calculated based on the number of passengers offloading
	 *
	 * @return true, if offloading is successful
	 */
	boolean offloadPassengers() {
		if (!offloading) {
			int numPassengers = 0;
			for (int i = 0; i < passByFloor[currFloor].size(); i++) {
				numPassengers += passByFloor[currFloor].get(i).getNumPass();
			}
			offloadDelay = (int)(Math.ceil((double)(numPassengers) / (double)(passPerTick)));
			passengers -= numPassengers;
		}
		offloading = true;
		timeInState++;
		return true;
	}
	
	/**
	 * Determines if the offloading process is still ongoing.
	 *
	 * @return true, if passengers are still offloading; else false.
	 */
	boolean stillOffloading() {
		if (timeInState < offloadDelay) {
			return true;
		}
		offloadDelay = 0;
		offloading = false;
		return false;
	}
	
	/**
	 * Boards passengers onto the elevator.
	 * Calculates board delay based on the number of passengers boarding
	 *
	 * @param p the passenger group to board the elevator
	 * @return true, if successful
	 */
	boolean boardPassengers(Passengers p) {
		if (capacityReached(p)) {
			return false;
		}
		numPassengersBoarding += p.getNumPass();
		boardDelay = (int)(Math.ceil((double)(numPassengersBoarding) / (double)(passPerTick)));
		passByFloor[p.getDestFloor()].add(p);
		passengers += p.getNumPass();
		boarding = true;
		return true;
	}
	
	/**
	 * Determines whether or not there are passengers in the elevator who need to offload 
	 * in the current floor.
	 *
	 * @return true, if there are passengers to offload; else false.
	 */
	boolean offloadHere() {
		return (!passByFloor[currFloor].isEmpty());
	}
	
	/**
	 * Determines if the boarding process is still ongoing.
	 *
	 * @return true, if still boarding; else false;
	 */
	boolean stillBoardingPassengers() {
		timeInState++;
		if (timeInState < boardDelay && passengers <= capacity) {
			return true;
		}
		boardDelay = 0;
		boarding = false;
		numPassengersBoarding = 0;
		return false;
	}
	
	/**
	 * Opens the elevator door.
	 */
	void openDoor() {
		doorState++;
		timeInState++;
	}
	
	/**
	 * Closes the elevator door.
	 */
	void closeDoor() {
		doorState--;
		timeInState++;
	}
	
	/**
	 * Determines if the door is still in the process of closing.
	 *
	 * @return true, if door is still closing; else false
	 */
	boolean doorStillClosing() {
		return (doorState != 0); 
	}
	
	/**
	 * Checks if elevator door is closed.
	 *
	 * @return true, if door is fully closed; else false
	 */
	boolean isClosed() {
		return (doorState == 0);
	}
	
	/**
	 * Checks if elevator door is open.
	 *
	 * @return true, if door is fully open; else false
	 */
	boolean isOpen() {
		return (doorState == ticksDoorOpenClose);
	}
	
	/**
	 * Checks if elevator is empty.
	 *
	 * @return true, if no passengers are on board
	 */
	boolean isEmpty() {
		return (passengers == 0);
	}
	
	/**
	 * Determines if there is enough space for another passenger group to be boarded.
	 *
	 * @param p the passenger group to be boarded
	 * @return true, if passengers cannot fit onto elevator
	 */
	boolean capacityReached(Passengers p) {
		return (passengers + p.getNumPass() > capacity);
	}
	
	/**
	 * Determines if the elevator is at maximum capacity.
	 *
	 * @return true, if elevator is full
	 */
	boolean elevatorFull() {
		return (passengers >= capacity);
	}
	
	/**
	 * Changes the direction of motion of  elevator.
	 */
	void changeDirection() {
		direction *= -1;
	}
	
	/**
	 * Calculates the number of passenger groups offloading in the current floor.
	 *
	 * @return the number of passenger groups offloading
	 */
	int totalOffloading() {
		return passByFloor[currFloor].size();
	}
	
	/**
	 * Gets the capacity.
	 *
	 * @return the capacity
	 */
	int getCapacity() {
		return capacity;
	}

	/**
	 * Sets the capacity.
	 *
	 * @param capacity the new capacity
	 */
	void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	/**
	 * Gets the ticks per floor.
	 *
	 * @return the ticks per floor
	 */
	int getTicksPerFloor() {
		return ticksPerFloor;
	}

	/**
	 * Sets the ticks per floor.
	 *
	 * @param ticksPerFloor the new ticks per floor
	 */
	void setTicksPerFloor(int ticksPerFloor) {
		this.ticksPerFloor = ticksPerFloor;
	}

	/**
	 * Gets the ticks door open close.
	 *
	 * @return the ticks door open close
	 */
	int getTicksDoorOpenClose() {
		return ticksDoorOpenClose;
	}

	/**
	 * Sets the ticks door open close.
	 *
	 * @param ticksDoorOpenClose the new ticks door open close
	 */
	void setTicksDoorOpenClose(int ticksDoorOpenClose) {
		this.ticksDoorOpenClose = ticksDoorOpenClose;
	}

	/**
	 * Gets the pass per tick.
	 *
	 * @return the pass per tick
	 */
	int getPassPerTick() {
		return passPerTick;
	}

	/**
	 * Sets the pass per tick.
	 *
	 * @param passPerTick the new pass per tick
	 */
	void setPassPerTick(int passPerTick) {
		this.passPerTick = passPerTick;
	}

	/**
	 * Gets the curr state.
	 *
	 * @return the curr state
	 */
	int getCurrState() {
		return currState;
	}

	/**
	 * Gets the prev state.
	 *
	 * @return the prev state
	 */
	int getPrevState() {
		return prevState;
	}

	/**
	 * Sets the prev state.
	 *
	 * @param prevState the new prev state
	 */
	void setPrevState(int prevState) {
		this.prevState = prevState;
	}

	/**
	 * Gets the prev floor.
	 *
	 * @return the prev floor
	 */
	int getPrevFloor() {
		return prevFloor;
	}

	/**
	 * Sets the prev floor.
	 *
	 * @param prevFloor the new prev floor
	 */
	void setPrevFloor(int prevFloor) {
		this.prevFloor = prevFloor;
	}

	/**
	 * Gets the curr floor.
	 *
	 * @return the curr floor
	 */
	int getCurrFloor() {
		return currFloor;
	}

	/**
	 * Sets the curr floor.
	 *
	 * @param currFloor the new curr floor
	 */
	void setCurrFloor(int currFloor) {
		this.currFloor = currFloor;
	}

	/**
	 * Gets the direction.
	 *
	 * @return the direction
	 */
	int getDirection() {
		return direction;
	}

	/**
	 * Sets the direction.
	 *
	 * @param direction the new direction
	 */
	void setDirection(int direction) {
		this.direction = direction;
	}

	/**
	 * Gets the time in state.
	 *
	 * @return the time in state
	 */
	int getTimeInState() {
		return timeInState;
	}

	/**
	 * Sets the time in state.
	 *
	 * @param timeInState the new time in state
	 */
	void setTimeInState(int timeInState) {
		this.timeInState = timeInState;
	}

	/**
	 * Gets the door state.
	 *
	 * @return the door state
	 */
	int getDoorState() {
		return doorState;
	}

	/**
	 * Sets the door state.
	 *
	 * @param doorState the new door state
	 */
	void setDoorState(int doorState) {
		this.doorState = doorState;
	}

	/**
	 * Gets the passengers.
	 *
	 * @return the passengers
	 */
	int getPassengers() {
		return passengers;
	}

	/**
	 * Sets the passengers.
	 *
	 * @param passengers the new passengers
	 */
	void setPassengers(int passengers) {
		this.passengers = passengers;
	}

	/**
	 * Gets the pass by floor.
	 *
	 * @return the pass by floor
	 */
	ArrayList<Passengers>[] getPassByFloor() {
		return passByFloor;
	}

	/**
	 * Sets the pass by floor.
	 *
	 * @param passByFloor the new pass by floor
	 */
	void setPassByFloor(ArrayList<Passengers>[] passByFloor) {
		this.passByFloor = passByFloor;
	}

	/**
	 * Gets the move to floor.
	 *
	 * @return the move to floor
	 */
	int getMoveToFloor() {
		return moveToFloor;
	}

	/**
	 * Sets the move to floor.
	 *
	 * @param moveToFloor the new move to floor
	 */
	void setMoveToFloor(int moveToFloor) {
		this.moveToFloor = moveToFloor;
	}

	/**
	 * Gets the post move to floor dir.
	 *
	 * @return the post move to floor dir
	 */
	int getPostMoveToFloorDir() {
		return postMoveToFloorDir;
	}

	/**
	 * Sets the post move to floor dir.
	 *
	 * @param postMoveToFloorDir the new post move to floor dir
	 */
	void setPostMoveToFloorDir(int postMoveToFloorDir) {
		this.postMoveToFloorDir = postMoveToFloorDir;
	}


	/**
	 * Gets the offload delay.
	 *
	 * @return the offload delay
	 */
	public int getOffloadDelay() {
		return offloadDelay;
	}


	/**
	 * Sets the offload delay.
	 *
	 * @param offloadDelay the new offload delay
	 */
	public void setOffloadDelay(int offloadDelay) {
		this.offloadDelay = offloadDelay;
	}


	/**
	 * Gets the board delay.
	 *
	 * @return the board delay
	 */
	public int getBoardDelay() {
		return boardDelay;
	}


	/**
	 * Sets the board delay.
	 *
	 * @param boardDelay the new board delay
	 */
	public void setBoardDelay(int boardDelay) {
		this.boardDelay = boardDelay;
	}
	
	
	
	
}
