package com.axelor.app.gst.controller;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import com.axelor.app.AppSettings;
import com.axelor.app.gst.service.InvoiceService;
import com.axelor.app.gst.service.SequenceService;
import com.axelor.gst.app.Invoice;
import com.axelor.gst.app.InvoiceLine;
import com.axelor.gst.app.Party;


import com.axelor.meta.CallMethod;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.Inject;

public class InvoiceController {

	@Inject
	private InvoiceService service;
	
	@Inject
	private SequenceService sequence;
	
	
	public void setContact(ActionRequest req,ActionResponse resp) {
		
		Invoice invoice=req.getContext().asType(Invoice.class);
		try {
			Party party=invoice.getParty();
			
			resp.setValue("partyContact", service.getAddress(party, "primary"));
			
			resp.setValue("invoiceAddress", service.getAddress(party, "invoice"));
			
			resp.setValue("shippingAddress", service.getAddress(party, "shipping"));
		}catch(Exception e) { }
	}
	public void setAmounts(ActionRequest req,ActionResponse resp) {
		Invoice invoice=req.getContext().asType(Invoice.class);
		List<InvoiceLine> invoiceLine= invoice.getInvoiceItem();
		try {

			resp.setValue("netAmount", service.getAmounts(invoiceLine, "netAmount"));
			
			resp.setValue("netIgst", service.getAmounts(invoiceLine, "igst"));
			
			resp.setValue("netCgst", service.getAmounts(invoiceLine, "cgst"));
			
			resp.setValue("netSgst", service.getAmounts(invoiceLine, "sgst"));
			
			resp.setValue("grossAmount", service.getAmounts(invoiceLine, "grossAmount"));
			
		}catch(Exception e) { }
	}
	
	
	public void setValuesOnChange(ActionRequest req,ActionResponse resp) {
		
		Invoice invoice=req.getContext().asType(Invoice.class);
		
		resp.setValue("invoiceItem", service.invoiceItemAmounts(invoice));		
	}
	
	
	public void setInvoiceItem(ActionRequest req,ActionResponse resp) {
		
		Invoice invoice=req.getContext().asType(Invoice.class);
		
		List<Integer> invoiceItems= (List<Integer>) req.getContext().get("productId");
		
		resp.setValue("invoiceItem", service.invoiceItemOnCreate(invoice, invoiceItems));	
	}
	
	
	public void setInvoiceReference(ActionRequest req,ActionResponse resp) {
		
		try {
			resp.setValue("reference", sequence.getNextSequence("Invoice"));
			
		}catch(NullPointerException e) {
			
			resp.addError("reference", "No sequence is specified for this model");
		}
	}
	
	
	@CallMethod
	public String getLogoUrl() {
			
		return AppSettings.get().get("file.upload.dir").concat(File.separator);
	}
	
	
	public void setGst(ActionRequest req,ActionResponse resp) {
		
		Invoice invoice=req.getContext().asType(Invoice.class);
		
		resp.setValue("invoiceItem", service.invoiceItemAmounts(invoice));
	}
	
	
	public void getChartData(ActionRequest req,ActionResponse resp) {
		LocalDate fromDate= LocalDate.parse(req.getContext().get("fromDate").toString(), DateTimeFormatter.ISO_DATE_TIME);
		LocalDate toDate= LocalDate.parse(req.getContext().get("toDate").toString(), DateTimeFormatter.ISO_DATE_TIME);
		
		List<Map<String, Object>> dataList=service.getUnpaidInvoiceData(fromDate, toDate);
		resp.setData(dataList);
		
	}
	
	public void getCustomerNo(ActionRequest req,ActionResponse resp) {
		
		List<Map<String, Object>> customerList=service.getCustomerPerState();
		resp.setData(customerList);
	}
	
	public void getGrossAmount(ActionRequest req,ActionResponse resp) {
		
		List<Map<String, Object>> amountList=service.getAmountsPerStatus();
		resp.setData(amountList);
	}

	public void getCategoryPerProduct(ActionRequest req,ActionResponse resp) {
		LocalDateTime fromDate= LocalDateTime.parse(req.getContext().get("fromDate").toString(), DateTimeFormatter.ISO_DATE_TIME);
		LocalDateTime toDate= LocalDateTime.parse(req.getContext().get("toDate").toString(), DateTimeFormatter.ISO_DATE_TIME);
		
		
		List<Map<String, Object>> dataList=service.getPerCategory(fromDate, toDate);
		System.out.println(dataList);
		resp.setData(dataList);
		
	}


} 
