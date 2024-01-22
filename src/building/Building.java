package building;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import myfileio.MyFileIO;
import genericqueue.GenericQueue;

// TODO: Auto-generated Javadoc
//CLASS MADE BY SEAN
/**
 * The Class Building.
 */
// TODO: Auto-generated Javadoc
public class Building {
	
	/**  Constants for direction. */
	private final static int UP = 1;
	
	/** The Constant DOWN. */
	private final static int DOWN = -1;
	
	/** The Constant LOGGER. */
	private final static Logger LOGGER = Logger.getLogger(Building.class.getName());
	
	/**  The fh - used by LOGGER to write the log messages to a file. */
	private FileHandler fh;
	
	/**  The fio for writing necessary files for data analysis. */
	private MyFileIO fio;
	
	/**  File that will receive the information for data analysis. */
	private File passDataFile;

	/**  passSuccess holds all Passengers who arrived at their destination floor. */
	private ArrayList<Passengers> passSuccess;
	
	/**  gaveUp holds all Passengers who gave up and did not use the elevator. */
	private ArrayList<Passengers> gaveUp;
	
	/**  The number of floors - must be initialized in constructor. */
	private final int NUM_FLOORS;
	
	/**  The size of the up/down queues on each floor. */
	private final int FLOOR_QSIZE = 10;	
	
	/** passQ holds the time-ordered queue of Passengers, initialized at the start 
	 *  of the simulation. At the end of the simulation, the queue will be empty.
	 */
	private GenericQueue<Passengers> passQ;
	
	/** The skip. */
	private boolean skip = false;

	/**  The size of the queue to store Passengers at the start of the simulation. */
	private final int PASSENGERS_QSIZE = 1000;	

	/**  The number of elevators - must be initialized in constructor. */
	private final int NUM_ELEVATORS;
	
	/** The floors. */
	public Floor[] floors;
	
	/** The elevators. */
	private Elevator[] elevators;
	
//	private Elevator lift;
	
	/**  The Call Manager - it tracks calls for the elevator, analyzes them to answer questions and prioritize calls. */
	private CallManager callMgr;
	
	
	
	/** The prev state. */
	private int prevState;
	
	/** The curr state. */
	private int currState;
	
	/** The time in state. */
	private int timeInState;
	
	// Add any fields that you think you might need here...

	
	
	/**
	 * Instantiates a new building.
	 *
	 * @param numFloors the num floors
	 * @param numElevators the num elevators
	 * @param logfile the logfile
	 */
	//checked by Nolan C
	public Building(int numFloors, int numElevators, String logfile) {
		NUM_FLOORS = numFloors;
		NUM_ELEVATORS = numElevators;
		passQ = new GenericQueue<Passengers>(PASSENGERS_QSIZE);
		passSuccess = new ArrayList<Passengers>();
		gaveUp = new ArrayList<Passengers>();
		Passengers.resetStaticID();
		initializeBuildingLogger(logfile);
		// passDataFile is where you will write all the results for those passengers who
		// successfully
		// arrived at their destination and those who gave up...
		fio = new MyFileIO();
		passDataFile = fio.getFileHandle(logfile.replaceAll(".log", "PassData.csv"));

		// create the floors, call manager and the elevator arrays
		// note that YOU will need to create and config each specific elevator...
		floors = new Floor[NUM_FLOORS];
		for (int i = 0; i < NUM_FLOORS; i++) {
			floors[i] = new Floor(FLOOR_QSIZE);
		}
		callMgr = new CallManager(floors, NUM_FLOORS);
		elevators = new Elevator[NUM_ELEVATORS];
		// TODO: if you defined new fields, make sure to initialize them here

	}

	// TODO: Place all of your code HERE - state methods and helpers...

	/**
	 * Configure elevator.
	 *
	 * @param capacity the capacity
	 * @param floorTicks the floor ticks
	 * @param doorTicks the door ticks
	 * @param tickPassengers the tick passengers
	 */
	//checked by Nolan C
	public void configureElevator(int capacity, int floorTicks, int doorTicks, int tickPassengers) { // Configures
																										// elevator
		for (int i = 0; i < NUM_ELEVATORS; i++) {
			elevators[i] = new Elevator(NUM_FLOORS, capacity, floorTicks, doorTicks, tickPassengers);
		}
	}

	/**
	 * Update call status.
	 */
	public void updateCallStatus() { //Needed for elevator Sim
		callMgr.updateCallStatus();
	}

