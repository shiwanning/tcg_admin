package com.tcg.admin.common.error;

public final class AdminErrorCode {
	
	public static final String UNKNOWN_ERROR = "UNKNOWN_ERROR";
	
	public static final String UNSUPPORT_ENCODING = "UNSUPPORT_ENCODING";
	
    public static final String REQUEST_SUCCESS = "REQUEST_SUCCESS";
    /**
     * 取得遠端EJB錯誤
     */
    public static final String REMOTE_EJB_ERROR = "US_REMOTE_EJB_ERROR";

    /**
     * 帳號名稱格式錯誤
     */
    public static final String NAME_FORMAT_ERROR = "US_NAME_FORMAT_ERROR";
    /**
     * 密碼格式錯誤
     */
    public static final String PWD_FORMAT_ERROR = "US_PASSWORD_FORMAT_ERROR";
    /**
     * 登入禁用
     */
    public static final String FORBIDDEN_LOGIN_ERROR = "US_FORBIDDEN_LOGIN_ERROR";
    /**
     * 登入禁用
     */
    public static final String FORBIDDEN_LOGIN_TYPE = "FORBIDDEN_LOGIN_TYPE";
    
    /**
     * IP 不可登入
     */
    public static final String IP_NOT_ALLOW = "IP_NOT_ALLOW";

    public static final String  NOT_LOGIN_BEEN_THIRTY_DAYS = "NOT_LOGIN_BEEN_THIRTY_DAYS";

    /**
     * 用户登录IP跟当前IP不匹配
     */
    public static final String IP_HAS_CHANGED = "IP_HAS_CHANGED";

    /**
     *  未绑定谷歌，不允许登入
     */
    public static final String  GOOGLE_AUTHENTICATION_IS_NOT_SETUP = "GOOGLE_AUTHENTICATION_IS_NOT_SETUP";

    /**
     * 帐号或密碼錯誤
     */
    public static final String INCORRECT_USERNAME_PWD_ERROR = "US_INCORRECT_USERNAME_PASSWORD_ERROR";

    /**
     * 帐号不存在
     */
    public static final String USER_NOT_EXIST = "USER_NOT_EXIST";

    /**
     * 谷歌认证密碼錯誤
     */
    public static final String INCORRECT_GOOGLE_PWD_ERROR = "US_INCORRECT_GOOGLE_PWD_ERROR";

    /**
     * 没绑定谷歌账号
     */
    public static final String NOT_BIND_GOOGLE_OTP = "NOT_BIND_GOOGLE_OTP";
    /**
     * 密碼錯誤
     */
    public static final String INCORRECT_PWD_ERROR = "US_INCORRECT_PASSWORD_ERROR";
    /**
     * 資金密碼錯誤
     */
    public static final String INCORRECT_PAYMENT_PWD_ERROR = "US_INCORRECT_PAYMENT_PASSWORD_ERROR";
    /**
     * 密碼錯誤次數到達上限
     */
    public static final String INCORRECT_PWD_MAX_TIMES_ERROR = "US_INCORRECT_PASSWORD_MAX_TIMES_ERROR";
    /**
     * 帳號名稱含有"ts"
     */
    public static final String NAME_HAVE_TS_ERROR = "US_NAME_HAVE_TS_ERROR";
    /**
     * 帳號未登入
     */
    public static final String USER_NOT_LOGIN_ERROR = "US_USER_NOT_LOGIN_ERROR";

    /**
     * 新密碼為空
     */
    public static final String NEW_PWD_EMPTY_ERROR = "US_NEW_PASSWORD_EMPTY_ERROR";
    /**
     * 確認密碼為空
     */
    public static final String COFIRM_PWD_EMPTY_ERROR = "US_COFIRM_PASSWORD_EMPTY_ERROR";
    /**
     * 舊密碼為空
     */
    public static final String OLD_PWD_EMPTY_ERROR = "US_OLD_PASSWORD_EMPTY_ERROR";
    /**
     * 密碼已經使用過
     */
    public static final String NEW_PWD_EXISTS_HISTORY_ERROR = "US_NEW_PASSWORD_EXISTS_HISTORY_ERROR";
    /**
     * 舊密碼格式錯誤
     */
    public static final String OLD_PWD_FORMAT_ERROR = "US_OLD_PASSWORD_FORMAT_ERROR";
    /**
     * 新密碼格式錯誤
     */
    public static final String NEW_PWD_FORMAT_ERROR = "US_NEW_PASSWORD_FORMAT_ERROR";

