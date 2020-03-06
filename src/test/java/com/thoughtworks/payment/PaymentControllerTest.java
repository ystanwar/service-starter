package com.thoughtworks.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.api.payment.PaymentFailureResponse;
import com.thoughtworks.api.payment.PaymentRequest;
import com.thoughtworks.api.payment.PaymentSuccessResponse;
import com.thoughtworks.exceptions.*;
import com.thoughtworks.payment.model.BankDetails;
import com.thoughtworks.payment.model.Payment;
import com.thoughtworks.prometheus.Prometheus;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.prometheus.client.CollectorRegistry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.metrics.CompositeMeterRegistryAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.MetricsAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    @BeforeEach
    void setUp() {

        when(prometheus.getPaymentsCounter()).thenReturn(Counter
                .builder("paymentService")
                .description("counter for number of payments")
                .tags("counter", "number of payments")
                .register(meterRegistry));
    }

    @AfterEach
    void tearDown() {
        collectorRegistry.clear();
    }

    @Test
    public void testGetAllPayments() throws Exception {

        List<Payment> paymentList = new ArrayList<>();

        BankDetails beneficiary = new BankDetails("user1", 12, "HDFC1");
        BankDetails payee = new BankDetails("user2", 12346, "HDFC1234");

        Payment payment = new Payment(500,beneficiary,payee);
        payment.setId(1);
        paymentList.add(payment);

        payment = new Payment(2100, beneficiary, payee);
        payment.setId(2);
        paymentList.add(payment);

        when(paymentService.getAll()).thenReturn(paymentList);

        ResultActions mockResult = mockMvc.perform(get("/payments"))
                .andExpect(status().is2xxSuccessful());

        String responseJson = mockResult.andReturn().getResponse().getContentAsString();

        ObjectMapper objectmapper = new ObjectMapper();
        List<Payment> paymentListResponse = new ObjectMapper().readValue(responseJson, objectmapper.getTypeFactory().constructCollectionType(List.class, Payment.class));

        verify(paymentService, times(1)).getAll();
        assertEquals(2, paymentListResponse.size());

        assertEquals(500, paymentListResponse.get(0).getAmount());
        assertEquals(2100, paymentListResponse.get(1).getAmount());
    }

    @Test
    public void createPayment() throws Exception {
        BankDetails beneficiary = new BankDetails("user1", 12, "HDFC1");
        BankDetails payee = new BankDetails("user2", 12346, "HDFC1234");

        Payment payment = new Payment(500, beneficiary, payee);
        payment.setId(1);

        ObjectMapper objectMapper = new ObjectMapper();

        PaymentSuccessResponse response = new PaymentSuccessResponse();
        response.setStatusMessage("Payment done successfully");
        response.setPaymentId(payment.getId());

        when(paymentService.create(any(PaymentRequest.class))).thenReturn(payment);

        mockMvc.perform(post("/payments")
                .content("{\"amount\":500," +
                        "\"beneficiary\":{\"name\":\"user1\",\"accountNumber\":12345,\"ifscCode\":\"HDFC1234\"}" +
                        ",\"payee\":{\"name\":\"user2\",\"accountNumber\":12346,\"ifscCode\":\"HDFC1234\"}" +
                        "}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().string(objectMapper.writeValueAsString(response)));

        verify(paymentService).create(any(PaymentRequest.class));
    }

    @Test
    public void createPaymentWithBeneficiaryDetailsNotExists() throws Exception {
        when(paymentService.create(any(PaymentRequest.class))).thenThrow(new ResourceNotFoundException("message", "Beneficiary AccountDetails Not Found"));

        Map<String, String> errors = new HashMap<>();
        errors.put("message", "Beneficiary AccountDetails Not Found");
        ObjectMapper objectMapper = new ObjectMapper();

        mockMvc.perform(post("/payments")
                .content("{\"amount\":500," +
                        "\"beneficiary\":{\"name\":\"user1\",\"accountNumber\":12,\"ifscCode\":\"HDFC1\"}" +
                        ",\"payee\":{\"name\":\"user2\",\"accountNumber\":12346,\"ifscCode\":\"HDFC1234\"}" +
                        "}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(objectMapper.writeValueAsString(new PaymentFailureResponse("MISSING_INFO", errors))));

        verify(paymentService).create(any(PaymentRequest.class));
    }

    @Test
    public void createPaymentWithPayeeDetailsNotExists() throws Exception {
        Map<String, String> errors = new HashMap<>();
        errors.put("message", "Payee AccountDetails Not Found");
        ObjectMapper objectMapper = new ObjectMapper();

        when(paymentService.create(any(PaymentRequest.class))).thenThrow(new ResourceNotFoundException("message", "Payee AccountDetails Not Found"));

        mockMvc.perform(post("/payments")
                .content("{\"amount\":500," +
                        "\"beneficiary\":{\"name\":\"user1\",\"accountNumber\":12345,\"ifscCode\":\"HDFC1234\"}" +
                        ",\"payee\":{\"name\":\"user2\",\"accountNumber\":12,\"ifscCode\":\"HDFC1\"}" +
                        "}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(objectMapper.writeValueAsString(new PaymentFailureResponse("MISSING_INFO", errors))));

        verify(paymentService).create(any(PaymentRequest.class));
    }

    @Test
    public void createPaymentWithAmountGreaterThanLimit() throws Exception {
        when(paymentService.create(any(PaymentRequest.class))).thenThrow(MethodArgumentNotValidException.class);

        mockMvc.perform(post("/payments")
                .content("{\"amount\":10000000," +
                        "\"beneficiary\":{\"name\":\"user1\",\"accountNumber\":12345,\"ifscCode\":\"HDFC1234\"}" +
                        ",\"payee\":{\"name\":\"user2\",\"accountNumber\":67890,\"ifscCode\":\"HDFC1234\"}" +
                        "}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("{\"message\":\"INVALID_INPUT\",\"reasons\":{\"amount\":\"amount cannot be greater than 100000\"}}"));

        verify(paymentService, times(0)).create(any(PaymentRequest.class));
    }

    @Test
    public void createPaymentWithMultipleValidationErrors() throws Exception {

        when(paymentService.create(any(PaymentRequest.class))).thenThrow(MethodArgumentNotValidException.class);

        ResultActions mockResult = mockMvc.perform(post("/payments")
                .content(getContentForMultipleValidationErrors())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        String responseJson = mockResult.andReturn().getResponse().getContentAsString();
        PaymentErrorResponseJson errorResponse = new ObjectMapper().readValue(responseJson, PaymentErrorResponseJson.class);
        assertErrorResponse(errorResponse);

        verify(paymentService, times(0)).create(any(PaymentRequest.class));

    }

    private String getContentForMultipleValidationErrors() {
        return "{\"amount\":10000000," +
                "\"beneficiary\":{\"name\":\"user1\",\"accountNumber\":12345,\"ifscCode\":\"\"}" +
                ",\"payee\":{\"name\":\"user2\",\"accountNumber\":67890,\"ifscCode\":\"HDFC1234\"}" +
                "}";
    }

    private void assertErrorResponse(PaymentErrorResponseJson errorResponse) {
        assertNotNull(errorResponse);
        boolean isAmountInvalid = false;
        boolean isBeneficiaryIfscCodeEmpty = false;

        Map<String, String> reasons = errorResponse.getReasons();
        for (Map.Entry<String, String> reason : reasons.entrySet()) {
            if (reason.getKey().equalsIgnoreCase("amount")
                    && reason.getValue().equalsIgnoreCase("amount cannot be greater than 100000")) {
                isAmountInvalid = true;
            }

            if (reason.getKey().equalsIgnoreCase("beneficiary.ifscCode")
                    && reason.getValue().equalsIgnoreCase("ifsc code must not be empty")) {
                isBeneficiaryIfscCodeEmpty = true;
            }
        }
        assertTrue(isAmountInvalid);
        assertTrue(isBeneficiaryIfscCodeEmpty);

    }

    @Test
    public void createPaymentWithBeneficiaryBankInfoMissing() throws Exception {
        Map<String, String> errors = new HashMap<>();
        errors.put("message", "Bank info not found for ABCD234");
        ObjectMapper objectMapper = new ObjectMapper();

        when(paymentService.create(any(PaymentRequest.class))).thenThrow(new ResourceNotFoundException("message", "Bank info not found for ABCD234"));

        mockMvc.perform(post("/payments")
                .content("{\"amount\":500," +
                        "\"beneficiary\":{\"name\":\"user1\",\"accountNumber\":12345,\"ifscCode\":\"ABCD234\"}" +
                        ",\"payee\":{\"name\":\"user2\",\"accountNumber\":12,\"ifscCode\":\"HDFC1234\"}" +
                        "}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(objectMapper.writeValueAsString(new PaymentFailureResponse("MISSING_INFO", errors))));

        verify(paymentService).create(any(PaymentRequest.class));
    }


    @Test
    public void createPaymentWithInvalidIfscCodeFormat() throws Exception {
        Map<String, String> errors = new HashMap<>();
        errors.put("message", "Invalid ifscCode format ->ABCD");
        ObjectMapper objectMapper = new ObjectMapper();

        when(paymentService.create(any(PaymentRequest.class))).thenThrow(new ValidationException("message", "Invalid ifscCode format ->ABCD"));

        mockMvc.perform(post("/payments")
                .content("{\"amount\":500," +
                        "\"beneficiary\":{\"name\":\"user1\",\"accountNumber\":12345,\"ifscCode\":\"ABCD\"}" +
                        ",\"payee\":{\"name\":\"user2\",\"accountNumber\":12,\"ifscCode\":\"HDFC1234\"}" +
                        "}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(objectMapper.writeValueAsString(new PaymentFailureResponse("INVALID_INPUT", errors))));

        verify(paymentService).create(any(PaymentRequest.class));
    }


    @Test
    public void createPaymentWithGeneralException() throws Exception {
        Map<String, String> errors = new HashMap<>();
        errors.put("message", "Could not process the request");
        ObjectMapper objectMapper = new ObjectMapper();

        when(paymentService.create(any(PaymentRequest.class))).thenThrow(new Exception("Could not process the request"));

        mockMvc.perform(post("/payments")
                .content("{\"amount\":500," +
                        "\"beneficiary\":{\"name\":\"user1\",\"accountNumber\":12345,\"ifscCode\":\"HDFC1234\"}" +
                        ",\"payee\":{\"name\":\"user2\",\"accountNumber\":12,\"ifscCode\":\"HDFC1234\"}" +
                        "}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(objectMapper.writeValueAsString(new PaymentFailureResponse("SERVER_ERROR", errors))));

        verify(paymentService).create(any(PaymentRequest.class));
    }

    @Test
    public void createPaymentWithInvalidRequestFormat() throws Exception {
        Map<String, String> errors = new HashMap<>();
        errors.put("message", "Request body missing or incorrect format");
        ObjectMapper objectMapper = new ObjectMapper();

        when(paymentService.create(any(PaymentRequest.class))).thenThrow(new Exception("paymentService.create() not expected to be called for this test case"));

        mockMvc.perform(post("/payments")
                .content("{\"amount\":500" +
                        "\"beneficiary\":{\"name\":\"user1\",\"accountNumber\":12345,\"ifscCode\":\"ABCD\"}" +
                        ",\"payee\":{\"name\":\"user2\",\"accountNumber\":12,\"ifscCode\":\"HDFC1234\"}" +
                        "}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(objectMapper.writeValueAsString(new PaymentFailureResponse("INVALID_INPUT", errors))));

        verify(paymentService, times(0)).create(any(PaymentRequest.class));
    }

    @Test
    public void testCannotCreatePaymentDueToSuspectedFraud() throws Exception {
        when(paymentService.create(any(PaymentRequest.class))).thenThrow(new PaymentRefusedException("SUSPECTED_FRAUD", "Suspected fraudulent transaction"));

        Map<String, String> errors = new HashMap<>();
        errors.put("SUSPECTED_FRAUD", "Suspected fraudulent transaction");
        ObjectMapper objectMapper = new ObjectMapper();

        mockMvc.perform(post("/payments")
                .content("{\"amount\":500," +
                        "\"beneficiary\":{\"name\":\"user1\",\"accountNumber\":12345,\"ifscCode\":\"HDFC1\"}" +
                        ",\"payee\":{\"name\":\"user2\",\"accountNumber\":12345,\"ifscCode\":\"HDFC1234\"}" +
                        "}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string(objectMapper.writeValueAsString(new PaymentFailureResponse("REQUEST_UNPROCESSABLE", errors))));

        verify(paymentService).create(any(PaymentRequest.class));
    }


    @Test
    public void testCannotCreatePaymentDueToDependencyError() throws Exception {
        when(paymentService.create(any(PaymentRequest.class))).thenThrow(new DependencyException("ExternalService", "BankService - HDFC1234", "/checkFraud", "UNAVAILABLE", new Exception()));

        Map<String, String> errors = new HashMap<>();
        errors.put("message", "Could not process the request");
        ObjectMapper objectMapper = new ObjectMapper();

        mockMvc.perform(post("/payments")
                .content("{\"amount\":500," +
                        "\"beneficiary\":{\"name\":\"user1\",\"accountNumber\":12345,\"ifscCode\":\"HDFC1\"}" +
                        ",\"payee\":{\"name\":\"user2\",\"accountNumber\":12345,\"ifscCode\":\"HDFC1234\"}" +
                        "}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(objectMapper.writeValueAsString(new PaymentFailureResponse("SERVER_ERROR", errors))));

        verify(paymentService).create(any(PaymentRequest.class));
    }

    @Test
    public void testCannotCreatePaymentDueToBusinessError() throws Exception {
        when(paymentService.create(any(PaymentRequest.class))).thenThrow(new BusinessException("SUSPECTED_FRAUD", "Suspected fraudulent transaction"));

        Map<String, String> errors = new HashMap<>();
        errors.put("SUSPECTED_FRAUD", "Suspected fraudulent transaction");
        ObjectMapper objectMapper = new ObjectMapper();

        mockMvc.perform(post("/payments")
                .content("{\"amount\":500," +
                        "\"beneficiary\":{\"name\":\"user1\",\"accountNumber\":12345,\"ifscCode\":\"HDFC1\"}" +
                        ",\"payee\":{\"name\":\"user2\",\"accountNumber\":12345,\"ifscCode\":\"HDFC1234\"}" +
                        "}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string(objectMapper.writeValueAsString(new PaymentFailureResponse("REQUEST_UNPROCESSABLE", errors))));

        verify(paymentService).create(any(PaymentRequest.class));
    }
}

class PaymentErrorResponseJson {
    private String message;
    private Map<String, String> reasons;

    public String getMessage() {
        return message;
    }

    public Map<String, String> getReasons() {
        return reasons;
    }
}
