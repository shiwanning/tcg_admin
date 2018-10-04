package com.tcg.admin.controller;

import java.util.List;

import org.springframework.http.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tcg.admin.model.Role;
import com.tcg.admin.service.RolesGeneratorService;
import com.tcg.admin.to.RoleGeneratorTO;
import com.tcg.admin.to.response.JsonResponseT;

@RestController
@RequestMapping(value = "/resources/rolesGenerator", produces = MediaType.APPLICATION_JSON_VALUE)
public class RolesGeneratorResource {
	
    @Autowired
	private RolesGeneratorService rolesGeneratorService;
	
    @GetMapping("/generate")
	public JsonResponseT<RoleGeneratorTO> generatePemissionsByRoles(@RequestParam(value = "roleIds", required = false) List<Integer> roleIds) {
		JsonResponseT<RoleGeneratorTO> response = new JsonResponseT<>(true);
		response.setValue(rolesGeneratorService.buildPermission(roleIds));
		return response;
	}
	
    @GetMapping("/rolesByCategory")
	public JsonResponseT<List<Role>> getRolesByCategory() {
		JsonResponseT<List<Role>> response = new JsonResponseT<>(true);
		 response.setValue(rolesGeneratorService.getRolesByUserCategory());
		return response;
	}

}
