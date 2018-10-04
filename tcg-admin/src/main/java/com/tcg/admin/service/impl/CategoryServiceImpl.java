package com.tcg.admin.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tcg.admin.common.constants.IErrorCode;
import com.tcg.admin.common.error.AdminErrorCode;
import com.tcg.admin.common.exception.AdminServiceBaseException;
import com.tcg.admin.model.Category;
import com.tcg.admin.model.CategoryRole;
import com.tcg.admin.persistence.CategoryRepositoryCustom;
import com.tcg.admin.persistence.springdata.ICategoryRepository;
import com.tcg.admin.persistence.springdata.ICategoryRoleRepository;
import com.tcg.admin.service.CategoryService;
import com.tcg.admin.to.QueryCategoriesTO;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Autowired
    private ICategoryRepository categoryRepository;

    @Autowired
    private ICategoryRoleRepository categoryRoleRepository;

    @Autowired
    private CategoryRepositoryCustom categoryRepositoryCustom;


    @Override
    public List<Category> queryCategory() {
        return categoryRepository.findAll();
    }

    @Override
    public List<Category> queryCategoryByRoleId(List<Integer> roleIds) {
        return categoryRepository.queryCategoryAndRoles(roleIds,1);
    }

    @Override
    public List<QueryCategoriesTO> queryCategoryAndRoles(Integer categoryId, Integer activeFlag) {
        List<QueryCategoriesTO> queryList = new ArrayList<>();
        QueryCategoriesTO newQueryCategoriesTO;
        try {
            List<Object[]> dataList = categoryRepositoryCustom.findAll(categoryId, activeFlag);

            for (Object[] obj : dataList) {
                newQueryCategoriesTO = new QueryCategoriesTO();
                newQueryCategoriesTO.setCategoryId(Integer.parseInt(ObjectUtils.toString(obj[0])));
                newQueryCategoriesTO.setCategoryName(ObjectUtils.toString(obj[1]));
                newQueryCategoriesTO.setRoleId(Integer.parseInt(ObjectUtils.toString(obj[2])));
                newQueryCategoriesTO.setRoleName(ObjectUtils.toString(obj[3]));
                newQueryCategoriesTO.setActiveFlag(Integer.parseInt(ObjectUtils.toString(obj[4])));
                queryList.add(newQueryCategoriesTO);
            }
        } catch (Exception ex) {
            throw new AdminServiceBaseException(IErrorCode.UNKNOWN_EXCEPTION, ex.getMessage(), ex);
        }
        return queryList;
    }

    @Override
    public Category createCategory(Category category) {
        return categoryRepository.saveAndFlush(category);
    }

    @Override
    public void updateCategory(Category category) {
        Category model = categoryRepository.findOne(category.getCategoryId());
        model.setCategoryName(category.getCategoryName());
        model.setDescription(category.getDescription());
        model.setUpdateOperator(category.getUpdateOperator());
        categoryRepository.saveAndFlush(model);
    }

    @Override
    public void deleteCategory(Integer categoryId) {
        try {

            // 依照 categoryId 查詢
            Category category = categoryRepository.findOne(categoryId);

            // 如果沒有該 category, 則拋Exception
            if (category == null) {
                throw new AdminServiceBaseException(AdminErrorCode.CATEGORY_NOT_EXIST_ERROR, "category not exists");
            }

            List<CategoryRole> categoryRoleRelation = categoryRoleRepository.findCategoryRoleByCategoryId(categoryId);
            if (!categoryRoleRelation.isEmpty()) {
                throw new AdminServiceBaseException(AdminErrorCode.CATEGORY_MUST_BE_UNCORELATED_WHEN_DELETING_ERROR,
                        "categoryId still has roles");
            }
            categoryRepository.delete(category);

        } catch (AdminServiceBaseException usbe) {
            throw usbe;
        } catch (Exception ex) {
            throw new AdminServiceBaseException(IErrorCode.UNKNOWN_EXCEPTION, ex.getMessage(), ex);
        }
    }

}
