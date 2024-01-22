 import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import building.Building;
import myfileio.MyFileIO;
//Class by Nolan C


// TODO: Auto-generated Javadoc
/**
 * The Class ElevatorSimController.
 */
// TODO: Auto-generated Javadoc
public class ElevatorSimController {
	
	/**  Constant to specify the configuration file for the simulation. */
	private static final String SIM_CONFIG = "ElevatorSimConfig.csv";
	
	/**  Constant to make the Passenger queue contents visible after initialization. */
	private boolean PASSQ_DEBUG=false;
	
	/** The gui. */
	private ElevatorSimulation gui;
	
	/** The building. */
	private Building building;
	
	/** The fio. */
	private MyFileIO fio;

	/** The num floors. */
	private final int NUM_FLOORS;
	
	/** The num elevators. */
	private final int NUM_ELEVATORS;
	
	/** The num floors. */
	private int numFloors;
	
	/** The num elevators. */
	private int numElevators;
	
	/** The capacity. */
	private int capacity;
	
	/** The floor ticks. */
	private int floorTicks;
	
	/** The door ticks. */
	private int doorTicks;
	
	/** The pass per tick. */
	private int passPerTick;
	
	/** The testfile. */
	private String testfile;
	
	/** The logfile. */
	private String logfile;
	
	/** The step cnt. */
	private int stepCnt = 0;
	
	/** The end sim. */
	private boolean endSim = false;
	
	private boolean isFinished = false;
	
	
	// METHODS CHECKED BY YUKI YANASE
	
	// Checked by YY
	public int getCurrentFloor() {
		return building.getCurrFloor(0);
	}

	public String getTestName() {
		return testfile;
	}
	
	public int getNumFloors() {
		return numFloors;
	}


	public int getNumElevators() {
		return numElevators;
	}

	

	
	/**
	 * Instantiates a new elevator sim controller. 
	 * Reads the configuration file to configure the building and
	 * the elevator characteristics and also select the test
	 * to run. Reads the passenger data for the test to run to
	 * initialize the passenger queue in building...
	 *
	 * @param gui the gui
	 */
	// Checked by YY
	public ElevatorSimController(ElevatorSimulation gui) {
		this.gui = gui;
		fio = new MyFileIO();
		// IMPORTANT: DO NOT CHANGE THE NEXT LINE!!! Update the config file itself
		// (ElevatorSimConfig.csv) to change the configuration or test being run.
		configSimulation(SIM_CONFIG);
		NUM_FLOORS = numFloors;
		NUM_ELEVATORS = numElevators;
		logfile = testfile.replaceAll(".csv", ".log");
		building = new Building(NUM_FLOORS,NUM_ELEVATORS,logfile);
		
		//TODO: YOU still need to configure the elevators in the building here....
		
		initializePassengerData(testfile);
		building.configureElevator(capacity, floorTicks, doorTicks, passPerTick);
		enableLogging();

		
	}
	
	//TODO: Write methods to update the GUI display
	//      Needs to cover the Elevator state, Elevator passengers
	//      and queues for each floor, as well as the current time
	
	/**
	 * Config simulation. Reads the filename, and parses the
	 * parameters.
	 *
	 * @param filename the filename
	 */
	// Checked by YY
	private void configSimulation(String filename) {
		File configFile = fio.getFileHandle(filename);
		try ( BufferedReader br = fio.openBufferedReader(configFile)) {
			String line;
			while ((line = br.readLine())!= null) {
				parseElevatorConfigData(line);
			}
			fio.closeFile(br);
		} catch (IOException e) { 
			System.err.println("Error in reading file: "+filename);
			e.printStackTrace();
		}
	}
	
	/**
	 * Parses the elevator simulation config file to configure the simulation:
	 * number of floors and elevators, the actual test file to run, and the
	 * elevator characteristics.
	 *
	 * @param line the line
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	// Checked by YY
	private void parseElevatorConfigData(String line) throws IOException {
		String[] values = line.split(",");
		if (values[0].equals("numFloors")) {
			numFloors = Integer.parseInt(values[1]);
		} else if (values[0].equals("numElevators")) {
			numElevators = Integer.parseInt(values[1]);
		} else if (values[0].equals("passCSV")) {
			testfile = values[1];
		} else if (values[0].equals("capacity")) {
			capacity = Integer.parseInt(values[1]);
		} else if (values[0].equals("floorTicks")) {
			floorTicks = Integer.parseInt(values[1]);
		} else if (values[0].equals("doorTicks")) {
			doorTicks = Integer.parseInt(values[1]);
		} else if (values[0].equals("passPerTick")) {
			passPerTick = Integer.parseInt(values[1]);
		}
	}
	
	/**
	 * Initialize passenger data. Reads the supplied filename,
	 * and for each passenger group, identifies the pertinent information
	 * and adds it to the passengers queue in Building...
	 *
	 * @param filename the filename
	 */
	// Checked by YY
	private void initializePassengerData(String filename) {
		boolean firstLine = true;
		File passInput = fio.getFileHandle(filename);
		try (BufferedReader br = fio.openBufferedReader(passInput)) {
			String line;
			while ((line = br.readLine())!= null) {
				if (firstLine) {
					firstLine = false;
					continue;
				}
				parsePassengerData(line);
			}
			fio.closeFile(br);
		} catch (IOException e) { 
			System.err.println("Error in reading file: "+filename);
			e.printStackTrace();
		}
		if (PASSQ_DEBUG) building.dumpPassQ();
	}	
	
