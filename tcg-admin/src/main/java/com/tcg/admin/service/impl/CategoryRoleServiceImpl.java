package com.tcg.admin.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.tcg.admin.common.error.AdminErrorCode;
import com.tcg.admin.common.exception.AdminServiceBaseException;
import com.tcg.admin.model.CategoryRole;
import com.tcg.admin.persistence.springdata.ICategoryRepository;
import com.tcg.admin.persistence.springdata.ICategoryRoleRepository;
import com.tcg.admin.persistence.springdata.IRoleRepository;
import com.tcg.admin.service.CategoryRoleService;

@Service
@Transactional
public class CategoryRoleServiceImpl implements CategoryRoleService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CategoryRoleServiceImpl.class);

    @Autowired
    private IRoleRepository roleRepository;

    @Autowired
    private ICategoryRepository categoryRepository;

    @Autowired
    private ICategoryRoleRepository categoryRoleRepository;


    @Override
    public List<CategoryRole> queryCategoryRoleByRoleId(Integer roleId) {
        List<Integer> roleList = Lists.newLinkedList();
        roleList.add(roleId);
        return categoryRoleRepository.findCategoryRoleByRoleId(roleList);
    }


    /**
     * category(1)與Role(多)的關係設定
     * @param categoryId, roleList
     * @param roleList
     * @throws AdminServiceBaseException
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void correlateCategory(List<Integer> categoryId, List<Integer> roleList, String updatOperator) {
        try {
            /* category 是否存在 */
            if (categoryRepository.findOne(categoryId.get(0)) == null) {
                throw new AdminServiceBaseException(AdminErrorCode.CATEGORY_NOT_EXIST_ERROR, "category not found!");
            }

            /* role 是否存在  */
            if(roleList.size() != roleRepository.findAll(roleList).size()){
                throw new AdminServiceBaseException(AdminErrorCode.ROLE_NOT_EXIST_ERROR, "role not found!");
            }

            /* 解除全部Category與role的連結關係 */
            List<CategoryRole> oriCategoryRoleList = categoryRoleRepository.findCategoryRoleByRoleId(roleList);

            if (CollectionUtils.isNotEmpty(oriCategoryRoleList)) {
                categoryRoleRepository.deleteByRoleId(roleList);
            }
            
            if(CollectionUtils.isEmpty(roleList) || CollectionUtils.isEmpty(categoryId)) {
            	return;
            }
            
            /* 重新設定Category與role連結關係 */
            List<CategoryRole> reSetupCategoryRoleList = new ArrayList<>();

            for (Integer roleId : roleList) {
        		for(Integer id: categoryId){
        			 CategoryRole categoryRoleOp;
                     categoryRoleOp = new CategoryRole();
                     categoryRoleOp.setCategoryId(id);
                     categoryRoleOp.setRoleId(roleId);
                     categoryRoleOp.setCreateOperator(updatOperator);
                     categoryRoleOp.setUpdateOperator(updatOperator);
                     reSetupCategoryRoleList.add(categoryRoleOp);
        		}
            }
            categoryRoleRepository.save(reSetupCategoryRoleList);

        } catch (AdminServiceBaseException usbe) {
            throw usbe;
        } catch (Exception e) {
            throw new AdminServiceBaseException(AdminErrorCode.UNKNOWN_ERROR, e.getMessage(), e);
        }
    }
}
