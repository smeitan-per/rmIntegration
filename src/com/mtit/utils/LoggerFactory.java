package com.mtit.utils;

import org.apache.log4j.Logger;

/**
 * Logger Factory for creating a log file entry.
 * 
 * @author Mei
 * 
 */
public class LoggerFactory {

	public static Logger getLogger(Class clazz) {
		return getLogger(clazz.getName());
	}

	public static Logger getLogger(String categoryName) {
		return Logger.getLogger(categoryName);
	}
}
