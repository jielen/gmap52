package com.anyi.gp.pub;

/**
 * @author   leidaohong
 */
public class BoolMessage {
  private boolean success= false;
  private String message= "";
  
  public String vsBusiMsg;
  public String vsDigest;
  
  public BoolMessage(){
  }
  
  public BoolMessage(boolean success, String message){
    this.success= success;
    this.message= message;
  }
  
  /**
 * @return   Returns the message.
 * @uml.property   name="message"
 */
  public String getMessage() {
    return message;
  }
  /**
 * @param message   The message to set.
 * @uml.property   name="message"
 */
  public void setMessage(String message) {
    this.message = message;
  }
  /**
 * @return   Returns the success.
 * @uml.property   name="success"
 */
  public boolean isSuccess() {
    return success;
  }
  /**
 * @param success   The success to set.
 * @uml.property   name="success"
 */
  public void setSuccess(boolean success) {
    this.success = success;
  }
  
}
