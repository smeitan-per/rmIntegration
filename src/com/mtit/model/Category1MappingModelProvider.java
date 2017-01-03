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

import com.mtit.entity.Category1MappingDao;
import com.mtit.process.SyncException;

/**
 * Title Mapping model provider for the SWT screen
 * 
 * @author Mei
 * 
 */
public enum Category1MappingModelProvider {

	INSTANCE;

	private Map<String, CategoryMappingType> map;
	private List<CategoryMappingType> list;
	
	private Category1MappingModelProvider() { 
		list = new ArrayList<CategoryMappingType>();
		
		sortList();
	}

	private void sortList() {
		list.clear();
		map = Category1MappingDao.getCat1Mapping();

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
	 * @throws SyncException 
	 */
	public void add(CategoryMappingType mapType) throws SyncException {
		Category1MappingDao.add(mapType);
		list.add(mapType);
		sortList();
		
	}
	
	/**
	 * Delete the Category Mapping type.
	 * @param CategoryMappingType
	 * @throws SyncException 
	 */
	public void delete(CategoryMappingType mapType) throws SyncException {
		list.remove(mapType);
		map.remove(mapType.getMYOBCategory());
		
		Category1MappingDao.delete(mapType);
	}
	
}
