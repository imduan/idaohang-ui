package com.chuang.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Config {

    public static final char LOG_SPLIT = '\t';

    private static final Logger logger = LoggerFactory.getLogger(Config.class);

    public static String get(String strKey) {
        return ConfigHelper.getConfig(strKey);
    }

    public static int get(String strKey, int nDefault) {
        int nRet = nDefault;
        String strVal = ConfigHelper.getConfig(strKey);
        try {
            nRet = Integer.valueOf(strVal);
        } catch (Exception e) {
            logger.info("get");
        }
        return nRet;
    }

    public static boolean isUserDebug() {
        int s = get("debug", 0);
        return (s == 1);
    }
}
