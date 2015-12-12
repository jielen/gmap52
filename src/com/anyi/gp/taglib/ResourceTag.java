package com.anyi.gp.taglib;

import java.io.Writer;

import javax.servlet.jsp.JspException;

import org.apache.log4j.Logger;
import org.w3c.dom.Node;

import com.anyi.gp.pub.LangResource;
import com.anyi.gp.util.XMLTools;

/**
 * @author   leidaohong
 */
public class ResourceTag extends BaseTag implements ITag {
  private final static Logger log = Logger.getLogger(ResourceTag.class);

  private final static String TYPE_LANG_TRANS = "langtrans";

  private String lang = "C";

  private String type = TYPE_LANG_TRANS;

  private String code = "";

  // 以上进入 taglib;

  public ResourceTag() {
    this.release();
  }

  public void release() {
    super.release();

    this.lang = "C";
    this.type = "";
    this.code = "";
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
      String msg= "\nResourceTag.endX():\n"+ e.getMessage();
      log.debug(msg);
      throw new JspException(msg);
    }
    return EVAL_PAGE;
  }

  public void setTagAttributes(Node tagNode){
    this.setLang(XMLTools.getNodeAttr(tagNode, "lang", this.lang));
    this.setType(XMLTools.getNodeAttr(tagNode, "type", this.type));
    this.setCode(XMLTools.getNodeAttr(tagNode, "code", this.code));
  }

  public String getBodytext() {
    return "";
  }
  public void setBodytext(String text){
  }

  public void init(){
  }

  public String make(){
    String transCode= getCode();
    return LangResource.getInstance().getLang(transCode);
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
 * @return   Returns the code.
 * @uml.property   name="code"
 */
  public String getCode() {
    return code;
  }

  /**
 * @param code   The code to set.
 * @uml.property   name="code"
 */
  public void setCode(String code) {
    this.code = code;
  }

  /**
 * @return   Returns the lang.
 * @uml.property   name="lang"
 */
  public String getLang() {
    return lang;
  }

  /**
 * @param lang   The lang to set.
 * @uml.property   name="lang"
 */
  public void setLang(String lang) {
    this.lang = lang;
  }

  /**
 * @return   Returns the type.
 * @uml.property   name="type"
 */
  public String getType() {
    return type;
  }

  /**
 * @param type   The type to set.
 * @uml.property   name="type"
 */
  public void setType(String type) {
    this.type = type;
  }
}
