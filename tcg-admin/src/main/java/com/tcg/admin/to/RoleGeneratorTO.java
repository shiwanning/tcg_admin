package com.tcg.admin.to;

import java.util.List;
import java.util.Map;

public class RoleGeneratorTO {
	
	private List<Map<String, Object>> permissions;
	private List<Map<String, Object>> genericVariables;
	
	public List<Map<String, Object>> getPermissions() {
		return permissions;
	}
	public void setPermissions(List<Map<String, Object>> permissions) {
		this.permissions = permissions;
	}
	public List<Map<String, Object>> getGenericVariables() {
		return genericVariables;
	}
	public void setGenericVariables(List<Map<String, Object>> genericVariables) {
		this.genericVariables = genericVariables;
	}
	
	

}
