package com.anyi.gp.taglib.components;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import com.anyi.gp.Datum;
import com.anyi.gp.Pub;
import com.anyi.gp.context.ApplusContext;
import com.anyi.gp.meta.MetaManager;

public class Page {

  public final static String PAGE_TYPE_KEY = "__page_type";

  public final static String PAGE_TYPE_LIST = "list";

  public final static String PAGE_TYPE_EDIT = "edit";

  public final static String PAGE_TYPE_AUTO = "auto";

  public final static String PAGE_TYPE_REPORT = "report";

  public final static String PAGE_TYPE_SELECT = "select";

  public final static String PAGE_TYPE_CARD = "card";
  
  public static String LOCAL_RESOURCE_PATH = "/";
  
  static {
    if(Pub.parseBool(ApplusContext.getEnvironmentConfig().get("localResource"))){
      LOCAL_RESOURCE_PATH = "c:/ufgov/";
    }
  }

  public static final String TAG_INTERFACE_CONDITION = "tabledatatag_interface_condition";

  public static final String OWNER_TYPE_FREE = "free";

  public static final String OWNER_TYPE_GRID = "grid";

  public static final String OWNER_TYPE_BOXSET = "boxset";

  public static final String OWNER_TYPE_SEARCH = "search";

  public static final String DOMID_INPUT = "TextInput";

  public static final String DOMID_FOCUS_BUTTON = "FocusButton";

  public final static String SCRIPT_BEGIN = "\n<script language=\"javascript\">\n";

  public final static String SCRIPT_END = "\n</script>\n";

  private ServletRequest request = null;

  private String pageName = null;

  private String initScript = null;

  private Include include = null;

  //包含所有Free、Search、BoxSet，不包含Grid
  private List allContainers = new ArrayList();

  private Map compoMetas = new HashMap();

  private CompoMetaPart mainCompoMeta = null;

  private Map tableDatas = new HashMap();

  private SessionData sessionData = null;

  private Toolbar toolbar = null;

  private List boxsets = new ArrayList();

  private List tabstrips = new ArrayList();

  private List grids = new ArrayList();

  private Logic logic = null;

  private Map frees = new HashMap();

  private Map searches = new HashMap();

  private Map datums = new HashMap();
  
  private TreeView treeview = null;

  private String pageType = PAGE_TYPE_AUTO;

  public static Page getPage(ServletRequest request) {
    Page currPage = (Page) request.getAttribute("page");
    if (currPage == null) {
      currPage = new Page(request);
      request.setAttribute("page", currPage);
    }
    return currPage;
  }

  private Page(ServletRequest request) {
    this.request = request;
    String vsPath = ((HttpServletRequest) request).getServletPath();
    int viPos = vsPath.lastIndexOf("/");
    this.pageName = vsPath.substring(viPos + 1);
  }

  public static void addInclude(ServletRequest request, Include o) {
    o.setOwnerPage(getPage(request));
    getPage(request).include = o;
  }

  public static void addCompoMetaPart(ServletRequest request, CompoMetaPart o) {
    o.setOwnerPage(getPage(request));
    if (getPage(request).mainCompoMeta == null || o.isIsmain()) {
      getPage(request).mainCompoMeta = o;
      getPage(request).pageType = o.getType();
    }
    getPage(request).compoMetas.put(o.getName(), o);
  }

  public static void addTableDataPart(ServletRequest request, TableDataPart o) {
    o.setOwnerPage(getPage(request));
    getPage(request).tableDatas.put(o.getTablename(), o);
  }

  public static void addSessionData(ServletRequest request, SessionData o) {
    o.setOwnerPage(getPage(request));
    getPage(request).sessionData = o;
  }

  public static void addToolbar(ServletRequest request, Toolbar o) {
    o.setOwnerPage(getPage(request));
    getPage(request).toolbar = o;
  }

  public static void addTreeView(ServletRequest request, TreeView o) {
    o.setOwnerPage(getPage(request));
    getPage(request).treeview = o;
  }

