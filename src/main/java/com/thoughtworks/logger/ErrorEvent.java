package com.thoughtworks.logger;

import com.google.gson.JsonObject;
import org.apache.logging.log4j.Logger;

import java.util.LinkedHashMap;
import java.util.Map;

import static net.logstash.logback.argument.StructuredArguments.v;

public class ErrorEvent {
    private Logger logger;
    private String description;
    private String name;
    private Map<String, String> properties = new LinkedHashMap<>();

    public ErrorEvent(String name, String description, Logger logger) {
        this.name = name;
        this.logger = logger;
        this.description = description;
    }

    public void addProperty(String key, String value) {
        properties.put(key, value);
    }

    public void publish() {
        JsonObject logDetails = new JsonObject();
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            logDetails.addProperty(entry.getKey(), entry.getValue());
        }
        logger.info("{name:{},description:{},details:{}}", v("name", name), v("description", description), v("details", logDetails));
    }
}
