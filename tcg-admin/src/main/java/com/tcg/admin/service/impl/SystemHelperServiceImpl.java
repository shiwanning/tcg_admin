package com.tcg.admin.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tcg.admin.common.constants.SystemHelperConstant;
import com.tcg.admin.common.error.AdminErrorCode;
import com.tcg.admin.common.exception.AdminServiceBaseException;
import com.tcg.admin.common.helper.RequestHelper;
import com.tcg.admin.model.MenuItem;
import com.tcg.admin.model.Operator;
import com.tcg.admin.model.SystemHelper;
import com.tcg.admin.model.SystemHelperTemp;
import com.tcg.admin.model.Task;
import com.tcg.admin.persistence.RoleOperatorCustomRepository;
import com.tcg.admin.persistence.SystemHelperRepositoryCustom;
import com.tcg.admin.persistence.springdata.IMenuItemRepository;
import com.tcg.admin.persistence.springdata.IRoleMenuPermissionRepository;
import com.tcg.admin.persistence.springdata.ISystemHelperRepository;
import com.tcg.admin.persistence.springdata.ISystemHelperTempRepository;
import com.tcg.admin.service.CommonMenuService;
import com.tcg.admin.service.RoleMenuPermissionService;
import com.tcg.admin.service.SystemHelperService;
import com.tcg.admin.service.WorkFlowService;
import com.tcg.admin.to.SystemHelperTO;
import com.tcg.admin.to.TaskTO;
import com.tcg.admin.to.UserInfo;
import com.tcg.admin.utils.StringTools;

/**
 * Created by chris.h on 12/13/2017.
 */
     
@Service
@Transactional
public class SystemHelperServiceImpl implements SystemHelperService {

	private static final Integer SUPER_MANAGER_ROLE_ID = 1;
	
    @Autowired
    private ISystemHelperRepository systemHelperRepository;
    
    @Autowired
    private ISystemHelperTempRepository systemHelperTempRepository;

    @Autowired
    private SystemHelperRepositoryCustom systemHelperRepositoryCustom;
    
    @Autowired
	private WorkFlowService workflowManager;
    
    @Autowired
	private OperatorLoginService operatorLoginService;

    @Autowired
	private IMenuItemRepository menuItemRepository;
	
    @Autowired
    private IRoleMenuPermissionRepository roleMenuRepository;

    @Autowired
    private RoleOperatorCustomRepository roleOperatorCustomRepository;
    
    @Autowired
    private RoleMenuPermissionService roleMenuPermissionService;
    
    @Autowired
    private CommonMenuService menuProxyService;

    @Override
    public int saveSystemHelper(SystemHelperTO to) {
    	/*
         * Extract the operator name and operator ID
         * */
    	UserInfo<Operator> userInfo = RequestHelper.getCurrentUser();
    	String operatorName = userInfo.getUser().getOperatorName();
    	Integer operatorId = userInfo.getUser().getOperatorId();

    	/*
         * check if the menu ID has already the helper
         * */
    	SystemHelper helper = systemHelperRepository.findOne(to.getMenuId());
    	
    	if(helper !=null){
    		throw new AdminServiceBaseException(AdminErrorCode.HELPER_EXIST, "Helper is existing");
    	}
    	
    	/*
         * prepare the model to save
         * */
    		SystemHelper model = new SystemHelper();
    		model.setMenuId(to.getMenuId());
            model.setCreateOperator(operatorId);
            model.setCreateOperatorName(operatorName);
            model.setState(SystemHelperConstant.HELPER_PENDING);
            model.setStatus(SystemHelperConstant.ACTIVE);
            
            // save
            systemHelperRepository.saveAndFlush(model);
            
            return this.savePendingRequest(to, operatorName, operatorId);
    	
        
    }
    
