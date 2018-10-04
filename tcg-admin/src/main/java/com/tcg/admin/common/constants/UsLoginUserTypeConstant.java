package com.tcg.admin.common.constants;

public enum UsLoginUserTypeConstant {

    CUSTOMER(0), OPERATOR(1), CUSTOMER_AUTO(999);

    private int type;

    UsLoginUserTypeConstant(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
    
    public static UsLoginUserTypeConstant getByType(int type) {
        for (UsLoginUserTypeConstant loginUserType : UsLoginUserTypeConstant.values()) {
            if (loginUserType.getType() == type) {
                return loginUserType;
            }
        }
        return null;
    }

}
