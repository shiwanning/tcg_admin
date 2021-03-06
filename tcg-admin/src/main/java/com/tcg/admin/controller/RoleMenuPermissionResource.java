package com.tcg.admin.controller;

import com.tcg.admin.common.annotation.OperationLog;
import com.tcg.admin.common.constants.OperationFunctionConstant;
import com.tcg.admin.common.error.AdminErrorCode;
import com.tcg.admin.common.exception.AdminServiceBaseException;
import com.tcg.admin.common.helper.RequestHelper;
import com.tcg.admin.controller.json.JsTreeMenu;
import com.tcg.admin.model.ApiLabel;
import com.tcg.admin.model.MenuItem;
import com.tcg.admin.model.Operator;
import com.tcg.admin.model.Role;
import com.tcg.admin.service.BpmPermissionService;
import com.tcg.admin.service.CommonMenuService;
import com.tcg.admin.service.MerchantService;
import com.tcg.admin.service.RoleMenuPermissionService;
import com.tcg.admin.to.TreeTo;
import com.tcg.admin.to.UserInfo;
import com.tcg.admin.to.response.JsonResponse;
import com.tcg.admin.to.response.JsonResponseT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping(value = "/resources/permissions", produces = MediaType.APPLICATION_JSON_VALUE)
public class RoleMenuPermissionResource {

	private static final Logger LOGGER = LoggerFactory.getLogger(RoleMenuPermissionResource.class);
	
    @Autowired
    private RoleMenuPermissionService roleMenuPermissionService;

    @Autowired
    private CommonMenuService commonService;

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private BpmPermissionService bpmPermissionService;

    /**
     * 查詢完整的目錄選單結構
     * query full menu tree structure
     * 
     * example: menuId 0 mapped to highest menuItem 1
     * menuId 1 mapped to list of MenuItem 2,3,4
     * menuId 2 map to list of MenuItem 5
     * menuId 3 map to list MenuItem 6
     */
    @GetMapping("/all")
    public Map<Integer, List<MenuItem>> queryAllMenuTree() {
        return roleMenuPermissionService.queryAllMenuTree();
    }

    @GetMapping("/alljson")
    public List<TreeTo> queryAllMenuTreeJson() {
    	return roleMenuPermissionService.getAllTreeTo();
    }

    /**
     * 查詢管理員所具有的目錄權限結構
     * query menu tree structure of a specific operator
     * 
     * example: menuId 0 mapped to highest menuItem 1
     * menuId 1 mapped to list of MenuItem 2,3,4
     * menuId 2 map to list of MenuItem 5
     * menuId 3 map to list MenuItem 6
     */
    @GetMapping("/operator/tree/{operatorId}")
    public Map<String, List<MenuItem>> queryMenuTreeByOperator(@PathVariable("operatorId") Integer operatorId) {
        return roleMenuPermissionService.queryAllMenuTreeByOperator(operatorId);
    }

    @GetMapping("/tree")
    public Map<String, List<MenuItem>> queryMenuTreeByToken() {
        UserInfo<Operator> userInfo = RequestHelper.getCurrentUser();
        return roleMenuPermissionService.queryAllMenuTreeByOperator(userInfo.getUser().getOperatorId());
    }
    /**
     * 查詢管理員所具有的目錄權限, 並組成目錄樹
     */
    @GetMapping("/menu")
    public JsonResponseT<JsTreeMenu> getOperatorJsMenuTree() {
        UserInfo<Operator> userInfo = RequestHelper.getCurrentUser();

        if(userInfo == null) {
        	throw new AdminServiceBaseException(AdminErrorCode.OPERATOR_NOT_EXIST_ERROR, "not have current user.");
        }
        
        JsonResponseT<JsTreeMenu> result = new JsonResponseT<>();
        result.setValue(roleMenuPermissionService.getOperatorJsMenuTree(userInfo.getUser()));
        result.setSuccess(Boolean.TRUE);

        return result;
    }

    /**
     * 查詢管理員所具有的按鈕權限 
     */
    @GetMapping("/buttons/permissionGroup")
    public JsonResponseT<List<Integer>> getOperatorButtonPermissions() {

        UserInfo<Operator> userInfo = RequestHelper.getCurrentUser();

        JsonResponseT<List<Integer>> result = new JsonResponseT<>();
        result.setValue(roleMenuPermissionService.getAllButtonList(userInfo.getUserName()));
        result.setSuccess(Boolean.TRUE);
        return result;
    }



    /**
     * 查詢管理員所具有的按鈕權限
     */
    @GetMapping("/buttons")
    public JsonResponseT<List<MenuItem>> getOperatorButtonPermissionsForService() {

        UserInfo<Operator> userInfo = RequestHelper.getCurrentUser();

        JsonResponseT<List<MenuItem>> result = new JsonResponseT<>();
        result.setValue(roleMenuPermissionService.getAllButtonListForService(userInfo.getUserName()));
        result.setSuccess(Boolean.TRUE);

        return result;
    }

