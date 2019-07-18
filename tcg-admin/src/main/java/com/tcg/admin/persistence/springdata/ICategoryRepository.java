package com.tcg.admin.persistence.springdata;


import com.tcg.admin.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;


/**
 * <p>Title: com.yx.us.persistence.ICategoryRepository</p>
 * <p>Description: Category的對應DAO</p>
 * <p>Copyright: Copyright (c) TCG Corp. 2017. All Rights Reserved.</p>
 * <p>Company: TCG SRD Team</p>
 *
 */
public interface ICategoryRepository extends JpaRepository<Category, Integer> {

    @Query("SELECT a FROM Category a where 1=1 and a.categoryId = ?1 ")
    List<Category> queryCategoryAndRoles(Integer categoryId);

    @Query("SELECT a FROM Category a, CategoryRole b, Role r where 1=1 and a.categoryId = b.categoryId and r.roleId = b.roleId and b.roleId in ?1 and r.activeFlag = ?2 ")
    List<Category> queryCategoryAndRoles(List<Integer> categoryId, Integer activeFlag);


    @Query("SELECT a FROM Category a where 1=1 order by a.categoryName")
    List<Category> findAllOrderByCatregoryName();
}
