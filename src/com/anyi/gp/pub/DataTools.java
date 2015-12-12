package com.anyi.gp.pub;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.anyi.gp.BusinessException;
import com.anyi.gp.Datum;
import com.anyi.gp.Delta;
import com.anyi.gp.TableData;
import com.anyi.gp.meta.CompoMeta;
import com.anyi.gp.meta.Field;
import com.anyi.gp.meta.MetaManager;
import com.anyi.gp.meta.TableMeta;
import com.anyi.gp.taglib.ITag;
import com.anyi.gp.util.StringTools;
import com.anyi.gp.util.XMLTools;
import com.anyi.gp.workflow.util.WFException;

public class DataTools{
	private final static Logger log = Logger.getLogger(DataTools.class);

	public static final String FIELD_LIST_KEY = "u_fields_list";

	private DataTools(){
	}

	/**
	 * 生成 fields map;
	 * @param oFields
	 * @param tIsMakeFieldList
	 * @return
	 */
	public static Map makeFieldMap(Node oFields, boolean tIsMakeFieldList){
		return makeFieldMap(oFields.getChildNodes(), tIsMakeFieldList);
	}

	private static Map makeFieldMap(NodeList fields, boolean tIsMakeFieldList){
		Map voFieldMap = new HashMap();
		List voList = new ArrayList();
		Node voField = null;
		String vsField = "";
		for(int i = 0, len = fields.getLength(); i < len; i++){
			voField = fields.item(i);
			if(voField.getNodeType() != Node.ELEMENT_NODE)
				continue;
			vsField = XMLTools.getNodeAttr(voField, "name");
			if(tIsMakeFieldList)
				voList.add(vsField);
			voFieldMap.put(vsField, voField);
		}
		if(tIsMakeFieldList)
			voFieldMap.put(FIELD_LIST_KEY, voList);
		return voFieldMap;
	}

	public static Map makeFieldMap(Node oFields){
		return makeFieldMap(oFields, false);
	}

	/**
	 * 获取 table meata 的外部接口参数; leidh; 20060719;
	 * ITag.TAG_OUTER_INTERFACE_FIELD_MAP： Map: key=tablename; value= attributes
	 * document of fields of table;
	 * @param tableName
	 * @param request
	 * @return
	 */
	public static Document getOuterTableDoc(String tableName, HttpServletRequest request){
		Map propMap = getOuterTablePool(tableName, request);
		if(propMap == null)
			return null;
		Document tableDoc = (Document) propMap.get(tableName);
		return tableDoc;
	}

	private static Map getOuterTablePool(String tableName, HttpServletRequest request){
		if(request == null)
			return null;
		return (Map) request.getAttribute(ITag.TAG_OUTER_INTERFACE_FIELD_MAP);
	}

	/**
	 * 获取值集 list;
	 * @param sVSCode
	 * @return
   * TODO 从beanManager中获取
	 */
	public static List getVS(String sVSCode, String cond){
		if(sVSCode == null || sVSCode.length() <= 0)
			return null;
		List voVSList = GeneralFunc.getValueSet(sVSCode, false, cond);
		repairVS(voVSList);
		return voVSList;
	}

	/**
	 * 修补 valueset list;
	 * @param oVSList
	 */
	private static void repairVS(List oVSList){
		if(oVSList == null)
			return;
		String[] vasValue = null;
		boolean vtIsRepair = false;
		for(int i = 0, len = oVSList.size(); i < len; i++){
			vasValue = (String[]) oVSList.get(i);
			if(vasValue[1] != null && vasValue[1].trim().length() > 0)
				break;
			if(i >= 1 || len == 1){
				vtIsRepair = true;
				break;
			}
		}
		if(vtIsRepair){
			for(Iterator iter = oVSList.iterator(); iter.hasNext();){
				vasValue = (String[]) iter.next();
				vasValue[1] = vasValue[0];
			}
		}
	}

	public static Node getOuterTableField(String tableName, HttpServletRequest request, String field){
		Map pool = getOuterTableFieldsPool(tableName, request);
		if(pool == null)
			return null;
		return (Node) pool.get(field);
	}

