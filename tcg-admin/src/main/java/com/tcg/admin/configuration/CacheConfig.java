package com.tcg.admin.configuration;


import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;

@Configuration
@EnableCaching
public class CacheConfig {
    
	static {
		System.setProperty("net.sf.ehcache.skipUpdateCheck", "true");
	}
	
    @Bean
    public EhCacheManagerFactoryBean ehCacheCacheManager() {
        EhCacheManagerFactoryBean cmfb = new EhCacheManagerFactoryBean();
        cmfb.setCacheManagerName("cache-ap-admin");
        ClassPathResource res = new ClassPathResource("ehcache.xml");
        cmfb.setConfigLocation(res);
        cmfb.setShared(false);
        return cmfb;
    }
    
    @Bean(name = "cacheManager")
    @Primary
    public CacheManager cacheManager() {
        net.sf.ehcache.CacheManager cm = ehCacheCacheManager().getObject();
        return new EhCacheCacheManager(cm);
    }
    
    @Bean(name = "shiroCacheManager")
    public EhCacheManager shiroCacheManager() {
        EhCacheManager ec = new EhCacheManager();
        ec.setCacheManagerConfigFile("classpath:ehcache-shiro.xml");
        return ec;
    }
    
}
