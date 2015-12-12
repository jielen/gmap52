/** 
 * Copyright ? 2004 BeiJing UFGOV Software Co. Ltd. 
 * All right reserved. 
 * Jun 23, 2005 Powered By chihongfeng 
 */
package com.anyi.gp.workflow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.anyi.gp.TableData;


/**
 * @author   leidaohong
 */
public class BusinessJuniorExp implements Comparator {

  /**
   * ����
   */
  public static final String BIGGER = "b";

  /**
   * С��
   */
  public static final String SMALLER = "s";

  //����
  public static final String EQUAL = "eq";

  //    ���ڵ���
  public static final String BIGGER_EQUAL = "beq";

  //    С�ڵ���
  public static final String SMALLER_EQUAL = "seq";

  //    ������
  public static final String NOT_EQUAL = "!eq";

  //    ����
  public static final String LIKE = "like";

  //    ������
  public static final String NOT_LIKE = "!like";

  /******************************��ֵ*************************************/
  /**
   * ����
   */
  public static final String TBIGGER = "tb";

  /**
   * С��
   */
  public static final String TSMALLER = "ts";

  //����
  public static final String TEQUAL = "teq";

  //    ���ڵ���
  public static final String TBIGGER_EQUAL = "tbeq";

  //    С�ڵ���
  public static final String TSMALLER_EQUAL = "tseq";

  //    ������
  public static final String TNOT_EQUAL = "!teq";

  //    ����
  public static final String TLIKE = "tlike";

  //    ������
  public static final String TNOT_LIKE = "!tlike";

  /************************���ֵ************************************/
  /**
   * ����
   */
  public static final String XBIGGER = "xb";

  /**
   * С��
   */
  public static final String XSMALLER = "xs";

  //����
  public static final String XEQUAL = "xeq";

  //    ���ڵ���
  public static final String XBIGGER_EQUAL = "xbeq";

  //    С�ڵ���
  public static final String XSMALLER_EQUAL = "xseq";

  //    ������
  public static final String XNOT_EQUAL = "!xeq";

  //    ����
  public static final String XLIKE = "xlike";

  //    ������
  public static final String XNOT_LIKE = "!xlike";

  /*********************************��Сֵ ***************************/

  /**
   * ����
   */
  public static final String NBIGGER = "nb";

  /**
   * С��
   */
  public static final String NSMALLER = "ns";

  //����
  public static final String NEQUAL = "neq";

  //    ���ڵ���
  public static final String NBIGGER_EQUAL = "nbeq";

  //    С�ڵ���
  public static final String NSMALLER_EQUAL = "nseq";

  //    ������
  public static final String NNOT_EQUAL = "!neq";

  //    ����
  public static final String NLIKE = "nlike";

  //    ������
  public static final String NNOT_LIKE = "!nlike";

  /**
   * ���ʽ�еĲ���1,�� max("����1"��"����2")
   */
  private String para1 = "";

  /**
   * ���ʽ�еĲ���1,�� max("����1"��"����2")
   */
  private String para2 = "";

  /**
   * ���ʽ���������� max
   */
  private String symbol = null;

  /**
   * ���캯������ڲ���Ϊ���ʽ�ִ�����max("����1"��"����2")
   * @param exp
   */
  public BusinessJuniorExp(String exp) throws IllegalArgumentException {
    if (exp == null)
      throw new IllegalArgumentException("�޷�ʶ��ı��ʽ[" + exp + "]");
    Pattern p = Pattern.compile("^(.*)\\(\"(.*)\"\\,\"(.*)\"\\)$");
    Matcher m = p.matcher(exp.trim());
    if (m.find()) {
      this.symbol = m.group(1);
      this.para1 = m.group(2);
      this.para2 = m.group(3);
    }

  }

  /**
   * @param para1
   * @param para2
   * @param symbol
   */
  public BusinessJuniorExp(String para1, String para2, String symbol) {
    super();
    this.para1 = para1;
    this.para2 = para2;
    this.symbol = symbol;
  }

