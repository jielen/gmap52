/**
 * 此类仅用于对 ActiveX 客户端提供数据支持;与此无关者莫入;
 * leidh;20060818;
 */

package com.anyi.gp.pub;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.anyi.gp.BusinessException;
import com.anyi.gp.Delta;
import com.anyi.gp.TableData;
import com.anyi.gp.meta.Field;
import com.anyi.gp.meta.MetaManager;
import com.anyi.gp.meta.TableMeta;
import com.anyi.gp.util.StringTools;

public class AxDataProviderFacade {
  private static final Logger log = Logger.getLogger(AxDataProviderFacade.class);

  public static String deltaToAxListData(Delta delta, TableMeta tableMeta,
    String newDigest) {
    StringBuffer s = new StringBuffer();
    //s.append("<?xml version=\"1.0\" encoding=\"gb2312\" standalone=\"yes\"?>\n");
    s.append("<root>\n");
    s.append(deltaToAxData(delta, tableMeta, ""));
    s.append("</root>\n");
    log.debug("\ndeltaToAxListData():\n" + s);
    return s.toString();
  }

  public static String deltaToAxData(Delta delta, TableMeta tableMeta,
    String newDigest) {
    return deltaToAxData(delta, makeAxDataMeta(tableMeta), newDigest);
  }

  public static String deltaToAxData(Delta delta, String axMeta, String newDigest) {
    StringBuffer s = new StringBuffer();
    s.append("<table name=\"");
    s.append(delta.getName());
    s.append("\" digest=\"");
    s.append(newDigest);
    s.append("\">\n");
    s.append("<DATAPACKET Version=\"2.0\">\n");
    s.append(axMeta);
    s.append(deltaToAxData(delta));
    s.append("</DATAPACKET>\n");
    s.append("</table>\n");
    //System.out.println(s.toString());
    return s.toString();
  }

  public static String makeAxDataMeta(TableMeta tableMeta) {
    StringBuffer s = new StringBuffer();
    s.append("<METADATA>\n");
    s.append("<FIELDS>\n");
    for (Iterator iter = tableMeta.getFieldNames().iterator(); iter.hasNext();) {
      Field field = tableMeta.getField((String) iter.next());
      s.append("<FIELD attrname=\"");
      s.append(field.getName());
      s.append("\" fieldtype=\"");
      s.append(gpFieldTypeToAx(field));
      s.append("\" WIDTH=\"");
      s.append(field.getMaxLength());
      s.append("\" />\n");
    }
    s.append("</FIELDS>\n");
    s.append("</METADATA>\n");
    return s.toString();
  }

  public static String deltaToAxData(Delta delta) {
    StringBuffer s = new StringBuffer();
    s.append("<ROWDATA>\n");
    for (Iterator iter = delta.iterator(); iter.hasNext();) {
      TableData tableData = (TableData) iter.next();
      s.append(tableDataToAxData(tableData));
    }
    s.append("</ROWDATA>\n");
    return s.toString();
  }

  public static String tableDataToAxData(TableData tableData) {
    StringBuffer s = new StringBuffer();
    s.append("<ROW ");
    for (Iterator iter = tableData.getFieldNames().iterator(); iter.hasNext();) {
      String fieldName = (String) iter.next();
      String value = tableData.getFieldValue(fieldName);
      if (value != null) {
        if (tableData.getKiloFieldList() != null
          && tableData.getKiloFieldList().contains(fieldName)) {
          value = StringTools.kiloStyle(value);
        } else if (tableData.getDateFieldList() != null
          && tableData.getDateFieldList().contains(fieldName)) {
          value = StringTools.formatDate(value);
        }
      }
      s.append(fieldName);
      s.append("=\"");
      s.append(StringTools.toXMLString(value));
      s.append("\" ");
    }
    s.append("/>\n");
    return s.toString();
  }

  public static String getCompoInfo(String compoName, String requestDataType)
    throws BusinessException {
    StringBuffer buf = new StringBuffer();
    buf.append("<compo name=\"");
    buf.append(compoName);
    buf.append("\" ismain=\"");
    buf.append("");
    buf.append("\" requestdatatype=\"");
    buf.append(requestDataType);
    buf.append("\">\n");
    TableMeta entityMeta = MetaManager.getTableMetaByCompoName(compoName);
    buf.append(getTableInfo(entityMeta, requestDataType));
    buf.append("</compo>\n");
    return buf.toString();
  }