	public static Map getOuterTableFieldsPool(String tableName, HttpServletRequest request){
		Map propMap = getOuterTablePool(tableName, request);
		if(propMap == null)
			return null;
		Map pool = (Map) propMap.get(getOuterTableFieldsKey(tableName));
		if(pool == null){
			synchronized(propMap){
				pool = (Map) propMap.get(getOuterTableFieldsKey(tableName));
				if(pool == null){
					doOuterTableFieldsMap(tableName, request);
				}
			}
		}
		return (Map) propMap.get(getOuterTableFieldsKey(tableName));
	}

	private static void doOuterTableFieldsMap(String tableName, HttpServletRequest request){
		Document doc = getOuterTableDoc(tableName, request);
		if(doc == null)
			return;
		NodeList outerFields = null;
		try{
			outerFields = XPathAPI.selectNodeList(doc.getDocumentElement(), "//field");
		}catch(TransformerException e){
			throw new RuntimeException(e);
		}
		Map outerFieldMap = DataTools.makeFieldMap(outerFields, false);
		Map propMap = getOuterTablePool(tableName, request);
		propMap.put(getOuterTableFieldsKey(tableName), outerFieldMap);
	}

	private static String getOuterTableFieldsKey(String table){
		return table + "_fields_20061220";
	}

	/**
	 * 将 TableData 中的子表内容转换为 Delta;
	 * 
	 * @param tableData
	 * @param childTableName
	 * @return
   * @deprecated tableData和delta数据包不再推荐使用
	 */
	public static Delta getChildTable(TableData tableData, String childTableName){
		if(null == tableData){
			return null;
		}
		Delta result = new Delta(tableData.getChildTables(childTableName));
		result.setName(childTableName);
		if(null != result && 0 != result.size()){
			return result;
		}
		result = new Delta();
		result.setName(childTableName);
		List allNames = tableData.getChildTableNames();
		for(Iterator i = allNames.iterator(); i.hasNext();){
			String directChildName = (String) i.next();
			List directChildList = tableData.getChildTables(directChildName);
			for(Iterator j = directChildList.iterator(); j.hasNext();){
				TableData directChild = (TableData) j.next();
				List subChildList = getChildTable(directChild, childTableName);
				if(null != subChildList){
					result.addAll(subChildList);
				}
			}
		}
		return result;
	}

	public static String getPageDataIdInRequest(String compo, String pageType){
		return "__" + pageType + "_data_" + compo;
	}

  /**
   * 产生默认html span
   * @param oParamFieldMap
   * @return
   */
	public static String makeDefaultValueSpan(Map oParamFieldMap){
		if(oParamFieldMap == null)
			return "";
		Set voFieldSet = oParamFieldMap.keySet();
		if(voFieldSet == null)
			return "";
		StringBuffer voBuf = new StringBuffer();
		voBuf.append("<span id=\"FieldDefaultValueSpan\" style=\"display:none;\">\n");
		for(Iterator iter = voFieldSet.iterator(); iter.hasNext();){
			String vsField = (String) iter.next();
			if(vsField == null || vsField.equals(FIELD_LIST_KEY))
				continue;
			Node voField = (Node) oParamFieldMap.get(vsField);
			String vsDefValue = XMLTools.getNodeAttr(voField, "defvalue", null);
			if(vsDefValue == null)
				continue;
			voBuf.append("<span field=\"");
			voBuf.append(XMLTools.getNodeAttr(voField, "name", ""));
			voBuf.append("\">");
			voBuf.append(vsDefValue);
			voBuf.append("</span>\n");
		}
		voBuf.append("</span>\n");
		return voBuf.toString();
	}

  /**
   * 取值集
   * @param sVSCode
   * @return
   */
	public static List getVS(String sVSCode){
		return getVS(sVSCode, null);
	}

