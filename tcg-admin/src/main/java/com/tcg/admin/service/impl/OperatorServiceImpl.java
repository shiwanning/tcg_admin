package com.tcg.admin.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.tcg.admin.common.constants.IErrorCode;
import com.tcg.admin.common.constants.LoginConstant;
import com.tcg.admin.common.error.AdminErrorCode;
import com.tcg.admin.common.exception.AdminServiceBaseException;
import com.tcg.admin.model.ApiLabel;
import com.tcg.admin.model.BehaviorLog;
import com.tcg.admin.model.Merchant;
import com.tcg.admin.model.MerchantOperator;
import com.tcg.admin.model.Operator;
import com.tcg.admin.model.OperatorPasswordHistory;
import com.tcg.admin.model.OperatorProfile;
import com.tcg.admin.model.Role;
import com.tcg.admin.model.RoleOperator;
import com.tcg.admin.persistence.ActivityLogPersistence;
import com.tcg.admin.persistence.OperatorPasswordHistoryRepositoryCustom;
import com.tcg.admin.persistence.OperatorRepositoryCustom;
import com.tcg.admin.persistence.springdata.IApiLabelRepository;
import com.tcg.admin.persistence.springdata.IMerchantOperatorRepository;
import com.tcg.admin.persistence.springdata.IOperatorPasswordHistoryRepository;
import com.tcg.admin.persistence.springdata.IOperatorProfileRepository;
import com.tcg.admin.persistence.springdata.IOperatorRepository;
import com.tcg.admin.persistence.springdata.IRoleOperatorRepository;
import com.tcg.admin.persistence.springdata.IRoleRepository;
import com.tcg.admin.service.MerchantService;
import com.tcg.admin.service.OperatorService;
import com.tcg.admin.service.RoleService;
import com.tcg.admin.to.ActivityLog;
import com.tcg.admin.to.Merchants;
import com.tcg.admin.to.NoneAdminInfo;
import com.tcg.admin.to.OperatorCreateTO;
import com.tcg.admin.to.OperatorsTO;
import com.tcg.admin.to.QueryJsonTO;
import com.tcg.admin.to.QueryOperatorsTO;
import com.tcg.admin.to.Roles;
import com.tcg.admin.to.SortTo;
import com.tcg.admin.to.UserInfo;
import com.tcg.admin.utils.MD5Utils;
import com.tcg.admin.utils.OpUtils;
import com.tcg.admin.utils.StringTools;

@Service
@Transactional
public class OperatorServiceImpl implements OperatorService {

    private static final String DEFAULT_DWP = "123456";
    
    private static final Logger LOGGER = LoggerFactory.getLogger(OperatorServiceImpl.class);
    
    @Autowired
    private OperatorLoginService operatorLoginService;

    @Autowired
    private IOperatorRepository operatorRepository;

    @Autowired
    private IOperatorProfileRepository operatorProfileRepository;

    @Autowired
    private OperatorRepositoryCustom operatorRepositoryCustom;

    @Autowired
    private IMerchantOperatorRepository merchantOperatorRepository;

    @Autowired
    private IRoleOperatorRepository roleOperatorRepository;

    @Autowired
    private IOperatorPasswordHistoryRepository operatorPasswordHistoryRepository;

    @Autowired
    private OperatorPasswordHistoryRepositoryCustom operatorPasswordHistoryRepositoryCustom;

    @Autowired
    private IRoleRepository roleRepository;

    @Autowired
    private RoleService roleService;

    @Autowired
    private IApiLabelRepository apiLabelRepository;

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private ActivityLogPersistence activityLogPersistence;

