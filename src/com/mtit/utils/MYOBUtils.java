/**
 * 
 */
package com.mtit.utils;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.mtit.entity.TitleMappingDao;
import com.mtit.process.SyncException;

/**
 * Class that provides functionality to map MYOB fields.
 * 
 * @author Mei
 *
 */
public class MYOBUtils {

	// Unit of measure descriptions
	private static final String EACH="Ea";
	private static final String KILOGRAM="kg";
	private static final String GRAM="g";
	private static final String POUND="lb";
	private static final String OUNCE="oz";
	private static final String LITRE="l";
	private static final String MILLILITRE="ml";

	// Title Mapping list
	private static Map<String, String> titleMap = null;
	
	/**
	 * Helper utility to convert MYOB unit of measure to a string to be 
	 * used with WebWidget.
	 * 
	 * @param myobUom
	 * @return
	 */
	public static String getUnitOfMeasure(int myobUom) {
		String uom = null;
		switch (myobUom) {
		case 0:
			uom = EACH;
			break;
		case 1:
			uom = KILOGRAM;
			break;
		case 2:
			uom = GRAM;
			break;
		case 3:
			uom = POUND;
			break;
		case 4:
			uom = OUNCE;
			break;
		case 5:
			uom = LITRE;
			break;
		case 6:
			uom = MILLILITRE;
			break;
		default:
			break;
		}
		
		return uom;
	}

	/**
	 * Returns the title based on what has been set in the Title Mapping xml file.
	 * 
	 * @param description
	 * @return
	 * @throws SyncException 
	 */
	public static String transformTitle(String myobDesc) throws SyncException {
		String returnString = myobDesc;
		if (titleMap == null) {
			titleMap = TitleMappingDao.getTitleMappingString();
		}
		
		for (Iterator<Entry<String, String>> itr=titleMap.entrySet().iterator(); itr.hasNext(); ) {
			Entry<String, String> entry = itr.next();
			
			if (myobDesc.contains(entry.getKey())) {
				// Form the string to be replaced first.
				String key = entry.getKey();

				// Replace all punctuation
				key = key.replaceAll("\\p{Punct}", "\\\\p{Punct}");
				
				if ( !entry.getKey().matches("\\W\\w+\\W")) {
					key = "\\b" + key + "\\b";
				} 

				if ("del".equals(entry.getValue())) {
					returnString = returnString.replaceAll(key, "");
				} else {
					returnString = returnString.replaceAll(key, entry.getValue());
				}
			}
		}
		
		returnString = returnString.replaceAll("\\s\\s", " ");
		return returnString;
	}

}
