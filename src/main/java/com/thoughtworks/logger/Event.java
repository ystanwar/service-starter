package com.thoughtworks.logger;

import com.google.gson.JsonObject;
import org.springframework.lang.Nullable;
import org.apache.logging.log4j.Logger;

import java.util.LinkedHashMap;
import java.util.Map;

import static net.logstash.logback.argument.StructuredArguments.v;

public class Event {
    protected Logger logger;
    protected String name;
    protected String description;
    protected Map<String, String> properties = new LinkedHashMap<>();

    public Event(String name, @Nullable String description, Logger logger) {
        if (name == null) {
            throw new IllegalArgumentException("Event name cannot be null");
        }
        this.name = name;
        this.description = description == null ? "" : description;
        this.logger = logger;
    }

    public Event addProperty(String key, String value) {
        properties.put(key, value);
        return this;
    }

    public void publish() {
        JsonObject logDetails = getPropertiesJson();
        logger.info("{name:{},description:{},details:{}}", v("name", name), v("description", description), v("details", logDetails));
    }

    protected JsonObject getPropertiesJson() {
        JsonObject logDetails = new JsonObject();
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            logDetails.addProperty(entry.getKey(), entry.getValue());
        }
        return logDetails;
    }
}
