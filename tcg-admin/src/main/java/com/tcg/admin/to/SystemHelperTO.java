package com.tcg.admin.to;

/**
 * Created by chris.h on 12/13/2017.
 */
public class SystemHelperTO {

    private Integer menuId;

    private Integer targetMenuId;

    private Integer requesterId;

    private String requester;

    private String enContent;

    private String cnContent;

    private Integer status;

    private Integer createOperator;

    private Integer updateOperator;

    private String createOperatorName;

    private String updateOperatorName;
    
    private String action;
    
    private Integer taskId;
    
    private String remarks;
    
    private String query;

    public Integer getMenuId() {
        return menuId;
    }

    public void setMenuId(Integer menuId) {
        this.menuId = menuId;
    }

    public Integer getTargetMenuId() {
        return targetMenuId;
    }

    public void setTargetMenuId(Integer targetMenuId) {
        this.targetMenuId = targetMenuId;
    }

    public Integer getRequesterId() {
        return requesterId;
    }

    public void setRequesterId(Integer requesterId) {
        this.requesterId = requesterId;
    }

    public String getRequester() {
        return requester;
    }

    public void setRequester(String requester) {
        this.requester = requester;
    }

    public String getEnContent() {
        return enContent;
    }

    public void setEnContent(String enContent) {
        this.enContent = enContent;
    }

    public String getCnContent() {
        return cnContent;
    }

    public void setCnContent(String cnContent) {
        this.cnContent = cnContent;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getCreateOperator() {
        return createOperator;
    }

    public void setCreateOperator(Integer createOperator) {
        this.createOperator = createOperator;
    }

    public Integer getUpdateOperator() {
        return updateOperator;
    }

    public void setUpdateOperator(Integer updateOperator) {
        this.updateOperator = updateOperator;
    }

    public String getCreateOperatorName() {
        return createOperatorName;
    }

    public void setCreateOperatorName(String createOperatorName) {
        this.createOperatorName = createOperatorName;
    }

    public String getUpdateOperatorName() {
        return updateOperatorName;
    }

    public void setUpdateOperatorName(String updateOperatorName) {
        this.updateOperatorName = updateOperatorName;
    }

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public Integer getTaskId() {
		return taskId;
	}

	public void setTaskId(Integer taskId) {
		this.taskId = taskId;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}
    
	
    
    
}
