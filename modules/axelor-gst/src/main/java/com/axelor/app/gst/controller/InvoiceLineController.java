package com.axelor.app.gst.controller;

import java.math.BigDecimal;

import com.axelor.app.gst.service.InvoiceLineService;
import com.axelor.app.gst.service.InvoiceService;
import com.axelor.gst.app.Address;
import com.axelor.gst.app.Company;
import com.axelor.gst.app.Invoice;
import com.axelor.gst.app.InvoiceLine;
import com.axelor.gst.app.Product;
import com.axelor.gst.app.State;
import com.axelor.inject.Beans;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;

public class InvoiceLineController {
	
	public void setPrice(ActionRequest request, ActionResponse response) {
		InvoiceLine invoiceLine=request.getContext().asType(InvoiceLine.class);
		BigDecimal costPrice=invoiceLine.getProduct().getCostPrice();
		response.setValue("price", costPrice);			
	}
	
	//for setting product info,
	public void setProductInfo(ActionRequest request,ActionResponse response) {
	InvoiceLine invoiceLine=request.getContext().asType(InvoiceLine.class);
    Product prod=invoiceLine.getProduct();
    String prodCode=prod.getCode();
    String prodName=prod.getName();
    String prodFullName=" ["+prodCode+"] "+prodName;
    response.setValue("item", prodFullName);
    response.setValue("gstRate", prod.getGstRate());	
	}
	
	//for setting netAmount
	public void setNetAmount(ActionRequest request,ActionResponse response) {
		InvoiceLine invoiceLine=request.getContext().asType(InvoiceLine.class);
		BigDecimal costPrice=invoiceLine.getProduct().getCostPrice();
		Integer quantity= invoiceLine.getQty();		
		BigDecimal netAmount=costPrice.multiply(BigDecimal.valueOf(quantity));
		response.setValue("netAmount", netAmount);	
	}
	
	//for setting gst,sgst,cgst
	public void setGst(ActionRequest request, ActionResponse response) {
		InvoiceLine invoiceLine = request.getContext().asType(InvoiceLine.class);
		Invoice invoice = invoiceLine.getInvoice();
		if(invoice == null) {
			invoice=request.getContext().getParent().asType(Invoice.class);
		}
		BigDecimal sAndCgst=BigDecimal.ZERO;
		BigDecimal igst =BigDecimal.ZERO;
		
		Boolean isState = Beans.get(InvoiceService.class).compareState(invoice.getInvoiceAddress(),invoice.getShippingAddress());//--//
		if(isState) {
			sAndCgst=Beans.get(InvoiceLineService.class).calculateSgstAndCgst(invoiceLine);
		}else {
			igst=Beans.get(InvoiceLineService.class).calculateIgst(invoiceLine);
		}
		response.setValue("sgst", sAndCgst);
		response.setValue("cgst", sAndCgst);
		response.setValue("igst", igst);
	}
	
	//for setting grossAmount
	public void setGrossAmount(ActionRequest request, ActionResponse response)
	{    
		//getting object of invoiceLine
     	InvoiceLine invoiceLine= request.getContext().asType(InvoiceLine.class); //aftert this line it will go to invoiceLineService class
		BigDecimal grossAmount =Beans.get(InvoiceLineService.class).calculateGrossAmount(invoiceLine); //after calculation grossAmount value arrive here
		response.setValue("grossAmount", grossAmount); //from here it is sent to view from were it is done by action.
		
	}
	
}	
		
		
		
		
//		  BigDecimal netAmount=invoiceLine.getNetAmount();
//	      BigDecimal igst=invoiceLine.getIgst();
//	      BigDecimal cgst=invoiceLine.getCgst();
//	      BigDecimal sgst=invoiceLine.getSgst();
//	      BigDecimal zero=new BigDecimal(0);
	     // BigDecimal grossAmount= new BigDecimal(0);
//	      if(igst.equals(BigDecimal.ZERO)) {
//	    	  BigDecimal grossAmount= netAmount.add(sgst).add(cgst);
//	    	  response.setValue("grossAmount",grossAmount);
//	      }
//	      else
//	      { 
//	    	  BigDecimal grossAmount= netAmount.add(igst);
//	    	  response.setValue("grossAmount",grossAmount);
//	      }
	      //BigDecimal grossAmount= netAmount.add(igst);
	     // System.err.println(grossAmount);
		
		//response.setValue("grossAmount",grossAmount);


//	public void setIgst(ActionRequest request,ActionResponse response)
//	{
//		InvoiceLine invoiceLine=request.getContext().asType(InvoiceLine.class);
//		Invoice invoice= request.getContext().getParent().asType(Invoice.class);
//		
//		BigDecimal gstRate=invoiceLine.getGstRate();
//		
//		BigDecimal netAmount = invoiceLine.getNetAmount();
//		
//		Address address = invoice.getInvoiceAddress();
//		State state =address.getState();
//		String stName=state.getName();
//		
//		Company company= invoice.getCompany();
//		State companyState= company.getAddress().getState();
//		String cmStateName=companyState.getName();
//		if(stName.equals(cmStateName)) 
//		{
//			BigDecimal igst= netAmount.multiply(gstRate);
//		System.out.println(igst);
//			response.setValue("igst",igst);
//		}
//	}


