package com.tcg.admin.utils.shiro;

import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.Maps;
import com.tcg.admin.model.Operator;
import com.tcg.admin.model.OperatorProfile;
import com.tcg.admin.to.OperatorAndProfileTo;

public class OtpUtils {

    private static ConcurrentMap<Integer, Operator> otpOperatorMap = Maps.newConcurrentMap();
    private static ConcurrentMap<Integer, OperatorProfile> otpOperatorProfileMap = Maps.newConcurrentMap();
    private static ConcurrentMap<Integer, Integer> errorCountMap = Maps.newConcurrentMap();

    private OtpUtils() {}
    
    public static boolean isNeedOtp(Integer operatorId) {
        return otpOperatorMap.get(operatorId) != null;
    }

    public static void removeOtpData(Integer operatorId) {
        otpOperatorMap.remove(operatorId);
        otpOperatorProfileMap.remove(operatorId);
    }

    public static void putNeedOtp(Integer operatorId, OperatorProfile profile, Operator operator) {
        otpOperatorMap.put(operatorId, operator);
        otpOperatorProfileMap.put(operatorId, profile);
    }

    public static OperatorAndProfileTo getOperatorAndProfile(Integer operatorId) {
        Operator operator = otpOperatorMap.get(operatorId);
        OperatorProfile operatorProfile = otpOperatorProfileMap.get(operatorId);

        return new OperatorAndProfileTo(operator, operatorProfile);
    }

    public static int addLoginErrorCount(int operatorId) {
        int originErrorCount;
        if (errorCountMap.get(operatorId) == null) {
            originErrorCount = 1;
        } else {
            originErrorCount = errorCountMap.get(operatorId) + 1;
        }

        errorCountMap.put(operatorId, originErrorCount);

        return originErrorCount;
    }
    
    public static void removeErrorCount(int operatorId) {
        errorCountMap.remove(operatorId);
    }

}
