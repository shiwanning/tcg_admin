package com.tcg.admin.persistence.springdata;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tcg.admin.model.MerchantProperties;

public interface IMerchantPropertiesRepository extends JpaRepository<MerchantProperties, Long> {
    
    MerchantProperties findByMerchantCode(String merchantCode);
    
}
