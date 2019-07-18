package com.tcg.admin.client;


import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import com.tcg.admin.to.MerchantWalletDepositTo;
import com.tcg.admin.utils.DateTools;
import com.tcg.admin.utils.HttpUtils;
import com.tcg.admin.utils.JsonUtils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: gls client <br/>
 *
 * @author Eddie
 */
@Service
@Transactional
public class OdsClientService {

    @Value("${ods-be.service.address}")
    private String odsHost;

    public List<MerchantWalletDepositTo> getWalletDepositTotal(String merchantCode, Date startDate, Date endDate) {
        Map<String,String> map = new HashMap<>();
        map.put("merchantCode",merchantCode);
        map.put("startDate", DateTools.format(startDate, DateTools.YYYY_MM_DD_HH_MM_SS));
        map.put("endDate", DateTools.format(endDate, DateTools.YYYY_MM_DD_HH_MM_SS));

        String responseString = HttpUtils.doGet("http://" + odsHost + "/ods-be/v2/merchant/wallet/deposit-total",HttpUtils.getNameValuePairs(map));
        JsonNode jsonNode = JsonUtils.getJsonNodeFromJson(responseString);
        if(jsonNode != null && jsonNode.get("success").asBoolean()){
            return JsonUtils.getListFromJsonNode(jsonNode.get("value"),MerchantWalletDepositTo.class);
        }else {
            return Lists.newLinkedList();
        }
    }
}
