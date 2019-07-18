package com.tcg.admin.service;


import java.util.List;

import com.tcg.admin.model.MerchantOperator;
import com.tcg.admin.model.Operator;

public interface MerchantOperatorService {

    void  save(List<MerchantOperator> merchantOperatorList);

    void  deleteByOperatorId(Integer operatorId);

	List<MerchantOperator> findByMerchantCode(String merchantCode);

	Operator getAdminOperator(String merchantCode);

}
