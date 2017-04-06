package ca.empowered.nms.simulator.api;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ca.empowered.nms.simulator.config.Settings;
import ca.empowered.nms.simulator.event.EventObserver;
import ca.empowered.nms.simulator.node.Element;
import ca.empowered.nms.simulator.node.Relationship;
import ca.empowered.nms.simulator.utils.Constants.STATE;
import ca.empowered.nms.simulator.utils.ValidRelationships;

public final class NodeFactory {
	
	private static final Logger log = LogManager.getLogger(NodeFactory.class.getName());
	private static HashMap<String, Element> allNodes = new HashMap<>();
	private static ArrayList<Relationship> allRelationships = new ArrayList<>();
	private static EventObserver eventObserver;
	
	// just for testing
	private static ArrayList<Element> disconnectedNodes = new ArrayList<>();
	
	/*public void turnKeyOn() {
		for (Element thisNode : nodes.values()) {
			
		}
	}*/
	
	public static void relateNodes() {
		for (Element thisNode : allNodes.values()) {
			for (Element otherNode : allNodes.values()) {
				// don't connect to itself
				if ( thisNode.equals(otherNode) )
					continue;
				
				// don't connect to same object multiple times
				if ( thisNode.connectedObjects.contains(otherNode) )
					continue;

				//log.debug("this node: " + thisNode.getName() + " other node: " + otherNode.getName());
								
				// is this connection valid
				if (ValidRelationships.isValidRelationship(thisNode, otherNode)) {
					Relationship thisRelationship = new Relationship();
					thisRelationship.setElement1(thisNode);					
					thisRelationship.setElement2(otherNode);
					thisRelationship.connect();
					
					if (!thisNode.getCurrentState().equals(STATE.UP))
						thisNode.setCurrentState(STATE.UP);
					if (!otherNode.getCurrentState().equals(STATE.UP))
						otherNode.setCurrentState(STATE.UP);
					
					allRelationships.add(thisRelationship);
					
					//log.debug(thisRelationship);
					log.debug("related to: "+thisNode.getName() + " <--> " + otherNode.getName());
				}
			}
		}
		
		for (Relationship rel: allRelationships) {
			log.info(rel+" : "+rel.getElement1().getName()+"["+rel.getElement1().getCurrentState()+"]"
		+" <-->"+rel.getElement2().getName()+"["+rel.getElement2().getCurrentState()+"]");
		}
		
		for (Element thisNode : allNodes.values()) {
			/*log.debug(thisNode.getName());
			log.debug(thisNode.relationships.size());*/
			if (thisNode.relationships.size() == 0)
				disconnectedNodes.add(thisNode);
		}
		log.warn("disconnected elements: "+disconnectedNodes.size());
		for (Element thisNode : disconnectedNodes) {
			log.debug(thisNode.getName());
		}
	}
	
	public static void generateNodes() {
		
		// TODO: change 3
		generateNodes("ca.empowered.nms.simulator.node.Host", Settings.getHostCount());
		generateNodes("ca.empowered.nms.simulator.node.WebApplication", Settings.getWebApplicationCount());
		generateNodes("ca.empowered.nms.simulator.node.Card", Settings.getCardCount());
		
		log.info("generated objects: " + allNodes.size());
		
	}
	
	private static void generateNodes(String className, int count) {
		String thisClassName = className.replaceAll(".*\\.", "");
		//log.debug("class name: "+className);
		//log.debug("this class name: "+thisClassName);
		for ( int i = 0; i < count; i++ ) {
			String instanceName = thisClassName + "-" + (i + 1000) + Settings.getNodeNameSuffix();
			log.debug("instance name: "+instanceName);
			
			try {
				Class<?> thisClass = Class.forName(className);
				Constructor<?> thisConstructor = thisClass.getConstructor(null);
				Element thisElement = (Element)thisConstructor.newInstance(null);
				
				thisElement.setName(instanceName);
				
				if (thisElement.countObservers() < 1)
					thisElement.addObserver(NodeFactory.eventObserver);
				
				thisElement.setCurrentState(STATE.UNKNOWN);
				thisElement.changeStateRandomly(Settings.getRandomizeEventGeneration());
				
				//log.debug(thisObject);
				
				allNodes.put(instanceName, thisElement);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
	}

	public static EventObserver getEventObserver() {
		return eventObserver;
	}

	public static void setEventObserver(EventObserver eventObserver) {
		NodeFactory.eventObserver = eventObserver;
	}

	public static HashMap<String, Element> getAllNodes() {
		return allNodes;
	}

	/*private static void setAllNodes(HashMap<String, Element> allElements) {
		NodeFactory.allNodes = allElements;
	}*/

	public static ArrayList<Relationship> getAllRelationships() {
		return allRelationships;
	}

	public static void setAllRelationships(ArrayList<Relationship> allRelationships) {
		NodeFactory.allRelationships = allRelationships;
	}
	
}
