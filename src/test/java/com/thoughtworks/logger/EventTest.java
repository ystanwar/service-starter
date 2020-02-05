package com.thoughtworks.logger;

import net.logstash.logback.argument.StructuredArgument;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.ArgumentCaptor;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class EventTest {

    @MockBean
    Logger logger;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void checkEventPublishWithoutProperties() {
        Logger logger2 = mock(Logger.class);

        ArgumentCaptor<String> messageArgument = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<StructuredArgument> structuredArgument1 = ArgumentCaptor.forClass(StructuredArgument.class);
        ArgumentCaptor<StructuredArgument> structuredArgument2 = ArgumentCaptor.forClass(StructuredArgument.class);
        ArgumentCaptor<StructuredArgument> structuredArgument3 = ArgumentCaptor.forClass(StructuredArgument.class);

        Event event = new Event("TestEvent", "Test description", logger2);
        event.publish();
        verify(logger2).info(messageArgument.capture(), structuredArgument1.capture(), structuredArgument2.capture(), structuredArgument3.capture());
        assertEquals("{name:{},description:{},details:{}}", messageArgument.getValue());
        assertEquals("TestEvent", structuredArgument1.getValue().toString());
        assertEquals("Test description", structuredArgument2.getValue().toString());
        assertEquals("{}", structuredArgument3.getValue().toString());
    }

    @Test
    public void checkEventPublishWithoutPropertiesNullDesc() {
        Logger logger2 = mock(Logger.class);

        ArgumentCaptor<String> messageArgument = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<StructuredArgument> structuredArgument1 = ArgumentCaptor.forClass(StructuredArgument.class);
        ArgumentCaptor<StructuredArgument> structuredArgument2 = ArgumentCaptor.forClass(StructuredArgument.class);
        ArgumentCaptor<StructuredArgument> structuredArgument3 = ArgumentCaptor.forClass(StructuredArgument.class);

        Event event = new Event("TestEvent", null, logger2);
        event.publish();
        verify(logger2).info(messageArgument.capture(), structuredArgument1.capture(), structuredArgument2.capture(), structuredArgument3.capture());
        assertEquals("{name:{},description:{},details:{}}", messageArgument.getValue());
        assertEquals("TestEvent", structuredArgument1.getValue().toString());
        assertEquals("", structuredArgument2.getValue().toString());
        assertEquals("{}", structuredArgument3.getValue().toString());
    }

    @Test
    public void checkEventPublishWithProperties() {
        Logger logger2 = mock(Logger.class);

        ArgumentCaptor<String> messageArgument = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<StructuredArgument> structuredArgument1 = ArgumentCaptor.forClass(StructuredArgument.class);
        ArgumentCaptor<StructuredArgument> structuredArgument2 = ArgumentCaptor.forClass(StructuredArgument.class);
        ArgumentCaptor<StructuredArgument> structuredArgument3 = ArgumentCaptor.forClass(StructuredArgument.class);

        Event event = new Event("TestEvent", "Test description", logger2);
        event.addProperty("prop1", "hello world");
        event.publish();
        verify(logger2).info(messageArgument.capture(), structuredArgument1.capture(), structuredArgument2.capture(), structuredArgument3.capture());
        assertEquals("{name:{},description:{},details:{}}", messageArgument.getValue());
        assertEquals("TestEvent", structuredArgument1.getValue().toString());
        assertEquals("Test description", structuredArgument2.getValue().toString());
        assertEquals("{\"prop1\":\"hello world\"}", structuredArgument3.getValue().toString());
    }

    @Test
    public void checkEventPublishWithoutPropertiesNullName() {
        Logger logger2 = mock(Logger.class);
        assertThrows(IllegalArgumentException.class, () -> (new Event(null, "Test description", logger2)).publish());
    }
}
