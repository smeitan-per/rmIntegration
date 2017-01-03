/**
 * 
 */
package com.mtit.entity;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.mtit.businessRules.BusinessRuleType;
import org.mtit.businessRules.BusinessRulesDocument;
import org.mtit.businessRules.BusinessRulesDocument.BusinessRules;

import com.mtit.process.SyncException;
import com.mtit.utils.LoggerFactory;
import com.mtit.utils.RoundUtil;

/**
 * DAO Class for accessing the Access database and retrieving objects into
 * memory.
 * 
 * @author Mei
 * 
 */
public class RMDao {

	public static final String DATA_SOURCE_NAME = "dataSourceName";

	public static final String query = "select s.Barcode, s.stock_id, d.dept_name, s.description, cat1, cat2, cv.description as cat3,"
			+ " sp.supplier, cost, percentage, sell, quantity as stockquantity, "
			+ " static_quantity, s.custom2, longdesc, unitof_measure, tare_weight, freight"
			+ " from Stock s, TaxCodes t, categorisedStock c, categoryValues cv, "
			+ " Departments d, supplier sp "
			+ " where s.sales_tax = t.code "
			+ " and s.stock_id = c.stock_id "
			+ " and s.dept_id = c.dept_id "
			+ " and d.dept_id = s.dept_id "
			+ " and s.supplier_id = sp.supplier_id"
			+ " and c.category_level = 3"
			+ " and c.catvalue_id = cv.catvalue_id"
			+ " and s.barcode <> '<dft>' " + " and s.barcode <>  '<del>'"
			+ " and s.inactive = false";

	private Connection conn;

	private Logger logger = LoggerFactory.getLogger(RMDao.class);
	
	/**
	 * Constructor
	 */
	public RMDao() throws SyncException {
		super();
		getConnection();
	}

	/**
	 * Get the connection.
	 * @throws SyncException 
	 * 
	 * @throws Exception
	 */
	public void getConnection() throws SyncException {

		String dataSourceName = IntProperties.getProperty(IntProperties.DATA_SOURCE_NAME);
		String dbURL = "jdbc:ucanaccess://"+dataSourceName+";jackcessOpener=com.mtit.entity.CryptoCodecOpener";

		try {
			conn = DriverManager.getConnection(dbURL, "", "");
		} catch (SQLException e) {
			throw new SyncException("Encountered SQL error when connecting to ODBC connection:"+ e.getMessage());
		}

	}

	/**
	 * Main class to retrieve the transactions from the Access DB and return as
	 * a list.
	 * 
	 * @return
	 * @throws SyncException 
	 * @throws Exception
	 */
	public List<RetailManagerObject> getRMList() throws SyncException {

		List<RetailManagerObject> returnList = new ArrayList<RetailManagerObject>();

		
		Statement s=null;
		try {
			s = conn.createStatement();


			s.execute(query);

			ResultSet rs = s.getResultSet();

			while (rs.next()) {
				RetailManagerObject rmo = new RetailManagerObject();
				rmo.setBarcode(rs.getString(1));
				rmo.setStockId(rs.getInt(2));
				rmo.setDeptName(rs.getString(3));
				rmo.setDescription(rs.getString(4));
				rmo.setCategory1(rs.getString(5));
				rmo.setCategory2(rs.getString(6));
				rmo.setCategory3(rs.getString(7));
				rmo.setSupplier(rs.getString(8));
				rmo.setCost(rs.getDouble(9));
				rmo.setPercentage(rs.getDouble(10));
				rmo.setSellPrice(rs.getDouble(11)*(100+rmo.getPercentage())/100);
				rmo.setQuantity(rs.getInt(12));
				rmo.setStaticQuantity(rs.getBoolean(13));
				rmo.setCustom2(rs.getString(14));
				rmo.setLongDesc(rs.getString(15));
				rmo.setUom(rs.getInt(16));
				rmo.setWeight(rs.getString(17));
				rmo.setFreight(rs.getBoolean(18));

				// Default the web price and quantity to the MYOB sell price and
				// quantity first.
				// If required, this will get overridden later on.
				rmo.setWebQuantity(rmo.getQuantity());
				rmo.setWebPrice(RoundUtil.round(rmo.getSellPrice()));
				returnList.add(rmo);
			}
		} catch (SQLException e) {
			throw new SyncException("Unable to get list of products from MYOB:"+e.getMessage());
		} finally {
			if (s!= null) {
				try {
					s.close();
				} catch (SQLException e) {
					logger.error("Unable to close SQL statement connection");
				}
			}
		}
		return returnList;
	}
	
