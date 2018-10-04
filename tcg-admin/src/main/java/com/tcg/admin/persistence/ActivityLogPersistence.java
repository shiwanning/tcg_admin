package com.tcg.admin.persistence;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.mysema.query.BooleanBuilder;
import com.mysema.query.jpa.impl.JPADeleteClause;
import com.mysema.query.jpa.impl.JPAQuery;
import com.tcg.admin.model.BehaviorLog;
import com.tcg.admin.model.QBehaviorLog;
import com.tcg.admin.utils.QuerydslPageUtil;

@Repository
public class ActivityLogPersistence {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActivityLogPersistence.class);

    @PersistenceContext(unitName = "persistenceUnit")
    private EntityManager entityManager;

    public Page<BehaviorLog> findActivityLogList(String username, Integer actionType, Pageable pageable) {

        EntityManager em = entityManager;

        JPAQuery query = new JPAQuery(em);

        QBehaviorLog behaviorLog = QBehaviorLog.behaviorLog;

        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(behaviorLog.operatorName.eq(username));

        if(actionType != null) {
            if(actionType == 4) {
                List<Integer> actionTypeList = new ArrayList();
                actionTypeList.add(4);
                actionTypeList.add(41);
                booleanBuilder.and(behaviorLog.resourceType.in(actionTypeList));
            }else{
                booleanBuilder.and(behaviorLog.resourceType.eq(actionType));
            }
        }

        query.from(behaviorLog)
                .where(booleanBuilder)
                .orderBy(behaviorLog.startProcessDate.desc());

        return QuerydslPageUtil.pagination(query, behaviorLog, pageable);
    }

    public void cleanBehaviorLogs() {

        EntityManager em = entityManager;

        QBehaviorLog behaviorLog = QBehaviorLog.behaviorLog;

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, - 32);
        LOGGER.debug("delete behaviorLog data 30 days ago {}", cal.getTime());
        try {
            new JPADeleteClause(em, behaviorLog).where(behaviorLog.startProcessDate.lt(cal.getTime())).execute();
        }catch (Exception e) {
        	LOGGER.error("cleanBehaviorLogs error", e);
        }

    }

}
