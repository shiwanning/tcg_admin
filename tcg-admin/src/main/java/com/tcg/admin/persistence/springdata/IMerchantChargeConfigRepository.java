package com.tcg.admin.persistence.springdata;


import com.tcg.admin.model.MerchantChargeConfig;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IMerchantChargeConfigRepository extends JpaRepository<MerchantChargeConfig, Long> {

    @Modifying()
    @Query("delete from MerchantChargeConfig config where config.merchantCode = :merchantCode")
    void deleteByMerchantCode(@Param("merchantCode") String merchantCode);

    @Query("select config from MerchantChargeConfig config " +
           "where config.merchantCode = :merchantCode order by config.createTime ")
    List<MerchantChargeConfig> getMerchantTemplateList(@Param("merchantCode") String merchantCode);

    @Query("select config from MerchantChargeConfig config " +
           "where config.templateRid = :templateRid " +
           "order by lower(config.merchantCode)")
    List<MerchantChargeConfig> getTemplateMerchantList(@Param("templateRid") Long templateRid);
}
