package com.tcg.admin.test;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.context.annotation.Configuration;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;

@Configuration
public class JndiConfig {
    
    static {
        try {
            BasicDataSource dataSource =  new BasicDataSource();
            dataSource.setUsername("TCG_ADMIN");
            dataSource.setPassword("Aa123456");
            dataSource.setUrl("jdbc:oracle:thin:@10.8.91.55:1521:TCG");
            dataSource.setDriverClassName("oracle.jdbc.OracleDriver");
            
            SimpleNamingContextBuilder builder = new SimpleNamingContextBuilder();
            builder.bind("persistence/admin", dataSource);
            builder.activate();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
