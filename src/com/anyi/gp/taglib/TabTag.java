package com.anyi.gp.taglib;

import java.io.Writer;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyContent;

import org.w3c.dom.Node;

import com.anyi.gp.util.XMLTools;

public class TabTag extends BaseTag implements ITag {

  private static final long serialVersionUID = -6821243485453160239L;

  private String id = "";

  private String caption = "";

  private boolean isselected = false;

  private String cssclass = "";

  private String style = "";

  // 以上进入标记;

  private String bodytext = "";

  public void release() {
    super.release();

    this.id = "";
    this.caption = "";
    this.isselected = false;
    this.cssclass = "";
    this.style = "";

    this.bodytext = "";
  }

  public TabTag() {
  }

  public int doStartTag() throws JspException {
    return beginX(pageContext.getOut());
  }

  public int doEndTag() throws JspException {
    return endX(pageContext.getOut());
  }

  public int beginX(Writer out) throws JspException {
    return EVAL_BODY_BUFFERED;
  }

  public int endX(Writer out) throws JspException {
    init();
    TabstripTag tabstripTag = (TabstripTag) this.getParent();
    if (tabstripTag != null) {
      tabstripTag.addTab(this);
    }
    return EVAL_PAGE;
  }

  public void init() {
    BodyContent bc = this.getBodyContent();
    if (bc != null)
      this.setBodytext(bc.getString());
  }

  /**
   * @return   Returns the id.
   * @uml.property   name="id"
   */
  public String getId() {
    return id;
  }

  /**
   * @param id   The id to set.
   * @uml.property   name="id"
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * @return   Returns the caption.
   * @uml.property   name="caption"
   */
  public String getCaption() {
    return caption;
  }

  /**
   * @param caption   The caption to set.
   * @uml.property   name="caption"
   */
  public void setCaption(String caption) {
    this.caption = caption;
  }

  /**
   * @return   Returns the isselected.
   * @uml.property   name="isselected"
   */
  public boolean isIsselected() {
    return isselected;
  }

  /**
   * @param isselected   The isselected to set.
   * @uml.property   name="isselected"
   */
  public void setIsselected(boolean isselected) {
    this.isselected = isselected;
  }

  /**
   * @return   Returns the cssclass.
   * @uml.property   name="cssclass"
   */
  public String getCssclass() {
    return cssclass;
  }

  /**
   * @param cssclass   The cssclass to set.
   * @uml.property   name="cssclass"
   */
  public void setCssclass(String cssclass) {
    this.cssclass = cssclass;
  }

  /**
   * @return   Returns the style.
   * @uml.property   name="style"
   */
  public String getStyle() {
    return style;
  }

  /**
   * @param style   The style to set.
   * @uml.property   name="style"
   */
  public void setStyle(String style) {
    this.style = style;
  }

  /**
   * <applus:area> 接口方法的实现;
   */
  public void setTagAttributes(Node tagNode) {
    this.setId(XMLTools.getNodeAttr(tagNode, "id", this.id));
    this.setCaption(XMLTools.getNodeAttr(tagNode, "caption", this.caption));
    this.setIsselected(Boolean.valueOf(
      XMLTools.getNodeAttr(tagNode, "isselected", "" + this.isselected))
      .booleanValue());
    this.setCssclass(XMLTools.getNodeAttr(tagNode, "cssclass", this.cssclass));
    this.setStyle(XMLTools.getNodeAttr(tagNode, "style", this.style));
  }

  public boolean isContainer() {
    return true;
  }

  public boolean isAllowChildren() {
    return true;
  }

  public boolean isAllowBody() {
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
