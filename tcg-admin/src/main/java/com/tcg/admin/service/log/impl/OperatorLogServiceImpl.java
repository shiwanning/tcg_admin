package com.tcg.admin.service.log.impl;

import com.tcg.admin.model.OperatorLog;
import com.tcg.admin.persistence.springdata.IMenuItemRepository;
import com.tcg.admin.persistence.springdata.IMerchantOperatorRepository;
import com.tcg.admin.persistence.springdata.IOperatorLogRepository;
import com.tcg.admin.persistence.springdata.IRoleMenuPermissionRepository;
import com.tcg.admin.service.MenuItemService;
import com.tcg.admin.service.MerchantService;
import com.tcg.admin.service.log.OperatorLogService;
import com.tcg.admin.to.condition.OperatorLogCondition;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Lyndon.j
 * @date
 */
@Service
public class OperatorLogServiceImpl implements OperatorLogService {

    @Autowired
    private IOperatorLogRepository operatorLogRepository;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private IMenuItemRepository menuItemRepository;

    @Autowired
    private MenuItemService menuItemService;

    @Autowired
    private IMerchantOperatorRepository merchantOperatorRepository;

    @Autowired
    private IRoleMenuPermissionRepository roleMenuRepository;

    @Override
    public Page<OperatorLog> getOperatorLogList(final OperatorLogCondition condition, int pageNo, int pageSize) {
        Sort sort = new Sort(Sort.Direction.DESC, "createDate");
        Pageable pageable = new PageRequest(pageNo-1<0?0:pageNo-1, pageSize,sort);
        Page<OperatorLog> result = operatorLogRepository.findAll(getSpecification(condition), pageable);
        return result;
    }


    @Override
    public OperatorLog insert(OperatorLog operatorLog) {
        return operatorLogRepository.saveAndFlush(operatorLog);
    }


    @Override
    public OperatorLog insert(String merchantCode, String operatorName, String editedOperatorName, String function, String oldValue, String newValue, String requestParam) {
        OperatorLog log = new OperatorLog();
        log.setOperatorName(operatorName);
        log.setOperateFunction(function);
        log.setEditedOperatorName(editedOperatorName);
        log.setMerchantCode(merchantCode);
        log.setOldData(oldValue);
        log.setNewData(newValue);
        log.setLogContent(requestParam);
        log.setCreateDate(new Date());
        return insert(log);
    }

    @Override
    public void update(Long id,String newValue) {
        OperatorLog log = operatorLogRepository.findOne(id);
        log.setNewData(newValue);
        operatorLogRepository.saveAndFlush(log);
    }

    private Specification<OperatorLog> getSpecification(final OperatorLogCondition condition) {
        Specification<OperatorLog> specification = new Specification<OperatorLog>() {
            @Override
            public Predicate toPredicate(Root<OperatorLog> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                List<Predicate> list = new ArrayList<>();

                if (StringUtils.isNotBlank(condition.getEditedUserName())) {
                    list.add(cb.equal(root.get("editedOperatorName").as(String.class), condition.getEditedUserName()));
                }
                if (StringUtils.isNotBlank(condition.getMerchant())) {
                    list.add(cb.equal(root.get("merchantCode").as(String.class), condition.getMerchant()));
                }
                if (StringUtils.isNotBlank(condition.getUserName())) {
                    list.add(cb.equal(root.get("operatorName").as(String.class), condition.getUserName()));
                }
                if (StringUtils.isNotBlank(condition.getFunction())) {
                    list.add(cb.equal(root.get("operateFunction").as(String.class), condition.getFunction()));
                }
                if (null != condition.getStartDateTime() && null != condition.getEndDateTime()) {
                    list.add(cb.between(root.<Date>get("createDate"), condition.getStartDateTime(), condition.getEndDateTime()));
                }

                Predicate[] p = new Predicate[list.size()];
                return cb.and(list.toArray(p));
            }
        };
        return specification;
    }
}