    /**
     * 帳號已使用,不可重複
     */
    public static final String CUSTOMER_EXIST_ERROR = "US_CUSTOMER_EXIST_ERROR";
    /**
     * 使用者不存在
     */
    public static final String CUSTOMER_NOT_EXIST_ERROR = "US_CUSTOMER_NOT_EXIST_ERROR";
    /**
     * 電子郵箱不存在
     */
    public static final String CUSTOMER_EMAIL_NOT_EXIST_ERROR = "US_CUSTOMER_EMAIL_NOT_EXIST_ERROR";

    /**
     * There is no SMTP Setting for the merchant.
     */
    public static final String MERCHANT_SMTP_NOT_EXIST_ERROR = "MERCHANT_SMTP_NOT_EXIST_ERROR";

    /**
     * There is merchant informtion for the Customer
     */
    public static final String CUSTOMER_HAS_NO_MERCHANT = "CUSTOMER_HAS_NO_MERCHANT";

    /**
     * 電子郵箱錯誤
     */
    public static final String CUSTOMER_EMAIL_ISDEFFERENT_ERROR = "US_CUSTOMER_EMAIL_ISDEFFERENT_ERROR";

    /**
     * Send the MAIL fail
     */
    public static final String SEND_EMAIL_FAIL = "SEND_EMAIL_FAIL";

    /**
     * 有必填欄位未填
     */
    public static final String CUSTOMER_INPUT_FIELD_REQUIRE_ERROR = "US_CUSTOMER_INPUT_FIELD_REQUIRE_ERROR";

    public static final String PROHIBIT_PASSWORD_123456 = "PROHIBIT_PASSWORD_123456";
    /**
     * 輸入欄位與確認欄位內容不符
     */
    public static final String CUSTOMER_INPUT_FIELD_MISMATCH_ERROR = "US_CUSTOMER_INPUT_FIELD_MISMATCH_ERROR";
    /**
     * 推薦人欄位不合法
     */
    public static final String CUSTOMER_ILLEGAL_RECOMMENDER_ERROR = "US_CUSTOMER_ILLEGAL_RECOMMENDER_ERROR";

    public static final String RECOMMENDER_NOT_CANNOT_BE_SELF = "RECOMMENDER_NOT_CANNOT_BE_SELF";

    public static final String RECOMMENDER_NOT_CANNOT_BE_SUBORDINATE = "RECOMMENDER_NOT_CANNOT_BE_SUBORDINATE";

    public static final String RECOMMENDER_NOT_CHANGED = "RECOMMENDER_NOT_CHANGED";

    public static final String RECOMMENDER_NOT_FOUND = "RECOMMENDER_NOT_FOUND";

    public static final String MERCHANT_CANNOT_CHANGE_RECOMMENDER = "MERCHANT_CANNOT_CHANGE_RECOMMENDER";

    /**
     * 會員不能註冊下級
     */
    public static final String CUSTOMER_REGISTER_LOWER_CUSTOMER_ERROR = "US_CUSTOMER_REGISTER_LOWER_CUSTOMER_ERROR";

    /**
     * 上級不具有開戶權限
     */
    public static final String CUSTOMER_NO_RIGHT_TO_REGISTER_ERROR = "US_CUSTOMER_NO_RIGHT_TO_REGISTER_ERROR";
    /**
     * 使用者禁用
     */
    public static final String CUSTOMER_ACTIVEFLAG_ERROR = "US_CUSTOMER_ACTIVEFLAG_ERROR";
    /**
     * 無法送出新密碼
     */
    public static final String CUSTOMER_SENDING_NEW_PWD_ERROR = "US_SENDING_NEWPASSWORD_ERROR";
    /**
     * 新密碼與資金密碼不可相同
     */
    public static final String CUSTOMER_PAYMENT_LOGIN_PWD_IS_SAME_ERROR = "US_CUSTOMER_PAYMENT_LOGIN_PASSWORD_IS_SAME_ERROR";
    /**
     * 新資金密碼與登入密碼不可相同
     */
    public static final String CUSTOMER_LOGIN_PAYMENT_PWD_IS_SAME_ERROR = "US_CUSTOMER_LOGIN_PAYMENT_PASSWORD_IS_SAME_ERROR";

