package com.tcg.admin.to;

import java.util.List;

public class MerchantChargeTemplateConfigTO {

    private String merchantCode;
    private List<Long> templateIds;

    public String getMerchantCode() {
        return merchantCode;
    }

    public void setMerchantCode(String merchantCode) {
        this.merchantCode = merchantCode;
    }

    public List<Long> getTemplateIds() {
        return templateIds;
    }

    public void setTemplateIds(List<Long> templateIds) {
        this.templateIds = templateIds;
    }
}
