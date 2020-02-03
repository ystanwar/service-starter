package com.thoughtworks.prometheus;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Gauge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class Prometheus {

    private Counter paymentFailedCounter;
    private Counter paymentsCounter;
    private Counter paymentSuccessCounter;
    private Gauge paymentRequestTime;

    @Autowired
    Prometheus(MeterRegistry meterRegistry,
               CollectorRegistry collectorRegistry) {
        paymentFailedCounter = Counter
                .builder("paymentService")
                .description("counter for number of payments")
                .tags("counter", "number of payments failed")
                .register(meterRegistry);

        paymentsCounter = Counter
                .builder("paymentService")
                .description("counter for number of payments")
                .tags("counter", "number of payments")
                .register(meterRegistry);

        paymentSuccessCounter = Counter
                .builder("paymentService")
                .description("counter for successful payments")
                .tags("counter", "successful payments")
                .register(meterRegistry);

        paymentRequestTime = Gauge.build()
                .name("payment_Request_Time")
                .help("calculate time for per payment request")
                .register(collectorRegistry);
    }

    public Counter getPaymentFailedCounter() {
        return paymentFailedCounter;
    }

    public Counter getPaymentsCounter() {
        return paymentsCounter;
    }

    public Counter getPaymentSuccessCounter() {
        return paymentSuccessCounter;
    }

    public Gauge getPaymentRequestTime() {
        return paymentRequestTime;
    }
}
