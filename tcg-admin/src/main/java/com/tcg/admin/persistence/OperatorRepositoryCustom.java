package com.tcg.admin.persistence;


import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Maps;
import com.tcg.admin.to.NoneAdminInfo;
import com.tcg.admin.to.SortTo;

@Repository
public class OperatorRepositoryCustom extends BaseDAORepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(OperatorRepositoryCustom.class);

    public List<Object[]> findAll( List<Integer> roleIds, List<Integer> merchantIds, String operatorName, Integer roleId, Integer activeFlag, Integer merchantId, String sortBy, String sortOrder) {
    	LOGGER.debug("===========OperatorRepositoryCustom.findAll===============");

        Map<String, Object> keyMap = Maps.newHashMap();
        StringBuilder sb = new StringBuilder();

        sb.append(" select   ")
        .append(" oper.operator_id operatorId, ")  //[0]
        .append(" oper.operator_name operatorName, ")//[1] oper.operator_name oper.nickname
        .append(" oper.nickname nickname, ")     //[2]
        .append(" oper.active_flag activeFlag, ")  //[3]
        .append(" mercOper.MERCHANT_ID, ")      //[4]
//        .append(" CAST(merc.MERCHANT_NAME AS VARCHAR(32)) deptNames, ")      //[5] cast(t2.name as varchar) column_name
        .append(" r.role_id, ")         //[6]
        .append(" CAST(r.role_name AS VARCHAR(100)) roleNames ")        //[7]
        //.append(", CAST(cate.category_name AS VARCHAR(32)) categoryName ")        //[8]
        .append(" from us_operator oper")
        .append(" left join US_MERCHANT_OPERATOR mercOper on oper.operator_id = mercOper.operator_id ")
//        .append(" left join us_merchant merc on mercOper.merchant_id = merc.MERCHANT_ID ")
        .append(" left join us_role_operator roleOperator on oper.operator_id = roleOperator.operator_id")
        .append(" left join us_role r on roleOperator.role_id = r.role_id ")
        .append(" where 1=1 ");

        if(StringUtils.isNotBlank(operatorName)) {
            sb.append(" and UPPER(oper.operator_name) like :operatorName ");
            keyMap.put("operatorName","%"+operatorName.toUpperCase()+"%");
        }


        if(activeFlag != null) {
            sb.append(" AND oper.active_flag = :activeFlag ");
            keyMap.put("activeFlag", activeFlag);
        }

        if(roleId != null){
            sb.append(" and oper.operator_id in ( ");
            sb.append(" select distinct oper.operator_id operatorId  from us_operator oper ");
            sb.append(" left join us_role_operator roleOperator on oper.operator_id = roleOperator.operator_id ");
            sb.append(" where 1=1 ");
            sb.append(" and roleOperator.role_Id = :roleId ) ");
            keyMap.put("roleId", roleId);
        }else if (CollectionUtils.isNotEmpty(roleIds)) {
            sb.append(" and (oper.operator_id in ( ");
            sb.append(" select distinct oper.operator_id operatorId  from us_operator oper ");
            sb.append(" left join us_role_operator roleOperator on oper.operator_id = roleOperator.operator_id ");
            sb.append(" where 1=1 ");
            sb.append(" and roleOperator.role_Id in (:roleIds))  ");
            sb.append(" or roleOperator.role_Id is null  )");
            keyMap.put("roleIds", roleIds);
        }

        if(merchantId != null){
            sb.append(" and oper.operator_id in ( ");
            sb.append(" select distinct oper.operator_id operatorId  from us_operator oper ");
            sb.append(" left join US_MERCHANT_OPERATOR mercOper on oper.operator_id = mercOper.operator_id ");
            sb.append(" where 1=1 ");
            sb.append(" and mercOper.MERCHANT_ID = :merchantId )");
            keyMap.put("merchantId", merchantId);
        } else if (CollectionUtils.isNotEmpty(merchantIds)) {
            sb.append(" and oper.operator_id in ( ");
            sb.append(" select distinct oper.operator_id operatorId  from us_operator oper ");
            sb.append(" left join US_MERCHANT_OPERATOR mercOper on oper.operator_id = mercOper.operator_id ");
            sb.append(" where 1=1 ");
            sb.append(" and mercOper.MERCHANT_ID in (:merchantIds) )");
            keyMap.put("merchantIds", merchantIds);
        }

        sb.append(" order by " + sortBy + " " + sortOrder);

        return 	this.getListForPage4SQL(sb.toString(),keyMap, null, null);
    }

    public List<Object[]> findAll2(NoneAdminInfo adminInfo, String operatorName, Integer roleId, Integer activeFlag, Integer merchantId, String baseMerchantCode, SortTo sortTo) {
    	LOGGER.debug("===========OperatorRepositoryCustom.findAll2===============");

        Map<String, Object> keyMap = Maps.newHashMap();
        StringBuilder sb = new StringBuilder();

        boolean isAdmin = adminInfo.isAdmin();
        List<Integer> roleIds = adminInfo.getRoleIds();
        
        sb.append(" select operatorId,  operatorName, nickname, activeFlag, baseMerchantCode, ")
                .append(" (select sys.stragg(merchant_name || ',') merchants  from tcg_admin.admin_merchant mercs, tcg_admin.us_merchant_operator mercOpers WHERE mercs.merchant_id = mercOpers.merchant_id AND mercOpers.operator_id = operatorId" );

        if(isAdmin){
            sb.append("  and mercs.merchant_id in (select merchant_id from tcg_admin.admin_merchant )) merchants, ");
        }else{
            sb.append(" and mercs.merchant_id in (select merchant_id from tcg_admin.us_merchant_operator where operator_id = :operatorId)) merchants, ");
        }

        sb.append(" (select sys.stragg(mercs.merchant_id || ',') merchantId from tcg_admin.admin_merchant mercs, tcg_admin.us_merchant_operator mercOpers WHERE mercs.merchant_id = mercOpers.merchant_id AND mercOpers.operator_id = operatorId" );
        if(isAdmin){
            sb.append("  and mercs.merchant_id in (select merchant_id from tcg_admin.admin_merchant)) merchantId, ");
        }else{
            sb.append(" and mercs.merchant_id in (select merchant_id from tcg_admin.us_merchant_operator where operator_id = :operatorId)) merchantId, ");
        }

          sb.append(" (select listagg(role_name,', ') within group(order by role_name) roles  from tcg_admin.us_role roles, tcg_admin.us_role_operator roleOpers WHERE roles.role_id = roleOpers.role_id AND roleOpers.operator_id = operatorId) roles, ")
          .append(" (select listagg(roleOpers.role_id,', ') within group(order by roleOpers.role_id) roleIds  from tcg_admin.us_role roles, tcg_admin.us_role_operator roleOpers WHERE roles.role_id = roleOpers.role_id AND roleOpers.operator_id = operatorId) roleIds")
          .append(" FROM ")
          .append(" ((select distinct oper.operator_id operatorId,  oper.operator_name operatorName, oper.nickname nickname, oper.active_flag activeFlag, oper.base_merchant_code baseMerchantCode ")
          .append(" FROM tcg_admin.us_operator oper, tcg_admin.US_MERCHANT_OPERATOR mercOper  ");
        //if roles has no category permissions
        if(CollectionUtils.isEmpty(roleIds) && !isAdmin) {
            sb.append(" , tcg_admin.us_role_operator roleOpers ");
            sb.append(" WHERE 1=1 ");
            sb.append("  and oper.operator_id = roleOpers.operator_id");
            sb.append(" and oper.operator_id = mercOper.operator_id AND merchant_id IN (SELECT merchant_id FROM tcg_admin.us_merchant_operator WHERE operator_id = :operatorId) ");
        } else {
            //admin role
            sb.append(" WHERE 1=1 ");
            sb.append(" and oper.operator_id = mercOper.operator_id");
            if(isAdmin){
                sb.append("  AND merchant_id  IN (SELECT merchant_id FROM tcg_admin.admin_merchant) ");
            }else{
                sb.append(" AND merchant_id IN (SELECT merchant_id FROM tcg_admin.us_merchant_operator WHERE operator_id = :operatorId) ");
            }

        }

        //query condition of operatorName
        if(StringUtils.isNotBlank(operatorName)) {
            sb.append(" and UPPER(oper.operator_name) like :operatorName ");
            keyMap.put("operatorName","%"+operatorName.toUpperCase()+"%");
        }
        //query condition of merchant
        if(merchantId != null) {
            sb.append(" AND merchant_id = :merchantId ");
            keyMap.put("merchantId", merchantId);
        }
        //query condition of roles
        if(roleId != null){
            sb.append(" AND :roleId IN (SELECT role_id FROM tcg_admin.us_role_operator usRoles WHERE usRoles.operator_id = oper.operator_id) ");
            keyMap.put("roleId", roleId);
        }
        //query condition of status
        if(activeFlag != null) {
            sb.append(" AND active_flag = :activeFlag ");
            keyMap.put("activeFlag", activeFlag);
        }

        if(baseMerchantCode != null) {
            sb.append(" AND base_merchant_code = :baseMerchantCode ");
            keyMap.put("baseMerchantCode", baseMerchantCode);
        }

        sb.append(" ) ");
        //only append this part if query is not from admin role
        if(!isAdmin) {
            sb.append(" MINUS ")
                    .append(" (select distinct oper.operator_id operatorId,  oper.operator_name operatorName, oper.nickname nickname, oper.active_flag activeFlag, oper.base_merchant_code baseMerchantCode ")
                    .append(" FROM tcg_admin.us_operator oper, tcg_admin.US_MERCHANT_OPERATOR mercOper, tcg_admin.us_role_operator roleOp ")
                    .append(" WHERE oper.operator_id = mercOper.operator_id  ")
                    .append(" AND  oper.OPERATOR_ID = roleOp.OPERATOR_ID");
            //if roles has no category permissions
              if(CollectionUtils.isNotEmpty(roleIds)) {
                  sb.append(" AND roleOp.role_id not in (:roleIds)");
                  keyMap.put("roleIds", roleIds);
              }
            sb.append(") ");

        }
        sb.append(") o_list")
        .append(" order by " + sortTo.getSortBy() + " " + sortTo.getSortOrder());

        if(!isAdmin) {
            keyMap.put("operatorId", adminInfo.getOperatorId());
        }

        return 	this.getListForPage4SQL(sb.toString(),keyMap, null, null);
    }

}
