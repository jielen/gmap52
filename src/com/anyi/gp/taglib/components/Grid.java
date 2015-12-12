package com.anyi.gp.taglib.components;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.anyi.gp.Datum;
import com.anyi.gp.Pub;
import com.anyi.gp.TableData;
import com.anyi.gp.access.DBSupport;
import com.anyi.gp.context.ApplusContext;
import com.anyi.gp.core.dao.BaseDao;
import com.anyi.gp.meta.CompoMeta;
import com.anyi.gp.meta.Field;
import com.anyi.gp.meta.MetaManager;
import com.anyi.gp.meta.TableMeta;
import com.anyi.gp.meta.TableMetaBuilder;
import com.anyi.gp.pub.DataTools;
import com.anyi.gp.pub.GeneralFunc;
import com.anyi.gp.pub.LangResource;
import com.anyi.gp.pub.SessionUtils;
import com.anyi.gp.taglib.ITag;
import com.anyi.gp.util.FileTools;
import com.anyi.gp.util.StringTools;
import com.anyi.gp.util.XMLTools;

public class Grid implements Container, HtmlComponent {

  private static final Logger log = Logger.getLogger(Grid.class);

  public static final String TYPE_GRID = "Grid";

  public static final String TYPE_DATAGRID = "DataGrid";

  public static final String TYPE_SELECTGRID = "SelectGrid";

  public static final String DOMID_SUFFIX_COL = "_Col";

  public static final String DOMID_SUFFIX_HEAD_CELL = "_HeadCell";

  public static final String DOMID_PREFIX_PARENT_CELL = "PARENT_";

  public static final int SELECT_CHECK_COL_WIDTH = 30;

  public static final String SELECT_CHECK_FIELD_NAME = "U_SelectCheckBox";

  public static final String GRID_PROPERTY_USERS = "gp" + File.separator
    + "gridproperty" + File.separator + "users" + File.separator;

  public static final String GRID_PROPERTY_DEFAULT = "gp" + File.separator
    + "gridproperty" + File.separator + "default" + File.separator;

  public final static int DEFAULT_COL_WIDTH = 100;

  private String idsuffix = "_GRID";

  private String type = TYPE_GRID;

  private String componame = "";

  private String tablename = "";

  private int pagesize = -1;

  private boolean isfromdb = true;

  private boolean isvisible = true;

  private boolean isallowvisible = true;

  private boolean iswritable = true;

  private boolean isreadonly = false;

  private boolean islightrow = true;

  private boolean isexistcheck = true;

  private boolean isenterfirstrow = true;

  private boolean ismultisel = true;

  private int tabindex = 0;

  private int cardcolcount = 1;

  private String cssclass = "";

  private String style = "";

  private String oninit = "";

  private int headrowheight = 20;

  private int rowheight = 20;

  private boolean isappendbutton = true;

  private boolean isinsertbutton = true;

  private boolean isdeletebutton = true;

  private boolean issavepropbutton = true;

  private boolean isdeletepropbutton = true;

  private boolean isautoappear = true;

  private String propfileid = "";

  private String boxsetid = "";

  private String sumfields = "";

  private String sumposition = "last";

  private String sumbackcolor = "#F1F2F6";

  private String sumdescfield = "";

  private String totaldescfield = "";

  private String sumdesc = "页计";

  private String totaldesc = "合计";

  private String innerlinecolor = "#DADBDD";

  private String sumcond = "";

  private String id = "";

  // 以上属性进入 taglib;

  private boolean ispagiatclient = false;

  private String bodyText = "";

  private int zindex = 0;

  private int headrowcount = 1;

  private TableMeta tablemeta = null;

  private CompoMeta compoMeta = null;

  private Map tablefieldmap = null;

  private Document paramDoc = null;

  private Node headtable = null;

  private List fieldslist = null;

  private Map paramfieldmap = null;

  private Map valuesetmap = null;

  private Map editboxmap = null;

  private int width = 0;

  private int height = 0;

  private String position = "absolute";

  private boolean isregjs = true;

  private String headdigest = "";

  private GridProp gridProp = new GridProp();

  private CellAttr[][] axxoHeadCell = null;

  private String useridforarea = null;

  private String pagenameforarea = null;

  private String outerpanelclass = "clsGridOuterPanel4";

  private String innerpanelclass = "clsGridInnerPanel4";

  private String headtableclass = "clsGridHeadTable4";

  private String bodytableclass = "clsGridBodyTable4";

  private String bodyimagepanelclass = "clsGridBodyImagePanel4";

  private String componameforboxset = "";

  private String tablenameforboxset = "";

  private boolean isfromdbforboxset = true;

  private Datum mainData = null;
  
  private List asFieldName = new ArrayList();
  
  private boolean asFieldNameInitialized = false;
  
  private String rowidField = null;

  private Page ownerPage = null;

  private boolean initialized = false;

  private HttpServletRequest request = null;

  public String getId() {
    return this.id;
  }

  public void setOwnerPage(Page ownerPage) {
    this.ownerPage = ownerPage;
  }

  protected void init() {
    init(true);
  }

  public void writeHTML(Writer out) throws IOException {
    if (!this.initialized) {
      init();
    }
    make(out);
  }

