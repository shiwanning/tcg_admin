package com.tcg.admin.service.impl;


import java.math.BigDecimal;
import java.util.*;

import com.tcg.admin.common.helper.RequestHelper;
import com.tcg.admin.persistence.springdata.*;
import com.tcg.admin.to.*;
import com.tcg.admin.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
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
import com.tcg.admin.service.MerchantService;
import com.tcg.admin.service.OperatorService;
import com.tcg.admin.service.RoleService;
import weblogic.socket.Login;


@Service
@Transactional
public class OperatorServiceImpl implements OperatorService {

    private static final String DEFAULT_DWP = "123456";
    
    private static final Logger LOGGER = LoggerFactory.getLogger(OperatorServiceImpl.class);

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
    private IBehaviorLogRepository iBehaviorLogRepository;

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

    @Autowired
    private IBehaviorLogRepository behaviorLogRepository;


    @Autowired
    private IMerchantRepository merchantRepository;

    @Autowired
    private  OperatorLoginService operatorLoginService;

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

            saveModel.setCreateOperator(RequestHelper.getCurrentUser().getUser().getOperatorName());
            saveModel.setIsNeedReset(1);

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
            throw new AdminServiceBaseException(AdminErrorCode.UNKNOWN_ERROR, e.getMessage(), e);
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
            throw new AdminServiceBaseException(AdminErrorCode.UNKNOWN_ERROR, e.getMessage(), e);
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
            throw new AdminServiceBaseException(AdminErrorCode.UNKNOWN_ERROR, e.getMessage(), e);
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
    public Operator changeActiveFlag(String operatorName, int activeFlag) {
        Operator operator = operatorRepository.findByOperatorName(operatorName);

        try {
            if (operator == null || operator.getActiveFlag().equals(LoginConstant.ACTIVE_FLAG_DELETE.getStatusCode())) {
                throw new AdminServiceBaseException(AdminErrorCode.OPERATOR_NOT_EXIST_ERROR, "Operator does not exist.");
            }
            //[activeFlag = 2 => can login in 30min ]
            operator.setActiveFlag(activeFlag);
            operator.setErrorCount(0);
            operator.setGoogleErrorCount(0);
            operator.setErrorTime(null);
            operator.setUpdateOperator(RequestHelper.getCurrentUser().getUser().getOperatorName());
            operator.setUpdateTime(new Date());

            operatorLoginService.removeUserInfo(operator.getOperatorName());

            return operatorRepository.saveAndFlush(operator);
        } catch (Exception e) {
            throw new AdminServiceBaseException(AdminErrorCode.UNKNOWN_ERROR, e.getMessage(), e);
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
            operator.setUpdateOperator(RequestHelper.getCurrentUser().getUser().getOperatorName());
            operator.setUpdateTime(new Date());
            operatorRepository.save(operator);

        } catch (AdminServiceBaseException usbe) {
            throw usbe;
        } catch (Exception e) {
            throw new AdminServiceBaseException(AdminErrorCode.UNKNOWN_ERROR, e.getMessage(), e);
        }

    }

