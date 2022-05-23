package com.axelor.app.gst.module;

import com.axelor.app.AxelorModule;
import com.axelor.app.gst.service.InvoiceLineService;
import com.axelor.app.gst.service.InvoiceLineServiceImp;
import com.axelor.app.gst.service.InvoiceService;
import com.axelor.app.gst.service.InvoiceServiceImp;
import com.axelor.app.gst.service.SequenceService;
import com.axelor.app.gst.service.SequenceServiceImp;


public class Module extends AxelorModule {
	
	
	@Override
	protected void configure() {
		bind(InvoiceLineService.class).to(InvoiceLineServiceImp.class);
		bind(InvoiceService.class).to(InvoiceServiceImp.class);
		bind(SequenceService.class).to(SequenceServiceImp.class);
	}
}
