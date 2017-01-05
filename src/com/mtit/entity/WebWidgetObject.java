/**
 * 
 */
package com.mtit.entity;

import java.io.IOException;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import org.apache.log4j.Logger;

import com.csvreader.CsvReader;
import com.mtit.process.SyncException;
import com.mtit.transformer.WebWidgetsTransformer;
import com.mtit.utils.LoggerFactory;

/**
 * POJO for storing web widget objects so that this can be used for reuploading
 * to the web widgets website.
 * 
 * @author Mei
 *
 */
public class WebWidgetObject {
	public static Logger logger = LoggerFactory.getLogger(WebWidgetObject.class);

	public static final String DEFAULT_GROUP_ID = "-9";
	
	private String p_code = "";
	private String p_title = "";
	private String p_order = "100";
	private String p_img = "";
	private String p_details = "";
	private String p_extra1 = "";
	private String p_extra2 = "";
	private String p_extra3 = "";
	private String p_group = "";
	private String p_groupid = DEFAULT_GROUP_ID;
	private String p_groupid2 = DEFAULT_GROUP_ID;
	private String p_groupid3 = DEFAULT_GROUP_ID;
	private String p_groupid4 = DEFAULT_GROUP_ID;
	private String p_groupid5 = DEFAULT_GROUP_ID;
	private String p_groupid6 = DEFAULT_GROUP_ID;
	private String p_groupid7 = DEFAULT_GROUP_ID;
	private String p_groupid8 = DEFAULT_GROUP_ID;
	private String p_price = "0";
	private String p_priceprediscount = "0";
	private String p_pricea = "0";
	private String p_priceb = "0";
	private String p_pricec = "0";
	private String p_priced = "0";
	private String p_pricee = "0";
	private String p_pricef = "0";
	private String p_priceg = "0";
	private String p_priceh = "0";
	private String p_pricebreaka = "0"; 
	private String p_pricebreaka_minqty = "0";
	private String p_pricebreakb = "0";
	private String p_pricebreakb_minqty = "0";
	private String p_pricebreakc = "0";
	private String p_pricebreakc_minqty = "0";
	private String p_shipping = "0";
	private String p_shipping_int = "0";
	private String p_uom = "";
	private String p_weight = "0";
	private String p_outofstockmessage = "";
	private String p_minqty = "0";
	private String p_maxqty = "0";
	private String p_qtyinc = "0";
	private String p_qtyinstock = "0";
	private String p_qtylowstock = "0";
	private String p_supplierprice = "";
	private String p_suppliercode = "";
	private String p_suppliername = "";
	private String p_filename = "";
	private String p_metatitle = "";
	private String p_emailfile = "";
	private String p_sale_ends = "";

	private boolean hasValueChanged;
	
	/**
	 * Empty Constructor
	 */
	public WebWidgetObject() {
		super();
	}

