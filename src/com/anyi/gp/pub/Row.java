package com.anyi.gp.pub;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.anyi.gp.TableData;
import com.anyi.gp.util.XMLTools;

/**
 * @author   leidaohong
 */
public class Row {
  /**
   * Logger for this class
   */
  private static final Logger logger = Logger.getLogger(Row.class);

  public static final String INSERT_ACTION = "insert";

  public static final String DELETE_ACTION = "delete";

  public static final String UPDATE_ACTION = "update";

  private String action = null;

  private String rowId = null;

  private Map fields = null;

  public Set getAllFieldNames(){
    return fields.keySet();
  }

  public Row(Node node) {
    init(getAction(node), getRowId(node), getFieldNames(node), getValueMap(node,
      "oldvalue"), getValueMap(node, "newvalue"));
  }

  public Row(String action, String rowId, List fieldNames, List oldValue,
    List newValue) {
    init(action, rowId, fieldNames, oldValue, newValue);
  }

  /**
   * 从tableData中初始化Row
   * @param action
   * @param rowId
   * @param tableData
   */
  public Row(String action, String rowId, TableData tableData){
    init(action, rowId, tableData);
  }
  
  private void init(String action, String rowId, List fieldNames, List oldValue,
    List newValue) {
    this.rowId = rowId;
    this.fields = new HashMap();
    if (action.equalsIgnoreCase(INSERT_ACTION)) {
      this.action = INSERT_ACTION;
    } else if (action.equalsIgnoreCase(DELETE_ACTION)) {
      this.action = DELETE_ACTION;
    } else {
      this.action = UPDATE_ACTION;
    }
    for (int i = 0, j = fieldNames.size(); i < j; i++) {
      String oldV = null;
      String newV = null;
      if (this.action == INSERT_ACTION) {
        newV = (String) newValue.get(i);
      } else if (this.action == DELETE_ACTION) {
        oldV = (String) oldValue.get(i);
      } else {
        newV = (String) newValue.get(i);
        oldV = (String) oldValue.get(i);
      }
      fields.put(fieldNames.get(i), new RowDataPair(oldV, newV));
    }
  }

  private void init(String action, String rowId, List fieldNames, Map oldValue,
     Map newValue) {
     this.rowId = rowId;
     this.fields = new HashMap();
     if (action.equalsIgnoreCase(INSERT_ACTION)) {
       this.action = INSERT_ACTION;
     } else if (action.equalsIgnoreCase(DELETE_ACTION)) {
       this.action = DELETE_ACTION;
     } else {
       this.action = UPDATE_ACTION;
     }
     for (int i = 0, j = fieldNames.size(); i < j; i++) {
       String oldV = null;
       String newV = null;
       String fieldname= (String)fieldNames.get(i);
       if (this.action == INSERT_ACTION) {
         newV= (String)newValue.get(fieldname);
       } else if (this.action == DELETE_ACTION) {
         oldV= (String)oldValue.get(fieldname);
       } else {
         newV= (String)newValue.get(fieldname);
         oldV= (String)oldValue.get(fieldname);
         if (oldV== null) oldV= "";
       }
       fields.put(fieldNames.get(i), new RowDataPair(oldV, newV));
     }
   }

  private void init(String action, String rowId, TableData tableData){
    this.rowId = rowId;
    this.fields = new HashMap();
    if (action.equalsIgnoreCase(INSERT_ACTION)) {
      this.action = INSERT_ACTION;
    } else if (action.equalsIgnoreCase(DELETE_ACTION)) {
      this.action = DELETE_ACTION;
    } else {
      this.action = UPDATE_ACTION;
    }
    
    List fieldNames = tableData.getFieldNames();
    for(int i = 0; i < fieldNames.size(); i++){
      String oldV = null;
      String newV = null;
      String fieldname= (String)fieldNames.get(i);
      
      if (this.action == INSERT_ACTION) {
        newV= (String)tableData.getFieldValue(fieldname);
      } else if (this.action == DELETE_ACTION) {
        oldV= (String)tableData.getFieldValue(fieldname);
      } else {
        newV= (String)tableData.getFieldValue(fieldname);
        if (oldV== null) oldV= "";
      }
      fields.put(fieldNames.get(i), new RowDataPair(oldV, newV));
    }
  }
  
