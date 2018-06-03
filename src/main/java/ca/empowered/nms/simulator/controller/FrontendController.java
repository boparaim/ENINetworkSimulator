package ca.empowered.nms.simulator.controller;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ca.empowered.nms.simulator.app.WebApplication;
import ca.empowered.nms.simulator.db.dao.EdgeRepository;
import ca.empowered.nms.simulator.db.dao.EventRepository;
import ca.empowered.nms.simulator.db.dao.NodeRepository;
import ca.empowered.nms.simulator.db.model.Edge;
import ca.empowered.nms.simulator.db.model.Event;
import ca.empowered.nms.simulator.db.model.Event.TYPE;
import ca.empowered.nms.simulator.db.model.Node;
import ca.empowered.nms.simulator.json.JsonResponse;
import ca.empowered.nms.simulator.json.JsonResponse.STATUS;
import ca.empowered.nms.simulator.json.JsonTopologyStats;

@RestController
@PropertySource({
	"classpath:application.${ca.empowered.nms.simulator.environment}.properties"	
})
@RequestMapping("/fe")
public class FrontendController {

	private static final Logger log = LogManager.getLogger(FrontendController.class.getName());
	
	/*@Autowired
	private Environment environment;*/

    @Autowired
	private RabbitTemplate rabbitTemplate;
    
    @Autowired
	private NodeRepository nodeRepository;
    
    @Autowired
	private EdgeRepository edgeRepository;
    
    @Autowired
    private EventRepository eventRepository;
	
	@Value("${ca.empowered.nms.simulator.applicationName}")
	private String applicationName;
	
	private ObjectMapper objectMapper = new ObjectMapper();
	
	public FrontendController() {
		log.debug("FrontendController constructor");
	}
	
	@PostConstruct
	public void postContructor() {
		log.debug("FrontendController post constructor");
	}
	
    @RequestMapping(value="/nodes", method=RequestMethod.GET)
    public @ResponseBody Iterable<Node> getNodes() {
    	log.debug("getting nodes");
    	ArrayList<String> nodeIdentifiers = new ArrayList<>();
    	ArrayList<Node> uniqueNodes = new ArrayList<>();
    	nodeRepository.findAll().forEach(node -> {
    		String identifier = ""+node.getName()+node.getIp();
    		if (nodeIdentifiers.contains(identifier)) {
				// do nothing
			} else {
				nodeIdentifiers.add(identifier);
				uniqueNodes.add(node);
			}
    	});
    	
    	return uniqueNodes;
    }
	
    @RequestMapping(value="/edges", method=RequestMethod.GET)
    public @ResponseBody Iterable<Edge> getEdges() {
    	log.debug("getting edges");
    	ArrayList<String> edgeIdentifiers = new ArrayList<>();
    	ArrayList<Edge> uniqueEdges = new ArrayList<>();
    	edgeRepository.findAll().forEach(edge -> {
    		String identifier = ""+edge.getNodeIdA()+"-"+edge.getIfIndexA()
    							+"-"+edge.getNodeIdB()+"-"+edge.getIfIndexB();
    		if (edgeIdentifiers.contains(identifier)) {
				// do nothing
			} else {
				edgeIdentifiers.add(identifier);
				uniqueEdges.add(edge);
			}
    	});
    	
    	return uniqueEdges;
    }
    
    @RequestMapping(path="/node", method=RequestMethod.PUT)
    public @ResponseBody JsonResponse addNode(
    		@RequestBody Node nodeJson) {
    	log.debug("adding node "+nodeJson.toString());

		JsonResponse jsonResponse = new JsonResponse();
    	if (nodeJson == null 
    			|| nodeJson.getName() == null
    			|| nodeJson.getName().isEmpty()
    			|| nodeJson.getIp() == null
    			|| nodeJson.getIp().isEmpty()) {
    		jsonResponse.setStatus(STATUS.FAIL);
    		jsonResponse.setMessage("request is missing required data");
    		jsonResponse.setMetadata("{'required-fields':'name,ip'}");
    		return jsonResponse;
    	}
    	
    	// save in db
    	nodeJson = nodeRepository.save(nodeJson);
    	
    	Event event = new Event();
    	event.setType(TYPE.NODE_CREATED.toString());
    	try {
			event.setMetadata(objectMapper.writeValueAsString(nodeJson));
		} catch (JsonProcessingException e) {
			log.error(e);
			event.setMetadata(e.getMessage());
		}
    	eventRepository.save(event);
    	    	
    	// send to clients
    	rabbitTemplate.convertAndSend(WebApplication.objectTopicExchangeName, 
    			"ca.empowered.nms.simulator.object.new-node", 
    			event);
    	
    	jsonResponse.setStatus(STATUS.PASS);
    	jsonResponse.setMessage("node added");
    	jsonResponse.setMetadata("{'id':'"+nodeJson.getId()+"','name':'"+nodeJson.getName()+"'}");
    	return jsonResponse;
    }
    
