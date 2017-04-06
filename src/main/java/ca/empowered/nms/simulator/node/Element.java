package ca.empowered.nms.simulator.node;

import java.util.ArrayList;
import java.util.Observable;

import ca.empowered.nms.simulator.utils.Constants;
import ca.empowered.nms.simulator.utils.Constants.STATE;

public abstract class Element extends Observable implements Controllable, Relatable {
	
	private String name;
		
	private Constants.STATE currentState;
	private Boolean changeStateRandomly;
	
	public ArrayList<Element> connectedObjects = new ArrayList<>();
	public ArrayList<Relationship> relationships = new ArrayList<>();
	
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
	}

	@Override
	public void changeStateRandomly(Boolean changeRandomly) {
		this.changeStateRandomly = changeRandomly;
	}
	
}
