/**
 * 
 */
package com.mtit.transformer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.csvreader.CsvReader;
import com.mtit.entity.IntProperties;
import com.mtit.entity.RMDao;
import com.mtit.entity.RetailManagerObject;
import com.mtit.entity.WebWidgetObject;
import com.mtit.process.SyncException;
import com.mtit.utils.DateFormatter;
import com.mtit.utils.LoggerFactory;
import com.mtit.utils.MYOBUtils;
import com.mtit.utils.WebWidgetsUtils;

/**
 * @author Mei
 *
 */
public class WebWidgetsTransformer implements ITransformer {
	
	private final String dateFormat="dd/MM/yyyy";
	
//	private final String header = "p_code,p_title,p_order,p_details,p_extra1,p_extra2,p_extra3" +
//			",p_group,p_groupid,p_groupid2,p_groupid3,p_groupid4,p_groupid5,p_groupid6,p_groupid7" +
//			",p_groupid8,p_price,p_priceprediscount,p_pricea,p_priceb,p_pricec,p_priced,p_pricee" +
//			",p_pricef,p_priceg,p_priceh,p_pricebreaka,p_pricebreaka_minqty,p_pricebreakb,p_pricebreakb_minqty" +
//			",p_pricebreakc,p_pricebreakc_minqty,p_shipping,p_shipping_int,p_uom,p_weight" +
//			",p_outofstockmessage,p_minqty,p_maxqty,p_qtyinc,p_qtyinstock,p_qtylowstock" +
//			",p_supplierprice,p_suppliercode,p_suppliername,p_filename,p_metatitle,p_emailfile,p_sales_end,";

	private final String header = "p_code,p_title,p_order,p_details,p_extra1" +
	",p_groupid,p_groupid2,p_groupid3,p_groupid4" +
	",p_price,p_priceprediscount,p_pricea,p_priceb,p_pricec,p_priced" +
	",p_uom,p_weight,p_qtyinstock,p_sales_end,";

	private String inputFileName;

	private String outputFile;
	
	private String deleteFile;
	
	private Map<String,WebWidgetObject> wwMap;
	
	private List<String> stocksToBeDeleted;
	
	public static final String SEPARATOR=",";
	
	private boolean isFileValid = true;
	
	private int safetyMargin = 0;
	private int recordsToRemove=0;
	
	private static Logger logger = LoggerFactory.getLogger(WebWidgetsTransformer.class);
	
