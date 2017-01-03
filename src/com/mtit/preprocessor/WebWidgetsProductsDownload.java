/**
 * 
 */
package com.mtit.preprocessor;

import org.apache.log4j.Logger;

import com.mtit.entity.IntProperties;
import com.mtit.process.SyncException;
import com.mtit.utils.FileDownload;
import com.mtit.utils.LoggerFactory;

/**
 * Web Widgets products download class to help download a csv file containing
 * all products
 * 
 * @author Mei
 *
 */
public class WebWidgetsProductsDownload implements IPreprocessor {

	private String downloadUrl = null;
	
	private String outputFile = null;
	
	private Logger logger;
	
	/**
	 * Constructor
	 */
	public WebWidgetsProductsDownload() {
		logger = LoggerFactory.getLogger(WebWidgetsProductsDownload.class);
		
		try {
			downloadUrl = IntProperties.getProperty(IntProperties.WEB_WIDGET_PRODUCTS_DOWNLOAD_URL);
			outputFile = IntProperties.getProperty(IntProperties.WEB_WIDGET_INPUT_FILE);
		} catch (Exception e) {
			logger.error("Unable to get " + IntProperties.WEB_WIDGET_PRODUCTS_DOWNLOAD_URL + " property");
		}
	}
	
	@Override
	public void process() throws SyncException {
		System.out.println("Downloading from URL:" + downloadUrl);
		FileDownload.downloadAsCsv(downloadUrl, outputFile);
	}

}
