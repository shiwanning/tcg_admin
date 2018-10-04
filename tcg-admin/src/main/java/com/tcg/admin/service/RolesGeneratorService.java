package com.tcg.admin.service;

import java.util.List;

import com.tcg.admin.model.Role;
import com.tcg.admin.to.RoleGeneratorTO;

public interface RolesGeneratorService {
	
	RoleGeneratorTO buildPermission(List<Integer> rolesId);

	List<Role> getRolesByUserCategory();
	
}
