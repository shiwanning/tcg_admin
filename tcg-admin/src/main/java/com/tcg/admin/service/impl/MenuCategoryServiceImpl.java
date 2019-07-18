package com.tcg.admin.service.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.tcg.admin.common.error.AdminErrorCode;
import com.tcg.admin.common.exception.AdminServiceBaseException;
import com.tcg.admin.common.helper.RequestHelper;
import com.tcg.admin.model.MenuCategory;
import com.tcg.admin.model.MenuCategoryMenu;
import com.tcg.admin.model.MenuItem;
import com.tcg.admin.model.Merchant;
import com.tcg.admin.model.MerchantMenuCategorKey;
import com.tcg.admin.model.MerchantMenuCategory;
import com.tcg.admin.model.Operator;
import com.tcg.admin.persistence.MenuCategoryRepositoryCustom;
import com.tcg.admin.persistence.springdata.IMenuCategoryMenuRepository;
import com.tcg.admin.persistence.springdata.IMenuCategoryRepository;
import com.tcg.admin.persistence.springdata.IMenuItemRepository;
import com.tcg.admin.persistence.springdata.IMerchantMenuCategoryRepository;
import com.tcg.admin.persistence.springdata.IMerchantRepository;
import com.tcg.admin.service.CommonMenuService;
import com.tcg.admin.service.MenuCategoryService;
import com.tcg.admin.to.MenuCategoryTO;
import com.tcg.admin.to.UserInfo;

