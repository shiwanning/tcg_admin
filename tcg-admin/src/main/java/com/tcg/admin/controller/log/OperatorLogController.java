/*
 * Copyright(c) 2019 WishLand All rights reserved.
 * distributed with this file and available online at
 */
package com.tcg.admin.controller.log;

import com.tcg.admin.model.OperatorLog;
import com.tcg.admin.service.log.OperatorLogService;
import com.tcg.admin.to.condition.OperatorLogCondition;
import com.tcg.admin.to.response.JsonResponseT;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @version 1.0
 * @author lyndon.J
 */

@Validated
@RestController
@RequestMapping("/resources/operatorLog")
public class OperatorLogController{

    
    @Resource
    private OperatorLogService operatorLogService;


	@GetMapping("/list")
	public JsonResponseT<Page<OperatorLog>> getOperatorLogList(@RequestParam(value = "merchant", required = false)String merchant,
												 @RequestParam(value = "userName", required = false)String userName,
												 @RequestParam(value = "function", required = false)String function,
												 @RequestParam(value = "editedUserName", required = false)String editedUserName,
												 @RequestParam(value = "startDateTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date startDateTime,
												 @RequestParam(value = "endDateTime", required = false)  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endDateTime,
												 @RequestParam(value = "pageNo", required = false) int pageNo,
												 @RequestParam(value = "pageSize", required = false) int pageSize) {
		OperatorLogCondition condition = new OperatorLogCondition();
		condition.setMerchant(merchant);
		condition.setUserName(userName);
		condition.setEditedUserName(editedUserName);
		condition.setStartDateTime(startDateTime);
		condition.setEndDateTime(endDateTime);
		condition.setFunction(function);
		Page<OperatorLog> page = operatorLogService.getOperatorLogList(condition, pageNo, pageSize);
		return new JsonResponseT<>(true,page);
	}

	

}
