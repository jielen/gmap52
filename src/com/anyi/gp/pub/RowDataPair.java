package com.anyi.gp.pub;

import com.anyi.gp.util.StringTools;

/**
 * @author   leidaohong
 */
public class RowDataPair {

  private String oldValue;

  private String newValue;
//需要处理特殊字符
  public RowDataPair(String oldValue, String newValue) {
    this.oldValue = StringTools.toXMLString(oldValue);
    this.newValue = StringTools.toXMLString(newValue);
  }

  /**
 * @return   Returns the newValue.
 * @uml.property   name="newValue"
 */
public String getNewValue() {
	return StringTools.toXMLString(newValue);
}

  /**
 * @param newValue   The newValue to set.
 * @uml.property   name="newValue"
 */
public void setNewValue(String newValue) {
	this.newValue = newValue;
}

  /**
 * @return   Returns the oldValue.
 * @uml.property   name="oldValue"
 */
public String getOldValue() {
	return StringTools.toXMLString(oldValue);
}

  /**
 * @param oldValue   The oldValue to set.
 * @uml.property   name="oldValue"
 */
public void setOldValue(String oldValue) {
	this.oldValue = oldValue;
}

}
