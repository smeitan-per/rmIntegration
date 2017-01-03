package com.mtit.postprocessor;

import java.io.File;
import java.io.IOException;

import org.apache.xmlbeans.XmlException;
import org.mtit.wwProductUploadResp.ProductuploadoutputDocument;

public class Wwtest {

	public static void main(String[] args) {
		
		String filename = "C:/workspace/PartyZone/reports/wwresponse_20121030144705.txt";
		
		try {
			ProductuploadoutputDocument doc = ProductuploadoutputDocument.Factory.parse(new File(filename));
		} catch (XmlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
