/**
 * 
 */
package com.mtit.entity;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import org.mtit.businessRules.CategoriesDocument;
import org.mtit.businessRules.CategoryMappingType;

import com.mtit.process.SyncException;
import com.mtit.utils.LoggerFactory;

/**
 * Retrieves the category3 mapping from the cat3Mapping.xml file. 
 * @author Mei
 *
 */
public class Category3MappingDao extends CategoryMappingDao {
	
	private static final String CAT3_MAPPING_FILE = "cat3Mapping.xml";
	private static Logger logger = LoggerFactory.getLogger(Category3MappingDao.class);
	
	/**
	 * Get a list of category3 mapping from the config file.  
	 * @return
	 */
	public static Map<String, CategoryMappingType> getCat3Mapping() {
		Map<String, CategoryMappingType> titleMaps = new TreeMap<String, CategoryMappingType>();
		List<Integer> catToRemove = new ArrayList<Integer>();
		
		try {
			RMDao dao = new RMDao();
			List<String> dbCategoryList = dao.populateCat3(null);
			
			// First remove the one that says "N/A" or "all"
			dbCategoryList.remove(new String("all"));
			dbCategoryList.remove(new String("<N/A>"));
			
			CategoriesDocument category3 = getCat3Doc();

			if (category3.getCategories() != null) {
				CategoryMappingType[] categoryType = category3.getCategories().getCategoryMappingArray();
				for ( int i = 0; i < categoryType.length; i++ ) {
					// Only add to the map if the category still exists in MYOB, otherwise ignore.
					if (dbCategoryList.contains(categoryType[i].getMYOBCategory())) {
						titleMaps.put(categoryType[i].getMYOBCategory(), categoryType[i]);
					} else {
						catToRemove.add(i);
					}
				}
			} 

			Collections.reverse(catToRemove);
			
			for (Iterator<Integer> itr=catToRemove.listIterator(); itr.hasNext(); ) {
				category3.getCategories().removeCategoryMapping(itr.next());
			}
			refreshFromDB(titleMaps, dbCategoryList, category3);
			save(category3);
		} catch (SyncException e) {
			logger.error("Unable to retrieve category 3 list", e);
		}


		return titleMaps;
	}

	/**
	 * Helper class to delete category Map.
	 */
	public static void delete(CategoryMappingType mapType) {
		CategoriesDocument categories = getCat3Doc();
		
		CategoryMappingType[] mapTypes = categories.getCategories().getCategoryMappingArray();
		
		for ( int i = 0; i < mapTypes.length; i++ ) {
			CategoryMappingType fileMapType = mapTypes[i];
			
			if (mapType.getMYOBCategory().equals(fileMapType.getMYOBCategory())) {
				categories.getCategories().removeCategoryMapping(i);
			}
		}
		
		save(categories);
	}
	
	/**
	 * Add a new category mapping
	 * @param CategoryMappingType
	 */
	public static void add(CategoryMappingType cat3MappingType) {
		CategoriesDocument categories = getCat3Doc();
		
		if (categories.getCategories()==null) {
			categories.addNewCategories();
		}
		categories.getCategories().addNewCategoryMapping();
		categories.getCategories().setCategoryMappingArray(categories.getCategories().sizeOfCategoryMappingArray()-1, cat3MappingType);
	
		save(categories); 
	}
	
	/**
	 * Helper save the title Map config file
	 */
	public static void save(CategoriesDocument categories) {
		try {
			XmlOptions opts = new XmlOptions();
			opts.setSavePrettyPrint();
			opts.setSavePrettyPrintIndent(4);

//			File categoryFile = new File("../config/" + CAT3_MAPPING_FILE); 
			File categoryFile = new File("config/" + CAT3_MAPPING_FILE);

			categories.save(categoryFile, opts);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Reads the cat1Mapping.xml config file and return the xmlbean CategoriesDocument.
	 * 
	 * @return
	 */
	private static CategoriesDocument getCat3Doc() {
		CategoriesDocument categories = null;

		URL url = ClassLoader.getSystemResource(CAT3_MAPPING_FILE);
		
		if (url != null) {
			File cat3MapFile = new File(url.getFile());
			
			try {
				categories = CategoriesDocument.Factory.parse(cat3MapFile);
			} catch (XmlException e) {
				logger.error("Unable to read xml file format " + cat3MapFile.getName());
				e.printStackTrace();
			} catch (IOException e) {
				// file does not exist but that's ok. we'll create a new one
				logger.error("No category 1 mapping configured yet. Starting one now");
			}
		} else {
			categories = CategoriesDocument.Factory.newInstance();
		}

		return categories;
	}
}
