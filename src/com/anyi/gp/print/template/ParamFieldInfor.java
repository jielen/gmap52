/*$Id: ParamFieldInfor.java,v 1.1 2008/02/22 09:09:33 liuxiaoyong Exp $ */
package com.anyi.gp.print.template;

/**
 * <p>
 * Title: 参数信息
 * </p>
 * <p>
 * Description: 描述页面上参数的详细信息
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company: UFGOV
 * </p>
 *
 * @author leejianwei
 * @version 1.0
 */
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author   leidaohong
 */
public class ParamFieldInfor extends TextElement {

  private boolean header;

  private boolean footer;

  public ParamFieldInfor() {
  }

  public ParamFieldInfor(Node node) {
    super(node);
    NodeList nodes = ((Element) node).getElementsByTagName("isHeader");
    for (int i = 0, j = nodes.getLength(); i < j; i++) {
      Node ele = nodes.item(i);
      String value = ele.getFirstChild().getNodeValue();
      if (value.equalsIgnoreCase("y")) {
        setHeader(true);
      } else {
        setFooter(true);
      }
    }
  }

  /**
 * @return   Returns the header.
 * @uml.property   name="header"
 */
public boolean isHeader() {
	return header;
}

  /**
 * @param header   The header to set.
 * @uml.property   name="header"
 */
public void setHeader(boolean header) {
	this.header = header;
}

  /**
 * @return   Returns the footer.
 * @uml.property   name="footer"
 */
public boolean isFooter() {
	return footer;
}

  /**
 * @param footer   The footer to set.
 * @uml.property   name="footer"
 */
public void setFooter(boolean footer) {
	this.footer = footer;
}

}
