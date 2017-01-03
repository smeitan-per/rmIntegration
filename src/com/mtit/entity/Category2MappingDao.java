/**
 * 
 */
package com.mtit.entity;

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
 * Retrieves the category2 mapping from the cat2Mapping.xml file. 
 * @author Mei
 *
 */
public class Category2MappingDao extends CategoryMappingDao {
	
	private static final String CAT2_MAPPING_FILE = "cat2Mapping.xml";
	private static Logger logger = LoggerFactory.getLogger(Category2MappingDao.class);
	
	/**
	 * Get a list of category2 mapping from the config file.  
	 * @return
	 */
	public static Map<String, CategoryMappingType> getCat2Mapping() {
		Map<String, CategoryMappingType> titleMaps = new TreeMap<String, CategoryMappingType>();

		try {
			RMDao dao = new RMDao();
			List<String> dbCategoryList = dao.populateCat2(null);
			List<Integer> catToRemove = new ArrayList<Integer>();
			
			// First remove the one that says "N/A" or "all"
			dbCategoryList.remove(new String("all"));
			dbCategoryList.remove(new String("<N/A>"));
			
			CategoriesDocument category2 = getCatDoc(CAT2_MAPPING_FILE);

			if (category2.getCategories() != null) {
				CategoryMappingType[] categoryType = category2.getCategories().getCategoryMappingArray();
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
				category2.getCategories().removeCategoryMapping(itr.next());
			}
			
			refreshFromDB(titleMaps, dbCategoryList, category2);
			save(category2);
		} catch (SyncException e) {
			logger.error("Unable to retrieve category 2 list", e);
		}


		return titleMaps;
	}

	/**
	 * Helper class to delete category Map.
	 * @throws SyncException 
	 */
	public static void delete(CategoryMappingType mapType) throws SyncException {
		CategoriesDocument categories = getCatDoc(CAT2_MAPPING_FILE);
		
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
	public static void add(CategoryMappingType cat2MappingType) throws SyncException {
		CategoriesDocument categories = getCatDoc(CAT2_MAPPING_FILE);
		
		if (categories.getCategories()==null) {
			categories.addNewCategories();
		}
		categories.getCategories().addNewCategoryMapping();
		categories.getCategories().setCategoryMappingArray(categories.getCategories().sizeOfCategoryMappingArray()-1, cat2MappingType);
	
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

			categories.save(getFile(CAT2_MAPPING_FILE), opts);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
