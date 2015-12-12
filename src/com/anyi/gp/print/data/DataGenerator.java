package com.anyi.gp.print.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.anyi.gp.Delta;
import com.anyi.gp.TableData;
import com.anyi.gp.access.CommonService;
import com.anyi.gp.access.DBSupport;
import com.anyi.gp.context.ApplusContext;
import com.anyi.gp.core.dao.BaseDao;
import com.anyi.gp.print.bean.PrintConstants;
import com.anyi.gp.print.util.PrintTPLUtil;
import com.anyi.gp.pub.DBHelper;
import com.anyi.gp.pub.DataTools;
import com.anyi.gp.pub.RightUtil;

public class DataGenerator {
	
	/**
	 * 根据页面数据类型生成页面数据
	 * @param printParameter
	 * @return
	 */
	public List generatePageData(String printData){
		List pageDataList = new ArrayList();
		printData = PrintTPLUtil.packSpecial(printData);
		if (printData.startsWith(PrintConstants.DATA_TAG_XMLDATA)) {
			pageDataList = generatePageXMLData(printData);
		}
		if (printData.startsWith(PrintConstants.DATA_TAG_XMLDATAS)) {
			pageDataList = generatePageXMLSData(printData);
		}
		if (printData.startsWith(PrintConstants.DATA_TAG_PAGEDATA)) {
			pageDataList = generateTableData(printData);
		}
		if (printData.startsWith(PrintConstants.DATA_TAG_TABLEDATA)) {
			pageDataList = generateTableData(printData);
		}
		if (printData.startsWith(PrintConstants.DATA_TAG_TEMPLATE)) {
			pageDataList = generateTemplateData(printData);
		}
		if (printData.startsWith(PrintConstants.DATA_TAG_DELTA)) {
			pageDataList = generateDeltaData(printData);
		}
		return pageDataList;
	}
	/**
	 * 
	 * @param printParameter
	 *            HashMap
	 * @return List
	 */
	public List generateEditPageData(Map printParameter) {
		try {
			String printData = (String) printParameter.get(PrintConstants.PRINT_PARAMETER_PRINT_DATA);
			String condtition = (String) printParameter.get(PrintConstants.PRINT_PARAMETER_LISTPAGE_CONDITION);
			String ruleID = (String)printParameter.get(PrintConstants.PRINT_PARAMETER_LISTPAGE_RULEID);
			if (condtition != null && ruleID != null && !ruleID.equals("")) {
				return generateContinuePrintData(printParameter);
			}else{
				return this.generatePageData(printData);
			}
		} catch (Exception e) {
			throw new RuntimeException(
					"Class DataGenerator, Method generateEditPageData(HashMap printParameter) Error : "
							+ e.getMessage() + "\n");
		}
	}

	public List generateListPageData(Map printParameter) {
		List dataList = new ArrayList();
		try {
			Delta data = null;
			String printData = (String) printParameter.get(PrintConstants.PRINT_PARAMETER_PRINT_DATA);
			if(printData == null || printData.equals("")){
//				从页面获取查询条件，该条件是由一些值对组成的字符串，由";"隔开
				String condition = (String) printParameter.get(PrintConstants.PRINT_PARAMETER_LISTPAGE_CONDITION);
				String ruleID = (String) printParameter.get(PrintConstants.PRINT_PARAMETER_LISTPAGE_RULEID);
				String keyCondition = (String) printParameter.get(PrintConstants.PRINT_PARAMETER_LISTPAGE_KEY_CONDITION);
//				Map keys = this.getPageCondition(condition);
//				CommonService service = (CommonService) ApplusContext.getBean("commonService");
//				data = service.getDBDataById(ruleID, keys);
				data = this.getPrintDeltaByKeyCondition(ruleID, condition, keyCondition);
				Map map = new HashMap();
				map.put(PrintConstants.PRINT_PARAMETER_DATA_TYPE, PrintConstants.DATA_TYPE_DELTA);
				map.put(PrintConstants.PRINT_PARAMETER_DATA, data);
				dataList.add(map);
			}else{
				return this.generatePageData(printData);
			}
		} catch (Exception e) {
			throw new RuntimeException("Method generateListPageData(HashMap printParameter) Error : "
							+ e.getMessage() + "\n");
		}
		return dataList;
	}

