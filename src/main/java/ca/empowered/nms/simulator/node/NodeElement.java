package ca.empowered.nms.simulator.node;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.graphstream.graph.implementations.AbstractGraph;
import org.graphstream.graph.implementations.AdjacencyListNode;

import ca.empowered.nms.simulator.api.NodeManager;
import ca.empowered.nms.simulator.utils.Constants.STATE;

public class NodeElement extends AdjacencyListNode implements Controllable {
	
	private static final Logger log = LogManager.getLogger(NodeElement.class.getName());
	
	private STATE currentState;
	private static Observable observable = new Observable();

	public NodeElement(AbstractGraph graph, String id) {
		super(graph, id);
		observers = new LinkedHashSet<>();
	}
	
	public boolean isRelatableTo(NodeElement otherNode) {
		boolean isValid = false;
		NodeElement thisNode = this;
		
		// don't connect to itself
		if ( thisNode.equals(otherNode) ) {
			return isValid;
		}
		
		// don't connect to same object multiple times
		if ( thisNode.hasEdgeBetween(otherNode) ) {
			return isValid;
		}
		
		String thisNodeClass = thisNode.getAttribute("class");
		String otherNodeClass = otherNode.getAttribute("class");
				
		// is a relationship defined for these two types
		if ( !thisNode.hasAttribute("rel"+otherNodeClass) ) {
			return isValid;
		}
		if ( !otherNode.hasAttribute("rel"+thisNodeClass) ) {
			return isValid;
		}
		
		int thisNodeOtherPossibleCount = 0;
		int otherNodeThisPossibleCount = 0;
		
		thisNodeOtherPossibleCount = thisNode.getAttribute("rel"+otherNodeClass);
		otherNodeThisPossibleCount = otherNode.getAttribute("rel"+thisNodeClass);
		
		// validate we haven't passed the limit on any side
		if (thisNodeOtherPossibleCount > 0
				&& otherNodeThisPossibleCount > 0) {
			synchronized (otherNode) {
				thisNode.setAttribute("rel"+otherNodeClass, (thisNodeOtherPossibleCount - 1));
				otherNode.setAttribute("rel"+thisNodeClass, (otherNodeThisPossibleCount - 1));
			}
			
			isValid = true;
		}
		
		return isValid;
	}

	@Override
	public STATE getCurrentState() {
		return STATE.valueOf( this.getAttribute("state").toString().toUpperCase() );
	}
	
	@Override
	public void setCurrentState(STATE state) {
		this.addAttribute("state", state.toString());
		
		this.setChanged();
		this.notifyObservers(this);
		
		if (state.equals(STATE.DOWN))
			this.addAttribute("ui.class", this.getAttribute("class")+"Down");
		else if (state.equals(STATE.DEGRADED))
			this.addAttribute("ui.class", this.getAttribute("class")+"Degraded");
		else if (state.equals(STATE.UP))
			this.addAttribute("ui.class", this.getAttribute("class"));
		else if (state.equals(STATE.UNKNOWN))
			this.addAttribute("ui.class", this.getAttribute("class"));
		
		
		// only report DOWNs to children
		if (!state.equals(STATE.DOWN))
			return;
		
		// now notify related nodes of this change
		Iterator<NodeElement> it = this.getNeighborNodeIterator();
		while ( it.hasNext() ) {
			// if other element is below/behind/depends on this element
			NodeElement otherNode = it.next();
			log.debug("this level: ["+this.getAttribute("class")+"]"+this.getAttribute("rank")
				+" other level: ["+otherNode.getAttribute("class")+"]"+otherNode.getAttribute("rank"));
			if ( Integer.parseInt(otherNode.getAttribute("rank").toString()) >= Integer.parseInt(this.getAttribute("rank").toString()) ) {
				continue;
			}
			
			otherNode.setCurrentState(this.getCurrentState());
		}
	}
	
	// source code from Observable
	
	/** Tracks whether this object has changed. */
   private boolean changed;
 
   /* List of the Observers registered as interested in this Observable. */
   private LinkedHashSet observers;
 
   /**
    * Adds an Observer. If the observer was already added this method does
    * nothing.
    *
    * @param observer Observer to add
    * @throws NullPointerException if observer is null
    */
   public synchronized void addObserver(Observer observer)
   {
     if (observer == null)
       throw new NullPointerException("can't add null observer");
     observers.add(observer);
   }
 
   /**
    * Reset this Observable's state to unchanged. This is called automatically
    * by <code>notifyObservers</code> once all observers have been notified.
    *
    * @see #notifyObservers()
    */
   protected synchronized void clearChanged()
   {
     changed = false;
   }
 
   /**
    * Returns the number of observers for this object.
    *
    * @return number of Observers for this
    */
   public synchronized int countObservers()
   {
     return observers.size();
   }
 
   /**
    * Deletes an Observer of this Observable.
    *
    * @param victim Observer to delete
    */
   public synchronized void deleteObserver(Observer victim)
   {
     observers.remove(victim);
   }
 
   /**
    * Deletes all Observers of this Observable.
    */
   public synchronized void deleteObservers()
   {
     observers.clear();
   }
 
   /**
    * True if <code>setChanged</code> has been called more recently than
    * <code>clearChanged</code>.
    *
    * @return whether or not this Observable has changed
    */
   public synchronized boolean hasChanged()
   {
     return changed;
   }
 
   /**
    * If the Observable has actually changed then tell all Observers about it,
    * then reset state to unchanged.
    *
    * @see #notifyObservers(Object)
    * @see Observer#update(Observable, Object)
    */
   public void notifyObservers()
   {
     notifyObservers(null);
   }
 
   /**
    * If the Observable has actually changed then tell all Observers about it,
    * then reset state to unchanged. Note that though the order of
    * notification is unspecified in subclasses, in Observable it is in the
    * order of registration.
    *
    * @param obj argument to Observer's update method
    * @see Observer#update(Observable, Object)
    */
   public void notifyObservers(Object obj)
   {
     if (! hasChanged())
       return;
     // Create clone inside monitor, as that is relatively fast and still
     // important to keep threadsafe, but update observers outside of the
     // lock since update() can call arbitrary code.
     Set s;
     synchronized (this)
       {
         s = (Set) observers.clone();
       }
     int i = s.size();
     Iterator iter = s.iterator();
     while (--i >= 0)
       ((Observer) iter.next()).update(observable, obj);
     clearChanged();
   }
 
   /**
    * Marks this Observable as having changed.
    */
   protected synchronized void setChanged()
   {
     changed = true;
   }

}
