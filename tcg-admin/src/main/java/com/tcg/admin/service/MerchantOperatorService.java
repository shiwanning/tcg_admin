package com.tcg.admin.service;


import com.tcg.admin.common.exception.AdminServiceBaseException;
import com.tcg.admin.model.MerchantOperator;

import java.util.List;

public interface MerchantOperatorService {

    void  save(List<MerchantOperator> merchantOperatorList) throws AdminServiceBaseException;

    void  deleteByOperatorId(Integer operatorId) throws AdminServiceBaseException;

}
