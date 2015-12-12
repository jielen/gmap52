/** $Id: Delta.java,v 1.2 2009/02/24 02:34:37 liuxiaoyong Exp $ */
package com.anyi.gp;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.anyi.gp.util.StringTools;
import com.anyi.gp.util.XMLTools;

/**
 * @author   leidaohong
 * @Title  Delta 数据包类 <p> Delta数据包是一个集合类,它对应业务中的多个业务对像如:发货单,凭证等,它是由多个TableData构成 </p>
 * @Date  2002.4.15
 */
public class Delta implements Serializable, List {
  private final static Logger log = Logger.getLogger(Delta.class);

  public Delta() {
  }

  public Delta(ResultSet rs) {
    parseDelta(rs);
  }
  
  public void parseDelta(List list){
	  
  }
  
  /**
   * 使用parseDelta()方法,增强使用的灵活性;
   * 在构造方法中写太多的实现代码,将这些代码写成一个独立的方法,
   * 然后在构造子中调用.leidh;20060325;
   * @param rs
   */
  public void parseDelta(ResultSet rs) {
    try {
      ResultSetMetaData rsmd = rs.getMetaData();
      int columnCount = rsmd.getColumnCount();
      String[] columnNames = new String[columnCount];
      String[] columnTypes = new String[columnCount];
      for(int i = 0; i < columnCount; i++){
        columnNames[i] = rsmd.getColumnName(i + 1);
        columnTypes[i] = rsmd.getColumnTypeName(i + 1);
      }
      while (rs.next()) {
        String tableName = rsmd.getTableName(1);
        TableData tableData = new TableData(tableName);
        for (int i = 0; i < columnCount; i++) { // 处理每一列
          String columnName = columnNames[i];
          String columnType = columnTypes[i];
          String columnValue = rs.getString(columnName);
          if (columnType.equalsIgnoreCase("char")
            || columnType.equalsIgnoreCase("varchar")) {
            if (columnValue != null) {
              tableData.setField(columnName, columnValue);
            } else {
              tableData.setField(columnName, "");
            }
          } else if (columnType.equalsIgnoreCase("timestamp")
            || columnType.equalsIgnoreCase("date")) {
            if (columnValue != null) {
              tableData.setField(columnName, columnValue.substring(0, 10));
            } else {
              tableData.setField(columnName, "");
            }
          } else if (columnType.equalsIgnoreCase("int")) {
            Integer intTmp = null;
            if (columnValue == null) {
              intTmp = Integer.valueOf("0");
            } else {
              intTmp = Integer.valueOf(columnValue);
            }
            tableData.setField(columnName, intTmp);
          } else if (columnType.equalsIgnoreCase("float")
            || columnType.equalsIgnoreCase("decimal")
            || columnType.equalsIgnoreCase("numeric")
            || columnType.equalsIgnoreCase("number")) {
            BigDecimal tmp = null;
            if (columnValue == null) {
              tmp = new BigDecimal("0");
            } else {
              tmp = new BigDecimal(columnValue);
            }
            tableData.setField(columnName, tmp);
          } else {
            if ((columnValue != null) && (columnValue.length() > 0)) {
              tableData.setField(columnName, columnValue);
            } else {
              tableData.setField(columnName, "");
            }
          }
        }
        add(tableData);
      }
    } catch (SQLException e) {
      throw new RuntimeException("从数据集构造Delta对象出错： " + e.toString());
    }
  }

  public Delta(List entitySet) {
    for (int i = 0; i < entitySet.size(); i++) {
      add(entitySet.get(i));
    }
  }

  /**
   * 构造一个Delta类
   *
   * @param entitySet
   *          XML格式的数据包
   */
  public Delta(String entitySet) {
    this(XMLTools.stringToDocument(entitySet));
  }

  /**
   * 构造一个Delta类
   *
   * @param entitySet
   *          Node格式的数据包
   */
  public Delta(Node entitySet) {
    Node NData;
    if (entitySet.getNodeType() == Node.DOCUMENT_NODE) {
      NData = ((Document) entitySet).getDocumentElement();
    } else {
      NData = entitySet;
    }
    NodeList NDataChildList = NData.getChildNodes();
    for (int i = 0; i < NDataChildList.getLength(); i++) {
      Node NDataChild = NDataChildList.item(i);
      if (NDataChild.getNodeType() == Node.ELEMENT_NODE) {
        createEntitySet(NDataChild);
      }
    }
  }