	/**
	 * End sim.
	 *
	 * @param i the i
	 * @return true, if successful
	 */
	//checked by Nolan C
	public boolean endSim(int i) { // Ends simulation when requirments are met, called by elevator sim
		if (getCurrState(i) == 0 && passQ.isEmpty()) {
			return true;
		}
		return false;
	}

	/**
	 * Gets the curr state.
	 *
	 * @param i the i
	 * @return the curr state
	 */
	public int getCurrState(int i) { // needed for gui and elevator sim 
		return elevators[i].getCurrState();
	}

	/**
	 * Gets the num passengers.
	 *
	 * @param i the i
	 * @return the num passengers
	 */
	public int getNumPassengers(int i) {
		return elevators[i].getPassengers();
	}

	/**
	 * Gets the curr floor.
	 *
	 * @param i the i
	 * @return the curr floor
	 */
	public int getCurrFloor(int i) {
		return elevators[i].getCurrFloor();
	}

	/**
	 * Gets the pass per floor up.
	 *
	 * @return the pass per floor up
	 */
	//checked by Nolan C
	public int[] getPassPerFloorUp() {
		int[] newFloors = new int[NUM_FLOORS];
		for (int i = 0; i < floors.length; i++) {
			newFloors[i] = floors[i].getNumPassInUpQueue();
		}
		return newFloors;

	}

	/**
	 * Gets the pass per floor down.
	 *
	 * @return the pass per floor down
	 */
	//checked by Nolan C
	public int[] getPassPerFloorDown() {
		int[] newFloors = new int[NUM_FLOORS];
		for (int i = 0; i < floors.length; i++) {
			newFloors[i] = floors[i].getNumPassInDownQueue();
		}
		return newFloors;
	}

	/**
	 * Gets the pass per floor.
	 *
	 * @return the pass per floor
	 */
	//checked by Nolan C
	public int[] getPassPerFloor() {
		int[] ans = new int[NUM_FLOORS];
		for (int i = 0; i < floors.length; i++) {
			ans[i] = floors[i].getNumPassInUpQueue() + floors[i].getNumPassInDownQueue();
		}
		return ans;
		// TODO Auto-generated method stub
	}

	/**
	 * Gets the pass queue size.
	 *
	 * @return the pass queue size
	 */
	public int getPassQueueSize() { //Size for Nolan
		return passQ.size();
	}

	/**
	 * Check passenger queue.
	 *
	 * @param time the time
	 */

	//checked by Nolan C
	public void checkPassengerQueue(int time) {
		if (!passQ.isEmpty()) {
			if (time == passQ.peek().getTime()) {
				while (!passQ.isEmpty() && passQ.peek().getTime() == time) {
					floors[passQ.peek().getOnFloor()].addToQueue(passQ.peek());
					logCalls(time, passQ.peek().getNumPass(), passQ.peek().getOnFloor(), passQ.peek().getDirection(),
							passQ.peek().getId());
					passQ.remove();
				}
			}
		}

	}

	/**
	 * Adds the passengers to queue.
	 *
	 * @param time the time
	 * @param numPass the num pass
	 * @param fromFloor the from floor
	 * @param toFloor the to floor
	 * @param polite the polite
	 * @param wait the wait
	 * @param elevatorNumber the elevator number
	 * @return true, if successful
	 */
	//checked by Nolan C
	public boolean addPassengersToQueue(int time, int numPass, int fromFloor, int toFloor, boolean polite, int wait,
			int elevatorNumber) { //Adds the passengers to queue and is called in the beginning

		return passQ.offer(new Passengers(time, numPass, fromFloor, toFloor, polite, wait));
	}

	/**
	 * Elevator state changed.
	 *
	 * @param elevator the elevator
	 * @return true, if successful
	 */
	//checked by Nolan C
	public boolean elevatorStateChanged(Elevator elevator) {
		if (elevator.getCurrState() != elevator.getPrevState()) {
			return true;
		}
		return false;
	}

