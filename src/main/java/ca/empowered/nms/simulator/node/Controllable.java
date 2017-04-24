package ca.empowered.nms.simulator.node;

import ca.empowered.nms.simulator.utils.Constants.STATE;

/**
 * Interface for all node elements to manage current state.
 * 
 * @author mboparai
 *
 */
public interface Controllable {

	/**
	 * Get current state of the this node.
	 * 
	 * @return
	 */
	public STATE getCurrentState();

	/**
	 * Set current state for this node.
	 * 
	 * @param newState
	 */
	public void setCurrentState(STATE newState);
	
}
