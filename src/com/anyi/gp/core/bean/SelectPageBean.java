package com.anyi.gp.core.bean;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.anyi.gp.Datum;
import com.anyi.gp.Pub;
import com.anyi.gp.access.DBSupport;
import com.anyi.gp.access.PageDataProvider;
import com.anyi.gp.context.ApplusContext;
import com.anyi.gp.meta.CompoMeta;
import com.anyi.gp.meta.Field;
import com.anyi.gp.meta.Foreign;
import com.anyi.gp.meta.MetaManager;
import com.anyi.gp.meta.TableMeta;
import com.anyi.gp.pub.DBHelper;
import com.anyi.gp.pub.GeneralFunc;
import com.anyi.gp.pub.RightUtil;
import com.anyi.gp.pub.SessionUtils;
import com.anyi.gp.taglib.components.Page;
import com.anyi.gp.util.StringTools;

public class SelectPageBean extends PaginationPageBean{
  
  public final static String GRID_COLUMN_ADJUST_WIDTH = "6px";
  
  private boolean isMultiSelect;
  
  private List selectFields;
  
  private String sqlid;
  
  private String condition;
  
  private int totalcount;
  
  private String masterCompoName;
  
  private String masterTableName;
  
  private String masterSelectField;
  
  private boolean isFromSql = false;
  
  private String svCond;
  
  public String getSvCond() {
    return svCond;
  }

  public void setSvCond(String svCond) {
    this.svCond = svCond;
  }

  public SelectPageBean(){
  }
  
  /**
   * 请求设置
   */
  public void setRequest(HttpServletRequest newRequest) {
    super.setRequest(newRequest);
    this.beanInit();
  }
  
  /**
   * bean初始化
   */
  protected void beanInit(){
    super.beanInit();
    setMasterCompoName();
    setMasterTableName();
    setMasterSelectField();    
    setMultiSelect();
    setSelectFields();
    setPaginationInfo();
    setCondition();
    setSqlid();

  }

  public void setMasterCompoName() {
    this.masterCompoName = request.getParameter("masterCompoName");
  }

  public void setMasterTableName() {
    this.masterTableName = request.getParameter("masterTableName");
  }
  
  public void setMasterSelectField() {
    this.masterSelectField = request.getParameter("masterFieldName");
  }
  
  public String getCondition() {
    return condition;
  }

  public void setCondition() {
    this.condition = request.getParameter("condition");
  }

  public String getSqlid() {
    return sqlid;
  }

  public void setSqlid() {
    this.sqlid = request.getParameter("sqlid");
    this.isFromSql = Pub.parseBool(request.getParameter("isFromSql"));
  }

  public int getTotalcount() {
    return totalcount;
  }

  public void setTotalcount(int totalcount) {
    this.totalcount = totalcount;
  }

  /**
   * 设置是否可以多选
   */
  protected void setMultiSelect() {
    isMultiSelect = "true".equalsIgnoreCase(request.getParameter("ismulti"));
  }
  
  /**
   * 设置选择字段,直接从delta中取字段
   */
  protected void setSelectFields() {
    if(selectFields == null){
      Datum datum = (Datum) request.getAttribute("model");
      if(datum == null){
        datum = initPageData();
      }
      
      List data = datum.getData();
      if(data == null || data.size() == 0){//重新获取
        return;
      }
      
      if(tableName == null || tableName.equals("")){
        Map map = (Map)data.get(0);
        if(map == null || map.size() == 0) return;
        selectFields = new ArrayList();
        selectFields.addAll(map.keySet());
      }
      else{
        selectFields = new ArrayList();
        selectFields.addAll(MetaManager.getTableMeta(tableName).getSelectFieldNames());
      }
    }
  }
  
