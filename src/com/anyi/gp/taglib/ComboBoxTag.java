//$Id: ComboBoxTag.java,v 1.12 2008/06/02 13:42:21 huangcb Exp $
package com.anyi.gp.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyContent;

import com.anyi.gp.taglib.components.ComboBox;
import com.anyi.gp.taglib.components.Page;
import com.anyi.gp.taglib.components.TextBox;

public class ComboBoxTag extends TextBoxTag {
  
  private static final long serialVersionUID = -1451207501412541766L;

  protected TextBox makeO(){
    return new ComboBox();
  }
  
  public int doEndTag() throws JspException {
    Page.addEditBox(pageContext.getRequest(), getO());
    BodyContent bc = this.getBodyContent();
    if(bc != null){
      ((ComboBox)getO()).setBodyText(bc.getString());
    }
    try {      
      ((ComboBox)getO()).writeHTML(pageContext.getOut());
    } catch (IOException e) {
      throw new JspException(e);
    }
    this.setO(this.makeO());
    return EVAL_PAGE;
  }

  public void setUsercond(String usercond) {
    ((ComboBox)this.getO()).setUsercond(usercond);
  }

  public void setSqlid(String sqlid) {
    ((ComboBox)this.getO()).setSqlid(sqlid);
  }

  public void setCondition(String condition) {
    ((ComboBox)this.getO()).setCondition(condition);
  }

  public void setValuesetcode(String valuesetcode) {
    ((ComboBox)this.getO()).setValuesetcode(valuesetcode);
  }
}