	/**
	 * Curr state stop.
	 *
	 * @param time the time
	 * @param lift the lift
	 * @return the int
	 */
	//checked by Nolan C
	public int currStateStop(int time, Elevator lift) {
		if (!callMgr.callPending()) {
			return lift.STOP;
		} else {
			Passengers priority = callMgr.prioritizePassengerCalls(lift.getCurrFloor());
			if (priority.getOnFloor() == lift.getCurrFloor()) {
				lift.setDirection(priority.getDirection()); // Change elevator direction
				return lift.OPENDR;
			} else {
				if (priority.getOnFloor() > lift.getCurrFloor()) { // Checker
					lift.setDirection(UP);
				} else {
					lift.setDirection(DOWN);
				}
				lift.setMoveToFloor(priority.getOnFloor());
				lift.setPostMoveToFloorDir(priority.getDirection());
				return lift.MVTOFLR;
			}
		}

	}

	/**
	 * Curr state mv to flr.
	 *
	 * @param time the time
	 * @param lift the lift
	 * @return the int
	 */
	//checked by Nolan C
	public int currStateMvToFlr(int time, Elevator lift) {
		lift.moveElevator();
		if (lift.getCurrFloor() != lift.getMoveToFloor()) {
			return lift.MVTOFLR;
		}
		lift.setDirection(lift.getPostMoveToFloorDir());
		// might not be necessary
		return lift.OPENDR;
	}

	/**
	 * Curr state open dr.
	 *
	 * @param time the time
	 * @param lift the lift
	 * @return the int
	 */
	//checked by Nolan C
	public int currStateOpenDr(int time, Elevator lift) {
		lift.openDoor();

		if (lift.getDoorState() != lift.getTicksDoorOpenClose()) { // Whatever static int is for open
			return lift.OPENDR;
		} else if (lift.getDoorState() == lift.getTicksDoorOpenClose() && lift.offloadHere()) {
			lift.setPrevFloor(lift.getCurrFloor());
			return lift.OFFLD;
		} else {
			lift.setPrevFloor(lift.getCurrFloor());
			return lift.BOARD;
		}
	}
	/**
	 * Curr state off ld.
	 *
	 * @param time the time
	 * @param lift the lift
	 * @return the int
	 */
	//checked by Nolan C
	public int currStateOffLd(int time, Elevator lift) {
		// check direction
		int numOffloading = lift.totalOffloading();
		lift.offloadPassengers(); // offloadPassengers is a method needed in elevator (should return a boolean,
									// but also do stuff internally in elevator)
		for (int i = 0; i < numOffloading; i++) { // totalOffloading gets the number of people that should be offloading
													// off the elevator
			Passengers p = lift.getPassByFloor()[lift.getCurrFloor()].remove(0);
			p.setTimeArrived(time);
			passSuccess.add(p);
			logArrival(time, p.getNumPass(), lift.getCurrFloor(), p.getId());
		}
		if (!lift.stillOffloading()) { // Takes in account the time need and extra ticks, could be a true or false
										// variable that I just get, or a method. It should be changed in the
										// offloadPassengers() method though
			if (callMgr.callOnThisFloorDirection(lift.getCurrFloor(), lift.getDirection())) { // the 2nd TODO method in Call Manager															
				return Elevator.BOARD;
			}
			if (!callMgr.callInDirection(lift.getCurrFloor(), lift.getDirection())
					&& callMgr.callOnThisFloor(lift.getCurrFloor()) && lift.isEmpty()) {
				lift.changeDirection();
			}
			if (lift.getPassengers() == 0
					&& callMgr.callOnThisFloorDirection(lift.getCurrFloor(), lift.getDirection())) {
				return Elevator.BOARD;
			}
			return Elevator.CLOSEDR;
		}
		return Elevator.OFFLD;
	}

	/**
	 * Curr state board.
	 *
	 * @param time the time
	 * @param lift the lift
	 * @return the int
	 */
	//checked by Nolan C
	public int currStateBoard(int time, Elevator lift) {
		while (!floors[lift.getCurrFloor()].whichQueueIsEmpty(lift.getDirection()) && !skip) {
			Passengers p = floors[lift.getCurrFloor()].peekCertainQueue(lift.getDirection());
			if (p.getTimeWillGiveUp() >= time) {
				if (lift.capacityReached(floors[lift.getCurrFloor()].peekCertainQueue(lift.getDirection()))) { //Capacity reached
					p.setPolite(true);
					if (!skip) //Skip a person
						logSkip(time, p.getNumPass(), p.getOnFloor(), p.getDirection(), p.getId());
					skip = true;
				} else {
					p.setBoardTime(time);
					skip = false;
					lift.boardPassengers(p);
					logBoard(time, p.getNumPass(), lift.getCurrFloor(), lift.getDirection(), p.getId()); // Board the person
					floors[lift.getCurrFloor()].removeCertainQueue(lift.getDirection());
				}
			} else {
				gaveUp.add(p);
				logGiveUp(time, p.getNumPass(), p.getOnFloor(), p.getDirection(), p.getId());
				floors[lift.getCurrFloor()].removeCertainQueue(lift.getDirection());
			}
//			if (lift.elevatorFull())
//				skip = true;
		}
		if (lift.stillBoardingPassengers()) { // same idea as stillOffloading, but also checks if elevator has enough									// capacity and takes in account time.
			return lift.BOARD;
		} else {
			skip = false;
			return lift.CLOSEDR;
		}
	}

