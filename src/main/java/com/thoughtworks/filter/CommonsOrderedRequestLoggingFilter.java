package com.thoughtworks.filter;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@Service
@Order(2)
public class CommonsOrderedRequestLoggingFilter extends CommonsRequestLoggingFilter {
    CommonsOrderedRequestLoggingFilter() {
        super();
        this.setIncludeQueryString(true);
        this.setIncludeHeaders(true);
        this.setIncludePayload(true);
        this.setIncludeClientInfo(true);
        this.setMaxPayloadLength(10000);
        this.setAfterMessagePrefix("PAYMENT ");
    }
}
