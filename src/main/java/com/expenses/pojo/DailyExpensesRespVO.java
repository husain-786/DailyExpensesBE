package com.expenses.pojo;

import java.util.List;

import lombok.Data;

@Data
public class DailyExpensesRespVO {
	
	private ResultVO resultVO;
	
	private List<ProductDetailsVO> productDetailsVOs;	// stores details of all products between selected dates.....

	private List<UserAndPricePOJO> priceThatOtherWillPay;	// stores user and price details that will be paid to current user....
	
	private List<UserAndPricePOJO> priceThatWillBePaidToOthers;	// stores user and price details that will be paid By current user....
	
	private List<UserAndPricePOJO> usersList;	// used to return user details....
	
	private List<UserAndPricePOJO> finalCalculations;	// stores user and price details that will be paid By current user....
}
