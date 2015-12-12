package com.anyi.gp.workflow.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.anyi.gp.access.WorkflowService;
import com.anyi.gp.meta.MetaManager;
import com.anyi.gp.meta.TableMeta;
import com.anyi.gp.pub.DBHelper;
import com.anyi.gp.pub.GeneralFunc;
import com.anyi.gp.pub.SessionUtils;
import com.anyi.gp.workflow.util.WFConst;
import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.xwork.ActionSupport;

/**
 * 
 * 获取待办已办列表
 * TODO:将url的拼接移到service中
 */
public class GetWorklistAction extends ActionSupport {

  private static final long serialVersionUID = 1904498524397412030L;

  private static final String[] v51Products = new String[] { "GL", "CU", "GF", "BG",
    "RP", "PR", "FA", "DB", "PD", "BD", "NT", "BM", "HD", "MOM", "ZC" };

  public static String WORK_TYPE_TODO_LIST = "workTodoList";

  public static String WORK_TYPE_DONE_LIST = "workDoneList";

  public static String WORK_TYPE_DRAFT_LIST = "draftList";

  public static int DEF_MAX_NUM = 100;

  private WorkflowService wfService;
 
  private int start;
  
  private int limit;

  private String workType;

  private String resultType;

  private String token;

  public void setWorkType(String workType) {
    this.workType = workType;
  }

  public void setResultType(String resultType) {
    this.resultType = resultType;
  }

  public void setWfService(WorkflowService wfService) {
    this.wfService = wfService;
  }

  public void setStart(int start) {
    this.start = start;
  }

  public void setLimit(int limit) {
    this.limit = limit;
  }

  public String execute() throws Exception {
    HttpServletRequest request = ServletActionContext.getRequest();
    token = SessionUtils.getToken(ServletActionContext.getRequest());
    String userId = (String) SessionUtils.getAttribute(request, "svUserID");
    List compoList = (List) request.getAttribute("compoArray");
    String searchCondition = (String)request.getAttribute("searchCondition");
    String listType = null;
    
    if (WORK_TYPE_TODO_LIST.equals(workType)) {
      listType = "WF_FILTER_COMPO_TODO";
    } else if (WORK_TYPE_DONE_LIST.equals(workType)) {
      listType = "WF_FILTER_COMPO_DONE";
    } else {
      listType = "WF_COMPO_DRAFT";
    }
    
    int end = start + limit;
    int count = 0;
    Map data = new HashMap();
    for (int i = 0; i < compoList.size(); i++) {
      String compoId = (String) compoList.get(i);
      int countTmp = wfService.getWfdataCountByUserComp(userId, compoId, listType, searchCondition);
      if(countTmp > 0){
        List wfdata = wfService.getWrappedWfdataListByUserComp(userId, compoId, listType, searchCondition, start+1, end);
        wfdata = populateUrl(compoId, wfdata);
        wfdata = addCompoSize(countTmp + "", wfdata);
        data.put(compoId, wfdata);
        count += countTmp;
      }
    }

    request.setAttribute("data", data);
    request.setAttribute("count", count + "");
    request.setAttribute("resultType", resultType);

    return NONE;
  }

  public List populateUrl(String compoId, List datas) {
    String url = getCommonUrl(compoId);
    if (datas != null) {
      for (int i = 0; i < datas.size(); i++) {
        Map data = (Map) datas.get(i);
        String resUrl = getDataURL(url, compoId, data);
        data.put(WFConst.WF_PAGE_URL, resUrl);
      }
    }
    return datas;
  }
  
  public List addCompoSize(String compoSize, List datas) {
  	if (datas != null) {
      for (int i = 0; i < datas.size(); i++) {
        Map data = (Map) datas.get(i);
        data.put("compoSize", compoSize);
      }
    }
    return datas;
  }
  
  private String getCommonUrl(String compoId){
    String sql = "select url from ap_menu_compo where compo_id = ? and url is not null";
    return (String)DBHelper.queryOneValue(sql, new Object[]{compoId});
  }
  
  private String getDataURL(String url, String compoId, Map data) {
    String processInsId = "";
    if(data.get(WFConst.PROCESS_INST_ID_FIELD) != null)
    	processInsId = data.get(WFConst.PROCESS_INST_ID_FIELD).toString();
    String condition = data.get(WFConst.WF_CONDITION) == null ? "" : (String) data.get(WFConst.WF_CONDITION);
    String productCode = getProductCode(compoId);
    String tokens = SessionUtils.getToken(ServletActionContext.getRequest());
    String newParam = "".equals(condition)? "" : parseCondition(condition);
    String result = url;
    if(isV51Product(productCode)){
      String tableName = null;
      TableMeta meta = MetaManager.getTableMetaByCompoName(compoId);
      if(meta != null){
        tableName = meta.getName();
      }
      condition = parseConditionTo51(condition, tableName);
      if(result == null)
        result = "/" + productCode + "/portlet.jsp?fieldvalue=" + compoId + "_E&PROCESS_INST_ID=" + processInsId;
    }else{
      if(result == null)
        result = "/" + productCode + "/getpage_" + compoId + ".action?" + newParam;
    }
    if(result.indexOf("?") > 0){
      result += "&";
    }else{
      result += "?";
    }
    
    String isEdit = GeneralFunc.getOneOption("OPT_AS_IS_EDIT");
    if("N".equals(isEdit)){
    	result += "function=getlistpage";
    }else{
    	result += "function=geteditpage";
    }
    
    result += "&componame=" + compoId;
    result += "&condition=" + condition;
    //result += "&" + newParam;
    result += "&token=" + tokens;
    return result;

  }

  private String parseCondition(String condition) {
    String result = "";
    String[] array = condition.split(";");
    for (int i = 0; i < array.length; i++) {
      String[] temp = array[i].split("=");
      result += temp[0] + "=" + temp[1] + "&";
    }
    if (result.length() > 0) {
      result = result.substring(0, result.length() - 1);
    }
    return result;
  }

  private String getProductCode(String compoId) {
    int dotPos = compoId.indexOf("_");
    String productCode = compoId.substring(0, dotPos);
    return productCode;
  }

  private boolean isV51Product(String productCode) {
    for(int i = 0; i < v51Products.length; i++){
      if(productCode.equals(v51Products[i])){
        return true;
      }
    }
    return false;
  }
  
  private String parseConditionTo51(String condition, String tableName) {
    String newCon = "";
    String[] array = condition.split(";");  
    for (int i = 0; i < array.length; i++) {
      String[] temp = array[i].split("=");
      if(tableName != null){
        newCon += tableName + ".";
      }
      newCon += temp[0] + "=" + "'" + temp[1] + "'" + " and ";
    }
    if (newCon.length() > 0) {
      newCon = newCon.substring(0, newCon.length() - 4);
    }
    return newCon;
  }
}
