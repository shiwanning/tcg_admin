package com.tcg.admin.model;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Cacheable(value = true)
@Entity
@Table(name = "US_BPM_GROUP")
public class BpmGroup extends BaseEntity {

  private static final long serialVersionUID = 7830309216895869544L;

  /**
   * 審核功能名稱
   */
  @Id
  @Column(name = "GROUP_NAME")
  private String groupName;

  /**
   * 對應的申請功能ID
   */
  @Column(name = "MENU_ID")
  private Integer menuId;



  public String getGroupName() {
    return groupName;
  }

  public void setGroupName(String groupName) {
    this.groupName = groupName;
  }

  public Integer getMenuId() {
    return menuId;
  }

  public void setMenuId(Integer menuId) {
    this.menuId = menuId;
  }


}
