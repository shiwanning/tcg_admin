package com.tcg.admin.persistence.springdata;


import com.tcg.admin.model.OperatorPasswordHistory;

import org.springframework.data.jpa.repository.JpaRepository;

public interface IOperatorPasswordHistoryRepository extends JpaRepository<OperatorPasswordHistory, Integer> {

}
