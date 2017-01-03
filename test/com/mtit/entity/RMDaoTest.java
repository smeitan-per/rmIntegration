/**
 * 
 */
package com.mtit.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mtit.entity.RMDao;
import com.mtit.entity.RetailManagerObject;
import com.mtit.process.SyncException;

/**
 * @author Mei
 *
 */
public class RMDaoTest {

	private RMDao dao = null;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		dao = new RMDao();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link com.mtit.entity.RMDao#getRMList()}.
	 */
	@Test
	public void testGetRMList() {
		
		java.util.List<RetailManagerObject> rmList = null;
		try {
			rmList = dao.getRMList();
			
			assertEquals(3398, rmList.size());
			
			for (Iterator<RetailManagerObject> itr=rmList.iterator(); itr.hasNext(); ) {
				RetailManagerObject obj = (RetailManagerObject) itr.next();

				// Check the static quantity field.
				if ("31325GO".equals(obj.getBarcode())) {
						assertTrue("Static quantity is true", obj.isStaticQuantity());
				} else if ("98".equals(obj.getBarcode())){
					assertFalse("Static quantity is false", obj.isStaticQuantity());
				}
				
				// Check percentage is correct
				if ("673101".equals(obj.getBarcode()) ||
						"048419691082".equals(obj.getBarcode()) ||
						"9310280648245".equals(obj.getBarcode()) ||
						"TAXEXEMPT".equals(obj.getBarcode()) ) {
					assertEquals(0, obj.getPercentage(),0);
				} else if ("9310280646173".equals(obj.getBarcode())) {
					assertEquals(15, obj.getPercentage(), 2);
				} else if ("9310720270401".equals(obj.getBarcode())){
					assertEquals(12.5, obj.getPercentage(),2);
				}
				
				// Check a couple of quantity
				if ("071444437448".equals(obj.getBarcode())) {
					assertEquals(-1, obj.getQuantity());
				}
				
				if ("377".equals(obj.getBarcode())) {
					assertEquals(82, obj.getQuantity());
				}
				
				// Check everything for barcode 13
				if ("071444404372".equals(obj.getBarcode())) {
					assertEquals(6710, obj.getStockId());
					assertEquals("Sales", obj.getDeptName());
					assertEquals("Balloon 11\" 5 Around Stars Pk50", obj.getDescription());
					assertEquals("PriBal", obj.getCategory1());
					assertEquals("5", obj.getCategory2());
					assertEquals("<N/A>", obj.getCategory3());
					assertEquals(33.50, obj.getCost(),2);
					assertEquals(45.57, obj.getSellPrice(),2);
					assertEquals(15, obj.getPercentage(),2);
					assertEquals(0, obj.getQuantity());
					assertFalse(obj.isStaticQuantity());
					assertEquals(0, obj.getWebQuantity());
					assertEquals(45.57, obj.getWebPrice(),2);
				}
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertNotNull(rmList);
		assertTrue(rmList.size() > 0);
	}
	
	@Test
	public void testGetDepartments() throws SyncException {
		String[] departments = dao.getDepartments();
		
		assertEquals(8, departments.length);

		assertEquals("all", departments[0]);
		assertEquals("<Default>", departments[1]);
		assertEquals("Eqpt Sale", departments[2]);
		assertEquals("Event", departments[3]);
		assertEquals("Rental", departments[4]);
		assertEquals("Sales", departments[5]);
		assertEquals("Sales Old", departments[6]);
		assertEquals("Service", departments[7]);
	}
}
