// $Id: TableData.java,v 1.48 2006/04/04 01:30:57 leidaohong Exp
package com.anyi.gp;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.anyi.gp.util.StringTools;

/**
 * 用于表示数据库记录表中的一条记录，概念上类似于一个映射表(Map)
 */
public class TableData implements Serializable {
  public static final String INSERT = "insert";

  public static final String DELETE = "delete";

  public static final String UPDATE = "update";

  /** 当前主表加上Detail表的表名、Detail组成的 Map */
  private Map values = new HashMap();

  /** 可见的数据列名 */
  private List fieldNames = new ArrayList();

  /** 真实的数据列名 */
  private List realFieldNames = new ArrayList();

  /** Detail表名组成的List */
  private List childTableNames = new ArrayList();

  /** 当前部件名，或表名 */
  private String name;

  /** 动作：新增/删除/修改 */
  private String action;

  private TableData oldValue;

  private List kiloFieldList;

  private List dateFieldList;

  public TableData() {
  }

  /**
   * @param entityName业务对像名
   */
  public TableData(String entityName) {
    this.name = entityName;
  }

  public TableData(Map map){
  	Set entrySet = map.entrySet();
  	Iterator itera = entrySet.iterator();
  	while(itera.hasNext()){
  		Map.Entry entry = (Map.Entry)itera.next();
  		setField(entry.getKey().toString(), entry.getValue());
  	}
  }
  
  /**
   * 从 XML 构造一个 TableData，XML的结构同 toString() 生成的内容
   * @param entity org.w3c.dom.Element 对象
   * @see #toString
   */
  public TableData(Element entity) {
    // 得到当前的部件名
    name = entity.getAttribute("name");
    if (name == null)
      throw new IllegalStateException("构造的Element中没有提供业务对像的名称");

    // 得到主表的所有字段名及值
    NodeList entityFields = entity.getChildNodes();
    Element entityField = null;
    for (int i = 0; i < entityFields.getLength(); i++) {
      if (entityFields.item(i).getNodeType() == Node.ELEMENT_NODE) {
        entityField = (Element) entityFields.item(i);
        if (entityField.getNodeName().equalsIgnoreCase("field"))
          setField(entityField.getAttribute("name"), entityField
            .getAttribute("value"));
      }
    }

    // 得到Detail表的内容
    NodeList entityDetails = entity.getChildNodes();
    for (int i = 0; i < entityDetails.getLength(); i++) {
      if (entityDetails.item(i).getNodeType() == Node.ELEMENT_NODE) {
        entityField = (Element) entityDetails.item(i);
        // 找到名为entity的Element
        if (entityField.getNodeName().equalsIgnoreCase("entity")) {
          // 把子Entity的各个属性都设置好，然后setField
          String szDetail = entityField.getAttribute("name");
          childTableNames.add(szDetail);
          List lsDetail = new ArrayList();
          NodeList rows = entityField.getChildNodes();
          // 依次得到每一个row的信息
          for (int m = 0; m < rows.getLength(); m++) {
            // 可以写一个Method嵌套调用
            NodeList tmp = rows.item(m).getChildNodes();
            for (int n = 0; n < tmp.getLength(); n++) {
              if (tmp.item(n).getNodeType() == Node.ELEMENT_NODE) {
                TableData enTmp = new TableData((Element) tmp.item(n));
                lsDetail.add(enTmp);
              }
            }
          }
          setField(szDetail, lsDetail);
        }
      }
    }
  }

  /** 取表名 */
  public String getName() {
    return name;
  }

  /** 设置表名 */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * 设置字段值，如果字段值为空值(null)，自动转换为空字符串("")
   */
  public void setField(String 字段名, Object 字段值) {
    if (字段名 == null)
      throw new IllegalArgumentException("给TableData设置字段值时，字段名为空值");
    if (fieldNames.indexOf(字段名) < 0) {
      fieldNames.add(字段名);
      realFieldNames.add(字段名);
    }
    if (字段值 == null)
      字段值 = "";
    values.put(字段名, 字段值);
  }

  /**
   * 取字段值
   * @param 字段名
   * @return 字段值
   */
  public Object getField(String 字段名) {
    Object result = values.get(字段名);
    return result;
  }

  /** getField 的别名 */
  public Object getFieldObject(String 字段名) {
    return getField(字段名);
  }

  /**
   * 取字段值，字段不存在时返回空字符串("")
   * @param 字段名
   * @return 字段值
   */
  public String getFieldValue(String 字段名) {
    Object value = getField(字段名);
    if (null != value)
      return value.toString();
    return "";
  }

