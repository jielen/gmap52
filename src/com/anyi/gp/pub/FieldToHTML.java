
package com.anyi.gp.pub;

import java.util.List;

import com.anyi.gp.meta.Field;
import com.anyi.gp.taglib.components.Page;
import com.anyi.gp.util.StringTools;

/**
 * <p>
 * Title: Field转换为html格式
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2002
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author unascribed
 * @version 1.0
 */
public class FieldToHTML {
  private FieldToHTML() {
  }

  /**
   * @Title 不可见Field的处理
   * @param tableName
   * @param field
   * @param value
   * @return
   */
  public static String invisibleFieldToHTML(String tableName, Field field,
      String value) {
    String curValue = field.getDefaultValue();
    if (value != null) {
      curValue = value;
    }
    String result = "<span fieldname=\"" + field.getName() + "\""
        + " display=\"none\" defaultValue=\"" + field.getDefaultValue() + "\""
        + " id=\"" + tableName + "_" + field.getName() + "ID\" value=\""
        + curValue + "\"></span>";
    return result;
  }

  /**
   * @Title
   * @param tableName
   * @param field
   * @param language
   * @return
   */
  public static String fieldCaptionToHTML(String tableName, Field field,
      String language, boolean visible) {
    if (language == null) {
      throw new IllegalArgumentException("FieldToHTML类的fieldCaptiontoHTML"
          + "方法：语种参数为null。");
    }
    String name = field.getName();
    LangResource resource = LangResource.getInstance();
    StringBuffer result = new StringBuffer();
    result.append("<span name=\"" + name + "Caption\"");
    result.append(" id=\"" + tableName + "_" + name + "CaptionID\"");
    if (!visible) {
      result.append(" style=\"display:none\"");
    }
    if (field.isPk()) {
      result.append(" class=\"keyFieldCaption\"");
    } else {
      result.append(" class=\"normalFieldCaption\"");
    }
    result.append(" fieldname=\"" + name + "\">");
    result.append(resource.getLang(name));
    String type = field.getType();
    if (type.equalsIgnoreCase("label")) {
      result.append("<a href =\"");
      result.append(field.getUrl());
      result.append("\" target = \"blank\">");
    }
    if (type.equalsIgnoreCase("label")) {
      result.append(":" + field.getDataItemDesc());
      result.append("</a>");
    }
    if ((!field.isAllowNull()) && visible && (!field.isSave())) {
      result.append("<span class=\"asterisk\">*</span>");
    }
    result.append("</span>");
    return result.toString();
  }

  /**
   * @param tableName
   * @param field
   * @param value
   * @param readOnly
   * @return
   */
  public static String fieldEditToHTML(String tableName, Field field,
      String value, boolean readOnly, boolean visible, int height) {
    String result = null;
    String type = field.getType();
    if (field.isFk() && field.getRefName()!=null) {
      result = foreignKeyToHTML(tableName, field, value, readOnly, visible);
    } else 
    	if (field.getVscode() != null) {
      result = valuesetToHTML(tableName, field, value, readOnly, visible);
    } else if (type.equalsIgnoreCase("text")) {
      result = textToHTML(tableName, field, value, readOnly, visible, height,
          false);
    } else if (type.equalsIgnoreCase("label")) {
      result = labelToHTML(tableName, field, value, readOnly, visible);
    } else if (type.equalsIgnoreCase("num")) {
      result = numToHTML(tableName, field, value, readOnly, visible);
    } else if (type.equalsIgnoreCase("date")) {
      result = dateToHTML(tableName, field, value, readOnly, visible);
    } else if (type.equalsIgnoreCase("datetime")) {
      result = datetimeToHTML(tableName, field, value, readOnly, visible);
    } else if (type.equalsIgnoreCase("Blob")) {
      result = textToHTML(tableName, field, value, readOnly, visible, height,
          true);
    } else if (type.equalsIgnoreCase("Seq")) { // chupp add
      result = textToHTML(tableName, field, value, readOnly, visible, height,
          true);
    } else {
      throw new IllegalArgumentException("未知的元素类型。");
    }
    return result;
  }

