package building;
// ListIterater can be used to look at the contents of the floor queues for 
// debug/display purposes...
import java.util.ListIterator;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import genericqueue.GenericQueue;

// TODO: Auto-generated Javadoc
// CLASS MADE BY SEAN AMINOV
/**
 * The Class Floor. This class provides the up/down queues to hold
 * Passengers as they wait for the Elevator.
 */
public class Floor {
	/**  Constant for representing direction. */
	private static final int UP = 1;
	private static final int DOWN = -1;

	/** The queues to represent Passengers going UP or DOWN */	
	private GenericQueue<Passengers> down;
	private GenericQueue<Passengers> up;
	
	private int numPassInDownQueue;
	private int numPassInUpQueue;
	
	public Floor(int qSize) {
		down = new GenericQueue<Passengers>(qSize);
		up = new GenericQueue<Passengers>(qSize);
	}
	

	//checked by Nolan C
	public boolean addToQueue(Passengers p) {
		if (p.getDirection() == UP) {
			numPassInUpQueue = numPassInUpQueue + p.getNumPass();
			return up.offer(p);
		} else {
			numPassInDownQueue = numPassInDownQueue + p.getNumPass();
			return down.offer(p);
		}
	}
	
	public int getNumPassInUpQueue() {
		return numPassInUpQueue;
	}
	
	public int getNumPassInDownQueue() {
		return numPassInDownQueue;
	}
	
	public Passengers peekUpQueue() {
		return up.peek();
	}
	
	public Passengers peekDownQueue() {
		return down.peek();
	}
	
	public Passengers peekCertainQueue(int direction) { //Peeks the q
		if (direction == UP) {
			return up.peek();
		} else {
			return down.peek();
		}
	}
		
	
	public Passengers removeUpQueue() {
		numPassInUpQueue = numPassInUpQueue - up.peek().getNumPass();
		return up.poll(); // Will remove but also return null if empty
	}
	
	public Passengers removeDownQueue() {
		numPassInDownQueue = numPassInDownQueue - down.peek().getNumPass();
		return down.poll();
	}
	
	public Passengers removeCertainQueue(int direction) { //Removes depending on direction
		if (direction == UP) {
			return removeUpQueue();
		} else {
			return removeDownQueue();
		}
	}
	

	//checked by Nolan C
	public boolean isEmptyUpQueue() {
		if (up.isEmpty()) {
			return true;
		} else {
			return false;
		}
	}
	
	//checked by Nolan C
	public boolean isEmptyDownQueue() {
		if (down.isEmpty()) {
			return true;
		} else {
			return false;
		}
	}
	
	//checked by Nolan C
	public boolean whichQueueIsEmpty(int direction) {
		if (direction == UP) {
			return isEmptyUpQueue();
		} else {
			return isEmptyDownQueue();
		}
	}
	

	//checked by Nolan C
	public boolean bothQueuesEmpty() {
		if (down.isEmpty() && up.isEmpty()) {
			return true;
		} else {
			return false;
		}
	}
	
	
	
	// TODO: Write the helper methods needed for this class. 
	// You probably will only be accessing one queue at any
	// given time based upon direction - you could choose to 
	// account for this in your methods.
	
	/**
	 * Queue string. This method provides visibility into the queue
	 * contents as a string. What exactly you would want to visualize 
	 * is up to you
	 *
	 * @param dir determines which queue to look at
	 * @return the string of queue contents
	 */
	String queueString(int dir) {
		String str = "";
		ListIterator<Passengers> list;
		list = (dir == UP) ?up.getListIterator() : down.getListIterator();
		if (list != null) {
			while (list.hasNext()) {
				// choose what you to add to the str here.
				// Example: str += list.next().getNumPass();
				if (list.hasNext()) str += ",";
			}
		}
		return str;	
	}
	
	
}