  public static void addBoxSet(ServletRequest request, BoxSet o) {
    o.setOwnerPage(getPage(request));
    getPage(request).boxsets.add(o);
    getPage(request).allContainers.add(o);
    if (getPage(request).isNew()
      && o.getTableName().equalsIgnoreCase(getPage(request).getMainTableName())
      && (o.getRelaobjid() == null || o.getRelaobjid().trim().equals(""))) {
      o.setIsSetDefaultValue();
    }
  }

  public static void addGrid(ServletRequest request, Grid o) {
    o.setOwnerPage(getPage(request));
    getPage(request).grids.add(o);
  }

  public static void addTabstrip(ServletRequest request, Tabstrip o) {
    o.setOwnerPage(getPage(request));
    getPage(request).tabstrips.add(o);
  }

  public static void addEditBox(ServletRequest request, EditBox o) {
    if (o.getGroupId() != null) {
      putEditBoxInSearch(request, o);
    } else {
      putEditBoxInFree(request, o);
    }
    if (o.getCompoName() != null && !o.getCompoName().trim().equals("")
      && o.getFieldName() != null && !o.getFieldName().trim().equals("")) {
      if (MetaManager.getCompoMeta(o.getCompoName()).isNoField(o.getFieldName())) {
        o.setIsallowinput(false);
      }
    }
  }

  private static void putEditBoxInFree(ServletRequest request, EditBox o) {
    String tableName = o.getTableName();
    if (tableName == null || tableName.equals("") || o.getFieldName() == null
      || o.getFieldName().equals("")) {
      tableName = "NOTINTABLE";
    }
    Free free = (Free) getPage(request).frees.get(tableName);
    if (free == null) {
      free = new Free();
      free.setOwnerPage(getPage(request));
      free.setCompoName(o.getCompoName());
      free.setTableName(tableName);
      getPage(request).frees.put(tableName, free);
      getPage(request).allContainers.add(free);
    }
    o.setContainer(free);
    setEditBoxIntialValue(request, o, tableName);
    free.addEditBox(o);
  }

  /**
   * 满足如下条件时应该把编辑框的初始值设为其缺省值
   * 编辑页面、新增、主表字段、被Free或独立的BoxSet管理(BoxSet部分在BoxSet里控制)
   */
  private static void setEditBoxIntialValue(ServletRequest request, EditBox o,
    String tableName) {
    if (getPage(request).isNew()
      && tableName.equalsIgnoreCase(getPage(request).getMainTableName())) {
      o.setValueAsDefValue();
    }
  }

  private static void putEditBoxInSearch(ServletRequest request, EditBox o) {
    Search search = getSearch(request, o.getGroupId(),o.getTableName());
    o.setContainer(search);
    search.addEditBox(o);
  }

  private static Search getSearch(ServletRequest request, String groupId,String tableName) {
    Search search = (Search) getPage(request).searches.get(groupId);
    if (search == null) {
      search = new Search();
      search.setGroupid(groupId);
      search.setOwnerPage(getPage(request));
      search.setTableName(tableName);
      getPage(request).searches.put(groupId, search);
      getPage(request).allContainers.add(search);
    }
    return search;
  }

  public static void addSearchBox(ServletRequest request, SearchBox o) {
    o.setOwnerPage(getPage(request));
    Search search = getSearch(request, o.getGroupId(),o.getTableName());
    search.setSearchBox(o);
  }

  public static void addLogic(ServletRequest request, Logic o) {
    o.setOwnerPage(getPage(request));
    getPage(request).logic = o;
  }

  public HttpServletRequest getCurrRequest() {
    return (HttpServletRequest) this.request;
  }

  public String getMainTableName() {
    return this.mainCompoMeta.getCompoMeta().getMasterTable();
  }

