package com.tcg.admin.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tcg.admin.common.constants.IErrorCode;
import com.tcg.admin.common.exception.AdminServiceBaseException;
import com.tcg.admin.model.State;
import com.tcg.admin.model.StateRelationship;
import com.tcg.admin.persistence.springdata.IOperatorRepository;
import com.tcg.admin.persistence.springdata.IStateRepository;
import com.tcg.admin.service.StateService;

@Service
@Transactional
public class StateServiceImpl implements StateService {

    @Autowired
    private IOperatorRepository operatorRepository;

    @Autowired
    private IStateRepository stateRepository;

    @Override
    public List<State> findAll() throws AdminServiceBaseException {
        return stateRepository.findAll();
    }

    @Override
    public State createState(State state) throws AdminServiceBaseException {
        return stateRepository.saveAndFlush(state);
    }

    @Override
    public void updateState(State state) throws AdminServiceBaseException {
        State insertModel = stateRepository.findOne(state.getStateId());
        insertModel.setStateName(state.getStateName());
        insertModel.setType(state.getType());
        insertModel.setDescription(state.getDescription());
        insertModel.setParentId(state.getParentId());
        insertModel.setUpdateTime(new Date());
        stateRepository.saveAndFlush(insertModel);
    }

	@Override
	public State getState(Integer stateId) throws AdminServiceBaseException {
		State state = stateRepository.findOne(stateId);
		if (state == null) {
			throw new AdminServiceBaseException(IErrorCode.UNKNOWN_ERROR, "State Not Found");
		}
		return state;
	}

	@Override
	public List<StateRelationship> getStateRelationship(Integer stateId) {
		return stateRepository.getRelStateById(stateId);
		
	}

    @Override
    public List<StateRelationship> getStateRelationship(Integer fromStateId, Integer toStateId) {
        return stateRepository.getRelStateById(fromStateId, toStateId);
    }

}
