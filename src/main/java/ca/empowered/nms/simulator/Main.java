package ca.empowered.nms.simulator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import ca.empowered.nms.simulator.api.NodeFactory;
import ca.empowered.nms.simulator.event.NotificationFactory;

public class Main {

	private static final Logger log = LogManager.getLogger(Main.class.getName());
	
	public static void main(String[] args) {
		
		log.info("INIT: ENINetworkSimulator");
		
		ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");

		//log.debug("randomize: "+Settings.getRandomizeEventGeneration());
		//Settings settings = (Settings)context.getBean("settings");
		//log.debug("hosts: "+Settings.getHostCount());
		//log.debug("randomize: "+Settings.getRandomizeEventGeneration());
		

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
