package com.tcg.admin.persistence.springdata;

import com.tcg.admin.model.MenuCategoryMenu;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Set;

public interface IMenuCategoryMenuRepository extends JpaRepository<MenuCategoryMenu, Integer>, CrudRepository<MenuCategoryMenu, Integer> {
    
    @Query("select DISTINCT(menuCategoryMenu.menuId) from MenuCategoryMenu menuCategoryMenu")
    List<Integer> findMenuIdDistinct();
    
	@Query("select menuCategoryMenu.menuId from MenuCategoryMenu menuCategoryMenu where menuCategoryMenu.menuCategoryName in (?1)")
	Set<Integer> findMenuIdByMenuCategoryName(List<String> menuCategoryName);

    @Query("select menuCategoryMenu.menuId from MenuCategoryMenu menuCategoryMenu where menuCategoryMenu.menuCategoryName = (?1)")
    List<Integer> findByMenuCategoryName(String menuCategoryName);

    @Query("select menuCategoryMenu from MenuCategoryMenu menuCategoryMenu where menuCategoryMenu.menuCategoryName = (?1)")
    List<MenuCategoryMenu> findByMenuCategoryNames(String menuCategoryName);

    @Query("select menuCategoryMenu.menuId from MenuCategoryMenu menuCategoryMenu where menuCategoryMenu.menuCategoryName = (?1) and menuCategoryMenu.menuId in (?2)")
    List<Integer> selectMenuByCategoryNameAndMenuIds(String menuCategoryName, List<Integer> menuId);

    @Modifying
    @Query("delete from MenuCategoryMenu menuCategoryMenu where menuCategoryMenu.menuCategoryName = (?1)")
    void deleteMenuCategoryName(String menuCategoryName);

    @Modifying
    @Query("delete from MenuCategoryMenu menuCategoryMenu where menuCategoryMenu.menuCategoryName = (?1) and menuCategoryMenu.menuId in (?2)")
    void deleteMenuByCategoryNameAndMenuIds(String menuCategoryName, List<Integer> menuId);

    @Query("select distinct menuCategoryMenu.menuId from MenuCategoryMenu menuCategoryMenu,MenuItem menuItem where menuCategoryMenu.menuCategoryName !='SYSTEM' and menuItem.menuId=menuCategoryMenu.menuId ")
    List<Integer>  queryByMenuCategoryMenu();

    @Query("select distinct menuCategoryMenu.menuId from MenuCategoryMenu menuCategoryMenu,MenuItem menuItem where menuCategoryMenu.menuCategoryName !='SYSTEM' and menuItem.menuId=menuCategoryMenu.menuId  and menuCategoryMenu.menuCategoryName = (?1)")
    List<Integer> queryMenuItemByCategoryName(String categoryName);

    @Query("select distinct menuCategoryMenu.menuId from MenuCategoryMenu menuCategoryMenu where menuCategoryMenu.menuId NOT IN (?1) and menuCategoryMenu.menuCategoryName != 'SYSTEM' ")
    List<Integer>  getMenuCategoryExcept(List<Integer> menuId);

    @Query("select distinct menuCategoryMenu.menuId from MenuCategoryMenu menuCategoryMenu,MenuItem menuItem where menuCategoryMenu.menuCategoryName !='SYSTEM' and menuItem.menuId=menuCategoryMenu.menuId and menuItem.isButton =1")
    List<Integer>  queryByMenuId();
}