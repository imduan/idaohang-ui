package com.chuang.util;

import com.qunar.flight.qmonitor.QMonitor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: doubhor
 * Date: 13-11-14
 * Time: 下午3:42
 * To change this template use File | Settings | File Templates.
 */
public class MonitorUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(MonitorUtils.class);
    private static final String MONITOR_NAME_ERROR = "_error";
    private static final String MONITOR_NAME_SLOW = "_slow";
    private static final String MONITOR_NAME_TIMEOUT = "_timeout";

    private static final String CFG_FILE_NAME = "monitorUtils.properties";
    private static final String CFGNAME_REPORT_OLDERROR = "report.olderror";
    private static final String CFGNAME_SLOW_TIME = "slow.time";
    private static final String CFGNAME_TIMEOUT_TIME = "timeout.time";
    private static final int CFG_REPORT_OLDERROR;
    private static long CFG_SLOW_TIME = 100;
    private static long CFG_TIMEOUT_TIME = 1000;

    static {
        int reportOldError = 1;
        try {
            ClassPathResource classPathResource = new ClassPathResource(CFG_FILE_NAME);
            Properties props = new Properties();
            PropertiesLoaderUtils.fillProperties(props, classPathResource);

            String cfgStr = props.getProperty(CFGNAME_REPORT_OLDERROR);
            Integer intVal = parseInteger(cfgStr);
            if (intVal != null) {
                reportOldError = intVal != 0 ? 1 : 0;
            }
            cfgStr = props.getProperty(CFGNAME_SLOW_TIME);
            Long cfgVal = parseLong(cfgStr);
            if (cfgVal != null && cfgVal > 0L) {
                CFG_SLOW_TIME = cfgVal;
            }
            cfgStr = props.getProperty(CFGNAME_TIMEOUT_TIME);
            cfgVal = parseLong(cfgStr);
            if (cfgVal != null && cfgVal > 0L) {
                CFG_TIMEOUT_TIME = cfgVal;
            }
        } catch (FileNotFoundException e) {
            LOGGER.debug("act=loadMonitorUtilsConfig desc=fileNotFound");
        } catch (IOException e) {
            LOGGER.debug("act=loadMonitorUtilsConfig err=io expt={}", e.getMessage());
        } catch (Exception e) {
            LOGGER.error("err=loadMonitorUtilsConfig", e);
        }

        CFG_REPORT_OLDERROR = reportOldError;
        LOGGER.info("act=showMonitorUtilsConfig reportOldError={} slowTime={} timeoutTime={}",
                CFG_REPORT_OLDERROR, CFG_SLOW_TIME, CFG_TIMEOUT_TIME);
    }

    public static void monitor(String reqName, boolean failed, long procTime) {
        monitor("request", reqName, failed, procTime);
    }
    public static void monitor(String monitorPrefixName, String reqName, boolean failed, long procTime) {
        if (StringUtils.isEmpty(reqName) ) {
            return;
        }

        if (failed) {
            // compatible mode
            if (CFG_REPORT_OLDERROR != 0) {
                QMonitor.recordOne(reqName + "_error");
            }
            // compatible mode end
            LOGGER.error("err=monitorGotError reqName={} reqTime={} monitorName={}",
                    reqName, procTime, monitorPrefixName + MONITOR_NAME_ERROR);
            QMonitor.recordOne(monitorPrefixName + MONITOR_NAME_ERROR, procTime);
        }
        if (procTime > CFG_SLOW_TIME) {
            LOGGER.warn("err=monitorGotSlow reqName={} reqTime={} monitorName={}",
                    reqName, procTime, monitorPrefixName + MONITOR_NAME_SLOW);
            QMonitor.recordOne(monitorPrefixName + MONITOR_NAME_SLOW, procTime);
        }
        if (procTime > CFG_TIMEOUT_TIME) {
            LOGGER.error("err=monitorGotTimeout reqName={} reqTime={} monitorName={}",
                    reqName, procTime, monitorPrefixName + MONITOR_NAME_TIMEOUT);
            QMonitor.recordOne(monitorPrefixName + MONITOR_NAME_TIMEOUT, procTime);
        }

        LOGGER.info("act=monitorRequest reqName={} reqTime={} monitorPrefix={}",
                reqName, procTime, monitorPrefixName);
        QMonitor.recordOne(reqName, procTime);
    }
    public static void recordOne(String name, long procTime) {
        if (isErrorMonitor(name)) {
            LOGGER.error("err=monitorRequest reqName={} reqTime={}", name, procTime);
        }
        else {
            LOGGER.info("act=monitorRequest reqName={} reqTime={}", name, procTime);
        }
        QMonitor.recordOne(name, procTime);
    }
    private static boolean isErrorMonitor(String monitorName) {
        return StringUtils.containsIgnoreCase(monitorName, "error") ||
                StringUtils.containsIgnoreCase(monitorName, "slow") ||
                StringUtils.containsIgnoreCase(monitorName, "timeout");
    }
    private static Long parseLong(String timeStr) {
        if (StringUtils.isBlank(timeStr)) {
            return null;
        }
        Long timeVal = null;
        try {
            timeVal = Long.valueOf(timeStr);
        }
        catch (Exception e) {
            timeVal = null;
        }
        return timeVal;
    }
    private static Integer parseInteger(String cfgStr) {
        if (StringUtils.isBlank(cfgStr)) {
            return null;
        }
        Integer cfgVal = null;
        try {
            cfgVal = Integer.valueOf(cfgStr);
        }
        catch (Exception e) {
            cfgVal = null;
        }
        return cfgVal;
    }

//    public static void recordOne(String monitorName, String reqName, long procTime) {
//        if (isErrorMonitor(monitorName)) {
//            LOGGER.error("err=monitorRequest reqName={} reqTime={} monitorName={}",
//                    reqName, procTime, monitorName);
//        }
//        else {
//            LOGGER.info("act=monitorRequest reqName={} reqTime={} monitorName={}",
//                    reqName, procTime, monitorName);
//        }
//        QMonitor.recordOne(monitorName, procTime);
//    }
}
