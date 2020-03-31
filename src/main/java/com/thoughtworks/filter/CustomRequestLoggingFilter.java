package com.thoughtworks.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static net.logstash.logback.argument.StructuredArguments.v;

@Slf4j
@Service
@Order(2)
public class CustomRequestLoggingFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper((HttpServletRequest) servletRequest);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper((HttpServletResponse) servletResponse);

        MDC.put("request.id", String.valueOf(UUID.randomUUID()));
        MDC.put("parentrequest.id", ((HttpServletRequest) servletRequest).getHeader("PARENT_REQ_ID"));
        try {
            filterChain.doFilter(requestWrapper, responseWrapper);
        } finally {
            if (log.isInfoEnabled()) {
                logRequestAndResponse((HttpServletRequest) servletRequest, (HttpServletResponse) servletResponse);
            }
            MDC.clear();
            responseWrapper.copyBodyToResponse();
        }
    }

    private void logRequestAndResponse(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws JsonProcessingException {
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(httpServletRequest);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(httpServletResponse);
        String requestBody = new String(requestWrapper.getContentAsByteArray());
        String responseBody = new String(responseWrapper.getContentAsByteArray());
        ObjectMapper objectMapper = new ObjectMapper();

        log.info("{\"eventCode\":\"{}\",\"description\":\"{}\",\"details\":{\"headers\":{},\"params\":{},\"body\":\"{}\"}}", v("eventCode", "REQUEST_RECEIVED"), v("description", httpServletRequest.getMethod() + " " + httpServletRequest.getServletPath()), v("headers", objectMapper.writeValueAsString(getHeaders(httpServletRequest))), v("params", objectMapper.writeValueAsString(httpServletRequest.getParameterMap())), v("body", requestBody));

        log.info("{\"eventCode\":\"{}\",\"description\":\"{}\",\"details\":{\"statusCode\":{},\"headers\":{},\"body\":\"{}\"}}", v("eventCode", "RESPONSE_SENT"), v("description", httpServletRequest.getMethod() + " " + httpServletRequest.getServletPath()), v("statusCode", httpServletResponse.getStatus()), v("headers", objectMapper.writeValueAsString(getHeaders(httpServletRequest))), v("body", getResponseContent(responseBody)));
    }

    private Map<String, String> getHeaders(HttpServletRequest httpServletRequest) {
        Map<String, String> headerMap = new HashMap<>();
        Enumeration en = httpServletRequest.getHeaderNames();
        while (en.hasMoreElements()) {
            String headerName = (String) en.nextElement();
            headerMap.put(headerName, httpServletRequest.getHeader(headerName));
        }
        return headerMap;
    }

    private String getResponseContent(String responseBody) {
        if (responseBody.length() > 1000) responseBody = "\"payload is too large to log\"";
        return responseBody;
    }

    private String getRequestContent(String requestBody) {
        if (requestBody.length() > 1000) requestBody = "\"payload is too large to log\"";
        return requestBody;
    }
}
