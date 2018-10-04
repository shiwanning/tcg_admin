package com.tcg.admin.persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class MerchantRepositoryCustom extends BaseDAORepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(OperatorRepositoryCustom.class);

    public  List<Object[]> findPNCMerchants(Integer operatorId) {
        Map<String, Object> keyMap = new HashMap<String, Object>();
        StringBuilder sb = new StringBuilder();

        sb.append(" select   ")
                .append(" merca.merchant_id, ")
                .append(" CAST(merca.merchant_code AS VARCHAR(100)) merchantCode,")        //[1]
                .append(" CAST(merca.merchant_name AS VARCHAR(100)) merchantName,")        //[1]
                .append(" merca.parent_id ")
                .append(" from ")
                .append(" admin_merchant merca , ")
                .append(" admin_merchant_operator mercOp ")
                .append(" where 1=1 ")
                .append(" and merca.merchant_Id = mercOp.merchant_Id ")
                .append(" and mercOp.operator_Id = :operatorId ");

        keyMap.put("operatorId", operatorId);

        return 	this.findBySql(sb.toString(),keyMap);
    }

}
