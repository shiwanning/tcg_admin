package com.tcg.admin.utils.shiro;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tcg.admin.common.constants.UsLoginUserTypeConstant;
import com.tcg.admin.model.Operator;
import com.tcg.admin.model.RoleMenuPermission;
import com.tcg.admin.model.RoleOperator;
import com.tcg.admin.persistence.springdata.IMenuItemRepository;
import com.tcg.admin.persistence.springdata.IOperatorRepository;
import com.tcg.admin.persistence.springdata.IRoleMenuPermissionRepository;
import com.tcg.admin.persistence.springdata.IRoleOperatorRepository;
import com.tcg.admin.persistence.springdata.IRoleRepository;

@Service
public class JpaRealm extends AuthorizingRealm {

	private static final String REALM_NAME = "jpaRealm";

    @Autowired
    private IOperatorRepository operatorRepository;

    @Autowired
    private IRoleMenuPermissionRepository roleMenuRepository;

    @Autowired
    private IMenuItemRepository menuRepository;

    @Autowired
    private IRoleRepository roleRepository;

    @Autowired
    private IRoleOperatorRepository roleOperatorRepository;

    public JpaRealm() {
        setName(REALM_NAME);
    }

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof CustomizedToken;
    }

    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authToken) {
        CustomizedToken token = (CustomizedToken) authToken;
        if (token.getLoginUserType() == UsLoginUserTypeConstant.CUSTOMER.getType()) {
            return null;
        } else if (token.getLoginUserType() == UsLoginUserTypeConstant.OPERATOR.getType()) {
            return operatorAuthenticate(token);
        } else {
            throw new AuthenticationException("Invalid authentication !");
        }
    }

    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {

        String operatorName = (String) principals.getPrimaryPrincipal();
        Operator operator = operatorRepository.findByOperatorName(operatorName);

    	Set<String> roleNames = new HashSet<>();
    	Set<String> permissionNames = new HashSet<>();
        
        List<RoleOperator> roleOperatorList = roleOperatorRepository.findByOperatorId(operator.getOperatorId());
        for (RoleOperator ro : roleOperatorList) {
            roleNames.add(roleRepository.findOne(ro.getRoleId()).getRoleName());
            List<RoleMenuPermission> roleMenuList = roleMenuRepository.findByRoleId(ro.getRoleId());
            for (RoleMenuPermission p : roleMenuList) {
                permissionNames.add(menuRepository.findOne(p.getMenuId()).getMenuName());
            }
        }

        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        authorizationInfo.setRoles(roleNames);
        authorizationInfo.setStringPermissions(permissionNames);
        return authorizationInfo;
    }

    private AuthenticationInfo operatorAuthenticate(CustomizedToken token) {
        String operatorName = (String) token.getPrincipal();
        String password = token.getPassword();

        Operator operator = operatorRepository.findByOperatorName(operatorName);
        if (operator == null) {
            throw new UnknownAccountException();
        }
        return new SimpleAuthenticationInfo(operatorName, password, getName());
    }
}
