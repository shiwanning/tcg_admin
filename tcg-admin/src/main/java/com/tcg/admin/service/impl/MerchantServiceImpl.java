package com.tcg.admin.service.impl;

import java.util.*;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.tcg.admin.cache.MerchantCacheEvict;
import com.tcg.admin.cache.MerchantCacheable;
import com.tcg.admin.client.MisClientService;
import com.tcg.admin.common.constants.DepartmentConstant;
import com.tcg.admin.common.constants.LoginConstant;
import com.tcg.admin.common.constants.TaskStateConstant;
import com.tcg.admin.common.error.AdminErrorCode;
import com.tcg.admin.common.exception.AdminServiceBaseException;
import com.tcg.admin.common.helper.RequestHelper;
import com.tcg.admin.model.MenuCategory;
import com.tcg.admin.model.Merchant;
import com.tcg.admin.model.MerchantMenuCategorKey;
import com.tcg.admin.model.MerchantMenuCategory;
import com.tcg.admin.model.MerchantOperator;
import com.tcg.admin.model.Operator;
import com.tcg.admin.model.OperatorProfile;
import com.tcg.admin.model.RoleOperator;
import com.tcg.admin.model.Task;
import com.tcg.admin.persistence.RoleOperatorCustomRepository;
import com.tcg.admin.persistence.springdata.IMenuCategoryRepository;
import com.tcg.admin.persistence.springdata.IMerchantMenuCategoryRepository;
import com.tcg.admin.persistence.springdata.IMerchantOperatorRepository;
import com.tcg.admin.persistence.springdata.IMerchantRepository;
import com.tcg.admin.persistence.springdata.IOperatorProfileRepository;
import com.tcg.admin.persistence.springdata.IOperatorRepository;
import com.tcg.admin.persistence.springdata.IRoleOperatorRepository;
import com.tcg.admin.service.MerchantService;
import com.tcg.admin.service.OperatorService;
import com.tcg.admin.service.RoleMenuPermissionService;
import com.tcg.admin.service.RoleService;
import com.tcg.admin.service.WorkFlowService;
import com.tcg.admin.to.MerchantChargeTemplateTypeTO;
import com.tcg.admin.to.NoneAdminInfo;
import com.tcg.admin.to.OperatorCreateTO;
import com.tcg.admin.to.TaskTO;
import com.tcg.admin.to.UserInfo;
import com.tcg.admin.utils.MD5Utils;
import com.tcg.admin.utils.OpUtils;
import com.tcg.admin.utils.StringTools;

@Service
@Transactional
public class MerchantServiceImpl implements MerchantService {

    public static final Integer CATEGORY_SYSTEM = 1;
    public static final Integer ACTIVE = 1;
    public static final String MERCHANT_TYPE_ALL = "99";
    private static final Integer SUPER_MANAGER_ROLE_ID = 1;
    
    private static final Comparator<Merchant> MerchantComparator = new Comparator<Merchant>() {
        public int compare(Merchant o1, Merchant o2) {
            // write comparison logic here like below , it's just a sample 
            return o1.getMerchantCode().compareToIgnoreCase(o2.getMerchantCode());
        }
    };
    
    private List<Merchant> admMerchantList = new ArrayList<>();

    @Autowired
    private IOperatorRepository operatorRepository;

    @Autowired
    private IMerchantRepository merchantRepository;

    @Autowired
    private IMerchantOperatorRepository merchantOperatorRepository;

    @Autowired
    private IRoleOperatorRepository roleOperatorRepository;

    @Autowired
    private RoleOperatorCustomRepository roleOperatorCustomRepository;

    @Autowired
    private IOperatorProfileRepository operatorProfileRepository;

    @Autowired
    private OperatorService operatorService;

    @Autowired
    private WorkFlowService workFlowManager;

    @Autowired
    private RoleService roleService;

    @Autowired
    private IMerchantMenuCategoryRepository merchantMenuCategoryRepository;

    @Autowired
    private IMenuCategoryRepository menuCategoryRepository;
    
    @Autowired
    private RoleMenuPermissionService roleMenuPermissionService;
    
    @Autowired
    private MisClientService misClientService;

    @Override
    public List<Merchant> getMerchantList() {
        //取得user所屬的Merchants
        return this.queryOperatorMerchants(false);
    }

