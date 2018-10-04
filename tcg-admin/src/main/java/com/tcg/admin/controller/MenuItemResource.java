package com.tcg.admin.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tcg.admin.model.MenuItem;
import com.tcg.admin.service.MenuItemService;
import com.tcg.admin.service.MerchantService;
import com.tcg.admin.service.RoleMenuPermissionService;
import com.tcg.admin.to.NoneAdminInfo;

@RestController
@RequestMapping(value = "/resources/menuItem", produces = MediaType.APPLICATION_JSON_VALUE)
public class MenuItemResource {

    @Autowired
	private RoleMenuPermissionService roleMenuPermissionService;

    @Autowired
	private MenuItemService menuItemService;

    @Autowired
	private MerchantService merchantService;

	/**
	 * 使用者角色擁有的權限資源
	 */
	@GetMapping("/byToken")
	public List<MenuItem> queryMenuItemsByToken() {
		NoneAdminInfo noneAdminInfo = merchantService.checkAdmin(false);
		List<MenuItem> menuItemList;
		if(noneAdminInfo.isAdmin()){
			menuItemList = menuItemService.queryMenuItems(true);
		}else{
			menuItemList = roleMenuPermissionService.queryMenuItemsByOperatorId(noneAdminInfo.getOperatorId());
		}

		return menuItemList;
	}

	/**
	 * 功能Api
	 */
	@GetMapping("/forSystem")
	public List<MenuItem> queryMenuItems() {
		return menuItemService.queryMenuItems(merchantService.checkAdmin(false).isAdmin());
	}
}
