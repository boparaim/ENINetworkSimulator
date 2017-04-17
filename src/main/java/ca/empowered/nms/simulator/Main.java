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
import ca.empowered.nms.simulator.config.Settings;
import ca.empowered.nms.simulator.node.Element;
import ca.empowered.nms.simulator.node.Host;
import ca.empowered.nms.simulator.node.Relationship;
import ca.empowered.nms.simulator.ui.GraphManager;

public class Main {

	private static final Logger log = LogManager.getLogger(Main.class.getName());
	
	public static void main(String[] args) {
		
		log.info("INIT: ENINetworkSimulator");
		
		ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
		
		//log.debug("randomize: "+Settings.getRandomizeEventGeneration());
		//Settings settings = (Settings)context.getBean("settings");
		//log.debug("hosts: "+Settings.getHostCount());
		//log.debug("randomize: "+Settings.getRandomizeEventGeneration());
		
		// superclass
		log.debug("> "+Element.class.isAssignableFrom(Host.class));	// true
		log.debug("> "+Host.class.isAssignableFrom(Element.class));	// false

		//ValidRelationships validRelationships = (ValidRelationships)context.getBean("validRelationships");
		//GraphManager.displayUI();
		
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