    /**
     * 2.add Operator
     * 创建用户
     * @param operator
     *            operator model that include mandatory fields which are
     *            username, password ,nickname and activeflag.
     * @return
     * @throws AdminServiceBaseException
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Operator addOperator(OperatorCreateTO operator) {
        try {
            // 帳號已存在
            if(operatorRepository.findByOperatorName(operator.getOperatorName()) != null) {
                throw new AdminServiceBaseException(AdminErrorCode.OPERATOR_EXIST_ERROR, "Operator name already existed");
            }
            //Base Merchant Code
            if(StringUtils.isEmpty(operator.getBaseMerchantCode())) {
                throw new AdminServiceBaseException(AdminErrorCode.OPERATOR_DETAILS_REQUIRE_ERROR, "One of the field is required");
            }

            // 後台管理員帳號必填欄位
            if (!OpUtils.verifyOperatorFields(operator)) {
                throw new AdminServiceBaseException(AdminErrorCode.OPERATOR_DETAILS_REQUIRE_ERROR, "One of the field is required");
            }

            // 後台管理員帳號格式
            if (!OpUtils.verifyOperatorName(operator.getOperatorName())) {
                throw new AdminServiceBaseException(AdminErrorCode.NAME_FORMAT_ERROR, "Operator name format is invalid");
            }


            // 後台管理員暱稱
            if (!OpUtils.verifyOperatorNickname(operator.getNickname())) {
                throw new AdminServiceBaseException(AdminErrorCode.OPERATOR_NICKNAME_ERROR, "Nickname format is invalid");
            }

            Operator saveModel = new Operator();
            // 後台管理使用狀態 = 1 : 可登入
            saveModel.setActiveFlag(LoginConstant.ACTIVE_FLAG_LOGIN_SUCCESS.getStatusCode());
            // 密碼加密 MD5
            saveModel.setPassword(MD5Utils.encrypt("123456")); //default
            saveModel.setOperatorName(operator.getOperatorName());
            saveModel.setNickname(operator.getNickname());
            List<MerchantOperator> merchantOperatorList = new ArrayList<>();

            saveModel.setBaseMerchantCode(operator.getBaseMerchantCode());
            //新增一個 operator-department 指派記錄
            saveModel = operatorRepository.saveAndFlush(saveModel);

            Integer operatorId = saveModel.getOperatorId();

            OperatorProfile profile = new OperatorProfile();
            profile.setOperatorId(operatorId);
            operatorProfileRepository.saveAndFlush(profile);

            MerchantOperator merchantOperatorRelation = new MerchantOperator();
            merchantOperatorRelation.setMerchantId(Integer.parseInt(operator.getMerchantId()));
            merchantOperatorRelation.setOperatorId(operatorId);
            merchantOperatorList.add(merchantOperatorRelation);

            merchantOperatorRepository.save(merchantOperatorList);

            /**
             * Assign roles
             */
            if(CollectionUtils.isNotEmpty(operator.getRoleIdList())){
                roleService.assignRoles(operatorId, operator.getRoleIdList());
            }

