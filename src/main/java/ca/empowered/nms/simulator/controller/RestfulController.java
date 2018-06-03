package ca.empowered.nms.simulator.controller;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import ca.empowered.nms.simulator.app.WebApplication;
import ca.empowered.nms.simulator.db.dao.NodeRepository;
import ca.empowered.nms.simulator.db.model.Node;
import ca.empowered.nms.simulator.json.JsonResponse;
import ca.empowered.nms.simulator.json.JsonResponse.STATUS;

@RestController
@PropertySource({
	"classpath:application.${ca.empowered.nms.simulator.environment}.properties"	
})
public class RestfulController {

	private static final Logger log = LogManager.getLogger(RestfulController.class.getName());
	
	@Autowired
	private Environment environment;
	
	@Value("${ca.empowered.nms.simulator.applicationName}")
	private String applicationName;
	
	public RestfulController() {
		log.debug("RestfulController constructor");
	}
	
	@PostConstruct
	public void postContructor() {
		log.debug("RestfulController post constructor");
	}
	
    @RequestMapping("/test-logging")
    public String testLogging() {
    	System.out.println(applicationName);
    	System.out.println(environment.getProperty("ca.empowered.nms.simulator.applicationVersion"));
    	System.out.println(environment.getProperty("ca.empowered.nms.simulator.frontendSettings"));
    	
    	log.debug(applicationName);
    	log.debug(environment.getProperty("ca.empowered.nms.simulator.applicationVersion"));
    	log.debug(environment.getProperty("ca.empowered.nms.simulator.frontendSettings"));
    	
        return "logging tests";
    }

    @Autowired
	private RabbitTemplate rabbitTemplate;
	
    @RequestMapping("/test-rabbitmq")
    public String testRabbitMQ() {
    	rabbitTemplate.convertAndSend(WebApplication.topicExchangeName, "ca.empowered.nms.simulator.test", 
    			"Hello from RabbitMQ!");
    	
    	return "rabbitmq works";
    }
    
    @Autowired
	private NodeRepository nodeRepository;
    
    @RequestMapping("/test-db")
    public @ResponseBody Iterable<Node> testDb() {
    	Node node = new Node();
    	node.setIp("0.0.0.0");
    	node.setName("node-two");
    	nodeRepository.save(node);
    	
    	return nodeRepository.findAll();
    }
    
    @MessageMapping("/test-websocket")
    @SendTo("/topic/test-websocket-reply")
    public String testWebSocket(
    		@RequestParam String data) throws Exception {
    	log.debug("web socket: "+data);
    	Thread.sleep(5000);
    	return "got "+data;
    }
    
    /**
     * Eg:
     * {
			"name":"node-one",
			"ip":"1.2.3.4"
		}
	 * [{"key":"Content-Type","value":"application/json"}]
	 * 
     * @param nodeJson
     * @return
     */
    @RequestMapping(path="/add-node", method=RequestMethod.POST)
    public @ResponseBody JsonResponse addNode(
    		@RequestBody Node nodeJson) {

		JsonResponse jsonResponse = new JsonResponse();
    	if (nodeJson == null 
    			|| nodeJson.getName() == null
    			|| nodeJson.getName().isEmpty()
    			|| nodeJson.getIp() == null
    			|| nodeJson.getIp().isEmpty()) {
    		jsonResponse.setStatus(STATUS.FAIL);
    		jsonResponse.setMessage("request missing data");
    		jsonResponse.setMetadata("{'required-fields':'name,ip'}");
    		return jsonResponse;
    	}
    	
    	// save in db
    	nodeJson = nodeRepository.save(nodeJson);
    	
    	// send to clients
    	rabbitTemplate.convertAndSend(WebApplication.objectTopicExchangeName, 
    			"ca.empowered.nms.simulator.object.new-node", 
    			nodeJson);
    	
    	log.debug(nodeJson.toString());
    	
    	jsonResponse.setStatus(STATUS.PASS);
    	jsonResponse.setMessage("node added");
    	jsonResponse.setMetadata("{'id':'"+nodeJson.getId()+"','name':'"+nodeJson.getName()+"'}");
    	return jsonResponse;
    }

}