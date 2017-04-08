package ca.empowered.nms.simulator.config;

import org.springframework.stereotype.Component;

@Component
public final class Settings {

	//private static final Logger log = LogManager.getLogger(Settings.class.getName());
	
	// TODO: change 5
	
	//@Value("${eni.nms.simulator.count.Host}")
	private static Integer hostCount;
	private static Integer webApplicationCount;
	private static Integer cardCount;
	private static Boolean evenlyDistributeRelationships;
	private static Boolean randomizeEventGeneration;
	private static String nodeNameSuffix;
	private static String restServerIP;
	private static String restServerPort;
	private static String restServerPath;
	private static String appName;
	private static String restDestinationURL;
	private static Integer hostLevel;
	private static Integer webApplicationLevel;
	private static Integer cardLevel;
	private static Boolean restClientEnabled;

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

	public static Integer getCardCount() {
		return cardCount;
	}

	public static void setCardCount(Integer cardCount) {
		Settings.cardCount = cardCount;
	}

	public static String getRestServerIP() {
		return restServerIP;
	}

	public static void setRestServerIP(String restServerIP) {
		Settings.restServerIP = restServerIP;
	}

	public static String getRestServerPort() {
		return restServerPort;
	}

	public static void setRestServerPort(String restServerPort) {
		Settings.restServerPort = restServerPort;
	}

	public static String getRestServerPath() {
		return restServerPath;
	}

	public static void setRestServerPath(String restServerPath) {
		Settings.restServerPath = restServerPath;
	}

	public static String getAppName() {
		return appName;
	}

	public static void setAppName(String appName) {
		Settings.appName = appName;
	}

	public static String getRestDestinationURL() {
		return restDestinationURL;
	}

	public static void setRestDestinationURL(String restDestinationURL) {
		Settings.restDestinationURL = restDestinationURL;
	}

	public static Integer getHostLevel() {
		return hostLevel;
	}

	public static void setHostLevel(Integer hostLevel) {
		Settings.hostLevel = hostLevel;
	}

	public static Integer getWebApplicationLevel() {
		return webApplicationLevel;
	}

	public static void setWebApplicationLevel(Integer webApplicationLevel) {
		Settings.webApplicationLevel = webApplicationLevel;
	}

	public static Integer getCardLevel() {
		return cardLevel;
	}

	public static void setCardLevel(Integer cardLevel) {
		Settings.cardLevel = cardLevel;
	}

	public static Boolean getRestClientEnabled() {
		return restClientEnabled;
	}

	public static void setRestClientEnabled(Boolean restClientEnabled) {
		Settings.restClientEnabled = restClientEnabled;
	}
}
