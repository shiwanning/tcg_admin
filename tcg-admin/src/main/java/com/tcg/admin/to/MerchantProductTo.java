package com.tcg.admin.to;

import java.math.BigDecimal;
import java.util.List;

public class MerchantProductTo {

    private BigDecimal mainWallet = new BigDecimal(0);
    private BigDecimal cashPledge = new BigDecimal(0);
    private BigDecimal availableCredit = new BigDecimal(0);
    private BigDecimal merchantCredit = new BigDecimal(0);
    private BigDecimal availableBalance = new BigDecimal(0);
    private List<MerchantProductDetailTo> productDetailList;

    public BigDecimal getMainWallet() {
        return mainWallet;
    }

    public void setMainWallet(BigDecimal mainWallet) {
        this.mainWallet = mainWallet;
    }

    public BigDecimal getCashPledge() {
        return cashPledge;
    }

    public void setCashPledge(BigDecimal cashPledge) {
        this.cashPledge = cashPledge;
    }

    public BigDecimal getAvailableCredit() {
        return availableCredit;
    }

    public void setAvailableCredit(BigDecimal availableCredit) {
        this.availableCredit = availableCredit;
    }

    public BigDecimal getMerchantCredit() {
        return merchantCredit;
    }

    public void setMerchantCredit(BigDecimal merchantCredit) {
        this.merchantCredit = merchantCredit;
    }

    public BigDecimal getAvailableBalance() {
        return availableBalance;
    }

    public void setAvailableBalance(BigDecimal availableBalance) {
        this.availableBalance = availableBalance;
    }

    public List<MerchantProductDetailTo> getProductDetailList() {
        return productDetailList;
    }

    public void setProductDetailList(List<MerchantProductDetailTo> productDetailList) {
        this.productDetailList = productDetailList;
    }
}
