package com.anyi.gp.taglib;

import com.anyi.gp.taglib.components.FileBox;
import com.anyi.gp.taglib.components.TextBox;

public class FileBoxTag extends TextBoxTag {

  private static final long serialVersionUID = -685200302160781705L;

  private FileBox o = null;

  protected TextBox makeO(){
    return new FileBox();
  }
  
  public void setInterfaceclass(String interfaceclass) {
    this.o.setInterfaceclass(interfaceclass);
  }

  public void setIsattachbtnvisible(boolean isattachbtnvisible) {
    this.o.setIsattachbtnvisible(isattachbtnvisible);
  }

  public void setIsdelbtnvisible(boolean isdelbtnvisible) {
    this.o.setIsdelbtnvisible(isdelbtnvisible);
  }

  public void setIsopenbtnvisible(boolean isopenbtnvisible) {
    this.o.setIsopenbtnvisible(isopenbtnvisible);
  }

  public void setIsmultiattach(boolean ismultiattach) {
    this.o.setIsmultiattach(ismultiattach);
  }

  public void setInterfaceparams(String interfaceparams) {
    this.o.setInterfaceparams(interfaceparams);
  }

}
