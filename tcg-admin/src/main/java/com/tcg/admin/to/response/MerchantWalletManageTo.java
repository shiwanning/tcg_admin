package com.tcg.admin.to.response;

import java.math.BigDecimal;

public class MerchantWalletManageTo {
    private Integer walletId;
    private Integer merchantId;
    private String merchantCore;
    private String merchantDesc;
    private Integer accountTypeId;
    private String accountTypeName;
    private String currnecy;
    private Integer isUse;
    private Integer isAutoTransfer;
    private Integer hasParent;
    private Integer parentWalletId;
    private String createTime;
    private String updateTime;
    private Integer accountId;
    private Integer customerId;
    private BigDecimal balance;
    private BigDecimal creditLimit;
    private Integer isSeamless;
    private Integer isSingle;
    private String displayName;
    private BigDecimal limitRemain;
    private BigDecimal totalCreditLimit;
    private Integer returnTo;
    private Integer supportSeamless;
    private BigDecimal rate;


    public Integer getSupportSeamless() {
        return supportSeamless;
    }

    public void setSupportSeamless(Integer supportSeamless) {
        this.supportSeamless = supportSeamless;
    }

    public Integer getReturnTo() {
        return returnTo;
    }

    public void setReturnTo(Integer returnTo) {
        this.returnTo = returnTo;
    }

    public BigDecimal getLimitRemain() {
        return limitRemain;
    }

    public void setLimitRemain(BigDecimal limitRemain) {
        this.limitRemain = limitRemain;
    }

    public BigDecimal getTotalCreditLimit() {
        return totalCreditLimit;
    }

    public void setTotalCreditLimit(BigDecimal totalCreditLimit) {
        this.totalCreditLimit = totalCreditLimit;
    }

    public Integer getWalletId() {
        return walletId;
    }

    public void setWalletId(Integer walletId) {
        this.walletId = walletId;
    }

    public Integer getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Integer merchantId) {
        this.merchantId = merchantId;
    }

    public String getMerchantCore() {
        return merchantCore;
    }

    public void setMerchantCore(String merchantCore) {
        this.merchantCore = merchantCore;
    }

    public String getMerchantDesc() {
        return merchantDesc;
    }

    public void setMerchantDesc(String merchantDesc) {
        this.merchantDesc = merchantDesc;
    }

    public Integer getAccountTypeId() {
        return accountTypeId;
    }

    public void setAccountTypeId(Integer accountTypeId) {
        this.accountTypeId = accountTypeId;
    }

    public String getAccountTypeName() {
        return accountTypeName;
    }

    public void setAccountTypeName(String accountTypeName) {
        this.accountTypeName = accountTypeName;
    }

    public String getCurrnecy() {
        return currnecy;
    }

    public void setCurrnecy(String currnecy) {
        this.currnecy = currnecy;
    }

    public Integer getIsUse() {
        return isUse;
    }

    public void setIsUse(Integer isUse) {
        this.isUse = isUse;
    }

    public Integer getIsAutoTransfer() {
        return isAutoTransfer;
    }

    public void setIsAutoTransfer(Integer isAutoTransfer) {
        this.isAutoTransfer = isAutoTransfer;
    }

    public Integer getHasParent() {
        return hasParent;
    }

    public void setHasParent(Integer hasParent) {
        this.hasParent = hasParent;
    }

    public Integer getParentWalletId() {
        return parentWalletId;
    }

    public void setParentWalletId(Integer parentWalletId) {
        this.parentWalletId = parentWalletId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(BigDecimal creditLimit) {
        this.creditLimit = creditLimit;
    }

    public Integer getIsSeamless() {
        return isSeamless;
    }

    public void setIsSeamless(Integer isSeamless) {
        this.isSeamless = isSeamless;
    }

    public Integer getIsSingle() {
        return isSingle;
    }

    public void setIsSingle(Integer isSingle) {
        this.isSingle = isSingle;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

}
