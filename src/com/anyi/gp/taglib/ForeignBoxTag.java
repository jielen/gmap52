package com.anyi.gp.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyContent;

import com.anyi.gp.taglib.components.ForeignBox;
import com.anyi.gp.taglib.components.Page;
import com.anyi.gp.taglib.components.TextBox;

public class ForeignBoxTag extends TextBoxTag{

  private static final long serialVersionUID = -4452207124922568905L;
 
	public ForeignBoxTag(){
	}

	protected TextBox makeO(){
	  return new ForeignBox();
	}
	
	public int doEndTag() throws JspException {
    Page.addEditBox(pageContext.getRequest(), (ForeignBox)getO());
    BodyContent bc = this.getBodyContent();
    if(bc != null){
      ((ForeignBox)getO()).setBodyText(bc.getString());
    }
    try {      
      ((ForeignBox)getO()).writeHTML(pageContext.getOut());
    } catch (IOException e) {
      throw new JspException(e);
    }
    this.setO(this.makeO());
    return EVAL_PAGE;
  }

  public void setIsstrictinput(boolean isstrictinput) {
    ((ForeignBox)getO()).setIsstrictinput(isstrictinput);
  }

  public void setIsclearinput(boolean isclearinput) {
    ((ForeignBox)getO()).setIsclearinput(isclearinput);
  }

  public void setIsautofilldata(boolean isautofilldata) {
    ((ForeignBox)getO()).setIsautofilldata(isautofilldata);
  }

  public void setNoclearfields(String noclearfields) {
    ((ForeignBox)getO()).setNoclearfields(noclearfields);
  }

  public void setSearchedfields(String searchedfields) {
    ((ForeignBox)getO()).setSearchedfields(searchedfields);
  }

  public void setDefsearchtext(String defsearchtext) {
    ((ForeignBox)getO()).setDefsearchtext(defsearchtext);
  }

  public void setIsfuzzymatch(boolean isfuzzymatch) {
    ((ForeignBox)getO()).setIsfuzzymatch(isfuzzymatch);
  }

  public void setIsallcheckvisible(boolean isallcheckvisible) {
    ((ForeignBox)getO()).setIsallcheckvisible(isallcheckvisible);
  }

  public void setIsallowrollback(boolean isallowrollback) {
    ((ForeignBox)getO()).setIsallowrollback(isallowrollback);
  }

  public void setSqlid(String sqlid) {
    ((ForeignBox)getO()).setSqlid(sqlid);
  }

  public void setCondition(String condition) {
    ((ForeignBox)getO()).setCondition(condition);
  }

  public void setIstreeview(boolean istreeview) {
    ((ForeignBox)getO()).setIstreeview(istreeview);
  }

  public void setUsercond(String usercond) {
    ((ForeignBox)getO()).setUsercond(usercond);
  }

  public void setIsboxconddisabled(boolean isboxconddisabled) {
    ((ForeignBox)getO()).setIsboxconddisabled(isboxconddisabled);
  }

  public void setBodyText(String text) {
    ((ForeignBox)getO()).setBodytext(text);
  }
  
  public void setIsmultisel(boolean isMultiSel){
    ((ForeignBox)getO()).setIsMultiSel(isMultiSel);
  }
}
