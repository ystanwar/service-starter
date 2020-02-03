package com.thoughtworks.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.error.PaymentErrorResponse;
import com.thoughtworks.payment.message.PaymentResponse;
import com.thoughtworks.payment.model.BankDetails;
import com.thoughtworks.payment.model.Payment;
import com.thoughtworks.prometheus.Prometheus;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Gauge;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.metrics.CompositeMeterRegistryAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.MetricsAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentController.class)
@Import({MetricsAutoConfiguration.class, CompositeMeterRegistryAutoConfiguration.class, CollectorRegistry.class})
public class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    MeterRegistry meterRegistry;

    @Autowired
    CollectorRegistry collectorRegistry;

    @MockBean
    PaymentService paymentService;

    @MockBean
    PaymentRepository paymentRepository;

    @MockBean
    Prometheus prometheus;

    @AfterEach
    void tearDown() {
        collectorRegistry.clear();
    }

    @Test
    public void createPayment() throws Exception {
        BankDetails beneficiary = new BankDetails("user1", 12, "HDFC1");
        BankDetails payee = new BankDetails("user2", 12346, "HDFC1234");

        Payment payment = new Payment(500, beneficiary, payee);
        payment.setId(1);

        ObjectMapper objectMapper = new ObjectMapper();

        PaymentResponse response = new PaymentResponse();
        response.setStatusMessage("Payment done successfully");
        response.setPaymentId(payment.getId());

        when(prometheus.getPaymentsCounter()).thenReturn(Counter
                .builder("paymentService")
                .description("counter for number of payments")
                .tags("counter", "number of payments")
                .register(meterRegistry));

        when(prometheus.getPaymentSuccessCounter()).thenReturn(Counter
                .builder("paymentService")
                .description("counter for successful payments")
                .tags("counter", "successful payments")
                .register(meterRegistry));

        when(prometheus.getPaymentRequestTime()).thenReturn(Gauge.build()
                .name("payment_Request_Time")
                .help("calculate time for per payment request")
                .register(collectorRegistry));

        when(paymentService.create(any(Payment.class))).thenReturn(payment);

        mockMvc.perform(post("/payments")
                .content("{\"amount\":500," +
                        "\"beneficiary\":{\"name\":\"user1\",\"accountNumber\":12345,\"ifscCode\":\"HDFC1234\"}" +
                        ",\"payee\":{\"name\":\"user2\",\"accountNumber\":12346,\"ifscCode\":\"HDFC1234\"}" +
                        "}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().string(objectMapper.writeValueAsString(response)));

        verify(paymentService).create(any(Payment.class));
    }

    @Test
    public void createPaymentWithBeneficiaryDetailsNotExists() throws Exception {
        when(paymentService.create(any(Payment.class))).thenThrow(new BeneficiaryAccountDetailsNotFound("message", "Beneficiary AccountDetails Not Found"));
        Map<String, String> errors = new HashMap<>();
        errors.put("message", "Beneficiary AccountDetails Not Found");
        ObjectMapper objectMapper = new ObjectMapper();

        when(prometheus.getPaymentsCounter()).thenReturn(Counter
                .builder("paymentService")
                .description("counter for number of payments")
                .tags("counter", "number of payments")
                .register(meterRegistry));

        mockMvc.perform(post("/payments")
                .content("{\"amount\":500," +
                        "\"beneficiary\":{\"name\":\"user1\",\"accountNumber\":12,\"ifscCode\":\"HDFC1\"}" +
                        ",\"payee\":{\"name\":\"user2\",\"accountNumber\":12346,\"ifscCode\":\"HDFC1234\"}" +
                        "}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(objectMapper.writeValueAsString(new PaymentErrorResponse("PAYMENT_FAILED", errors))));

        verify(paymentService).create(any(Payment.class));
    }

    @Test
    public void createPaymentWithPayeeDetailsNotExists() throws Exception {
        Map<String, String> errors = new HashMap<>();
        errors.put("message", "Payee AccountDetails Not Found");
        ObjectMapper objectMapper = new ObjectMapper();

        when(prometheus.getPaymentsCounter()).thenReturn(Counter
                .builder("paymentService")
                .description("counter for number of payments")
                .tags("counter", "number of payments")
                .register(meterRegistry));

        when(paymentService.create(any(Payment.class))).thenThrow(new PayeeAccountDetailsNotFound("message", "Payee AccountDetails Not Found"));

        mockMvc.perform(post("/payments")
                .content("{\"amount\":500," +
                        "\"beneficiary\":{\"name\":\"user1\",\"accountNumber\":12345,\"ifscCode\":\"HDFC1234\"}" +
                        ",\"payee\":{\"name\":\"user2\",\"accountNumber\":12,\"ifscCode\":\"HDFC1\"}" +
                        "}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(objectMapper.writeValueAsString(new PaymentErrorResponse("PAYMENT_FAILED", errors))));

        verify(paymentService).create(any(Payment.class));
    }
}
