/**
 * 
 */
package com.mtit.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import com.csvreader.CsvReader;
import com.mtit.entity.IntProperties;
import com.mtit.process.SyncException;

/**
 * File Download class to enable accessing of an http URL and saving 
 * the input stream as a file .
 * 
 * @author Mei
 *
 */
public class FileDownload {

	private static final String WEBSITE_GROUP_DOWNLOAD_URL = "webSiteGroupDownloadUrl";
	private static final String GROUP_FILE_LOCATION = "groupFileLocation";
	
	private static Properties properties = null;
	
	/**
	 * Downloads group file 
	 * 
	 * @throws SyncException
	 */
	public static String downloadGroupFile() throws SyncException {
		
		if (properties == null) {
			properties = IntProperties.loadProperties();
		}
		
		String groupDownloadURL = properties.getProperty(WEBSITE_GROUP_DOWNLOAD_URL);
		String groupFile = properties.getProperty(GROUP_FILE_LOCATION);
		
		downloadAsCsv(groupDownloadURL, groupFile);
		return groupFile;
	}
	
	/**
	 * Downloads a file to a given filename as a csv file.
	 * 
	 * @param downloadUrl
	 * @param filename
	 * @throws SyncException 
	 */
	public static void downloadAsCsv(String downloadUrl, String outputFile) throws SyncException {
		if (downloadUrl != null) {
			CsvReader reader = null;
			BufferedWriter writer = null;
			try {
				// Get an input stream for reading
	            InputStream in = SSLConnection.getInputStream(downloadUrl);

	            // Create a buffered Reader for efficency
	            BufferedReader breader = new BufferedReader(new InputStreamReader(in));
	            writer = new BufferedWriter(new FileWriter(outputFile));
	            reader = new CsvReader(breader);
	            
	            while (reader.readRecord()) {
	            	writer.write(reader.getRawRecord());
	            	writer.newLine();
	            }
			} catch (IOException e) {
				throw new SyncException("Unable to write contents of download from " + downloadUrl + ":" + e.getMessage());
			} finally {
	            try {
	            	if (reader != null) {
	            		reader.close();
	            	}

	            	if (writer != null) {
	            		writer.close();
	            	}
	            } catch (IOException e) {
					throw new SyncException("Unable to close the reader or the writer: " + e.getMessage());
				}
			}
		}
	}
}
