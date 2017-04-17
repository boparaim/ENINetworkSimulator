package ca.empowered.nms.simulator.node;

import ca.empowered.nms.simulator.config.Settings;
import ca.empowered.nms.simulator.utils.Constants.RELATIONSHIP;

public class NetworkConnection extends LogicalElement {

	
	public NetworkConnection() {
		this.level = Settings.getNetworkConnectionLevel();
	}
	
	public void setValidRelationships() {
		validRelations.add(RELATIONSHIP.CAUSES);
		validRelations.add(RELATIONSHIP.CONNECTS);
	}
	
}