            return saveModel;

        } catch (AdminServiceBaseException usbe) {
            throw usbe;
        } catch (Exception e) {
            throw new AdminServiceBaseException(IErrorCode.UNKNOWN_EXCEPTION, e.getMessage(), e);
        }

    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Operator updateOperatorOfMerchant(OperatorCreateTO operator) {
        Operator saveModel = new Operator();
        try {
            saveModel = operatorRepository.findByOperatorName(operator.getOperatorName());
            if (LoginConstant.ACTIVE_FLAG_LOGIN_SUCCESS.getStatusCode() != saveModel.getActiveFlag()) {
                // 後台管理使用狀態 = 1 : 可登入
                saveModel.setActiveFlag(LoginConstant.ACTIVE_FLAG_LOGIN_SUCCESS.getStatusCode());
                saveModel = operatorRepository.saveAndFlush(saveModel);

                //新增一個 operator-merchant 指派記錄
                List<MerchantOperator> merchantOperatorList = new ArrayList<>();
                int deptSize = operator.getMerchantIdList().size();
                for(int i=0 ; i < deptSize; i++) {
                    MerchantOperator merchantOperatorRelation = new MerchantOperator();
                    merchantOperatorRelation.setMerchantId(operator.getMerchantIdList().get(i));
                    merchantOperatorRelation.setOperatorId(saveModel.getOperatorId());
                    merchantOperatorList.add(merchantOperatorRelation);
                }
                merchantOperatorRepository.save(merchantOperatorList);
            }
        } catch (Exception e) {
            throw new AdminServiceBaseException(IErrorCode.UNKNOWN_EXCEPTION, e.getMessage(), e);
        }
        return saveModel;
    }

    @Override
    public Operator addOperatorOfMerchant(OperatorCreateTO operator) {
        Operator saveModel = new Operator();
        try {
            // 帳號已存在
            int exist = operatorRepository.isOperatorExist(operator.getOperatorName());
            if(exist == 0) {
                // 後台管理使用狀態 = 7 : 登入禁用
                saveOperator(saveModel,operator,LoginConstant.ACTIVE_FLAG_DELETE.getStatusCode());
            }else{
                throw new AdminServiceBaseException(AdminErrorCode.OPERATOR_EXIST_ERROR, "Operator name already existed");
            }
        } catch (Exception e) {
            throw new AdminServiceBaseException(IErrorCode.UNKNOWN_EXCEPTION, e.getMessage(), e);
        }
        return saveModel;
    }

    private Operator saveOperator(Operator saveModel,OperatorCreateTO operator,Integer activeFlag){
        // 密碼加密 MD5
        saveModel.setPassword(MD5Utils.encrypt("123456")); //default
        saveModel.setOperatorName(operator.getOperatorName());
        saveModel.setNickname(operator.getNickname());
        saveModel.setActiveFlag(activeFlag);
        saveModel.setBaseMerchantCode(operator.getBaseMerchantCode());
        operatorRepository.saveAndFlush(saveModel);

        OperatorProfile profile = new OperatorProfile();
        profile.setOperatorId(saveModel.getOperatorId());
        operatorProfileRepository.saveAndFlush(profile);

        return saveModel;
    }

    /**
     * 3.change Active Flag
     * @param operatorName
     *            operator to change active flag.
     * @param activeFlag
     *            the active status, inactive or active.
     * @throws AdminServiceBaseException
     */
    @Override
    public void changeActiveFlag(String operatorName, int activeFlag) {
        Operator operator = operatorRepository.findByOperatorName(operatorName);

        try {
            if (operator == null || operator.getActiveFlag().equals(LoginConstant.ACTIVE_FLAG_DELETE.getStatusCode())) {
                throw new AdminServiceBaseException(AdminErrorCode.OPERATOR_NOT_EXIST_ERROR, "Operator does not exist.");
            }
            //[activeFlag = 2 => can login in 30min ]
            operator.setActiveFlag(activeFlag);
            operator.setErrorCount(0);
            operator.setErrorTime(null);

            operatorRepository.save(operator);
        } catch (Exception e) {
            throw new AdminServiceBaseException(IErrorCode.UNKNOWN_EXCEPTION, e.getMessage(), e);
        }
    }


    /**
     * 4.delete Operator
     * @param operatorId
     *            - operatorId
     * @throws AdminServiceBaseException
     */
    @Override
    public void deleteOperator(Integer operatorId) {
        try {
            // 帳號不存在
            if (!operatorRepository.exists(operatorId)) {
                throw new AdminServiceBaseException(AdminErrorCode.OPERATOR_NOT_EXIST_ERROR, "Operator not found!");
            }

            // 帳號已刪除
            Operator operator = operatorRepository.findOne(operatorId);
            if (operator.getActiveFlag() == LoginConstant.ACTIVE_FLAG_DELETE.getStatusCode()) {
                throw new AdminServiceBaseException(AdminErrorCode.OPERATOR_ACTIVE_FLAG_ERROR, "Operator already deleted!");
            }

            // Update to deleted status.
            operator.setActiveFlag(LoginConstant.ACTIVE_FLAG_DELETE.getStatusCode());
            operatorRepository.save(operator);

        } catch (AdminServiceBaseException usbe) {
            throw usbe;
        } catch (Exception e) {
            throw new AdminServiceBaseException(IErrorCode.UNKNOWN_EXCEPTION, e.getMessage(), e);
        }

    }

    /**
     * 5. reset Password 用户管理 - 重置用户密码
     * @param username
     *            key to find operator's detail.
     * @throws AdminServiceBaseException
     */
    @Override
    public void resetPassword(String username) {
        try {
            Operator tempOperator = operatorRepository.findByOperatorName(username);
            if (tempOperator == null) {
                throw new AdminServiceBaseException(AdminErrorCode.OPERATOR_NOT_EXIST_ERROR, "Operator not found!");
            }

            if (tempOperator.getActiveFlag() == LoginConstant.ACTIVE_FLAG_DELETE.getStatusCode()) {
                throw new AdminServiceBaseException(AdminErrorCode.OPERATOR_ACTIVE_FLAG_ERROR, "Operator active flag is forbidden!");
            }
            LOGGER.info("Resetting operator {}'s password!!", username);
            tempOperator.setPassword(MD5Utils.encrypt(DEFAULT_DWP));
            operatorRepository.save(tempOperator);

        } catch (AdminServiceBaseException usbe) {
            throw usbe;
        } catch (Exception e) {
            throw new AdminServiceBaseException(IErrorCode.UNKNOWN_EXCEPTION, e.getMessage(), e);
        }
    }

    /**
     * 6.Assign Role
     * 配置角色
     * @param operatorId
     * @param roleList
     * @throws AdminServiceBaseException
     */
    @Override
    public void assignRoles(Integer operatorId, List<Integer> roleList) {
        roleService.assignRoles(operatorId, roleList);
    }

    /**
     * 7.Search User
     * @param operatorId
     * @return
     * @throws AdminServiceBaseException
     */
    @Override
    public Operator findOperatorById(Integer operatorId) {
        return operatorRepository.findOne(operatorId);
    }

    @Override
    public Operator findOperatorByName(String operatorName) {
        return operatorRepository.findByOperatorName(operatorName);
    }

    /**
     * Update operator details. Nickname, Email, Password
     *
     * @param operator
     *
     * @return
     *
     * @throws AdminServiceBaseException
     */
    @Override
    public void updateOperator(Operator operator) {
        try {
            Operator tempOperator = operatorRepository.findByOperatorId(operator.getOperatorId());
            if (tempOperator == null) {
                throw new AdminServiceBaseException(AdminErrorCode.OPERATOR_NOT_EXIST_ERROR, "Operator not found!");
            }

            if (tempOperator.getActiveFlag() == LoginConstant.ACTIVE_FLAG_DELETE.getStatusCode()) {
                throw new AdminServiceBaseException(AdminErrorCode.OPERATOR_ACTIVE_FLAG_ERROR, "Operator active flag is forbidden!");
            }
            LOGGER.info("Update operator {}'s nickname to {} and email {}!!", tempOperator.getOperatorName(), operator.getNickname(), operator.getProfile().getEmail());

            tempOperator.setNickname(operator.getNickname());
            tempOperator.getProfile().setEmail(operator.getProfile().getEmail());
            tempOperator.getProfile().setPageSize(operator.getProfile().getPageSize());
            tempOperator.getProfile().setNotiSound(operator.getProfile().getNotiSound());

            operatorRepository.saveAndFlush(tempOperator);

        } catch (AdminServiceBaseException usbe) {
            throw usbe;
        } catch (Exception e) {
            throw new AdminServiceBaseException(IErrorCode.UNKNOWN_EXCEPTION, e.getMessage(), e);
        }
    }

    public QueryJsonTO queryOperator2(String operatorName, Integer roleId, Integer activeFlag, Integer merchantId, String baseMerchantCode, Integer offset, Integer length, String sortBy, String sortOrder) {
        QueryJsonTO json = new QueryJsonTO();
        List<QueryOperatorsTO> queryList = new ArrayList<>();
        Merchants merchants;
        Roles roles;
        Integer defaultLength = 20;

        List<Object[]> dataList;
        try {

            NoneAdminInfo noneAdminInfo = merchantService.checkAdmin(true);
            dataList = operatorRepositoryCustom.findAll2(noneAdminInfo, operatorName, roleId, activeFlag, merchantId , baseMerchantCode , SortTo.of(sortBy, sortOrder));
            for(Object[] obj : dataList){
                QueryOperatorsTO queryOperatorsTO = new QueryOperatorsTO();
                queryOperatorsTO.setOperatorId(Integer.parseInt(ObjectUtils.toString(obj[0])));
                queryOperatorsTO.setOperatorName( ObjectUtils.toString(obj[1]));
                queryOperatorsTO.setNickname(ObjectUtils.toString(obj[2]));
                queryOperatorsTO.setActiveFlag(Integer.parseInt(ObjectUtils.toString(obj[3])));
                queryOperatorsTO.setBaseMerchantCode(ObjectUtils.toString(obj[4]));
                List<String> merchNames = new ArrayList<>(Arrays.asList(ObjectUtils.toString(obj[5]).split(",")));
                queryOperatorsTO.setMerchantNames(StringTools.listToString(merchNames));
                //set up merchant for each user
                merchants = new Merchants();
                List<String> merchIds = new ArrayList<>(Arrays.asList(ObjectUtils.toString(obj[6]).split(",")));
                merchants.setMerchantIds(merchIds);
                merchants.setMerchantNames(merchNames);
                queryOperatorsTO.setMerchants(merchants);

                if(queryOperatorsTO.getMerchantNames().length() > defaultLength+1 ) {
                    queryOperatorsTO.setMerchantNamesForTb(queryOperatorsTO.getMerchantNames().substring(0, defaultLength).concat("..."));
                }else{
                    queryOperatorsTO.setMerchantNamesForTb(queryOperatorsTO.getMerchantNames());
                }

                queryOperatorsTO.setRoleNames(ObjectUtils.toString(obj[7]));
                //set up roles for each operator
                List<String> roleIds = new ArrayList<>(Arrays.asList(ObjectUtils.toString(obj[8]).split(","))); //roleIdList
                roles = new Roles();
                roles.setRoleIds(roleIds);
                queryOperatorsTO.setRoles(roles);
                if(queryOperatorsTO.getRoleNames().length() > defaultLength+1 ) {
                    queryOperatorsTO.setRoleNamesForTb(queryOperatorsTO.getRoleNames().substring(0, defaultLength).concat("..."));
                }else{
                    queryOperatorsTO.setRoleNamesForTb(queryOperatorsTO.getRoleNames());
                }
                queryList.add(queryOperatorsTO);

            }
        } catch (Exception ex) {
            throw new AdminServiceBaseException(IErrorCode.UNKNOWN_EXCEPTION, ex.getMessage(), ex);
        }

        ArrayList<QueryOperatorsTO> returnList = new ArrayList<>();
        if(!queryList.isEmpty()) {
            for (int i = (offset - 1) * length; i < offset * length; i++) {
                if (i < queryList.size())
                    returnList.add(queryList.get(i));
            }
        }
        json.setTotalCount(queryList.size()); //totalCount
        json.setQueryList(returnList); //returnList
        return json;
    }

    @Override
    public void changeOperatorPassword(String username, String oldPassword, String newPassword, String confirmPassword) {

        try {
            // 新密碼不可為空
            if (StringUtils.isBlank(newPassword)) {
                AdminServiceBaseException use = new AdminServiceBaseException(AdminErrorCode.NEW_PWD_EMPTY_ERROR, "New password can't be empty");
                LOGGER.error(use.getMessage());
                throw use;
            }

            // 確認密碼不可為空
            if (StringUtils.isBlank(confirmPassword)) {
                AdminServiceBaseException use =
                        new AdminServiceBaseException(AdminErrorCode.COFIRM_PWD_EMPTY_ERROR, "Confirm password can't be empty");
                LOGGER.error(use.getMessage());
                throw use;
            }

            // 舊密碼不可為空
            if (StringUtils.isBlank(oldPassword)) {
                AdminServiceBaseException use = new AdminServiceBaseException(AdminErrorCode.OLD_PWD_EMPTY_ERROR, "Old password can't be empty");
                LOGGER.error(use.getMessage());
                throw use;
            }

            // 舊密碼需小於6碼 , 大於16碼
            if (!OpUtils.verifyPassword(oldPassword)) {
                AdminServiceBaseException use = new AdminServiceBaseException(AdminErrorCode.OLD_PWD_FORMAT_ERROR,
                                                                              "Old Password length can't be shorter than 6 or greater than 16!");
                LOGGER.error(use.getMessage());
                throw use;
            }
            // 新密碼需小於6碼 , 大於16碼
            if (!OpUtils.verifyPassword(newPassword)) {
                AdminServiceBaseException use = new AdminServiceBaseException(AdminErrorCode.NEW_PWD_FORMAT_ERROR,
                                                                              "New Password length can't be shorter than 6 or greater than 16!");
                LOGGER.error(use.getMessage());
                throw use;
            }

            Operator operator = queryByOperatorName(username);
            OperatorProfile operatorProfile = operator == null ? null : operatorProfileRepository.findOne(operator.getOperatorId());

            // 帳號是否存在
            if (operator == null || operator.getActiveFlag() == LoginConstant.ACTIVE_FLAG_DELETE.getStatusCode()) {
                AdminServiceBaseException use =
                        new AdminServiceBaseException(AdminErrorCode.OPERATOR_NOT_EXIST_ERROR, "The username is not existed.");
                LOGGER.error(use.getMessage());
                throw use;
            }

            // 輸入的舊密碼不符
            if (!operator.getPassword().equals(MD5Utils.encrypt(oldPassword))) {
                AdminServiceBaseException use = new AdminServiceBaseException(AdminErrorCode.INCORRECT_PWD_ERROR, "Old password is incorrect.");
                LOGGER.error(use.getMessage());
                throw use;
            }

            // 新密碼與舊密碼不可相同
            if (newPassword.equals(oldPassword)) {
                AdminServiceBaseException use = new AdminServiceBaseException(AdminErrorCode.NEW_OLD_PWD_IS_SAME_ERROR,
                                                                              "New password and Old password can't be the same.");
                LOGGER.error(use.getMessage());
                throw use;
            }

            // 新密碼與確認密碼須一致
            if (!newPassword.equals(confirmPassword)) {
                AdminServiceBaseException use = new AdminServiceBaseException(AdminErrorCode.NEW_CONFIRM_PWD_IS_DIFF_ERROR,
                                                                              "New password and Confirm password is different.");
                LOGGER.error(use.getMessage());
                throw use;
            }

            String newPasswordMd5 = MD5Utils.encrypt(newPassword);

            // 新密碼不可和過去5次的修改記錄一樣
            List<OperatorPasswordHistory> passwordHistory = operatorPasswordHistoryRepositoryCustom.findLastPasswordHistory(username, 5);
            boolean pass = true;
            for (OperatorPasswordHistory oph : passwordHistory) {
                if (oph.getPassword().equals(newPasswordMd5)) {
                    pass = false;
                    break;
                }
            }
            if (!pass) {
                AdminServiceBaseException use = new AdminServiceBaseException(AdminErrorCode.NEW_PWD_EXISTS_HISTORY_ERROR,
                                                                              "New password can't be the same as the last 5 history records.");
                LOGGER.error(use.getMessage());
                throw use;
            }

            operator.setPassword(newPasswordMd5);
            operatorRepository.save(operator);

            // 儲存修改密碼記錄
            OperatorPasswordHistory oph = new OperatorPasswordHistory();
            oph.setCreateTime(new Date());
            oph.setOperatorName(username);
            oph.setPassword(newPasswordMd5);
            operatorPasswordHistoryRepository.save(oph);
            
            operatorProfile.setPasswdLastModifyDate(new Date());
            operatorProfileRepository.save(operatorProfile);

        } catch (AdminServiceBaseException usbe) {
            throw usbe;
        } catch (Exception e) {
            throw new AdminServiceBaseException(IErrorCode.UNKNOWN_ERROR, e.getMessage(), e);
        }
    }

    @Override
    public Operator queryByOperatorName(String operatorName) {
        try {
            return operatorRepository.findByOperatorName(operatorName);
        } catch (Exception ex) {
            throw new AdminServiceBaseException(IErrorCode.UNKNOWN_EXCEPTION, ex.getMessage(), ex);
        }
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
	@Override
	public List<ActivityLog> getActivityLog(UserInfo<Operator> userInfo, Integer page, Integer pageSize, Integer actionType) {
        Pageable pageable = new PageRequest(page, pageSize);
        Page<BehaviorLog> rawLogs = activityLogPersistence.findActivityLogList(userInfo.getUser().getOperatorName(), actionType, pageable);
        List<BehaviorLog> logList = rawLogs.getContent();

        List<ActivityLog> activityLogs = new ArrayList<>();

        List<Integer> resourceIdList = new ArrayList<>();

        for(BehaviorLog behaviorLog: logList){
            if(behaviorLog.getResourceId()!=null){
                resourceIdList.add(behaviorLog.getResourceId());
            }
        }

        if(!CollectionUtils.isEmpty(resourceIdList)){
            List<ApiLabel> labelList = apiLabelRepository.findByIds(resourceIdList);
            Map<Integer, List<ApiLabel>> apMap = new HashMap<>();

            for(ApiLabel apiLabel:labelList){
                List<ApiLabel> apList = new ArrayList<>();

                if(apMap.containsKey(apiLabel.getMenuId())){
                    apList = apMap.get(apiLabel.getMenuId());
                    apList.add(apiLabel);
                }else{
                    apList.add(apiLabel);
                }
                apMap.put(apiLabel.getMenuId(),apList);
            }

            for(BehaviorLog behaviorLog: logList){
                if(apMap.containsKey(behaviorLog.getResourceId())){
                    activityLogs.add(new ActivityLog(behaviorLog, apMap.get(behaviorLog.getResourceId())));
                }else if(behaviorLog.getResourceType() != null && (behaviorLog.getResourceType() == 4 || behaviorLog.getResourceType() == 41)) {
                    activityLogs.add(new ActivityLog(behaviorLog, null));
                }
            }

        }else{
            for(BehaviorLog behaviorLog: logList){
                activityLogs.add(new ActivityLog(behaviorLog, null));
            }
        }

        return activityLogs;
    }

    @Override
    public Operator queryByOperatorId(Integer operatorId) {
        try {
            return operatorRepository.findByOperatorId(operatorId);
        } catch (Exception ex) {
            throw new AdminServiceBaseException(IErrorCode.UNKNOWN_EXCEPTION, ex.getMessage(), ex);
        }
    }


	@Override
	public Object findOperatorBySession(String token, boolean flag) {
		Map<String, Object> userProfile = new HashMap<>();
		UserInfo<Operator> userInfo = operatorLoginService.getSessionUser(token);
		List<Merchant> usMerchants = merchantService.queryOperatorMerchants(flag);
		List<String> merchantNames = new ArrayList<>();
		userProfile.put("operatorName", userInfo.getUser().getOperatorName());
		userProfile.put("operatorId", userInfo.getUser().getOperatorId());
		userProfile.put("operatorNickname", userInfo.getUser().getNickname());
		for(Merchant usMerchant:usMerchants){
			merchantNames.add(usMerchant.getMerchantCode());
		}
		userProfile.put("merchant", merchantNames);
		return userProfile;
	}

    @Override
    public Operator findOperatorByToken(String token) {
        UserInfo<Operator> userInfo = operatorLoginService.getSessionUser(token);
        return userInfo.getUser();
    }

    /**
     * transfer operator data from old console
     *
     * @param op
     *
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Operator transferOperator(OperatorCreateTO op) {
        Operator operator = operatorRepository.findByOperatorName(op.getOperatorName());

        if(operator!=null) {
            return operator;
        }

        //get role id from oss (tac)
        List<Role> roleList = roleRepository.findByRoleNames(op.getRoleNameList());

        if(CollectionUtils.isEmpty(roleList)) {
            throw new AdminServiceBaseException(AdminErrorCode.ROLE_NOT_EXIST_ERROR,"Role not exist");
        }

        Operator saveModel = new Operator();
        // 後台管理使用狀態 = 1 : 可登入
        saveModel.setActiveFlag(LoginConstant.ACTIVE_FLAG_LOGIN_SUCCESS.getStatusCode());
        saveModel.setPassword(MD5Utils.encrypt(op.getPassword()));
        saveModel.setOperatorName(op.getOperatorName());
        saveModel.setNickname(op.getNickname());
        saveModel = operatorRepository.saveAndFlush(saveModel);

        OperatorProfile profile = new OperatorProfile();
        profile.setOperatorId(saveModel.getOperatorId());
        operatorProfileRepository.saveAndFlush(profile);

        //新增一個 operator-department 指派記錄
        List<MerchantOperator> merchantOperatorList = new ArrayList<>();

        for(int i : op.getMerchantIdList()) {
            MerchantOperator merchantOperatorRelation = new MerchantOperator();
            merchantOperatorRelation.setMerchantId(i);
            merchantOperatorRelation.setOperatorId(saveModel.getOperatorId());
            merchantOperatorList.add(merchantOperatorRelation);
        }

        merchantOperatorRepository.save(merchantOperatorList);

        List<RoleOperator> copyRoles = new ArrayList<>();
        for (Role role : roleList) {
            RoleOperator roleOperatorOfNewOperatorName = new RoleOperator();
            roleOperatorOfNewOperatorName.setRoleId(role.getRoleId());
            roleOperatorOfNewOperatorName.setOperatorId(saveModel.getOperatorId());
            copyRoles.add(roleOperatorOfNewOperatorName);
        }
        roleOperatorRepository.save(copyRoles);
        return saveModel;
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
	@Override
	public List<OperatorsTO> getOperatorsByActiveFlag(List<Integer> statusCodes) {
		List<OperatorsTO> op = Lists.newLinkedList();

		List<Object []> list = operatorRepository.getOperatorsByActiveFlag(statusCodes);

		if(list != null){
			for (Object[] obj : list){
				OperatorsTO opObj = new OperatorsTO();
				opObj.setOperatorId((Integer) obj[0]);
				opObj.setOperatorName((String) obj[1]);

				op.add(opObj);

			}
		}


		return op;
	}

    @Override
    public void updateBaseMerchant(OperatorCreateTO operatorCreateTO) {
        try {
            Operator tempOperator = operatorRepository.findByOperatorId(operatorCreateTO.getOperatorId());
            if (tempOperator == null) {
                throw new AdminServiceBaseException(AdminErrorCode.OPERATOR_NOT_EXIST_ERROR, "Operator not found!");
            }

            if (tempOperator.getActiveFlag() == LoginConstant.ACTIVE_FLAG_DELETE.getStatusCode()) {
                throw new AdminServiceBaseException(AdminErrorCode.OPERATOR_ACTIVE_FLAG_ERROR, "Operator active flag is forbidden!");
            }

            Merchant merchant = merchantService.getMerchant(Integer.parseInt(operatorCreateTO.getMerchantId()));
            if (merchant == null) {
                throw new AdminServiceBaseException(AdminErrorCode.MERCHANT_NOT_EXIST_ERROR, "Merchant not found!");
            }

            tempOperator.setBaseMerchantCode(merchant.getMerchantCode());

            operatorRepository.saveAndFlush(tempOperator);

        } catch (AdminServiceBaseException usbe) {
            throw usbe;
        } catch (Exception e) {
            throw new AdminServiceBaseException(IErrorCode.UNKNOWN_EXCEPTION, e.getMessage(), e);
        }
    }

    @Override
    public List<Operator> findOperatorByIds(List<Integer> operatorIds) {
        return operatorRepository.findByOperatorIdIn(operatorIds);
    }

}
