package com.axelor.app.gst.service;

import java.math.BigDecimal;

import com.axelor.gst.app.InvoiceLine;

public interface InvoiceLineService {
	
	public BigDecimal calculateSgstAndCgst(InvoiceLine invoiceLine);
	public BigDecimal calculateIgst(InvoiceLine invoiceLine);
	public BigDecimal claculateNetAmount(InvoiceLine invoiceLine);
	public BigDecimal calculateGrossAmount(InvoiceLine invoiceLine); //after this it will go to implementation after checking the binding file
	public BigDecimal calculateGST(InvoiceLine invoiceLine);
	

}
