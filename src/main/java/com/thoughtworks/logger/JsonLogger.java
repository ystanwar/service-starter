package com.thoughtworks.logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import org.apache.logging.log4j.Logger;

import java.util.Map;

import static net.logstash.logback.argument.StructuredArguments.v;

public class JsonLogger {
    public static void logEventAsJson(Logger logger, String eventCode, String description, Map<String, Object> details) throws JsonProcessingException {
        String detailsString = "nothing";
        if (details != null) {
            ObjectMapper mapMapper = new ObjectMapper();
            detailsString = mapMapper.writeValueAsString(details);
        }

        logger.info("{\"eventCode\":\"{}\",\"description\":\"{}\",\"details\":{}}", v("eventCode", eventCode), v("description", description), v("details", detailsString));

    }

    public static void logErrorAsJson(Logger logger, String eventCode, String description, JsonObject details, Exception exception) {
        logger.error("{\"eventCode\":\"{}\",\"description\":\"{}\",\"details\":{},\"exception\":\"{}\",\"stackTrace\":\"{}\"}", v("eventCode", eventCode), v("description", description), v("details", details), v("exception", exception.toString()), v("stackTrace", exception.getStackTrace()));
    }
}
