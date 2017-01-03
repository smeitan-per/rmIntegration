/**
 * 
 */
package com.mtit.process;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mtit.businessRules.BusinessRuleType;

import com.mtit.entity.RMDao;
import com.mtit.entity.RetailManagerObject;
import com.mtit.utils.DateFormatter;

/**
 * @author Mei
 * 
 */
public class RMManagerTest {

	private Map<Integer, BusinessRuleType> businessRules;
	private List<RetailManagerObject> myobObjects;
	private RMManager manager;
	private String dateFormat="dd/MM/yyyy";
	
	/**
	 * @throws java.lang.Exception
	 */
	/**
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		businessRules = new TreeMap<Integer, BusinessRuleType>();

		manager = new RMManager();
		
		// Set up some business rules
		// Set up the default business rule that will apply to all stocks
		BusinessRuleType defaultRuleType = BusinessRuleType.Factory
				.newInstance();
		defaultRuleType.setSequence(new BigInteger("1"));
		defaultRuleType.setCategory1("all");
		defaultRuleType.setCategory2("all");
		defaultRuleType.setCategory3("all");
		defaultRuleType.setDepartment("all");
		defaultRuleType.setStockRangeTitle("Default");
		defaultRuleType.setSupplier("all");
		defaultRuleType.setStockRule("M");

		businessRules.put(defaultRuleType.getSequence().intValue(),
				defaultRuleType);

		// Set up an Acme rule
		BusinessRuleType acmeRuleType = BusinessRuleType.Factory.newInstance();
		acmeRuleType.setSequence(new BigInteger("2"));
		acmeRuleType.setDepartment("Homewares");
		acmeRuleType.setCategory1("Acme");
		acmeRuleType.setCategory2("all");
		acmeRuleType.setCategory3("all");
		acmeRuleType.setSupplier("Sampson Supply Company Pty Ltd");
		acmeRuleType.setStockRangeTitle("Acme default");
		acmeRuleType.setStockRule("9999");

		businessRules.put(acmeRuleType.getSequence().intValue(), acmeRuleType);

		// Set up an Acme Iron specific rule
		BusinessRuleType ironRuleType = BusinessRuleType.Factory.newInstance();
		ironRuleType.setSequence(new BigInteger("3"));
		ironRuleType.setDepartment("Homewares");
		ironRuleType.setCategory1("Acme");
		ironRuleType.setCategory2("Iron");
		ironRuleType.setCategory3("White");
		ironRuleType.setSupplier("Storey and Associates");
		ironRuleType.setStockRangeTitle("Acme Iron");
		ironRuleType.setStockRule("-5");

		businessRules.put(ironRuleType.getSequence().intValue(), ironRuleType);

		RMDao dao = new RMDao();
		myobObjects = dao.getRMList();

	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testApplyRule() {

		for (Iterator<RetailManagerObject> itr = myobObjects.iterator(); itr
				.hasNext();) {
			RetailManagerObject rmObject = (RetailManagerObject) itr.next();

			manager.applyStockRule(businessRules, rmObject);

			// Check barcode 61, for Crackers, should be 104.
			if ("9415869000046".equals(rmObject.getBarcode())) {
				assertEquals(15, rmObject.getQuantity());
				assertEquals(15, rmObject.getWebQuantity());
			}

			if ("SCB".equals(rmObject.getBarcode())) {
				assertEquals(0, rmObject.getQuantity());
				assertEquals(9999, rmObject.getWebQuantity());
			}

//			if ("6".equals(rmObject.getBarcode())) {
//				assertEquals(12, rmObject.getQuantity());
//				assertEquals(7, rmObject.getWebQuantity());
//			}
		}
	}

	@Test
	public void testApplyPromoPrice() throws SyncException {
		for (Iterator<RetailManagerObject> itr=myobObjects.iterator(); itr.hasNext(); ) {
			RetailManagerObject obj = (RetailManagerObject) itr.next();
			manager.applyPromotionalPrice(obj);
			double rrp=3.84d;
			
			if (obj.getStockId()==18) {
				assertEquals(0, obj.getWebPriceA(),0);
				assertEquals(0, obj.getWebPriceB(),0);
				assertEquals(0, obj.getWebPriceC(),0);
				assertEquals(3.46d, obj.getWebPrice(), 0);
				assertEquals(rrp, obj.getWebPreDiscountPrice(), 0);
				
//				Calendar calendar = Calendar.getInstance();
//				calendar.set(Calendar.DAY_OF_MONTH, 31);
//				calendar.set(Calendar.MONTH, 11);
//				calendar.set(Calendar.YEAR, 2012);
//				
//				Calendar resultCal = Calendar.getInstance();
//				resultCal.setTime(obj.getSaleEndDate());
				assertEquals("31/12/2012", DateFormatter.formatDate(obj.getSaleEndDate(), dateFormat));
			}
		}
	}
	
	@Test
	public void testApplyPriceGrade() {

//		for (Iterator<RetailManagerObject> itr=myobObjects.iterator(); itr.hasNext(); ) {
//			RetailManagerObject obj = (RetailManagerObject) itr.next();
//			manager.applyPriceGrades(obj);
//			
//			// Stock level price grading.
//			if (obj.getStockId()==5) {
//				assertEquals(125.31d, obj.getWebPriceA(),0);
//				assertEquals(131.91d, obj.getWebPrice(),0);
//				assertEquals(131.91d, obj.getWebPreDiscountPrice(),0);
//			}
//				 	
//			// Category level price grading
//			if (obj.getStockId() == 2) {
//				assertEquals(873.44d, obj.getWebPriceA(),0);
//				assertEquals(0, obj.getWebPriceB(),0);
//				assertEquals(0, obj.getWebPriceC(),0);
//				assertEquals(0, obj.getWebPriceD(),0);
//				assertEquals(910.67d, obj.getWebPrice(), 0);
//				assertEquals(919.41d, obj.getWebPreDiscountPrice(), 0);
//			}
//			
//			if (obj.getStockId()==18) {
//				assertEquals(0, obj.getWebPriceA(),0);
//				assertEquals(0, obj.getWebPriceB(),0);
//				assertEquals(0, obj.getWebPriceC(),0);
//				assertEquals(0, obj.getWebPriceD(),0);
//				assertEquals(3.64d, obj.getWebPrice(), 0);
//				assertEquals(3.84d, obj.getWebPreDiscountPrice(), 0);
//			}
//			
//			// Freight category
//			if (obj.getStockId()== 20) {
//				assertEquals(0.24d, obj.getWebPriceA(),0);
//				assertEquals(15, obj.getWebPriceB(),0);
//				assertEquals(11.47d, obj.getWebPriceC(),0);
//				assertEquals(0, obj.getWebPriceD(),0);
//				assertEquals(21.49d, obj.getWebPrice(), 0);
//				assertEquals(10.24d, obj.getWebPreDiscountPrice(), 0);
//			}
//		}
		
		
	}
	
//	@Test
//	public void testSynchronize() {
//
//		try {
//			Controller.synchronize();
//		} catch (SyncException e) {
//			e.printStackTrace();
//		}
//
//	}
	
}
