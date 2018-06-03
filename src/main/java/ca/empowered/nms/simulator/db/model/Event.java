package ca.empowered.nms.simulator.db.model;

import java.io.Serializable;
import java.math.BigInteger;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * This class represents an event entry.
 * Edges are connected based on IP address. We assume that each device has one IP identifying
 * that device at time of adding an edge.
 * But in the DB, edges bind to the node ids (which are generated at time of adding the node
 * to the DB)
 * 
 * @author mboparai
 *
 */
@Entity
public class Event implements Serializable, MessageObject {

	private static final long serialVersionUID = 1326602283689327353L;
	
	public enum TYPE { NODE_CREATED, NODE_DELETED, NODE_UPDATED, EDGE_CREATED, EDGE_DELETED, EDGE_UPDATED,
		TOPOLOGY_DELETED };
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private BigInteger id;
	private String type;
	private String metadata;
	
	@Override
	public String toString() {
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("event {\n");
		stringBuffer.append("\tid: "+id+",\n");
		stringBuffer.append("\ttype: "+type+",\n");
		stringBuffer.append("\tmetadata: "+metadata+",\n");
		stringBuffer.append("}");
		return stringBuffer.toString();
	}
	
	public BigInteger getId() {
		return id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMetadata() {
		return metadata;
	}

	public void setMetadata(String metadata) {
		this.metadata = metadata;
	}
	
}
