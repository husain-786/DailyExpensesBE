package com.expenses.pojo;

import java.util.Date;

import lombok.Data;

@Data
public class EachExpensesDetailsVO {
	
	private int srno;
	
	private int productSrno;
	
    private String buyerUserUuid;
	
	private String contriUserUuid;
	
	private int price;

    private Date buyDate;
    
}
