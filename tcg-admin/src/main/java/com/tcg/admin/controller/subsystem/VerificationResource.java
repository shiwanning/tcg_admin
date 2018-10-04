package com.tcg.admin.controller.subsystem;

import org.springframework.http.MediaType;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.primitives.Ints;
import com.tcg.admin.common.helper.RequestHelper;
import com.tcg.admin.controller.core.BehaviorRequestWrapper;
import com.tcg.admin.service.VerificationService;
import com.tcg.admin.to.VerificationData;
import com.tcg.admin.to.response.JsonResponse;

@RestController
@RequestMapping(value = "/resources/subsystem/verification", produces = MediaType.APPLICATION_JSON_VALUE)
public class VerificationResource {

    @Autowired
    private VerificationService verificationService;

    @PostMapping("/basic")
    public JsonResponse basic(@RequestHeader(value = "Authorization", required = false)  String token, 
            @RequestParam(value = "merchant", required = false) String merchant, 
            @RequestParam(value = "menuId", required = false) Integer menuId,
            HttpServletRequest request) throws UnsupportedEncodingException {
        
    	String localMerchant = merchant;
    	Integer localMenuId = menuId;
    	
        if(menuId == null) {
            BehaviorRequestWrapper br = (BehaviorRequestWrapper) request;
            String body = br.getBody();
            Map<String, String> parameterMap = RequestHelper.getMapFromUrlFormat(body);
            localMerchant = parameterMap.get("merchant");
            localMenuId = parameterMap.get("menuId") == null ? null : Ints.tryParse(parameterMap.get("menuId"));
        }
        
        VerificationData verificationData = new VerificationData();
        verificationData.setMenuId(localMenuId);
        verificationData.setMerchant(localMerchant);
        verificationData.setToken(token);
        return new JsonResponse(verificationService.basic(verificationData));
    }

    @GetMapping("/advanced")
    public JsonResponse advanced(
            @RequestHeader(value = "Authorization", required = false)  String token, 
            @RequestParam(value = "merchant", required = false) String merchant, 
            @RequestParam(value = "ip", required = false) String ip, 
            @RequestParam(value = "menuId", required = false) Integer menuId,
            @RequestParam(value = "params", required = false) String params) {
        VerificationData verificationData = new VerificationData();
        verificationData.setMenuId(menuId);
        verificationData.setMerchant(merchant);
        verificationData.setToken(token);
        verificationData.setParams(params);
        verificationData.setIp(ip);
        return new JsonResponse(verificationService.advanced(verificationData));
    }

    @PostMapping("/advanced")
    public JsonResponse advanced2(@RequestHeader(value = "Authorization", required = false)  String token, @RequestBody VerificationData verificationData) {
        verificationData.setToken(token);
        return new JsonResponse(verificationService.advanced(verificationData));
    }

    @GetMapping("/menu")
    public JsonResponse menuRecord(
            @RequestHeader(value = "Authorization", required = false)  String token, 
            @RequestParam(value = "menuId", required = false) Integer menuId) {
        VerificationData verificationData = new VerificationData();
        verificationData.setMenuId(menuId);
        verificationData.setToken(token);
        return new JsonResponse(verificationService.menuRecord(verificationData));
    }

    @PostMapping("/task")
    public JsonResponse task( @RequestParam(value = "taskId", required = false) Integer taskId, 
            @RequestHeader(value = "Authorization", required = false)  String token) {
        VerificationData verificationData = new VerificationData();
        verificationData.setToken(token);
        verificationData.setWorkflowId(taskId);
        return new JsonResponse(verificationService.task(verificationData));
    }

    @PostMapping("/url")
    public JsonResponse resourceByUrl(@RequestHeader(value = "Authorization", required = false)  String token, 
            @RequestParam(value = "uri", required = false) String uri,
            HttpServletRequest request) throws UnsupportedEncodingException {
        
    	String localUri = uri;
    	
        if(uri == null) {
            BehaviorRequestWrapper br = (BehaviorRequestWrapper) request;
            String body = br.getBody();
            Map<String, String> parameterMap = RequestHelper.getMapFromUrlFormat(body);
            localUri = parameterMap.get("uri");
        }
        
        VerificationData verificationData = new VerificationData();
        verificationData.setToken(token);
        verificationData.setUrl(localUri);
        return new JsonResponse(verificationService.byResourceUrl(verificationData));
    }

    @PostMapping("/advancedUrl")
    public JsonResponse advanceResourceByUrl2(@RequestHeader(value = "Authorization", required = false)  String token, 
            @RequestBody VerificationData verificationData) {
        verificationData.setToken(token);
        return new JsonResponse(verificationService.advancedByResourceUrl(verificationData));
    }

    @GetMapping("/advancedUrl")
    public JsonResponse advanceResourceByUrl(@RequestHeader(value = "Authorization", required = false)  String token, 
            @RequestParam(value = "merchant", required = false) String merchant, 
            @RequestParam(value = "ip", required = false) String ip, 
            @RequestParam(value = "params", required = false) String params,
            @RequestParam(value = "uri", required = false) String uri) {
        VerificationData verificationData = new VerificationData();
        verificationData.setToken(token);
        verificationData.setUrl(uri);
        verificationData.setMerchant(merchant);
        verificationData.setToken(token);
        verificationData.setParams(params);
        verificationData.setIp(ip);
        return new JsonResponse(verificationService.advancedByResourceUrl(verificationData));
    }

    @PostMapping("/merchant")
    public JsonResponse merchant(@RequestHeader(value = "Authorization", required = false)  String token, 
            @RequestParam(value = "merchant", required = false) String merchant,
            HttpServletRequest request) throws UnsupportedEncodingException {

    	String localMerchant = merchant;
    	
        if(merchant == null) {
            BehaviorRequestWrapper br = (BehaviorRequestWrapper) request;
            String body = br.getBody();
            Map<String, String> parameterMap = RequestHelper.getMapFromUrlFormat(body);
            localMerchant = parameterMap.get("merchant");
        }
        
        VerificationData verificationData = new VerificationData();
        verificationData.setMerchant(localMerchant);
        verificationData.setToken(token);
        return new JsonResponse(verificationService.merchant(verificationData));
    }
}
