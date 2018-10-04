package com.tcg.admin.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.tcg.admin.common.constants.SessionConstants;
import com.tcg.admin.common.exception.AdminServiceBaseException;
import com.tcg.admin.common.helper.RequestHelper;
import com.tcg.admin.model.Announcement;
import com.tcg.admin.model.Operator;
import com.tcg.admin.model.ReadAnnouncement;
import com.tcg.admin.persistence.AnnouncementRepositoryCustom;
import com.tcg.admin.persistence.springdata.IAnnouncementReadRepository;
import com.tcg.admin.persistence.springdata.IAnnouncementRepository;
import com.tcg.admin.service.AnnouncementService;
import com.tcg.admin.service.FileService;
import com.tcg.admin.service.OperatorAuthenticationService;
import com.tcg.admin.to.AnnouncementTO;
import com.tcg.admin.to.FileInfoTo;
import com.tcg.admin.to.UserInfo;
import com.tcg.admin.utils.DateTools;

@Service
@Transactional
public class AnnouncementServiceImpl implements AnnouncementService {

    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

    @Autowired
    private IAnnouncementRepository announcementRepository;

    @Autowired
    private AnnouncementRepositoryCustom announcementRepositoryCustom;

    @Autowired
    private OperatorAuthenticationService operatorAuthService;
    
    @Autowired
    private IAnnouncementReadRepository readAnnouncementRepository;
    
    @Autowired
    private FileService fileService;

    @Override
    public Announcement saveAnnouncement(AnnouncementTO announcementTO, FileInfoTo chineseFileInfo, FileInfoTo englishFileInfo) {
        Announcement model = new Announcement();
        if(chineseFileInfo != null) {
        	model.setCnAttachFileName(chineseFileInfo.getFileName());
            model.setCnAttachFileUrl(chineseFileInfo.getFileUrl());
        }
        if(englishFileInfo != null) {
        	model.setEnAttachFileName(englishFileInfo.getFileName());
            model.setEnAttachFileUrl(englishFileInfo.getFileUrl());
        }
        return save(model,announcementTO);
    }