    /**
     * 資金密碼,新密碼,確認密碼不可為空
     */
    public static final String CUSTOMER_PWD_IS_EMPTY = "US_CUSTOMER_PASSWORD_IS_EMPTY";
    /**
     * 新密碼與舊密碼不可相同
     */
    public static final String NEW_OLD_PWD_IS_SAME_ERROR = "US_NEW_OLD_PASSWORD_IS_SAME_ERROR";
    /**
     * 新密碼與確認密碼須一致
     */
    public static final String NEW_CONFIRM_PWD_IS_DIFF_ERROR = "NEW_CONFIRM_PASSWORD_IS_DIFF_ERROR";
    /**
     * 舊密碼輸入錯誤
     */
    public static final String OLD_PWD_ERROR = "US_OLD_PASSWORDE_ERROR";
    /**
     * 舊资金密码輸入錯誤
     */
    public static final String OLD_PAYMENT_PWD_ERROR = "US_OLD_PAYMENT_PASSWORDE_ERROR";

    /**
     * CUSTOMER_ID不可為空
     */
    public static final String CUSTOMER_ID_IS_EMPTY = "US_CUSTOMER_ID_IS_EMPTY";

    /**
     * 欄位不可修改
     */
    public static final String CUSTOMER_ILLEGAL_MODIFY_FIELD_ERROR = "CUSTOMER_ILLEGAL_MODIFY_FIELD_ERROR";

    /**
     * EMAIL格式錯誤
     */
    public static final String CUSTOMER_EMAIL_FORMAT_ERROR = "US_CUSTOMER_EMAIL_FORMAT_ERROR";
    /**
     * EMAIL已使用,不可重複
     */
    public static final String CUSTOMER_EMAIL_DUPLICATE_ERROR = "US_CUSTOMER_EMAIL_DUPLICATE_ERROR";
    /**
     * EMAIL與確認EMAIL不一致
     */
    public static final String CUSTOMER_COMFIRM_EMAIL_IS_DIFF_ERROR = "CUSTOMER_COMFIRM_EMAIL_IS_DIFF_ERROR";
    /**
     * 確認要變更的帳號名稱不同於新帳號名稱
     */
    public static final String CUSTOMER_NAME_IS_EQUAL_ERROR = "CUSTOMER_NAME_IS_EQUAL_ERROR";
    /**
     * QQ號碼格式不符
     */
    public static final String CUSTOMER_QQ_NO_FORMAT_ERROR = "US_CUSTOMER_QQ_NO_FORMAT_ERROR";
    /**
     * 手機號碼格式不符
     */
    public static final String CUSTOMER_MOBILE_NO_FORMAT_ERROR = "US_CUSTOMRE_MOBILE_NO_FORMAT_ERROR";
    /**
     * 邮政编码格式不符
     */
    public static final String CUSTOMER_ZIPCODE_FORMAT_ERROR = "US_CUSTOMER_ZIPCODE_FORMAT_ERROR";
    /**
     * 匿稱格式不符
     */
    public static final String CUSTOMER_NICKNAME_ERROR = "US_CUSTOMER_NICKNAME_ERROR";

    /**
     * Mobile already in used by another customer
     */
    public static final String CUSTOMER_MOBILE_NO_DUPLICATED = "CUSTOMER_MOBILE_NO_DUPLICATED";

    /**
     * QQ already in used by another customer
     */
    public static final String CUSTOMER_QQ_DUPLICATED = "CUSTOMER_QQ_DUPLICATED";
    /**
     * 檢核提款人姓名格式不合法
     */
    public static final String CUSTOMER_PAYEE_FORMAT_ERROR = "US_CUSTOMER_PAYEE_FORMAT_ERROR";

