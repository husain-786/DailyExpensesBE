package com.expenses.pojo;

import lombok.Data;

@Data
public class UserAndPricePOJO {
	
	private String userUuid;
	
	private String userName;
	
	private String userEmail;
	
	private int totalPriceToPay;
	
	public UserAndPricePOJO() {
		super();
	}

	public UserAndPricePOJO(String userUuid, String userName, double totalPriceToPay) {
		super();
		this.userUuid = userUuid;
		this.userName = userName;
		this.totalPriceToPay = (int) Math.ceil(totalPriceToPay);
	}
	
}
