package com.tcg.admin.persistence.springdata;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tcg.admin.model.OperatorAuth;

public interface IOperatorAuthRepository extends JpaRepository<OperatorAuth, Long>{

    OperatorAuth findByOperatorId(Integer operatorId);
    
}
