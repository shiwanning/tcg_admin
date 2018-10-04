package com.tcg.admin.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tcg.admin.common.exception.AdminServiceBaseException;
import com.tcg.admin.model.AdminRoles;
import com.tcg.admin.service.AdminRolesService;
import com.tcg.admin.to.response.JsonResponseT;

@RestController
@RequestMapping(value = "/resources/adminroles", produces = MediaType.APPLICATION_JSON_VALUE)
public class AdminRolesResource {
	
	@Autowired
	AdminRolesService adminRolesService;
	
	@GetMapping("/getAll")
	public JsonResponseT<List<AdminRoles>> getAdminRoles() throws AdminServiceBaseException {
		JsonResponseT<List<AdminRoles>> response = new JsonResponseT<>(true);
		response.setValue(adminRolesService.getAll());
		return response;
	}

}
