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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
import com.tcg.admin.cache.RedisCacheEvict;
import com.tcg.admin.common.helper.RequestHelper;
import com.tcg.admin.model.Announcement;
import com.tcg.admin.model.Operator;
import com.tcg.admin.model.ReadAnnouncement;
import com.tcg.admin.persistence.AnnouncementRepositoryCustom;
import com.tcg.admin.persistence.springdata.IAnnouncementReadRepository;
import com.tcg.admin.persistence.springdata.IAnnouncementRepository;
import com.tcg.admin.service.AnnouncementService;
import com.tcg.admin.service.FileService;
import com.tcg.admin.service.RoleMenuPermissionService;
import com.tcg.admin.service.TacCacheService;
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
    private IAnnouncementReadRepository readAnnouncementRepository;
    
    @Autowired
    private FileService fileService;
    
    @Autowired
    private RoleMenuPermissionService roleMenuPermissionService;
    
    @Autowired
    private TacCacheService tacCacheService;

    @Override
    @CacheEvict(cacheNames="tac-announcement", allEntries=true)
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
        
        model.setCreateOperatorName(RequestHelper.getCurrentUser().getUser().getOperatorName());
        return save(model,announcementTO);
    }

    @Override
    @CacheEvict(cacheNames="tac-announcement", allEntries=true)
    public Announcement updateAnnouncement(AnnouncementTO announcementTo, FileInfoTo chineseFileInfo, FileInfoTo englishFileInfo, 
    		Boolean deleteChineseFile, Boolean deleteEnglishFile) {
    	
        Announcement model = announcementRepository.findByAnnouncementID(announcementTo.getAnnouncementId());
        
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
        
        Announcement result = save(model, announcementTo);
        
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
        UserInfo<Operator> userInfo = RequestHelper.getCurrentUser();
        List<Map<String,String>> merchList =  roleMenuPermissionService.getMerchants(userInfo);
        Set<String> merchantIdSet = getMerchantIdSet(merchList);
        for(Announcement announcement :  data.getContent()) {
            if(canView(merchantIdSet, announcement.getMerchants())) {
                newAnnouncementList.add(announcement);
            }
        }

        return  new PageImpl<>(newAnnouncementList, pageable, data.getTotalElements());
    }

    private Announcement save(Announcement model,AnnouncementTO announcementTO) {
        model.setEnContent(announcementTO.getEnContent());
        model.setCnContent(announcementTO.getCnContent());
        model.setStatus(announcementTO.getStatus());
        model.setEnSummary(announcementTO.getEnSummary());
        model.setCnSummary(announcementTO.getCnSummary());
        model.setFrequency(announcementTO.getFrequency());
        model.setStartTime(DateTools.parseDate(announcementTO.getStartTime(), DATE_TIME_FORMAT));
        if(announcementTO.getMaintenanceStartTime() != null) {
        	model.setMaintenanceStartTime(DateTools.parseDate(announcementTO.getMaintenanceStartTime(), DATE_TIME_FORMAT));
        }
        if(announcementTO.getMaintenanceEndTime() != null) {
        	model.setMaintenanceEndTime(DateTools.parseDate(announcementTO.getMaintenanceEndTime(), DATE_TIME_FORMAT));
        }
        model.setMerchants(announcementTO.getMerchants());
        model.setMerchantType(announcementTO.getMerchantType());
        model.setAnnouncementType(announcementTO.getAnnouncementType());
        model.setVendor(announcementTO.getVendor());
        model.setUpdateOperatorName(RequestHelper.getCurrentUser().getUser().getOperatorName());
        return announcementRepository.saveAndFlush(model);
    }

    @Override
    @CacheEvict(cacheNames="tac-announcement", allEntries=true)
    public void updateStatus(AnnouncementTO announcementTO) {
        Announcement model = announcementRepository.findByAnnouncementID(announcementTO.getAnnouncementId());
        model.setStatus(announcementTO.getStatus());
        model.setUpdateOperatorName(RequestHelper.getCurrentUser().getUser().getOperatorName());
        announcementRepository.save(model);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @Cacheable(cacheNames="tac-announcement", key = "'getAvailableAnnouncement:@' + #announcementType + '@' + #status + '@' + #vendor + '@' + #operatorId + '@' + #hourforDate")
    public Map<String, Object> getAvailableAnnouncement(Integer operatorId, String status, String announcementType, String vendor, Integer hourforDate) {
        Map<String, Object> map = Maps.newHashMap();
        List<Announcement> resultAnnouncement = Lists.newLinkedList();
        Date currentDate = new Date();
        Date endTime = DateUtils.ceiling(currentDate, Calendar.HOUR);
        Date startTime = DateUtils.addMonths(DateUtils.truncate(currentDate, Calendar.DATE), -3);
        List<Announcement> data = announcementRepository.findByStartTimeGreaterThanAndStatusAndType(startTime, endTime, announcementType);
        List<ReadAnnouncement> readAnn = readAnnouncementRepository.findReadAnnouncementByOperatorId(operatorId);
        Set<Integer> readAnnouncement = this.getReadAnnouncement(readAnn);
        List<Map<String,String>> merchList =  roleMenuPermissionService.getMerchants(operatorId);
        //筛选当前用户所拥有mercahnt的 announcement
        List<Announcement> newAnnouncementList1 = filterMerchantAnnouncement(data, merchList);
        
        //筛选符合条件的数据
        for(Announcement announcement: newAnnouncementList1){
            if(announcement.getStartTime().after(currentDate) || (!("0").equals(announcementType) && !announcement.getAnnouncementType().equals(announcementType))){
            	continue;
            }
            if((vendor==null || vendor.equals(announcement.getVendor())) && isMatchStatusAnnocement(status, announcement, readAnnouncement)) {
            	resultAnnouncement.add(announcement);
            }
        }

        map.put("announcement", resultAnnouncement);
        map.put("readAnnouncement", readAnnouncement);
        
        return map;
    }

	private List<Announcement> filterMerchantAnnouncement(List<Announcement> data,
			List<Map<String, String>> merchList) {
    	List<Announcement> newAnnouncementList = Lists.newLinkedList();
    	Set<String> merchantIdSet = getMerchantIdSet(merchList);
    	for(Announcement announcement :  data) {
            String merchants = announcement.getMerchants();
            if(merchants == null || merchants.trim().isEmpty()) {
            	newAnnouncementList.add(announcement);
            	continue;
            }
            String[] merchantIds = merchants.split(",");
            for (String id : merchantIds) {
            	if(merchantIdSet.contains(id)) {
            		newAnnouncementList.add(announcement);
                    break;
            	}
            }
        }
		return newAnnouncementList;
	}

	private Set<String> getMerchantIdSet(List<Map<String, String>> merchList) {
		Set<String> merchantIds = Sets.newHashSet();
		for(Map<String,String> map : merchList) {
			merchantIds.add(map.get("merchantId"));
        }
		return merchantIds;
	}

	private boolean isMatchStatusAnnocement(String status, Announcement announcement, Set<Integer> readAnnouncement) {
    	return "0".equals(status) || isMatchReadAnnounced(status, announcement, readAnnouncement) || isMatchUnreadAnnounced(status, announcement, readAnnouncement);
	}
        
	private boolean isMatchReadAnnounced(String status, Announcement announcement, Set<Integer> readAnnouncement) {
		return ("1").equals(status) && readAnnouncement.contains(announcement.getAnnouncementId());
	}
	
	private boolean isMatchUnreadAnnounced(String status, Announcement announcement, Set<Integer> readAnnouncement) {
		return ("2").equals(status) && !readAnnouncement.contains(announcement.getAnnouncementId());
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
	
	@RedisCacheEvict(key = "'getUnreadActiveAnnouncementCount:' + #operatorId")
	public void markAsReadAnnouncementById(Integer operatorId, List<Integer> idsToRead) {
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
			readAnnouncementRepository.save(readAnnouncements);
			tacCacheService.evictCacheForReadAnnouncement(operatorId, idsToRead.get(0));
		}
		
	}

    @Override
    @Cacheable(cacheNames="tac-announcement", key = "'getUnreadActiveAnnouncementCount:' + #operatorId")
    public Map<String, Integer> getUnreadActiveAnnouncementCount(Integer operatorId) {
        Map<String, Integer> resuleMap = Maps.newHashMap();
        
        Date currentDate = new Date();
        Date endTime = DateUtils.ceiling(currentDate, Calendar.HOUR);
        Date startTime = DateUtils.addMonths(DateUtils.truncate(currentDate, Calendar.DATE), -3);
        List<Announcement> data = announcementRepository.findByStartTimeGreaterThanAndStatus(startTime, endTime);
        List<ReadAnnouncement> readAnn = readAnnouncementRepository.findReadAnnouncementByOperatorId(operatorId);
        Set<Integer> readAnnouncement = this.getReadAnnouncement(readAnn);
        List<Map<String,String>> merchList =  roleMenuPermissionService.getMerchants(operatorId);
        Set<String> merchantIdSet = getMerchantIdSet(merchList);
        for(Announcement announcement :  data) {
            String merchants = announcement.getMerchants();
            if(isUnreadAnnouncement(announcement, readAnnouncement, merchantIdSet, merchants)) {
                int typeCount = resuleMap.get(announcement.getAnnouncementType()) == null ? 0 : resuleMap.get(announcement.getAnnouncementType());
                resuleMap.put(announcement.getAnnouncementType(), typeCount + 1);
            }
        }
        
        return resuleMap;
    }

    private boolean isUnreadAnnouncement(Announcement announcement, Set<Integer> readAnnouncement, Set<String> merchantIdSet, String merchants) {
        if(merchants != null && !merchants.trim().isEmpty()) {
            String[] merchantIds = merchants.split(",");
            for (String id : merchantIds) {
                if (merchantIdSet.contains(id) && !readAnnouncement.contains(announcement.getAnnouncementId()) ) {
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
        
        UserInfo<Operator> userInfo = RequestHelper.getCurrentUser();
        List<Map<String,String>> merchList = roleMenuPermissionService.getMerchants(userInfo);
        Set<String> merchantIdSet = getMerchantIdSet(merchList);
        for(Announcement announcement : announcements) {
            String merchants = announcement.getMerchants();
            if(canView(merchantIdSet, merchants)) {
                resultList.add(announcement.getAnnouncementId());
            }
        }
        
        return resultList;
    }

    private boolean canView(Set<String> merchantIdSet, String merchants) {
        if(merchants != null && !merchants.trim().isEmpty()) {
            String[] merchantIds = merchants.split(",");
            for (String id : merchantIds) {
                if (merchantIdSet.contains(id)) {
                    return true;
                }
            }
            return false;
        }else{
            return true;
        }
    }
}
