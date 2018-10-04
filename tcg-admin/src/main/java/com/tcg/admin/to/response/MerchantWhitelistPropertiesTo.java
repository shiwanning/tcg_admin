package com.tcg.admin.to.response;

public class MerchantWhitelistPropertiesTo {
    private String merchantCode;
    private boolean whitelistFunction;
    
    public MerchantWhitelistPropertiesTo() {
    	// Do nothing
    }
    
    public MerchantWhitelistPropertiesTo(String merchantCode, boolean whitelistFunction) {
        this.merchantCode = merchantCode;
        this.whitelistFunction = whitelistFunction;
    }
    
    public String getMerchantCode() {
        return merchantCode;
    }
    public void setMerchantCode(String merchantCode) {
        this.merchantCode = merchantCode;
    }
    public boolean isWhitelistFunction() {
        return whitelistFunction;
    }
    public void setWhitelistFunction(boolean whitelistFunction) {
        this.whitelistFunction = whitelistFunction;
    }
    
    
}