  private static String textToHTML(String tableName, Field field, String value,
      boolean readOnly, boolean visible, int height, boolean isBlob) {
    StringBuffer result = new StringBuffer();
    String name = field.getName();
    String curValue = field.getDefaultValue();
    if (value != null) {
      curValue = value;
    }
    if (curValue == null) {
      curValue = "";
    }
    result.append("<span id=\"" + tableName + "_" + name + "Span\"");
    if (!visible) {
      result.append(" style=\"display:none\"");
    }
    result.append(">");
    if (height > 1) {
      result.append("<textarea name=\"" + name + "Edit\" rows=\"" + height
          + "\"");
      result.append(" wrap=\"soft\" class=\"clsMemo\"");
    } else {
      result.append("<input type=\"text\" id=\"matchValue\" name=\"" + name
          + "Edit\"");
      result.append(" value=\"输入要搜索的关键字" + StringTools.packSpecial(curValue)
          + "\"");
      result
          .append(" ondblclick=\"matchValue_DblClick()\"  onFocus=\"matchValue_Focus()\" onBlur=\"matchValue_Blur()\" class=\"normalFrameFieldEdit\" size=\"20\"");
    }
    result.append(" id=\"" + tableName + "_" + name + "ID\"");
    result.append(" tablename=\"" + tableName + "\"");
    result.append(" fieldname=\"" + name + "\"");
    result.append(" default=\"" + field.getDefaultValue() + "\"");
    result.append(" fieldType=\"text\"");
    result.append(" minLength=\"" + field.getMinLength() + "\"");
    if (readOnly) {
      result.append(" alwaysReadonly=\"true\"");
    }
    if (isBlob) {
      result.append(" readonly");
    }
    if (field.isNoField()) {
      result.append(" autonum=\"true\"");
    } else {
      if (field.isNoRule()) {
        result.append(" norulenum=\"true\"");
      }
    }
    result.append(" maxlength=\"" + field.getMaxLength() + "\"");
    result.append(" onchange=\"text_Change();\"");
    result.append(" onkeypress=\"text_KeyPress();\">");
    if (height > 1) {
      result.append(StringTools.packSpecial(curValue) + "</textarea>");
    } else {
      result.append("</input>");
    }
    if (isBlob) {
      if (curValue == "") { // 附件上传判断是否有，无的话显示附加，有，则显示删除。
        result.append("<input name='" + name + "' tablename='" + tableName
            + "' type='button' value='附加' id='" + name + "_BWSID' "
            + "onclick='browseFile();' style='display:'/>");
        result.append("<input name='" + name + "' tablename='" + tableName
            + "' type='button' value='删除' id='" + name + "_DELID' "
            + "onclick='deleteFile();' style='display:none'/>");
      } else {
        result.append("<input name='" + name + "' tablename='" + tableName
            + "' type='button' value='附加' id='" + name + "_BWSID' "
            + "onclick='browseFile();' style='display:none'/>");
        result.append("<input name='" + name + "' tablename='" + tableName
            + "' type='button' value='删除' id='" + name + "_DELID' "
            + "onclick='deleteFile();' style='display:'/>");
      }
      result.append("<input name='" + name + "' tablename='" + tableName
          + "' type='button' value='打开'  onclick='readFile();'/>");

    }
    result.append("</span>");
    return result.toString();
  }

  private static String labelToHTML(String tableName, Field field,
      String value, boolean readOnly, boolean visible) {
    StringBuffer result = new StringBuffer();
    String name = field.getName();
    String curValue = field.getDefaultValue();
    if (value != null) {
      curValue = value;
    }
    if (curValue == null) {
      curValue = "";
    }
    result.append("<span id=\"" + tableName + "_" + name + "Span\"");
    if (!visible) {
      result.append(" style=\"display:none\"");
    }
    result.append(">");
    result.append("<input type=\"hidden\" name=\"" + name + "Edit\"");
    result.append(" value=\"" + StringTools.packSpecial(curValue) + "\"");
    result.append(" id=\"" + tableName + "_" + name + "ID\"");
    result.append(" tablename=\"" + tableName + "\"");
    result.append(" fieldname=\"" + name + "\"");
    result.append(" default=\"" + field.getDefaultValue() + "\"");
    result.append(" fieldType=\"label\"");
    result.append(" minLength=\"" + field.getMinLength() + "\"");
    if (readOnly) {
      result.append(" alwaysReadonly=\"true\"");
    }
    result.append(" maxlength=\"" + field.getMaxLength() + "\"");
    result.append("</input>");
    result.append("</span>");
    return result.toString();
  }

