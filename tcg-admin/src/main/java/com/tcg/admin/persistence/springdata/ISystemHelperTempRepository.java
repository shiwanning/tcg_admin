package com.tcg.admin.persistence.springdata;

import com.tcg.admin.model.SystemHelperTemp;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by chris.h on 12/13/2017.
 */
public interface ISystemHelperTempRepository extends JpaRepository<SystemHelperTemp, Integer> {

	 @Query("select a from SystemHelperTemp a where a.taskId =?1")
	 SystemHelperTemp findHelperTempByTaskId(Integer taskId);
	 
	 @Query("select taskId from SystemHelperTemp a where a.requesterId =?1 and status in ?2 ")
	 List<Integer> findRejectOrAcceptedHelper(Integer operatorId, List<Integer> status);

}
