package com.tcg.admin.to;

import java.util.List;

public class MerchantChargeTemplateMerchangsTO {

    private Long templateId;
    private String templateName;
    private List<MerchantTo> merchants;
    private Integer merchantCount;

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public List<MerchantTo> getMerchants() {
        return merchants;
    }

    public void setMerchants(List<MerchantTo> merchants) {
        this.merchants = merchants;
    }

    public Integer getMerchantCount() {
        return merchantCount;
    }

    public void setMerchantCount(Integer merchantCount) {
        this.merchantCount = merchantCount;
    }
}
