/*$Id: DataColFilter.java,v 1.2 2008/03/25 06:58:36 liuxiaoyong Exp $ */
package com.anyi.gp.print.template;

/**
 * <p>
 * Title: 对零数据或空数据列进行过滤实现自动缩列调整
 * </p>
 * <p>
 * Description: 当一列数据为空或全为零数据时，如果用户选择不打印零数据或空数据列， 则动态的改变模板，将符合条件的数据列过滤掉
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
   * 过滤不打印德数据列
   *
   * @param rs
   *          ResultSet 数据结果集
   * @param labels
   *          TextElement[] 表格表头字段信息
   * @param fields
   *          TextElement[] 表格表体字段信息
   * @param isFilterEmptyData
   *          boolean 是否打印空数据
   * @param isFilterZeroData
   *          boolean 是否打印零数据
   * @throws PrintingException
   */
  public void filterDataCols(ResultSet rs, TextElement[] labels,
      TextElement[] fields, boolean isFilterEmptyData, boolean isFilterZeroData)
      throws PrintingException {
    if (labels.length != fields.length) {
      throw new PrintingException("DataColFilter类的零（空）数据列过滤时出错，"
          + "标题数目不等于字段数目！");
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
   * 判断一列数据是否是空
   *
   * @param rs
   *          ResultSet 数据结果集
   * @param fieldName
   *          String 字段名
   * @throws PrintingException
   * @return boolean 如果一列数据全为空返回true，否则返回false;
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
      throw new PrintingException("DataColFilter类的isEmptyDataCol方法判断是否是空数据出错");
    }
    return result;
  }

  /**
   * 判断某列是否是零数据列
   *
   * @param rs
   *          ResultSet 数据结果集
   * @param fieldName
   *          String 字段名
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
      throw new PrintingException("DataColFilter类的isEmptyDataCol方法判断是否是空数据出错");
    }
    return result;
  }

}
