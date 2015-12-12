package com.anyi.gp.taglib;

import com.anyi.gp.taglib.components.DateBox;
import com.anyi.gp.taglib.components.TextBox;

public class DateBoxTag extends TextBoxTag {

  private static final long serialVersionUID = 2060970277516928701L;

  protected TextBox makeO(){
    return new DateBox("DateBox");
  }

  public void setPicktype(String picktype) {
    ((DateBox)this.getO()).setPicktype(picktype);
  }
}
