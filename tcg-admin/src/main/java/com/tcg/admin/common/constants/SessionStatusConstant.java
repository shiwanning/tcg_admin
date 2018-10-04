package com.tcg.admin.common.constants;

public enum SessionStatusConstant {

    /**
     * SESSION_IS_VALID: Session is valid | SESSION_IS_INVALID: Session is
     * invalid
     */
    VALID("SESSION_IS_VALID"), INVALID("SESSION_IS_INVALID");

    private String type;

    SessionStatusConstant(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
    
    @Override
    public String toString() {
        return type;
    }
}
