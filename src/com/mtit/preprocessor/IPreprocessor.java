package com.mtit.preprocessor;

import com.mtit.process.SyncException;

/**
 * Interface for doing preprocessing work. 
 * 
 * @author Mei
 *
 */
public interface IPreprocessor {

	public void process() throws SyncException;
	
}
