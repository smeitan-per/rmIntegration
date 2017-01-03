/**
 * 
 */
package com.mtit.process;

/**
 * Provides ability to throw useful exceptions
 * 
 * @author Mei
 *
 */
public class SyncException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8450989173108216777L;

	/**
	 * 
	 * @param errorMessage
	 */
	public SyncException(String errorMessage) {
		super("SyncException: " + errorMessage);
	}
}