    @Override
    @MerchantCacheEvict(key="'getMerchants:' + #operator.operatorId")
    public void assignOperatorMerchants(Operator operator, List<Integer> pageMerchantIds) {
        try {

            //如果查到的Operator activeFlag 是 删除狀態7,拋 Exception
            if (LoginConstant.ACTIVE_FLAG_DELETE.getStatusCode() == operator.getActiveFlag()) {
                throw new AdminServiceBaseException(AdminErrorCode.OPERATOR_ACTIVE_FLAG_ERROR, "operator not normal status");
            }

             /* merchant vaild */
            //查詢要指派Operator過去的部門
            List<Merchant> merchantFoundList = merchantRepository.findAll(pageMerchantIds);
            int merchantListSize = merchantFoundList.size();
            int merchantIdSize = pageMerchantIds.size();

            //如果查詢不到部門,拋Exception
            if (merchantListSize != merchantIdSize) {
                throw new AdminServiceBaseException(AdminErrorCode.MERCHANT_NOT_EXIST_ERROR, "merchantId not exists");
            }

            /**
             *  A assign 品牌A給 B
             *  做法:
             *  1.查出A全部的品牌 看是否包含B的品牌
             *    1-1. 如果A全部包含B的品牌,邏輯不變
             *      A: 品牌A, 品牌B, 品牌C
             *      B: 品牌B, 品牌C
             *    1-2. 如果A不全部包含B的品牌,
             *      A: 品牌A, 品牌B
             *      B: 品牌B, 品牌C
             *       S1: 新增品牌A, 保留品牌B,C
             *       S2: 取消品牌B,
             *    1-3. 如果A完全不包含B的品牌 (不可能發生: 0%)
             *
             */

            NoneAdminInfo noneAdminInfo = this.checkAdmin(false);
            if(!noneAdminInfo.isAdmin()){
                //帳號的merchants
                List<Integer> accountMerchantList = merchantOperatorRepository.findMerchantIdListByOperatorId(noneAdminInfo.getOperatorId());
                //指定user的merchants
                List<Integer> userMerchantList = merchantOperatorRepository.findMerchantIdListByOperatorId(operator.getOperatorId());

                if(!accountMerchantList.containsAll(userMerchantList)){
                    //保留不包含的部分 + 頁面指定merchants的部分
                    userMerchantList.removeAll(accountMerchantList);
                    pageMerchantIds.addAll(userMerchantList);
                }
            }

            //上面所有檢核都通過, 先把原本的  operator-merchant 指派記錄刪除
            merchantOperatorRepository.deleteByOperatorId(operator.getOperatorId());
            List<MerchantOperator> merchantOperatorList = new ArrayList<>();
            //新增一個 operator-merchant 指派記錄
            for(int i=0 ; i < pageMerchantIds.size(); i++) {
                MerchantOperator merchantOperatorRelation = new MerchantOperator();
                merchantOperatorRelation.setMerchantId(pageMerchantIds.get(i));
                merchantOperatorRelation.setOperatorId(operator.getOperatorId());
                merchantOperatorList.add(merchantOperatorRelation);
            }
            merchantOperatorRepository.save(merchantOperatorList);
            operator.setUpdateOperator(RequestHelper.getCurrentUser().getUser().getOperatorName());
            operator.setUpdateTime(new Date());
            operatorRepository.saveAndFlush(operator);

        } catch (AdminServiceBaseException usbe) {
            throw usbe;
        } catch (Exception ex) {
            throw new AdminServiceBaseException(AdminErrorCode.UNKNOWN_ERROR, ex.getMessage(), ex);
        }

    }

