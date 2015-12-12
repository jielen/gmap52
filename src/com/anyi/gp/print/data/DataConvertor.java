/* Generated by Together */
/* $Id: DataConvertor.java,v 1.9 2009/07/10 08:36:55 liuxiaoyong Exp $ */
package com.anyi.gp.print.data;

/**
 * <p>
 * Title: DataConvertor是将TableData或Delta数据包转换成JasperReport格式数据
 * </p>
 * <p>
 * Description: 主要对数据进行转换
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company: UFGOV
 * </p>
 * 
 * @author zuodf
 * @version 1.0
 */
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.anyi.gp.Delta;
import com.anyi.gp.TableData;
import com.anyi.gp.bean.ValBean;
import com.anyi.gp.context.ApplusContext;
import com.anyi.gp.core.dao.BaseDao;
import com.anyi.gp.meta.Field;
import com.anyi.gp.meta.MetaManager;
import com.anyi.gp.meta.TableMeta;
import com.anyi.gp.print.bean.PrintConstants;
import com.anyi.gp.pub.DBHelper;
import com.anyi.gp.pub.DataTools;
import com.anyi.gp.util.XMLTools;

public class DataConvertor {

	private static String sgprowcount = "FIXROWCOUNT";

	private static String sRecordid = "Recordid";

	private static String sRecordTotal = "RecordTotal";

