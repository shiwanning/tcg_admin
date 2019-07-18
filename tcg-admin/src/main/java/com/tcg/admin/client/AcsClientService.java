package com.tcg.admin.client;


import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import com.tcg.admin.common.constants.ACSConstants;
import com.tcg.admin.to.AccountInfo;
import com.tcg.admin.to.response.AcsBaseResponse;
import com.tcg.admin.utils.HttpUtils;
import com.tcg.admin.utils.JsonUtils;
import com.yx.acs.client.ACServiceFactory;
import com.yx.acs.model.AcsAccountTotalCredit;
import com.yx.acs.service.AccountChangeService;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: acs client <br/>
 *
 * @author Eddie
 */
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@Service
public class AcsClientService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AcsClientService.class);

    @Value("${acs.service.host}")
    private String acsHost;
    @Value("${acs.service.address}")
    private String basePath;

    private final ACServiceFactory factory = ACServiceFactory.getInstance();
    private AccountChangeService service;

    private AccountChangeService getAccountChangeService() {
        if (service == null) {
            service = factory.createAccountChangeService();
        }
        return service;
    }

    public AccountInfo getCustomerAccountInfo(String customerId, Integer accountTypeId) {
        String apiUrl = "/accounts/customer/%s/accountType/%d";
        String responseString = HttpUtils.get("http://" + acsHost + "/ac-service-service/resources" + String.format(apiUrl,customerId,accountTypeId));
        JsonNode responseNode = JsonUtils.getJsonNodeFromJson(responseString);
        if(responseNode.get("success") != null && StringUtils.equals("false" , responseNode.get("success").asText())){
            return null;
        }else{
            return JsonUtils.fromJson(responseString,AccountInfo.class);
        }
    }

    public List<AccountInfo> getCustomerAccountList(String customerId) {
        String apiUrl = "/accounts/customer/%s";
        String responseString = HttpUtils.get("http://" + acsHost + "/ac-service-service/resources" + String.format(apiUrl,customerId));
        JsonNode responseNode = JsonUtils.getJsonNodeFromJson(responseString);
        if(responseNode != null && responseNode.get("success") != null && StringUtils.equals("false",responseNode.get("success").asText())){
            return Lists.newLinkedList();
        }else {
            return JsonUtils.getListFromJsonNode(responseNode,AccountInfo.class);
        }
    }

    public Boolean createCustomerAccountInfo(String customerId, Integer accountTypeId) {
        String apiUrl = "/accounts/customer/%s/accountType/%d";
        StringBuilder responseString = HttpUtils.postJson("http://" + acsHost + "/ac-service-service/resources" + String.format(apiUrl,customerId,accountTypeId),new HashMap<String, String>());
        JsonNode response = JsonUtils.getJsonNodeFromJson(responseString.toString());
        return response.get("success").asBoolean();
    }


    public boolean lodgeDebitTransaction(String customerId, Integer accountId, BigDecimal amount, int transTypeId,
                                         String remarks, String orderNo) {

        try {
            String url = "http://" + acsHost + basePath + "/transactions/debit";

            Map<String,String> map = new HashMap<>();
            map.put("customerId",customerId);
            map.put("accountId",accountId.toString());
            map.put("amount",amount.abs().negate().toString());
            map.put("debit",ACSConstants.DEBIT.toString());
            map.put("txTypeId",String.valueOf(transTypeId));
            map.put("remark",remarks);
            map.put("orderNo", orderNo);
            map.put("bookTxId",String.valueOf(0));

            StringBuilder responseBuilder = HttpUtils.postJson(url, map);
            AcsBaseResponse response = JsonUtils.fromJson(responseBuilder.toString(),AcsBaseResponse.class);

            if(response == null) {
                LOGGER.error("Acs LodgeDebitTransaction fail, reponse is null.");
                return false;
            } else if(!response.getSuccess()) {
                LOGGER.error("Acs LodgeDebitTransaction fail, errorCode: " + response.getErrorCode() + ", message:" + response.getMessage());
                return false;
            }

            return true;
        } catch(Exception e) {
            LOGGER.error("Acs lodgeDebitTransaction fail.", e);
            return false;
        }
    }

    public boolean lodgeCreditTransaction(String customerId, Integer accountId, BigDecimal amount, int transTypeId,
                                          String remarks, String orderNo) {
        try {
            String url = "http://" + acsHost + basePath + "/transactions/credit";

            Map<String,String> map = new HashMap<>();
            map.put("customerId",customerId);
            map.put("accountId",accountId.toString());
            map.put("amount",amount.toString());
            map.put("debit",ACSConstants.CREDIT.toString());
            map.put("txTypeId",String.valueOf(transTypeId));
            map.put("remark",remarks);
            map.put("orderNo", orderNo);
            map.put("bookTxId",String.valueOf(0));

            StringBuilder responseBuilder = HttpUtils.postJson(url, map);
            AcsBaseResponse response = JsonUtils.fromJson(responseBuilder.toString(),AcsBaseResponse.class);

            if(response == null) {
                LOGGER.error("Acs LodgeCreditTransaction fail, reponse is null.");
                return false;
            }
            if(!response.getSuccess()) {
                LOGGER.error("Acs LodgeCreditTransaction fail, errorCode: " + response.getErrorCode() + ", message:" + response.getMessage());
                return false;
            }

            return true;
        } catch(Exception e) {
            LOGGER.error("Acs lodgeCreditTransaction fail.", e);
            return false;
        }

    }

//    public Boolean updateCustomerAccountAmount(String customerId, Long accountId, String orderNo, Long amount,Integer transactionCode,String remark) {
//
//    	Map<String,String> map = new HashMap<>();
//
//    	map.put("customerId",customerId);
//        map.put("accountId",accountId.toString());
//        map.put("amount",amount.toString());
//        map.put("debit","0");
//        map.put("txTypeId",transactionCode.toString());
//        map.put("remark",remark);
//        map.put("orderNo", orderNo);
//
//        StringBuilder responseBuilder = HttpUtils.postJson("http://" + acsHost + "/ac-service-service/resources/transactions/credit",map);
//        JsonNode response = JsonUtils.getJsonNodeFromJson(responseBuilder.toString());
//        return response.get("success").asBoolean();
//    }

    public void addorupdateTotalCreditLimit(Integer customerId, BigDecimal totalCreditLimit) {
        getAccountChangeService().setAccountTotalCredit(customerId,totalCreditLimit);
    }

    public BigDecimal getTotalCreditLimit(Integer customerId) {
        AcsAccountTotalCredit accountTotalCredit = getAccountChangeService().getAccountTotalCredit(customerId);
        if(accountTotalCredit != null){
            return accountTotalCredit.getCreditLimit();
        }else{
            return BigDecimal.ZERO;
        }
    }

    public void updateCreditLimit(Integer accountId , BigDecimal creditLimit) {
        getAccountChangeService().updateCreditLimit(accountId,creditLimit);
    }


}
