/**
 * 
 */
package com.mtit.entity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.csvreader.CsvReader;
import com.mtit.process.SyncException;
import com.mtit.utils.FileDownload;
import com.mtit.utils.LoggerFactory;

/**
 * Retrieves the website category from the ww-groups.csv file. 
 * @author Mei
 *
 */
public class WebSiteCategoryDao {
	
	private static Logger logger = LoggerFactory.getLogger(WebSiteCategoryDao.class);
	
	private static Map<String, String> categories = new TreeMap<String, String>();
	
	/**
	 * Reads the ww-groups.csv config file and return the CSV object.
	 * 
	 * @return
	 * @throws SyncException 
	 */
	public static Map<String, String> getWWCategories() throws SyncException {

		if (categories.size()==0) {
			readFile();
		}
		return categories;
	}
	
	/**
	 * Refresh the categories list
	 * @throws SyncException 
	 */
	public static void refresh() throws SyncException {
		FileDownload.downloadGroupFile();
		readFile();
	}
	
	/**
	 * Read the downloaded csv file.
	 * 
	 * @throws SyncException
	 */
	public static void readFile() throws SyncException {

		String filename = IntProperties.getProperty(IntProperties.GROUP_FILE_LOCATION);
		
		char delimiter = ',';
		if (IntProperties.getProperty(IntProperties.GROUP_FILE_DELIMITER) != null) {
			delimiter = IntProperties.getProperty(IntProperties.GROUP_FILE_DELIMITER).charAt(0);
		}
		File file = new File(filename);
		
		categories.put("NA", "-9");
		
		if (file != null) {


			CsvReader reader;
			try {
				reader = new CsvReader(new FileReader(file));
				reader.setDelimiter(delimiter);
				reader.readHeaders();

				while (reader.readRecord()) {
					if (reader.get("GROUPID").isEmpty() || 
							reader.get("GROUP_NAME").isEmpty()) {
						System.out.println("groupid="+reader.get("GROUPID"));
						System.out.println("groupname="+reader.get("GROUP_NAME"));
						
						throw new SyncException("Unable to find GROUP_NAME, please check the config/ww-groups.csv file");
					}
					categories.put(reader.get("GROUP_NAME"), reader.get("GROUPID"));
				}
			} catch (FileNotFoundException e) {
				logger.error("Unable to find file " + IntProperties.GROUP_FILE_LOCATION);
			} catch (Throwable e) {
				logger.error(e);
				throw new SyncException(e.getMessage());
			}
		}

	}
}
