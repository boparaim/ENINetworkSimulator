package ca.empowered.nms.simulator.event;

import java.sql.Timestamp;

import ca.empowered.nms.simulator.config.Settings;
import ca.empowered.nms.simulator.node.NodeElement;
import ca.empowered.nms.simulator.utils.Constants.SEVERITY;
import ca.empowered.nms.simulator.utils.Constants.STATE;

/**
 * This class represents a notification.
 * 
 * @author mboparai
 *
 */
public class Notification {

	//https://docs.moogsoft.com/display/050203/Generic+Rest+LAM
	//Above is a link to default mappings and what they represent

	// class/instance/type
	private String id;

	// this app
	private String manager;
	// element
	private String source;
	private String className;
	private String instanceName;
	private String eventName;
	private String source_id; // IP ADDRESS. THIS IS MANDATORY IN MOOG
	// populate on element on generation
	private String location;
	// populate on element on generation
	private String type;
	// notify or clear
	private SEVERITY severity;
	private String description;
	private long timestamp;
	//Class Is needed for MOOG and likely all monitoring solutions. Class should be stuff like Memory, CPU, Storage etc.
	private String classType;
	private int notificationID;
	
	public Notification(String className, String instanceName, String eventName, String ip) {

		this.className = className;
		this.instanceName = instanceName;
		this.eventName = eventName;
		this.source_id = ip;


		manager = Settings.getAppName();
		source = instanceName;
		
		// TODO: change back to milliseconds
		//timestamp = new Timestamp(System.currentTimeMillis()).toInstant().toEpochMilli();
		timestamp = new Timestamp(System.currentTimeMillis()).toInstant().getEpochSecond();
	}
	
	//public Notification(NodeElement node) {
	//	this(node);
	//}
	
	public Notification(NodeElement node) {
		this(node.getAttribute("class"), node.getId(), node.getCurrentState().toString(), node.getAttribute("ip_address"));
	}
	
	/**
	 * Get json representation of this notification.
	 * 
	 * @return
	 */
	public String toJSON() {
		// TODO: testing with moog
		int sev = 1;
		if (severity.toString().equals("CLEAR"))
			sev = 0;
		if (severity.toString().equals("CRITICAL"))
			sev = 5;


		return
				  "{\"events\":[{"
				+ "\"signature\":\""+id+"\", "
				+ "\"manager\":\""+manager+"\", "
				+ "\"source\":\""+instanceName+"\", "
				+ "\"source_id\":\""+source_id+"\" , "
				+ "\"class\":\""+classType+"\", "
				+ "\"agent_location\":\"ottawa\", "
				+ "\"external_id\":\""+notificationID+"\", "
				+ "\"type\":\""+eventName+"\", "
				+ "\"severity\":"+sev+", "
				+ "\"description\":\""+description+"\", "
				+ "\"agent_time\":"+timestamp+"}]}";
	}
	public String getID() { return id; }
	public void setID() { id = this.className + "::" + this.instanceName + "::" + this.classType;}
	public String getManager() {
		return manager;
	}
	public void setManager(String manager) {
		this.manager = manager;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getInstanceName() {
		return instanceName;
	}
	public void setInstanceName(String instanceName) {
		this.instanceName = instanceName;
	}
	public String getEventName() {
		return eventName;
	}
	public void setEventName(String eventName) {
		this.eventName = eventName;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getType() {
		return type;
	}
	public void setType(SEVERITY severity) {
		if (severity == SEVERITY.CRITICAL)
		{
			this.type = "DOWN";
		}
		if (severity == SEVERITY.MAJOR)
		{
			this.type = "HIGH LOAD";
		}
		if (severity == SEVERITY.CLEAR) {
			this.type = "UP";
		}
		else if (severity == SEVERITY.INFO) {
			this.type = "INFO";
		}
	}
	public SEVERITY getSeverity() {
		return severity;
	}
	public void setSeverity(SEVERITY severity) {
		this.severity = severity;
	}
	/**
	 * Update severity of this notification based on current state.
	 * 
	 * @param node
	 */
	public void updateSeverity(NodeElement node) {
		if (node.getCurrentState().equals(STATE.DOWN))
			this.setSeverity(SEVERITY.CRITICAL);
		else if (node.getCurrentState().equals(STATE.DEGRADED))
			this.setSeverity(SEVERITY.MAJOR);
		else if (node.getCurrentState().equals(STATE.UP))
			this.setSeverity(SEVERITY.CLEAR);
		else
			this.setSeverity(SEVERITY.INFO);
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	/*public void updateDescription() {
		description = className+" "+instanceName+" "+eventName+" "+severity;
	}*/
	public int getNotificationID() {return notificationID;}
	public void setNotificationID(int notificationID) { this.notificationID = notificationID; }
	public void updateNotificationID() { notificationID += 1;} //temporary value for dev purposes.
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	public String getId() {
		return id;
	}
	public String getSource_id() {return source_id;}
	public void setSource_id(String source_id) {this.source_id = source_id;}

	/*
	Class Type is generated from the POI. POIs are areas of failure such as CPU,MEM,STORAGE,etc.
	Class Should be the failure point. What SPECIFICALLY is being monitored. What SPECIFICALLY went wrong. Desc
	could be generated from this.
	 */
	public String getClassType() { return classType; }
	public void setClassType(String classType) { this.classType = classType; }


}
