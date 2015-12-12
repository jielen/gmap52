package com.kingdrive.framework.controller.web;

import java.util.HashMap;
import java.util.Map;

public class EventMapping {
  private String event;

  private Map resultMappings;

  public EventMapping(String event) {
    this.event = event;
    this.resultMappings = new HashMap();
  }

  public String getEvent() {
    return event;
  }

  public void putScreen(String name, String screen) {
    if (resultMappings == null) {
      resultMappings = new HashMap();
    }
    resultMappings.put(name, screen);
  }

  public String getScreen(String name) {
    return (String) resultMappings.get(name);
  }

  public String toString() {
    StringBuffer result = new StringBuffer();
    result.append("event:").append(event).append("\n");
    Object[] keys = resultMappings.keySet().toArray();
    for (int i = 0; i < keys.length; i++) {
      result.append("\t\t").append("result:").append(keys[i]).append("\t")
          .append("screen:").append(resultMappings.get(keys[i])).append("\n");
    }
    return result.toString();
  }
}
