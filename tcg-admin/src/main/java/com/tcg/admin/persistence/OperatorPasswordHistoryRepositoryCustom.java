package com.tcg.admin.persistence;


import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.tcg.admin.model.OperatorPasswordHistory;

@Repository
public class OperatorPasswordHistoryRepositoryCustom {

    @PersistenceContext(unitName = "persistenceUnit")
    private EntityManager entityManager;

    public OperatorPasswordHistory findLastPasswordHistory(String operatorName) {
        List<OperatorPasswordHistory> dataList = findLastPasswordHistory(operatorName, 1);
        return dataList.isEmpty() ? null : dataList.get(0);
    }

    @SuppressWarnings("unchecked")
    public List<OperatorPasswordHistory> findLastPasswordHistory(String operatorName, int size) {
        Query q = entityManager.createQuery("select o from OperatorPasswordHistory o where o.operatorName = :operatorName order by create_time desc");
        q.setMaxResults(size);
        q.setParameter("operatorName", operatorName);
        return q.getResultList();
    }

}
