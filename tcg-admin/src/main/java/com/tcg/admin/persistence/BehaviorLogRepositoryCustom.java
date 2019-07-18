package com.tcg.admin.persistence;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Lists;
import com.mysema.query.BooleanBuilder;
import com.mysema.query.jpa.impl.JPAQuery;
import com.tcg.admin.common.error.AdminErrorCode;
import com.tcg.admin.common.exception.AdminServiceBaseException;
import com.tcg.admin.model.BehaviorLog;
import com.tcg.admin.model.QBehaviorLog;
import com.tcg.admin.to.condition.BehaviorCondition;
import com.tcg.admin.utils.DateTools;
import com.tcg.admin.utils.QuerydslPageUtil;

@Repository
public class BehaviorLogRepositoryCustom extends BaseDAORepository {
	
	private static final Integer LOGIN = 4;
	private static final Integer LOGOUT = 41;
	private static final Integer MENU = 9;
	private static final Integer TASK = 10;
	private static final Integer ALL = 0;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(BehaviorLogRepositoryCustom.class);
	
	@PersistenceContext(unitName = "persistenceUnit")
    private EntityManager entityManager;
	


	public List<Object[]> statisticalLoginStatus(String userName, String date){
		StringBuilder sb = new StringBuilder();
		Map<String, Object> keyMap = Maps.newHashMap();
		keyMap.put("userName", userName);
		keyMap.put("calDate", date);
		sb.append(" select a.IP_ADDRESS, SUM(CASE WHEN a.remark is null OR (a.remark is not null and a.remark <> 'SUCCESS') then 1 ELSE 0 END) errorCount, ")
		.append(" SUM(CASE WHEN a.remark is not null and a.remark = 'SUCCESS' then 1 ELSE 0 END) successCount ")
		.append(" from BEHAVIOR_LOG a ")
		.append(" where a.OPERATOR_NAME = :userName")
		.append(" and a.RESOURCE_TYPE = 4 ");
		if(StringUtils.isNotEmpty(date)){
			sb.append(" and a.END_PROC_DATE > TO_DATE(:calDate , 'yyyy-MM-dd HH24:mi:ss') ");
		}
		sb.append(" group by a.IP_ADDRESS ");

		return this.getListForPage4SQL(sb.toString(), keyMap, null, null);
	}