  /**
   * 搜索信息元素
   * @return 
   * @throws IOException 
   */
  public String getSearchTable() {
    StringWriter out = new StringWriter();
    out.write("<tr align='right'>\n <td ></td>\n <td align=right>");
    out.write("<table border=0 cellpadding='1' cellspacing='0'><tr>");
    out.write("<td  valign=center nowrap><input type='text' ");
    out.write("id='matchValue' value='输入要搜索的关键字' style='border:1");
    out.write(" solid #8B8B89; background-color:transparent;' ondblclick='matchValue_DblClick()'  ");
    out.write("onFocus='matchValue_Focus()' onBlur='matchValue_Blur()' onKeyPress='matchValue_KeyPress()' size='20'");
    out.write("></td>");
    out.write("<td valign=center nowrap><table border=0 cellpadding='0' cellspacing='0'><tr><td><img src=\"" + Page.LOCAL_RESOURCE_PATH + "style/img/main/simple_search.jpg\" id='simpleSearch' class=\"clsListCallEdit\" onclick=\"simpleSearch()\" onMouseOver=\"menuChange(event)\" onMouseOut=\"menuBlur('simpleSearch')\"></td></tr></table></td>\n");
    out.write("<td valign=center nowrap><table border=0 cellpadding='0' cellspacing='0'><tr><td><img src=\"" + Page.LOCAL_RESOURCE_PATH + "style/img/main/high_search.jpg\" id='highSearch' class=\"clsListCallEdit\" onclick=\"openMenu(event,'searchMenu')\" onMouseOver=\"menuChange(event)\" onMouseOut=\"menuBlur('highSearch')\"></td></tr></table></td>\n");
    out.write("<td width=\"2\" background=\"E6F0F8\">&nbsp;</td>");
    out.write("</tr></table>");
    out.write("</td></tr>");
    
    return out.toString();
  }

  /**
   * 数据表表头信息
   * @return
   * @throws IOException 
   * @throws IOException 
   */
  public void getDataTableHeader(Writer out) throws IOException {
    List vfields = selectFields;
    if (vfields == null) return;
    
    String fieldName = null;
    String fieldCaption = null;
    String blankImg = "<img src=\"" + Page.LOCAL_RESOURCE_PATH + "style/img/main/blank.gif\"/></img>";
    
    //out.write(StringTools.getMargin(9));
    out.write("<td class=\"clsGridHeadCell4\" id=\"");
    out.write(tableName);
    out.write("_HFillCell\" width=\"0\">&nbsp;</td>\n");
    //out.write(StringTools.getMargin(9));
    out.write("<td class=\"clsGridHeadCell4\" align=\"center\"");
    if(!isMultiSelect) {
      out.write(" style=\"display: none\"");
    }
    out.write(" width=\"30\" id=\"");
    out.write(tableName);
    out.write("_CHKCell\"><input type=\"checkbox\" name=\"selectAll\" class=\"clsCHK\"");
    out.write(" id=\"selectAllID\" onclick=\"selectAll()\"></input></td>");
    out.write(PageRequestInfo.NEW_LINE);
    
    int colNo = 1;
    for (Iterator iter = vfields.iterator(); iter.hasNext(); colNo++) {
      fieldCaption = null;
      fieldName = (String) iter.next();
      fieldCaption = resource.getLang(fieldName);

      //out.write(StringTools.getMargin(9));
      out.write("<td class=\"clsGridHeadCell4\" align=\"center\" width=\"100\" id=\"");
      out.write(tableName + "_" + fieldName);
      out.write("Cell\"> <table cellSpacing=0 cellPadding=0 width=\"100\" border=0 ><tr><td align=\"center\">");
      out.write("<span class=\"clsGridHeadCell\" onclick=\"sortTable(");
      out.write(colNo + ")\" field=\"" + fieldName);
      out.write("\" sortdir=\"0\">" + fieldCaption + "</span>" + blankImg);
      out.write("</td><td width=\"" + GRID_COLUMN_ADJUST_WIDTH + "\"");
      out.write(" class=\"clsColResize\" onmousedown=\"mousedown()\"> </td></tr></table></td>");
      out.write(PageRequestInfo.NEW_LINE);
    }
    out.write("<td class=\"clsGridHeadCell4\"");
    out.write(" id=\"" + tableName + "_TFillCell\" width=\"0\">&nbsp;</td>\n");

  }
  