  /**
   * 取摘要数据
   * @param sql
   * @param params
   * @param compoName
   * @param tableName
   * @return
   */
  public static String getDigest(String sql, List params, String compoName, String tableName){
    if(sql == null || sql.length() == 0)
      return "";
    
    String digest = null;
    Connection conn = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    try{
      conn = DAOFactory.getInstance().getConnection();
      pst = conn.prepareStatement(sql);
      for(int i = 0; i < params.size(); i++){
        Object obj = params.get(i);
        if(obj instanceof java.sql.Date){
          pst.setDate(i + 1, (java.sql.Date)obj);
        }
        else if(obj instanceof java.sql.Timestamp){
          pst.setTimestamp(i + 1, (java.sql.Timestamp)obj);
        }
        else{
          pst.setObject(i + 1, obj);
        }
      }
      rs = pst.executeQuery();
      digest = DataTools.getDigest(rs, compoName, tableName);
    }catch(SQLException e){
      String msg = "\nDataTools.getDigest():\n获取digest失败.\n" + e.getMessage();
      log.debug(msg);
      throw new RuntimeException(e);
    }finally{
      DBHelper.closeConnection(conn, pst, rs);
    }
    return digest;
  }
  
  /**
   * 取结果集的摘要信息
   * @param rs
   * @param compoName
   * @param tableName
   * @return
   */
	public static String getDigest(ResultSet rs, String compoName, String tableName){
		Map saveFieldMap = getSaveFieldNamePool(tableName);
		StringBuffer voBuf = new StringBuffer();
		try{
			ResultSetMetaData rsmd = rs.getMetaData();
			while(rs.next()){
				for(int i = 1; i <= rsmd.getColumnCount(); i++){
					String field = rsmd.getColumnName(i);
					if(saveFieldMap != null && saveFieldMap.containsKey(field)){
					  voBuf.append(rs.getString(i));
          }
				}
			}
		}catch(SQLException e){
			throw new RuntimeException(e);
		}
		return StringTools.getDigest(voBuf.toString());
	}

  public static String getDigest(Datum datum, String tableName){
    if(datum == null) return "";
    
    List data = datum.getData();
    if(data == null || data.isEmpty()) return "";
    
    Map saveFieldMap = getSaveFieldNamePool(tableName);
    StringBuffer sb = new StringBuffer();
    
    for(int i = 0; i < data.size(); i++){
      Map dataMap = (Map)data.get(i);
      Set entrySet = dataMap.entrySet();
      Iterator itera = entrySet.iterator();
      while(itera.hasNext()){
        Entry entry = (Entry)itera.next();
        String field = entry.getKey().toString();
        if(saveFieldMap != null && saveFieldMap.containsKey(field)){
          sb.append(entry.getValue());
        }
      }
    }
    return StringTools.getDigest(sb.toString());
  }
  
	private static Map getSaveFieldNamePool(String tableName){
//		if(StringTools.isEmptyString(compoName))
//			return null;
		if(StringTools.isEmptyString(tableName))
			return null;
		TableMeta meta = MetaManager.getTableMeta(tableName);
		TableMeta child = meta.getTableMeta(tableName, true);
		Map saveFieldMap = new HashMap();
		for(Iterator iter = child.getSaveFieldNames().iterator(); iter.hasNext();){
			saveFieldMap.put(iter.next(), null);
		}
		return saveFieldMap;
		//TODO
	}

  /**
   * 判断部件是不是单表部件
   * @param compo
   * @return
   */
	public static boolean isSingleTable(String compo){
		CompoMeta compoMeta = MetaManager.getCompoMeta(compo);
		List compoTables = compoMeta.getTableMeta().getChildTableNames();
		return (compoTables == null || compoTables.isEmpty());
	}

	public static Map getCalls(String sCompo){
		if(sCompo == null || sCompo.equalsIgnoreCase(""))
			return null;
    CompoMeta compoMeta = MetaManager.getCompoMeta(sCompo);
//    List calls = compoMeta.getCalls();
//    
//    if(calls == null)
//      return null;
//    
//    Map callMap = new HashMap();
//    for(int i = 0; i < calls.size(); i++){
//      Call call = (Call) calls.get(i);
//      callMap.put(call.getName(), null);
//    }
    
    return compoMeta.getCalls();
  }

  /**
   * 获取session数据
   * @param componame
   * @param request
   * @return
   */
	public static String getSessionDataXML(String componame, HttpServletRequest request){
		////return sessionMapToXML(GlobalUtil.getAllGlobalVariable(request.getSession(), componame));
    return sessionMapToXML(SessionUtils.getAllSessionVariables(request));
	}