  /**
   * 解析平台4.0的tabledataxml数据格式为Delta对象内容.
   * leidh;20060425;
   * @param xml
   */
  public void parseXml4(String xml) {
    if (xml == null)
      return;
    Document doc = null;
    try {
      doc = XMLTools.stringToDocument(xml);
    } catch (Exception e) {
      String msg = "\nDelta.parseXml4():\n解析xml时出现异常;\n" + xml + "\n"
        + e.getClass().getName() + e.getMessage();
      log.debug(msg);
      throw new RuntimeException(e);
    }
    if (doc == null)
      return;
    parseXml4(doc.getDocumentElement());
  }

  /**
   * 解析平台4.0的tabledataxml数据格式为Delta对象内容.
   * leidh;20060425;
   * @param root documentElement;
   */
  public void parseXml4(Node root) {
    if (root == null)
      return;
    Node rowSet = null;
    this.setName(root.getNodeName());
    try {
      rowSet = XPathAPI.selectSingleNode(root, "rowset");
    } catch (TransformerException e) {
      String msg = "\nDelta.parseXml4():\n选取rowset结点时出现异常;\n"
        + e.getClass().getName() + "\n" + e.getMessage();
      log.debug(msg);
      throw new RuntimeException(e);
    }
    while (this.size() > 0) {
      this.remove(0);
    }
    if (rowSet == null)
      return;
    XMLTools.trimChildNodes(rowSet);
    for (int i = 0; i < rowSet.getChildNodes().getLength(); i++) {
      Node row = rowSet.getChildNodes().item(i);
      XMLTools.trimChildNodes(row);
      TableData tableData = new TableData(root.getNodeName());
      for (int j = 0; j < row.getChildNodes().getLength(); j++) {
        Node col = row.getChildNodes().item(j);
        tableData.setField(col.getNodeName(), XMLTools.getNodeText(col));
      }
      this.add(tableData);
    }
  }

  /**
   * @param entityNode
   *          创建TableData类.
   */
  private void createEntitySet(Node entityNode) {
    TableData entityData = new TableData((Element) entityNode);
    entitysList.add(entityData);
  }

  public int hashCode() {
    return entitysList.hashCode();
  }

  public boolean equals(Object o) {
    return entitysList.equals(o);
  }

