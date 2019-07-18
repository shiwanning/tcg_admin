package com.tcg.admin.persistence.springdata;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.tcg.admin.model.MenuCategory;

public interface IMenuCategoryRepository extends JpaRepository<MenuCategory, String> {
	
	@Query("select menuCategory from MenuCategory menuCategory where menuCategory.categoryName = (?1)")
	MenuCategory findMenuCategoryByMenuCategoryName(String categoryName);

	@Query("select menuCategory.categoryName from MenuCategory menuCategory where menuCategory.categoryName != 'SYSTEM' ")
	List<String> findAllCategoryName();
	
	List<MenuCategory> findByCategoryNameNot(String categoryName);

}