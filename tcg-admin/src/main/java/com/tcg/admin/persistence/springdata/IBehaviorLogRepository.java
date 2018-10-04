package com.tcg.admin.persistence.springdata;

import com.tcg.admin.model.BehaviorLog;

import org.springframework.data.jpa.repository.JpaRepository;

public interface IBehaviorLogRepository extends JpaRepository<BehaviorLog, Long> {

}
