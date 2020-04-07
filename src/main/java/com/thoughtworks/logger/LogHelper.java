package com.thoughtworks.logger;

import com.google.gson.JsonObject;

import org.slf4j.Logger;
import static net.logstash.logback.argument.StructuredArguments.v;


public class LogHelper {

    public static void logEventJson(Logger logger, String eventCode, String description, JsonObject details){
        logger.error("{\"eventCode\":\"{}\",\"description\":\"{}\",\"details\":\"{}\"}", v("eventCode", eventCode), 
        v("description", description), v("details", details.toString()));
    }

    public static void logErrorJson(Logger logger, String eventCode, String description, JsonObject details, Exception exception){
        logger.error("{\"eventCode\":\"{}\",\"description\":\"{}\",\"details\":\"{}\",\"exception\":\"{}\"}", 
        v("eventCode", eventCode), v("description", description), v("details", details.toString()), v("exception", 
        exception.getClass().toString()));
    }
}