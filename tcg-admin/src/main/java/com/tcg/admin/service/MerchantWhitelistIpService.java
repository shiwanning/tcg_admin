package com.tcg.admin.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.tcg.admin.model.MerchantWhitelistIp;

public interface MerchantWhitelistIpService {
    
    MerchantWhitelistIp getMerchantWhitelistIp(Long rid);
    
    boolean isExistMerchantWhitelistIp(String merchant, String ip);
    
    Page<MerchantWhitelistIp> getMerchantWhitelistIps(String merchant, String ip, Pageable pageable);
    
    void save(MerchantWhitelistIp entity);
    
    void delete(Long rid);
}
