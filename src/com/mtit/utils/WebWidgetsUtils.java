/**
 * 
 */
package com.mtit.utils;

import java.math.BigInteger;
import java.util.Map;

import org.apache.log4j.Logger;
import org.mtit.businessRules.CategoryMappingType;

import com.mtit.entity.Category1MappingDao;
import com.mtit.entity.Category2MappingDao;
import com.mtit.entity.Category3MappingDao;
import com.mtit.entity.IntProperties;
import com.mtit.entity.RetailManagerObject;
import com.mtit.entity.WebSiteCategoryDao;
import com.mtit.entity.WebWidgetObject;
import com.mtit.process.SyncException;

/**
 * Helper class for various utilities to convert MYOB data to WebWidgets format.
 * 
 * @author Mei
 *
 */
public class WebWidgetsUtils {

	private static Map<String, String> websiteMap;
	private static Map<String, CategoryMappingType> cat1Map;
	private static Map<String, CategoryMappingType> cat2Map;
	private static Map<String, CategoryMappingType> cat3Map;
	
	private static Logger logger = LoggerFactory.getLogger(WebWidgetsUtils.class);
	
	public static final String MAIN_GROUP = "p_groupid";
	public static final String P_GROUP_ID2 = "p_groupid2";
	public static final String P_GROUP_ID3 = "p_groupid3";

	/**
	 * Helper method to find what website group ID an MYOB category should be
	 * mapped into.
	 * 
	 * @param myobCategoryID
	 * @return
	 */
	public static String getWebsiteGroup(String myobCategoryID) {
	
		String mainGroupID = null;
		try {
			mainGroupID = IntProperties.getProperty(IntProperties.MAIN_WEBSITE_GROUP);
		} catch (SyncException e) {
			logger.error("Unable to find property mainWebsiteGroup");
		}

		String groupName=null;
		
		// Check what the main Group ID is. 
		// - If 1, then category1 will map to p_groupid, category2 will map to p_group_id2 and map category3 to p_group_id3.
		// - If 2, then category1 will map to p_group_id2, category2 will map to p_groupid and category3 will map to p_group_id3
		// - If 3, then category1 will map to p_group_id2 and category2 will map to p_group_id3 and category3 will map to p_groupid.
		if ("1".equals(mainGroupID)) {
			switch (Integer.valueOf(myobCategoryID).intValue()) {
			case 1:
				groupName = MAIN_GROUP; 
				break;
			case 2:
				groupName = P_GROUP_ID2;
				break;
			case 3:
				groupName = P_GROUP_ID3;
				break;
			}
		} else if ("2".equals(mainGroupID)) {
			switch (Integer.valueOf(myobCategoryID).intValue()) {
			case 1:
				groupName = P_GROUP_ID2; 
				break;
			case 2:
				groupName = MAIN_GROUP;
				break;
			case 3:
				groupName = P_GROUP_ID3;
				break;
			}
		} else if ("3".equals(mainGroupID)) {
			switch (Integer.valueOf(myobCategoryID).intValue()) {
			case 1:
				groupName = P_GROUP_ID2; 
				break;
			case 2:
				groupName = P_GROUP_ID3;
				break;
			case 3:
				groupName = MAIN_GROUP;
				break;
			}
		}
		return groupName;
	}


	/**
	 * Helper method to set the groupID that sets new items apart if this is configured.
	 * 
	 * @param newWW
	 * @param rmObj
	 * @throws SyncException 
	 */
	public static void setNewItemGroup(WebWidgetObject newWW,
			RetailManagerObject rmObj) throws SyncException {

		if (websiteMap == null) {
			websiteMap = WebSiteCategoryDao.getWWCategories();
		}
		
		String newItemGroupName = IntProperties.getProperty(IntProperties.NEW_ITEM_GROUP_NAME);
		
		String newItemGroupID = websiteMap.get(newItemGroupName);
			
		if (newItemGroupID != null) {
			
			// To determine which group to set the new item, see what group
			// has been set. 
			// - If p_group3 has been set, then use p_group4
			// - If p_group2 has been set, then use p_group3
			// - If p_group1 has been set, then use p_group2
			// - If no group has been set, then use p_group1
			if (!WebWidgetObject.DEFAULT_GROUP_ID.equals(newWW.getP_groupid3())) {
				newWW.setP_groupid4(newItemGroupID);
			} else if (!WebWidgetObject.DEFAULT_GROUP_ID.equals(newWW.getP_groupid2())) {
				newWW.setP_groupid3(newItemGroupID);
			} else if (!WebWidgetObject.DEFAULT_GROUP_ID.equals(newWW.getP_groupid())) {
				newWW.setP_groupid2(newItemGroupID);
			} else {
				// None of the above groups have been set. Use the main group
				newWW.setP_groupid(newItemGroupID);
			}
		}
	}
	
	/**
	 * Maps the MYOB category fields to the appropriate website field
	 * 
	 * @param newWW
	 * @param category
	 * @param categoryType
	 */
	public static void mapCategory(WebWidgetObject newWW, String category,
			int categoryType) {
		
		String mappedWebsiteField = getWebsiteGroup(String.valueOf(categoryType));
		BigInteger mappedValue = null;
		CategoryMappingType cattype = null;
		
		if (cat1Map == null) {
			cat1Map = Category1MappingDao.getCat1Mapping();
		}
		
		if (cat2Map == null) {
			cat2Map = Category2MappingDao.getCat2Mapping();
		}
		
		if (cat3Map == null) {
			cat3Map = Category3MappingDao.getCat3Mapping();
		}
		
		switch (categoryType) {
		case 1:
			cattype = cat1Map.get(category);
			break;
		case 2:
			cattype = cat2Map.get(category);
			break;
		case 3:
			cattype = cat3Map.get(category);
			break;
		default:
			break;
		}

		if (cattype != null) {
			mappedValue = cattype.getWebSiteCategoryID();
		}
		
		if (mappedValue != null) {
			if (MAIN_GROUP.equals(mappedWebsiteField)) {
				newWW.setP_groupid(mappedValue.toString());
			} else if (P_GROUP_ID2.equals(mappedWebsiteField)) {
				// If p_groupid is not already set, then set this to p_groupid, otherwise set to p_groupid2
				if (newWW.getP_groupid() == null || WebWidgetObject.DEFAULT_GROUP_ID.equals(newWW.getP_groupid())) {
					newWW.setP_groupid(mappedValue.toString());
				} else {
					newWW.setP_groupid2(mappedValue.toString());
				}
			} else if (P_GROUP_ID3.equals(mappedWebsiteField)) {
				if (newWW.getP_groupid() == null || WebWidgetObject.DEFAULT_GROUP_ID.equals(newWW.getP_groupid())) {
					newWW.setP_groupid(mappedValue.toString());
				} else if (newWW.getP_groupid2() == null || WebWidgetObject.DEFAULT_GROUP_ID.equals(newWW.getP_groupid2())) {
					newWW.setP_groupid2(mappedValue.toString());
				} else {
					newWW.setP_groupid3(mappedValue.toString());
				}
			} else {
				logger.error("Mapped website field is invalid:" + mappedWebsiteField);
			}
		}
	}
}
