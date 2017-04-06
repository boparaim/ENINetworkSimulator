package ca.empowered.nms.simulator.node;

import ca.empowered.nms.simulator.utils.Constants.RELATIONSHIP;

public class Interface extends PhysicalElement {

	public void setValidRelationships() {
		validRelations.add(RELATIONSHIP.CAUSES);
		validRelations.add(RELATIONSHIP.PARTOF);
		validRelations.add(RELATIONSHIP.CONNECTEDTO);
		validRelations.add(RELATIONSHIP.CONNECTEDVIA);
	}
	
}
