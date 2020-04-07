package com.thoughtworks.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
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

import static net.logstash.logback.argument.StructuredArguments.kv;

@Slf4j
@Service
@Order(2)
public class CustomRequestLoggingFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper((HttpServletRequest) servletRequest);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper((HttpServletResponse) servletResponse);

        HttpServletRequest httpSR = (HttpServletRequest) servletRequest;
        String xCorrelationId = httpSR.getHeader("X-Correlation-ID");
        String xRequestId = httpSR.getHeader("X-Request-ID");
        if (xRequestId==null || xRequestId.isEmpty()){
            xRequestId = String.valueOf(UUID.randomUUID());
        }
        if (xCorrelationId==null || xCorrelationId.isEmpty()){
            xCorrelationId = xRequestId;
        }

        MDC.put("X-Correlation-ID", xCorrelationId);
        MDC.put("X-Request-ID", xRequestId);

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

        log.info(httpServletRequest.getMethod() + " " + httpServletRequest.getServletPath(), kv("eventCode", "REQUEST_RECEIVED"), kv("headers", objectMapper.writeValueAsString(getHeaders(httpServletRequest))), kv("params", objectMapper.writeValueAsString(httpServletRequest.getParameterMap())), kv("body", requestBody));
       
        log.info(httpServletRequest.getMethod() + " " + httpServletRequest.getServletPath(), kv("eventCode", "RESPONSE_SENT"), kv("statusCode", httpServletResponse.getStatus()), kv("headers", objectMapper.writeValueAsString(getHeaders(httpServletRequest))), kv("body", getResponseContent(responseBody)));
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