  /**
   * 取字段值，字段不存在时返回0
   */
  public float getFieldFloat(String fieldName) {
    Object value = getField(fieldName);
    if (value != null)
      return Float.parseFloat((String) value);
    return 0.0F;
  }

  /**
   * 取字段值，字段不存在时返回0
   */
  public int getFieldInt(String fieldName) {
    Object value = getField(fieldName);
    if (value != null && !value.equals("")) {
      String s = value.toString();
      int i = s.indexOf(".");
      if (i > -1)
        s = s.substring(0, i);
      return Integer.parseInt(s);
    }
    return 0;
  }

  /**
   * 取字段名，不包括子表表名
   * @return 元素类型为 String ，字段名
   */
  public List getFieldNames() {
    List result = new ArrayList(fieldNames);
    result.removeAll(childTableNames);
    return result;
  }

  /**
   * 删除一个字段
   */
  public void deleteField(String fieldName) {
    if (childTableNames.contains(fieldName)) {
      throw new IllegalArgumentException("不能直接删除TableData中的子表");
    }
    fieldNames.remove(fieldName);
    realFieldNames.remove(fieldName);
    values.remove(fieldName);
  }

  /**
   * 删除主表或子表中的一个字段
   * @param tableName 表名
   * @param fieldName 字段名
   */
  public void deleteField(String tableName, String fieldName) {
    if (tableName.equalsIgnoreCase(name)) {
      deleteField(fieldName);
      return;
    }
    List detailNames = getChildTableNames();
    if (detailNames != null) {
      if (detailNames.contains(tableName)) {
        List details = getChildTables(tableName);
        for (Iterator iter = details.iterator(); iter.hasNext();) {
          TableData detail = (TableData) iter.next();
          detail.deleteField(fieldName);
        }
      } else {
        for (Iterator iter = detailNames.iterator(); iter.hasNext();) {
          List details = getChildTables((String) iter.next());
          for (Iterator iter2 = details.iterator(); iter.hasNext();) {
            TableData detail = (TableData) iter2.next();
            detail.deleteField(tableName, fieldName);
          }
        }
      }
    }
  }

  /**
   * 取子表表名
   * @return 元素类型为 String ，子表表名
   */
  public List getChildTableNames() {
    return childTableNames;
  }

  /**
   * @param 子表表名
   * @return 返回指定集合类型字段名的值集合.
   */
  public List getChildTables(String 子表表名) {
    ArrayList detailsList = (ArrayList) values.get(子表表名);
    if (null != detailsList)
      return detailsList;

    detailsList = new ArrayList();
    values.put(子表表名, detailsList);
    childTableNames.add(子表表名);
    return detailsList;
  }

  /** 添加子表数据 */
  public void setChildTable(String 子表表名, List 子表数据列表, int 没有用的参数) {
    /*-- HH: 2006-8-31 删除这一段
    if (values.get(子表表名) != null) {
      values.put(子表表名, 子表数据列表);
      return;
    }
    */
    values.put(子表表名, 子表数据列表);

    if (!childTableNames.contains(子表表名)) {
      childTableNames.add(子表表名);
      fieldNames.add(子表表名);
      realFieldNames.add(子表表名);
    }
    return;
  }

  /**
   * 设置可见数据列。用于不删除数据但隐藏部分数据列。
   * 【注意】这个方法对子表名不起作用
   * @param fieldNames 可见数据列，元素值为 String，不包含子表。其内容将被拷贝一份，
   *  因此在调用后可以修改 fieldNames 而不会产生副作用。
   */
  public void setFieldNames(List fieldNames) {
    this.fieldNames.clear();
    this.fieldNames.addAll(fieldNames);
    this.fieldNames.removeAll(childTableNames);
    this.fieldNames.addAll(childTableNames);
  }

  /**
   * 将TableData转换成一个XML格式的字符串，可以再通过 TableData(Element) 还原
   */
  public String toString() {
    StringBuffer s = new StringBuffer();
    s.append("<entity name=\"").append(name).append("\">\n");

    String fieldName = null;
    List detailsList = null;
    TableData detail = null;
    for (Iterator iter = fieldNames.iterator(); iter.hasNext();) {
      fieldName = (String) iter.next();
      if (!childTableNames.contains(fieldName)) {
        String v = StringTools.toXMLString(Tools_toString(values.get(fieldName)));
        v = v.replaceAll("\n", "&#10;");
        s.append("<field name=\"").append(fieldName).append("\" value=\"").append(v)
          .append("\" />\n");
      } else {
        s.append("<entity name=\"").append(fieldName).append("\">\n");
        detailsList = (List) values.get(fieldName);
        for (Iterator iter2 = detailsList.iterator(); iter2.hasNext();) {
          detail = (TableData) iter2.next();
          s.append("<row>\n");
          s.append(detail.toString());
          s.append("\n</row>\n");
        }
        s.append("</entity>\n");
      }
    }
    s.append("</entity>");
    return s.toString();
  }