    @GetMapping("/{operatorId}/allMenu")
    @OperationLog(type = OperationFunctionConstant.WATCH_PERMISSIONS)
    public List<TreeTo> queryAllMenuTreeByOperator(@PathVariable("operatorId") Integer operatorId) {
        roleMenuPermissionService.getAllTreeTo(operatorId);
        return roleMenuPermissionService.getAllTreeTo(operatorId);
    }
    
    @GetMapping("/menuByOperatorId")
    public List<TreeTo> queryAllMenuTreeByOperatorId() {
        UserInfo<Operator> userInfo = RequestHelper.getCurrentUser();
        return roleMenuPermissionService.findPermissionOfOperator(userInfo.getUser().getOperatorId(), userInfo);
    }

    @GetMapping("/menuByMenuCategoryName")
    public List<TreeTo> queryAllMenuTreeByMenuCategoryName(@RequestParam(value = "menuCategoryNames", required = false) List<String> menuCategoryNames) {
        return roleMenuPermissionService.queryAllMenuTreeByMenuCategoryName(menuCategoryNames);
    }

    /**
     * Update a menuItem.
     *
     * 更新權限資源內容
     */
    @PostMapping("/menu")
    public JsonResponse updateMenuItem(@RequestBody MenuItem menuItem) {
        roleMenuPermissionService.updateMenuItem(menuItem);
        return new JsonResponse(true);
    }

    @PostMapping("/menu/lang")
	public JsonResponse updateMenuLabel(@RequestBody ApiLabel apiLabel){
		roleMenuPermissionService.saveOrUpdateLabel(apiLabel);
		return new JsonResponse(true);
	}

    /**
     * Create correlation between role and menu. Use roleId and menuIdList to link the relationship.
     *
     * 建立角色與權限資源的關聯
     */
    @PutMapping("/{roleId}/_correlate_permission")
    public JsonResponse correlatePermission(@PathVariable("roleId") Integer roleId, @RequestBody List<Integer> menuIdList) {
        roleMenuPermissionService.correlatePermission(roleId, menuIdList);
        return JsonResponse.OK;
    }


    /**
     * 以ID查詢權限資源
     */
    @GetMapping("/menu/{menuItemId}")
    public MenuItem queryMenuItem(@PathVariable("menuItemId") Integer menuItemId) {
        return roleMenuPermissionService.queryMenuItemById(menuItemId);
    }

    /**
     * 查詢角色擁有的權限資源
     * <P>URL-http://localhost:7001/user-service-service/resources/roleMenuPermission/{roleId}/_query_menu_items_by_role</P>
     * <P>HTTP Type-GET</P>
     *
     * @param roleId
     *
     * @return
     *
     * @throws AdminServiceBaseException
     */
    @GetMapping("/menu/role/{roleId}")
    public List<MenuItem> queryMenuItemsByRole(@PathVariable("roleId") Integer roleId) {
        return roleMenuPermissionService.queryMenuItemsByRole(roleId);
    }

    /**
     * 驗證使用者是否擁有此權限
     */
    @GetMapping("/role/menu/{menuId}")
    public List<Integer> queryRoleByMenuId(@PathVariable("menuId") Integer menuId) {
        return roleMenuPermissionService.queryRolesByMenuId(menuId);
    }

    /**
     * 更新選單
     */
    @GetMapping("/refresh")
    public Boolean refreshMenu() {
        commonService.refresh();
        merchantService.renewMerchantList();
        return true;
    }

    /**
     * 查詢Operator的MenuTree
     * Menu Tree
     */
    @GetMapping("/{operatorId}/menu")
    public JsonResponseT<JsTreeMenu> getOperatorJsMenuTree(@PathVariable("operatorId") Integer operatorId) {
        JsonResponseT<JsTreeMenu> result = new JsonResponseT<>();
        Operator operator = new Operator();
        operator.setOperatorId(operatorId);
        result.setValue(roleMenuPermissionService.getOperatorJsMenuTree(operator));
        result.setSuccess(Boolean.TRUE);
        return result;
    }

    @GetMapping("/roles/menu/{menuId}/{pageNumber}/{pageSize}")
    public JsonResponseT<Object> getRolesByMenuId(@PathVariable("menuId") Integer menuId,
            @PathVariable("pageNumber") Integer pageNumber,
            @PathVariable("pageSize") Integer pageSize,
            @RequestParam(value = "activeFlag", required = false) Integer activeFlag,
            @RequestParam(value = "sortOrder", required = false) String sortOrder,
            @RequestParam(value = "sortColumn", required = false) String sortColumn
                                     ) {
        JsonResponseT<Object> response = new JsonResponseT<>(true);
        Page<Role> queryResult = roleMenuPermissionService.getRolesByMenuId(menuId, activeFlag, pageNumber, pageSize, sortOrder, sortColumn);

        response.setValue(queryResult);
        return response;
    }

    @GetMapping("/bpmPermission")
    public JsonResponseT<Set<String>> getBpmPermission() {

        UserInfo<Operator> userInfo = RequestHelper.getCurrentUser();

        Set<String> result = bpmPermissionService.getBpmGroupKeysByOperator(userInfo.getUser());

        JsonResponseT<Set<String>> response = new JsonResponseT<>(true);

        response.setValue(result);

        return response;
    }

}