	/**
	 * Parses the line of passenger data into tokens, and 
	 * passes those values to the building to be added to the
	 * passenger queue
	 *
	 * @param line the line of passenger input data
	 */
	// Checked by YY
	private void parsePassengerData(String line) {
		int time=0, numPass=0, fromFloor=0, toFloor=0;
		boolean polite = true;
		int wait = 1000;
		String[] values = line.split(",");
		for (int i = 0; i < values.length; i++) {
			switch (i) {
				case 0 : time      = Integer.parseInt(values[i]); break;
				case 1 : numPass   = Integer.parseInt(values[i]); break;
				case 2 : fromFloor   = Integer.parseInt(values[i]); break;
				case 3 : toFloor  = Integer.parseInt(values[i]); break;
				case 5 : wait      = Integer.parseInt(values[i]); break;
				case 4 : polite = "TRUE".equalsIgnoreCase(values[i]); break;
			}
		}
		building.addPassengersToQueue(time,numPass,fromFloor,toFloor,polite,wait, 0);	
	}
	
	/**
	 * Enable logging. A pass-through from the GUI to building
	 */
	// Checked by YY
	public void enableLogging() {
		building.enableLogging();
		building.setLogging(true);
	}
	
	// TODO: Write any other helper methods that you may need to access data from the building...
	
	
 	/**
	 * Step sim. See the comments below for the functionality you
	 * must implement......
	 */
	// Checked by YY
	public void stepSim() {
		if(endSim) {
			return;
		}
 		// DO NOT MOVE THIS - YOU MUST INCREMENT TIME FIRST!
		stepCnt++;
		
		// TODO: Write the rest of this method
		// If simulation is not completed (not all passengers have been processed
		// or elevator(s) are not all in STOP state), then
		// 		1) check for arrival of any new passengers
		// 		2) update the elevator
		// 		3) update the GUI 
		//  else 
		//    	1) update the GUI
		//		2) close the logs
		//		3) process the passenger results
		//		4) send endSimulation to the GUI to stop ticks.
		if(building.getCurrState(0) != 0 || passProcessed() == false) {
			building.checkPassengerQueue(stepCnt);
			building.updateCallStatus();
			building.updateElevator(stepCnt);
			if (gui != null) {
				gui.updateGUI(stepCnt, building.getCurrState(0),building.getNumPassengers(0),building.getPassPerFloorUp(), building.getPassPerFloorDown(), building.getCurrFloor(0));
			}
		} else {
			if (gui != null) {
				gui.updateGUI(stepCnt, building.getCurrState(0),building.getPassQueueSize(),building.getPassPerFloorUp(), building.getPassPerFloorDown(),building.getCurrFloor(0));
			}
			building.updateElevator(stepCnt);
			if (gui != null) {
				gui.updateGUI(stepCnt, building.getCurrState(0),building.getNumPassengers(0),building.getPassPerFloorUp(), building.getPassPerFloorDown(),building.getCurrFloor(0));
			}
			building.closeLogs(stepCnt);
			building.processPassengerData();
			endSim = true;
			if (gui != null) {
				gui.endSim();
			}
		}
	}
	
	/**
	 * Pass processed.
	 *
	 * @return true, if successful
	 */
	// Checked by YY
	private boolean passProcessed() {
		if (building.getPassQueueSize() != 0) {
			return false;
		}
		if (building.getNumPassengers(0) != 0) {
			return false;
		}
		for (int i = 0; i < 6; i++) {
			if (building.getPassPerFloor()[i] != 0) {
				return false;
			}
		}
		return true;
	}
	
	// Checked by YY
	public boolean endSim() {
		return endSim;
	}
	
	/**
	 * Gets the building. ONLY USED FOR JUNIT TESTING - YOUR GUI SHOULD NOT ACCESS THIS!.
	 *
	 * @return the building
	 */
	// Checked by YY
	Building getBuilding() {
		return building;
	}

}