    /**
     * 會員組不存在
     */
    public static final String CUSTOMER_GROUP_IS_NOT_EXISTED = "US_CUSTOMER_GROUP_IS_NOT_EXISTED";
    /**
     * 會員組名稱不存在
     */
    public static final String CUSTOMER_GROUP_NAME_IS_EMPTY = "US_CUSTOMER_GROUP_NAME_IS_EMPTY";

    /**
     * 會員組排序不存在
     */
    public static final String CUSTOMER_GROUP_SORTING_IS_EMPTY = "CUSTOMER_GROUP_SORTING_IS_EMPTY";

    /**
     * 會員組名稱已使用,不可重複
     */
    public static final String CUSTOMER_GROUP_NAME_IS_EXISTED = "US_CUSTOMER_GROUP_NAME_IS_EXISTED";
    /**
     * 會員組名稱長度太長
     */
    public static final String CUSTOMER_GROUP_NAME_IS_TOO_LONG = "US_CUSTOMER_GROUP_NAME_IS_TOO_LONG";

    /**
     * 會員組參數名稱不可為空
     */
    public static final String CUSTOMER_GROUP_PARAMETER_NAME_IS_EMPTY = "US_CUSTOMER_GROUP_PARAMETER_NAME_IS_EMPTY";
    /**
     * 會員組參數值不可為空
     */
    public static final String CUSTOMER_GROUP_PARAMETER_VALUE_IS_EMPTY = "US_CUSTOMER_GROUP_PARAMETER_VALUE_IS_EMPTY";
    /**
     * 會員組參數名稱長度太長
     */
    public static final String CUSTOMER_GROUP_PARAMETER_NAME_IS_TOO_LONG = "US_CUSTOMER_GROUP_PARAMETER_NAME_IS_TOO_LONG";
    /**
     * 會員組參數值長度太長
     */
    public static final String CUSTOMER_GROUP_PARAMETER_VALUE_IS_TOO_LONG = "US_CUSTOMER_GROUP_PARAMETER_VALUE_IS_TOO_LONG";
    /**
     * 會員組名稱不可相同
     */
    public static final String CUSTOMER_GROUP_NAME_CANNOT_BE_SAME = "US_CUSTOMER_GROUP_NAME_CANNOT_BE_SAME";
    /**
     * 群組參數不存在
     */
    public static final String CUSTOMER_GROUP_PARAMETER_IS_NOT_EXISTED = "US_CUSTOMER_GROUP_PARAMETER_IS_NOT_EXISTED";

    /**
     * 群組類別重複
     */
    public static final String CUSTOMER_GROUP_TYPE_IS_EXISTED = "US_CUSTOMER_SAME_GROUP_NAME_AND_GROUP_TYPE_IS_EXISTED";
    /**
     * 群組類別不可為空
     */
    public static final String CUSTOMER_GROUP_TYPE_ID_IS_EMPTY = "US_CUSTOMER_GROUP_TYPE_ID_IS_EMPTY";
    /**
     * 群組類別不存在
     */
    public static final String CUSTOMER_GROUP_TYPE_NOT_EXIST_ERROR = "US_CUSTOMER_GROUP_TYPE_NOT_EXIST_ERROR";

    /**
     * 使用者已在此群組中
     */
    public static final String CUSTOMER_ALREADY_IN_GROUP_ERROR = "US_CUSTOMER_ALREADY_IN_GROUP_ERROR";
    /**
     * 使用者不在此群組中
     */
    public static final String CUSTOMER_NOT_IN_GROUP_ERROR = "US_CUSTOMER_NOT_IN_GROUP_ERROR";
    /**
     * 輸入的查詢參數錯誤
     */
    public static final String CUSTOMER_PARAMS_FORMAT_ERROR = "US_CUSTOMER_PARAMS_FORMAT_ERROR";

    /**
     * 代理樹層級超過最大層級
     */
    public static final String CUSTOMER_MAX_AGENT_LEVEL_ERROR = "MAX_AGENT_LEVEL_ERROR";

