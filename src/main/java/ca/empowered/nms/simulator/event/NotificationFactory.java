package ca.empowered.nms.simulator.event;

import java.util.*;

import ca.empowered.nms.simulator.api.NodeManager;
import ca.empowered.nms.simulator.node.NodeTemplate;
import ca.empowered.nms.simulator.utils.Constants;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ca.empowered.nms.simulator.api.rest.RestServer;
import ca.empowered.nms.simulator.config.Settings;
import ca.empowered.nms.simulator.node.NodeElement;
import scala.Array;
import scala.Int;
import sun.awt.SunHints;

/**
 * This class handles notifications.
 *
 * @author mboparai
 */
public class NotificationFactory {

    private static final Logger log = LogManager.getLogger(NotificationFactory.class.getName());
    /**
     * list of notifications
     */
    private static HashMap<String, Notification> allNotifications = new HashMap<>();

    /**
     * Add notification to the notification list.
     *
     * @param node
     */
    public static void submitNotifcation(NodeElement node) {
        Notification notification = new Notification(node);

        notification.updateSeverity(node);
        notification.updateNotificationID();

        if (node.getCurrentState() == Constants.STATE.DEGRADED) {
            HashMap<String, Integer> pois = node.getAttribute("pois");


            for (Map.Entry<String, Integer> POI : pois.entrySet()) {

                notification.setDescription(POI.getKey() + " is at " + POI.getValue() + "% usage!");
                notification.setClassType(POI.getKey());
            }
        } else {

            notification.setDescription(node.getId() + " is " + node.getCurrentState());
            notification.setClassType("HEARTBEAT");
        }
        notification.setID();
        allNotifications.put(notification.getId(), notification);
        reportNotifcation(notification);
    }


    /**
     * Send a notification to rest web service.
     *
     * @param notification
     */
    public static void reportNotifcation(Notification notification) {
        if (!Settings.getRestClientEnabled()) {
            log.warn("trying to send JSON while eni.nms.simulator.rest.client.enabled is set to FALSE");
            return;
        }
        //log.debug("notification: "+notification);
        RestServer.sendJSON(notification.toJSON());
    }

    /**
     * Get list of all notifications.
     *
     * @return
     */
    public static HashMap<String, Notification> getAllNotifications() {
        return allNotifications;
    }


}
