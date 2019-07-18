package com.tcg.admin.to;

import java.math.BigDecimal;

import javax.validation.constraints.NotNull;

/**
 * Created by chris.h on 2017/1/25.
 */
public class MerchantCreditTo {

    @NotNull
    private String merchantCode;
    @NotNull
    private BigDecimal leverMultiplier;
    @NotNull
    private BigDecimal virtualCashPledge;

    public String getMerchantCode() {
        return merchantCode;
    }

    public void setMerchantCode(String merchantCode) {
        this.merchantCode = merchantCode;
    }

    public BigDecimal getLeverMultiplier() {
        return leverMultiplier;
    }

    public void setLeverMultiplier(BigDecimal leverMultiplier) {
        this.leverMultiplier = leverMultiplier;
    }

    public BigDecimal getVirtualCashPledge() {
        return virtualCashPledge;
    }

    public void setVirtualCashPledge(BigDecimal virtualCashPledge) {
        this.virtualCashPledge = virtualCashPledge;
    }
}
