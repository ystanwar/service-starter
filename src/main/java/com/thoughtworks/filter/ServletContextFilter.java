package com.thoughtworks.filter;

import com.thoughtworks.prometheus.Prometheus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.ServletContextRequestLoggingFilter;

import javax.servlet.ServletException;

@Configuration
public class ServletContextFilter {
    @Autowired
    private Prometheus prometheus;

    @Bean
    public ServletContextRequestLoggingFilter logFilter() throws ServletException {
        ServletContextRequestLoggingFilter filter
                = new ServletContextRequestLoggingFilter();
        filter.setIncludeQueryString(true);
        filter.setIncludeHeaders(true);
        filter.setIncludePayload(true);
        filter.setIncludeClientInfo(true);
        filter.setMaxPayloadLength(10000);
        filter.setAfterMessagePrefix("PAYMENT ");
        return filter;
    }

}
