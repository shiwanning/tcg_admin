package com.tcg.admin.model;

import org.apache.commons.lang.StringUtils;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * <p>
 * Title: com.tcg.admin.model.SystemParameter
 * </p>
 * <p>
 * Description: User Service系統共用參數設定表
 * </p>
 * @version 1.0
 */
@Cacheable(value = true)
@Entity
@Table(name = "US_SYSTEM_PARAM")
public class SystemParameter extends BaseEntity {

    /**
     * 密碼保留天數
     */
    @Transient
    public static final String MEMBER_PASSWORD_KEEP_DAYS = "PASSWORD_KEEP_DAYS";
    /**
     * 最大代理層級
     */
    @Transient
    public static final String MEMBER_MAX_AGENT_LEVEL = "MAX_AGENT_LEVEL";
    private static final long serialVersionUID = -349517222146020489L;
    /**
     * 列表名稱
     */
    @Id
    @Column(name = "PARAM_NAME")
    private String paramName;

    /**
     * 列表值
     */
    @Column(name = "PARAM_VALUE")
    private String paramValue;

    /**
     * 列表說明
     */
    @Column(name = "DESCRIPTION")
    private String description;

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getParamValue() {
        return paramValue;
    }

    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((paramName == null) ? 0 : paramName.hashCode());
        result = prime * result + ((paramValue == null) ? 0 : paramValue.hashCode());
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((getVersion() == null) ? 0 : getVersion().hashCode());
        return result;
    }


    @Override
    public boolean equals(Object obj) {
        return (obj != null && obj instanceof SystemParameter && this.toString().equals(obj.toString()));
    }


    @Override
    public String toString() {
        String str = "";

        str += StringUtils.defaultIfEmpty(paramName, "");
        str += StringUtils.defaultIfEmpty(paramValue, "");
        str += StringUtils.defaultIfEmpty(description, "");
        str += StringUtils.defaultIfEmpty("" + getVersion(), "");

        return str;
    }
}
