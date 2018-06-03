package ca.empowered.nms.simulator.json;

public class JsonResponse {
	
	public static enum STATUS {PASS, FAIL, WARN};
	private String status;
	private String message;
	/**
	 * format should be json object containing key:value pairs
	 */
	private String metadata;
	
	public JsonResponse() {
		
	}
	
	public JsonResponse(STATUS status, String message) {
		this.status = status.toString();
		this.message = message;
	}
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public void setStatus(STATUS status) {
		this.status = status.toString();
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getMetadata() {
		return metadata;
	}
	public void setMetadata(String metadata) {
		this.metadata = metadata;
	}
	
}
