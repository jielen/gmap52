package com.anyi.gp.taglib;

import com.anyi.gp.taglib.components.NumericBox;
import com.anyi.gp.taglib.components.TextBox;

/**
 * @author   leidaohong
 */
public class NumericBoxTag extends TextBoxTag {
  private static final long serialVersionUID = -6893231904536442113L;

  public NumericBoxTag() {
    super();
  }
  
  protected TextBox makeO(){
    return new NumericBox();
  }

  public void setMaxvalue(double maxvalue) {
    ((NumericBox)this.getO()).setMaxvalue(maxvalue);
  }

  public void setMinvalue(double minvalue) {
    ((NumericBox)this.getO()).setMinvalue(minvalue);
  }

  public void setLength(int length) {
    ((NumericBox)this.getO()).setLength(length);
  }

  public void setScale(int scale) {
    ((NumericBox)this.getO()).setScale(scale);
  }

  public void setIskilo(boolean iskilo) {
    ((NumericBox)this.getO()).setIskilo(iskilo);
  }
}
