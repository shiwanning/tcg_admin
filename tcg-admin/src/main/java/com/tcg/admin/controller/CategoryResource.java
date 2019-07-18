package com.tcg.admin.controller;

import java.util.List;

import org.springframework.http.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tcg.admin.common.exception.AdminServiceBaseException;
import com.tcg.admin.common.helper.RequestHelper;
import com.tcg.admin.model.Category;
import com.tcg.admin.model.CategoryRole;
import com.tcg.admin.model.Operator;
import com.tcg.admin.service.CategoryRoleService;
import com.tcg.admin.service.CategoryService;
import com.tcg.admin.service.impl.OperatorLoginService;
import com.tcg.admin.to.CategoryRoleCreateTO;
import com.tcg.admin.to.QueryCategoriesTO;
import com.tcg.admin.to.UserInfo;
import com.tcg.admin.to.response.JsonResponseT;

@RestController
@RequestMapping(value = "/resources/categories", produces = MediaType.APPLICATION_JSON_VALUE)
public class CategoryResource {

    @Autowired
	private CategoryService categoryService;
    @Autowired
	private OperatorLoginService operatorLoginService;
    @Autowired
	private CategoryRoleService categoryroleService;

    @GetMapping
	public JsonResponseT<List<Category>> queryCategory() throws AdminServiceBaseException {
		JsonResponseT<List<Category>> response = new JsonResponseT<>(true);
		response.setValue(categoryService.queryCategory());
		return response;
	}

    @GetMapping("/activeFlag/{activeFlag}/categoryId/{categoryId}")
	public JsonResponseT<List<QueryCategoriesTO>> queryCategory(
	        @PathVariable("activeFlag") Integer activeFlag,
	        @PathVariable("categoryId") Integer categoryId) throws AdminServiceBaseException {
		JsonResponseT<List<QueryCategoriesTO>> response = new JsonResponseT<>(true);
		response.setValue(categoryService.queryCategoryAndRoles(categoryId,activeFlag));
		return response;
	}

    @PostMapping("/createAnnouncement")
	public JsonResponseT<Category> createCategory(@RequestBody Category category) throws AdminServiceBaseException {
		JsonResponseT<Category> response = new JsonResponseT<>(true);
		UserInfo<Operator> userInfo = RequestHelper.getCurrentUser();
		String operator = userInfo.getUser().getOperatorId().toString();
		category.setCreateOperator(operator);
		category.setUpdateOperator(operator);
		response.setValue(categoryService.createCategory(category));
		return response;
	}

	@PutMapping
	public JsonResponseT<Boolean> updateCategory(@RequestBody Category category) throws AdminServiceBaseException {
		UserInfo<Operator> userInfo = RequestHelper.getCurrentUser();
		String operator = userInfo.getUser().getOperatorId().toString();
		category.setUpdateOperator(operator);
		categoryService.updateCategory(category);
		return new JsonResponseT<>(true);
	}

	@DeleteMapping("/categoryId/{categoryId}")
	public JsonResponseT<Boolean> deleteCategory(@PathVariable("categoryId") Integer categoryId) throws AdminServiceBaseException {
		categoryService.deleteCategory(categoryId);
		return new JsonResponseT<>(true);
	}

	@PostMapping("/categoryRole/")
	public JsonResponseT<CategoryRole> correlateCategory(@RequestBody CategoryRoleCreateTO categoryRole) throws AdminServiceBaseException {
		JsonResponseT<CategoryRole> response = new JsonResponseT<>(true);
		UserInfo<Operator> userInfo = RequestHelper.getCurrentUser();
		String updateOperator = userInfo.getUser().getOperatorId().toString();
		categoryroleService.correlateCategory(categoryRole.getCategoryIdList(), categoryRole.getRoleList(),updateOperator);
		return response;
	}
}
