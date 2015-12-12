package com.anyi.gp.access;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import com.anyi.gp.bean.ValBean;
import com.anyi.gp.context.ApplusContext;
import com.anyi.gp.core.dao.BaseDao;
import com.anyi.gp.print.util.PrintFileUtil;
import com.anyi.gp.pub.DBHelper;
import com.anyi.gp.pub.LangResource;
import com.anyi.gp.util.StringTools;
import com.anyi.gp.util.XMLTools;

public class FileExportService {

  /**
   * ����Excel�ļ��ľ�̬���������ⲿ����
   * @param tableName �������ļ���
   * @param tableHead ��ͷ����
   * @param tableData ��������
   * @param filePath ����Ϊnull
   * @param ruleID ȡ������ID
   * @param condition ��ѯ����
   */
  public static void createExcel(String tableName, String userNumLimCondition,
    String tableHead, String tableData, String filePath, String ruleID,
    String condition, String valueSet) {
    List tableDataList = new ArrayList();
    FileExportService fileExport = new FileExportService();

    /*****��ȡҳͷ��ʾ��ʽ����**************/
    Map map = fileExport.getTableHeadMap(tableHead);
    tableDataList.add(map);
    /********************��ȡbody������**************************/
    if (tableData != null) {
      /************* ҳ��ѡ�������ݣ���ʱֻ����ѡ�������*********/
      List list = fileExport.getTableBodyList(map, tableData, valueSet);
      tableDataList.addAll(list);
    } else {
      /******ҳ��û��ѡ�����ݣ���ʱ����ruleID��condition��ѯ���ݺ󵼳�*****/
      List dataString = fileExport.getDataListByRuleID(tableName, ruleID, condition,
        userNumLimCondition);
      List list = fileExport.getTableBodyList(map, dataString, valueSet);
      tableDataList.addAll(list);
    }
    fileExport.createExcel(tableName, tableDataList, null, condition);
  }

  /**
   * ����ļ����ͻ��˵ľ�̬���������ⲿ����
   * @param filePath �ļ�·������Ϊ��ʱ��ȡĬ�ϵ��ļ�·��
   * @param fileName �ļ�������������չ��
   * @param response
   */
  public static void exportExcel(String filePath, String fileName,
    HttpServletResponse response) {
    FileExportService fileExport = new FileExportService();
    fileExport.exportExcelFile(filePath, fileName, response);
  }

  private static Map getExcelConditionLabel(String condition) {
    Map excelCondLabelMap = new HashMap();
    Map cond = getPageCondition(condition);
    Iterator dataIter = cond.entrySet().iterator();
    int j = 0;
    while (dataIter.hasNext()) {
      Entry entry = (Entry) dataIter.next();
      String entryKey = (String) entry.getKey();
      String entryValue = (String) entry.getValue();
      String res_na = LangResource.getInstance().getLang(entryKey);
      if (res_na != null) {
        excelCondLabelMap.put(entryKey, res_na + " : " + entryValue);
      }
      j++;
    }
    return excelCondLabelMap;
  }

