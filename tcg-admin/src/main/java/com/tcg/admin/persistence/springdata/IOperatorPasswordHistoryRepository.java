package com.tcg.admin.persistence.springdata;


import com.tcg.admin.model.OperatorPasswordHistory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IOperatorPasswordHistoryRepository extends JpaRepository<OperatorPasswordHistory, Integer> {


    @Query("select a from OperatorPasswordHistory a where 1=1 and a.operatorName = ?1 and a.isReset = 1 order by a.updateTime")
    List<OperatorPasswordHistory> getOperatorPasswordHistory(String operatorName);
}