    /**
     * 5. reset Password 用户管理 - 重置用户密码
     * @param username
     *            key to find operator's detail.
     * @throws AdminServiceBaseException
     */
    @Override
    public Map<String, String> resetPassword(String username) {
        Map<String, String> resetPassword = new HashMap<String, String>();
        try {
            Operator tempOperator = operatorRepository.findByOperatorName(username);
            if (tempOperator == null) {
                throw new AdminServiceBaseException(AdminErrorCode.OPERATOR_NOT_EXIST_ERROR, "Operator not found!");
            }

            if (tempOperator.getActiveFlag() == LoginConstant.ACTIVE_FLAG_DELETE.getStatusCode()) {
                throw new AdminServiceBaseException(AdminErrorCode.OPERATOR_ACTIVE_FLAG_ERROR, "Operator active flag is forbidden!");
            }

            OperatorProfile profile = tempOperator.getProfile();
            profile.setPasswdLastModifyDate(new Date());


            LOGGER.info("Resetting operator {}'s password!!", username);
            String pwd = makeRandomPassword();
            String storagePwd = MD5Utils.encrypt(pwd);
            tempOperator.setPassword(storagePwd);
            tempOperator.setUpdateOperator(RequestHelper.getCurrentUser().getUser().getOperatorName());
            tempOperator.setIsNeedReset(1);
            tempOperator.setUpdateTime(new Date());

            // 重置密码儲存修改密碼記錄
            OperatorPasswordHistory oph = new OperatorPasswordHistory();
            oph.setCreateTime(new Date());
            oph.setOperatorName(username);
            oph.setPassword(storagePwd);
            oph.setIsReset(1);//重置密码
            operatorPasswordHistoryRepository.save(oph);

            operatorRepository.save(tempOperator);
//            operatorProfileRepository.save(one);

            resetPassword.put("userName",username );
            resetPassword.put("password",pwd);
        } catch (AdminServiceBaseException usbe) {
            throw usbe;
        } catch (Exception e) {
            throw new AdminServiceBaseException(AdminErrorCode.UNKNOWN_ERROR, e.getMessage(), e);
        }
        return  resetPassword;
    }