  /**
   * ����Excel�ļ�����List����д��excel�ļ���
   * @param tableName��Excel�ļ���
   * @param tableDateList�� ���ݰ�
   * @param filePath: �ļ������·��
   */
  public void createExcel(String tableName, List tableDataList, String filePath,
    String condition) {
    if (tableDataList != null && tableDataList.size() > 0) {
      HSSFWorkbook wb = new HSSFWorkbook();//������HSSFWorkbook����
      HSSFSheet sheet = wb.createSheet(tableName);//�����µ�sheet����
      int currentRow = 0;
      /****�����ѯ�������ڣ�������д��EXCEL�ļ���****/
//      if (condition != null) {
//        HSSFRow rowLabel = sheet.createRow(0);
//        rowLabel.setHeight((short) 20);
//        Map labelMap = new HashMap();
//        labelMap = getExcelConditionLabel(condition);
//        Iterator dataIter = labelMap.entrySet().iterator();
//        while (dataIter.hasNext()) {
//          Entry entry = (Entry) dataIter.next();
//          HSSFCell cellLabel = rowLabel.createCell((short) currentRow);
//          cellLabel.setEncoding(HSSFCell.ENCODING_UTF_16);//����cell���������ĸ�λ�ֽڽض�
//          cellLabel.setCellValue((String) entry.getValue());
//          currentRow++;
//        }
//      }
      /**********����ѯ������д��EXCEL�ļ���********************************/
      for (int i = 0; i < tableDataList.size(); i++) {
        HSSFRow row = sheet.createRow(i + currentRow);
        Map map = (Map) tableDataList.get(i);
        Iterator iter = map.entrySet().iterator();
        int j = 0;
        while (iter.hasNext()) {
          Entry entry = (Entry) iter.next();
          HSSFCell cell = row.createCell((short) j);
          cell.setEncoding(HSSFCell.ENCODING_UTF_16);//����cell���������ĸ�λ�ֽڽض�
          Object tmpValue = entry.getValue();
          if(tmpValue == null){
            cell.setCellValue("");
          }else if(tmpValue instanceof BigDecimal){
            cell.setCellValue(StringTools.addZero(tmpValue.toString(), 2)); 
          }else{
            cell.setCellValue(tmpValue.toString());
          }
          j++;
        }
      }
      if (filePath == null || filePath.equals("")) {
        filePath = ApplusContext.getEnvironmentConfig().get("reportspath");
      }
      if (!filePath.endsWith(File.separator)) {
        filePath += File.separator;
      }
      
      String fileName = filePath + tableName + ".xls";
      OutputStream fileOut = null;
      try {
        fileOut = new FileOutputStream(fileName);
        wb.write(fileOut);
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        try {
          fileOut.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  /**
   * ����ļ����ͻ���
   * @param filePath: �ļ�·������Ϊ��ʱ��ȡĬ�ϵ��ļ�·��
   * @param fileName: �ļ�������������չ��������Ϊ��
   * @param response
   */
  public void exportExcelFile(String filePath, String fileName,
    HttpServletResponse response) {
    if (filePath == null || filePath.equals("")) {
      filePath = ApplusContext.getEnvironmentConfig().get("reportspath");
      if (!filePath.endsWith(File.separator)) {
        filePath += File.separator;
      }
    }
    String fileAllName = filePath + fileName + ".xls";
    OutputStream ouputStream = null;
    try {
      response.reset();
      response.setContentType("application/vnd.ms-excel");
      response.setHeader("Content-Disposition", "attachment; filename=" + fileName
        + ".xls");
      byte[] baos = PrintFileUtil.getPrintPdfFile(fileAllName);
      if (baos != null) {
        ouputStream = response.getOutputStream();
        ouputStream.write(baos);
        ouputStream.flush();
      }
      PrintFileUtil.deleteFile(fileAllName);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        ouputStream.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * ��ȡ��ͷ���ݲ�ת����Map,��������EXCEL�ı�����
   * ��ͷ���ݸ�ʽ��
   *    <elements>
   *        <element name='' value='' />
   *        <element name='' value=''>
   *              ......
   *    </elements>
   * @return TreeMap
   * @param String tableHead
   */
  public Map getTableHeadMap(String tableHead) {
    Map map = new LinkedHashMap(16, 0.75F, false);
    if (tableHead != null) {
      Node elements = XMLTools.stringToDocument(tableHead).getDocumentElement();
      if (elements != null) {
        NodeList tableheadList = XMLTools.selectNodeList(elements, "element");
        for (int i = 0; i < tableheadList.getLength(); i++) {
          Node tablehead = tableheadList.item(i);
          String fieldName = XMLTools.getNodeAttr(tablehead, "name");
          String fieldValue = XMLTools.getNodeAttr(tablehead, "value");
          map.put(fieldName, fieldValue);
        }
      }
    }
    return map;
  }

  /**
   * ��delta��ʽ������ת����List�������ձ����е��ֶ�˳������
   * @return List
   * @param Map tableHeadMap: ������ 
   * @param String data : body�����ݣ�delta��ʽ
   * @param String valueSet: ֵ��
   */
  public List getTableBodyList(Map tableHeadMap, String data, String valueSet) {
    List tableBodyList = new ArrayList();
    String vendorParserClass = "org.apache.xerces.parsers.SAXParser";
    try {
      XMLReader reader = XMLReaderFactory.createXMLReader(vendorParserClass);
      reader.setContentHandler(new ParseXML(tableBodyList));
      InputSource inputSource = new InputSource(new ByteArrayInputStream(data.getBytes("UTF-8")));
      reader.parse(inputSource); 
    } catch (SAXException e) {
      e.printStackTrace();
    }catch (IOException e) {
      e.printStackTrace();
    }
    return getTableBodyList(tableHeadMap, tableBodyList, valueSet); 
  }

  /**
   * ��list���ݰ��ձ����е��ֶ�˳������
   * @param tableHeadMap
   * @param data
   * @param valueSet
   * @return
   */
  public List getTableBodyList(Map tableHeadMap, List data, String valueSet) {
    if (tableHeadMap == null || tableHeadMap.isEmpty())
      return data;

    Map mValueSetMap = getPageCondition(valueSet);
    List tableBodyList = new ArrayList();

    if (data != null && tableHeadMap != null) {//����ͷ˳��ת������
      for (int i = 0; i < data.size(); i++) {
        Map map = new LinkedHashMap(16, 0.75F, false);
        Map entity = (Map) data.get(i);

        Iterator iter = tableHeadMap.entrySet().iterator();
        while (iter.hasNext()) {
          Entry entry = (Entry) iter.next();
          String entName = (String) entry.getKey();//��ͷ�ֶ���
          if (mValueSetMap != null && mValueSetMap.get(entName) != null) {
            String fieldVal = getValSet((String) mValueSetMap.get(entName),
               entity.get(entName) == null ? "" : entity.get(entName).toString());
            if (fieldVal != null && !fieldVal.equals("")) {
              map.put(entName, fieldVal);
            } else {
              map.put(entName, entity.get(entName));
            }
          } else {
            map.put(entName, entity.get(entName));
          }
        }

        tableBodyList.add(map);
      }
    }
    return tableBodyList;
  }

  /**
   * ��ȡֵ��
   * @param valSetId
   * @param valId
   * @return
   */
  private String getValSet(String valSetId, String valId) {
    String fieldVal = "";
    Map params = new HashMap();
    params.put("VALSET_ID", valSetId);
    params.put("VAL_ID", valId);
    String ruleID = "gmap-common.getVal";
    try {
      BaseDao dao = (BaseDao) ApplusContext.getBean("baseDao");
      List list = dao.queryForList(ruleID, params);
      if (list != null && list.size() > 0) {
        ValBean val = (ValBean) list.get(0);
        fieldVal = val.getVal();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return fieldVal;
  }

  /**
   * ����ȡ������ID��������ѯ����
   * @param String ruleID, String condition
   * @return String: delta��ʽ����
   */
  public List getDataListByRuleID(String tableName, String ruleID, String condition,
    String userNumLimCondition) {
    List newParams = new ArrayList();
    BaseDao dao = (BaseDao) ApplusContext.getBean("baseDao");
    DBSupport support = (DBSupport) ApplusContext.getBean("dbSupport");
    Map map = getPageCondition(condition);
    String partSql = dao.getSql(ruleID, map, newParams);
    if (userNumLimCondition != null && userNumLimCondition.length() > 0) {
      partSql = support.wrapSqlByCondtion(partSql, userNumLimCondition);
    }
    return dao.queryForListBySql(partSql, newParams.toArray());
//    PageDataProvider dataProvider = (PageDataProvider)ApplusContext.getBean("pageDataProvider");
//    Map params = new HashMap();
//    DBHelper.parseParamsSimpleForSql(condition, params);
//    dataProvider.setUserNumLimCondition(userNumLimCondition);
//    return dataProvider.getPageData(0, 0, 0, tableName, ruleID,params, false).getData();
  }

  /**
   * ��String��ʽ���ݣ�ֵ�ԣ�ת����Map��ʽ����
   * @param condition
   * @return
   */
  private static Map getPageCondition(String condition) {
    return DBHelper.parseParamsSimpleForSql(condition);
  }

  public static HSSFWorkbook createExcelBySqlId(String tableName, String sqlId,
    String condition) throws Exception {
    Map paramsMap = new HashMap();
    DBHelper.parseParamsSimpleForSql(condition, paramsMap);
    BaseDao dao = (BaseDao) ApplusContext.getBean("baseDao");
    List dataList = dao.queryForList(sqlId, paramsMap);
    int rowNum = 0;
    HSSFWorkbook wb = new HSSFWorkbook();
    HSSFSheet sheet = wb.createSheet(tableName);
    HSSFRow row = sheet.createRow(rowNum);
    HSSFCell cell = null;
    // ��ӡ��������
    Iterator iter = paramsMap.entrySet().iterator();
    int column = 2;
    while (iter.hasNext()) {
      Entry entry = (Entry) iter.next();
      cell = row.createCell((short) column);
      cell.setEncoding(HSSFCell.ENCODING_UTF_16);// ����cell���������ĸ�λ�ֽڽض�
      cell.setCellValue(entry.getKey().toString() + "="
        + entry.getValue().toString());
      column += 2;
    }
    rowNum++;
    if (dataList.size() > 0) {
      for (int i = 0; i < dataList.size(); i++) {
        Map data = (Map) dataList.get(i);
        createHSSFRow(sheet, data, rowNum);
        if (rowNum == 1) {
          rowNum += 2;
        } else {
          rowNum++;
        }
      }
    }
    return wb;
  }

  private static void createHSSFRow(HSSFSheet sheet, Map data, int rowNo) {
    HSSFRow head = null;
    HSSFRow row = null;
    if (rowNo == 1) {
      head = sheet.createRow(rowNo);
      row = sheet.createRow(++rowNo);
    } else {
      row = sheet.createRow(rowNo);
    }
    Iterator iter = data.entrySet().iterator();
    int i = 0;
    HSSFCell cell = null;
    while (iter.hasNext()) {
      Entry entry = (Entry) iter.next();
      if (head != null) {
        String fieldName = entry.getKey().toString();
        String transName = LangResource.getInstance().getLang(fieldName);
        if (transName != null && transName.length() > 0)
          fieldName = transName;
        cell = createCell(head, (short) i, fieldName);
      }
      cell = createCell(row, (short) i, entry.getValue() == null ? "" : entry
        .getValue().toString());
      i++;
    }
  }

  private static HSSFCell createCell(HSSFRow row, short column, String value) {
    HSSFCell cell = row.createCell(column);
    cell.setEncoding(HSSFCell.ENCODING_UTF_16);
    cell.setCellValue(value);
    return cell;
  }

  /**
   * 
   * delta���ݽ���
   *
   */
  private class ParseXML extends DefaultHandler {
    
    List tableBodyList = new ArrayList();
    
    Map dataMap = null;
    
    public ParseXML(List tableBodyList){
      this.tableBodyList = tableBodyList;
    }
    
    public void startElement(String namespaceURI, String localName, String qName,
      Attributes attr) throws SAXException {
      if("entity".equalsIgnoreCase(localName)){
        dataMap = new LinkedHashMap(16, 0.75F, false);
      }else if("field".equalsIgnoreCase(localName)){
        dataMap.put(attr.getValue("name"), attr.getValue("value"));
      }
    }

    public void endElement(String namespaceURI, String localName, String qName)
      throws SAXException {
      if("entity".equalsIgnoreCase(localName)){
        tableBodyList.add(dataMap);
      }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
    }
  }

}
