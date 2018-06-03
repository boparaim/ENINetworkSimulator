package ca.empowered.nms.simulator.json;

public class JsonTopologyStats {
	
	int totalNodes;
	int totalEdges;
	int uniqueNodes;
	int uniqueEdges;
	
	public int getTotalNodes() {
		return totalNodes;
	}
	public void setTotalNodes(int totalNodes) {
		this.totalNodes = totalNodes;
	}
	public int getTotalEdges() {
		return totalEdges;
	}
	public void setTotalEdges(int totalEdges) {
		this.totalEdges = totalEdges;
	}
	public int getUniqueNodes() {
		return uniqueNodes;
	}
	public void setUniqueNodes(int uniqueNodes) {
		this.uniqueNodes = uniqueNodes;
	}
	public int getUniqueEdges() {
		return uniqueEdges;
	}
	public void setUniqueEdges(int uniqueEdges) {
		this.uniqueEdges = uniqueEdges;
	}
	
}
