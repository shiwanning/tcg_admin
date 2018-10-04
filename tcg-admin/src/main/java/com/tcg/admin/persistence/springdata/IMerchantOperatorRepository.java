package com.tcg.admin.persistence.springdata;


import com.tcg.admin.model.Merchant;
import com.tcg.admin.model.MerchantOperator;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

import java.util.List;

import javax.persistence.QueryHint;

public interface IMerchantOperatorRepository extends JpaRepository<MerchantOperator, Integer> {

    /**
     * 取得某個 department 下有哪些 operator 的 Id
     *
     * @param deptId
     *
     * @return
     */
    List<MerchantOperator> findByMerchantId(Integer merchantId);

    /**
     * 查詢 operatorId 是否有隸屬 merchantId 的 record
     *
     * @param merchantId
     * @param operatorId
     *
     * @return
     */
    @Query("select count(mercOp) from MerchantOperator mercOp where mercOp.merchantId = ?1 and mercOp.operatorId = ?2")
    int countByDeptIdAndOperatorId(Integer merchantId, Integer operatorId);

    /**
     * 依 operatorId 刪除隸屬部門的 record
     *
     * @param operatorId
     */
    @Modifying()
    @Query("delete from MerchantOperator mercOp where mercOp.operatorId = ?1")
    void deleteByOperatorId(Integer operatorId);

    /**
     * 取得某個operator屬於哪些department
     *
     * @param operatorId
     *
     * @return
     */
    @Query("select mercOp.merchantId from MerchantOperator mercOp where mercOp.operatorId = ?1")
    @QueryHints(value={@QueryHint(name=org.hibernate.annotations.QueryHints.CACHEABLE, value="true")})
    List<Integer> findMerchantIdListByOperatorId(Integer operatorId);

    /**
     * @see findMerchantIdListByOperatorId
     */
    @Query("select mercOp.merchantId from MerchantOperator mercOp where mercOp.operatorId = ?1")
    @QueryHints(value={@QueryHint(name=org.hibernate.annotations.QueryHints.CACHEABLE, value="false")})
    List<Integer> findMerchantIdListByOperatorIdWithoutCache(Integer operatorId);
    
    /**
     * 取得某個operator屬於哪些merchantCode
     * @param operatorId
     * @return
     */
    //TODO
    @Query("select merc.merchantCode from Merchant merc, MerchantOperator mercOp where 1=1 and merc.merchantId = mercOp.merchantId and mercOp.operatorId = ?1")
    List<String> findMerchantCodeListByOperatorId(Integer operatorId);

    /**
     * 取得多筆operator所屬部門
     *
     * @param merchantId
     *
     * @return
     */
    @Query("select mercOp from MerchantOperator mercOp where mercOp.operatorId in (?1)")
    List<MerchantOperator> findMerchantIdListByOperatorIdList(List<Integer> operatorIdList);

    @Query("select mercOp.operatorId from MerchantOperator mercOp where mercOp.merchantId = ?1 and  mercOp.operatorId in (?2) ")
    List<Integer> findMerchantOperatorIdByOperatorIds(Integer merchantId, List<Integer> operatorIdList);

    @Query("select merc from Merchant merc, MerchantOperator mercOp where 1=1 and merc.merchantId = mercOp.merchantId and mercOp.operatorId = ?1")
    List<Merchant> findMerchants(Integer operatorId);

}