    private static String makeRandomPassword(){
        char charr[] = "abcdefghijklmnopqrstuvwxyz!@#$%^&*ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890".toCharArray();


        String reg = "^(?:(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[^A-Za-z0-9])).{8,16}$";
        Random r = new Random();
        StringBuilder sb = null;
        while(true){
            sb = new StringBuilder();
            for (int x = 0; x < (Math.random()*8 + 8); ++x) {
                sb.append(charr[r.nextInt(charr.length)]);
            }
            if(sb.toString().matches(reg)){
                break;
            }
        }

        return sb.toString();
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
            LOGGER.info("Update operator {}'s nickname to {} and email {} and Phone {}!!", tempOperator.getOperatorName(), operator.getNickname(), operator.getProfile().getEmail(), operator.getProfile().getPhone());

            tempOperator.setNickname(operator.getNickname());
            tempOperator.getProfile().setEmail(operator.getProfile().getEmail());
            tempOperator.getProfile().setPhone(operator.getProfile().getPhone());
            tempOperator.getProfile().setPageSize(operator.getProfile().getPageSize());
            tempOperator.getProfile().setNotiSound(operator.getProfile().getNotiSound());

            operatorRepository.saveAndFlush(tempOperator);

        } catch (AdminServiceBaseException usbe) {
            throw usbe;
        } catch (Exception e) {
            throw new AdminServiceBaseException(AdminErrorCode.UNKNOWN_ERROR, e.getMessage(), e);
        }
    }


    @Override
    public List<Integer> querySubscriptionMerchant(Integer userId) {
        Operator byOperatorId = operatorRepository.findByOperatorId(userId);
        if(byOperatorId == null){
            throw new AdminServiceBaseException(AdminErrorCode.USER_NOT_EXIST);
        }
        NoneAdminInfo noneAdminInfo = merchantService.checkAdmin(true);
        return  merchantService.querySubscriptionMerchant(noneAdminInfo.getOperatorId(), noneAdminInfo.isAdmin(), byOperatorId.getOperatorId());

    }

    public QueryJsonTO queryOperator2(String operatorName, Integer roleId, Integer activeFlag, Integer merchantId, String baseMerchantCode, String lastLoginIP, PageableTO pageableTo) {
        QueryJsonTO json = new QueryJsonTO();
        List<QueryOperatorsTO> queryList = new ArrayList<>();
        Merchants merchants;
        Roles roles;
        Integer defaultLength = 20;


        Integer start = (pageableTo.getPageNo()-1)*pageableTo.getPageSize();
        Integer end = pageableTo.getPageNo() * pageableTo.getPageSize();
        List<Object[]> dataList;
        java.text.DateFormat format1 = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Map<String, Object> resultMap = new HashMap<>();
        try {

            NoneAdminInfo noneAdminInfo = merchantService.checkAdmin(true);
            resultMap = operatorRepositoryCustom.findAll2(noneAdminInfo, operatorName, roleId, activeFlag, merchantId , baseMerchantCode , lastLoginIP, SortTo.of(pageableTo.getSortBy(), pageableTo.getSortOrderBy()), start, end);
            dataList = (List<Object[]>)resultMap.get("list");
            for(Object[] obj : dataList){

                boolean isTransferDate = !(ObjectUtils.toString(obj[5]) == null || "".equals(ObjectUtils.toString(obj[5])));
                QueryOperatorsTO queryOperatorsTO = new QueryOperatorsTO();
                queryOperatorsTO.setOperatorId(Integer.parseInt(ObjectUtils.toString(obj[0])));
                queryOperatorsTO.setOperatorName( ObjectUtils.toString(obj[1]));
                queryOperatorsTO.setNickname(ObjectUtils.toString(obj[2]));
                queryOperatorsTO.setActiveFlag(Integer.parseInt(ObjectUtils.toString(obj[3])));
                queryOperatorsTO.setBaseMerchantCode(ObjectUtils.toString(obj[4]));
                queryOperatorsTO.setLastLoginTime(isTransferDate ? format1.parse(ObjectUtils.toString(obj[5])): null);
                queryOperatorsTO.setLastLoginIP(ObjectUtils.toString(obj[6]));
                queryOperatorsTO.setGoogleAuthStatus(obj[7]== null ? null: Integer.parseInt(ObjectUtils.toString(obj[7])));
                List<String> merchNames = new ArrayList<>(Arrays.asList(ObjectUtils.toString(obj[8]).split(",")));
                queryOperatorsTO.setMerchantNames(StringTools.listToString(merchNames));
                //set up merchant for each user
                merchants = new Merchants();
                List<String> merchIds = new ArrayList<>(Arrays.asList(ObjectUtils.toString(obj[9]).split(",")));
                merchants.setMerchantIds(merchIds);
                merchants.setMerchantNames(merchNames);
                queryOperatorsTO.setMerchants(merchants);

                if(queryOperatorsTO.getMerchantNames().length() > defaultLength+1 ) {
                    queryOperatorsTO.setMerchantNamesForTb(queryOperatorsTO.getMerchantNames().substring(0, defaultLength).concat("..."));
                }else{
                    queryOperatorsTO.setMerchantNamesForTb(queryOperatorsTO.getMerchantNames());
                }

                queryOperatorsTO.setRoleNames(ObjectUtils.toString(obj[10]));
                //set up roles for each operator
                List<String> roleIds = new ArrayList<>(Arrays.asList(ObjectUtils.toString(obj[11]).split(","))); //roleIdList
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
            throw new AdminServiceBaseException(AdminErrorCode.UNKNOWN_ERROR, ex.getMessage(), ex);
        }

        ArrayList<QueryOperatorsTO> returnList = new ArrayList<>();
//        if(!queryList.isEmpty()) {
//            for (int i = (pageableTo.getPageNo() - 1) * pageableTo.getPageSize(); i < pageableTo.getPageNo() * pageableTo.getPageSize(); i++) {
//                if (i < queryList.size())
//                    returnList.add(queryList.get(i));
//            }
//        }
        for(QueryOperatorsTO po: queryList){
            returnList.add(po);
        }
        BigDecimal total = (BigDecimal) resultMap.get("total");
        json.setTotalCount(total.intValue()); //totalCount
        json.setQueryList(returnList); //returnList
        return json;
    }

    @Override
    public void changeOperatorPassword(String username, String oldPassword, String newPassword, String confirmPassword, boolean isReset) {

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

        String reg = "^(?:(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[^A-Za-z0-9])).{8,16}$";
        if(!newPassword.matches(reg)){
            AdminServiceBaseException use = new AdminServiceBaseException(AdminErrorCode.NEW_PWD_FORMAT_ERROR,
                    "New Password length can't be shorter than 6 or greater than 16!");
        }
        // 新密码不能为123456
        if("123456".equals(newPassword)){
            AdminServiceBaseException use = new AdminServiceBaseException(AdminErrorCode.PROHIBIT_PASSWORD_123456,
                    "New Password can't be 123456!");
            LOGGER.error(use.getMessage());
            throw use;
        }

        Operator operator = queryByOperatorName(username);
        OperatorProfile operatorProfile = operator == null ? null : operator.getProfile();

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
        operator.setIsNeedReset(0);
        AuthorizationUtils.putIsNeedReset(operator.getOperatorId(), false);

        operatorProfile.setPasswdLastModifyDate(new Date());

//        operatorProfileRepository.save(operatorProfile);
        operatorRepository.save(operator);

        // 儲存修改密碼記錄
        OperatorPasswordHistory oph = new OperatorPasswordHistory();
        oph.setCreateTime(new Date());
        oph.setOperatorName(username);
        oph.setPassword(newPasswordMd5);
        oph.setIsReset(0);
        operatorPasswordHistoryRepository.save(oph);

        if(!AuthorizationUtils.isNeedOperation(operator.getOperatorId())){
            updateBehaviorLogLogSuccess(username, operator.getProfile().getLastLoginTime());
        }
    }

    @Override
    public Operator queryByOperatorName(String operatorName) {
        try {
            return operatorRepository.findByOperatorName(operatorName);
        } catch (Exception ex) {
            throw new AdminServiceBaseException(AdminErrorCode.UNKNOWN_ERROR, ex.getMessage(), ex);
        }
    }


    @Override
    public BehaviorLog getCurrentLoginBehaviorLog(String userName) {
        List<BehaviorLog> currentLoginBehaviorLog = iBehaviorLogRepository.getCurrentLoginBehaviorLog(userName);
        if(currentLoginBehaviorLog.size() > 0){
            return currentLoginBehaviorLog.get(0);
        }
        return null;
    }

    @Override
    public void updateThirtyNotLoginUser() {
        operatorRepository.updateThirtyNotLoginUser();
    }

    @Override
    public void updateUsOperatorByMisMerchantStatus(Integer active, String merchantCode) {

        UserInfo<Operator> userInfo = RequestHelper.getCurrentUser();
        String operatorName = userInfo.getUser().getOperatorName();

        Merchant byMerchantCode = merchantRepository.findByMerchantCode(merchantCode);
        if(byMerchantCode == null){
           throw new AdminServiceBaseException(AdminErrorCode.MERCHANT_NOT_EXIST_ERROR, " merchant not found!");
        }
        Integer status =  active == 0 ?
                LoginConstant.ACTIVE_FLAG_SYSTEM_LOGIN_PROHIBITED.getStatusCode()
                : LoginConstant.ACTIVE_FLAG_LOGIN_SUCCESS.getStatusCode();
        try {
            //将归属品牌下的用户禁用
            operatorRepository.updateUsOperatorByMisMerchantStatus(status, operatorName, merchantCode);



            if(status == LoginConstant.ACTIVE_FLAG_SYSTEM_LOGIN_PROHIBITED.getStatusCode()){
                //将归属品牌下的后台用户踢出
                List<Integer> operatorsByBaseMerchantId = operatorRepository.getOperatorsByBaseMerchantId(merchantCode);
                for(Integer kickId : operatorsByBaseMerchantId){
                    AuthInfo authInfo = AuthorizationUtils.getSessionAuthInfo(kickId);
                    if (null != authInfo && null != authInfo.getToken()) {
                        operatorLoginService.logout(authInfo.getToken());
                    }
                }
                //将订阅品牌的用户，取消订阅，不可逆
                //merchantOperatorRepository.deleteByMerchantCode(merchantCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @Override
    public List<BehaviorLogTo> getActivityLoginLog(String userName, Integer page, Integer pageSize) {

        Operator tempOperator = operatorRepository.findByOperatorName(userName);
        if (tempOperator == null) {
            throw new AdminServiceBaseException(AdminErrorCode.OPERATOR_NOT_EXIST_ERROR, "Operator not found!");
        }
        Pageable pageable = new PageRequest(page, pageSize);
        Page<BehaviorLog> rawLogs = activityLogPersistence.findActivityLogList(userName, 4, pageable);
        List<BehaviorLog> logLoginList = rawLogs.getContent();

        List<BehaviorLogTo> activityLogs = new ArrayList<>();

        for(BehaviorLog behaviorLog: logLoginList){
            BehaviorLogTo behaviorLogTo = new BehaviorLogTo();
            BeanUtils.copyProperties(behaviorLog, behaviorLogTo);
            if("127.0.01".equals(behaviorLog.getIp())){
                behaviorLogTo.setArea("localhost");
            }else{
                behaviorLogTo.setArea(behaviorLog.getIp() == null ? "" :IpUtils.getArea(behaviorLog.getIp()));
            }
            activityLogs.add(behaviorLogTo);
        }
        return activityLogs;
    }

    @Override
    public Map<String, Object> getOperatorProfile(String userName) {
        Operator tempOperator = operatorRepository.findByOperatorName(userName);
        if (tempOperator == null) {
            throw new AdminServiceBaseException(AdminErrorCode.OPERATOR_NOT_EXIST_ERROR, "Operator not found!");
        }
        OperatorProfile one = operatorProfileRepository.getOne(tempOperator.getOperatorId());
        if(one == null){
            throw new AdminServiceBaseException(AdminErrorCode.OPERATOR_NOT_EXIST_ERROR, "Operator not found!");
        }
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("operatorName", tempOperator.getOperatorName());
        resultMap.put("nickName", tempOperator.getNickname());
        resultMap.put("email", one.getEmail());
        resultMap.put("phone", one.getPhone());
        resultMap.put("createTime", tempOperator.getCreateTime());
        resultMap.put("updateTime", tempOperator.getUpdateTime());
        resultMap.put("creator", tempOperator.getCreateOperator());
        resultMap.put("updater", tempOperator.getUpdateOperator());
        return  resultMap;
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
	@Override
	public Map<String, Object> getActivityLog(String userName, Integer page, Integer pageSize, Integer actionType) {

        Map<String, Object>  resultMap = new HashMap<String, Object>();
        Operator tempOperator = operatorRepository.findByOperatorName(userName);
        if (tempOperator == null) {
            throw new AdminServiceBaseException(AdminErrorCode.OPERATOR_NOT_EXIST_ERROR, "Operator not found!");
        }
        Pageable pageable = new PageRequest(page, pageSize);
        Page<BehaviorLog> rawLogs = activityLogPersistence.findActivityLogList(userName, actionType, pageable);

        long totalElements = rawLogs.getTotalElements();

        resultMap.put("totalElements",totalElements );
        List<BehaviorLog> logList = rawLogs.getContent();

        List<ActivityLog> activityLogs = new ArrayList<>();

        List<Integer> resourceIdList = new ArrayList<>();

        for(BehaviorLog behaviorLog: logList){
            if(behaviorLog.getResourceId()!=null){
                resourceIdList.add(behaviorLog.getResourceId());
            }
        }

        if (CollectionUtils.isEmpty(resourceIdList)) {
        	for(BehaviorLog behaviorLog: logList){
                BehaviorLogTo behaviorLogTo = new BehaviorLogTo();
                BeanUtils.copyProperties(behaviorLog, behaviorLogTo);
                activityLogs.add(new ActivityLog(behaviorLogTo, null));
            }
            resultMap.put("activityLogs", activityLogs);
        	return resultMap;
        }
        	
        // 取得多語係列表
        Map<Integer, List<ApiLabel>> apMap = getApMapByResourceId(resourceIdList);

        for(BehaviorLog behaviorLog: logList){
            BehaviorLogTo behaviorLogTo = new BehaviorLogTo();
            BeanUtils.copyProperties(behaviorLog, behaviorLogTo);
            if(apMap.containsKey(behaviorLog.getResourceId())){
                activityLogs.add(new ActivityLog(behaviorLogTo, apMap.get(behaviorLog.getResourceId())));
            }else if(behaviorLog.getResourceType() != null && (behaviorLog.getResourceType() == 4 || behaviorLog.getResourceType() == 41)) {
                activityLogs.add(new ActivityLog(behaviorLogTo, null));
            }
        }
        resultMap.put("activityLogs", activityLogs);
        return resultMap;
    }

    private Map<Integer, List<ApiLabel>> getApMapByResourceId(List<Integer> resourceIdList) {
    	List<ApiLabel> labelList = apiLabelRepository.findByIds(resourceIdList);
        Map<Integer, List<ApiLabel>> apMap = Maps.newHashMap();

        for(ApiLabel apiLabel:labelList){
            List<ApiLabel> apList = Lists.newLinkedList();

            if(apMap.containsKey(apiLabel.getMenuId())){
                apList = apMap.get(apiLabel.getMenuId());
                apList.add(apiLabel);
            }else{
                apList.add(apiLabel);
            }
            apMap.put(apiLabel.getMenuId(),apList);
        }
		return apMap;
	}

	@Override
    public Operator queryByOperatorId(Integer operatorId) {
        try {
            return operatorRepository.findByOperatorId(operatorId);
        } catch (Exception ex) {
            throw new AdminServiceBaseException(AdminErrorCode.UNKNOWN_ERROR, ex.getMessage(), ex);
        }
    }


	@Override
	public Object findOperatorBySession(String token, boolean flag) {
		Map<String, Object> userProfile = new HashMap<>();
		UserInfo<Operator> userInfo = AuthorizationUtils.getSessionUser(token);
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
        UserInfo<Operator> userInfo = AuthorizationUtils.getSessionUser(token);
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
            tempOperator.setUpdateOperator(RequestHelper.getCurrentUser().getUser().getOperatorName());
            tempOperator.setUpdateTime(new Date());
            operatorRepository.saveAndFlush(tempOperator);
            
            //新增一個订阅
            if(merchantOperatorRepository.findByMerchantIdAndOperatorId(merchant.getMerchantId(), tempOperator.getOperatorId()) == null) {
            	MerchantOperator merchantOperatorRelation = new MerchantOperator();
                merchantOperatorRelation.setMerchantId(merchant.getMerchantId());
                merchantOperatorRelation.setOperatorId(tempOperator.getOperatorId());

                merchantOperatorRepository.saveAndFlush(merchantOperatorRelation);
            }

        } catch (AdminServiceBaseException usbe) {
            throw usbe;
        } catch (Exception e) {
            throw new AdminServiceBaseException(AdminErrorCode.UNKNOWN_ERROR, e.getMessage(), e);
        }
    }

    @Override
    public List<Operator> findOperatorByIds(List<Integer> operatorIds) {
        return operatorRepository.findByOperatorIdIn(operatorIds);
    }

    private void updateBehaviorLogLogSuccess(String userName, Date lastLoginTime){
        BehaviorLog currentLoginBehaviorLog = getCurrentLoginBehaviorLog(userName);
        if(currentLoginBehaviorLog != null){
            currentLoginBehaviorLog.setRemark("SUCCESS");
            currentLoginBehaviorLog.setEndProcessDate(lastLoginTime);
            behaviorLogRepository.saveAndFlush(currentLoginBehaviorLog);
        }
    }

    @Override
    public Map<String, Object> findOperatorByAccountName(String queryName) {
        Map<String, Object> userProfile = new HashMap<>();

        Operator queryOperator = operatorRepository.findByOperatorName(queryName);
        if (null == queryOperator) {
            LOGGER.info("can not find operator by operator name. operator name:{}",queryName);
            throw new AdminServiceBaseException(AdminErrorCode.OPERATOR_NOT_EXIST_ERROR);
        }
        List<Merchant> usMerchants = merchantService.getOperatorMerchant(queryOperator);
        List<String> merchantNames = new ArrayList<>();
        userProfile.put("operatorName", queryOperator.getOperatorName());
        userProfile.put("operatorId", queryOperator.getOperatorId());
        userProfile.put("operatorNickname", queryOperator.getNickname());
        userProfile.put("activeFlag", queryOperator.getActiveFlag());
        userProfile.put("phone", queryOperator.getProfile().getPhone());
        userProfile.put("baseMerchant", queryOperator.getBaseMerchantCode());
        for(Merchant usMerchant:usMerchants){
            merchantNames.add(usMerchant.getMerchantCode());
        }
        userProfile.put("merchants", merchantNames);
        return userProfile;
    }

}
