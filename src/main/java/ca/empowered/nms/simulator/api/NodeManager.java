package ca.empowered.nms.simulator.api;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.graphstream.algorithm.ConnectedComponents;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.NodeFactory;
import org.graphstream.graph.implementations.AbstractGraph;
import org.graphstream.graph.implementations.AdjacencyListGraph;
import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.Units;
import org.graphstream.ui.swingViewer.DefaultView;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.Viewer.CloseFramePolicy;

import ca.empowered.nms.simulator.config.Settings;
import ca.empowered.nms.simulator.event.EventObserver;
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
		graph.addAttribute("ui.title", Settings.getAppName());
		
		// improve visual quality
		if (Settings.isUiAntiAlias())
			graph.addAttribute("ui.quality");
		if (Settings.isUiQuality())
			graph.addAttribute("ui.antialias");
		
		// stylesheet
		graph.addAttribute("ui.stylesheet", "url('"+Settings.getCssStyleSheet()+"')");
		
		graph.setNodeFactory(new NodeFactory<NodeElement>() {
			public NodeElement newInstance(String id, Graph graph) {
				return new NodeElement((AbstractGraph) graph, id);
			}
		});
		
		// graph -> viewer -> view
		//				   -> renderer -> camera

		// show GUI ?
		if (Settings.isDisplayGUI()) {
			Viewer viewer = graph.display();
			DefaultView view = ((DefaultView)viewer.getView(Viewer.DEFAULT_VIEW_ID));
			view.getCamera().setViewCenter(0, 0, 0);

			if (!Settings.isGuiClosesApp()) {
				viewer.setCloseFramePolicy(CloseFramePolicy.CLOSE_VIEWER);
			}

			
			
			view.addMouseWheelListener(new MouseWheelListener() {				
				@Override
				public void mouseWheelMoved(MouseWheelEvent e) {
					//log.debug("mouse wheel event: "+e.getWheelRotation()+" "+e.getScrollAmount());
					
					// 1x to nx
					double currentViewPercent = view.getCamera().getViewPercent();
					double newViewPercent = currentViewPercent;
					double temp = currentViewPercent + (e.getWheelRotation() * Settings.getUiZoomFactor());
					if ( temp > 0 && temp < 2 ) {
						newViewPercent = temp;
						//log.debug("current: "+currentViewPercent+" new: "+newViewPercent);
					}
					view.getCamera().setViewPercent(newViewPercent);
					
					double width = view.getSize().getWidth();
					double height = view.getSize().getHeight();
					double xCenter = width / 2;
					double yCenter = height  / 2;
					double x = e.getX();						
					double y = e.getY();
					double xFromCenter = e.getX() - xCenter;						
					double yFromCenter = -e.getY() + yCenter;
					double cameraWidth = view.getCamera().getMetrics().getSize().x();
					double cameraHeight = view.getCamera().getMetrics().getSize().y();
					double cameraRatioX = width/cameraWidth;
					double cameraRatioY = width/cameraHeight;
					//double cameraWidth = view.getCamera().getMetrics().lengthToPx(view.getCamera().getMetrics().graphWidthGU(), Units.GU);
					//double cameraHeight = view.getCamera().getMetrics().lengthToPx(view.getCamera().getMetrics().graphHeightGU(), Units.GU);
					//log.debug(x*view.getCamera().getMetrics().ratioPx2Gu+" x "+y*view.getCamera().getMetrics().ratioPx2Gu+" ("+cameraWidth+","+cameraHeight+")");
					//log.debug(xCenter+" x "+yCenter+" ("+e.getX()+","+e.getY()+")");
					// these 2 give have width and height
					log.debug(width+","+height);
					log.debug(Arrays.toString(view.getCamera().getMetrics().viewport));
					log.debug(xCenter+" x "+yCenter);
					log.debug(x+","+y);
					log.debug(xFromCenter+","+yFromCenter);
					log.debug(cameraWidth+","+cameraHeight);
					log.debug(cameraRatioX+","+cameraRatioY);
					
					view.getCamera().setViewCenter(xFromCenter/cameraRatioX, yFromCenter/cameraRatioY, 0);

					/*double width = view.getSize().width;
					double height = view.getSize().height;
					double xcenter = width/2;
					double ycenter = height/2;
					double xratio = width/cameraWidth;
					double yratio = height/cameraHeight;
					double boundWidth = view.getBounds().getWidth();
					double boundHeight = view.getBounds().getHeight();
					log.debug("frame: "+width+","+height+"\n"
							+"frame center: "+xcenter+","+ycenter+"\n"
							+"camera: "+cameraWidth+","+cameraHeight+"\n"
							+"bound: "+boundWidth+","+boundHeight+"\n"
							+"ratio: "+xratio+"/"+yratio);
					log.debug("\npointing at: "+e.getX()+","+e.getY()+"\n"
							+" pointing at on screen: "+e.getXOnScreen()+","+e.getYOnScreen()+"\n"
							+" frame size: "+view.size().width+","+view.size().height+"\n"
							+" camera pointing at: "+view.getCamera().getViewCenter().x+","+view.getCamera().getViewCenter().y+"\n"
							+" camera diagonal: "+view.getCamera().getGraphDimension()+"\n"
							+" camera size: "+view.getCamera().getMetrics().getSize().x()+","+view.getCamera().getMetrics().getSize().y()+"\n"
							+" camera low point: "+view.getCamera().getMetrics().getLowPoint().x+","+view.getCamera().getMetrics().getLowPoint().y+"\n"
							+" camera high point: "+view.getCamera().getMetrics().getHighPoint().x+","+view.getCamera().getMetrics().getHighPoint().y+"\n"
							+" camera size pixel: "+view.getCamera().getMetrics().lengthToPx(view.getCamera().getMetrics().getSize().x(), Units.GU)
							+","+view.getCamera().getMetrics().lengthToPx(view.getCamera().getMetrics().getSize().y(), Units.GU)+"\n"
							+" camera diagonal pixel : "+view.getCamera().getMetrics().lengthToPx(view.getCamera().getGraphDimension(), Units.GU)+"\n");*/
					//log.debug("point: "+view.getCamera().getViewCenter().toString());
					//log.debug("area: "+view.size().toString());
					//if (newViewPercent < 1.0)
				}
			});
		}
		
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
				log.info(nodeTemplate.getName()+": this template is disabled");
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
		// for css property manipulation
		node.addAttribute("class", className);
		
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
		
		// for a large data set, use multiple threads
		if (allNodes.size() > 5000) {
			graph.getNodeSet()
				.parallelStream()
				.forEach(thisNode -> {
					//log.info("1 processing: "+Thread.currentThread().getName()+" -> "+thisNode.getId());
					
					graph.getNodeSet()
					.parallelStream()
					.forEach(otherNode -> {
						//log.info("2 processing: "+Thread.currentThread().getName()+" -> "+thisNode.getId()+" :: "+otherNode.getId());
						NodeElement thisNodeElement = (NodeElement)thisNode;
						NodeElement otherNodeElement = (NodeElement)otherNode;
	
						if ( thisNodeElement.isRelatableTo(otherNodeElement) ) {
							graph.addEdge(thisNode.getId()+"."+otherNode.getId(), thisNode.getId(), otherNode.getId());
							
							log.debug("connected: node1: " + thisNode.getId() + " node2: " + otherNode.getId());
							synchronized (disconnectedNodes) {
								if (disconnectedNodes.contains(thisNode.getId())) {
									disconnectedNodes.remove(thisNode.getId());
								}
								if (disconnectedNodes.contains(otherNode.getId())) {
									disconnectedNodes.remove(otherNode.getId());
								}								
							}
						} else {
							//log.debug("invalid relationship: node1: " + thisNode.getId() + " node2: " + otherNode.getId());
						}
					});
				});
		}
		// for a small data set, use single thread
		else {
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
						//log.debug("invalid relationship: node1: " + thisNode.getId() + " node2: " + otherNode.getId());
					}
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

	public static Graph getGraph() {
		return graph;
	}	
}
