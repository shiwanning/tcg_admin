package com.tcg.admin.to;

import java.io.Serializable;
import java.util.List;

/**
 * Created by chris.h on 2017/1/25.
 */
public class Roles implements Serializable {

    private static final long serialVersionUID = 8355930389498971266L;

    private List<String> roleIds;

    private List<String> roleNames;

    public List<String> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(List<String> roleIds) {
        this.roleIds = roleIds;
    }

    public List<String> getRoleNames() {
        return roleNames;
    }

    public void setRoleNames(List<String> roleNames) {
        this.roleNames = roleNames;
    }
}
