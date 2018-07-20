package ca.empowered.nms.simulator.controller;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
import io.pkts.PacketHandler;
import io.pkts.Pcap;
import io.pkts.packet.IPv4Packet;
import io.pkts.packet.Packet;
import io.pkts.packet.TCPPacket;
import io.pkts.protocol.Protocol;

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
    private EntityManager entityManager;

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
	
	/**
	 * returns unique nodes based on node-name and node-ip
	 */
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
	
    /**
     * returns unique edges based on edge-node-ids and edge-if-indices
     * @return
     */
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
    
    /**
     * 
     * @param nodeJson
     * @return
     */
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
    	
    	BigInteger nodeId = nodeRepository.findNodeIdForIp(nodeJson.getIp());
    	if (nodeId != null) {
    		jsonResponse.setStatus(STATUS.FAIL);
    		jsonResponse.setMessage("a node with given ip already exists");
    		jsonResponse.setMetadata("{'id':'"+nodeId+"'}");
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
    	
    	BigInteger edgeId = edgeRepository.findEdgeId(nodeIdA, edgeJson.getIfIndexA(), nodeIdB, edgeJson.getIfIndexB());
    	if (edgeId != null) {
    		jsonResponse.setStatus(STATUS.FAIL);
    		jsonResponse.setMessage("an edge with given details already exists");
    		jsonResponse.setMetadata("{'id':'"+edgeId+"'}");
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
    
    @RequestMapping(value="/topology-data", method=RequestMethod.GET)
    public void getTopologyData(HttpServletResponse response) {
    	log.debug("exporting topology data");

    	try {
    		StringBuilder stringBuilder = new StringBuilder();
    		stringBuilder.append("{\n");
    		
    		stringBuilder.append("\t'nodes':[\n");
    		
    		Iterable<Node> nodes = getNodes();
    		nodes.forEach(node -> {
    			stringBuilder.append("\t\t{\n");
        		stringBuilder.append("\t\t\t'name':'"+node.getName()+"',\n");
        		stringBuilder.append("\t\t\t'ip':'"+node.getIp()+"'\n");
        		stringBuilder.append("\t\t},\n");
    		});
    		
    		stringBuilder.append("\t],\n");
    		
    		stringBuilder.append("\t'edges':[\n");
    		
    		Iterable<Edge> edges = getEdges();
    		edges.forEach(edge -> {
    			try {
		    		stringBuilder.append("\t\t{\n");
		    		stringBuilder.append("\t\t\t'nodeIpA':'"+nodeRepository.findById(
		    				edge.getNodeIdA()).get().getIp()+"',\n");
		    		stringBuilder.append("\t\t\t'nodeIpB':'"+nodeRepository.findById(
		    				edge.getNodeIdB()).get().getIp()+"',\n");
		    		stringBuilder.append("\t\t\t'ifIndexA':'"+edge.getIfIndexA()+"',\n");
		    		stringBuilder.append("\t\t\t'ifIndexB':'"+edge.getIfIndexB()+"'\n");
		    		stringBuilder.append("\t\t},\n");
    			} catch (Exception e) {
    				log.error(e.getMessage(), e);
    			}
	    	});
    	
    		stringBuilder.append("\t]\n");
    		
    		stringBuilder.append("}");
    		response.setContentType("text/plain");
    		FileCopyUtils.copy(stringBuilder.toString().replaceAll("'", "\"").replaceAll(",\\s+]", "]").getBytes(), 
    				response.getOutputStream());
			response.flushBuffer();
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
    }
    
    @RequestMapping(value="/pcap-file", method=RequestMethod.POST)
    public @ResponseBody JsonResponse postTopologyData(@RequestParam("file") MultipartFile file) {
    	log.debug("posting topology data");
    	
    	JsonResponse jsonResponse = new JsonResponse();
		jsonResponse.setStatus(STATUS.PASS);
		jsonResponse.setMessage("file uploaded successfully");
		jsonResponse.setMetadata("{'file':'"+file.getName()+"'}");
		
		try {
			final ArrayList<String> knownConnections = new ArrayList<>();
			final ArrayList<String> knownNodes = new ArrayList<>();
			
			final Pcap pcap = Pcap.openStream(file.getInputStream());
			
	        pcap.loop(new PacketHandler() {
	            @Override
	            public boolean nextPacket(final Packet packet) throws IOException {
	
	                if (packet.hasProtocol(Protocol.IPv4)) {
	                    IPv4Packet ipv4Packet = (IPv4Packet)packet.getPacket(Protocol.IPv4);
	                    String name = ipv4Packet.getName();
	                    String sourceIP = ipv4Packet.getSourceIP();
	                    String destinationIP = ipv4Packet.getDestinationIP();
	                    
	                    if (packet.hasProtocol(Protocol.TCP)) {
	                        TCPPacket tcpPacket = (TCPPacket)ipv4Packet.getPacket(Protocol.TCP);
	                        String sourcePort = String.valueOf(tcpPacket.getSourcePort());
	                        String destinationPort = String.valueOf(tcpPacket.getDestinationPort());
		                    
	                        if (destinationPort.equals("80")
	                                || destinationPort.equals("443")) {
	                        	/*log.debug(name+" "+sourceIP+":"+sourcePort+"\t\t->\t\t"
	                                +destinationIP+":"+destinationPort);*/

			                    String connectionId = sourceIP+"|"+sourcePort+"|"
			                    						+destinationIP+"|"+destinationPort;
			                    
	                        	if (!knownNodes.contains(sourceIP))
	                        		knownNodes.add(sourceIP);
	                        	if (!knownNodes.contains(destinationIP))
	                        		knownNodes.add(destinationIP);
	                        	if (!knownConnections.contains(connectionId))
	                        		knownConnections.add(connectionId);
	                        }
	                    }
	                }
	
	                return true;
	            }
	        });
	        
	        knownNodes.forEach(nodeIp -> {
	        	Node node = new Node();
	        	node.setName("IPv4-"+nodeIp);
	        	node.setIp(nodeIp);
	        	addNode(node);
	        });
	        
	        knownConnections.forEach(connectionString -> {
	        	String[] parts = connectionString.split("\\|");
	        	String nodeIpA = parts[0];
	        	String ifIndexA = parts[1];
	        	String nodeIpB = parts[2];
	        	String ifIndexB = parts[3];
	        	
	        	//log.debug(connectionString+"\n"+nodeIpA+"\n"+nodeIpB);
	        		        	
	        	Edge edge = new Edge();
	        	edge.setNodeIpA(nodeIpA);
	        	edge.setIfIndexA(ifIndexA);
	        	edge.setNodeIpB(nodeIpB);
	        	edge.setIfIndexB(ifIndexB);
	        	addEdge(edge);
	        });
	        
			jsonResponse.setMessage("file processed successfully");
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			jsonResponse.setStatus(STATUS.FAIL);
			jsonResponse.setMessage("file uploaded successfully but not processed properly.");
		}
    	
		return jsonResponse;
    }

    @RequestMapping(value="/update-node-coordinates", method=RequestMethod.POST)
    public @ResponseBody JsonResponse recomputeNodeCoordinates() {
    	log.debug("starting layout computation");
    	
    	JsonResponse jsonResponse = new JsonResponse();
		jsonResponse.setStatus(STATUS.PASS);
		jsonResponse.setMessage("started the layout compution");
		jsonResponse.setMetadata("{'target':'node server'}");
		
    	HttpURLConnection connection = null;
		try {
			URL url = new URL("http://127.0.0.1:4001/update-coordinates");
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			
		    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		    StringBuilder response = new StringBuilder();
		    String line;
		    while ((line = reader.readLine()) != null) {
		      response.append(line).append('\n');
		    }
		    reader.close();
			    
			//log.debug(response.toString());
			log.debug("started layout rcomputation");
    	} catch (Exception ex) {
    		log.error(ex.getMessage(), ex);
    		if (connection != null)
    			connection.disconnect();

    		jsonResponse.setStatus(STATUS.FAIL);
    		jsonResponse.setMessage("failed to start the layout compution");
    	}
		return jsonResponse;
    }
    
    @RequestMapping(value="/node-coordinates-updated", method=RequestMethod.POST)
    public @ResponseBody JsonResponse pushNodeCoordinates() {
    	log.debug("finished layout recomputation");

		JsonResponse jsonResponse = new JsonResponse();
		jsonResponse.setStatus(STATUS.PASS);
		jsonResponse.setMessage("your notification has been received");
		jsonResponse.setMetadata("{'origin':'node server'}");
    	
    	/*Iterable<Node> nodes = getNodes();
    	StringBuilder nodesString = new StringBuilder();
    	nodesString.append("[");
    	nodes.forEach(node -> {
        	nodesString.append(node.toString()+",");
    	});
    	nodesString.append("]");
    	log.debug(nodes.toString());
    	log.debug(nodesString.toString());*/
		
		Event event = new Event();
    	event.setType(TYPE.NODE_COORDINATES_UPDATED.toString());
		event.setMetadata("{\"origin\":\"node server\"}");
    	eventRepository.save(event);
    	
    	rabbitTemplate.convertAndSend(WebApplication.objectTopicExchangeName, 
    			"ca.empowered.nms.simulator.object.new-edge", 
    			event);
		
		return jsonResponse;
    }
    
}