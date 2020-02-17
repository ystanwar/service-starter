package com.thoughtworks.filter;

import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.ServletContextRequestLoggingFilter;

import javax.servlet.*;
import java.io.IOException;
import java.util.UUID;

@Configuration
public class ServletContextFilter implements Filter {
    @Bean
    public ServletContextRequestLoggingFilter logFilter() throws ServletException {
        ServletContextRequestLoggingFilter filter
                = new ServletContextRequestLoggingFilter();
        filter.setIncludeQueryString(true);
        filter.setIncludePayload(true);
        filter.setMaxPayloadLength(10000);
        filter.setAfterMessagePrefix("PAYMENT ");
        return filter;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        MDC.put("request.id", String.valueOf(UUID.randomUUID()));
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
