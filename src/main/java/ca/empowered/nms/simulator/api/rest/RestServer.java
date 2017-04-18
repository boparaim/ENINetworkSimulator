package ca.empowered.nms.simulator.api.rest;

import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.put;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.graphstream.graph.Edge;

import ca.empowered.nms.simulator.api.NodeManager;
import ca.empowered.nms.simulator.config.Settings;
import ca.empowered.nms.simulator.event.Notification;
import ca.empowered.nms.simulator.event.NotificationFactory;
import ca.empowered.nms.simulator.node.NodeElement;
import ca.empowered.nms.simulator.utils.Constants.STATE;
import spark.Request;
import spark.Response;

public class RestServer {

	private static final Logger log = LogManager.getLogger(RestServer.class.getName());
	
	public RestServer() {
		log.debug("INIT -> listening for rest requests on http://" + Settings.getRestServerIP() + ":" + Settings.getRestServerPort()
		+Settings.getRestServerPath());
		// using apache spark
		// by default port is 4567
		// options can be set on command line - https://github.com/apache/spark/blob/master/conf/spark-env.sh.template
		
		// GET - show
		// POST - create
		// PUT - update
		// DELETE - remove
		
		// TODO: for development we are doing everything with GET
		// later - remove /get,/set, etc from paths  and get() routes
		
		get(Settings.getRestServerPath()+"/get/help", (request, response) -> {
            return processWebRequest(request, response).body();
        });
		
		get(Settings.getRestServerPath()+"/get/nodes", (request, response) -> {
            return processWebRequest(request, response).body();
        });
		
		get(Settings.getRestServerPath()+"/get/node/:name", (request, response) -> {
            return processWebRequest(request, response).body();
        });
		
		get(Settings.getRestServerPath()+"/get/related-nodes/:name", (request, response) -> {
            return processWebRequest(request, response).body();
        });
		
		get(Settings.getRestServerPath()+"/get/all-underlying-nodes/:name", (request, response) -> {
            return processWebRequest(request, response).body();
        });
		
		get(Settings.getRestServerPath()+"/get/notifications", (request, response) -> {
            return processWebRequest(request, response).body();
        });
		
		get(Settings.getRestServerPath()+"/set/node-state/:name/:state", (request, response) -> {
			return processWebRequest(request, response).body();
		});
		
		get(Settings.getRestServerPath()+"/set/related-nodes-state/:name/:state", (request, response) -> {
			return processWebRequest(request, response).body();
		});
		
        post(Settings.getRestServerPath(), (request, response) -> {
            return processWebRequest(request, response).body();
        });
		
		put(Settings.getRestServerPath()+"/set/node-state/:state", (request, response) -> {
			return processWebRequest(request, response).body();
		});

		delete(Settings.getRestServerPath()+"/", (request, response) -> {
			return processWebRequest(request, response).body();
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
        
        ArrayList<String> allNodes = null;
        HashMap<String, Notification> allNotifications = null;
		String nodeName = null;
		String newState = null;
		StringBuffer buffer = null;
        
        switch (path[1]) {
	        case "get":
	        	switch (path[2]) {
		        	case "help":
		        		data = "Available paths: \n"
		        				+ "/get/help\t\t shows this help\n"
		        				+ "/get/nodes\t\t lists all nodes\n"
		        				+ "/get/node/<node-name>\t\t shows the given node\n"
		        				+ "/get/related-nodes/<node-name>\t\t lists nodes given nodes are related to\n"
		        				+ "/set/node-state/<node-name>/<state>\t\t updates state for the given node\n"
		        				+ "/get/notifications\t\t lists all notifications\n"
		        				+ "/set/related-nodes-state/<node-name>/<state>\n"
		        				+ "/get/all-underlying-nodes/<node-name>\t\t lists nodes given nodes are related including nodes below nodes\n";
		        		break;
		        	case "nodes":
		        		allNodes = NodeManager.getAllNodes();
		        		data = "[";
		        		for (String name : allNodes) {
		        			data += "{\"name\":\""+name+"\", \"state\":\""+NodeManager.getGraph().getNode(name).getAttribute("state")+"\"},";
		        		}

						buffer = new StringBuffer(data);
						data = buffer.reverse().toString().replaceFirst(",", "");
						data = new StringBuffer(data).reverse().toString();

		        		data += "]";
		        		break;
		        		
		        	case "node":
		        		nodeName = request.params(":name");
		        		if (nodeName == null || nodeName.isEmpty())
		        			data = "{\"method\":\""+Arrays.toString(path).replaceAll(", ", ".")+"\", \"status\":\"ERROR\", \"message\":\"This method requires \"name\" of a node.\"}";
		        		else {
		        			allNodes = NodeManager.getAllNodes();
		        			
		        			if ( !allNodes.contains(nodeName) )
		        				data = "{\"method\":\""+Arrays.toString(path).replaceAll(", ", ".")+"\", \"status\":\"ERROR\", \"message\":\"No node found with given name - "+nodeName+"\"}";
		        			else {
		        				data = "{\"name\":\""+nodeName+"\", \"state\":\""+NodeManager.getGraph().getNode(nodeName).getAttribute("state")+"\"}";
		        			}
		        		}
		        		break;
		        		
		        	case "related-nodes":
		        		nodeName = request.params(":name");
		        		if (nodeName == null || nodeName.isEmpty())
		        			data = "{\"method\":\""+Arrays.toString(path).replaceAll(", ", ".")+"\", \"status\":\"ERROR\", \"message\":\"This method requires \"name\" of a node.\"}";
		        		else {
		        			allNodes = NodeManager.getAllNodes();
		        			
		        			if ( !allNodes.contains(nodeName) )
		        				data = "{\"method\":\""+Arrays.toString(path).replaceAll(", ", ".")+"\", \"status\":\"ERROR\", \"message\":\"No node found with given name - "+nodeName+"\"}";
		        			else {
		        				NodeElement requestedNode = NodeManager.getGraph().getNode(nodeName);
		        				data = "{\"count\":\""+requestedNode.getEdgeSet().size()+"\", \"nodes\":[";		        				
		        				for (Edge edge : requestedNode.getEdgeSet()) {
		        					data += "\"" + edge.getOpposite(requestedNode).getId() + "\",";
		        				}

								buffer = new StringBuffer(data);
								data = buffer.reverse().toString().replaceFirst(",", "");
								data = new StringBuffer(data).reverse().toString();
								
		        				data += "]}";
		        			}
		        		}
		        		break;
		        		
		        	case "notifications":
		        		allNotifications = NotificationFactory.getAllNotifications();
		        		data = "[";
		        		for (Notification notification : allNotifications.values()) {
		        			data += notification.toJSON()+",";
		        		}

						buffer = new StringBuffer(data);
						data = buffer.reverse().toString().replaceFirst(",", "");
						data = new StringBuffer(data).reverse().toString();

		        		data += "]";
		        		break;
		        		
		        	case "all-underlying-nodes":
		        		nodeName = request.params(":name");
		        		if (nodeName == null || nodeName.isEmpty())
		        			data = "{\"method\":\""+Arrays.toString(path).replaceAll(", ", ".")+"\", \"status\":\"ERROR\", \"message\":\"This method requires \"name\" of a node.\"}";
		        		else {
		        			allNodes = NodeManager.getAllNodes();
		        			
		        			if ( !allNodes.contains(nodeName) )
		        				data = "{\"method\":\""+Arrays.toString(path).replaceAll(", ", ".")+"\", \"status\":\"ERROR\", \"message\":\"No node found with given name - "+nodeName+"\"}";
		        			else {
		        				NodeElement requestedNode = NodeManager.getGraph().getNode(nodeName);
		        				data = "{\"count\":\""+requestedNode.getEdgeSet().size()+"\", \"nodes\":[";
		        				data = getAllUnderlyingNodes(requestedNode, data);

								buffer = new StringBuffer(data);
								data = buffer.reverse().toString().replaceFirst(",", "");
								data = new StringBuffer(data).reverse().toString();
								
		        				data += "]}";
		        			}
		        		}
		        		break;
		        		
		        	default:
		        		
	        	}
	        	break;
	        case "post":
	        	break;
	        case "set":
	        	switch (path[2]) {
		        	case "node-state":
		        		nodeName = request.params(":name");
		        		newState = request.params(":state");
		        		if (nodeName == null || nodeName.isEmpty() || newState == null || newState.isEmpty())
		        			data = "{\"method\":\""+Arrays.toString(path).replaceAll(", ", ".")+"\", \"status\":\"ERROR\", \"message\":\"This method requires \"name\" of a node.\"}";
		        		else {
		        			allNodes = NodeManager.getAllNodes();
		        			
		        			if ( !allNodes.contains(nodeName) )
		        				data = "{\"method\":\""+Arrays.toString(path).replaceAll(", ", ".")+"\", \"status\":\"ERROR\", \"message\":\"No node found with given name - "+nodeName+"\"}";
		        			else if ( !newState.equalsIgnoreCase("up")
		        					&& !newState.equalsIgnoreCase("down")
		        					&& !newState.equalsIgnoreCase("degraded"))
		        				data = "{\"method\":\""+Arrays.toString(path).replaceAll(", ", ".")+"\", \"status\":\"ERROR\", \"message\":\"Invalid state. Valid values for state are UP|DOWN|DOWNGRADED\"}";
		        			else {
		        				NodeElement requestedNode = NodeManager.getGraph().getNode(nodeName);
		        				if ( newState.equalsIgnoreCase("up") )
		        					requestedNode.setCurrentState(STATE.UP);
		        				else if ( newState.equalsIgnoreCase("down") )
		        					requestedNode.setCurrentState(STATE.DOWN);
		        				else if ( newState.equalsIgnoreCase("degraded") )
		        					requestedNode.setCurrentState(STATE.DEGRADED);
		        				data = "{\"method\":\""+Arrays.toString(path).replaceAll(", ", ".")+"\", \"status\":\"SUCCESS\", \"message\":\"State for "+requestedNode.getId()
		        						+" updated to "+requestedNode.getCurrentState().toString()+"\"}";
		        			}
		        		}
		        		
		        		break;
		        		
		        	case "related-nodes-state":
		        		nodeName = request.params(":name");
		        		newState = request.params(":state");
		        		if (nodeName == null || nodeName.isEmpty() || newState == null || newState.isEmpty())
		        			data = "{\"method\":\""+Arrays.toString(path).replaceAll(", ", ".")+"\", \"status\":\"ERROR\", \"message\":\"This method requires \"name\" of a node.\"}";
		        		else {
		        			/*allNodes = NodeManager.getAllNodes();
		        			
		        			if ( !allNodes.containsKey(nodeName) )
		        				data = "{\"method\":\""+Arrays.toString(path).replaceAll(", ", ".")+"\", \"status\":\"ERROR\", \"message\":\"No node found with given name - "+nodeName+"\"}";
		        			else if ( !newState.matches("[Uu][Pp]|[Dd][Oo][Ww][Nn]") )
		        				data = "{\"method\":\""+Arrays.toString(path).replaceAll(", ", ".")+"\", \"status\":\"ERROR\", \"message\":\"Invalid state. Valid values for state are UP|DOWN\"}";
		        			else {
		        				Element requestedNode = allNodes.get(nodeName);
		        				data = "{\"method\":\""+Arrays.toString(path).replaceAll(", ", ".")+"\", \"status\":\"SUCCESS\", \"message\":\"States updated\", \"nodes\":[";		        				
		        				
		        				if ( newState.matches("[Uu][Pp]") )
		        					requestedNode.setCurrentState(STATE.UP);
		        				if ( newState.matches("[Dd][Oo][Ww][Nn]") )
		        					requestedNode.setCurrentState(STATE.DOWN);
		        				data += "{\"name\":\"" + requestedNode.getName() + "\", \"state\":\""+requestedNode.getCurrentState()+"\"},";
		        				GraphManager.updateNodeState(requestedNode, requestedNode.getCurrentState());
		        				
		        				data = setStateForRelatedNodes(requestedNode, newState, data);

								buffer = new StringBuffer(data);
								data = buffer.reverse().toString().replaceFirst(",", "");
								data = new StringBuffer(data).reverse().toString();
								
		        				data += "]}";
			        			
		        			}*/
		        		}
		        		
		        		break;
		        		
	        		default:
	        			
	        	}
	        	break;
	        case "delete":
	        	break;
        	default:
        		data = "{\"method\":\""+Arrays.toString(path).replaceAll(", ", ".")+"\", \"status\":\"ERROR\", \"message\":\"Undefined method.\"}";
        }
        
        //JSONObject event = new Event().toJSON();
		log.debug(request.requestMethod() + " request at " + request.pathInfo() + " Response: " + data);
        response.body( data );
		
		return response;
	}
	
	public static String getAllUnderlyingNodes(NodeElement node, String data) {
		for (Edge edge : node.getEdgeSet()) {
			NodeElement otherNode = edge.getOpposite(node);
			if ( Integer.parseInt(otherNode.getAttribute("rank").toString()) < Integer.parseInt(node.getAttribute("rank").toString()) ) {
				data += "\"" + otherNode.getId() + "\",";
				
				data = getAllUnderlyingNodes(otherNode, data);
			}
		}
		
		return data;
	}
	
	public static String setStateForRelatedNodes(NodeElement element, String newState, String data) {
		/*for (Relationship relationship : element.relationships) {
			Element otherNode = relationship.getOtherNode(element);
			if ( otherNode.level < element.level ) {
				if ( newState.matches("[Uu][Pp]") )
					otherNode.setCurrentState(STATE.UP);
				if ( newState.matches("[Dd][Oo][Ww][Nn]") )
					otherNode.setCurrentState(STATE.DOWN);
				data += "{\"name\":\"" + otherNode.getName() + "\", \"state\":\""+otherNode.getCurrentState()+"\"},";
				GraphManager.updateNodeState(otherNode, otherNode.getCurrentState());
			
				log.debug("===== "+element.level+" other "+otherNode.level);
				data = setStateForRelatedNodes(otherNode, newState, data);
			}
		}*/
		
		return data;
	}
	
	public static void sendJSON(String jsonPayload) {
				
		// TODO: this will cause an issue when # of objects goes above few Ks, use executor instead
		new Thread() {
			@Override
			public void run() {
				super.run();
				try {
					URL url = new URL(Settings.getRestDestinationURL());
			
					HttpURLConnection con = (HttpURLConnection) url.openConnection();
					con.setDoOutput(true);
					con.setDoInput(true);
					con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
					con.setRequestProperty("Accept", "application/json");
					con.setRequestMethod("POST");
			
					log.debug("sending HTTP request with JSON : "+jsonPayload);
			
					OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
					wr.write(jsonPayload);
					wr.flush();
					
					//display what returns the POST request	
					StringBuilder sb = new StringBuilder();  
					int HttpResult = con.getResponseCode(); 
					if (HttpResult == HttpURLConnection.HTTP_OK) {
					    BufferedReader br = new BufferedReader(
					            new InputStreamReader(con.getInputStream(), "utf-8"));
					    String line = null;  
					    while ((line = br.readLine()) != null) {  
					        sb.append(line + "\n");  
					    }
					    br.close();
					    log.debug("received response: " + sb.toString());  
					} else {
						log.warn(con.getContent());
						log.warn(con.getResponseMessage());  
						log.warn(con.toString());
					}
				} catch (Exception e) {
					log.debug(e.getMessage(), e);
				}
			}
		}.start();		
	}
}
