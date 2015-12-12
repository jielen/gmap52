package com.anyi.gp.meta;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class CompoMeta {

  private String name;

  private String parentName;

  private String noField;

  private String typeField;

  private String typeTable;

  private String wfFlowType;

  private String wfListType;

  private String wfDefTemp;

  private boolean wfTempUsed;

  private String titleField;

  private List autoNumFields;

  private String masterTable;

  private boolean autoList = false;

  private String orderBy;

  private boolean grantToAll = false;

  private String dateField;

  private String titleDate;

  private String printType = "0";

  private String valsetField;

  private String briefFields;

  private String iconName;

  private Map calls = new HashMap();

  private TableMeta tableMeta;

  private String _xml = "";

  public List getAutoNumFields() {
    return autoNumFields;
  }

  public void setAutoNumFields(List autoNumFields) {
    this.autoNumFields = autoNumFields;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getNoField() {
    return noField;
  }

  public void setNoField(String noField) {
    this.noField = noField;
  }

  public String getTitleField() {
    return titleField;
  }

  public void setTitleField(String titleField) {
    this.titleField = titleField;
  }

  public String getTypeField() {
    return typeField;
  }

  public void setTypeField(String typeField) {
    this.typeField = typeField;
  }

  public String getTypeTable() {
    return typeTable;
  }

  public void setTypeTable(String typeTable) {
    this.typeTable = typeTable;
  }

  public String getWfDefTemp() {
    return wfDefTemp;
  }

  public void setWfDefTemp(String wfDefTemp) {
    this.wfDefTemp = wfDefTemp;
  }

  public String getWfFlowType() {
    return wfFlowType;
  }

  public void setWfFlowType(String wfFlowType) {
    this.wfFlowType = wfFlowType;
  }

  public String getWfListType() {
    return wfListType;
  }

  public void setWfListType(String wfListType) {
    this.wfListType = wfListType;
  }

  public boolean isWfTempUsed() {
    return wfTempUsed;
  }

  public void setWfTempUsed(boolean wfTempUsed) {
    this.wfTempUsed = wfTempUsed;
  }

  public Map getCalls() {
    return calls;
  }

  public void setCalls(Map calls) {
    this.calls = calls;
  }

  public void setCalls(List calls) {
    if (!calls.isEmpty()) {
      for (int i = 0; i < calls.size(); i++) {
        addCall((Call) calls.get(i));
      }
    }
  }

  public void addCall(Call call) {
    this.calls.put(call.getName(), call);
  }

  public String getMasterTable() {
    return masterTable;
  }

  public void setMasterTable(String masterTable) {
    this.masterTable = masterTable;
  }

  public String getParentName() {
    return parentName;
  }

  public void setParentName(String parentName) {
    this.parentName = parentName;
  }

  public boolean isCompoSupportWF() {
    return isWfTempUsed();
  }

  public boolean isAutoList() {
    return autoList;
  }

  public void setAutoList(boolean autoList) {
    this.autoList = autoList;
  }

  public String getDateField() {
    return dateField;
  }

  public void setDateField(String dateField) {
    this.dateField = dateField;
  }

  public boolean isGrantToAll() {
    return grantToAll;
  }

  public void setGrantToAll(boolean grantToAll) {
    this.grantToAll = grantToAll;
  }

  public String getOrderBy() {
    return orderBy;
  }

  public void setOrderBy(String orderBy) {
    this.orderBy = orderBy;
  }

  public String getPrintType() {
    return printType;
  }

  public void setPrintType(String printType) {
    this.printType = printType;
  }

  public TableMeta getTableMeta() {
    return tableMeta;
  }

  public void setTableMeta(TableMeta tableMeta) {
    this.tableMeta = tableMeta;
  }

  public String getTitleDate() {
    return titleDate;
  }

  public void setTitleDate(String titleDate) {
    this.titleDate = titleDate;
  }

  public String getValsetField() {
    return valsetField;
  }

  public void setValsetField(String valsetField) {
    this.valsetField = valsetField;
  }

  public String getBriefFields() {
    return briefFields;
  }

  public void setBriefFields(String briefFields) {
    this.briefFields = briefFields;
  }

  public String getIconName() {
    return iconName;
  }

  public void setIconName(String iconName) {
    this.iconName = iconName;
  }

  public boolean isNoField(String fieldName) {
    if (fieldName == null) {
      return false;
    }
    return fieldName.equalsIgnoreCase(noField)
      || (autoNumFields != null && autoNumFields.contains(fieldName));
  }

  public String toXml() {
    if (_xml.length() == 0) {
      StringBuffer buf = new StringBuffer();
      buf.append("<xml id=\"CompoMeta_" + name
        + "_XML\" asynch=\"false\" encoding=\"GBK\">\n");
      buf.append("<compometa name=\"");
      buf.append(name);
      buf.append("\" nofield=\"");
      buf.append(noField);
      buf.append("\" typefield=\"");
      buf.append(typeField);
      buf.append("\" typetable=\"");
      buf.append(typeTable);
      buf.append("\" wfflowtype=\"");
      buf.append(wfFlowType);
      buf.append("\" wflisttype=\"");
      buf.append(wfListType);
      buf.append("\" wfdeftemp=\"");
      buf.append(wfDefTemp);
      buf.append("\" iswfusedtemp=\"");
      buf.append(wfTempUsed);
      buf.append("\" titlefield=\"");
      buf.append(titleField);
      buf.append("\" autonumfields=\"");
      if (autoNumFields != null) {
        for (int i = 0; i < autoNumFields.size(); i++) {
          if (i == 0)
            buf.append(autoNumFields.get(i));
          else
            buf.append("," + autoNumFields.get(i));
        }

      }
      buf.append("\">\n");
      buf.append("<tables>\n");
      if (tableMeta != null)
        buf.append(tableMeta.getAllTableNames(true));
      buf.append("</tables>\n");
      buf.append("<calls>\n");
      if (calls != null) {
        Iterator itera = calls.entrySet().iterator();
        while (itera.hasNext()) {
          Entry entry = (Entry) itera.next();
          buf.append(((Call) entry.getValue()).toXML());
        }
      }
      buf.append("</calls>\n");
      buf.append("</compometa>\n");
      buf.append("</xml>\n");
      _xml = buf.toString();
    }
    return _xml;
  }
}
