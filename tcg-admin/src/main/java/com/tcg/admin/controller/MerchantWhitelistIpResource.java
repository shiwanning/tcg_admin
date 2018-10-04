package com.tcg.admin.controller;

import java.util.Date;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tcg.admin.common.error.AdminErrorCode;
import com.tcg.admin.common.helper.RequestHelper;
import com.tcg.admin.model.MerchantWhitelistIp;
import com.tcg.admin.model.Operator;
import com.tcg.admin.service.MerchantWhitelistIpService;
import com.tcg.admin.service.impl.OperatorLoginService;
import com.tcg.admin.to.UserInfo;
import com.tcg.admin.to.response.JsonResponse;
import com.tcg.admin.to.response.JsonResponseT;
import com.tcg.admin.utils.IpUtils;

@RestController
@RequestMapping(value = "/resources/merchants/whitelist", produces = MediaType.APPLICATION_JSON_VALUE)
public class MerchantWhitelistIpResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(MerchantResource.class);

    @Autowired
    private MerchantWhitelistIpService merchantWhitelistIpService;
    
    @Autowired
    private OperatorLoginService operatorLoginService;
    
    @GetMapping
    public JsonResponseT<Page<MerchantWhitelistIp>> get(@RequestParam(value = "merchantCode", required = false) String merchantCode, 
            @RequestParam(value = "ip", required = false) String ip, 
            @RequestParam(value = "pageNo", required = false) Integer pageNo,
            @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        JsonResponseT<Page<MerchantWhitelistIp>> response = new JsonResponseT<>(true);
        Pageable pageable = new PageRequest(pageNo-1, pageSize);
        Page<MerchantWhitelistIp> merchatList = merchantWhitelistIpService.getMerchantWhitelistIps(merchantCode, ip, pageable);
        response.setValue(merchatList);
        return response;
    }
    
    @PostMapping
    public ResponseEntity<JsonResponse> create(@RequestBody MerchantWhitelistIp vo) {
        
        JsonResponse response = new JsonResponse(true);

        if(!IpUtils.validIpv4(vo.getIp()) && !IpUtils.validIpv6(vo.getIp())) {
            response.setSuccess(false);
            response.setErrorCode(AdminErrorCode.INVALID_PARAMETERS);
            response.setMessage("ip format error: " + vo.getIp());
            return ResponseEntity.badRequest().body(response);
        }
        
        UserInfo<Operator> userInfo = operatorLoginService.getSessionUser(RequestHelper.getToken());
        
        MerchantWhitelistIp entity = new MerchantWhitelistIp();
        
        entity.setMerchantCode(vo.getMerchantCode());
        entity.setIp(vo.getIp());
        entity.setCreateOperatorName(userInfo.getUser().getOperatorName());
        entity.setUpdateOperatorName(userInfo.getUser().getOperatorName());
        
        merchantWhitelistIpService.save(entity);
        
        return ResponseEntity.ok().body(response);
    }
    
    @PutMapping
    public ResponseEntity<JsonResponse> update(@RequestBody MerchantWhitelistIp vo) {
        
        JsonResponse response = new JsonResponse(true);

        MerchantWhitelistIp entity = merchantWhitelistIpService.getMerchantWhitelistIp(vo.getRid());
        
        if(entity == null) {
            response.setSuccess(false);
            response.setErrorCode(AdminErrorCode.DATA_NOT_FOUND);
            response.setMessage("rid not found: " + vo.getRid());
            return ResponseEntity.badRequest().body(response);
        }
        
        if(!IpUtils.validIpv4(vo.getIp()) && !IpUtils.validIpv6(vo.getIp())) {
            response.setSuccess(false);
            response.setErrorCode(AdminErrorCode.INVALID_PARAMETERS);
            response.setMessage("ip format error: " + vo.getIp());
            return ResponseEntity.badRequest().body(response);
        }
        
        UserInfo<Operator> userInfo = operatorLoginService.getSessionUser(RequestHelper.getToken());
        
        entity.setMerchantCode(vo.getMerchantCode());
        entity.setIp(vo.getIp());
        entity.setUpdateOperatorName(userInfo.getUser().getOperatorName());
        entity.setUpdateTime(new Date());
        
        merchantWhitelistIpService.save(entity);
        
        return ResponseEntity.ok().body(response);
    }
    
    @DeleteMapping
    public JsonResponse delete(@RequestParam(value = "rid", required = false) Long rid) {
        JsonResponse response = new JsonResponse(true);
        merchantWhitelistIpService.delete(rid);
        return response;
    }
    
    
    
}
