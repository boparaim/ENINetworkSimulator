package ca.empowered.nms.simulator.db.model;

import java.io.Serializable;
import java.math.BigInteger;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

/**
 * This class represents an edge entry.
 * Edges are connected based on IP address. We assume that each device has one IP identifying
 * that device at time of adding an edge.
 * But in the DB, edges bind to the node ids (which are generated at time of adding the node
 * to the DB)
 * 
 * @author mboparai
 *
 */
@Entity
public class Edge implements Serializable, MessageObject {

	private static final long serialVersionUID = 1326602283689327353L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private BigInteger id;
	//@Column(name="nodeIdA")
	private BigInteger nodeIdA;
	private BigInteger nodeIdB;
	private String ifIndexA;
	private String ifIndexB;
	@Transient
	private String nodeIpA;
	@Transient
	private String nodeIpB;
	
	@Override
	public String toString() {
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("edge {\n");
		stringBuffer.append("\tid: "+id+",\n");
		stringBuffer.append("\tnodeIdA: "+nodeIdA+",\n");
		stringBuffer.append("\tnodeIdB: "+nodeIdB+",\n");
		stringBuffer.append("\tifIndexA: "+ifIndexA+",\n");
		stringBuffer.append("\tifIndexB: "+ifIndexB+",\n");
		stringBuffer.append("\tnodeIpA: "+nodeIpA+",\n");
		stringBuffer.append("\tnodeIpB: "+nodeIpB+",\n");
		stringBuffer.append("}");
		return stringBuffer.toString();
	}
	
	public BigInteger getId() {
		return id;
	}

	public BigInteger getNodeIdA() {
		return nodeIdA;
	}

	public void setNodeIdA(BigInteger nodeIdA) {
		this.nodeIdA = nodeIdA;
	}

	public BigInteger getNodeIdB() {
		return nodeIdB;
	}

	public void setNodeIdB(BigInteger nodeIdB) {
		this.nodeIdB = nodeIdB;
	}

	public String getIfIndexA() {
		return ifIndexA;
	}

	public void setIfIndexA(String ifIndexA) {
		this.ifIndexA = ifIndexA;
	}

	public String getIfIndexB() {
		return ifIndexB;
	}

	public void setIfIndexB(String ifIndexB) {
		this.ifIndexB = ifIndexB;
	}

	public String getNodeIpA() {
		return nodeIpA;
	}

	public void setNodeIpA(String nodeIpA) {
		this.nodeIpA = nodeIpA;
	}

	public String getNodeIpB() {
		return nodeIpB;
	}

	public void setNodeIpB(String nodeIpB) {
		this.nodeIpB = nodeIpB;
	}
		
}