  public String getGridColTable() {
    List vfields = selectFields;
    if (vfields== null) return "";
    
    StringBuffer result = new StringBuffer();
    result.append("<table id=\"");
    result.append(tableName + "ColTable\" class=\"hideArea\">\n");
    result.append("<tr>\n");
    result.append("<td locked=false U_Hidden=false id=\"" + tableName);
    result.append("HFillField\" app=false field=\"HFill\">填充字段</td>\n");
    result.append("<td locked=true U_Hidden=false id=\"" + tableName);
    result.append("CHKField\" app=false field=\"CHK\">选择字段</td>\n");
    for (Iterator iter = vfields.iterator(); iter.hasNext();) {
      String fieldName = (String) iter.next();
      result.append("<td locked=false U_Hidden=false id=\"");
      result.append(tableName);
      result.append(fieldName);
      result.append("Field\" app=false field=\"");
      result.append(fieldName);
      result.append("\">" + resource.getLang(fieldName) + "</td>\n");
    }
    result.append("<td locked=false U_Hidden=false id=\"" + tableName);
    result.append("TFillField\" app=false field=\"TFill\">填充字段</td>\n");
    result.append("</tr>\n");
    result.append("</table>\n");
    return result.toString();
  }
  
  public String getDataCols() {
    List vfields = selectFields;
    if (vfields== null) return "";
    
    StringBuffer result = new StringBuffer();
    result.append("<COLGROUP id=\"" + tableName + "COL\">");
    result.append("<COL id=\"" + tableName + "HFillCol\" name=\"HFill\"></COL>");
    result.append("<COL id=\"" + tableName + "chkCol\" name=\"chk\"></COL>");
    
    for (Iterator iter = vfields.iterator(); iter.hasNext();) {
      String fieldName = (String) iter.next();
      result.append("<col id=\"");
      result.append(tableName);
      result.append(fieldName);
      result.append("COL\" name=\"" + fieldName + "\"");
      result.append("></COL>\n");
    }
    
    result.append("<COL id=\"" + tableName + "TFillCol\" name=\"TFill\"></COL>");
    result.append("</COLGROUP>");
    return result.toString();
  }
  
  /**
   * 数据表内容信息
   * @return
   * @throws IOException 
   */
  public void getDataTableBody(Writer out) throws IOException{
    List vfields = selectFields;
    if (vfields== null) return ;
    
    Datum datum = (Datum) request.getAttribute("model");
    if (datum == null) {
      throw new IllegalArgumentException("生成选择页面的请求参数中没有model属性。");
    }
    String totalCount = (String)datum.getMetaFieldValue("rowcountofdb");
    if(totalCount.equals("0"))
      return;
    
    List data = datum.getData();
    Map row = null;
    for (Iterator rows = data.iterator(); rows.hasNext();) {
      //out.write(StringTools.getMargin(8));
      out.write("<tr onmouseover=\"color_bh()\" onmouseout=\"color_re()\" onclick=\"gridRowClick()\">");
      out.write(NEW_LINE);
      //out.write(StringTools.getMargin(9));
      out.write("<td class=\"clsGridBodyCell4\" width=\"0\">&nbsp;</td>\n <td class=\"clsGridBodyCell4\" align=\"center\"");
      //out.write(StringTools.getMargin(9));
      
      if(!isMultiSelect) {
        out.write(" style=\"visibility: hidden\" width=\"1\"");
      } 
      else {
        out.write(" width=\"30\"");
      }
      
      out.write("><input name=\"check\" type=\"checkbox\" value=\"\" onclick=\"selectPart()\"></td>");
      out.write(PageRequestInfo.NEW_LINE);
      
      row = (Map) rows.next();
      for (Iterator iter = vfields.iterator(); iter.hasNext();) {
        String name = iter.next().toString();
        String fieldValue = StringTools.ifNull(row.get(name), "");
        String fieldData = fieldValue;
        if (fieldData == null || fieldData.equalsIgnoreCase("")) {
          fieldData = "&nbsp;";
        }
        //out.write(StringTools.getMargin(9));
        out.write("<td class=\"clsGridBodyCell4\">");
        out.write(fieldData);
        out.write("</td>");
        out.write(NEW_LINE);
      }
      
      //out.write(StringTools.getMargin(9));
      out.write("<td class=\"clsGridBodyCell4\" width=\"0\">&nbsp;</td>\n </tr>");
      //out.write(StringTools.getMargin(8));
      out.write(NEW_LINE);
    }
    
  }

  
  /**
   * 获取方案的弹出菜单
   * @param schemaType
   * @param compoId
   * @param userId
   * @return
   */
  public String getSchemaNameMenuHTML() {
    List scheList = GeneralFunc.getSearchSchema(this.compoName + "_search", userId);
    if(scheList.size() > 0){
      return getSchemaNameMenuHTML("search", this.compoName, scheList);
    }    
    return "";
  }

