package com.thoughtworks.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentController.class)
public class PaymentControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    PaymentService paymentService;

    @MockBean
    PaymentRepository paymentRepository;

    @Test
    public void createPayment() throws Exception {
        BankDetails beneficiary = new BankDetails("user1", 12, "HDFC1");
        BankDetails payee = new BankDetails("user2", 12346, "HDFC1234");

        Payment payment = new Payment(500, beneficiary, payee);

        ObjectMapper objectMapper = new ObjectMapper();
        when(paymentService.create(any(Payment.class))).thenReturn(payment);

        mockMvc.perform(post("/payments")
                .content("{\"amount\":500," +
                        "\"beneficiary\":{\"name\":\"user1\",\"accountNumber\":12345,\"ifscCode\":\"HDFC1234\"}" +
                        ",\"payee\":{\"name\":\"user2\",\"accountNumber\":12346,\"ifscCode\":\"HDFC1234\"}" +
                        "}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().string(objectMapper.writeValueAsString(payment)));

        verify(paymentService).create(any(Payment.class));
    }

    @Test
    public void createPaymentWithBeneficiaryDetailsNotExists() throws Exception {
        when(paymentService.create(any(Payment.class))).thenThrow(new BeneficiaryAccountDetailsNotFound("Beneficiary AccountDetails Not Found"));

        MvcResult response = mockMvc.perform(post("/payments")
                .content("{\"amount\":500," +
                        "\"beneficiary\":{\"name\":\"user1\",\"accountNumber\":12,\"ifscCode\":\"HDFC1\"}" +
                        ",\"payee\":{\"name\":\"user2\",\"accountNumber\":12346,\"ifscCode\":\"HDFC1234\"}" +
                        "}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();

        assertEquals(response.getResolvedException().getMessage(), "Beneficiary AccountDetails Not Found");
        verify(paymentService).create(any(Payment.class));
    }
    @Test
    public void createPaymentWithPayeeDetailsNotExists() throws Exception {
        when(paymentService.create(any(Payment.class))).thenThrow(new PayeeAccountDetailsNotFound("Payee AccountDetails Not Found"));

        MvcResult response = mockMvc.perform(post("/payments")
                .content("{\"amount\":500," +
                        "\"beneficiary\":{\"name\":\"user1\",\"accountNumber\":12345,\"ifscCode\":\"HDFC1234\"}" +
                        ",\"payee\":{\"name\":\"user2\",\"accountNumber\":12,\"ifscCode\":\"HDFC1\"}" +
                        "}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();

        assertEquals(response.getResolvedException().getMessage(), "Payee AccountDetails Not Found");
        verify(paymentService).create(any(Payment.class));
    }
}
