package com.anyi.gp.taglib;

import com.anyi.gp.taglib.components.DateBox;
import com.anyi.gp.taglib.components.TextBox;


public class DatetimeBoxTag extends DateBoxTag {

  private static final long serialVersionUID = -8372396881980398939L;

  protected TextBox makeO(){
    return new DateBox("DatetimeBox");
  }
}
