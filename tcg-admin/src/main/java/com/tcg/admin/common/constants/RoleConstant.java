package com.tcg.admin.common.constants;

public enum RoleConstant {
    
    ACTIVE_FLAG_NORMAL(1),
    ACTIVE_FLAG_DELETED(0);
    
    private int activeFlagType;

    RoleConstant(int activeFlagType) {
        this.activeFlagType = activeFlagType;
    }

    public int getActiveFlagType() {
        return activeFlagType;
    }

    public static RoleConstant getByActiveFlagType(int activeFlagType) {
        for(RoleConstant roleActiveFlag : RoleConstant.values()) {
            if(roleActiveFlag.getActiveFlagType() == activeFlagType) {
                return roleActiveFlag;
            }
        }
        
        return null;
    }
}
