package com.anyi.gp.core.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import com.anyi.gp.Datum;
import com.anyi.gp.Pub;
import com.anyi.gp.access.DBSupport;
import com.anyi.gp.context.ApplusContext;
import com.anyi.gp.meta.Field;
import com.anyi.gp.meta.Foreign;
import com.anyi.gp.meta.MetaManager;
import com.anyi.gp.meta.TableMeta;
import com.anyi.gp.pub.DBHelper;
import com.anyi.gp.pub.RightUtil;
import com.anyi.gp.pub.SessionUtils;
import com.anyi.gp.util.StringTools;
import com.anyi.gp.util.XMLTools;
import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.webwork.interceptor.ServletRequestAware;

/**
 * 
 * 外部实体选择
 * 
 * @author liuxiaoyong
 * 
 */
public class SelectPageAction extends PageAction implements ServletRequestAware {

  private static final long serialVersionUID = -7435052684647257423L;

  private String masterCompoName;

  private String masterTableName;

  private String masterSelectField;

  private boolean isFromSql;

  private int pagesize;

  private int currentpage;

  private int totalcount;

  private String direction;

  protected HttpServletRequest request;

  public void setServletRequest(HttpServletRequest request) {
    this.request = request;
  }

  public void setIsFromSql(boolean isFromSql) {
    this.isFromSql = isFromSql;
  }

  public void setMasterCompoName(String masterCompoName) {
    this.masterCompoName = masterCompoName;
  }

  public void setMasterSelectField(String masterSelectField) {
    this.masterSelectField = masterSelectField;
  }

  public void setMasterTableName(String masterTableName) {
    this.masterTableName = masterTableName;
  }

  public void setCurrentpage(int currentpage) {
    this.currentpage = currentpage;
  }

  public void setPagesize(int pagesize) {
    this.pagesize = pagesize;
  }

  public void setTotalcount(int totalcount) {
    this.totalcount = totalcount;
  }

  public void setDirection(String direction) {
    this.direction = direction;
  }

