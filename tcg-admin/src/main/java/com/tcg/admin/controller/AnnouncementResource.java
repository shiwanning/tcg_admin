package com.tcg.admin.controller;

import java.io.IOException;
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

import com.tcg.admin.common.helper.RequestHelper;
import com.tcg.admin.model.Announcement;
import com.tcg.admin.model.Operator;
import com.tcg.admin.service.AnnouncementService;
import com.tcg.admin.service.FileService;
import com.tcg.admin.service.OperatorAuthenticationService;
import com.tcg.admin.service.impl.IMService;
import com.tcg.admin.to.AnnouncementTO;
import com.tcg.admin.to.FileInfoTo;
import com.tcg.admin.to.UserInfo;
import com.tcg.admin.to.response.JsonResponseT;
import com.tcg.admin.utils.DateTools;

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
    private OperatorAuthenticationService operatorAuthService;
    
    @Autowired
    private FileService fileService;
	
    @GetMapping
    public JsonResponseT<Object> queryAnnouncement(
            @RequestParam(value = "startDate", required = false)String startDate,
            @RequestParam(value = "endDate", required = false)String endDate,
            @RequestParam(value = "summaryContent", required = false)String summaryContent,
            @RequestParam(value = "status", required = false)int status,
            @RequestParam(value = "pageNo", required = false) int pageNo,
            @RequestParam(value = "pageSize", required = false) int pageSize,
            @RequestParam(value = "announcementType", required = false) String announcementType) {
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

        Date startDate = DateTools.toDate("MM/dd/yyyy hh:mm:ss", emergency.getStartTime());
        Date today = DateTools.toDate("MM/dd/yyyy hh:mm:ss", new Date());
        
		try {
			if(emergency.getAnnouncementType().equalsIgnoreCase(AnnouncementService.EMERGENCY)
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
        Date startDate = DateTools.toDate("MM/dd/yyyy hh:mm:ss", emergency.getStartTime());
        Date today = DateTools.toDate("MM/dd/yyyy hh:mm:ss", new Date());

        try {
			if(emergency.getAnnouncementType().equalsIgnoreCase(AnnouncementService.EMERGENCY)
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
    public JsonResponseT<Map<String, Object>> getAvailableAnnouncement(@RequestParam(value="endDate", required = false)String endDate,
            @RequestParam(value="pageNo", required = false) int pageNo,
            @RequestParam(value="pageSize", required = false) int pageSize,
            @RequestParam(value="status", required = false) String status,
            @RequestParam(value="announcementType", required = false) String announcementType,
            @RequestParam(value="vendor", required = false) String vendor) {
    	JsonResponseT<Map<String, Object>> response = new JsonResponseT<>(true);
    	response.setValue(announcementService.getAvailableAnnouncement2(endDate, pageNo, pageSize, status, announcementType, vendor));
    	 return response;
    }
    
    @GetMapping("/unReadActiveAnnouncementCount")
    public JsonResponseT<Map<String, Integer>> getUnReadActiveAnnouncementCount() {
        JsonResponseT<Map<String, Integer>> response = new JsonResponseT<>(true);
        UserInfo<Operator> userInfo = operatorAuthService.getOperatorByToken(RequestHelper.getToken());
        Integer operatorId = userInfo.getUser().getOperatorId();
        response.setValue(announcementService.getUnreadActiveAnnouncementCount(operatorId));
         return response;
    }
    
    @PutMapping("/markAsRead")
    public JsonResponseT<Map<String, Object>> markAsRead(@RequestBody AnnouncementTO announcementTO) {
    	JsonResponseT<Map<String, Object>> response = new JsonResponseT<>(true);
    	
    	if(announcementTO.getIdsToRead() == null && announcementTO.getAnnouncementType() != null) {
    	    List<Integer> announcementIds = announcementService.getIdsToListByType(announcementTO.getAnnouncementType());
    	    announcementTO.setIdsToRead(announcementIds);
    	}
    	
    	announcementService.markAsReadAnnouncementById(announcementTO.getIdsToRead());
    	response.setValue(announcementService.getAvailableAnnouncement(announcementTO.getEndDate()));
    	return response;
    }
}