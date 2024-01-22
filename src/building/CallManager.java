package building;

// Written by Yuki Yanase

/**
 * The Class CallManager. This class models all of the calls on each floor,
 * and then provides methods that allow the building to determine what needs
 * to happen (ie, state transitions).
 */
public class CallManager {
	
	/** The floors. */
	private Floor[] floors;
	
	/** The num floors. */
	private final int NUM_FLOORS;
	
	/** The Constant UP. */
	private final static int UP = 1;
	
	/** The Constant DOWN. */
	private final static int DOWN = -1;
	
	/** The up calls array indicates whether or not there is a up call on each floor. */
	private boolean[] upCalls;
	
	/** The down calls array indicates whether or not there is a down call on each floor. */
	private boolean[] downCalls;
	
	/**  The up call pending - true if any up calls exist. */
	private boolean upCallPending;
	
	/**  The down call pending - true if any down calls exit. */
	private boolean downCallPending;
	
	//TODO: Add any additional fields here..
	
	
	//METHODS CHECKED BY SEAN AMINOV
	
	/**
	 * Instantiates a new call manager.
	 *
	 * @param floors the floors
	 * @param numFloors the num floors
	 */
	public CallManager(Floor[] floors, int numFloors) {
		this.floors = floors;
		NUM_FLOORS = numFloors;
		upCalls = new boolean[NUM_FLOORS];
		downCalls = new boolean[NUM_FLOORS];
		upCallPending = false;
		downCallPending = false;
	}
	
	/**
	 * Update call status. This is an optional method that could be used to compute
	 * the values of all up and down call fields statically once per tick (to be
	 * more efficient, could only update when there has been a change to the floor queues -
	 * either passengers being added or being removed. The alternative is to dynamically
	 * recalculate the values of specific fields when needed.
	 */
	void updateCallStatus() {
		upCallPending = false;
		downCallPending = false;
		for (int i = 0; i < NUM_FLOORS; i++) {
			if (!floors[i].bothQueuesEmpty()) {				
				if (!floors[i].isEmptyUpQueue()) {
					upCallPending = true;
					upCalls[i] = true;
				} else {
					upCalls[i] = false;
				}
				if (!floors[i].isEmptyDownQueue()) {
					downCallPending = true;
					downCalls[i] = true;
				} else {
					downCalls[i] = false;
				}
			} else {
				upCalls[i] = false;
				downCalls[i] = false;
			}
		}

	}

	/**
	 * Prioritize passenger calls from STOP STATE.
	 *
	 * @param currFloor the curr floor
	 * @return the passengers
	 */
	Passengers prioritizePassengerCalls(int currFloor) {
		if (upCalls[currFloor] || downCalls[currFloor]) {
			if (upCalls[currFloor] && downCalls[currFloor]) {
				if (upCallsAboveFloor(currFloor) >= downCallsBelowFloor(currFloor)) {
					return floors[currFloor].peekUpQueue();
				} else {
					return floors[currFloor].peekDownQueue();
				}
			}
			else if (upCalls[currFloor]) {
				return floors[currFloor].peekUpQueue();
			}
			else if (downCalls[currFloor]) {
				return floors[currFloor].peekDownQueue();			
			}
		}
		if (totalNumUpCallsPending() > totalNumDownCallsPending()) {
			return floors[lowestUpCall()].peekUpQueue();
		}
		else if (totalNumUpCallsPending() < totalNumDownCallsPending()) {
			return floors[highestDownCall()].peekDownQueue();
		} else {
			if (Math.abs(currFloor - lowestUpCall()) > Math.abs(currFloor - highestDownCall())) {
				return floors[highestDownCall()].peekDownQueue();
			} else {
				return floors[lowestUpCall()].peekUpQueue();
			}
		}
	}
	

