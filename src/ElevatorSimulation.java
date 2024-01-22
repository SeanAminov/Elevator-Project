
import building.Elevator;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
// TODO: Auto-generated Javadoc
//Class by Nolan C

// TODO: Auto-generated Javadoc
/**
 * The Class ElevatorSimulation.
 */
public class ElevatorSimulation extends Application {
	
	/**  Instantiate the GUI fields. */
	private ElevatorSimController controller;
	
	/** The num floors. */
	private final int NUM_FLOORS;
	
	/** The num elevators. */
	private final int NUM_ELEVATORS;
	
	/** The curr floor. */
	private int currFloor;
	
	/** The passengers. */
	private int passengers;
	
	/** The time. */
	private int time;
	
	
	/** The scene width. */
	private final int sceneWidth = 642;
	
	/** The scene height. */
	private final int sceneHeight = 400;
	
	/** The t 1. */
	//The timeline
	private Timeline t, t1;
	
	/** The main. */
	//
	private BorderPane main = new BorderPane();
	
	/** The title pane. */
	private GridPane titlePane = new GridPane();
	
	/** The button pane. */
	private BorderPane buttonPane = new BorderPane();
	
	/** The viewing pane. */
	private GridPane viewingPane = new GridPane();	
	

	/**  Local copies of the states for tracking purposes. */
	private final int STOP = Elevator.STOP;
	
	/** The mvtoflr. */
	private final int MVTOFLR = Elevator.MVTOFLR;
	
	/** The opendr. */
	private final int OPENDR = Elevator.OPENDR;
	
	/** The offld. */
	private final int OFFLD = Elevator.OFFLD;
	
	/** The board. */
	private final int BOARD = Elevator.BOARD;
	
	/** The closedr. */
	private final int CLOSEDR = Elevator.CLOSEDR;
	
	/** The mv1flr. */
	private final int MV1FLR = Elevator.MV1FLR;

	
	
	/** The main scene. */
	Scene mainScene = new Scene(main, sceneWidth, sceneHeight);
	
	/** The main title. */
	Label mainTitle = new Label("Tomi Elevator");
	
	/** The time label. */
	Label timeLabel = new Label("");
	
	/** The passengers per floor. */
	Label[] passengersPerFloor;
	
	/** The passengers on elevator. */
	Label passengersOnElevator = new Label("");
	
	/** The step N. */
	Button stepSim, start, log, stepN;
	
	/** The elevator open. */
	private Circle elevatorOpened;
	
	/** The elevator closed. */
	private Rectangle elevatorClosed, boarding;
	
	/** The boarding. */
	private Polygon offloading, triUp, triDown;
	
	/** The is done. */
	private boolean isDone = false;
	
	
	/**
	 * Instantiates a new elevator simulation.
	 */
	// Checked by YY
	public ElevatorSimulation() {
		controller = new ElevatorSimController(this);	
		NUM_FLOORS = controller.getNumFloors();
		NUM_ELEVATORS = controller.getNumElevators();
		currFloor = controller.getCurrentFloor();
		passengersPerFloor = new Label[NUM_FLOORS*2];
	}

	/**
	 * Start.
	 *
	 * @param primaryStage the primary stage
	 * @throws Exception the exception
	 */
	// Checked by YY
	@Override
	public void start(Stage primaryStage) throws Exception {
		// You need to design the GUI. Note that the test name should
		// appear in the Title of the window!!
//		primaryStage.setTitle("Elevator Simulation - "+ controller.getTestName());
//		primaryStage.show();

		//TODO: Complete your GUI, including adding any helper methods.
		//      Meet the 30 line limit...
		
		//------------------------------
		titlePane.add(mainTitle, 1, 0);
		titlePane.add(timeLabel,1,1);
		timeLabel.setAlignment(Pos.CENTER);
		main.setTop(titlePane);
		BorderPane.setAlignment(titlePane, Pos.TOP_CENTER);
		titlePane.setAlignment(Pos.CENTER);
		main.setCenter(viewingPane);
		main.setBottom(buttonPane);
		makeButtons();
		makeViewingPane();
		primaryStage.setTitle("Elevator Simulation -- " + controller.getTestName());
		primaryStage.setScene(mainScene);
		primaryStage.show();
		
		
	}
	
