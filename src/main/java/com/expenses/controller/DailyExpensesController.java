package com.expenses.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.expenses.pojo.DailyExpensesReqVO;
import com.expenses.pojo.DailyExpensesRespVO;
import com.expenses.pojo.ResultVO;
import com.expenses.pojo.UserDtlVo;
import com.expenses.service.DailyExpensesService;

@RestController
public class DailyExpensesController {
	
	@Autowired
	private DailyExpensesService dailyExpensesService;
	
	
	
	@PostMapping("/get/all/active/users")
	public DailyExpensesRespVO getAllActiveUsers(@RequestBody DailyExpensesReqVO dailyExpensesReqVO) {		
		return dailyExpensesService.getAllActiveUsers(dailyExpensesReqVO);
	}
	
	@PostMapping("/get/all/other/users")
	public DailyExpensesRespVO getAllOtherActiveUsers(@RequestBody DailyExpensesReqVO dailyExpensesReqVO) {		
		return dailyExpensesService.getAllOtherActiveUsers(dailyExpensesReqVO);
	}
	
	// Saving product Details.,....
	@PostMapping("/save/details")
	public DailyExpensesRespVO saveDetails(@RequestBody DailyExpensesReqVO dailyExpensesReqVO) {		
		return dailyExpensesService.saveDetails(dailyExpensesReqVO);
	}
	
	/*
		@CrossOrigin(origins = "http://localhost:3000")
		-> controller level configuration for cores...
	*/
	@PostMapping("/add/user")
	public ResultVO addUser(@RequestBody UserDtlVo userDtlVo) {		
		return dailyExpensesService.addUser(userDtlVo);		
	}	
	
	@PostMapping("/get/complete/details")
	public DailyExpensesRespVO getCompleteDetails(@RequestBody DailyExpensesReqVO dailyExpensesReqVO) {		
		return dailyExpensesService.getCompleteDetails(dailyExpensesReqVO);		
	}
	
	@PostMapping("/price/togive")
	public DailyExpensesRespVO priceTogive(@RequestBody DailyExpensesReqVO dailyExpensesReqVO) {		
		return dailyExpensesService.priceTogive(dailyExpensesReqVO);		
	}
	
	@PostMapping("/price/toget")
	public DailyExpensesRespVO priceToget(@RequestBody DailyExpensesReqVO dailyExpensesReqVO) {		
		return dailyExpensesService.priceToget(dailyExpensesReqVO);		
	}
	
	@PostMapping("/final/result")
	public DailyExpensesRespVO finalResult(@RequestBody DailyExpensesReqVO dailyExpensesReqVO) {		
		return dailyExpensesService.finalResult(dailyExpensesReqVO);		
	}
	
}
