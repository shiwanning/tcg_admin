package com.tcg.admin.configuration;

import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;


/**
 * Description: ApplicationConfig Description
 */
@Configuration
@ComponentScan(basePackages = {"com.tcg.admin"})
@Import({SwaggerConfig.class, CommonConfig.class, DatabaseConfig.class, CacheConfig.class})
@PropertySource({"classpath:environment.properties", "classpath:redis_config.properties"})
@EnableScheduling
@EnableAspectJAutoProxy
public class ApplicationConfig {

    @Bean
    public PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
    
    @Bean
    public CommonsMultipartResolver multipartResolver() {
    	CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
    	multipartResolver.setMaxUploadSize(50000000);
    	return multipartResolver;
    }
}