    @Override
    public Operator copyOperator(Integer originOperatorId, String newOperatorName) {

        try {

            // 後台管理員帳號格式
            if (!OpUtils.verifyOperatorName(newOperatorName)) {
                throw new AdminServiceBaseException(AdminErrorCode.NAME_FORMAT_ERROR, "Operator name format is invalid");
            }

            //查詢 originOperatorId 的 record
            Operator operatorFoundById = operatorRepository.findOne(originOperatorId);

            //如果 originOperatorId 查不到 record, 拋Exception
            if (operatorFoundById == null) {
                throw new AdminServiceBaseException(AdminErrorCode.OPERATOR_NOT_EXIST_ERROR, "originOperatorId not exists");
            }

            //如果 originOperatorId 查到的 record, activeFlag 不是 Normal 狀態 拋Exception
            if (LoginConstant.ACTIVE_FLAG_LOGIN_SUCCESS.getStatusCode() != operatorFoundById.getActiveFlag()) {
                throw new AdminServiceBaseException(AdminErrorCode.OPERATOR_ACTIVE_FLAG_ERROR, "originOperatorId active flag abnormal");
            }

            //查詢 newOperatorName 的 record
            Operator operatorFoundByName = operatorRepository.findByOperatorName(newOperatorName);

            //如果查到 newOperatorName 的 record, 則拋 Exception
            if (operatorFoundByName != null) {
                throw new AdminServiceBaseException(AdminErrorCode.OPERATOR_EXIST_ERROR, "newOperatorName already exists");
            }

            //如果上面檢核都通過, 新增一個 Operator
            Operator newOperator = new Operator();

            newOperator.setOperatorName(newOperatorName);
            newOperator.setPassword(operatorFoundById.getPassword());
            newOperator.setActiveFlag(LoginConstant.ACTIVE_FLAG_LOGIN_SUCCESS.getStatusCode());
            newOperator.setNickname(newOperatorName);
            newOperator.setIsNeedReset(1);
            newOperator.setCreateTime(new Date());
            newOperator.setCreateOperator(RequestHelper.getCurrentUser().getUser().getOperatorName());
            newOperator.setBaseMerchantCode(operatorFoundById.getBaseMerchantCode());

            OperatorProfile profile = new OperatorProfile();
            profile.setPageSize(20);
            Operator operatorCreated = operatorRepository.saveAndFlush(newOperator);

            Integer operatorId = operatorCreated.getOperatorId();
            profile.setOperatorId(operatorId);
            operatorProfileRepository.saveAndFlush(profile);


            //查詢原本 originOperatorId 的品牌
            List<Integer> merchantsOfOriginOperatorId = merchantOperatorRepository.findMerchantIdListByOperatorId(originOperatorId);

            //複製原本 originOperatorId 的品牌
            List<MerchantOperator> copyMerchs = new ArrayList<>();
            for (Integer merchantId : merchantsOfOriginOperatorId) {
                MerchantOperator merchantOperatorRelation = new MerchantOperator();
                merchantOperatorRelation.setMerchantId(merchantId);
                merchantOperatorRelation.setOperatorId(operatorCreated.getOperatorId());
                copyMerchs.add(merchantOperatorRelation);
            }
            merchantOperatorRepository.save(copyMerchs);

            //查詢原本 originOperatorId 的角色
            List<RoleOperator> rolesOfOriginOperatorId = roleOperatorRepository.findByOperatorId(originOperatorId);

            //複製原本 originOperatorId 的角色
            List<RoleOperator> copyRoles = new ArrayList<>();
            for (RoleOperator roleOperator : rolesOfOriginOperatorId) {
                RoleOperator roleOperatorOfNewOperatorName = new RoleOperator();
                roleOperatorOfNewOperatorName.setRoleId(roleOperator.getRoleId());
                roleOperatorOfNewOperatorName.setOperatorId(operatorCreated.getOperatorId());
                copyRoles.add(roleOperatorOfNewOperatorName);
            }
            roleOperatorRepository.save(copyRoles);

            return operatorCreated;

        } catch (AdminServiceBaseException usbe) {
            throw usbe;
        } catch (Exception ex) {
            throw new AdminServiceBaseException(AdminErrorCode.UNKNOWN_ERROR, ex.getMessage(), ex);
        }

    }


    @Override
    public Merchant queryOperatorMerchant(Operator operator) {
        Merchant result = null;
        try {
            List<Integer> mercIds = merchantOperatorRepository.findMerchantIdListByOperatorId(operator.getOperatorId());

            if (!mercIds.isEmpty()) {
                result = merchantRepository.findOne(mercIds.get(0));
            }
        } catch (Exception e) {
            throw new AdminServiceBaseException(AdminErrorCode.UNKNOWN_ERROR, e.getMessage(), e);
        }
        return result;
    }

