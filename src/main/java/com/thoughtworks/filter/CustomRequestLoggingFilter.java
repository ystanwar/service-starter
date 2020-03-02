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

            Map<String, String[]> requestParams = servletRequest.getParameterMap();
            ObjectMapper requestParamMapper = new ObjectMapper();
            String paramString = requestParamMapper.writerWithDefaultPrettyPrinter().writeValueAsString(requestParams);

            HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
            HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;


            logger.info("{\"eventCode\":\"{}\",\"description\":\"{}\",\"details\":{\"params\":{}, \"body\":{}},\"exception\":\"{}\"}", v("eventCode", "REQUEST_RECEIVED"), v("description", httpServletRequest.getMethod() + " " + httpServletRequest.getServletPath()), v("params", paramString), v("body", requestBody), v("exception", ""));

            logger.info("{\"eventCode\":\"{}\",\"description\":\"{}\",\"details\":{\"statusCode\":\"{}\",\"body\":{}},\"exception\":\"{}\"}", v("eventCode", "RESPONSE_SENT"), v("description", httpServletRequest.getMethod() + " " + httpServletRequest.getServletPath()), v("statusCode", httpServletResponse.getStatus()), v("body", responseBody), v("exception", ""));

            MDC.clear();
            responseWrapper.copyBodyToResponse();
        }
    }
}
