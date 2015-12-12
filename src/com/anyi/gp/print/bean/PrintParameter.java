package com.anyi.gp.print.bean;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author   zhangyw
 */
public class PrintParameter implements Serializable{

  /**
   * 
   */
  private static final long serialVersionUID = -8450302409664394293L;
  private Map parameter = new HashMap();

  public PrintParameter(){
  }
  
  public PrintParameter(Map parameter){
    this.parameter = parameter;
  }

  /**
 * @param parameter   HashMap
 * @uml.property   name="parameter"
 */
  public void setParameter(Map parameter){
    this.parameter = parameter;
  }

  /**
 * @return   HashMap
 * @uml.property   name="parameter"
 */
  public Map getParameter(){
    return this.parameter;
  }

  /**
   *
   * @param paramName String
   * @return String
   */
  public String getParameter(String paramName){
    if(paramName != null){
      return(String)this.parameter.get(paramName);
    }
    return null;
  }

  /**
   *
   * @param paramName String
   * @param paramValue String
   */
  public void addParameter(String paramName, String paramValue){
    if(paramName != null){
      this.parameter.put(paramName, paramValue);
    }
  }

  /**
   *
   * @param parameter PrintParameter
   */
  public void addAllParameter(PrintParameter parameter){
    if(parameter != null){
      this.parameter.putAll(parameter.getParameter());
    }
  }

  /**
   *
   */
  public void clear(){
    if(this.parameter != null){
      this.parameter.clear();
    }
  }
  
  /**
   * 
   * @param name
   */
  public void remove(String name){
    if(this.parameter != null){
      this.parameter.remove(name);
    }    
  }
  
  /**
   * 
   * @param name
   * @return
   */
  public Object get(String name){
    if(this.parameter != null){
      return this.parameter.get(name);
    } 
    return null;
  }
}
