package com.chuang.exception;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;

public class QExceptionResolver extends AbstractHandlerExceptionResolver {

    private static final Logger LOGGER = LoggerFactory.getLogger(QExceptionResolver.class);

    private HttpMessageConverter<?>[] messageConverters;

    public void setMessageConverters(HttpMessageConverter<?>[] messageConverters) {
        this.messageConverters = messageConverters;
    }

    @Override
    protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
            Exception ex) {
        boolean htmlView = true;
        if ((handler instanceof HandlerMethod)) {
            htmlView = this.isHtmlView((HandlerMethod) handler);
        }

        if (htmlView) {
            if (ex instanceof QException) {
            	QException ttsEx = (QException) ex;
                // 未登录跳转至登录页
                if (ttsEx.getErrorCode() == ErrorCode.ERR_USER_NOT_LOGIN) {
                    return new ModelAndView("redirect", new ModelMap().addAttribute("type", "mis"));
                }
                
                // 其他跳转首页
                LOGGER.error("[ExceptionResolver] QDx2Exception={}, ex={}", ttsEx.getMessage(), ex);
                return new ModelAndView("redirect", new ModelMap().addAttribute("type", "mis_index"));
            }
            LOGGER.error("[ExceptionResolver] unexpected error!", ex);
            throw new RuntimeException("Unexpected error: " + ex.getMessage(), ex);
        } else {
            ServletWebRequest webRequest = new ServletWebRequest(request, response);
            if (ex instanceof QException) {
                try {
                    LOGGER.error("[ExceptionResolver] ttsexception code={}", ((QException) ex).getErrorCode()
                            .getCode());
                    return handleResponseBody(((QException) ex).getErrorCode(), webRequest);
                } catch (Exception ioe) {
                    LOGGER.error("Handling of [" + ioe.getClass().getName() + "] resulted in Exception", ioe);
                    return null;
                }
            }
            LOGGER.error("[ExceptionResolver] unknown err ", ex);
            try {
                webRequest.getResponse().setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return handleResponseBody(ErrorCode.ERR_UNKNOWN_EXCEPTION, webRequest);
            } catch (Exception ioe) {
                LOGGER.error("Handling of [" + ioe.getClass().getName() + "] resulted in Exception", ioe);
                return null;
            }
        }
    }

    /**
     * 获取handlerMethod对应的ResponseType
     * 
     * @param handlerMethod
     * @return
     */
    private boolean isHtmlView(HandlerMethod handlerMethod) {
        Method method = handlerMethod.getMethod();
        return !method.isAnnotationPresent(ResponseBody.class);
    }

    /**
     * Copied from {@link org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerExceptionResolver}
     */
    @SuppressWarnings("unchecked")
    private ModelAndView handleResponseBody(Object returnValue, ServletWebRequest webRequest) throws ServletException,
            IOException {

        HttpInputMessage inputMessage = new ServletServerHttpRequest(webRequest.getRequest());
        List<MediaType> acceptedMediaTypes = inputMessage.getHeaders().getAccept();
        if (acceptedMediaTypes.isEmpty()) {
            acceptedMediaTypes = Collections.singletonList(MediaType.ALL);
        }
        MediaType.sortByQualityValue(acceptedMediaTypes);
        HttpOutputMessage outputMessage = new ServletServerHttpResponse(webRequest.getResponse());
        Class<?> returnValueType = returnValue.getClass();
        if (this.messageConverters != null) {
            for (MediaType acceptedMediaType : acceptedMediaTypes) {
                for (HttpMessageConverter messageConverter : this.messageConverters) {
                    if (messageConverter.canWrite(returnValueType, acceptedMediaType)) {
                        messageConverter.write(returnValue, acceptedMediaType, outputMessage);
                        return new ModelAndView();
                    }
                }
            }
        }
        if (LOGGER.isWarnEnabled()) {
            LOGGER.warn("Could not find HttpMessageConverter that supports return type [" + returnValueType + "] and "
                    + acceptedMediaTypes);
        }
        return null;
    }
}
