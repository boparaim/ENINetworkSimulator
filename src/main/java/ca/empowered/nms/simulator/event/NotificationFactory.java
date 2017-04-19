package ca.empowered.nms.simulator.event;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ca.empowered.nms.simulator.api.rest.RestServer;
import ca.empowered.nms.simulator.config.Settings;
import ca.empowered.nms.simulator.node.NodeElement;

public class NotificationFactory {

	private static final Logger log = LogManager.getLogger(NotificationFactory.class.getName());
	private static HashMap<String, Notification> allNotifications = new HashMap<>();

	public static void submitNotifcation(NodeElement node) {
		Notification notification = new Notification(node);
		notification.updateSeverity(node);
		notification.updateDescription();
		allNotifications.put(notification.getId(), notification);
		
		reportNotifcation(notification);
	}
	
	private static void reportNotifcation(Notification notification) {
		if (!Settings.getRestClientEnabled()) {
			log.warn("trying to send JSON while eni.nms.simulator.rest.client.enabled is set to FALSE");
			return;
		}
		//log.debug("notification: "+notification);
		RestServer.sendJSON(notification.toJSON());
	}

	public static HashMap<String, Notification> getAllNotifications() {
		return allNotifications;
	}
	
}
