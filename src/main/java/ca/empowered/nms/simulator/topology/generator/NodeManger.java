package ca.empowered.nms.simulator.topology.generator;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.graphstream.graph.Graph;

import ca.empowered.nms.simulator.topology.element.Node;

public class NodeManger {

	private static final Logger log = LogManager.getLogger(NodeManger.class.getName());
	private Graph graph;
	
	public NodeManger(CustomGraph graph) {
		setGraph(graph);
	}
	
	public void createMap(MultiValuedMap<Node, Node> networkMap) {
		// use concurrency to create and connect nodes in graph
		// no layout computation at this point
		for (Node nodeA : networkMap.keySet()) {
			for (Node nodeB : networkMap.get(nodeA)) {
				//log.debug(nodeA.getName()+" -> "+nodeB.getName());
				if (graph.getNode(nodeA.getName()) == null)
					graph.addNode(nodeA.getName());
				if (graph.getNode(nodeB.getName()) == null)
					graph.addNode(nodeB.getName());
				
				if (!graph.getNode(nodeA.getName()).hasEdgeBetween(nodeB.getName()))
					graph.addEdge(nodeA.getName()+"."+nodeB.getName(), nodeA.getName(), nodeB.getName());
				
				
				graph.getNode(nodeA.getName()).addAttribute("ui.class", nodeA.getClassName());
				graph.getNode(nodeB.getName()).addAttribute("ui.class", nodeB.getClassName());
				graph.getNode(nodeA.getName()).addAttribute("ui.label", nodeA.getClassName());
				graph.getNode(nodeB.getName()).addAttribute("ui.label", nodeB.getClassName());
				//graph.getNode(nodeA.getName()).addAttribute("layout.weight", .5);
				//graph.getNode(nodeB.getName()).addAttribute("layout.weight", .5);
				//graph.getEdge(nodeA.getName()+"."+nodeB.getName()).addAttribute("layout.weight", .49f);
				//graph.getEdge(nodeA.getName()+"."+nodeB.getName()).addAttribute("layout.ignored", true);
			}
		}
	}
	
	public Graph getGraph() {
		return this.graph;
	}
	
	public void setGraph(Graph graph) {
		this.graph = graph;
	}
	
}
