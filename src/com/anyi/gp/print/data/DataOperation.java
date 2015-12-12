package com.anyi.gp.print.data;

/**
 * ʵ�����ݵ���������
 * <p>
 * Title: ���ݲ�����
 * </p>
 * <p>
 * Description: ʵ�����ݵļ���������
 * </p>
 * <p>
 * Copyright: Copyright (c) 2002
 * </p>
 * <p>
 * Company: UFGOV
 * </p>
 *
 * @author zhangyw
 * @version 1.0
 */
public class DataOperation {
  public DataOperation() {
  }

  /**
   * �ӷ�
   *
   * @param source1
   *          Double
   * @param source2
   *          Double
   * @return Double
   */
  public static Double add(Double source1, Double source2) {
    if (source1 == null && source2 == null) {
      return new Double(0);
    }
    return new Double(source1.doubleValue() + source2.doubleValue());
  }

  /**
   * �ӷ�
   *
   * @param source1
   *          String
   * @param source2
   *          String
   * @return Double
   */
  public static Double add(String source1, String source2) {
    if (source1.equals("") && source2.equals("")) {
      return new Double(0);
    }
    double d1 = 0;
    double d2 = 0;
    try {
      d1 = Double.parseDouble(source1);
      d2 = Double.parseDouble(source2);
    } catch (NumberFormatException e) {
      //
    }
    return new Double(d1 + d2);
  }

  /**
   * ����
   *
   * @param source1
   *          Double
   * @param source2
   *          Double
   * @return Double
   */
  public static Double subtract(Double source1, Double source2) {
    if (source1 == null && source2 == null) {
      return new Double(0);
    }
    return new Double(source1.doubleValue() - source2.doubleValue());
  }

  /**
   * ����
   *
   * @param source1
   *          String
   * @param source2
   *          String
   * @return Double
   */
  public static Double subtract(String source1, String source2) {
    if (source1.equals("") && source2.equals("")) {
      return new Double(0);
    }
    double d1 = 0;
    double d2 = 0;
    try {
      d1 = Double.parseDouble(source1);
      d2 = Double.parseDouble(source2);
    } catch (NumberFormatException e) {
      //
    }
    return new Double(d1 - d2);
  }

  /**
   * �˷�
   *
   * @param source1
   *          Double
   * @param source2
   *          Double
   * @return Double
   */
  public static Double multiply(Double source1, Double source2) {
    if (source1 == null && source2 == null) {
      return new Double(0);
    }
    return new Double(source1.doubleValue() * source2.doubleValue());
  }

  /**
   * �˷�
   *
   * @param source1
   *          String
   * @param source2
   *          String
   * @return Double
   */
  public static Double multiply(String source1, String source2) {
    if (source1.equals("") && source2.equals("")) {
      return new Double(0);
    }
    double d1 = 0;
    double d2 = 0;
    try {
      d1 = Double.parseDouble(source1);
      d2 = Double.parseDouble(source2);
    } catch (NumberFormatException e) {
      //
    }
    return new Double(d1 * d2);
  }

  /**
   * ����
   *
   * @param source1
   *          Double
   * @param source2
   *          Double
   * @return Double
   */
  public static Double divide(Double source1, Double source2) {
    if (source1 == null && source2 == null) {
      return new Double(0);
    }
    if (Math.abs(source2.doubleValue()) < 0.0000001) {
      return new Double(0);
    }
    return new Double(source1.doubleValue() / source2.doubleValue());
  }

  /**
   * ����
   *
   * @param source1
   *          String
   * @param source2
   *          String
   * @return Double
   */
  public static Double divide(String source1, String source2) {
    if (source1.equals("") && source2.equals("")) {
      return new Double(0);
    }
    double d1 = 0;
    double d2 = 0;
    try {
      d1 = Double.parseDouble(source1);
      d2 = Double.parseDouble(source2);
      if (Math.abs(d2) < 0.0000001) {
        d2 = 1;
      }
    } catch (NumberFormatException e) {
      //
    }
    return new Double(d1 / d2);
  }

  /**
   * ȡģ
   *
   * @param source1
   *          Integer
   * @param source2
   *          Integer
   * @return String
   */
  public static String mod(Integer source1, Integer source2) {
    if (source1 == null || source2 == null || source2.intValue() == 0) {
      return "";
    }
    int result = source1.intValue() % source2.intValue();
    return result + "";
  }

  /**
   * ȡģ
   *
   * @param source1
   *          String
   * @param source2
   *          String
   * @return String
   */
  public static String mod(String source1, String source2) {
    if (source1 == "" || source2 == "" || source2.equals("0")) {
      return "";
    }
    int result = Integer.parseInt(source1) % Integer.parseInt(source2);
    return result + "";
  }
}