    /*
     * Save the request in Pending status and will change later after approved or rejected
     * */
    private int savePendingRequest(SystemHelperTO to, String operatorName, Integer operatorId){
        SystemHelperTemp temp = new SystemHelperTemp();

        /*
         * Create the task and get the task ID as part of model
         * */
        Task task = workflowManager.createMultipleTask(prepareTask(to.getMenuId(), operatorName));
        temp.setTaskId(task.getTaskId());
        temp.setMenuId(to.getMenuId());
        temp.setRequester(operatorName);
        temp.setRequesterId(operatorId);
        temp.setEnContentTemp(to.getEnContent());
        temp.setCnContentTemp(to.getCnContent());
        temp.setStatus(SystemHelperConstant.TEMP_PENDING);
        
        // save
        return systemHelperTempRepository.saveAndFlush(temp).getTaskId();
    }
    
    /*
     * Preparing the TASK details
     * */
    private TaskTO prepareTask(Integer menuId, String operatorName){
    	TaskTO taskTO = new TaskTO();
    	taskTO.setStateId(SystemHelperConstant.PENDING_STATE);
    	taskTO.setSubsysTaskId(this.generateSubSysId(menuId));
    	MenuItem menuItem = menuProxyService.getMenuMap().get(menuId);
    	String description = menuItem == null ? menuId + " | " + "Helper" : menuItem.getMenuName();
    	
    	taskTO.setTaskDescription(description);
    	taskTO.setOperatorName(operatorName);
    	
    	return taskTO;
    }
    
    private String generateSubSysId(Integer menuId){
    	SimpleDateFormat sdf = new SimpleDateFormat("HHmmss");
    	StringBuilder sb = new StringBuilder();
    	Date today = new Date();
    	Calendar cal = Calendar.getInstance();
    	cal.setTime(today);
    	
    	sb.append(cal.get(Calendar.MONTH));
    	sb.append(cal.get(Calendar.DATE));
    	sb.append(sdf.format(cal.getTime()));
    	sb.append("_");
    	sb.append(menuId.toString());
    	
    	return sb.toString();
    }

    /*
     * For updating helper just add another request and create new task for it
     * */
    @Override
    public int updateSystemHelper(SystemHelperTO to) {
    	UserInfo<Operator> userInfo = RequestHelper.getCurrentUser();
    	String operatorName = userInfo.getUser().getOperatorName();
    	Integer operatorId = userInfo.getUser().getOperatorId();
        SystemHelper model = systemHelperRepository.findOne(to.getMenuId());
        
        if(model == null){
    		throw new AdminServiceBaseException(AdminErrorCode.HELPER_NOT_FOUND, "Helper not found");
    	}
        
        // save and create new task
        return this.savePendingRequest(to, operatorName, operatorId);
    }

	@Override
	public Page<SystemHelper> querySystemHelper(Integer menuId,String menuName, Integer status, int pageNo, int pageSize) {
		List<Integer> menuIdList = new ArrayList<>();
		if(StringUtils.isNotBlank(menuName)){
			menuIdList = menuItemRepository.querByMenuName("%"+menuName+"%", RequestHelper.getLanguage());
			if(CollectionUtils.isEmpty(menuIdList)){
				menuIdList.add(-1); //to make it find nothing, return empty Page<SystemHelper>
			}
		}
		return systemHelperRepositoryCustom.find(menuId, menuIdList, status, new PageRequest(pageNo, pageSize));
	}

    @Override
    public void updateStatus(SystemHelperTO to) {
        SystemHelper model = systemHelperRepository.findOne(to.getMenuId());
        model.setStatus(to.getStatus());
        systemHelperRepository.saveAndFlush(model);
    }

	@Override
	public SystemHelper findHelperByMenuId(Integer menuId) {
		return systemHelperRepository.findOne(menuId);
	}

	@Override
	public SystemHelperTemp findHelperTempByTaskId(Integer taskId) {
		return systemHelperTempRepository.findHelperTempByTaskId(taskId);
	}

