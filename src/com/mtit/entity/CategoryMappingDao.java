package com.mtit.entity;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.mtit.businessRules.CategoriesDocument;
import org.mtit.businessRules.CategoryMappingType;

import com.mtit.process.SyncException;
import com.mtit.utils.LoggerFactory;

public class CategoryMappingDao {
	
	private static Logger logger = LoggerFactory.getLogger(CategoryMappingDao.class);

	protected static void refreshFromDB(Map<String, CategoryMappingType> titleMaps, List<String> dbCategoryList,
			CategoriesDocument doc)
		throws SyncException {

		
		for (int i=0; i<dbCategoryList.size(); i++) {
			String myobCategory = dbCategoryList.get(i);

			if (!titleMaps.containsKey(myobCategory)) {
				if (doc.getCategories()==null) {
					doc.addNewCategories();
				}
				CategoryMappingType mapType = doc.getCategories().addNewCategoryMapping();
				mapType.setMYOBCategory(myobCategory);
				titleMaps.put(myobCategory, mapType);
			}
		}
	}

	/**
	 * Reads the cat1Mapping.xml config file and return the xmlbean CategoriesDocument.
	 * 
	 * @return
	 * @throws SyncException 
	 */
	protected static CategoriesDocument getCatDoc(String filename) throws SyncException {
		CategoriesDocument categories = null;

		if (getFile(filename).exists()) {
			
			try {
				categories = CategoriesDocument.Factory.parse(getFile(filename));
			} catch (XmlException e) {
				logger.error("Unable to read xml file format " + getFile(filename).getName());
				e.printStackTrace();
			} catch (IOException e) {
				logger.error("No category 1 mapping configured yet. Starting one now");
			}
		} else {
			categories = CategoriesDocument.Factory.newInstance();
		}
		return categories;
	}
	
	protected static File getFile(String filename) throws SyncException {
		return new File(IntProperties.getProperty(IntProperties.CONFIG_LOCATION) + "/" + filename);
	}
}
