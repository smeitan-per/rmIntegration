package com.mtit.preprocessor;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mtit.entity.IntProperties;
import com.mtit.process.SyncException;

public class WebWidgetsProductDownloadTest {

	private WebWidgetsProductsDownload preprocessor; 
	
	@Before
	public void setUp() throws Exception {
		IntProperties.init();
		preprocessor = new WebWidgetsProductsDownload();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testProcess() {
		try {
			preprocessor.process();
		} catch (SyncException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
