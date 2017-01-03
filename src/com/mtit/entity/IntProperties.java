/**
 * 
 */
package com.mtit.entity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.mtit.process.SyncException;
import com.mtit.utils.LoggerFactory;

/**
 * Allows for reading of properties set in rmIntegration.properties.
 * 
 * @author Mei
 *
 */
public class IntProperties {

	public static final String PROPERTIES_FILE = "rmIntegration.properties";
	
	public static String propertyPath = null;
	
	private static Properties properties;
	
	private static Logger logger = LoggerFactory.getLogger(IntProperties.class);
	
	public static final String CONFIG_LOCATION = "configLocation";
	public static final String DATA_SOURCE_NAME = "dataSourceName";
	public static final String DELETE_FILE_PREFIX = "deleteFilePrefix";
	public static final String DELETE_PRODUCT_URL = "deleteProductURL";
	public static final String GROUP_FILE_DELIMITER = "groupFileDelimiter";
	public static final String GROUP_FILE_LOCATION = "groupFileLocation";
	public static final String MAIN_WEBSITE_GROUP = "mainWebsiteGroup";
	public static final String NEW_ITEM_GROUP_FIELD_ID = "newItemGroupFieldId";
	public static final String NEW_ITEM_GROUP_NAME = "newItemGroupName";
	public static final String OUTPUT_FILE_PREFIX = "outputFilePrefix";
	public static final String OUTPUT_FORMAT_TRANSFORMER = "outputFormatTransformer";
	public static final String POSTPROCESSOR = "postProcessor";
	public static final String PREPROCESSOR = "preProcessor";
	public static final String PROMO_PRICE_MESSAGE = "promoPriceMessage";
	public static final String RESPONSE_REPORT_PREFIX = "responseReportPrefix";
	public static final String SAFETY_MARGIN = "safetyMargin";
	public static final String WEB_PRICE_GRADE = "webPriceGrade";
	public static final String WEB_PRICE_MESSAGE = "webPriceMessage";
	public static final String WEBSITE_GROUP_DOWNLOAD_URL = "webSiteGroupDownloadUrl";
	public static final String WEB_WIDGET_INPUT_FILE = "webWidgetInputFile";
	public static final String WEB_WIDGET_PRODUCTS_DOWNLOAD_URL = "webWidgetProductsDownloadURL";
	public static final String WEB_WIDGET_PRODUCTS_UPLOAD_URL = "webWidgetProductsUploadURL";
	
	/**
	 * Reads the properties file rmIntegration.properties and returns the 
	 * properties object.
	 * 
	 * @return
	 * @throws Exception 
	 */
	public static Properties loadProperties() throws SyncException {
		
		if (properties == null) {
			// Read properties file
			properties = new Properties();

			try {
				if (propertyPath != null) {
					properties.load(new FileInputStream(new File(propertyPath + "/" + PROPERTIES_FILE)));
				} else {
					URL url = ClassLoader.getSystemResource(PROPERTIES_FILE);
					properties.load(new FileInputStream(url.getFile()));
				}
			} catch (FileNotFoundException e) {
				logger.error("Unable to find file " + PROPERTIES_FILE);
				throw new SyncException("Unable to find file " + PROPERTIES_FILE);
			} catch (IOException e) {
				logger.error("Unable to load properties file " + PROPERTIES_FILE);
				throw new SyncException("Unable to load properties file " + PROPERTIES_FILE);
			}
		}
		
		return properties;
	}
	
	/**
	 * Returns a property value based on the key
	 * 
	 * @param propertyKey
	 * @return
	 * @throws SyncException 
	 */
	public static String getProperty(String propertyKey) throws SyncException {
		return loadProperties().getProperty(propertyKey);
	}
	
	public static void init() {
		properties = null;
	}
}