    /**
     * 客製化欄位名稱不可為空
     */
    public static final String CUSTOMER_DYNAMIC_NAME_IS_EMPTY = "US_CUSTOMER_DYNAMIC_NAME_IS_EMPTY";
    /**
     * 客製化欄位名稱不存在
     */
    public static final String CUSTOMER_DYNAMIC_NAME_IS_EXISTED = "US_CUSTOMER_DYNAMIC_NAME_IS_EXISTED";
    /**
     * 必填客製化欄位值不可為空
     */
    public static final String CUSTOMER_DYNAMIC_VALUE_IS_EMPTY = "US_CUSTOMER_DYNAMIC_VALUE_IS_EMPTY";
    /**
     * 此客製化欄位不存在
     */
    public static final String CUSTOMER_DYNAMIC_DEFINE_IS_NOT_FOUND = "US_CUSTOMER_DYNAMIC_DEFINE_IS_NOT_FOUND";
    /**
     * 客製化欄位值格式錯誤
     */
    public static final String CUSTOMER_DYNAMIC_VALUE_FORMAT_ERROR = "US_CUSTOMER_DYNAMIC_VALUE_FORMAT_ERROR";

    /**
     * Operator帳號已存在
     */
    public static final String OPERATOR_EXIST_ERROR = "US_OPERATOR_EXIST_ERROR";
    /**
     * Operator帳號不存在
     */
    public static final String OPERATOR_NOT_EXIST_ERROR = "US_OPERATOR_NOT_EXIST_ERROR";
    /**
     * Operator沒有所屬角色
     */
    public static final String OPERATOR_WITHOUT_ROLE_ERROR = "US_OPERATOR_WITHOUT_ROLE_ERROR";
    /**
     * Operator選單權限為空
     */
    public static final String OPERATOR_WITHOUT_MENU_PRIVILEGE_ERROR = "US_OPERATOR_WITHOUT_MENU_PRIVILEGE_ERROR";
    /**
     * Operator activeFlag 不是 Normal 狀態
     */
    public static final String OPERATOR_ACTIVE_FLAG_ERROR = "US_OPERATOR_ACTIVE_FLAG_ERROR";

    /**
     * 密码禁止登陆
     */
    public static  final String OPERATOR_LOGIN_PROHIBITED = "US_OPERATOR_ACTIVE_FLAG_PASSWORD_LOGIN_PROHIBITED_ERROR";
    /**
     * 後台管理員暱稱格式錯誤
     */
    public static final String OPERATOR_NICKNAME_ERROR = "US_OPERATOR_NICKNAME_ERROR";
    /**
     * 後台管理員帳號的必填欄位未填
     * 
     */
    public static final String OPERATOR_DETAILS_REQUIRE_ERROR = "US_OPERATOR_DETAILS_REQUIRE_ERROR";

    /**
     * systemId已使用
     */
    public static final String SYSTEM_ID_ALREADY_USED = "US_SYSTEM_ID_ALREADY_USED";

    /**
     * role不存在
     */
    public static final String ROLE_NOT_EXIST_ERROR = "US_ROLE_NOT_EXIST_ERROR";
    
    public static final String ROLES_NOT_EXIST_ERROR = "ROLES_NOT_EXIST_ERROR";
    /**
     * RoleName的 activeFlag 必須要是 Normal 才可複製
     */
    public static final String ROLE_ACTIVE_FLAG_OF_INPUT_MUST_BE_NORMAL_ERROR = "US_ROLE_ACTIVE_FLAG_OF_INPUT_MUST_BE_NORMAL_ERROR";
    /**
     * 不允許重複的 roleName
     */
    public static final String ROLE_NAME_ALREADY_EXIST_ERROR = "US_ROLE_NAME_ALREADY_EXIST_ERROR";
    /**
     * 依 roleId 刪除角色時，該角色必須要沒有 permission 關聯,否則拋Exception
     */
    public static final String ROLE_MUST_BE_UNCORELATED_WHEN_DELETING_ERROR = "US_ROLE_MUST_BE_UNCORELATED_WHEN_DELETING_ERROR";
    /**
     * role的 activeFlag 不是 Normal 狀態
     */
    public static final String ROLE_STATUS_ABNORMAL_ERROR = "US_ROLE_STATUS_ABNORMAL_ERROR";

