package com.tcg.admin.persistence.springdata;


import com.tcg.admin.model.SystemParameter;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * <p>Title: com.yx.us.persistence.ISystemParameterRepository</p>
 * <p>Description: US系統參數基本操作DAO</p>
 * <p>Copyright: Copyright (c) UniStar Corp. 2014. All Rights Reserved.</p>
 * <p>Company: UnitStar DEV Team</p>
 * @author Marc
 * @version 1.0
 */
public interface ISystemParameterRepository extends JpaRepository<SystemParameter, String> {

	
	/**
	 * 取回密碼限制天數物件
	 * @return
	 */
    SystemParameter findByParamName(String paramName);
}
