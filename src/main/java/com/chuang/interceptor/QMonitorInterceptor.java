package com.chuang.interceptor;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;

import com.chuang.model.RequestData;
import com.chuang.util.MonitorUtils;


public class QMonitorInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        
        String path = request.getServletPath();

        Map pathVariables = (Map) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        if(pathVariables == null){
        	RequestData.setRequestName("");
            return true;
        }
        
        for (Object pathVariable : pathVariables.values()) {
            path = StringUtils.remove(path, "/" + pathVariable);
        }

        path = path.replace("/", "_").substring(1);
        RequestData.setRequestName(path);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        if (!StringUtils.isEmpty(RequestData.getRequestName())) {
        	MonitorUtils.monitor(RequestData.getRequestName(), false, RequestData.getConsumedTime());
        }
    }
}
