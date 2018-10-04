package com.tcg.admin.persistence.springdata;


import com.tcg.admin.model.MerchantMail;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface IMerchantMailRepository extends JpaRepository<MerchantMail, Integer> {

    @Query("select m from MerchantMail m where m.merchantCode =?1")
    MerchantMail findByMerchantCode(String merchantCode);
}
