package com.tcg.admin.cache;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.cache.annotation.CacheEvict;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@CacheEvict(cacheNames="tac-redis")
public @interface RedisCacheEvict {

	String key() default "";
	
	boolean allEntries() default false;
	
}
