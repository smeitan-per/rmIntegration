/**
 * 
 */
package com.mtit.entity;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import org.mtit.businessRules.TitleMappingType;
import org.mtit.businessRules.TitlesDocument;

import com.mtit.process.SyncException;
import com.mtit.utils.LoggerFactory;

/**
 * Retrieves the title mapping from the titleMapping.xml file. 
 * @author Mei
 *
 */
public class TitleMappingDao {
	
	private static final String TITLE_MAPPING_FILE = "titleMapping.xml";
	private static Logger logger = LoggerFactory.getLogger(TitleMappingDao.class);

	/**
	 * Get a list of title mapping from the config file.  
	 * @return
	 * @throws SyncException 
	 */
	public static Map<String, String> getTitleMappingString() throws SyncException {
		Map<String, String> titleMaps = new TreeMap<String, String>();
		
		TitlesDocument titles = getTitlesDoc();
		if (titles.getTitles() != null) {
			TitleMappingType[] mapTypes = titles.getTitles().getTitleMappingArray();
			for ( int i = 0; i < mapTypes.length; i++ ) {
				titleMaps.put(mapTypes[i].getMYOBDesc(), mapTypes[i].getWebsiteTitle());
			}
		} 

		return titleMaps;
	}

	/**
	 * Get a list of title mapping from the config file.  
	 * @return
	 * @throws SyncException 
	 */
	public static Map<String, TitleMappingType> getTitleMapping() throws SyncException {
		Map<String, TitleMappingType> titleMaps = new TreeMap<String, TitleMappingType>();
		
		TitlesDocument titles = getTitlesDoc();
		if (titles.getTitles() != null) {
			TitleMappingType[] mapTypes = titles.getTitles().getTitleMappingArray();
			for ( int i = 0; i < mapTypes.length; i++ ) {
				titleMaps.put(mapTypes[i].getMYOBDesc(), mapTypes[i]);
			}
		} 

		return titleMaps;
	}

	/**
	 * Helper class to delete title Map.
	 * @throws SyncException 
	 */
	public static void delete(TitleMappingType mapType) throws SyncException {
		TitlesDocument titles = getTitlesDoc();
		
		TitleMappingType[] mapTypes = titles.getTitles().getTitleMappingArray();
		
		for ( int i = 0; i < mapTypes.length; i++ ) {
			TitleMappingType fileMapType = mapTypes[i];
			
			if (mapType.getMYOBDesc().equals(fileMapType.getMYOBDesc())) {
				titles.getTitles().removeTitleMapping(i);
			}
		}
		
		save(titles);
	}
	
	/**
	 * Add a new title mapping
	 * @param TitleMapType
	 * @throws SyncException 
	 */
	public static void add(TitleMappingType titleMappingType) throws SyncException {
		TitlesDocument titles = getTitlesDoc();
		
		if (titles.getTitles()==null) {
			titles.addNewTitles();
		}
		titles.getTitles().addNewTitleMapping();
		titles.getTitles().setTitleMappingArray(titles.getTitles().sizeOfTitleMappingArray()-1, titleMappingType);
	
		System.out.println("Saving title count=" + titles.getTitles().sizeOfTitleMappingArray());
		save(titles); 
	}
	
	/**
	 * Helper save the title Map config file
	 * @throws SyncException 
	 */
	public static void save(TitlesDocument titles) throws SyncException {
		try {
			XmlOptions opts = new XmlOptions();
			opts.setSavePrettyPrint();
			opts.setSavePrettyPrintIndent(4);

			File titleFile = new File(IntProperties.getProperty(IntProperties.CONFIG_LOCATION) + "/" + TITLE_MAPPING_FILE);
			titles.save(titleFile , opts);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Reads the titleMapping.xml config file and return the xmlbean TitlesDocument.
	 * 
	 * @return
	 * @throws SyncException 
	 */
	private static TitlesDocument getTitlesDoc() throws SyncException {
		TitlesDocument titles = null;

		String fileLocation = IntProperties.getProperty(IntProperties.CONFIG_LOCATION)+"/"+TITLE_MAPPING_FILE;
		File titleMapFile = new File(fileLocation);
		
		if (titleMapFile.exists()) {
			
			
			try {
				titles = TitlesDocument.Factory.parse(titleMapFile);
			} catch (XmlException e) {
				logger.error("Unable to read xml file format " + titleMapFile.getName());
				e.printStackTrace();
			} catch (IOException e) {
				// file does not exist but that's ok. we'll create a new one
				logger.error("No title mapping configured yet. Starting one now");
			}
		} else {
			titles = TitlesDocument.Factory.newInstance();
		}

		return titles;
	}
}