@Service
@Transactional
public class MenuCategoryServiceImpl implements MenuCategoryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MenuCategoryServiceImpl.class);
    
    @Autowired
    private CommonMenuService menuCacher;

    @Autowired
    private MenuCategoryRepositoryCustom menuCategoryRepo;

    @Autowired
    private IMenuCategoryRepository menuCategoryRepository;

    @Autowired
    private IMenuItemRepository menuItemRepository;

    @Autowired
    private IMenuCategoryMenuRepository menuCategoryMenuRepository;

    @Autowired
    private IMerchantRepository merchantRepository;

    @Autowired
    private IMerchantMenuCategoryRepository merchantMenuCategoryRepository;

    @Autowired
    private OperatorLoginService operatorLoginService;

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public Page<MenuCategory> queryMenuCategory(MenuCategoryTO to) {
        return menuCategoryRepo.queryMenuCategory(to , new PageRequest(to.getPageNo(), to.getPageSize()));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public List<MenuCategoryMenu> deleteMenuCategory(MenuCategoryTO to) {
        MenuCategory menuCategory = menuCategoryRepository.findMenuCategoryByMenuCategoryName(to.getCategoryName());
        if(menuCategory != null) {
            menuCategoryRepository.delete(menuCategory);

            /**
             *  transfer (other menuCategory to SYSTEM)
             */
//            List<Integer> list = menuCategoryMenuRepository.findByMenuCategoryName(to.getCategoryName());
            //节点被选后不在SYSTEM中
//            Set<Integer> existList = menuCategoryMenuRepository.selectMenuByCategoryNameAndMenuIds("SYSTEM",list);

            List<MenuCategoryMenu> saveModelList = Lists.newLinkedList();
            
            menuCategoryMenuRepository.deleteMenuCategoryName(to.getCategoryName());

            //品牌关联的权限群组删除
            List<MerchantMenuCategory> deleteList =  merchantMenuCategoryRepository.findMerchantMenuCategoryByMenuCategoryName(to.getCategoryName());
            merchantMenuCategoryRepository.delete(deleteList);

            genAndSaveSystemMenuList();

            return saveModelList;
        }else{
            throw new AdminServiceBaseException(AdminErrorCode.MENU_CATEGORY_NOT_EXIST_ERROR, "Menu Category Not Exist Error");
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveMenuCategoryMenu(List<MenuCategoryMenu> list) {
        menuCategoryMenuRepository.save(list);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void correlatePermission(MenuCategoryTO to) {
        try {
            // 1.每個menuId都要存在__________________________________________________________________
            for (Integer menuId : to.getMenuIdList()) {
                MenuItem menu = menuCacher.getMenuMap().get(menuId);
                if (menu == null) {
                    throw new AdminServiceBaseException(AdminErrorCode.MENU_NOT_EXIST_ERROR, "Can't Find The Corresponding Menu");
                }
            }
            //2.检查menuId（isButton=1）是否已存在_____________拿掉??(权限是可以被多个权限群组所拥有)______________________________________________________________
//            List<Integer> menuIds =menuCategoryMenuRepository.queryByMenuId();
//            for(Integer menuId: to.getMenuIdList()){
//                if(menuIds.contains(menuId))
//                {
//                    throw new AdminServiceBaseException(AdminErrorCode.MENU_CATEGORY_MENU_ALREADY_EXIST, "Permission Already Exist");
//                }
//            }
            //3.创建menu_category___________________________________________________________________________
            MenuCategory category = menuCategoryRepository.findMenuCategoryByMenuCategoryName(to.getCategoryName());

            MenuCategory categoryAfterSave;

            MenuCategory categoryNew = new MenuCategory();
            if(category==null) {
                UserInfo<Operator> userInfo = RequestHelper.getCurrentUser();
                String  operatorName = userInfo.getUser().getOperatorName();
                categoryNew.setCategoryName(to.getCategoryName().toUpperCase());
                categoryNew.setCreateOperator(operatorName);
                categoryAfterSave = menuCategoryRepository.save(categoryNew);
            }else{
                throw new AdminServiceBaseException(AdminErrorCode.MENU_CATEGORY_ALREADY_EXIST, "CategoryName Already Exist");
            }

            // 4.關聯關係___________________________________________________________________________
            String menuCategoryName = categoryAfterSave.getCategoryName();
            List<MenuCategoryMenu> menuCategoryMenus = Lists.newLinkedList();
            for (Integer menu : to.getMenuIdList()) {
                MenuCategoryMenu menuCategoryMenu = new MenuCategoryMenu();
                menuCategoryMenu.setMenuCategoryName(menuCategoryName);
                menuCategoryMenu.setMenuId(menu);
                menuCategoryMenus.add(menuCategoryMenu);
            }

            menuCategoryMenuRepository.save(menuCategoryMenus);

            //?? 拿掉，无论是否节点,如果存在在SYSTEM中都删除
//            List<Integer> deleteList = this.maintainSystem(to.getMenuIdList());
            List<Integer> deleteList = to.getMenuIdList();

            if(CollectionUtils.isNotEmpty(deleteList)){
                menuCategoryMenuRepository.deleteMenuByCategoryNameAndMenuIds("SYSTEM", deleteList);
            }


            //将新添加的权限群组添加到每一个品牌中
            List<Merchant> merchantList = merchantRepository.findAllmerchantExceptCompany();
            List<MerchantMenuCategory> list = new ArrayList<>();
            for (Merchant merchant : merchantList) {
                MerchantMenuCategory model = new MerchantMenuCategory();
                MerchantMenuCategorKey key = new MerchantMenuCategorKey();
                key.setMenuCategoryName(menuCategoryName);
                key.setMerchantCode(merchant.getMerchantCode());
                model.setKey(key);
                list.add(model);
            }
            merchantMenuCategoryRepository.save(list);
        } catch (AdminServiceBaseException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new AdminServiceBaseException(AdminErrorCode.UNKNOWN_ERROR, ex.getMessage(), ex);
        }
    }

    /**
     * EDIT Category Name
     * @param to
     * @throws AdminServiceBaseException
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public List<MenuCategoryMenu> reCorrelatePermission(MenuCategoryTO to) {
            // 1.每個menuId都要存在__________________________________________________________________
            for (Integer menuId : to.getMenuIdList()) {
                MenuItem menu = menuCacher.getMenuMap().get(menuId);
                if (menu == null) {
                    throw new AdminServiceBaseException(AdminErrorCode.MENU_NOT_EXIST_ERROR, "Can't Find The Corresponding Menu");
                }
            }
            //3.创建menu_category___________________________________________________________________________
            MenuCategory category = menuCategoryRepository.findMenuCategoryByMenuCategoryName(to.getCategoryName());

            if(category == null)
            {
                throw new AdminServiceBaseException(AdminErrorCode.MENU_CATEGORY_NOT_EXIST_ERROR, "Menu Category Not Exist Error");
            }

            UserInfo<Operator> userInfo = RequestHelper.getCurrentUser();
            String  operatorName = userInfo.getUser().getOperatorName();
            category.setUpdateOperator(operatorName);
            menuCategoryRepository.save(category);

            // 4.關聯關係___________________________________________________________________________
            String menuCategoryName = to.getCategoryName();
            //DB data
            List<Integer> modelList = menuCategoryMenuRepository.findByMenuCategoryName(menuCategoryName);

            //db撈出來的資料
            List<Integer> dbList = new ArrayList<>();
            dbList.addAll(modelList);

            //UI 來源資料
            List<Integer> uiList = to.getMenuIdList();
            //UI 來源資料 for 新增
            List<Integer> tarGetList = new ArrayList<>();
            tarGetList.addAll(uiList);

            List<MenuCategoryMenu> saveModelList = new ArrayList<>();
            for(Integer i: tarGetList){
                MenuCategoryMenu menuCategoryMenu = new MenuCategoryMenu();
                menuCategoryMenu.setMenuCategoryName(menuCategoryName);
                menuCategoryMenu.setMenuId(i);
                saveModelList.add(menuCategoryMenu);
            }
            if(CollectionUtils.isNotEmpty(dbList)){
                menuCategoryMenuRepository.deleteMenuByCategoryName(menuCategoryName);
            }
            if(CollectionUtils.isNotEmpty(tarGetList)){
                menuCategoryMenuRepository.deleteMenuByCategoryNameAndMenuIds("SYSTEM", tarGetList);
            }

        return saveModelList;
    }

    @Override
    public void reCorrelateRefresh(List<MenuCategoryMenu> menuCategoryMenus) {
        saveMenuCategoryMenu(menuCategoryMenus);
        genAndSaveSystemMenuList();
    }

    private List<Integer> maintainSystem(List<Integer> saveList){
        List<Integer> systemDelete = new ArrayList<>();
        for (Integer menuId : saveList) {
            MenuItem menu = menuCacher.getMenuMap().get(menuId);
            if(menu != null && menu.getIsButton() != null && menu.getIsButton()==1){
                systemDelete.add(menuId);
            }else{
                //todo 如果需要把母節點刪除
            }
        }
        return systemDelete;
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public List<MenuCategoryMenu> genAndSaveSystemMenuList() {
        
        List<MenuCategoryMenu> saveModelList = Lists.newLinkedList();
        
        // 从四月一号开始的新功能
//        Date startDate = null;
//        try {
//            startDate = DateUtils.parseDate("2018-04-01", "yyyy-MM-dd");
//        } catch (ParseException e) {
//            LOGGER.error("parser date error", e);
//        }
        
        List<Integer> existMenuIds = menuCategoryMenuRepository.findMenuIdDistinct();
        //获取menu_item表中所有item和menu_Category_menu对比,刷新
        List<Integer> allButtonMenuIds = menuItemRepository.findButtons();
        
        // 删除存在的 menuId
        allButtonMenuIds.removeAll(existMenuIds);
        
        for(Integer menuId : allButtonMenuIds){
            MenuCategoryMenu menuCategoryMenu = new MenuCategoryMenu();
            menuCategoryMenu.setMenuCategoryName("SYSTEM");
            menuCategoryMenu.setMenuId(menuId);
            saveModelList.add(menuCategoryMenu);
        }
        saveMenuCategoryMenu(saveModelList);
        
//        List<Integer> allNotButtonMenuIds = menuItemRepository.findButtonNewMenuId(startDate, 0);
//        List<Integer> allSystemMenuId = menuCategoryMenuRepository.findByMenuCategoryName("SYSTEM");
//
//        allNotButtonMenuIds.removeAll(allSystemMenuId);
//
//        saveModelList = Lists.newLinkedList();
//
//        for(Integer menuId : allNotButtonMenuIds){
//            MenuCategoryMenu menuCategoryMenu = new MenuCategoryMenu();
//            menuCategoryMenu.setMenuCategoryName("SYSTEM");
//            menuCategoryMenu.setMenuId(menuId);
//            saveModelList.add(menuCategoryMenu);
//        }
//        saveMenuCategoryMenu(saveModelList);
//
        
        LOGGER.info("generate and save system category menu id: {}", StringUtils.join(allButtonMenuIds, ","));
        return saveModelList;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public List<MenuItem> queryMenuItemsByCategory(String categoryName) {
        try {
            List<Integer> menuIdList;
            if (StringUtils.isNotBlank(categoryName)){
                menuIdList = menuCategoryMenuRepository.queryMenuItemByCategoryName(categoryName);
            }else{
                menuIdList = menuCategoryMenuRepository.queryByMenuCategoryMenu();
            }
            List<MenuItem> menuItemList = Lists.newLinkedList();
            for (Integer menuId : menuIdList) {
                MenuItem menu = menuCacher.getMenuMap().get(menuId);
                if ( menu != null){
                    menuItemList.add(menuCacher.getMenuMap().get(menuId));
                }
            }
            return menuItemList;
        } catch (Exception ex) {
            throw new AdminServiceBaseException(AdminErrorCode.UNKNOWN_ERROR, ex.getMessage(), ex);
        }
    }

    @Override
    public List<Merchant> queryAllMerchantByCategoryName(String menuCategoryName) {
        return merchantRepository.findMenuCategoryByMenuCategoryName(menuCategoryName);
    }
}