    /**
     * merchant不存在
     */
    public static final String MERCHANT_NOT_EXIST_ERROR = "US_MERCHANT_NOT_EXIST_ERROR";
    /**
     * merchant parentMerchantId查不出對應的Merchant
     */
    public static final String MERCHANT_PARENT_NOT_EXIST_ERROR = "US_MERCHANT_PARENT_NOT_EXIST_ERROR";
    /**
     * 不允許重複的 merchant 名稱
     */
    public static final String MERCHANT_NAME_ALREADY_EXIST_ERROR = "US_MERCHANT_NAME_ALREADY_EXIST_ERROR";
    /**
     * updateMerchant只能接收狀態為 Normal的紀錄
     */
    public static final String MERCHANT_ACTIVE_FLAG_OF_INPUT_MUST_BE_NORMAL_ERROR = "US_MERCHANT_ACTIVE_FLAG_OF_INPUT_MUST_BE_NORMAL_ERROR";
    /**
     * merchant activeFlag 不是 Normal 狀態
     */
    public static final String MERCHANT_STATUS_ABNORMAL_ERROR = "US_MERCHANT_STATUS_ABNORMAL_ERROR";
    /**
     * 依 merchantId 刪除角色時，該角色必須要沒有 operator 關聯,否則拋Exception
     */
    public static final String MERCHANT_MUST_BE_UNCORELATED_WHEN_DELETING_ERROR = "US_MERCHANT_MUST_BE_UNCORELATED_WHEN_DELETING_ERROR";
    /**
     * Merchant名稱不可為空白
     */
    public static final String MERCHANT_NAME_MUST_NOT_BE_EMPTY_ERROR = "US_MERCHANT_NAME_MUST_NOT_BE_EMPTY_ERROR";
    /**
     * 已相同的指派記錄
     */
    public static final String MERCHANT_ASSIGN_DUPLICATED_ERROR = "US_MERCHANT_ASSIGN_DUPLICATED_ERROR";

    /**
     * 權限資源內容不存在
     */
    public static final String MENU_NOT_EXIST_ERROR = "US_MENU_NOT_EXIST_ERROR";
    /**
     * 有role已分配此選單的權限則不可刪除
     */
    public static final String MENU_BELONG_TO_ROLE_ERROR = "US_MENU_BELONG_TO_ROLE_ERROR";
    /**
     * 權限資源內容已存在,不可重複
     */
    public static final String MENU_ALREADY_EXIST = "US_MENU_ALREADY_EXIST";
    /**
     * 父節點不存在
     */
    public static final String MENU_PARENT_NOT_EXIST_ERROR = "US_MENU_PARENT_NOT_EXIST_ERROR";
    /**
     * 關連不存在
     */
    public static final String CORRELATION_NOT_EXIST_ERROR = "US_CORRELATION_NOT_EXIST_ERROR";
    /**
     * 登入失敗
     */
    public static final String LOGIN_FAIL = "LOGIN_FAIL";


    /**
     * 需求參數不存在
     */
    public static final String PARAMETER_IS_REQUIRED = "PARAMETER_IS_REQUIRED";

    /**
     * 此下級不存在
     */
    public static final String SUBORDINATE_NOT_EXIST_ERROR = "US_SUBORDINATE_NOT_EXIST_ERROR";

    /**
     * 不能查询自己的订单
     */
    public static final String QUERY_LOGIN_USER_ORDER_IS_INVALID = "US_QUERY_LOGIN_USER_ORDER_IS_INVALID";

    /**
     * 非法的品牌名
     */
    public static final String CUSTOMER_ILLEGAL_MERCHANT_ERROR = "CUSTOMER_ILLEGAL_MERCHANT_ERROR";

    /**
     * Password expired.
     */
    public static final String PWD_EXPIRED_ERROR = "US_PASSWORD_EXPIRED_ERROR";

