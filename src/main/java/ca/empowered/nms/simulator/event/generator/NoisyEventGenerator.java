package ca.empowered.nms.simulator.event.generator;

import java.util.ArrayList;
import java.util.Random;

import ca.empowered.nms.simulator.api.NodeManager;
import ca.empowered.nms.simulator.event.Notification;
import ca.empowered.nms.simulator.event.NotificationFactory;
import ca.empowered.nms.simulator.node.NodeElement;
import ca.empowered.nms.simulator.utils.Constants.SEVERITY;

/**
 * This class is an event generator that creates events on list of given nodes from list 
 * of given events.
 * 
 * @author mboparai
 *
 */
public class NoisyEventGenerator extends EventGenerator {
	
	public NoisyEventGenerator(final Integer intervalSeconds, final ArrayList<String> nodeList, final String eventNameSet) {
		// eventNameSet - comma separated list of possible event names. Don't include standard event names - down/up/degraded
		super(intervalSeconds, nodeList, eventNameSet);
		
		generatorThread = new Thread() {
			@Override
			public void run() {
				super.run();
				
				Random random1 = new Random();
				Random random2 = new Random();
				Random random3 = new Random();
				
				// generated events doesn't depend on node state
				// and doesn't have any effect on the state of that node
				while (true) {
					if (generatorThread.isInterrupted())
						break;
					
					try {
						Thread.sleep(intervalSeconds * 1000);
						
						log.info("generating an event in "+NoisyEventGenerator.this.getClass().getSimpleName());
						int i = random1.nextInt(nodeList.size());
						int j = random2.nextInt(alarmNameList.size());
						int k = random3.nextInt(SEVERITY.values().length);
						log.debug("i:"+i+" "+nodeList.get(i)+", j:"+j+" "+alarmNameList.get(j)+", k:"+k+" "+SEVERITY.values()[k].toString());
						
						NodeElement node = NodeManager.getGraph().getNode(nodeList.get(i));
						Notification notification = new Notification(node);//, alarmNameList.get(j));
						notification.setSeverity(SEVERITY.values()[k]);
						notification.setType(notification.getSeverity());
						notification.setDescription(alarmNameList.get(j)); //Removes NULL issue
						for(String x : alarmNameList)
						{
							log.debug(x);
						}
						log.debug(j);
						//notification.setDescription(notification.getDescription() + " " + alarmNameList.get(j));
						notification.setNotificationID(random1.nextInt(1000));

						synchronized (NotificationFactory.getAllNotifications()) {
							NotificationFactory.getAllNotifications().put(notification.getId(), notification); //DISABLED FOR TESTING REASONS
						}
						NotificationFactory.getAllNotifications().forEach((s, n) -> {
							log.debug(s+" "+n);
						});

						//NotificationFactory.getAllNotifications().put(notification.getId(), notification);
						
						NotificationFactory.reportNotifcation(notification);
					} catch (Exception e) {
						log.error(e.getMessage(), e);
					}
				}
			}
		};
	}
	
	@Override
	public void start() {
		generatorThread.start();
	}
	
	@Override
	public void stop() {
		generatorThread.interrupt();
	}
	
}
