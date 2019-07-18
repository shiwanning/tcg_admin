package com.tcg.admin.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;

public class EhcacheUtil {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(EhcacheUtil.class);
    
    // keep hibernate cache
    private static final List<String> DEFAULT_EXCEPT = Collections.singletonList("UpdateTimestampsCache");

    private EhcacheUtil() {
    	throw new IllegalStateException("Utility class");
    }
    
    /**
     * no except cache name
     */
    public static void refreshCaches(String cacheManagerName) {
        cacheRemove(cacheManagerName, Collections.<String>emptyList());
    }

    /**
     * include except cache name
     */
    public static void refreshCaches(String cacheManagerName, List<String> except) {
        cacheRemove(cacheManagerName, except);
    }

    private static void cacheRemove(String cacheManagerName, List<String> except) {
        List<String> exceptAll = new ArrayList<>(DEFAULT_EXCEPT);
        exceptAll.addAll(except);

        final CacheManager cacheManager = CacheManager.getCacheManager(cacheManagerName);
        if (cacheManager != null && ArrayUtils.isNotEmpty(cacheManager.getCacheNames())) {
        	cacheRemoveProcess(cacheManager, exceptAll);
        }
    }

    private static void cacheRemoveProcess(CacheManager cacheManager, List<String> exceptAll) {
    	try {
            for (String name : cacheManager.getCacheNames()) {
                if (containNone(name, exceptAll)) {
                    cacheRemove(cacheManager, name);
                }
            }
            LOGGER.info("Refresh cache manager: {}, except: {}", cacheManager.getName(), StringUtils.join(exceptAll, ","));
        } catch (Exception e) {
            LOGGER.warn("Refresh cache manager: {} failed: {}", cacheManager.getName(), ExceptionUtils.getRootCause(e));
        }
	}

	private static boolean containNone(String str, List<String> except) {
        for (String exceptStr : except) {
            if (str.contains(exceptStr)) {
                return false;
            }
        }
        return true;
    }

    private static void cacheRemove(CacheManager cacheManager, String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            try {
                cache.removeAll();
            } catch (Exception e) {
                LOGGER.warn("Remove cache: {} failed: {}", cacheName, ExceptionUtils.getRootCause(e));
            }
        }
    }
}
