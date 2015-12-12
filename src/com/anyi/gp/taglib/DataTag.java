package com.anyi.gp.taglib;

/**
 * <p>Title: UFGOV A++ Platform Page Tag</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: UFGOV</p>
 * @author lijianwei
 * @version 1.0
 */
import java.io.Writer;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyContent;

import org.apache.log4j.Logger;
import org.w3c.dom.Node;

import com.anyi.gp.util.XMLTools;

/**
 * @author   leidaohong
 */
public class DataTag extends BaseTag implements ITag {
  private static final long serialVersionUID = 690063823590006333L;
  private static final Logger log = Logger.getLogger(DataTag.class);
  public static String TAG_NAME= "applus:data";

  private String bodytext= "";

  public void release(){
    super.release();
    this.bodytext = "";
  }

  public DataTag() {
  }

  public int doStartTag() throws JspException{
    return beginX(pageContext.getOut());
  }

  public int doEndTag() throws JspException{
    return endX(pageContext.getOut());
  }

  public int beginX(Writer out) throws JspException {
    return EVAL_BODY_BUFFERED;
  }

  public int endX(Writer out) throws JspException{
    try{
      init();
      out.write(make());
    }catch(Exception e){
      String msg= "\nDataTag.endX():\n"+ e.getMessage();
      log.debug(msg);
      throw new JspException(msg);
    }
    return EVAL_PAGE;
  }

  public void init(){
    BodyContent bc = this.getBodyContent();
    if (bc != null) this.setBodytext(bc.getString());
  }

  public String make(){
    StringBuffer voBuf= new StringBuffer();
    Object tag = this.getParent();
    if (tag != null) {
      try {
        ((IChildTag) tag).setChildBodyText(TAG_NAME, this.getBodytext());
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    } else {
      try {
        voBuf.append("<xml id=\"" + this.getId()
            + "\" asynch=\"false\" encoding=\"GBK\">\n");
        voBuf.append(this.getBodytext());
        voBuf.append("</xml>\n");
        regObj();
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
    return voBuf.toString();
  }

  public void setTagAttributes(Node tagNode){
    this.setId(XMLTools.getNodeAttr(tagNode, "id", this.id));
  }

  public boolean isContainer(){
    return false;
  }
  public boolean isAllowChildren(){
    return false;
  }
  public boolean isAllowBody(){
    return true;
  }

  /**
 * @return   Returns the bodytext.
 * @uml.property   name="bodytext"
 */
  public String getBodytext() {
    return bodytext;
  }
  /**
 * @param bodytext   The bodytext to set.
 * @uml.property   name="bodytext"
 */
  public void setBodytext(String bodytext) {
    this.bodytext = bodytext;
  }
}
