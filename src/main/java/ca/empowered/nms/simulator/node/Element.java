package ca.empowered.nms.simulator.node;

import ca.empowered.nms.simulator.utils.Constants;
import ca.empowered.nms.simulator.utils.Constants.STATE;

public abstract class Element implements Controllable, Relatable {
	
	private String name;
	
	
	private Constants.STATE currentState;
	private Boolean changeStateRandomly;
	
	public String getName() {
		return name;
	}

	public RelationshipList<Constants.RELATIONSHIP> getRelationships() {
		return relationships;
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
	}

	@Override
	public void changeStateRandomly(Boolean changeRandomly) {
		this.changeStateRandomly = changeRandomly;
	}
	
}
