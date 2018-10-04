package com.tcg.admin.service.impl;


import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tcg.admin.model.AdminRoles;
import com.tcg.admin.persistence.springdata.IAdminRolesRepository;
import com.tcg.admin.service.AdminRolesService;

@Service
@Transactional
public class AdminRolesServiceImpl implements AdminRolesService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminRolesServiceImpl.class);

    @Autowired
    private IAdminRolesRepository adminRolesRepository;

	@Override
	public List<AdminRoles> getAll() {
		return adminRolesRepository.findAll();
	}

  

}
