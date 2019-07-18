package com.tcg.admin.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tcg.admin.model.MerchantOperator;
import com.tcg.admin.model.Operator;
import com.tcg.admin.persistence.springdata.IMerchantOperatorRepository;
import com.tcg.admin.persistence.springdata.IOperatorRepository;
import com.tcg.admin.service.MerchantOperatorService;

@Service
@Transactional
public class MerchantOperatorServiceImpl implements MerchantOperatorService {

    @Autowired
    private IMerchantOperatorRepository merchantOperatorRepository;
    
    @Autowired
    private IOperatorRepository operatorRepository;

    @Override
    public void deleteByOperatorId(Integer operatorId) {
        merchantOperatorRepository.deleteByOperatorId(operatorId);
    }

    @Override
    public void save(List<MerchantOperator> merchantOperatorList) {
        merchantOperatorRepository.save(merchantOperatorList);
    }

	@Override
	public List<MerchantOperator> findByMerchantCode(String merchantCode) {
		return merchantOperatorRepository.findByMerchantCode(merchantCode);
	}
	
	@Override
	public Operator getAdminOperator(String merchantCode) {
		List<MerchantOperator> mos = findByMerchantCode(merchantCode);
		if(!mos.isEmpty()) {
			return operatorRepository.findOne(mos.get(0).getOperatorId());
		} else {
			return null;
		}
	}

}