    /**
     * 推薦人和上級代理同時存在
     */
    public static final String REFER_AND_RECOMMENDERID_CAN_NOT_SIMULTANEOUS_EXIST = "REFER_AND_RECOMMENDERID_CAN_NOT_SIMULTANEOUS_EXIST";

    /**
     * Operator first login change pwd error.
     */
    public static final String FIRST_LOGIN_FORCE_CHANGE_PWD_ERROR = "FIRST_LOGIN_FORCE_CHANGE_PWD_ERROR";

    public static final String CUSTOMER_SECURITY_QUESTION_NOT_FOUND = "CUSTOMER_SECURITY_QUESTION_NOT_FOUND";

    public static final String CUSTOMER_SECURITY_QUESTION_NOT_MATCH = "CUSTOMER_SECURITY_QUESTION_NOT_MATCH";

    public static final String NOT_SUPPORT_THE_QUESTION = "NOT_SUPPORT_THE_QUESTION";

    public static final String GET_PWD_TOKEN_IS_INVALID = "GET_PASSWORD_TOKEN_IS_INVALID";

    public static final String CUSTOMER_AGENT_MAP_ALREADY_EXIST = "CUSTOMER_AGENT_MAP_ALREADY_EXIST";


    /**
     * CATEGORY 不存在
     */
    public static final String CATEGORY_NOT_EXIST_ERROR = "US_CATEGORY_NOT_EXIST_ERROR";
    
    public static final String OPERATOR_HAS_MULTIPLE_CATEGORY = "OPERATOR_HAS_MULTIPLE_CATEGORY_ERROR";

    /**
     * 依 categoryId 刪除category時，該category必須要沒有 role 關聯,否則拋Exception
     */
    public static final String CATEGORY_MUST_BE_UNCORELATED_WHEN_DELETING_ERROR = "US_CATEGORY_MUST_BE_UNCORELATED_WHEN_DELETING_ERROR";

    public static final String WORKFLOW_IN_PROCESS = "WORKFLOW_IN_PROCESS";
    
    public static final String WORKFLOW_NO_PERMISSION = "WORKFLOW_NO_PERMISSION";
    
    public static final String WORKFLOW_LOCKED_BY_OTHER_USER = "WORKFLOW_LOCKED_BY_OTHER_USER";
    
    public static final String WORKFLOW_TASK_ALREADY_CLOSE = "WORKFLOW_TASK_ALREADY_CLOSE";

    public static final String NEXT_STATE_ID_NOT_CORRECT = "NEXT_STATE_ID_NOT_CORRECT";

    public static final String WORKFLOW_TASK_EXISTED = "WORKFLOW_TASK_EXISTED";

    public static final String NOT_FOUND_MERCHANT_IN_HEADER = "NOT_FOUND_MERCHANT_IN_HEADER";

    public static final String TOKEN_IS_INVALID = "TOKEN_IS_INVALID";

    public static final String CUSTOMER_NOT_LOGIN = "CUSTOMER_NOT_LOGIN";

    public static final String CUSTOMER_INFO_IS_NULL = "CUSTOMER_INFO_IS_NULL";

    public static final String CUSTOMER_NOT_MATCH = "CUSTOMER_ID_NOT_MATCH";

    public static final String CUSTOMER_INFO_IS_INCOMPLETE = "CUSTOMER_INFO_IS_INCOMPLETE";

    public static final String ACCESS_REMOTE_API_FAIL = "ACCESS_REMOTE_API_FAIL";

    public static final String ENCRYPTION_REQUIRED = "ENCRYPTION_REQUIRED";

    public static final String FUNCTION_NOT_AVAILABLE = "FUNCTION_NOT_AVAILABLE";

    public static final String GENERIC_SYSTEM_ERROR = "UNKNOWN_SYSTEM_ERROR";

    public static final String INVALID_CAPTCHA = "INVALID_CAPTCHA";

    public static final String INVALID_PARAMETERS = "INVALID_PARAMETERS";

    public static final String OPERATOR_IS_NOT_MERCHANT = "OPERATOR_IS_NOT_MERCHANT";

    /** Feedback Error Codes */