    @RequestMapping(path="/edge", method=RequestMethod.PUT)
    public @ResponseBody JsonResponse addEdge(
    		@RequestBody Edge edgeJson) {
    	log.debug("adding edge "+edgeJson.toString());

		JsonResponse jsonResponse = new JsonResponse();
    	if (edgeJson == null
    			|| edgeJson.getIfIndexA() == null
    			|| edgeJson.getIfIndexA().isEmpty()
    			|| edgeJson.getIfIndexB() == null
    			|| edgeJson.getIfIndexB().isEmpty()
    			|| edgeJson.getNodeIpA() == null
    			|| edgeJson.getNodeIpA().isEmpty()
    			|| edgeJson.getNodeIpB() == null
    			|| edgeJson.getNodeIpB().isEmpty()) {
    		jsonResponse.setStatus(STATUS.FAIL);
    		jsonResponse.setMessage("request is missing required data");
    		jsonResponse.setMetadata("{'required-fields':'nodeIpA,nodeIpB,ifIndexA,ifIndexB'}");
    		return jsonResponse;
    	}
    	
    	BigInteger nodeIdA = nodeRepository.findNodeIdForIp(edgeJson.getNodeIpA());
    	BigInteger nodeIdB = nodeRepository.findNodeIdForIp(edgeJson.getNodeIpB());
    	edgeJson.setNodeIdA(nodeIdA);
    	edgeJson.setNodeIdB(nodeIdB);
    	
    	if (edgeJson.getNodeIdA() == null
    			|| edgeJson.getNodeIdB() == null) {
    		jsonResponse.setStatus(STATUS.FAIL);
    		jsonResponse.setMessage("defined nodes are not present in DB");
    		jsonResponse.setMetadata("{'nodeIpA':'"+edgeJson.getNodeIpA()+"','nodeIpB':'"+edgeJson.getNodeIpB()+"'}");
    		return jsonResponse;
    	}
    	
    	log.debug("-> "+edgeJson.toString());
    	
    	// save in db
    	edgeJson = edgeRepository.save(edgeJson);
    	
    	Event event = new Event();
    	event.setType(TYPE.EDGE_CREATED.toString());
    	try {
			event.setMetadata(objectMapper.writeValueAsString(edgeJson));
		} catch (JsonProcessingException e) {
			log.error(e);
			event.setMetadata(e.getMessage());
		}
    	eventRepository.save(event);
    	
    	// send to clients
    	rabbitTemplate.convertAndSend(WebApplication.objectTopicExchangeName, 
    			"ca.empowered.nms.simulator.object.new-edge", 
    			event);
    	
    	jsonResponse.setStatus(STATUS.PASS);
    	jsonResponse.setMessage("edge added");
    	jsonResponse.setMetadata("{'id':'"+edgeJson.getId()
    							+"','nodeIdA':'"+edgeJson.getNodeIdA()
    							+"','nodeIdB':'"+edgeJson.getNodeIdB()+"'}");
    	return jsonResponse;
    }
    
    @RequestMapping(value="/stats", method=RequestMethod.GET)
    public @ResponseBody JsonTopologyStats getStats() {
    	log.debug("getting stats");
    	
    	JsonTopologyStats stats = new JsonTopologyStats();
    	stats.setTotalNodes(nodeRepository.getTotalNodeCount());
    	stats.setTotalEdges(edgeRepository.getTotalEdgeCount());
    	stats.setUniqueNodes(((ArrayList<Node>)getNodes()).size());
    	stats.setUniqueEdges(((ArrayList<Edge>)getEdges()).size());
    	
    	return stats;
    }
	
