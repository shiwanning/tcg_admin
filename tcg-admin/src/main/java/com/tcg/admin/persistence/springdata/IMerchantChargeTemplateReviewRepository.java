package com.tcg.admin.persistence.springdata;


import com.tcg.admin.model.MerchantChargeTemplateReview;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;

public interface IMerchantChargeTemplateReviewRepository extends JpaRepository<MerchantChargeTemplateReview, Long> {

    @Query("select review from MerchantChargeTemplateReview review " +
           "where (review.merchantChargeTemplateModify.merchantChargeTemplate.name = :templateName or :templateName is null) " +
           "and (review.createOperatorName = :operator or :operator is null) " +
           "and (review.createTime > :startDate or :startDate is null) " +
           "and (review.createTime <= :endDate or :endDate is null) " +
           "and (review.status = :status or :status is null) order by review.updateTime desc ")
    Page<MerchantChargeTemplateReview> getList(@Param("templateName") String templateName, @Param("operator") String operator, @Param("status") Integer status, @Param("startDate")Date startDate, @Param("endDate")Date endDate,
                                               Pageable pageable);


    @Query("select review from MerchantChargeTemplateReview review, MerchantChargeConfig merchantChargeConfig " +
           "where review.merchantChargeTemplateModify.merchantChargeTemplate.rid = merchantChargeConfig.templateRid " +
           "and merchantChargeConfig.merchantCode = :merchant " +
           "and (review.merchantChargeTemplateModify.merchantChargeTemplate.name = :templateName or :templateName is null) " +
           "and (review.createOperatorName = :operator or :operator is null) " +
           "and (review.createTime > :startDate or :startDate is null) " +
           "and (review.createTime <= :endDate or :endDate is null) " +
           "and (review.status = :status or :status is null) order by review.updateTime desc ")
    Page<MerchantChargeTemplateReview> getList(@Param("merchant") String merchant, @Param("templateName") String templateName, @Param("operator") String operator, @Param("status") Integer status, @Param("startDate")Date startDate, @Param("endDate")Date endDate,
                                               Pageable pageable);

    @Query("select review from MerchantChargeTemplateReview review " +
           "where review.merchantChargeTemplateModify.merchantChargeTemplate.rid = :rid " +
           "and review.status = 0")
    MerchantChargeTemplateReview getWaitingForReviewTemplate(@Param("rid") Long rid);

    @Query("select review from MerchantChargeTemplateReview review " +
           "where review.rid = :rid " +
           "and review.status = 0")
    MerchantChargeTemplateReview getUnReviewInfo(@Param("rid") Long rid);



}