	/**
	 * Returns a list of barcodes.
	 * 
	 * @return
	 * @throws SyncException
	 */
	public List<String> getBarcodes() throws SyncException {

		List<String> returnList = new ArrayList<String>();
		String query="select barcode from stock s where s.barcode <> '<dft>' and s.barcode <>  '<del>'";
		
		Statement s=null;
		try {
			s = conn.createStatement();


			s.execute(query);

			ResultSet rs = s.getResultSet();

			while (rs.next()) {
				String barcode = rs.getString(1);
				if (barcode != null)
				returnList.add(barcode.toUpperCase());
			}
		} catch (SQLException e) {
			throw new SyncException("Unable to get list of barcodes from MYOB:"+e.getMessage());
		} finally {
			if (s!= null) {
				try {
					s.close();
				} catch (SQLException e) {
					logger.error("Unable to close SQL statement connection");
				}
			}
		}
		return returnList;
	}

	/**
	 * Get a list of all the departments in the database. For the first item in
	 * the array, add "all".
	 * 
	 * @return
	 * @throws SyncException 
	 */
	public String[] getDepartments() throws SyncException {
		String query = "select dept_name from departments order by dept_name";
		String[] departments = null;

		Statement s = null;
		try {
			s = conn.createStatement();
			s.execute(query);
			ResultSet rs = s.getResultSet();

			departments = new String[rs.getFetchSize()];

			List<String> departmentList = new ArrayList<String>();
			departmentList.add("all");
			while (rs.next()) {
				departmentList.add(rs.getString(1));
			}
			departments = departmentList.toArray(departments);

		} catch (SQLException e) {
			throw new SyncException("Unable to retrieve departments list:"+ e.getMessage());
		} finally {
			if (s != null) {
				try {
					s.close();
				} catch (SQLException e) {
					logger.error("UNable to close SQL connection");
				}
			}
		} 

		return departments;
	}

	/**
	 * Get list of suppliers
	 * 
	 * @return
	 * @throws SyncException 
	 */
	public String[] getSuppliers() throws SyncException {
		String query = "select supplier from Supplier order by supplier";
		String[] suppliers = null;

		Statement s = null;
		try {
			s = conn.createStatement();
			s.execute(query);
			ResultSet rs = s.getResultSet();

			suppliers = new String[rs.getFetchSize()];

			List<String> supplierList = new ArrayList<String>();
			supplierList.add("all");
			while (rs.next()) {
				supplierList.add(rs.getString(1));
			}
			suppliers = supplierList.toArray(suppliers);

		} catch (SQLException e) {
			throw new SyncException("Unable to get supplier list:" + e.getMessage());
		} finally {
			if (s != null) {
				try {
					s.close();
				} catch (SQLException e) {
					logger.error("Unable to close connection");
				}
			}
		}

		return suppliers;
	}

