package com.chuang.interceptor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.util.WebUtils;

public class AccessLogFilter implements Filter {

    private static final Logger accessLogger = LoggerFactory.getLogger("dx2_localman_access");
    private static final Logger STATISTICS = LoggerFactory.getLogger("STATISTICS");

    private static final String logFormat = "url:{}, status:{}, return:{}";

    protected boolean shouldFilter(HttpServletRequest request) throws ServletException {
        // return !accessLogger.isInfoEnabled();
        return true;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException,
            ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        int nStatCode = 404;
        if (shouldFilter(httpRequest)) {
            AccessLogResponseWrapper responseWrapper = new AccessLogResponseWrapper(httpResponse);
            filterChain.doFilter(request, responseWrapper);
            nStatCode = responseWrapper.getStatusCode();
            byte[] body = responseWrapper.toByteArray();
            String cType = httpResponse.getContentType();
            String strResp = "";
            if (cType != null && cType.contains("application/json")) {
                strResp = new String(body, "UTF-8");
            }
            try {
                String logResp = strResp;
                accessLogger.info(logFormat, getRequestUrl(httpRequest), responseWrapper.getStatusCode(), logResp);
            } catch (Exception e) {
                accessLogger.info(logFormat, getRequestUrl(httpRequest), responseWrapper.getStatusCode(),
                        "can not convert result to print.");
            }
            httpResponse.setStatus(responseWrapper.getStatusCode());
            copyBodyToResponse(body, httpResponse);
        } else {
            // Proceed without invoking this filter...
            filterChain.doFilter(request, response);
        }

        // 记录统计信息,需要过滤其他的访问
        if (httpResponse != null && nStatCode == 200) {
            String path = httpRequest.getServletPath();
            path = path.replace("/", "_").substring(1);
            if (path.contains("qmonitor.jsp") || path.contains("healthcheck.html")) {
            } else {
//                STATISTICS
//                        .info("access_url={}\tlogType=access\tQunarGlobal={}\tip={}",
//                                path, QProjectUtil.getQunarGlobal(httpRequest, httpResponse),
//                                QUtil.getUserIPString(httpRequest));
            }
        }
    }

    private String getRequestUrl(HttpServletRequest httpRequest) {
        if (StringUtils.isEmpty(httpRequest.getQueryString())) {
            return httpRequest.getRequestURI();
        } else
            return httpRequest.getRequestURI() + "?" + httpRequest.getQueryString();
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }

    private void copyBodyToResponse(byte[] body, HttpServletResponse response) throws IOException {
        if (body.length > 0) {
            response.setContentLength(body.length);

            FileCopyUtils.copy(body, response.getOutputStream());
        }
    }

    /**
     * {@link HttpServletRequest} wrapper that buffers all content written to the {@linkplain #getOutputStream() output
     * stream} and {@linkplain #getWriter() writer}, and allows this content to be retrieved via a
     * {@link #toByteArray() byte array}.
     */
    private static class AccessLogResponseWrapper extends HttpServletResponseWrapper {

        private final ByteArrayOutputStream content = new ByteArrayOutputStream();

        private final ServletOutputStream outputStream = new ResponseServletOutputStream();

        private PrintWriter writer;

        private int statusCode = HttpServletResponse.SC_OK;

        private AccessLogResponseWrapper(HttpServletResponse response) {
            super(response);
        }

        @Override
        public void setStatus(int sc) {
            super.setStatus(sc);
            this.statusCode = sc;
        }

        @Override
        public void setStatus(int sc, String sm) {
            super.setStatus(sc, sm);
            this.statusCode = sc;
        }

        @Override
        public void sendError(int sc) throws IOException {
            super.sendError(sc);
            this.statusCode = sc;
        }

        @Override
        public void sendError(int sc, String msg) throws IOException {
            super.sendError(sc, msg);
            this.statusCode = sc;
        }

        @Override
        public void setContentLength(int len) {
        }

        @Override
        public ServletOutputStream getOutputStream() {
            return this.outputStream;
        }

        @Override
        public PrintWriter getWriter() throws IOException {
            if (this.writer == null) {
                String characterEncoding = getCharacterEncoding();
                this.writer = (characterEncoding != null ? new ResponsePrintWriter(characterEncoding)
                        : new ResponsePrintWriter(WebUtils.DEFAULT_CHARACTER_ENCODING));
            }
            return this.writer;
        }

        @Override
        public void resetBuffer() {
            this.content.reset();
        }

        @Override
        public void reset() {
            super.reset();
            resetBuffer();
        }

        private int getStatusCode() {
            return statusCode;
        }

        private byte[] toByteArray() {
            return this.content.toByteArray();
        }

        private class ResponseServletOutputStream extends ServletOutputStream {

            @Override
            public void write(int b) throws IOException {
                content.write(b);
            }

            @Override
            public void write(byte[] b, int off, int len) throws IOException {
                content.write(b, off, len);
            }

        }

        private class ResponsePrintWriter extends PrintWriter {

            private ResponsePrintWriter(String characterEncoding) throws UnsupportedEncodingException {
                super(new OutputStreamWriter(content, characterEncoding));
            }

            @Override
            public void write(char buf[], int off, int len) {
                super.write(buf, off, len);
                super.flush();
            }

            @Override
            public void write(String s, int off, int len) {
                super.write(s, off, len);
                super.flush();
            }

            @Override
            public void write(int c) {
                super.write(c);
                super.flush();
            }
        }
    }

}
