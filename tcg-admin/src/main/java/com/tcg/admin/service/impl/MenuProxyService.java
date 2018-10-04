package com.tcg.admin.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.tcg.admin.model.MenuItem;
import com.tcg.admin.persistence.springdata.IMenuItemRepository;
import com.tcg.admin.service.CommonMenuService;


/**
 * <p>Title: com.yx.us.service.MenuProxyService</p>
 * <p>Description: Menu cache proxy service</p>
 * <p>Copyright: Copyright (c) UniStar Corp. 2014. All Rights Reserved.</p>
 * <p>Company: UnitStar DEV Team</p>
 *
 * @author Marc
 * @version 1.0
 */
@Service
@Transactional
public class MenuProxyService implements CommonMenuService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MenuProxyService.class);

    @Autowired
    private IMenuItemRepository menuRepository;

    private List<MenuItem> allMenuList = Lists.newLinkedList();

    private Map<Integer, MenuItem> allMenuMap = new ConcurrentHashMap<>();

    private Map<Integer, List<MenuItem>> wholeMenuTree = new ConcurrentHashMap<>();

    @Override
    public  List<MenuItem> getMenuList() {
        //初始化
        synchronized (this) {
            if (allMenuList.isEmpty()) {
                refresh();
            }

            return this.allMenuList;
        }
    }

    @Override
    public Map<Integer, MenuItem> getMenuMap() {
        if(allMenuMap.isEmpty()) {
        getMenuList();
        }
        return this.allMenuMap;
    }

    @Override
    public Map<Integer, List<MenuItem>> getWholeMenuTree() {
        if(wholeMenuTree.isEmpty()) {
        getMenuList();
        }
        return this.wholeMenuTree;
    }

    @Override
    public synchronized void refresh() {
    	LOGGER.info("refresh wholeMenuTree");
        allMenuList.clear();
        load();//讀所有MENU
        allMenuMap.clear();
        buildMenuMap();//組MENU的MAP
        wholeMenuTree.clear();
        buildMenuTree();//組完整的目錄樹
    }

    @Override
    public synchronized void addMenu(MenuItem menu) {
        menuRepository.saveAndFlush(menu);
        refresh();
    }

    @Override
    public synchronized void updateMenu(MenuItem menu) {
        menuRepository.saveAndFlush(menu);
        refresh();
    }

    @Override
    public synchronized void removeMenu(MenuItem menu) {
        menuRepository.delete(menu);
        refresh();
    }

    /**
     * 讀取所有選單
     */
    private void load() {
        LOGGER.info("===============reload menu!!================");
        this.allMenuList = menuRepository.findAllOrderByLevelAndDisplayOrderDesc();
        LOGGER.info("===============reload success!!================");
    }

    /**
     * 組成所有Menu的map
     */
    private void buildMenuMap() {

        for (MenuItem menu : allMenuList) {
            allMenuMap.put(menu.getMenuId(), menu);
        }
    }

    /**
     * 組成系統完整選單樹
     */
    private void buildMenuTree() {

        //先加入首層目錄
        List<MenuItem> rootMenuList = new ArrayList<MenuItem>();
        for (MenuItem menu : allMenuList) {

            if (menu.getTreeLevel().equals(1)) {
                rootMenuList.add(menu);
            }
        }
        wholeMenuTree.put(0, rootMenuList);

        //逐步加入第二層以後的目錄
        for (MenuItem outterMenu : allMenuList) {

            List<MenuItem> sonMenuList = new ArrayList<MenuItem>();
            for (MenuItem innerMenu : allMenuList) {
                if (innerMenu.getParentId().equals(outterMenu.getMenuId())) {
                    sonMenuList.add(innerMenu);
                }
            }

            if (!sonMenuList.isEmpty()) {
                this.wholeMenuTree.put(outterMenu.getMenuId(), sonMenuList);
            }

        }
    }

}
