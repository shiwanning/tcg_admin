package com.tcg.admin.persistence;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.tcg.admin.common.exception.AdminServiceBaseException;
import com.tcg.admin.model.State;

@Repository
public class StateRepositoryCustom extends BaseDAORepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(StateRepositoryCustom.class);

    @PersistenceContext(unitName = "persistenceUnit")
    private EntityManager entityManager;

	public List<State> getStateListByType(String type)	throws AdminServiceBaseException {
		
		/* Query String*/
		StringBuilder sb = new StringBuilder();
		sb.append("select state from State state ");
		if(StringUtils.isNotBlank(type)){
			sb.append(" and state.type =?1 ");
		}

		TypedQuery<State> q = entityManager.createQuery(sb.toString(), State.class);
		if(StringUtils.isNotBlank(type)){
			q.setParameter(1, type);
		}

		return q.getResultList();
	
	}
}
