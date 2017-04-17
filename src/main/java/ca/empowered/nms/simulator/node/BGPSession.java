package ca.empowered.nms.simulator.node;

import ca.empowered.nms.simulator.config.Settings;
import ca.empowered.nms.simulator.utils.Constants.RELATIONSHIP;

public class BGPSession extends LogicalElement {

	public BGPSession() {
		this.level = Settings.getBgpSessionLevel();
	}
	
	public void setValidRelationships() {
		validRelations.add(RELATIONSHIP.CONNECTS);
		validRelations.add(RELATIONSHIP.CAUSES);
	}
	
}
