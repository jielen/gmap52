package com.anyi.gp.pub;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.anyi.gp.TableData;
import com.anyi.gp.meta.MetaManager;
import com.anyi.gp.meta.TableMeta;
import com.anyi.gp.util.XMLTools;

/**
 * @author   leidaohong
 */
public class RowDelta {
  /**
   * Logger for this class
   */
  private static final Logger logger = Logger.getLogger(RowDelta.class);
  
  private List tableMetas = new ArrayList();
  
  private Map tableDatas = new HashMap();

  public RowDelta(String data) {
    this(XMLTools.stringToDocument(data));
  }

  public RowDelta(Document doc) {
    createRowTableMetas(doc);
    createRowTableDatas(doc);
  }

  /**
   * 从tableData初始化RowDeleta，为ServiceFacade中的save方法用
   * @param action
   * @param compoName
   * @param tableData
   */
  public RowDelta(String action, String compoName, TableData tableData){
    TableMeta tableMeta = MetaManager.getTableMetaByCompoName(compoName);
    RowTableMeta rowTableMeta = new RowTableMeta(compoName, tableMeta);
    tableMetas.add(rowTableMeta);
    
    List tableDataList = new ArrayList();
    tableDataList.add(tableData);
    RowTableData rowTableData = new RowTableData(action, rowTableMeta, tableDataList);
    tableDatas.put(tableMeta.getName(), rowTableData);
    
    initChildRowDelta(action, compoName, tableData);
  }
  
  private void initChildRowDelta(String action, String compoName, TableData tableData){
    List childNames = tableData.getChildTableNames();
    for(int i = 0; i < childNames.size(); i++){
      String childName = childNames.get(i).toString();
      TableMeta tableMeta = MetaManager.getTableMeta(childName);
      RowTableMeta rowTableMeta = new RowTableMeta(compoName, tableMeta);
      tableMetas.add(rowTableMeta);
      
      List childTables = tableData.getChildTables(childName);
      RowTableData rowTableData = new RowTableData(action, rowTableMeta, childTables);
      tableDatas.put(childName, rowTableData);
      
      for(int j = 0; j < childTables.size(); j++){
        TableData iTableData = (TableData)childTables.get(j);
        initChildRowDelta(action, compoName, iTableData);
      }
    }
  }
  
  public RowTableData getRowTableData(String tableName) {
    return (RowTableData) tableDatas.get(tableName);
  }

  private void createRowTableMetas(Document rootElement) {
    NodeList tablesMetaNodes = null;
    try {
      tablesMetaNodes = XPathAPI.selectNodeList(rootElement.getDocumentElement(), "digest//table");
    } catch (TransformerException e) {
      logger
        .error(
          "createRowTableMetas(String, Element) - Error ocurred when resolve RowManager data.",
          e);
      throw new RuntimeException(e);
    }
   
    for (int i = 0, j = tablesMetaNodes.getLength(); i < j; i++) {
      Element item = (Element) tablesMetaNodes.item(i);
      tableMetas.add(new RowTableMeta(item));
    }
    
  }

  private void createRowTableDatas(Document rootElement) {
    try {
      for (int i = 0; i < tableMetas.size(); i++) {
        RowTableMeta rowTableMeta = (RowTableMeta)tableMetas.get(i);
        String tableName = rowTableMeta.tableName;
        Node tableDataNode = XPathAPI.selectSingleNode(rootElement
          .getDocumentElement(), "table[@name='" + tableName + "']");
        if (tableDataNode == null) {
          continue;
        }
        
        RowTableData tableData = new RowTableData(rowTableMeta, tableDataNode);
        tableDatas.put(tableName, tableData);
        
      }
    } catch (TransformerException e) {
      logger.error(
          "createRowTableDatas(String, Element) - Error ocurred when resolve RowManager data.",
          e);
      throw new RuntimeException(e);
    }
  }

  public List getTableMetas() {
    return tableMetas;
  }

  public String toString() {
    StringBuffer result = new StringBuffer("<data>");
    
    result.append("<digest>");
    for (int i = 0; i < tableMetas.size(); i++) {
      result.append(((RowTableMeta)tableMetas.get(i)).toString());
    }
    result.append("</digest>");
    
    for (Iterator iter = tableDatas.values().iterator(); iter.hasNext();) {
      RowTableData data = (RowTableData) iter.next();
      result.append(data.toString());
    }
    
    result.append("</data>");
    
    return result.toString();
  }
}