  public String doExecute() {
    String cond = condition;
    String searCond = this.searchCond;
    if (!type.equalsIgnoreCase(ADVANCED_SEARCH) && !type.equalsIgnoreCase(ADVANCED_PAGINATION)) {
      if (searCond != null && !searCond.equals("")) {
        if (cond.equals("")) {
          cond = searCond;
        } else {
          cond += ";" + searCond;
        }
      }
    }

    Map paramsMap = new HashMap();
    DBHelper.parseParamsSimpleForSql(cond, paramsMap);

    if (userid == null)
      userid = (String) paramsMap.get("userid");
    
    //System.out.println("######################################################");
    //System.out.println("userId:" + userid);
    String userNumLimCondition = "";

    if(masterTableName != null && masterTableName.length() > 0
      && masterCompoName != null && masterCompoName.length() > 0){
      if(masterTableName.equals(MetaManager.getCompoMeta(masterCompoName).getMasterTable())){
        String realFieldName = request.getParameter("realFieldName");
        List realFieldNameList = java.util.Arrays.asList(realFieldName.split(","));
        
        TableMeta tableMeta = MetaManager.getTableMeta(masterTableName);
        if(masterSelectField != null && masterSelectField.length() > 0){
          String[] sTemp = masterSelectField.split(",");
          for(int i = 0; i < sTemp.length; i++){
            Field field = tableMeta.getField(sTemp[i]);
            if(field == null || !realFieldNameList.contains(field.getRefField())){//主表选择字段对应的外部实体字段与外部实体的实际选择字段比较
              continue;
            }
            if(!field.isSave()){//非保存字段，查询此外部实体对应的其他保存字段的数值权限
              List fields = ((Foreign)tableMeta.getForeign(field.getRefName())).getFields();
              for(int j = 0; j < fields.size(); j++){
                Field tField = (Field)fields.get(i);
                String fieldName = tField.getName();
                if(sTemp[i].equals(fieldName) || !tField.isSave())
                  continue;
                
                String refFieldName = tField.getRefField();
                
                String iCond = getListUserLimCondition(refFieldName, fieldName);
                if(iCond != null && iCond.length() > 0){
                  if(userNumLimCondition.length() > 0){
                    userNumLimCondition += " and " + iCond;
                  }
                  else{
                    userNumLimCondition = iCond;
                  }
                }
              }
            }
            else{//保存字段，添加数值权限
              String iCond = getListUserLimCondition(field.getRefField(), sTemp[i]);
              if(iCond != null && iCond.length() > 0){
                if(userNumLimCondition.length() > 0){
                  userNumLimCondition += " and " + iCond;
                }
                else{
                  userNumLimCondition = iCond;
                }
              }
            }
          }
        }
      }
    }

    String quotUserNumLim = RightUtil.getUserNumLimCondition(
        ServletActionContext.getRequest(), userid, "fquote", componame,
        null, null);
    //System.out.println("svCoCode:" + SessionUtils.getAttribute(request, "svCoCode"));
    //System.out.println("quotUserNumLim:" + quotUserNumLim);
    if (userNumLimCondition != null && userNumLimCondition.length() > 0) {
      if (quotUserNumLim != null && quotUserNumLim.length() > 0) {
        userNumLimCondition += " and ";
        userNumLimCondition += quotUserNumLim;
      }
    } else {
      if (quotUserNumLim != null && quotUserNumLim.length() > 0) {
        userNumLimCondition = quotUserNumLim;
      }
    }
    
    if (type.equalsIgnoreCase(ADVANCED_SEARCH) || type.equalsIgnoreCase(ADVANCED_PAGINATION)) {// 搜索类型为高级搜索时，将搜索条件附加上；
      if (userNumLimCondition != null && userNumLimCondition.length() > 0)
        userNumLimCondition += " and ";
      searCond = searCond.replaceAll(";", " and ");// 将;替换为and
      userNumLimCondition += searCond;
    }
    provider.setUserNumLimCondition(userNumLimCondition);
    
    //System.out.println("userNumLimCondition:" + userNumLimCondition);
    
    int pageIndex = Pub.calcPageIndex(currentpage, direction, pagesize,
        totalcount);
    if (pageIndex <= 0)
      pageIndex = 1;
    int rowmin = (pageIndex - 1) * pagesize + 1;
    int rowmax = pageIndex * pagesize;

    Datum datum = null;
    if (isFromSql && (sqlid == null || sqlid.length() == 0)) {// sqlid为空直接查询表
      DBSupport support = (DBSupport) ApplusContext.getBean("dbSupport");
      List newParams = new ArrayList();

      String matchCond = (String) paramsMap.get("matchCond");// 仅添加搜索框的条件
      paramsMap = new HashMap();

      if (matchCond != null) {
        List saveFieldNames = MetaManager.getTableMeta(tablename).getSaveFieldNames();
        for (int i = 0; i < saveFieldNames.size(); i++)
          paramsMap.put(saveFieldNames.get(i), matchCond);
      }
      
      String sql = support.wrapSqlByTableName(tablename, paramsMap, newParams);
      
      if (matchCond != null) {
        sql = sql.replaceAll("[=]", "like");
        sql = sql.replaceAll("and", "or");
      }

      StringBuffer orderStr = new StringBuffer("");//排序和年度
      TableMeta tableMeta = MetaManager.getTableMeta(tablename);
      List keyFieldNames = tableMeta.getKeyFieldNames();
      if(keyFieldNames != null){
        orderStr.append(" order by ");
        for(int i = 0; i < keyFieldNames.size(); i++){
          orderStr.append(keyFieldNames.get(i) + ",");
          if("ND".equalsIgnoreCase((String)keyFieldNames.get(i))){
            sql = support.wrapSqlByCondtion(sql, "ND=" + SessionUtils.getAttribute(request, "svNd"));
          }
        }
      }
      
      if(orderStr.toString().endsWith(",")){
        sql += orderStr.substring(0, orderStr.length()-1);
      }
      
      if (totalcount < 0)
        totalcount = provider.getTotalCount(support
            .wrapSqlForCount(sql), newParams);
      if (totalcount <= 0) {// 没有数据
        return SUCCESS;
      }
      
      //System.out.println("sql:" + sql);
      paramsMap.put("rownum", rowmax + "");
      paramsMap.put("rn", rowmin + "");
      newParams.add(rowmax + "");
      newParams.add(rowmin + "");
      if (userNumLimCondition != null && userNumLimCondition.length() > 0) {
        sql = support.wrapSqlByCondtion(sql, userNumLimCondition);
      }      
      sql = support.wrapPaginationSql(sql);
      datum = provider.getPaginationData(pageIndex, totalcount, pagesize,
          tablename, sql, newParams, false);
    } else {
      if (totalcount < 0) {// 取总条数
        totalcount = provider.getTotalCount(sqlid, paramsMap);
      }
      if (totalcount <= 0) {// 没有数据
        return SUCCESS;
      }

      paramsMap.put("rownum", rowmax + "");
      paramsMap.put("rn", rowmin + "");

      // 取分页数据
      datum = provider.getPaginationData(pageIndex, totalcount, pagesize,
          tablename, sqlid, paramsMap, false);
    }

    int totalPage = totalcount % pagesize == 0 ? totalcount / pagesize
        : totalcount / pagesize + 1;
    StringBuffer sb = new StringBuffer();
    sb.append("<?xml version=\"1.0\" encoding=\"GBK\"?>\n");
    List data = datum.getData();
    if (data != null) {
      sb.append("<delta totalCount=\"" + totalcount + "\" totalPage=\""
          + totalPage + "\" currentPage=\"" + pageIndex
          + "\" pageSize=\"" + pagesize + "\">\n");
      for (int i = 0; i < data.size(); i++) {
        sb.append("<entity name=\"null\">\n");
        Map map = (Map) data.get(i);
        Set entrySet = map.entrySet();
        Iterator itera = entrySet.iterator();
        while (itera.hasNext()) {
          Entry entry = (Entry) itera.next();
          sb.append("<field name=\"");
          sb.append(entry.getKey());
          sb.append("\" value=\"");
          sb.append(entry.getValue() == null ? "" : XMLTools
              .getValidStringForXML(entry.getValue().toString()));
          sb.append("\"/>\n");
        }
        sb.append("</entity>\n");
      }
      sb.append("</delta>\n");
    }
    
    //System.out.println("######################################################");
    
    resultstring = sb.toString();
    return SUCCESS;
  }

