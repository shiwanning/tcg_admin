package com.tcg.admin.client;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.tcg.admin.to.MerchantChargeTemplateTypeTO;
import com.tcg.admin.to.MerchantProductTo;
import com.tcg.admin.to.client.ChargeTemplateResponseTo;
import com.tcg.admin.to.client.MerchantProductResponseTo;
import com.tcg.admin.utils.HttpUtils;
import com.tcg.admin.utils.JsonUtils;

@Service
@Transactional
public class MisClientService {

	private static final Logger LOGGER = LoggerFactory.getLogger(MisClientService.class);
	
	@Value("${mis.service.host}")
    private String misHost;
	
	@Value("${mis.service.basePath}")
	private String basePath;
	
	private String getMisHttpBasePath() {
		return "http://" + misHost + basePath;
	}
	
	public void updateMisRechargeDetail(String merchantCode, String orderNo, Integer accountType, BigDecimal amount, String operator, String fileUrl, String remark) {
        Map<String,String> map = new HashMap<>();
        map.put("merchantCode",merchantCode);
        map.put("orderNo", orderNo);
        map.put("accountType", String.valueOf(accountType));
        map.put("amount", String.valueOf(amount));
        map.put("operator", operator);
        
        if(fileUrl != null) {
        	map.put("fileUrl", String.valueOf(fileUrl));
        }
        
        if(remark != null) {
        	map.put("remark", String.valueOf(remark));
        }

        HttpUtils.post(getMisHttpBasePath() + "/recharge/detail/update", map);
    }
	
	public List<MerchantChargeTemplateTypeTO> getMerchantTemplateList(String merchantCode) {
		NameValuePair[] nameValuePairs = new NameValuePair[1];
		nameValuePairs[0] = new BasicNameValuePair("merchantCode", merchantCode);

        String json = HttpUtils.doGet(getMisHttpBasePath() + "/merchant/charge/merchant/templates", nameValuePairs);
        
        LOGGER.info("merchant charge response: {}", json);
        
        ChargeTemplateResponseTo baseResponse = JsonUtils.fromJson(json, ChargeTemplateResponseTo.class);
        
        if(!Boolean.TRUE.equals(baseResponse.getSuccess())) {
        	LOGGER.error("MIS /merchant/charge/merchant/templates error: " + baseResponse.getErrorCode());
        	return Lists.newLinkedList();
        }
        
        List<MerchantChargeTemplateTypeTO> results = baseResponse.getValue();
        if(results == null) {
        	results = Lists.newLinkedList();
        }
        
        return results;
	}

	public MerchantProductTo getProductInfo(String merchantCode) {
		NameValuePair[] nameValuePairs = new NameValuePair[1];
		nameValuePairs[0] = new BasicNameValuePair("merchantCode", merchantCode);
		String json = HttpUtils.doGet(getMisHttpBasePath() + "/merchant/charge/merchant/product/info", nameValuePairs);
		MerchantProductResponseTo baseResponse = JsonUtils.fromJson(json, MerchantProductResponseTo.class);

		return baseResponse.getValue();
	}
	
	
}
