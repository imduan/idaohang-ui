/* RequestData.java
 * 
 * Copyright (c) 2012 Qunar.com. All Rights Reserved. */
package com.chuang.model;

import java.util.UUID;

/**
 * 存放每个Request的私有数据(ThreadLocal)
 * 
 * @author zhimin.duan
 */
public class RequestData {

    private static ThreadLocal<Long> requestTime = new ThreadLocal<Long>();

    private static ThreadLocal<String> requestUuid = new ThreadLocal<String>();
    /**
     * 当前请求的标识名，用于日志
     */
    private static ThreadLocal<String> requestName = new ThreadLocal<String>();

    /**
     * 初始化数据
     */
    public static void initialize() {
        requestTime.set(System.currentTimeMillis());
        requestUuid.set(Long.toHexString(UUID.randomUUID().getMostSignificantBits()));
    }

    /**
     * 获取当前请求的唯一ID
     * 
     * @return
     */
    public static String getRequestUUID() {
        return requestUuid.get();
    }

    /**
     * 获取从请求开始到现在的毫秒数
     * 
     * @return 如果initialize()方法没有被调用，返回-1
     */
    public static long getConsumedTime() {
        Long start = requestTime.get();
        if (start == null) {
            return -1;
        }
        return System.currentTimeMillis() - start.longValue();
    }

    public static void setRequestName(String request) {
        requestName.set(request);
    }

    public static String getRequestName() {
        return requestName.get();
    }
	
}
