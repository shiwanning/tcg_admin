package com.tcg.admin.to.response;

import java.io.Serializable;

public class OperatorInfoTo implements Serializable {
    
    private static final long serialVersionUID = -4372924182123426120L;
    
    private Integer operatorId;
    private String operatorName;
    
    public Integer getOperatorId() {
        return operatorId;
    }
    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }
    public String getOperatorName() {
        return operatorName;
    }
    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }
    
}
