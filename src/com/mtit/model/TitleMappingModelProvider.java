/**
 * 
 */
package com.mtit.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.mtit.businessRules.TitleMappingType;

import com.mtit.entity.TitleMappingDao;
import com.mtit.process.SyncException;

/**
 * Title Mapping model provider for the SWT screen
 * 
 * @author Mei
 * 
 */
public enum TitleMappingModelProvider {

	INSTANCE;

	private Map<String, TitleMappingType> map;
	private List<TitleMappingType> list;
	
	private TitleMappingModelProvider() { 
		list = new ArrayList<TitleMappingType>();
		
		try {
			sortList();
		} catch (SyncException e) {
			e.printStackTrace();
		}
	}

	private void sortList() throws SyncException {
		list.clear();
		map = TitleMappingDao.getTitleMapping();

		for (Iterator<Entry<String, TitleMappingType>> itr=map.entrySet().iterator(); itr.hasNext(); ) {
			Entry<String, TitleMappingType> entry = itr.next();
			list.add(entry.getValue());
		}
	}
	
	/**
	 * Get a list of all the rmRules
	 * @return
	 */
	public List<TitleMappingType> getTitleMappings() {
		
		return list;
	}

	/**
	 * Add a new title Mapping type
	 * 
	 * @param ruletype
	 * @throws SyncException 
	 */
	public void add(TitleMappingType mapType) throws SyncException {
		System.out.println("model provider add");
		TitleMappingDao.add(mapType);
		list.add(mapType);
		sortList();
		
	}
	
	/**
	 * Delete the title Mapping type.
	 * @param TitleMappingType
	 * @throws SyncException 
	 */
	public void delete(TitleMappingType mapType) throws SyncException {
		list.remove(mapType);
		map.remove(mapType.getMYOBDesc());
		
		TitleMappingDao.delete(mapType);
	}
	
}
