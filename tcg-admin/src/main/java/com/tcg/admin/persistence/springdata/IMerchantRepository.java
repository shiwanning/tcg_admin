package com.tcg.admin.persistence.springdata;


import com.tcg.admin.model.Merchant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IMerchantRepository extends JpaRepository<Merchant, Integer> {

    /**
     * 依 merchantId 查詢所有狀態的 record
     *
     * @param deptName
     *
     * @return
     */
    @Query("select d from Merchant d where d.merchantId = ?1")
    public Merchant findByMerchantId(Integer merchantId);

    /**
     * 依 merchantName 查詢所有狀態的 record
     *
     * @param deptName
     *
     * @return
     */
    @Query("select d from Merchant d where d.status = 1 and d.merchantCode = ?1")
    public Merchant findByMerchantCode(String merchantCode);
    
    @Query("select d from Merchant d where d.merchantCode = ?1")
    public Merchant findMerchantByCode(String merchantCode);

    /**
     * 依 merchantName 查詢目前狀態為正常的 record 筆數
     *
     * @param roleName
     *
     * @return 筆數
     */
    @Query("select count(d) from Merchant d where d.status = 1 and d.merchantCode = ?1")
    public int countByMerchantCode(String merchantCode);

    /**
     * 依 merchantId 查詢目前狀態為正常的 record 筆數
     *
     * @param merchantId
     *
     * @return 筆數
     */
    @Query("select count(d) from Merchant d where d.status = 1 and d.merchantId = ?1")
    public int countByMerchantId(Integer merchantId);

    /**
     * 依 displayOrder 做排序，查詢目前狀態為正常的 record
     *
     * @param merchantId
     *
     * @return 筆數
     */
    @Query("select d from Merchant d where d.status = 1")
    public List<Merchant> findAllOrderByDisplayOrder();
    
    @Query("select d from Merchant d ")
    public List<Merchant> findAllmerchant();

    @Query("select d from Merchant d where d.merchantType = ?1")
    public List<Merchant> findByMerchantType(String merchantTypeId);

    @Query("select d from Merchant d where d.merchantType <> '9'")
    public List<Merchant> findAllmerchantExceptCompany();

    @Query("select merchant from MerchantMenuCategory merchantMenuCategory, Merchant merchant where merchantMenuCategory.key.merchantCode = merchant.merchantCode and merchantMenuCategory.key.menuCategoryName = (?1)")
    public List<Merchant> findMenuCategoryByMenuCategoryName(String menuCategoryName);



    @Query(value = " select mercs.merchant_id from tcg_admin.admin_merchant mercs, tcg_admin.us_merchant_operator mercOpers " +
            " WHERE mercs.merchant_id = mercOpers.merchant_id " +
            " AND mercOpers.operator_id = ?1 "
            +" and mercs.merchant_id in (select merchant_id from tcg_admin.us_merchant_operator) ", nativeQuery = true)

    List<Integer> querySubscriptionMerchantIsAdmin(Integer userId);

    @Query(value = " select mercs.merchant_id from tcg_admin.admin_merchant mercs, tcg_admin.us_merchant_operator mercOpers " +
            " WHERE mercs.merchant_id = mercOpers.merchant_id " +
            " AND mercOpers.operator_id = ?1 "
            + " and mercs.merchant_id in (select merchant_id from tcg_admin.us_merchant_operator where operator_id = ?2) ", nativeQuery = true)
    List<Integer> querySubscriptionMerchantIsNotAdmin(Integer userId, Integer operatorId);
}
