package com.tcg.admin.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tcg.admin.common.exception.AdminServiceBaseException;
import com.tcg.admin.model.Transaction;
import com.tcg.admin.persistence.springdata.ITransactionRepository;
import com.tcg.admin.service.TransactionService;

@Service
@Transactional
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private ITransactionRepository transactionRepository;

    @Override
    public List<Transaction> findAll() throws AdminServiceBaseException {
        return transactionRepository.findAll();
    }

	@Override
	public Transaction insertTransaction(Transaction transaction) throws AdminServiceBaseException {
        return transactionRepository.saveAndFlush(transaction);
	}

	@Override
	public List<Transaction> findBtTaskId(Long taskId) {
		return transactionRepository.findByTaskIdOrderByCreateTimeAsc(taskId.intValue());
	}
    
}