  private String getSchemaNameMenuHTML(String schemaType, String compoId, List scheList) {
    StringBuffer sMenu = new StringBuffer();
    StringBuffer sSystemMenu = new StringBuffer();
    StringBuffer sUserMenu = new StringBuffer();
    int maxLength = 0;
    if (schemaType.equalsIgnoreCase("search")) {
      //compoId = compoId + "_search";
      sMenu.append("<table class=menu ID=searchMenu value=\"schemaMenuID\"><tr>\n");
    }
    
    int count = 0;
    for(int i = 0; i < scheList.size(); i++){
      count++;
      Map map = (Map)scheList.get(i);
      String scheDesc = (String)map.get("USER_SCHE_DESC");
      String isSystemSche = (String)map.get("IS_SYSTEM_SCHE");
      int temp = this.calculateSymbolNum(scheDesc);
      int tmpSLen = 0;
      if (temp > 0)
        tmpSLen = scheDesc.length() - (temp + 1) / 2;
      else
        tmpSLen = scheDesc.length();
      if (tmpSLen > maxLength)
        maxLength = tmpSLen;
      
      if (isSystemSche.equalsIgnoreCase("y") || isSystemSche.equals("1"))
        sSystemMenu.append(getMenuRowHTML(schemaType, scheDesc, compoId,i + 1));
      else
        sUserMenu.append(getMenuRowHTML(schemaType, scheDesc, compoId, i + 1));
    }
      
    sMenu.append(sSystemMenu.toString());
    if (!sSystemMenu.toString().equals("") && !sUserMenu.toString().equals(""))
      sMenu.append("<tr><td><hr size='1px' color='#7184A9'></td></tr>\n");
    sMenu.append(sUserMenu.toString());
    if (count <= 0 && schemaType.equalsIgnoreCase("search"))
      sMenu.append("<tr><td id=searchMenu1 value=\"\"></td></tr>\n");
    if (count <= 0 && schemaType.equalsIgnoreCase("stat"))
      sMenu.append("<tr><td id=statMenu1 value=\"\"></td></tr>\n");
    if (schemaType.equalsIgnoreCase("search")) {
      sMenu.append("<tr><td id=searchMaxLen value=\"");
      sMenu.append(maxLength);
      sMenu.append("\"></td></tr>\n");
    }
    if (schemaType.equalsIgnoreCase("stat")) {
      sMenu.append("<tr><td id=statMaxLen value=\"");
      sMenu.append(maxLength);
      sMenu.append("\"></td></tr>\n");
    }
    
    sMenu.append("<tr><td><hr size='1px' color='#7184A9'></td></tr>\n");
    if (schemaType.equalsIgnoreCase("search"))
      sMenu.append("<tr><td id=searchM value=\"设置方案\" onclick='searchF()' onmouseover='doHight(event.toElement)' onmouseout='clearHight(event,\"searchM\")'>&nbsp&nbsp设置方案&nbsp&nbsp</td></tr>\n");
    
    sMenu.append("</tr></table>");
    return sMenu.toString();
  }

