package com.thoughtworks.filter;

import com.thoughtworks.prometheus.Prometheus;
import io.micrometer.core.instrument.Counter;
import io.prometheus.client.Gauge;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.*;
import java.io.IOException;
import java.util.UUID;

@Service
public class PaymentsFilter implements Filter {

    @Autowired
    private Prometheus prometheus;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        MDC.put("request.id", String.valueOf(UUID.randomUUID()));

        Gauge paymentRequestTime;
        Counter paymentsCounter;
        paymentsCounter = prometheus.getPaymentsCounter();
        paymentsCounter.increment();
        paymentRequestTime = prometheus.getPaymentRequestTime();
        Gauge.Timer timer = paymentRequestTime.startTimer();
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            timer.setDuration();
        }

    }
}
