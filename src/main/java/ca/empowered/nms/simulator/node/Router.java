package ca.empowered.nms.simulator.node;

import ca.empowered.nms.simulator.utils.Constants.RELATIONSHIP;

public class Router extends RelayDevice {

	public void setValidRelationships() {
		validRelations.add(RELATIONSHIP.CAUSES);
		validRelations.add(RELATIONSHIP.CONNECTEDTO);
		validRelations.add(RELATIONSHIP.CONNECTEDVIA);
	}
	
}