	/**
	 * Constructor
	 * @param reader
	 * @throws SyncException 
	 */
	public WebWidgetObject(CsvReader reader) throws SyncException {

		try {
			p_code=reader.get("p_code".toUpperCase());
			p_title=reader.get("p_title".toUpperCase());
			p_order=reader.get("p_order".toUpperCase());
			p_img=reader.get("p_img".toUpperCase());
			p_details=reader.get("p_details".toUpperCase());
			p_extra1=reader.get("p_extra1".toUpperCase());
			p_extra2=reader.get("p_extra2".toUpperCase());;
			p_extra3=reader.get("p_extra3".toUpperCase());;
			p_group=reader.get("p_group".toUpperCase());
			
			String tmpGroupId = reader.get("p_groupid".toUpperCase());
			if (!"0".equals(tmpGroupId)) {
				p_groupid=tmpGroupId;
			}

			tmpGroupId = reader.get("p_groupid2".toUpperCase());
			if (!"0".equals(tmpGroupId)) {
				p_groupid2=tmpGroupId;
			}
			
			tmpGroupId = reader.get("p_groupid3".toUpperCase());
			if (!"0".equals(tmpGroupId)) {
				p_groupid3=tmpGroupId;
			}
			
			tmpGroupId = reader.get("p_groupid4".toUpperCase());
			if (!"0".equals(tmpGroupId)) {
				p_groupid4=tmpGroupId;
			}
			
			tmpGroupId = reader.get("p_groupid5".toUpperCase());
			if (!"0".equals(tmpGroupId)) {
				p_groupid5=tmpGroupId;
			}
			
			tmpGroupId = reader.get("p_groupid6".toUpperCase());
			if (!"0".equals(tmpGroupId)) {
				p_groupid6=tmpGroupId;
			}
			
			tmpGroupId = reader.get("p_groupid7".toUpperCase());
			if (!"0".equals(tmpGroupId)) {
				p_groupid7=tmpGroupId;
			}
			
			tmpGroupId = reader.get("p_groupid8".toUpperCase());
			if (!"0".equals(tmpGroupId)) {
				p_groupid8=tmpGroupId;
			}
			p_price=reader.get("p_price".toUpperCase());
			p_priceprediscount=reader.get("p_priceprediscount".toUpperCase());
			p_pricea=reader.get("p_pricea".toUpperCase());
			p_priceb=reader.get("p_priceb".toUpperCase());
			p_pricec=reader.get("p_pricec".toUpperCase());
			p_priced=reader.get("p_priced".toUpperCase());
			p_pricee=reader.get("p_pricee".toUpperCase());
			p_pricef=reader.get("p_pricef".toUpperCase());
			p_priceg=reader.get("p_priceg".toUpperCase());
			p_priceh=reader.get("p_priceh".toUpperCase());
			p_pricebreaka=reader.get("p_pricebreaka".toUpperCase());
			p_pricebreaka_minqty=reader.get("p_pricebreaka_minqty".toUpperCase());
			p_pricebreakb=reader.get("p_pricebreakb".toUpperCase());
			p_pricebreakb_minqty=reader.get("p_pricebreakb_minqty".toUpperCase());
			p_pricebreakc=reader.get("p_pricebreakc".toUpperCase());
			p_pricebreakc_minqty=reader.get("p_pricebreakc_minqty".toUpperCase());
			p_shipping=reader.get("p_shipping".toUpperCase());
			p_shipping_int=reader.get("p_shipping_int".toUpperCase());
			p_uom=reader.get("p_uom".toUpperCase());
			p_weight=reader.get("p_weight".toUpperCase());
			p_outofstockmessage=reader.get("p_outofstockmessage".toUpperCase());
			p_minqty=reader.get("p_minqty".toUpperCase());
			p_maxqty=reader.get("p_maxqty".toUpperCase());
			p_qtyinc=reader.get("p_qtyinc".toUpperCase());
			p_qtyinstock=reader.get("p_qtyinstock".toUpperCase());
			p_qtylowstock=reader.get("p_qtylowstock".toUpperCase());
			p_supplierprice=reader.get("p_suplierprice".toUpperCase());
			p_suppliercode=reader.get("p_suppliercode".toUpperCase());
			p_suppliername=reader.get("p_suppliername".toUpperCase());;
			p_filename=reader.get("p_filename".toUpperCase());;
			p_metatitle=reader.get("p_metatitle".toUpperCase());
			p_emailfile=reader.get("p_emailfile".toUpperCase());
			p_sale_ends=reader.get("p_sale_ends".toUpperCase());

			if (p_code.isEmpty() || p_title.isEmpty()) {
				throw new SyncException("Something is wrong with the product download. Unable to find p_code record");
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String getValueFromArray(String[] arrays, int index) {
		try {
			return arrays[index];
		} catch (ArrayIndexOutOfBoundsException e) {
			return "";
		}
	}
	/**
	 * @return the p_code
	 */
	public String getP_code() {
		return p_code;
	}
	/**
	 * @param p_code the p_code to set
	 */
	public void setP_code(String p_code) {
		checkStringModified(this.p_code, p_code);
		this.p_code = p_code;
	}
	
	/**
	 * @return the p_title
	 */
	public String getP_title() {
		return p_title;
	}
	/**
	 * @param p_title the p_title to set
	 */
	public void setP_title(String p_title) {
		checkStringModified(this.p_title, p_title);
		this.p_title = p_title;
	}
	/**
	 * @return the p_order
	 */
	public String getP_order() {
		return p_order;
	}
	/**
	 * @param p_order the p_order to set
	 */
	public void setP_order(String p_order) {
		checkStringModified(this.p_order, p_order);
		this.p_order = p_order;
	}
	/**
	 * @return the p_details
	 */
	public String getP_details() {
		return p_details;
	}
	/**
	 * @param p_details the p_details to set
	 */
	public void setP_details(String p_details) {
		checkStringModified(this.p_details, p_details);
		this.p_details = p_details;
	}
	/**
	 * @return the p_extra1
	 */
	public String getP_extra1() {
		return p_extra1;
	}
	/**
	 * @param p_extra1 the p_extra1 to set
	 */
	public void setP_extra1(String p_extra1) {
		checkStringModified(this.p_extra1, p_extra1);
		this.p_extra1 = p_extra1;
	}
	/**
	 * @return the p_extra2
	 */
	public String getP_extra2() {
		return p_extra2;
	}
	/**
	 * @param p_extra2 the p_extra2 to set
	 */
	public void setP_extra2(String p_extra2) {
		checkStringModified(this.p_extra2, p_extra2);
		this.p_extra2 = p_extra2;
	}
	/**
	 * @return the p_extra3
	 */
	public String getP_extra3() {
		return p_extra3;
	}
	/**
	 * @param p_extra3 the p_extra3 to set
	 */
	public void setP_extra3(String p_extra3) {
		checkStringModified(this.p_extra3, p_extra3);
		this.p_extra3 = p_extra3;
	}
	/**
	 * @return the p_group
	 */
	public String getP_group() {
		return p_group;
	}
	/**
	 * @param p_group the p_group to set
	 */
	public void setP_group(String p_group) {
		checkStringModified(this.p_group, p_group);
		this.p_group = p_group;
	}
	/**
	 * @return the p_groupid
	 */
	public String getP_groupid() {
		return p_groupid;
	}
	/**
	 * @param p_groupid the p_groupid to set
	 */
	public void setP_groupid(String p_groupid) {
		checkStringModified(this.p_groupid, p_groupid);
		this.p_groupid = p_groupid;
	}
	/**
	 * @return the p_groupid2
	 */
	public String getP_groupid2() {
		return p_groupid2;
	}
	/**
	 * @param p_groupid2 the p_groupid2 to set
	 */
	public void setP_groupid2(String p_groupid2) {
		checkStringModified(this.p_groupid2, p_groupid2);
		this.p_groupid2 = p_groupid2;
	}
	/**
	 * @return the p_groupid3
	 */
	public String getP_groupid3() {
		return p_groupid3;
	}
	/**
	 * @param p_groupid3 the p_groupid3 to set
	 */
	public void setP_groupid3(String p_groupid3) {
		checkStringModified(this.p_groupid3, p_groupid3);
		this.p_groupid3 = p_groupid3;
	}
	/**
	 * @return the p_groupid4
	 */
	public String getP_groupid4() {
		return p_groupid4;
	}
	/**
	 * @param p_groupid4 the p_groupid4 to set
	 */
	public void setP_groupid4(String p_groupid4) {
		checkStringModified(this.p_groupid4, p_groupid4);
		this.p_groupid4 = p_groupid4;
	}
	/**
	 * @return the p_groupid5
	 */
	public String getP_groupid5() {
		return p_groupid5;
	}
	/**
	 * @param p_groupid5 the p_groupid5 to set
	 */
	public void setP_groupid5(String p_groupid5) {
		checkStringModified(this.p_groupid5, p_groupid5);
		this.p_groupid5 = p_groupid5;
	}
	/**
	 * @return the p_groupid6
	 */
	public String getP_groupid6() {
		return p_groupid6;
	}
	/**
	 * @param p_groupid6 the p_groupid6 to set
	 */
	public void setP_groupid6(String p_groupid6) {
		checkStringModified(this.p_groupid6, p_groupid6);
		this.p_groupid6 = p_groupid6;
	}
	/**
	 * @return the p_groupid7
	 */
	public String getP_groupid7() {
		return p_groupid7;
	}
	/**
	 * @param p_groupid7 the p_groupid7 to set
	 */
	public void setP_groupid7(String p_groupid7) {
		checkStringModified(this.p_groupid7, p_groupid7);
		this.p_groupid7 = p_groupid7;
	}
	/**
	 * @return the p_groupid8
	 */
	public String getP_groupid8() {
		return p_groupid8;
	}
	/**
	 * @param p_groupid8 the p_groupid8 to set
	 */
	public void setP_groupid8(String p_groupid8) {
		checkStringModified(this.p_groupid8, p_groupid8);
		this.p_groupid8 = p_groupid8;
	}
	/**
	 * @return the p_price
	 */
	public String getP_price() {
		return p_price;
	}
	/**
	 * @param p_price the p_price to set
	 */
	public void setP_price(String p_price) {
		checkDoubleModified(this.p_price, p_price);
		this.p_price = p_price;
	}
	/**
	 * @return the p_priceprediscount
	 */
	public String getP_priceprediscount() {
		return p_priceprediscount;
	}
	/**
	 * @param p_priceprediscount the p_priceprediscount to set
	 */
	public void setP_priceprediscount(String p_priceprediscount) {
		checkDoubleModified(this.p_priceprediscount, p_priceprediscount);
		this.p_priceprediscount = p_priceprediscount;
	}
	/**
	 * @return the p_pricea
	 */
	public String getP_pricea() {
		return p_pricea;
	}
	/**
	 * @param p_pricea the p_pricea to set
	 */
	public void setP_pricea(String p_pricea) {
		checkDoubleModified(this.p_pricea, p_pricea);
		this.p_pricea = p_pricea;
	}
	/**
	 * @return the p_priceb
	 */
	public String getP_priceb() {
		return p_priceb;
	}
	/**
	 * @param p_priceb the p_priceb to set
	 */
	public void setP_priceb(String p_priceb) {
		checkDoubleModified(this.p_priceb, p_priceb);
		this.p_priceb = p_priceb;
	}
	/**
	 * @return the p_pricec
	 */
	public String getP_pricec() {
		return p_pricec;
	}
	/**
	 * @param p_pricec the p_pricec to set
	 */
	public void setP_pricec(String p_pricec) {
		checkDoubleModified(this.p_pricec, p_pricec);
		this.p_pricec = p_pricec;
	}
	/**
	 * @return the p_priced
	 */
	public String getP_priced() {
		return p_priced;
	}
	/**
	 * @param p_priced the p_priced to set
	 */
	public void setP_priced(String p_priced) {
		checkDoubleModified(this.p_priced, p_priced);
		this.p_priced = p_priced;
	}
	/**
	 * @return the p_pricee
	 */
	public String getP_pricee() {
		return p_pricee;
	}
	/**
	 * @param p_pricee the p_pricee to set
	 */
	public void setP_pricee(String p_pricee) {
		checkDoubleModified(this.p_pricee, p_pricee);
		this.p_pricee = p_pricee;
	}
	/**
	 * @return the p_pricef
	 */
	public String getP_pricef() {
		return p_pricef;
	}
	/**
	 * @param p_pricef the p_pricef to set
	 */
	public void setP_pricef(String p_pricef) {
		checkDoubleModified(this.p_pricef, p_pricef);
		this.p_pricef = p_pricef;
	}
	/**
	 * @return the p_priceg
	 */
	public String getP_priceg() {
		return p_priceg;
	}
	/**
	 * @param p_priceg the p_priceg to set
	 */
	public void setP_priceg(String p_priceg) {
		checkDoubleModified(this.p_priceg, p_priceg);
		this.p_priceg = p_priceg;
	}
	/**
	 * @return the p_priceh
	 */
	public String getP_priceh() {
		return p_priceh;
	}
	/**
	 * @param p_priceh the p_priceh to set
	 */
	public void setP_priceh(String p_priceh) {
		checkDoubleModified(this.p_priceh, p_priceh);
		this.p_priceh = p_priceh;
	}
	/**
	 * @return the p_pricebreaka
	 */
	public String getP_pricebreaka() {
		return p_pricebreaka;
	}
	/**
	 * @param p_pricebreaka the p_pricebreaka to set
	 */
	public void setP_pricebreaka(String p_pricebreaka) {
		checkDoubleModified(p_pricebreaka, p_pricebreaka);
		this.p_pricebreaka = p_pricebreaka;
	}
	/**
	 * @return the p_pricebreaka_minqty
	 */
	public String getP_pricebreaka_minqty() {
		return p_pricebreaka_minqty;
	}
	/**
	 * @param p_pricebreaka_minqty the p_pricebreaka_minqty to set
	 */
	public void setP_pricebreaka_minqty(String p_pricebreaka_minqty) {
		this.p_pricebreaka_minqty = p_pricebreaka_minqty;
	}
	/**
	 * @return the p_pricebreakb
	 */
	public String getP_pricebreakb() {
		return p_pricebreakb;
	}
	/**
	 * @param p_pricebreakb the p_pricebreakb to set
	 */
	public void setP_pricebreakb(String p_pricebreakb) {
		this.p_pricebreakb = p_pricebreakb;
	}
	/**
	 * @return the p_pricebreakb_minqty
	 */
	public String getP_pricebreakb_minqty() {
		return p_pricebreakb_minqty;
	}
	/**
	 * @param p_pricebreakb_minqty the p_pricebreakb_minqty to set
	 */
	public void setP_pricebreakb_minqty(String p_pricebreakb_minqty) {
		this.p_pricebreakb_minqty = p_pricebreakb_minqty;
	}
	/**
	 * @return the p_pricebreakc
	 */
	public String getP_pricebreakc() {
		return p_pricebreakc;
	}
	/**
	 * @param p_pricebreakc the p_pricebreakc to set
	 */
	public void setP_pricebreakc(String p_pricebreakc) {
		this.p_pricebreakc = p_pricebreakc;
	}
	/**
	 * @return the p_pricebreakc_minqty
	 */
	public String getP_pricebreakc_minqty() {
		return p_pricebreakc_minqty;
	}
	/**
	 * @param p_pricebreakc_minqty the p_pricebreakc_minqty to set
	 */
	public void setP_pricebreakc_minqty(String p_pricebreakc_minqty) {
		this.p_pricebreakc_minqty = p_pricebreakc_minqty;
	}
	/**
	 * @return the p_shipping
	 */
	public String getP_shipping() {
		return p_shipping;
	}
	/**
	 * @param p_shipping the p_shipping to set
	 */
	public void setP_shipping(String p_shipping) {
		this.p_shipping = p_shipping;
	}
	/**
	 * @return the p_shipping_int
	 */
	public String getP_shipping_int() {
		return p_shipping_int;
	}
	/**
	 * @param p_shipping_int the p_shipping_int to set
	 */
	public void setP_shipping_int(String p_shipping_int) {
		this.p_shipping_int = p_shipping_int;
	}
	/**
	 * @return the p_uom
	 */
	public String getP_uom() {
		return p_uom;
	}
	/**
	 * @param p_uom the p_uom to set
	 */
	public void setP_uom(String p_uom) {
		checkDoubleModified(this.p_uom, p_uom);
		this.p_uom = p_uom;
	}
	/**
	 * @return the p_weight
	 */
	public String getP_weight() {
		return p_weight;
	}
	/**
	 * @param p_uom the p_uom to set
	 */
	public void setP_weight(String p_weight) {
		checkDoubleModified(this.p_weight, p_weight);
		this.p_weight = p_weight;
	}
	/**
	 * @return the p_outofstockmessage
	 */
	public String getP_outofstockmessage() {
		return p_outofstockmessage;
	}
	/**
	 * @param p_outofstockmessage the p_outofstockmessage to set
	 */
	public void setP_outofstockmessage(String p_outofstockmessage) {
		checkDoubleModified(this.p_outofstockmessage, p_outofstockmessage);
		this.p_outofstockmessage = p_outofstockmessage;
	}
	/**
	 * @return the p_minqty
	 */
	public String getP_minqty() {
		return p_minqty;
	}
	/**
	 * @param p_minqty the p_minqty to set
	 */
	public void setP_minqty(String p_minqty) {
		this.p_minqty = p_minqty;
	}
	/**
	 * @return the p_maxqty
	 */
	public String getP_maxqty() {
		return p_maxqty;
	}
	/**
	 * @param p_maxqty the p_maxqty to set
	 */
	public void setP_maxqty(String p_maxqty) {
		this.p_maxqty = p_maxqty;
	}
	/**
	 * @return the p_qtyinc
	 */
	public String getP_qtyinc() {
		return p_qtyinc;
	}
	/**
	 * @param p_qtyinc the p_qtyinc to set
	 */
	public void setP_qtyinc(String p_qtyinc) {
		this.p_qtyinc = p_qtyinc;
	}
	/**
	 * @return the p_qtyinstock
	 */
	public String getP_qtyinstock() {
		return p_qtyinstock;
	}
	/**
	 * @param p_qtyinstock the p_qtyinstock to set
	 */
	public void setP_qtyinstock(String p_qtyinstock) {
		checkDoubleModified(this.p_qtyinstock, p_qtyinstock);
		this.p_qtyinstock = p_qtyinstock;
	}
	/**
	 * @return the p_qtylowstock
	 */
	public String getP_qtylowstock() {
		return p_qtylowstock;
	}
	/**
	 * @param p_qtylowstock the p_qtylowstock to set
	 */
	public void setP_qtylowstock(String p_qtylowstock) {
		checkDoubleModified(this.p_qtylowstock, p_qtylowstock);
		this.p_qtylowstock = p_qtylowstock;
	}
	/**
	 * @return the p_supplierprice
	 */
	public String getP_supplierprice() {
		return p_supplierprice;
	}
	/**
	 * @param p_supplierprice the p_supplierprice to set
	 */
	public void setP_supplierprice(String p_supplierprice) {
		this.p_supplierprice = p_supplierprice;
	}
	/**
	 * @return the p_suppliercode
	 */
	public String getP_suppliercode() {
		return p_suppliercode;
	}
	/**
	 * @param p_suppliercode the p_suppliercode to set
	 */
	public void setP_suppliercode(String p_suppliercode) {
		this.p_suppliercode = p_suppliercode;
	}
	/**
	 * @return the p_suppliername
	 */
	public String getP_suppliername() {
		return p_suppliername;
	}
	/**
	 * @param p_suppliername the p_suppliername to set
	 */
	public void setP_suppliername(String p_suppliername) {
		this.p_suppliername = p_suppliername;
	}
	/**
	 * @return the p_filename
	 */
	public String getP_filename() {
		return p_filename;
	}
	/**
	 * @param p_filename the p_filename to set
	 */
	public void setP_filename(String p_filename) {
		this.p_filename = p_filename;
	}
	/**
	 * @return the p_metatitle
	 */
	public String getP_metatitle() {
		return p_metatitle;
	}
	/**
	 * @param p_metatitle the p_metatitle to set
	 */
	public void setP_metatitle(String p_metatitle) {
		this.p_metatitle = p_metatitle;
	}
	/**
	 * @return the p_emailfile
	 */
	public String getP_emailfile() {
		return p_emailfile;
	}
	/**
	 * @param p_emailfile the p_emailfile to set
	 */
	public void setP_emailfile(String p_emailfile) {
		this.p_emailfile = p_emailfile;
	}

	/**
	 * @return the p_sale_ends
	 */
	public String getP_sale_ends() {
		return p_sale_ends;
	}

	/**
	 * @param p_sale_ends the p_sale_ends to set
	 */
	public void setP_sale_ends(String p_sale_ends) {
		checkStringModified(this.p_sale_ends, p_sale_ends);
		this.p_sale_ends = p_sale_ends;
	}

	public boolean isHasValueChanged() {
		return hasValueChanged;
	}

	public void setHasValueChanged(boolean hasValueChanged) {
		this.hasValueChanged = hasValueChanged;
	}

	/**
	 * Set the records into a single line delimited by SEPARATOR.
	 */
	public String toString() {
		String SEPARATOR = WebWidgetsTransformer.SEPARATOR;
		
		p_title = p_title.replaceAll("\"", "\"\"");
		p_details = p_details.replaceAll("\"", "\"\"");
		p_filename = p_filename.replaceAll("\"", "\"\"");
		p_metatitle = p_metatitle.replaceAll("\"", "\"\"");
		
		String returnString = "\""+ p_code +  "\""+ SEPARATOR 
		+ "\"" + p_title + "\"" + SEPARATOR
		+ "\"" + p_order + "\"" + SEPARATOR
		+ "\"" + p_details + "\"" + SEPARATOR
		+ "\"" + p_extra1 + "\"" + SEPARATOR
		+ "\"" + p_groupid + "\"" + SEPARATOR
		+ "\"" + p_groupid2 + "\"" + SEPARATOR
		+ "\"" + p_groupid3 + "\"" + SEPARATOR
		+ "\"" + p_groupid4 + "\"" + SEPARATOR
		+ "\"" + p_price + "\"" + SEPARATOR
		+ "\"" + p_priceprediscount + "\"" + SEPARATOR
		+ "\"" + p_pricea + "\"" + SEPARATOR
		+ "\"" + p_priceb + "\"" + SEPARATOR
		+ "\"" + p_pricec + "\"" + SEPARATOR
		+ "\"" + p_priced + "\"" + SEPARATOR
		+ "\"" + p_uom + "\"" + SEPARATOR
		+ "\"" + p_weight + "\"" + SEPARATOR 
		+ "\"" + p_qtyinstock + "\"" + SEPARATOR
		+ "\"" + p_sale_ends + "\"" + SEPARATOR;

		return returnString;
	}
	
	/**
	 * Helper method to check if a string value has changed. 
	 * 
	 * @param origString
	 * @param newString
	 */
	private void checkStringModified(String origString, String newString) {
		if (!isHasValueChanged() && !origString.equals(newString)) {
			logger.debug("p_code="+p_code+":old_value="+origString+":new_value="+newString);
			setHasValueChanged(true);
		}		
	}

	/**
	 * Helper method to check if a double value has changed.
	 * 
	 * @param origDoubleStr
	 * @param newDoubleStr
	 */
	private void checkDoubleModified(String origDoubleStr, String newDoubleStr) {
		if (!isHasValueChanged() && origDoubleStr !=null && newDoubleStr != null
				&& !Double.valueOf(origDoubleStr).equals(Double.valueOf(newDoubleStr))) {
			logger.debug("p_code="+p_code+":old_value="+Double.valueOf(origDoubleStr)+":new_value="+Double.valueOf(newDoubleStr));
			setHasValueChanged(true);
		}		
	}
}	
