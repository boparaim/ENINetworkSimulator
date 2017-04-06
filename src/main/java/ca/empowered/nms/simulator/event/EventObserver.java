package ca.empowered.nms.simulator.event;

import java.util.Observable;
import java.util.Observer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ca.empowered.nms.simulator.node.Element;

public class EventObserver implements Observer {

	private static final Logger log = LogManager.getLogger(EventObserver.class.getName());
	
	@Override
	public void update(Observable observable, Object object) {
		Element element = (Element)observable;
		
		log.debug("got status update for "+element.getName()+" --> "+element.getCurrentState() + " = " + object.toString());
	}

}