  public boolean parse(Object val) {
    if (val instanceof String) {
      String value = (String) val;
      if (BIGGER.equalsIgnoreCase(symbol)) {
        return bigger(value);
      } else if (BIGGER_EQUAL.equalsIgnoreCase(symbol)) {
        return bigger(value) || equal(value);
      } else if (SMALLER.equalsIgnoreCase(symbol))
        return smaller(value);
      else if (SMALLER_EQUAL.equalsIgnoreCase(symbol))
        return smaller(value) || equal(value);
      else if (EQUAL.equalsIgnoreCase(symbol))
        return equal(value);
      else if (NOT_EQUAL.equalsIgnoreCase(symbol))
        return !equal(value);
      else if (LIKE.equalsIgnoreCase(symbol))
        return like(value);
      else if (NOT_LIKE.equalsIgnoreCase(symbol))
        return !like(value);
      else
        return false;
    }
    //��һ������
    else if (val instanceof ArrayList) {
      List value = (List) val;

      //��ֵ
      if (TBIGGER.equalsIgnoreCase(symbol)) {
        return bigger(total(value));
      } else if (TBIGGER_EQUAL.equalsIgnoreCase(symbol)) {
        return bigger(total(value)) || equal(total(value));
      } else if (TSMALLER.equalsIgnoreCase(symbol))
        return smaller(total(value));
      else if (TSMALLER_EQUAL.equalsIgnoreCase(symbol))
        return smaller(total(value)) || equal(total(value));
      else if (TEQUAL.equalsIgnoreCase(symbol))
        return equal(total(value));
      else if (TNOT_EQUAL.equalsIgnoreCase(symbol))
        return !equal(total(value));
      else if (TLIKE.equalsIgnoreCase(symbol))
        return like(total(value));
      else if (TNOT_LIKE.equalsIgnoreCase(symbol))
        return !like(total(value));
      //���ֵ
      else if (XBIGGER.equalsIgnoreCase(symbol)) {
        return bigger(max(value));
      } else if (XBIGGER_EQUAL.equalsIgnoreCase(symbol)) {
        return bigger(max(value)) || equal(max(value));
      } else if (XSMALLER.equalsIgnoreCase(symbol))
        return smaller(max(value));
      else if (XSMALLER_EQUAL.equalsIgnoreCase(symbol))
        return smaller(max(value)) || equal(max(value));
      else if (XEQUAL.equalsIgnoreCase(symbol))
        return equal(max(value));
      else if (XNOT_EQUAL.equalsIgnoreCase(symbol))
        return !equal(max(value));
      else if (XLIKE.equalsIgnoreCase(symbol))
        return like(max(value));
      else if (XNOT_LIKE.equalsIgnoreCase(symbol))
        return !like(max(value));
      //��Сֵ
      else if (NBIGGER.equalsIgnoreCase(symbol)) {
        return bigger(min(value));
      } else if (NBIGGER_EQUAL.equalsIgnoreCase(symbol)) {
        return bigger(min(value)) || equal(min(value));
      } else if (NSMALLER.equalsIgnoreCase(symbol))
        return smaller(min(value));
      else if (NSMALLER_EQUAL.equalsIgnoreCase(symbol))
        return smaller(min(value)) || equal(min(value));
      else if (NEQUAL.equalsIgnoreCase(symbol))
        return equal(min(value));
      else if (NNOT_EQUAL.equalsIgnoreCase(symbol))
        return !equal(min(value));
      else if (NLIKE.equalsIgnoreCase(symbol))
        return like(min(value));
      else if (NNOT_LIKE.equalsIgnoreCase(symbol))
        return !like(min(value));
      else
        return false;
    } else
      return false;
  }

  public boolean parse(TableData data) {
    if (isCompoExpression()) {
      String[] atts = para1.split("[.]");
      String value = null;
      //TableMeta meta = MetaPool.getTableMeta(entityName);
      //if(data.getName().equals(meta.getName())) {//why???
      //���ֻ�ǲ���
      if (atts.length == 1) {
    	  //ֻ�ǲ�����: add by liubo
    	  value = data.getName();
        return parse(value);//true;
      }
      //����������ֶ�
      if (atts.length == 2) {
        String compoName = atts[0];
        if (!compoName.equals(data.getName())) {
        	return false;
        }//�ж�sup_condition�еĲ�����data�Ĳ������Ƿ���ͬ: add by liubo
        String fieldName = atts[1];
        value = data.getFieldValue(fieldName);
        return parse(value);
      } else {
        //������ӱ��ֶ�
        //TODO: ֻ�����˶����ӱ�
        String cName = atts[1];
        String cField = atts[2];
        List childList = data.getChildTables(cName);
        //�õ��ӱ����ݼ�
        List childFieldValues = new ArrayList(); //�ӱ�ĳ�ֶ�ֵ��
        for (Iterator it = childList.iterator(); it.hasNext();) {
          TableData cData = (TableData) it.next();
          String cFieldValue = cData.getFieldValue(cField);
          if (null == cFieldValue)
            break;
          childFieldValues.add(cFieldValue);
        }
        return parse(childFieldValues);
        //                    }
      }
      //}
      //return false;
    }
    return false;
  }

  /* ʵ������
   * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
   */
  public int compare(Object o1, Object o2) {
    float f1 = Float.parseFloat((String) o1);
    float f2 = Float.parseFloat((String) o2);
    if (f1 == f2)
      return 0;
    if (f2 < f1)
      return -1;
    return 1;
  }

  /**
   * ���ֵ
   * @param value
   * @return
   */
  public Float max(List value) {
    if (value != null && !value.isEmpty()) {
      Collections.sort(value, this);
      return new Float(String.valueOf(value.get(0)));
    }
    return null;
  }

