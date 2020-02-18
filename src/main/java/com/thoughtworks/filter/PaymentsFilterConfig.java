package com.thoughtworks.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaymentsFilterConfig {

    @Autowired
    PaymentsFilter paymentsFilter;

    @Bean
    public FilterRegistrationBean<PaymentsFilter> filterRegistrationBean() {
        FilterRegistrationBean<PaymentsFilter> registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(paymentsFilter);
        registrationBean.addUrlPatterns("/payments");
        return registrationBean;
    }
}
