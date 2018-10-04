package com.tcg.admin.persistence.springdata;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface IUserRepositoryBase<T> extends JpaRepository<T, Integer>, JpaSpecificationExecutor<T>, QueryDslPredicateExecutor<T> {

}
