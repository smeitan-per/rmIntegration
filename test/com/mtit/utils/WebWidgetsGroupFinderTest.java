package com.mtit.utils;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mtit.entity.IntProperties;
import com.mtit.process.SyncException;

public class WebWidgetsGroupFinderTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetWebsiteGroupCat1() throws SyncException {
		String mainWebsiteGroup = IntProperties.getProperty(IntProperties.MAIN_WEBSITE_GROUP);
		
		if ("1".equals(mainWebsiteGroup)) {
			String groupID = WebWidgetsUtils.getWebsiteGroup("1");
			assertEquals("Group ID is not p_groupid:" + groupID, 
					WebWidgetsUtils.MAIN_GROUP , groupID);

			groupID = WebWidgetsUtils.getWebsiteGroup("2");
			assertEquals("Group ID is not p_groupid2:" + groupID, 
					WebWidgetsUtils.P_GROUP_ID2 , groupID);

			groupID = WebWidgetsUtils.getWebsiteGroup("3");
			assertEquals("Group ID is not p_groupid3:" + groupID, 
					WebWidgetsUtils.P_GROUP_ID3 , groupID);

		} else if ("2".equals(mainWebsiteGroup)) {
			String groupID = WebWidgetsUtils.getWebsiteGroup("1");
			assertEquals("Group ID is not p_groupid2:" + groupID, 
					WebWidgetsUtils.P_GROUP_ID2 , groupID);

			groupID = WebWidgetsUtils.getWebsiteGroup("2");
			assertEquals("Group ID is not p_groupid:" + groupID, 
					WebWidgetsUtils.MAIN_GROUP , groupID);

			groupID = WebWidgetsUtils.getWebsiteGroup("3");
			assertEquals("Group ID is not p_groupid3:" + groupID, 
					WebWidgetsUtils.P_GROUP_ID3 , groupID);			
		}  else if ("3".equals(mainWebsiteGroup)) {
			String groupID = WebWidgetsUtils.getWebsiteGroup("1");
			assertEquals("Group ID is not p_groupid2:" + groupID, 
					WebWidgetsUtils.P_GROUP_ID2 , groupID);

			groupID = WebWidgetsUtils.getWebsiteGroup("2");
			assertEquals("Group ID is not p_groupid3:" + groupID, 
					WebWidgetsUtils.P_GROUP_ID3 , groupID);

			groupID = WebWidgetsUtils.getWebsiteGroup("3");
			assertEquals("Group ID is not p_groupid:" + groupID, 
					WebWidgetsUtils.MAIN_GROUP , groupID);			
		}
 	}
}
