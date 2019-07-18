package com.tcg.admin.test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

import com.tcg.admin.configuration.ApplicationConfig;
import com.tcg.admin.configuration.WebConfig;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ContextConfiguration(classes = {JndiConfig.class, WebConfig.class, ApplicationConfig.class})
@WebAppConfiguration
public @interface IntegrationTest {
    
}
