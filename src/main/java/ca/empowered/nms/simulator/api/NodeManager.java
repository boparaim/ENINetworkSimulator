package ca.empowered.nms.simulator.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.NodeFactory;
import org.graphstream.graph.implementations.AbstractGraph;
import org.graphstream.graph.implementations.AdjacencyListGraph;

import ca.empowered.nms.simulator.config.Settings;
import ca.empowered.nms.simulator.event.EventObserver;
import ca.empowered.nms.simulator.node.Element;
import ca.empowered.nms.simulator.node.NodeElement;
import ca.empowered.nms.simulator.node.NodeTemplate;
import ca.empowered.nms.simulator.utils.Constants.STATE;

public final class NodeManager {
	
	private static final Logger log = LogManager.getLogger(NodeManager.class.getName());
	
	private static EventObserver eventObserver;
	private static ArrayList<NodeTemplate> nodeTemplates = new ArrayList<NodeTemplate>();
	private static Graph graph;
	private static boolean usable = false;
	
	private static ArrayList<String> allNodes = new ArrayList<>();
	private static ArrayList<String> disconnectedNodes = new ArrayList<>();
	
	public static void init() {
		graph = new AdjacencyListGraph(Settings.getAppName());  // SingleGraph or MultiGraph() // less memory/faster - AdjacencyListGraph // thread-safe - ConcurrentGraph
		graph.setStrict(true);
		graph.setAutoCreate( false );
		// improve visual quality
		graph.addAttribute("ui.quality");
		graph.addAttribute("ui.antialias");
		// stylesheet
		graph.addAttribute("ui.stylesheet", "url('graph-stream.css')");
		
		graph.setNodeFactory(new NodeFactory<NodeElement>() {
			public NodeElement newInstance(String id, Graph graph) {
				return new NodeElement((AbstractGraph) graph, id);
			}
		});
		//graph.display();
		
		usable = true;
	}
	
	public static void generateNodes() {
		if (!usable || graph == null) {
			log.error("NodeFactory is not initialized properly. Call init() before using it.");
			return;
		}
		
		for ( NodeTemplate nodeTemplate : nodeTemplates ) {
			log.debug(" -> "+nodeTemplate.toString());
			if ( !nodeTemplate.getEnabled() ) {
				log.info("this template is disabled");
				continue;
			}
			for ( int i = 0; i < nodeTemplate.getCount(); i++ ) {
				addNode(i,
						nodeTemplate.getName(), 
						nodeTemplate.getDescription(), 
						nodeTemplate.getInitialState(), 
						nodeTemplate.getRank(), 
						nodeTemplate.getRelatableTo());
			}
		}
		
		log.info("generated objects: " + graph.getNodeCount());
		
	}
	
	public static void addNode(int id, String className, String description, STATE initialState, int rank, HashMap<String, Integer> relatableTo) {
		if (!usable || graph == null) {
			log.error("NodeFactory is not initialized properly. Call init() before using it.");
			return;
		}
		
		String instanceName = className + "-" + (id + 1000) + Settings.getNodeNameSuffix();
		log.debug("instance name: "+instanceName);		
		graph.addNode(instanceName);
		
		NodeElement node = graph.getNode(instanceName);
		node.addAttribute("description", description);
		node.addAttribute("state", initialState.toString());
		node.addAttribute("rank", rank);
		node.addAttribute("ui.label", instanceName);
		node.addAttribute("ui.class", className);
		
		for (String key: relatableTo.keySet()) {
			node.addAttribute("rel"+key, relatableTo.get(key));
		}
		
		node.addObserver(eventObserver);
		
		allNodes.add(node.getId());
		disconnectedNodes.add(node.getId());
		//try { Thread.sleep(20); } catch (Exception e) {}
	}
	
	public static void relateNodes() {
		if (!usable || graph == null) {
			log.error("NodeFactory is not initialized properly. Call init() before using it.");
			return;
		}
		
		for (Node thisNode : graph.getNodeSet()) {
			for (Node otherNode : graph.getNodeSet()) {
				
				NodeElement thisNodeElement = (NodeElement)thisNode;
				NodeElement otherNodeElement = (NodeElement)otherNode;
				
				if ( thisNodeElement.isRelatableTo(otherNodeElement) ) {
					graph.addEdge(thisNode.getId()+"."+otherNode.getId(), thisNode.getId(), otherNode.getId());
					
					log.debug("connected: node1: " + thisNode.getId() + " node2: " + otherNode.getId());
					if (disconnectedNodes.contains(thisNode.getId())) {
						disconnectedNodes.remove(thisNode.getId());
					}
					if (disconnectedNodes.contains(otherNode.getId())) {
						disconnectedNodes.remove(otherNode.getId());
					}
				} else {
					log.debug("invalid relationship: node1: " + thisNode.getId() + " node2: " + otherNode.getId());
				}
			}
		}
		
		log.info("disconnected objects: " + disconnectedNodes.size());
		for (String nodeName : disconnectedNodes) {
			log.debug("x\t "+nodeName);
		}
	}

	public static EventObserver getEventObserver() {
		return eventObserver;
	}

	public static void setEventObserver(EventObserver eventObserver) {
		NodeManager.eventObserver = eventObserver;
	}

	public static ArrayList<String> getAllNodes() {
		return allNodes;
	}

	public ArrayList<NodeTemplate> getNodeTemplates() {
		return nodeTemplates;
	}

	public void setNodeTemplates(ArrayList<NodeTemplate> nodeTemplates) {
		this.nodeTemplates = nodeTemplates;
	}	
}
