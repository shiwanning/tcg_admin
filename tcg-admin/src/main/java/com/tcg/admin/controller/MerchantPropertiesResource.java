package com.tcg.admin.controller;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tcg.admin.common.helper.RequestHelper;
import com.tcg.admin.model.MerchantProperties;
import com.tcg.admin.model.Operator;
import com.tcg.admin.service.MerchantPropertiesService;
import com.tcg.admin.service.impl.OperatorLoginService;
import com.tcg.admin.to.UserInfo;
import com.tcg.admin.to.response.JsonResponse;
import com.tcg.admin.to.response.JsonResponseT;
import com.tcg.admin.to.response.MerchantWhitelistPropertiesTo;

@RestController
@RequestMapping(value = "/resources/merchants/properties", produces = MediaType.APPLICATION_JSON_VALUE)
public class MerchantPropertiesResource {

    @Autowired
    private MerchantPropertiesService merchantPropertiesService;
    
    @Autowired
    private OperatorLoginService operatorLoginService;
    
    @GetMapping("/whitelist")
    public JsonResponseT<MerchantWhitelistPropertiesTo> get(@RequestParam(value = "merchantCode", required = false) String merchantCode) {
        JsonResponseT<MerchantWhitelistPropertiesTo> response = new JsonResponseT<>(true);
        MerchantProperties mp = merchantPropertiesService.getMerchantProperties(merchantCode);
        MerchantWhitelistPropertiesTo to;
        if(mp == null) {
            to = new MerchantWhitelistPropertiesTo(merchantCode, false);
        } else {
            to = new MerchantWhitelistPropertiesTo(merchantCode, "Y".equals(mp.getWhitelistFunction()));
        }
        
        response.setValue(to);
        return response;
    }
    
    @PutMapping("/whitelist")
    public JsonResponse saveProperties(@RequestBody MerchantWhitelistPropertiesTo vo) {
        
        JsonResponse response = new JsonResponse(true);
        
        UserInfo<Operator> userInfo = RequestHelper.getCurrentUser();
        
        MerchantProperties entity = merchantPropertiesService.getMerchantProperties(vo.getMerchantCode());
        
        if(entity == null) {
            entity = new MerchantProperties();
            entity.setMerchantCode(vo.getMerchantCode());
            entity.setCreateOperatorName(userInfo.getUser().getOperatorName());
        } else {
            entity.setUpdateTime(new Date());
        }
        
        entity.setUpdateOperatorName(userInfo.getUser().getOperatorName());
        entity.setWhitelistFunction(vo.isWhitelistFunction() ? "Y" : "N");
        
        merchantPropertiesService.save(entity);
        
        return response;
    }
    
}
