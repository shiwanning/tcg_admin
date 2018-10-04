package com.tcg.admin.service;

import com.tcg.admin.model.Category;
import com.tcg.admin.to.QueryCategoriesTO;

import java.util.List;

public interface CategoryService {


	List<Category> queryCategory();

	List<Category> queryCategoryByRoleId(List<Integer> roleIds);

	List<QueryCategoriesTO> queryCategoryAndRoles(Integer categoryId, Integer activeFlag);

	Category createCategory(Category category);

	void updateCategory(Category category);


	void deleteCategory(Integer categoryId);


}
