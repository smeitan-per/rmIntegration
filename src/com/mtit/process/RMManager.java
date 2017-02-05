package com.mtit.process;

import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.mtit.businessRules.BusinessRuleType;

import com.mtit.entity.IntProperties;
import com.mtit.entity.PriceGradeObject;
import com.mtit.entity.PromoPricingObject;
import com.mtit.entity.RMDao;
import com.mtit.entity.RetailManagerObject;
import com.mtit.utils.RoundUtil;

public class RMManager {
	private RMDao dao;

	private Map<Integer, PromoPricingObject> promoPrices;
	private Map<Integer, PriceGradeObject> stockPriceGrades;
	private List<PriceGradeObject> categoryPriceGrades;
	private PriceGradeObject defaultPriceGrade;

	/**
	 * Constructor
	 */
	public RMManager() {
		try {
			dao = new RMDao();

			promoPrices = dao.getPromotionalItems();
			stockPriceGrades = dao.getStockPriceGrade();
			categoryPriceGrades = dao.getCategoryPriceGrade();
			defaultPriceGrade = dao.getDefaultPriceGrade();
		} catch (SyncException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Helper method to apply business rules for a given retailManagerObject.
	 * 
	 * @param businessRuleList
	 */
	public void applyStockRule(Map<Integer, BusinessRuleType> businessRules,
			RetailManagerObject rmObject) {

		for (Iterator<Entry<Integer, BusinessRuleType>> itr = businessRules
				.entrySet().iterator(); itr.hasNext();) {
			Entry<Integer, BusinessRuleType> entry = (Entry<Integer, BusinessRuleType>) itr
					.next();
			BusinessRuleType ruleType = (BusinessRuleType) entry.getValue();

			if ((ruleType.getDepartment().equalsIgnoreCase(
					rmObject.getDeptName()) || ruleType.getDepartment()
					.equalsIgnoreCase("all"))
					&& (ruleType.getCategory1().equalsIgnoreCase(
							rmObject.getCategory1()) || ruleType.getCategory1()
							.equalsIgnoreCase("all"))
					&& (ruleType.getCategory2().equalsIgnoreCase(
							rmObject.getCategory2()) || ruleType.getCategory2()
							.equalsIgnoreCase("all"))
					&& (ruleType.getCategory3().equalsIgnoreCase(
							rmObject.getCategory3()) || ruleType.getCategory3()
							.equalsIgnoreCase("all"))
					&& (ruleType.getSupplier().equals(rmObject.getSupplier()) || ruleType
							.getSupplier().equalsIgnoreCase("all")) 
					&& (ruleType.getKeyword() == null || (containsKeyword(rmObject.getDescription(), ruleType.getKeyword())) ) ) {

				if (ruleType.getStockRule().equals("9999")
						|| rmObject.isStaticQuantity()) {
					rmObject.setWebQuantity(9999);
				} else if (ruleType.getStockRule().equals("M")) {
					rmObject.setWebQuantity(rmObject.getQuantity());
				} else if (!("M".equals(ruleType.getStockRule()) || "9999"
						.equals(ruleType.getStockRule()))) {

					rmObject.setWebQuantity(rmObject.getQuantity()
							+ Integer.valueOf(ruleType.getStockRule()));

				}

				if (rmObject.getWebQuantity() < 0) {
					rmObject.setWebQuantity(0);
				}
			}
		}
	}

	/**
	 * Helper method to determine if a description contains a particular keyword
	 * 
	 * @param description
	 * @param keyword
	 * @return
	 */
	private boolean containsKeyword(String description, String keyword) {
		if (description != null && keyword != null
				&& (description.toLowerCase().contains(keyword))) {
			return true;
		}
		return false;
	}

	/**
	 * Apply promotional pricing if it has been configured and is still valid.
	 * 
	 * @param stock
	 * @return
	 * @throws SyncException 
	 */
	public boolean applyPromotionalPrice(RetailManagerObject stock) throws SyncException {

		if (promoPrices.containsKey(stock.getStockId())) {
			double rrp = RoundUtil.round(stock.getSellPrice());
			PromoPricingObject ppobj = promoPrices.get(stock.getStockId());
			int discountPercentage = ppobj.getPricingValue();
			stock.setWebPrice(RoundUtil.round(rrp * (100 + discountPercentage)
					/ 100));
			stock.setWebPreDiscountPrice(RoundUtil.round(rrp));
			if (ppobj.getEndDate().after(Calendar.getInstance().getTime())) {
				stock.setSaleEndDate(ppobj.getEndDate());
			}
			stock.setSpecialMessage(IntProperties.getProperty(IntProperties.PROMO_PRICE_MESSAGE));
			return true;
		} else {
			stock.setSpecialMessage("");
			stock.setSaleEndDate(null);
			return false;
		}
	}

	/**
	 * First check if the web price grade is set. If so, apply the equivalent
	 * price grade rule. After that, look up the other price grades and set to
	 * the priceA, priceB, priceC or priceD fields respectively.
	 * 
	 * @param stock
	 * @throws SyncException 
	 */
	public void applyPriceGrades(RetailManagerObject stock) throws SyncException {
		if (stockPriceGrades.containsKey(stock.getStockId())) {
			applyPriceGrade(stockPriceGrades.get(stock.getStockId()), stock);
		} else {
			boolean isCatPriceGradeApplied = false;
			// Check for category price grade.
			for (Iterator<PriceGradeObject> itr = categoryPriceGrades
					.iterator(); itr.hasNext();) {
				PriceGradeObject categoryPriceGrade = (PriceGradeObject) itr
						.next();

				if (categoryPriceGrade.getDeptName()
						.equals(stock.getDeptName())
						&& categoryPriceGrade.getCat1().equals(
								stock.getCategory1())
						&& categoryPriceGrade.getCat2().equals(
								stock.getCategory2())
						&& categoryPriceGrade.getCat3().equals(
								stock.getCategory3())) {
					applyPriceGrade(categoryPriceGrade, stock);
					isCatPriceGradeApplied = true;
				}
			}

			// Now apply the default priceGrade if this exists and no other
			// price grades have been applied.
			if (!isCatPriceGradeApplied && defaultPriceGrade != null) {
				applyPriceGrade(defaultPriceGrade, stock);
			}

		}
	}

	private void applyPriceGrade(PriceGradeObject priceGradeObject,
			RetailManagerObject stock) throws SyncException {

		if (priceGradeObject.getPriceGradeA() != 0) {
			double value = applyPriceRule(stock,
					priceGradeObject.getPriceGradeA(),
					priceGradeObject.getValueA());

			if (isWebPriceGrade("A")) {
				stock.setWebPrice(value);
				stock.setSpecialMessage(IntProperties
						.getProperty(IntProperties.WEB_PRICE_MESSAGE));
			} else {
				stock.setWebPriceA(value);
			}
		}

		if (priceGradeObject.getPriceGradeB() != 0) {
			double value = applyPriceRule(stock,
					priceGradeObject.getPriceGradeB(),
					priceGradeObject.getValueB());

			if (isWebPriceGrade("B")) {
				stock.setWebPrice(value);
				stock.setSpecialMessage(IntProperties
						.getProperty(IntProperties.WEB_PRICE_MESSAGE));
			} else {
				stock.setWebPriceB(value);
			}
		}

		if (priceGradeObject.getPriceGradeC() != 0) {
			double value = applyPriceRule(stock,
					priceGradeObject.getPriceGradeC(),
					priceGradeObject.getValueC());

			if (isWebPriceGrade("C")) {
				stock.setWebPrice(value);
				stock.setSpecialMessage(IntProperties
						.getProperty(IntProperties.WEB_PRICE_MESSAGE));
			} else {
				stock.setWebPriceC(value);
			}
		}

		if (priceGradeObject.getPriceGradeD() != 0) {
			double value = applyPriceRule(stock,
					priceGradeObject.getPriceGradeD(),
					priceGradeObject.getValueD());

			if (isWebPriceGrade("D")) {
				stock.setWebPrice(value);
				stock.setSpecialMessage(IntProperties
						.getProperty(IntProperties.WEB_PRICE_MESSAGE));
			} else {
				stock.setWebPriceD(value);
			}
		}

		if (priceGradeObject.getPriceGradeA() != 0
				|| priceGradeObject.getPriceGradeB() != 0
				|| priceGradeObject.getPriceGradeC() != 0
				|| priceGradeObject.getPriceGradeD() != 0) {
			stock.setWebPreDiscountPrice(RoundUtil.round(stock.getSellPrice()));
		} else {
			stock.setSpecialMessage("");
		}
	}

	/**
	 * Apply the price grade rule based on the different configuration.
	 * 
	 * @param stock
	 * @param priceGrade
	 * @param priceValue
	 *            when 1=RRP-% when 2=RRP-$ when 3=Fixed $ when 4=Cost+% when
	 *            5=Cost+$
	 * @return
	 */
	private double applyPriceRule(RetailManagerObject stock, int priceGrade,
			double priceValue) {

		double value = 0;
		double rrp = stock.getSellPrice();
		if (priceGrade == 1) {
			value = RoundUtil.round(rrp * (100 - priceValue) / 100);
		} else if (priceGrade == 2) {
			value = RoundUtil.round(rrp - priceValue);
		} else if (priceGrade == 3) {
			value = priceValue;
		} else if (priceGrade == 4) {
			value = stock.getCost() * (100 + priceValue) / 100;
			// Apply the GST
			value = RoundUtil
					.round(value * (100 + stock.getPercentage()) / 100);
		} else if (priceGrade == 5) {
			value = stock.getCost() + priceValue;
			// Apply the GST
			value = RoundUtil
					.round(value * (100 + stock.getPercentage()) / 100);
		}
		return value;
	}

	/**
	 * Get the web price grade to be applied to all stocks.
	 * 
	 * @return
	 * @throws SyncException 
	 * @throws Exception
	 */
	public boolean isWebPriceGrade(String priceGrade) throws SyncException {
		String webPriceGrade = IntProperties.getProperty(IntProperties.WEB_PRICE_GRADE);
		return priceGrade.equals(webPriceGrade);
	}
}