  /**
   * 只有在满足如下4个条件时应该为主表增加一个缺省行：
   * 编辑页面、新增、主表的字段通过Free或独立的BoxSet管理、主表没有字段通过grid管理
   */
  private boolean shouldAddRowInMainTable() {
    return isNew()
      && (isMainTableContainer(frees.values()) || isMainTableBoxSet(this.boxsets))
      && !isMainTableContainer(grids);
  }

  private boolean isMainTableBoxSet(Collection c) {
    for (Iterator iter = c.iterator(); iter.hasNext();) {
      BoxSet boxset = (BoxSet) iter.next();
      if (boxset.getRelaobjid() != null && !boxset.getRelaobjid().trim().equals("")) {
        continue;
      }
      if (boxset.getTableName().equalsIgnoreCase(this.getMainTableName()) 
          && !boxset.getEditBoxes().isEmpty()) {
        return true;
      }
    }
    return false;
  }

  private boolean isMainTableContainer(Collection c) {
    for (Iterator iter = c.iterator(); iter.hasNext();) {
      Container cont = (Container) iter.next();
      if (cont.getTableName().equalsIgnoreCase(getMainTableName())) {
        return true;
      }
    }
    return false;
  }

  //所有标记的html输出完成后，整理对象之间的关系，为输出脚本做准备
  private void finishBuildPage() {
    moveEditBoxToFree();
    moveEditBoxToSearch();
  }

  /**
   * 当BoxSet里面的EditBox有groupId的时候，应该把它从BoxSet里取出，放到相应的Search中
   */
  private void moveEditBoxToSearch() {
    for (Iterator iter = this.searches.values().iterator(); iter.hasNext(); ){
      Search search = (Search)iter.next();
      String groupId = search.getGroupid();
      String tableName = search.getTableName();
      for (Iterator iter2 = this.boxsets.iterator(); iter2.hasNext(); ){
        BoxSet boxset = (BoxSet)iter2.next();
        if (!boxset.getTableName().equalsIgnoreCase(tableName)){
          continue;
        }
        for (Iterator iter3 = boxset.getEditBoxes().iterator(); iter3.hasNext(); ){
          EditBox editBox = (EditBox)iter3.next();
          if (editBox.getGroupId() != null && editBox.getGroupId().equalsIgnoreCase(groupId)){
            boxset.removeEditBox(editBox);
            search.addEditBox(editBox);
          }
        }
      }
    }
  }

  /**
   * 当EditBox有groupId，但是最终却没有相应的SearchBox的时候，应该把EditBox移到Free中，去掉Search
   */
  private void moveEditBoxToFree() {
    for (Iterator iter = searches.entrySet().iterator(); iter.hasNext();) {
      Map.Entry entry = (Map.Entry) iter.next();
      Search item = (Search) entry.getValue();
      if (item.hasSearchBox()) {
        continue;
      }
      for (Iterator iter2 = item.getEditBoxes().iterator(); iter2.hasNext();) {
        Page.putEditBoxInFree(this.request, (EditBox) iter2.next());
      }
      iter.remove();
    }
  }

