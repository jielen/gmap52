/**
 * 此类仅用于对 ActiveX 客户端提供数据支持;与此无关者莫入;
 * 用于 <object> 标记中加入参数 <param>;
 * leidh;20060822;
 */

package com.anyi.gp.taglib;

import java.io.Writer;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.jsp.JspException;

import org.apache.log4j.Logger;
import org.w3c.dom.Node;

import com.anyi.gp.Pub;
import com.anyi.gp.pub.SessionUtils;
import com.anyi.gp.util.StringTools;
import com.anyi.gp.util.XMLTools;

/**
 * @author   leidaohong
 */
public class AxParamTag extends BaseTag implements IVisibleTag{
  private static final Logger log = Logger.getLogger(AxParamTag.class);

  static final String PARAM_SERVER_PROTOCOL = "SERVER_PROTOCOL";
  static final String PARAM_SERVER_NAME = "SERVER_NAME";
  static final String PARAM_SERVER_PORT = "SERVER_PORT";
  static final String PARAM_WEB_ROOT = "WEB_ROOT";
  static final String PARAM_SESSION_ID = "SESSION_ID";
  static final String PARAM_SESSION_NAMES = "SESSION_NAMES";
  
  private String componame= "";
  private boolean issession = true;
  private boolean issessionid = true;
  private boolean isbaseurl = true;
  //以上进入 taglib;

  
  public AxParamTag() {
    initFieldsOfTag();
    initFieldsOfNotTag();
  }

  public void release() {
    super.release();
    initFieldsOfTag();
    initFieldsOfNotTag();
  }

  void initFieldsOfTag() {
    componame= "";
    issession = true;
    issessionid = true;
    isbaseurl = true;
}

  void initFieldsOfNotTag() {
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
    try {
      initFieldsOfNotTag();
      init();
      regObj();
      out.write(make());
      out.write(makeJS());
    } catch (Exception e) {
      String msg = "\nAxParamTag.endX():\n" + e.getMessage();
      log.debug(msg);
      e.printStackTrace();
      throw new JspException(msg);
    }
    return EVAL_PAGE;
  }

  public String getBodytext() {
    return "";
  }

  public void setBodytext(String text) {
  }

  public void init() {
    if (StringTools.isEmptyString(componame)){
      componame= (String)getRequest().getAttribute("componame");
    }
    if (StringTools.isEmptyString(componame)){
      componame= getRequest().getParameter("componame");
    }
  }
  
  /**
   * <applus:area> 接口方法的实现;
   */
  public void setTagAttributes(Node tagNode) {
    setComponame(XMLTools.getNodeAttr(tagNode, "componame", componame));
    setIssession(Boolean
        .valueOf(XMLTools.getNodeAttr(tagNode, "issession", ""
                + issession)).booleanValue());
    setIssessionid(Boolean
        .valueOf(XMLTools.getNodeAttr(tagNode, "issessionid", ""
                + issessionid)).booleanValue());
    setIsbaseurl(Boolean
        .valueOf(XMLTools.getNodeAttr(tagNode, "isbaseurl", ""
                + isbaseurl)).booleanValue());
  }

  public boolean isContainer() {
    return false;
  }

  public boolean isAllowChildren() {
    return true;
  }

  public boolean isAllowBody() {
    return false;
  }
  
  public String make() {
    StringBuffer buf= new StringBuffer();
    buf.append(makeServerInfo());
    buf.append(makeSessionId());
    buf.append(makeSessionVars());
    return buf.toString();
  }
  
  String makeSessionId(){
    if (!issessionid) return "";
    String sessionId= getRequest().getSession().getId();
    sessionId= StringTools.wrapChars(sessionId, 
        new int[]{2,14,28,32,43,56,68,78}, new int[]{11,23,36,45,58,66,72,88});
    return makeParamMark(PARAM_SESSION_ID, sessionId);
  }
  
  String makeServerInfo(){
    if (!isbaseurl) return ""; 
    StringBuffer buf= new StringBuffer();
    buf.append(makeParamMark(PARAM_SERVER_PROTOCOL, getRequest().getProtocol()));
    buf.append(makeParamMark(PARAM_SERVER_NAME, getRequest().getServerName()));
    buf.append(makeParamMark(PARAM_SERVER_PORT, ""+ getRequest().getServerPort()));
    buf.append(makeParamMark(PARAM_WEB_ROOT, Pub.getWebRoot(getRequest())));
    return buf.toString();
  }
  
  String makeSessionVars(){
    if (!issession) return ""; 
    Map varMap= SessionUtils.getAllSessionVariables(getRequest());//GlobalUtil.getAllGlobalVariable(getRequest().getSession(), componame);
    if (varMap== null) return "";
    StringBuffer buf= new StringBuffer();
    StringBuffer nameBuf= new StringBuffer();
    String name= null;
    for (Iterator iter= varMap.keySet().iterator(); iter.hasNext();){
      name= (String)iter.next();
      if (name== null) continue;
      if (name.equalsIgnoreCase("alias")) continue;

      if (varMap.get(name)==null) continue;
      
      buf.append(makeParamMark(name, varMap.get(name).toString()));
      nameBuf.append(name);
      nameBuf.append(",");
    }
    buf.append(makeParamMark(PARAM_SESSION_NAMES, nameBuf.toString()));
    return buf.toString();
  }

  String makeParamMark(String name, String value){
    StringBuffer buf= new StringBuffer();
    buf.append("<PARAM NAME=\"");
    buf.append(name);
    buf.append("\" VALUE=\"");
    buf.append(value);
    buf.append("\" >\n");
    return buf.toString();
  }

  public String makeJS() {
    return "";
  }
  
  String makeAttr() {
    return "";
  }

  /**
 * @return   Returns the isbaseurl.
 * @uml.property   name="isbaseurl"
 */
public boolean isIsbaseurl() {
	return isbaseurl;
}

  /**
 * @param isbaseurl   The isbaseurl to set.
 * @uml.property   name="isbaseurl"
 */
public void setIsbaseurl(boolean isbaseurl) {
	this.isbaseurl = isbaseurl;
}

  /**
 * @return   Returns the issession.
 * @uml.property   name="issession"
 */
public boolean isIssession() {
	return issession;
}

  /**
 * @param issession   The issession to set.
 * @uml.property   name="issession"
 */
public void setIssession(boolean issession) {
	this.issession = issession;
}

  /**
 * @return   Returns the issessionid.
 * @uml.property   name="issessionid"
 */
public boolean isIssessionid() {
	return issessionid;
}

  /**
 * @param issessionid   The issessionid to set.
 * @uml.property   name="issessionid"
 */
public void setIssessionid(boolean issessionid) {
	this.issessionid = issessionid;
}

  public boolean isIsvisible() {
    return true;
  }

  public void setIsvisible(boolean isvisible) {
  }

  /**
 * @return   Returns the componame.
 * @uml.property   name="componame"
 */
public String getComponame() {
	return componame;
}

  /**
 * @param componame   The componame to set.
 * @uml.property   name="componame"
 */
public void setComponame(String componame) {
	this.componame = componame;
}

}
