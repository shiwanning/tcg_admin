package com.tcg.admin.configuration;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
import com.google.common.collect.Lists;

@Configuration
@EnableCaching
public class CacheConfig {

	static {
		System.setProperty("net.sf.ehcache.skipUpdateCheck", "true");
	}

	@Value("#{'${redis.sentinels.hosts}'.split(',')}")
	private List<String> sentinelHosts;

	@Value("${redis.sentinels.master}")
	private String masterName;

	@Value("#{'${redis.sentinels.ports}'.split(',')}")
	private List<Integer> ports;

	@Bean
	public RedisConnectionFactory jedisConnectionFactory() {

		RedisSentinelConfiguration sentinelConfig = new RedisSentinelConfiguration().master(masterName);

		for (int index = 0 ; index < sentinelHosts.size() ; index++) {
			sentinelConfig.sentinel(sentinelHosts.get(index), ports.get(index));
		}

		return new JedisConnectionFactory(sentinelConfig);
	}

    @Bean
    public RedisTemplate<String, Object> redisTemplate(
            RedisConnectionFactory factory, ObjectMapper mapper) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(factory);
        
        ObjectMapper redisMapper = mapper.copy().enableDefaultTyping(DefaultTyping.NON_FINAL, As.PROPERTY);

        GenericJackson2JsonRedisSerializer gs = new GenericJackson2JsonRedisSerializer(redisMapper);
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        redisTemplate.setValueSerializer(gs);
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setHashKeySerializer(stringRedisSerializer);
        
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
    
    @Bean
    public StringRedisTemplate shiroRedisTemplate(
            RedisConnectionFactory factory) {
        return new StringRedisTemplate(factory);
    }
	
	@Bean
	public CacheManager redisCacheManager(RedisTemplate<String, Object> redisTemplate) {
		RedisCacheManager cacheManager = new RedisCacheManager(redisTemplate);
	    cacheManager.setDefaultExpiration(30000);
	    cacheManager.afterPropertiesSet();
	    cacheManager.setCacheNames(Lists.newArrayList("tac-redis", "tac-auth-redis", "tac-announcement", 
	    		"tac-merchant", "tac-task-create", "task-operator-viewers"));
		return cacheManager;
	}
	
	@Bean(name = "cacheManager")
	@Primary
	public SimpleCacheManager cacheManager(CacheManager redisCacheManager, CacheManager ehCacheManager) {
		SimpleCacheManager scm = new SimpleCacheManager();
		List<Cache> caches = Lists.newLinkedList();
		caches.add(redisCacheManager.getCache("tac-redis"));
		for(String ehCacheName : ehCacheManager.getCacheNames()) {
			caches.add(ehCacheManager.getCache(ehCacheName));
		}
		scm.setCaches(caches);
		return scm;
	}

}
