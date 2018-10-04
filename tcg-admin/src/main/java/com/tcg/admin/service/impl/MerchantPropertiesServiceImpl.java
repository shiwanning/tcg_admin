package com.tcg.admin.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tcg.admin.model.MerchantProperties;
import com.tcg.admin.persistence.springdata.IMerchantPropertiesRepository;
import com.tcg.admin.service.MerchantPropertiesService;

@Service
@Transactional
public class MerchantPropertiesServiceImpl implements MerchantPropertiesService {

    @Autowired
    private IMerchantPropertiesRepository merchantPropertiesRepository;
    
    @Override
    public MerchantProperties getMerchantProperties(String merchantCode) {
        return merchantPropertiesRepository.findByMerchantCode(merchantCode);
    }

    @Override
    public void save(MerchantProperties entity) {
        merchantPropertiesRepository.saveAndFlush(entity);
    }

}