	//TODO: Write any additional methods here. Things that you might consider:
	//      1. pending calls - are there any? only up? only down?
	//      2. is there a call on the current floor in the current direction
	//      3. How many up calls are pending? how many down calls are pending? 
	//      4. How many calls are pending in the direction that the elevator is going
	//      5. Should the elevator change direction?
	//
	//      These are an example - you may find you don't need some of these, or you may need more...
	
	
	/**
	 * Determines if there are passengers waiting on the current floor 
	 * who want to go in the same direction as the elevator.
	 *
	 * @param currFloor the current floor
	 * @param direction the direction of the elevator
	 * @return true, if conditions are met; else false
	 */
	boolean passengersOnFloor(int currFloor, int direction) {
		if (direction == UP) {
			if (upCalls[currFloor]) {
				return true;
			}
		}
		if (direction == DOWN) {
			if (downCalls[currFloor]) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns the highest floor with a down call.
	 *
	 * @return the corresponding floor; 0 if no down calls
	 */
	int highestDownCall() {
		for (int i = NUM_FLOORS - 1; i > 0; i--) {
			if (downCalls[i]) {
				return i;
			}
		}
		return 0;
	}
	
	/**
	 * Returns the lowest floor with an up call.
	 *
	 * @return the corresponding floor; 0 if no up calls
	 */
	int lowestUpCall() {
		for (int i = 0; i < NUM_FLOORS; i++) {
			if (upCalls[i]) {
				return i;
			}
		}
		return 0;
	}
	
	/**
	 * Calculates the number of up calls above the floor.
	 *
	 * @param floor the floor to be used
	 * @return the number of calls
	 */
	int upCallsAboveFloor(int floor) {
		int count = 0;
		for (int i = floor + 1; i < NUM_FLOORS; i++) {
			if (upCalls[i]) {
				count++;
			}
		}
		return count;
	}
	
	/**
	 * Calculates the number of down calls below the floor.
	 *
	 * @param floor the floor to be used
	 * @return the number of calls
	 */
	int downCallsBelowFloor(int floor) {
		int count = 0;
		for (int i = floor - 1; i > 0; i--) {
			if (downCalls[i]) {
				count++;
			}
		}
		return count;
	}
	
	/**
	 * Determines if there are any pending calls in either direction.
	 *
	 * @return true, if there is a pending call; else false
	 */
	boolean callPending() {
		return (upCallPending || downCallPending);
	}
	
	/**
	 * Determines if there is a pending up call on any floor
	 *
	 * @return true, if there is an up call; else false
	 */
	boolean upCallPending() {
		for (int i = 0; i < upCalls.length; i++) {
			if (upCalls[i]) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Determines if there is a pending down call on any floor
	 *
	 * @return true, if there is a down call; else false
	 */
	boolean downCallPending() {
		for (int i = 0; i < downCalls.length; i++) {
			if (downCalls[i]) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns the total number of pending calls.
	 *
	 * @return the number of pending calls
	 */
	int numCallsPending() {
		return totalNumUpCallsPending() + totalNumDownCallsPending();
	}
	
	/**
	 * The total number of pending up calls.
	 *
	 * @return the number of up calls
	 */
	int totalNumUpCallsPending() {
		int numCalls = 0;
		for (int i = 0; i < NUM_FLOORS; i++) {
			if (upCalls[i]) {
				numCalls++;
			}
		}
		return numCalls;
	}
	
	/**
	 * The total number of pending down calls.
	 *
	 * @return the number of down calls
	 */
	int totalNumDownCallsPending() {
		int numCalls = 0;
		for (int i = 0; i < NUM_FLOORS; i++) {
			if (downCalls[i]) {
				numCalls++;
			}
		}
		return numCalls;
	}

	/**
	 * Checks if there is a call on a specific floor
	 *
	 * @param floor the floor in question
	 * @return true, if there is a call; else false
	 */
	boolean callOnThisFloor(int floor) {
		return (upCalls[floor] || downCalls[floor]);
	}
	
	/**
	 * Checks if there is a call on a specific floor in a specific direction.
	 *
	 * @param floor the floor in question
	 * @param direction the direction in question
	 * @return true, if there is a matching call; else false
	 */
	boolean callOnThisFloorDirection(int floor, int direction) {
		if (direction == UP) {
			return upCalls[floor];
		} else {
			return downCalls[floor];
		}
	}
	
	/**
	 * Checks if there is any call on a floor that is toward a specified direction from a starting floor.
	 *
	 * @param floor the starting floor
	 * @param direction the direction in question
	 * @return true, if there is call; else false
	 */
	boolean callInDirection(int floor, int direction) {
		if (direction == UP) {
			for (int i = floor + 1; i < NUM_FLOORS; i++) {
				if (upCalls[i] || downCalls[i]) {
					return true;
				}
			}
		}
		if (direction == DOWN) {
			for (int i = floor - 1; i >= 0; i--) {
				if (upCalls[i] || downCalls[i]) {
					return true;
				}
			}
		}
		return false;
	}

}