	/**
	 * Curr state close dr.
	 *
	 * @param time the time
	 * @param lift the lift
	 * @return the int
	 */
	//checked by Nolan C
	public int currStateCloseDr(int time, Elevator lift) {
		lift.closeDoor();
		if (callMgr.passengersOnFloor(lift.getCurrFloor(), lift.getDirection())
				&& (!floors[lift.getCurrFloor()].peekCertainQueue(lift.getDirection()).isPolite())) {
			return lift.OPENDR;
		} else if (!lift.doorStillClosing()) { // another method or int/getter to tell
			if (lift.isEmpty()) {
				if (!callMgr.callPending()) {
					return lift.STOP;
				} else if (callMgr.callInDirection(lift.getCurrFloor(), lift.getDirection())) {
					return lift.MV1FLR;
				} else if (callMgr.callOnThisFloorDirection(lift.getCurrFloor(), lift.getDirection())) {
					return lift.OPENDR;
				} else if (callMgr.callPending()) {
					lift.changeDirection();
					if (callMgr.callOnThisFloor(lift.getCurrFloor())) {
						return lift.OPENDR;
					} else {
						return lift.MV1FLR;
					}
				}
			} else {
				return lift.MV1FLR;
			}
		}
		return lift.CLOSEDR;
	}

	/**
	 * Curr state mv 1 flr.
	 *
	 * @param time the time
	 * @param lift the lift
	 * @return the int
	 */
	//checked by Nolan C
	public int currStateMv1Flr(int time, Elevator lift) {

		lift.moveElevator();
		if (!lift.stillMoving()) {
			if (!floors[lift.getCurrFloor()].whichQueueIsEmpty(lift.getDirection()) || lift.offloadHere()) {
				return lift.OPENDR;
			} else if (!callMgr.callPending() && lift.isEmpty()) {
				return lift.STOP;
			} else if (lift.isEmpty() && !callMgr.callInDirection(lift.getCurrFloor(), lift.getDirection())
					&& callMgr.callOnThisFloorDirection(lift.getCurrFloor(), -(lift.getDirection()))) { // Might have to
																										// add stuff in
																										// the end
				lift.changeDirection();
				return lift.OPENDR;
			} else if (lift.getCurrFloor() == 0 || lift.getCurrFloor() == floors.length - 1
					&& floors[lift.getCurrFloor()].whichQueueIsEmpty(lift.getDirection())) {
				return lift.OPENDR;
			}
		}
		return lift.MV1FLR;
	}

	/**
	 * Access floor.
	 *
	 * @param lift the lift
	 * @return the floor
	 */
	public Floor accessFloor(Elevator lift) {
		return floors[lift.getCurrFloor()];
	}

