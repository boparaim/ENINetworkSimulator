package ca.empowered.nms.simulator.node;

import java.util.HashMap;

import ca.empowered.nms.simulator.utils.Constants.STATE;

public class NodeTemplate {
	private String name;
	private String description;
	private Integer count;
	private Boolean enabled;
	private STATE initialState;
	private Integer rank;
	private HashMap<String, Integer> relatableTo = new HashMap<>();
	
	@Override
	public String toString() {
		String stringRepresentation = "";
		stringRepresentation += "name : "+name+", ";
		stringRepresentation += "description : "+description+", ";
		stringRepresentation += "count : "+count+", ";
		stringRepresentation += "enabled : "+enabled+", ";
		stringRepresentation += "initialState : "+initialState+", ";
		stringRepresentation += "rank : "+rank+", ";
		for (String key : relatableTo.keySet() ) {
			stringRepresentation += "{name : "+key+", ";
			stringRepresentation += "max : "+relatableTo.get(key)+"}, ";
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
	public HashMap<String, Integer> getRelatableTo() {
		return relatableTo;
	}
}
