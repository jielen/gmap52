package com.anyi.gp;

public class BusinessException extends Exception {

  public BusinessException() {
    super();
  }

  public BusinessException(String arg0, Throwable arg1) {
    super(arg0, arg1);
  }

  public BusinessException(String arg0) {
    super(arg0);
  }

  public BusinessException(Throwable arg0) {
    super(arg0);
  }

}
