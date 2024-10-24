package com.expenses.service;

import com.expenses.pojo.DailyExpensesReqVO;
import com.expenses.pojo.DailyExpensesRespVO;
import com.expenses.pojo.ResultVO;
import com.expenses.pojo.UserDtlVo;

public interface DailyExpensesService {

	DailyExpensesRespVO saveDetails(DailyExpensesReqVO dailyExpensesReqVO);

	ResultVO addUser(UserDtlVo userDtlVo);

	DailyExpensesRespVO getCompleteDetails(DailyExpensesReqVO dailyExpensesReqVO);

	DailyExpensesRespVO priceTogive(DailyExpensesReqVO dailyExpensesReqVO);

	DailyExpensesRespVO priceToget(DailyExpensesReqVO dailyExpensesReqVO);

	DailyExpensesRespVO finalResult(DailyExpensesReqVO dailyExpensesReqVO);

	DailyExpensesRespVO getAllActiveUsers(DailyExpensesReqVO dailyExpensesReqVO);

	DailyExpensesRespVO getAllOtherActiveUsers(DailyExpensesReqVO dailyExpensesReqVO);

}