	private static String sessionMapToXML(Map mpSession){
		StringBuffer result = new StringBuffer();
		result.append("<session>\r\n");
		if(null == mpSession){
			return result.append("\r\n</session>").toString();
		}
		Object[] szTmp = mpSession.keySet().toArray();
		String coCode = null, orgCode = null, poCode = null, nd = null;
		for(int i = 0; i < szTmp.length; i++){
			String name = (String) szTmp[i];
			if((name != null) && !name.equalsIgnoreCase("alias")){
				String value = (String) mpSession.get(szTmp[i]);
				if(name.equalsIgnoreCase("svCoCode")){
					coCode = value;
				}else if(name.equalsIgnoreCase("svOrgCode")){
					orgCode = value;
				}else if(name.equalsIgnoreCase("svPoCode")){
					poCode = value;
				}else if(name.equalsIgnoreCase("svNd")){
					nd = value;
				}
				String tagName = StringTools.getValidTagName(name);
				result.append("  <").append(tagName).append(">");
				result.append(value).append("</").append(tagName).append(">\n");
			}
		}
		String orgPoCode = GeneralFunc.getOrgPosiCode(coCode, orgCode, poCode, nd);
		result.append("  <svOrgPoCode>");
		result.append(orgPoCode).append("</svOrgPoCode>\n");
		return result.append("\r\n</session>").toString();
	}

	/**
	 * 返回数据的合计字段的xml
	 * @param totalData
	 * @return
	 */
	public static String getDBTotal(String tableName, String totalFields, Map totalData){
		StringBuffer voBuf = new StringBuffer();
		voBuf.append("<table ");
		voBuf.append("name=\"").append(tableName).append("\" ");
		voBuf.append("totalfields=\"").append(totalFields).append("\" ");
		voBuf.append(">\n");
		Iterator iterator = totalData.entrySet().iterator();
		while(iterator.hasNext()){
			Entry entry = (Entry) iterator.next();
			String fieldName = (String) entry.getKey();
			voBuf.append("<" + fieldName + ">");
			voBuf.append(entry.getValue());
			voBuf.append("</" + fieldName + ">\n");
		}
		voBuf.append("</table>\n");
		return voBuf.toString();
	}
	
