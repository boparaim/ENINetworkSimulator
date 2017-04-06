package ca.empowered.nms.simulator.api;

import java.lang.reflect.Constructor;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ca.empowered.nms.simulator.config.Settings;
import ca.empowered.nms.simulator.node.Element;
import ca.empowered.nms.simulator.utils.Constants.STATE;

public class NodeFactory {
	
	private static final Logger log = LogManager.getLogger(NodeFactory.class.getName());
	private HashMap<String, Element> nodes = new HashMap<>();
	
	public void relateNodes() {
		for (Element node : nodes.values()) {
			
		}
	}
	
	public void generateNodes() {
		
		generateNodes("ca.empowered.nms.simulator.node.Host", Settings.getHostCount());
		generateNodes("ca.empowered.nms.simulator.node.WebApplication", Settings.getWebApplicationCount());
		
		log.debug("generated objects: " + nodes.size());
		
	}
	
	private void generateNodes(String className, int count) {
		String thisClassName = className.replaceAll(".*\\.", "");
		log.debug("class name: "+className);
		log.debug("this class name: "+thisClassName);
		for ( int i = 0; i < count; i++ ) {
			String instanceName = thisClassName + "-" + (i + 1000) + Settings.getNodeNameSuffix();
			log.debug("instance name: "+instanceName);
			
			try {
				Class<?> thisClass = Class.forName(className);
				Constructor<?> thisConstructor = thisClass.getConstructor(null);
				Element thisObject = (Element)thisConstructor.newInstance(null);
				
				thisObject.setName(instanceName);
				thisObject.setCurrentState(STATE.UNKNOWN);
				thisObject.changeStateRandomly(Settings.getRandomizeEventGeneration());
				
				log.debug(thisObject);
				
				nodes.put(instanceName, thisObject);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
	}
	
}