    /**
     *
     * @param flag set to true to use "System" merchant, should set to false most case.
     * @return
     * @throws AdminServiceBaseException
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @Override
    public List<Merchant> queryOperatorMerchants(boolean flag) {
        UserInfo<Operator> userInfo = RequestHelper.getCurrentUser();
        Operator user = userInfo.getUser();
        List<Merchant> merchants = this.getAllAdmMerchantList();

        //只取正常状态下的merchant
       // merchants = getNormalMerchant(merchants);

        List<Merchant> resultList = Lists.newLinkedList();
        if (this.checkAdmin(false).isAdmin()) {
            for(Merchant m : merchants) {
                if(checkMerchant(m, flag)) {
                	resultList.add(m);
                }
            }
        } else {
            List<Integer> merchantIdList = merchantOperatorRepository.findMerchantIdListByOperatorId(user.getOperatorId());
            for(Merchant m : merchants) {
                if(merchantIdList.contains(m.getMerchantId()) && checkMerchant(m, flag)) {
                	resultList.add(m);
                }
            }
        }

        return resultList;
    }

    private boolean checkMerchant(Merchant m, boolean flag) {
		return !"9".equals(m.getMerchantType()) && (flag || !m.getMerchantId().equals(1));
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
    @Override
    public void renewMerchantList() {
        admMerchantList =  this.getAll();
        Collections.sort(admMerchantList, MerchantComparator);
    }
    /**
     * get Merchant list for USS and put in a static list.
     * @return
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @Override
    public List<Merchant> getAllAdmMerchantList() {
        if(admMerchantList.isEmpty()) {
            admMerchantList =  this.getAll();
        }
        Collections.sort(admMerchantList, MerchantComparator);
        return admMerchantList;
    }

    /**
     * check if static merchant list is same as data in USS
     * @return
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @Override
    @MerchantCacheable(key="'checkAdmMerchantList'")
    public List<Merchant> checkAdmMerchantList() {
        List<Merchant> merList =  this.getAll();
        if(admMerchantList.size() != merList.size()) {
            admMerchantList.clear();
            admMerchantList.addAll(merList);
        }
        Collections.sort(admMerchantList, MerchantComparator);
        return admMerchantList;
    }

    @Override
    public List<Merchant> getAll() {
    	return merchantRepository.findAllmerchant();
    }

    @Override
    public NoneAdminInfo checkAdmin(boolean callByRole) {
        NoneAdminInfo noneAdminInfo = new NoneAdminInfo();
        UserInfo<Operator> userInfo = RequestHelper.getCurrentUser();
        Integer operatorId = userInfo.getUser().getOperatorId();
        // the roleIds below to Operator(id)
        List<Integer> roleIds = roleOperatorCustomRepository.findRoleIdListByOperatorId(operatorId);

        Boolean isAdmin = roleIds.contains(SUPER_MANAGER_ROLE_ID);

        if(!isAdmin && callByRole){
            /* categories:  SYSTEM,B2B,SUP,CS,FIN,ISD,LOTT,MGMT,OPS,PVP,RM,RNG,OTH,HR
               如果該Role有category的權限,可查詢或Assign該category的role給User
               Admin能查詢全部的Role以及Assign全部的Role給User */

            //Manager of the categories, the Roles have the roles of the categories
            List<Object[]> queryRoleIds = roleOperatorCustomRepository.findRoleIdListOfCategoryByOperatorId(operatorId);

            List<Integer> assignRoleIds = new ArrayList<>();
            for(Object obj: queryRoleIds){
                assignRoleIds.add(Integer.parseInt(ObjectUtils.toString(obj)));
            }

            if(CollectionUtils.isNotEmpty(assignRoleIds)){
                roleIds = assignRoleIds;
            }

