package com.tcg.admin.persistence.springdata;

import com.tcg.admin.model.SystemHelper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by chris.h on 12/13/2017.
 */
public interface ISystemHelperRepository extends JpaRepository<SystemHelper, Integer> {
    @Query("select a from SystemHelper a where a.menuId =?1")
    SystemHelper findBySystemHelperID(Integer menuId);

}
