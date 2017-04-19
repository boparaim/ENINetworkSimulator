package ca.empowered.nms.simulator.config;

import org.springframework.stereotype.Component;

@Component
public final class Settings {

	//private static final Logger log = LogManager.getLogger(Settings.class.getName());
	
	private static String appName;
	private static String nodeNameSuffix;
	private static Boolean evenlyDistributeRelationships;
	private static Boolean randomizeEventGeneration;
	private static String restServerIP;
	private static String restServerPort;
	private static String restServerPath;
	private static String restDestinationURL;
	private static Boolean restClientEnabled;
	private static String cssStyleSheet;
	private static boolean displayGUI;
	private static boolean guiClosesApp;
	private static boolean uiAntiAlias;
	private static boolean uiQuality;
	private static String uiLayoutAlgorithm;
	private static String uiRenderer;
	private static boolean uiUseOpenGL;
	private static boolean uiUseDirectX;
	private static double uiZoomFactor;
	
	public static String getAppName() {
		return appName;
	}
	public static void setAppName(String appName) {
		Settings.appName = appName;
	}
	public static String getNodeNameSuffix() {
		return nodeNameSuffix;
	}
	public static void setNodeNameSuffix(String nodeNameSuffix) {
		Settings.nodeNameSuffix = nodeNameSuffix;
	}
	public static Boolean getEvenlyDistributeRelationships() {
		return evenlyDistributeRelationships;
	}
	public static void setEvenlyDistributeRelationships(Boolean evenlyDistributeRelationships) {
		Settings.evenlyDistributeRelationships = evenlyDistributeRelationships;
	}
	public static Boolean getRandomizeEventGeneration() {
		return randomizeEventGeneration;
	}
	public static void setRandomizeEventGeneration(Boolean randomizeEventGeneration) {
		Settings.randomizeEventGeneration = randomizeEventGeneration;
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
	public static String getRestDestinationURL() {
		return restDestinationURL;
	}
	public static void setRestDestinationURL(String restDestinationURL) {
		Settings.restDestinationURL = restDestinationURL;
	}
	public static Boolean getRestClientEnabled() {
		return restClientEnabled;
	}
	public static void setRestClientEnabled(Boolean restClientEnabled) {
		Settings.restClientEnabled = restClientEnabled;
	}
	public static String getCssStyleSheet() {
		return cssStyleSheet;
	}
	public static void setCssStyleSheet(String cssStyleSheet) {
		Settings.cssStyleSheet = cssStyleSheet;
	}
	public static boolean isDisplayGUI() {
		return displayGUI;
	}
	public static void setDisplayGUI(boolean displayGUI) {
		Settings.displayGUI = displayGUI;
	}
	public static boolean isGuiClosesApp() {
		return guiClosesApp;
	}
	public static void setGuiClosesApp(boolean guiClosesApp) {
		Settings.guiClosesApp = guiClosesApp;
	}
	public static boolean isUiAntiAlias() {
		return uiAntiAlias;
	}
	public static void setUiAntiAlias(boolean uiAntiAlias) {
		Settings.uiAntiAlias = uiAntiAlias;
	}
	public static boolean isUiQuality() {
		return uiQuality;
	}
	public static void setUiQuality(boolean uiQuality) {
		Settings.uiQuality = uiQuality;
	}
	public static String getUiLayoutAlgorithm() {
		return uiLayoutAlgorithm;
	}
	public static void setUiLayoutAlgorithm(String uiLayoutAlgorithm) {
		Settings.uiLayoutAlgorithm = "org.graphstream.ui.layout.springbox.implementations." + uiLayoutAlgorithm;
		System.out.println("class is "+Settings.uiLayoutAlgorithm);
		System.setProperty("org.graphstream.ui.layout", Settings.uiLayoutAlgorithm);
	}
	public static String getUiRenderer() {
		return uiRenderer;
	}
	public static void setUiRenderer(String uiRenderer) {
		Settings.uiRenderer = uiRenderer;
		System.setProperty("org.graphstream.ui.renderer", Settings.uiRenderer);
	}
	public static boolean isUiUseOpenGL() {
		return uiUseOpenGL;
	}
	public static void setUiUseOpenGL(boolean uiUseOpenGL) {
		Settings.uiUseOpenGL = uiUseOpenGL;
		System.setProperty("sun.java2d.opengl", String.valueOf(Settings.uiUseOpenGL));
	}
	public static boolean isUiUseDirectX() {
		return uiUseDirectX;
	}
	public static void setUiUseDirectX(boolean uiUseDirectX) {
		Settings.uiUseDirectX = uiUseDirectX;
		System.setProperty("sun.java2d.directx", String.valueOf(Settings.uiUseDirectX));
	}
	public static double getUiZoomFactor() {
		return uiZoomFactor;
	}
	public static void setUiZoomFactor(double uiZoomFactor) {
		Settings.uiZoomFactor = uiZoomFactor;
	}
}