  private String getMenuRowHTML(String schemaType, String tmpS, String compoId, int index) {
    StringBuffer sMenu = new StringBuffer();
    sMenu.append("<tr><td id=");
    if (schemaType.equalsIgnoreCase("search")) {
      sMenu.append("searchMenu" + index);
      sMenu.append(" value=");
      sMenu.append(tmpS);
      sMenu.append(" onclick='directSearchF(\"");
      sMenu.append(tmpS);
      sMenu.append("\")' ");
      sMenu.append("onmouseover='doHight(event.toElement)' ");
      sMenu.append("onmouseout='clearHight(event,\"searchMenu");
      sMenu.append(index);
    }
    if (schemaType.equalsIgnoreCase("stat")) {
      sMenu.append("statMenu" + index);
      sMenu.append(" value=");
      sMenu.append(tmpS);
      sMenu.append(" onclick='directStatSearch(\"");
      sMenu.append(tmpS);
      sMenu.append("\")' ");
      sMenu.append("onmouseover='doHight(event.toElement)' ");
      sMenu.append("onmouseout='clearHight(event,\"statMenu");
      sMenu.append(index);
    }
    sMenu.append("\")'>&nbsp&nbsp");
    sMenu.append(tmpS);
    sMenu.append("&nbsp&nbsp</td></tr>\n");
    return sMenu.toString();
  }
  
  /**
   * 计算数字符号的个数
   *
   * @param str
   *          String
   */
  private int calculateSymbolNum(String str) {
    String symbol = "1234567890abcdefghijklmnopqrstuvwxyz!@#$%^&*(){}:?><,./';]['-+=";
    int number = 0;
    for (int i = 0; i < str.length(); i++) {
      if ((symbol.indexOf((str.substring(i, i + 1)).toLowerCase())) >= 0)
        number++;
    }
    return number;
  }
  
  /**
   * 
   * @return
   */
  public String getEntityFields(){
    StringBuffer buf = new StringBuffer();
    List fieldList = selectFields;
    buf.append("<span id=\"entityMeta\" entityName=\"");
    buf.append(compoName);
    buf.append("\" sqlid=\"");
    buf.append(sqlid);
    buf.append("\" condition=\"");
    buf.append(condition);
    buf.append("\" totalcount=\"");
    buf.append(totalcount);    
    buf.append("\" tableName=\"");
    buf.append(tableName);
    buf.append("\" masterCompoName=\"");
    buf.append(masterCompoName);
    buf.append("\" masterTableName=\"");
    buf.append(masterTableName);
    buf.append("\" masterSelectField=\"");
    buf.append(masterSelectField);
    buf.append("\" realFieldName=\"");
    buf.append(request.getParameter("realFieldName"));
    buf.append("\" isFromSql=\"");
    buf.append(isFromSql);    
    buf.append("\" pageField=\"null\" dateField=\"null\" valsetField=\"null\" parentCompo=\"null\" wfCompoName=\"");
    buf.append(compoName);
    buf.append("\" wftype=\"null\" defaultTemplate=\"null\" templateIsUsed=\"false\" printtype=\"0\">\n");
    
    TableMeta tableMeta = null;
    if(tableName != null && tableName.length() > 0){
      tableMeta = MetaManager.getTableMeta(tableName);
      if(tableMeta == null && compoName != null && compoName.length() > 0){
        CompoMeta compoMeta = MetaManager.getCompoMeta(compoName);
        if(compoMeta != null)
          tableMeta = compoMeta.getTableMeta();
      }
    }
    
    if (fieldList!= null){
      for (int i= 0; i< fieldList.size(); i++){
        buf.append("<field name=\"");
        buf.append(fieldList.get(i));
        buf.append("\" no=\"");
        buf.append(i);
        
        Field field = null;
        if(tableMeta != null){
          field = tableMeta.getField((String)fieldList.get(i));
        }
        if(field != null){
          buf.append("\" type=\"" + field.getType());
          buf.append("\" save=\"" + (field.isSave() == true ? "Y" : "N"));
          buf.append("\" isKiloStyle=\"false\"");
          buf.append("\" pk=\"" + field.isPk() + "\" />\n");
        }else{
          buf.append("\" type=\"TEXT\" save=\"y\" isKiloStyle=\"false\" pk=\"false\" />\n");
        }
      }
    }
    buf.append("</span>\n");
    return buf.toString();
  }
  
