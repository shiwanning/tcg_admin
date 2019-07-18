package com.tcg.admin.controller;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.collect.Lists;
import com.tcg.admin.common.constants.AnnouncementConstant;
import com.tcg.admin.common.helper.RequestHelper;
import com.tcg.admin.model.Announcement;
import com.tcg.admin.model.Operator;
import com.tcg.admin.service.AnnouncementService;
import com.tcg.admin.service.FileService;
import com.tcg.admin.service.impl.IMService;
import com.tcg.admin.to.AnnouncementTO;
import com.tcg.admin.to.FileInfoTo;
import com.tcg.admin.to.UserInfo;
import com.tcg.admin.to.response.JsonResponse;
import com.tcg.admin.to.response.JsonResponseT;

@RestController
@RequestMapping(value = "/resources/announcement", produces = MediaType.APPLICATION_JSON_VALUE)
public class AnnouncementResource {

	private static final Logger LOGGER = LoggerFactory.getLogger(AnnouncementResource.class);
	
	public static final String ANNOUNCEMENT_FILE_MODULE = "announcement";
	
    @Autowired
    private AnnouncementService announcementService;
    
    @Autowired
	IMService imService;
    
    @Autowired
    private FileService fileService;
	
    @GetMapping
    public JsonResponseT<Object> queryAnnouncement(
            @RequestParam(value = "startDate", required = false)String startDate,
            @RequestParam(value = "endDate", required = false)String endDate,
            @RequestParam(value = "summaryContent", required = false)String summaryContent,
            @RequestParam(value = "status", required = true, defaultValue = "0")int status,
            @RequestParam(value = "pageNo", required = true, defaultValue = "0") int pageNo,
            @RequestParam(value = "pageSize", required = true, defaultValue = "20") int pageSize,
            @RequestParam(value = "announcementType", required = true, defaultValue = "0") String announcementType) {
        JsonResponseT<Object> response = new JsonResponseT<>(true);
        Page<Announcement> queryResultresponse = announcementService.queryAnnouncement(startDate, endDate, summaryContent, status, pageNo, pageSize, announcementType);
        response.setValue(queryResultresponse);
        return response;
    }

    @PostMapping("/createAnnouncement")
    public JsonResponseT<Announcement> createAnnouncement(
    		@RequestParam(value = "chineseFile", required = false) MultipartFile chinesefile,
    		@RequestParam(value = "englishFile", required = false) MultipartFile englishfile,
    		AnnouncementTO announcementTO) throws IOException {
        JsonResponseT<Announcement> response = new JsonResponseT<>(true);
        FileInfoTo chineseFileInfo = null;
        FileInfoTo englishFileInfo = null;
        
        if(chinesefile != null) {
        	chineseFileInfo = fileService.uploadFile(chinesefile, "announcement");
        }
        
        if(englishfile != null) {
        	englishFileInfo = fileService.uploadFile(englishfile, "announcement");
        }
        
        Announcement emergency = announcementService.saveAnnouncement(announcementTO, chineseFileInfo, englishFileInfo);
        response.setValue(emergency);

        Date startDate = emergency.getStartTime();
        Date today = new Date();
        
		try {
			if(emergency.getAnnouncementType().equalsIgnoreCase(AnnouncementConstant.EMERGENCY)
					&& !startDate.after(today)){
				imService.publishEmergencyAnnouncement(emergency);
			}
		} catch (Exception e) {
			LOGGER.error("ERROR IN IM SERVICE", e);
		}
        return response;
    }


    @PostMapping("/updateAnnouncement")
    @PutMapping("/updateAnnouncement")
    public JsonResponseT<Announcement> updateAnnouncement(
    		@RequestParam(value = "chineseFile", required = false) MultipartFile chinesefile,
    		@RequestParam(value = "englishFile", required = false) MultipartFile englishfile,
    		@RequestParam(value = "deleteChineseFile", defaultValue = "false") boolean deleteChineseFile,
    		@RequestParam(value = "deleteEnglishFile", defaultValue = "false") boolean deleteEnglishFile,
    		AnnouncementTO announcementTO) throws IOException {
        JsonResponseT<Announcement> response = new JsonResponseT<>(true);
        
        FileInfoTo chineseFileInfo = null;
        FileInfoTo englishFileInfo = null;
        
        if(chinesefile != null) {
        	chineseFileInfo = fileService.uploadFile(chinesefile, ANNOUNCEMENT_FILE_MODULE);
        }
        
        if(englishfile != null) {
        	englishFileInfo = fileService.uploadFile(englishfile, ANNOUNCEMENT_FILE_MODULE);
        }
        
        Announcement emergency = announcementService.updateAnnouncement(announcementTO, chineseFileInfo, englishFileInfo, deleteChineseFile, deleteEnglishFile);
        Date startDate = emergency.getStartTime();
        Date today = new Date();

        try {
			if(emergency.getAnnouncementType().equalsIgnoreCase(AnnouncementConstant.EMERGENCY)
					&& !startDate.after(today)){
				imService.publishEmergencyAnnouncement(emergency);
			}
		} catch (Exception e) {
			LOGGER.error("ERROR IN IM SERVICE", e);
		}
        return response;
    }

