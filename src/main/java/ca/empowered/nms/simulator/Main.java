package ca.empowered.nms.simulator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import ca.empowered.nms.simulator.api.NodeFactory;
import ca.empowered.nms.simulator.config.Settings;

public class Main {

	private static final Logger log = LogManager.getLogger(Main.class.getName());
	
	public static void main(String[] args) {
		
		log.info("INIT: ENINetworkSimulator");
		
		ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");

		log.debug("randomize: "+Settings.getRandomizeEventGeneration());
		//Settings settings = (Settings)context.getBean("settings");
		log.debug("hosts: "+Settings.getHostCount());
		//log.debug("randomize: "+Settings.getRandomizeEventGeneration());
		
		NodeFactory nodeFactory = (NodeFactory)context.getBean("nodeFactory");
		nodeFactory.generateNodes();
		
		
		
		((ConfigurableApplicationContext)context).close();
		
	}

}