	public Map<String, Object> findUserBehaviorLog(String username, Integer page, Integer pageSize, Integer actionType){
 		StringBuilder jpql=new StringBuilder();
		jpql.append("SELECT log FROM BehaviorLog log")
			.append(" WHERE 1=1")
			.append(" AND log.operatorName = :oprName");
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
				List<Integer> actionTypeList = Lists.newLinkedList();
				actionTypeList.add(4);
				actionTypeList.add(41);
				params.put("actionTypes", actionTypeList);
			}else {
				params.put("actionType", actionType);
			}
		}
		return this.findObjectListByJpl(jpql.toString(), params, page, pageSize);
	}


	public Page<BehaviorLog> findUserBehaviorLog(BehaviorCondition condition, Pageable pageable){
		EntityManager em = entityManager;
		JPAQuery query = new JPAQuery(em);
		QBehaviorLog behaviorLog = QBehaviorLog.behaviorLog;

		BooleanBuilder booleanBuilder = new BooleanBuilder();

		if(StringUtils.isNotEmpty(condition.getUsername())) {
			booleanBuilder.and(behaviorLog.operatorName.like("%"+condition.getUsername()+"%"));
		}

		Date start = DateTools.parseDate(condition.getStartDateTime(), "yyyy/MM/dd HH:mm:ss");
		Date end = DateTools.parseDate(condition.getEndDateTime(), "yyyy/MM/dd HH:mm:ss");

		booleanBuilder.and(behaviorLog.startProcessDate.between(start,end));

		/* merchant view */
		if(condition.getActionType()!= ALL){
			booleanBuilder.and(behaviorLog.resourceType.eq(condition.getActionType()));
		}else{
			// merchant view not included menu(9), task(10) now
			List<Integer> actionTypeList = Arrays.asList(LOGIN, LOGOUT, MENU, TASK);
			booleanBuilder.and(behaviorLog.resourceType.notIn(actionTypeList));
		}

		if(StringUtils.isNotEmpty(condition.getKeyword())) {
			booleanBuilder.and(behaviorLog.parameters.like("%"+condition.getKeyword()+"%"));
		}

		if (CollectionUtils.isNotEmpty(condition.getResourceIdList())){
			if(condition.getMenuIds().containsAll(condition.getResourceIdList())){
				booleanBuilder.and(behaviorLog.resourceId.in(condition.getResourceIdList()));
			}else{
				throw new AdminServiceBaseException(AdminErrorCode.MENU_NOT_EXIST_ERROR, "no permission for these menuId");
			}
		}else if (CollectionUtils.isNotEmpty(condition.getMenuIds())) {
			booleanBuilder.and(behaviorLog.resourceId.in(condition.getMenuIds()));
		}

		if (CollectionUtils.isNotEmpty(condition.getMerchantCodeList())) {
			booleanBuilder.and(behaviorLog.merchantCode.in(condition.getMerchantCodeList()));
		}

		if (StringUtils.isNotEmpty(condition.getMerchant())) {
			booleanBuilder.and(behaviorLog.merchantCode.eq(condition.getMerchant()));
		}

		query.from(behaviorLog)
				.where(booleanBuilder)
				.orderBy(behaviorLog.startProcessDate.desc());

		return QuerydslPageUtil.pagination(query, behaviorLog, pageable);
	}

	public Page<BehaviorLog> findUserBehaviorLogForSystem(BehaviorCondition condition, Pageable pageable){
		EntityManager em = entityManager;
		JPAQuery query = new JPAQuery(em);
		
		
		
		QBehaviorLog behaviorLog = QBehaviorLog.behaviorLog;

		BooleanBuilder booleanBuilder = new BooleanBuilder();

		List<Integer> actionTypeList = Arrays.asList(LOGIN, LOGOUT);

		if(StringUtils.isNotEmpty(condition.getUsername())) {
			booleanBuilder.and(behaviorLog.operatorName.like("%"+condition.getUsername()+"%"));
		}

		Date start = DateTools.parseDate(condition.getStartDateTime(), "yyyy/MM/dd HH:mm:ss");
		Date end = DateTools.parseDate(condition.getEndDateTime(), "yyyy/MM/dd HH:mm:ss");

		booleanBuilder.and(behaviorLog.startProcessDate.between(start,end));

		if(condition.getActionType()!= 0){
			if(condition.getActionType() == 4){
				booleanBuilder.and(behaviorLog.resourceType.in(actionTypeList));
			}else {
				booleanBuilder.and(behaviorLog.resourceType.eq(condition.getActionType()));
			}
		}

		if(StringUtils.isNotEmpty(condition.getKeyword())) {
			booleanBuilder.and(behaviorLog.parameters.like("%"+condition.getKeyword()+"%"));
		}

		if (CollectionUtils.isNotEmpty(condition.getResourceIdList())){
			if(condition.getMenuIds().containsAll(condition.getResourceIdList())){
				booleanBuilder.and(behaviorLog.resourceId.in(condition.getResourceIdList()));
			}else{
				throw new AdminServiceBaseException(AdminErrorCode.MENU_NOT_EXIST_ERROR, "no permission for these menuId");
			}
		}else if (CollectionUtils.isNotEmpty(condition.getMenuIds())) {
			booleanBuilder.and(behaviorLog.resourceId.in(condition.getMenuIds()).or(behaviorLog.resourceType.in(actionTypeList)));
		}

		if (StringUtils.isNotEmpty(condition.getMerchant())) {
			booleanBuilder.and(behaviorLog.merchantCode.eq(condition.getMerchant()));
		}

		query.from(behaviorLog)
				.where(booleanBuilder)
				.orderBy(behaviorLog.startProcessDate.desc());

		return QuerydslPageUtil.pagination(query, behaviorLog, pageable);
	}

}
