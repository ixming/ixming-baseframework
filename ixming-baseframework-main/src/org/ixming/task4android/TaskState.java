package org.ixming.task4android;

public enum TaskState {

	/**
	 * prepare for running
	 */
	Preparing,
	
	/**
	 * is now running
	 */
	Running,
	
	/**
	 * is interrupted or forced to stop
	 */
	Interrupted,
	
	/**
	 * already ending
	 */
	Finished
	
}