  public String getForeignFieldMeta(){
    List fieldList = selectFields;
    if (fieldList== null) return "";
    
    StringWriter out = new StringWriter();
    out.write("<span name=\"foreignfieldmeta\" id=\"foreignfieldmeta\">\n");
    if (fieldList != null) {
      for (int i = 0; i < fieldList.size(); i++) {
        out.write("<field name=\"");
        out.write(fieldList.get(i).toString());
        out.write("\" ");
        out.write("no=\"");
        out.write(i + "");
        out.write("\" type=\"TEXT\" save=\"y\" isKiloStyle=\"false\" pk=\"true\" ");
        out.write(" />\n");
      }
    }
    out.write("</span>\n");
    return out.toString();
  }

  /**
   * 重新获取分页数据
   * @return
   */
  private Datum initPageData(){
    int pageIndex = 0;
    String sqlcode = request.getParameter("sqlid");
    String condition = request.getParameter("condition");
    this.isFromSql = Pub.parseBool(request.getParameter("isFromSql"));
    
    int pagesize = Integer.parseInt(ApplusContext.getEnvironmentConfig().get("pagesize"));
    if (pagesize <= 0) {
      pagesize = 100;
    }
    
    PageDataProvider dataProvider = (PageDataProvider)ApplusContext.getBean("pageDataProvider");
    Map params = new HashMap();
    DBHelper.parseParamsSimpleForSql(condition, params);
    
    int totalCount = 0;
    Datum datum = null;

    String userNumLimCondition = "";
    if(masterTableName != null && masterTableName.length() > 0
      && masterCompoName != null && masterCompoName.length() > 0){
      if(masterTableName.equals(MetaManager.getCompoMeta(masterCompoName).getMasterTable())){
        String realFieldName = request.getParameter("realFieldName");
        TableMeta tableMeta = MetaManager.getTableMeta(masterTableName);
        if(masterSelectField != null && masterSelectField.length() > 0){
          String[] sTemp = masterSelectField.split(",");
          for(int i = 0; i < sTemp.length; i++){
            Field field = tableMeta.getField(sTemp[i]);
            if(field == null || !realFieldName.equals(field.getRefField())){//主表选择字段对应的外部实体字段与外部实体的实际选择字段比较
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
                
                String cond = getListUserLimCondition(refFieldName, fieldName);
                if(cond != null && cond.length() > 0){
                  if(userNumLimCondition.length() > 0){
                    userNumLimCondition += " and " + cond;
                  }
                  else{
                    userNumLimCondition = cond;
                  }
                }
              }
            }
            else{//保存字段，添加数值权限
              String cond = getListUserLimCondition(realFieldName, sTemp[i]);
              if(cond != null && cond.length() > 0){
                if(userNumLimCondition.length() > 0){
                  userNumLimCondition += " and " + cond;
                }
                else{
                  userNumLimCondition = cond;
                }
              }
            }
          }
        }
      }
    }
    
