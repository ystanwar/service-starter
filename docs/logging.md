#Logging

### Logging framework
Following are commonly used logging frameworks in Java. In this application we are using logback. Above frameworks 
natively implement slf4j.

1.Log 4j 2.x.

2.Logback.

### Log Levels - The log level indicates the severity or importance.
    1. Trace  - Detailed information than DEBUG.
    2. Debug  - Fine grained details of an event useful for debugging.
    3. Info   - Information messages to identify progress of application.
    4. Warn   - Warning messages, pottential harmful situation.
    5. Error  - Error/ Exception events.

## Text Logging
Text logging is printing log messages are plain text/string in log file/console.
The problem with text logging files is they are unstructured text data. This makes it hard to query them for any 
sort of useful information.

Hence, as a best practice always use structured logging

## Structured Logging
Structured logging can be thought of as a stream of key-value pairs/ JSON format for every event logged, instead of 
just the plain text line of conventional logging.
   
### Using Structured Logging

  1. #### Add JSON formatter and layout
    
     Create a logback-spring.xml and add appenders, configure json layout and json formatter.
     
     ``` 
     <configuration>
     <appender name="json" class="ch.qos.logback.core.ConsoleAppender">
                <layout class="com.thoughtworks.logger.CustomJsonLayout">
                    <jsonFormatter
                            class="ch.qos.logback.contrib.jackson.JacksonJsonFormatter">
                        <prettyPrint>true</prettyPrint>
                    </jsonFormatter>
                    <timestampFormat>yyyy-MM-dd' 'HH:mm:ss.SSS</timestampFormat>
                </layout>
      </appender>
      <logger name="com.thoughtworks" level="info" additivity="false">
             <appender-ref ref="json"/>
      </logger>
     </configuration>
     ```
  2. #### Logging events
  
     Log events for API calls should produce following log messages.
     
     ```
     {
       "timestamp" : "2020-04-02 12:07:36.578",
       "level" : "INFO",
       "thread" : "Test worker",
       "mdc" : {
         "parentrequest.id" : null,
         "request.id" : "e386edb3-82fa-4a0b-8795-dee74582a9b6"
       },
       "logger" : "com.thoughtworks.filter.CustomRequestLoggingFilter",
       "message" : "{"eventCode":"REQUEST_RECEIVED","description":"POST","details":{"headers":{"Content-Type":"application/json"},"params":{},"body":""}}",
       "context" : "default"
     }
     ```
  3. #### Setting MDC attributes.
  
     Logback provides an MDC map to set correlation ID of request and other diagnostic attributes.
     
     ```
       MDC.put("request.id", String.valueOf(UUID.randomUUID()));
       log.info("successfully created");
       MDC.clear();
     ```   
     
  
     
