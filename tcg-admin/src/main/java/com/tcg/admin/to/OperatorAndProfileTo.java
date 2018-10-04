package com.tcg.admin.to;

import com.tcg.admin.model.Operator;
import com.tcg.admin.model.OperatorProfile;

public class OperatorAndProfileTo {

    private Operator operator;
    private OperatorProfile operatorProfile;
    
    public OperatorAndProfileTo(Operator operator, OperatorProfile operatorProfile) {
        this.operator = operator;
        this.operatorProfile = operatorProfile;
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    public OperatorProfile getOperatorProfile() {
        return operatorProfile;
    }

    public void setOperatorProfile(OperatorProfile operatorProfile) {
        this.operatorProfile = operatorProfile;
    }
    
}
