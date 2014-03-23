/* JSONSerializable.java Copyright (c) 2012 Qunar.com. All Rights Reserved. */
package com.chuang.model;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * toString杞琂SON
 * 
 * @author zhimin.duan
 */
public class JSONSerializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(JSONSerializable.class);

    // java对象转换成json字符串
    @Override
    public String toString() {
        ObjectMapper om = new ObjectMapper();
        Writer w = new StringWriter();
        String json = null;
        try {
            om.writeValue(w, this);
            json = w.toString();
            w.close();
        } catch (IOException e) {
            // 错误处理
        }
        return json;
    }
}