  public String writeHTML() {
    StringWriter sw = new StringWriter();
    try {
      writeHTML(sw);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return sw.toString();
  }

  public void writeInitScript(Writer out) throws IOException {
    if (!this.initialized) {
      init();
    }
    out.write("var " + this.getScriptVarName());
    out.write(" = new " + this.getType() + "();\n");
    writeInitMetaScript(out);
    out.write(this.getScriptVarName() + ".make('" + this.getId() + "');\n");
    if (this.getPagesize() > 0) {
      if (this.ownerPage.getPageType().equals(Page.PAGE_TYPE_REPORT)
        || this.ownerPage.getPageType().equals(Page.PAGE_TYPE_EDIT)) {
        out.write("vtIsPagiAtClient= true;\n");
      }
      out.write(this.getScriptVarName() + ".oPagination.setPagiAtClient(false);\n");
    }

    out.write(this.getScriptVarName() + ".init();\n");
    out.write(this.getScriptVarName() + ".refreshSumRow();\n");
    if (width > 0) {
      out.write(this.getScriptVarName() + ".oRect.iWidth=" + width + ";\n");
    }
    if (height > 0) {
      out.write(this.getScriptVarName() + ".oRect.iHeight=" + height + ";\n");
    }
    out.write(this.getScriptVarName() + ".resize();\n");
    out.write(this.getScriptVarName() + ".restoreProp();\n");

    out.write("PageX.regCtrlObj(\"" + this.getId() + "\", ");
    out.write(this.getScriptVarName() + ");\n");
    out.write(this.getScriptVarName() + "=null;\n");

  }

  private void writeInitMetaScript(Writer out) throws IOException {
    out.write(this.getScriptVarName());
    out.write(".asFieldName = new Array(");
    for(int i=0,j=this.asFieldName.size(); i < j ; i++){
      out.write("\"");
      out.write((String)this.asFieldName.get(i));
      out.write("\"");
      if (i < j - 1){
        out.write(",");
      }
    }
    out.write(");\n");
    if (this.rowidField != null){
      out.write(this.getScriptVarName());
      out.write(".sRowIdField = \"" + this.rowidField + "\";\n");
    }
  }

  public void init(boolean tIsFromJSP) {
    if (this.getId() == null || this.getId().length() == 0) {
      this.setId(this.getTableName() + this.getIdsuffix());
    }
    this.paramDoc = XMLTools.stringToDocument(this.getBodyText());
    if (this.paramDoc == null) {
      throw new RuntimeException(this.getClass().getName()
        + ".init():\n缺少参数: <meta>;\n");
    }
    try {
      this.headtable = XPathAPI.selectSingleNode(this.paramDoc, "//head/table");
    } catch (TransformerException e) {
      throw new RuntimeException(e);
    }
    if (tIsFromJSP) {
      initProp();
    }
    Node voFields;
    try {
      voFields = XPathAPI.selectSingleNode(this.paramDoc, "//fields");
    } catch (TransformerException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
    if (voFields == null) {
      throw new RuntimeException("在<applus:grid>中的<applus:meta>中没有发现<fields>;");
    }
    this.paramfieldmap = DataTools.makeFieldMap(voFields, true);
    this.fieldslist = (List) this.paramfieldmap.get(DataTools.FIELD_LIST_KEY);
    // 外部接口参数处理；chupp;20060824
    this.initOuterInterfaceFieldProp();
    // 接口参数处理; 
    this.initTagInterfaceFieldProp();
    this.initHeadCell();
    this.initTableMeta();
    this.initFieldAlign();
    if (tIsFromJSP) {
      this.initVS();
      this.editboxmap = this.initEditBox();
    }
    componameforboxset = getCompoName();
    tablenameforboxset = getTableName();
    isfromdbforboxset = isIsfromdb();
    this.initialized = true;
  }

  /**
   * 外部接口参数的处理,chupp,20060824
   */
  private void initOuterInterfaceFieldProp() {
    HttpServletRequest request = this.getRequest();
    for (Iterator iter = this.fieldslist.iterator(); iter.hasNext();) {
      Node voParamField = (Node) this.paramfieldmap.get(iter.next());
      String vsField = XMLTools.getNodeAttr(voParamField, "name");
      Node voOuterField = DataTools.getOuterTableField(getTableName(), request,
        vsField);
      if (voOuterField == null)
        continue;
      XMLTools.copyAttrs(voOuterField, voParamField);
    }
  }

  private void initFieldAlign() {
    Node voParamField = null;
    Field voTableField = null;
    String vsFieldName = "";
    String vsFieldType = "";
    String vsOldAlign = "";
    for (Iterator iter = this.fieldslist.iterator(); iter.hasNext();) {
      voParamField = (Node) this.paramfieldmap.get(iter.next());
      vsFieldName = XMLTools.getNodeAttr(voParamField, "name");
      voTableField = (Field) this.tablefieldmap.get(vsFieldName);
      vsFieldType = voTableField.getType();
      if (vsFieldType.toUpperCase().equals("NUM")) {
        vsOldAlign = XMLTools.getNodeAttr(voParamField, "align");
        vsOldAlign = vsOldAlign.trim();
        if (vsOldAlign == null || (!vsOldAlign.equals("left"))
          && !vsOldAlign.equals("center") && !vsOldAlign.equals("right")) {
          XMLTools.setNodeAttr(voParamField, "align", "right");
        }
      }
    }
  }

  private void initHeadCell() {
    if (this.headtable != null && this.axxoHeadCell == null) {
      this.axxoHeadCell = this.parseTDMap(this.headtable);
    }
    if (this.axxoHeadCell == null) {
      return;
    }
    int viRow = this.axxoHeadCell.length - 1;
    List voFieldsList = new ArrayList();
    String vsField = "";
    for (int i = 0, len = this.axxoHeadCell[viRow].length; i < len; i++) {
      vsField = this.axxoHeadCell[viRow][i].fieldname;
      if (this.fieldslist.contains(vsField) == false) {
        continue;
      }
      voFieldsList.add(vsField);
    }
    this.fieldslist = voFieldsList;
  }

  private void initProp() {
    this.headdigest = StringTools.getDigest(XMLTools
      .nodeToStringWithLimitedDeep(this.headtable));
    disposeProp();
    if (gridProp.isEmpty()) {
      return;
    }
    this.initPropHeadTable();
  }

  private Document makePropDoc(String sProp) {
    if (sProp == null || sProp.length() == 0) {
      return null;
    }
    StringBuffer voBuf = new StringBuffer();
    voBuf.append("<?xml version=\"1.0\"?>\n");
    voBuf.append(sProp);
    Document voPropDoc = XMLTools.stringToDocument(voBuf.toString());
    return voPropDoc;
  }

  /**
   * 处理表格格式定义;分项进行,如果没有定义,按默认定义进行;
   */
  private void disposeProp() {
    String vsDefaultProp = "";
    try {
      vsDefaultProp = FileTools.readTextFile(this.getPropFileName(this.getUserId2(),
        this.getPageName2(), this.getTableName(), this.getId(), getPropfileid(),
        false));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    String prop = "";
    if (Boolean.valueOf(ApplusContext.getEnvironmentConfig().get("isrunningmode"))
      .booleanValue()) {
      try {
        prop = FileTools
          .readTextFile(this.getPropFileName(this.getUserId2(), this.getPageName2(),
            this.getTableName(), this.getId(), getPropfileid(), true));
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    } else {
      prop = "";
    }
    if (prop == null || prop.length() < 10) {
      prop = vsDefaultProp;
      vsDefaultProp = "";
    }
    if (prop == null || prop.length() < 10) {
      return;
    }
    Document voDoc = this.makePropDoc(prop);
    Document voDefDoc = this.makePropDoc(vsDefaultProp);
    Node voCols = null;
    Node voSort = null;
    Node voHead = null;
    Node voDiscard = null;
    try {
      voCols = XPathAPI.selectSingleNode(voDoc.getFirstChild(), "cols");
      voSort = XPathAPI.selectSingleNode(voDoc.getFirstChild(), "sortfields");
      voHead = XPathAPI.selectSingleNode(voDoc.getFirstChild(), "head");
      voDiscard = XPathAPI.selectSingleNode(voDoc.getFirstChild(), "discard");
      if (voCols == null && voDefDoc != null) {
        voCols = XPathAPI.selectSingleNode(voDefDoc.getFirstChild(), "cols");
      }
      if (voSort == null && voDefDoc != null) {
        voSort = XPathAPI.selectSingleNode(voDefDoc.getFirstChild(), "sortfields");
      }
      if (voHead == null && voDefDoc != null) {
        voHead = XPathAPI.selectSingleNode(voDefDoc.getFirstChild(), "head");
      }
    } catch (TransformerException e) {
      throw new RuntimeException(e);
    }
    disposeGridPropCols(voCols);
    disposeGridPropSortedFields(voSort);
    disposeGridPropHead(voHead);
    disposeGridPropDiscard(voDiscard);
  }

  private void disposeGridPropCols(Node node) {
    if (node == null)
      return;
    gridProp.lockedField = XMLTools.getNodeAttr(node, "lockedfield", "");
    gridProp.cols = new HashMap();
    XMLTools.trimChildNodes(node);
    for (int i = 0, len = node.getChildNodes().getLength(); i < len; i++) {
      Node child = node.getChildNodes().item(i);
      String fieldName = XMLTools.getNodeAttr(child, "name", "");
      TableData col = new TableData();
      col.setField("name", fieldName);
      col.setField("isvisible", XMLTools.getNodeAttr(child, "isvisible", "true"));
      col.setField("width", XMLTools.getNodeAttr(child, "width", String
        .valueOf(DEFAULT_COL_WIDTH)));
      gridProp.cols.put(fieldName, col);
    }
  }

  private void disposeGridPropSortedFields(Node node) {
    if (node == null)
      return;
    gridProp.isAscend = Boolean.valueOf(
      XMLTools.getNodeAttr(node, "isascend", "true")).booleanValue();
    gridProp.sortedFields = XMLTools.getNodeText(node);
  }

  private void disposeGridPropDiscard(Node node) {
    if (node == null)
      return;
    String discard = XMLTools.getNodeText(node);
    if (discard != null && discard.indexOf("false") > 0)
      gridProp.disCard = false;
    else
      gridProp.disCard = true;
  }

  private void disposeGridPropHead(Node node) {
    if (node == null)
      return;
    gridProp.headDigest = XMLTools.getNodeAttr(node, "digest", "");
    XMLTools.trimChildNodes(node);
    if (node.hasChildNodes()) {
      gridProp.headTable = node.getFirstChild();
    }
  }

  private void initPropHeadTable() {
    if (gridProp.headTable == null)
      return;
    if (gridProp.headDigest.equals(this.headdigest) == false) {
      gridProp.headTable = null;
      gridProp.headDigest = "";
      return;
    }
    headtable = gridProp.headTable;
  }

  /**
   * 初始化 tablemeta;
   *
   * @throws java.lang.Exception
   */
  private void initTableMeta() {
    if (isIsfromdb() && (componame == null || componame.equals(""))) {
      String msg = "\nGrid.initTableMeta():\n参数 componame 为空;请指定一个有效的componame;grid id: "
        + getId();
      log.error(msg);
      throw new RuntimeException(msg);
    }
    this.compoMeta = MetaManager.getCompoMeta(componame);
    if (tablename == null || tablename.equals("")) {
      String msg = "\nGrid.initTableMeta():\n参数 tablename 为空;请指定一个有效的tablename;grid id: "
        + getId();
      log.error(msg);
      throw new RuntimeException(msg);
    }
    if (this.isIsfromdb()) {
      this.tablemeta = MetaManager.getTableMeta(tablename);
    } else {
      this.tablemeta = TableMetaBuilder.getDefaultTableMeta(tablename, fieldslist);
    }
    this.tablefieldmap = tablemeta.getFields();
  }

  /**
   * 获取 valueset list;
   */
  private void initVS() {
    if (this.isIsfromdb() == false) {
      return;
    }
    this.valuesetmap = new HashMap();
    for (Iterator iter = this.fieldslist.iterator(); iter.hasNext();) {
      String vsField = (String) iter.next();
      Field voField = (Field) this.tablefieldmap.get(vsField);
      if (voField == null)
        continue;
      String sqlid = null;
      String vsVSCode = voField.getVscode();
      String boxType = null;
      Node voParamField = (Node) this.paramfieldmap.get(vsField);
      if (voParamField != null) {
        String vsParamVSCode = XMLTools.getNodeAttr(voParamField, "valuesetcode");
        if (vsParamVSCode != null && vsParamVSCode.trim().length() > 0) {
          vsVSCode = vsParamVSCode;
        }
        String vsSqlid = XMLTools.getNodeAttr(voParamField, "sqlid");
        if (vsSqlid != null && vsSqlid.trim().length() > 0) {
          sqlid = vsSqlid;
        }

        boxType = XMLTools.getNodeAttr(voParamField, "editboxtype");
      }
      if ((vsVSCode == null || vsVSCode.length() == 0)
        && (boxType == null || boxType.length() == 0 || !"ComboBox"
          .equalsIgnoreCase(boxType))) {
        continue;
      }

      if (sqlid != null && sqlid.length() > 0) {
        String condition = XMLTools.getNodeAttr(voParamField, "condition");
        BaseDao dao = (BaseDao) ApplusContext.getBean("baseDao");
        DBSupport support = (DBSupport) ApplusContext.getBean("dbSupport");
        Map paramsMap = support.parseParamsSimpleForSql(condition);
        List newParams = new ArrayList();
        String sql = dao.getSql(sqlid, paramsMap, newParams);
        List result = dao.queryForListBySql(sql, newParams.toArray());
        if (result != null && !result.isEmpty()) {
          Map newMap = new HashMap();
          for (int i = 0; i < result.size(); i++) {
            Map map = (Map) result.get(i);
            newMap.put(map.get("VAL_ID").toString(), map.get("VAL"));
            valuesetmap.put(vsField, newMap);
          }
        }
      } else {
        List vsList = DataTools.getVS(vsVSCode);
        if (vsList != null) {
          Map vsMap = new HashMap();
          for (Iterator iter1 = vsList.iterator(); iter1.hasNext();) {
            String[] values = (String[]) iter1.next();
            vsMap.put(values[0], values[1]);
            valuesetmap.put(vsField, vsMap);
          }
        }
      }
    }
  }

  /**
   * 生成相关的 value set;
   *
   * @return
   */
  private String makeVS() {
    StringBuffer voBuf = new StringBuffer();
    voBuf.append("\n<span id='ValueSetSpan' style='display:none;'>\n");
    if (this.valuesetmap != null) {
      Set voFieldSet = this.valuesetmap.keySet();
      for (Iterator iter = voFieldSet.iterator(); iter.hasNext();) {
        String vsField = (String) iter.next();
        Map vsMap = (Map) valuesetmap.get(vsField);
        voBuf.append("<span id='" + vsField + "_VS' ");
        
        Set entrySet = vsMap.entrySet();
        for(Iterator iter1 = entrySet.iterator(); iter1.hasNext();){
         Entry entry = (Entry)iter1.next();
         voBuf.append("a" + entry.getKey() + "='" + entry.getValue() + "' ");
        }

        voBuf.append("></span>\n");
      }
    }
    voBuf.append("</span>\n");
    return voBuf.toString();
  }

  /**
   * 生成相关的 grid property;
   */
  private String makeProp() {
    StringBuffer voBuf = new StringBuffer();
    voBuf.append("<xml id='" + this.getId() + "_GridPropXML' asynch='false'>\n");
    if (!gridProp.isEmpty()) {
      voBuf.append(gridProp.toString());
    } else {
      voBuf.append("<root>\n</root>\n");
    }
    voBuf.append("</xml>\n");
    return voBuf.toString();
  }

  public void make(Writer out) throws IOException {
    out.write(this.makeOuterPanel_1());
    this.makeK(out);
    out.write(this.makeActionButton());
    out.write(this.makePagination());
    out.write(this.makeVS());
    out.write(this.makeProp());
    out.write(this.makeFocusStopInput());
    out.write(this.makeOuterPanel_2());
  }

  private void makeK(Writer out) throws IOException {
    String vsHeadStream = this.makeHead();
    out.write(makeInnerPanel_1());

    out.write("<div id='HeadDiv' class='clsHeadDiv4'>\n");
    out.write("<div id='HeadTableDiv' class='clsHeadTableDiv4'>\n");
    out.write(vsHeadStream);
    out.write(this.makeHeadReplaceCell());
    out.write(this.makeMoveColPanel());
    out.write("</div>\n");
    out.write("</div>\n");

    out.write(this.makeLockHead_1());
    out.write(vsHeadStream);
    out.write(this.makeLockHead_2());
    out.write(this.makeLockBody());

    makeBody(out);

    out.write(makeLockSumRow());
    out.write(makeSumRow());
    out.write(makeBodyImagePanel_1());
    out.write("<div id='BodyTableImageDiv' class='clsBodyTableImageDiv4'>\n");
    out.write("</div>\n");
    out.write(makeBodyImagePanel_2());
    out
      .write("<div id='NewDiv' style='position:absolute; left:-1000px; top:-1000px;'>\n");
    out.write("</div>\n");
    out.write(makeInnerPanel_2());
    out.write(this.makeOther());
    out.write(DataTools.makeDefaultValueSpan(this.paramfieldmap));
  }

  /**
   * 生成 action button;
   *
   * @return
   */
  private String makeActionButton() {
    if (this.getType().equals(TYPE_DATAGRID) == false) {
      this.isappendbutton = false;
      this.isinsertbutton = false;
      this.isdeletebutton = false;
    }
    String vsDisp = "";
    StringBuffer voBuf = new StringBuffer();
    voBuf
      .append("<div id='ActionButtonPanel' style='position:absolute; overflow:visible; z-index:999999; left:-1000px;top:-1000px;width:10px;height:10px;'>");
    voBuf
      .append("<table id='ActionButtonTable' border='0' cellspacing='0' cellpadding='0' style=''>");
    voBuf.append("<tbody>");
    voBuf.append("<tr>");
    voBuf.append("<td>");
    vsDisp = (this.isappendbutton) ? "" : "none";
    voBuf
      .append("<input type='button' id='AppendRowButton' value='增加' class='clsGridActionButton4' style='display:"
        + vsDisp + ";width:30px;'>");
    voBuf.append("</td>");
    voBuf.append("<td>");
    vsDisp = (this.isinsertbutton) ? "" : "none";
    voBuf
      .append("<input type='button' id='InsertRowButton' value='插入' class='clsGridActionButton4' style='display:"
        + vsDisp + ";width:30px;'>");
    voBuf.append("</td>");
    voBuf.append("<td>");
    vsDisp = (this.isdeletebutton) ? "" : "none";
    voBuf
      .append("<input type='button' id='DeleteRowButton' value='删除' class='clsGridActionButton4' style='display:"
        + vsDisp + ";width:30px;'>");
    voBuf.append("</td>");
    voBuf.append("<td>");
    voBuf.append(makeUserButtons());
    voBuf.append("</td>");
    voBuf.append("<td>");
    vsDisp = (this.issavepropbutton) ? "" : "none";
    voBuf
      .append("<input type='button' id='SavePropButton' value='保存风格' class='clsGridActionButton4' style='display:"
        + vsDisp + ";width:54px;'>");
    voBuf.append("</td>");
    voBuf.append("<td>");
    vsDisp = (this.isdeletepropbutton) ? "" : "none";
    voBuf
      .append("<input type='button' id='DeletePropButton' value='删除风格' class='clsGridActionButton4' style='display:"
        + vsDisp + ";width:54px;'>");
    voBuf.append("</td>");
    voBuf.append("</tr>");
    voBuf.append("</tbody>");
    voBuf.append("</table>");
    voBuf.append("</div>");
    return voBuf.toString();
  }

  private String makeUserButtons() {
    Node voUserDefined = null;
    try {
      voUserDefined = XPathAPI.selectSingleNode(this.paramDoc, "root/userdefined");
    } catch (Exception e) {
      String vsErr = this.getClass().getName()
        + ".makeUserButtons():\n获取 <applus:meta> 中 userdefined 信息失败.\n"
        + e.getMessage();
      log.info(vsErr);
      throw new RuntimeException(vsErr);
    }
    if (voUserDefined == null)
      return "";
    if (voUserDefined.hasChildNodes() == false)
      return "";
    List voChildList = XMLTools.getValidChildNodeList(voUserDefined);
    if (voChildList == null)
      return "";
    StringBuffer voBtnBuf = new StringBuffer();
    voBtnBuf
      .append("<table id='UserDefinedButtonTable' border='0' cellspacing='0' cellpadding='0' style=''>");
    voBtnBuf.append("<tbody>");
    voBtnBuf.append("<tr>");
    for (Iterator iter = voChildList.iterator(); iter.hasNext();) {
      Node voBtn = (Node) iter.next();
      if (voBtn == null)
        continue;
      String vsId = XMLTools.getNodeAttr(voBtn, "id", null);
      if (vsId == null || vsId.trim().length() == 0)
        continue;
      boolean vtIsUsable = Pub.parseBool(XMLTools.getNodeAttr(voBtn, "isusable",
        "true"));
      boolean vtIsVisible = Pub.parseBool(XMLTools.getNodeAttr(voBtn, "isvisible",
        "true"));
      String vsCaption = XMLTools.getNodeText(voBtn);
      if (vsCaption == null)
        vsCaption = "";
      voBtnBuf.append("<td>");
      voBtnBuf.append(makeUserButton(vsId, vsCaption, vtIsUsable, vtIsVisible));
      voBtnBuf.append("</td>");
    }
    voBtnBuf.append("</tr>");
    voBtnBuf.append("</tbody>");
    voBtnBuf.append("</table>");
    return voBtnBuf.toString();
  }

  private String makeUserButton(String id, String caption, boolean isUsable,
    boolean isVisible) {
    StringBuffer voBuf = new StringBuffer();
    voBuf.append("<input ");
    voBuf.append("type='button' ");
    voBuf.append("id='" + id + "' ");
    voBuf.append("value='" + caption + "' ");
    voBuf.append("class='clsGridActionButton4' ");
    voBuf.append("style=\"");
    voBuf.append("display:").append(isVisible ? ";" : "none;").append(" ");
    voBuf.append("\" ");
    voBuf.append(isUsable ? "" : "disabled").append(" ");
    voBuf.append(">");
    return voBuf.toString();
  }

  /**
   * 生成 edit box string;
   *
   * @return
   */
  private void makeEditBox(Writer out) throws IOException {
    if (TYPE_DATAGRID.equals(getType()) == false) {
      return;
    }
    Set voKeySet = this.editboxmap.keySet();
    EditBox voBox = null;
    for (Iterator iter = voKeySet.iterator(); iter.hasNext();) {
      voBox = (EditBox) this.editboxmap.get(iter.next());
      voBox.writeHTML(out);
    }
  }

  /**
   * 初始化 edit box;
   */
  private Map initEditBox() {
    Map voBoxMap = null;
    if (this.getType().equals(TYPE_DATAGRID) == false
      && (boxsetid == null || boxsetid.trim().equals(""))) {
      return null;
    }
    if (this.fieldslist == null) {
      return null;
    }
    voBoxMap = EditBoxFactory.batchMakeEditBoxByFields(fieldslist, paramfieldmap,
      getCompoName(), getTableName(), TextBox.OWNER_TYPE_GRID, isfromdb, iswritable,
      "_GridBox", "", "", this);
    if (voBoxMap != null) {
      Set fieldSet = voBoxMap.keySet();
      for (Iterator iter = fieldSet.iterator(); iter.hasNext();) {
        EditBox voBox = (EditBox) voBoxMap.get(iter.next());
        if (voBox == null)
          continue;
        voBox.init();
      }
    }
    return voBoxMap;
  }

  /**
   * 生成表头串;
   *
   * @return
   */
  private String makeHead() {
    StringBuffer voBuf = new StringBuffer();
    voBuf.append("<table id='HeadTable' ");
    voBuf.append("cellspacing='0px' cellpadding='0px' border='1px' borderColor='");
    voBuf.append(innerlinecolor);
    voBuf.append("'");
    voBuf.append("class='");
    voBuf.append(this.getHeadtableclass());
    voBuf.append(" ");
    voBuf.append(this.getCssclass());
    voBuf.append("' ");
    voBuf.append(" style='");
    voBuf.append(this.getStyle());
    voBuf.append(this.getAdjustHeadTableStyle());
    voBuf.append(";' ");
    voBuf.append(">\n");
    voBuf.append(this.makeHeadColGroup());
    voBuf.append(this.makeHeadBody());
    voBuf.append("</table>\n");
    return voBuf.toString();
  }

  /**
   * 生成表头的 tbody;
   *
   * @return
   */
  public String makeHeadBody() {
    if (this.headtable != null && this.axxoHeadCell == null) {
      this.axxoHeadCell = this.parseTDMap(this.headtable);
    }
    String vsTBody = "";
    if (this.axxoHeadCell != null) {
      vsTBody = this.makeHeadTableBodyForComplex(this.axxoHeadCell);
    } else {
      vsTBody = this.makeHeadTableBodyForSimple();
    }
    return vsTBody;
  }

  /**
   * 生成 <colGroup>串;
   */
  private String makeHeadColGroup() {
    StringBuffer buf = new StringBuffer();
    buf.append("<colgroup id='HeadColGroup'>\n");
    buf.append(makeColGroup(true, true));
    buf.append("</colgroup>\n");
    return buf.toString();
  }

  private String makeBodyColGroup(boolean forBody) {
    StringBuffer buf = new StringBuffer();
    buf.append("<colgroup id='BodyColGroup'>\n");
    buf.append(makeColGroup(false, forBody));
    buf.append("</colgroup>\n");
    return buf.toString();
  }

  /**
   *
   * @param isHead 是表头的<colgroup>,还是表体的<colgroup>;
   * @param forBody 是表体锁定部分的<colgroup>,还是表体正常部分的<colgroup>;
   * @return
   */
  private String makeColGroup(boolean isHead, boolean forBody) {
    StringBuffer buf = new StringBuffer();
    if (isHead || (!isHead && !forBody)) {
      buf.append(makeColGroupCheckCol());
    }
    if (!isHead && !forBody && StringTools.isEmptyString(gridProp.lockedField)) {
      return buf.toString();
    }
    for (Iterator iter = this.fieldslist.iterator(); iter.hasNext();) {
      String fieldName = (String) iter.next();
      buf.append(makeColGroupNormalCol(fieldName, isHead));
      if (!this.asFieldNameInitialized){
        this.asFieldName.add(fieldName);
      }
      if (!isHead && !forBody && fieldName.equals(gridProp.lockedField)) {
        break;
      }
    }
    this.asFieldNameInitialized = true;
    return buf.toString();
  }

  /**
   * 加入 checkbox 的 <col>;
   */
  private String makeColGroupCheckCol() {
    StringBuffer voBuf = new StringBuffer();
    voBuf.append("<col id='" + this.getColID(SELECT_CHECK_FIELD_NAME) + "' ");
    voBuf.append("fieldname='" + Grid.SELECT_CHECK_FIELD_NAME + "' ");
    voBuf.append("style='");
    voBuf.append("width:" + Grid.SELECT_CHECK_COL_WIDTH + "px; ");
    voBuf.append("display:" + ((this.isexistcheck) ? "" : "none") + "; ");
    voBuf.append("'");
    voBuf.append(">\n");
    return voBuf.toString();
  }

  private String makeColGroupNormalCol(String fieldName, boolean isHead) {
    StringBuffer buf = new StringBuffer(128);
    String vsDisp = "";
    String vsWidth = "";
    Node paramField = (Node) this.paramfieldmap.get(fieldName);
    Field tableField = (Field) this.tablefieldmap.get(fieldName);
    TableData propCol = null;
    if (gridProp.cols != null)
      propCol = (TableData) gridProp.cols.get(fieldName);
    buf.append("<col id='" + this.getColID(fieldName) + "' ");
    buf.append("fieldname='" + fieldName + "' ");
    buf.append("fieldtype='" + tableField.getType() + "'");
    buf.append("length='" + +tableField.getLength() + "' ");
    buf.append("scale='" + tableField.getDecLength() + "' ");
    buf.append("caption='" + XMLTools.getNodeAttr(paramField, "caption") + "' ");
    buf.append("align='" + XMLTools.getNodeAttr(paramField, "align") + "' ");
    buf.append("isforcedflt='"
      + XMLTools.getNodeAttr(paramField, "isforcedflt", "false") + "' ");
    buf.append("ispk='" + tableField.isPk() + "' ");
    if (tableField.isRowID()){
      this.rowidField = fieldName;
    }
    buf.append("isrowid='" + tableField.isRowID() + "' "); 
    buf.append("iskilo='" + tableField.getKiloStyle() + "' ");
    buf.append("issave='" + tableField.isSave() + "' ");
    boolean vtIsAllowNull = Boolean
      .valueOf(
        XMLTools.getNodeAttr(paramField, "isallownull", tableField.isAllowNull()
          + "")).booleanValue();
    buf.append("isallownull='" + vtIsAllowNull + "' ");
    if (this.isNoField(fieldName)) {
      buf.append("isallowinput='false'");
    } else {
      buf.append("isallowinput='"
        + XMLTools.getNodeAttr(paramField, "isallowinput", "true") + "' ");
    }
    boolean vtIsForceReadOnly = Pub.parseBool(XMLTools.getNodeAttr(paramField,
      "isforcereadonly"));
    if (vtIsForceReadOnly || tableField.isRowID()) {
      buf.append("isreadonly='true' ");
    } else {
      buf.append("isreadonly='" + XMLTools.getNodeAttr(paramField, "isreadonly")
        + "' ");
    }
    buf.append("isforcereadonly='" + vtIsForceReadOnly + "' ");
    buf.append("isallowvisible='"
      + XMLTools.getNodeAttr(paramField, "isallowvisible", "true") + "' ");
    buf.append("isboxvisible='"
      + XMLTools.getNodeAttr(paramField, "isboxvisible", "true") + "' ");
    buf.append("iszoomimage='"
      + XMLTools.getNodeAttr(paramField, "iszoomimage", "false") + "' ");
    EditBox voBox = null;
    if (this.getType().equals(TYPE_DATAGRID)) {
      voBox = (EditBox) this.editboxmap.get(fieldName);
    }
    if (voBox != null) {
      buf.append("editboxid='" + voBox.getId() + "' ");
      buf.append("editboxtype='"
        + XMLTools.getNodeAttr(paramField, "editboxtype", "TextBox") + "' ");
    }
    buf.append("style='");
    if (propCol != null) {
      vsWidth = propCol.getFieldValue("width");
      vsDisp = propCol.getFieldValue("isvisible");
      vsDisp = "true".equals(vsDisp) ? "" : "none";
    } else {
      vsWidth = XMLTools.getNodeAttr(paramField, "width", DEFAULT_COL_WIDTH + "");
      vsDisp = ((Pub
        .parseBool(XMLTools.getNodeAttr(paramField, "isvisible", "true"))) ? ""
        : "none");
    }
    buf.append("width:" + vsWidth + "px; ");
    buf.append("display:" + vsDisp + "; ");
    if (!isHead) {
      String align = XMLTools.getNodeAttr(paramField, "align", null);
      if (align == null) {
        String fieldType = tableField.getType();
        if (Field.DATA_TYPE_NUM.equals(fieldType)) {
          buf.append("text-align:right; ");
        }
      } else {
        buf.append("text-align:" + align + "; ");
      }
    }
    buf.append("'");
    buf.append(">\n");
    return buf.toString();
  }

  private boolean isNoField(String fieldName) {
    if (this.compoMeta != null && this.compoMeta.getNoField() != null) {
      if (fieldName.equalsIgnoreCase(this.compoMeta.getNoField())) {
        return true;
      }
    }
    Field fieldMeta = this.tablemeta.getField(fieldName);
    if (fieldMeta.isNoField()){
      return true;
    }
    return false;
  }

  
  /**
   * 生成简单表头串;
   *
   * @return
   */
  private String makeHeadTableBodyForSimple() {
    Node voField = null;
    String vsFieldName = "";
    String vsCaption = "";
    String vsCellID = "";
    StringBuffer voBuf = new StringBuffer();
    voBuf.append("<tbody>\n");
    voBuf.append("<tr style='height:" + headrowheight + "px;'>\n");
    voBuf.append(this.makeHeadTableCellForCheckCell());
    for (Iterator iter = this.fieldslist.iterator(); iter.hasNext();) {
      vsFieldName = (String) iter.next();
      voField = (Node) this.paramfieldmap.get(vsFieldName);
      vsCaption = XMLTools.getNodeAttr(voField, "caption");
      vsCellID = this.getHeadCellID(vsFieldName);
      voBuf.append(this.makeHeadTableCell(vsCellID, vsFieldName, vsCaption, 1, 1,
        "", "", true));
    }
    voBuf.append("</tr>\n");
    voBuf.append("</tbody>\n");
    return voBuf.toString();
  }

  private String makeBodyTableBody(boolean forBody) {
    return makeBodyTableBody(0, -1, forBody);
  }

  public void setMainData(Datum mainData) {
    this.mainData = mainData;
  }

  private Datum getMainData() {
    if (mainData == null) {
      return this.ownerPage.getDatum(tablename);
    } else {
      return mainData;
    }
  }

  private String makeBodyTableBody(int fromRow, int rowCount, boolean forBody) {
    Datum mainData = getMainData();
    if (mainData.hasChildDatum(tablename)) {
      mainData = mainData.getChildDatum(tablename);
    }
    String effectField = null;
    //调用PageX.makeReportGrid的报表页面无部件;leidh;20070227;
    if (!StringTools.isEmptyString(componame)
      && !StringTools.isEmptyString(tablename)) {
    }
    return makeBodyTableBody(mainData, fromRow, rowCount, effectField, forBody);
  }

  private String makeBodyTableBody(Datum datum, int fromRow, int rowCount,
    String effectField, boolean forBody) {
    List data = datum.getData();
    if (fromRow < 0)
      fromRow = 0;
    if (data == null)
      rowCount = 0;
    else if (rowCount < 0 || rowCount > data.size())
      rowCount = data.size();
    StringBuffer buf = new StringBuffer();
    buf.append("<table id='BodyTable' ");
    buf.append(getBodyTableClassAndStyle());
    buf.append(" cellpadding='0px'  cellspacing='0px' border='1px' bordercolor='");
    buf.append(innerlinecolor);
    buf.append("'>\n");
    buf.append(makeBodyColGroup(forBody));
    buf.append("<tbody>\n");
    String iCount = (String) datum.getMetaFieldValue("rowcountofdb");
    if (iCount != null && iCount.length() > 0 && Integer.parseInt(iCount) > 0) {
      for (int i = fromRow; i < fromRow + rowCount; i++) {
        Map row = (Map) data.get(i);
        buf.append(makeBodyTableRow(row, effectField, forBody));
      }
    }
    buf.append("</tbody>\n");
    buf.append("</table>\n");
    return buf.toString();
  }

  private String makeBodyTableRow(Map row, String effectField, boolean forBody) {
    StringBuffer buf = new StringBuffer();
    buf.append("<tr style='height:" + getRowheight() + "px;'>\n");
    int i = 0;
    for (Iterator iter = this.fieldslist.iterator(); iter.hasNext(); i++) {
      // 在第一列加入 checkbox; 只在 LockBodyTable 上才加入;
      if (!forBody && i == 0) {
        buf.append(makeBodyTableRowCheck(StringTools
          .ifNull(row.get(effectField), "")));
        if (StringTools.isEmptyString(gridProp.lockedField))
          break;
      }
      String field = (String) iter.next();
      String value = StringTools.ifNull(row.get(field), "");
      Map VSMap = null;
      if (valuesetmap != null) {
        VSMap = (Map) valuesetmap.get(field);
      }
      if (VSMap != null)
        value = (String) VSMap.get(value);
      if (value == null || value.length() == 0 || " ".equals(value))
        value = "&nbsp;";
      buf.append("<td class='clsGridBodyCell4' UNSELECTABLE='on'>");
      buf.append(value);
      buf.append("</td>\n");
      // 如是现在是生成锁定层的 BodyTable，则只生成到 this.sLockedFieldName 为止；
      if (!forBody && field.equals(gridProp.lockedField))
        break;
    }
    buf.append("</tr>\n");
    return buf.toString();
  }

  /**
   * 在第一列加入 checkbox; 只在 LockBodyTable 上才加入;
   */
  private String makeBodyTableRowCheck(String effectValue) {
    String name = "";
    if (!StringTools.isEmptyString(effectValue)) {
      name = getEffectPrefix() + effectValue;
    }
    return "<td class='clsCHKCell4'><input type='checkbox' class='clsCHK4' name='"
      + name + "' tabindex='-100'></td>\n";
  }

  String getEffectPrefix() {
    return "EFFECT_" + tablename + "_";
  }

  /**
   * 生成复杂表头串; axxoCellAttr[RowIndex][ColIndex];
   */
  private String makeHeadTableBodyForComplex(CellAttr[][] axxoCell) {
    CellAttr voUnit = null;
    String vsCellID = "";
    Node voField = null;
    StringBuffer voBuf = new StringBuffer();
    voBuf.append("<tbody>\n");
    LangResource resource = LangResource.getInstance();
    for (int i = 0, rows = axxoCell.length; i < rows; i++) {
      voBuf.append("<tr style='height:" + headrowheight + "px;'>\n");
      if (i == 0) {
        voBuf.append(this.makeHeadTableCellForCheckCell());
      }
      for (int j = 0, cols = axxoCell[i].length; j < cols; j++) {
        voUnit = axxoCell[i][j];
        if (voUnit == null) {
          throw new RuntimeException("表头定义错误;请查实更正;");
        }
        if (voUnit.rowspan <= 0 || voUnit.colspan <= 0) {
          continue;
        }
        if (voUnit.isleaf) {
          vsCellID = this.getHeadCellID(voUnit.fieldname);
          voField = (Node) this.paramfieldmap.get(voUnit.fieldname);
          voUnit.caption = XMLTools.getNodeAttr(voField, "caption");
          if (voUnit.caption.length() == 0) {
            voUnit.caption = resource.getLang(voUnit.fieldname);
          }
        } else {
          vsCellID = this.getHeadParentCellID(voUnit.rowindex + "_"
            + voUnit.colindex);
        }
        // 生成表头单元格;
        voBuf.append(this.makeHeadTableCell(vsCellID, voUnit.fieldname,
          voUnit.caption, voUnit.rowspan, voUnit.colspan, voUnit.parentcode,
          voUnit.childfields, voUnit.isleaf));
      }
      voBuf.append("</tr>\n");
    }
    voBuf.append("</tbody>\n");
    return voBuf.toString();
  }

  /**
   * 生成表头单元格串;在第一列加入 check box;
   */
  private String makeHeadTableCellForCheckCell() {
    StringBuffer voBuf = new StringBuffer();
    voBuf.append("<td class='clsCHKCell4' id='"
      + this.getHeadCellID(Grid.SELECT_CHECK_FIELD_NAME) + "' rowspan='"
      + this.headrowcount + "' colspan='1' fieldname='"
      + Grid.SELECT_CHECK_FIELD_NAME + "' ");
    voBuf.append(">");
    voBuf.append("<input type='checkbox' class='clsCHK4' tabindex='-100'></input>");
    voBuf.append("</td>\n");
    return voBuf.toString();
  }

  /**
   * 生成表头单元格串;
   */
  private String makeHeadTableCell(String sCellID, String sFieldName,
    String sCaption, int iRowSpan, int iColSpan, String sParentCode,
    String sChildFields, boolean tIsLeaf) {
    StringBuffer voBuf = new StringBuffer();
    String vsStyleClass = "clsGridHeadCell4";
    String vsAsterisk = "";
    Field voTableField = null;
    Node voParamField = null;
    if (tIsLeaf) {
      voTableField = (Field) this.tablefieldmap.get(sFieldName);
      voParamField = (Node) paramfieldmap.get(sFieldName);
      if ((voTableField.isPk())) {
        vsStyleClass = "clsGridHeadKeyCell4";
        vsAsterisk = "<span class='asterisk'>*</span>";
      } else {
        if (!Pub
          .parseBool(XMLTools.getNodeAttr(voParamField, "isallownull", "true"))
          || !voTableField.isAllowNull()) {
          vsAsterisk = "<span class='asterisk'>*</span>";
        }
      }
    }
    voBuf.append("<td class='" + vsStyleClass + "' id='" + sCellID + "' fieldname='"
      + sFieldName + "' parentcode='" + sParentCode + "' childfields='"
      + sChildFields + "' rowspan=" + iRowSpan + " colspan=" + iColSpan
      + " isleaf='" + tIsLeaf + "' sortdir='0' ");
    voBuf.append("style='padding-top:2px;' UNSELECTABLE='on'>");
    voBuf.append(sCaption + vsAsterisk);
    if (tIsLeaf) { // chupp;20061119
      voBuf.append("<span><img src=\"");
      voBuf.append(Page.LOCAL_RESOURCE_PATH + "style/img/main/blank.gif\"/></span>");
    }
    voBuf.append("</td>\n");
    return voBuf.toString();
  }

  /**
   * 列隐藏时,用于替换父单元格的 <table>;
   */
  String makeHeadReplaceCell() {
    StringBuffer voBuf = new StringBuffer();
    voBuf
      .append("<table class=\"clsGridHeadTable4\" id=\"HeadRepCellTable\" style=\"position:absolute; width:100px; border-width:0px; display:none; z-index:99;\" borderColor=\"");
    voBuf.append(innerlinecolor);
    voBuf.append("\" cellSpacing=\"0\" cellPadding=\"0\" border=\"1\">\n");
    voBuf.append("<tbody>\n");
    voBuf.append("<tr style=\"height:" + headrowheight + "px;\">\n");
    voBuf.append("<td class=\"clsGridHeadCell4\">&nbsp;</td>\n");
    voBuf.append("</tr>\n");
    voBuf.append("</tbody>\n");
    voBuf.append("</table>\n");
    return voBuf.toString();
  }

  String makeLockHead_1() {
    StringBuffer voBuf = new StringBuffer();
    voBuf
      .append("<div id='LockHeadDiv' style='position:absolute; display:; overflow:hidden; z-index:6;'>\n");
    voBuf.append("<div id='LockHeadTableDiv' class='clsHeadTableDiv4'>\n");
    return voBuf.toString();
  }

  String makeLockHead_2() {
    return "</div>\n</div>\n";
  }

  String makeLockBody() {
    StringBuffer buf = new StringBuffer();
    buf
      .append("<div id='LockBodyDiv' style='position:absolute; display:; overflow:hidden; z-index:8;'>\n");
    buf.append("<div id='LockBodyTableDiv' class='clsBodyTableDiv4'>\n");
    buf.append(makeBodyTableBody(false));
    buf.append("</div>\n");
    buf.append("</div>\n");
    return buf.toString();
  }

  private void makeBody(Writer out) throws IOException {
    out.write("<div id='BodyDiv' class='clsBodyDiv4'>\n");
    out.write("<div id='BodyTableDiv' class='clsBodyTableDiv4'>\n");
    out.write(makeBodyTableBody(true));
    out.write("</div>\n");
    makeEditBox(out);
    out.write("</div>\n");
  }

  private String makeLockSumRow() {
    StringBuffer voBuf = new StringBuffer();
    voBuf
      .append("<div id='Grid_LockSumRowDiv' style='position:absolute; display:none; overflow:hidden; z-index:20;'>\n");
    voBuf
      .append("<div id='Grid_LockSumRowTableDiv' class='clsBodyTableDiv4' style=\"");
    voBuf.append("background-color:").append(sumbackcolor).append(";");
    voBuf.append("\">\n");
    Datum datum = makeSumDelta();
    if (datum.getData().size() > 0) {
      voBuf.append(makeBodyTableBody(datum, 0, -1, null, false));
    }
    voBuf.append("</div>\n");
    voBuf.append("</div>\n");
    return voBuf.toString();
  }

  private String makeSumRow() {
    StringBuffer voBuf = new StringBuffer();
    voBuf.append("<div id='Grid_SumRowDiv' ");
    int row = 0;
    if (!StringTools.isEmptyString(sumdescfield)) {
      voBuf.append("sumrow=\"").append(row++).append("\" ");
    }
    if (!StringTools.isEmptyString(totaldescfield)) {
      voBuf.append("totalrow=\"").append(row++).append("\" ");
    }
    voBuf
      .append("style='position:absolute; display:none; overflow:hidden; z-index:20;'>\n");
    voBuf.append("<div id='Grid_SumRowTableDiv' class='clsBodyTableDiv4' style=\"");
    voBuf.append("background-color:").append(sumbackcolor).append(";");
    voBuf.append("\">\n");
    Datum datum = makeSumDelta();
    if (datum.getData().size() > 0) {
      voBuf.append(makeBodyTableBody(datum, 0, -1, null, true));
    }
    voBuf.append("</div>\n");
    voBuf.append("</div>\n");
    return voBuf.toString();
  }

  private Datum makeSumDelta() {
    Datum datum = new Datum();
    if (!StringTools.isEmptyString(sumdescfield)) {
      Map row = new HashMap();
      row.put(sumdescfield, sumdesc);
      datum.getData().add(row);
      Object obj = datum.getMetaFieldValue("rowcountofdb");
      if (obj != null && obj.toString().length() > 0) {
        datum
          .addMetaField("rowcountofdb", Integer.parseInt(obj.toString()) + 1 + "");
      } else {
        datum.addMetaField("rowcountofdb", "1");
      }
    }
    if (!StringTools.isEmptyString(totaldescfield)) {
      Map row = new HashMap();
      row.put(totaldescfield, totaldesc);
      datum.getData().add(row);
      Object obj = datum.getMetaFieldValue("rowcountofdb");
      if (obj != null && obj.toString().length() > 0) {
        datum
          .addMetaField("rowcountofdb", Integer.parseInt(obj.toString()) + 1 + "");
      } else {
        datum.addMetaField("rowcountofdb", "1");
      }
    }
    return datum;
  }

  private String makeMoveColPanel() {
    StringBuffer voBuf = new StringBuffer();
    voBuf
      .append("<div id='HeadMoveColDiv' style='position:absolute;left:-1000px;top:-1000px;display:none;'>\n");
    voBuf
      .append("<table border='0' cellspacing='0' cellpadding='0' width='100%' height='100%'>\n");
    voBuf.append("<tbody>\n");
    voBuf.append("<tr>\n");
    voBuf
      .append("<td id='HeadMoveColTD' style='text-align:center; vertical-align:middle;'>\n");
    voBuf.append("</td>\n");
    voBuf.append("</tr>\n");
    voBuf.append("</tbody>\n");
    voBuf.append("</table>\n");
    voBuf.append("</div>\n");
    return voBuf.toString();
  }

  private String makeOther() {
    StringBuffer voBuf = new StringBuffer();
    voBuf
      .append("<div id='MoveLineDiv' style='position:absolute; display:none; background-color:red; width:1px; height:100px; z-index:9;'>\n");
    voBuf.append("</div>\n");
    return voBuf.toString();
  }

  private String makeFocusStopInput() {
    return "<input type='text' id='FocusStopInput' style='position:absolute;left:-1000px;top:-1000px;width:100px;height:20px;'>\n";
  }

  private String makeOuterPanel_1() {
    StringBuffer voBuf = new StringBuffer();
    voBuf.append("<div id='");
    voBuf.append(this.getId());
    voBuf.append("' ");
    voBuf.append(this.makeAttr());
    voBuf.append(" class='");
    voBuf.append(this.getOuterpanelclass());
    voBuf.append(" ");
    voBuf.append(this.getCssclass());
    voBuf.append("' ");
    voBuf.append("style='");
    voBuf.append(this.getOuterPanelStyle());
    voBuf.append(this.getStyle());
    voBuf.append(this.getAdjustOuterPanelStyle());
    voBuf.append(";' ");
    voBuf.append("hidefocus='true' ");
    voBuf.append(">\n");
    return voBuf.toString();
  }

  private String makeOuterPanel_2() {
    return "</div>\n";
  }

  private String makeInnerPanel_1() {
    StringBuffer voBuf = new StringBuffer();
    voBuf.append("<div id='InnerDiv' ");
    voBuf.append("class='");
    voBuf.append(this.getInnerpanelclass());
    voBuf.append(" ");
    voBuf.append(this.getCssclass());
    voBuf.append("' ");
    voBuf.append("style='");
    voBuf.append(this.getStyle());
    voBuf.append(this.getAdjustInnerPanelStyle());
    voBuf.append(";' ");
    voBuf.append("hidefocus='true' ");
    voBuf.append(">\n");
    return voBuf.toString();
  }

  private String makeInnerPanel_2() {
    return "</div>\n";
  }

  private String makeBodyImagePanel_1() {
    StringBuffer voBuf = new StringBuffer();
    voBuf.append("<div id='BodyImageDiv' ");
    voBuf.append("class='");
    voBuf.append(this.getBodyimagepanelclass());
    voBuf.append(" ");
    voBuf.append(this.getCssclass());
    voBuf.append("' ");
    voBuf.append("style='");
    voBuf.append(this.getStyle());
    voBuf.append(this.getAdjustBodyImagePanelStyle());
    voBuf.append(";' ");
    voBuf.append("hidefocus='true' ");
    voBuf.append(">\n");
    return voBuf.toString();
  }

  private String makeBodyImagePanel_2() {
    return "</div>\n";
  }

  private String getOuterPanelStyle() {
    StringBuffer voBuf = new StringBuffer();
    voBuf.append(";");
    if (this.getWidth() > 0) {
      voBuf.append("width:" + this.getWidth() + "; ");
    }
    if (this.getHeight() > 0) {
      voBuf.append("height:" + this.getHeight() + "; ");
    }
    voBuf.append("display:" + (this.isIsvisible() ? "" : "none") + "; ");
    return voBuf.toString();
  }

  private String getAdjustOuterPanelStyle() {
    StringBuffer voBuf = new StringBuffer();
    voBuf.append(";");
    voBuf.append("overflow:hidden;");
    voBuf.append("padding:0px;");
    voBuf.append("margin:0px;");
    voBuf.append("border-width:0px;");
    return voBuf.toString();
  }

  private String getAdjustInnerPanelStyle() {
    StringBuffer voBuf = new StringBuffer();
    voBuf.append(";");
    voBuf.append("position:postion;");
    voBuf.append("overflow:hidden;");
    voBuf.append("padding:0px;");
    voBuf.append("margin:0px;");
    voBuf.append("display:;");
    voBuf.append("visibility:visible;");
    voBuf.append("left:;");
    voBuf.append("top:;");
    voBuf.append("width:100%;");
    voBuf.append("height:100%;");
    return voBuf.toString();
  }

  private String getAdjustBodyImagePanelStyle() {
    StringBuffer voBuf = new StringBuffer();
    voBuf.append(";");
    voBuf.append("position:postion;");
    voBuf.append("overflow:auto;");
    voBuf.append("border-width:0;");
    voBuf.append("padding:0px;");
    voBuf.append("margin:0px;");
    voBuf.append("display:;");
    voBuf.append("visibility:visible;");
    voBuf.append("left:;");
    voBuf.append("top:;");
    voBuf.append("width:100%;");
    voBuf.append("height:100%;");
    return voBuf.toString();
  }

  private String getAdjustHeadTableStyle() {
    StringBuffer voBuf = new StringBuffer();
    voBuf.append(";");
    voBuf.append("position:absolute;");
    voBuf.append("table-layout:fixed;");
    voBuf.append("border-width:0;");
    voBuf.append("display:;");
    voBuf.append("visibility:visible;");
    voBuf.append("left:0;");
    voBuf.append("top:;");
    voBuf.append("width:;");
    voBuf.append("height:;");
    return voBuf.toString();
  }

  private String getAdjustBodyTableStyle() {
    StringBuffer voBuf = new StringBuffer();
    voBuf.append(";");
    voBuf.append("position:absolute;");
    voBuf.append("table-layout:fixed;");
    voBuf.append("border-width:0;");
    voBuf.append("display:;");
    voBuf.append("visibility:visible;");
    voBuf.append("left:0;");
    voBuf.append("top:;");
    voBuf.append("width:;");
    voBuf.append("height:;");
    return voBuf.toString();
  }

  private String makeAttr() {
    StringBuffer voBuf = new StringBuffer();
    voBuf.append("componame='" + this.componame + "' ");
    voBuf.append("tablename='" + this.tablename + "' ");
    voBuf.append("headrowheight=" + headrowheight + " ");
    voBuf.append("rowheight=" + this.rowheight + " ");
    voBuf.append("tabindex=" + this.tabindex + " ");
    voBuf.append("cardcolcount=" + this.cardcolcount + " ");
    // voBuf.append("isvisible='" + this.isvisible + "' ");
    voBuf.append("iswritable='" + this.iswritable + "' ");
    voBuf.append("isreadonly='" + this.isreadonly + "' ");
    voBuf.append("islightrow='" + this.islightrow + "' ");
    voBuf.append("isexistcheck='" + this.isexistcheck + "' ");
    voBuf.append("isenterfirstrow='" + this.isenterfirstrow + "' ");
    voBuf.append("ismultisel='" + this.ismultisel + "' ");
    voBuf.append("isappendbutton='" + this.isappendbutton + "' ");
    voBuf.append("isinsertbutton='" + this.isinsertbutton + "' ");
    voBuf.append("isdeletebutton='" + this.isdeletebutton + "' ");
    voBuf.append("issavepropbutton='" + this.issavepropbutton + "' ");
    voBuf.append("isdeletepropbutton='" + this.isdeletepropbutton + "' ");
    voBuf.append("isautoappear='" + this.isIsautoappear() + "' ");
    voBuf.append("isfromdb='" + this.isfromdb + "' ");
    voBuf.append("headdigest='" + this.headdigest + "' ");
    voBuf.append("propfileid='" + this.getPropfileid() + "' ");
    voBuf.append("sumfields='" + this.getSumfields() + "' ");
    voBuf.append("sumposition='" + this.getSumposition() + "' ");
    voBuf.append("sumbackcolor='" + this.getSumbackcolor() + "' ");
    voBuf.append("sumdescfield='" + sumdescfield + "' ");
    voBuf.append("totaldescfield='" + totaldescfield + "' ");
    voBuf.append("sumdesc='" + sumdesc + "' ");
    voBuf.append("sumcond=\"" + sumcond + "\" ");
    voBuf.append("totaldesc='" + totaldesc + "' ");
    voBuf.append("innerlinecolor=\"" + innerlinecolor + "\" ");
    voBuf.append("bodytableclassandstyle=\"" + getBodyTableClassAndStyle() + "\" ");
    return voBuf.toString();
  }

  private String getBodyTableClassAndStyle() {
    StringBuffer voStyleBuf = new StringBuffer();
    voStyleBuf.append("class='");
    voStyleBuf.append(this.getBodytableclass());
    voStyleBuf.append(" ");
    voStyleBuf.append(this.getCssclass());
    voStyleBuf.append("' ");
    voStyleBuf.append("style='");
    voStyleBuf.append(this.getStyle());
    voStyleBuf.append(this.getAdjustBodyTableStyle());
    voStyleBuf.append(";' ");
    return voStyleBuf.toString();
  }

  private String getColID(String sFieldName) {
    if (sFieldName == null || sFieldName.length() == 0) {
      return null;
    }
    String vsColID = sFieldName + Grid.DOMID_SUFFIX_COL;
    return vsColID;
  }

  /**
   * 获取列头
   * <TD>的id.
   *
   * @param sFieldName
   * @return
   */
  private String getHeadCellID(String sFieldName) {
    if (sFieldName == null || sFieldName.length() == 0) {
      return "";
    }
    String vsCellID = sFieldName + Grid.DOMID_SUFFIX_HEAD_CELL;
    return vsCellID;
  }

  /**
   * 获取列头的父
   */
  private String getHeadParentCellID(String sParentCode) {
    String vsCellID = Grid.DOMID_PREFIX_PARENT_CELL + sParentCode
      + Grid.DOMID_SUFFIX_HEAD_CELL;
    return vsCellID;
  }

  /**
   * 分析 <Head><Table></Head> 中的 <table>;
   */
  private CellAttr[][] parseTDMap(Node oTable) {
    if (oTable == null || oTable.getNodeName().equalsIgnoreCase("table") == false) {
      return null;
    }
    if (oTable.getChildNodes() == null) {
      return null;
    }
    List voRowList = XMLTools.getValidChildNodeList(oTable);
    int viRowCount = voRowList.size();
    if (viRowCount == 0) {
      return null;
    }
    if (((Node) voRowList.get(0)).getNodeName().equalsIgnoreCase("tbody")) {
      oTable = (Node) voRowList.get(0);
      voRowList = XMLTools.getValidChildNodeList(oTable);
      viRowCount = voRowList.size();
      if (viRowCount == 0) {
        return null;
      }
    }
    int viColCount = 0;
    this.headrowcount = viRowCount;
    Map voUnitMap = new HashMap();
    CellAttr voUnit = null;
    CellAttr voUnit2 = null;
    String vsKey = "";
    Node voRow = null;
    Node voCell = null;
    for (int i = 0, len = voRowList.size(); i < len; i++) {
      voRow = (Node) voRowList.get(i);
      if (voRow.getNodeName() == "#text") {
        continue;
      }
      List voCellList = XMLTools.getValidChildNodeList(voRow);
      for (int j = 0, k = 0, len1 = voCellList.size(); j < len1; j++, k++) {
        // 校验列序号 k 值是否被使用;
        // 如果出现合并单元格中包含有多行,则会在下行中出现列序号被占用的情况;
        while (true) {
          vsKey = i + "_" + k;
          if (voUnitMap.containsKey(vsKey)) {
            k++;
          } else {
            break;
          }
        }
        voCell = (Node) voCellList.get(j);
        if (voCell.getNodeName() == "#text") {
          continue;
        }
        voUnit = new CellAttr();
        voUnit.rowindex = i;
        voUnit.colindex = k;
        voUnit.rowspan = Pub.parseInt(XMLTools.getNodeAttr(voCell, "rowspan"));
        voUnit.colspan = Pub.parseInt(XMLTools.getNodeAttr(voCell, "colspan"));
        if (voUnit.rowspan <= 0) {
          voUnit.rowspan = 1;
        }
        if (voUnit.colspan <= 0) {
          voUnit.colspan = 1;
        }
        voUnit.caption = XMLTools.getNodeText(voCell);
        voUnit.fieldname = voUnit.caption;
        voUnit.parentcode = "";
        voUnit.childfields = "";
        voUnit.isleaf = ((i + voUnit.rowspan >= viRowCount) ? true : false);
        vsKey = voUnit.rowindex + "_" + voUnit.colindex;
        voUnitMap.put(vsKey, voUnit);
        if (voUnit.colindex + voUnit.colspan > viColCount) {
          viColCount = voUnit.colindex + voUnit.colspan;
        }
        // 在合并的单元格中,对于位置被占用的单元格的补齐处理;
        if (voUnit.rowspan > 1 || voUnit.colspan > 1) {
          int viRowNumber = 0; // 行数记数器;
          int viColNumber = 0; // 列数记数器;
          for (int x = 0; x < voUnit.rowspan; x++) {
            for (int y = 0; y < voUnit.colspan; y++) {
              if (x == 0 && y == 0) {
                continue;
              }
              viRowNumber = voUnit.rowindex + x;
              viColNumber = voUnit.colindex + y;
              voUnit2 = new CellAttr();
              voUnit2.rowindex = viRowNumber;
              voUnit2.colindex = viColNumber;
              voUnit2.rowspan = 0;
              voUnit2.colspan = 0;
              voUnit2.caption = "";
              voUnit2.fieldname = "";
              voUnit2.parentcode = "";
              voUnit2.childfields = "";
              voUnit2.isleaf = false;
              vsKey = viRowNumber + "_" + viColNumber;
              voUnitMap.put(vsKey, voUnit2);
              if (voUnit2.colindex + voUnit2.colspan > viColCount) {
                viColCount = voUnit2.colindex + voUnit2.colspan;
              }
            }
          }
        }
        if (voUnit.colspan > 1) {
          k = voUnit.colindex + voUnit.colspan - 1;
        }
      }
    }
    // 确定父子关系;从父找到子;
    // 在子中记录直接父的 ParentCode;
    Set voKeySet = voUnitMap.keySet();
    String vsChildKey = "";
    int viChildRowIndex = -1;
    int viChildColIndex = -1;
    CellAttr voChildUnit = null;
    for (Iterator iter = voKeySet.iterator(); iter.hasNext();) {
      vsKey = (String) iter.next();
      voUnit = (CellAttr) voUnitMap.get(vsKey);
      if (voUnit.isleaf) {
        continue;
      }
      viChildRowIndex = voUnit.rowindex + voUnit.rowspan;
      for (int x = 0; x < voUnit.colspan; x++) {
        viChildColIndex = voUnit.colindex + x;
        vsChildKey = viChildRowIndex + "_" + viChildColIndex;
        voChildUnit = (CellAttr) voUnitMap.get(vsChildKey);
        if (voChildUnit == null) {
          continue;
        }
        voChildUnit.parentcode = vsKey;
        if (voChildUnit.isleaf) {
          voUnit.childfields += ";" + voChildUnit.fieldname; // child
        } else {
          voUnit.childfields += ";" + vsChildKey; // child
        }
      }
    }
    // 确定子父关系;从子找到父;
    // 在父中记录第一个子的字段名;
    voKeySet = voUnitMap.keySet();
    String vsParentKey = "";
    int viParentRowIndex = -1;
    int viParentColIndex = -1;
    CellAttr voParentUnit = null;
    for (Iterator iter = voKeySet.iterator(); iter.hasNext();) {
      vsKey = (String) iter.next();
      voUnit = (CellAttr) voUnitMap.get(vsKey);
      if (voUnit.isleaf == false) {
        continue;
      }
      for (int x = 0; x < this.headrowcount; x++) {
        viParentRowIndex = x;
        viParentColIndex = voUnit.colindex;
        vsParentKey = viParentRowIndex + "_" + viParentColIndex;
        voParentUnit = (CellAttr) voUnitMap.get(vsParentKey);
        if (voParentUnit == null) {
          continue;
        }
        voParentUnit.fieldname = voUnit.fieldname; // FieldName;
      }
    }
    // 整理成行列方式;
    CellAttr[][] vaxxoCell = new CellAttr[viRowCount][viColCount];
    voKeySet = voUnitMap.keySet();
    for (Iterator iter = voKeySet.iterator(); iter.hasNext();) {
      vsKey = (String) iter.next();
      voUnit = (CellAttr) voUnitMap.get(vsKey);
      vaxxoCell[voUnit.rowindex][voUnit.colindex] = voUnit;
    }
    voUnitMap.clear();
    return vaxxoCell;
  }

  /**
   * 设置 report grid 风格;
   *
   * @param sStyle
   * @param sArrayStyle
   */
  public void setReportStyle(String sStyle, String sArrayStyle)
    throws RuntimeException {
    if (sStyle == null || sStyle.length() == 0) {
      return;
    }
    if (sArrayStyle == null || sArrayStyle.length() == 0) {
      return;
    }
    Document voXML = XMLTools.stringToDocument(sStyle);
    List voEntityList = XMLTools.getValidChildNodeList(voXML.getFirstChild());
    if (voEntityList == null || voEntityList.size() == 0) {
      return;
    }
    String[] vasCR = StringTools.split2(sArrayStyle, "*");
    if (vasCR.length < 2) {
      throw new RuntimeException(this.getClass().getName()
        + ".setReportStyle():\n无效的参数;\nparameters: sArrayStyle: " + sArrayStyle);
    }
    int viRowCount = Pub.parseInt(vasCR[1]);
    StringBuffer voFieldsBuf = new StringBuffer();
    // 每一行中所占列数的记数器;当上下行中的列数都相等时,
    // 则将下一个 cell 转为第一行的列单元;
    int[] vaiColsPerRow = new int[viRowCount];
    int viCurRow = 0;
    StringBuffer[] vaoBuf = new StringBuffer[viRowCount]; // 每行一个
    for (int i = 0; i < viRowCount; i++) {
      vaoBuf[i] = new StringBuffer();
      vaiColsPerRow[i] = 0;
    }
    Node voEntity = null;
    Node voNameField = null;
    Node voCaptionField = null;
    Node voRowsField = null;
    Node voColsField = null;
    Node voTypeField = null;
    Node voAlignField = null;
    String vsName = "";
    String vsCaption = "";
    int viRows = 0;
    int viCols = 0;
    int viType = 0;
    String vsAlign = "left";
    for (int i = 0, len = voEntityList.size(); i < len; i++) {
      voEntity = (Node) voEntityList.get(i);
      try {
        voNameField = XPathAPI.selectSingleNode(voEntity, "field[@name='NAME']");
        voCaptionField = XPathAPI.selectSingleNode(voEntity,
          "field[@name='CAPTION']");
        voTypeField = XPathAPI.selectSingleNode(voEntity, "field[@name='TYPE']");
        voRowsField = XPathAPI.selectSingleNode(voEntity, "field[@name='ROWS']");
        voColsField = XPathAPI.selectSingleNode(voEntity, "field[@name='COLS']");
        voAlignField = XPathAPI.selectSingleNode(voEntity, "field[@name='ALIGN']");
      } catch (TransformerException e) {
        throw new RuntimeException(this.getClass().getName()
          + ".setReportStyle():\n" + e.getMessage());
      }
      vsName = "";
      vsCaption = "";
      viRows = 0;
      viCols = 0;
      viType = 0;
      vsAlign = "left";
      if (voNameField != null) {
        vsName = XMLTools.getNodeAttr(voNameField, "value");
      }
      if (voCaptionField != null) {
        vsCaption = XMLTools.getNodeAttr(voCaptionField, "value");
      }
      if (voTypeField != null) {
        viType = Pub.parseInt(XMLTools.getNodeAttr(voTypeField, "value"));
      }
      if (voRowsField != null) {
        viRows = Pub.parseInt(XMLTools.getNodeAttr(voRowsField, "value"));
      }
      if (voColsField != null) {
        viCols = Pub.parseInt(XMLTools.getNodeAttr(voColsField, "value"));
      }
      if (voAlignField != null) {
        vsAlign = XMLTools.getNodeAttr(voAlignField, "value");
      }
      if (viType == 0) {
        continue;
      }
      if (viType == 1) {
        voFieldsBuf.append(makeDefPageField(vsName, vsCaption, vsAlign));
      }
      // 找到当前单元格所应加入的行;
      viCurRow = 0;
      for (int x = 1; x < viRowCount; x++) {
        if (vaiColsPerRow[x] < vaiColsPerRow[x - 1]) {
          viCurRow = x;
          continue;
        }
      }
      // 生成当前单元格的 TD 表达串,并记入到相应的行缓冲中;
      vaoBuf[viCurRow].append("<td rowspan='" + viRows + "' colspan='" + viCols
        + "'>" + vsName + "</td>\n");
      // 记录行中列数的变化;
      for (int x = 0; x < viRows; x++) {
        if (viCurRow + x > viRowCount) {
          break;
        }
        vaiColsPerRow[viCurRow + x] += viCols;
      }
    }
    // 组成最终的表格结果,由 this.parseTDMap() 进行分析;
    StringBuffer voTableBuf = new StringBuffer();
    voTableBuf.append("<table border='1'>\n");
    for (int i = 0; i < viRowCount; i++) {
      voTableBuf.append("<tr>\n");
      voTableBuf.append(vaoBuf[i].toString());
      voTableBuf.append("</tr>\n");
    }
    voTableBuf.append("</table>\n");
    StringBuffer voBuf = new StringBuffer();
    voBuf.append("<head>\n");
    voBuf.append(voTableBuf.toString());
    voBuf.append("</head>\n");
    voBuf.append("<fields>\n");
    voBuf.append(voFieldsBuf.toString());
    voBuf.append("</fields>\n");
    this.setBodyText(voBuf.toString());
    return;
  }

  private String makePagination() {
    if (this.getPagesize() <= 0) {
      return "";
    }
    int viTopPadding = 2;
    int viWidth = 517;
    int viHeight = 22 + viTopPadding;
    StringBuffer voBuf = new StringBuffer();
    voBuf
      .append("<div id=\"PaginationConsoleDiv\" style=\"border-width:1px; position:absolute; width:"
        + viWidth + "px; height:" + viHeight + "px; overflow:hidden;\" ");
    voBuf.append("tablename='" + this.getTableName() + "' ");
    voBuf.append("pagesize='" + this.getPagesize() + "' ");
    voBuf.append("ispagiatclient='" + this.isIspagiatclient() + "' ");
    voBuf.append(">\n");
    voBuf
      .append("<table id=\"LayoutTable\" border=\"0\" cellspacing=\"0px\" cellpadding=\"0px\" style=\"position:absolute; left:0; top:"
        + viTopPadding + ";  table-layout:fixed;\">\n");
    voBuf.append("<tbody>\n");
    voBuf.append("<tr style=\"height:20px\">\n");
    voBuf
      .append("<td id=\"InfoCellTD\" style=\"width:260px; text-align:right; vertical-align:center; font-size:9pt;\">\n");
    voBuf.append("</td>\n");
    voBuf.append("<td style=\"width:10px; font-size:9pt;\">\n");
    voBuf.append("</td>\n");
    voBuf
      .append("<td style=\"width:18px; background:transparent url('" + Page.LOCAL_RESOURCE_PATH + "style/img/tab/lf.gif') no-repeat fixed right center;\">\n");
    voBuf.append("</td>\n");
    voBuf.append("<td style=\"width:30px; vertical-align:bottom;\">\n");
    voBuf
      .append("<input type=\"button\" id=\"FirstButton\" value=\"首页\" style=\"border-width:0px; height:18px; background:transparent; font-size:9pt;\">\n");
    voBuf.append("</td>\n");
    voBuf
      .append("<td style=\"width:18px; background:transparent url('" + Page.LOCAL_RESOURCE_PATH + "style/img/tab/l.gif') no-repeat fixed right center;\">\n");
    voBuf.append("</td>\n");
    voBuf.append("<td style=\"width:30px; vertical-align:bottom;\">\n");
    voBuf
      .append("<input type=\"button\" id=\"PreviousButton\" value=\"上页\" style=\"border-width:0px; height:18px; background:transparent; font-size:9pt;\">\n");
    voBuf.append("</td>\n");
    voBuf
      .append("<td style=\"width:18px; background:transparent url('" + Page.LOCAL_RESOURCE_PATH + "style/img/tab/r.gif') no-repeat fixed right center;\">\n");
    voBuf.append("</td>\n");
    voBuf.append("<td style=\"width:30px; vertical-align:bottom;\">\n");
    voBuf
      .append("<input type=\"button\" id=\"NextButton\" value=\"下页\" style=\"border-width:0px; height:18px; background:transparent; font-size:9pt;\">\n");
    voBuf.append("</td>\n");
    voBuf
      .append("<td style=\"width:18px; background:transparent url('" + Page.LOCAL_RESOURCE_PATH + "style/img/tab/rl.gif') no-repeat fixed right center;\">\n");
    voBuf.append("</td>\n");
    voBuf.append("<td style=\"width:30px; vertical-align:bottom;\">\n");
    voBuf
      .append("<input type=\"button\" id=\"LastButton\" value=\"末页\" style=\"border-width:0px; height:18px; background:transparent; font-size:9pt;\">\n");
    voBuf.append("</td>\n");
    voBuf.append("<td style=\"width:35px;\">\n");
    voBuf
      .append("<input type=\"text\" id=\"JumpTextBox\" style=\"border:1px solid gray; width:30px; height:18px; text-align:center; background:transparent;\">\n");
    voBuf.append("</td>\n");
    voBuf.append("<td style=\"width:20px;\">\n");
    voBuf
      .append("<input type=\"button\" id=\"GoButton\" value=\"go\" style=\"border:1px solid gray; height:18px; background:transparent;\">\n");
    voBuf.append("</td>\n");
    voBuf.append("</tr>\n");
    voBuf.append("</tbody>\n");
    voBuf.append("</table>\n");
    voBuf.append("</div>");
    return voBuf.toString();
  }

  private String getPropFileName(String sUserId, String sPageName,
    String sTableName, String sGridId, String sPropFileId, boolean tIsUser) {
    if (sPageName == null) {
      return null;
    }
    if (sGridId == null) {
      return null;
    }
    if (sTableName == null) {
      return null;
    }
    String vsPropPath = Grid.GRID_PROPERTY_USERS;
    if (!Boolean.valueOf(ApplusContext.getEnvironmentConfig().get("isrunningmode"))
      .booleanValue()) {
      tIsUser = false;
    }
    if (!tIsUser) {
      vsPropPath = Grid.GRID_PROPERTY_DEFAULT;
      sUserId = "default";
    }
    String vsFile = (sUserId + "$" + sPageName + "$" + sTableName + "$" + sPropFileId)
      + ".xml";
    String vsPath = GeneralFunc.getWorkPath() + vsPropPath;
    FileTools.makeDir(vsPath);
    vsFile = vsPath + vsFile;
    return vsFile;
  }

  private String getPageName2() {
    if (this.getPagenameforarea() != null) {
      return this.getPagenameforarea();
    }
    if (this.ownerPage == null) {
      return "";
    }
    return this.ownerPage.getPageName();
  }

  private String getUserId2() {
    if (this.getUseridforarea() != null) {
      return this.getUseridforarea();
    }
    String vsUserId = SessionUtils.getAttribute(this.getRequest().getSession(),
      "svUserID");
    return vsUserId;
  }

  public void setTableName(String tablename) {
    this.tablename = tablename;
  }

  public void setIswritable(boolean iswritable) {
    this.iswritable = iswritable;
  }

  public void setIslightrow(boolean islightrow) {
    this.islightrow = islightrow;
  }

  public void setIsexistcheck(boolean isexistcheck) {
    this.isexistcheck = isexistcheck;
  }

  public void setIsenterfirstrow(boolean isenterfirstrow) {
    this.isenterfirstrow = isenterfirstrow;
  }

  public void setIsmultisel(boolean ismultisel) {
    this.ismultisel = ismultisel;
  }

  public void setType(String type) {
    this.type = type;
  }

  public void setCssclass(String cssclass) {
    this.cssclass = cssclass;
  }

  public void setStyle(String style) {
    this.style = style;
  }

  public void setOninit(String oninit) {
    this.oninit = oninit;
  }

  public String getCssclass() {
    return cssclass;
  }

  public boolean isEnterfirstrow() {
    return isenterfirstrow;
  }

  public boolean isExistcheck() {
    return isexistcheck;
  }

  public boolean isLightrow() {
    return islightrow;
  }

  public boolean isMultisel() {
    return ismultisel;
  }

  public boolean isWritable() {
    return iswritable;
  }

  public String getOninit() {
    return oninit;
  }

  public String getStyle() {
    return style;
  }

  public String getTableName() {
    return tablename;
  }

  public String getType() {
    return type;
  }

  public int getPagesize() {
    return pagesize;
  }

  public void setPagesize(int pagesize) {
    this.pagesize = pagesize;
  }

  public int getTabindex() {
    return tabindex;
  }

  public void setTabindex(int tabindex) {
    this.tabindex = tabindex;
  }

  public void setWritable(boolean writable) {
    this.iswritable = writable;
  }

  public void setLightrow(boolean lightrow) {
    this.islightrow = lightrow;
  }

  public void setMultisel(boolean multisel) {
    this.ismultisel = multisel;
  }

  public boolean isIswritable() {
    return iswritable;
  }

  public boolean isIsmultisel() {
    return ismultisel;
  }

  public boolean isIslightrow() {
    return islightrow;
  }

  public boolean isIsexistcheck() {
    return isexistcheck;
  }

  public boolean isIsenterfirstrow() {
    return isenterfirstrow;
  }

  public void setExistcheck(boolean existcheck) {
    this.isexistcheck = existcheck;
  }

  public void setEnterfirstrow(boolean enterfirstrow) {
    this.isenterfirstrow = enterfirstrow;
  }

  public int getRowheight() {
    return rowheight;
  }

  public void setRowheight(int rowheight) {
    this.rowheight = rowheight;
  }

  public String getCompoName() {
    return componame;
  }

  public void setCompoName(String componame) {
    this.componame = componame;
  }

  public boolean isIsappendbutton() {
    return isappendbutton;
  }

  public void setIsappendbutton(boolean isappendbutton) {
    this.isappendbutton = isappendbutton;
  }

  public boolean isIsdeletebutton() {
    return isdeletebutton;
  }

  public void setIsdeletebutton(boolean isdeletebutton) {
    this.isdeletebutton = isdeletebutton;
  }

  public boolean isIsinsertbutton() {
    return isinsertbutton;
  }

  public void setIsinsertbutton(boolean isinsertbutton) {
    this.isinsertbutton = isinsertbutton;
  }

  public String getIdsuffix() {
    return idsuffix;
  }

  public void setIdsuffix(String idsuffix) {
    this.idsuffix = idsuffix;
  }

  public boolean isIsvisible() {
    return isvisible;
  }

  public void setIsvisible(boolean isvisible) {
    this.isvisible = isvisible;
  }

  public boolean isIsfromdb() {
    if (this.tablename == null || this.tablename.length() == 0) {
      return false;
    }
    return isfromdb;
  }

  public void setIsfromdb(boolean isfromdb) {
    this.isfromdb = isfromdb;
  }

  public int getZindex() {
    return zindex;
  }

  public void setZindex(int zindex) {
    this.zindex = zindex;
  }

  public int getWidth() {
    return width;
  }

  public void setWidth(int width) {
    this.width = width;
  }

  public int getHeight() {
    return height;
  }

  public void setHeight(int height) {
    this.height = height;
  }

  public String getPosition() {
    return position;
  }

  public void setPosition(String position) {
    this.position = position;
  }

  private String getBodyText() {
    return bodyText;
  }

  public void setBodyText(String text) {
    this.bodyText = "<root>\n" + text + "</root>\n";
  }

  public boolean isIsregjs() {
    return isregjs;
  }

  public void setIsregjs(boolean isregjs) {
    this.isregjs = isregjs;
  }

  public boolean isIspagiatclient() {
    return ispagiatclient;
  }

  public void setIspagiatclient(boolean ispagiatclient) {
    this.ispagiatclient = ispagiatclient;
  }

  public boolean isIsreadonly() {
    return isreadonly;
  }

  public void setIsreadonly(boolean isreadonly) {
    this.isreadonly = isreadonly;
  }

  public boolean isIssavepropbutton() {
    return issavepropbutton;
  }

  public void setIssavepropbutton(boolean issavepropbutton) {
    this.issavepropbutton = issavepropbutton;
  }

  public boolean isIsdeletepropbutton() {
    return isdeletepropbutton;
  }

  public void setIsdeletepropbutton(boolean isdeletepropbutton) {
    this.isdeletepropbutton = isdeletepropbutton;
  }

  public String getUseridforarea() {
    return useridforarea;
  }

  public void setUseridforarea(String useridforarea) {
    this.useridforarea = useridforarea;
  }

  public String getPagenameforarea() {
    return pagenameforarea;
  }

  public void setPagenameforarea(String pagenameforarea) {
    this.pagenameforarea = pagenameforarea;
  }

  class CellAttr {
    public int rowindex = -1;

    public int colindex = -1;

    public int rowspan = -1;

    public int colspan = -1;

    public String caption = "";

    public String fieldname = "";

    public String parentcode = "";

    public String childfields = "";

    public boolean isleaf = false;
  }

  public boolean isIsallowvisible() {
    return isallowvisible;
  }

  public void setIsallowvisible(boolean isallowvisible) {
    this.isallowvisible = isallowvisible;
  }

  public int getCardcolcount() {
    return cardcolcount;
  }

  public void setCardcolcount(int cardcolcount) {
    this.cardcolcount = cardcolcount;
  }

  public void setTagAttributes(Node oTagNode) {
    this.setId(XMLTools.getNodeAttr(oTagNode, "id", this.id));
    this.setIdsuffix(XMLTools.getNodeAttr(oTagNode, "idsuffix", this.idsuffix));
    this.setType(XMLTools.getNodeAttr(oTagNode, "type", this.type));
    this.setCompoName(XMLTools.getNodeAttr(oTagNode, "componame", this.componame));
    this.setTableName(XMLTools.getNodeAttr(oTagNode, "tablename", this.tablename));
    this
      .setIsfromdb(Boolean.valueOf(
        XMLTools.getNodeAttr(oTagNode, "isfromdb", "" + this.isfromdb))
        .booleanValue());
    this.setTabindex(Integer.parseInt(XMLTools.getNodeAttr(oTagNode, "tabindex", ""
      + this.tabindex)));
    this.setPagesize(Integer.parseInt(XMLTools.getNodeAttr(oTagNode, "pagesize", ""
      + this.pagesize)));
    this.setIsvisible(Boolean.valueOf(
      XMLTools.getNodeAttr(oTagNode, "isvisible", "" + this.isvisible))
      .booleanValue());
    this.setIswritable(Boolean.valueOf(
      XMLTools.getNodeAttr(oTagNode, "iswritable", "" + this.iswritable))
      .booleanValue());
    this.setIsreadonly(Boolean.valueOf(
      XMLTools.getNodeAttr(oTagNode, "isreadonly", "" + this.isreadonly))
      .booleanValue());
    this.setIsallowvisible(Boolean.valueOf(
      XMLTools.getNodeAttr(oTagNode, "isallowvisible", "" + this.isallowvisible))
      .booleanValue());
    this.setIslightrow(Boolean.valueOf(
      XMLTools.getNodeAttr(oTagNode, "islightrow", "" + this.islightrow))
      .booleanValue());
    this.setIsexistcheck(Boolean.valueOf(
      XMLTools.getNodeAttr(oTagNode, "isexistcheck", "" + this.isexistcheck))
      .booleanValue());
    this.setIsenterfirstrow(Boolean.valueOf(
      XMLTools.getNodeAttr(oTagNode, "isenterfirstrow", "" + this.isenterfirstrow))
      .booleanValue());
    this.setIsmultisel(Boolean.valueOf(
      XMLTools.getNodeAttr(oTagNode, "ismultisel", "" + this.ismultisel))
      .booleanValue());
    this.setIsappendbutton(Boolean.valueOf(
      XMLTools.getNodeAttr(oTagNode, "isappendbutton", "" + this.isappendbutton))
      .booleanValue());
    this.setIsinsertbutton(Boolean.valueOf(
      XMLTools.getNodeAttr(oTagNode, "isinsertbutton", "" + this.isinsertbutton))
      .booleanValue());
    this.setIsdeletebutton(Boolean.valueOf(
      XMLTools.getNodeAttr(oTagNode, "isdeletebutton", "" + this.isdeletebutton))
      .booleanValue());
    this.setIssavepropbutton(Boolean
      .valueOf(
        XMLTools.getNodeAttr(oTagNode, "issavepropbutton", ""
          + this.issavepropbutton)).booleanValue());
    this.setIsdeletepropbutton(Boolean.valueOf(
      XMLTools.getNodeAttr(oTagNode, "isdeletepropbutton", ""
        + this.isdeletepropbutton)).booleanValue());
    this.setHeadrowheight(Integer.parseInt(XMLTools.getNodeAttr(oTagNode,
      "headrowheight", "" + headrowheight)));
    this.setRowheight(Integer.parseInt(XMLTools.getNodeAttr(oTagNode, "rowheight",
      "" + this.rowheight)));
    this.setCardcolcount(Integer.parseInt(XMLTools.getNodeAttr(oTagNode,
      "cardcolcount", "" + this.cardcolcount)));
    this
      .setPropfileid(XMLTools.getNodeAttr(oTagNode, "propfileid", this.propfileid));
    this.setCssclass(XMLTools.getNodeAttr(oTagNode, "cssclass", this.cssclass));
    this.setStyle(XMLTools.getNodeAttr(oTagNode, "style", this.style));
    this.setOninit(XMLTools.getNodeAttr(oTagNode, "oninit", this.oninit));
    this.setSumfields(XMLTools.getNodeAttr(oTagNode, "sumfields", sumfields));
    this.setSumposition(XMLTools.getNodeAttr(oTagNode, "sumposition", sumposition));
    this.setSumbackcolor(XMLTools
      .getNodeAttr(oTagNode, "sumbackcolor", sumbackcolor));
    this.setSumdescfield(XMLTools
      .getNodeAttr(oTagNode, "sumdescfield", sumdescfield));
    this.setTotaldescfield(XMLTools.getNodeAttr(oTagNode, "totaldescfield",
      totaldescfield));
    this.setSumdesc(XMLTools.getNodeAttr(oTagNode, "sumdesc", sumdesc));
    this.setTotaldesc(XMLTools.getNodeAttr(oTagNode, "totaldesc", totaldesc));
  }

  protected String getBodyimagepanelclass() {
    return bodyimagepanelclass;
  }

  protected void setBodyimagepanelclass(String bodyimagepanelclass) {
    this.bodyimagepanelclass = bodyimagepanelclass;
  }

  protected String getBodytableclass() {
    return bodytableclass;
  }

  protected void setBodytableclass(String bodytableclass) {
    this.bodytableclass = bodytableclass;
  }

  protected String getHeadtableclass() {
    return headtableclass;
  }

  protected void setHeadtableclass(String headtableclass) {
    this.headtableclass = headtableclass;
  }

  protected String getInnerpanelclass() {
    return innerpanelclass;
  }

  protected void setInnerpanelclass(String innerpanelclass) {
    this.innerpanelclass = innerpanelclass;
  }

  protected String getOuterpanelclass() {
    return outerpanelclass;
  }

  protected void setOuterpanelclass(String outerpanelclass) {
    this.outerpanelclass = outerpanelclass;
  }

  public String getPropfileid() {
    return propfileid;
  }

  public void setPropfileid(String propfileid) {
    this.propfileid = propfileid;
  }

  public boolean isIsautoappear() {
    return isautoappear;
  }

  public void setIsautoappear(boolean isautoappear) {
    this.isautoappear = isautoappear;
  }

  public String getSumfields() {
    return sumfields;
  }

  public void setSumfields(String sumfields) {
    this.sumfields = sumfields;
  }

  public String getSumposition() {
    return sumposition;
  }

  public void setSumposition(String sumposition) {
    this.sumposition = sumposition;
  }

  public String getSumbackcolor() {
    return sumbackcolor;
  }

  public void setSumbackcolor(String sumbackcolor) {
    this.sumbackcolor = sumbackcolor;
  }

  public Map getParamFields() {
    return paramfieldmap;
  }

  public String getSumdesc() {
    return sumdesc;
  }

  public void setSumdesc(String sumdesc) {
    if(sumdesc == null)
      this.sumdesc = "";
    else
      this.sumdesc = LangResource.getInstance().getLang(sumdesc);
  }

  public String getSumdescfield() {
    return sumdescfield;
  }

  public void setSumdescfield(String sumdescfield) {
      this.sumdescfield = sumdescfield;
  }

  public String getTotaldesc() {
    return totaldesc;
  }

  public void setTotaldesc(String totaldesc) {
    if(totaldesc == null)
      totaldesc = "";
    else
      this.totaldesc = LangResource.getInstance().getLang(totaldesc);
  }

  public String getTotaldescfield() {
    return totaldescfield;
  }

  public void setTotaldescfield(String totaldescfield) {
    this.totaldescfield = totaldescfield;
  }

  public Map getTableFields() {
    return tablefieldmap;
  }

  public Map getEditBoxs() {
    return editboxmap;
  }

  public List getFieldNames() {
    return fieldslist;
  }

  public String getBoxsetid() {
    return boxsetid;
  }

  public void setBoxsetid(String boxsetid) {
    this.boxsetid = boxsetid;
  }

  public String getTablenameforboxset() {
    return tablenameforboxset;
  }

  public String getComponameforboxset() {
    return componameforboxset;
  }

  public boolean isIsfromdbforboxset() {
    return isfromdbforboxset;
  }

  public String getInnerlinecolor() {
    return innerlinecolor;
  }

  public void setInnerlinecolor(String innerlinecolor) {
    this.innerlinecolor = innerlinecolor;
  }

  public String getSumcond() {
    return sumcond;
  }

  public void setSumcond(String sumcond) {
    this.sumcond = sumcond;
  }

  public Map getGridPropCols(){
    return this.gridProp.cols;
  }
  
  private class GridProp {
    public String lockedField;

    public Map cols; //key=fieldName; value=col with TableData;

    public String sortedFields;

    public boolean isAscend = true;

    public boolean disCard = true;

    public Node headTable;

    public String headDigest;

    public GridProp() {
      clear();
    }

    public void clear() {
      lockedField = "";
      cols = null;
      sortedFields = "";
      isAscend = true;
      headTable = null;
      headDigest = "";
    }

    public boolean isEmpty() {
      if (!StringTools.isEmptyString(lockedField))
        return false;
      if (!StringTools.isEmptyString(sortedFields))
        return false;
      if (cols != null && cols.size() > 0)
        return false;
      if (headTable != null)
        return false;
      return true;
    }

    public String toString() {
      StringBuffer buf = new StringBuffer();
      buf.append("<root>\n");
      buf.append("<cols lockedfield=\"");
      buf.append(lockedField);
      buf.append("\">\n");
      if (cols != null) {
        for (Iterator iter = cols.keySet().iterator(); iter.hasNext();) {
          String field = (String) iter.next();
          TableData row = (TableData) cols.get(field);
          buf.append("<col name=\"");
          buf.append(field);
          buf.append("\" isvisible=\"");
          buf.append(row.getField("isvisible"));
          buf.append("\" width=\"");
          buf.append(row.getField("width"));
          buf.append("\" />\n");
        }
      }
      buf.append("</cols>\n");
      buf.append("<sortfields isascend=\"");
      buf.append(isAscend);
      buf.append("\">");
      buf.append(sortedFields);
      buf.append("</sortfields>\n");
      buf.append("<head digest=\"");
      buf.append(headDigest);
      buf.append("\">");
      buf.append(XMLTools.nodeToString(headTable));
      buf.append("</head>\n");
      buf.append("<discard>");
      buf.append(this.disCard);
      buf.append("</discard>");
      buf.append("</root>\n");
      return buf.toString();
    }
  }

  public void setHeadrowheight(int headrowheight) {
    this.headrowheight = headrowheight;
  }

  /**
   * 获取默认的 page field meta;
   */
  private String makeDefPageField(String sField, String sCap, String sAlign) {
    StringBuffer voBuf = new StringBuffer();
    voBuf.append("<field name=\"");
    voBuf.append(sField);
    voBuf.append("\" caption=\"");
    voBuf.append(sCap);
    voBuf.append("\" editboxtype=\"TextBox\" width=\"100\" align=\"");
    voBuf.append(sAlign);
    voBuf
      .append("\" isvisible=\"true\" isallowinput=\"true\" isreadonly=\"false\" ");
    voBuf.append("iszoomimage=\"false\" ispopupimage=\"false\"/>\n");
    return voBuf.toString();
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setRequest(HttpServletRequest request) {
    this.request = request;
  }

  public HttpServletRequest getRequest() {
    if (this.ownerPage != null) {
      return this.ownerPage.getCurrRequest();
    } else {
      return this.request;
    }
  }

  public List getEditBoxes() {
    if (editboxmap == null) {
      return null;
    }
    return new ArrayList(editboxmap.values());
  }

  public Page getPage() {
    return this.ownerPage;
  }

  public String getScriptVarName() {
    return this.getId() + "_GridV";
  }
  
  /**
   * 接口参数处理; leidh; 20050831;
   */
  private void initTagInterfaceFieldProp() {
    HttpServletRequest request = this.getRequest();
    if(request == null){
      return;
    }
    Map voPropMap = (Map) request.getAttribute(ITag.TAG_INTERFACE_FIELD_PROP_MAP);
    if (voPropMap == null)
      return;
    Map voTableMap = (Map) voPropMap.get(this.getTableName());
    if (voTableMap == null)
      return;
    for (Iterator iter = this.fieldslist.iterator(); iter.hasNext();) {
      Node voParamField = (Node) this.paramfieldmap.get(iter.next());
      String vsField = XMLTools.getNodeAttr(voParamField, "name");
      if (voTableMap.containsKey(vsField) == false)
        continue;
      int viProp = Integer.parseInt((String) voTableMap.get(vsField));
      if (viProp == ITag.TAG_INTERFACE_FIELD_READWRITE) {
        XMLTools.setNodeAttr(voParamField, "isreadonly", "false");
        XMLTools.setNodeAttr(voParamField, "isallowinput", "true");
        XMLTools.setNodeAttr(voParamField, "isvisible", "true");
      } else {
        if ((viProp == ITag.TAG_INTERFACE_FIELD_READONLY)) {
          XMLTools.setNodeAttr(voParamField, "isreadonly", "true");
        }
        if ((viProp == ITag.TAG_INTERFACE_FIELD_INVISIBLE)) {
          XMLTools.setNodeAttr(voParamField, "isvisible", "false");
        }
      }
    }
  }
}
