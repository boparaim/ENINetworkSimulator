package ca.empowered.nms.simulator.node;

import ca.empowered.nms.simulator.config.Settings;
import ca.empowered.nms.simulator.utils.Constants.RELATIONSHIP;

public class Interface extends PhysicalElement {

	public Interface() {
		//Interface.level = Settings.getInterfaceLevel();
	}
	
	public void setValidRelationships() {
		validRelations.add(RELATIONSHIP.CAUSES);
		validRelations.add(RELATIONSHIP.PARTOF);
		validRelations.add(RELATIONSHIP.CONNECTEDTO);
		validRelations.add(RELATIONSHIP.CONNECTEDVIA);
	}
	
}
