package com.tcg.admin.service;

import com.tcg.admin.common.exception.AdminServiceBaseException;
import com.tcg.admin.model.Announcement;
import com.tcg.admin.to.AnnouncementTO;
import com.tcg.admin.to.FileInfoTo;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

/**
 * Created by ian.r on 6/7/2017.
 */
public interface AnnouncementService {
	
	static final Integer ACTIVE = 1;
	
	static final String ALL = "0";
	
	static final String EMERGENCY = "1";
	
	static final String MAINTENANCE = "2";
	
	static final String UPDATE = "3";

    Announcement saveAnnouncement(AnnouncementTO announcementTO, FileInfoTo chineseFileInfo, FileInfoTo englishFileInfo);

    Announcement updateAnnouncement(AnnouncementTO announcementTO, FileInfoTo chineseFileInfo, FileInfoTo englishFileInfo, Boolean deleteChineseFile, Boolean deleteEnglishFile) throws AdminServiceBaseException;

    Page<Announcement> queryAnnouncement(String startDate, String endDate, String summaryContent, int status,int pageNo, int pageSize,String announcementType);

    void updateStatus(AnnouncementTO announcementTO) throws AdminServiceBaseException;
    
    Map<String, Object> getAvailableAnnouncement(String currentDate) throws AdminServiceBaseException;

    Map<String, Object> getAvailableAnnouncement2(String currentDate, Integer  pageNo , Integer pageSize, String status, String announcementType,String vendor) throws AdminServiceBaseException;

    void markAsReadAnnouncementById(List<Integer> idsToRead);

    Map<String, Integer> getUnreadActiveAnnouncementCount(Integer operatorId);

    List<Integer> getIdsToListByType(String announcementType);
}
