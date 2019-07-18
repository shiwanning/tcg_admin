package com.tcg.admin.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import com.tcg.admin.model.Announcement;
import com.tcg.admin.to.AnnouncementTO;
import com.tcg.admin.to.FileInfoTo;

public interface AnnouncementService {

    Announcement saveAnnouncement(AnnouncementTO announcementTO, FileInfoTo chineseFileInfo, FileInfoTo englishFileInfo);

    Announcement updateAnnouncement(AnnouncementTO announcementTO, FileInfoTo chineseFileInfo, FileInfoTo englishFileInfo, Boolean deleteChineseFile, Boolean deleteEnglishFile);

    Page<Announcement> queryAnnouncement(String startDate, String endDate, String summaryContent, int status,int pageNo, int pageSize,String announcementType);

    void updateStatus(AnnouncementTO announcementTO);

    Map<String, Object> getAvailableAnnouncement(Integer operatorId, String status, String announcementType, String vendor, Integer hourforDate);

    void markAsReadAnnouncementById(Integer operatorId, List<Integer> idsToRead);

    Map<String, Integer> getUnreadActiveAnnouncementCount(Integer operatorId);

    List<Integer> getIdsToListByType(String announcementType);
}
