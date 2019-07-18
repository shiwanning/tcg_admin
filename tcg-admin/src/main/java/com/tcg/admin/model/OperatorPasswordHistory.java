package com.tcg.admin.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


/**
 * <p>
 * Title: com.tcg.admin.model.OperatorPasswordHistory
 * </p>
 * <p>
 * Description: 管理員修改密碼紀錄
 * </p>
 *
 * @version 1.0
 */
@Entity
@Table(name = "US_OPERATOR_PASSWORD_HISTORY")
public class OperatorPasswordHistory extends BaseEntity {

    private static final long serialVersionUID = 3242284300539334070L;
    /**
     * 流水號
     */
    @Id
    @SequenceGenerator(name = "SEQ_OPERATOR_PASSWORD_HISTORY",
                       sequenceName = "SEQ_OPERATOR_PASSWORD_HISTORY", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_OPERATOR_PASSWORD_HISTORY")
    @Column(name = "SEQ_ID")
    private Integer seqId;

    /**
     * 管理員名稱
     */
    @Column(name = "OPERATOR_NAME")
    private String operatorName;

    /**
     * 變更後的密碼
     */
    @Column(name = "PASSWORD")
    private String password;

    /**
     * 變更後的密碼
     */
    @Column(name = "IS_RESET")
    private Integer isReset;


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public Integer getSeqId() {
        return seqId;
    }

    public void setSeqId(Integer seqId) {
        this.seqId = seqId;
    }

    public Integer getIsReset() {
        return isReset;
    }

    public void setIsReset(Integer isReset) {
        this.isReset = isReset;
    }
}
