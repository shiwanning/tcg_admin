package com.tcg.admin.service;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.data.domain.Page;

import com.tcg.admin.controller.core.BehaviorRequestWrapper;
import com.tcg.admin.model.BehaviorLog;
import com.tcg.admin.model.MenuItem;
import com.tcg.admin.to.VerificationData;

public interface BehaviorLogService {

	public static final Integer VIEW = 0;
	public static final Integer ADD = 1;
	public static final Integer EDIT = 2;
	public static final Integer REMOVE = 3;
	public static final Integer LOGIN = 4;
	public static final Integer LOGOUT = 41;
	public static final Integer EXPORT = 5;
	public static final Integer REJECT = 6;
	public static final Integer APPROVE = 7;
	public static final Integer CHECK = 8;
	public static final Integer MENU = 9;
	public static final Integer TASK = 10;
	public static final Integer OTHER = 11;

	void saveBehaviorLog(HttpServletRequest request,
			BehaviorRequestWrapper behaviorRequestWrapper
			,String customerName, Date startDate, Date endDate, String merchantCode, MenuItem menuItem, String pathStrings);
	Page<BehaviorLog> queryBehaviorLog(String merchant,String username, int actionType, List<Integer> resourceIdList,String keyword, String startDateTime, String endDateTime ,int pageNo, int pageSize);

	Page<BehaviorLog> queryBehaviorLogOfSystem(String merchant,String username, int actionType, List<Integer> resourceIdList,String keyword, String startDateTime, String endDateTime ,int pageNo, int pageSize);

	void writeBehaviorLog(HttpServletRequest request,
						 BehaviorRequestWrapper behaviorRequestWrapper
			,String customerName, Date startDate, Date endDate, String merchantCode);


	void saveBehaviorLog(VerificationData verificationData, MenuItem menuItem );
}
