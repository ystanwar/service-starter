//package com.thoughtworks.filter;
//
//import org.slf4j.MDC;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.filter.CommonsRequestLoggingFilter;
//
//import javax.servlet.*;
//import java.io.IOException;
//
//@Configuration
//public class RequestLoggingFilterConfig implements Filter {
//    @Bean
//    public CommonsRequestLoggingFilter logFilter() throws ServletException {
//        CommonsRequestLoggingFilter filter
//                = new CommonsRequestLoggingFilter();
//        filter.setIncludeQueryString(true);
//        filter.setIncludePayload(true);
//        filter.setMaxPayloadLength(10000);
//        filter.setAfterMessagePrefix("PAYMENT ");
//        return filter;
//    }
//
//    @Override
//    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
//        MDC.put("req.id", String.valueOf(1));
//        filterChain.doFilter(servletRequest,servletResponse);
//    }
//}
