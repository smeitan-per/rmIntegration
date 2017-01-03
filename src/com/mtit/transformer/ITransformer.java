/**
 * 
 */
package com.mtit.transformer;

import com.mtit.entity.RetailManagerObject;
import com.mtit.process.SyncException;

/**
 * Interface for transformation of records from MYOB Retail Manager format
 * to another format. 
 * 
 * @author Mei
 *
 */
public interface ITransformer {

	/**
	 * Method to transform a retailManager object to a new format.
	 * @param rmObj
	 * @throws SyncException 
	 */
	public void transform(RetailManagerObject rmObj) throws SyncException;
	
	/**
	 * Method to write an output file.
	 */
	public void writeOutput() throws SyncException;

	/**
	 * Sets the output file to write to.
	 * @param outputFile
	 */
	public void setOutputFile(String outputFile);

	/**
	 * Sets the delete file to write to
	 * @param deleteFile
	 */
	public void setDeleteFile(String deleteFile);

}