  /**
   * @deprecated
   * @param tableData
   * @param entityMeta
   * @param childTables
   * @param tIsTableMeta
   * @param tIsDataXmlHead
   * @return
   */
  public static String toFlatPageDataX(TableData tableData, TableMeta entityMeta,
    List childTables, boolean tIsTableMeta, boolean tIsDataXmlHead) {
    StringBuffer s = new StringBuffer();
    List entityChildTable = childTables;
    if (null != entityMeta) {
      entityChildTable = entityMeta.getAllChildTableNames();
    }
    if (null == tableData) {
      tableData = new TableData();
    }

    String tagName = StringTools.getValidTagName(tableData.getName());
    if (null != entityMeta) {
      tagName = StringTools.getValidTagName(entityMeta.getName());
    }

    int rowcount1 = 0;
    boolean vtIsExistData = (tableData.getFieldNames() != null && tableData
      .getFieldNames().size() > 0);

    TableData voMainTable = tableData;
    if (tIsTableMeta) {
      if (!vtIsExistData) {
        voMainTable = makeDefaultTableData(entityMeta, tagName);
      } else {
        rowcount1 = 1;
      }
    }

    //加入千分位的处理;leidh;20061229;
    if (voMainTable.getKiloFieldList() == null
      || !voMainTable.getKiloFieldList().isEmpty()) {
      voMainTable.setKiloFieldList(entityMeta.getKiloFields());
    }

    if (tIsTableMeta) {
//      s.append("<xml id=\"TableMeta_").append(tagName);
//      s.append("_XML\" asynch=\"false\" encoding=\"GBK\">\n");
//      s.append(DataTools.genTableMeta(tagName, voMainTable.getFieldNames(), request));
//      s.append("\n");
//      s.append("</xml>\n\n");
      s.append(MetaManager.getTableMeta(tagName).toXml());
    }

    if (tIsDataXmlHead) {
      s.append("<xml id=\"TableData_").append(tagName);
      s.append("_XML\" asynch=\"false\" encoding=\"GBK\">");
      s.append("\n");
    }
    s.append("<").append(tagName).append(">");
    s.append("\n");

    if (!vtIsExistData) {
      s
        .append("<meta pageindex=\"1\" fromrow=\"0\" torow=\"0\" rowcountofpage=\"0\" rowcountofdb=\"0\">");
    } else {
      s.append("<meta pageindex=\"1\" fromrow=\"1\" torow=\"").append(rowcount1)
        .append("\" rowcountofpage=\"0\" rowcountofdb=\"").append(rowcount1).append(
          "\">");
    }
    s.append("</meta>\n");

    s.append("<rowset>");
    s.append("\n");
    s.append(voMainTable.toXML());
    s.append("\n");
    s.append("</rowset>");
    s.append("\n");
    s.append("</").append(tagName).append(">");
    s.append("\n");
    if (tIsDataXmlHead) {
      s.append("</xml>");
      s.append("\n");
    }

    // 构造各个子表
    for (Iterator i = entityChildTable.iterator(); i.hasNext();) {
      String childTableName = (String) i.next();
      if (childTableName.equals(tableData.getName())) {
        continue;
      }
      if (!childTables.contains(childTableName)) {
        continue;
      }
      tagName = StringTools.getValidTagName(childTableName);

      List childList = DataTools.getChildTable(tableData, childTableName);
      int rowcount = (null == childList) ? 0 : childList.size();
      if (0 == rowcount && tIsTableMeta) {
        childList.add(makeDefaultTableData(entityMeta, tagName));
      }

      if (tIsTableMeta) {
//        s.append("<xml id=\"TableMeta_").append(tagName);
//        s.append("_XML\" asynch=\"false\" encoding=\"GBK\">\n");
//        s.append(DataTools.genTableMeta(tagName, ((TableData) childList.get(0))
//          .getFieldNames(), request));
//        s.append("\n");
//        s.append("</xml>\n\n");
        s.append(MetaManager.getTableMeta(tagName).toXml());
      }

      if (tIsDataXmlHead) {
        s.append("<xml id=\"TableData_").append(tagName);
        s.append("_XML\" asynch=\"false\" encoding=\"GBK\">");
      }
      s.append("\n");
      s.append("<").append(tagName).append(">");
      s.append("\n");

      if (0 == rowcount) {
        s
          .append("<meta pageindex=\"1\" fromrow=\"0\" torow=\"0\" rowcountofpage=\"0\" rowcountofdb=\"0\">");
      } else {
        s.append("<meta pageindex=\"1\" fromrow=\"1\" torow=\"").append(rowcount)
          .append("\" rowcountofpage=\"0\" rowcountofdb=\"").append(rowcount)
          .append("\">");
      }
      s.append("</meta>\n");

      s.append("<rowset>");
      //加入千分位的处理;leidh;20061229;
      TableMeta childMeta = entityMeta.getTableMeta(childTableName, true);
      if (null != childList) {
        for (Iterator i2 = childList.iterator(); i2.hasNext();) {
          TableData t = (TableData) i2.next();

          //加入千分位的处理;leidh;20061229;
          if (t.getKiloFieldList() == null || !t.getKiloFieldList().isEmpty()) {
            t.setKiloFieldList(childMeta.getKiloFields());
          }

          s.append("\n");
          s.append(t.toXML());
        }
      }
      s.append("\n");
      s.append("</rowset>");
      s.append("\n");
      s.append("</").append(tagName).append(">");
      s.append("\n");
      if (tIsDataXmlHead) {
        s.append("</xml>");
        s.append("\n");
      }
    }
    return s.toString();
  }
  
  /**
   * 生成默认的 tabledata;
   *
   * @param oMeta
   * @param sTableName
   * @return
   */
  public static TableData makeDefaultTableData(TableMeta oMeta, String sTableName) {
    if (oMeta == null) {
      return null;
    }
    TableData voTableData = new TableData();
    TableMeta voTableMeta = oMeta.getTableMeta(sTableName, true);
    if (voTableMeta == null) {
      return null;
    }
    List voFields = voTableMeta.getFieldNames();
    for (Iterator iter = voFields.iterator(); iter.hasNext();) {
      voTableData.setField((String) iter.next(), "");
    }
    return voTableData;
  }
  
