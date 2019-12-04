package com.thoughtworks.bankclient;


import com.thoughtworks.payment.BankDetails;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.net.ssl.HttpsURLConnection;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
public class BankClientTest {

    @Autowired
    BankClient bankClient;

    @MockBean
    URL url;

    @Test
    public void checkBankDetails() throws Exception {
        URL u = mock(URL.class);
        HttpsURLConnection huc = mock(HttpsURLConnection.class);
        when(new URL(anyString())).thenReturn(u);
        when(u.openConnection()).thenReturn(huc);
        assertEquals(200, bankClient.checkBankDetails(12345,"HDFC1234"));
    }

    @Test
    public void failsToCheckBankDetails() throws Exception {
        assertEquals(404, bankClient.checkBankDetails(0000,"HDFC1234"));
    }
}
