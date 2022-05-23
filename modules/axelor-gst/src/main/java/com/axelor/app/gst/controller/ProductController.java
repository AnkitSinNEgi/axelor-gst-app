package com.axelor.app.gst.controller;

import java.util.List;

import com.axelor.gst.app.Product;
import com.axelor.gst.app.repo.ProductRepository;
import com.axelor.meta.CallMethod;
import com.google.inject.Inject;

public class ProductController {
	
	@Inject
	private ProductRepository product;
	
	@CallMethod
	public String getProductIds(List<Integer> ids) {
		
		String str="";
		
		if(ids==null) {
			
			List<Product> list=product.all().fetch();
			
			for(Product p:list) {
				
				str+=Long.toString(p.getId());
				
				if(list.indexOf(p)!=list.size()-1) 
					str+=",";
			}
		}
		else {
			
			for(Integer id:ids) {
				
				str+=Integer.toString(id);
				
				if(ids.indexOf(id)!=ids.size()-1) 
					str+=",";
			}
		}
		
		return str;
	}
}