	public DataConvertor() {
		try {
			//jbInit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * 
	 * @param source
	 *            String
	 * @param fixRowCount
	 *            int
	 * @param printParameter
	 *            HashMap
	 * @return List
	 */
	public List convertData(String source, int fixRowCount, Map printParameter) {
		try {
			if (source != null) {
				if (source.startsWith(PrintConstants.DATA_TAG_DELTA)) {
					return convertDeltaData(source, fixRowCount, printParameter);
				} else if (source.startsWith(PrintConstants.DATA_TAG_TABLEDATA)) {
					return convertTableData(source, fixRowCount, printParameter);
				} else if (source.startsWith(PrintConstants.DATA_TAG_XMLDATA)) {
					return convertPageXMLData(source, fixRowCount, printParameter);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(
					"Class DataConvertor, Method convertData(String source, int fixRowCount, HashMap printParameter) Error : "
							+ e.getMessage() + "\n");
		}
		return null;
	}

	/**
	 * 
	 * @param source
	 *            String
	 * @param fixRowCount
	 *            int
	 * @param printParameter
	 *            HashMap
	 * @return List
	 */

	public List convertTableData(String source, int fixRowCount, Map printParameter) {
		try {
			Document xmlData = XMLTools.stringToDocument(source);
			TableData tableData = new TableData(xmlData.getDocumentElement());
			return convertTableData(tableData, fixRowCount, printParameter);

		} catch (Exception e) {
			throw new RuntimeException(
					"Class DataConvertor, Method convertTableData(String source, int fixRowCount, HashMap printParameter) Error : "
							+ e.getMessage() + "\n");
		}
	}

	/**
	 * 
	 * @param tableData
	 *            TableData
	 * @param fixRowCount
	 *            int
	 * @param printParameter
	 *            HashMap
	 * @return List
	 */
	public List convertTableData(TableData tableData, int fixRowCount, Map printParameter) {
		try {
			String childTableName = null;
			String valueSet = "";
			if (printParameter != null) {
				childTableName = (String) printParameter.get(PrintConstants.PRINT_PARAMETER_CHILDTABLE_NAME);
				valueSet = (String)printParameter.get(PrintConstants.PRINT_PARAMETER_VALUE_SET);
			}
			if (childTableName == null) {
				return convertTableData(tableData, fixRowCount, "", valueSet);
			} else {
				return convertTableData(tableData, fixRowCount, childTableName, valueSet);
			}
		} catch (Exception e) {
			throw new RuntimeException(
					"Class DataConvertor, Method convertTableData(TableData tableData, int fixRowCount, HashMap printParameter) Error : "
							+ e.getMessage() + "\n");
		}
	}

	/**
	 * 
	 * @param source
	 *            TableData
	 * @param ifixrowcount
	 *            int
	 * @param childTableName
	 *            String
	 * @return List
	 */
	public List convertTableData(TableData source, int ifixrowcount, String childTableName, String valueSet) {
		List dataList = new ArrayList();
		Map valueSetMap = new HashMap();
		DBHelper.parseParamsSimpleForSql(valueSet, valueSetMap);
		try {
			int childTableCount;
			int recordCount;
			String fieldName = "";
			String value = "";
			Map mainData = new HashMap();
			List ChildTableNames = source.getChildTableNames();
			childTableCount = ChildTableNames.size();
			List fieldNames = source.getFieldNames();
			for (int i = 0; i < fieldNames.size(); i++) {
				fieldName = fieldNames.get(i).toString();
				value = Tools_toString(source.getField(fieldName));
				if (value == null) {
					value = "";
				}
				if(valueSetMap != null && valueSetMap.get(fieldName) != null){
					String fieldVal = getValSet((String)valueSetMap.get(fieldName), value);
					if(fieldVal != null && !fieldVal.equals("")){
						mainData.put(fieldName, fieldVal);
					}else{
						mainData.put(fieldName, value);
					}
				}else{
					mainData.put(fieldName, value);
				}
			}
			dataList.add(mainData);
			String ChildTableName = "";
			for (int j = 0; j < childTableCount; j++) {
				ChildTableName = ChildTableNames.get(j).toString();
				if (childTableName != null && !childTableName.equals("")
						&& !childTableName.equals(ChildTableName)) {
					continue;
				}
				recordCount = 0;
				int rows = 0;
				int gps = 0;
				Collection colData = new ArrayList();
				HashSet Fields = new HashSet();
				List ChildTables = (List) source.getField(ChildTableName);
				Iterator iChildTable = ChildTables.iterator();
				while (iChildTable.hasNext()) {
					TableData coCodeEntity = (TableData) iChildTable.next();
					List fieldNameList = coCodeEntity.getFieldNames();
					Map subData = new HashMap();
					for (int i = 0, count = fieldNameList.size(); i < count; i++) {
						fieldName = fieldNameList.get(i).toString();
						value = Tools_toString(coCodeEntity.getField(fieldName));
						if (value == null || value.equals("")) {
							value = "";
						}
						if(valueSetMap != null && valueSetMap.get(fieldName) != null){
							String fieldVal = getValSet((String)valueSetMap.get(fieldName), value);
							if(fieldVal != null && !fieldVal.equals("")){
								subData.put(fieldName, fieldVal);
							}else{
								subData.put(fieldName, value);
							}
						}else{
							subData.put(fieldName, value);
						}
						//subData.put(fieldName, value);
						if (!Fields.contains(fieldName)) {
							Fields.add(fieldName);
						}
					}
					if (ifixrowcount > 0) {
						if (ifixrowcount == rows) {
							gps++;
							subData.put(sgprowcount, String.valueOf(gps));
							rows = 0;
						} else {
							subData.put(sgprowcount, String.valueOf(gps));
						}
					}
					recordCount += 1;
					subData.put(sRecordid, String.valueOf(recordCount));
					colData.add(subData);
					rows++;
				}
				if (ifixrowcount > 0 && rows < ifixrowcount) {
					for (; rows < ifixrowcount; rows++) {
						Map subData = new HashMap();
						subData.put(sgprowcount, String.valueOf(gps));
						if (!Fields.isEmpty()) {
							Iterator iterator = Fields.iterator();
							while (iterator.hasNext()) {
								String getField = (String) iterator.next();
								subData.put(getField, "");
							}
						}
						subData.put(sRecordid, String.valueOf(recordCount));
						colData.add(subData);
					}
				}
				if (colData != null && colData.size() > 0) {
					JRMapCollectionDataSource jrxmlds = new JRMapCollectionDataSource(
							colData);
					dataList.add(jrxmlds);
				} else {
					JREmptyDataSource jrxmlds = new JREmptyDataSource();
					dataList.add(jrxmlds);
				}
				mainData.put(sRecordTotal + j, String.valueOf(recordCount));
			}
			if (dataList.size() == 1) {
				JREmptyDataSource jrxmlds = new JREmptyDataSource();
				dataList.add(jrxmlds);
			}
		} catch (Exception e) {
			throw new RuntimeException(
					"Class DataConvertor, Method convertTableData(TableData source, int ifixrowcount, String childTableName) Error : "
							+ e.getMessage() + "\n");
		}
		return dataList;
	}

	/**
	 * 
	 * @param source
	 *            String
	 * @param fixRowCount
	 *            int
	 * @param printParameter
	 *            HashMap
	 * @return List
	 */
	public List convertDeltaData(String source, int fixRowCount, Map printParameter) {
		Delta printDelta = new Delta(source);
		String valueSet = "";
		if(printParameter != null)valueSet = (String)printParameter.get(PrintConstants.PRINT_PARAMETER_VALUE_SET);
		return convertDeltaData(printDelta, fixRowCount, valueSet);
	}

	/**
	 * 
	 * @param source
	 *            Delta
	 * @param ifixrowcount
	 *            int
	 * @return List
	 */
	public List convertDeltaData(Delta source, int ifixrowcount, String valueSet) {
		List dataList = new ArrayList();
		Map valueSetMap = new HashMap();
		DBHelper.parseParamsSimpleForSql(valueSet, valueSetMap);
		try {
			if(source == null)return dataList;
			TableData tmp = new TableData();
			String fName = null;
			String fVal = null;
			int rows = 0;
			int gps = 0;
			int Recordcout = 0;
			Map tmpresult = new HashMap();
			Collection colData = new ArrayList();
			HashSet Fields = new HashSet();
			for (Iterator iter = source.iterator(); iter.hasNext();) {
				tmp = (TableData) iter.next();
				if (tmp.getName() == null) {
					tmp.setName("");
				}
				if (tmp.getName().equalsIgnoreCase("foot") || tmp.getName().equalsIgnoreCase("head")) {
					for (Iterator iter1 = tmp.getFieldNames().iterator(); iter1.hasNext();) {
						fName = (String) iter1.next();
						fVal = Tools_toString(tmp.getField(fName));
						if (fVal == null || fVal.equals("")) {
							fVal = "　";
						}
						if (!tmpresult.containsKey(fName)) { // 加入字段值
							if(valueSetMap != null && valueSetMap.get(fName) != null){
								String fieldVal = getValSet((String)valueSetMap.get(fName), fVal);
								if(fieldVal != null && !fieldVal.equals("")){
									tmpresult.put(fName, fieldVal);
								}else{
									tmpresult.put(fName, fVal);
								}
							}else{
								tmpresult.put(fName, fVal);
							}
						}
					}
					continue;
				}
				/**
				 * 下面处理的数据来源:
				 * PrintX.js中调用方法PrintX_getListSelectPrintDataOneDelta产生的数据
				 * 此时：entity name = "body"
				 */
				Map subresult = new HashMap();
				for (Iterator iter1 = tmp.getFieldNames().iterator(); iter1.hasNext();) {
					fName = (String) iter1.next();
					fVal = Tools_toString(tmp.getField(fName));
					if (fVal == null || fVal.equals("")) {
						fVal = "";
					}
					if (!subresult.containsKey(fName)) { // 加入字段值
						if(valueSetMap != null && valueSetMap.get(fName) != null){
							String fieldVal = getValSet((String)valueSetMap.get(fName), fVal);
							if(fieldVal != null && !fieldVal.equals("")){
								subresult.put(fName, fieldVal);
							}else{
								subresult.put(fName, fVal);
							}
						}else{
							subresult.put(fName, fVal);
						}
					}
					if (!Fields.contains(fName)) {
						Fields.add(fName);
					}
				}
				if (ifixrowcount > 0) {
					if (ifixrowcount == rows) {
						gps++;
						subresult.put(sgprowcount, String.valueOf(gps));
						rows = 0;
					} else {
						subresult.put(sgprowcount, String.valueOf(gps));
					}
				}
				Recordcout += 1;
				subresult.put(sRecordid, String.valueOf(Recordcout));
				colData.add(subresult);
				rows++;
				continue;
			}
			if (ifixrowcount > 0 && rows < ifixrowcount) {
				for (; rows < ifixrowcount; rows++) {
					Map subresulta = new HashMap();
					subresulta.put(sgprowcount, String.valueOf(gps));
					if (!Fields.isEmpty()) {
						Iterator iterator = Fields.iterator();
						while (iterator.hasNext()) {
							String getField = (String) iterator.next();
							subresulta.put(getField, "");
						}
					}
					subresulta.put(sRecordid, String.valueOf(Recordcout));
					colData.add(subresulta);
				}
			}
			tmpresult.put(sRecordTotal + "0", String.valueOf(Recordcout));
			dataList.add(tmpresult);
			if (colData != null && colData.size() > 0) {
				JRMapCollectionDataSource jrxmlds = new JRMapCollectionDataSource(
						colData);
				dataList.add(jrxmlds);
			} else {
				JREmptyDataSource jrxmlds = new JREmptyDataSource();
				dataList.add(jrxmlds);
			}
		} catch (Exception e) {
			throw new RuntimeException(
					"Class DataConvertor, Method convertDeltaData(Delta source, int ifixrowcount) Error : "
							+ e.getMessage() + "\n");
		}
		return dataList;
	}

	/**
	 * 转换页面传递数据为数据源，返回Object数组第一个元素是参数，其他元素为子表数据源，用于新页面打印。 
	 * 传递的数据格式： <XMLDATA>
	 * <compometa>……</ compometa>//确定主、子表关系
	 * <session>……</session> //session数据
	 * <pagedata>……</pagedata> //页面数据，包括主表和子表 
	 * </XMLDATA>
	 * 
	 * @param source
	 *            String
	 * @param fixRowCount
	 *            int
	 * @param printParameter
	 *            HashMap
	 * @return List
	 */
	public List convertPageXMLData(String source, int fixRowCount, Map printParameter) {
		String childTableName = null;
		if (printParameter != null) {
			childTableName = (String) printParameter.get(PrintConstants.PRINT_PARAMETER_CHILDTABLE_NAME);
		}
		if (childTableName == null) {
			return convertPageXMLData(source, fixRowCount, "");
		} else {
			return convertPageXMLData(source, fixRowCount, childTableName);
		}

	}

	/**
	 * 
	 * @param source
	 *            String
	 * @param fixRowCount
	 *            int
	 * @param childTableName
	 *            String
	 * @return List
	 */
	public List convertPageXMLData(String source, int fixRowCount, String childTableName) {
		List dataList = new ArrayList();
		try {
			Map parameter = new HashMap();
			Collection colData = null;
			Document xmlData = XMLTools.stringToDocument(source);
			Element element = xmlData.getDocumentElement();
			NodeList compoMetaNodes = element.getElementsByTagName("compometa");
			String compoName = this.getCompoName(compoMetaNodes.item(0));
			NodeList pageDataNodes = element.getElementsByTagName("pagedata");
			/**
			 * 获取session节点数据并保持到List
			 */
			NodeList sessionDataNodes = element.getElementsByTagName("session");
			parameter = getSessionDataMap(sessionDataNodes);
			dataList.add(parameter);
			
			/**
			 * 处理pagedata节点的数据
			 */
			if (pageDataNodes != null && pageDataNodes.getLength() != 0) {
				Element pageData = (Element) pageDataNodes.item(0);
				NodeList datas = pageData.getElementsByTagName("data");
				if (datas != null && datas.getLength() != 0) {
					Map tableType = new HashMap();
					tableType = getTableType(compoMetaNodes);
					int subTableCount = getSubTableCount(tableType);
					if (subTableCount == 0) {
						subTableCount = 1;
					}
					int totalRecords = 0;
					int subTableIndex = 1;
					String tableName = "";
					Element tableDatas = (Element) datas.item(0);
					Set tableSets = tableType.keySet();
					Iterator iterator = tableSets.iterator();
					while (iterator.hasNext()) {
						tableName = (String) iterator.next();
						NodeList tableData = tableDatas.getElementsByTagName(tableName);
						if (tableData != null && tableData.getLength() > 0) {
							if (tableType.get(tableName).equals("subTable")) { // 子表形成数据源
								if (childTableName != null && !childTableName.equals("") && !childTableName.equals(tableName)) {
									continue;
								}
								colData = new ArrayList();
								if (subTableIndex == 1) {
									colData = getSubTableDataList(compoName, tableData, fixRowCount);
									Element data = (Element) tableData.item(0); // 计算第一个子表的记录数
									Element rowsetData = (Element) data.getElementsByTagName("rowset").item(0);
									NodeList rowDatas = rowsetData.getElementsByTagName("row");
									totalRecords = rowDatas.getLength();
								} else {
									colData = getSubTableDataList(compoName, tableData, 0);
								}
								if (colData != null && colData.size() > 0) {
									JRMapCollectionDataSource jrxmlds = new JRMapCollectionDataSource(colData);
									dataList.add(jrxmlds);
								} else {
									JREmptyDataSource jrxmlds = new JREmptyDataSource();
									dataList.add(jrxmlds);
								}
							} else if (tableType.get(tableName).equals("mainTable")) { // 主表形成参数
								Map mainTableDataMap = null;
								mainTableDataMap = getMainTableDataMap(compoName, tableData);
								if (mainTableDataMap != null && !mainTableDataMap.isEmpty()) {
									parameter.putAll(mainTableDataMap);
								}
								if(tableType.size() == 1){//仅有主表，将主表信息添加进去
								  //System.out.println("while mainTable tableName : " + tableName);
								  colData = getSubTableDataList(compoName, tableData, fixRowCount);
                  Element data = (Element) tableData.item(0); // 计算第一个子表的记录数
                  Element rowsetData = (Element) data.getElementsByTagName("rowset").item(0);
                  NodeList rowDatas = rowsetData.getElementsByTagName("row");
                  totalRecords = rowDatas.getLength();
                  JRMapCollectionDataSource jrxmlds = new JRMapCollectionDataSource(colData);
                  dataList.add(jrxmlds);
								}
							}
							parameter.put(sRecordTotal + "0", String.valueOf(totalRecords));
						}
					}
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(
					"Class DataConvertor, Method convertPageXMLData(String source, int fixRowCount) Error : "
							+ e.getMessage() + "\n");
		}
		if (dataList.size() == 1) {
			dataList.add(new JREmptyDataSource());
		}
		return dataList;
	}

	/**
	 * 
	 * @param colData
	 *            Collection
	 */
	public void outcolData(Collection colData) {
		HashSet[] FieldName;
		FieldName = new HashSet[1];
		Iterator iterbody = colData.iterator();
		Map mapfieldname = (Map) iterbody.next();
		Set setfieldname = mapfieldname.entrySet();
		Iterator iterfieldname = setfieldname.iterator();
		while (iterfieldname.hasNext()) {
			Map.Entry mapen = (Map.Entry) iterfieldname.next();
			if (!FieldName[0].contains(mapen.getKey())) {
				FieldName[0].add(mapen.getKey());
			}
			// System.out.println("fieldname=" + mapen.getKey() + ";fieldvalue="
			// + mapen.getValue());
		}
	}

	/**
	 * 根据传来的compoMeta获取部件名
	 * 
	 * @param compoMetaNode
	 * @return
	 */
	protected String getCompoName(Node compoMetaNode) {
		String compoName = "";
		Element element = (Element) compoMetaNode;
		compoName = element.getAttribute("name");
		return compoName;
	}

	/**
	 * 取得页面中传递的表的类型，主表还是子表,用于新页面打印
	 * 
	 * @param compoMetaNodes
	 *            NodeList
	 * @return HashMap
	 */
	public Map getTableType(NodeList compoMetaNodes) {
		Map tableType = null;
		if (compoMetaNodes != null && compoMetaNodes.getLength() != 0) {
			Element compoMeta = (Element) compoMetaNodes.item(0);
			NodeList tableNodes = compoMeta.getElementsByTagName("tables");
			if (tableNodes != null && tableNodes.getLength() != 0) {
				tableType = new HashMap();
				Element table = (Element) tableNodes.item(0);
				NodeList tables = table.getElementsByTagName("table");
				for (int i = 0; i < tables.getLength(); i++) {
					Element subTable = (Element) tables.item(i);
					Element parentNode = (Element) subTable.getParentNode();
					String tagName = parentNode.getTagName();
					if (tagName != null && tagName.equals("tables")) {
						tableType.put(subTable.getAttribute("name"), "mainTable");
					} else {
						tableType.put(subTable.getAttribute("name"), "subTable");
					}
				}
			}
		}
		return tableType;
	}

	/**
	 * 取得页面传递数据中的子表个数，用于新页面打印
	 * 
	 * @param tableType
	 *            HashMap
	 * @return int
	 */
	protected int getSubTableCount(Map tableType) {
		int subTableCount = 0;
		if (tableType != null && tableType.size() != 0) {
			Collection values = new ArrayList();
			values = tableType.values();
			if (values != null && values.size() > 0) {
				String value = "";
				Iterator iterator = values.iterator();
				while (iterator.hasNext()) {
					value = (String) iterator.next();
					if (value.equals("subTable")) {
						subTableCount++;
					}
				}
			}
		}
		return subTableCount;
	}

	/**
	 * 取得页面传递的session数据Map，用于新页面打印。
	 * 
	 * @param sessionDataNodes
	 *            NodeList
	 * @return HashMap
	 */
	public Map getSessionDataMap(NodeList sessionDataNodes) {
		Map sessionDataMap = null;
		if (sessionDataNodes != null && sessionDataNodes.getLength() != 0) {
			Element sessionData = (Element) sessionDataNodes.item(0);
			sessionDataMap = getRowDataMap(sessionData);
		}
		return sessionDataMap;
	}

	/**
	 * 取得主表参数，用于新页面打印。
	 * 
	 * @param tableData
	 *            NodeList
	 * @return HashMap
	 */
	public Map getMainTableDataMap(String compoName, NodeList tableData) {
		Map mainTableDataMap = null;
		if (tableData != null) {
			Element data = (Element) tableData.item(0);
			this.convertValueSet(compoName, data, true);
			Element rowsetData = (Element) data.getElementsByTagName("rowset")
					.item(0);
			Element rowData = (Element) rowsetData.getElementsByTagName("row")
					.item(0);
			mainTableDataMap = getRowDataMap(rowData);
		}
		return mainTableDataMap;
	}

	/**
	 * 取得子表数据源，用于新页面打印。
	 * 
	 * @param tableData
	 *            NodeList
	 * @param fixRowCount
	 *            int
	 * @return Collection
	 */
	public Collection getSubTableDataList(String compoName, NodeList tableData, int fixRowCount) {
		Collection subTableDataList = null;
		if (tableData != null) {
			subTableDataList = new ArrayList();
			Map rowDataMap = null;
			int seq = 0;
			Element data = (Element) tableData.item(0);
			this.convertValueSet(compoName, data, false);
			Element rowsetData = (Element) data.getElementsByTagName("rowset").item(0);
			NodeList rowDatas = rowsetData.getElementsByTagName("row");
			int i = 0;
			for (; i < rowDatas.getLength(); i++) {
				Element rowData = (Element) rowDatas.item(i);
				rowDataMap = new HashMap();
				rowDataMap = getRowDataMap(rowData);
				rowDataMap.put(sRecordid, String.valueOf(i + 1)); // 为每条记录增加一个记录编号，从1开始
				if (fixRowCount > 0) { // 分组的情况
					rowDataMap.put(sgprowcount, String.valueOf(seq));
					if ((i + 1) % fixRowCount == 0) {
						seq++;
					}
				}
				subTableDataList.add(rowDataMap);
			}
			// 填充分组时的空记录
			if (fixRowCount > 0 && i % fixRowCount != 0) {
				int j = i % fixRowCount;
				for (; j < fixRowCount; j++) {
					rowDataMap = new HashMap();
					rowDataMap.put(sRecordid, String.valueOf(++i));
					rowDataMap.put(sgprowcount, String.valueOf(seq));
					subTableDataList.add(rowDataMap);
				}
			}
		}
		return subTableDataList;
	}

	/**
	 * 取得行数据的HashMap，用于新页面打印。
	 * 
	 * @param rowData
	 *            Element
	 * @return HashMap
	 */
	public Map getRowDataMap(Element rowData) {
		Map rowDataMap = null;
		if (rowData != null) {
			rowDataMap = new HashMap();
			NodeList nodeDatas = rowData.getChildNodes();
			String name = "";
			String value = "";
			for (int i = 0; i < nodeDatas.getLength(); i++) {
				Node node = nodeDatas.item(i);
				name = node.getNodeName();
				if (node.getFirstChild() == null) {
					value = "";
				} else {
					value = node.getFirstChild().getNodeValue();
				}
				if (value == null || value.equals("null")
						|| value.equals("undefined")) {
					value = "";
				}
				rowDataMap.put(name, value);
			}
		}
		return rowDataMap;
	}

	/**
	 * 对于新页面传来的数据，包含的是值集代码，转换为值集名称
	 * 
	 * @param compoName
	 * @param source
	 * @return
	 */
	public void convertValueSet(String componame, Element source,
			boolean isMainTable) {
		try {
			String tableName = source.getTagName();
			Map tableVsFields = this.getTableVsFields(componame, tableName,
					isMainTable);
			this.convertValueSet(tableVsFields, source);
		} catch (Exception e) {

		}
	}

	/**
	 * 对于新页面传来的数据，包含的是值集代码，转换为值集名称
	 * 
	 * @param fieldNameMap
	 *            HashMap
	 * @param source
	 *            Element
	 */
	public void convertValueSet(Map fieldNameMap, Element source) {
		try {
			NodeList rowsetDatas = source.getElementsByTagName("rowset");
			if (rowsetDatas != null && fieldNameMap != null
					&& !fieldNameMap.isEmpty()) {
				for (int i = 0; i < rowsetDatas.getLength(); i++) {
					String nodeName = "";
					String nodeValue = "";
					String valueSetName = "";
					Map hasValueSet = null;
					Element rowsetData = (Element) rowsetDatas.item(i);
					NodeList rowDatas = rowsetData.getElementsByTagName("row");
					if (rowDatas != null) {
						for (int j = 0; j < rowDatas.getLength(); j++) {
							Element rowData = (Element) rowDatas.item(j);
							if (rowData != null) {
								NodeList childNodes = rowData.getChildNodes();
								for (int k = 0; k < childNodes.getLength(); k++) {
									Node data = childNodes.item(k);
									nodeName = data.getNodeName();
									if (data.getFirstChild() != null) {
										nodeValue = data.getFirstChild()
												.getNodeValue();
										if (nodeValue != null
												&& !nodeValue.equals("")) {
											hasValueSet = (Map) fieldNameMap
													.get(nodeName);
											if (hasValueSet != null
													&& !hasValueSet.isEmpty()) {
												valueSetName = (String) hasValueSet
														.get(nodeValue);
												if (valueSetName != null
														&& !nodeValue
																.equals(valueSetName)) {
													data
															.getFirstChild()
															.setNodeValue(
																	valueSetName);
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {

		}
	}

	/**
	 * 转换值集代码
	 * 
	 * @param componame
	 *            String
	 * @param data
	 *            TableData
	 */
	public void convertValueSet(String componame, TableData data) {
		try {
			// maintable
			Map tableVsFields = this.getTableVsFields(componame, data
					.getName(), true);
			this.convertValueSet(tableVsFields, data);
			// childtable
			String childTableName = "";
			List dataSet = null;
			TableData row = null;
			List childTablesName = data.getChildTableNames();
			Iterator iterator = childTablesName.iterator();
			while (iterator.hasNext()) {
				childTableName = (String) iterator.next();
				tableVsFields = this.getTableVsFields(componame,
						childTableName, false);
				dataSet = data.getChildTables(childTableName);
				if (dataSet != null && !dataSet.isEmpty()) {
					for (Iterator iter = dataSet.iterator(); iter.hasNext();) {
						row = (TableData) iter.next();
						if (row != null) {
							this.convertValueSet(tableVsFields, row);
						}
					}
				}
			}
		} catch (Exception e) {

		}
	}

	/**
	 * 转换值集代码为值集名称
	 * 
	 * @param fieldNameMap
	 *            HashMap
	 * @param data
	 *            TableData
	 */

	public void convertValueSet(Map fieldNameMap, TableData data) {
		try {
			String name = "";
			String value = "";
			String valueSetName = "";
			Map hasValueSet = null;
			List tfieldNames = null;
			Iterator fiterator = null;
			if (fieldNameMap != null && !fieldNameMap.isEmpty() && data != null) {
				tfieldNames = data.getFieldNames();
				fiterator = tfieldNames.iterator();
				while (fiterator.hasNext()) {
					name = (String) fiterator.next();
					if (name != null && fieldNameMap.containsKey(name)) {
						value = (String) data.getField(name);
						if (value != null) {
							hasValueSet = (Map) fieldNameMap.get(name);
							if (hasValueSet != null && !hasValueSet.isEmpty()) {
								valueSetName = (String) hasValueSet.get(value);
								if (valueSetName != null
										&& !value.equals(valueSetName)) {
									data.setField(name, valueSetName);
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {

		}
	}

	/**
	 * 取表中值集字段
	 * 
	 * @param compoName
	 *            String
	 * @param tableName
	 *            String
	 * @param isMainTable
	 *            boolean
	 * @return HashMap
	 */

	public Map getTableVsFields(String compoName, String tableName,
			boolean isMainTable) {
		Map fieldNameMap = new HashMap();
		try {
			TableMeta tableMeta = MetaManager
					.getTableMetaByCompoName(compoName);
			if (!isMainTable) {
				tableMeta = tableMeta.getTableMeta(tableName, true);
			}
			List fieldNames = tableMeta.getFieldNames();
			if (fieldNames != null && fieldNames.size() > 0) {
				Iterator iterator = fieldNames.iterator();
				String fieldName = "";
				String valueSetCode;
				while (iterator.hasNext()) {
					fieldName = (String) iterator.next();
					valueSetCode = "";
					Field field = tableMeta.getField(fieldName);
					if (field != null) {
						valueSetCode = field.getVscode();
					}
					if (valueSetCode != null && !valueSetCode.equals("")) {
						Map vsmap = new HashMap();
						List valueSetList = DataTools.getVS(valueSetCode);
						if (valueSetList != null && valueSetList.size() > 0) {
							Iterator vsiterator = valueSetList.iterator();
							while (vsiterator.hasNext()) {
								String[] valueItem = (String[]) vsiterator
										.next();
								if (valueItem != null && valueItem.length > 0) {
									vsmap.put(valueItem[0], valueItem[1]);
								}
							}
						}
						if (vsmap != null && !vsmap.isEmpty()) {
							fieldNameMap.put(fieldName, vsmap);
						}
					}
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("EditPrint 的 getTableVsFields 取表值集字段出错："
					+ e.toString());
		}
		return fieldNameMap;
	}

	/**
	 * 对象转换为字符串
	 * 
	 * @param o
	 * @return
	 */
	public static String Tools_toString(Object o) {
		if (null == o) {
			return "";
		}
		if (o instanceof Double) {
			return Double_to6DecString(((Number) o).doubleValue());
		}
		return o.toString();
	}

	private static final java.text.DecimalFormat fmt = new java.text.DecimalFormat(
			"0.000000");

	// 小数点后6个0
	/** 浮点数转换成字符串，小数点后先保留6位，在截去多余的0，至少保留2位小数 */
	public static String Double_to6DecString(double d) {
		String s = fmt.format(d);
		int n = s.lastIndexOf(".");
		if (-1 == n) {
			return s;
		}
		int i;
		for (i = s.length() - 1; i > n + 2; i--) {
			if ('0' != s.charAt(i)) {
				break;
			}
		}
		return s.substring(0, i + 1);
	}

	/**
	 * 获取值集
	 * @param valSetId
	 * @param valId
	 * @return
	 */
	private String getValSet(String valSetId, String valId){
		String fieldVal = "";
		Map params = new HashMap();
		params.put("VALSET_ID", valSetId);
		params.put("VAL_ID", valId);
		String ruleID = "gmap-common.getVal";
		try{
			BaseDao dao = (BaseDao) ApplusContext.getBean("baseDao");
			List list = dao.queryForList(ruleID, params);
			if(list != null && list.size()>0){
				ValBean val = (ValBean)list.get(0);
				fieldVal = val.getVal();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return fieldVal;
	}
}
