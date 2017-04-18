package ca.empowered.nms.simulator.node;

import java.util.ArrayList;
import java.util.Observable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ca.empowered.nms.simulator.ui.GraphManager;
import ca.empowered.nms.simulator.utils.Constants;
import ca.empowered.nms.simulator.utils.Constants.STATE;

public abstract class Element extends Observable implements Controllable {

	private static final Logger log = LogManager.getLogger(Element.class.getName());
	private String name;
		
	private Constants.STATE currentState;
	private Boolean changeStateRandomly;
	
	/** we will use levels to know what node depends on what other node and then propagate events that way */
	public Integer level = 0;
	
	public ArrayList<Element> connectedObjects = new ArrayList<>();
	//public ArrayList<Relationship> relationships = new ArrayList<>();
	
	public String getName() {
		return name;
	}

	@Override
	public STATE getCurrentState() {
		return this.currentState;
	}

	@Override
	public Boolean changeStateRandomly() {
		return this.changeStateRandomly;
	}

	public void setName(String name) {
		this.name = name;
	}

	/*public void setRelationships(RelationshipList<Constants.RELATIONSHIP> relationships) {
		this.relationships = relationships;
	}*/

	@Override
	public void setCurrentState(STATE newState) {
		this.currentState = newState;
		this.setChanged();
		this.notifyObservers(newState);
		
		// only report DOWNs to children
		if (!newState.equals(STATE.DOWN))
			return;
		
		// now notify related nodes of this change
		/*for ( Relationship relationship : this.relationships ) {
			// if other element is below/behind/depends on this element
			Element otherNode = relationship.getOtherNode(this);
			log.debug("** level: ["+this.getClass().getSimpleName()+"]"+this.level+" other level: ["+otherNode.getClass().getSimpleName()+"]"+otherNode.level);
			if ( otherNode.level >= this.level ) {
				continue;
			}
			
			otherNode.setCurrentState(this.getCurrentState());
			otherNode.setChanged();
			otherNode.notifyObservers(otherNode.getCurrentState());
		
			GraphManager.updateNodeState(otherNode, otherNode.getCurrentState());
		}*/
	}

	@Override
	public void changeStateRandomly(Boolean changeRandomly) {
		this.changeStateRandomly = changeRandomly;
	}
	
}
