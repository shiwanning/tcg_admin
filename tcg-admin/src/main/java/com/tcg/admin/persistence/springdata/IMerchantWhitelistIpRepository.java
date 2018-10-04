package com.tcg.admin.persistence.springdata;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.tcg.admin.model.MerchantWhitelistIp;

public interface IMerchantWhitelistIpRepository extends JpaRepository<MerchantWhitelistIp, Long> {
    
    MerchantWhitelistIp findByMerchantCodeAndIp(String merchantCode, String ip);
    
    Page<MerchantWhitelistIp> findByMerchantCodeAndIpLikeOrderByRidDesc(String merchantCode, String ip, Pageable pageable);
    
    List<MerchantWhitelistIp> findByMerchantCode(String merchantCode);
}
