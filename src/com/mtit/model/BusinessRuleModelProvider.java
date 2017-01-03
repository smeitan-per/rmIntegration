/**
 * 
 */
package com.mtit.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.mtit.businessRules.BusinessRuleType;

import com.mtit.entity.BusinessRulesDao;
import com.mtit.process.SyncException;

/**
 * @author Mei
 * 
 */
public enum BusinessRuleModelProvider {

	INSTANCE;

	private Map<Integer, BusinessRuleType> rmRule;
	private List<BusinessRuleType> rmRuleList;
	
	private BusinessRuleModelProvider() { 
		rmRuleList = new ArrayList<BusinessRuleType>();
		
		try {
			sortList();
		} catch (SyncException e) {
			e.printStackTrace();
		}
	}

	private void sortList() throws SyncException {
		rmRuleList.clear();
		rmRule = BusinessRulesDao.getBusinessRules();

		for (Iterator<Entry<Integer, BusinessRuleType>> itr=rmRule.entrySet().iterator(); itr.hasNext(); ) {
			Entry<Integer, BusinessRuleType> entry = itr.next();
			rmRuleList.add(entry.getValue());
		}
	}
	
	/**
	 * Get a list of all the rmRules
	 * @return
	 */
	public List<BusinessRuleType> getRMRules() {
		return rmRuleList;
	}

	/**
	 * Add a new business rule type
	 * 
	 * @param ruletype
	 * @throws SyncException 
	 */
	public void add(BusinessRuleType ruletype) throws SyncException {
		BusinessRulesDao.add(ruletype);
		sortList();
		
	}
	
	/**
	 * Delete the business rule type.
	 * @param businessRuleType
	 * @throws SyncException 
	 */
	public void delete(BusinessRuleType businessRuleType) throws SyncException {
		rmRuleList.remove(businessRuleType);
		rmRule.remove(businessRuleType.getSequence().intValue());
		
		BusinessRulesDao.delete(businessRuleType);
	}
	
}
