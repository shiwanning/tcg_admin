package com.tcg.admin.controller;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tcg.admin.model.MenuItem;
import com.tcg.admin.model.SystemHelper;
import com.tcg.admin.model.SystemHelperTemp;
import com.tcg.admin.service.RoleMenuPermissionService;
import com.tcg.admin.service.SystemHelperService;
import com.tcg.admin.service.impl.IMService;
import com.tcg.admin.to.SystemHelperTO;
import com.tcg.admin.to.TreeTo;
import com.tcg.admin.to.response.JsonResponseT;

@RestController
@RequestMapping(value = "/resources/systemHelper", produces = MediaType.APPLICATION_JSON_VALUE)
public class SystemHelperResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(SystemHelperResource.class);
    
    @Autowired
    private SystemHelperService systemHelperService;
    
    @Autowired
	IMService imService;
    
    @Autowired
    private RoleMenuPermissionService roleMenuPermissionService;

    @GetMapping
    public JsonResponseT<Object> querySystemHelper(@RequestParam(value = "menuId", required = false)Integer menuId,
            @RequestParam(value = "menuName", required = false)String menuName,
            @RequestParam(value = "status", required = false)Integer status,
            @RequestParam(value = "pageNo", required = false) int pageNo,
            @RequestParam(value = "pageSize", required = false) int pageSize) {
        JsonResponseT<Object> response = new JsonResponseT<>(true);
        Page<SystemHelper> queryResultresponse = systemHelperService.querySystemHelper(menuId, menuName, status, pageNo, pageSize);
        response.setValue(queryResultresponse);
        return response;
    }

    /*
     * API for creating helper
     * */
    @PostMapping("/create")
    public JsonResponseT<Object> createSystemHelper(@RequestBody SystemHelperTO to) {
        JsonResponseT<Object> response = new JsonResponseT<>(true);
        int taskId = systemHelperService.saveSystemHelper(to);
        uploadChanges(taskId);
        return response;
    }


    /*
     * API to update helper
     * */
    @PutMapping("/update")
    public JsonResponseT<Object> updateSystemHelper(@RequestBody SystemHelperTO to) {
        JsonResponseT<Object> response = new JsonResponseT<>(true);
        int taskId = systemHelperService.updateSystemHelper(to);
        uploadChanges(taskId);
        return response;
    }

    @PutMapping("/updateStatus")
    public JsonResponseT<SystemHelper> updateStatus(@RequestBody SystemHelperTO to) {
        JsonResponseT<SystemHelper> response = new JsonResponseT<>(true);
        systemHelperService.updateStatus(to);
        return response;
    }
    
    @GetMapping("/helperMenu")
    public List<TreeTo> getSystemHelperMenu() {
        return roleMenuPermissionService.getAllTreeTo();
    }
    
    private List<TreeTo> returnChildList(Map<Integer, List<MenuItem>> result, List<MenuItem> childList) throws InvocationTargetException, IllegalAccessException {
        List<TreeTo> menuItemLists = new ArrayList<>();
        for (int i = 0; i < childList.size(); i++) {
            MenuItem menuItem = childList.get(i);
            Integer menuId = menuItem.getMenuId();

            //代表有子節點
            List<MenuItem> subList = result.get(menuId);
            TreeTo cloneTree = new TreeTo();
            BeanUtils.copyProperties(cloneTree,menuItem);
            if (subList != null && !subList.isEmpty()) {
                cloneTree.setList(this.returnChildList(result, subList));
            }
            menuItemLists.add(cloneTree);
        }

        return menuItemLists;
    }
    
    /*
     * API to call in every click in menu
     * */
    @GetMapping("/findHelperByMenuId")
    public JsonResponseT<SystemHelper> findHelperByMenuId(@RequestParam(value = "menuId", required = false)Integer menuId) {
        JsonResponseT<SystemHelper> response = new JsonResponseT<>(true);
        response.setValue(systemHelperService.findHelperByMenuId(menuId));
        return response;
    }
    
    /*
     * API to call for getting the task
     * */
    @GetMapping("/findHelperTempByTaskId")
    public JsonResponseT<SystemHelperTemp> findHelperTempByTaskId(@RequestParam(value = "taskId", required = false)Integer taskId) {
        JsonResponseT<SystemHelperTemp> response = new JsonResponseT<>(true);
        response.setValue(systemHelperService.findHelperTempByTaskId(taskId));
        return response;
    }
    
    /*
     * API to call for approving the task 
     * */
    @PutMapping("/approve")
    public JsonResponseT<SystemHelperTemp> approveHelper(@RequestBody SystemHelperTO to) {
        JsonResponseT<SystemHelperTemp> response = new JsonResponseT<>(true);
        SystemHelperTemp temp = systemHelperService.approveHelperRequest(to);
        response.setValue(temp);
        
        uploadChanges(temp.getTaskId());
        return response;
    }
    
    /*
     * API to call rejecting task
     * */
    @PutMapping("/reject")
    public JsonResponseT<SystemHelperTemp> rejectHelper(@RequestBody SystemHelperTO to) {
        JsonResponseT<SystemHelperTemp> response = new JsonResponseT<>(true);
        SystemHelperTemp temp = systemHelperService.rejectHelperRequest(to);
        response.setValue(temp);
        uploadChanges(temp.getTaskId());
        return response;
    }
    
    /*
     * API to call in every click in menu
     * */
    @GetMapping("/search")
    public JsonResponseT<List<SystemHelper>> searchHelper(@RequestParam(value = "query", required = false) String query, 
            @RequestParam(value = "lang", required = false) String lang) {
        JsonResponseT<List<SystemHelper>> response = new JsonResponseT<>(true);
        response.setValue(systemHelperService.search(query, lang));
        return response;
    }

    @GetMapping("/searchByMenuId")
    public JsonResponseT<List<SystemHelper>> searchHelper(@RequestParam(value = "menuId", required = false) Integer menuId) {
        JsonResponseT<List<SystemHelper>> response = new JsonResponseT<>(true);
        response.setValue(systemHelperService.searchByMenuId(menuId));
        return response;
    }
    
    @GetMapping("/searchByRoleId")
    public JsonResponseT<List<SystemHelper>> searchHelperByRole(@RequestParam(value = "roleId", required = false) Integer roleId) {
        JsonResponseT<List<SystemHelper>> response = new JsonResponseT<>(true);
        response.setValue(systemHelperService.searchByRole(roleId));
        return response;
    }
    
    @PutMapping("/closeNotification")
    public JsonResponseT<Object> closeNotification(@RequestBody SystemHelperTO to) {
        JsonResponseT<Object> response = new JsonResponseT<>(true);
        systemHelperService.closeNotification(to);
        return response;
    }
    
    @GetMapping("/searchBySingleMenuId")
    public JsonResponseT<List<SystemHelper>> searchBySingleMenuId(@RequestParam(value = "menuId", required = false) Integer menuId) {
        JsonResponseT<List<SystemHelper>> response = new JsonResponseT<>(true);
        response.setValue(systemHelperService.searchBySingleMenuId(menuId));
        return response;
    }
    
    private void uploadChanges(Integer taskId) {
    	try {
			imService.uploadChanges(taskId, true);
		} catch (Exception e) {
		    LOGGER.error("ERROR IN IM SERVICE", e);
		}
    }
}