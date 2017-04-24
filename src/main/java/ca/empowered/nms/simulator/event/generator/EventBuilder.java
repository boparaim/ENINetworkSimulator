package ca.empowered.nms.simulator.event.generator;

/**
 * Interface for Event generators.
 * 
 * @author mboparai
 *
 */
public interface EventBuilder {

	/**
	 * Start the generator thread.
	 */
	public void start();
	
	/**
	 * Stop the generator thread.
	 */
	public void stop();
	
}
