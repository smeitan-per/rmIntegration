/**
 * 
 */
package com.mtit.entity;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import org.mtit.businessRules.BusinessRuleType;
import org.mtit.businessRules.BusinessRulesDocument;

import com.mtit.process.SyncException;

/**
 * Retrieve the business rules from the businessrules.xml file. 
 * @author Mei
 *
 */
public class BusinessRulesDao {
	
	private static final String BUSINESS_RULE_FILE = "businessRules.xml";
	private static BusinessRulesDocument rules = null;
	private static File file;
	
	
	/**
	 * Get a list of business rules from 
	 * @return
	 * @throws SyncException 
	 */
	public static Map<Integer, BusinessRuleType> getBusinessRules() throws SyncException {
		Map<Integer, BusinessRuleType> rmRule = new TreeMap<Integer, BusinessRuleType>();
		
		rules = getBusinessRulesDoc();
		
		BusinessRuleType[] ruleTypes = rules.getBusinessRules().getBusinessRuleArray();
		for ( int i = 0; i < ruleTypes.length; i++ ) {
			rmRule.put((ruleTypes[i].getSequence()==null)?0:ruleTypes[i].getSequence().intValue(), ruleTypes[i]);
		}

		return rmRule;
	}

	/**
	 * Helper class to delete business rule.
	 * @throws SyncException 
	 */
	public static void delete(BusinessRuleType businessRuleType) throws SyncException {
		rules = getBusinessRulesDoc();
		
		BusinessRuleType[] ruleTypes = rules.getBusinessRules().getBusinessRuleArray();
		for ( int i = 0; i < ruleTypes.length; i++ ) {
			BusinessRuleType ruleType = ruleTypes[i];
			
			if (ruleType.getSequence().equals(businessRuleType.getSequence())) {
				rules.getBusinessRules().removeBusinessRule(i);
			}
		}
		
		save(rules);
	}
	
	/**
	 * Add a new business rule.
	 * @param businessRuleType
	 * @throws SyncException 
	 */
	public static void add(BusinessRuleType businessRuleType) throws SyncException {
		Map<Integer, BusinessRuleType> ruleMap = getBusinessRules();
		
		rules.getBusinessRules().addNewBusinessRule();
		rules.getBusinessRules().setBusinessRuleArray(rules.getBusinessRules().sizeOfBusinessRuleArray()-1, businessRuleType);
		ruleMap.put(businessRuleType.getSequence().intValue(), 
				rules.getBusinessRules().getBusinessRuleArray(rules.getBusinessRules().sizeOfBusinessRuleArray()-1));
	
		// Now for each rule with a sequence number greater than the one being added, add 5 to the digits.
		BusinessRuleType compRuleType = null;
		BigInteger sequence = new BigInteger("1");
		for ( Iterator<Entry<Integer, BusinessRuleType>> itr=ruleMap.entrySet().iterator(); itr.hasNext();  ) {
			compRuleType = itr.next().getValue();
			compRuleType.setSequence(sequence);
			sequence = sequence.add(new BigInteger("5"));
		}
		
		
		save(rules); 
	}
	
	/**
	 * Helper save the business config file
	 * @throws SyncException 
	 */
	public static void save(BusinessRulesDocument rules) throws SyncException {
		try {
			XmlOptions opts = new XmlOptions();
			opts.setSavePrettyPrint();
			opts.setSavePrettyPrintIndent(4);

			rules.save(getFile(), opts);
			
		} catch (IOException e) {
			throw new SyncException("Unable to format pretty print:" + e.getMessage());
		}
	}
	
	/**
	 * Reads the businessRule.xml config file and return the xmlbean BusinessRulesDocument.
	 * 
	 * @return
	 * @throws SyncException 
	 */
	private static BusinessRulesDocument getBusinessRulesDoc() throws SyncException {
		try {
			return BusinessRulesDocument.Factory.parse(getFile());

		} catch (XmlException e) {
			throw new SyncException("Unable to read xml file format " + BUSINESS_RULE_FILE);
		} catch (IOException e) {
			// file does not exist but that's ok. we'll create a new one
			throw new SyncException("No business rules configured yet. Starting one now");
		}
	}
	
	private static File getFile() throws SyncException {
		if (file == null) {
			file = new File(IntProperties.getProperty(IntProperties.CONFIG_LOCATION)+"/"+BUSINESS_RULE_FILE); 
		}
		return file;
	}
}