  /**
   * TableData转化成Map
   * @param entity
   * @return
   */
  public static Map TableData2Map(TableData entity){
    Map map = new HashMap();
    if (entity != null) {
      List fieldNames = entity.getFieldNames();
      for (Iterator iter = fieldNames.iterator(); iter.hasNext();) {
        String fieldName = iter.next().toString().trim();
        String value = entity.getField(fieldName).toString();
        if(value.indexOf("|") > 0){//处理数组
          String[] multiValue = value.split("\\|");
          List valueList = new ArrayList();
          for(int j = 0; j < multiValue.length; j++){
            if(multiValue[j] != null && multiValue[j].trim().length() > 0)
              valueList.add(multiValue[j].trim());
          }
          map.put(fieldName.trim(), valueList);
        }
        else{
          map.put(fieldName.trim(), value.trim());
        }
      }
    }
    return map;
  }
  
  /**
   * Map转化成TableData
   * @param map
   * @return
   */
  public static TableData Map2TableData(Map map){
    if(map == null)
      return new TableData();
    else
      return new TableData(map);    
  }
  
  /**
   * 解析页面数据
   * 
   * @param xmlData
   * @return
   * @throws WFException
   */
  public static TableData parseData(String xmlData) throws BusinessException{
    TableData data = null;
    if(xmlData == null || xmlData.length() == 0){
      return null;
    }
    Document xmlDoc = XMLTools.stringToDocument(xmlData);
    data = new TableData(xmlDoc.getDocumentElement());
    return data;
  }
  
  public static List getUniqlList(List list) {
  	List resList = new ArrayList(); 
  	for (int i = 0; i < list.size(); i++) {
  		String obj = (String)list.get(i);
  		if (obj != null && obj.length() > 0 && !resList.contains(obj)) {//&& !resList.contains(obj)
  			resList.add(obj);
  		}
  	}
  	return resList;
  }
  
  public static TableData disposeEntity(ResultSet rs, int columnCount,
    ResultSetMetaData rsmd) {
    TableData tableData = new TableData();
    try {
      for (int i = 1; i <= columnCount; i++) { // 处理每一列
        String columnName = rsmd.getColumnName(i);
        String columnType = rsmd.getColumnTypeName(i);
        String columnValue = rs.getString(columnName);
        if (columnType.equalsIgnoreCase("char")
          || columnType.equalsIgnoreCase("varchar")) {
          if (columnValue != null) {
            tableData.setField(columnName, columnValue);
          } else {
            tableData.setField(columnName, "");
          }
        } else if (columnType.equalsIgnoreCase("timestamp")) {
          if (columnValue != null) {
            tableData.setField(columnName, columnValue.substring(0, 10));
          } else {
            tableData.setField(columnName, "");
          }
        } else if (columnType.equalsIgnoreCase("int")) {
          Integer intTmp = null;
          if (null == columnValue) {
            intTmp = Integer.valueOf("0");
          } else {
            intTmp = Integer.valueOf(columnValue);
          }
          tableData.setField(columnName, intTmp);
        } else if (columnType.equalsIgnoreCase("float")) {
          Float floatTmp = null;
          if (null == columnValue) {
            floatTmp = Float.valueOf("0");
          } else {
            floatTmp = Float.valueOf(columnValue);
          }
          tableData.setField(columnName, floatTmp);
        } else {
          if ((columnValue != null) && (columnValue.length() > 0)) {
            tableData.setField(columnName, columnValue);
          } else {
            tableData.setField(columnName, "");
          }
        }
      }
    } catch (SQLException ex) {
      throw new RuntimeException("disposeEntity方法：" + ex.toString());
    }
    return tableData;
  }
  
