package com.thoughtworks.payment;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
public class PaymentApplicationTests {

    @MockBean
    RestTemplate restTemplate;

    @Test
    public void contextLoads() {
    }
}
