package com.axelor.app.gst.service;

import java.math.BigDecimal;
import com.axelor.gst.app.InvoiceLine;
import com.google.inject.Inject;

public class InvoiceLineServiceImp implements InvoiceLineService{
	
	@Inject InvoiceService invoiceService;
	
	@Override
	public BigDecimal calculateGST(InvoiceLine invoiceLine) {
		BigDecimal gstRate =invoiceLine.getProduct().getGstRate();
		BigDecimal gstR=gstRate.divide(BigDecimal.valueOf(100));
		return gstR;
	}
	
	@Override
	public BigDecimal claculateNetAmount(InvoiceLine invoiceLine) {
		Integer quantity =invoiceLine.getQty();
		BigDecimal costPrice =invoiceLine.getProduct().getCostPrice();
		BigDecimal netAmount =costPrice.multiply(BigDecimal.valueOf(quantity));		
		return netAmount;
	}

	@Override
	public BigDecimal calculateSgstAndCgst(InvoiceLine invoiceLine) {		
		BigDecimal netAmount=claculateNetAmount(invoiceLine);
		BigDecimal sgst=netAmount.multiply(calculateGST(invoiceLine)).divide(BigDecimal.valueOf(2));		
		return sgst;
	}

	@Override
	public BigDecimal calculateIgst(InvoiceLine invoiceLine) {
		BigDecimal gstRate=invoiceLine.getGstRate();		
		BigDecimal netAmount=claculateNetAmount(invoiceLine);
		BigDecimal sgst=netAmount.multiply(gstRate.divide(BigDecimal.valueOf(100)));	
		
		return sgst;
	}
	//grossAmount will be calculated here (logic is written here)
	@Override
	public BigDecimal calculateGrossAmount(InvoiceLine invoiceLine) {
	     BigDecimal netAmount=invoiceLine.getNetAmount();
	     BigDecimal cgst=invoiceLine.getCgst();
	     BigDecimal igst =invoiceLine.getIgst();
	     BigDecimal sgst=invoiceLine.getSgst();
	     BigDecimal grossAmount=BigDecimal.ZERO;
	     
	     if(igst.equals(BigDecimal.ZERO)) {
	    	   grossAmount= netAmount.add(sgst).add(cgst);
	      }
	      else
	      { 
	    	  grossAmount= netAmount.add(igst);
	      }
		//it will return the calculated amount
		return grossAmount;
	}

	
}
