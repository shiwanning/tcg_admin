package com.tcg.admin.controller;

import org.springframework.http.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tcg.admin.service.CommonMenuService;
import com.tcg.admin.service.MerchantService;
import com.tcg.admin.utils.EhcacheUtil;
import com.tcg.admin.to.response.JsonResponse;

@RestController
@RequestMapping(value = "/resources/system/cache", produces = MediaType.APPLICATION_JSON_VALUE)
public class SystemResource {

    @Autowired
    private CommonMenuService commonService;

    @Autowired
    private MerchantService merchantService;

    @GetMapping("/refresh")
    public JsonResponse refreshMenu() {
        commonService.refresh();
        merchantService.renewMerchantList();
        EhcacheUtil.refreshCaches("cache-jpa-admin");
        EhcacheUtil.refreshCaches("cache-ap-admin");
        return new JsonResponse(true);
    }
}