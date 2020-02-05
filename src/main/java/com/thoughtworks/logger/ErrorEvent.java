package com.thoughtworks.logger;

import org.apache.logging.log4j.Logger;

import static net.logstash.logback.argument.StructuredArguments.v;

public class ErrorEvent extends Event {

    public ErrorEvent(String name, String description, Logger logger) {
        super(name, description, logger);
    }

    public void publish() {
        logger.error("{name:{},description:{},details:{}}", v("name", name), v("description", description), v("details", getPropertiesJson()));
    }
}
