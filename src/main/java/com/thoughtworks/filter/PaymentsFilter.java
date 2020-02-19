package com.thoughtworks.filter;

import com.thoughtworks.prometheus.Prometheus;
import io.micrometer.core.instrument.Counter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

@Service
public class PaymentsFilter implements Filter {

    @Autowired
    private Prometheus prometheus;

    private static Logger logger = LogManager.getLogger(PaymentsFilter.class);

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        MDC.put("request.id", String.valueOf(UUID.randomUUID()));
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper((HttpServletRequest) servletRequest);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper((HttpServletResponse) servletResponse);

        Counter paymentsCounter;
        paymentsCounter = prometheus.getPaymentsCounter();
        paymentsCounter.increment();
        try {
            filterChain.doFilter(requestWrapper, responseWrapper);
        } finally {
            String requestBody = new String(requestWrapper.getContentAsByteArray());
            String responseBody = new String(responseWrapper.getContentAsByteArray());
            logger.info("PAYMENT REQUEST ->{}", requestBody);
            logger.info("PAYMENT RESPONSE ->{}", responseBody);

            responseWrapper.copyBodyToResponse();
        }

    }
}
