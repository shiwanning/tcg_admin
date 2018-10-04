package com.tcg.admin.service;

import com.tcg.admin.common.exception.AdminServiceBaseException;
import com.tcg.admin.model.MenuCategory;
import com.tcg.admin.model.MenuCategoryMenu;
import com.tcg.admin.model.MenuItem;
import com.tcg.admin.model.Merchant;
import com.tcg.admin.to.MenuCategoryTO;
import org.springframework.data.domain.Page;
import java.util.List;

public interface MenuCategoryService {

	Page<MenuCategory> queryMenuCategory(MenuCategoryTO to) throws AdminServiceBaseException;

    List<MenuCategoryMenu> deleteMenuCategory(MenuCategoryTO to) throws AdminServiceBaseException;

    void saveMenuCategoryMenu(List<MenuCategoryMenu> list) throws AdminServiceBaseException;

    void correlatePermission(MenuCategoryTO to) throws AdminServiceBaseException;

    List<MenuCategoryMenu> reCorrelatePermission(MenuCategoryTO to) throws AdminServiceBaseException;

    List<MenuItem> queryMenuItemsByCategory(String menuCategoryName) throws AdminServiceBaseException;

    List<Merchant> queryAllMerchantByCategoryName(String menuCategoryName) throws AdminServiceBaseException;

    List<MenuCategoryMenu> genAndSaveSystemMenuList();
}