  /**
   * @return 将Delta类转换成一个XML文件格式的字符串
   */
  public String toString() {
    try {
      StringWriter writer = new StringWriter();
      write(writer);
      return writer.toString();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void write(Writer writer) throws IOException {
    TableData entityDataTemp;
    writer.write("<delta>\n");
    for (int i = 0; i < entitysList.size(); i++) {
      entityDataTemp = (TableData) entitysList.get(i);
      writer.write("  ");
      writer.write(entityDataTemp.toString() + "\n");
      writer.write("\n");
    }
    writer.write("</delta>\n");
  }

  public String toXML() {
    StringBuffer s = new StringBuffer();
    String tagName = StringTools.getValidTagName(getName());
    s.append("<").append(tagName).append(">");
    for (Iterator iter = this.iterator(); iter.hasNext();) {
      s.append("\n");
      TableData tableData = (TableData) iter.next();
      s.append(tableData.toXML());
    }
    s.append("\n");
    s.append("</").append(tagName).append(">");
    return s.toString();
  }

  public List subList(int fromIndex, int toIndex) {
    return entitysList.subList(fromIndex, toIndex);
  }

  public ListIterator listIterator(int index) {
    return entitysList.listIterator(index);
  }

  public ListIterator listIterator() {
    return entitysList.listIterator();
  }

  public int lastIndexOf(Object o) {
    return entitysList.lastIndexOf(o);
  }

  public int indexOf(Object o) {
    return entitysList.indexOf(o);
  }

  public Object remove(int index) {
    return entitysList.remove(index);
  }

  public void add(int index, Object o) {
    if (o instanceof TableData) {
      entitysList.add(index, o);
    } else {
      throw new IllegalArgumentException("Delta类中执行add方法时,参数类型不为TableData");
    }
  }

  public boolean add(Object o) {
    if (o instanceof TableData) {
      return entitysList.add(o);
    }
    else if(o instanceof Map){
      return entitysList.add(new TableData((Map)o));
    }
    throw new IllegalArgumentException("Delta类中执行add方法时,参数类型不为TableData");
  }

  public Object set(int index, Object o) {
    if (o instanceof TableData) {
      return entitysList.set(index, o);
    }
    throw new IllegalArgumentException("Delta类中执行set方法时,参数类型不为TableData");
  }

  public Object get(int index) {
    return entitysList.get(index);
  }

  public void clear() {
    entitysList.clear();
  }

  public boolean retainAll(Collection c) {
    return entitysList.retainAll(c);
  }

  public boolean removeAll(Collection c) {
    return entitysList.removeAll(c);
  }

  public boolean addAll(int index, Collection c) {
    return entitysList.addAll(index, c);
  }

  public boolean addAll(Collection c) {
    return entitysList.addAll(c);
  }

  public boolean contains(Object o) {
    return entitysList.contains(o);
  }

  public boolean containsAll(Collection c) {
    return entitysList.containsAll(c);
  }

  public boolean remove(Object o) {
    return entitysList.remove(o);
  }

  public Object[] toArray(Object[] o) {
    return entitysList.toArray(o);
  }

  public Object[] toArray() {
    return entitysList.toArray();
  }

  public Iterator iterator() {
    return entitysList.iterator();
  }

  public boolean isEmpty() {
    return entitysList.isEmpty();
  }

  public int size() {
    return entitysList.size();
  }

  /**
   * @param format   The format to set.
   * @uml.property   name="format"
   */
  public void setFormat(TableData format) {
    this.format = format;
  }

  /**
   * @return   Returns the format.
   * @uml.property   name="format"
   */
  public TableData getFormat() {
    return this.format;
  }

  // --private Node NData;
  private List entitysList = new ArrayList();

  private TableData format;

  private String name;

  private int pageIndex = 0;

  private int pageSize = 0;

  private int fromRow = 0;

  private int rowCountOfDB = 0;

  private List kiloFieldList;

  private List dateFieldList;

  private String sqlid;
  
  private String condition;
  
  public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public String getSqlid() {
		return sqlid;
	}

	public void setSqlid(String sqlid) {
		this.sqlid = sqlid;
	}

	/**
   * 数据集名称
   * @uml.property   name="name"
   */
  public String getName() {
    return name;
  }

  /**
   * @param name   The name to set.
   * @uml.property   name="name"
   */
  public void setName(String tName) {
    this.name = tName;
  }

  /**
   * 当前页号，从 1 开始，如果为 0 表示没有记录
   * @uml.property   name="pageIndex"
   */
  public int getPageIndex() {
    if (0 == size()) {
      return 0;
    }
    return pageIndex;
  }

  /**
   * @param pageIndex   The pageIndex to set.
   * @uml.property   name="pageIndex"
   */
  public void setPageIndex(int pageIndex) {
    this.pageIndex = pageIndex;
  }

  /**
   * 页面大小，为 0 表示任意大，不分页
   * @uml.property   name="pageSize"
   */
  public int getPageSize() {
    return pageSize;
  }

  /**
   * @param pageSize   The pageSize to set.
   * @uml.property   name="pageSize"
   */
  public void setPageSize(int pageSize) {
    this.pageSize = pageSize;
  }

  /**
   * 开始行号，从 1 开始，如果为 0 表示没有记录
   * @uml.property   name="fromRow"
   */
  public int getFromRow() {
    if (0 == size()) {
      return 0;
    }
    return fromRow;
  }

  /**
   * @param fromRow   The fromRow to set.
   * @uml.property   name="fromRow"
   */
  public void setFromRow(int fromRow) {
    this.fromRow = fromRow;
  }

  /** 结束行号，包括该行，如果为 0 表示没有记录 */
  public int getToRow() {
    if (0 == size()) {
      return 0;
    }
    int toRow = fromRow + size() - 1;
    return toRow;
  }

  /**
   * 数据库中的记录总行数
   * @uml.property   name="rowCountOfDB"
   */
  public int getRowCountOfDB() {
    return rowCountOfDB;
  }

  /**
   * @param rowCountOfDB   The rowCountOfDB to set.
   * @uml.property   name="rowCountOfDB"
   */
  public void setRowCountOfDB(int rowCountOfDB) {
    this.rowCountOfDB = rowCountOfDB;
  }

  /**
   * @return   Returns the kiloFieldList.
   * @uml.property   name="kiloFieldList"
   */
  public List getKiloFieldList() {
    return kiloFieldList;
  }

  /**
   * @param kiloFieldList   The kiloFieldList to set.
   * @uml.property   name="kiloFieldList"
   */
  public void setKiloFieldList(List kiloFieldList) {
    this.kiloFieldList = kiloFieldList;
    for (Iterator iter = this.iterator(); iter.hasNext();) {
      ((TableData) iter.next()).setKiloFieldList(kiloFieldList);
    }
  }

  /**
   * @return   Returns the dateFieldList.
   * @uml.property   name="dateFieldList"
   */
  public List getDateFieldList() {
    return dateFieldList;
  }

  /**
   * @param dateFieldList   The dateFieldList to set.
   * @uml.property   name="dateFieldList"
   */
  public void setDateFieldList(List dateFieldList) {
    this.dateFieldList = dateFieldList;
    for (Iterator iter = this.iterator(); iter.hasNext();) {
      ((TableData) iter.next()).setDateFieldList(dateFieldList);
    }
  }
  
  /**
   * 获取entitysList
   * @return
   */
  public List getEntitysList(){
  	return entitysList;
  }
}