            noneAdminInfo.setRoleIds(roleIds);
        }

        noneAdminInfo.setOperatorId(operatorId);
        noneAdminInfo.setAdmin(isAdmin);

        return noneAdminInfo;
    }

    @Override
    public Map<Integer, Merchant> queryOperatorMerchant(List<Integer> operatorIdList) {
        Map<Integer, Merchant> operatorDepartmentMap = new HashMap<>();

        try {
            List<MerchantOperator> merchantOpList = merchantOperatorRepository.findMerchantIdListByOperatorIdList(operatorIdList);

            for (MerchantOperator merchantOp : merchantOpList) {
                operatorDepartmentMap.put(merchantOp.getOperatorId(), merchantRepository.findOne(merchantOp.getMerchantId()));
            }

        } catch (Exception e) {
            throw new AdminServiceBaseException(AdminErrorCode.UNKNOWN_ERROR, e.getMessage(), e);
        }

        return operatorDepartmentMap;
    }


    @Override
    public List<Integer> querySubscriptionMerchant(Integer operatorId, boolean admin, Integer userId) {
        if(admin){
            return merchantRepository.querySubscriptionMerchantIsAdmin(userId);
        }
        return merchantRepository.querySubscriptionMerchantIsNotAdmin(userId, operatorId);
    }

    @Override
    public Merchant getMerchant(Integer merchantId) {
        return merchantRepository.findByMerchantId(merchantId);
    }

    @Override
    public void deleteMerchant(Integer merchantId) {
        try {

            //依照 merchantId 查詢
            Merchant merchantFound = merchantRepository.findByMerchantId(merchantId);

            //如果沒有該 merchantId 的 record, 則拋Exception
            if (merchantFound == null) {
                throw new AdminServiceBaseException(AdminErrorCode.MERCHANT_NOT_EXIST_ERROR, "merchantId not exists");
            }

            //依 merchantId 刪除角色時，該角色必須要沒有 operator 關聯,否則拋Exception
            List<MerchantOperator> operators = merchantOperatorRepository.findByMerchantId(merchantId);
            if (!operators.isEmpty()) {
                throw new AdminServiceBaseException(AdminErrorCode.MERCHANT_MUST_BE_UNCORELATED_WHEN_DELETING_ERROR, "merchantId still has operators");
            }

            merchantFound.setStatus(DepartmentConstant.ACTIVE_FLAG_DELETED.getActiveFlagType());

            merchantRepository.saveAndFlush(merchantFound);

        } catch (AdminServiceBaseException usbe) {
            throw usbe;
        } catch (Exception ex) {
            throw new AdminServiceBaseException(AdminErrorCode.UNKNOWN_ERROR, ex.getMessage(), ex);
        }
    }

	@Override
	public Merchant findMerchantByMerchantCode(String merchantCode) {
		return merchantRepository.findByMerchantCode(merchantCode);
	}


    // *TCGAdmin: 1.creater operator(admin) 3.subscribe to "system" roles 3.subscribe merchant to operator 4.close task
	@Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void approveMerchant(OperatorCreateTO operatorCreateTO, Integer taskId,List<Integer> operatorIdList,List<Integer> roleIdList, String operatorName) {
             Integer gwMerchantId = Integer.parseInt(operatorCreateTO.getMerchantId());

        try {

            //1.create merchant (merchantId: GW merchantId)
            int merchantNum = merchantRepository.countByMerchantCode(operatorCreateTO.getMerchantCode());
            if(merchantNum == 0){
                merchantRepository.saveAndFlush(transMerchantVO(operatorCreateTO));
                roleMenuPermissionService.createAllCategoryRelationFromMerchant(operatorCreateTO.getMerchantCode());
            }else{
                Merchant merchant = merchantRepository.findByMerchantCode(operatorCreateTO.getMerchantCode());
                if(StringUtils.isNotEmpty(operatorCreateTO.getUsMerchantId())) { //USS Crash
                    merchant.setUsMerchantId(Integer.parseInt(operatorCreateTO.getUsMerchantId()));
                }
                merchantRepository.saveAndFlush(merchant);
            }

            //2.(create operator-adminUser): and assign merchant to operator(admin)
            //[rule now] merchantType : company =>user did not suscribe merchant
            //in the future might need to suscribe
            if(StringTools.isNotBlank(operatorName)) {
                Operator adminUser = operatorService.updateOperatorOfMerchant(transOperatorVO(gwMerchantId, operatorName, ACTIVE));

                //3.(add):assign roles to adminUser
                if(!roleIdList.isEmpty()){
                    roleService.assignRoles(adminUser.getOperatorId(), roleIdList);
                }

            }

            //4.subscribe merchant to operator(user) & Auto subscribe to "System" role category
            //Auto subscribe to "System" roles =>subscribe merchant to users below "system" category (ex:administrator...)
            List<Integer> adminUsers = operatorRepository.queryUserFromCategory(CATEGORY_SYSTEM, ACTIVE);
            operatorIdList.addAll(adminUsers);

            this.subscribeUser(gwMerchantId, operatorIdList);

            //6. close task
            UserInfo<Operator> userInfo = RequestHelper.getCurrentUser();
            TaskTO taskTO = new TaskTO();
            taskTO.setTaskId(taskId);
            taskTO.setStateId(201);//
            workFlowManager.closeTaskWithOutCheck(taskTO, userInfo);

        } catch (AdminServiceBaseException usbe) {
            throw usbe;
        } catch (Exception ex) {
            throw new AdminServiceBaseException(AdminErrorCode.UNKNOWN_ERROR, ex.getMessage(), ex);
        }
    }

    private Merchant transMerchantVO(OperatorCreateTO operatorCreateTO){
        UserInfo<Operator> userInfo = RequestHelper.getCurrentUser();
        Integer opId = userInfo.getUser().getOperatorId();

        Merchant merchant = new Merchant();
        merchant.setMerchantId(Integer.parseInt(operatorCreateTO.getMerchantId()));
        if(StringUtils.isNotEmpty(operatorCreateTO.getUsMerchantId())){ //USS Crash
          merchant.setUsMerchantId(Integer.parseInt(operatorCreateTO.getUsMerchantId()));
          merchant.setCustomerId(operatorCreateTO.getCustomerId());
        }
        merchant.setMerchantCode(operatorCreateTO.getMerchantCode());
        merchant.setMerchantName(operatorCreateTO.getMerchantName());
        merchant.setMerchantType(operatorCreateTO.getMerchantType());
        merchant.setStatus(Integer.parseInt(operatorCreateTO.getStatus()));
        merchant.setParentId(Integer.parseInt(operatorCreateTO.getParentId()));
        merchant.setUpline(operatorCreateTO.getUpline());
        merchant.setCreateOperator(opId);
        merchant.setCurrency(operatorCreateTO.getMerchantCurrency());
        return merchant;
    }

    // *TCGAdmin: 1.creater operator(admin) , subscribe merchant to operator 2.create task 3.call IM
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public Task createMerchant(String gwMerchantId, String operatorName, String merchantName, String baseMerchantCode) {

        try {
            //1.(create operator-adminUser): and assign merchant to operator(admin)
            if(StringUtils.isNotEmpty(operatorName)){
                OperatorCreateTO operator = transOperatorVO(null,operatorName,7);
                operator.setBaseMerchantCode(baseMerchantCode);
            	operatorService.addOperatorOfMerchant(operator);//default delete
            }
            return this.createTask(gwMerchantId, merchantName);
        } catch (AdminServiceBaseException usbe) {
            throw usbe;
        } catch (Exception ex) {
            throw new AdminServiceBaseException(AdminErrorCode.UNKNOWN_ERROR, ex.getMessage(), ex);
        }
    }

    @Override
    public Task reApprove(OperatorCreateTO operatorCreateTO) {
    	
    	if(operatorCreateTO.getActionType() != null && "MODIFY".equalsIgnoreCase(operatorCreateTO.getActionType())){
    		Operator op = operatorRepository.findByOperatorName(operatorCreateTO.getOperatorName());
    		op.setOperatorName(operatorCreateTO.getNewOperatorName());
    		operatorRepository.saveAndFlush(op);
    	}else if(operatorCreateTO.getActionType() != null && "INSERT".equalsIgnoreCase(operatorCreateTO.getActionType())){
    		int count = operatorRepository.isOperatorExist(operatorCreateTO.getOperatorName());
    		if(count == 0){
    			Operator saveModel = new Operator();
    	        // 密碼加密 MD5
    	        saveModel.setPassword(MD5Utils.encrypt("123456")); //default
    	        saveModel.setOperatorName(operatorCreateTO.getNewOperatorName());
    	        saveModel.setNickname(operatorCreateTO.getNewOperatorName());
    	        saveModel.setActiveFlag(LoginConstant.ACTIVE_FLAG_DELETE.getStatusCode());
    	        saveModel = operatorRepository.saveAndFlush(saveModel);
    	        
    	        OperatorProfile profile = new OperatorProfile();
    	        profile.setOperatorId(saveModel.getOperatorId());
    	        operatorProfileRepository.saveAndFlush(profile);

    		}
    	}else if(operatorCreateTO.getActionType() != null && "DELETE".equalsIgnoreCase(operatorCreateTO.getActionType())){
    		Operator op = operatorRepository.findByOperatorName(operatorCreateTO.getOperatorName());
    		if(op != null){
    			operatorRepository.delete(op);
    		}
    	}
    	return this.updateTaskOfApprove(operatorCreateTO.getMerchantId(), operatorCreateTO.getMerchantName());
    }

    private Task createTask(String gwMerchantId, String merchantName){
        UserInfo<Operator> userInfo;
        TaskTO taskTO = new TaskTO();
        taskTO.setSubsysTaskId(gwMerchantId);
        taskTO.setStateId(TaskStateConstant.CREATE_MERCHANT_STATE_ID);
        taskTO.setTaskDescription(merchantName);
        userInfo = RequestHelper.getCurrentUser();
        taskTO.setOperatorName(userInfo.getUser().getOperatorName());
        //2.create task

        return workFlowManager.createTask(taskTO);
    }

    private Task updateTaskOfApprove(String gwMerchantId, String merchantName){
        UserInfo<Operator> userInfo;
        TaskTO taskTO = new TaskTO();
        taskTO.setSubsysTaskId(gwMerchantId);
        taskTO.setStateId(TaskStateConstant.CREATE_MERCHANT_STATE_ID);
        taskTO.setTaskDescription(merchantName);
        userInfo = RequestHelper.getCurrentUser();
        taskTO.setOperatorName(userInfo.getUser().getOperatorName());
        //2.create task
        return workFlowManager.updateTaskByMerchantId(taskTO,userInfo);
    }

    /**
     * transOperatorVO
     * @param merchantId
     * @param operatorName
     * @return
     */
    private OperatorCreateTO transOperatorVO(Integer merchantId, String operatorName, Integer activeFlag){
        OperatorCreateTO operator = new OperatorCreateTO();
        operator.setOperatorName(operatorName);
        operator.setNickname(operatorName);
        List<Integer> merchantList = new ArrayList<>();
        merchantList.add(merchantId);
        operator.setMerchantIdList(merchantList);
        operator.setActiveFlag(activeFlag);
        return operator;
    }

    /**
     * subscribe Merchant to Operators
     * @param merchantId
     * @param operatorIdList
     */
    private void subscribeUser(Integer merchantId, List<Integer> operatorIdList) {
        List<MerchantOperator> merchantOperatorList = new ArrayList<>();
        List<Operator> operatorFoundList = new ArrayList<>();

        //查詢已經訂閱的operatorIds 並移除
        List<Integer> operatorIds = merchantOperatorRepository.findMerchantOperatorIdByOperatorIds(merchantId,operatorIdList);
        operatorIdList.removeAll(operatorIds);

        if(!operatorIdList.isEmpty()){
           operatorFoundList = operatorRepository.findOperatorIdListByOperatorIdList(operatorIdList);
        }
        for(Operator ope:operatorFoundList){
            if (ope == null) {
                throw new AdminServiceBaseException(AdminErrorCode.OPERATOR_NOT_EXIST_ERROR, "operatorName not exists");
            }
            MerchantOperator merchantOperatorRelation = new MerchantOperator();
            merchantOperatorRelation.setMerchantId(merchantId);
            merchantOperatorRelation.setOperatorId(ope.getOperatorId());
            merchantOperatorList.add(merchantOperatorRelation);
        }
        merchantOperatorRepository.save(merchantOperatorList);
    }
    
    @Override
    @MerchantCacheEvict(allEntries = true)
    public void subscribeUser(String merchantCode, List<String> operatorNames, List<Integer> roleIds) {
        List<Operator> operators = operatorRepository.findByOperatorNameIn(operatorNames);
        Merchant merchant = merchantRepository.findByMerchantCode(merchantCode);
        for(Operator op : operators) {
        	MerchantOperator merchantOperator = merchantOperatorRepository.findByMerchantIdAndOperatorId(merchant.getMerchantId(), op.getOperatorId());
        	if(merchantOperator == null) {
        		merchantOperator = new MerchantOperator();
        		merchantOperator.setMerchantId(merchant.getMerchantId());
        		merchantOperator.setOperatorId(op.getOperatorId());
        		merchantOperatorRepository.save(merchantOperator);
        	}
        }
        
        if(roleIds != null) {
        	List<RoleOperator> ros = roleOperatorRepository.findAll(roleIds);
            
            for(RoleOperator ro : ros) {
            	MerchantOperator merchantOperator = merchantOperatorRepository.findByMerchantIdAndOperatorId(merchant.getMerchantId(), ro.getOperatorId());
            	if(merchantOperator == null) {
            		merchantOperator = new MerchantOperator();
            		merchantOperator.setMerchantId(merchant.getMerchantId());
            		merchantOperator.setOperatorId(ro.getOperatorId());
            		merchantOperatorRepository.save(merchantOperator);
            	}
            }
        }
        
    }

    @Override
    public void updateMerchant(OperatorCreateTO operatorCreateTO){

        Merchant merchant = merchantRepository.findMerchantByCode(operatorCreateTO.getMerchantCode());

        if (merchant == null) {
            throw new AdminServiceBaseException(AdminErrorCode.MERCHANT_NOT_EXIST_ERROR, "Merchantcode not exists");
        }

        String merchantName = operatorCreateTO.getMerchantName();

        //modify status
        if(StringUtils.isEmpty(merchantName)){
            merchant.setStatus(Integer.parseInt(operatorCreateTO.getStatus()));
        }else{
            //modify info
            merchant.setStatus(Integer.parseInt(operatorCreateTO.getStatus()));
            merchant.setMerchantName(merchantName);
            merchant.setParentId(Integer.parseInt(operatorCreateTO.getParentId()));
            merchant.setUpline(operatorCreateTO.getUpline());
        }
        merchantRepository.save(merchant);
        //refresh merchantList
        this.renewMerchantList();
    }

    public List<Merchant> getMerchantByType(String merchantTypeId) {
        if(merchantTypeId.equals(MERCHANT_TYPE_ALL))
            return merchantRepository.findAllmerchantExceptCompany();
        else
            return merchantRepository.findByMerchantType(merchantTypeId);
    }


    public Map<String, Object> getProductInfo(String baseMerchantCode) {
        List<String> merchantCategory =  merchantMenuCategoryRepository.findMerchantCategoryWithOutSystem(baseMerchantCode);
        List<String> merchantCategoryList =  menuCategoryRepository.findAllCategoryName();
        List<MerchantChargeTemplateTypeTO> merchantTemplateList = misClientService.getMerchantTemplateList(baseMerchantCode);

        Map<String, Object> result = new HashMap<>();
        result.put("merchantCategory", merchantCategory);
        result.put("merchantCategoryList", merchantCategoryList);
        result.put("merchantTemplateList", merchantTemplateList);
        return result;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void updateProductInfo(OperatorCreateTO operatorCreateTO) {
        String baseMerchantCode = operatorCreateTO.getBaseMerchantCode();
        List<MerchantMenuCategory> merchatCategory =  merchantMenuCategoryRepository.findMerchantMenuCategoryByMerchantCode(baseMerchantCode);
        merchantMenuCategoryRepository.delete(merchatCategory);

        if(StringUtils.isBlank(operatorCreateTO.getMenuCategoryNames())) {
        	return;
        }
        
        List<String> menuCategoryNameList = Arrays.asList(operatorCreateTO.getMenuCategoryNames().trim().split(","));

        List<MenuCategory> list = menuCategoryRepository.findAll(menuCategoryNameList);
        if(menuCategoryNameList.size()!= list.size()){
            throw new AdminServiceBaseException(AdminErrorCode.MENU_CATEGORY_SIZE_NOT_CORRECT_ERROR, "menu category size not correct");
        }

        List<MerchantMenuCategory> saveList = new ArrayList<>();
        for(String str: menuCategoryNameList){
            MerchantMenuCategory merchantMenuCategory = new MerchantMenuCategory();
            MerchantMenuCategorKey key = new MerchantMenuCategorKey();
            key.setMerchantCode(baseMerchantCode);
            key.setMenuCategoryName(str);
            merchantMenuCategory.setKey(key);
            saveList.add(merchantMenuCategory);
        }
        if(CollectionUtils.isNotEmpty(saveList)){
            merchantMenuCategoryRepository.save(saveList);
        }
    }

	@Override
	public void createMerchant(Merchant company, Merchant merchant, Boolean createCompany) {
		if(createCompany) {
			merchantRepository.save(company);
			roleMenuPermissionService.createAllCategoryRelationFromMerchant(company.getMerchantCode());
		}
		merchantRepository.save(merchant);
		roleMenuPermissionService.createAllCategoryRelationFromMerchant(merchant.getMerchantCode());
	}

	@Override
	public Operator createAdminOperator(String merchantCode, List<Integer> roles) {
		Operator operator = new Operator();
		String adminName = generateAdminName(merchantCode);
		Merchant merchant = merchantRepository.findByMerchantCode(merchantCode);
		
		operator.setOperatorName(adminName);
        operator.setNickname(adminName);
        operator.setActiveFlag(LoginConstant.ACTIVE_FLAG_LOGIN_SUCCESS.getStatusCode());
        operator.setBaseMerchantCode(merchantCode);
        operator.setPassword(MD5Utils.encrypt("123456"));
        operator.setIsNeedReset(1);

        UserInfo<Operator> userInfo = RequestHelper.getCurrentUser();
        operator.setCreateOperator(userInfo.getUser().getOperatorName());
        operatorRepository.saveAndFlush(operator);

        MerchantOperator merchantOperatorRelation = new MerchantOperator();
        merchantOperatorRelation.setMerchantId(merchant.getMerchantId());
        merchantOperatorRelation.setOperatorId(operator.getOperatorId());
        
        merchantOperatorRepository.saveAndFlush(merchantOperatorRelation);
        
        OperatorProfile profile = new OperatorProfile();
        profile.setOperatorId(operator.getOperatorId());
        operatorProfileRepository.saveAndFlush(profile);
        
        roleService.assignRoles(operator.getOperatorId(), roles);

        return operator;
	}

	private String generateAdminName(String merchantCode) {
		String firstAdminName = merchantCode + "_admin";
		
		if(firstAdminName.length() > 16) {
			firstAdminName = merchantCode + "_adm";
		}
		String adminName = firstAdminName;
		Integer suffix = 0;
		Integer last = 0;
		
		while(operatorRepository.findByOperatorName(adminName) != null) {
			suffix += 10;
			last = (int) (Math.random() * 10);
			adminName = firstAdminName + Integer.toHexString(suffix + last);
			
			if(suffix >= 15) {
				throw new AdminServiceBaseException(AdminErrorCode.OPERATOR_NAME_ERROR, "Can't find admin name for use");
			}
		}
		
		return adminName;
	}

	private List<Merchant>getNormalMerchant(List<Merchant> list) {
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }

        Iterator<Merchant> iterator = list.iterator();

        while (iterator.hasNext()) {
            //0,停用,1正常，2未审核
            if (!isEqualsStatus(iterator.next(), 1)) {
                iterator.remove();
            }
        }

        return list;
    }

    private boolean isEqualsStatus(Merchant merchant,int status) {

        if (null == merchant || null == merchant.getStatus()) {
            return false;
        }

        if (merchant.getStatus().intValue() == status) {
            return true;
        }
        return false;
    }

    @Override
    public List<Merchant> getOperatorMerchant(Operator operator) {

        List<Integer> merchantIdList = merchantOperatorRepository.findMerchantIdListByOperatorId(operator.getOperatorId());
        if (CollectionUtils.isEmpty(merchantIdList)) {
            return Collections.emptyList();
        }

        List<Merchant> merchants = merchantRepository.findAll();
        //merchants = getNormalMerchant(merchants);
        List<Merchant> resultList = Lists.newLinkedList();
        for(Merchant m : merchants) {
            if(merchantIdList.contains(m.getMerchantId()) && checkMerchant(m, false)) {
                resultList.add(m);
            }
        }

        return resultList;
    }

}