package com.tcg.admin.model;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


/**
 * <p>Title: com.tcg.admin.model.CustomerGroupMapper</p>
 * <p>Description: 角色與管理員對應資料表</p>
 * @version 1.0
 */
@Cacheable(value = true)
@Entity
@Table(name = "US_ROLE_OPERATOR")
public class RoleOperator extends BaseEntity {

	/**
	 * <code>serialVersionUID</code> 的註解
	 */
	private static final long serialVersionUID = 4203636442907026397L;

	/** 主鍵 */
	@Id
	@SequenceGenerator(name = "seq_role_operator", sequenceName = "SEQ_ROLE_OPERATOR", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_role_operator")
	@Column(name = "SEQ_ID")
	private Integer seqId;
	
	/** 外部鍵(角色) */
	@Column(name = "ROLE_ID")
	private Integer roleId;

	
	/** 外部鍵(管理員) */
	@Column(name = "OPERATOR_ID")
	private Integer operatorId;

	public Integer getRoleId() {
		return roleId;
	}

	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}

	public Integer getOperatorId() {
		return operatorId;
	}

	public void setOperatorId(Integer operatorId) {
		this.operatorId = operatorId;
	}

	public Integer getSeqId() {
		return seqId;
	}

	public void setSeqId(Integer seqId) {
		this.seqId = seqId;
	}
}
