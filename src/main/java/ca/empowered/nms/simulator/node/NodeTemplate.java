package ca.empowered.nms.simulator.node;

import java.util.ArrayList;
import java.util.HashMap;

import ca.empowered.nms.simulator.utils.Constants.STATE;

/**
 * Template for nodes.
 * 
 * @author mboparai
 *
 */
public class NodeTemplate {
	/**
	 * name (or class name) for the node
	 */
	private String name;
	/**
	 * description for the node
	 */
	private String description;
	/**
	 * number of nodes to create from this template
	 */
	private Integer count;
	/**
	 * flag for disabling this template in order to not create nodes of this type
	 */
	private Boolean enabled;
	/**
	 * initialize the node with this state
	 */
	private STATE initialState;
	/**
	 * rank of this node type which is used for event navigation
	 */
	private Integer rank;
	/**
	 * list of node types this node type can connect to
	 */

	private String ip_addr; // Need IP
	//Points of Interest are areas on a Node that can fail. Think CPU, MEMORY, HDD, etc.
	private HashMap<String,Integer> POI;


	private HashMap<String, Integer> relatableTo = new HashMap<>();

	/**
	 * String representation of this node template
	 */
	@Override
	public String toString() {
		String stringRepresentation = "";
		stringRepresentation += "name : " + name + ", ";
		stringRepresentation += "description : " + description + ", ";
		stringRepresentation += "count : " + count + ", ";
		stringRepresentation += "enabled : " + enabled + ", ";
		stringRepresentation += "initialState : " + initialState + ", ";
		stringRepresentation += "rank : " + rank + ", ";
		stringRepresentation += "ip_address : " + ip_addr + ", ";
		for (String key : relatableTo.keySet()) {
			stringRepresentation += "{name : " + key + ", ";
			stringRepresentation += "max : " + relatableTo.get(key) + "}, ";
		}
		return stringRepresentation;
	}

	public String getName() {
		return name;
	}

	public NodeTemplate setName(String name) {
		this.name = name;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public NodeTemplate setDescription(String description) {
		this.description = description;
		return this;
	}

	public Integer getCount() {
		return count;
	}

	public NodeTemplate setCount(Integer count) {
		this.count = count;
		return this;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public NodeTemplate setEnabled(Boolean enabled) {
		this.enabled = enabled;
		return this;
	}

	public String getIP_addr() {
		return ip_addr;
	}

	public NodeTemplate setIP_addr(String ip_addr) {
		this.ip_addr = ip_addr;
		return this;
	}

	public STATE getInitialState() {
		return initialState;
	}

	public NodeTemplate setInitialState(STATE initialState) {
		this.initialState = initialState;
		return this;
	}

	public Integer getRank() {
		return rank;
	}

	public NodeTemplate setRank(Integer rank) {
		this.rank = rank;
		return this;
	}
	public HashMap<String, Integer> getPOIS() { return POI; }

	public NodeTemplate setPOIS(HashMap<String, Integer> POI) {
		this.POI = POI;
		return this;
	}

	public HashMap<String, Integer> getRelatableTo() {
		return relatableTo;
	}
}