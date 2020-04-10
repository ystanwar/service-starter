package com.thoughtworks.logger;

import ch.qos.logback.classic.spi.ILoggingEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class CustomJsonLayoutTest {

    @Mock
    ILoggingEvent event;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void ensureCustomDataIsAddedToJsonMap() {
        Map<String, String> mdcPropertyMap = new HashMap<>();
        mdcPropertyMap.put("samplethread", "v1234");
        mdcPropertyMap.put("appVersion", "v1.0");
        mdcPropertyMap.put("correlation_id", "12345");

        when(event.getMDCPropertyMap()).thenReturn(mdcPropertyMap);

        CustomJsonLayout layout = new CustomJsonLayout();
        Map<String, Object> map = new HashMap<>();

        layout.addCustomDataToJsonMap(map, event);
        assertTrue(map.containsKey("j_samplethread"));
        assertEquals(map.get("j_samplethread"), "v1234");
        assertTrue(map.containsKey("j_appVersion"));
        assertEquals(map.get("j_appVersion"), "v1.0");
        assertTrue(map.containsKey("j_correlation_id"));
        assertEquals(map.get("j_correlation_id"), "12345");
    }
}