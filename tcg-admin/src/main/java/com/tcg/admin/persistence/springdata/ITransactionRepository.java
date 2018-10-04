package com.tcg.admin.persistence.springdata;

import com.tcg.admin.model.Transaction;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ITransactionRepository extends JpaRepository<Transaction, String> {

	List<Transaction> findByTaskIdOrderByCreateTimeAsc(Integer taskId);

}