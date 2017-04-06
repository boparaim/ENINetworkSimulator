package ca.empowered.nms.simulator.config;

import org.springframework.stereotype.Component;

@Component
public final class Settings {

	//private static final Logger log = LogManager.getLogger(Settings.class.getName());
	
	//@Value("${eni.nms.simulator.count.Host}")
	private static Integer hostCount;
	private static Integer webApplicationCount;
	private static Boolean evenlyDistributeRelationships;
	private static Boolean randomizeEventGeneration;
	private static String nodeNameSuffix;

	public static Integer getHostCount() {
		return Settings.hostCount;
	}

	public static Integer getWebApplicationCount() {
		return Settings.webApplicationCount;
	}

	public static Boolean getEvenlyDistributeRelationships() {
		return Settings.evenlyDistributeRelationships;
	}

	public static Boolean getRandomizeEventGeneration() {
		return Settings.randomizeEventGeneration;
	}

	public static String getNodeNameSuffix() {
		return Settings.nodeNameSuffix;
	}

	public static void setHostCount(Integer hostCount) {
		Settings.hostCount = hostCount;
	}

	public static void setWebApplicationCount(Integer webApplicationCount) {
		Settings.webApplicationCount = webApplicationCount;
	}

	public static void setEvenlyDistributeRelationships(Boolean evenlyDistributeRelationships) {
		Settings.evenlyDistributeRelationships = evenlyDistributeRelationships;
	}

	public static void setRandomizeEventGeneration(Boolean randomizeEventGeneration) {
		Settings.randomizeEventGeneration = randomizeEventGeneration;
	}

	public static void setNodeNameSuffix(String nodeNameSuffix) {
		Settings.nodeNameSuffix = nodeNameSuffix;
	}
}
