package com.anyi.gp.taglib;

public class PasswordBoxTag extends TextBoxTag {
  
  /**
   * 
   */
  private static final long serialVersionUID = 5223459983662058721L;

  public PasswordBoxTag() {
    super();
    this.getO().setBoxtype("PasswordBox");
    this.getO().setInputtype("password");
  }

}
