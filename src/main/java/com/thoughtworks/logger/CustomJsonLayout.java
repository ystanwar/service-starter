package com.thoughtworks.logger;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.contrib.json.classic.JsonLayout;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class CustomJsonLayout extends JsonLayout {

    @Override
    protected void addCustomDataToJsonMap(Map<String, Object> map, ILoggingEvent event) {
        super.addCustomDataToJsonMap(map, event);
        Map<String, String> mdcPropertyMap = event.getMDCPropertyMap();
        Iterator<Entry<String, String>> iterator = mdcPropertyMap.entrySet().iterator();
        while (iterator.hasNext()){
            Entry<String, String> entry = iterator.next();
            map.put("j_"+entry.getKey(), entry.getValue());
        }
        
    }
}
