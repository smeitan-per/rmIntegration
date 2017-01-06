/**
 * 
 */
package com.mtit.postprocessor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.mtit.wwProductUploadResp.Product;
import org.mtit.wwProductUploadResp.ProductuploadoutputDocument;

import com.mtit.entity.IntProperties;
import com.mtit.process.SyncException;
import com.mtit.utils.LoggerFactory;

/**
 * Web Widgets Postprocessor to upload products file 
 * 
 * @author Mei
 *
 */
public class WebWidgetsProductsUpload implements IPostProcessor {

	private String inputFile = null;
	private String deleteFile = null;
	
	private String uploadURL= null;

	private String deleteURL = null;
	
	private String reportLoc=null;
	private BufferedWriter writer=null;
	private FileOutputStream fos=null;
		
	private Logger logger = LoggerFactory.getLogger(WebWidgetsProductsUpload.class);
	
	public WebWidgetsProductsUpload() {
		super();
		loadProperties();
	}
	
	private void loadProperties() {
		try {
			uploadURL = IntProperties.getProperty(IntProperties.WEB_WIDGET_PRODUCTS_UPLOAD_URL);
			deleteURL = IntProperties.getProperty(IntProperties.DELETE_PRODUCT_URL);
			reportLoc = IntProperties.getProperty(IntProperties.RESPONSE_REPORT_PREFIX) + ".txt";
		} catch (Exception e) {
			logger.error("Unable to get " + IntProperties.WEB_WIDGET_PRODUCTS_UPLOAD_URL + " property");
		}
	}
	
