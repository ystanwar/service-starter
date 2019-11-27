package com.thoughtworks.payment;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
        BankDetails beneficiary = new BankDetails(2, "user1", 12345, "HDFC1234");
        BankDetails payee = new BankDetails(1, "user2", 12346, "HDFC1234");

        Payment payment = new Payment(1, 100, beneficiary, payee);
        when(paymentService.create(any(Payment.class))).thenReturn(payment);

        mockMvc.perform(post("/payments")
                .content("{\"id\":1,\"amount\":100,\"beneficiary_name\":\"user1\",\"beneficiary_accountNumber\":12345,\"beneficiary_ifscCode\":\"HDFC1234\"," +
                        "\"payee_name\":\"user2\",\"payee_accountNumber\":12346,\"payee_ifscCode\":\"HDFC1234\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        verify(paymentService).create(any(Payment.class));
    }
}
