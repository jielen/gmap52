package com.anyi.gp.taglib.components;

public class FileBox extends TextBox {

  private String interfaceclass;

  private String interfaceparams;

  private boolean isattachbtnvisible = true;

  private boolean isdelbtnvisible = true;
  
  private boolean isopenbtnvisible = true;

  private boolean ismultiattach = false;

  public FileBox(){
    this.setBoxtype("FileBox");
  }
  
  public void init() {
    super.init();
    this.setMaxlen(MAX_LEN);
    this.setMinlen(MIN_LEN);
  }

  protected String makeOtherTD() {
    String vsReadOnly = (this.isIsreadonly()) ? " disabled " : "";

    StringBuffer voSBuf = new StringBuffer();
    voSBuf.append("<td id=\"FileBoxActionButtonDiv\" class='clsFileBoxActionTD' ");
    voSBuf.append("style=\"display:");
    voSBuf.append((isattachbtnvisible ? "" : "none"));
    voSBuf.append("\">");
    voSBuf.append("<input type='button' ");
    voSBuf.append("id=\"AttachButton\" ");
    voSBuf.append("value='¸½¼Ó' ");
    voSBuf.append("tabindex='' ");
    voSBuf.append("class='clsFileBoxAttachBtn' ");
    voSBuf.append(vsReadOnly + ">");
    voSBuf.append("<input type='button' id=\"DeleteButton\" value='É¾³ý' ");
    voSBuf.append("tabindex='' class='clsFileBoxDelBtn' style='display:none' ");
    voSBuf.append(vsReadOnly + ">");
    voSBuf.append("<input type='button' id=\"OpenButton\" value='´ò¿ª' tabindex='' ");
    voSBuf.append("class='clsFileBoxOpenBtn' style='display:"
      + (isopenbtnvisible ? "" : "none") + ";' >");
    voSBuf.append("</td>");
    return voSBuf.toString();
  }

  protected String makeAttr() {
    StringBuffer voBuf = new StringBuffer();
    voBuf.append(super.makeAttr());
    voBuf.append("interfaceclass='" + interfaceclass + "' ");
    voBuf.append("interfaceparams='" + interfaceparams + "' ");
    voBuf.append("isattachbtnvisible='" + isattachbtnvisible + "' ");
    voBuf.append("isdelbtnvisible='" + isdelbtnvisible + "' ");
    voBuf.append("isopenbtnvisible='" + isopenbtnvisible + "' ");
    voBuf.append("ismultiattach='" + ismultiattach + "' ");
    return voBuf.toString();
  }

  public String getInterfaceclass() {
    return interfaceclass;
  }

  public void setInterfaceclass(String interfaceclass) {
    this.interfaceclass = interfaceclass;
  }

  public boolean isIsattachbtnvisible() {
    return isattachbtnvisible;
  }

  public void setIsattachbtnvisible(boolean isattachbtnvisible) {
    this.isattachbtnvisible = isattachbtnvisible;
  }

  public boolean isIsdelbtnvisible() {
    return isdelbtnvisible;
  }

  public void setIsdelbtnvisible(boolean isdelbtnvisible) {
    this.isdelbtnvisible = isdelbtnvisible;
  }

  public boolean isIsopenbtnvisible() {
    return isopenbtnvisible;
  }

  public void setIsopenbtnvisible(boolean isopenbtnvisible) {
    this.isopenbtnvisible = isopenbtnvisible;
  }

  public boolean isIsmultiattach() {
    return ismultiattach;
  }

  public void setIsmultiattach(boolean ismultiattach) {
    this.ismultiattach = ismultiattach;
  }

  public String getInterfaceparams() {
    return interfaceparams;
  }

  public void setInterfaceparams(String interfaceparams) {
    this.interfaceparams = interfaceparams;
  }

}
