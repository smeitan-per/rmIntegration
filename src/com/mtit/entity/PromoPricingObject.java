package com.mtit.entity;

import java.util.Date;

/**
 * MYOB Promotional Pricing object for storing promotional pricing information.
 * 
 * @author Mei
 * 
 */
public class PromoPricingObject {

	private int stockId;
	private Date endDate;
	private int pricingValue;

	/**
	 * @return the stockId
	 */
	public int getStockId() {
		return stockId;
	}

	/**
	 * @param stockId
	 *            the stockId to set
	 */
	public void setStockId(int stockId) {
		this.stockId = stockId;
	}

	/**
	 * @return the endDate
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate
	 *            the endDate to set
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * @return the pricingValue
	 */
	public int getPricingValue() {
		return pricingValue;
	}

	/**
	 * @param pricingValue
	 *            the pricingValue to set
	 */
	public void setPricingValue(int pricingValue) {
		this.pricingValue = pricingValue;
	}

}
