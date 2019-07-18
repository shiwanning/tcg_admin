package com.tcg.admin.common.aspect;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.tcg.admin.common.annotation.OperationLog;
import com.tcg.admin.common.constants.OperationFunctionConstant;
import com.tcg.admin.common.helper.RequestHelper;
import com.tcg.admin.model.*;
import com.tcg.admin.service.AuthService;
import com.tcg.admin.service.MerchantService;
import com.tcg.admin.service.OperatorService;
import com.tcg.admin.service.RoleService;
import com.tcg.admin.service.log.OperatorLogService;
import com.tcg.admin.utils.JsonUtils;
import org.apache.commons.collections.CollectionUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * com.tcg.admin.common.aspect
 *
 * @author lyndon.j
 * @version 1.0
 * @date 2019/6/26 17:06
 */
@Aspect
@Component
public class OperatorLogAspect {
    private static final Logger LOGGER = LoggerFactory.getLogger(OperatorLogAspect.class);
    private static final String parameterId = "id";
    private static final String parameterName = "name";
    private static final String parameterPassWord = "Password";

    @Autowired
    private OperatorLogService operatorLogService;
    @Autowired
    private OperatorService operatorService;
    @Autowired
    private AuthService authService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private MerchantService merchantService;

    @Pointcut("@annotation(com.tcg.admin.common.annotation.OperationLog)")
    public void pointCut() {

    }

    @Before("pointCut()")
    public void before(JoinPoint joinPoint) {
        OperationLog operationLog = getAnnotation(joinPoint);
        if (operationLog == null) {
            return;
        }
        Map<String, Object> parameterMap = buildMethodParamMap(joinPoint,operationLog.type());

        Operator operator = RequestHelper.getCurrentUser().getUser();

        Operator editedOperator = getEditedOperator(parameterMap, operationLog.type());

        if (editedOperator == null) {
            LOGGER.info("can not find edited operator,invoke method : {},parameters :{}", joinPoint.getSignature().getName(), joinPoint.getArgs());
            return;
        }
        String oldValue = getValue(operationLog.type(), editedOperator, true);
        try {
            OperatorLog operatorLog = operatorLogService.insert(editedOperator.getBaseMerchantCode(), operator.getOperatorName(), editedOperator.getOperatorName(),
                    operationLog.type(), oldValue, null, JsonUtils.toJson(parameterMap));

            RequestHelper.getRequest().setAttribute("logId", operatorLog.getId());
            RequestHelper.getRequest().setAttribute("operatorId", editedOperator.getOperatorId());
        } catch (Exception e) {
            LOGGER.error("operator log error : ", e);
            e.printStackTrace();
        }
    }


    @After("pointCut()")
    public void after(JoinPoint joinPoint) {
        OperationLog operationLog = getAnnotation(joinPoint);
        if (operationLog == null) {
            return;
        }
        Long logId = (Long) RequestHelper.getRequest().getAttribute("logId");
        Integer operatorId = (Integer) RequestHelper.getRequest().getAttribute("operatorId");
        if (null == logId || null == operatorId) {
            return;
        }
        Operator operator = operatorService.findOperatorById(operatorId);
        String newValue = getValue(operationLog.type(), operator, false);
        if (OperationFunctionConstant.COPY_ACCOUNT.equals(operationLog.type())) {
            Map<String, Object> parameterMap = buildMethodParamMap(joinPoint,operationLog.type());
            newValue = getOperatorName(parameterMap);
        }
        operatorLogService.update(logId, newValue);
    }


    private OperationLog getAnnotation(JoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        if (null != method) {
            return method.getAnnotation(OperationLog.class);
        }
        return null;
    }

    private List<String> getMethodParameterName(JoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        String[] names = methodSignature.getParameterNames();
        return Arrays.asList(names);
    }

    private List<Object> getMethodParameterValue(JoinPoint joinPoint) {
        return Arrays.asList(joinPoint.getArgs());
    }

    private void getRequestParam() {
        Map<String, String[]> parameterMap = RequestHelper.getRequest().getParameterMap();
        System.out.println(JsonUtils.toJson(parameterMap));
    }

    private Map<String, Object> buildMethodParamMap(JoinPoint joinPoint,String functionType) {

        List<String> names = getMethodParameterName(joinPoint);
        List<Object> values = getMethodParameterValue(joinPoint);
        if (CollectionUtils.isEmpty(names) || CollectionUtils.isEmpty(values)) {
            return null;
        }

        Map<String, Object> paramMap = Maps.newHashMapWithExpectedSize(names.size());

        for (int i = 0; i < names.size(); i++) {
            paramMap.put(names.get(i), values.get(i));
        }

        if (OperationFunctionConstant.MODIFY_PASS_WORD.equals(functionType)) {
            paramMap = removePassword(paramMap);
        }

        return paramMap;
    }


