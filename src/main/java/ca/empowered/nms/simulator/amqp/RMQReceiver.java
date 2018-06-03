package ca.empowered.nms.simulator.amqp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class RMQReceiver {
	
	private static final Logger log = LogManager.getLogger(RMQReceiver.class.getName());
	
	public void receiveMessage(String message) {
		log.debug("received message: "+message);
	}
	
}
