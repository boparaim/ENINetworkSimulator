package ca.empowered.nms.simulator.node;

import ca.empowered.nms.simulator.utils.Constants.RELATIONSHIP;

public class NetworkConnection extends LogicalElement {

	public void setValidRelationships() {
		validRelations.add(RELATIONSHIP.CAUSES);
		validRelations.add(RELATIONSHIP.CONNECTS);
	}
	
}