	/**
	 * 
	 * @param printParameter
	 *            HashMap
	 * @return List
	 */
	public List generateReportPageData(Map printParameter) {
		List data = new ArrayList();
		try {
			Delta delta = null;
			String printData = (String) printParameter.get(PrintConstants.PRINT_PARAMETER_PRINT_DATA);
			if(printData == null || printData.equals("")){
				String condition = (String) printParameter.get(PrintConstants.PRINT_PARAMETER_LISTPAGE_CONDITION);
				String keyCondition = (String) printParameter.get(PrintConstants.PRINT_PARAMETER_LISTPAGE_KEY_CONDITION);
				String ruleID = (String) printParameter.get(PrintConstants.PRINT_PARAMETER_LISTPAGE_RULEID);
//				Map cond = this.getPageCondition(condition);
//				CommonService service = (CommonService) ApplusContext.getBean("commonService");
//				delta = service.getDBDataById(ruleID, cond);
				delta = this.getPrintDeltaByKeyCondition(ruleID, condition, keyCondition);
				if (delta != null) {
					delta.add(generateReportHeadData(printParameter));
					Map map = new HashMap();
					map.put(PrintConstants.PRINT_PARAMETER_DATA_TYPE, PrintConstants.DATA_TYPE_DELTA);
					map.put(PrintConstants.PRINT_PARAMETER_DATA, delta);
					data.add(map);
				}
			}else{
				data = this.generatePageData(printData);
			}
		} catch (Exception e) {
			throw new RuntimeException(
					"Class DataGenerator , Method generateReportPageData(HashMap printParameter) Error : "
							+ e.getMessage() + "\n");
		}
		return data;
	}
	/**
	 * Generate Report Page Head Data, which From js page
	 * 
	 * @param printParameter
	 *            HashMap
	 * @return TableData
	 */
	public TableData generateReportHeadData(Map printParameter) {
		TableData headData = null;
		if (printParameter != null) {
			String printHeadData = (String) printParameter.get(PrintConstants.PRINT_PARAMETER_PRINT_HEADDATA);
			headData = PrintTPLUtil.stringToTableData(printHeadData);
			headData.setName("head");
		}
		return headData;
	}

	/**
	 * 产生Delta数据包
	 * @param String printData
	 * @return List
	 */
	 
	public List generateDeltaData(String printData) {
		List dataList = new ArrayList();
		try {
			List list = PrintTPLUtil.splitString(printData, "<delta>", "</delta>", false);
			Iterator iterator = list.iterator();
			while (iterator.hasNext()) {
			  Map map = new HashMap();
				printData = (String) iterator.next();
				map.put(PrintConstants.PRINT_PARAMETER_DATA_TYPE,PrintConstants.DATA_TYPE_DELTA_STRING);
				map.put(PrintConstants.PRINT_PARAMETER_DATA, printData);
				dataList.add(map);
			}
		} catch (Exception e) {
			throw new RuntimeException(
					"Class DataGenerator, Method  generateDeltaData(String printData) error : "
							+ e.getMessage() + "\n");
		}
		return dataList;
	}

	/**
	 * 
	 * @param printData
	 * @return
	 */
	public List generateTemplateData(String printData) {
		List dataList = new ArrayList();
		try {
			List list = PrintTPLUtil.splitString(printData, "<template>", "</template>", true);
			Iterator iterator = list.iterator();
			while (iterator.hasNext()) {
				Map map = new HashMap();
				printData = (String) iterator.next();
				map.put(PrintConstants.PRINT_PARAMETER_DATA_TYPE, PrintConstants.DATA_TYPE_DELTA_STRING);
				map.put(PrintConstants.PRINT_PARAMETER_DATA, printData);
				dataList.add(map);
			}
		} catch (Exception e) {
			throw new RuntimeException(
					"Class DataGenerator, Method generateTemplateData(String data) error : "
							+ e.getMessage() + "\n");
		}
		return dataList;
	}

	public List generateTableData(String printData) {
		List data = new ArrayList();
		try {
			if (printData.startsWith(PrintConstants.DATA_TAG_PAGEDATA)) {
				printData = printData.substring(PrintConstants.DATA_TAG_PAGEDATA.length());
			}
			if (printData.lastIndexOf(">") != -1) {
				printData = printData.substring(0, printData.lastIndexOf(">") + 1);
			}
			List list = null;
			if (printData.indexOf("</entity>\n</entity>") != -1) {
				list = PrintTPLUtil.splitString(printData, "<entity ", "</entity>\n</entity>", false);
			} else if (printData.indexOf("</entity></entity>") != -1) {
				list = PrintTPLUtil.splitString(printData, "<entity ", "</entity></entity>", false);
			} else {
				list = new ArrayList();
				list.add(printData);
			}
			Iterator iterator = list.iterator();
			while (iterator.hasNext()) {
				Map map = new HashMap();
				printData = (String) iterator.next();
				map.put(PrintConstants.PRINT_PARAMETER_DATA_TYPE, PrintConstants.DATA_TYPE_TABLEDATA_STRING);
				map.put(PrintConstants.PRINT_PARAMETER_DATA, printData);
				data.add(map);
			}
		} catch (Exception e) {
			throw new RuntimeException(
					"Class DataGenerator, generateTableData(String printData) Error : "
							+ e.getMessage() + "\n");
		}
		return data;
	}