  public static String getTableInfo(TableMeta tableMeta, String requestDataType)
    throws BusinessException {
    StringBuffer buf = new StringBuffer();
    buf.append("<table name=\"");
    buf.append(tableMeta.getName());
    buf.append("\" ");
    buf.append("physicaltable=\"");
    buf.append("");
    buf.append("\" ");
    buf.append("isdigest=\"");
    buf.append("edit".equalsIgnoreCase(requestDataType) ? true : false);
    buf.append("\" ");
    buf.append("digest=\"");
    buf.append("");
    buf.append("\" ");

    List keyFieldList = tableMeta.getKeyFieldNames();
    StringBuffer voKeyFieldBuf = new StringBuffer();
    if (keyFieldList != null) {
      for (int i = 0; i < keyFieldList.size(); i++) {
        if (i > 0)
          voKeyFieldBuf.append(",");
        voKeyFieldBuf.append(keyFieldList.get(i));
      }
    }
    buf.append("keyfields=\"");
    buf.append(voKeyFieldBuf.toString());
    buf.append("\" ");

    List fieldNameList = tableMeta.getFieldNames();
    StringBuffer voNumFieldBuf = new StringBuffer();
    StringBuffer voDateFieldBuf = new StringBuffer();
    StringBuffer voDatetimeFieldBuf = new StringBuffer();

    if (fieldNameList != null) {
      for (int i = 0; i < fieldNameList.size(); i++) {
        Field voField = tableMeta.getField((String) fieldNameList.get(i));
        if (Field.DATA_TYPE_NUM.equals(voField.getType().toUpperCase())) {
          if (voNumFieldBuf.length() > 0)
            voNumFieldBuf.append(",");
          voNumFieldBuf.append(fieldNameList.get(i));
        }
        if (Field.DATA_TYPE_DATE.equals(voField.getType().toUpperCase())) {
          if (voDateFieldBuf.length() > 0)
            voDateFieldBuf.append(",");
          voDateFieldBuf.append(fieldNameList.get(i));
        }
        if (Field.DATA_TYPE_DATETIME.equals(voField.getType().toUpperCase())) {
          if (voDatetimeFieldBuf.length() > 0)
            voDatetimeFieldBuf.append(",");
          voDatetimeFieldBuf.append(fieldNameList.get(i));
        }
      }
    }
    buf.append("numericfields=\"");
    buf.append(voNumFieldBuf.toString());
    buf.append("\" ");
    buf.append("datefields=\"");
    buf.append(voDateFieldBuf.toString());
    buf.append("\" ");
    buf.append("datetimefields=\"");
    buf.append(voDatetimeFieldBuf.toString());
    buf.append("\" ");
    buf.append("onceautonumfields=\"");
    buf.append("");
    buf.append("\" ");
    buf.append("onceautonums=\"\" ");

    List notSaveFieldList = tableMeta.getNoSaveFieldNames();
    StringBuffer voNotSaveFieldBuf = new StringBuffer();
    if (notSaveFieldList != null) {
      for (int i = 0; i < notSaveFieldList.size(); i++) {
        if (i > 0)
          voNotSaveFieldBuf.append(",");
        voNotSaveFieldBuf.append(notSaveFieldList.get(i));
      }
    }
    buf.append("notsavefields=\"");
    buf.append(voNotSaveFieldBuf.toString());
    buf.append("\" ");
    buf.append(">\n");

    //处理子表;
    List childTableNameList = tableMeta.getChildTableNames();
    if (childTableNameList != null && !childTableNameList.isEmpty()) {
      for (Iterator iter = childTableNameList.iterator(); iter.hasNext();) {
        buf.append(getTableInfo(tableMeta.getTableMeta((String) iter.next(), true),
          requestDataType));
      }
    }
    buf.append("</table>\n");
    return buf.toString();
  }

