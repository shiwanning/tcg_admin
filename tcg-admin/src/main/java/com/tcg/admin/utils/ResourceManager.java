package com.tcg.admin.utils;

import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Description : using resource bundle get environment properties
 */
public class ResourceManager {
	
    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceManager.class);

    public static final String ENVIRONMENT = "environment";
	public static final String SUBSYS = "subs_api_url";
    
    private ResourceManager() {}
    
	/**
     * 所有參數設定對應 environemnt.properties
     *
	 * @param key
	 * @return String
	 */
	public static String getEnvironmentResourceString(String key) {
        ResourceBundle resource = ResourceBundle.getBundle(ENVIRONMENT);
        return resource.getString(key);
    }

    public static List<String> getEnvironmentResourceAllKeys() {
        ResourceBundle resource = ResourceBundle.getBundle(ENVIRONMENT);

        return Collections.list(resource.getKeys());
    }
    
    public static String getSubsysResourceString(String key){
    	return getResourceString(SUBSYS, key);
    }
    
    public static String getResourceString(String bundle, String key){
        return ResourceBundle.getBundle(bundle).getString(key);
    }
}
