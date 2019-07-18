package com.tcg.admin.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tcg.admin.cache.RedisCacheable;
import com.tcg.admin.model.DomainProperties;
import com.tcg.admin.persistence.springdata.IDomainPropertiesRepository;
import com.tcg.admin.service.DomainService;

@Service
public class DomainServiceImpl implements DomainService {

	private static final Logger LOGGER = LoggerFactory.getLogger(DomainServiceImpl.class);
	
	@Autowired
	private IDomainPropertiesRepository domainPropertiesRepository;
	
	@Override
	@RedisCacheable(key="'domainProperties:' + #host")
	public DomainProperties getDomainProperties(String host) {
		
		LOGGER.info("get domain properties for host: {}", host);
		
		DomainProperties dp = domainPropertiesRepository.findByDomain(host);
		
		if(dp == null) {
			dp = createDefaultDomainProperties(host);
		}
		
		return dp;
	}

	private DomainProperties createDefaultDomainProperties(String domainName) {
		DomainProperties dp = new DomainProperties();
		
		dp.setDomain(domainName);
		dp.setLoginGoogleOtp(true);
		dp.setLoginPassword(true);
		
		return dp;
	}

}