  private static String numToHTML(String tableName, Field field, String value,
      boolean readOnly, boolean visible) {
    StringBuffer result = new StringBuffer();
    String name = field.getName();
    String curValue = field.getDefaultValue();
    boolean isKiloStyle = field.getKiloStyle();
    if (value != null) {
      curValue = value;
    }
    int decLen = field.getDecLength();
    if (decLen > 0) {
      curValue = StringTools.addZero(curValue, decLen);
    }
    result.append("<span id=\"" + tableName + "_" + name + "Span\"");
    if (!visible) {
      result.append(" style=\"display:none\"");
    }
    result.append(">");
    result.append("<input type=\"text\" kiloStyle=\"" + isKiloStyle
        + "\" name=\"" + name + "Edit\"");
    result.append(" id=\"" + tableName + "_" + name + "ID\"");
    result.append(" tablename=\"" + tableName + "\"");
    result.append(" fieldname=\"" + name + "\"");
    if (isKiloStyle) {
      result.append(" value=\"" + StringTools.packSpecial(curValue) + "\"");
    } else {
      result.append(" value=\"输入要搜索的关键字" + StringTools.packSpecial(curValue)
          + "\"");
    }
    result.append(" default=\"" + field.getDefaultValue() + "\"");
    if (readOnly) {
      result.append(" alwaysReadonly=\"true\"");
    }
    result
        .append(" ondblclick=\"matchValue_DblClick()\"  onFocus=\"matchValue_Focus()\" onBlur=\"matchValue_Blur()\" class=\"normalFrameFieldEdit\" size=\"20\" ");
    result.append(" fieldType=\"num\"");
    if (field.getMinValue() != defaultNumValue) {
      result.append(" minValue=\"" + field.getMinValue() + "\"");
    }
    if ((field.getMaxValue() > field.getMinValue())
        && (field.getMaxValue() != defaultNumValue)) {
      result.append(" maxValue=\"" + field.getMaxValue() + "\"");
    }
    result.append(" decLength=\"" + field.getDecLength() + "\"");
    result.append(" maxlength=\"" + field.getMaxLength() + "\"");
    result.append(" onkeypress=\"num_KeyPress();\"");
    result.append(" onblur=\"field_Blur();\"");
    result.append(" onfocus=\"field_Focus();\"");
    result.append(" onchange=\"num_Change();\">");
    result.append("</input>");
    result.append("</span>");
    return result.toString();
  }

  private static String dateToHTML(String tableName, Field field, String value,
      boolean readOnly, boolean visible) {
    StringBuffer result = new StringBuffer();
    String name = field.getName();
    String curValue = field.getDefaultValue();
    if (value != null) {
      curValue = value;
    }
    result.append("<span id=\"" + tableName + "_" + name + "Span\"");
    if (!visible) {
      result.append(" style=\"display:none\"");
    }
    result.append(">");
    result.append("<input type=\"text\" name=\"" + name + "Edit\"");
    result.append(" id=\"" + tableName + "_" + name + "ID\"");
    result.append(" tablename=\"" + tableName + "\"");
    result.append(" fieldname=\"" + name + "\"");
    result.append(" value=\"" + curValue + "\"");
    result.append(" default=\"" + field.getDefaultValue() + "\"");
    result.append(" class=\"normalFieldEdit\"");
    if (readOnly) {
      result.append(" alwaysReadonly=\"true\"");
    }
    result.append(" fieldType=\"date\"");
    result.append(" maxlength=\"10\"");
    result.append(" onkeypress=\"date_KeyPress();\"");
    result.append(" onchange=\"date_Change();\">");
    result.append("</input>");

    result.append("<img fieldname=\"" + name + "\"");
    result.append(" tablename=\"" + tableName + "\"");
    result.append(" id=\"" + tableName + "_" + name + "DateIMGID\"");
    result
        .append(" src=\"" + Page.LOCAL_RESOURCE_PATH + "style/img/main/calendar.gif\" width=\"20\" height=\"18\"");
    result.append(" align=\"absbottom\"");
    result.append(" class=\"foreignIMG\"");
    if (readOnly) {
      result.append(" style=\"display:none\"");
    }
    result.append(" onmouseenter=\"mouseEnterForeignIMG();\"");
    result.append(" onmouseout=\"mouseOutForeignIMG();\"");
    result.append(" onmousedown=\"mouseDown();\"");
    result.append(" onmouseup=\"mouseUp();\"");
    result.append(" onclick=\"Date_Select('" + tableName + "_" + name + "ID','" + name
        + "')\">");
    result.append("</img>");

    result.append("</span>");
    return result.toString();
  }

