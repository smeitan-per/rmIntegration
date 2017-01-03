package com.mtit.postprocessor;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mtit.process.SyncException;

public class WebWidgetsProductUploadTest {

	private WebWidgetsProductsUpload upload = new WebWidgetsProductsUpload();
	
	@Before
	public void setUp() throws Exception {
		upload.setInputFile("C:/workspace/PartyZone/output/WebWidgets20121030142416.csv");
	}

	@After
	public void tearDown() throws Exception {
	}

//	@Test
//	public void testProcess() throws SyncException {
//		upload.process();
//	}

}
