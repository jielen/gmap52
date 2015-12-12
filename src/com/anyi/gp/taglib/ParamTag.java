package com.anyi.gp.taglib;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

import java.lang.reflect.Field;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTag;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.Tag;

/**
 * @author   leidaohong
 */
public class ParamTag extends BodyTagSupport {
  private String name;

  private String value = null;

  public ParamTag() {
  }

  public int doStartTag() throws JspException {
    return BodyTag.EVAL_BODY_BUFFERED;
  }

  public int doEndTag() throws JspException {
    try {
      Tag voParent = this.getParent();
      if (voParent == null)
        throw new JspException("param 标记只能作为子标记出现;");
      if (this.value == null) {
        BodyContent bc = this.getBodyContent();
        if (bc != null)
          this.setValue(bc.getString());
      }
      this.setAttribute(voParent, this.name, this.value);
      return Tag.EVAL_PAGE;
    } catch (Exception e) {
      throw new JspException(e.getMessage());
    }
  }

  private void setAttribute(Object object, String name, Object value)
      throws JspException {
    if (object == null)
      return;
    if (name == null)
      throw new JspException("param 标记中属性: name 不能为空;");

    Field voField = null;
    try {
      voField = ((Class) object).getField(name);
    } catch (NoSuchFieldException e) {
      String vsMsg = "没有发现参数: " + name + ";\n";
      throw new JspException(vsMsg + e.getMessage());
    } catch (SecurityException e) {
      String vsMsg = "您不允许访问属性: " + name + ";\n";
      throw new JspException(vsMsg + e.getMessage());
    }

    try {
      voField.set(object, value);
    } catch (IllegalAccessException e) {
      String vsMsg = name + " 属性不允许外部访问;\n";
      throw new JspException(vsMsg + e.getMessage());
    } catch (IllegalArgumentException e) {
      String vsMsg = name + " 属性的参数值不合法;\n";
      throw new JspException(vsMsg + e.getMessage());
    }
  }

  /**
 * @param name   The name to set.
 * @uml.property   name="name"
 */
public void setName(String name) {
	this.name = name;
}

  /**
 * @param value   The value to set.
 * @uml.property   name="value"
 */
public void setValue(String value) {
	this.value = value;
}
}