  private static String datetimeToHTML(String tableName, Field field,
      String value, boolean readOnly, boolean visible) {
    StringBuffer result = new StringBuffer();
    String name = field.getName();
    String curValue = field.getDefaultValue();
    if (value != null) {
      curValue = value;
    }
    result.append("<span id=\"" + tableName + "_" + name + "Span\"");
    if (!visible) {
      result.append(" style=\"display:none\"");
    }
    result.append(">");
    result.append("<input type=\"text\" name=\"" + name + "Edit\"");
    result.append(" id=\"" + tableName + "_" + name + "ID\"");
    result.append(" tablename=\"" + tableName + "\"");
    result.append(" fieldname=\"" + name + "\"");
    result.append(" value=\"" + curValue + "\"");
    result.append(" default=\"" + field.getDefaultValue() + "\"");
    result.append(" class=\"normalFieldEdit\"");
    if (readOnly) {
      result.append(" alwaysReadonly=\"true\"");
    }
    result.append(" fieldType=\"datetime\"");
    result.append(" maxlength=\"" + field.getMaxLength() + "\"");
    result.append(" onkeypress=\"datetime_KeyPress();\"");
    result.append(" onchange=\"datetime_Change();\">");
    result.append("</input>");
    result.append("<img fieldname=\"" + name + "\"");
    result.append(" tablename=\"" + tableName + "\"");
    result.append(" id=\"" + tableName + "_" + name + "DateIMGID\"");
    result
        .append(" src=\"" + Page.LOCAL_RESOURCE_PATH + "style/img/main/calendar.gif\" width=\"20\" height=\"18\"");
    result.append(" align=\"absbottom\"");
    result.append(" class=\"foreignIMG\"");
    result.append(" onmouseenter=\"mouseEnterForeignIMG();\"");
    result.append(" onmouseout=\"mouseOutForeignIMG();\"");
    result.append(" onmousedown=\"mouseDown();\"");
    result.append(" onmouseup=\"mouseUp();\"");
    result.append(" onclick=\"Datetime_Select('" + tableName + "','" + name
        + "')\">");
    result.append("</img>");
    result.append("</span>");
    return result.toString();
  }

