package com.anyi.gp.sso;

import java.util.Map;
import java.util.Set;

import com.anyi.gp.domain.User;


public interface SessionContext {

  String get(String key);
  
  User getCurrentUser();
  
  Set getAllPropertyNames();
  
  public Map getSessionMap();
}
