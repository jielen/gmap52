/*$Id: DataColFilter.java,v 1.2 2008/03/25 06:58:36 liuxiaoyong Exp $ */
package com.anyi.gp.print.template;

/**
 * <p>
 * Title: �������ݻ�������н��й���ʵ���Զ����е���
 * </p>
 * <p>
 * Description: ��һ������Ϊ�ջ�ȫΪ������ʱ������û�ѡ�񲻴�ӡ�����ݻ�������У� ��̬�ĸı�ģ�壬�����������������й��˵�
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
import java.sql.ResultSet;
import java.sql.SQLException;

import com.anyi.gp.print.exception.PrintingException;

public class DataColFilter {

  public DataColFilter() {
  }

  /**
   * ���˲���ӡ��������
   *
   * @param rs
   *          ResultSet ���ݽ����
   * @param labels
   *          TextElement[] ����ͷ�ֶ���Ϣ
   * @param fields
   *          TextElement[] �������ֶ���Ϣ
   * @param isFilterEmptyData
   *          boolean �Ƿ��ӡ������
   * @param isFilterZeroData
   *          boolean �Ƿ��ӡ������
   * @throws PrintingException
   */
  public void filterDataCols(ResultSet rs, TextElement[] labels,
      TextElement[] fields, boolean isFilterEmptyData, boolean isFilterZeroData)
      throws PrintingException {
    if (labels.length != fields.length) {
      throw new PrintingException("DataColFilter����㣨�գ������й���ʱ����"
          + "������Ŀ�������ֶ���Ŀ��");
    }
    for (int i = 0, j = fields.length; i < j; i++) {
      TextElement field = fields[i];
      TextElement label = labels[i];
      String fieldName = field.getName();
      boolean isEmpty = false;
      boolean isZero = false;
      if (isFilterEmptyData) {
        isEmpty = isEmptyDataCol(rs, fieldName);
      }
      if ((!isEmpty) && isFilterZeroData) {
        isZero = isZeroDataCol(rs, fieldName);
      }
      if (isEmpty || isZero) {
        field.setPrint(false);
        label.setPrint(false);
        int width = field.getWidth();
        for (int k = i + 1; k < j; k++) {
          TextElement afterField = fields[k];
          TextElement afterLabel = labels[k];
          afterField.setX(afterField.getX() - width);
          afterLabel.setX(afterLabel.getX() - width);
        }
      }
    }
  }

  /**
   * �ж�һ�������Ƿ��ǿ�
   *
   * @param rs
   *          ResultSet ���ݽ����
   * @param fieldName
   *          String �ֶ���
   * @throws PrintingException
   * @return boolean ���һ������ȫΪ�շ���true�����򷵻�false;
   */
  private boolean isEmptyDataCol(ResultSet rs, String fieldName)
      throws PrintingException {
    boolean result = true;
    try {
      rs.first();
      while (rs.next()) {
        Object fieldValue = rs.getObject(fieldName);
        if (fieldValue != null) {
          return false;
        }
      }
    } catch (SQLException e) {
      throw new PrintingException("DataColFilter���isEmptyDataCol�����ж��Ƿ��ǿ����ݳ���");
    }
    return result;
  }

  /**
   * �ж�ĳ���Ƿ�����������
   *
   * @param rs
   *          ResultSet ���ݽ����
   * @param fieldName
   *          String �ֶ���
   * @throws PrintingException
   * @return boolean
   */
  private boolean isZeroDataCol(ResultSet rs, String fieldName)
      throws PrintingException {
    boolean result = true;
    try {
      rs.first();
      while (rs.next()) {
        String fieldValue = rs.getString(fieldName);
        if (fieldValue != null) {
          if ((!fieldValue.equals("0") && (!fieldValue.equals("0.0")) && (!fieldValue
              .equals("0.00")))) {
            return false;
          }
        }
      }
    } catch (SQLException e) {
      throw new PrintingException("DataColFilter���isEmptyDataCol�����ж��Ƿ��ǿ����ݳ���");
    }
    return result;
  }

}
