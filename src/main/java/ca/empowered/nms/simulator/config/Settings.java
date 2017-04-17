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
	private static Integer loadBalancerCount;
	private static Integer routerCount;
	private static Integer switchCount;
	private static Integer interfaceCount;
	private static Integer bgpSessionCount;
	private static Integer networkConnectionCount;
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
	private static Integer Level;
	private static Integer loadBalancerLevel;
	private static Integer routerLevel;
	private static Integer switchLevel;
	private static Integer interfaceLevel;
	private static Integer bgpSessionLevel;
	private static Integer networkConnectionLevel;
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

	public static Integer getLoadBalancerCount() {
		return loadBalancerCount;
	}

	public static void setLoadBalancerCount(Integer loadBalancerCount) {
		Settings.loadBalancerCount = loadBalancerCount;
	}

	public static Integer getRouterCount() {
		return routerCount;
	}

	public static void setRouterCount(Integer routerCount) {
		Settings.routerCount = routerCount;
	}

	public static Integer getSwitchCount() {
		return switchCount;
	}

	public static void setSwitchCount(Integer switchCount) {
		Settings.switchCount = switchCount;
	}

	public static Integer getInterfaceCount() {
		return interfaceCount;
	}

	public static void setInterfaceCount(Integer interfaceCount) {
		Settings.interfaceCount = interfaceCount;
	}

	public static Integer getBgpSessionCount() {
		return bgpSessionCount;
	}

	public static void setBgpSessionCount(Integer bgpSessionCount) {
		Settings.bgpSessionCount = bgpSessionCount;
	}

	public static Integer getNetworkConnectionCount() {
		return networkConnectionCount;
	}

	public static void setNetworkConnectionCount(Integer networkConnectionCount) {
		Settings.networkConnectionCount = networkConnectionCount;
	}

	public static Integer getLoadBalancerLevel() {
		return loadBalancerLevel;
	}

	public static void setLoadBalancerLevel(Integer loadBalancerLevel) {
		Settings.loadBalancerLevel = loadBalancerLevel;
	}

	public static Integer getRouterLevel() {
		return routerLevel;
	}

	public static void setRouterLevel(Integer routerLevel) {
		Settings.routerLevel = routerLevel;
	}

	public static Integer getSwitchLevel() {
		return switchLevel;
	}

	public static void setSwitchLevel(Integer switchLevel) {
		Settings.switchLevel = switchLevel;
	}

	public static Integer getInterfaceLevel() {
		return interfaceLevel;
	}

	public static void setInterfaceLevel(Integer interfaceLevel) {
		Settings.interfaceLevel = interfaceLevel;
	}

	public static Integer getBgpSessionLevel() {
		return bgpSessionLevel;
	}

	public static void setBgpSessionLevel(Integer bgpSessionLevel) {
		Settings.bgpSessionLevel = bgpSessionLevel;
	}

	public static Integer getNetworkConnectionLevel() {
		return networkConnectionLevel;
	}

	public static void setNetworkConnectionLevel(Integer networkConnectionLevel) {
		Settings.networkConnectionLevel = networkConnectionLevel;
	}
}
