package ca.empowered.nms.simulator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import ca.empowered.nms.simulator.api.NodeManager;
import ca.empowered.nms.simulator.config.Settings;
import ca.empowered.nms.simulator.event.generator.EventBuilder;
import ca.empowered.nms.simulator.event.generator.NoisyEventGenerator;
import ca.empowered.nms.simulator.event.generator.RandomEventGenerator;

/**
 * Entry point for the application.
 * 
 * @author mboparai
 *
 */
public class Main {

	private static final Logger log = LogManager.getLogger(Main.class.getName());
	
	public static void main(String[] args) {
		
		long startTime = System.nanoTime();
		
		log.info("INIT: ENINetworkSimulator");
		
		// initialize Spring
		ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
		
		NodeManager.generateNodes();
		log.info("CREATION: took "+((System.nanoTime() - startTime)/1000000)+"(ms) "+((System.nanoTime() - startTime)/1000000000)+"(s)");
		NodeManager.relateNodes();
		NodeManager.exportTopology();
		
		if (Settings.isNoisyEventGeneration()) {
			EventBuilder evenBuilder1 = new NoisyEventGenerator(
					Settings.getNoisyEventGenerationInterval(), 
					NodeManager.getAllNodes(), 
					Settings.getNoisyEventGenerationEvents());
			evenBuilder1.start();
		}

		if (Settings.isRandomizeEventGeneration()) {
			EventBuilder evenBuilder2 = new RandomEventGenerator(
					Settings.getRandomizeEventGenerationInterval(), 
					NodeManager.getAllNodes());
			evenBuilder2.start();
		}
		
		((ConfigurableApplicationContext)context).close();
		
		long endTime = System.nanoTime();
		long timeDiff = endTime - startTime;
		log.info("END: took "+timeDiff+"(ns) "+(timeDiff/1000000)+"(ms) or "+(timeDiff/1000000000)+"(s)");
	}

}