  private static final DecimalFormat 小数点后6个0的格式 = new DecimalFormat("0.000000");

  /** 浮点数转换成字符串，小数点后先保留6位，在截去多余的0，至少保留1位小数 */
  private static String Double_to6DecString(double d) {
    String s = 小数点后6个0的格式.format(d);
    int n = s.lastIndexOf(".");
    if (-1 == n)
      return s;
    int i;
    for (i = s.length() - 1; i > n + 1; i--)
      if ('0' != s.charAt(i))
        break;
    return s.substring(0, i + 1);
  }

  private static String Tools_toString(Object o) {
    if (null == o)
      return "";
    if (o instanceof Double)
      return Double_to6DecString(((Number) o).doubleValue());
    return o.toString();
  }

  /**
   * 将 TableData 转换为 xml 格式，形式为:<pre>
   * ＜row＞
   * ＜C1＞value1＜/C1＞
   * ＜C2＞value2＜/C2＞
   * ＜/row＞
   * </pre>
   */
  public String toXML() {
    StringBuffer s = new StringBuffer();
    s.append("<row>\n");
    for (Iterator iter = fieldNames.iterator(); iter.hasNext();) {
      String fieldName = (String) iter.next();
      String tagName = StringTools.getValidTagName(fieldName);
      if (!childTableNames.contains(fieldName)) {
        s.append("<").append(tagName).append(">");
        Object value = values.get(fieldName);
        if (value instanceof Double) {
          value = Tools_toString(value);
        }
        if (value != null) {
          if (kiloFieldList != null && kiloFieldList.contains(fieldName)) {
            String srcValue= StringTools.replaceAll(String.valueOf(value), ",", "");
            value = StringTools.kiloStyle(srcValue);
          } else if (dateFieldList != null && dateFieldList.contains(fieldName)) {
            value = StringTools.formatDate((String) value);
          }
        }
        s.append(StringTools.toXMLString(String.valueOf(value).toString()));
        s.append("</").append(tagName).append(">\n");
      }
    }
    s.append("</row>\n");
    return s.toString();
  }

  /** 动作：新增/删除/修改 */
  public String getAction() {
    return action;
  }

  public void setAction(String action) {
    this.action = action;
  }

  /** 旧值，新增时为 null，删除时为 this */
  public TableData getOldValue() {
    if (INSERT.equals(action)) {
      return null;
    } else if (DELETE.equals(action)) {
      return this;
    }
    return oldValue;
  }

  /** 保存旧记录，将现有记录复制一份，目前不处理嵌套的子表 */
  public void saveOldValue() {
    this.oldValue = new TableData();
    for (Iterator i = getFieldNames().iterator(); i.hasNext();) {
      String fieldName = (String) i.next();
      if (!getChildTableNames().contains(fieldName)) {
        this.oldValue.setField(fieldName, this.getField(fieldName));
      }
    }
  }

  /** 新值，新增和修改时为 this，删除时为 null */
  public TableData getNewValue() {
    if (DELETE.equals(action)) {
      return null;
    }
    return this;
  }

  /**
   * 根据oldValue恢复TableData的数据
   */
  public void restoreOldValue() {
    if (this.oldValue == null)
      return;
    TableData oldTD = this.oldValue;
    for (Iterator i = oldTD.getFieldNames().iterator(); i.hasNext();) {
      String fieldName = (String) i.next();
      this.setField(fieldName, oldTD.getField(fieldName));
    }
  }

  /** 取用千分位表示的字段名 */
  public List getKiloFieldList() {
    return kiloFieldList;
  }

  /** 设置用千分位表示的字段名 */
  public void setKiloFieldList(List kiloFieldList) {
    this.kiloFieldList = kiloFieldList;
  }

  /** 取日期类型的字段名 */
  public List getDateFieldList() {
    return dateFieldList;
  }

  /** 设置日期类型的字段名 */
  public void setDateFieldList(List dateFieldList) {
    this.dateFieldList = dateFieldList;
  }
}