	/* (non-Javadoc)
	 * @see com.mtit.postprocessor.IPostProcessor#process()
	 */
	@Override
	public void process() throws SyncException {
		processDelete();
		
		HttpClient httpclient = HttpClientBuilder.create().build();
		HttpPost httppost = new HttpPost(uploadURL);

		FileBody bin = new FileBody(new File(inputFile));
		HttpEntity resEntity = null;

		try {
			HttpEntity reqEntity = MultipartEntityBuilder.create()
					.addBinaryBody("uploadFile", new File(inputFile), ContentType.create("application/octet-stream"), "WebWidgets.csv")
					.build();
			httppost.setEntity(reqEntity);
			logger.debug("executing request " + httppost.getRequestLine());

            HttpResponse response = httpclient.execute(httppost);
			resEntity = response.getEntity();
		
			String responseStr = EntityUtils.toString(resEntity);
			logger.info("webwidget response=" + responseStr);
			if (response.getStatusLine().getStatusCode() == 200) {
				preprocessUploadResp(responseStr);
			} else {
				throw new SyncException("Unable to upload file to web widgets. Error is: " + response.getStatusLine().getReasonPhrase());
			}
            
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new SyncException(e.getMessage());
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			throw new SyncException(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			throw new SyncException(e.getMessage());
		} finally {
			closeReport();
			if (resEntity != null) {
				try {
					EntityUtils.consume(resEntity);
				} catch (IOException e) {
					logger.error("Unable to consume resEntity:" + e.getMessage());
				}
			}
		}
	}

	private void preprocessUploadResp(String responseStr) throws SyncException {
		String endElement = "</productuploadoutput>";
		responseStr = responseStr.replace("<productuploadoutput>", "<productuploadoutput xmlns=\"http://www.mtit.org/WWProductUploadResp\">");
		responseStr = responseStr.replaceAll("&", "&amp;");

		responseStr = responseStr.replaceAll("(?m)^[\t]*\r?\n", "");

		responseStr = responseStr.replaceAll("\\t", "");
			
		int index=responseStr.indexOf(endElement);
		index += endElement.length();
		
		responseStr = responseStr.substring(0, index + 1);
		
		try {
			ProductuploadoutputDocument doc = ProductuploadoutputDocument.Factory.parse(responseStr);
			
			Map<String, String> map = new TreeMap<String, String>();
			
			int responseSize = doc.getProductuploadoutput().sizeOfProductArray();
			for (int i=0; i< responseSize; i++ ) {
				Product output = doc.getProductuploadoutput().getProductArray(i);
				map.put(output.getUpdatestatus()+output.getLineno(), output.getUpdatestatus() + ":" +
						output.getCode() + "," + output.getTitle() + "," + output.getLineno() + "," +
						output.getErrormessage());
			}

			for (Iterator<Entry<String, String>> itr=map.entrySet().iterator(); itr.hasNext(); ) {
				Entry<String, String> entry = itr.next();
				if (!entry.getKey().startsWith("ignored1")) {
					writeReport(entry.getValue());
				}
			}
		} catch (XmlException e) {
			logger.debug("XML response="+responseStr);
			throw new SyncException("Invalid xml response format from Web Widgets: " + e.getMessage());
		}

	}

	/**
	 * Read the delete file and append to the deleteURL and process it.
	 * @throws SyncException 
	 */
	private void processDelete() throws SyncException {
	
		File file = new File(deleteFile);
		String line = null;
		String url = null;
		
		if (deleteURL != null && file.exists()) {
			BufferedReader br = null;
			try {
				br = new BufferedReader(new FileReader(file));
				while ((line = br.readLine()) != null ) {
					String barcode = line.split(",")[0];
					url = deleteURL + "&deleteProduct=" + barcode;
 
					HttpClient httpclient = new DefaultHttpClient();
					HttpPost httppost = new HttpPost(url);

					HttpEntity resEntity = null;
					HttpResponse response = httpclient.execute(httppost);
					resEntity = response.getEntity();

					String responseStr = EntityUtils.toString(resEntity);

					preprocessDeleteResp(line, responseStr);

				}
			} catch (MalformedURLException e) {
				logger.error("URL is not valid: " + url +":"+ e.getMessage());
			} catch (IOException e) {
				logger.error("Unable to read contents of " + url + ":" + e.getMessage());
			} finally {
				try {
					if (br != null) {
						br.close();
					}
				} catch (IOException e) {
					logger.error("Unable to close the reader or the writer: " + e.getMessage());
				}
			}
		}
	}
	
	/**
	 * Preprocess the delete file output before writing.
	 * @param barcode
	 * @param responseStr
	 * @throws SyncException 
	 * @throws IOException 
	 */
	private void preprocessDeleteResp(String barcode, String responseStr) throws SyncException {
		String reportLine = null;
		if (responseStr.contains("1 product deleted")) {
			reportLine = "delete: " + barcode + ",1,";
		} else {
			reportLine = "delete: " + barcode + ",1," +responseStr;
		}
		writeReport(reportLine);
	}

	/**
	 * Produce a report 
	 * @param response
	 * @throws SyncException 
	 */
	private void writeReport(String response) throws SyncException {
		if (writer == null) {
			openReport();
		}
		try {
			writer.write(response);
			writer.newLine();
		} catch (IOException e) {
			throw new SyncException("Unable to write to report " + reportLoc);
		}
	}
	
	/**
	 * Set up the report to be written.
	 * @param response
	 * @throws SyncException
	 */
	private void openReport() throws SyncException {
		
		try {

			fos = new FileOutputStream(new File(reportLoc));
			
			writer = new BufferedWriter(new OutputStreamWriter(fos));
			writer.write("status:p_code,title,line_no,error_message");
			writer.newLine();
			
		} catch (IOException e) {
			throw new SyncException("Unable to print report: " + reportLoc);
		} 
	}
	
	/**
	 * Close report
	 * @throws SyncException
	 */
	private void closeReport() throws SyncException {
		
		try {
			if (writer != null) {
				writer.close();
			}
			if (fos != null) {
				fos.close();
			}
		} catch (IOException e) {
			throw new SyncException("Unable to close file output stream and file writer");
		}
	}

	@Override
	public void setInputFile(String genFile) {
		this.inputFile = genFile;
	}

	@Override
	public void setDeleteFile(String deleteFile) {
		this.deleteFile = deleteFile;
	}

	public static void main(String[] args) {
		File outputFile = new File("output/test.xml");
		try {
			ProductuploadoutputDocument doc = ProductuploadoutputDocument.Factory.parse(outputFile);
			int count = doc.getProductuploadoutput().getProductArray().length;
		} catch (XmlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
	}
}
