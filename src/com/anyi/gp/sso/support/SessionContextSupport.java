package com.anyi.gp.sso.support;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.anyi.gp.domain.User;
import com.anyi.gp.sso.SessionContext;

public class SessionContextSupport implements SessionContext, Serializable {
  
  private static final long serialVersionUID = -5121169416070168L;

  private User ui = null;
  
  private Map properties = new HashMap();
  
  public SessionContextSupport(User ui){
    this.ui = ui;
  }
  
  public String get(String key){
    return (String)properties.get(key);
  }

  public User getCurrentUser() {
    return ui;
  }
  
  public void put(String key,String value){
    properties.put(key, value);
  }
  
  public int getPropertiesSize(){
    return properties.size();
  }
  
  public Set getAllPropertyNames() {
  	return properties.keySet();
  }
  
  public Map getSessionMap(){
    return properties;
  }
}
