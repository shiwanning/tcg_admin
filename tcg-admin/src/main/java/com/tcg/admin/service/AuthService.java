package com.tcg.admin.service;

import com.tcg.admin.model.Operator;
import com.tcg.admin.model.OperatorAuth;
import com.tcg.admin.to.UserInfo;

import java.util.Date;

public interface AuthService {
    
    /**
     * 取得 google 认证资料
     */
    OperatorAuth getGoogleAuth(Integer operatorId);
    
    /**
     * 产生新的 google 认证资料
     * 
     * @return authKey
     */
    String generateGoogleAuth(Operator operator);
    
    /**
     * 修改认证启用状态
     */
    Boolean setGoogleAuthStatus(Integer operatorId, Boolean status, boolean isAuto);
    
    /**
     * 创建 google auth key
     * @param operatorId
     */
    String createNewGoogleAuthKey();

    /**
     * 启用 google 验证
     */
    void activeGoogleAuth(Integer operatorId, String key, boolean isAuto);

    void activeGoogleAuthName(Integer operatorId, String key, boolean isAuto, String userName);

    boolean authorize(String key, Integer otp);

    /**
     * 保存最后 pass 时间
     * @param operatorId
     */
    void saveLastPassTime(Integer operatorId);

    void generateGoogleAuthBindRole(UserInfo<Operator> userInfo, Integer operatorId);

    void deleteByOperatorId(Integer operatorId);

    void logSuccessLogin(Integer operatorId, String userName, Date lastLoginTime);
}
