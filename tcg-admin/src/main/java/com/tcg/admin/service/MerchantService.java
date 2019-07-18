package com.tcg.admin.service;


import com.tcg.admin.common.exception.AdminServiceBaseException;
import com.tcg.admin.model.Merchant;
import com.tcg.admin.model.Operator;
import com.tcg.admin.model.Task;
import com.tcg.admin.to.NoneAdminInfo;
import com.tcg.admin.to.OperatorCreateTO;

import java.util.List;
import java.util.Map;

public interface MerchantService {

	List<Merchant> getMerchantList();

	/**
     * <pre>
	 * Delete a department, for role management.
	 *  
	 * 刪除部門
	 * </pre>
	 * 
	 * @param merchantId
	 * @throws AdminServiceBaseException
	 */
    void deleteMerchant(Integer merchantId);


	/**
	 * <pre>
	 * Assign operator to department, for role management.
	 *  
	 * 指派operator到特定部門
	 * </pre>
	 * 
	 * @param operatorName, merchantId
	 * @throws AdminServiceBaseException
	 */
    void assignOperatorMerchants(Operator operator, List<Integer> merchantIds);

    /**
     * <pre>
	 * 复制某一存在用户
	 * 根据已存在的帐号进行复制，要求修改名称
	 * 被复制的用户必须与原用户有相同的角色权限功能
	 * 
	 * @param originOperatorId
	 * @param newOperatorName
	 * @return
	 * @throws AdminServiceBaseException
	 */
    Operator copyOperator(Integer originOperatorId, String newOperatorName);


    /**
     * 查詢operator所屬的部門
     * 
     * @param operator
     * @return
     * @throws AdminServiceBaseException
     */
    Merchant queryOperatorMerchant(Operator operator);

    List<Merchant> queryOperatorMerchants(boolean flag);

    void renewMerchantList();

    List<Merchant> getAllAdmMerchantList();

	List<Merchant> checkAdmMerchantList();

	List<Merchant> getAll();

	NoneAdminInfo checkAdmin(boolean callByRole);

    /**
     * 查詢多筆operator的所屬部門
     * 
     * @param operatorIdList
     * @return
     * @throws AdminServiceBaseException
     */
    Map<Integer, Merchant> queryOperatorMerchant(List<Integer> operatorIdList);

	Merchant getMerchant(Integer merchantId);

	Merchant findMerchantByMerchantCode(String merchantCode);

	void approveMerchant(OperatorCreateTO operatorCreateTO, Integer taskId, List<Integer> operatorIdList, List<Integer> roleIdList, String operatorName);

	Task createMerchant(String gwMerchantId, String operatorName, String merchantName, String baseMerchantCode);

	Task reApprove(OperatorCreateTO operatorCreateTO);

	void updateMerchant(OperatorCreateTO operatorCreateTO);

	List<Merchant> getMerchantByType(String merchantTypeId);

	Map<String, Object> getProductInfo(String baseMerchantCode);

	void updateProductInfo(OperatorCreateTO operatorCreateTO);

	void createMerchant(Merchant company, Merchant merchant, Boolean createCompany);

	Operator createAdminOperator(String merchantCode, List<Integer> roles);

	void subscribeUser(String merchantCode, List<String> operatorNames, List<Integer> roleIds);

	List<Integer> querySubscriptionMerchant(Integer operatorId, boolean admin, Integer userId);


	List<Merchant> getOperatorMerchant(Operator operator);
}
