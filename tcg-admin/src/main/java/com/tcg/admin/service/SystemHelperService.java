package com.tcg.admin.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.tcg.admin.model.SystemHelper;
import com.tcg.admin.model.SystemHelperTemp;
import com.tcg.admin.to.SystemHelperTO;

/**
 * Created by chris.h on 12/13/2017.
 */
public interface SystemHelperService {
	
    int saveSystemHelper(SystemHelperTO systemHelperTO);

    int updateSystemHelper(SystemHelperTO systemHelperTO);

    Page<SystemHelper> querySystemHelper(Integer menuId,String content, Integer status, int pageNo, int pageSize);

    void updateStatus(SystemHelperTO systemHelperTO);
    
    SystemHelper findHelperByMenuId(Integer menuId);
    
    SystemHelperTemp findHelperTempByTaskId(Integer taskId);
    
    SystemHelperTemp approveHelperRequest(SystemHelperTO to);
    
    SystemHelperTemp rejectHelperRequest(SystemHelperTO to);
    
    List<SystemHelper> search(String query, String lang);

    List<SystemHelper> searchByMenuId(Integer menuId);
    
    void closeNotification(SystemHelperTO to);

	List<SystemHelper> searchByRole(Integer roleId);

	List<SystemHelper> searchBySingleMenuId(Integer menuId);
    
}
