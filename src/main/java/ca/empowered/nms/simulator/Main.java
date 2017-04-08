package ca.empowered.nms.simulator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.stream.file.FileSinkImages;
import org.graphstream.stream.file.FileSinkImages.LayoutPolicy;
import org.graphstream.stream.file.FileSinkImages.OutputType;
import org.graphstream.stream.file.FileSinkImages.Resolutions;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteManager;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewerListener;
import org.graphstream.ui.view.ViewerPipe;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import ca.empowered.nms.simulator.api.NodeFactory;
import ca.empowered.nms.simulator.node.Element;
import ca.empowered.nms.simulator.node.Host;

public class Main {

	private static final Logger log = LogManager.getLogger(Main.class.getName());
	
	public static void main(String[] args) {
		
		log.info("INIT: ENINetworkSimulator");
		
		ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
		
		String stylesheet = 
				"node.marked { "
				+ "    fill-color: green; "
				+ "    size: 22px; "
				+ "    stroke-mode: plain; "
				+ "    stroke-color: blue;"
				+ "} "
				+ "node { "
				+ "	   fill-color: rgb(255,0,0); "
				+ "}"
				+ "edge { "
				+ "    shape: line; "
				+ "    fill-mode: dyn-plain; "
				+ "    fill-color: #222; "
				+ "    arrow-size: 3px, 2px; "
				+ "}"
				+ "sprite {	"
				+ "    shape: box; "
				+ "    size: 20px, 20px; "
				+ "    fill-mode: image-scaled; "
				+ "    fill-image: url('screenshot.png'); "
				+ "}";
		
		Graph graph = new SingleGraph("Tutorial 1");
		/*graph.addNode("A" );
		graph.addNode("B" );
		graph.addNode("C" );*/
		
		// to add graph to your UI
		/*Viewer viewer = new Viewer(graph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
		View view = viewer.addDefaultView(false);   // false indicates "no JFrame".
		myJFrame.add(view);*/
		
		graph.setStrict(false);
		graph.setAutoCreate( true );
		
		// improve quality
		graph.addAttribute("ui.quality");
		graph.addAttribute("ui.antialias");
		
		// take screenshot
		// this doesn't work
		//graph.addAttribute("ui.screenshot", "/screenshot.png");
		// so...
		//FileSinkImages pic = new FileSinkImages("prefix-", OutputType.PNG, Resolutions.VGA, OutputPolicy.BY_GRAPH_EVENT);
		FileSinkImages pic = new FileSinkImages(OutputType.PNG, Resolutions.VGA);
		 
		pic.setLayoutPolicy(LayoutPolicy.COMPUTED_FULLY_AT_NEW_IMAGE);
		
		// to resize the window
		Viewer viewer = graph.display();
		
		// in graph nodes connected by an edge tend to attract and other nodes tend to repel each other
		// this default behavior can be changed
		viewer.disableAutoLayout();
		viewer.enableAutoLayout();
		
		// don't quit the program when graph UI is closed
		//viewer.setCloseFramePolicy(Viewer.CloseFramePolicy.HIDE_ONLY);
		
		// to get the events form view in graph
		ViewerPipe fromViewer = viewer.newViewerPipe();
		fromViewer.addViewerListener(new ViewerListener() {			
			@Override
			public void viewClosed(String viewName) {
				log.error("UI event: closed");
			}
			
			@Override
			public void buttonReleased(String id) {
				log.error("UI event: released");
			}
			
			@Override
			public void buttonPushed(String id) {
				log.error("UI event: pushed");
			}
		});
		fromViewer.addSink(graph);
		fromViewer.pump();
		
		graph.addEdge("AB", "A", "B");
		graph.addEdge("BC", "B", "C");
		graph.addEdge("CA", "C", "A");
		
		/*Viewer viewer = graph.display(false);*/
		View view = viewer.getDefaultView();
		//((ViewPanel) view).resizeFrame(1000, 600);
		//view.getCamera().setViewCenter(800, 450, 0);
		//view.getCamera().setViewPercent(0.25);

		graph.addAttribute("ui.stylesheet", stylesheet);
		//graph.addAttribute("ui.stylehseet", "url('http://www.deep.in/the/site/mystylesheet')");
		//graph.addAttribute("ui.stylesheet", "url(file:///somewhere/over/the/rainbow/stylesheet')");
		
		Node A = graph.getNode("A");
		Edge AB = graph.getEdge("AB");
		log.debug("node A - "+A.getIndex());
		log.debug("edge AB - "+AB.getId());
		log.debug("node count - "+graph.getNodeCount());
		log.debug("has edge b/w A and B -"+A.hasEdgeBetween(graph.getNode("B").getId()));
		
		A.addAttribute("name", "testing a node attribute");
		log.debug(A.getAttribute("name").toString());
		log.debug("has attribute - "+A.hasAttribute("name"));
		A.removeAttribute("name");
		// remove all
		A.clearAttributes();
		
		// eclusively place a node
		//A.setAttribute("xyz", 1, 3, 0);

		
		// to show extra images/data on a node
		SpriteManager sman = new SpriteManager(graph);
		Sprite s1 = sman.addSprite("S1");
		Sprite s2 = sman.addSprite("S2");
		Sprite s3 = sman.addSprite("S3");
		/*double p1[] = Toolkit.nodePosition(n);*/
		// params - radius, xyAngle, xzAngle
		//s1.setPosition(2,1,0);
		s1.setPosition(0.5);
		s2.setPosition(0.5);
		s3.setPosition(0.5);
		
		s1.attachToNode("A");
		s2.attachToNode("B");
		s3.attachToNode("C");
		
		
		for(Node n:graph) {
			log.debug(n.getId());
			n.addAttribute("ui.label", n.getId());
			n.addAttribute("ui.class", "marked");
			
			try { Thread.sleep(1000); } catch (Exception e) {}
		}

		s1.attachToEdge("AB");
		s2.attachToEdge("BC");
		s3.attachToEdge("CA");
		
		for(Edge e:graph.getEachEdge()) {
			log.debug(e.getId());
			double speedMax = e.getNumber("speedMax") / 130.0;
			e.setAttribute("ui.color", speedMax);
						
			try { Thread.sleep(1000); } catch (Exception ex) {}
		}
		
		try { pic.writeAll(graph, "screenshot.png"); } catch (Exception ex) {}
		

		//log.debug("randomize: "+Settings.getRandomizeEventGeneration());
		//Settings settings = (Settings)context.getBean("settings");
		//log.debug("hosts: "+Settings.getHostCount());
		//log.debug("randomize: "+Settings.getRandomizeEventGeneration());
		
		// superclass
		log.debug("> "+Element.class.isAssignableFrom(Host.class));	// true
		log.debug("> "+Host.class.isAssignableFrom(Element.class));	// false

		//ValidRelationships validRelationships = (ValidRelationships)context.getBean("validRelationships");
		
		//NodeFactory nodeFactory = (NodeFactory)context.getBean("nodeFactory");
		NodeFactory.generateNodes();
		NodeFactory.relateNodes();
		
		/*for ( String notifName : NotificationFactory.getAllNotifications().keySet() ) {
			log.debug(notifName+"\n"+NotificationFactory.getAllNotifications().get(notifName).toJSON());
		}*/
		
		// let it run forever
		/*try {
			while (true)
				Thread.sleep(365 * 24 * 60 * 60 * 1000);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}*/
		
		((ConfigurableApplicationContext)context).close();
		
	}

}
