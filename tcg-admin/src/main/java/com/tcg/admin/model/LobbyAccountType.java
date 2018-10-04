package com.tcg.admin.model;

import java.util.Date;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Cacheable
@Entity
@Table(schema = "TCG_POKERDB", name = "T_LOBBY_ACCOUNT_TYPE")
public class LobbyAccountType
{
    @Id
    @Column(name = "ACCOUNT_TYPE_ID")
    private Integer accountTypeId;

    @Column(name = "ACCOUNT_TYPE_NAME")
    private String accountTypeName;

    @Column(name = "CREATE_TIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

    @Column(name = "UPDATE_TIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime;

    @Column(name = "LEVEL_ID")
    private Integer levelId;

    @Column(name = "FIRM_NAME")
    private String firmName;

    @Column(name = "STATUS")
    private Integer status;

    @Column(name = "IS_TCG")
    private Integer isTcg;

    @Column(name = "HAS_PVP")
    private String hasPvp;

    @Column(name = "HAS_RNG")
    private String hasRng;

    @Column(name = "HAS_LOTT")
    private String hasLott;

    @Column(name = "HAS_LIVE")
    private String hasLive;

    @Column(name = "HAS_FISH")
    private String hasFish;

    @Column(name = "HAS_SPORTS")
    private String hasSports;

    @Column(name = "HAS_ELOTT")
    private String hasElott;

    @Column(name = "SUP_CURRENCY")
    private String supCurrency;

    public Integer getAccountTypeId() {
        return accountTypeId;
    }

    public void setAccountTypeId(Integer accountTypeId) {
        this.accountTypeId = accountTypeId;
    }

    public String getAccountTypeName() {
        return accountTypeName;
    }

    public void setAccountTypeName(String accountTypeName) {
        this.accountTypeName = accountTypeName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getLevelId() {
        return levelId;
    }

    public void setLevelId(Integer levelId) {
        this.levelId = levelId;
    }

    public String getFirmName() {
        return firmName;
    }

    public void setFirmName(String firmName) {
        this.firmName = firmName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getIsTcg() {
        return isTcg;
    }

    public void setIsTcg(Integer isTcg) {
        this.isTcg = isTcg;
    }

    public String getHasPvp() {
        return hasPvp;
    }

    public void setHasPvp(String hasPvp) {
        this.hasPvp = hasPvp;
    }

    public String getHasRng() {
        return hasRng;
    }

    public void setHasRng(String hasRng) {
        this.hasRng = hasRng;
    }

    public String getHasLott() {
        return hasLott;
    }

    public void setHasLott(String hasLott) {
        this.hasLott = hasLott;
    }

    public String getHasLive() {
        return hasLive;
    }

    public void setHasLive(String hasLive) {
        this.hasLive = hasLive;
    }

    public String getHasFish() {
        return hasFish;
    }

    public void setHasFish(String hasFish) {
        this.hasFish = hasFish;
    }

    public String getHasSports() {
        return hasSports;
    }

    public void setHasSports(String hasSports) {
        this.hasSports = hasSports;
    }

    public String getHasElott() {
        return hasElott;
    }

    public void setHasElott(String hasElott) {
        this.hasElott = hasElott;
    }

    public String getSupCurrency() {
        return supCurrency;
    }

    public void setSupCurrency(String supCurrency) {
        this.supCurrency = supCurrency;
    }
}
