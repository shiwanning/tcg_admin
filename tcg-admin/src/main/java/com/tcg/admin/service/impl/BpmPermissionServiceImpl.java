package com.tcg.admin.service.impl;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.tcg.admin.common.constants.IErrorCode;
import com.tcg.admin.common.error.AdminErrorCode;
import com.tcg.admin.common.exception.AdminServiceBaseException;
import com.tcg.admin.model.BpmGroup;
import com.tcg.admin.model.BpmRoleGroup;
import com.tcg.admin.model.MenuItem;
import com.tcg.admin.model.Operator;
import com.tcg.admin.model.Role;
import com.tcg.admin.persistence.springdata.IBpmGroupRepository;
import com.tcg.admin.persistence.springdata.IBpmRoleGroupRepository;
import com.tcg.admin.service.BpmPermissionService;
import com.tcg.admin.service.RoleMenuPermissionService;
import com.tcg.admin.service.RoleService;
import com.tcg.admin.to.Permission;
import com.tcg.admin.utils.StringTools;
import com.tcg.admin.utils.TreeTool;

@Service
@Transactional
public class BpmPermissionServiceImpl implements BpmPermissionService {

    @Autowired
    RoleMenuPermissionService rolePermissionService;

    @Autowired
    private IBpmRoleGroupRepository iBpmRoleGroupRepository;

    @Autowired
    private IBpmGroupRepository iBpmGroupRepository;

    @Autowired
    private RoleService roleService;

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public List<String> getGroupKeys(Integer roleId) {
        return iBpmRoleGroupRepository.findBpmGroupKeyByRoleId(roleId);
    }

    public List<BpmGroup> findAllBpmGroup() {
        return iBpmGroupRepository.findAll();
    }

    /**
     * 變更審核權限
     *
     * @param roleId
     * @param menuIdList
     *
     * @throws
     */
    @Override
    public void updatePermissionRoleGroup(Integer roleId, List<Integer> menuIdList) {

        try {
            if (roleId == null) {
                throw new AdminServiceBaseException(AdminErrorCode.PARAMETER_IS_REQUIRED, "roleId is null.");
            }

            // 新增之前先依role id 把所有對應的group刪除，再新增新的group對應
            iBpmRoleGroupRepository.deleteByRoleId(roleId);

            if (menuIdList != null && !menuIdList.isEmpty()) {

                // 依role id與對應menu id 增加role與group mapping
                List<BpmGroup> bpmGroupList = iBpmGroupRepository.findByMenuId(menuIdList);
                Date current = new Date();
                for (BpmGroup bpmGroup : bpmGroupList) {
                    BpmRoleGroup bpmRoleGroup = new BpmRoleGroup();
                    bpmRoleGroup.setRoleId(roleId);
                    bpmRoleGroup.setCreateTime(current);
                    bpmRoleGroup.setUpdateTime(current);
                    bpmRoleGroup.setGroupName(bpmGroup.getGroupName());
                    iBpmRoleGroupRepository.saveAndFlush(bpmRoleGroup);
                }
            }
        } catch (AdminServiceBaseException e) {
            throw e;
        } catch (Exception e) {
            throw new AdminServiceBaseException(IErrorCode.UNKNOWN_ERROR, e.getMessage(), e);
        }
    }

    /**
     * 取得有審核權限的MenuItem id list
     *
     * @param roleId
     *
     * @return
     *
     * @throws
     */
    @Override
    public List<Integer> getBpmCheckListByRole(Integer roleId) {
        return iBpmGroupRepository.findBpmCheckListByRole(roleId);
    }

    @Override
    public List<?> generatePermissionTree(Integer roleId) {

        if (StringTools.isEmptyOrNull(Integer.toString(roleId)) || roleId.equals(0)) {
            throw new AdminServiceBaseException(AdminErrorCode.PARAMETER_ERROR, "request param required");
        }

        List<?> returnValue = null;
        List<Permission> list = Lists.newLinkedList();
        Map<Integer, Permission> hashmap = Maps.newHashMap();
        Map<Integer, List<MenuItem>> map = rolePermissionService.queryAllMenuTree();
        for (Map.Entry<Integer, List<MenuItem>> m : map.entrySet()) {
            for (MenuItem mi : m.getValue()) {
                if (mi != null) {
                	Permission r = new Permission();
                    r.setDescription(mi.getDescription());
                    r.setIsLeaf(mi.getIsLeaf());
                    r.setDisplayOrder(mi.getDisplayOrder());
                    r.setParentId(mi.getParentId());
                    r.setMenuName(mi.getMenuName());
                    r.setTreeLevel(mi.getTreeLevel());
                    r.setUrl(mi.getUrl());
                    r.setMenuId(mi.getMenuId());
                    r.setIsDisplay(mi.getIsDisplay());
                    r.setIsButton(mi.getIsButton());
                    r.setLabels(mi.getLabels());
                    if (roleId == -1) {
                        List<BpmGroup> bpmList = this.findAllBpmGroup();
                        for (BpmGroup bpmGroup : bpmList) {
                            if (bpmGroup.getMenuId().equals(r.getMenuId())) {
                                r.setBpm(mi.getMenuId());
                            }
                        }
                    }
                    hashmap.put(r.getMenuId(), r);
                }
            }
        }
        for (Map.Entry<Integer, Permission> per : hashmap.entrySet()) {
            list.add(per.getValue());
        }
        if (roleId == -1) {//以treegrid方式显示
            returnValue = Permission.permisionTreeGrid(list, list, 0);
        } else if (roleId == -3) {//以树状显示
            List<Permission> list1 = Lists.newLinkedList();
            Permission p = new Permission();
            p.setDescription("根节点");
            p.setParentId(-1);
            p.setMenuName("根节点");
            p.setTreeLevel(-1);
            p.setMenuId(0);
            list1.add(p);
            for (Permission permission : list) {
                if (!permission.getTreeLevel().equals(3)) {
                    list1.add(permission);
                }
            }
            returnValue = TreeTool.permissionTree(list1, list1, -1, 1);

        }

        if (CollectionUtils.isEmpty(returnValue)) {
            throw new AdminServiceBaseException(AdminErrorCode.GENERIC_SYSTEM_ERROR, "role id is not valid");
        }

        return returnValue;
    }

    @Override
    public Set<String> getBpmGroupKeysByOperator(Operator operator) {

        List<Role> roles = roleService.queryOperatorAllRoles(operator);

        Set<String> result = new HashSet<>();

        for(Role role : roles) {
            result.addAll(new HashSet<>(getGroupKeys(role.getRoleId())));
        }

        return result;
    }
}
