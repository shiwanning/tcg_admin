package com.tcg.admin.service.impl;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tcg.admin.client.AcsClientService;
import com.tcg.admin.client.GlsClientService;
import com.tcg.admin.client.MisClientService;
import com.tcg.admin.client.OdsClientService;
import com.tcg.admin.common.constants.ACSConstants;
import com.tcg.admin.common.constants.AcsAccountType;
import com.tcg.admin.common.error.AdminErrorCode;
import com.tcg.admin.common.exception.AdminServiceBaseException;
import com.tcg.admin.common.helper.RequestHelper;
import com.tcg.admin.model.Merchant;
import com.tcg.admin.persistence.springdata.IMerchantRepository;
import com.tcg.admin.service.MerchantChargeService;
import com.tcg.admin.to.AccountInfo;
import com.tcg.admin.to.FileInfoTo;
import com.tcg.admin.to.MerchantProductTo;
import com.tcg.admin.utils.OrderNoUtil;

@Service
public class MerchantChargeServiceImpl implements MerchantChargeService {

	private static final Logger LOGGER = LoggerFactory.getLogger(MerchantChargeServiceImpl.class);
	
	@Autowired
    private IMerchantRepository merchantRepository;
	@Autowired
    private AcsClientService acsClientService;
    @Autowired
    private GlsClientService glsClientService;
    @Autowired
    private OdsClientService odsClientService;
    @Autowired
    private MisClientService misClientService;
	
	@Override
	public MerchantProductTo getProductInfo(String merchantCode) {
		return misClientService.getProductInfo(merchantCode);
	}

	@Override
	public boolean updateMerchantAccountAmount(String merchantCode, BigDecimal amount, Boolean isCashPledge,
			String remark, FileInfoTo file) {
		String operatorName = RequestHelper.getCurrentUser() == null ? "unknow" : RequestHelper.getCurrentUser().getUser().getOperatorName();

        Merchant merchant = merchantRepository.findMerchantByCode(merchantCode);
        Integer transactionCode = ACSConstants.WALLET_C_DEPOSIT_MAIN;
        
        AccountInfo mainWallet = getDepositAccount(merchant.getCustomerId(), AcsAccountType.MAIN.getId());
        String orderNo = OrderNoUtil.getOrderNo(OrderNoUtil.OrderTypeEnum.TCG);

        //主钱包加钱
        Boolean isMainSuccess = acsClientService.lodgeCreditTransaction(merchant.getCustomerId(), mainWallet.getAccountId().intValue()
                , amount, transactionCode , remark, orderNo);

        if(isMainSuccess && !isCashPledge){
            updateMisRechargeDetail(merchant.getMerchantCode(), orderNo, AcsAccountType.MAIN.getId(), amount, operatorName, file, remark);
            return true;
        } else if(isMainSuccess){

            AccountInfo pledgeWallet = getDepositAccount(merchant.getCustomerId(), AcsAccountType.CASH_PLEDGE.getId());
            //主钱包扣钱
            transactionCode = ACSConstants.WALLET_D_DEPOSIT_MAIN;
            if(acsClientService.lodgeDebitTransaction(merchant.getCustomerId(), mainWallet.getAccountId().intValue()
                    , amount, transactionCode , remark, orderNo)){
                //押金钱包加钱
                transactionCode = ACSConstants.WALLET_C_DEPOSIT_PLEDGE;

                if(acsClientService.lodgeCreditTransaction(merchant.getCustomerId(), pledgeWallet.getAccountId().intValue()
                        , amount, transactionCode , remark, orderNo)){

                    updateMisRechargeDetail(merchant.getMerchantCode(), orderNo, AcsAccountType.CASH_PLEDGE.getId(), amount, operatorName, null, remark);
                    return true;
                }
            }
        }

        return false;
	}

	@Override
	public boolean cashoutMerchantAccountAmount(String merchantCode, BigDecimal amount, Boolean isCashPledge,
			String remark) {
		Merchant merchant = merchantRepository.findMerchantByCode(merchantCode);
        
        AccountInfo mainWallet = getDepositAccount(merchant.getCustomerId(), AcsAccountType.MAIN.getId());
        
        String orderNo = OrderNoUtil.getOrderNo(OrderNoUtil.OrderTypeEnum.TCG);
        
        if(mainWallet.getBalance().compareTo(amount) < 0) {
        	throw new AdminServiceBaseException(AdminErrorCode.AMOUNT_INVALID, "Balance not enough.");
        }
        
        //主钱包扣钱
        if(!isCashPledge) {
        	return acsClientService.lodgeDebitTransaction(merchant.getCustomerId(), mainWallet.getAccountId().intValue()
                    , amount, ACSConstants.WALLET_D_DEPOSIT_MAIN , remark, orderNo);
        }
        
        AccountInfo cashPledgeWallet = getDepositAccount(merchant.getCustomerId(), AcsAccountType.CASH_PLEDGE.getId());
        
        // 押金钱包扣前后主钱包加钱
        Boolean isCashPledgeSuccess = acsClientService.lodgeDebitTransaction(merchant.getCustomerId(), cashPledgeWallet.getAccountId().intValue()
                , amount, ACSConstants.WALLET_D_DEPOSIT_PLEDGE , remark, orderNo);
        
        if(isCashPledgeSuccess) {
        	return acsClientService.lodgeCreditTransaction(merchant.getCustomerId(), mainWallet.getAccountId().intValue()
                    , amount, ACSConstants.WALLET_C_DEPOSIT_MAIN , remark, orderNo);

        }
        
        return false;
	}
	
	private AccountInfo getDepositAccount(String customerId, Integer accountType){
        AccountInfo depositAccount = acsClientService.getCustomerAccountInfo(customerId, accountType);
        //新增主钱包
        if(depositAccount == null) {
            if (acsClientService.createCustomerAccountInfo(customerId, accountType)) {
                depositAccount = acsClientService.getCustomerAccountInfo(customerId, accountType);
            }else{
                throw new AdminServiceBaseException(AdminErrorCode.UNKNOWN_ERROR);
            }
        }
        return depositAccount;
    }
	
	private void updateMisRechargeDetail(String merchantCode, String orderNo, Integer accountType, BigDecimal amount, String operator, FileInfoTo file, String remark) {
    	try {
    		String fileUrl = file == null ? null : file.getFileUrl();
    		misClientService.updateMisRechargeDetail(merchantCode, orderNo, accountType, amount, operator, fileUrl, remark);
    	} catch(Exception e) {
    		LOGGER.error("mis updateMisRechargeDetail error.", e);
    	}
    }

}
