package com.axelor.app.gst.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.axelor.gst.app.Address;
import com.axelor.gst.app.Invoice;
import com.axelor.gst.app.InvoiceLine;
import com.axelor.gst.app.Party;

public interface InvoiceService {
	
	public boolean checkString(String s1,String s2);
	
	public boolean compareState(Address s1,Address s2);
	
	public List<InvoiceLine> invoiceItemAmounts(Invoice invoice);
	
	public List<InvoiceLine> invoiceItemOnCreate(Invoice invoice,List<Integer> ids);
	
	public  Object getAddress(Party p,String str);
	
	public BigDecimal getAmounts(List<InvoiceLine> il,String str);
	
	public List<Map<String,Object>> getUnpaidInvoiceData(LocalDate fromDate,LocalDate toDate);
	
	public List<Map<String,Object>> getPerCategory(LocalDateTime fromDate,LocalDateTime toDate);
	
	public List<Map<String,Object>> getCustomerPerState();
	
	public List<Map<String,Object>> getAmountsPerStatus();
	
}
