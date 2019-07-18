package com.tcg.admin.persistence.springdata;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tcg.admin.model.OperatorAuth;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface IOperatorAuthRepository extends JpaRepository<OperatorAuth, Long>{

    OperatorAuth findByOperatorId(Integer operatorId);

    @Modifying()
    @Query("delete from OperatorAuth mercOp where mercOp.operatorId = ?1")
    void deleteByOperatorId(Integer operatorId);
    
}
