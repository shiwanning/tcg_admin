package com.tcg.admin.service;


import java.util.List;
import java.util.Map;

import com.tcg.admin.common.exception.AdminServiceBaseException;
import com.tcg.admin.model.BehaviorLog;
import com.tcg.admin.model.Operator;
import com.tcg.admin.to.*;

/**
 * <pre>
 * Operator Service is an API layer interface for developers to call 
 * and use in service logic layer.
 * It has contain methods to modify operator information in database, 
 * with some basic input validations.
 * </pre>
 * 
 * 
 */

public interface OperatorService {
	/**
	 * <pre>
	 * Register an new back-end Operator, this will return an Operator model
	 * after insert is successes, input fields encapsulated in Operator model
	 * will be verify with certain format rules. Password will be encrypted with
	 * MD5 mechanism. An AdminServiceBaseException with error message will be
	 * thrown when those fields fail to pass the validation.
	 *
	 * Required fields: OperatorName, nickname, password.
	 * 
	 * Format Check
	 * OperatorName: 6~16 alphabet letters or numbers, starting with "_" or a alphabet letter.
	 * Password: 6~16 alphabet letters or numbers
	 * Nickname: 1~16 alphabet letters or numbers, without white space.
	 * 
	 * 註冊後臺帳戶
	 * </pre>
	 * 
	 * @param operator
	 *            operator model that include mandatory fields which are
	 *            username, password ,nickname and activeflag.
	 * @return the operator model been inserted to the database.
	 * @throws AdminServiceBaseException
	 *             if username is already existed or password length &lt; 6 and
	 *             &gt; 16 or operatorName, password, nickname and activeflag is
	 *             blank or not correct format.
	 */
	Operator addOperator(OperatorCreateTO operatorCreateTO);

	Operator updateOperatorOfMerchant(OperatorCreateTO operatorCreateTO);

	Operator addOperatorOfMerchant(OperatorCreateTO operatorCreateTO);

	/**
	 * <pre>
	 * Change back-end operator's active flag status. To inactive or active by operatorName and activeFlag.
	 * 
	 * 修改後台帳號的狀態
	 * 
	 * activeFlag
	 * 0:手動禁用(後台人員禁用) ｜ 1：可登入｜ 2：登入禁用(登入次數超過)｜ 7：用戶已被刪除
	 * </pre>
	 * 
	 * @param operatorName
	 *            operator to change active flag.
	 * @param activeFlag
	 *            the active status, inactive or active.
	 * @throws AdminServiceBaseException
	 *             if operatorName not found or activeflag is
	 *             LoginConstant.ActiveFlagDelete
	 * @see com.yx.commons.constant.LoginConstant
	 */
	Operator changeActiveFlag(String operatorName, int activeFlag);


	/**
	 * <pre>
	 * This will set operator's activeflag status to deleted (LoginConstant.ActiveFlagDelete). Will not actually erase the record from database.
	 *
	 * 刪除管理員帳號
	 * </pre>
	 *
	 * @param operatorId
	 *            - operatorId
	 * @throws AdminServiceBaseException
	 * @see com.yx.commons.constant.LoginConstant
	 */
	void deleteOperator(Integer operatorId);

	/**
	 * <pre>
	 * Input a String variable operator name as an parameter to reset operator's
	 * password to default value (123456). Operator name will be check in database,
	 * exception throws when its not found. The new password value will be encrypted with
	 * MD5 mechanism.
	 *
	 * 重置管理員密碼
	 * </pre>
	 *
	 * @param username
	 *            key to find operator's detail.
	 * @throws AdminServiceBaseException
	 *             if operator is not found or active flag is
	 *             LoginConstant.ActiveFlagDelete.
	 * @see com.yx.commons.constant.LoginConstant
	 */
	Map<String, String> resetPassword(String username);


	/**
	 * 配置Role
	 * @param operatorId
	 * @param roles
	 * @throws AdminServiceBaseException
     */
	void assignRoles(Integer operatorId, List<Integer> roles);

    QueryJsonTO queryOperator2(String operatorName, Integer roleId, Integer activeFlag, Integer merchantId, String baseMerchantCode,String lastLoginIP, PageableTO pageableTo);

    List<Integer> querySubscriptionMerchant(Integer userId);
	/**
	 * find Operator By Id
	 * @param operatorId
	 * @throws AdminServiceBaseException
     */
	Operator findOperatorById(Integer operatorId);

    Operator findOperatorByName(String operatorName);

    /**
     * Update operator details. Nickname, Email, Password
     *
     * @param operator
     *
     * @return
     *
     * @throws AdminServiceBaseException
     */
    void updateOperator(Operator operator);

    /**
     * <pre>
     * Query operator's detail in database by name.
     *
     * 用管理者名稱找尋相關資料
     * </pre>
     *
     * @param operatorName key to query in database
     *
     * @return Operator
     *
     * @throws UserServiceBaseException
     */
    Operator queryByOperatorName(String operatorName);

    /**
     * <pre>
     * Query operator's detail in database by name.
     *
     * 用管理者ID找尋相關資料
     * </pre>
     *
     * @param operatorId key to query in database
     *
     * @return Operator
     *
     * @throws UserServiceBaseException
     */
    Operator queryByOperatorId(Integer operatorId);

    /**
     * <pre>
     * Change operator's password in database, use Operator model as parameter.
     * This method will validate certain operator fields to allow user to update its password.
     * Those required fields are:
     * newPassword,
     * confirmPassword,
     * oldPassword,
     * and username.
     * It will also check the operator's active flag is not equal LoginConstant.ActiveFlagDelete,
     * new password cannot be same with five previous passwords.
     *
     * 修改管理員登入密碼
     * </pre>
     *
     * @param operator operator model that include mandatory fields which are
     *                 username, oldPassword, newPassword, confirmPassword.
     *
     * @param b
	 * @throws UserServiceBaseException
     * @see com.yx.commons.constant.LoginConstant
     */
    void changeOperatorPassword(String username, String oldPassword, String newPassword, String confirmPassword, boolean isReset);

    Map<String, Object> getActivityLog(String userName, Integer page, Integer pageSize, Integer actionType);

    Map<String, Object> getOperatorProfile(String userName);

    BehaviorLog getCurrentLoginBehaviorLog(String userName);
    
    Object findOperatorBySession(String token, boolean flag);

    Operator findOperatorByToken(String token);
    /**
     * transfer operator data from old console
     * @return
     */
    Operator transferOperator(OperatorCreateTO op);
    
    List<OperatorsTO> getOperatorsByActiveFlag(List<Integer> statusCodes);

    void updateBaseMerchant(OperatorCreateTO operatorCreateTO);

    List<Operator> findOperatorByIds(List<Integer> operatorIds);

	List<BehaviorLogTo> getActivityLoginLog(String userName, Integer page, Integer pageSize);

	void updateThirtyNotLoginUser();

	void updateUsOperatorByMisMerchantStatus(Integer active, String merchantCode);

	Map<String, Object> findOperatorByAccountName(String accountName);
}
