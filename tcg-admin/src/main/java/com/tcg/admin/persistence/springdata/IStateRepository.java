package com.tcg.admin.persistence.springdata;

import com.tcg.admin.model.State;
import com.tcg.admin.model.StateRelationship;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IStateRepository extends JpaRepository<State, Integer> {

	List<State> findByParentId(Integer id);
	
	@Query("select count(*) from State s where s.stateId=?1 and s.parentId=?2")
	int isParentOf(Integer stateIdTest, Integer stateIdParent);
	
	@Query("select sr from StateRelationship sr where sr.fromState = ?1")
    List<StateRelationship> getRelStateById(Integer stateId);

	@Query("select sr from StateRelationship sr where sr.fromState = ?1 and sr.toState = ?2")
	List<StateRelationship> getRelStateById(Integer fromStateId, Integer toStateId);
}