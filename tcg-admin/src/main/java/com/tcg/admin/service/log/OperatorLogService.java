package com.tcg.admin.service.log;

import com.tcg.admin.model.OperatorLog;
import com.tcg.admin.to.condition.OperatorLogCondition;
import org.springframework.data.domain.Page;

/**
 *
 * @author lyndon.J
 */
public interface OperatorLogService {

    Page<OperatorLog> getOperatorLogList(OperatorLogCondition condition, int pageNo, int pageSize);

    OperatorLog insert(OperatorLog operatorLog);

    OperatorLog insert(String merchantCode, String operatorName, String editedOperatorName, String function, String oldValue, String newValue, String requestParam);

    void update(Long id,String newValue);

}
