package ca.empowered.nms.simulator.node;

public class Relationship {

	Element element1;
	Element element2;
	
	public void connect() {
		element1.getRelationships().add(this);
		element2.getRelationships().add(this);
	}
	
}
