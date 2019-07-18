package com.tcg.admin.persistence;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mysema.query.BooleanBuilder;
import com.mysema.query.jpa.impl.JPAQuery;
import com.tcg.admin.common.constants.TaskConstant;
import com.tcg.admin.model.QStateRelationship;
import com.tcg.admin.model.QTask;
import com.tcg.admin.model.Task;
import com.tcg.admin.to.TaskQueryTO;
import com.tcg.admin.utils.DateTools;
import com.tcg.admin.utils.QuerydslPageUtil;

@Repository
public class TaskRepositoryCustom extends BaseDAORepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskRepositoryCustom.class);

    @PersistenceContext(unitName = "persistenceUnit")
    private EntityManager entityManager;

	private static final Integer DATE_WITH_TIME_LENGTH = 19;
   
	public Map<String, Object> getAvailableTaskByMerhants(List<Integer> merchantIds, List<Integer> roleIds, Integer pageNo, Integer maxResult, List<String> stateTypes, List<Integer> stateIds) {
		
		String jpql = generateAvailableTaskByMerhantsJpql(merchantIds, stateTypes, stateIds);

					  
		/*parameters container*/
		Map<String, Object> map = Maps.newHashMap();
		
		/* values for query */
		
		if(CollectionUtils.isNotEmpty(merchantIds)){
			map.put("ids", merchantIds);
			map.put("roleIds", roleIds);
		  }
		List<String> validViewStatus = Lists.newLinkedList();
		validViewStatus.add(TaskConstant.PROCESSING_STATUS);
		validViewStatus.add(TaskConstant.OPEN_STATUS);
		map.put("status", validViewStatus);
		map.put("owner", 0);
		if(!stateTypes.isEmpty()) {
		    map.put("stateTypes", stateTypes);
        }
		if(stateIds != null && !stateIds.isEmpty()) {
			map.put("stateId", stateIds);
		}

		/*process sql string*/
		return  this.findObjectListByJpl(jpql, map, pageNo, maxResult);
		
	}

	private String generateAvailableTaskByMerhantsJpql(List<Integer> merchantIds, List<String> stateTypes, List<Integer> stateIds) {
		StringBuilder jpql = new StringBuilder();
		if(CollectionUtils.isNotEmpty(merchantIds)){
			jpql.append("SELECT DISTINCT task ");
			jpql.append("FROM TaskSm task, RoleMenuPermission roleMenuP, StateRelationship stateRel ");
			jpql.append("WHERE task.status IN :status ");
			jpql.append("AND (task.merchantId IN :ids OR task.merchantId IS NULL) ");
			jpql.append("AND roleMenuP.roleId IN :roleIds ");
			  
			  if(!stateTypes.isEmpty()) {
				  jpql.append("AND task.state.type IN :stateTypes ");
			  }
			  if(stateIds != null && !stateIds.isEmpty()) {
				  jpql.append("AND task.state.stateId IN :stateId ");
			  }
			  
			  jpql.append("AND stateRel.fromState = task.state.stateId ");
		      jpql.append("AND stateRel.state.menuId IN roleMenuP.menuId ");
		      jpql.append("AND task.owner = :owner ");
		      jpql.append("ORDER BY task.updateTime DESC");
		}else{
			jpql.append("SELECT DISTINCT task ");
			jpql.append("FROM TaskSm task ");
			jpql.append("WHERE task.status IN :status ");
			  if(!stateTypes.isEmpty()) {
				  jpql.append("AND task.state.type IN :stateTypes ");
              }
			  if(stateIds != null && !stateIds.isEmpty()) {
				  jpql.append("AND task.state.stateId IN :stateId ");
			  }
			  jpql.append("AND task.owner = :owner ");
			  jpql.append("ORDER BY task.updateTime DESC");
		}
		return jpql.toString();
	}

	/**
     * @description : pull the claimed task in database
     * @param merchantIds: merchants id's of the operator 
     * @param pageNo : used to set a page, usually used in paging
	 * @param maxResult : max return result in the display
	 * @return List of Task object
     * */

	
	public Map<String, Object> getClaimedTaskByMerhants(List<Integer> merchantIds, Integer pageNo, Integer maxResult)
			 {
		
		/* Query String*/
		StringBuilder jpql = new StringBuilder();
		jpql.append("SELECT task ");
		jpql.append("FROM Task task ");
		jpql.append("WHERE task.merchantId ");
		jpql.append("IN :ids ");
		jpql.append("AND task.status = :status ");
		jpql.append("AND task.owner >= :owner ");
		jpql.append("ORDER BY task.updateTime DESC");
		  
		/*parameters container*/
		Map<String, Object> map = Maps.newHashMap();
		
		/* values for query */
		map.put("ids", merchantIds);
		map.put("status", TaskConstant.OPEN_STATUS);
		map.put("owner", 1);
		
		/*process sql string*/
		return this.findObjectListByJpl(jpql.toString(), map, pageNo, maxResult);
	
	}
	
	public Map<String, Object> getTaskOfOperator(List<Integer> merchantIds,
			List<Integer> roleIds,
			Integer pageNo,
			Integer maxResult,
			Integer operatorId,
			List<Integer> taskIds) {
		
				
				/* Query String*/
				StringBuilder jpql = new StringBuilder();
				
				if(CollectionUtils.isNotEmpty(merchantIds)){
					jpql.append("SELECT DISTINCT task ");
					jpql.append("FROM TaskSm task, RoleMenuPermission roleMenuP, StateRelationship stateRel ");
					jpql.append("WHERE (task.status IN (:status) ");
					jpql.append("AND (task.merchantId IN :ids OR task.merchantId IS NULL) ");
					jpql.append("AND roleMenuP.roleId IN (:roleIds) ");
					jpql.append("AND stateRel.fromState = task.state.stateId ");
					jpql.append("AND stateRel.state.menuId IN (roleMenuP.menuId) ");
					jpql.append("AND task.owner = :owner) ");
					  if(CollectionUtils.isNotEmpty(taskIds)){
						  jpql.append("OR (task.taskId IN (:taskIds)) ");
					  }
					  jpql.append("ORDER BY task.updateTime DESC");
				}else{
					jpql.append("SELECT DISTINCT task ");
					jpql.append("FROM TaskSm task ");
					jpql.append("WHERE (task.status IN (:status) ");
					jpql.append("AND task.owner = :owner) ");
					  if(CollectionUtils.isNotEmpty(taskIds)){
						  jpql.append("OR (task.taskId IN (:taskIds)) ");
					  }
					  jpql.append("ORDER BY task.updateTime DESC");
				}
							  
				/*parameters container*/
				Map<String, Object> map = Maps.newHashMap();
				
				/* values for query */
				
				if(CollectionUtils.isNotEmpty(merchantIds)){
					map.put("ids", merchantIds);
					map.put("roleIds", roleIds);
				}
				if(CollectionUtils.isNotEmpty(taskIds)){
					map.put("taskIds", taskIds);
				}
				List<String> validViewStatus = Lists.newLinkedList();
				validViewStatus.add(TaskConstant.PROCESSING_STATUS);
				validViewStatus.add(TaskConstant.OPEN_STATUS);
				map.put("status", validViewStatus);
				map.put("owner", operatorId);

				/*process sql string*/
				return  this.findObjectListByJpl(jpql.toString(), map, pageNo, maxResult);
				
			}

	public Page<Task> getAll(boolean isSytemAdmin,
							 TaskQueryTO taskQueryTO,
							 List<Integer> excludeStateIds) {

		Pageable pageable = new PageRequest(taskQueryTO.getPageNo() -1, taskQueryTO.getPageSize());

		JPAQuery query = new JPAQuery(entityManager);

		BooleanBuilder booleanBuilder = new BooleanBuilder();

		booleanBuilder.and(QTask.task.state.viewUrl.isNotNull());

		if(!isSytemAdmin){
			booleanBuilder.and(QStateRelationship.stateRelationship.state.type.notIn("SYS", "MER"));
		}

		if (!taskQueryTO.getMerchantIds().isEmpty()) {
			booleanBuilder.and(QTask.task.merchantId.in(taskQueryTO.getMerchantIds()));
		}
		
		if(!excludeStateIds.isEmpty()){
            booleanBuilder.and(QStateRelationship.stateRelationship.state.stateId.notIn(excludeStateIds));
        }


		if(StringUtils.isNotBlank(taskQueryTO.getStatus())){
			booleanBuilder.and(QTask.task.status.eq(taskQueryTO.getStatus()));
            booleanBuilder.and(QStateRelationship.stateRelationship.fromState.eq(QTask.task.state.stateId));
		}else{
			booleanBuilder.and(QTask.task.status.in(taskQueryTO.getDefStat()));
            if(taskQueryTO.getDefStat().size() ==1){
                booleanBuilder.and(QStateRelationship.stateRelationship.toState.eq(QTask.task.state.stateId));
            }else{
                booleanBuilder.and(QStateRelationship.stateRelationship.fromState.eq(QTask.task.state.stateId));
            }
		}

		if (StringUtils.isNotBlank(taskQueryTO.getOwnerName())) {
			booleanBuilder.and(QTask.task.ownerName.like("%"+taskQueryTO.getOwnerName()+"%"));
		}

		if (StringUtils.isNotBlank(taskQueryTO.getStateType())) {
			booleanBuilder.and(QTask.task.state.type.eq(taskQueryTO.getStateType()));
		}

		if (taskQueryTO.getStateId() != null) {
			booleanBuilder.and(QTask.task.state.stateId.eq(taskQueryTO.getStateId()));
		}

		andStartDate(taskQueryTO.getStartDate(), booleanBuilder);
		andEndDate(taskQueryTO.getEndDate(), booleanBuilder);

		query.from(QTask.task, QStateRelationship.stateRelationship).distinct()
				.where(booleanBuilder)
				.orderBy(QTask.task.createTime.desc());
		
		return QuerydslPageUtil.pagination(query, QTask.task, pageable);
	}

	private void andEndDate(String endDate, BooleanBuilder booleanBuilder) {
		if(StringUtils.isNotEmpty(endDate)) {
			if(endDate.trim().length() != DATE_WITH_TIME_LENGTH) {
				booleanBuilder.and(QTask.task.createTime.before(DateTools.getNextDate(DateTools.parseDate(endDate, "yyyy/MM/dd"))));
			}else {
				booleanBuilder.and(QTask.task.createTime.before(DateTools.parseDate(endDate, "yyyy/MM/dd hh:mm:ss")));
			}
		}
	}

	private void andStartDate(String startDate, BooleanBuilder booleanBuilder) {
		if(StringUtils.isNotEmpty(startDate)) {
			if(startDate.trim().length() != DATE_WITH_TIME_LENGTH) {
				booleanBuilder.and(QTask.task.createTime.after(DateTools.parseDate(startDate, "yyyy/MM/dd")));
			} else {
				booleanBuilder.and(QTask.task.createTime.after(DateTools.parseDate(startDate, "yyyy/MM/dd hh:mm:ss")));
			}
		}
	}
	
}
