package com.chuang.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.HtmlUtils;
import org.springframework.web.util.JavaScriptUtils;

import com.alibaba.fastjson.JSON;

/**
 * @author zhiqi.zhou
 */
public class EscapeTool {
    private static final Logger logger = LoggerFactory.getLogger(EscapeTool.class);

    public static String html(String data) {
        if (data == null) {
            return null;
        }
        return HtmlUtils.htmlEscape(data);
    }

    public static String javascript(String data) {
        if (data == null) {
            return null;
        }
        return JavaScriptUtils.javaScriptEscape(data);
    }

    public static String url(String data) {
        if (data == null) {
            return null;
        }
        try {
            return URLEncoder.encode(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    public static String toJson(Object data) {
        if (data == null) {
            return null;
        }
        try {
            return JSON.toJSONString(data);
        } catch (Exception e) {
            return null;
        }
    }

}