  /**
   * 参数处理
   */
  public void before() {
    if (pagesize <= 0) {
      pagesize = Integer.parseInt(ApplusContext.getEnvironmentConfig().get("pagesize"));
      if (pagesize <= 0) {
        pagesize = 100;
      }
    }
    if ((tablename == null || tablename.length() == 0 || tablename.equals("null")) && componame != null) {
      if (MetaManager.getCompoMeta(componame) != null)
        tablename = MetaManager.getCompoMeta(componame).getMasterTable();
    }
    if ((masterCompoName == null || masterCompoName.length() == 0 || masterCompoName.equals("null"))&& masterCompoName != null) {
      masterCompoName = null;
    }
    if ((masterTableName == null || masterTableName.length() == 0 || masterTableName.equals("null"))&& masterTableName != null) {
      masterTableName = null;
    }
    if ((sqlid == null || sqlid.length() == 0 || sqlid.equals("null")) && sqlid != null) {
      sqlid = null;
    }
    this.condition = StringTools.convertXML(condition);
    this.searchCond = StringTools.convertXML(searchCond);
  }
  
  private String getListUserLimCondition(String refFieldName, String fieldName){
    //String sqlPart = " (MASTER." + refFieldName + " in (select " + fieldName + " from " + masterTableName + " MASTER where ";
    String userNumLimCondition = RightUtil.getUserNumLimCondition(request, 
          userid, "fwatch", masterCompoName, tablename, fieldName);
    //if(userNumLimCondition.length() > 0){
    //  userNumLimCondition = sqlPart + userNumLimCondition + "))";
    //}

    if(refFieldName.equals(fieldName)){
      return userNumLimCondition;
    }
    return userNumLimCondition.replaceAll(fieldName, refFieldName);
  }
}
