package com.tcg.admin.model;

import java.io.Serializable;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Cacheable(value = true)
@Entity
@Table(name = "US_BPM_ROLE_GROUP")
@SequenceGenerator(name = "seq_bpm_role_group", sequenceName = "SEQ_BPM_ROLE_GROUP",
    allocationSize = 1)
public class BpmRoleGroup extends BaseEntity implements Serializable {

  private static final long serialVersionUID = 6289238817408112500L;

  /**
   * pKey
   */
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_bpm_role_group")
  @Column(name = "SEQ_ID")
  private Integer seqId;

  /**
   * 審核功能名稱
   */
  @Column(name = "GROUP_NAME")
  private String groupName;


  /**
   * 角色ID
   */
  @Column(name = "ROLE_ID")
  private Integer roleId;

  public Integer getSeqId() {
    return seqId;
  }

  public void setSeqId(Integer seqId) {
    this.seqId = seqId;
  }

  public String getGroupName() {
    return groupName;
  }

  public void setGroupName(String groupName) {
    this.groupName = groupName;
  }


  public Integer getRoleId() {
    return roleId;
  }

  public void setRoleId(Integer roleId) {
    this.roleId = roleId;
  }

}
