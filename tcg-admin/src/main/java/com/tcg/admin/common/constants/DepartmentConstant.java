package com.tcg.admin.common.constants;

public enum DepartmentConstant {

    ACTIVE_FLAG_DELETED(0),

    ACTIVE_FLAG_NORMAL(1), ;

    private int activeFlagType;

    DepartmentConstant(int activeFlagType) {
        this.activeFlagType = activeFlagType;
    }

    public int getActiveFlagType() {
        return activeFlagType;
    }
    
    public static DepartmentConstant getByActiveFlagType(int activeFlagType) {
        for(DepartmentConstant departmentActiveFlag : DepartmentConstant.values()) {
            if(departmentActiveFlag.getActiveFlagType() == activeFlagType) {
                return departmentActiveFlag;
            }
        }
        
        return null;
    }
    
}