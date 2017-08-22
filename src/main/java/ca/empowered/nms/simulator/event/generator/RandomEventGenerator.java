package ca.empowered.nms.simulator.event.generator;

import java.lang.reflect.Array;
import java.util.*;

import ca.empowered.nms.simulator.api.NodeManager;
import ca.empowered.nms.simulator.node.NodeElement;
import ca.empowered.nms.simulator.utils.Constants.STATE;

/**
 * This class is an event generator that creates events on list of given nodes.
 * 
 * @author mboparai
 *
 */
public class RandomEventGenerator extends EventGenerator {

	public RandomEventGenerator(final Integer intervalSeconds, final ArrayList<String> nodeList) {
		super(intervalSeconds, nodeList, "UP,DOWN");
		
		generatorThread = new Thread() {
			@Override
			public void run() {
				super.run();
				
				Random random1 = new Random();
				Random random2 = new Random();
				Random random3 = new Random();
				ArrayList<NodeElement> test = new ArrayList<NodeElement>();

				// generated events doesn't depend on node state
				// and doesn't have any effect on the state of that node
				//TODO: New Node is created every loop?
				while (true) {
					if (generatorThread.isInterrupted())
						break;
					
					try {
						Thread.sleep(intervalSeconds * 1000);
						
						log.info("generating an event in "+RandomEventGenerator.this.getClass().getSimpleName());
						int i = random1.nextInt(nodeList.size());
						int j = random2.nextInt(alarmNameList.size());

						NodeElement node = NodeManager.getGraph().getNode(nodeList.get(i));

						test.add(node); //List of created nodes from above.
						//TODO Update NODE POI's
						HashMap<String,Integer> pois = node.getAttribute("pois");

						//Iterates through node's POIs, randomly updates them with a % load

						for (Map.Entry<String,Integer> entry : pois.entrySet()) {

							int k = random3.nextInt(100);
							pois.put(entry.getKey(), k);
						}

						if (!checkPOIS(pois).isEmpty()) {
							node.setCurrentState(STATE.DEGRADED);
						}
						else {
							node.setCurrentState(STATE.valueOf(alarmNameList.get(j)));
						}
						//POI ISSUE. SETS TO DEGRADED.

						node.setAttribute("pois", pois);


						//TODO ADD RANDOM LOGIC FOR SERVER GOING DOWN VS TRIGGERED POI



					} catch (Exception e) {
						log.error(e.getMessage(), e);
					}
				}
			}
		};
	}

	//Triggers will be where a node's POI's are high enough that they require Notification.
	public static HashMap<String,Integer> checkPOIS(HashMap<String, Integer> poiVal) {
		int threshold = 95;
		HashMap<String, Integer> triggeredPOIS = new HashMap<>();

		for(Map.Entry<String, Integer> val : poiVal.entrySet()) {
			if (val.getValue() > threshold) {
				triggeredPOIS.put(val.getKey(),val.getValue());
			}
		}
		return triggeredPOIS;

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
