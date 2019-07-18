package com.tcg.admin.common.constants;

public enum AcsAccountType {

    PVP(1),MAIN(2),RNG(3),LOTT(4), CASH_PLEDGE(5), POINT(9);
    
    private Integer id;
    
    AcsAccountType(Integer id) {
        this.id = id;
    }
    
    public Integer getId() {
        return id;
    }
}
