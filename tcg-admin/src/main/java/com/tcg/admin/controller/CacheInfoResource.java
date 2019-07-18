package com.tcg.admin.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.tcg.admin.to.response.JsonResponseT;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;

@RestController
@RequestMapping(value = "/resources/cacheInfo", produces = MediaType.APPLICATION_JSON_VALUE)
public class CacheInfoResource {
	
	@GetMapping
    public JsonResponseT<Map> cacheInfo(
    		@RequestParam(value = "cacheManagerName", required = false)String cacheManagerName
    		) {
            JsonResponseT<Map> response = new JsonResponseT<>(true);
            Map<String, List> r = Maps.newHashMap();
            CacheManager cacheManager = CacheManager.getCacheManager(cacheManagerName);
            for(String cacheName : cacheManager.getCacheNames()) {
            	Cache cache = cacheManager.getCache(cacheName);
            	List keys = Lists.newLinkedList();
            	for(Object key : cache.getKeys()) {
            		keys.add(key.toString());
            	}
            	r.put(cacheName, keys);
            }
            response.setValue(r);
            return response;
    }
	
}
