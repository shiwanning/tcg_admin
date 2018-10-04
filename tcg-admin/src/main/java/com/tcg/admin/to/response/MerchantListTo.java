package com.tcg.admin.to.response;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

public class MerchantListTo {
    
    private List<Map<String,String>> merchants = Lists.newArrayList();

    public MerchantListTo(List<Map<String, String>> merchants) {
        this.merchants = merchants;
    }

    public List<Map<String, String>> getMerchants() {
        return merchants;
    }

    public void setMerchants(List<Map<String, String>> merchants) {
        this.merchants = merchants;
    }
    
}
