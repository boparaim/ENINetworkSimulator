package ca.empowered.nms.simulator.node;

import ca.empowered.nms.simulator.utils.Constants;

public class Relationship {

	private Element element1;
	private Element element2;
	private Constants.DIRECTION eventPropagationDirection;
	private boolean isConnected;
	
	public void connect() {
		element1.relationships.add(this);
		element2.relationships.add(this);
		element1.connectedObjects.add(element2);
		element2.connectedObjects.add(element1);
		
		isConnected = true;
	}
	
	public boolean propagateEvent(Element element) {
		if ( !isConnected )
			return false;
		
		switch ( eventPropagationDirection ) {
			case NODE1TONNODE2:
				if (element.equals(element1))
					return true;
				break;
			case NODE2TONNODE1:
				if (element.equals(element2))
					return true;
				break;
			default:
				break;
		}
		
		return false;
	}

	public Element getElement1() {
		return element1;
	}

	public void setElement1(Element element1) {
		this.element1 = element1;
	}

	public Element getElement2() {
		return element2;
	}

	public void setElement2(Element element2) {
		this.element2 = element2;
	}

	public Constants.DIRECTION getEventPropagationDirection() {
		return eventPropagationDirection;
	}

	public void setEventPropagationDirection(Constants.DIRECTION eventPropagationDirection) {
		this.eventPropagationDirection = eventPropagationDirection;
	}
	
}