  /**
   * ����Сֵ
   * @param value
   * @return
   */
  public Float min(List value) {
    if (value != null && !value.isEmpty()) {
      Collections.sort(value, this);
      return new Float(String.valueOf(value.get(value.size() - 1)));
    }
    return null;
  }

  /**
   * ����
   * @param value
   * @return
   */
  public boolean like(Object value) {
    if (value instanceof String)
      //if(value!=null && ((String)value).indexOf(this.para2)>=0)
      //    return true;
      if (null != value) {
        return like((String) value, para2);
      }

    return false;
  }

  /**
   * ��չԭ����like������strSub�е�'%'���������[a-z0-9]�����ַ���'?'һ��[a-z0-9]�����ַ�
   * ���û�а���'%',����Ϊǰ����'%��,Ϊ����֮ǰ��ʹ��'%'�����
   */
  public boolean like(String strSource, String strSub) {
    boolean result = false;
    String ER_MORE_CHAR = "\\w*";
    String ER_MORE_CHAR2 = "\\\\w*";
    String ER_ONE_CHAR = ".";
    String MORE_CHAR = "%";
    String ONE_CHAR = "\\?";
    String strPattern = "";
    if (null == strSource || null == strSub) {
      return false;
    }
    if (strSub.indexOf(MORE_CHAR) == -1) {
      strPattern = ER_MORE_CHAR + strSub + ER_MORE_CHAR;
      result = Pattern.matches(strPattern, strSource);
    } else {
      strPattern = strSub.replaceAll(MORE_CHAR, ER_MORE_CHAR2);
      if (strSub.indexOf(ONE_CHAR) == -1) {
        strPattern = strPattern.replaceAll(ONE_CHAR, ER_ONE_CHAR);
      }
      result = Pattern.matches(strPattern, strSource);
    }
    return result;
  }

  /**
   * ����
   * @param value
   * @return
   */
  public boolean bigger(Object value) {
    float v = 0;
    if (value instanceof Float)
      v = ((Float) value).floatValue();
    else if (value instanceof String)
      v = Float.parseFloat((String) value);
    else
      return false;
    float to = Float.parseFloat(para2);
    if (v > to)
      return true;
    return false;
  }

  /**
   * ����
   * @param value
   * @return
   */
  public boolean equal(Object v) {
    if (v instanceof String) {
      String value = (String) v;
      if (value != null && value.trim().equals(para2))
        return true;
    } else if (v instanceof Float) {
      Float value = (Float) v;
      if (value != null && value.floatValue() == Float.parseFloat(para2))
        return true;
    }
    return false;
  }

  /**
   * С��
   * @param value
   * @return
   */
  public boolean smaller(Object value) {
    float v = 0;
    if (value instanceof Float)
      v = ((Float) value).floatValue();
    else if (value instanceof String)
      v = Float.parseFloat((String) value);
    else
      return false;
    float to = Float.parseFloat(para2);
    if (v < to)
      return true;
    return false;
  }

  /**
   * �ܺ�
   * @param value
   * @return
   */
  public Float total(List value) {
    float t = 0;
    if (value != null && value.size() > 0) {
      for (int i = 0; i < value.size(); i++) {
        float v = Float.parseFloat((String) value.get(i));
        t += v;
      }
      return new Float(t);
    }
    return null;
  }

  /**
   * �Ƿ��ǲ������ʽ
   */
  public boolean isCompoExpression() {
    return true;
  }

  /**
   * 
   * @return
   */
  public boolean isSysExpression() {
    return false;
  }

  /**
   * @return   ���� para1��
   * @uml.property   name="para1"
   */
  public String getPara1() {
    return para1;
  }

  /**
   * @param para1   Ҫ���õ� para1��
   * @uml.property   name="para1"
   */
  public void setPara1(String para1) {
    this.para1 = para1;
  }

  /**
   * @return   ���� para2��
   * @uml.property   name="para2"
   */
  public String getPara2() {
    return para2;
  }

  /**
   * @param para2   Ҫ���õ� para2��
   * @uml.property   name="para2"
   */
  public void setPara2(String para2) {
    this.para2 = para2;
  }

  /**
   * @return   ���� symbol��
   * @uml.property   name="symbol"
   */
  public String getSymbol() {
    return symbol;
  }

  /**
   * @param symbol   Ҫ���õ� symbol��
   * @uml.property   name="symbol"
   */
  public void setSymbol(String symbol) {
    this.symbol = symbol;
  }

  public static void main(String[] args) {
    List a = new ArrayList();
    a.add("1");
    a.add("8");
    a.add("-6.3");
    a.add("12.7");
    BusinessJuniorExp exp = new BusinessJuniorExp(
      "teq(\"AS_COMPANY.COCODE\",\"12.7\")");
    System.out.println(exp.parse(a));
  }
}
