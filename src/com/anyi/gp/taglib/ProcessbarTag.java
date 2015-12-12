package com.anyi.gp.taglib;


import java.io.Writer;

import javax.servlet.jsp.JspException;

import org.apache.log4j.Logger;
import org.w3c.dom.Node;

import com.anyi.gp.Pub;
import com.anyi.gp.util.XMLTools;

public class ProcessbarTag extends BaseTag implements ITag{
  private final static Logger log = Logger.getLogger(TextBoxTag.class);

  private String id = "";

  private double max = 99;

  private double min = 0;

  private double value = 0;

  private boolean isautorun = false;

  private String cssclass = "";

  private String style = "";

  private String oninit = "";

  public ProcessbarTag() {
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
      regObj();
      out.write(makeJS());
    }catch(Exception e){
      String msg= "\nProcessbarTag.endX():\n"+ e.getMessage();
      log.debug(msg);
      throw new JspException(msg);
    }
    return EVAL_PAGE;
  }

  public void setTagAttributes(Node tagNode){
    this.setId(XMLTools.getNodeAttr(tagNode, "id", this.id));
  }

  public String getBodytext() {
    return "";
  }
  public void setBodytext(String text){
  }

  public void init(){
  }

  private String getPageInitMethod(){
    return "_Procesbar_"+ getId();
  }

  private String makeJS() {
    String vsElementID = "ele_" + Pub.getUID();
    String vsScriptID = "Script_" + Pub.getUID();
    StringBuffer vsbStream = new StringBuffer();
    vsbStream.append("\n<div id=\"" + vsElementID + "\"></div>\n");
    vsbStream.append("<script id=\"" + vsScriptID
        + "\" language=\"javascript\">\n");
    vsbStream.append("function " + getPageInitMethod()+ "(){\n");

    vsbStream.append("var voProcess = new Search();\n");
    vsbStream.append("voProcess.nMax= \"" + this.getMax() + "\";\n");
    vsbStream.append("voProcess.nMin= " + this.getMin() + ";\n");
    vsbStream.append("voProcess.nValue= \"" + this.getValue() + "\";\n");

    vsbStream.append("voProcess.makeK(" + vsElementID + ");\n");
    vsbStream.append("voProcess.init();\n");
    vsbStream.append("voProcess.resize();\n");

    vsbStream.append("voProcess.setClass(\"" + this.getCssclass() + "\");\n");
    vsbStream.append("voProcess.setStyle(\"" + this.getStyle() + "\");\n");

    // 向 PageX 中注册 Processbar 对象;
    vsbStream
        .append("PageX.regCtrlObj(\"" + this.getId() + "\", voProcess);\n");
    vsbStream.append("var me = voProcess;\n");
    vsbStream.append(this.getOninit() + ";\n");
    vsbStream.append("me=null;\n");
    vsbStream.append("voProcess=null;\n");
    vsbStream.append(vsScriptID + ".removeNode(true);\n");

    vsbStream.append("if (" + this.isIsautorun() + "){\n");
    vsbStream.append("voProcess.autoRun();\n");
    vsbStream.append("}\n");
    vsbStream.append("}\n");
    vsbStream.append("PageX.regPageInitMethod(" + getPageInitMethod()
        + ");\n");
    vsbStream.append(getPageInitMethod() + "=null;\n");
    vsbStream.append("</script>\n");
    return vsbStream.toString();
  }

  public boolean isContainer(){
    return false;
  }
  public boolean isAllowChildren(){
    return false;
  }
  public boolean isAllowBody(){
    return false;
  }

  /**
 * @param id   The id to set.
 * @uml.property   name="id"
 */
public void setId(String id) {
	this.id = id;
}

  /**
 * @return   Returns the id.
 * @uml.property   name="id"
 */
public String getId() {
	return id;
}

  /**
 * @param cssclass   The cssclass to set.
 * @uml.property   name="cssclass"
 */
public void setCssclass(String cssclass) {
	this.cssclass = cssclass;
}

  /**
 * @return   Returns the cssclass.
 * @uml.property   name="cssclass"
 */
public String getCssclass() {
	return cssclass;
}

  /**
 * @param style   The style to set.
 * @uml.property   name="style"
 */
public void setStyle(String style) {
	this.style = style;
}

  /**
 * @return   Returns the style.
 * @uml.property   name="style"
 */
public String getStyle() {
	return style;
}

  /**
 * @param oninit   The oninit to set.
 * @uml.property   name="oninit"
 */
public void setOninit(String oninit) {
	this.oninit = oninit;
}

  /**
 * @return   Returns the oninit.
 * @uml.property   name="oninit"
 */
public String getOninit() {
	return oninit;
}

  /**
 * @return   Returns the max.
 * @uml.property   name="max"
 */
public double getMax() {
	return max;
}

  /**
 * @param max   The max to set.
 * @uml.property   name="max"
 */
public void setMax(double max) {
	this.max = max;
}

  /**
 * @return   Returns the min.
 * @uml.property   name="min"
 */
public double getMin() {
	return min;
}

  /**
 * @param min   The min to set.
 * @uml.property   name="min"
 */
public void setMin(double min) {
	this.min = min;
}

  /**
 * @return   Returns the value.
 * @uml.property   name="value"
 */
public double getValue() {
	return value;
}

  /**
 * @param value   The value to set.
 * @uml.property   name="value"
 */
public void setValue(double value) {
	this.value = value;
}

  /**
 * @return   Returns the isautorun.
 * @uml.property   name="isautorun"
 */
public boolean isIsautorun() {
	return isautorun;
}

  /**
 * @param isautorun   The isautorun to set.
 * @uml.property   name="isautorun"
 */
public void setIsautorun(boolean isautorun) {
	this.isautorun = isautorun;
}
}
