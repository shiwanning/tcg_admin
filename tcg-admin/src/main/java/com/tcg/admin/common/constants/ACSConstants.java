package com.tcg.admin.common.constants;

import com.google.common.collect.Maps;

import java.util.Map;

public class ACSConstants {

    private static final Map<Integer, String> TX_TYPE_MAP = Maps.newHashMap();
    
    public static final Integer TCG_C_DEPOSIT = 8301;
    public static final Integer TCG_D_WITHDRAW = 8401;
    public static final Integer TCG_D_DEPOSIT_CHARGE = 8302;
    public static final Integer TCG_D_WITHDRAW_FEE = 8402;
    
    public static final Integer WALLET_C_DEPOSIT_MAIN = 8303;
    public static final Integer WALLET_D_DEPOSIT_MAIN = 8403;
    public static final Integer WALLET_D_DEPOSIT_MAIN_CHARGE = 8304;
    
    public static final Integer WALLET_C_DEPOSIT_PLEDGE = 8305;
    public static final Integer WALLET_D_DEPOSIT_PLEDGE = 8405;
    public static final Integer WALLET_D_DEPOSIT_PLEDGE_CHARGE = 8306;

    public static final Integer WALLET_C_DEPOSIT_PRODUCT = 8307;
    public static final Integer WALLET_D_DEPOSIT_PRODUCT = 8407;
    
	public static final int BANK_ACCT_TYPE_ID = 900;

    public static final Integer ACCT_TYPE_MAIN= 2;
    public static final Integer ACCT_TYPE_PLEDGE = 5;
	
	public static final Integer CREDIT = 1;
	public static final Integer DEBIT = 2;
	
    static {

    	TX_TYPE_MAP.put(TCG_C_DEPOSIT, "Merchant Deposit");
        TX_TYPE_MAP.put(TCG_D_WITHDRAW, "TCG Payment");
        TX_TYPE_MAP.put(TCG_D_DEPOSIT_CHARGE, "Merchant Deposit Charges");
        TX_TYPE_MAP.put(TCG_D_WITHDRAW_FEE, "TCG Payment Charges");
        TX_TYPE_MAP.put(WALLET_C_DEPOSIT_MAIN, "Merchant Deposit Wallet Main");
        TX_TYPE_MAP.put(WALLET_D_DEPOSIT_MAIN, "Merchant Deposit Wallet Main Down");
        TX_TYPE_MAP.put(WALLET_D_DEPOSIT_MAIN_CHARGE, "Merchant Deposit Wallet Main Fee");
        TX_TYPE_MAP.put(WALLET_C_DEPOSIT_PLEDGE, "Merchant Deposit Wallet Pledge");
        TX_TYPE_MAP.put(WALLET_D_DEPOSIT_PLEDGE, "Merchant Deposit Wallet Pledge Down");
        TX_TYPE_MAP.put(WALLET_D_DEPOSIT_PLEDGE_CHARGE, "Merchant Deposit Wallet Pledge Fee");

        TX_TYPE_MAP.put(WALLET_C_DEPOSIT_PRODUCT, "Merchant Deposit Wallet Product");
        TX_TYPE_MAP.put(WALLET_D_DEPOSIT_PRODUCT, "Merchant Deposit Wallet Product down");

    }

	private ACSConstants(){
		throw new IllegalStateException("Constant Class");
	}
	
    public static String getTxCode(Integer txTypeId) {
        String code = TX_TYPE_MAP.get(txTypeId);
        if (code == null) {
            return "无";
        }
        return code;
    }

}
