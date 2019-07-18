package com.tcg.admin.to;

import com.google.common.collect.Lists;
import com.tcg.admin.common.error.AdminErrorCode;
import com.tcg.admin.common.exception.AdminServiceBaseException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.List;

public class MerchantChargeDetailTO {

    private Long rid;
    private Integer detailType;
    private String code;
    private String product;
    private String subProduct;
    private Integer rangeRateFlag;
    private Integer winLossCalculateFlag;
    private Integer accumulateNegative;
    private Integer rateType;
    private BigDecimal rate;
    private BigDecimal charge;
    private String remark;
    private List<MerchantChargeIntervalrateTO> intervalrateList = Lists.newLinkedList();

    public Long getRid() {
        return rid;
    }

    public void setRid(Long rid) {
        this.rid = rid;
    }

    public Integer getDetailType() {
        return detailType;
    }

    public void setDetailType(Integer detailType) {
        this.detailType = detailType;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getSubProduct() {
        return subProduct;
    }

    public void setSubProduct(String subProduct) {
        this.subProduct = subProduct;
    }

    public Integer getRangeRateFlag() {
        return rangeRateFlag;
    }

    public void setRangeRateFlag(Integer rangeRateFlag) {
        this.rangeRateFlag = rangeRateFlag;
    }

    public Integer getWinLossCalculateFlag() {
        return winLossCalculateFlag;
    }

    public void setWinLossCalculateFlag(Integer winLossCalculateFlag) {
        this.winLossCalculateFlag = winLossCalculateFlag;
    }

    public Integer getAccumulateNegative() {
        return accumulateNegative;
    }

    public void setAccumulateNegative(Integer accumulateNegative) {
        this.accumulateNegative = accumulateNegative;
    }

    public Integer getRateType() {
        return rateType;
    }

    public void setRateType(Integer rateType) {
        this.rateType = rateType;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public BigDecimal getCharge() {
        return charge;
    }

    public void setCharge(BigDecimal charge) {
        this.charge = charge;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public List<MerchantChargeIntervalrateTO> getIntervalrateList() {
        return intervalrateList;
    }

    public void setIntervalrateList(List<MerchantChargeIntervalrateTO> intervalrateList) {
        this.intervalrateList = intervalrateList;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        try {
            final Class bcc = this.getClass();
            Field[] fields = bcc.getDeclaredFields();
            for (Field f : fields) {
                String fieldName = f.getName();
                if (fieldName.contains("rid") || fieldName.contains("remark")) {
                    continue;
                }
                String getMethodName = "get".concat(fieldName.substring(0, 1).toUpperCase().concat(fieldName.substring(1, fieldName.length())));
                final Method bcGetMethod = bcc.getMethod(getMethodName);

                Object value = bcGetMethod.invoke(this);
                sb.append(fieldName).append("=").append(value).append(",");
            }
        } catch (Exception e) {
            throw new AdminServiceBaseException(AdminErrorCode.UNKNOWN_ERROR, "MerchantChargeDetailTO to String error", e);
        }
        return sb.delete(sb.length()-1, sb.length()).toString();
    }
}
