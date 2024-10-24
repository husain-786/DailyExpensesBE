package com.expenses.pojo;

import java.util.Date;

import lombok.Data;

@Data
public class DailyExpensesReqVO {
	
	private String buyerUserUuid;
	
	private String contriUserDtls;	// contri-users in JSON format [{"name":"UUID", "Code":"PRICE"}]
	
	private String product;
	
	private int totalPrice;
	
	private Date fromDate;
	
	private Date toDate;
	
	private Date buyDate;	// used to save date of buying....
	
	private boolean paid;
	
	private boolean equalDistributed;
	
	private String mode;	// GET:- other users will pay to current user...., else current user will pay to other users...
	
}