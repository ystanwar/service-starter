//package com.thoughtworks.filter;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.filter.CommonsRequestLoggingFilter;
//import org.springframework.web.filter.ServletContextRequestLoggingFilter;
//
//import javax.servlet.ServletException;
//
/* NOTE: Uncomment and use this class instead of
 CommonsOrderedRequestLoggingFilter if there is no need to set order
 */
//@Configuration
//public class CommonsRequestLoggingFilterConfig {
//
//    @Bean
//    public CommonsRequestLoggingFilter logFilter() throws ServletException {
//        CommonsRequestLoggingFilter filter
//                = new CommonsRequestLoggingFilter();
//        filter.setIncludeQueryString(true);
//        filter.setIncludeHeaders(true);
//        filter.setIncludePayload(true);
//        filter.setIncludeClientInfo(true);
//        filter.setMaxPayloadLength(10000);
//        filter.setAfterMessagePrefix("PAYMENT ");
//        return filter;
//    }
//}