    String cond = RightUtil.getUserNumLimCondition(request,
            userId, "fquote", compoName, null, null);
    if(cond != null && cond.length() > 0){
      if(userNumLimCondition != null && userNumLimCondition.length() > 0){
        userNumLimCondition += " and " + cond;
      }else{
        userNumLimCondition += cond;
      }
    }
    
    dataProvider.setUserNumLimCondition(userNumLimCondition);
    if(isFromSql && (sqlcode == null || sqlcode.length() == 0)){//sqlid为空直接查询表
      DBSupport support = (DBSupport)ApplusContext.getBean("dbSupport");
      List newParams = new ArrayList();
      
      StringBuffer orderStr = new StringBuffer("");//排序和年度
      TableMeta tableMeta = MetaManager.getTableMeta(tableName);
      List keyFieldNames = tableMeta.getKeyFieldNames();
      if(keyFieldNames != null){
        orderStr.append(" order by ");
        for(int i = 0; i < keyFieldNames.size(); i++){
          orderStr.append(keyFieldNames.get(i) + ",");
          if("ND".equalsIgnoreCase((String)keyFieldNames.get(i))){
            params.put("ND", SessionUtils.getAttribute(request, "svNd"));
          }
        }
      }
      
      String sql = support.wrapSqlByTableName(tableName, params, newParams);
      if(orderStr.toString().endsWith(",")){
        sql += orderStr.substring(0, orderStr.length()-1);
      }
      if(userNumLimCondition != null && userNumLimCondition.length() > 0){
          sql = support.wrapSqlByCondtion(sql, userNumLimCondition);
      }
      
      totalCount = dataProvider.getTotalCount(support.wrapSqlForCount(sql), newParams);
//      params.put("rownum", pagesize + "");
//      params.put("rn", pageIndex + "");
      
      newParams.add(pagesize + "");
      newParams.add(pageIndex + "");
      sql = support.wrapPaginationSql(sql);
      
      datum = dataProvider.getPaginationData(pageIndex + 1, totalCount, pagesize, tableName, sql, newParams, false);
    }
    else{     
      
      totalCount = dataProvider.getTotalCount(sqlcode, params);
      params.put("rownum", pagesize + "");
      params.put("rn", pageIndex + "");
    
      datum = dataProvider.getPaginationData(pageIndex + 1, totalCount, pagesize, tableName, sqlcode, params, false);
      
      datum.addMetaField("isFromSql", isFromSql + "");
      datum.addMetaField("masterCompoName", masterCompoName);
      datum.addMetaField("masterTableName", masterTableName);
      datum.addMetaField("masterSelectField", masterSelectField);
    }
    
    int totalPage = totalCount/pagesize;
    totalPage = totalCount % pagesize == 0 ? totalPage: totalPage + 1;
    
    request.setAttribute("model", datum);
    request.setAttribute(compoName + "currentPage", pageIndex + 1 + "");
    request.setAttribute(compoName + "totalPage", totalPage + "");
    request.setAttribute(compoName + "totalCount", totalCount + "");
    
    setTotalcount(totalCount);
    return datum;
  }
  
  private String getListUserLimCondition(String refFieldName, String fieldName){
    //String sqlPart = " (MASTER." + refFieldName + " in (select " + fieldName + " from " + masterTableName + " MASTER where ";
    String userNumLimCondition = RightUtil.getUserNumLimCondition(request, 
          userId, "fwatch", masterCompoName, tableName, fieldName);
    //if(userNumLimCondition.length() > 0){
    //  userNumLimCondition = sqlPart + userNumLimCondition + "))";
    //}
    if(refFieldName.equals(fieldName)){
      return userNumLimCondition;
    }
    return userNumLimCondition.replaceAll(fieldName, refFieldName);
    //return userNumLimCondition;
  }
}
