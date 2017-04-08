package ca.empowered.nms.simulator.node;

import ca.empowered.nms.simulator.config.Settings;
import ca.empowered.nms.simulator.utils.Constants.RELATIONSHIP;

public class Switch extends RelayDevice {

	public Switch() {
		//Switch.level = Settings.getSwitchLevel();
	}
	
	public void setValidRelationships() {
		validRelations.add(RELATIONSHIP.CAUSES);
		validRelations.add(RELATIONSHIP.CONNECTEDTO);
		validRelations.add(RELATIONSHIP.CONNECTEDVIA);
	}
	
}
