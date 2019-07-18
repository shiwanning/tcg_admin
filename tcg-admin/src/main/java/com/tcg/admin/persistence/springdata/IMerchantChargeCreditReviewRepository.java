package com.tcg.admin.persistence.springdata;


import com.tcg.admin.model.MerchantChargeCreditReview;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Date;


/**
 * <p>Title: com.yx.us.persistence.IMenuItemRepository</p>
 * <p>Description: MenuItem選單基本操作DAO</p>
 * <p>Copyright: Copyright (c) UniStar Corp. 2014. All Rights Reserved.</p>
 * <p>Company: UnitStar DEV Team</p>
 *
 * @author Marc
 * @version 1.0
 */
public interface IMerchantChargeCreditReviewRepository extends JpaRepository<MerchantChargeCreditReview, Long> {

    @Query("select review from MerchantChargeCreditReview review " +
           "where review.rid = :rid " +
           "and review.status = 0")
    MerchantChargeCreditReview getUnReviewInfo(@Param("rid") Long rid);

    @Query("select review from MerchantChargeCreditReview review " +
           "where (review.merchantCode = :merchantCode or :merchantCode is null) " +
           "and (review.leverMultiplier = :leverMultiplier or :leverMultiplier is null) " +
           "and (review.createOperatorName = :operator or :operator is null) " +
           "and (review.createTime > :startDate or :startDate is null) " +
           "and (review.createTime <= :endDate or :endDate is null) " +
           "and (review.status = :status or :status is null) order by review.updateTime desc ")
    Page<MerchantChargeCreditReview> getList(@Param("merchantCode") String merchantCode, @Param("leverMultiplier") BigDecimal leverMultiplier, @Param("operator") String operator, @Param("status") Integer status, @Param("startDate")Date startDate, @Param("endDate")Date endDate,
                                             Pageable pageable);
}
