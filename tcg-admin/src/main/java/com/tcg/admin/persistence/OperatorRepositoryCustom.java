package com.tcg.admin.persistence;


import java.math.BigDecimal;
import java.util.HashMap;
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
        String str = "test";
        sb.append(" select   ")
                .append(" oper.operator_id operatorId, ")  //[0]
                .append(" oper.operator_name operatorName, ")//[1] oper.operator_name oper.nickname
                .append(" oper.nickname nickname, ")     //[2]
                .append(" oper.active_flag activeFlag, ")  //[3]
                .append(" mercOper.MERCHANT_ID, ")      //[4]
                .append(" r.role_id, ")         //[6]
                .append(" CAST(r.role_name AS VARCHAR(100)) roleNames ")        //[7]
                .append(" from us_operator oper")
                .append(" left join US_MERCHANT_OPERATOR mercOper on oper.operator_id = mercOper.operator_id ")
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

    public Map<String, Object> findAll2(NoneAdminInfo adminInfo, String operatorName, Integer roleId, Integer activeFlag, Integer merchantId, String baseMerchantCode, String lastLoginIP, SortTo sortTo, Integer start, Integer end) {
        LOGGER.debug("===========OperatorRepositoryCustom.findAll2===============");

        Map<String, Object> keyMap = Maps.newHashMap();
        StringBuilder sb = new StringBuilder();

        boolean isAdmin = adminInfo.isAdmin();
        List<Integer> roleIds = adminInfo.getRoleIds();
        boolean notEmpty = StringUtils.isNotEmpty(lastLoginIP);
        if(isAdmin) {
            sb.append(initAdminJpql(notEmpty));
        } else {
            sb.append(initNotAdminJpql(notEmpty));
            keyMap.put("operatorId", adminInfo.getOperatorId());
        }


        //if roles has no category permissions
        if(CollectionUtils.isEmpty(roleIds) && !isAdmin) {
            if(notEmpty){
                sb.append(" ,( select " +
                        " lg.OPERATOR_NAME, lg.IP_ADDRESS, max(lg.END_PROC_DATE) END_PROC_DATE " +
                        " from BEHAVIOR_LOG lg where lg.RESOURCE_TYPE = 4 and lg.IP_ADDRESS = :ipAddress group by  lg.OPERATOR_NAME, lg.IP_ADDRESS) bev");
                keyMap.put("ipAddress", lastLoginIP);
            }
            sb.append(" , tcg_admin.us_role_operator roleOpers ");
            sb.append(" WHERE 1=1 ");
            sb.append("  and oper.operator_id = roleOpers.operator_id");
            sb.append(" and proOper.OPERATOR_ID = oper.operator_id ");
            sb.append(" and oper.operator_id = mercOper.operator_id");

            if(notEmpty){
                sb.append(" AND bev.OPERATOR_NAME = oper.OPERATOR_NAME ");
            }
            conditionOperatorName(operatorName, sb, keyMap);
            conditionMerchantId(merchantId, sb, keyMap);
            conditionRoleId(roleId, sb, keyMap);
            conditionActiveFlag(activeFlag, sb, keyMap);
            conditionBaseMerchantCode(baseMerchantCode, sb, keyMap);
//            conditionBaseLastLoginIP(lastLoginIP, sb, keyMap);
            sb.append("  and oper.operator_id not in (select distinct a.operator_id from  tcg_admin.us_merchant_operator a " +
                    " where not exists(select 1 from tcg_admin.us_merchant_operator b where a.merchant_id = b.merchant_id and b.operator_id = :operatorId ))) ");
        } else {
            //admin role
            if(notEmpty){
                sb.append(" ,( select " +
                        " lg.OPERATOR_NAME, lg.IP_ADDRESS, max(lg.END_PROC_DATE) END_PROC_DATE " +
                        " from BEHAVIOR_LOG lg where lg.RESOURCE_TYPE = 4 and lg.IP_ADDRESS = :ipAddress group by  lg.OPERATOR_NAME, lg.IP_ADDRESS) bev");
                keyMap.put("ipAddress", lastLoginIP);
            }
            if(!isAdmin){
                sb.append(" , tcg_admin.us_role_operator roleOp");
            }
            sb.append(" WHERE 1=1 ");
            if(notEmpty){
                sb.append(" AND bev.OPERATOR_NAME = oper.OPERATOR_NAME ");
            }
            if(!isAdmin){
                sb.append(" AND oper.OPERATOR_ID = roleOp.OPERATOR_ID ");
            }
            sb.append(" and oper.operator_id = mercOper.operator_id");
            sb.append(" and proOper.OPERATOR_ID = oper.operator_id ");
            conditionOperatorName(operatorName, sb, keyMap);
            conditionMerchantId(merchantId, sb, keyMap);
            conditionRoleId(roleId, sb, keyMap);
            conditionActiveFlag(activeFlag, sb, keyMap);
            conditionBaseMerchantCode(baseMerchantCode, sb, keyMap);
//            conditionBaseLastLoginIP(lastLoginIP, sb, keyMap);
            if(isAdmin){
                sb.append("  AND merchant_id  IN (SELECT merchant_id FROM tcg_admin.admin_merchant))");
            }else{
                if(CollectionUtils.isNotEmpty(roleIds)) {
                    sb.append(" AND roleOp.role_id in (:roleIds)");
                    keyMap.put("roleIds", roleIds);
                }
                sb.append("  and oper.operator_id not in (select distinct a.operator_id from  tcg_admin.us_merchant_operator a " +
                        " where not exists(select 1 from tcg_admin.us_merchant_operator b where a.merchant_id = b.merchant_id and b.operator_id = :operatorId ))) ");
            }

        }

        sb.append(" ty left join US_OPERATOR_AUTH auth on ty.operatorId = auth.OPERATOR_ID ");


        sb.append(" ) ");
        //if roles has no category permissions
        if(CollectionUtils.isEmpty(roleIds) && !isAdmin) {
            sb.append(" MINUS ")
                    .append(" (select ty.operatorId, ty.operatorName, ty.nickname, ty.activeFlag, ty.baseMerchantCode, ty.lastLoginTime, ty.lastLoginIP, auth.STATUS googleAuthStatus  from ")
                    .append(" (select distinct oper.operator_id operatorId,  oper.operator_name operatorName, oper.nickname nickname, oper.active_flag activeFlag, oper.base_merchant_code baseMerchantCode, proOper.LAST_LOGIN_TIME lastLoginTime, proOper.LAST_LOGIN_IP lastLoginIP ")
                    .append(" FROM tcg_admin.us_operator oper, tcg_admin.US_MERCHANT_OPERATOR mercOper, tcg_admin.us_role_operator roleOp, tcg_admin.US_OPERATOR_PROFILE proOper ")
                    .append(" WHERE oper.operator_id = mercOper.operator_id  ")
                    .append(" and proOper.OPERATOR_ID = oper.operator_id ")
                    .append(" AND  oper.OPERATOR_ID = roleOp.OPERATOR_ID ");
            sb.append(") ");
            sb.append("ty left join US_OPERATOR_AUTH auth on ty.operatorId = auth.OPERATOR_ID )");
        }
        sb.append(") o_list")
                .append(" order by " + sortTo.getSortBy() + " " + sortTo.getSortOrder() + "  nulls last ");



        //获取total
        List<BigDecimal> dataTotal = this.getListForPage4SQL("select count(*) from ( " + sb.toString() + " )", keyMap, null, null);

        String pageSql = "select * from (select ax.*, ROWNUM AS rowno from (" +sb.toString() + ") ax where ROWNUM <= :end) where rowno > :start";
        //获取List
        keyMap.put("start", start);
        keyMap.put("end", end);
        List<Object[]> dataList = this.getListForPage4SQL(pageSql, keyMap, null, null);

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("list", dataList);
        resultMap.put("total", dataTotal.get(0));
        return 	resultMap;
    }

    private void conditionBaseMerchantCode(String baseMerchantCode, StringBuilder sb, Map<String, Object> keyMap) {
        if(baseMerchantCode != null) {
            sb.append(" AND base_merchant_code = :baseMerchantCode ");
            keyMap.put("baseMerchantCode", baseMerchantCode);
        }
    }

    private void conditionBaseLastLoginIP(String lastLoginIP, StringBuilder sb, Map<String, Object> keyMap){
        if(lastLoginIP != null) {
            sb.append(" AND LAST_LOGIN_IP = :lastLoginIP ");
            keyMap.put("lastLoginIP", lastLoginIP);
        }
    }

    private void conditionActiveFlag(Integer activeFlag, StringBuilder sb, Map<String, Object> keyMap) {
        if(activeFlag != null) {
            sb.append(" AND active_flag = :activeFlag ");
            keyMap.put("activeFlag", activeFlag);
        }
    }

    private void conditionRoleId(Integer roleId, StringBuilder sb, Map<String, Object> keyMap) {
        if(roleId != null){
            sb.append(" AND :roleId IN (SELECT role_id FROM tcg_admin.us_role_operator usRoles WHERE usRoles.operator_id = oper.operator_id) ");
            keyMap.put("roleId", roleId);
        }
    }

    private void conditionMerchantId(Integer merchantId, StringBuilder sb, Map<String, Object> keyMap) {
        if(merchantId != null) {
            sb.append(" AND merchant_id = :merchantId ");
            keyMap.put("merchantId", merchantId);
        }
    }

    private void conditionOperatorName(String operatorName, StringBuilder sb, Map<String, Object> keyMap) {
        if(StringUtils.isNotBlank(operatorName)) {
            sb.append(" and UPPER(oper.operator_name) like :operatorName ");
            keyMap.put("operatorName","%"+operatorName.toUpperCase()+"%");
        }
    }

    private Object initNotAdminJpql(boolean notEmpty) {
        StringBuilder sb = new StringBuilder();
        sb.append(" select operatorId,  operatorName, nickname, activeFlag, baseMerchantCode, lastLoginTime, lastLoginIP, googleAuthStatus, ")
                .append(" (select sys.stragg(merchant_name || ',') merchants  from tcg_admin.admin_merchant mercs, tcg_admin.us_merchant_operator mercOpers WHERE mercs.merchant_id = mercOpers.merchant_id AND mercOpers.operator_id = operatorId" )
                .append(" and mercs.merchant_id in (select merchant_id from tcg_admin.us_merchant_operator where operator_id = :operatorId)) merchants, ")
                .append(" (select sys.stragg(mercs.merchant_id || ',') merchantId from tcg_admin.admin_merchant mercs, tcg_admin.us_merchant_operator mercOpers WHERE mercs.merchant_id = mercOpers.merchant_id AND mercOpers.operator_id = operatorId" )
                .append(" and mercs.merchant_id in (select merchant_id from tcg_admin.us_merchant_operator where operator_id = :operatorId)) merchantId, ")
                .append(" (select listagg(role_name,', ') within group(order by role_name) roles  from tcg_admin.us_role roles, tcg_admin.us_role_operator roleOpers WHERE roles.role_id = roleOpers.role_id AND roleOpers.operator_id = operatorId) roles, ")
                .append(" (select listagg(roleOpers.role_id,', ') within group(order by roleOpers.role_id) roleIds  from tcg_admin.us_role roles, tcg_admin.us_role_operator roleOpers WHERE roles.role_id = roleOpers.role_id AND roleOpers.operator_id = operatorId) roleIds")
                .append(" FROM ")
                .append(" ((select ty.operatorId, ty.operatorName, ty.nickname, ty.activeFlag, ty.baseMerchantCode, ty.lastLoginTime, ty.lastLoginIP, auth.STATUS googleAuthStatus from" +
                        "(select distinct oper.operator_id operatorId,  oper.operator_name operatorName, oper.nickname nickname, oper.active_flag activeFlag, oper.base_merchant_code baseMerchantCode ");
        if(notEmpty){
            sb.append("  ,bev.END_PROC_DATE lastLoginTime, bev.IP_ADDRESS lastLoginIP ");
        }else {
            sb.append(" ,proOper.LAST_LOGIN_TIME lastLoginTime, proOper.LAST_LOGIN_IP lastLoginIP ");
        }
        sb.append(" FROM tcg_admin.us_operator oper, tcg_admin.US_MERCHANT_OPERATOR mercOper, tcg_admin.US_OPERATOR_PROFILE proOper  ");
        return sb;
    }

    private StringBuilder initAdminJpql(boolean notEmpty) {
        StringBuilder sb = new StringBuilder();
        sb.append(" select operatorId,  operatorName, nickname, activeFlag, baseMerchantCode, lastLoginTime, lastLoginIP, googleAuthStatus, ")
                .append(" (select sys.stragg(merchant_name || ',') merchants  from tcg_admin.admin_merchant mercs, tcg_admin.us_merchant_operator mercOpers WHERE mercs.merchant_id = mercOpers.merchant_id AND mercOpers.operator_id = operatorId" )
                .append("  and mercs.merchant_id in (select merchant_id from tcg_admin.admin_merchant )) merchants, ")
                .append(" (select sys.stragg(mercs.merchant_id || ',') merchantId from tcg_admin.admin_merchant mercs, tcg_admin.us_merchant_operator mercOpers WHERE mercs.merchant_id = mercOpers.merchant_id AND mercOpers.operator_id = operatorId" )
                .append("  and mercs.merchant_id in (select merchant_id from tcg_admin.admin_merchant)) merchantId, ")
                .append(" (select listagg(role_name,', ') within group(order by role_name) roles  from tcg_admin.us_role roles, tcg_admin.us_role_operator roleOpers WHERE roles.role_id = roleOpers.role_id AND roleOpers.operator_id = operatorId) roles, ")
                .append(" (select listagg(roleOpers.role_id,', ') within group(order by roleOpers.role_id) roleIds  from tcg_admin.us_role roles, tcg_admin.us_role_operator roleOpers WHERE roles.role_id = roleOpers.role_id AND roleOpers.operator_id = operatorId) roleIds")
                .append(" FROM ")
                .append(" ((select ty.operatorId, ty.operatorName, ty.nickname, ty.activeFlag, ty.baseMerchantCode, ty.lastLoginTime, ty.lastLoginIP, auth.STATUS googleAuthStatus from" +
                        "(select distinct oper.operator_id operatorId,  oper.operator_name operatorName, oper.nickname nickname, oper.active_flag activeFlag, oper.base_merchant_code baseMerchantCode ");
        if(notEmpty){
            sb.append("  ,bev.END_PROC_DATE lastLoginTime, bev.IP_ADDRESS lastLoginIP ");
        }else {
            sb.append(" ,proOper.LAST_LOGIN_TIME lastLoginTime, proOper.LAST_LOGIN_IP lastLoginIP ");
        }
        sb.append(" FROM tcg_admin.us_operator oper, tcg_admin.US_MERCHANT_OPERATOR mercOper, tcg_admin.US_OPERATOR_PROFILE proOper  ");
        return sb;
    }

}
