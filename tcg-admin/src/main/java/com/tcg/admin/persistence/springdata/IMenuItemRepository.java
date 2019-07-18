package com.tcg.admin.persistence.springdata;


import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.tcg.admin.model.MenuItem;


/**
 * <p>Title: com.yx.us.persistence.IMenuItemRepository</p>
 * <p>Description: MenuItem選單基本操作DAO</p>
 * <p>Copyright: Copyright (c) UniStar Corp. 2014. All Rights Reserved.</p>
 * <p>Company: UnitStar DEV Team</p>
 *
 * @author Marc
 * @version 1.0
 */
public interface IMenuItemRepository extends JpaRepository<MenuItem, Integer> {

    MenuItem findByMenuName(String menuName);

    @Query("SELECT m FROM MenuItem m where m.url = ?1 ")
    MenuItem findByUrl(String url);
    
    @Query("SELECT m FROM MenuItem m where m.url = ?1 ")
    List<MenuItem> findByUrlList(String url);

    @Query("SELECT m FROM MenuItem m where m.url like ?1 and m.accessType = ?2 ")
    List<MenuItem> findByBlurUrl(String likeUrl, String accessType);

    @Query("SELECT m FROM MenuItem m where m.url like ?1 and m.accessType = ?2 and m.treeLevel > 2 and m.menuType in (1,0) ")
    MenuItem findMenuItemForBehaviorLogByBlurUrl(String likeUrl, String accessType);

    @Query("SELECT m FROM MenuItem m where m.accessType <> 0 and m.menuType = 0 or m.accessType = 9 order by m.menuId")
    List<MenuItem> queryMenuItemsForSystem();

    @Query("SELECT m.menuId FROM MenuItem m where m.accessType <> 0 and m.menuType = 0 or m.accessType = 9 order by m.menuId")
    List<Integer> queryMenuIdsForSystem();

    @Query("SELECT m.menuId FROM MenuItem m where m.accessType <> 0 order by m.menuId")
    List<Integer> queryMenuIdsForAdmin();

    @Query("SELECT m.menuId FROM MenuItem m where m.menuId = ?1 or m.parentId = ?1")
    List<Integer> queryMenuIdsByParentId(Integer menuId);

    @Query("SELECT m FROM MenuItem m where m.accessType <> 0 order by m.menuId")
    List<MenuItem> queryMenuItemsForAdmin();

    /**
     * 取出所有的menuItem並先後依照level與顯示順序排序
     *
     * @return
     */
    @Query("SELECT m FROM MenuItem m ORDER BY m.treeLevel, m.displayOrder")
    List<MenuItem> findAllOrderByLevelAndDisplayOrderDesc();

    @Modifying
    @Query("delete from MenuItem m where m.menuName =?1")
    void deleteByMenuName(String menuName);

    @Modifying
    @Query("delete from MenuItem m where m.menuId =?1")
    void deleteByMenuId(Integer menuId);

    @Query("SELECT distinct m.menuId from MenuItem m, ApiLabel b where m.menuId = b.id and b.languageCode = (?2) and LOWER(b.label) like LOWER(?1) ")
    List<Integer> querByMenuName(String menuNameLike, String language);
    
    @Query("SELECT m.menuId FROM MenuItem m where m.menuId NOT IN (?1)")
    List<Integer> queryMenuItemOfBaseMerchant(List<Integer> menuHasServiceType);

    @Query("SELECT m FROM MenuItem m where m.menuId IN (?1)")
    List<MenuItem> findMenuItemByMenuIds(List<Integer> menuIdList);
    
    @Query("SELECT m.menuId FROM MenuItem m where m.menuId = ?1")
    Integer querySingleMenuId(Integer menuId);

    @Query("SELECT m.menuId FROM MenuItem m where m.isLeaf = ?1 AND m.createTime > ?2")
    List<Integer> findNewMenuIdByIsLeaf(int isLeaf, Date date);
    
    @Query("SELECT m.menuId FROM MenuItem m where m.createTime > ?1")
    List<Integer> findNewMenuId(Date date);
    
    @Query("SELECT m.menuId FROM MenuItem m where m.createTime > ?1 AND m.isButton = ?2")
    List<Integer> findButtonNewMenuId(Date date, Integer isButton);

    @Query("SELECT m.menuId FROM MenuItem m")
    List<Integer> findButtons();

    @Query("SELECT m.menuId FROM  MenuItem m where m.parentId = 0")
    List<Integer> findTopTreeNode();
}
