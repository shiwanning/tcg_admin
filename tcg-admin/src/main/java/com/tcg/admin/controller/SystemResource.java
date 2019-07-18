package com.tcg.admin.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tcg.admin.service.CommonMenuService;
import com.tcg.admin.service.MenuCategoryService;
import com.tcg.admin.service.MerchantService;
import com.tcg.admin.to.response.JsonResponse;
import com.tcg.admin.utils.EhcacheUtil;

@RestController
@RequestMapping(value = "/resources/system/cache", produces = MediaType.APPLICATION_JSON_VALUE)
public class SystemResource {

	private static final Logger LOGGER = LoggerFactory.getLogger(SystemResource.class);
	
    @Autowired
    private CommonMenuService commonService;

    @Autowired
    private MerchantService merchantService;
    
    @Autowired
    private MenuCategoryService menuCategoryService;

    @GetMapping("/refresh")
    public JsonResponse refreshMenu() {
    	genAndSaveSystemMenuList();
        commonService.refresh();
        merchantService.renewMerchantList();
        EhcacheUtil.refreshCaches("cache-jpa-admin");
        return new JsonResponse(true);
    }
    
    public void genAndSaveSystemMenuList() {
    	try {
    		menuCategoryService.genAndSaveSystemMenuList();
    	} catch(Exception e) {
    		LOGGER.error("genAndSaveSystemMenuList error.", e);
    	}
    }
}