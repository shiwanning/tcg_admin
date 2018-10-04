package com.tcg.admin.persistence.springdata;


import org.springframework.data.jpa.repository.JpaRepository;

import com.tcg.admin.model.AdminRoles;

public interface IAdminRolesRepository extends JpaRepository<AdminRoles, Integer> {


}