	public List generatePageXMLSData(String printData) {
		List dataList = new ArrayList();
		try {
			List list = PrintTPLUtil.splitString(printData, "<XMLDATAS>", "</XMLDATAS>", true);
			Iterator iterator = list.iterator();
			while (iterator.hasNext()) {
				Map map = new HashMap();
				printData = (String) iterator.next();
				map.put(PrintConstants.PRINT_PARAMETER_DATA_TYPE, PrintConstants.DATA_TYPE_PAGE_XML);
				map.put(PrintConstants.PRINT_PARAMETER_DATA, printData);
				dataList.add(map);
			}
		} catch (Exception e) {
			throw new RuntimeException(
					"Class DataGenerator, Method generatePageXMLSData(String printData) error : "
							+ e.getMessage() + "\n");
		}
		return dataList;

	}

	public List generatePageXMLData(String printData) {
		List dataList = new ArrayList();
		try {
			List list = PrintTPLUtil.splitString(printData, "<XMLDATA>", "</XMLDATA>", false);
			Iterator iterator = list.iterator();
			while (iterator.hasNext()) {
			  Map map = new HashMap();
				printData = (String) iterator.next();
				map.put(PrintConstants.PRINT_PARAMETER_DATA_TYPE, PrintConstants.DATA_TYPE_PAGE_XML);
				map.put(PrintConstants.PRINT_PARAMETER_DATA, printData);
				dataList.add(map);
			}
		} catch (Exception e) {
			throw new RuntimeException(
					"Class DataGenerator, Method generatePageXMLData(String printData) error : "
							+ e.getMessage() + "\n");
		}
		return dataList;
	}

	/**
	 * 获取连续打印时的数据，列表页面打印编辑页面数据时用
	 * @param printParameter
	 *            HashMap
	 * @return List
	 * TODO:可能具有严重的内存问题
	 */
	public List generateContinuePrintData(Map printParameter) {
		List dataList = new ArrayList();
		try {
			String sqlid = (String)printParameter.get(PrintConstants.PRINT_PARAMETER_LISTPAGE_RULEID);
			String condition = (String)printParameter.get(PrintConstants.PRINT_PARAMETER_LISTPAGE_CONDITION);
			String keyCondition = (String) printParameter.get(PrintConstants.PRINT_PARAMETER_LISTPAGE_KEY_CONDITION);
			List deltaList = this.getPrintDeltaListByKeyCondition(sqlid, condition, keyCondition);//取列表页面数据
			
			boolean continuePrint = true;
			List conditionLS = null;
			String continueCondition = (String) printParameter.get(PrintConstants.PRINT_PARAMETER_CONTINUE_CONDITION);
      String continueSqlid = (String)printParameter.get(PrintConstants.PRINT_PARAMETER_CONTINUE_RULEID);
      if(continueCondition == null || continueCondition.length() == 0
        || continueSqlid == null || continueSqlid.length() == 0){
        continuePrint = false;
      }else{
        conditionLS = PrintTPLUtil.splitString(continueCondition, ",");
      }
			for(int i = 0; i < deltaList.size(); i++){
				Delta delta = (Delta)deltaList.get(i);
				Iterator iterator = delta.iterator();
				while(iterator.hasNext()){
					TableData tableData = (TableData)iterator.next();
					tableData.setName("head");
				}
				if(continuePrint){
				  String cond = null;
				  if(conditionLS != null){
				    cond += ";" + (String)conditionLS.get(i);
				  }else{
            cond = condition;
          }
	        CommonService service = (CommonService) ApplusContext.getBean("commonService");
	        Delta subDelta = service.getDBDataById(continueSqlid, getPageCondition(cond));
	        iterator = subDelta.iterator();
	        while(iterator.hasNext()){
	          delta.add(iterator.next());
	        }
				}
				Map map = new HashMap();        
	      map.put(PrintConstants.PRINT_PARAMETER_DATA_TYPE, PrintConstants.DATA_TYPE_DELTA);
	      map.put(PrintConstants.PRINT_PARAMETER_DATA, delta);
	      dataList.add(map);
			}
		} catch (Exception e) {
			throw new RuntimeException(
					"Class DataGenerator, Method getContinuePrintData(HashMap printParameter) Error : "
							+ e.getMessage() + "\n");
		}
		return dataList;
	}

