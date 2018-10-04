package com.tcg.admin.service;

import java.util.List;

import com.tcg.admin.model.MenuItem;
import com.tcg.admin.to.ApiLabelTo;

public interface MenuItemService {

	MenuItem findMenuItemByBlurUrl(String blurUrl, String resourceType);

	MenuItem getMenuItemViaTaskId(Integer taskId);

	List<MenuItem> queryMenuItems(boolean isAdmin);
	
	ApiLabelTo getAllApiLabelTo();

}
