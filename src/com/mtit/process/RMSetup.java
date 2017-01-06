/**
 * 
 */
package com.mtit.process;

import java.io.File;

import org.apache.xmlbeans.XmlOptions;
import org.mtit.businessRules.BusinessRulesDocument;

import com.mtit.entity.IntProperties;
import com.mtit.entity.RMDao;

/**
 * @author Mei
 *
 */
public class RMSetup {

	private static RMDao dao;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		String outputFile=args[0];
		
		try {
			IntProperties.propertyPath = args[1];
			dao = new RMDao();
			
			XmlOptions opts = new XmlOptions();
			opts.setSavePrettyPrint();
			opts.setSavePrettyPrintIndent(4);

			BusinessRulesDocument doc= dao.getAllDeptAndCategories();
			doc.save(new File(outputFile), opts);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}

}
