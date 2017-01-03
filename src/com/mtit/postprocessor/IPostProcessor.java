package com.mtit.postprocessor;

import com.mtit.process.SyncException;

/**
 * PostProcessor interface for any processing that is to be done as
 * part of the synchronize process but after the main processing step.
 * 
 * @author Mei
 *
 */
public interface IPostProcessor {

	/**
	 * Process method.
	 * @throws SyncException 
	 */
	public void process() throws SyncException;

	/**
	 * Sets the input file to be uploaded.
	 * 
	 * @param genFile
	 */
	public void setInputFile(String genFile);
	
	/**
	 * Sets the delete file to be processed.
	 * 
	 * @param deleteFile
	 */
	public void setDeleteFile(String deleteFile);
}
