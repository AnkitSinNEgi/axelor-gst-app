package com.axelor.app.gst.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import com.axelor.gst.app.Address;
import com.axelor.gst.app.Contact;
import com.axelor.gst.app.Invoice;
import com.axelor.gst.app.InvoiceLine;
import com.axelor.gst.app.Party;
import com.axelor.gst.app.Product;
import com.axelor.gst.app.repo.ProductRepository;
import com.axelor.inject.Beans;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class InvoiceServiceImp implements InvoiceService {

	@Inject
	Provider<EntityManager> invoice;
	
	@Override
	public boolean checkString(String s1, String s2) {
		if(s1.equalsIgnoreCase(s2))
			return true;
		return false;
	}

	
	
	@Override
	public boolean compareState(Address s1, Address s2) {
		if(s1.getState().equals(s2.getState()))
			return true;
		return false;
	}

	
	
	@Override
	public List<InvoiceLine> invoiceItemAmounts(Invoice invoice) {
		
		List<InvoiceLine> invoiceItems=invoice.getInvoiceItem();
		
		BigDecimal igst=BigDecimal.ZERO;
		BigDecimal sgst=BigDecimal.ZERO;
		BigDecimal cgst=BigDecimal.ZERO;
		
		try {
			if(invoice.getCompany().equals(null) || invoice.getInvoiceAddress().equals(null) || invoice.getParty().equals(null)) {
				
				invoiceItems=invoice.getInvoiceItem();
				
				for(InvoiceLine item:invoiceItems) {
					
					item.setCgst(cgst);
					item.setSgst(sgst);
					item.setIgst(igst);
					item.setGrossAmount(item.getNetAmount());
				}
			}
			else {
				if(compareState(invoice.getCompany().getAddress(), invoice.getInvoiceAddress())) {
					
					for(InvoiceLine item:invoiceItems) {	
						
						BigDecimal netAmount=item.getNetAmount();
						
						BigDecimal gstRate=item.getGstRate().divide(BigDecimal.valueOf(100));
						
						sgst=netAmount.multiply(gstRate).divide(BigDecimal.valueOf(2));
						
						cgst=netAmount.multiply(gstRate).divide(BigDecimal.valueOf(2));
						
						BigDecimal grossAmount=netAmount;
						
						grossAmount=grossAmount.add(sgst);
						grossAmount=grossAmount.add(cgst);
						
						item.setIgst(igst);
						item.setSgst(sgst);
						item.setCgst(cgst);
						item.setGrossAmount(grossAmount);	
					}
				}
				else {
					for(InvoiceLine item:invoiceItems) {
						
						BigDecimal netAmount=item.getNetAmount();
						
						BigDecimal gstRate=item.getGstRate().divide(BigDecimal.valueOf(100));
						
						igst=netAmount.multiply(gstRate);
						
						BigDecimal grossAmount=netAmount;
						
						grossAmount=grossAmount.add(igst);
						
						item.setIgst(igst);
						item.setCgst(cgst);
						item.setSgst(sgst);
						item.setGrossAmount(grossAmount);
					}
				}
				}
		}catch(Exception e) {	}
		
		return invoiceItems;
	}

	
	
	@Override
	public List<InvoiceLine> invoiceItemOnCreate(Invoice invoice, List<Integer> ids) {
		
		List<InvoiceLine> items=new ArrayList<InvoiceLine>();
		
		for(Integer id:ids) {
			
			Long il_id=(long) id;
			
			Product i=Beans.get(ProductRepository.class).find(il_id);
			
			InvoiceLine invoiceLine=new InvoiceLine();
			
			invoiceLine.setProduct(i);
			
			invoiceLine.setInvoice(invoice);
			
			invoiceLine.setQty(1);
			
			invoiceLine.setHsbn(i.getHsbn());
			
			invoiceLine.setItem("["+i.getCode()+"] "+i.getName());
			
			invoiceLine.setNetAmount(i.getSalePrice());
			
			invoiceLine.setGstRate(i.getGstRate());
			
			invoiceLine.setPrice(i.getSalePrice());
			
			items.add(invoiceLine);
		}
		return items;
	}

	
	
	@Override
	public Object getAddress(Party p, String str) {
		
		if(str.equalsIgnoreCase("primary")) {
			
			List<Contact> contactList=p.getContact();
			
			Contact contact=null;
			
			for(Contact c:contactList) {
				if(checkString("primary", c.getType())) {
					contact= c;
				}
			}
			return contact;
		}
		else {
			
			List<Address> addressList=p.getAddress();
			
			Address address=null;
			
			for(Address a:addressList) {
				if(checkString(str, a.getType())) {
					address=a;
					break;
				}
				
				else if(checkString("default", a.getType())) 
					address=a;
			}
			return address;
		}
	}

	
	
	@Override
	public BigDecimal getAmounts(List<InvoiceLine> il, String str) {
		
		List<BigDecimal> list=new ArrayList <BigDecimal>();
		
		if(str.equalsIgnoreCase("netAmount")) {
			for(InvoiceLine item:il)
				list.add(item.getNetAmount());
		}
		
		else if(str.equalsIgnoreCase("igst"))
		{
			for(InvoiceLine item:il)
				list.add(item.getIgst());
		}
		
		else if(str.equalsIgnoreCase("sgst"))
		{
			for(InvoiceLine item:il)
				list.add(item.getSgst());
		}
		
		else if(str.equalsIgnoreCase("cgst"))
		{
			for(InvoiceLine item:il)
				list.add(item.getCgst());
		}
		
		else if(str.equalsIgnoreCase("grossAmount"))
		{
			for(InvoiceLine item:il)
				list.add(item.getGrossAmount());
		}
		
		BigDecimal total=BigDecimal.valueOf(0);
		
		for(BigDecimal value:list) 
			total=total.add(value);
		
		return total;
	}



	@Override
	public List<Map<String, Object>> getUnpaidInvoiceData(LocalDate fromDate, LocalDate toDate) {
		EntityManager em=invoice.get();
		Query query=em.createQuery("Select count(i) as total from Invoice i where i.status not in ('paid','cancelled') and date(i.invoiceDate) between :fd and :td group by i.party.name order by total");
		query.setParameter("fd", fromDate);
		query.setParameter("td", toDate);
		Query q=em.createQuery("Select i.party.name as pname from Invoice i where i.status not in ('paid','cancelled') and date(i.invoiceDate) between :fd and :td group by i.party.name order by count(i)");
		q.setParameter("fd", fromDate);
		q.setParameter("td", toDate);
		List<Object> countList=query.getResultList();
		List<String> nameList=q.getResultList();
		List<Map<String, Object>> dataList = new ArrayList<Map<String,Object>>();
		for(int i=0;i<countList.size();i++) {
			Map<String,Object> datamap=new HashMap<String, Object>();
			datamap.put("pname", nameList.get(i));
			datamap.put("total", countList.get(i));
			dataList.add(datamap);
		}
		return dataList;
	}



	@Override
	public List<Map<String, Object>> getCustomerPerState() {
		EntityManager em=invoice.get();
		Query q1=em.createQuery("Select count(i) from Invoice i group by i.invoiceAddress.state order by count(i)");
		Query q2=em.createQuery("Select i.invoiceAddress.state.name from Invoice i group by i.invoiceAddress.state.name order by count(i)");
		List<Object> countList=q1.getResultList();
		List<Object> stateList=q2.getResultList();
		List<Map<String, Object>> dataList = new ArrayList<Map<String,Object>>();
		for(int i=0;i<countList.size();i++) {
			Map<String,Object> datamap=new HashMap<String, Object>();
			datamap.put("state", stateList.get(i));
			datamap.put("total", countList.get(i));
			dataList.add(datamap);
		}
		return dataList;
	}



	@Override
	public List<Map<String, Object>> getAmountsPerStatus() {
		EntityManager em=invoice.get();
		Query q1=em.createQuery("Select sum(i.grossAmount) as amount From Invoice as i Group by i.status Order by amount");
		Query q2=em.createQuery("Select i.status as status From Invoice as i Group by i.status Order by sum(i.grossAmount)");
		List<Object> amountList=q1.getResultList();
		List<Object> statusList=q2.getResultList();
		List<Map<String, Object>> dataList = new ArrayList<Map<String,Object>>();
		for(int i=0;i<amountList.size();i++) {
			Map<String,Object> datamap=new HashMap<String, Object>();
			datamap.put("status", statusList.get(i));
			datamap.put("amount", amountList.get(i));
			dataList.add(datamap);
		}
		return dataList;
	}



	@Override
	public List<Map<String, Object>> getPerCategory(LocalDateTime fromDate, LocalDateTime toDate) {
		EntityManager em=invoice.get();
		Query q1=em.createQuery("Select count(i) from Invoice i,InvoiceLine il where i.id=il.invoice and i.status='paid' and i.invoiceDate between :fd and :td group by il.product  order by count(i)");
		q1.setParameter("fd", fromDate);
		q1.setParameter("td", toDate);
		System.out.println(q1.getResultList());
		Query q2=em.createQuery("Select il.product.name from Invoice i,InvoiceLine il where i.id=il.invoice and  i.status='paid' and i.invoiceDate between :fd and :td group by il.product.name order by count(i)");
		q2.setParameter("fd", fromDate);
		q2.setParameter("td", toDate);
		System.out.println(q2.getResultList());
		Query q3=em.createQuery("Select i.invoiceItem.product.category.name from Invoice i where i.status='paid' and i.invoiceDate between :fd and :td group by i.invoiceItem.product,i.invoiceItem.pruduct.category order by count(i)");
		q3.setParameter("fd", fromDate);
		q3.setParameter("td", toDate);
		List<Object> countList=q1.getResultList();
		List<Object> productList=q2.getResultList();
		List<Object> categoryList=q3.getResultList();
		List<Map<String, Object>> dataList = new ArrayList<Map<String,Object>>();
		for(int i=0;i<countList.size();i++) {
			Map<String,Object> datamap=new HashMap<String, Object>();
			datamap.put("product", productList.get(i));
			datamap.put("category", categoryList.get(i));
			datamap.put("total", countList.get(i));
			dataList.add(datamap);
		}
		System.out.println(dataList);
		return dataList;
	}
	
	
}
