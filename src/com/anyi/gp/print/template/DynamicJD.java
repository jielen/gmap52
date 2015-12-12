/* $Id: DynamicJD.java,v 1.1 2008/02/22 09:09:34 liuxiaoyong Exp $ */
package com.anyi.gp.print.template;

/**
 * 动态产生打印模板
 * <p>
 * Title: DynamicJD
 * </p>
 * <p>
 * Description: 通过传递页面元素的信息，动态产生打印模板
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
import java.awt.Color;
import java.util.List;

import net.sf.jasperreports.engine.JRAlignment;
import net.sf.jasperreports.engine.JRBox;
import net.sf.jasperreports.engine.JRElement;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRGraphicElement;
import net.sf.jasperreports.engine.JRTextElement;
import net.sf.jasperreports.engine.base.JRBaseBox;
import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.design.JRDesignField;
import net.sf.jasperreports.engine.design.JRDesignGroup;
import net.sf.jasperreports.engine.design.JRDesignLine;
import net.sf.jasperreports.engine.design.JRDesignParameter;
import net.sf.jasperreports.engine.design.JRDesignReportFont;
import net.sf.jasperreports.engine.design.JRDesignStaticText;
import net.sf.jasperreports.engine.design.JRDesignTextField;
import net.sf.jasperreports.engine.design.JasperDesign;

public class DynamicJD {
  /**
   * 构造函数
   */
  public DynamicJD() {
  }

  /**
   * 通过传递参数产生打印模板
   * 
   * @param name
   *          String 模板名称
   * @param cnName
   *          String 打印文件头名称
   * @param pageWidth
   *          int 页面宽度
   * @param pageHeight
   *          int 页面高度
   * @param rowHeight
   *          int 行高
   * @param params
   *          TextElement[] 参数变量
   * @param labels
   *          TextElement[] 表题变量
   * @param fields
   *          TextElement[] 数据库字段变量
   * @throws JRException
   * @return JasperDesign 动态打印模板
   */
  public JasperDesign getJasperDesign(String name, String cnName,
      int pageWidth, int pageHeight, int rowHeight, int rowDetailHeight,
      int pageHeaderH, int pageFooterH, ParamFieldInfor[] params,
      StaticTextInfor[] labels, TextElement[] fields) throws JRException {
    if (pageWidth < 565) {
      pageWidth = 565;
    }
    JasperDesign jasperDesign = new JasperDesign();
    // 1.页面设置
    setPageParam(jasperDesign, name, pageWidth, pageHeight);
    // 2.字体设置
    addFontsInfor(jasperDesign);
    // 3.设置参数
    addParameter(jasperDesign, params);
    // 4.字段变量
    addField(jasperDesign, fields);
    // 5.加入Title
    JRDesignBand title = new JRDesignBand();
    jasperDesign.setTitle(title);
    // 6.加入pageHeader
    JRDesignBand pageHeader = addPageHeader(jasperDesign, cnName, pageWidth,
        true);
    jasperDesign.setPageHeader(pageHeader);
    // 7.加入columnHeader
    JRDesignBand columnHeader = addColumnHeader(jasperDesign, pageHeaderH,
        labels, params);
    jasperDesign.setColumnHeader(columnHeader);
    // 8. 加入分组头
    JRDesignGroup group = new JRDesignGroup();
    group.setName("tableHeader");
    group.setReprintHeaderOnEachPage(true);
    JRDesignBand groupHeader = addGroupHeader(jasperDesign, rowHeight, labels);
    group.setGroupHeader(groupHeader);
    // 9. 加入detail
    JRDesignBand detail = addDetail(jasperDesign, rowDetailHeight, fields);
    jasperDesign.setDetail(detail);
    // 10.加入分组尾
    JRDesignBand groupFooter = addGroupFooter(jasperDesign, rowHeight, labels);
    group.setGroupFooter(groupFooter);
    jasperDesign.addGroup(group);
    // 11. 加入columnFooter
    JRDesignBand columnFooter = addColumnFooter(jasperDesign, pageFooterH,
        labels, params);
    jasperDesign.setColumnFooter(columnFooter);
    // 12. 加入pageFooter
    JRDesignBand pageFooter = addPageFooter(jasperDesign, pageWidth, true,
        false);
    jasperDesign.setPageFooter(pageFooter);
    // 13. 加入summary
    JRDesignBand summary = new JRDesignBand();
    jasperDesign.setSummary(summary);

    return jasperDesign;
  }

  /**
   * 通过传递参数产生打印模板
   * 
   * @param name
   *          String 模板名称
   * @param cnName
   *          String 打印文件头名称
   * @param pageWidth
   *          int 页面宽度
   * @param pageHeight
   *          int 页面高度
   * @param rowHeight
   *          int 行高
   * @param params
   *          TextElement[] 参数变量
   * @param labels
   *          TextElement[] 表题变量
   * @param fields
   *          TextElement[] 数据库字段变量
   * @throws JRException
   * @return JasperDesign 动态打印模板
   */
  public JasperDesign getJasperDesignNoPdf(String name, String cnName,
      int pageWidth, int pageHeight, int rowHeight, int rowDetailHeight,
      int pageHeaderH, int pageFooterH, ParamFieldInfor[] params,
      StaticTextInfor[] labels, TextElement[] fields) throws JRException {
    if (pageWidth < 565) {
      pageWidth = 565;
    }
    JasperDesign jasperDesign = new JasperDesign();
    // 1.页面设置
    setPageParam(jasperDesign, name, pageWidth, pageHeight);
    // 2.字体设置
    addFontsInfor(jasperDesign);
    // 3.设置参数
    addParameter(jasperDesign, params);
    // 4.字段变量
    addField(jasperDesign, fields);
    // 5.加入Title
    JRDesignBand title = new JRDesignBand();
    jasperDesign.setTitle(title);
    // 6.加入pageHeader
    JRDesignBand pageHeader = addPageHeader(jasperDesign, cnName, pageWidth,
        true);
    jasperDesign.setPageHeader(pageHeader);
    // 7.加入columnHeader
    JRDesignBand columnHeader = addColumnHeader(jasperDesign, pageHeaderH,
        labels, params);
    jasperDesign.setColumnHeader(columnHeader);
    // 8. 加入分组头
    JRDesignGroup group = new JRDesignGroup();
    group.setName("tableHeader");
    group.setReprintHeaderOnEachPage(true);
    JRDesignBand groupHeader = addGroupHeaderNoPdf(jasperDesign, rowHeight,
        labels);
    group.setGroupHeader(groupHeader);
    // 9. 加入detail
    JRDesignBand detail = addDetailNoPdf(jasperDesign, rowDetailHeight, fields);
    jasperDesign.setDetail(detail);
    // 10.加入分组尾
    JRDesignBand groupFooter = addGroupFooter(jasperDesign, rowHeight, labels);
    group.setGroupFooter(groupFooter);
    jasperDesign.addGroup(group);
    // 11. 加入columnFooter
    JRDesignBand columnFooter = addColumnFooter(jasperDesign, pageFooterH,
        labels, params);
    jasperDesign.setColumnFooter(columnFooter);
    // 12. 加入pageFooter
    JRDesignBand pageFooter = addPageFooter(jasperDesign, pageWidth, true,
        false);
    jasperDesign.setPageFooter(pageFooter);
    // 13. 加入summary
    JRDesignBand summary = new JRDesignBand();
    jasperDesign.setSummary(summary);

    return jasperDesign;
  }

  /**
   * //设置页面参数
   * 
   * @param jasperDesign
   *          JasperDesign 打印模板对象
   * @param name
   *          String 模板名
   * @param pageWidth
   *          int 页面宽度
   * @param pageHeight
   *          int 页面高度
   */
  private void setPageParam(JasperDesign jasperDesign, String name,
      int pageWidth, int pageHeight) {
    jasperDesign.setName(name);
    if (pageWidth < 595) {
      pageWidth = 595;
    }
    jasperDesign.setPageWidth(pageWidth + 120);
    jasperDesign.setPageHeight(pageHeight);
    jasperDesign.setColumnWidth(pageWidth + 40);
    jasperDesign.setColumnSpacing(0);
    jasperDesign.setLeftMargin(40);
    jasperDesign.setRightMargin(40);
    jasperDesign.setTopMargin(50);
    jasperDesign.setBottomMargin(20);
  }

  /**
   * 加入字体信息，默认是宋体
   * 
   * @param jasperDesign
   *          JasperDesign
   * @throws JRException
   */
  private void addFontsInfor(JasperDesign jasperDesign) throws JRException {
    JRDesignReportFont normalFont = new JRDesignReportFont();
    normalFont.setName("ST_Song");
    normalFont.setDefault(true);
    normalFont.setFontName("宋体");
    normalFont.setSize(10);
    normalFont.setPdfFontName("STSong-Light");
    normalFont.setPdfEncoding("UniGB-UCS2-H");
    normalFont.setPdfEmbedded(false);
    jasperDesign.addFont(normalFont);
  }

  /**
   * 向模板中加入加入参数变量
   * 
   * @param jasperDesign
   *          JasperDesign 打印模板对象
   * @param params
   *          ParamFieldInfor[] 参数变量信息
   * @throws JRException
   */
  private void addParameter(JasperDesign jasperDesign, TextElement[] params)
      throws JRException {
    for (int i = 0, j = params.length; i < j; i++) {
      JRDesignParameter parameter = new JRDesignParameter();
      TextElement param = params[i];
      parameter.setName(param.getName());
      parameter.setValueClassName(param.getClassType());
      jasperDesign.addParameter(parameter);
    }
  }

  /**
   * 向模板中加入字段变量
   * 
   * @param jasperDesign
   *          JasperDesign 模板对象
   * @param fields
   *          TextFieldInfor[] 字段变量信息
   * @throws JRException
   */
  private void addField(JasperDesign jasperDesign, TextElement[] fields)
      throws JRException {
    for (int i = 0, j = fields.length; i < j; i++) {
      JRDesignField field = new JRDesignField();
      TextElement f = fields[i];
      field.setName(f.getName());
      field.setValueClassName(f.getClassType());
      jasperDesign.addField(field);
    }
  }

  /**
   * 加入页头
   * 
   * @param jasperDesign
   *          JasperDesign 模板对象
   * @param name
   *          String 模板中文名
   * @throws JRException
   * @return JRDesignBand
   */
  private JRDesignBand addPageHeader(JasperDesign jasperDesign, String name,
      int pageWidth, boolean headerWithLine) throws JRException {
    JRDesignBand band = new JRDesignBand();
    band.setHeight(40);
    JRDesignExpression expression = new JRDesignExpression();
    expression.setValueClassName("java.lang.Boolean");
    boolean isPrint = ((name != null) && (!name.equals("undefined")));
    expression.setText("new Boolean(" + isPrint + ")");
    band.setPrintWhenExpression(expression);
    if ((name != null) && (!name.equals(""))) {
      List fonts = jasperDesign.getFontsList();
      JRDesignReportFont font = (JRDesignReportFont) fonts.get(0);
      JRDesignStaticText staticText = new JRDesignStaticText();
      staticText.setX(0);
      staticText.setY(0);
      staticText.setWidth(pageWidth - 100);
      staticText.setHeight(30);
      staticText.setForecolor(Color.black);
      staticText.setStretchType(JRElement.STRETCH_TYPE_RELATIVE_TO_BAND_HEIGHT);
      staticText.setTextAlignment(JRTextElement.TEXT_ALIGN_CENTER);
      font.setSize(10);
      staticText.setFont(font);
      staticText.setText(name);
      if (headerWithLine) {
        JRDesignLine line = new JRDesignLine();
        line.setX(0);
        line.setY(31);
        line.setWidth(pageWidth - 50);
        line.setHeight(0);
        line.setForecolor(new Color(0x80, 0x80, 0x80));
        line.setPositionType(JRElement.POSITION_TYPE_FLOAT);
        band.addElement(line);
      }
      band.addElement(staticText);
    }
    return band;
  }

  /**
   * 加入列头
   * 
   * @param jasperDesign
   *          JasperDesign 模板对象
   * @param rowHeigth
   *          int 行高
   * @param params
   *          ParamFieldInfor[] 参数对象信息
   * @param labels
   *          StaticTextInfor[] 静态标题信息
   * @throws JRException
   * @return JRDesignBand 列头对象
   */
  private JRDesignBand addColumnHeader(JasperDesign jasperDesign,
      int bandHeight, StaticTextInfor[] labels, ParamFieldInfor[] params)
      throws JRException {
    JRDesignBand band = new JRDesignBand();
    List fonts = jasperDesign.getFontsList();
    JRDesignReportFont font = (JRDesignReportFont) fonts.get(0);
    band.setHeight(bandHeight);
    JRDesignExpression expression = new JRDesignExpression();
    expression.setValueClassName("java.lang.Boolean");
    expression.setText("new Boolean($V{PAGE_NUMBER}.intValue() == 1)");
    band.setPrintWhenExpression(expression);
    for (int i = 0, j = params.length; i < j; i++) {
      JRDesignTextField textField = new JRDesignTextField();
      ParamFieldInfor param = params[i];
      if ((!param.isPrint()) || (!param.isHeader())) {
        continue;
      }
      textField.setX(param.getX());
      textField.setY(param.getY());
      textField.setWidth(param.getWidth());
      textField.setHeight(param.getHeight());
      textField.setTextAlignment(JRTextElement.TEXT_ALIGN_RIGHT);
      textField.setFont(font);
      expression = new JRDesignExpression();
      expression.setValueClassName(param.getClassType());
      expression.setText("$P{" + param.getName() + "}");
      textField.setExpression(expression);
      band.addElement(textField);
    }
    for (int i = 0, j = labels.length; i < j; i++) {
      StaticTextInfor label = labels[i];
      if ((!label.isPrint()) || (!label.getPosition().equals("pageHeader"))) {
        continue;
      }
      JRDesignStaticText staticText = new JRDesignStaticText();
      staticText.setX(label.getX() - 39);
      staticText.setY(label.getY());
      staticText.setWidth(label.getWidth() - 1);
      staticText.setHeight(label.getHeight() - 1);
      staticText.setForecolor(Color.black);
      staticText.setMode(JRElement.MODE_OPAQUE);
      staticText.setTextAlignment(JRTextElement.TEXT_ALIGN_CENTER);
      staticText.setStretchType(JRElement.STRETCH_TYPE_RELATIVE_TO_BAND_HEIGHT);
      staticText.setPrintWhenDetailOverflows(true);
      staticText.setPrintRepeatedValues(true);
      staticText.setFont(font);
      staticText.setText(label.getContent());
      band.addElement(staticText);
    }
    return band;
  }

  /**
   * 加入分组头
   * 
   * @param jasperDesign
   *          JasperDesign 动态模板对象
   * @param rowHeigth
   *          int 行高
   * @param params
   *          TextElement[] 参数对象
   * @param labels
   *          TextElement[] 标题对象
   * @throws JRException
   * @return JRDesignBand 分组头
   */
  private JRDesignBand addGroupHeader(JasperDesign jasperDesign, int rowHeigth,
      StaticTextInfor[] labels) throws JRException {
    JRDesignBand band = new JRDesignBand();
    List fonts = jasperDesign.getFontsList();
    JRDesignReportFont font = (JRDesignReportFont) fonts.get(0);
    band.setHeight(rowHeigth + 1);
    for (int i = 0, j = labels.length; i < j; i++) {
      StaticTextInfor label = labels[i];
      if ((!label.isPrint()) || (!label.getPosition().equals("tableHeader"))) {
        continue;
      }
      // JRDesignRectangle rectangle = new JRDesignRectangle();
      // rectangle.setX(label.getX() - 39);
      // rectangle.setY(label.getY() - 1);
      // rectangle.setWidth(label.getWidth() + 1);
      // rectangle.setHeight(label.getHeight() + 1);
      // rectangle.setForecolor(Color.black);
      // rectangle.setStretchType(JRElement.STRETCH_TYPE_RELATIVE_TO_BAND_HEIGHT);
      // rectangle.setPrintWhenDetailOverflows(true);
      // rectangle.setPrintRepeatedValues(true);
      // band.addElement(rectangle);
      JRDesignStaticText staticText = new JRDesignStaticText();
      staticText.setX(label.getX() - 39);
      staticText.setY(label.getY());
      staticText.setWidth(label.getWidth());
      staticText.setHeight(label.getHeight());
      // 去掉矩形框后改用设计元素的Box加上格线
      JRBox jrbox = new JRBaseBox();
      jrbox.setLeftBorderColor(Color.black);
      jrbox.setTopBorderColor(Color.black);
      jrbox.setRightBorderColor(Color.black);
      jrbox.setBottomBorderColor(Color.black);
      jrbox.setLeftBorder(JRGraphicElement.PEN_THIN);
      jrbox.setTopBorder(JRGraphicElement.PEN_THIN);
      jrbox.setRightBorder(JRGraphicElement.PEN_THIN);
      jrbox.setBottomBorder(JRGraphicElement.PEN_THIN);
      staticText.setBox(jrbox);
      staticText.setForecolor(Color.black);
      staticText.setMode(JRElement.MODE_OPAQUE);
      staticText.setTextAlignment(JRTextElement.TEXT_ALIGN_CENTER);
      if (label.getHeight() + label.getHeight() > rowHeigth) {
        staticText.setVerticalAlignment(JRAlignment.VERTICAL_ALIGN_MIDDLE);
      } else {
        staticText.setVerticalAlignment(JRAlignment.VERTICAL_ALIGN_TOP);
      }
      staticText.setStretchType(JRElement.STRETCH_TYPE_RELATIVE_TO_BAND_HEIGHT);
      staticText.setPrintWhenDetailOverflows(true);
      staticText.setPrintRepeatedValues(true);
      staticText.setFont(font);
      staticText.setText(label.getContent());
      band.addElement(staticText);
    }
    return band;
  }

  /**
   * 加入分组头，不输出矩形框，文本框或字段框之间两两相连
   * 
   * @param jasperDesign
   *          JasperDesign 动态模板对象
   * @param rowHeigth
   *          int 行高
   * @param params
   *          TextElement[] 参数对象
   * @param labels
   *          TextElement[] 标题对象
   * @throws JRException
   * @return JRDesignBand 分组头
   */
  private JRDesignBand addGroupHeaderNoPdf(JasperDesign jasperDesign,
      int rowHeigth, StaticTextInfor[] labels) throws JRException {
    JRDesignBand band = new JRDesignBand();
    List fonts = jasperDesign.getFontsList();
    JRDesignReportFont font = (JRDesignReportFont) fonts.get(0);
    band.setHeight(rowHeigth + 1);
    int ichkwidth = 39, iey = 1;
    for (int i = 0, j = labels.length; i < j; i++) {
      StaticTextInfor label = labels[i];
      if ((!label.isPrint()) || (!label.getPosition().equals("tableHeader"))) {
        continue;
      }
      JRDesignStaticText staticText = new JRDesignStaticText();
      if (i == 0) {
        if (label.getX() - 39 >= 0) {
          ichkwidth = 39;
        } else {
          ichkwidth = label.getX();
        }
        if (label.getY() - 1 >= 0) {
          iey = 1;
        } else {
          iey = label.getY();
        }

      }
      staticText
          .setX(label.getX() - ichkwidth >= 0 ? (label.getX() - ichkwidth) : 0);
      staticText.setY(label.getY() - iey >= 0 ? (label.getY() - iey) : 0);
      staticText.setWidth(label.getWidth());
      staticText.setHeight(label.getHeight());
      staticText.setForecolor(Color.black);
      staticText.setMode(JRElement.MODE_OPAQUE);
      staticText.setTextAlignment(JRTextElement.TEXT_ALIGN_CENTER);
      if (label.getHeight() + label.getHeight() > rowHeigth) {
        staticText.setVerticalAlignment(JRAlignment.VERTICAL_ALIGN_MIDDLE);
      } else {
        staticText.setVerticalAlignment(JRAlignment.VERTICAL_ALIGN_TOP);
      }
      staticText.setStretchType(JRElement.STRETCH_TYPE_RELATIVE_TO_BAND_HEIGHT);
      staticText.setPrintWhenDetailOverflows(true);
      staticText.setPrintRepeatedValues(true);
      staticText.setFont(font);
      staticText.setText(label.getContent());
      band.addElement(staticText);
    }
    return band;
  }

  /**
   * 加入表体，不输出矩形框，文本框或字段框之间两两相连
   * 
   * @param jasperDesign
   *          JasperDesign 模板对象
   * @param rowHeigth
   *          int 行高
   * @param fields
   *          TextFieldInfor[] 字段变量信息
   * @throws JRException
   * @return JRDesignBand 表体对象
   */
  private JRDesignBand addDetailNoPdf(JasperDesign jasperDesign, int rowHeigth,
      TextElement[] fields) throws JRException {
    JRDesignBand band = new JRDesignBand();
    int irowHeight = rowHeigth + 1;
    int ichkwidth = 39;
    band.setHeight(irowHeight);
    for (int i = 0, j = fields.length; i < j; i++) {
      JRDesignTextField textField = new JRDesignTextField();
      TextElement field = fields[i];
      if (!field.isPrint()) {
        continue;
      }
      if (i == 0) {
        if (field.getX() - 39 >= 0) {
          ichkwidth = 39;
        } else {
          ichkwidth = field.getX();
        }
      }
      textField.setX(field.getX() - ichkwidth >= 0 ? (field.getX() - ichkwidth)
          : 0);
      textField.setY(0);
      textField.setWidth(field.getWidth());
      if (irowHeight < field.getHeight() + 1) {
        irowHeight = field.getHeight() + 1;
      }
      textField.setHeight(irowHeight);
      textField.setStretchType(JRElement.STRETCH_TYPE_RELATIVE_TO_BAND_HEIGHT);
      textField.setMode(JRElement.MODE_OPAQUE);
      textField.setTextAlignment(field.getAlign());
      textField.setStretchType(JRElement.STRETCH_TYPE_RELATIVE_TO_BAND_HEIGHT);
      textField.setPrintWhenDetailOverflows(true);
      textField.setPrintRepeatedValues(true);
      textField.setStretchWithOverflow(true);
      textField.setBlankWhenNull(true);
      List fonts = jasperDesign.getFontsList();
      JRDesignReportFont font = (JRDesignReportFont) fonts.get(0);
      textField.setFont(font);
      JRDesignExpression expression = new JRDesignExpression();
      expression.setValueClassName(field.getClassType());
      // expression.setText("\" \" + String.valueOf($F{" + field.getContent() +
      // "})");
      expression.setText("$F{" + field.getContent() + "}");
      textField.setExpression(expression);
      band.addElement(textField);
    }
    band.setHeight(irowHeight);
    return band;
  }

  /**
   * 加入表体
   * 
   * @param jasperDesign
   *          JasperDesign 模板对象
   * @param rowHeigth
   *          int 行高
   * @param fields
   *          TextFieldInfor[] 字段变量信息
   * @throws JRException
   * @return JRDesignBand 表体对象
   */
  private JRDesignBand addDetail(JasperDesign jasperDesign, int rowHeigth,
      TextElement[] fields) throws JRException {
    JRDesignBand band = new JRDesignBand();
    int irowHeight = rowHeigth + 1;
    band.setHeight(irowHeight);
    for (int i = 0, j = fields.length; i < j; i++) {
      TextElement field = fields[i];
      if (!field.isPrint()) {
        continue;
      }
      if (irowHeight < field.getHeight() + 1) {
        irowHeight = field.getHeight() + 1;
      }
      // JRDesignRectangle rectangle = new JRDesignRectangle();
      // rectangle.setX(field.getX() - 39);
      // rectangle.setY(0);
      // rectangle.setWidth(field.getWidth() + 1);
      // rectangle.setHeight(irowHeight);
      // rectangle.setStretchType(JRElement.STRETCH_TYPE_RELATIVE_TO_BAND_HEIGHT);
      // rectangle.setPrintWhenDetailOverflows(true);
      // rectangle.setPrintRepeatedValues(true);
      // rectangle.setPen(JRGraphicElement.PEN_1_POINT);
      // band.addElement(rectangle);
      JRDesignTextField textField = new JRDesignTextField();
      textField.setX(field.getX() - 39);
      textField.setY(0);
      textField.setWidth(field.getWidth());
      textField.setHeight(irowHeight);
      // 去掉矩形框后改用设计元素的Box加上格线
      JRBox jrbox = new JRBaseBox();
      jrbox.setLeftBorderColor(Color.black);
      jrbox.setTopBorderColor(Color.black);
      jrbox.setRightBorderColor(Color.black);
      jrbox.setBottomBorderColor(Color.black);
      jrbox.setLeftBorder(JRGraphicElement.PEN_THIN);
      jrbox.setTopBorder(JRGraphicElement.PEN_THIN);
      jrbox.setRightBorder(JRGraphicElement.PEN_THIN);
      jrbox.setBottomBorder(JRGraphicElement.PEN_THIN);
      textField.setBox(jrbox);

      textField.setMode(JRElement.MODE_OPAQUE);
      textField.setTextAlignment(field.getAlign());
      textField.setStretchType(JRElement.STRETCH_TYPE_RELATIVE_TO_BAND_HEIGHT);
      textField.setPrintWhenDetailOverflows(true);
      textField.setPrintRepeatedValues(true);
      textField.setStretchWithOverflow(true);
      textField.setBlankWhenNull(true);
      List fonts = jasperDesign.getFontsList();
      JRDesignReportFont font = (JRDesignReportFont) fonts.get(0);
      textField.setFont(font);
      JRDesignExpression expression = new JRDesignExpression();
      expression.setValueClassName(field.getClassType());
      // expression.setText("\" \" + String.valueOf($F{" + field.getContent() +
      // "})");
      expression.setText("$F{" + field.getContent() + "}");
      textField.setExpression(expression);
      band.addElement(textField);
    }
    band.setHeight(irowHeight);
    return band;
  }

  /**
   * 加入分组尾
   * 
   * @param jasperDesign
   *          JasperDesign 动态模板对象
   * @param rowHeigth
   *          int 行高
   * @param params
   *          TextElement[] 参数对象
   * @param labels
   *          TextElement[] 标题对象
   * @throws JRException
   * @return JRDesignBand 分组尾
   */
  private JRDesignBand addGroupFooter(JasperDesign jasperDesign, int rowHeigth,
      TextElement[] labels) throws JRException {
    JRDesignBand band = new JRDesignBand();
    band.setHeight(0);
    return band;
  }

  /**
   * 加入列尾
   * 
   * @param jasperDesign
   *          JasperDesign 动态打印模板对象
   * @param rowHeigth
   *          int
   * @param params
   *          ParamFieldInfor[]
   * @throws JRException
   * @return JRDesignBand
   */
  private JRDesignBand addColumnFooter(JasperDesign jasperDesign,
      int bandHeight, StaticTextInfor[] labels, ParamFieldInfor[] params)
      throws JRException {
    JRDesignBand band = new JRDesignBand();
    List fonts = jasperDesign.getFontsList();
    JRDesignReportFont font = (JRDesignReportFont) fonts.get(0);
    band.setHeight(bandHeight);
    for (int i = 0, j = params.length; i < j; i++) {
      JRDesignTextField textField = new JRDesignTextField();
      ParamFieldInfor param = params[i];
      if ((!param.isPrint()) || (!param.isFooter())) {
        continue;
      }
      textField.setX(param.getX());
      textField.setY(param.getY());
      textField.setWidth(param.getWidth());
      textField.setHeight(param.getHeight());
      textField.setTextAlignment(JRTextElement.TEXT_ALIGN_RIGHT);
      textField.setFont(font);
      JRDesignExpression expression = new JRDesignExpression();
      expression.setValueClassName(param.getClassType());
      expression.setText("$P{" + param.getName() + "}");
      textField.setExpression(expression);
      band.addElement(textField);
    }
    for (int i = 0, j = labels.length; i < j; i++) {
      StaticTextInfor label = labels[i];
      if ((label.isPrint()) || (!label.getPosition().equals("pageFooter"))) {
        continue;
      }
      JRDesignStaticText staticText = new JRDesignStaticText();
      staticText.setX(label.getX() - 39);
      staticText.setY(label.getY());
      staticText.setWidth(label.getWidth() - 1);
      staticText.setHeight(label.getHeight() - 1);
      staticText.setForecolor(Color.black);
      staticText.setMode(JRElement.MODE_OPAQUE);
      staticText.setTextAlignment(JRTextElement.TEXT_ALIGN_CENTER);
      staticText.setStretchType(JRElement.STRETCH_TYPE_RELATIVE_TO_BAND_HEIGHT);
      staticText.setPrintWhenDetailOverflows(true);
      staticText.setPrintRepeatedValues(true);
      staticText.setFont(font);
      staticText.setText(label.getContent());
      band.addElement(staticText);
    }
    return band;
  }

  private JRDesignBand addPageFooter(JasperDesign jasperDesign, int pageWidth,
      boolean withPageNum, boolean withTotalNum) throws JRException {
    JRDesignBand band = new JRDesignBand();
    band.setHeight(30);
    JRDesignExpression expression = new JRDesignExpression();
    expression.setValueClassName("java.lang.Boolean");
    boolean isPrint = (withPageNum || withTotalNum);
    expression.setText("new Boolean(" + isPrint + ")");
    band.setPrintWhenExpression(expression);
    List fonts = jasperDesign.getFontsList();
    JRDesignReportFont font = (JRDesignReportFont) fonts.get(0);
    JRDesignTextField textField = new JRDesignTextField();
    if (withPageNum) {
      textField.setX(0);
      textField.setY(0);
      if (withTotalNum) {
        textField.setWidth(pageWidth / 2);
      } else {
        textField.setWidth(pageWidth - 50);
      }
      textField.setHeight(30);
      textField.setForecolor(Color.black);
      textField.setTextAlignment(JRTextElement.TEXT_ALIGN_RIGHT);
      textField.setFont(font);
      expression = new JRDesignExpression();
      expression.setValueClass(java.lang.String.class);
      expression.setText("\"第\" + String.valueOf($V{PAGE_NUMBER})+ \"页\"");
      textField.setExpression(expression);
      band.addElement(textField);
    }
    if (withTotalNum) {
      textField = new JRDesignTextField();
      textField.setX(pageWidth / 2 + 10);
      textField.setY(0);
      textField.setWidth(pageWidth / 2);
      textField.setHeight(30);
      textField.setForecolor(Color.black);
      textField.setTextAlignment(JRTextElement.TEXT_ALIGN_LEFT);
      textField.setFont(font);
      textField.setEvaluationTime(JRExpression.EVALUATION_TIME_REPORT);
      expression = new JRDesignExpression();
      expression.setValueClass(java.lang.String.class);
      expression.setText("\"共\" + String.valueOf($V{PAGE_NUMBER})+ \"页\"");
      textField.setExpression(expression);
      band.addElement(textField);
    }
    return band;
  }
}