  private static String valuesetToHTML(String tableName, Field field,
      String value, boolean readOnly, boolean visible) {
    String vsCode = field.getVscode();
    boolean isNull = field.isAllowNull();
    List valSet = GeneralFunc.getValueSet(vsCode, isNull, null);
    StringBuffer result = new StringBuffer();
    String name = field.getName();
    result.append("<span id=\"" + tableName + "_" + name + "Span\"");
    if (!visible) {
      result.append(" style=\"display:none\"");
    }
    result.append(">");
    result.append("<select id=\"" + tableName + "_" + name + "IDS\"");
    result.append(" name=\"" + name + "Select\"");
    result.append(" tablename=\"" + tableName + "\"");
    result.append(" fieldname=\"" + name + "\"");
    if (field.getVsEffectTable() != null) {
      result.append(" onchange=\"valueSet_S_Filter('");
      result.append(field.getVsEffectTable() + "')\"");
    } else {
      result.append(" onchange=\"valueSet_S_Change();\"");
    }
    result.append(" onblur=\"valueSet_S_Blur();\">");
    String key, kvalue;
    int currIndex = 0;
    String currValue = null;
    for (int i = 0; i < valSet.size(); i++) {
      String[] setVal = (String[]) valSet.get(i);
      key = setVal[0];
      kvalue = setVal[1];
      if (i == 0) {
        currValue = kvalue;
      }
      result.append("<option value='" + key + "'>" + kvalue + "</option>");
      // 查找值集序号时忽略大小写更改为大小写敏感;leidh;20040409;
      if (key.equals(value)) {
        currIndex = i;
        currValue = kvalue;
      }
    }
    result.append("</select>");
    result.append("<input type=\"text\" id=\"" + tableName + "_" + name
        + "ID\" name=\"" + name + "\"");
    result.append(" class=\"normalFieldEdit\"");
    result.append(" tablename=\"" + tableName + "\"");
    result.append(" fieldname=\"" + name + "\"");
    result.append(" fieldType=\"select\"");
    result.append(" default=\"" + field.getDefaultValue() + "\"");
    result.append(" value=\"" + currValue + "\"");
    if (field.getVsEffectTable() != null) {
      result.append(" hasEffectTable=\"true\"");
    }
    result.append(" read=\"false\"");
    result.append(" readonly");
    if (readOnly) {
      result.append(" alwaysReadonly=\"true\"");
    }
    result.append(" onfocus=\"valueSet_I_Focus()\">");
    result.append("</input>");
    result.append("</span>");
    result.append("<script language=\"JavaScript\">");
    result.append("var select=document.getElementById(\"" + tableName + "_"
        + name + "IDS\");");
    result.append("select.selectedIndex=" + currIndex + ";");
    result.append("select.style.display=\"none\";");
    result.append("</script>");
    return result.toString();
  }

  private static String foreignKeyToHTML(String tableName, Field field,
      String value, boolean readOnly, boolean visible) {
    StringBuffer result = new StringBuffer();
    String name = field.getName();
    String curValue = field.getDefaultValue();
    if (value != null) {
      curValue = value;
    }
    result.append("<span id=\"" + tableName + "_" + name + "Span\"");
    if (!visible) {
      result.append(" style=\"display:none\"");
    }
    result.append(">");
    result.append("<input type=\"text\" name=\"" + name + "Edit\"");
    result.append(" id=\"" + tableName + "_" + name + "ID\"");
    result.append(" tablename=\"" + tableName + "\"");
    result.append(" fieldname=\"" + name + "\"");
    if (field.isTreeView()) {
      result.append(" treeview=\"true\"");
    } else {
      result.append(" treeview=\"false\"");
    }
    result.append(" value=\"" + curValue + "\"");
    result.append(" isRight=\"Y\"");
    result.append(" default=\"" + field.getDefaultValue() + "\"");
    if (readOnly) {
      result.append(" alwaysReadonly=\"true\"");
    }
    result.append(" class=\"normalFieldEdit\"");
    result.append(" fieldType=\"foreignKey\"");
    result.append(" onchange=\"foreignKey_Change('" + field.getRefName()
        + "');\">");
    result.append("</input>");
    if (!readOnly) {
      result.append("<img fieldname=\"" + name + "\"");
      result.append(" tablename=\"" + tableName + "\"");
      if (field.isTreeView()) {
        result.append(" treeview=\"true\"");
      } else {
        result.append(" treeview=\"false\"");
      }
      result.append(" id=\"" + tableName + "_" + name + "ForeignIMGID\"");
      result.append(" src=\"" + Page.LOCAL_RESOURCE_PATH + "style/img/main/search.gif\"");
      result.append(" align=\"absbottom\"");
      result.append(" class=\"foreignIMG\"");
      result.append(" onmouseenter=\"mouseEnterForeignIMG();\"");
      result.append(" onmouseout=\"mouseOutForeignIMG();\"");
      result.append(" onmousedown=\"mouseDown();\"");
      result.append(" onmouseup=\"mouseUp();\"");
      result.append(" onclick=\"foreign_Select('" + field.getRefName()
          + "',null,false)\">");
      result.append("</img>");
    }
    result.append("</span>");
    return result.toString();
  }

  private final static int defaultNumValue = 1234567890;
}
