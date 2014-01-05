/**
 * 
 */
package com.chuang.util;

import java.io.IOException;
import java.util.Properties;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhimin.duan
 * 
 */
public class ConfigHelper {

    private final static Logger logger = LoggerFactory.getLogger(ConfigHelper.class);

    private static transient boolean initialized;

    private static final Object lock = new Object();

    private static Properties properties = new Properties();

    public static String getConfig(String key) {
        return getConfig(key, null);
    }

    public static String getConfig(String key, String defaultValue) {
        ensureInitialize();
        String value = (String) properties.get(key);
        if (value == null) {
            value = defaultValue;
        }
        return value;
    }

    protected static void ensureInitialize() {
        if (!initialized) {
            synchronized (lock) {
                if (!initialized) {
                    try {
                        initialize();
                        initialized = true;
                    } catch (Exception e) {
                        logger.error("initialize config error", e);
                    }
                }
            }
        }

    }

    private final static String DEFAULT_CONFIG_PATH = "config.properties";
    public static Configuration conf = null;

    protected static void initialize() throws IOException {
        String path = System.getProperty("config_path");
        if (path == null) {
            path = DEFAULT_CONFIG_PATH;
        }
        try {
            properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(path));

        } catch (IOException e) {
            logger.error("load config from '" + path + "' error", e);
            throw e;
        }

        conf = loadConfiguration(DEFAULT_CONFIG_PATH);
    }

    private static Configuration loadConfiguration(String file) throws IOException {
        try {
            return new PropertiesConfiguration(file);
        } catch (ConfigurationException e) {
            logger.error("load config from '" + file + "' error", e);
            e.printStackTrace();
        }
        return null;
    }
}