    private Operator getEditedOperator(Map<String, Object> paramMap, String function) {

        Operator operator = getOperator(function);

        if (null != operator) {
            return operator;
        }

        if (isIdParam(paramMap)) {
            Object id = getQueryOperatorParamValue(paramMap);
            if (id != null) {
                return operatorService.findOperatorById((Integer) id);
            }
        }

        Object operatorName = getQueryOperatorParamValue(paramMap);
        if (operatorName != null) {
            return operatorService.findOperatorByName(operatorName.toString());
        }

        return null;
    }

    private boolean isIdParam(Map<String, Object> paramMap) {
        for (String key : paramMap.keySet()) {
            if (key.toLowerCase().contains(parameterId)) {
                return true;
            }
        }
        return false;
    }

    private Object getQueryOperatorParamValue(Map<String, Object> paramMap) {

        for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
            if (entry.getKey().toLowerCase().contains(parameterId)) {
                return entry.getValue();
            }
            if (entry.getKey().toLowerCase().contains(parameterName)) {
                return entry.getValue();
            }
        }
        return null;
    }

    private String getOperatorName(Map<String, Object> paramMap) {

        for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
            if (entry.getKey().toLowerCase().contains(parameterName)) {
                return entry.getValue().toString();
            }
        }
        return null;
    }

    private OperatorAuth getGoogleAuthStatus(Integer operatorId) {
        return authService.getGoogleAuth(operatorId);
    }

    private List<Role> getRole(Operator operator) {
        return roleService.findOperatorAllRoleByOperatorId(operator.getOperatorId());
    }

    private List<Merchant> getMerchantList(Operator operator) {
        return merchantService.getOperatorMerchant(operator);
    }


    private List<String> getMerchantCodes(List<Merchant> list) {
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }

        List<String> resultList = Lists.newArrayListWithCapacity(list.size());
        for (Merchant merchant : list) {
            resultList.add(merchant.getMerchantCode());
        }

        return resultList;
    }

    private String getValue(String function, Operator operator, boolean isBefore) {
        switch (function) {
            case OperationFunctionConstant.MODIFY_ROLE:
                return JsonUtils.toJson(getRole(operator));
            case OperationFunctionConstant.MODIFY_SUBSCRIPTION_MERCHANT:
                List<Merchant> merchants = getMerchantList(operator);
                return JsonUtils.toJson(getMerchantCodes(merchants));
            case OperationFunctionConstant.MODIFY_STATUS:
            case OperationFunctionConstant.DELETE_ACCOUNT:
                return operator.getActiveFlag().toString();
            case OperationFunctionConstant.COPY_ACCOUNT:
                return isBefore ? null : operator.getOperatorName();
            case OperationFunctionConstant.RESET_PASS_WORD:
            case OperationFunctionConstant.WATCH_PERMISSIONS:
            case OperationFunctionConstant.MODIFY_PASS_WORD:
                return null;
            case OperationFunctionConstant.MODIFY_BELONGS_MERCHANT:
                return operator.getBaseMerchantCode();
            case OperationFunctionConstant.REMOVE_GOOGLE_VERIFICATION:
            case OperationFunctionConstant.ADD_GOOGLE_VERIFICATION:
                OperatorAuth auth = getGoogleAuthStatus(operator.getOperatorId());
                return null == auth ? null : auth.getStatus().ordinal()+"";
            case OperationFunctionConstant.MODIFY_NICK_NAME:
                return operator.getNickname();
            case OperationFunctionConstant.MODIFY_EMAIL:
                return operator.getProfile().getEmail();
            case OperationFunctionConstant.MODIFY_MOBILE:
                return operator.getProfile().getMobileNo();
            case OperationFunctionConstant.MODIFY_PROFILE:
                return getProfileJson(operator);
        }

        return null;
    }

    private String getProfileJson(Operator operator) {
        Map<String, String> profileMap = Maps.newHashMapWithExpectedSize(3);
        profileMap.put("phone", operator.getProfile().getPhone());
        profileMap.put("email", operator.getProfile().getEmail());
        profileMap.put("nick_name", operator.getNickname());
        return JsonUtils.toJson(profileMap);
    }

    //modify self ,parameter no include operator info need get for head
    private Operator getOperator(String functionType) {
        if (OperationFunctionConstant.ADD_GOOGLE_VERIFICATION.equals(functionType)
                || OperationFunctionConstant.MODIFY_PROFILE.equals(functionType)
                || OperationFunctionConstant.MODIFY_PASS_WORD.equals(functionType)) {
            return RequestHelper.getCurrentUser().getUser();
        }
        return null;
    }

    private Map<String,Object> removePassword(Map<String,Object> parameterMap) {
        Map<String, Object> result = Maps.newHashMapWithExpectedSize(parameterMap.size());
        Object object = parameterMap.values().iterator().next();
        if (!(object instanceof Map)) {
            return result;
        }

        for (Map.Entry entry : ((Map<String, Object>) object).entrySet()) {
            result.put(entry.getKey().toString(),entry.getValue());
            if (null != entry.getKey() && entry.getKey().toString().contains(parameterPassWord)) {
                result.put(entry.getKey().toString(), "********");
            }
        }

        return result;
    }
}