	public WebWidgetsTransformer() {
		try {
			inputFileName = IntProperties.getProperty(IntProperties.WEB_WIDGET_INPUT_FILE);
			
			if (IntProperties.getProperty(IntProperties.SAFETY_MARGIN) != null) {
				safetyMargin = Integer.valueOf(IntProperties.getProperty(IntProperties.SAFETY_MARGIN)).intValue();
			}
			wwMap = new HashMap<String, WebWidgetObject>();
			
			// Read each record and set to the record 

			// Ignore the first line as it's the header.
			CsvReader reader = new CsvReader(new FileReader(new File(inputFileName)));
			reader.readHeaders();
			

			RMDao dao = new RMDao();
			List<String> barcodes=dao.getBarcodes();
			
			
			while (reader.readRecord()) {
				WebWidgetObject obj=new WebWidgetObject(reader);
				String key=obj.getP_code().toUpperCase();
				
				if (obj.getP_details().contains(",") || obj.getP_title().contains(",")) {
					logger.error("Record has fields with ,: " + key);
				} else if (obj.getP_details().contains(System.getProperty("line.separator")) ||
						obj.getP_title().contains(System.getProperty("line.separator"))) {
					logger.error("Record has a new line: " + key);
				}
				
				if (!barcodes.contains(key)) {
					logger.error("Record is missing in MYOB: " + key + " , " + obj.getP_title());
				} else {
					wwMap.put(key, obj);
				}
			}
			
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see com.mtit.transformer.ITransformer#transform(com.mtit.entity.RetailManagerObject)
	 */
	@Override
	public void transform(RetailManagerObject rmObj) throws SyncException {
		WebWidgetObject ww = wwMap.get(rmObj.getBarcode().toUpperCase());
		
		checkForDuplicate();
		
		if ( ww==null ) {
			ww = createNewWWObj(rmObj);
			
			if (ww != null) {
				wwMap.put(rmObj.getBarcode().toUpperCase(), ww);
			}
		}
		
		if (ww!=null) {
			checkForStockDeletions(rmObj);

			if (isRemovable(rmObj, ww)) {
				recordsToRemove++;
				wwMap.remove(ww.getP_code());
			} else {

				ww.setP_qtyinstock(String.valueOf(rmObj.getWebQuantity()));
				ww.setP_price(String.valueOf(rmObj.getWebPrice()));
				ww.setP_pricea(String.valueOf(rmObj.getWebPriceA()));
				ww.setP_priceb(String.valueOf(rmObj.getWebPriceB()));
				ww.setP_pricec(String.valueOf(rmObj.getWebPriceC()));
				ww.setP_priced(String.valueOf(rmObj.getWebPriceD()));
				ww.setP_priceprediscount(String.valueOf(rmObj.getWebPreDiscountPrice()));

				if (rmObj.getSpecialMessage() != null) {
					
					if (IntProperties.getProperty(IntProperties.WEB_PRICE_MESSAGE).equals(rmObj.getSpecialMessage())) {
						// CHeck that the price is less than the price prediscount before setting.
						if (new BigDecimal(ww.getP_priceprediscount()).compareTo(new BigDecimal(ww.getP_price()) ) > 0) {
							ww.setP_extra1(rmObj.getSpecialMessage());
						} else {
							ww.setP_extra1("");
						}
					} else {
						ww.setP_extra1(rmObj.getSpecialMessage());
					}
				}

				if (rmObj.getSaleEndDate() != null ) {
					String salesEndDate = DateFormatter.formatDate(rmObj.getSaleEndDate(), dateFormat);
					ww.setP_sale_ends(salesEndDate);
				}
			}
		}
	}

	private boolean isRemovable(RetailManagerObject rmObj,
			WebWidgetObject ww) {
		String salesEndDateString = null;
		if (rmObj.getSaleEndDate()!= null && !"".equals(rmObj.getSaleEndDate())) {
			salesEndDateString = DateFormatter.formatDate(rmObj.getSaleEndDate(), dateFormat);
		}
		try {
			if (Double.valueOf(ww.getP_qtyinstock()).equals(rmObj.getWebQuantity()) &&
					Double.valueOf(ww.getP_price()).equals(rmObj.getWebPrice()) &&
					Double.valueOf(ww.getP_pricea()).equals(rmObj.getWebPriceA()) && 
					Double.valueOf(ww.getP_priceb()).equals(rmObj.getWebPriceB()) &&
					Double.valueOf(ww.getP_pricec()).equals(rmObj.getWebPriceC()) &&
					Double.valueOf(ww.getP_priced()).equals(rmObj.getWebPriceD()) &&
					Double.valueOf(ww.getP_priceprediscount()).equals(rmObj.getWebPreDiscountPrice()) &&
					((rmObj.getSpecialMessage() == null || rmObj.getSpecialMessage().equals("")) && 
							(ww.getP_extra1() == null || ww.getP_extra1().equals("")) ||
							rmObj.getSpecialMessage().trim().equals(ww.getP_extra1().trim())) &&
					((rmObj.getSaleEndDate() == null || rmObj.getSaleEndDate().equals("")) 
							&& (ww.getP_sale_ends() == null || ww.getP_sale_ends().equals("")) ||
							(salesEndDateString != null && ww.getP_sale_ends() != null &&
							salesEndDateString.equals(ww.getP_sale_ends())))) {
				return true;
			}
		}catch (NumberFormatException e) {
			System.out.println("issue found with p_code " + ww.getP_code());
			return false;
		}
		return false;
	}

	/**
	 * Check if stock item needs to be deleted.
	 * 
	 * @param rmObj
	 */
	private void checkForStockDeletions( RetailManagerObject rmObj) {

		if (rmObj.getDescription() != null && (rmObj.getDescription().contains(">>") ||
				rmObj.getDescription().contains(">>>"))) {
			// Build a list of barcodes to be deleted.
			if (stocksToBeDeleted == null) {
				stocksToBeDeleted = new ArrayList<String>();
			}
			
			stocksToBeDeleted.add(rmObj.getBarcode()+","+rmObj.getDescription());
			if (rmObj.getBarcode()!= null) {
				wwMap.remove(rmObj.getBarcode().toUpperCase());
			}
		}
	}

	//TODO 
	private void checkForDuplicate() {
		// TODO Auto-generated method stub
		
	}

	private WebWidgetObject createNewWWObj(RetailManagerObject rmObj) throws SyncException {
		// Do not create if:
		// - stock is a freight item
		// - stock's custom2 field is set to "N"
		if ( rmObj.isFreight() || rmObj.getCustom2() == null || rmObj.getDescription().contains(">>") ||
				rmObj.getDescription().contains(">>>") ||
				!("Y".equals(rmObj.getCustom2()) || "y".equals(rmObj.getCustom2()))) {
			return null;
		}
		
		// If stock is a static quantity, do not add if
		// quantity - safetymargin < 0
		if ( !rmObj.isStaticQuantity() && (rmObj.getQuantity() - safetyMargin) <= 0) {
			return null;
		}
		WebWidgetObject newWW = new WebWidgetObject();
		newWW.setP_code(rmObj.getBarcode());
		newWW.setP_title(MYOBUtils.transformTitle(rmObj.getDescription()));
		
		if (rmObj.getLongDesc() != null && !rmObj.getLongDesc().startsWith("Accessories")) {
			newWW.setP_details(rmObj.getLongDesc());
		}
		
		newWW.setP_uom(MYOBUtils.getUnitOfMeasure(rmObj.getUom()));
		newWW.setP_weight(rmObj.getWeight());
		newWW.setP_order("100");
		
		WebWidgetsUtils.mapCategory(newWW, rmObj.getCategory1(), 1);
		WebWidgetsUtils.mapCategory(newWW, rmObj.getCategory2(), 2);
		WebWidgetsUtils.mapCategory(newWW, rmObj.getCategory3(), 3);
		
		WebWidgetsUtils.setNewItemGroup(newWW, rmObj);
		
		return newWW;
	}

	@Override
	public void writeOutput() throws SyncException {
		if (stocksToBeDeleted != null) {
			writeDeleteFile();
		} else {
			// Remove old file if it exists
			File file = new File(deleteFile);
			if (file.exists()) {
				file.delete();
			}
		}
		
		BufferedWriter writer=null;
		FileOutputStream fos=null;
		
		logger.info("Started at " + new Date(System.currentTimeMillis()));
		try {
			fos = new FileOutputStream(new File(outputFile));
			
			writer = new BufferedWriter(new OutputStreamWriter(fos));
			writer.write(header);
			
			for (Iterator<Map.Entry<String, WebWidgetObject>> itr=wwMap.entrySet().iterator(); itr.hasNext(); ) {
				Entry<String, WebWidgetObject> entry = itr.next();
				
				if ("BATTAAA".equals(entry.getValue().getP_code()) || "FB1".equals(entry.getValue().getP_code())) {
					System.out.println("Original value="+ entry.getValue().toString());				
				}
				
				// Only create an entry if the value has changed, otherwise there is no point.
				if (entry.getValue().isHasValueChanged()) {
					writer.newLine();
					writer.write(entry.getValue().toString());					
				}
			}
		
			logger.info("Ended at " + new Date(System.currentTimeMillis()));
		} catch (IOException e) {
			throw new SyncException("Unable to write to file " + outputFile);
		} finally {
			try {
				if (writer != null) {
					writer.close();
				}

				if (fos != null) {

					fos.close();
				}

			} catch (IOException e) {
				logger.error("Unable to close output file stream and writer");
			}
		}
	}

	private void writeDeleteFile() throws SyncException {
		BufferedWriter writer=null;
		FileOutputStream fos=null;
		
		File file = new File(deleteFile);
		
		try {
			fos = new FileOutputStream(file);
			
			writer = new BufferedWriter(new OutputStreamWriter(fos));
			
			for (Iterator<String> itr=stocksToBeDeleted.iterator(); itr.hasNext();  ) {
				writer.write(itr.next());
				writer.newLine();
			}
		
		} catch (IOException e) {
			throw new SyncException("Unable to write to file " + deleteFile);
		} finally {
			try {
				if (writer != null) {
					writer.close();
				}

				if (fos != null) {

					fos.close();
				}

			} catch (IOException e) {
				logger.error("Unable to close output file stream and writer");
			}
		}
		
	}

	/**
	 * @return the isFileValid
	 */
	public boolean isFileValid() {
		return isFileValid;
	}

	/**
	 * @param isFileValid the isFileValid to set
	 */
	public void setFileValid(boolean isFileValid) {
		this.isFileValid = isFileValid;
	}

	@Override
	public void setOutputFile(String outputFile) {
		this.outputFile=outputFile;
	}

	@Override
	public void setDeleteFile(String deleteFile) {
		this.deleteFile = deleteFile;
	}

}