  public void writePageInitScript(Writer out) throws IOException {
    finishBuildPage();
    writeHiddenContent(out);
    writeOnloadEvent(out);
    out.write(Page.SCRIPT_BEGIN);
    out.write("function xx_init(){\n");
    out.write("var t1 = (new Date()).getTime();\n");
    out.write("PageX.localResPath = \"" + LOCAL_RESOURCE_PATH.substring(0, LOCAL_RESOURCE_PATH.length() - 1) + "\";\n");
    if (toolbar != null) {
      out.write("//********输出Toolbar\n");
      toolbar.writeInitScript(out);
    }
    if (!this.compoMetas.isEmpty()) {
      out.write("//********输出CompoMeta\n");
      for (Iterator iter = this.compoMetas.values().iterator(); iter.hasNext();) {
        ((Component) iter.next()).writeInitScript(out);
      }
    }
    if (!this.tableDatas.isEmpty()) {
      out.write("//********输出TableData\n");
      for (Iterator iter = this.tableDatas.values().iterator(); iter.hasNext();) {
        ((Component) iter.next()).writeInitScript(out);
      }
    }
    if (this.sessionData != null) {
      out.write("//********输出SessionData\n");
      this.sessionData.writeInitScript(out);
    }
    out.write("var t2 = (new Date()).getTime();\n");
    if (!this.tabstrips.isEmpty()) {
      out.write("//********输出Tabstrip\n");
      for (Iterator iter = tabstrips.iterator(); iter.hasNext();) {
        Tabstrip ts = (Tabstrip) iter.next();
        ts.writeInitScript(out);
        out.write("\n");
      }
    }
    out.write("var t3 = (new Date()).getTime();\n");
    if (!this.frees.isEmpty()) {
      out.write("//********输出Free\n");
      for (Iterator iter = this.frees.values().iterator(); iter.hasNext();) {
        ((Component) iter.next()).writeInitScript(out);
        out.write("//***next\n");
      }
    }
    out.write("var t4 = (new Date()).getTime();\n");
    if (!this.searches.isEmpty()) {
      out.write("//********输出Search\n");
      for (Iterator iter = this.searches.values().iterator(); iter.hasNext();) {
        ((Component) iter.next()).writeInitScript(out);
        out.write("//***next\n");
      }
    }
    out.write("var t5 = (new Date()).getTime();\n");
    if (!this.grids.isEmpty()) {
      out.write("//********输出Grid\n");
      for (Iterator iter = this.grids.iterator(); iter.hasNext();) {
        ((Component) iter.next()).writeInitScript(out);
        out.write("//***next\n");
      }
    }
    out.write("var t6 = (new Date()).getTime();\n");
    if (!this.boxsets.isEmpty()) {
      out.write("//********输出Boxset\n");
      for (Iterator iter = this.boxsets.iterator(); iter.hasNext();) {
        ((Component) iter.next()).writeInitScript(out);
        out.write("//***next\n");
      }
    }
    if(this.treeview != null){
      treeview.writeInitScript(out);
    }
    out.write("var t7 = (new Date()).getTime();\n");
    out.write("//********输出Init\n");
    out.write(getPageSelfInitScript());
    if (logic != null) {
      out.write("//********Logic\n");
      logic.writeInitScript(out);
    }
    out.write("//********输出业务系统的初始化脚本\n");
    if (this.initScript != null) {
      out.write(initScript);
    }
    out.write("initialized = true;\n");
    out.write("var t8 = (new Date()).getTime();\n");
//    out.write("alert(\"load time: \\t\" + (t1 - t0)");
//    out.write("+ \"\\ntoolbar time: \\t\" + (t2 - t1)");
//    out.write("+ \"\\ntabstrip time: \\t\" + (t3 - t2)");
//    out.write("+ \"\\nfree time: \\t\" + (t4 - t3)");
//    out.write("+ \"\\nsearch time: \\t\" + (t5 - t4)");
//    out.write("+ \"\\ngrid time: \\t\" + (t6 - t5)");
//    out.write("+ \"\\nboxset time: \\t\" + (t7 - t6)");
//    out.write("+ \"\\ninit time: \\t\" + (t8 - t7)");
//    out.write("+ \"\\ntotal time: \\t\" + (t8 - t0)");
//    out.write(");\n ");
    out.write("}\n");
    out.write(Page.SCRIPT_END);
  }
  
  private void writeHiddenContent(Writer out) throws IOException {
    if (this.grids.isEmpty() && !this.pageType.equalsIgnoreCase(PAGE_TYPE_REPORT)){
      return;
    }
    out.write("\n<span style=\"display:none\">\n");
    out.write("<OBJECT\n");
    out.write("id=\"gridMaker\"\n");
    out.write("classid=\"clsid:2118CCCB-3C98-4BFC-AFC7-CE582D07A99A\"\n");
    out.write("codebase=\"/");
    out.write(Pub.getWebRoot((HttpServletRequest)this.request));
    out.write("/TableMakerProj.ocx#version=1,0,0,0\"\n");
    out.write("width=100%\n");
    out.write("height=100%\n");
    out.write("align=center\n");
    out.write("hspace=0\n");
    out.write("vspace=0\n");
    out.write(">\n");
    out.write("</OBJECT>\n");
    out.write("</span>");
  }

