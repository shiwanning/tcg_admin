
package com.tcg.admin.controller.merchant;

import java.io.IOException;
import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.tcg.admin.common.error.AdminErrorCode;
import com.tcg.admin.common.exception.AdminServiceBaseException;
import com.tcg.admin.service.FileService;
import com.tcg.admin.service.MerchantChargeService;
import com.tcg.admin.to.FileInfoTo;
import com.tcg.admin.to.MerchantProductTo;
import com.tcg.admin.to.response.JsonResponse;
import com.tcg.admin.to.response.JsonResponseT;

/**
 * Created by ian.r on 5/22/2017.
 */

@RestController
@RequestMapping(value = "/resources/merchant/charge", produces = MediaType.APPLICATION_JSON_VALUE)
public class MerchantChargeResource {

    @Autowired
    private MerchantChargeService merchantChargeService;
    
    @Autowired
    private FileService fileService;

    @GetMapping("/merchant/product/info")
    public JsonResponseT<MerchantProductTo> getMerchantTemplateDetailList(
            @RequestParam(value = "merchantCode") String merchantCode) {
        JsonResponseT<MerchantProductTo> response = new JsonResponseT<>(true);
        response.setValue(merchantChargeService.getProductInfo(merchantCode));
        return response;
    }
    
    @PostMapping("/wallet/cashPledgeOrMain")
    public ResponseEntity updateCashPledgeOrMain(
            @RequestParam(value = "merchantCode", required = true) String merchantCode,
            @RequestParam(value = "isCashPledge", required = true) Boolean isCashPledge,
            @RequestParam(value = "amount", required = true) BigDecimal amount,
            @RequestParam(value = "remark", required = false) String remark,
            @RequestParam(value = "file", required = false) MultipartFile file) throws IOException {
        JsonResponse response = new JsonResponseT<>(true);
        FileInfoTo fileInfo = null;
        if(file != null) {
        	fileInfo = fileService.uploadFile(file, "mwalletCashPledgeOrMain");
        }
        
        if(merchantChargeService.updateMerchantAccountAmount(merchantCode, amount, isCashPledge, remark, fileInfo)){
            return ResponseEntity.ok().body(response);
        }else{
        	if(fileInfo != null) {
        		fileService.deleteFromUrl(fileInfo.getFileUrl());
        	}
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/wallet/cashoutPledgeOrMain")
    public JsonResponse cashoutPledgeOrMain(
            @RequestParam(value = "merchantCode", required = true) String merchantCode,
            @RequestParam(value = "isCashPledge", required = true) Boolean isCashPledge,
            @RequestParam(value = "amount", required = true) BigDecimal amount,
            @RequestParam(value = "remark", required = false) String remark) {
        if(amount.signum() != 1 ) {
        	throw new AdminServiceBaseException(AdminErrorCode.AMOUNT_INVALID, "Amount invalid");
        }
        
        merchantChargeService.cashoutMerchantAccountAmount(merchantCode, amount, isCashPledge, remark);
        
        return JsonResponse.OK;
    }
}
