package com.anyi.gp.taglib.components;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Node;

import com.anyi.gp.Pub;
import com.anyi.gp.util.XMLTools;

public class EditBoxFactory {

  public static final EditBox makeEditBox(Node node,Container container) {
    EditBox result = null;
    String boxType = XMLTools.getNodeAttr(node, "editboxtype","TextBox").toUpperCase();
    if (boxType.equals("TEXTBOX")){
      result = new TextBox();
    }else if (boxType.equals("FOREIGNBOX")){
      result = new ForeignBox();
    }else if (boxType.equals("DATEBOX")){
      result = new DateBox("DateBox");
    }else if (boxType.equals("DATETIMEBOX")){
      result = new DateBox("DatetimeBox");
    }else if (boxType.equals("COMBOBOX")){
      result = new ComboBox();
    }else if (boxType.equals("FILEBOX")){
      result = new FileBox();
    }else if (boxType.equals("IMAGEBOX")){
      result = new ImageBox();
    }else if (boxType.equals("NUMERICBOX")){
      result = new NumericBox();
    }else if (boxType.equals("TEXTAREABOX")){
      result = new TextAreaBox();
    }else {
      throw new RuntimeException("不支持的编辑框类型");
    }
    result.setContainer(container);
    result.initByField(node);
    return result;
  }

  public static final Map batchMakeEditBoxByFields(List fieldNameList, Map fieldMap,
    String compoName, String tableName, String ownerType, boolean isFromDB,
    boolean isOwnerWritable, String idSuffix, String style, String cssClass,
    Container container) {
    if (fieldNameList == null)
      return null;
    if (fieldMap == null)
      return null;
    Map voBoxMap = new HashMap();
    for (int i = 0; i < fieldNameList.size(); i++) {
      String vsField = (String) fieldNameList.get(i);
      Node voField = (Node) fieldMap.get(vsField);
      Node mergedField = prepareElement(voField, compoName, tableName, ownerType,
        isFromDB, isOwnerWritable, idSuffix, style, cssClass);
      EditBox voBox = makeEditBox(mergedField,container);
      voBoxMap.put(vsField, voBox);
    }
    return voBoxMap;
  }

  private static Node prepareElement(Node voField, String compoName,
    String tableName, String ownerType, boolean isFromDB, boolean isOwnerWritable,
    String idSuffix, String style, String cssClass) {

    Node result = voField.cloneNode(true);
    XMLTools.setNodeAttr(result, "idsuffix", idSuffix + Pub.getUID());
    XMLTools.setNodeAttr(result, "isfromdb", Boolean.toString(isFromDB));
    XMLTools.setNodeAttr(result, "componame", compoName);
    XMLTools.setNodeAttr(result, "tablename", tableName);
    XMLTools.setNodeAttr(result, "fieldname", XMLTools.getNodeAttr(result, "name"));
    if (TextBox.OWNER_TYPE_GRID.equals(ownerType)) {
      XMLTools.setNodeAttr(result, "isvisible", "false");
      XMLTools.setNodeAttr(result, "isfreemember", "false");
      XMLTools.setNodeAttr(result, "isalone", "false");
    } else if (TextBox.OWNER_TYPE_BOXSET.equals(ownerType)) {
      XMLTools.setNodeAttr(result, "isalone", "true");
    }
    if (!isOwnerWritable) {
      XMLTools.setNodeAttr(result, "isforcereadonly", "true");
    }
    XMLTools.setNodeAttr(result, "ownertype", ownerType);
    XMLTools.setNodeAttr(result, "style", XMLTools.getNodeAttr(result, "style") + ";" + style);
    XMLTools.setNodeAttr(result, "cssclass", XMLTools.getNodeAttr(result, "cssclass") + " " +  cssClass);
    XMLTools.setNodeAttr(result, "id", XMLTools.getNodeAttr(voField, "boxsetboxid",
      ""));
    return result;
  }

}
