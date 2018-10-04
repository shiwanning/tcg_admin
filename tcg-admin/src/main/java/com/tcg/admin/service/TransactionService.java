package com.tcg.admin.service;


import com.tcg.admin.common.exception.AdminServiceBaseException;
import com.tcg.admin.model.Transaction;

import java.util.List;

public interface TransactionService {
	
	public static final String CLAIM_STATUS = "claim";
	
	public static final String UNCLAIM_STATUS = "unclaim";
	
	public static final String CREATE_STATUS = "create";
	
	public static final String PROCESS_STATUS = "process";
	
	public static final String CLOSE_STATUS = "close";
	
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
