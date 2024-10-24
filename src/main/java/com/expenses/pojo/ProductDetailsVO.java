package com.expenses.pojo;

import java.util.Date;

import lombok.Data;

@Data
public class ProductDetailsVO {
	
	private int productSrno;
	
    private String buyerUserUuid;

    private String buyerName;

	private Date buyDate;

    private String product;    

    private int productPrice;
    
    private String contriusers;
    
    public ProductDetailsVO(int productSrno, String buyerUserUuid, String buyerName, Date buyDate, String product,
    		int productPrice) {
    	super();
    	this.productSrno = productSrno;
    	this.buyerUserUuid = buyerUserUuid;
    	this.buyerName = buyerName;
    	this.buyDate = buyDate;
    	this.product = product;
    	this.productPrice = productPrice;
    }
        
}
