package com.tcg.admin.service.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tcg.admin.model.ApiLabel;
import com.tcg.admin.model.MenuItem;
import com.tcg.admin.model.Task;
import com.tcg.admin.persistence.springdata.IApiLabelRepository;
import com.tcg.admin.persistence.springdata.IMenuItemRepository;
import com.tcg.admin.persistence.springdata.ITaskRepository;
import com.tcg.admin.service.MenuItemService;
import com.tcg.admin.to.ApiLabelTo;

@Service
@Transactional
public class MenuItemServiceImpl implements MenuItemService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MenuItemServiceImpl.class);

    @Autowired
    private IMenuItemRepository menuItemRepository;
    
    @Autowired
    private IApiLabelRepository apiLabelRepository;

    @Autowired
    private ITaskRepository taskRepository;

    @Override
    public MenuItem findMenuItemByBlurUrl(String blurUrl, String accessType) {
        MenuItem menuItem = null;
        List<MenuItem> menuItemList = menuItemRepository.findByBlurUrl(blurUrl, accessType);

        if(CollectionUtils.isNotEmpty(menuItemList)){
            menuItem = menuItemList.get(0);
        }

        return menuItem;
    }

    @Override
    public MenuItem getMenuItemViaTaskId(Integer taskId) {

        Task task = taskRepository.findOne(taskId);
        Integer menuItemId = task.getState().getMenuId();

        return menuItemRepository.findOne(menuItemId);
    }

    @Override
    public List<MenuItem> queryMenuItems(boolean isAdmin) {
        return isAdmin ? menuItemRepository.queryMenuItemsForAdmin():menuItemRepository.queryMenuItemsForSystem();
    }

	@Override
	@Cacheable(value="cache-ap-admin", key="'translateMenu'")
	public ApiLabelTo getAllApiLabelTo() {
		List<ApiLabel> all = apiLabelRepository.findAll();
		ApiLabelTo result = new ApiLabelTo();
		
		for(ApiLabel label : all) {
			if("zh_cn".equalsIgnoreCase(label.getLanguageCode())) {
				result.getCn().put(label.getMenuId(), label.getLabel());
			} else if ("en_us".equalsIgnoreCase(label.getLanguageCode())) {
				result.getEn().put(label.getMenuId(), label.getLabel());
			}
		}
		
		return result;
	}

}
