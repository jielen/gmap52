package com.kingdrive.framework.controller.web;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
import java.util.List;

public class URLMapping {
  private String url;

  private String screen;

  private String webActionClass = null;

  private List eventMappings = null;

  private boolean isClearParameter = false;

  private boolean isClearTemp = false;

  public URLMapping(String url, String screen) {
    this.url = url;
    this.screen = screen;
  }

  public URLMapping(String url, String screen, String webActionClass,
      List eventMappings, boolean isClearParameter, boolean isClearTemp) {
    this.url = url;
    this.screen = screen;
    this.webActionClass = webActionClass;
    this.eventMappings = eventMappings;
    this.isClearParameter = isClearParameter;
    this.isClearTemp = isClearTemp;
  }

  public boolean isClearParameter() {
    return isClearParameter;
  }

  public boolean isClearTemp() {
    return isClearTemp;
  }

  public String getScreen() {
    return screen;
  }

  public String getUrl() {
    return url;
  }

  public String getWebActionClass() {
    return webActionClass;
  }

  public List getEventMappings() {
    return eventMappings;
  }

  public String toString() {
    StringBuffer result = new StringBuffer("");
    result.append("url:").append(url).append("\tscreen:").append(screen)
        .append("\tweb action class:").append(webActionClass)
        .append("\tevent:").append(eventMappings);
    return result.toString();
  }
}