	/**
	 * 页面打印的查询条件
	 * @param condition String
	 * @return HashMap
	 */
	private Map getPageCondition(String condition) {
//		HashMap keys = new HashMap();
//		try {
//			if(condition == null || condition.equals(""))keys = null;
//			if(condition.indexOf(";")>0){
//				String[] cond = condition.split(";");
//				for(int i=0;i<cond.length;i++){
//					if(!cond[i].equals("") && cond[i] != null){
//						if(cond[i].indexOf("=")>0){
//							String[] params = cond[i].split("=");
//							if(params.length>1)keys.put(params[0], params[1]);
//						}
//					}
//				}
//			}else{
//				if(condition.indexOf("=")>0){
//					String[] params = condition.split("=");
//					if(params.length>1)keys.put(params[0], params[1]);
//				}
//			}
//		} catch (Exception e) {
//			throw new RuntimeException(
//					"Class DataGenerator, Method getListPageCondition(String componame, HttpServletRequest request) Error : "
//							+ e.getMessage() + "\n");
//		}
//		return keys;
	  return DBHelper.parseParamsSimpleForSql(condition);
	}
	
	public Delta getPrintDeltaByKeyCondition(String sqlid, String condition, String keyCondition){
		Delta delta = new Delta();
		List data = getPrintDataByKeyCondition(sqlid, condition, keyCondition);
		for (int i = 0; i < data.size(); i++) {
			Map map = (Map) data.get(i);
			delta.add(DataTools.Map2TableData(map));
		}
		return delta;
	}
	/**
	 * 根据sqlid，condition和keyCondition获取数据
	 * 
	 * @param sqlid
	 * @param condition
	 * @param keyCondition
	 * @return
	 */
	private List getPrintDataByKeyCondition(String sqlid, String condition, String keyCondition){
	  
	  List data = new ArrayList();
	  List newParams = new ArrayList();
	  Map params = this.getPageCondition(condition);
	  BaseDao dao = (BaseDao)ApplusContext.getBean("baseDao");
	  	  
	  if(keyCondition != null && keyCondition.length() > 0){
	    String sql = dao.getSql(sqlid, new HashMap());
	    DBSupport support = (DBSupport)ApplusContext.getBean("dbSupport");
  	  List keyConditionList = processKeyCondition(keyCondition);
  	  for(int i = 0; i < keyConditionList.size(); i++){
  	    String wrapperKeyCondition = (String)keyConditionList.get(i);
  	    String tmp = support.wrapSqlByCondtion(sql, wrapperKeyCondition, false);
  	    data.addAll(dao.queryForListBySql(tmp, newParams.toArray()));
  	  }
	  }else{
	    String sql = dao.getSql(sqlid, params, newParams);
	    data.addAll(dao.queryForListBySql(sql, newParams.toArray()));
	  }
	  
	  return data;
	}
	
	public List getPrintDeltaListByKeyCondition(String sqlid, String condition, String keyCondition){
		List deltaList = new ArrayList();
		List data = getPrintDataByKeyCondition(sqlid, condition, keyCondition);
		for(int i = 0; i < data.size(); i++){
			Delta delta = new Delta();
			Map map = (Map) data.get(i);
			delta.add(DataTools.Map2TableData(map));
			deltaList.add(delta);
		}
		return deltaList;
	}
	
	public static List processKeyCondition(String keyCondition){
	  return processKeyCondition(keyCondition, RightUtil.MAIN_TABLE_ALIAS);
	}
	
	public static List processKeyCondition(String keyCondition, String masterTable){
	  final int splitSize = 200;
	  List resultList = new ArrayList();
	  try{
	    JSONArray jsonArray = new JSONArray(keyCondition);
	    int arrayLength = jsonArray.length();
	    int size = arrayLength / splitSize + (arrayLength % splitSize > 0 ? 1 : 0);
	    for(int j = 0; j < size; j++){
	      StringBuffer result = new StringBuffer();
	      int i = j * splitSize;
	      int tmpSize = (j + 1) * splitSize;
	      if(tmpSize > arrayLength){
	        tmpSize = arrayLength;
	      }
  	    for(;i < tmpSize; i++){
  	      String tmp = "";
  	      JSONObject json = jsonArray.getJSONObject(i);
  	      Iterator iterator = json.keys();
  	      while(iterator.hasNext()){
  	        String keyName = (String)iterator.next();
  	        tmp += masterTable + "." + keyName + "='" + json.get(keyName) + "' and ";
  	      }
  	      if(tmp.length() > 0){
  	        result.append("(");
  	        result.append(tmp.substring(0, tmp.length() - 5));
  	        result.append(") or ");
  	      }
  	    }
  	    if(result.length() > 0){
  	      result = new StringBuffer("(" + result.substring(0, result.length() - 4) + ")");
  	    }
  	    if(!resultList.contains(result)){
  	      resultList.add(result.toString());
  	    }
	    }
	  }catch(JSONException e){
	    e.printStackTrace();
	  }
	  
	  return resultList;
	}
}
