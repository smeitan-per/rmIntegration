/**
 * 
 */
package com.mtit.entity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlOptions;
import org.mtit.businessRules.CategoriesDocument;
import org.mtit.businessRules.CategoryMappingType;

import com.mtit.process.SyncException;
import com.mtit.utils.LoggerFactory;

/**
 * Retrieves the category1 mapping from the cat1Mapping.xml file. 
 * @author Mei
 *
 */
public class Category1MappingDao extends CategoryMappingDao {
	
	private static final String CAT1_MAPPING_FILE = "cat1Mapping.xml";
	private static Logger logger = LoggerFactory.getLogger(Category1MappingDao.class);
	
	/**
	 * Get a list of category1 mapping from the config file.  
	 * @return
	 */
	public static Map<String, CategoryMappingType> getCat1Mapping() {
		Map<String, CategoryMappingType> titleMaps = new TreeMap<String, CategoryMappingType>();
		List<Integer> catToRemove = new ArrayList<Integer>();
		
		try {
			RMDao dao = new RMDao();
			List<String> dbCategoryList = dao.populateCat1(null);
			
			// First remove the one that says "N/A" or "all"
			dbCategoryList.remove(new String("all"));
			dbCategoryList.remove(new String("<N/A>"));
			
			CategoriesDocument category1 = getCatDoc(CAT1_MAPPING_FILE);

			if (category1.getCategories() != null) {
				CategoryMappingType[] categoryType = category1.getCategories().getCategoryMappingArray();
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
				category1.getCategories().removeCategoryMapping(itr.next());
			}
			refreshFromDB(titleMaps, dbCategoryList, category1);
			save(category1);
		} catch (SyncException e) {
			logger.error("Unable to retrieve category 1 list", e);
		}


		return titleMaps;
	}

	/**
	 * Helper class to delete category Map.
	 * @throws SyncException 
	 */
	public static void delete(CategoryMappingType mapType) throws SyncException {
		CategoriesDocument categories = getCatDoc(CAT1_MAPPING_FILE);
		
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
	 * @throws SyncException 
	 */
	public static void add(CategoryMappingType cat1MappingType) throws SyncException {
		CategoriesDocument categories = getCatDoc(CAT1_MAPPING_FILE);
		
		if (categories.getCategories()==null) {
			categories.addNewCategories();
		}
		categories.getCategories().addNewCategoryMapping();
		categories.getCategories().setCategoryMappingArray(categories.getCategories().sizeOfCategoryMappingArray()-1, cat1MappingType);
	
		save(categories); 
	}
	
	/**
	 * Helper save the title Map config file
	 * @throws SyncException 
	 */
	public static void save(CategoriesDocument categories) throws SyncException {
		try {
			XmlOptions opts = new XmlOptions();
			opts.setSavePrettyPrint();
			opts.setSavePrettyPrintIndent(4);

			File categoryFile = getFile(CAT1_MAPPING_FILE);

			categories.save(categoryFile, opts);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
