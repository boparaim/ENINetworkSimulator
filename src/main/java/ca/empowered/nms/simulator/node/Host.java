package ca.empowered.nms.simulator.node;

import ca.empowered.nms.simulator.utils.Constants.RELATIONSHIP;

public class Host extends System {

	public void setValidRelationships() {
		validRelations.add(RELATIONSHIP.CAUSES);
		validRelations.add(RELATIONSHIP.HOSTS);
		validRelations.add(RELATIONSHIP.COMPOSEDOF);
		validRelations.add(RELATIONSHIP.CONNECTEDTO);
		validRelations.add(RELATIONSHIP.CONNECTEDVIA);
	}
	
}
