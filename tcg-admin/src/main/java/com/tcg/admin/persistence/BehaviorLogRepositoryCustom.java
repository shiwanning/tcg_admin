package com.tcg.admin.persistence;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.mysema.query.BooleanBuilder;
import com.mysema.query.jpa.impl.JPAQuery;
import com.tcg.admin.common.error.AdminErrorCode;
import com.tcg.admin.common.exception.AdminServiceBaseException;
import com.tcg.admin.model.BehaviorLog;
import com.tcg.admin.model.QBehaviorLog;
import com.tcg.admin.utils.DateTools;
import com.tcg.admin.utils.QuerydslPageUtil;

@Repository
public class BehaviorLogRepositoryCustom extends BaseDAORepository implements Serializable {
	
	private static final long serialVersionUID = -929871946627149521L;
	private static final Integer LOGIN = 4;
	private static final Integer LOGOUT = 41;
	private static final Integer MENU = 9;
	private static final Integer TASK = 10;
	private static final Integer ALL = 0;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(BehaviorLogRepositoryCustom.class);
	
	@PersistenceContext(unitName = "persistenceUnit")
    private EntityManager entityManager;
	
	
	public Map<String, Object> findUserBehaviorLog(String username, Integer page, Integer pageSize, Integer actionType){
 		StringBuilder jpql=new StringBuilder();
		jpql.append("SELECT log FROM BehaviorLog log")
			.append(" WHERE 1=1")
			.append(" AND log.operatorName = :oprName");
//			.append(" AND log.resourceType != null");
		if(actionType!=null){
			if(actionType == 4){
				jpql.append(" AND log.resourceType in :actionTypes");
			}else {
				jpql.append(" AND log.resourceType=:actionType");
			}
		}
		jpql.append(" ORDER BY log.startProcessDate DESC");
		Map<String, Object> params = new HashMap<>();
		params.put("oprName", username);
		if(actionType!=null){
			if(actionType == 4){
				List<Integer> actionTypeList = new ArrayList();
				actionTypeList.add(4);
				actionTypeList.add(41);
				params.put("actionTypes", actionTypeList);
			}else {
				params.put("actionType", actionType);
			}
		}
		return this.findObjectListByJpl(jpql.toString(), params, page, pageSize);
	}


	public Page<BehaviorLog> findUserBehaviorLog(String merchant, String username, Integer actionType, List<Integer> menuIdList, String keyword, String startDateTime, String endDateTime, List<Integer> menuIds, List<String> merchantCodeList, Pageable pageable){
		EntityManager em = entityManager;
		JPAQuery query = new JPAQuery(em);
		QBehaviorLog behaviorLog = QBehaviorLog.behaviorLog;

		BooleanBuilder booleanBuilder = new BooleanBuilder();

		if(StringUtils.isNotEmpty(username)) {
			booleanBuilder.and(behaviorLog.operatorName.like("%"+username+"%"));
		}

		Date start = DateTools.toDate("yyyy/MM/dd HH:mm:ss", startDateTime);
		Date end = DateTools.toDate("yyyy/MM/dd HH:mm:ss", endDateTime);

		booleanBuilder.and(behaviorLog.startProcessDate.between(start,end));

		/* merchant view */
		if(actionType!= ALL){
			booleanBuilder.and(behaviorLog.resourceType.eq(actionType));
		}else{
			// merchant view not included menu(9), task(10) now
			List<Integer> actionTypeList = Arrays.asList(LOGIN, LOGOUT, MENU, TASK);
			booleanBuilder.and(behaviorLog.resourceType.notIn(actionTypeList));
		}

		if(StringUtils.isNotEmpty(keyword)) {
			booleanBuilder.and(behaviorLog.parameters.like("%"+keyword+"%"));
		}

		if (CollectionUtils.isNotEmpty(menuIdList)){
			if(menuIds.containsAll(menuIdList)){
				booleanBuilder.and(behaviorLog.resourceId.in(menuIdList));
			}else{
				throw new AdminServiceBaseException(AdminErrorCode.MENU_NOT_EXIST_ERROR, "no permission for these menuId");
			}
		}else if (CollectionUtils.isNotEmpty(menuIds)) {
			booleanBuilder.and(behaviorLog.resourceId.in(menuIds));
		}

		if (CollectionUtils.isNotEmpty(merchantCodeList)) {
			booleanBuilder.and(behaviorLog.merchantCode.in(merchantCodeList));
		}

		if (StringUtils.isNotEmpty(merchant)) {
			booleanBuilder.and(behaviorLog.merchantCode.eq(merchant));
		}

		query.from(behaviorLog)
				.where(booleanBuilder)
				.orderBy(behaviorLog.startProcessDate.desc());

		return QuerydslPageUtil.pagination(query, behaviorLog, pageable);
	}

	public Page<BehaviorLog> findUserBehaviorLogForSystem(String merchant, String username, Integer actionType, List<Integer> menuIdList, String keyword, String startDateTime, String endDateTime, List<Integer> menuIds, Pageable pageable){
		EntityManager em = entityManager;
		JPAQuery query = new JPAQuery(em);
		QBehaviorLog behaviorLog = QBehaviorLog.behaviorLog;

		BooleanBuilder booleanBuilder = new BooleanBuilder();

		List<Integer> actionTypeList = Arrays.asList(LOGIN, LOGOUT);

		if(StringUtils.isNotEmpty(username)) {
			booleanBuilder.and(behaviorLog.operatorName.like("%"+username+"%"));
		}

		Date start = DateTools.toDate("yyyy/MM/dd HH:mm:ss", startDateTime);
		Date end = DateTools.toDate("yyyy/MM/dd HH:mm:ss", endDateTime);

		booleanBuilder.and(behaviorLog.startProcessDate.between(start,end));

		if(actionType!= 0){
			if(actionType == 4){
				booleanBuilder.and(behaviorLog.resourceType.in(actionTypeList));
			}else {
				booleanBuilder.and(behaviorLog.resourceType.eq(actionType));
			}
		}

		if(StringUtils.isNotEmpty(keyword)) {
			booleanBuilder.and(behaviorLog.parameters.like("%"+keyword+"%"));
		}

		if (CollectionUtils.isNotEmpty(menuIdList)){
			if(menuIds.containsAll(menuIdList)){
				booleanBuilder.and(behaviorLog.resourceId.in(menuIdList));
			}else{
				throw new AdminServiceBaseException(AdminErrorCode.MENU_NOT_EXIST_ERROR, "no permission for these menuId");
			}
		}else if (CollectionUtils.isNotEmpty(menuIds)) {
			booleanBuilder.and(behaviorLog.resourceId.in(menuIds).or(behaviorLog.resourceType.in(actionTypeList)));
		}

		if (StringUtils.isNotEmpty(merchant)) {
			booleanBuilder.and(behaviorLog.merchantCode.eq(merchant));
		}

		query.from(behaviorLog)
				.where(booleanBuilder)
				.orderBy(behaviorLog.startProcessDate.desc());

		return QuerydslPageUtil.pagination(query, behaviorLog, pageable);
	}

}