  public static void printSessionError(HttpServletRequest request,
    HttpServletResponse response) throws IllegalArgumentException {
    response.setContentType(StringTools.CONTENT_TYPE);
    PrintWriter out = null;
    try {
      out = response.getWriter();
    } catch (IOException e) {
      throw new IllegalArgumentException("Dispatcher类返回结果到前端时IO错。");
    }
    out.println("<html>");
    out.println("<SCRIPT language=\"javascript\">");
    out.println("function relogin(){");
    out.println("  alert(\"会话停顿时间超限，当前操作失败。请重新登录！\");");
    out.println("}</SCRIPT>");
    out.println("<body onload=\"relogin();\"><table><tr><td>");
    out.println("会话停顿时间超限，当前操作失败。请重新登录！<br>");
    /*
    out.println("<br>以下输出为开发阶段调试信息<br>");

    out.println("isRequestedSessionIdFromCookie: "
      + request.isRequestedSessionIdFromCookie() + "<br>");
    Cookie[] cks = request.getCookies();
    if (cks != null) {
      out.println("cookie信息:<br>");
      for (int i = 0, j = cks.length; i < j; i++) {
        out.println("name:  " + cks[i].getName() + "<br>");
        out.println("value:  " + cks[i].getValue() + "<br>");
        out.println("path:  " + cks[i].getPath() + "<br>");
        out.println("domain:  " + cks[i].getDomain() + "<br>");
      }
    }
    out.println("isRequestedSessionIdFromURL: "
      + request.isRequestedSessionIdFromURL() + "<br>");
    out.println("url: " + request.getRequestURL().toString() + "<br>");
    out.println("sessionID-----------------------------------"
      + request.getRequestedSessionId() + "<br>");
    out.println("request.getSession(false)-------------------"
      + request.getSession(false) + "<br>");
    out.println("request.isRequestedSessionIdValid()---------"
      + request.isRequestedSessionIdValid() + "<br>");
      */
    out.println("</td></tr></table></body>");
    out.println("</html>");
  }
  
  public static void printProductNotAllowedError(HttpServletRequest request,
    HttpServletResponse response) throws IllegalArgumentException {
    response.setContentType(StringTools.CONTENT_TYPE);
    PrintWriter out = null;
    try {
      out = response.getWriter();
    } catch (IOException e) {
      throw new IllegalArgumentException("DataTools类返回结果到前端时IO错。");
    }
    out.println("<html>");
    out.println("<SCRIPT language=\"javascript\">");
    out.println("function relogin(){");
    out.println("  alert(\"没有购买此产品，此业务功能无法正常使用。请购买！\");");
    out.println("}</SCRIPT>");
    out.println("<body onload=\"relogin();\"><table><tr><td>");
    out.println("没有购买此产品，此业务功能无法正常使用。请购买！<br>");
    out.println("</td></tr></table></body>");
    out.println("</html>");
  }
  
  public static Field generateField(String name, String type, int length, int dec) {
    Field field = new Field();
    field.setName(name);
    field.setType(type);
    field.setMaxLength(length);
    field.setDecLength(dec);
    return field;
  }
  
  /*
   * busiParams参数格式:
   * <row>
   * <CO_CODE></CO_CODE>
   * <ACCOUNT_ID></ACCOUNT_ID>
   * <FISCAL></FISCAL>
   * </row>
   */
  public static Map generateParams(String paramsXml) {
    Document doc = null;
    try {
      doc = XMLTools.stringToDocument(paramsXml);
    } catch (IllegalArgumentException e) {
      String msg = "\nDataTools.generateParams():\n" + e.getMessage()
          + "\n解析参数数据出错，请检查参数数据格式是否正确。\n" + paramsXml;
      log.debug(msg);
      throw new RuntimeException(msg);
    } catch (Exception e) {
      String msg = "\nDataTools.generateParams():\n" + e.getMessage();
      log.debug(msg);
      throw new RuntimeException(msg);
    }
    return DataTools.rowToMap(doc.getDocumentElement());
  }
  
  public static Map rowToMap(Node row) {
    Map voMap = new HashMap();
    try {
      for (int i = 0, len = row.getChildNodes().getLength(); i < len; i++) {
        Node voField = row.getChildNodes().item(i);
        if (voField.getNodeType() != Node.ELEMENT_NODE)
          continue;
        voMap.put(voField.getNodeName(), XMLTools.getNodeText(voField));
      }
    } catch (Exception e) {
      String msg = "\nDataTools.rowToMap():\n" + e.getMessage();
      log.debug(msg);
      throw new RuntimeException(msg);
    }
    return voMap;
  }
}
