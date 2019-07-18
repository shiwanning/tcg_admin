package com.tcg.admin.service;

public interface TacCacheService {
	
	public void evictCacheForReadAnnouncement(Integer operatorId, Integer firstAnnouncementId);

}
