// $Id: ITag.java,v 1.3 2008/09/27 03:13:14 wangfei Exp $
package com.anyi.gp.taglib;

import java.io.Writer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;

import org.w3c.dom.Node;

/**
 * HH: 这个接口是做什么用的，Tag 接口还不够用吗？ 2006-10-13
 */
public interface ITag extends Tag{
  public final static String TAG_INTERFACE_FIELD_PROP_MAP= "tag_interface_field_prop_map";
  public final static int TAG_INTERFACE_FIELD_INVISIBLE= 0;
  public final static int TAG_INTERFACE_FIELD_READONLY= 1;
  public final static int TAG_INTERFACE_FIELD_READWRITE= 2;


  public final static String TAG_INTERFACE_COMPO_NAME= "tag_interface_compo_name";
  public static final String INVALID_DEFAULT_VALUE= "{invalid_default_value_2005-09-30}";
  
  public final static String REQ_PAGE_TYPE = "__request_pageType";
  
  /**
   * ITag.TAG_OUTER_INTERFACE_FIELD_MAP：
   * Map: key=tablename; value= attributes document of fields of table;
   */
  public final static String TAG_OUTER_INTERFACE_FIELD_MAP= "tag_outer_interface_field_map_20060718";

  public int beginX(Writer out)throws JspException;
  public int endX(Writer out)throws JspException;
  public void init();
  public String getBodytext();
  public void setBodytext(String text);
  public void setTagAttributes(Node tagNode);
  public void setRequest(HttpServletRequest request);
  public HttpServletRequest getRequest();
  public boolean isContainer();
  public boolean isAllowChildren();
  public boolean isAllowBody();
}
