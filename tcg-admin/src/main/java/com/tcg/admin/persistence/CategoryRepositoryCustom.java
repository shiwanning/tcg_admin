package com.tcg.admin.persistence;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Maps;

@Repository
public class CategoryRepositoryCustom extends BaseDAORepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(OperatorRepositoryCustom.class);

    public List<Object[]> findAll(Integer categoryId, Integer activeFlag) {
    	LOGGER.debug("===========CategoryRepositoryCustom.findAll===============");

        Map<String, Object> keyMap = Maps.newHashMap();
        StringBuilder sb = new StringBuilder();

        sb.append(" select   ")
                .append(" cate.category_id, ")  //[0]
                .append(" CAST(cate.category_name AS VARCHAR(100)) categoryName,")        //[1]
                .append(" r.role_id, ")     //[2]
                .append(" CAST(r.role_name AS VARCHAR(100)) roleName,")        //[3]
                .append(" r.active_flag ")      //[4]
                .append(" from us_category cate ")
                .append(" left join us_category_role cateRole on cate.category_id = cateRole.category_id ")
                .append(" left join us_role r on cateRole.role_id = r.role_id  ")
                .append(" where 1=1 ");

        if(categoryId != null){
            sb.append(" and cate.category_Id = :categoryId ");
            keyMap.put("categoryId", categoryId);
        }
        if(activeFlag != null){
            sb.append(" and r.active_flag = :activeFlag ");
            keyMap.put("activeFlag", activeFlag);
        }
        return 	this.findBySql(sb.toString(),keyMap);
    }

}
