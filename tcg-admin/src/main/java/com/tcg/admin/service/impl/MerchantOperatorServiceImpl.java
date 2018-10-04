package com.tcg.admin.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tcg.admin.model.MerchantOperator;
import com.tcg.admin.persistence.springdata.IMerchantOperatorRepository;
import com.tcg.admin.service.MerchantOperatorService;

@Service
@Transactional
public class MerchantOperatorServiceImpl implements MerchantOperatorService {

    @Autowired
    private IMerchantOperatorRepository merchantOperatorRepository;

    @Override
    public void deleteByOperatorId(Integer operatorId) {
        merchantOperatorRepository.deleteByOperatorId(operatorId);
    }

    @Override
    public void save(List<MerchantOperator> merchantOperatorList) {
        merchantOperatorRepository.save(merchantOperatorList);
    }

}