    @Override
    public Announcement updateAnnouncement(AnnouncementTO announcementTO, FileInfoTo chineseFileInfo, FileInfoTo englishFileInfo, 
    		Boolean deleteChineseFile, Boolean deleteEnglishFile) {
    	
        Announcement model = announcementRepository.findByAnnouncementID(announcementTO.getAnnouncementId());
        
        String chineseFileUrl = model.getCnAttachFileUrl();
        String englishFileUrl = model.getEnAttachFileUrl();
        
        if(deleteChineseFile) {
        	model.setCnAttachFileName(null);
            model.setCnAttachFileUrl(null);
        }
        
        if(deleteEnglishFile) {
        	model.setEnAttachFileName(null);
            model.setEnAttachFileUrl(null);
        }
        
        if(chineseFileInfo != null) {
        	model.setCnAttachFileName(chineseFileInfo.getFileName());
            model.setCnAttachFileUrl(chineseFileInfo.getFileUrl());
        }
        if(englishFileInfo != null) {
        	model.setEnAttachFileName(englishFileInfo.getFileName());
            model.setEnAttachFileUrl(englishFileInfo.getFileUrl());
        }
        
        Announcement result = save(model,announcementTO);
        
        if(deleteChineseFile) {
        	fileService.deleteFromUrl(chineseFileUrl);
        }
        
        if(deleteEnglishFile) {
        	fileService.deleteFromUrl(englishFileUrl);
        }
        
        return result;
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public Page<Announcement> queryAnnouncement(String startDate, String endDate, String summaryContent, int status, int pageNo, int pageSize,String announcementType) {
        Pageable pageable = new PageRequest(pageNo, pageSize);
        List<Announcement> newAnnouncementList = new ArrayList<>();
        Page<Announcement> data = announcementRepositoryCustom.find(startDate, endDate, summaryContent,status,pageable, announcementType,null);
        List<Map<String,String>> merchList =  (List<Map<String,String>>)operatorAuthService.getSessionValue(RequestHelper.getToken(), SessionConstants.OPERATOR_MERCHANTS);
        for(Announcement announcement :  data.getContent()) {
            if(canView(merchList, announcement.getMerchants())) {
                newAnnouncementList.add(announcement);
            }
        }

        return  new PageImpl<>(newAnnouncementList, pageable, data.getTotalElements());
    }

    private Announcement save(Announcement model,AnnouncementTO announcementTO)
    {
        if(model != null) {
            model.setEnContent(announcementTO.getEnContent());
            model.setCnContent(announcementTO.getCnContent());
            model.setStatus(announcementTO.getStatus());
            model.setEnSummary(announcementTO.getEnSummary());
            model.setCnSummary(announcementTO.getCnSummary());
            model.setFrequency(announcementTO.getFrequency());
            model.setStartTime(DateTools.toDate(DATE_TIME_FORMAT,announcementTO.getStartTime()));
            model.setMerchants(announcementTO.getMerchants());
            model.setMerchantType(announcementTO.getMerchantType());
            model.setAnnouncementType(announcementTO.getAnnouncementType());
            model.setVendor(announcementTO.getVendor());
            return announcementRepository.saveAndFlush(model);
        }
        return null;
    }

    @Override
    public void updateStatus(AnnouncementTO announcementTO) {
        Announcement model = announcementRepository.findByAnnouncementID(announcementTO.getAnnouncementId());
        model.setStatus(announcementTO.getStatus());
        announcementRepository.save(model);
    }


    private boolean doesIdExist(String str_toFind, List<Map<String,String>> merchList)  {
        for(Map<String,String> map : merchList) {
            if(map.get("merchantId").equals(str_toFind))
                return true;
        }
        return false;
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Map<String, Object> getAvailableAnnouncement(String currentDate) throws AdminServiceBaseException {
		Map<String, Object> map = Maps.newHashMap();
		 List<Announcement> newAnnouncementList = new ArrayList<>();
		Pageable pageable = new PageRequest(0, 50);
		UserInfo<Operator> userInfo = operatorAuthService.getOperatorByToken(RequestHelper.getToken());
		Integer operatorId = userInfo.getUser().getOperatorId();
		Page<Announcement> data = announcementRepositoryCustom.find(null, currentDate, null,AnnouncementService.ACTIVE,pageable, AnnouncementService.ALL,null);
		List<ReadAnnouncement> readAnn = readAnnouncementRepository.findReadAnnouncementByOperatorId(operatorId);
		 List<Map<String,String>> merchList =  (List<Map<String,String>>)operatorAuthService.getSessionValue(RequestHelper.getToken(), SessionConstants.OPERATOR_MERCHANTS);
		 
		for(Announcement announcement :  data.getContent())
        {
            String merchants = announcement.getMerchants();
            if(merchants != null && !merchants.trim().isEmpty()) {
                String[] merchantIds = merchants.split(",");
                for (String id : merchantIds) {
                    if (doesIdExist(id, merchList)) {
                        newAnnouncementList.add(announcement);
                        break;
                    }
                }
            }else{
            	newAnnouncementList.add(announcement);
            }
        }
		map.put("announcement", newAnnouncementList);
		map.put("readAnnouncement", this.getReadAnnouncement(readAnn));
		return map;
	}

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public Map<String, Object> getAvailableAnnouncement2(String currentDateStr, Integer  pageNo, Integer pageSize, String status, String announcementType,String vendor) throws AdminServiceBaseException {
        Map<String, Object> map = Maps.newHashMap();
        boolean next = true;
        List<Announcement> newAnnouncementList1 = new ArrayList<>();
        List<Announcement> newAnnouncementList2 = new ArrayList<>();
        List<Announcement> newAnnouncementList3 = new ArrayList<>();
        UserInfo<Operator> userInfo = operatorAuthService.getOperatorByToken(RequestHelper.getToken());
        Integer operatorId = userInfo.getUser().getOperatorId();
        Date currentDate = new Date();
        Date date = DateUtils.ceiling(currentDate, Calendar.HOUR);
        List<Announcement> data = announcementRepository.findByStartTimeGreaterThanAndStatus(date);
        List<ReadAnnouncement> readAnn = readAnnouncementRepository.findReadAnnouncementByOperatorId(operatorId);
        Set<Integer> readAnnouncement = this.getReadAnnouncement(readAnn);
        List<Map<String,String>> merchList =  (List<Map<String,String>>)operatorAuthService.getSessionValue(RequestHelper.getToken(), SessionConstants.OPERATOR_MERCHANTS);
        //筛选当前用户所拥有mercahnt的 announcement
        for(Announcement announcement :  data)
        {
            String merchants = announcement.getMerchants();
            if(merchants != null && !merchants.trim().isEmpty()) {
                String[] merchantIds = merchants.split(",");
                for (String id : merchantIds) {
                    if (doesIdExist(id, merchList)) {
                        newAnnouncementList1.add(announcement);
                        break;
                    }
                }
            }else{
                newAnnouncementList1.add(announcement);
            }
        }
        //筛选符合条件的数据
        for(Announcement announcement: newAnnouncementList1){
            if(announcement.getStartTime().after(currentDate)) {
                continue;
            }
            
            if(("0").equals(announcementType)|| announcement.getAnnouncementType().equals(announcementType)){
                if(vendor==null || vendor.equals(announcement.getVendor()))
                {
                    if(("0").equals(status)){
                        newAnnouncementList2.add(announcement);
                    }else if(("1").equals(status)){
                        if(readAnnouncement.contains(announcement.getAnnouncementId())){
                            newAnnouncementList2.add(announcement);
                        }
                    }else if(("2").equals(status)){
                        if(!readAnnouncement.contains(announcement.getAnnouncementId())){
                            newAnnouncementList2.add(announcement);
                        }
                    }

                }
            }
        }
        //筛选当前页面数据
        if(newAnnouncementList2.size() >= pageNo* pageSize){
            Integer max = (pageNo+1)* pageSize;
            if(newAnnouncementList2.size()<(pageNo+1)* pageSize){
                next = false;
                max = newAnnouncementList2.size();
            }
            newAnnouncementList3 = newAnnouncementList2.subList(pageNo* pageSize,max);
        }

        map.put("announcement", newAnnouncementList3);
        map.put("readAnnouncement", this.getReadAnnouncement(readAnn));
        map.put("next", next);
        map.put("pageNo", pageNo+1);
        return map;
    }
	
	private Set<Integer> getReadAnnouncement(List<ReadAnnouncement> readAnns){
		
		if(CollectionUtils.isNotEmpty(readAnns)){
			Set<Integer> annIds = new HashSet<>(997);
			for(ReadAnnouncement ra : readAnns) {
				annIds.add(ra.getAnnouncementId());
			}
			return annIds;
		}
		
		return Sets.newHashSet();
	}
	
	public void markAsReadAnnouncementById(List<Integer> idsToRead) {
		UserInfo<Operator> userInfo = operatorAuthService.getOperatorByToken(RequestHelper.getToken());
		Integer operatorId = userInfo.getUser().getOperatorId();
		List<ReadAnnouncement> readAnnouncements = Lists.newLinkedList();
		
		List<ReadAnnouncement> readAnnouncementsIds = readAnnouncementRepository.findReadAnnouncementByOperatorId(operatorId);
		
		Set<Integer> alreadyReadIds = getReadAnnouncement(readAnnouncementsIds);
				 
		idsToRead.removeAll(alreadyReadIds);
		
		if(!CollectionUtils.isEmpty(idsToRead)){
			for(Integer readItems: idsToRead){
				ReadAnnouncement read = new ReadAnnouncement();
				read.setOperatorId(operatorId);
				read.setAnnouncementId(readItems);
				readAnnouncements.add(read);
			}
		}
		
		readAnnouncementRepository.save(readAnnouncements);
	}

    @Override
    public Map<String, Integer> getUnreadActiveAnnouncementCount(Integer operatorId) {
        Map<String, Integer> resuleMap = Maps.newHashMap();
        
        Date currentDate = new Date();
        Date date = DateUtils.ceiling(currentDate, Calendar.HOUR);
        List<Announcement> data = announcementRepository.findByStartTimeGreaterThanAndStatus(date);
        List<ReadAnnouncement> readAnn = readAnnouncementRepository.findReadAnnouncementByOperatorId(operatorId);
        Set<Integer> readAnnouncement = this.getReadAnnouncement(readAnn);
        List<Map<String,String>> merchList =  (List<Map<String,String>>)operatorAuthService.getSessionValue(RequestHelper.getToken(), SessionConstants.OPERATOR_MERCHANTS);
        
        for(Announcement announcement :  data) {
            String merchants = announcement.getMerchants();
            if(isUnreadAnnouncement(announcement, readAnnouncement, merchList, merchants)) {
                int typeCount = resuleMap.get(announcement.getAnnouncementType()) == null ? 0 : resuleMap.get(announcement.getAnnouncementType());
                resuleMap.put(announcement.getAnnouncementType(), typeCount + 1);
            }
        }
        
        return resuleMap;
    }

    private boolean isUnreadAnnouncement(Announcement announcement, Set<Integer> readAnnouncement, List<Map<String, String>> merchList, String merchants) {
        if(merchants != null && !merchants.trim().isEmpty()) {
            String[] merchantIds = merchants.split(",");
            for (String id : merchantIds) {
                if (doesIdExist(id, merchList) && !readAnnouncement.contains(announcement.getAnnouncementId()) ) {
                    return true;
                }
            }
        } else if(!readAnnouncement.contains(announcement.getAnnouncementId())) {
            return true;
        }
        
        return false;
    }

    @Override
    public List<Integer> getIdsToListByType(String announcementType) {

        List<Announcement> announcements = announcementRepository.findActiveByAnnouncementTypeAndStartDate(announcementType, new Date());
        
        List<Integer> resultList = Lists.newLinkedList();
        
        List<Map<String,String>> merchList =  (List<Map<String,String>>)operatorAuthService.getSessionValue(RequestHelper.getToken(), SessionConstants.OPERATOR_MERCHANTS);
        
        for(Announcement announcement : announcements) {
            String merchants = announcement.getMerchants();
            if(canView(merchList, merchants)) {
                resultList.add(announcement.getAnnouncementId());
            }
        }
        
        return resultList;
    }

    private boolean canView(List<Map<String, String>> merchList, String merchants) {
        if(merchants != null && !merchants.trim().isEmpty()) {
            String[] merchantIds = merchants.split(",");
            for (String id : merchantIds) {
                if (doesIdExist(id, merchList)) {
                    return true;
                }
            }
            return false;
        }else{
            return true;
        }
    }
}
