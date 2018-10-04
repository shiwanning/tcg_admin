package com.tcg.admin.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tcg.admin.common.exception.AdminServiceBaseException;
import com.tcg.admin.model.MenuCategoryMenu;
import com.tcg.admin.service.CommonMenuService;
import com.tcg.admin.service.MenuCategoryService;
import com.tcg.admin.to.MenuCategoryTO;
import com.tcg.admin.to.response.JsonResponse;
import com.tcg.admin.to.response.JsonResponseT;

@RestController
@RequestMapping(value = "/resources/menuCategory", produces = MediaType.APPLICATION_JSON_VALUE)
public class MenuCategoryResource {

    @Autowired
	private MenuCategoryService menuCategoryService;

    @Autowired
	private CommonMenuService commonService;


    @GetMapping
	public JsonResponseT<Object> queryMenuCategory(
	        @RequestParam(value = "pageNo", required = false) Integer pageNo,
	        @RequestParam(value = "pageSize", required = false) Integer pageSize,
	        @RequestParam(value = "categoryName", required = false) String categoryName) throws AdminServiceBaseException {
		JsonResponseT<Object> response = new JsonResponseT<>(true);
		MenuCategoryTO to = new MenuCategoryTO();
		to.setCategoryName(categoryName);
		to.setPageNo(pageNo);
		to.setPageSize(pageSize);

		response.setValue(menuCategoryService.queryMenuCategory(to));
		return response;
	}

    @DeleteMapping
	public JsonResponse deleteMenuCategory(MenuCategoryTO to) throws AdminServiceBaseException {
		List<MenuCategoryMenu> returnList = menuCategoryService.deleteMenuCategory(to);
		menuCategoryService.saveMenuCategoryMenu(returnList);
		return new JsonResponse(true);
	}

    @PutMapping("/correlateMenuItem")
    public JsonResponse correlatePermission(@RequestBody MenuCategoryTO to) throws AdminServiceBaseException {
        menuCategoryService.correlatePermission(to);
		commonService.refresh();
        return new JsonResponse(true);
    }

    @PutMapping("/reCorrelateMenuItem")
	public JsonResponse reCorrelatePermission(@RequestBody MenuCategoryTO to) throws AdminServiceBaseException {
		List<MenuCategoryMenu> returnList = menuCategoryService.reCorrelatePermission(to);
		menuCategoryService.saveMenuCategoryMenu(returnList);
		commonService.refresh();
		return new JsonResponse(true);
	}


    @GetMapping("/menu/menuPermission")
    public JsonResponseT<Map<String, Object>> queryMenuItemsByMenuCategory(
            @RequestParam(value = "categoryName", required = false)String categoryName) {
		JsonResponseT<Map<String, Object>> response = new JsonResponseT<>(true);
		Map<String, Object> result = new HashMap<>();
		result.put("hasCategory", menuCategoryService.queryMenuItemsByCategory(null));
		if(StringUtils.isNotBlank(categoryName)){
			result.put("selected", menuCategoryService.queryMenuItemsByCategory(categoryName));
		}

		response.setValue(result);
        return response;
    }


	@GetMapping("/merchantByMenuCategoryName")
	public JsonResponseT<Object> queryAllMerchantByCategoryName(
	        @RequestParam(value = "menuCategoryName", required = false)String menuCategoryName) {
		JsonResponseT<Object> response = new JsonResponseT<>(true);
		response.setValue(menuCategoryService.queryAllMerchantByCategoryName(menuCategoryName));
		return response;
	}

}
