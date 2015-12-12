// $Id: BaseTag.java,v 1.2 2008/06/02 13:42:21 huangcb Exp $
package com.anyi.gp.taglib;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.log4j.Logger;

public class BaseTag extends BodyTagSupport {
  private static final long serialVersionUID = -1760358100184888318L;

  private final static Logger log = Logger.getLogger(BaseTag.class);
  public final static String WINDOW_MODAL= "winmodal";  //winmodal=1
  public final static String WINDOW_MODAL_0= "0";  //winmodal=0;表示非modal;
  public final static String WINDOW_MODAL_1= "1";  //winmodal=1;表示modal;
  public final static String END_BUF_IN_REQUEST= "endBufInRequest_20070122";
  public final static int CONTAINER_TAG_LEVEL= 1;
  public final static int COMMON_TAG_LEVEL= 5;
  public final static String SCRIPT_BEGIN= "<script language=\"javascript\">";
  public final static String SCRIPT_END= "</script>";
  
  private HttpServletRequest request = null;

  public BaseTag() {
    super();
  }

  /**
   * Tomcat 对自定义标签库的处理有问题，没有对每一次请求调用 release()，因此这里
   * 强制调用一下。
   * 【注意】可能影响子类的行为，仔细检查子类在 setPageContext 之前没有分配资源！
   * 参考：
   * http://java.sun.com/j2ee/1.4/docs/api/javax/servlet/jsp/tagext/Tag.html
   * [Bug 16001] - Tag.release() not invoked
   * http://www.mail-archive.com/tomcat-dev@jakarta.apache.org/msg39148.html
   */
  public void setPageContext(PageContext pc) {
    super.setPageContext(pc);
    release();
  }

  public void regObj() {
  }

  public Object getObj(String id) {
    return null;
  }

  public void setRequest(HttpServletRequest request) {
    this.request = request;
  }

  public HttpServletRequest getRequest() {
    if (this.request != null)
      return this.request;
    if (pageContext == null)
      return null;
    HttpServletRequest voReq = (HttpServletRequest) pageContext.getRequest();
    return voReq;
  }

  public String getPageName() {
    String page = "";
    try {
      String vsPath = getRequest().getServletPath();
      int viPos = vsPath.lastIndexOf("/");
      page = vsPath.substring(viPos + 1);
    } catch (Exception e) {
      String msg = "\nBaseTag.getPageName():\n获取页面名称时出错; requst object: "
        + getRequest() + "\n" + e.getMessage();
      log.error(msg);
      throw new RuntimeException(msg);
    }
    return page;
  }
  
  public StringBuffer getEndBufInRequest(int level){
    Map pool= getEndPoolInRequest();
    StringBuffer buf= (StringBuffer)pool.get(String.valueOf(level));
    if (buf== null){
      buf= new StringBuffer();
      pool.put(String.valueOf(level), buf);
    }
    return buf;
  }
  
  public Map getEndPoolInRequest(){
    if (getRequest()== null) return null;
    Map pool= (Map)getRequest().getAttribute(END_BUF_IN_REQUEST);
    if (pool== null){
      pool= new HashMap();
      getRequest().setAttribute(END_BUF_IN_REQUEST, pool);
    }
    return pool;
  }
  
  public void clearEndBufInRequest(){
    if (getRequest()== null) return;
    getRequest().setAttribute(END_BUF_IN_REQUEST, null);
  }
  
  public void addEndBufInRequest(String text, int level){
    StringBuffer buf= getEndBufInRequest(level);
    if (buf== null) return;
    buf.append(text);
  }
  
  public String getWindowModal(){
    if (getRequest()== null) return WINDOW_MODAL_0;
    String modal= getRequest().getParameter(WINDOW_MODAL);
    if (modal== null) return modal;
    return modal.trim();
  }
}