	/**
	 * Make button box.
	 */
	// Checked by YY
	public void makeButtons() {
		HBox buttons = new HBox(30);
		buttonPane.setTop(buttons);
		stepSim = new Button("Step Simulation");
		stepN = new Button("Step N Ticks");
		TextField n = new TextField();
		start = new Button("Run");
		log = new Button("Enable logging");
		buttons.getChildren().addAll(stepSim, stepN, n, start, log);
		
		if(isDone == false) {
			stepSim.setOnAction(e -> controller.stepSim());
			t = new Timeline(new KeyFrame(Duration.millis(200), ae -> controller.stepSim()));
			start.setOnAction(e -> {t.setCycleCount(Animation.INDEFINITE); t.play();});
			log.setOnAction(e -> controller.enableLogging());
			t1 = new Timeline(new KeyFrame(Duration.millis(200), ae -> controller.stepSim()));
			stepN.setOnAction(e -> {t1.setCycleCount(Integer.parseInt(n.getText())); t1.play();});
		}
	}
	
	/**
	 * Make viewing pane.
	 */
	// Checked by YY
	public void makeViewingPane() {
		viewingPane.setHgap(20);
		for (int i = 0; i < NUM_FLOORS; i++) {
			viewingPane.add(new Label("Floor " + (NUM_FLOORS - i)), 10, i);
			viewingPane.add(new Label("Up: " + "\n Down: "), 15, i);
		}
		boarding();
		offloading();
		elevatorOpened();
		elevatorClosed();
		viewingPane.add(elevatorOpened, 8, 5);
		viewingPane.add(elevatorClosed, 8, 5);
		viewingPane.add(boarding, 8, 5);
		viewingPane.add(offloading, 8, 5);
		
	}
	
	/**
	 * Boarding.
	 */
	// Checked by YY
	public void boarding() {
		boarding = new Rectangle();	
		boarding.setX(0);
		boarding.setY(0);
		boarding.setWidth(10);
		boarding.setHeight(10);
		boarding.setFill(Color.GREEN);
		boarding.setVisible(false);
	}
	
	/**
	 * Offloading.
	 */
	// Checked by YY
	public void offloading() {
		offloading = new Polygon();  // Right
		offloading.getPoints().addAll(5.0,20.0,25.0,20.0,15.0,20-10*Math.pow(3,0.5));
		offloading.setRotate(90);
		offloading.setStroke(Color.BLUE);
		offloading.setStrokeWidth(2);
		offloading.setFill(Color.BLUE);
		offloading.setVisible(false);
	}
	
	/**
	 * Elevator opened.
	 */
	// Checked by YY
	public void elevatorOpened() {
		elevatorOpened = new Circle();
		elevatorOpened.setCenterX(0);
		elevatorOpened.setCenterY(0);
		elevatorOpened.setRadius(10);
		elevatorOpened.setFill(Color.GREY);
		elevatorOpened.setVisible(true);
	}
	
	/**
	 * Elevator closed.
	 */
	// Checked by YY
	public void elevatorClosed() {
		elevatorClosed = new Rectangle();
		elevatorClosed.setX(0);
		elevatorClosed.setY(0);
		elevatorClosed.setWidth(10);
		elevatorClosed.setHeight(10);
		elevatorClosed.setFill(Color.BLACK);
		elevatorClosed.setVisible(false);
	}