	/**
	 * Returns a list of categories
	 * 
	 * @return
	 * @throws SyncException 
	 */
	public Map<Integer, String[]> getCategoriesByDept(String deptName) throws SyncException {
		
		String[] category1 = null;
		String[] category2 = null;
		String[] category3 = null;

		List<String> cat1List = populateCat1(deptName);
		List<String> cat2List = populateCat2(deptName);
		List<String> cat3List = populateCat3(deptName);

		Map<Integer, String[]> categoryMap = new HashMap<Integer, String[]>();

		if (cat1List != null) {
			category1 = new String[cat1List.size()];
			cat1List.toArray(category1);
		}
		
		if (cat2List != null) {
			category2 = new String[cat2List.size()];
			cat2List.toArray(category2);
		}
		
		if (cat3List != null) {
			category3 = new String[cat3List.size()];
			cat3List.toArray(category3);
		}
			
		categoryMap.put(1, category1);
		categoryMap.put(2, category2);
		categoryMap.put(3, category3);
		return categoryMap;
	}

	/**
	 * Method to retrieve all category 1 list.
	 * 
	 * @param deptName
	 * @return
	 * @throws SyncException
	 */
	public List<String> populateCat1(String deptName) throws SyncException {

		List<String> cat1List = new ArrayList<String>();
		cat1List.add("all");
		
		String query = "select distinct cat1 "
			+ " from stock s, departments d "
			+ " where s.dept_id = d.dept_id ";
		
		if (deptName != null) {
			query += " and d.dept_name = ? ";
		}
		query += " order by cat1";

		PreparedStatement s = null;
		try {
			s = conn.prepareStatement(query);
			if (deptName != null) {
				s.setString(1, deptName);
			}
			s.execute();

			ResultSet rs = s.getResultSet();

			while (rs.next()) {
				cat1List.add(rs.getString(1));
			}
		} catch (SQLException e) {
			throw new SyncException("Unable to get category 1 list:"+ e.getMessage());
		} finally {
			if (s != null) {
				try {
					s.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return cat1List;
	}

	/**
	 * MEthod to retrieve category 2 list from the stock table.
	 * @param deptName
	 * @return
	 * @throws SyncException 
	 */
	public List<String> populateCat2(String deptName) throws SyncException {

		List<String> cat2List = new ArrayList<String>();
		cat2List.add("all");
		
		String query = "select distinct cat2 "
			+ " from stock s, departments d "
			+ " where s.dept_id = d.dept_id ";
		
		if (deptName != null) {
			query += " and d.dept_name = ? ";
		}

		query += " order by cat2";

		PreparedStatement s = null;
		try {
			s = conn.prepareStatement(query);
			if (deptName != null) {
				s.setString(1, deptName);
			}
			s.execute();

			ResultSet rs = s.getResultSet();

			while (rs.next()) {
				cat2List.add(rs.getString(1));
			}
		} catch (SQLException e) {
			throw new SyncException("Unable to get category 2 list:"+ e.getMessage());
		} finally {
			if (s != null) {
				try {
					s.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return cat2List;
	}
	
	/**
	 * Method to retrieve category3 list from the categorisedstock table.
	 * @param deptName
	 * @return
	 * @throws SyncException
	 */
	public List<String> populateCat3(String deptName) throws SyncException {

		List<String> cat3List = new ArrayList<String>();
		cat3List.add("all");
		
		String query3 = "select distinct description "
			+ " from categoryValues cv, categorisedStock cs, departments d "
			+ " where cv.catvalue_id = cs.catvalue_id "
			+ " and cs.dept_id = d.dept_id "
			+ " and cs.category_level = 3 ";
		
		if (deptName != null) {
			query3 += "and d.dept_name = ? ";
		}
		
		query3 += " order by description";

		PreparedStatement s = null;
		try {
			s = conn.prepareStatement(query3);
			
			if (deptName != null) {
				s.setString(1, deptName);
			}
			s.execute();

			ResultSet rs = s.getResultSet();

			while (rs.next()) {
				cat3List.add(rs.getString(1));
			}
		} catch (SQLException e) {
			throw new SyncException("Unable to get category 3 list:"+ e.getMessage());
		} finally {
			if (s != null) {
				try {
					s.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return cat3List;
	}
	
	/**
	 * Get list of suppliers
	 * 
	 * @return
	 */
	public Map<Integer, PromoPricingObject> getPromotionalItems() {
		String query = "select pi.stock_id, p.end_date, p.pricing_value "
				+ " from promotions p, promotion_items pi "
				+ " where p.promotion_id = pi.promotion_id "
				+ " and p.status = 1 " + " and p.start_date <= Date() "
				+ " and p.end_date >= Date()";

		Map<Integer, PromoPricingObject> promoPrices = new HashMap<Integer, PromoPricingObject>();

		Statement s = null;
		try {
			s = conn.createStatement();
			s.execute(query);
			ResultSet rs = s.getResultSet();

			while (rs.next()) {
				PromoPricingObject ppObj = new PromoPricingObject();
				ppObj.setStockId(rs.getInt(1));
				ppObj.setEndDate(rs.getDate(2));
				ppObj.setPricingValue(rs.getInt(3));
				promoPrices.put(ppObj.getStockId(), ppObj);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (s != null) {
				try {
					s.close();
				} catch (SQLException e) {
					logger.error("Unable to close SQL connection");
				}
			}
		}

		return promoPrices;
	}

	/**
	 * Get a map of all the stock level price grade object.
	 * 
	 * @return
	 * @throws SyncException 
	 */
	public Map<Integer, PriceGradeObject> getStockPriceGrade() throws SyncException {
		Map<Integer, PriceGradeObject> priceGradeMap = new HashMap<Integer, PriceGradeObject>();

		String query = "select stock_id, ruleA, ruleB, ruleC, ruleD, " +
				"valueA, valueB, valueC, valueD from pricing_stock";

		Statement s = null;
		try {
			s = conn.createStatement();
			s.execute(query);
			ResultSet rs = s.getResultSet();

			while (rs.next()) {
				PriceGradeObject obj = new PriceGradeObject();
				int stockId = rs.getInt(1);
				
				obj.setPriceGradeA(rs.getInt(2));
				obj.setPriceGradeB(rs.getInt(3));
				obj.setPriceGradeC(rs.getInt(4));
				obj.setPriceGradeD(rs.getInt(5));
				obj.setValueA(rs.getFloat(6));
				obj.setValueB(rs.getFloat(7));
				obj.setValueC(rs.getFloat(8));
				obj.setValueD(rs.getFloat(9));
				
				priceGradeMap.put(stockId, obj);
			}

		} catch (SQLException e) {
			throw new SyncException("Unable to get price grades:" + e.getMessage());
		} finally {
			if (s != null) {
				try {
					s.close();
				} catch (SQLException e) {
					logger.error("Unable to close SQL connection");
				}
			}
		}
		return priceGradeMap;

	}

	/**
	 * Get a list of all the category level price grade object.
	 * 
	 * @return
	 * @throws SyncException 
	 */
	public List<PriceGradeObject> getCategoryPriceGrade() throws SyncException {
		List<PriceGradeObject> priceGrades = new ArrayList<PriceGradeObject>();

		String query = "select dept_name, cat1, cat2, cat3, ruleA, ruleB, ruleC, ruleD, " +
				"valueA, valueB, valueC, valueD from pricing_categories";

		Statement s = null;
		try {
			s = conn.createStatement();
			s.execute(query);
			ResultSet rs = s.getResultSet();

			while (rs.next()) {
				PriceGradeObject obj = new PriceGradeObject();
				obj.setDeptName(rs.getString(1));
				obj.setCat1(rs.getString(2));
				obj.setCat2(rs.getString(3));
				obj.setCat3(rs.getString(4));
				obj.setPriceGradeA(rs.getInt(5));
				obj.setPriceGradeB(rs.getInt(6));
				obj.setPriceGradeC(rs.getInt(7));
				obj.setPriceGradeD(rs.getInt(8));
				obj.setValueA(rs.getFloat(9));
				obj.setValueB(rs.getFloat(10));
				obj.setValueC(rs.getFloat(11));
				obj.setValueD(rs.getFloat(12));
				
				priceGrades.add(obj);
			}

		} catch (SQLException e) {
			throw new SyncException("Unable to get category price grades:"+e.getMessage());
		} finally {
			if (s != null) {
				try {
					s.close();
				} catch (SQLException e) {
					logger.error("Unable to close SQL connection");
				}
			}
		}
		return priceGrades;

	}

	/**
	 * Get the default price grade object.
	 * 
	 * @return
	 * @throws SyncException 
	 */
	public PriceGradeObject getDefaultPriceGrade() throws SyncException {
		PriceGradeObject obj = null;

		String query = "select '0', ruleA, ruleB, ruleC, ruleD, " +
				"valueA, valueB, valueC, valueD from pricing_global";

		Statement s = null;
		try {
			s = conn.createStatement();
			s.execute(query);
			ResultSet rs = s.getResultSet();

			while (rs.next()) {
				obj = new PriceGradeObject();
				
				obj.setCat3(rs.getString(1));
				obj.setPriceGradeA(rs.getInt(2));
				obj.setPriceGradeB(rs.getInt(3));
				obj.setPriceGradeC(rs.getInt(4));
				obj.setPriceGradeD(rs.getInt(5));
				obj.setValueA(rs.getFloat(6));
				obj.setValueB(rs.getFloat(7));
				obj.setValueC(rs.getFloat(8));
				obj.setValueD(rs.getFloat(9));
				
			}

		} catch (SQLException e) {
			throw new SyncException("Unable to get default price grade:" + e.getMessage());
		} finally {
			if (s != null) {
				try {
					s.close();
				} catch (SQLException e) {
					logger.error("Unable to close SQL connection");
				}
			}
		}
		return obj;

	}

	
	/**
	 * Get list of all department and categories.
	 * 
	 * @return
	 * @throws SyncException 
	 */
	public BusinessRulesDocument getAllDeptAndCategories() throws SyncException {
		String query = "select d.dept_name, cat1, cat2, cv.description as cat3, "
				+ " sp.supplier "
				+ " from Stock s, categorisedStock c, categoryValues cv,"
				+ " Departments d, supplier sp "
				+ " where s.stock_id = c.stock_id "
				+ " and s.dept_id = c.dept_id "
				+ " and d.dept_id = s.dept_id "
				+ " and s.supplier_id = sp.supplier_id "
				+ " and c.category_level = 3"
				+ " and c.catvalue_id = cv.catvalue_id";

		BusinessRulesDocument doc = null;
		Statement s = null;
		try {
			s = conn.createStatement();
			s.execute(query);

			ResultSet rs = s.getResultSet();

			doc = BusinessRulesDocument.Factory.newInstance();
			BusinessRules businessRules = doc.addNewBusinessRules();

			BigInteger i = new BigInteger("1");

			// Set a business rule type to for all first.
			BusinessRuleType allRuleType = businessRules.addNewBusinessRule();
			allRuleType.setSequence(i);
			allRuleType.setDepartment("all");
			allRuleType.setCategory1("all");
			allRuleType.setCategory2("all");
			allRuleType.setCategory3("all");
			allRuleType.setSupplier("all");
			allRuleType.setStockRule("M");

			while (rs.next()) {
				i = i.add(new BigInteger("5"));

				BusinessRuleType ruleType = businessRules.addNewBusinessRule();
				ruleType.setSequence(i);
				ruleType.setDepartment(rs.getString(1));
				ruleType.setCategory1(rs.getString(2));
				ruleType.setCategory2(rs.getString(3));
				ruleType.setCategory3(rs.getString(4));
				ruleType.setSupplier(rs.getString(5));
				ruleType.setStockRule("M");
			}

		} catch (SQLException e) {
			throw new SyncException("Unable to retrieve stocks:"+ e.getMessage());
		} finally {
			if (s != null) {
				try {
					s.close();
				} catch (SQLException e) {
					logger.error("Unable to close SQL connection");
				}
			}
		}
		return doc;
	}
}
