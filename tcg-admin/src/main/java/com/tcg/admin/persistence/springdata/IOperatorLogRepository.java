package com.tcg.admin.persistence.springdata;

import com.tcg.admin.model.OperatorLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * com.tcg.admin.persistence.springdata
 *
 * @author lyndon.j
 * @version 1.0
 * @date 2019/6/27 16:00
 */
public interface IOperatorLogRepository extends JpaRepository<OperatorLog, Long>, JpaSpecificationExecutor<OperatorLog> {



}
