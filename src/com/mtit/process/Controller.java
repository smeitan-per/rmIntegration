/**
 * 
 */
package com.mtit.process;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.mtit.businessRules.BusinessRuleType;

import com.mtit.entity.BusinessRulesDao;
import com.mtit.entity.IntProperties;
import com.mtit.entity.RMDao;
import com.mtit.entity.RetailManagerObject;
import com.mtit.postprocessor.IPostProcessor;
import com.mtit.preprocessor.IPreprocessor;
import com.mtit.transformer.ITransformer;
import com.mtit.utils.LoggerFactory;

/**
 * Controller process that is the starting point for starting off the synchronize process.
 * 
 * @author Mei
 *
 */
public class Controller {

	public static Logger logger = LoggerFactory.getLogger(Controller.class);
	
	/**
	 * Start the process to synchronize, which includes a preprocessor and a postprocessor
	 * if they are configured.
	 * @throws SyncException 
	 */
	public static void synchronize() throws SyncException {
		
		String postprocessorProp = IntProperties.getProperty(IntProperties.POSTPROCESSOR);
		String deleteFilePath = IntProperties.getProperty(IntProperties.DELETE_FILE_PREFIX);

		String genFile = generateFile();

		try {
			if (postprocessorProp != null ) {
				IPostProcessor postProcessor = (IPostProcessor) Class.forName(postprocessorProp).newInstance();
				postProcessor.setInputFile(genFile);
				postProcessor.setDeleteFile(deleteFilePath);
				postProcessor.process();
			}
		} catch (InstantiationException e) {
			throw new SyncException("Cannot instantiate class " + postprocessorProp);
		} catch (IllegalAccessException e) {
			throw new SyncException("Cannot access class " + postprocessorProp);
		} catch (ClassNotFoundException e) {
			throw new SyncException("Unable to find class " + postprocessorProp);
		}
	}
	
	/**
	 * Start the process to generate an output file
	 * @return 
	 * @throws SyncException 
	 */
	public static String generateFile() throws SyncException {
		// Start the preprocessor if exists.
		String preprocessorProp = IntProperties.getProperty(IntProperties.PREPROCESSOR);
		
		try {		
			if (preprocessorProp != null) {
				IPreprocessor preprocessor = (IPreprocessor) Class.forName(preprocessorProp).newInstance();
				preprocessor.process();
			}
		} catch (InstantiationException e) {
			throw new SyncException("Cannot instantiate class " + preprocessorProp);
		} catch (IllegalAccessException e) {
			throw new SyncException("Cannot access class " + preprocessorProp);
		} catch (ClassNotFoundException e) {
			throw new SyncException("Unable to find class " + preprocessorProp);
		}
		
		// Get the MYOB data
		String outputFile=null;
		String deleteFile=null;
		String outputFormat=null;
		
		try {
			logger.info("Started at " + new Date(System.currentTimeMillis()));
			RMDao dao = new RMDao();
			RMManager manager = new RMManager();

			// Read the web widgets object into the memory.
			outputFormat=IntProperties.getProperty(IntProperties.OUTPUT_FORMAT_TRANSFORMER);
			
			ITransformer transformer = (ITransformer) Class.forName(outputFormat).newInstance();
			
			logger.info("Start getRMList at " + new Date(System.currentTimeMillis()));
			List<RetailManagerObject> rmList = dao.getRMList();
			logger.info("Stop getRMList at " + new Date(System.currentTimeMillis()));
			
			Map<Integer, BusinessRuleType> businessRules = BusinessRulesDao.getBusinessRules();
			
			logger.info("Start applying rules at " + new Date(System.currentTimeMillis()));

			outputFile = IntProperties.getProperty(IntProperties.OUTPUT_FILE_PREFIX);
			deleteFile = IntProperties.getProperty(IntProperties.DELETE_FILE_PREFIX);
			
			// Add a date at the end of the file.
			outputFile += ".csv";
			
			transformer.setOutputFile(outputFile);
			transformer.setDeleteFile(deleteFile);
			
			for (Iterator<RetailManagerObject> itr=rmList.iterator(); itr.hasNext(); ) {
				RetailManagerObject rmObject = (RetailManagerObject) itr.next();

				rmObject.setSpecialMessage("");
				
				// Apply business rules
				manager.applyStockRule(businessRules, rmObject);
				
				// Apply promotional pricing if it exists, if not then look to see if price grading 
				// is configured.
				boolean promoPriceApplicable = manager.applyPromotionalPrice(rmObject);
				
				if (!promoPriceApplicable) {
					// Apply price grading
					manager.applyPriceGrades(rmObject);
				}

				transformer.transform(rmObject);
			}
			
			logger.info("Stop applying rules at " + new Date(System.currentTimeMillis()));
			transformer.writeOutput();
			logger.info("Stop writing output at " + new Date(System.currentTimeMillis()));
		} catch (InstantiationException e) {
			e.printStackTrace();
			throw new SyncException("Unable to instantiate class " + outputFormat);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new SyncException("Unable to access class " + outputFormat);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new SyncException("Unable to find class " + outputFormat);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return outputFile;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			IntProperties.propertyPath = args[0];
			generateFile();
//			synchronize();
		} catch (SyncException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
