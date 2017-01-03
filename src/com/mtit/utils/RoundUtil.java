/**
 * 
 */
package com.mtit.utils;

import java.math.BigDecimal;

/**
 * @author Mei
 *
 */
public class RoundUtil {
	/**
	 * Provides helper function to round double to 2 decimal places.
	 * 
	 * @param value
	 * @return
	 */
	public static double round(double value) {
		BigDecimal bdvalue = new BigDecimal(String.valueOf(value));
		BigDecimal rounded = bdvalue.setScale(2, BigDecimal.ROUND_HALF_UP);
		return rounded.doubleValue();
	}
}
