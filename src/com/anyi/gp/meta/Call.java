package com.anyi.gp.meta;

public class Call {
  
  private String name = null;
  
  private boolean wrLog = false;
  
  public Call() {
  }

  /**
 * @return   Returns the name.
 * @uml.property   name="name"
 */
  public String getName() {
  	return name;
  }

  /**
 * @param name   The name to set.
 * @uml.property   name="name"
 */
  public void setName(String name) {
  	this.name = name;
  }
  
  public boolean isWrLog() {
    return wrLog;
  }

  public void setWrLog(boolean wrLog) {
    this.wrLog = wrLog;
  }

  public String toXML(){
  	return "<call name=\"" + name + "\"></call>\n";
  }
}