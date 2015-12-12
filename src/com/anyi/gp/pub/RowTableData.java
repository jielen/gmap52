package com.anyi.gp.pub;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.anyi.gp.TableData;

/**
 * @author   leidaohong
 */
public class RowTableData {
  /**
   * Logger for this class
   */
  private static final Logger logger = Logger.getLogger(RowTableData.class);

  RowTableMeta meta = null;

  List records = new ArrayList();

  public RowTableData(Node tableData) {
    this(null, tableData);
  }

  public RowTableData(RowTableMeta meta, Node tableData) {
    this.meta = meta;
    NodeList rows = null;
    try {
      rows = XPathAPI.selectNodeList(tableData, "record");
    } catch (TransformerException e) {
      logger.error(
        "RowTableData(RowTableMeta, Node) - Error ocurred when parse Node", e);
      throw new RuntimeException(e);
    }
    for (int i = 0, j = rows.getLength(); i < j; i++) {
      addRecord(rows.item(i));
    }
  }

  public RowTableData(String action, RowTableMeta meta, List tableDataList){
    this.meta = meta;
    for(int i = 0; i < tableDataList.size(); i++){
      initRecords(action, "", (TableData)tableDataList.get(i));
    }
  }
  
  private void initRecords(String action, String rowId, TableData tableData){
    records.add(new Row(action, rowId, tableData));
  }
  
  private void addRecord(Node node) {
    records.add(new Row(node));
  }

  //chupp; add
  public void delRow(Row row) {
    records.remove(row);
  }

  /**
   * @return   Returns the records.
   * @uml.property   name="records"
   */
  public List getRecords() {
    return records;
  }

  /**
   * @return   Returns the meta.
   * @uml.property   name="meta"
   */
  public RowTableMeta getMeta() {
    return meta;
  }

  public String toString() {
    StringBuffer result = new StringBuffer();
    result.append("<table name=\"");
    result.append(meta.tableName);
    result.append("\">");
    for (Iterator iter = records.iterator(); iter.hasNext();) {
      Row row = (Row) iter.next();
      result.append(row.toString());
    }
    result.append("</table>");
    return result.toString();
  }
}
