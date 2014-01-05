/*
 * JSONSerializable.java Copyright (c) 2012 Qunar.com. All Rights Reserved.
 */
package com.chuang.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * toString转JSON字符串
 *
 * @author zhimin.duan
 */
public class JSONSerializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(JSONSerializable.class);

    @Override
    public String toString() {
        try {
        	return JSON.toJSON(this).toString();
        } catch (Exception e) {
            LOGGER.error("[JSONSerialize] serialize error class={}", this.getClass().getName(), e);
            return new JSONObject().toJSONString();
        }
    }
}