    public static final String USERNAME_MAX_LENGTH = "USERNAME_MAX_LENGTH";

    public static final String CONTENT_MAX_LENGTH = "CONTENT_MAX_LENGTH";

    /** SMS ERROR CODES */

    public static final String ACCOUNT_FORMAT_ERROR = "ACCOUNT_FORMAT_ERROR";

    public static final String REFUSED_BY_SERVER = "REFUSED_BY_SERVER";

    public static final String PWD_INCORRECT = "PASSWORD_INCORRECT";

    public static final String PWD_LOCKED = "PASSWORD_LOCKED";

    public static final String PARAMETER_ERROR = "PARAMETER_ERROR";

    public static final String UNKNOWN_ACCOUNT = "UNKNOWN_ACCOUNT";

    public static final String ACCOUNT_LOCKED_EXPIRED = "ACCOUNT_LOCKED_EXPIRED";

    public static final String ACCOUNT_NOT_ACTIVE = "ACCOUNT_NOT_ACTIVE";

    public static final String CHANNEL_CANNOT_USE = "CHANNEL_CANNOT_USE";

    public static final String ACCOUNT_BALANCE_NOT_ENOUGH = "ACCOUNT_BALANCE_NOT_ENOUGH";

    public static final String INTERNAL_ERROR = "INTERNAL_ERROR";

    public static final String SERVICE_CHARGE_ERROR = "SERVICE_CHARGE_ERROR";

	/* Validation Error Codes */

    public static final String MAX_SEND_LIMIT = "MAX_SEND_LIMIT";

    public static final String VALIDATION_EXPIRED_CODE = "VALIDATION_EXPIRED_CODE";

    public static final String VALIDATION_MAX_ATTEMPT_EXCEEDED = "VALIDATION_MAX_ATTEMPT_EXCEEDED";

    public static final String VALIDATION_CODE_NOT_EXIST = "VALIDATION_CODE_NOT_EXIST";

    public static final String USER_VALIDATED = "USER_ALREADY_VERIFIED";
    
    public static final String DATA_NOT_FOUND = "DATA_NOT_FOUND";
    
    /* Helper Error Codes */

    public static final String HELPER_EXIST = "HELPER_EXIST";
    public static final String HELPER_NOT_FOUND = "HELPER_NOT_FOUND";
    public static final String HELPER_TEMP_NOT_FOUND = "HELPER_TEMP_NOT_FOUND";

    public static final String MENU_CATEGORY_NOT_EXIST_ERROR = "MENU_CATEGORY_NOT_EXIST_ERROR";
    public static final String MENU_CATEGORY_MENU_ALREADY_EXIST = "MENU_CATEGORY_MENU_ALREADY_EXIST";
    public static final String MENU_CATEGORY_SIZE_NOT_CORRECT_ERROR = "MENU_CATEGORY_SIZE_NOT_CORRECT_ERROR";
    public static final String MENU_CATEGORY_ALREADY_EXIST = "MENU_CATEGORY_ALREADY_EXIST";
    public static final String MENU_BUTTON_MUST_EXIST = "MENU_BUTTON_MUST_EXIST";
    public static final String ALERADY_BIND = "ALERADY_BIND";

    public static final String REVIEW_TEMPLATE_EXIST = "REVIEW_TEMPLATE_EXIST";
    public static final String REVIEW_TEMPLATE_NON_EXIST = "REVIEW_TEMPLATE_NON_EXIST";
    public static final String TEMPLATE_CONTENT_DUPLICATE = "TEMPLATE_CONTENT_DUPLICATE";
    public static final String TEMPLATE_DETAIL_EMPTY = "TEMPLATE_DETAIL_EMPTY";
    public static final String TEMPLATE_NAME_DUPLICATE = "TEMPLATE_NAME_DUPLICATE";

    public static final String JSON_ERR = "JSON_ERR";

    // 金额不对
	public static final String AMOUNT_INVALID = "AMOUNT_INVALID";

	public static final String OPERATOR_NAME_ERROR = "OPERATOR_NAME_ERROR";
    
	private AdminErrorCode() {
		throw new IllegalStateException("Utility class");
	}
}
