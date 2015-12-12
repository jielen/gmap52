package com.anyi.gp.sso;

public class AuthenticationFailedException extends Exception {

  private static final long serialVersionUID = 7512038439894384857L;

  private String msg = null;

  public AuthenticationFailedException() {
    super();
    msg = "AuthenticationFailedException";
  }

  public AuthenticationFailedException(String s) {
    super(s);
    msg = s;
  }

  public String toString() {
    return msg;
  }
}
