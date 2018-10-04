package com.tcg.admin.service.config;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdminConfig {

	private static final Logger LOGGER = LoggerFactory.getLogger(AdminConfig.class);

	private static final String VERSION_FORMAT = "%s.%s";
	private static final String CONFIG_FILE = "uss_config.properties";
	private static final String VERSION = "version";
	private static final String BUILD = "build";
	private static final String KEY = "key";

	private static Configuration conf;

	private AdminConfig() {}
	
	static {
		try {
			conf = new PropertiesConfiguration(CONFIG_FILE);
		} catch (ConfigurationException e) {
			LOGGER.error("AdminConfig error", e);
		}
	}

	public static String getFullVersion() {
		return String.format(VERSION_FORMAT, conf.getString(VERSION), conf.getString(BUILD));
	}

	public static String getKey() {
		return conf.getString(KEY);
	}

}
