package ca.empowered.nms.simulator.node;

import ca.empowered.nms.simulator.utils.Constants.STATE;

public interface Controllable {

	public STATE getCurrentState();
	public void setCurrentState(STATE newState);
	
}
