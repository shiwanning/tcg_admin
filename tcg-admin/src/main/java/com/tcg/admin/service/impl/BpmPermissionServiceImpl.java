package com.tcg.admin.service.impl;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.tcg.admin.common.error.AdminErrorCode;
import com.tcg.admin.common.exception.AdminServiceBaseException;
import com.tcg.admin.model.BpmGroup;
import com.tcg.admin.model.BpmRoleGroup;
import com.tcg.admin.model.Operator;
import com.tcg.admin.model.Role;
import com.tcg.admin.persistence.springdata.IBpmGroupRepository;
import com.tcg.admin.persistence.springdata.IBpmRoleGroupRepository;
import com.tcg.admin.service.BpmPermissionService;
import com.tcg.admin.service.RoleMenuPermissionService;
import com.tcg.admin.service.RoleService;

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
            throw new AdminServiceBaseException(AdminErrorCode.UNKNOWN_ERROR, e.getMessage(), e);
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
    public Set<String> getBpmGroupKeysByOperator(Operator operator) {

        List<Role> roles = roleService.queryOperatorAllRoles(operator);

        Set<String> result = new HashSet<>();

        for(Role role : roles) {
            result.addAll(new HashSet<>(getGroupKeys(role.getRoleId())));
        }

        return result;
    }
}