  private void writeOnloadEvent(Writer out) throws IOException{
    out.write("\n<script language=\"javascript\" for=\"window\" event=\"onload()\">\n");
    out.write("xx_init();\n");
    out.write(Page.SCRIPT_END);
  }

  private String getPageSelfInitScript() {
    StringBuffer voBuf = new StringBuffer();
    voBuf.append("PageX.sPageLayout= \"free\";\n");
    if (this.mainCompoMeta != null) {
      voBuf.append("PageX.sPageType= \"" + this.mainCompoMeta.getType() + "\";\n");
    }
    voBuf.append(getBoxIndexes());
    voBuf.append("PageX.sName= \"" + this.pageName + "\";\n");
    voBuf.append(addRowScript());
    voBuf.append("PageX.tIsNew = " + this.isNew() + ";\n");
    voBuf.append("PageX.init();\n");
    return voBuf.toString();
  }
  
  //输出页面编辑框的顺序，在控制焦点时用
  private String getBoxIndexes(){
    StringBuffer result = new StringBuffer();
    result.append("PageX.boxIndexes = new Array();\n");
    int i = 0;
    for (Iterator containerIter = this.allContainers.iterator(); containerIter.hasNext(); ){
      Container container = (Container)containerIter.next();
      for (Iterator boxIterator = container.getEditBoxes().iterator(); boxIterator.hasNext();){
        EditBox box = (EditBox)boxIterator.next();
        result.append("PageX.boxIndexes[");
        result.append(i++);
        result.append("] = '");
        result.append(box.getId());
        result.append("';\n");
      }
    }
    return result.toString();
  }

  private String addRowScript() {
    StringBuffer result = new StringBuffer();
    if (!shouldAddRowInMainTable()) {
      result.append("PageX.shouldAddRow = false;\n");
    } else {
      result.append("PageX.shouldAddRow = true;\n");
      for (Iterator iter = frees.values().iterator(); iter.hasNext();) {
        Free free = (Free) iter.next();
        if (free.getTableName().equalsIgnoreCase(this.getMainTableName())) {
          result.append(free.getScriptVarName() + ".iCurRow = 0;\n");
        }
      }
      for (Iterator iter = boxsets.iterator(); iter.hasNext();) {
        BoxSet boxset = (BoxSet) iter.next();
        if (boxset.getTableName().equalsIgnoreCase(this.getMainTableName())) {
          result.append(boxset.getScriptVarName() + ".iCurRow = 0;\n");
        }
      }
    }
    return result.toString();
  }

  public Grid getGridById(String gridId) {
    for (Iterator iter = grids.iterator(); iter.hasNext();) {
      Grid item = (Grid) iter.next();
      if (item.getId().equalsIgnoreCase(gridId)) {
        return item;
      }
    }
    return null;
  }

  public boolean isNew() {
    String condition = (String) this.request
      .getAttribute(Page.TAG_INTERFACE_CONDITION);
    if (condition == null){
      condition = this.request.getParameter("condition");
      if (condition == null){
        return false;
      }
    }
    return isEditPage() && (condition.indexOf("1=0") >= 0);
  }

  public void setInitScript(String initScript) {
    this.initScript = initScript;
  }

  public String getPageType() {
    return this.pageType;
  }

  private boolean isEditPage() {
    return this.getPageType().equals(Page.PAGE_TYPE_EDIT);
  }

  public String getPageName() {
    return this.pageName;
  }

  public void putDatum(String tableName, Datum data) {
    this.datums.put(tableName, data);
  }

  public Datum getDatum(String tableName) {
    return (Datum) this.datums.get(tableName);
  }

  public void setPageType(String pageType) {
    this.pageType = pageType;
  }
}
