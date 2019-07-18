package com.tcg.admin.service;


import com.tcg.admin.common.exception.AdminServiceBaseException;
import com.tcg.admin.model.Transaction;

import java.util.List;

public interface TransactionService {
	
	/**
	 * <pre>
	 * insert a transaction
	 * 
	 * 建立transaction
	 * </pre>
	 * 
	 * @param transaction
	 *
	 * @return Transaction
	 * @throws AdminServiceBaseException
	 */
	Transaction insertTransaction(Transaction transaction) throws AdminServiceBaseException;

	List<Transaction> findAll() throws AdminServiceBaseException;

	List<Transaction> findBtTaskId(Long taskId);

}
