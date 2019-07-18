package com.tcg.admin.client;


import com.fasterxml.jackson.databind.JsonNode;
import com.tcg.admin.to.WalletTo;
import com.tcg.admin.to.response.MerchantWalletManageTo;
import com.tcg.admin.utils.HttpUtils;
import com.tcg.admin.utils.JsonUtils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
public class GlsClientService {

    @Value("${gls.service.host}")
    private String glsHost;
    @Value("${gls.console.host}")
    private String glsConsoleHost;

    public List<WalletTo> getWalletInfo() {
        String responseString = HttpUtils.get("http://" + glsHost + "/gcs/gamelobby/wallet/info");
        JsonNode jsonNode = JsonUtils.getJsonNodeFromJson(responseString);
        return JsonUtils.getListFromJsonNode(jsonNode.get("value"),WalletTo.class);
    }

    public List<MerchantWalletManageTo> getMerchantWalletInfo(String merchantCode) {
        Map<String,String> map = new HashMap<>();
        map.put("type", String.valueOf(0));
        map.put("merchantName",merchantCode);
        map.put("page",String.valueOf(1));
        map.put("pageSize",String.valueOf(999));
        map.put("sortBy","accountTypeName");
        map.put("sortOrder","asc");
        String url = "http://" + glsConsoleHost + "/tcg-gcs-console/wallet/list";
        String responseString = HttpUtils.doGet(url, HttpUtils.getNameValuePairs(map));
        JsonNode jsonNode = JsonUtils.getJsonNodeFromJson(responseString);
        return JsonUtils.getListFromJsonNode(jsonNode.get("list"),MerchantWalletManageTo.class);
    }
}
