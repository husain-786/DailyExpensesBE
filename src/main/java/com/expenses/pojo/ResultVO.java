package com.expenses.pojo;

import lombok.Data;

@Data
public class ResultVO {

	private String msgCode;
	
	private String msgDescr;
	
	private boolean isError;
	
	private String userUuid;


	public ResultVO(String msgCode, String msgDescr, boolean isrror) {
		super();
		this.msgCode = msgCode;
		this.msgDescr = msgDescr;
		this.isError = isrror;
	}
		
}
