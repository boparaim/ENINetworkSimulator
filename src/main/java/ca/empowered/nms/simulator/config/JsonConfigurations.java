package ca.empowered.nms.simulator.config;

import java.io.InputStream;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ca.empowered.nms.simulator.node.NodeTemplate;
import ca.empowered.nms.simulator.utils.Constants.STATE;

public class JsonConfigurations {

	private static final Logger log = LogManager.getLogger(JsonConfigurations.class.getName());
	private ObjectMapper objectMapper;
	private JsonNode jsonRootNode;
	private ArrayList<NodeTemplate> nodeTemplates = new ArrayList<NodeTemplate>();
	
	JsonConfigurations(String file) {
		try {
			objectMapper = new ObjectMapper();
			objectMapper.configure(JsonParser.Feature.ALLOW_YAML_COMMENTS, true);
			InputStream inputstream = this.getClass().getClassLoader().getResourceAsStream(file);
			if ( inputstream != null ) {
				log.debug("reading json config file");
				jsonRootNode = objectMapper.readTree(inputstream);
				
				readConfigs();
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
	
	private void readConfigs() {
		if (jsonRootNode == null) {
			log.error("Unable to read json configuration file. Can't create nodes.");
			return;
		}
		
		JsonNode nodes = jsonRootNode.path("nodes");
		for ( JsonNode node : nodes ) {
			log.debug(node.toString());
			
			NodeTemplate nodeTemplate = new NodeTemplate();
			nodeTemplate.setName(node.path("name").textValue());
			nodeTemplate.setDescription(node.path("description").textValue());
			nodeTemplate.setCount(node.path("count").asInt(0));
			nodeTemplate.setEnabled(node.path("enabled").asBoolean(false));
			if ( node.path("init-state").textValue().equalsIgnoreCase("down") )
				nodeTemplate.setInitialState(STATE.DOWN);
			else if ( node.path("init-state").textValue().equalsIgnoreCase("degraded") )
				nodeTemplate.setInitialState(STATE.DEGRADED);
			else if ( node.path("init-state").textValue().equalsIgnoreCase("up") )
				nodeTemplate.setInitialState(STATE.UP);
			else
				nodeTemplate.setInitialState(STATE.UNKNOWN);
			nodeTemplate.setRank(node.path("rank").asInt(0));
			
			JsonNode relatableTos = node.path("relatable-to");
			for ( JsonNode relatableTo : relatableTos ) {
				String name = relatableTo.path("name").textValue();
				Integer max = relatableTo.path("max").asInt(0);
				nodeTemplate.getRelatableTo().put(name, max);
			}
			
			nodeTemplates.add(nodeTemplate);
		}
	}
	
	public ArrayList<NodeTemplate> getNodeTemplates() {
		return nodeTemplates;
	}
	
}
