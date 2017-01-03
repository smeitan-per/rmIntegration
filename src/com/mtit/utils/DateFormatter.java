package com.mtit.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Helper class to format a date into a given format.
 * 
 * @author Mei
 *
 */
public class DateFormatter {

	/**
	 * Helper method to format date to a given format.
	 * 
	 * @param date
	 * @param dateFormat
	 * @return
	 */
	public static String formatDate(Date date, String dateFormat) {
		
		SimpleDateFormat df = new SimpleDateFormat(dateFormat);
		return df.format(date);
		
	}
}
