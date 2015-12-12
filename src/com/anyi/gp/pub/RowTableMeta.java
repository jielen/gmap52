package com.anyi.gp.pub;

import java.util.List;

import org.w3c.dom.Element;

import com.anyi.gp.meta.Field;
import com.anyi.gp.meta.TableMeta;
import com.anyi.gp.util.StringTools;

public class RowTableMeta {

  public String tableName = "";

  public String physicaltable = "";

  public String digest = "";

  public String isdigest = "false";

  public String keyfields = "";

  public String numericFields = "";

  public String notsaveFields = "";

  public String dateFields = "";
   
  public String datetimeFields = "";
  
  public String compoName = "";
  
  public String issave = "false";
  
  public String sqlid = "";
  
  public String condition = "";
  
  public String fromrow = "0";
  
  public String torow = "0";
  
  public String searchCond = "";
  
  public String searchType = "";
  
  public String userNumLimCondition = "";
  
  public String onceautonumfields = "";
  
  public String onceautonums = "";
  
  public String noAutoNumFields = "";
  public RowTableMeta(Element element) {
    this.dateFields = element.getAttribute("datefields");
    this.datetimeFields = element.getAttribute("datetimefields");
    this.digest = element.getAttribute("digest");
    this.isdigest = element.getAttribute("isdigest");
    this.keyfields = element.getAttribute("keyfields");
    this.notsaveFields = element.getAttribute("notsavefields");
    this.numericFields = element.getAttribute("numericfields");
    this.physicaltable = element.getAttribute("physicaltable");
    this.tableName = element.getAttribute("name");
    this.compoName = element.getAttribute("componame");
    this.issave = element.getAttribute("issave");
    this.sqlid = element.getAttribute("sqlid");
    this.condition = element.getAttribute("condition");
    this.fromrow = element.getAttribute("fromrow");
    this.torow = element.getAttribute("torow");
    this.searchCond = element.getAttribute("searchCond");
    this.searchType = element.getAttribute("searchType");
    this.userNumLimCondition = element.getAttribute("userNumLimCondition");
    this.onceautonumfields = element.getAttribute("onceautonumfields");
    this.onceautonums = element.getAttribute("onceautonums");
    this.noAutoNumFields = element.getAttribute("noAutoNumFields");
  }

  public RowTableMeta(String compoName, TableMeta tableMeta){
    initRowTableMeta(compoName, tableMeta);
  }
  
  private void initRowTableMeta(String compoName, TableMeta tableMeta){
    this.compoName = compoName;
    
    if(tableMeta != null){
      this.tableName = tableMeta.getName();
      
      List fieldList = tableMeta.getKeyFieldNames();
      this.keyfields = StringTools.parseString(fieldList, ",");      

      fieldList = tableMeta.getNoSaveFieldNames();
      this.notsaveFields = StringTools.parseString(fieldList, ",");
      
      fieldList = tableMeta.getFieldNames();
      StringBuffer voNumFieldBuf = new StringBuffer();
      StringBuffer voDateFieldBuf = new StringBuffer();
      StringBuffer voDatetimeFieldBuf = new StringBuffer();
     
      if (fieldList != null) {
        for (int i = 0; i < fieldList.size(); i++) {
          Field voField = tableMeta.getField((String) fieldList.get(i));
          if (Field.DATA_TYPE_NUM.equals(voField.getType().toUpperCase())) {
            if (voNumFieldBuf.length() > 0)
              voNumFieldBuf.append(",");
            voNumFieldBuf.append(fieldList.get(i));
          }
          if (Field.DATA_TYPE_DATE.equals(voField.getType().toUpperCase())) {
            if (voDateFieldBuf.length() > 0)
              voDateFieldBuf.append(",");
            voDateFieldBuf.append(fieldList.get(i));
          }
          if (Field.DATA_TYPE_DATETIME.equals(voField.getType().toUpperCase())) {
            if (voDatetimeFieldBuf.length() > 0)
              voDatetimeFieldBuf.append(",");
            voDatetimeFieldBuf.append(fieldList.get(i));
          }
        }
      }
      
      this.numericFields = voNumFieldBuf.toString();
      this.dateFields = voDateFieldBuf.toString();
      this.datetimeFields = voDatetimeFieldBuf.toString();
    }    
  }
  
  public String toString() {
    StringBuffer result = new StringBuffer();
    result.append("<table name=\"");
    result.append(tableName);
    result.append("\" physicaltable=\"");
    result.append(physicaltable);
    result.append("\" digest=\"");
    result.append(digest);
    result.append("\" isdigest=\"");
    result.append(isdigest);
    result.append("\" keyfields=\"");
    result.append(keyfields);
    result.append("\" numericfields=\"");
    result.append(numericFields);
    result.append("\" notsavefields=\"");
    result.append(notsaveFields);
    result.append("\" datefields=\"");
    result.append(dateFields);
    result.append("\" datetimefields=\"");
    result.append(datetimeFields); 
    result.append("\" componame=\"");
    result.append(compoName);
    result.append("\" issave=\"");
    result.append(issave);
    result.append("\" sqlid=\"");
    result.append(sqlid);
    result.append("\" condition=\"");
    result.append(condition);
    result.append("\" fromrow=\"");
    result.append(fromrow);
    result.append("\" torow=\"");
    result.append(torow);
    result.append("\" searchCond=\"");
    result.append(searchCond);
    result.append("\" searchType=\"");
    result.append(searchType);
    result.append("\" userNumLimCondition=\"");
    result.append(userNumLimCondition);
    result.append("\" onceautonumfields=\"");
    result.append(onceautonumfields);
    result.append("\" onceautonums=\"");
    result.append(onceautonums);
    result.append("\" noAutoNumFields=\"");
    result.append(noAutoNumFields);
    result.append("\" />");
    return result.toString();
  }
}
