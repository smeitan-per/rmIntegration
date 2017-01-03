package com.mtit.entity;

import java.util.Date;


/**
 * Retail Manager object 
 * 
 * @author Mei
 *
 */
public class RetailManagerObject {

	private String barcode;
	private int stockId;
	private String deptName;
	private String description;
	private String category1;
	private String category2;
	private String category3;
	private String supplier;
	private double cost;
	private double sellPrice;
	private double percentage;
	private int quantity;
	private boolean staticQuantity;
	private int webQuantity;
	private double webPrice;
	private double webPreDiscountPrice;
	private double webPriceA;
	private double webPriceB;
	private double webPriceC;
	private double webPriceD;
	private Date saleEndDate;
	private String specialMessage;
	private String custom2;
	private String longDesc;
	private int uom;
	private String weight;
	private boolean isFreight;
	
	/**
	 * public blank constructor
	 */
	public RetailManagerObject() {
		super();
	}
	
	/**
	 * Constructor
	 * 
	 * @param barcode
	 * @param stockId
	 * @param deptId
	 * @param description
	 * @param category1
	 * @param category2
	 * @param category3
	 * @param cost
	 * @param sellPrice
	 * @param percentage
	 * @param quantity
	 * @param staticQuantity
	 */
	public RetailManagerObject(String barcode, int stockId, String deptName, String description,
			String category1, String category2, String category3, double cost, double sellPrice,
			double percentage, int quantity, boolean staticQuantity) {
		this.barcode = barcode;
		this.stockId = stockId;
		this.deptName = deptName;
		this.description = description;
		this.category1 = category1;
		this.category2 = category2;
		this.category3 = category3;
		this.cost = cost;
		this.sellPrice = sellPrice;
		this.percentage = percentage;
		this.quantity = quantity;
		this.staticQuantity = staticQuantity;
	
	}

	/**
	 * @return the barcode
	 */
	public String getBarcode() {
		return barcode;
	}

	/**
	 * @param barcode the barcode to set
	 */
	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	/**
	 * @return the stockId
	 */
	public int getStockId() {
		return stockId;
	}

	/**
	 * @param stockId the stockId to set
	 */
	public void setStockId(int stockId) {
		this.stockId = stockId;
	}

	/**
	 * @return the deptName
	 */
	public String getDeptName() {
		return deptName;
	}

	/**
	 * @param deptName the deptName to set
	 */
	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the category1
	 */
	public String getCategory1() {
		return category1;
	}

	/**
	 * @param category1 the category1 to set
	 */
	public void setCategory1(String category1) {
		this.category1 = category1;
	}

	/**
	 * @return the category2
	 */
	public String getCategory2() {
		return category2;
	}

	/**
	 * @param category2 the category2 to set
	 */
	public void setCategory2(String category2) {
		this.category2 = category2;
	}

	/**
	 * @return the category3
	 */
	public String getCategory3() {
		return category3;
	}

	/**
	 * @param category3 the category3 to set
	 */
	public void setCategory3(String category3) {
		this.category3 = category3;
	}

	/**
	 * @return the supplier
	 */
	public String getSupplier() {
		return supplier;
	}

	/**
	 * @param supplierId the supplierId to set
	 */
	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}

	/**
	 * @return the cost
	 */
	public double getCost() {
		return cost;
	}

	/**
	 * @param cost the cost to set
	 */
	public void setCost(double cost) {
		this.cost = cost;
	}

	/**
	 * @return the sellPrice
	 */
	public double getSellPrice() {
		return sellPrice;
	}

	/**
	 * @param sellPrice the sellPrice to set
	 */
	public void setSellPrice(double sellPrice) {
		this.sellPrice = sellPrice;
	}

	/**
	 * @return the percentage
	 */
	public double getPercentage() {
		return percentage;
	}

	/**
	 * @param percentage the percentage to set
	 */
	public void setPercentage(double percentage) {
		this.percentage = percentage;
	}

	/**
	 * @return the quantity
	 */
	public int getQuantity() {
		return quantity;
	}

	/**
	 * @param quantity the quantity to set
	 */
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	/**
	 * @return the staticQuantity
	 */
	public boolean isStaticQuantity() {
		return staticQuantity;
	}

	/**
	 * @param staticQuantity the staticQuantity to set
	 */
	public void setStaticQuantity(boolean staticQuantity) {
		this.staticQuantity = staticQuantity;
	}

	/**
	 * @return the webQuantity
	 */
	public int getWebQuantity() {
		return webQuantity;
	}

	/**
	 * @param webQuantity the webQuantity to set
	 */
	public void setWebQuantity(int webQuantity) {
		this.webQuantity = webQuantity;
	}

	/**
	 * @return the webPrice
	 */
	public double getWebPrice() {
		return webPrice;
	}

	/**
	 * @param webPrice the webPrice to set
	 */
	public void setWebPrice(double webPrice) {
		this.webPrice = webPrice;
	}

	/**
	 * @return the webPreDiscountPrice
	 */
	public double getWebPreDiscountPrice() {
		return webPreDiscountPrice;
	}

	/**
	 * @param webPreDiscountPrice the webPreDiscountPrice to set
	 */
	public void setWebPreDiscountPrice(double webPreDiscountPrice) {
		this.webPreDiscountPrice = webPreDiscountPrice;
	}

	/**
	 * @return the webPriceA
	 */
	public double getWebPriceA() {
		return webPriceA;
	}

	/**
	 * @param webPriceA the webPriceA to set
	 */
	public void setWebPriceA(double webPriceA) {
		this.webPriceA = webPriceA;
	}

	/**
	 * @return the webPriceB
	 */
	public double getWebPriceB() {
		return webPriceB;
	}

	/**
	 * @param webPriceB the webPriceB to set
	 */
	public void setWebPriceB(double webPriceB) {
		this.webPriceB = webPriceB;
	}

	/**
	 * @return the webPriceC
	 */
	public double getWebPriceC() {
		return webPriceC;
	}

	/**
	 * @param webPriceC the webPriceC to set
	 */
	public void setWebPriceC(double webPriceC) {
		this.webPriceC = webPriceC;
	}

	/**
	 * @return the webPriceD
	 */
	public double getWebPriceD() {
		return webPriceD;
	}

	/**
	 * @param webPriceD the webPriceD to set
	 */
	public void setWebPriceD(double webPriceD) {
		this.webPriceD = webPriceD;
	}

	/**
	 * @return the saleEndDate
	 */
	public Date getSaleEndDate() {
		return saleEndDate;
	}

	/**
	 * @param saleEndDate the saleEndDate to set
	 */
	public void setSaleEndDate(Date saleEndDate) {
		this.saleEndDate = saleEndDate;
	}

	/**
	 * @return
	 */
	public String getSpecialMessage() {
		return specialMessage;
	}

	/**
	 * @param specialMessage
	 */
	public void setSpecialMessage(String specialMessage) {
		this.specialMessage = specialMessage;
	}

	public String getCustom2() {
		return custom2;
	}

	public void setCustom2(String custom2) {
		this.custom2 = custom2;
	}

	public String getLongDesc() {
		return longDesc;
	}

	public void setLongDesc(String longdesc) {
		this.longDesc = longdesc;
	}

	public int getUom() {
		return uom;
	}

	public void setUom(int uom) {
		this.uom = uom;
	}

	public String getWeight() {
		return weight;
	}

	public void setWeight(String weight) {
		this.weight = weight;
	}

	public boolean isFreight() {
		return isFreight;
	}

	public void setFreight(boolean freight) {
		this.isFreight = freight;
	}
}
