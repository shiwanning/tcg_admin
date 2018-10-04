package com.tcg.admin.service;


import com.tcg.admin.common.exception.AdminServiceBaseException;
import com.tcg.admin.model.State;
import com.tcg.admin.model.StateRelationship;

import java.util.List;

public interface StateService {

	List<State> findAll() throws AdminServiceBaseException;
	/**
	 * <pre>
	 * Create a state
	 * 
	 * 建立state
	 * </pre>
	 * 
	 * @param state
	 *            stateId,stateName,type,description,menuId,parentId
	 * @return State
	 * @throws AdminServiceBaseException
	 */
	State createState(State state) throws AdminServiceBaseException;

	/**
	 * <pre>
	 * Update a state
	 * 
	 * 修改state
	 * </pre>
	 * 
	 * @param state
	 *            stateId,stateName,type,description,menuId,parentId
	 * @throws AdminServiceBaseException
	 */
    void updateState(State state) throws AdminServiceBaseException;
    
    State getState(Integer stateId) throws AdminServiceBaseException;

	List<StateRelationship> getStateRelationship(Integer stateId) throws AdminServiceBaseException;

	List<StateRelationship> getStateRelationship(Integer fromStateId, Integer toStateId) throws AdminServiceBaseException;

}
