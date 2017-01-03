/**
 * 
 */
package com.mtit.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.mtit.businessRules.CategoryMappingType;

import com.mtit.entity.Category3MappingDao;

/**
 * Title Mapping model provider for the SWT screen
 * 
 * @author Mei
 * 
 */
public enum Category3MappingModelProvider {

	INSTANCE;

	private Map<String, CategoryMappingType> map;
	private List<CategoryMappingType> list;
	
	private Category3MappingModelProvider() { 
		list = new ArrayList<CategoryMappingType>();
		
		sortList();
	}

	private void sortList() {
		list.clear();
		map = Category3MappingDao.getCat3Mapping();

		for (Iterator<Entry<String, CategoryMappingType>> itr=map.entrySet().iterator(); itr.hasNext(); ) {
			Entry<String, CategoryMappingType> entry = itr.next();
			list.add(entry.getValue());
		}
	}
	
	public List<CategoryMappingType> refreshList() {
		
		sortList();
		
		return list;
	}
	
	/**
	 * Get a list of all the rmRules
	 * @return
	 */
	public List<CategoryMappingType> getTitleMappings() {
		
		return list;
	}

	/**
	 * Add a new Category Mapping type
	 * 
	 * @param ruletype
	 */
	public void add(CategoryMappingType mapType) {
		Category3MappingDao.add(mapType);
		list.add(mapType);
		sortList();
		
	}
	
	/**
	 * Delete the Category Mapping type.
	 * @param CategoryMappingType
	 */
	public void delete(CategoryMappingType mapType) {
		list.remove(mapType);
		map.remove(mapType.getMYOBCategory());
		
		Category3MappingDao.delete(mapType);
	}
	
}
