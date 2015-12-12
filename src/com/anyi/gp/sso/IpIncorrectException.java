package com.anyi.gp.sso;

public class IpIncorrectException extends Exception{

  private static final long serialVersionUID = 7727938143673970882L;

  public IpIncorrectException() {
    super();
  }

  public IpIncorrectException(String arg0, Throwable arg1) {
    super(arg0, arg1);
  }

  public IpIncorrectException(String arg0) {
    super(arg0);
  }

  public IpIncorrectException(Throwable arg0) {
    super(arg0);
  }
}
