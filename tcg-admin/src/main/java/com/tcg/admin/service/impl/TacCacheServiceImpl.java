package com.tcg.admin.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import com.tcg.admin.model.Announcement;
import com.tcg.admin.persistence.springdata.IAnnouncementRepository;
import com.tcg.admin.service.TacCacheService;

import java.util.Calendar;

@Service
public class TacCacheServiceImpl implements TacCacheService {

	private static final Logger LOGGER = LoggerFactory.getLogger(TacCacheServiceImpl.class);

	@Autowired
	private CacheManager redisCacheManager;

	@Autowired
	private IAnnouncementRepository announcementRepository;

	@Override
	public void evictCacheForReadAnnouncement(Integer operatorId, Integer firstAnnouncementId) {
		
		try {
			Announcement an = announcementRepository.findOne(firstAnnouncementId);
			
			if(an == null) {
				LOGGER.warn("no announcement data, ignore evict cache");
				return;
			}
			
			Integer announcementType = Integer.valueOf(an.getAnnouncementType());
			String vendor = an.getVendor();
			
			Cache targetCache = redisCacheManager.getCache("tac-announcement");

			int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

			targetCache.evict("getAvailableAnnouncement:@" + announcementType + "@" + null + "@" + vendor + "@" + operatorId + "@" + hour);
			targetCache.evict("getAvailableAnnouncement:@" + announcementType + "@" + 0 + "@" + vendor + "@" + operatorId + "@" + hour);
			targetCache.evict("getAvailableAnnouncement:@" + announcementType + "@" + 1 + "@" + vendor + "@" + operatorId + "@" + hour);
			targetCache.evict("getAvailableAnnouncement:@" + announcementType + "@" + 2 + "@" + vendor + "@" + operatorId + "@" + hour);

		} catch(Exception e) {
			LOGGER.error("clear cache Exception", e);
		}

	}

}
