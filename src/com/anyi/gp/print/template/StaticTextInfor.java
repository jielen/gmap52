package com.anyi.gp.print.template;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author   leidaohong
 */
public class StaticTextInfor extends TextElement {

  private String position;

  public StaticTextInfor() {
  }

  public StaticTextInfor(Node node) {
    super(node);
    NodeList nodes = ((Element) node).getElementsByTagName("position");
    for (int i = 0, j = nodes.getLength(); i < j; i++) {
      Node ele = nodes.item(i);
      String value = ele.getFirstChild().getNodeValue();
      setPosition(value);
    }
  }

  /**
 * @return   Returns the position.
 * @uml.property   name="position"
 */
public String getPosition() {
	return position;
}

  /**
 * @param position   The position to set.
 * @uml.property   name="position"
 */
public void setPosition(String position) {
	this.position = position;
}

}