	/**
	 * Elevator is empty.
	 *
	 * @param lift the lift
	 * @return true, if successful
	 */
	public boolean elevatorIsEmpty(Elevator lift) {
		if (lift.isEmpty()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Queues empty.
	 *
	 * @return true, if successful
	 */
	//checked by Nolan C
	public boolean queuesEmpty() {
		int total = 0;
		for (int i = 0; i < NUM_FLOORS; i++) {
			if (floors[i].bothQueuesEmpty()) {
				total++;
			}
		}
		if (total == NUM_FLOORS) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Floors empty.
	 *
	 * @return true, if successful
	 */
	//checked by Nolan C
	public boolean floorsEmpty() {
		if (callMgr.totalNumUpCallsPending() == 0 && callMgr.totalNumDownCallsPending() == 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Sets the logging.
	 *
	 * @param turnOn the new logging
	 */
	public void setLogging(boolean turnOn) {
		if (turnOn) {
			LOGGER.setLevel(Level.INFO);
		} else {
			LOGGER.setLevel(Level.OFF);
		}
	}
	
	
	
	// DO NOT CHANGE ANYTHING BELOW THIS LINE:
	/**
	 * Initialize building logger. Sets formating, file to log to, and
	 * turns the logger OFF by default
	 *
	 * @param logfile the file to log information to
	 */
	void initializeBuildingLogger(String logfile) {
		System.setProperty("java.util.logging.SimpleFormatter.format","%4$-7s %5$s%n");
		LOGGER.setLevel(Level.OFF);
		try {
			fh = new FileHandler(logfile);
			LOGGER.addHandler(fh);
			SimpleFormatter formatter = new SimpleFormatter();
			fh.setFormatter(formatter);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	
	
	/**
	 * Update elevator - this is called AFTER time has been incremented.
	 * -  Logs any state changes, if the have occurred,
	 * -  Calls appropriate method based upon currState to perform
	 *    any actions and calculate next state...
	 *
	 * @param time the time
	 */
	// YOU WILL NEED TO CODE ANY MISSING METHODS IN THE APPROPRIATE CLASSES...
	//checked by Nolan C
	public void updateElevator(int time) {
		for (Elevator lift: elevators) {
			if (elevatorStateChanged(lift))
				logElevatorStateChanged(time,lift.getPrevState(),lift.getCurrState(),lift.getPrevFloor(),lift.getCurrFloor());

			switch (lift.getCurrState()) {
				case Elevator.STOP: lift.updateCurrState(currStateStop(time, lift)); break;
				case Elevator.MVTOFLR: lift.updateCurrState(currStateMvToFlr(time, lift)); break;
				case Elevator.OPENDR: lift.updateCurrState(currStateOpenDr(time, lift)); break;
				case Elevator.OFFLD: lift.updateCurrState(currStateOffLd(time, lift)); break;
				case Elevator.BOARD: lift.updateCurrState(currStateBoard(time, lift)); break;
				case Elevator.CLOSEDR: lift.updateCurrState(currStateCloseDr(time, lift)); break;
				case Elevator.MV1FLR: lift.updateCurrState(currStateMv1Flr(time, lift)); break;
			}
		}
	}

	/**
	 * Process passenger data. Do NOT change this - it simply dumps the 
	 * collected passenger data for successful arrivals and give ups. These are
	 * assumed to be ArrayLists...
	 */
	public void processPassengerData() {
		
		try {
			BufferedWriter out = fio.openBufferedWriter(passDataFile);
			out.write("ID,Number,From,To,WaitToBoard,TotalTime\n");
			for (Passengers p : passSuccess) {
				String str = p.getId()+","+p.getNumPass()+","+(p.getOnFloor()+1)+","+(p.getDestFloor()+1)+","+
				             (p.getBoardTime() - p.getTime())+","+(p.getTimeArrived() - p.getTime())+"\n";
				out.write(str);
			}
			for (Passengers p : gaveUp) {
				String str = p.getId()+","+p.getNumPass()+","+(p.getOnFloor()+1)+","+(p.getDestFloor()+1)+","+
				             p.getWaitTime()+",-1\n";
				out.write(str);
			}
			fio.closeFile(out);
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Enable logging. Prints the initial configuration message.
	 * For testing, logging must be enabled BEFORE the run starts.
	 */
	public void enableLogging() {
		LOGGER.setLevel(Level.INFO);
		for (Elevator el:elevators)
			logElevatorConfig(el.getCapacity(), el.getTicksPerFloor(), el.getTicksDoorOpenClose(), el.getPassPerTick(), el.getCurrState(), el.getCurrFloor());
	}
	
	/**
	 * Close logs, and pause the timeline in the GUI.
	 *
	 * @param time the time
	 */
	public void closeLogs(int time) {
		if (LOGGER.getLevel() == Level.INFO) {
			logEndSimulation(time);
			fh.flush();
			fh.close();
		}
	}
	
	/**
	 * Prints the state.
	 *
	 * @param state the state
	 * @return the string
	 */
	private String printState(int state) {
		String str = "";
		
		switch (state) {
			case Elevator.STOP: 		str =  "STOP   "; break;
			case Elevator.MVTOFLR: 		str =  "MVTOFLR"; break;
			case Elevator.OPENDR:   	str =  "OPENDR "; break;
			case Elevator.CLOSEDR:		str =  "CLOSEDR"; break;
			case Elevator.BOARD:		str =  "BOARD  "; break;
			case Elevator.OFFLD:		str =  "OFFLD  "; break;
			case Elevator.MV1FLR:		str =  "MV1FLR "; break;
			default:					str =  "UNDEF  "; break;
		}
		return(str);
	}
	
	/**
	 * Dump passQ contents. Debug hook to view the contents of the passenger queue...
	 */
	public void dumpPassQ() {
		ListIterator<Passengers> passengers = passQ.getListIterator();
		if (passengers != null) {
			System.out.println("Passengers Queue:");
			while (passengers.hasNext()) {
				Passengers p = passengers.next();
				System.out.println(p);
			}
		}
	}

	/**
	 * Log elevator config.
	 *
	 * @param capacity the capacity
	 * @param ticksPerFloor the ticks per floor
	 * @param ticksDoorOpenClose the ticks door open close
	 * @param passPerTick the pass per tick
	 * @param state the state
	 * @param floor the floor
	 */
	private void logElevatorConfig(int capacity, int ticksPerFloor, int ticksDoorOpenClose, int passPerTick, int state, int floor) {
		LOGGER.info("CONFIG:   Capacity="+capacity+"   Ticks-Floor="+ticksPerFloor+"   Ticks-Door="+ticksDoorOpenClose+
				    "   Ticks-Passengers="+passPerTick+"   CurrState=" + (printState(state))+"   CurrFloor="+(floor+1));
	}
		
	/**
	 * Log elevator state changed.
	 *
	 * @param time the time
	 * @param prevState the prev state
	 * @param currState the curr state
	 * @param prevFloor the prev floor
	 * @param currFloor the curr floor
	 */
	private void logElevatorStateChanged(int time, int prevState, int currState, int prevFloor, int currFloor) {
		LOGGER.info("Time="+time+"   Prev State: " + printState(prevState) + "   Curr State: "+printState(currState)
		+"   PrevFloor: "+(prevFloor+1) + "   CurrFloor: " + (currFloor+1));
	}
	
	/**
	 * Log arrival.
	 *
	 * @param time the time
	 * @param numPass the num pass
	 * @param floor the floor
	 * @param id the id
	 */
	private void logArrival(int time, int numPass, int floor,int id) {
		LOGGER.info("Time="+time+"   Arrived="+numPass+" Floor="+ (floor+1)
		+" passID=" + id);						
	}
	
	/**
	 * Log calls.
	 *
	 * @param time the time
	 * @param numPass the num pass
	 * @param floor the floor
	 * @param dir the dir
	 * @param id the id
	 */
	private void logCalls(int time, int numPass, int floor, int dir, int id) {
		LOGGER.info("Time="+time+"   Called="+numPass+" Floor="+ (floor +1)
				+" Dir="+((dir>0)?"Up":"Down")+"   passID=" + id);
	}
	
	/**
	 * Log give up.
	 *
	 * @param time the time
	 * @param numPass the num pass
	 * @param floor the floor
	 * @param dir the dir
	 * @param id the id
	 */
	private void logGiveUp(int time, int numPass, int floor, int dir, int id) {
		LOGGER.info("Time="+time+"   GaveUp="+numPass+" Floor="+ (floor+1) 
				+" Dir="+((dir>0)?"Up":"Down")+"   passID=" + id);				
	}

	/**
	 * Log skip.
	 *
	 * @param time the time
	 * @param numPass the num pass
	 * @param floor the floor
	 * @param dir the dir
	 * @param id the id
	 */
	private void logSkip(int time, int numPass, int floor, int dir, int id) {
		LOGGER.info("Time="+time+"   Skip="+numPass+" Floor="+ (floor+1) 
				+" Dir="+((dir>0)?"Up":"Down")+"   passID=" + id);				
	}
	
	/**
	 * Log board.
	 *
	 * @param time the time
	 * @param numPass the num pass
	 * @param floor the floor
	 * @param dir the dir
	 * @param id the id
	 */
	private void logBoard(int time, int numPass, int floor, int dir, int id) {
		LOGGER.info("Time="+time+"   Board="+numPass+" Floor="+ (floor+1) 
				+" Dir="+((dir>0)?"Up":"Down")+"   passID=" + id);				
	}
	
	/**
	 * Log end simulation.
	 *
	 * @param time the time
	 */
	private void logEndSimulation(int time) {
		LOGGER.info("Time="+time+"   Detected End of Simulation");
	}
}
