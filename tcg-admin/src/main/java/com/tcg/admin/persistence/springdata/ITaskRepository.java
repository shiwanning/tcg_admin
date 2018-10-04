package com.tcg.admin.persistence.springdata;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.tcg.admin.model.Task;

public interface ITaskRepository extends JpaRepository<Task, Integer> {
	
	@Query("select task from Task task where task.merchantId in (?1) and task.owner = 0 and task.status = ?2")
	List<Task> findUnclaimedTaskByIds(List<Integer> merchantIds, String status);
	
	@Query("select task from Task task where task.merchantId in (?1) and task.owner >= 1 and task.status = ?2")
	List<Task> findClaimedTaskByIds(List<Integer> merchantIds, String status);

    Task findBySubSystemTaskAndStateId(String subSysTaskId, Integer stateId);

	@Query("select task from Task task where task.subSystemTask = ?1 and task.stateId = ?2 and task.status <> ?3")
	Task findBySubSystemTaskAndStateId(String subSystemTask, Integer stateId, String status);

	@Query("select task from Task task where task.taskId = ?1 ")
	Task findTask(Integer taskId);

	@Modifying
	@Query(value = "update WF_TASK wt set status = ?1 "
	        + " where CREATE_TIME < ?2 and status in (?4) "
	        + " AND EXISTS ( SELECT * FROM WF_STATE ws WHERE ws.STATE_ID = wt.STATE_ID AND ws.type in (?3) )", 
	        nativeQuery = true)
    int updateStatusByOpenTimeBeforeAndStateTypeAndStatusIn(String status, Date endDate, List<String> stateTypes, List<String> statuses);

	@Query("SELECT task.stateId, count(task.stateId) "
			+ "FROM TaskSm task "
			+ "WHERE task.status IN ('O', 'P') "
			+ "AND task.owner = 0 "
			+ "GROUP BY task.stateId ")
	List<Object[]> findAvailableTaskCountInfo();

	@Query("SELECT task.stateId, count(distinct task.taskId) "
			+ " FROM TaskSm task, RoleMenuPermission roleMenuP, StateRelationship stateRel "
			+ " WHERE "
			+ " stateRel.fromState = task.state.stateId "
			+ " AND stateRel.state.menuId IN roleMenuP.menuId  "
			+ " AND task.status IN ('O', 'P') "
			+ " AND (task.merchantId IN (?1) OR task.merchantId IS NULL)"
			+ " AND roleMenuP.roleId IN (?2)"
			+ " AND task.owner = 0 "
			+ "GROUP BY task.stateId ")
	List<Object[]> findAvailableTaskCountInfoByMerchant(List<Integer> merchantIds, List<Integer> roleIds);

}