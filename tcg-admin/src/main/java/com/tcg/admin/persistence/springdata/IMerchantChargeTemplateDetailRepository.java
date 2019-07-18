package com.tcg.admin.persistence.springdata;


import com.tcg.admin.model.MerchantChargeTemplateDetail;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IMerchantChargeTemplateDetailRepository extends JpaRepository<MerchantChargeTemplateDetail, Long> {

    @Query("select detail " +
           "from MerchantChargeConfig config, MerchantChargeTemplate template, MerchantChargeTemplateDetail detail " +
           "where config.merchantCode = :merchantCode " +
           "and config.merchantChargeTemplate.rid = template.rid " +
           "and detail.merchantChargeTemplate.rid = template.rid " +
           "and detail.detailType >= 3 " +
           "order by detail.createTime desc ")
    List<MerchantChargeTemplateDetail> getProductList(@Param("merchantCode") String merchantCode);

    List<MerchantChargeTemplateDetail> findByDetailTypeAndCode(int detailType, String code);

}
