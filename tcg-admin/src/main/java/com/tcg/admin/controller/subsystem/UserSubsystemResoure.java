package com.tcg.admin.controller.subsystem;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tcg.admin.model.Operator;
import com.tcg.admin.service.OperatorService;
import com.tcg.admin.service.RoleMenuPermissionService;
import com.tcg.admin.to.response.JsonResponseT;
import com.tcg.admin.to.response.OperatorInfoTo;

@RestController
@RequestMapping(value = "/resources/subsystem/user", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserSubsystemResoure {

    @Autowired
    private OperatorService operatorService;

    @Autowired
    private RoleMenuPermissionService roleMenuPermissionService;
    
    @GetMapping
    public JsonResponseT<List<Operator>> getOperator(@RequestParam(value = "operatorIds", required = false) List<Integer> operatorIds) {
        JsonResponseT<List<Operator>> jp = new JsonResponseT<>(true);
        List<Operator> operators = operatorService.findOperatorByIds(operatorIds);
        jp.setValue(operators);
        return jp;
    }
    
    @GetMapping("permission-list")
    public JsonResponseT<List<OperatorInfoTo>> getPermissionList(@RequestParam(value = "menuId", required = false) Integer menuId) {
        JsonResponseT<List<OperatorInfoTo>> jp = new JsonResponseT<>(true);
        jp.setValue(roleMenuPermissionService.findByMenuIdPermission(menuId));
        return jp;
    }

}