  public static String getDataBySql(String table, String sql, String kiloFields,
    String dsName) throws BusinessException {
    if (sql == null)
      return "";
    if (sql.toUpperCase().trim().indexOf("SELECT") != 0)
      return "";
    Connection conn = null;
    Statement stmt = null;
    ResultSet rs = null;
    Delta delta = null;
    String axMeta = "";
    try {
      if (StringTools.isEmptyString(dsName)) {
        conn = DAOFactory.getInstance().getConnection();
      } else {
        conn = DAOFactory.getInstance().getConnection(dsName);
      }
      stmt = conn.createStatement();
      rs = stmt.executeQuery(sql);
      delta = new Delta(rs);
      axMeta = makeAxDataMeta(rs);
    } catch (SQLException e) {
      throw new BusinessException("\nAxDataProviderFacade.getDataBySql(): "
        + e.toString());
    } finally {
      DBHelper.closeConnection(conn, stmt, rs);
    }

    delta.setPageSize(0x7FFFFFFF);
    delta.setName(table);
    String[] vasField = null;
    if (kiloFields != null)
      vasField = kiloFields.split(",");
    if (vasField != null) {
      List voKiloFields = new ArrayList();
      for (int i = 0; i < vasField.length; i++) {
        if (vasField[i] == null)
          continue;
        if ("".equals(vasField[i]))
          continue;
        voKiloFields.add(vasField[i]);
      }
      delta.setKiloFieldList(voKiloFields);
    }
    StringBuffer s = new StringBuffer();
    s.append("<root>\n");
    s.append(deltaToAxData(delta, axMeta, ""));
    s.append("</root>\n");
    log.debug("\ngetDataBySql():\n" + sql + "\n" + s);
    return s.toString();
  }

  public static String makeAxDataMeta(ResultSet rs) throws SQLException {
    StringBuffer s = new StringBuffer();
    s.append("<METADATA>\n");
    s.append("<FIELDS>\n");
    try {
      ResultSetMetaData metaData = rs.getMetaData();
      for (int i = 1; i <= metaData.getColumnCount(); i++) {
        s.append("<FIELD attrname=\"");
        s.append(metaData.getColumnName(i));
        s.append("\" fieldtype=\"");
        s.append(sqlFieldTypeToAx(metaData.getColumnType(i), metaData.getScale(i)));
        s.append("\" WIDTH=\"");
        if(metaData.getColumnDisplaySize(i) == 0){
          s.append(100);
        }else{
          s.append(metaData.getColumnDisplaySize(i));
        }
        s.append("\" />\n");
      }
    } catch (SQLException e) {
      log.debug("\nAxDataProviderFacade.makeAxDataMeta(): " + e.toString());
      throw e;
    }
    s.append("</FIELDS>\n");
    s.append("</METADATA>\n");
    return s.toString();
  }

  public static String sqlFieldTypeToAx(int sqlType, int scale) {
    String type = "";
    switch (sqlType) {
    case Types.BIGINT:
      type = "i8";
      break;
    case Types.BIT:
      type = "byte";
      break;
    case Types.INTEGER:
      type = "i4";
      break;
    case Types.SMALLINT:
      type = "i2";
      break;
    case Types.TINYINT:
      type = "i4";
      break;
    case Types.DATE:
      type = "date";
      break;
    case Types.TIME:
      type = "time";
      break;
    case Types.DECIMAL:
      if (scale == 0)
        type = "i8";
      else
        type = "r8";
      break;
    case Types.DOUBLE:
      if (scale == 0)
        type = "i8";
      else
        type = "r8";
      break;
    case Types.FLOAT:
      if (scale == 0)
        type = "i8";
      else
        type = "r8";
      break;
    case Types.NUMERIC:
      if (scale == 0)
        type = "i4";
      else
        type = "r8";
      break;
    case Types.REAL:
      if (scale == 0)
        type = "i8";
      else
        type = "r8";
      break;
    default:
      type = "string";
    }
    return type;
  }

  public static String gpFieldTypeToAx(Field field) {
    String type = "";
    if (Field.DATA_TYPE_TEXT.equalsIgnoreCase(field.getType())) {
      type = "string";
    } else if (Field.DATA_TYPE_NUM.equalsIgnoreCase(field.getType())) {
      if (field.getDecLength() <= 0)
        type = "i4";
      else
        type = "r8";
    } else if (Field.DATA_TYPE_DATE.equalsIgnoreCase(field.getType())) {
      type = "date";
    } else if (Field.DATA_TYPE_DATETIME.equalsIgnoreCase(field.getType())) {
      type = "datetime";
    } else if (Field.DATA_TYPE_SEQ.equalsIgnoreCase(field.getType())) {
      type = "i4";
    } else {
      type = "string";
    }
    return type;
  }

  public static String getValueSetXml(String vsId, String cond) {
    List vsList = DataTools.getVS(vsId, cond);
    if (vsList == null)
      return "";
    StringBuffer buf = new StringBuffer();
    buf.append("<root>\n");
    for (Iterator iter = vsList.iterator(); iter.hasNext();) {
      String[] item = (String[]) iter.next();
      buf.append("<row code=\"");
      buf.append(item[0]);
      buf.append("\" name=\"");
      buf.append(item[1]);
      buf.append("\" />\n");
    }
    buf.append("</root>\n");
    log.debug(buf.toString());
    return buf.toString();
  }

}
