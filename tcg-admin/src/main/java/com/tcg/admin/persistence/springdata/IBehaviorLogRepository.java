package com.tcg.admin.persistence.springdata;

import com.tcg.admin.model.BehaviorLog;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IBehaviorLogRepository extends JpaRepository<BehaviorLog, Long> {


    @Query(value = "select a.* from BEHAVIOR_LOG a " +
            " where 1=1 " +
            " and a.OPERATOR_NAME = ?1 " +
            " and a.START_PROC_DATE > TRUNC(SYSDATE-1) " +
            " and a.RESOURCE_TYPE = 4 " +
            " order by a.START_PROC_DATE desc "
            ,nativeQuery = true)
    List<BehaviorLog> getCurrentLoginBehaviorLog(String operatorName);
}
