package com.tcg.admin.to;

import com.tcg.admin.common.error.AdminErrorCode;
import com.tcg.admin.common.exception.AdminServiceBaseException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;

public class MerchantChargeIntervalrateTO {

    private Long rid;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    private BigDecimal rate;

    public BigDecimal getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(BigDecimal minAmount) {
        this.minAmount = minAmount;
    }

    public BigDecimal getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(BigDecimal maxAmount) {
        this.maxAmount = maxAmount;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public Long getRid() {
        return rid;
    }

    public void setRid(Long rid) {
        this.rid = rid;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        try {
            final Class bcc = this.getClass();
            Field[] fields = bcc.getDeclaredFields();
            for (Field f : fields) {
                String fieldName = f.getName();
                if (fieldName.contains("rid")) {
                    continue;
                }
                String getMethodName = "get".concat(fieldName.substring(0, 1).toUpperCase().concat(fieldName.substring(1, fieldName.length())));
                final Method bcGetMethod = bcc.getMethod(getMethodName);
                Object value = bcGetMethod.invoke(this);
                sb.append(fieldName).append("=").append(value).append(",");
            }
        } catch (Exception e) {
            throw new AdminServiceBaseException(AdminErrorCode.UNKNOWN_ERROR, "MerchantChargeIntervalrateTO to String error", e);
        }
        return sb.delete(sb.length()-1, sb.length()).toString();
    }
}