	/*
     * Approve the helper, transfer the temp reqest in SystemHelper and mark the temp as APPROVED
     * */
	@Override
	public SystemHelperTemp approveHelperRequest(SystemHelperTO to) {
		UserInfo<Operator> userInfo = RequestHelper.getCurrentUser();
    	String operatorName = userInfo.getUser().getOperatorName();
    	Integer operatorId = userInfo.getUser().getOperatorId();
    	
		SystemHelperTemp temp = systemHelperTempRepository.findHelperTempByTaskId(to.getTaskId());
		SystemHelper helper = this.findHelperByMenuId(to.getMenuId());
		
		if(helper == null){
    		throw new AdminServiceBaseException(AdminErrorCode.HELPER_NOT_FOUND, "Helper not found");
    	}
		
		helper.setState(SystemHelperConstant.HELPER_APPROVED);

		if(StringUtils.isNotBlank(temp.getEnContentTemp())) {
			helper.setEnContent(temp.getEnContentTemp());
		}
		if(StringUtils.isNotBlank(temp.getCnContentTemp())) {
			helper.setCnContent(temp.getCnContentTemp());
		}

		helper.setUpdateOperator(operatorId);
		helper.setUpdateOperatorName(operatorName);
		helper.setUpdateTime(new Date());
		
		systemHelperRepository.saveAndFlush(helper);
		
		/*
	     * Close the task after approved
	     * */
		this.closeTask(to.getTaskId(), SystemHelperConstant.APPROVED_STATE, userInfo);

		return this.proccessTask(temp, SystemHelperConstant.TEMP_APPROVED, operatorName, to);
	}

	/*
     * Just reject, Changed the status to rejected and add some comments
     * */
	@Override
	public SystemHelperTemp rejectHelperRequest(SystemHelperTO to) {
		UserInfo<Operator> userInfo = RequestHelper.getCurrentUser();
    	String operatorName = userInfo.getUser().getOperatorName();
    	
		SystemHelperTemp temp = systemHelperTempRepository.findHelperTempByTaskId(to.getTaskId());
		
		this.closeTask(to.getTaskId(), SystemHelperConstant.REJECT_STATE, userInfo);
		
		return this.proccessTask(temp, SystemHelperConstant.TEMP_REJECTED, operatorName, to);
	}
	
	/*
     * Changing the status add if has any remarks added by user
     * */
	private SystemHelperTemp proccessTask(SystemHelperTemp temp, Integer status, String operatorName, SystemHelperTO to){
		
		if(temp == null){
    		throw new AdminServiceBaseException(AdminErrorCode.HELPER_TEMP_NOT_FOUND, "Helper Temp not found");
    	}
		
		temp.setStatus(status);
		temp.setProcessedBy(operatorName);
		temp.setRemarks(to.getRemarks());
		
		return systemHelperTempRepository.saveAndFlush(temp); 
	}
	
	/*
     * Prepare Transfer Object for closing the task
     * */
	private void closeTask(Integer taskId, Integer stateId, UserInfo<Operator> userInfo){
		TaskTO taskTO = new TaskTO();
		taskTO.setTaskId(taskId);
		taskTO.setStateId(stateId);
		
		workflowManager.closeTask(taskTO, userInfo);
		
	}

	@Override
	public List<SystemHelper> search(String query, String lang) {
		List<SystemHelper> result = systemHelperRepositoryCustom.search(query, lang);
		
		return this.filterByPermissionAndContent(result, query, lang);
	}
	
