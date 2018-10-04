package com.tcg.admin.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tcg.admin.model.MerchantWhitelistIp;
import com.tcg.admin.persistence.springdata.IMerchantWhitelistIpRepository;
import com.tcg.admin.service.MerchantWhitelistIpService;

@Service
@Transactional
public class MerchantWhitelistIpServiceBean implements MerchantWhitelistIpService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MerchantWhitelistIpServiceBean.class);
    
    @Autowired
    private IMerchantWhitelistIpRepository merchantWhitelistIpRepository;
    
    @Override
    public MerchantWhitelistIp getMerchantWhitelistIp(Long rid) {
        return merchantWhitelistIpRepository.findOne(rid);
    }

    @Override
    public boolean isExistMerchantWhitelistIp(String merchantCode, String ip) {
        return merchantWhitelistIpRepository.findByMerchantCodeAndIp(merchantCode, ip) != null;
    }

    @Override
    public Page<MerchantWhitelistIp> getMerchantWhitelistIps(String merchantCode, String ip, Pageable pageable) {
        
        // 让 % 失效
    	String ipLikeQuery = ip == null ? "%" : "%" + ip.replace("%", "Q") + "%";
        return merchantWhitelistIpRepository.findByMerchantCodeAndIpLikeOrderByRidDesc(merchantCode, ipLikeQuery, pageable);
    }

    @Override
    public void save(MerchantWhitelistIp entity) {
        merchantWhitelistIpRepository.saveAndFlush(entity);
    }

    @Override
    public void delete(Long rid) {
        merchantWhitelistIpRepository.delete(rid);
    }

}
