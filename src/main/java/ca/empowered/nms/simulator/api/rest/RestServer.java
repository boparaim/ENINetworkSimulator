package ca.empowered.nms.simulator.api.rest;

import static spark.Spark.get;
import static spark.Spark.post;

import java.util.Arrays;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ca.empowered.nms.simulator.api.NodeFactory;
import ca.empowered.nms.simulator.config.Settings;
import ca.empowered.nms.simulator.node.Element;
import spark.Request;
import spark.Response;

public class RestServer {

	private static final Logger log = LogManager.getLogger(RestServer.class.getName());
	
	public RestServer() {
		log.debug("INIT -> listening for rest requests on http://" + Settings.getRestServerIP() + ":" + Settings.getRestServerPort()
		+Settings.getRestServerPath());
		// using apache spark
		// by default port is 4567
        post(Settings.getRestServerPath(), (request, response) -> {
            return processWebRequest(request, response).body();
        });
		
		get(Settings.getRestServerPath()+"/get/nodes", (req, res) -> {
            return processWebRequest(req, res).body();
        });
		
		get(Settings.getRestServerPath()+"/new", (req, res) -> {
            return processWebRequest(req, res).body();
        });
	}
	
	private Response processWebRequest(Request request, Response response) {
		response.status(200);
        response.type("application/json");
        
        String[] path = request.pathInfo().split("/");
        path = Arrays.stream(path)
        		.filter(s -> (s != null && !s.isEmpty()))
        		.toArray(String[]::new);
        //ArrayList<String> paths = new ArrayList<String>(Arrays.asList(path));
        /*for (String p: path)
        	log.debug(p);*/
        
        String data = "{}";
        
        switch (path[1]) {
	        case "get":
	        	switch (path[2]) {
		        	case "nodes":
		        		HashMap<String, Element> allNodes = NodeFactory.getAllNodes();
		        		data = "[";
		        		for (Element node : allNodes.values()) {
		        			data += "{\"name\":\""+node.getName()+"\", \"state\":\""+node.getCurrentState()+"\"},";
		        		}

						StringBuffer buffer = new StringBuffer(data);
						data = buffer.reverse().toString().replaceFirst(",", "");
						data = new StringBuffer(data).reverse().toString();

		        		data += "]";
		        		break;
		        	default:
		        		
	        	}
	        	break;
	        case "post":
	        	break;
        	default:
        		data = "{'method':'"+Arrays.toString(path).replaceAll(", ", "")+"', 'status':'ERROR', 'message':'Undefined method.'}";
        }
        
        //JSONObject event = new Event().toJSON();
		log.debug(request.requestMethod() + " request at " + request.pathInfo() + ". Response: {\"event\": [ " + "NULL" + " ] }");
        response.body( data );
		
		return response;
	}
}
