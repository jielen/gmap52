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
 * ���ڱ�ʾ���ݿ��¼���е�һ����¼��������������һ��ӳ���(Map)
 */
public class TableData implements Serializable {
  public static final String INSERT = "insert";

  public static final String DELETE = "delete";

  public static final String UPDATE = "update";

  /** ��ǰ�������Detail��ı�����Detail��ɵ� Map */
  private Map values = new HashMap();

  /** �ɼ����������� */
  private List fieldNames = new ArrayList();

  /** ��ʵ���������� */
  private List realFieldNames = new ArrayList();

  /** Detail������ɵ�List */
  private List childTableNames = new ArrayList();

  /** ��ǰ������������� */
  private String name;

  /** ����������/ɾ��/�޸� */
  private String action;

  private TableData oldValue;

  private List kiloFieldList;

  private List dateFieldList;

  public TableData() {
  }

  /**
   * @param entityNameҵ�������
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
   * �� XML ����һ�� TableData��XML�Ľṹͬ toString() ���ɵ�����
   * @param entity org.w3c.dom.Element ����
   * @see #toString
   */
  public TableData(Element entity) {
    // �õ���ǰ�Ĳ�����
    name = entity.getAttribute("name");
    if (name == null)
      throw new IllegalStateException("�����Element��û���ṩҵ����������");

    // �õ�����������ֶ�����ֵ
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

    // �õ�Detail�������
    NodeList entityDetails = entity.getChildNodes();
    for (int i = 0; i < entityDetails.getLength(); i++) {
      if (entityDetails.item(i).getNodeType() == Node.ELEMENT_NODE) {
        entityField = (Element) entityDetails.item(i);
        // �ҵ���Ϊentity��Element
        if (entityField.getNodeName().equalsIgnoreCase("entity")) {
          // ����Entity�ĸ������Զ����úã�Ȼ��setField
          String szDetail = entityField.getAttribute("name");
          childTableNames.add(szDetail);
          List lsDetail = new ArrayList();
          NodeList rows = entityField.getChildNodes();
          // ���εõ�ÿһ��row����Ϣ
          for (int m = 0; m < rows.getLength(); m++) {
            // ����дһ��MethodǶ�׵���
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

  /** ȡ���� */
  public String getName() {
    return name;
  }

  /** ���ñ��� */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * �����ֶ�ֵ������ֶ�ֵΪ��ֵ(null)���Զ�ת��Ϊ���ַ���("")
   */
  public void setField(String �ֶ���, Object �ֶ�ֵ) {
    if (�ֶ��� == null)
      throw new IllegalArgumentException("��TableData�����ֶ�ֵʱ���ֶ���Ϊ��ֵ");
    if (fieldNames.indexOf(�ֶ���) < 0) {
      fieldNames.add(�ֶ���);
      realFieldNames.add(�ֶ���);
    }
    if (�ֶ�ֵ == null)
      �ֶ�ֵ = "";
    values.put(�ֶ���, �ֶ�ֵ);
  }

  /**
   * ȡ�ֶ�ֵ
   * @param �ֶ���
   * @return �ֶ�ֵ
   */
  public Object getField(String �ֶ���) {
    Object result = values.get(�ֶ���);
    return result;
  }

  /** getField �ı��� */
  public Object getFieldObject(String �ֶ���) {
    return getField(�ֶ���);
  }

  /**
   * ȡ�ֶ�ֵ���ֶβ�����ʱ���ؿ��ַ���("")
   * @param �ֶ���
   * @return �ֶ�ֵ
   */
  public String getFieldValue(String �ֶ���) {
    Object value = getField(�ֶ���);
    if (null != value)
      return value.toString();
    return "";
  }

  /**
   * ȡ�ֶ�ֵ���ֶβ�����ʱ����0
   */
  public float getFieldFloat(String fieldName) {
    Object value = getField(fieldName);
    if (value != null)
      return Float.parseFloat((String) value);
    return 0.0F;
  }

  /**
   * ȡ�ֶ�ֵ���ֶβ�����ʱ����0
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
   * ȡ�ֶ������������ӱ����
   * @return Ԫ������Ϊ String ���ֶ���
   */
  public List getFieldNames() {
    List result = new ArrayList(fieldNames);
    result.removeAll(childTableNames);
    return result;
  }

  /**
   * ɾ��һ���ֶ�
   */
  public void deleteField(String fieldName) {
    if (childTableNames.contains(fieldName)) {
      throw new IllegalArgumentException("����ֱ��ɾ��TableData�е��ӱ�");
    }
    fieldNames.remove(fieldName);
    realFieldNames.remove(fieldName);
    values.remove(fieldName);
  }

  /**
   * ɾ��������ӱ��е�һ���ֶ�
   * @param tableName ����
   * @param fieldName �ֶ���
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
   * ȡ�ӱ����
   * @return Ԫ������Ϊ String ���ӱ����
   */
  public List getChildTableNames() {
    return childTableNames;
  }

  /**
   * @param �ӱ����
   * @return ����ָ�����������ֶ�����ֵ����.
   */
  public List getChildTables(String �ӱ����) {
    ArrayList detailsList = (ArrayList) values.get(�ӱ����);
    if (null != detailsList)
      return detailsList;

    detailsList = new ArrayList();
    values.put(�ӱ����, detailsList);
    childTableNames.add(�ӱ����);
    return detailsList;
  }

  /** ����ӱ����� */
  public void setChildTable(String �ӱ����, List �ӱ������б�, int û���õĲ���) {
    /*-- HH: 2006-8-31 ɾ����һ��
    if (values.get(�ӱ����) != null) {
      values.put(�ӱ����, �ӱ������б�);
      return;
    }
    */
    values.put(�ӱ����, �ӱ������б�);

    if (!childTableNames.contains(�ӱ����)) {
      childTableNames.add(�ӱ����);
      fieldNames.add(�ӱ����);
      realFieldNames.add(�ӱ����);
    }
    return;
  }

  /**
   * ���ÿɼ������С����ڲ�ɾ�����ݵ����ز��������С�
   * ��ע�⡿����������ӱ�����������
   * @param fieldNames �ɼ������У�Ԫ��ֵΪ String���������ӱ������ݽ�������һ�ݣ�
   *  ����ڵ��ú�����޸� fieldNames ��������������á�
   */
  public void setFieldNames(List fieldNames) {
    this.fieldNames.clear();
    this.fieldNames.addAll(fieldNames);
    this.fieldNames.removeAll(childTableNames);
    this.fieldNames.addAll(childTableNames);
  }

  /**
   * ��TableDataת����һ��XML��ʽ���ַ�����������ͨ�� TableData(Element) ��ԭ
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

  private static final DecimalFormat С�����6��0�ĸ�ʽ = new DecimalFormat("0.000000");

  /** ������ת�����ַ�����С������ȱ���6λ���ڽ�ȥ�����0�����ٱ���1λС�� */
  private static String Double_to6DecString(double d) {
    String s = С�����6��0�ĸ�ʽ.format(d);
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
   * �� TableData ת��Ϊ xml ��ʽ����ʽΪ:<pre>
   * ��row��
   * ��C1��value1��/C1��
   * ��C2��value2��/C2��
   * ��/row��
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

  /** ����������/ɾ��/�޸� */
  public String getAction() {
    return action;
  }

  public void setAction(String action) {
    this.action = action;
  }

  /** ��ֵ������ʱΪ null��ɾ��ʱΪ this */
  public TableData getOldValue() {
    if (INSERT.equals(action)) {
      return null;
    } else if (DELETE.equals(action)) {
      return this;
    }
    return oldValue;
  }

  /** ����ɼ�¼�������м�¼����һ�ݣ�Ŀǰ������Ƕ�׵��ӱ� */
  public void saveOldValue() {
    this.oldValue = new TableData();
    for (Iterator i = getFieldNames().iterator(); i.hasNext();) {
      String fieldName = (String) i.next();
      if (!getChildTableNames().contains(fieldName)) {
        this.oldValue.setField(fieldName, this.getField(fieldName));
      }
    }
  }

  /** ��ֵ���������޸�ʱΪ this��ɾ��ʱΪ null */
  public TableData getNewValue() {
    if (DELETE.equals(action)) {
      return null;
    }
    return this;
  }

  /**
   * ����oldValue�ָ�TableData������
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

  /** ȡ��ǧ��λ��ʾ���ֶ��� */
  public List getKiloFieldList() {
    return kiloFieldList;
  }

  /** ������ǧ��λ��ʾ���ֶ��� */
  public void setKiloFieldList(List kiloFieldList) {
    this.kiloFieldList = kiloFieldList;
  }

  /** ȡ�������͵��ֶ��� */
  public List getDateFieldList() {
    return dateFieldList;
  }

  /** �����������͵��ֶ��� */
  public void setDateFieldList(List dateFieldList) {
    this.dateFieldList = dateFieldList;
  }
}
