package com.tcg.admin.persistence.springdata;

import com.tcg.admin.model.CategoryRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


/**
 * <p>Title: com.yx.us.persistence.ICategoryRepository</p>
 * <p>Description: role與Category的對應DAO</p>
 * <p>Copyright: Copyright (c) TCG Corp. 2017. All Rights Reserved.</p>
 * <p>Company: TCG SRD Team</p>
 *
 */
public interface ICategoryRoleRepository extends JpaRepository<CategoryRole, Integer> {


    @Query("SELECT a FROM CategoryRole a WHERE categoryId = ?1")
    List<CategoryRole> findCategoryRoleByCategoryId(Integer categoryId);

    /**
     * 取得role屬於那些category
     *
     * @param roleId
     *
     * @return
     */
    @Query("SELECT a FROM CategoryRole a WHERE roleId in ?1")
    List<CategoryRole> findCategoryRoleByRoleId(List<Integer> roleIdList);
    
    @Modifying
    @Query("DELETE FROM CategoryRole x WHERE x.roleId in ?1")
    void deleteByRoleId(List<Integer> roleIdList);

}
