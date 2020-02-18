package com.thoughtworks.bankInfo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.exceptions.ResourceConflictException;
import com.thoughtworks.filter.PaymentsFilterConfig;
import com.thoughtworks.api.PaymentFailureResponse;
import com.thoughtworks.prometheus.Prometheus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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

@WebMvcTest(BankInfoController.class)
@Import({PaymentsFilterConfig.class})
public class BankInfoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BankInfoService bankInfoService;

    @MockBean
    Prometheus prometheus;

    @Test
    public void createBankInfo() throws Exception {
        BankInfo bankInfo = new BankInfo("HDFC", "http://localhost:8082");
        when(bankInfoService.create(any(BankInfo.class))).thenReturn(bankInfo);
        ObjectMapper objectMapper = new ObjectMapper();

        mockMvc.perform(post("/bankinfo")
                .content("{\"bankCode\":\"HDFC\"," +
                        "\"url\":\"http://localhost:8082\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().string(objectMapper.writeValueAsString(bankInfo)));

        verify(bankInfoService).create(any(BankInfo.class));
    }

    @Test
    public void cannotCreateBankWhenBankInfoAlreadyExists() throws Exception {
        when(bankInfoService.create(any(BankInfo.class))).thenThrow(new ResourceConflictException("message", "Bank info already exists"));
        Map<String, String> errors = new HashMap<>();
        errors.put("message", "Bank info already exists");
        ObjectMapper objectMapper = new ObjectMapper();
        mockMvc.perform(post("/bankinfo")
                .content("{\"bankCode\":\"HDFC\"," +
                        "\"url\":\"http://localhost:8082\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(content().string(objectMapper.writeValueAsString(new PaymentFailureResponse("REQUEST_CONFLICT", errors))));

        verify(bankInfoService).create(any(BankInfo.class));
    }

}
