package com.tcg.admin.service;

import com.tcg.admin.model.MerchantProperties;

public interface MerchantPropertiesService {
    
    MerchantProperties getMerchantProperties(String merchantCode);
    
    void save(MerchantProperties entity);
    
}