  private Map getValueMap(Node record, String oldOrNew) {
   Node oldOrNewNode = null;
   try {
     oldOrNewNode = XPathAPI.selectSingleNode(record, oldOrNew);
   } catch (TransformerException e) {
     logger.error("createRowTableDatas(String, Element) - Error ocurred when resolve RowManager data.", e);
     throw new RuntimeException(e);
   }
   Map result = new HashMap();
   if (oldOrNewNode == null) {
     return result;
   }
   NodeList valueNodes = oldOrNewNode.getChildNodes();
   String nodename= null, nodevalue= null;
   for (int i = 0, j = valueNodes.getLength(); i < j; i++) {
     if (valueNodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
       nodename= valueNodes.item(i).getNodeName();
       nodevalue= XMLTools.getNodeText(valueNodes.item(i));
       result.put(nodename, nodevalue);
     }
   }
   return result;
 }

  private List getFieldNames(Node recordNode) {
    List result = new ArrayList();
    NodeList fieldValues = null;
    try {
      if (getAction(recordNode).equalsIgnoreCase("delete")) {
        fieldValues = XPathAPI.selectSingleNode(recordNode, "oldvalue")
          .getChildNodes();
      } else {
        fieldValues = XPathAPI.selectSingleNode(recordNode, "newvalue")
          .getChildNodes();
      }
    } catch (TransformerException e) {
      logger.error(
        "getFieldNames(Node) - Error ocurred when get RowManager fields,node is "
          + XMLTools.nodeToString(recordNode), e);
      throw new RuntimeException(e);
    }
    for (int i = 0, j = fieldValues.getLength(); i < j; i++) {
      if (fieldValues.item(i).getNodeType() == Node.ELEMENT_NODE) {
        result.add(fieldValues.item(i).getNodeName());
      }
    }
    return result;
  }

  private String getAction(Node recordNode) {
    return XMLTools.getNodeAttr(recordNode, "action");
  }

  private String getRowId(Node recordNode) {
    return XMLTools.getNodeAttr(recordNode, "rowid");
  }

  /**
 * @return   Returns the action.
 * @uml.property   name="action"
 */
public String getAction() {
	return action;
}

  /**
 * @return   Returns the rowId.
 * @uml.property   name="rowId"
 */
public String getRowId() {
	return rowId;
}

  public String getOldString(String fieldName) {
    if (this.action == INSERT_ACTION) {
      return null;
    }
    return ((RowDataPair) fields.get(fieldName)).getOldValue();
  }

  public String getNewString(String fieldName) {
    if (this.action == DELETE_ACTION) {
      return null;
    }
    RowDataPair rowDataP = (RowDataPair) fields.get(fieldName);
    if (rowDataP == null)
      return null;
    return rowDataP.getNewValue();
  }

  public void setNewString(String fieldName, String value) {
    if (this.action == DELETE_ACTION) {
      return;
    }
    RowDataPair pair = (RowDataPair) fields.get(fieldName);
    if (pair == null) {
      pair = new RowDataPair("", "");
      fields.put(fieldName, pair);
    }
    pair.setNewValue(value);
  }

  public String getString(String fieldName, boolean isOld) {
    if (isOld) {
      return getOldString(fieldName);
    } else {
      return getNewString(fieldName);
    }
  }

  public String getWhateverValue(String fieldName) {
    if (this.action == DELETE_ACTION) {
      return getOldString(fieldName);
    } else {
      return getNewString(fieldName);
    }
  }

  public String toString() {
    StringBuffer result = new StringBuffer();
    result.append("<record action=\"");
    result.append(action);
    result.append("\" rowid=\"");
    result.append(rowId);
    result.append("\">");
    if (action == INSERT_ACTION) {
      result.append(newValueToString());
    } else if (action == DELETE_ACTION) {
      result.append(oldValueToString());
    } else {
      result.append(oldValueToString());
      result.append(newValueToString());
    }
    result.append("</record>");
    return result.toString();
  }

  private String newValueToString() {
    StringBuffer result = new StringBuffer();
    result.append("<newvalue>");
    for (Iterator iter = fields.keySet().iterator(); iter.hasNext();) {
      String fieldName = (String) iter.next();
      RowDataPair valuePair = (RowDataPair) fields.get(fieldName);
      result.append("<");
      result.append(fieldName);
      result.append(">");
      result.append(valuePair.getNewValue());
      result.append("</");
      result.append(fieldName);
      result.append(">");
    }
    result.append("</newvalue>");
    return result.toString();
  }

  private String oldValueToString() {
    StringBuffer result = new StringBuffer();
    result.append("<oldvalue>");
    for (Iterator iter = fields.keySet().iterator(); iter.hasNext();) {
      String fieldName = (String) iter.next();
      RowDataPair valuePair = (RowDataPair) fields.get(fieldName);
      result.append("<");
      result.append(fieldName);
      result.append(">");
      result.append(valuePair.getOldValue());
      result.append("</");
      result.append(fieldName);
      result.append(">");
    }
    result.append("</oldvalue>");
    return result.toString();
  }
}