	private List<SystemHelper> filterByPermissionAndContent(List<SystemHelper> result, String query, String lang){
		UserInfo<Operator> userInfo = RequestHelper.getCurrentUser();
		List<Integer> roleIdList = roleOperatorCustomRepository.findRoleIdListByOperatorId(userInfo.getUser().getOperatorId());
		Set<Integer> menuIdList = roleMenuRepository.findMenuIdListByRoleIdList(roleIdList);
		boolean isAdmin =  roleIdList.contains(SUPER_MANAGER_ROLE_ID);
		List<Integer> menuIdListofBase = roleMenuPermissionService.menuIdsByBaseMerchant(userInfo.getUser());
		
		List<SystemHelper> returnVal = new ArrayList<>();
		
	    for(SystemHelper helper : result){
	    	if(!((menuIdList.contains(helper.getMenuId()) && (menuIdListofBase.contains(helper.getMenuId()) || CollectionUtils.isEmpty(menuIdListofBase))) || isAdmin)) {
	    		continue;
	    	}
    		if (enUsMapping(lang, query, helper) || zhCnMapping(lang, query, helper)){
    			returnVal.add(helper);
    		}
	    	
	    }

	    return returnVal;
	}
	
	private boolean zhCnMapping(String lang, String query, SystemHelper helper) {
		return  "zh_CN".equalsIgnoreCase(lang)
           && (
           StringTools.sliceHtml(helper.getCnContent().toLowerCase()).contains(query.toLowerCase())
		   || helper.getMenuItem().getLabels().get("zh_CN").getLabel().toLowerCase().contains(query.toLowerCase())
		);
	}

	private boolean enUsMapping(String lang, String query, SystemHelper helper) {
		return "en_US".equalsIgnoreCase(lang)
    		    && (
    	    		      StringTools.sliceHtml(helper.getEnContent().toLowerCase()).contains(query.toLowerCase())
    	    			  || helper.getMenuItem().getLabels().get("en_US").getLabel().toLowerCase().contains(query.toLowerCase())
    	    			  );
	}

	private List<SystemHelper> filterByPermission(List<SystemHelper> result){
		UserInfo<Operator> userInfo = RequestHelper.getCurrentUser();
		List<Integer> roleIdList = roleOperatorCustomRepository.findRoleIdListByOperatorId(userInfo.getUser().getOperatorId());
		Set<Integer> menuIdList = roleMenuRepository.findMenuIdListByRoleIdList(roleIdList);
		
    	boolean isAdmin =  roleIdList.contains(SUPER_MANAGER_ROLE_ID);

		List<Integer> menuIdListofBase = roleMenuPermissionService.menuIdsByBaseMerchant(userInfo.getUser());
		List<SystemHelper> returnVal = new ArrayList<>();
		
	    for(SystemHelper helper : result){
	    	if((menuIdList.contains(helper.getMenuId()) && (menuIdListofBase.contains(helper.getMenuId()) || CollectionUtils.isEmpty(menuIdListofBase))) || isAdmin){
	    		returnVal.add(helper);
	    	}
	    }

	    return returnVal;
	}
	
	

	@Override
	public List<SystemHelper> searchByMenuId(Integer menuId) {
		List<Integer> menuIdList =  menuItemRepository.queryMenuIdsByParentId(menuId);
		List<SystemHelper> result = systemHelperRepositoryCustom.searchByMenuIds(menuIdList);
		return this.filterByPermission(result);
	}
	
	@Override
	public List<SystemHelper> searchByRole(Integer roleId){
		Set<Integer> menuIdList = roleMenuRepository.findMenuIdListByRoleIdList(Arrays.asList(roleId));
		
		List<SystemHelper> result = systemHelperRepositoryCustom.searchByMenuIds(menuIdList);
		return this.filterByPermission(result);
	}

	@Override
	public void closeNotification(SystemHelperTO to) {
		SystemHelperTemp temp = systemHelperTempRepository.findHelperTempByTaskId(to.getTaskId());
		if(temp !=null){
			temp.setStatus(SystemHelperConstant.TEMP_CLOSED);
			systemHelperTempRepository.saveAndFlush(temp); 
		}
	}
	
	@Override
	public List<SystemHelper> searchBySingleMenuId(Integer menuId) {
		Integer menuIdResult =  menuItemRepository.querySingleMenuId(menuId);
		List<SystemHelper> result = systemHelperRepositoryCustom.searchByMenuIds(Arrays.asList(menuIdResult));
		return this.filterByPermission(result);
	}
}