	/**
	 * Make triangles.
	 */
	// Checked by YY
	public void makeTriangles() {
		triUp = new Polygon();  //Up
		triUp.getPoints().addAll(5.0,20.0,25.0,20.0,15.0,20-10*Math.pow(3,0.5));
		triUp.setStroke(Color.RED);
		triUp.setStrokeWidth(2);
		triUp.setFill(Color.RED);
		triUp.setVisible(true);
		triDown = new Polygon();  //down
		triDown.getPoints().addAll(5.0,20.0,25.0,20.0,15.0,20-10*Math.pow(3,0.5));
		triDown.setStroke(Color.RED);
		triDown.setStrokeWidth(2);
		triDown.setFill(Color.RED);
		triDown.setVisible(true);

	}
	
	
	/**
	 * Update GUI.
	 *
	 * @param stepCount the step count
	 * @param currState the curr state
	 * @param numPassengers the num passengers
	 * @param passPerFloorUp the pass per floor up
	 * @param passPerFloorDown the pass per floor down
	 * @param currFloor the curr floor
	 */
	// Checked by YY
	public void updateGUI(int stepCount, int currState, int numPassengers, int[] passPerFloorUp, int[] passPerFloorDown, int currFloor) {
		this.currFloor = currFloor;
		time = stepCount;
		updateGUIViewingPane();
		if(currState == 6) {
			updateGUICase6();
		} else if(currState == 2) {
			updateGUICase2();
		} else if(currState == 3) {
			updateGUICase3();
		} else if(currState == 4) {
			updateGUICase4();
		}
		passengersOnElevator.setText(Integer.toString(numPassengers));
		viewingPane.getChildren().remove(passengersOnElevator);
		viewingPane.add(passengersOnElevator, 13, NUM_FLOORS-currFloor-1);
		for (int i = 0; i < NUM_FLOORS; i++) {
			viewingPane.getChildren().remove(passengersPerFloor[i]);
			passengersPerFloor[i] = new Label(passPerFloorUp[i] + "\n" + passPerFloorDown[i]);
			viewingPane.add(passengersPerFloor[i], 16, NUM_FLOORS-i-1);
		}
	}
	
	/**
	 * Update GUI case 4.
	 */
	// Checked by YY
	public void updateGUICase4() {
		elevatorClosed.setVisible(false);
		elevatorOpened.setVisible(false);
		boarding.setVisible(true);
		offloading.setVisible(false);
	}
	
	/**
	 * Update GUI case 3.
	 */
	// Checked by YY
	public void updateGUICase3() {
		elevatorClosed.setVisible(false);
		elevatorOpened.setVisible(false);
		boarding.setVisible(false);
		offloading.setVisible(true);
	}
	
	/**
	 * Update GUI case 2.
	 */
	// Checked by YY
	public void updateGUICase2() {
		elevatorClosed.setVisible(false);
		elevatorOpened.setVisible(true);
		boarding.setVisible(false);
		offloading.setVisible(false);
	}
	
	/**
	 * Update GUI case 6.
	 */
	// Checked by YY
	public void updateGUICase6() {
		elevatorClosed.setVisible(true);
		elevatorOpened.setVisible(false);
		boarding.setVisible(false);
		offloading.setVisible(false);
	}
	
	/**
	 * Update GUI viewing pane.
	 */
	// Checked by YY
	public void updateGUIViewingPane() {
		timeLabel.setText("           Time: " + time);
		viewingPane.getChildren().remove(elevatorOpened);
		viewingPane.getChildren().remove(elevatorClosed);
		viewingPane.getChildren().remove(boarding);
		viewingPane.getChildren().remove(offloading);
		viewingPane.add(elevatorOpened, 8, NUM_FLOORS-currFloor-1);
		viewingPane.add(elevatorClosed, 8, NUM_FLOORS-currFloor-1);
		viewingPane.add(boarding, 8, NUM_FLOORS-currFloor-1);
		viewingPane.add(offloading, 8, NUM_FLOORS-currFloor-1);
	}
	
	/**
	 * End sim.
	 */
	// Checked by YY
	public void endSim() {
		t.stop();
		t1.stop();
		isDone = true;
	}
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main (String[] args) {
		Application.launch(args);
	}
	
	

}