    @PutMapping("/updateStatus")
    public JsonResponseT<Announcement> updateStatus(@RequestBody AnnouncementTO announcementTO) {
        JsonResponseT<Announcement> response = new JsonResponseT<>(true);
        announcementService.updateStatus(announcementTO);
        return response;
    }

    @GetMapping("/activeAnnouncement")
    public JsonResponseT<Map<String, Object>> getAvailableAnnouncement(
            @RequestParam(value="pageNo", required = true, defaultValue = "0") int pageNo,
            @RequestParam(value="pageSize", required = true, defaultValue = "5") int pageSize,
            @RequestParam(value="status", required = false, defaultValue = "0") String status,
            @RequestParam(value="announcementType", required = true, defaultValue = "0") String announcementType,
            @RequestParam(value="maintenanceType", required = false) Integer maintenanceType,
            @RequestParam(value="vendor", required = false) String vendor) {
    	JsonResponseT<Map<String, Object>> response = new JsonResponseT<>(true);
    	
    	UserInfo<Operator> userInfo = RequestHelper.getCurrentUser();
        Integer operatorId = userInfo.getUser().getOperatorId();
        Calendar rightNow = Calendar.getInstance();
        Map<String, Object> announcementMap = announcementService.getAvailableAnnouncement(operatorId, status, announcementType, vendor, rightNow.get(Calendar.HOUR_OF_DAY));
      
        List<Announcement> allAnnouncementList = (List<Announcement>) announcementMap.get("announcement");
        
        if(maintenanceType != null) {
        	allAnnouncementList = matchMantainType(allAnnouncementList, maintenanceType);
        }

        //筛选当前页面数据
        boolean next = true;
        if(allAnnouncementList.size() >= pageNo* pageSize){
            Integer max = (pageNo+1)* pageSize;
            if(allAnnouncementList.size()<(pageNo+1)* pageSize){
                next = false;
                max = allAnnouncementList.size();
            }
            announcementMap.put("announcement", allAnnouncementList.subList(pageNo* pageSize,max));
        }
        announcementMap.put("next", next);
        announcementMap.put("pageNo", pageNo+1);
        
        response.setValue(announcementMap);
    	return response;
    }
    
    private List<Announcement> matchMantainType(List<Announcement> announcements, Integer maintenanceType) {
    	List<Announcement> result = Lists.newLinkedList();
    	for(Announcement an : announcements) {
    		if(isMatchMantainType(an, maintenanceType)) {
    			result.add(an);
    		}
    	}
		return result;
	}

	private boolean isMatchMantainType(Announcement announcement, Integer maintenanceType) {
    	if(maintenanceType == null) {
    		return true;
    	} else if(maintenanceType == 2) {
    		if (announcement.getMaintenanceEndTime() != null && announcement.getMaintenanceEndTime().after(new Date())) {
    			return true;
    		}
    		return false;
    	} else if(maintenanceType == 1) {
    		if (announcement.getMaintenanceEndTime() != null && announcement.getMaintenanceEndTime().before(new Date())) {
    			return true;
    		}
    		return false;
    	}
		return false;
	}
    
    @GetMapping("/unReadActiveAnnouncementCount")
    public JsonResponseT<Map<String, Integer>> getUnReadActiveAnnouncementCount() {
        JsonResponseT<Map<String, Integer>> response = new JsonResponseT<>(true);
        UserInfo<Operator> userInfo = RequestHelper.getCurrentUser();
        Integer operatorId = userInfo.getUser().getOperatorId();
        response.setValue(announcementService.getUnreadActiveAnnouncementCount(operatorId));
         return response;
    }
    
    @PutMapping("/markAsRead")
    public JsonResponse markAsRead(@RequestBody AnnouncementTO announcementTO) {
    	
    	if(announcementTO.getIdsToRead() == null && announcementTO.getAnnouncementType() != null) {
    	    List<Integer> announcementIds = announcementService.getIdsToListByType(announcementTO.getAnnouncementType());
    	    announcementTO.setIdsToRead(announcementIds);
    	}
    	UserInfo<Operator> userInfo = RequestHelper.getCurrentUser();
		Integer operatorId = userInfo.getUser().getOperatorId();
		
    	announcementService.markAsReadAnnouncementById(operatorId, announcementTO.getIdsToRead());
    	return JsonResponse.OK;
    }
}