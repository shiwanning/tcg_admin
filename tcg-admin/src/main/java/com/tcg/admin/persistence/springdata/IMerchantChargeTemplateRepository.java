package com.tcg.admin.persistence.springdata;


import com.tcg.admin.model.MerchantChargeTemplate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface IMerchantChargeTemplateRepository extends JpaRepository<MerchantChargeTemplate, Long> {

    @Query("select template from MerchantChargeTemplate template " +
           "where " +
           "(template.name like %:templateName% or :templateName is null) " +
           "and (template.templateType = :templateType or :templateType is null) " +
           "and (template.createOperatorName like %:operator% or :operator is null) " +
           "and (template.createTime > :startDate or :startDate is null) " +
           "and (template.createTime <= :endDate or :endDate is null) ")
    Page<MerchantChargeTemplate> getList(@Param("templateName") String templateName, @Param("templateType") Integer templateType, @Param("operator") String operator, @Param("startDate")Date startDate, @Param("endDate")Date endDate,
                                         Pageable pageable);

    @Query("select template from MerchantChargeTemplate template, MerchantChargeConfig merchantChargeConfig " +
           "where template.rid = merchantChargeConfig.templateRid " +
           "and merchantChargeConfig.merchantCode like %:merchant% " +
           "and (template.templateType = :templateType or :templateType is null) " +
           "and (template.name like %:templateName% or :templateName is null) " +
           "and (template.createOperatorName like %:operator% or :operator is null) " +
           "and (template.createTime > :startDate or :startDate is null) " +
           "and (template.createTime <= :endDate or :endDate is null) ")
    Page<MerchantChargeTemplate> getList(@Param("merchant") String merchant, @Param("templateName") String templateName, @Param("templateType") Integer templateType, @Param("operator") String operator, @Param("startDate")Date startDate, @Param("endDate")Date endDate,
                                         Pageable pageable);

    @Query("select template from MerchantChargeTemplate template " +
           "where (template.templateType = :templateType or :templateType is null) " +
           "order by template.templateType asc, template.createTime desc ")
    List<MerchantChargeTemplate> getTemplateTypeList(@Param("templateType") Integer templateType);


}
