package ca.empowered.nms.simulator.node;

import java.util.ArrayList;

import ca.empowered.nms.simulator.utils.Constants;

public interface Relatable {

	public ArrayList<Constants.RELATIONSHIP> validRelations = new ArrayList<>();
	
	public void setValidRelationships();
	
}
