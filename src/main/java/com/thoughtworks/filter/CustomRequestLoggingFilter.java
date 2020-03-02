package com.thoughtworks.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
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

@Service
@Order(2)
public class CustomRequestLoggingFilter implements Filter {
    private static Logger logger = LogManager.getLogger(CustomRequestLoggingFilter.class);

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper((HttpServletRequest) servletRequest);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper((HttpServletResponse) servletResponse);

        MDC.put("request.id", String.valueOf(UUID.randomUUID()));
        MDC.put("parentrequest.id", requestWrapper.getHeader("PARENT_REQ_ID"));
        try {
            filterChain.doFilter(requestWrapper, responseWrapper);
        } finally {
            String requestBody = new String(requestWrapper.getContentAsByteArray());
            String responseBody = new String(responseWrapper.getContentAsByteArray());

            if (responseBody.isEmpty()) {
                responseBody = "\"\"";
            } else if (responseBody.length() > 1000) responseBody = "\"payload is too large to log\"";

            if (requestBody.isEmpty()) {
                requestBody = "\"\"";
            } else if (requestBody.length() > 1000) requestBody = "\"payload is too large to log\"";


            HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
            HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;

            Map<String, String> headerMap = new HashMap<>();

            Enumeration en = (httpServletRequest).getHeaderNames();
            while (en.hasMoreElements()) {
                String headerName = (String) en.nextElement();
                headerMap.put(headerName, httpServletRequest.getHeader(headerName));
            }

            Map<String, String[]> requestParams = servletRequest.getParameterMap();
            ObjectMapper objectMapper = new ObjectMapper();
            String paramString = objectMapper.writeValueAsString(requestParams);

            String headerString = objectMapper.writeValueAsString(headerMap);


            //logEventAsJson(logger, "REQUEST_RECEIVED", httpServletRequest.getMethod() + " " + httpServletRequest.getServletPath(),detailsMap);
            logger.info("{\"eventCode\":\"{}\",\"description\":\"{}\",\"details\":{\"headers\":{},\"params\":{}, \"body\":{}},\"exception\":\"{}\"}", v("eventCode", "REQUEST_RECEIVED"), v("description", httpServletRequest.getMethod() + " " + httpServletRequest.getServletPath()), v("headers", headerString), v("params", paramString), v("body", requestBody), v("exception", ""));

            logger.info("{\"eventCode\":\"{}\",\"description\":\"{}\",\"details\":{\"statusCode\":\"{}\",\"headers\":{},\"body\":{}},\"exception\":\"{}\"}", v("eventCode", "RESPONSE_SENT"), v("description", httpServletRequest.getMethod() + " " + httpServletRequest.getServletPath()), v("statusCode", httpServletResponse.getStatus()), v("headers", headerString), v("body", responseBody), v("exception", ""));

            MDC.clear();
            responseWrapper.copyBodyToResponse();
        }
    }
}
