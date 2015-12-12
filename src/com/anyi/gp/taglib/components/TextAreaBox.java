package com.anyi.gp.taglib.components;

public class TextAreaBox extends TextBox {

  protected String makeInputBox() {
    StringBuffer voBuf = new StringBuffer();
    String vsAllowInput = (this.isIsallowinput()) ? "" : " readonly ";
    String vsReadOnly = (this.isIsreadonly()) ? " readonly " : "";
    voBuf.append("<textarea id='");
    voBuf.append(TextBox.DOMID_INPUT);
    voBuf.append("' ");
    voBuf.append("maxlength='");
    voBuf.append(this.getMaxlen());
    voBuf.append("' ");
    voBuf.append("class='clsTextAreaboxInput' ");
    voBuf.append("style='");
    voBuf.append(this.getStyle());
    voBuf.append("' ");
    voBuf.append("tabindex='1' ");
    voBuf.append(vsAllowInput);
    voBuf.append(vsReadOnly);
    voBuf.append(">");
    if (this.getValue() != null){
      voBuf.append(this.getValue());
    }
    voBuf.append("</textarea>\n");
    return voBuf.toString();
  }
  
}
