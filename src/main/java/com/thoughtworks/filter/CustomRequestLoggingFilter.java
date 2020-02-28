package com.thoughtworks.filter;

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

            HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;

            logger.info("{\"eventCode\":\"{}\",\"description\":\"{}\",\"details\":{\"params\":\"{}\", \"body\":{}},\"exception\":\"{}\"}", v("eventCode", "REQUEST_RECEIVED"), v("description", httpServletRequest.getMethod() + " " + httpServletRequest.getServletPath()), v("params", "parameters here"), v("body", requestBody), v("exception", ""));

            logger.info("{\"eventCode\":\"{}\",\"description\":\"{}\",\"details\":{\"params\":\"{}\", \"body\":{}},\"exception\":\"{}\"}", v("eventCode", "RESPONSE_SENT"), v("description", httpServletRequest.getMethod() + " " + httpServletRequest.getServletPath()), v("params", ""), v("body", responseBody), v("exception", ""));

            MDC.clear();
            responseWrapper.copyBodyToResponse();
        }
    }
}