    /*{
    	"nodes":[
    	{
    	"name":"node a",
    	"ip":"1.0.0.1"
    	},
    	{
    	"name":"node b",
    	"ip":"1.0.0.2"
    	}
    	],
    	"edges":[
    	{
    	"nodeIpA":"1.0.0.1",
    	"nodeIpB":"1.0.0.2",
    	"ifIndexA":"1",
    	"ifIndexB":"2"
    	}
    	]
    	}*/
    @RequestMapping(value="/topology-data", method=RequestMethod.POST)
    public @ResponseBody JsonResponse postTopologyData(@RequestBody(required=false) String datasetJsonString) {
    	log.debug("posting topology data");

		JsonResponse jsonResponse = new JsonResponse();
		
		boolean nullInput = false;
		boolean invalidInput = false;
		
		int nodesAdded = 0;
		int edgesAdded = 0;
		
    	if (datasetJsonString == null
    			|| datasetJsonString.isEmpty()) {
    		nullInput = true;
    	}
    	
		try {
	    	String utfJsonString = java.net.URLDecoder.decode(datasetJsonString, "UTF-8");
			JsonNode rootNode = objectMapper.readTree(utfJsonString);
			
			if (rootNode == null) {
	    		nullInput = true;
	    	}
			
			boolean rootNodeIsObject = false;
			try { rootNodeIsObject = rootNode.isObject(); } catch (Exception ex) {}
			
			if (rootNodeIsObject
					&& rootNode.has("nodes")
					&& rootNode.has("edges")) {
				JsonNode nodes = rootNode.get("nodes");
				JsonNode edges = rootNode.get("edges");
				boolean nodesIsArray = false;
				boolean edgesIsArray = false;
				try { nodesIsArray = nodes.isArray(); } catch (Exception ex) {}
				try { edgesIsArray = edges.isArray(); } catch (Exception ex) {}
				if (nodesIsArray && edgesIsArray) {
					log.debug(rootNode.toString());
					for (JsonNode node: nodes) {
						if (node.has("name")
								&& node.has("ip")) {
							log.debug(node.toString());
							Node nodeObj = new Node();
							nodeObj.setName(node.get("name").asText());
							nodeObj.setIp(node.get("ip").asText());
							
							JsonResponse jsonResponse2 = addNode(nodeObj);
							if (jsonResponse2.getStatus().equals(STATUS.PASS.toString()))
								nodesAdded++;
							else
								log.debug("unable to add node "+node.toString());
						} else {
							invalidInput = true;
							break;
						}
					};
					for (JsonNode edge: edges) {
						if (edge.has("nodeIpA")
								&& edge.has("nodeIpB")
								&& edge.has("ifIndexA")
								&& edge.has("ifIndexB")) {
							log.debug(edge.toString());
							Edge edgeObj = new Edge();
							edgeObj.setNodeIpA(edge.get("nodeIpA").asText());
							edgeObj.setNodeIpB(edge.get("nodeIpB").asText());
							edgeObj.setIfIndexA(edge.get("ifIndexA").asText());
							edgeObj.setIfIndexB(edge.get("ifIndexB").asText());

							JsonResponse jsonResponse2 = addEdge(edgeObj);
							if (jsonResponse2.getStatus().equals(STATUS.PASS.toString()))
								edgesAdded++;
							else
								log.debug("unable to add edge "+edge.toString());
						} else {
							invalidInput = true;
							break;
						}
					};
				} else {
					invalidInput = true;
				}
			} else {
				invalidInput = true;
			}
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			invalidInput = true;
		}
		
		String expectedFormat = "{'expected-format':'{'nodes':[{'name':'','ip':''}],'edges':[{'nodeIpA':'','nodeIpB':'','ifIndexA':'','ifIndexB':''}]}'}".replaceAll("'", "\"");
		if (nullInput) {
    		jsonResponse.setStatus(STATUS.FAIL);
    		jsonResponse.setMessage("no data found in your request");
    		jsonResponse.setMetadata(expectedFormat);
    	}
		else if (invalidInput) {
			jsonResponse.setStatus(STATUS.FAIL);
    		jsonResponse.setMessage("invalid data found in your request");
    		jsonResponse.setMetadata(expectedFormat);
		}
		else {
			jsonResponse.setStatus(STATUS.PASS);
    		jsonResponse.setMessage("data imported successfully");
    		jsonResponse.setMetadata("{'nodes-added':"+nodesAdded+",'edges-added':"+edgesAdded+"}");
		}
		
    	return jsonResponse;
    }
    
    @Autowired
    private EntityManager entityManager;
	
    @Transactional
    @RequestMapping(value="/topology-data", method=RequestMethod.DELETE)
    public @ResponseBody JsonResponse deleteTopologyData(@RequestBody(required=false) String datasetJsonString) {
    	log.debug("deleting topology data");

    	int nodesDeleted = nodeRepository.getTotalNodeCount();
    	int edgesDeleted = edgeRepository.getTotalEdgeCount();
    	int eventsDeleted = eventRepository.getTotalEventCount();
    	
    	JsonResponse jsonResponse = new JsonResponse();
		jsonResponse.setStatus(STATUS.PASS);
		jsonResponse.setMessage("data deleted successfully");
		jsonResponse.setMetadata("{'nodes-deleted':"+nodesDeleted
				+",'edges-deleted':"+edgesDeleted+",'events-deleted':"+eventsDeleted+"}");

		edgeRepository.deleteAll();
		nodeRepository.deleteAll();
		eventRepository.deleteAll();
		
		entityManager.joinTransaction();
		entityManager.createNativeQuery("alter table node AUTO_INCREMENT = 1").executeUpdate();
		entityManager.createNativeQuery("alter table edge AUTO_INCREMENT = 1").executeUpdate();
		entityManager.createNativeQuery("alter table event AUTO_INCREMENT = 1").executeUpdate();
		
		Event event = new Event();
    	event.setType(TYPE.TOPOLOGY_DELETED.toString());
		event.setMetadata("{\"origin\":\"from front-end\"}");
    	eventRepository.save(event);
		
		// send to clients
    	rabbitTemplate.convertAndSend(WebApplication.objectTopicExchangeName, 
    			"ca.empowered.nms.simulator.object.topology-delete", 
    			event);
		
    	return jsonResponse;
    }
    
}