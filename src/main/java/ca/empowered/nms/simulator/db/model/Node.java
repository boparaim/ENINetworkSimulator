package ca.empowered.nms.simulator.db.model;

import java.io.Serializable;
import java.math.BigInteger;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * This class represents a node entry.
 * Edges are connected based on IP address. We assume that each device has one IP identifying
 * that device at time of adding an edge.
 * But in the DB, edges bind to the node ids (which are generated at time of adding the node
 * to the DB)
 * 
 * @author mboparai
 *
 */
@Entity
public class Node implements Serializable, MessageObject {

	private static final long serialVersionUID = 1326602283689327353L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private BigInteger id;
	private String name;
	private String ip;
	private String mac;
	private String vendor;
	private String model;
	private String metadata;
	
	@Override
	public String toString() {
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("node {\n");
		stringBuffer.append("\tid: "+id+",\n");
		stringBuffer.append("\tname: "+name+",\n");
		stringBuffer.append("\tip: "+ip+",\n");
		stringBuffer.append("\tmac: "+mac+",\n");
		stringBuffer.append("\tvendor: "+vendor+",\n");
		stringBuffer.append("\tmodel: "+model+",\n");
		stringBuffer.append("\tmetadata: "+metadata+",\n");
		stringBuffer.append("}");
		return stringBuffer.toString();
	}
	
	public BigInteger getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	public String getVendor() {
		return vendor;
	}
	public void setVendor(String vendor) {
		this.vendor = vendor;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public String getMetadata() {
		return metadata;
	}
	public void setMetadata(String metadata) {
		this.metadata = metadata;
	}
	
}
