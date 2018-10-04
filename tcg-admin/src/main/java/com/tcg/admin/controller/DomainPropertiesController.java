package com.tcg.admin.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tcg.admin.common.helper.RequestHelper;
import com.tcg.admin.model.DomainProperties;
import com.tcg.admin.service.DomainService;
import com.tcg.admin.to.response.JsonResponseT;

@RestController
@RequestMapping(value = "/resources/domain", produces = MediaType.APPLICATION_JSON_VALUE)
public class DomainPropertiesController {

	private static final Logger LOGGER = LoggerFactory.getLogger(DomainPropertiesController.class);
	
	@Autowired
	private DomainService domainService;
	
	@GetMapping("/properties")
	public JsonResponseT<DomainProperties> createFile() {
		
		DomainProperties dp = domainService.getDomainProperties(RequestHelper.getHost());
		
		return new JsonResponseT<>(true, dp);
	}
	
}
