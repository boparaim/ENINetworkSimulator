package ca.empowered.nms.simulator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import ca.empowered.nms.simulator.api.NodeManager;

public class Main {

	private static final Logger log = LogManager.getLogger(Main.class.getName());
	
	public static void main(String[] args) {
		
		long startTime = System.nanoTime();
		
		log.info("INIT: ENINetworkSimulator");
		
		ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
		
		NodeManager.generateNodes();
		log.info("CREATION: took "+((System.nanoTime() - startTime)/1000000)+"(ms) "+((System.nanoTime() - startTime)/1000000000)+"(s)");
		NodeManager.relateNodes();
		
		((ConfigurableApplicationContext)context).close();
		
		long endTime = System.nanoTime();
		long timeDiff = endTime - startTime;
		log.info("END: took "+timeDiff+"(ns) "+(timeDiff/1000000)+"(ms) or "+(timeDiff/1000000000)+"(s)");
	}

}
