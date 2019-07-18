package com.tcg.admin.test.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

import com.tcg.admin.service.TacCacheService;
import com.tcg.admin.test.IntegrationTest;

//@RunWith(SpringRunner.class)
//@IntegrationTest
public class TacCacheServiceTest {

	@Autowired
	private TacCacheService tacCacheService;
	
	//@Test
	public void test() {
		tacCacheService.evictCacheForReadAnnouncement(14010, 5420);
	}
	
}
