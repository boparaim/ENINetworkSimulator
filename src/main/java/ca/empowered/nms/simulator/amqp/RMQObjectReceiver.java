package ca.empowered.nms.simulator.amqp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;

import ca.empowered.nms.simulator.db.model.Event;
import ca.empowered.nms.simulator.db.model.Event.TYPE;
import ca.empowered.nms.simulator.db.model.MessageObject;

@Component
public class RMQObjectReceiver {
	
	private static final Logger log = LogManager.getLogger(RMQObjectReceiver.class.getName());
	
	@Autowired
    public SimpMessageSendingOperations messagingTemplate;
	
	public void receiveMessage(MessageObject messageObject) {
		log.debug("received message: "+messageObject.getClass().getSimpleName());
		if (messageObject.getClass().getSimpleName().equals(Event.class.getSimpleName())) {
			Event event = (Event)messageObject;
			log.debug(">> "+event.toString());
			if (event.getType().equals(TYPE.NODE_CREATED.toString())
					|| event.getType().equals(TYPE.EDGE_CREATED.toString())
					|| event.getType().equals(TYPE.TOPOLOGY_DELETED.toString())
					|| event.getType().equals(TYPE.NODE_COORDINATES_UPDATED.toString())) {
				messagingTemplate.convertAndSend("/topic/test-websocket-reply", event);
			}
		}
	}
	
}
