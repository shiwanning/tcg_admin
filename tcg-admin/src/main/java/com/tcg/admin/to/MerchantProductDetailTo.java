package com.tcg.admin.to;

import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.List;

public class MerchantProductDetailTo {
    private String product;
    private String productName;
    private boolean isInUse;
    private BigDecimal productCredit = new BigDecimal(0);
    private BigDecimal minimumFees = new BigDecimal(0);
    private BigDecimal productBalance = new BigDecimal(0);
    private BigDecimal useCredit = new BigDecimal(0);
    private List<MerchantChargeDetailTO> detailList = Lists.newLinkedList();

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public BigDecimal getProductCredit() {
        return productCredit;
    }

    public void setProductCredit(BigDecimal productCredit) {
        this.productCredit = productCredit;
    }

    public BigDecimal getMinimumFees() {
        return minimumFees;
    }

    public void setMinimumFees(BigDecimal minimumFees) {
        this.minimumFees = minimumFees;
    }

    public BigDecimal getProductBalance() {
        return productBalance;
    }

    public void setProductBalance(BigDecimal productBalance) {
        this.productBalance = productBalance;
    }

    public BigDecimal getUseCredit() {
        return useCredit;
    }

    public void setUseCredit(BigDecimal useCredit) {
        this.useCredit = useCredit;
    }

    public List<MerchantChargeDetailTO> getDetailList() {
        return detailList;
    }

    public void setDetailList(List<MerchantChargeDetailTO> detailList) {
        this.detailList = detailList;
    }

    public boolean isInUse() {
        return isInUse;
    }

    public void setInUse(boolean inUse) {
        isInUse = inUse;
    }
}
