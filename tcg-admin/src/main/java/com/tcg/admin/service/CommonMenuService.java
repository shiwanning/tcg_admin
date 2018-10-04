package com.tcg.admin.service;


import java.util.List;
import java.util.Map;

import com.tcg.admin.model.MenuItem;


/**
 * <p>Title: com.yx.us.service.CommonMenuService</p>
 * <p>Description: 選單服務介面</p>
 * <p>Copyright: Copyright (c) UniStar Corp. 2014. All Rights Reserved.</p>
 * <p>Company: UnitStar DEV Team</p>
 * @author Marc
 * @version 1.0
 */
public interface CommonMenuService{
	
	/**
	 * 取得系統所有選單List
	 * @return
	 */
    List<MenuItem> getMenuList();

    /**
	 * 取得系統所有選單Map
	 * @return
	 */
    Map<Integer, MenuItem> getMenuMap();

    /**
	 * 取得系統完整的目錄樹
	 * @return
	 */
    Map<Integer, List<MenuItem>> getWholeMenuTree();

    /**
	 * 新增選單
	 * @return
	 */
    void addMenu(MenuItem menu);

    /**
	 * 更新選單
	 * @return
	 */
    void updateMenu(MenuItem menu);

    /**
	 * 移除選單
	 * @return
	 */
    void removeMenu(MenuItem menu);

    /**
	 * 更新系統所有選單
	 * @return
	 */
    void refresh();

}
