package com.tcg.admin.persistence.springdata;


import com.tcg.admin.model.MerchantChargeTemplateModify;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface IMerchantChargeTemplateModifyRepository extends JpaRepository<MerchantChargeTemplateModify, Long> {

    @Query("select modify from MerchantChargeTemplateModify modify " +
           "where modify.merchantChargeTemplate.rid = :templateRid " +
           "and modify.status = :status " +
           "and modify.updateTime < :createTime order by modify.createTime desc ")
    List<MerchantChargeTemplateModify> getList(@Param("templateRid") Long templateRid, @Param("status") Integer status, @Param("createTime")Date createTime);


}
