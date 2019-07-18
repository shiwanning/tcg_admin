package com.tcg.admin.service;

import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.data.domain.Page;

import com.tcg.admin.controller.core.BehaviorRequestWrapper;
import com.tcg.admin.model.BehaviorLog;
import com.tcg.admin.model.MenuItem;
import com.tcg.admin.to.VerificationData;
import com.tcg.admin.to.condition.BehaviorCondition;

public interface BehaviorLogService {

	void saveBehaviorLog(HttpServletRequest request,
			BehaviorRequestWrapper behaviorRequestWrapper
			,String customerName, Date date, String merchantCode, MenuItem menuItem, String pathStrings);
	
	Page<BehaviorLog> queryBehaviorLog(BehaviorCondition condition ,int pageNo, int pageSize);

	Page<BehaviorLog> queryBehaviorLogOfSystem(BehaviorCondition condition ,int pageNo, int pageSize);

	void writeBehaviorLog(HttpServletRequest request,
						 BehaviorRequestWrapper behaviorRequestWrapper
			,String customerName, Date date, String merchantCode);


	void saveBehaviorLog(VerificationData verificationData, MenuItem menuItem );

	Map<String, Object> statisticalLoginStatus(String userName, int pageNo, int pageSize, Date checkDate);
}
