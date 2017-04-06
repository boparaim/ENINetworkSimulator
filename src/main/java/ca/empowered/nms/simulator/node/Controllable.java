package ca.empowered.nms.simulator.node;

import ca.empowered.nms.simulator.utils.Constants;

public interface Controllable {

	public Constants.STATE getCurrentState();
	public void setCurrentState(Constants.STATE newState);
	public void changeStateRandomly(Boolean changeRandomly);
	public Boolean changeStateRandomly();
	
}
