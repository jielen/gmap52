package com.anyi.gp.taglib.components;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.anyi.gp.Pub;
import com.anyi.gp.TableData;
import com.anyi.gp.meta.Field;
import com.anyi.gp.meta.MetaManager;
import com.anyi.gp.meta.TableMeta;
import com.anyi.gp.meta.TableMetaBuilder;
import com.anyi.gp.pub.DataTools;
import com.anyi.gp.taglib.ITag;
import com.anyi.gp.util.StringTools;
import com.anyi.gp.util.XMLTools;

public class BoxSet implements Container, HtmlComponent {

  private static final Logger logger = Logger.getLogger(BoxSet.class);

  private static final String RELA_OBJ_TYPE_NONE = "none";

  private static final String RELA_OBJ_TYPE_GRID = "grid";

  private static final int CAPTION_WIDTH = 60;

  private String idsuffix = "_BOXSET";

  private String relaobjid = null;

  private String fields = "";

  private int rows = -1;

  private int cols = 3;

  private String captionwidths = ""; // 以逗号(,)分隔;

  private String cssclass = "";

  private String style = "";

  private String relaobjtype = RELA_OBJ_TYPE_NONE;

  private String componame = "";

  private String tablename = "";

  private boolean isfromdb = true;

  private boolean isvisible = true;

  private boolean iswritable = true;

  private boolean isreadonly = false;

  private boolean isautoappear = true;

  private boolean issynchfieldvisible = true;

  private boolean isallowinit = true; //用于多个boxset联合;leidh;20061206;

  private int tabindex = 0;

  private String oninit = "";

  private String id = null;

  // 以上为标记属性;

  private List capwidthlist = null;

  private List fieldlist = null;

  private Grid relaobj = null;

  private Map editboxmap = null;
  
  private List removedEditBox = new ArrayList();

  private Map paramfieldmap = null;

  private Map tablefieldmap = null;

  private Map rowcolfieldmap = null;

  private List invisibleboxlist = null;

  private TableMeta tablemeta = null;

  private int maxRows = 0;

  private Page ownerPage = null;

  private String bodyText = "";
  
  private boolean isSetDefaultValue = false;
  
  private boolean initialized = false;

  public BoxSet() {

  }

  public void setOwnerPage(Page ownerPage) {
    this.ownerPage = ownerPage;
  }

  public void writeHTML(Writer out) throws IOException {
    if (!this.initialized){
      init();
    }
    out.write(makeOuterPanel_1());
    if (isExistFields()) {
      makeHTML(out);
    }
    out.write(makeOuterPanel_2());
  }

  private void validate() {
    if (relaobjid != null && !relaobjid.trim().equals("")) {
      relaobj = this.ownerPage.getGridById(relaobjid);
      if (relaobj == null) {
        throw new RuntimeException("id为" + this.getId()
          + "的<applus:boxset>的关联对象(id=\"" + relaobjid + "\")不存在.");
      }
    }
  }

  private boolean isRelaBoxset() {
    return relaobj != null;
  }

  private void initBoxsetAttrFromRelaObj() {
    componame = relaobj.getComponameforboxset();
    tablename = relaobj.getTablenameforboxset();
    isfromdb = relaobj.isIsfromdbforboxset();
  }

  protected void init() {
    validate();
    initId();
    if (isRelaBoxset()) {
      initBoxsetAttrFromRelaObj();
    }
    initParamfieldmap();
    initFieldlist();
    this.initTagInterfaceFieldProp();
    editboxmap = EditBoxFactory.batchMakeEditBoxByFields(fieldlist, paramfieldmap,
      componame, tablename, TextBox.OWNER_TYPE_BOXSET, isfromdb, iswritable,
      "_BoxSet", "", "clsListPageEditBoxWithPercent", this);
    // 计算字段的行列值移到单独函数中处理; chupp; 20060825
    this.calculateFieldRowCol();
    if (rows < 0)
      rows = maxRows + 1;
    if (rows < 0) {
      rows = paramfieldmap.size() / cols;
      if (paramfieldmap.size() % cols > 0)
        rows++;
    }
    capwidthlist = StringTools.split(captionwidths, ",");
    for (int i = 0; i < cols; i++) {
      if (capwidthlist.size() <= i) {
        capwidthlist.add(String.valueOf(CAPTION_WIDTH));
        continue;
      }
      String vsWidth = (String) capwidthlist.get(i);
      if (vsWidth == null || vsWidth.equals("")) {
        capwidthlist.set(i, String.valueOf(CAPTION_WIDTH));
      }
    }
    initTableMeta();
    this.initialized = true;
  }

  private void initParamfieldmap() {
    if (isRelaBoxset()) {
      this.paramfieldmap = relaobj.getParamFields();
    } else if (bodyText != null && !bodyText.equals("")) {
      Document paramDoc = XMLTools.stringToDocument(this.bodyText);
      if (paramDoc == null) {
        throw new RuntimeException(this.getClass().getName()
          + ".init():\n解析子标记出错: <applus:meta>;\n");
      }
      Node voFields;
      try {
        voFields = XPathAPI.selectSingleNode(paramDoc, "//fields");
      } catch (TransformerException e) {
        logger.error(e);
        throw new RuntimeException(e);
      }
      if (voFields == null) {
        throw new RuntimeException("在<applus:boxset>中的<applus:meta>中没有发现<fields>;");
      }
      this.paramfieldmap = DataTools.makeFieldMap(voFields, true);
    } else {
      throw new RuntimeException("boxset标记既没有关联表格，有没有描述字段");
    }
  }

  private void initFieldlist() {
    if (!fields.trim().equals("")) {
      fieldlist = StringTools.split(fields, ",");
    }
    if ((fieldlist == null || fieldlist.size() <= 0) && relaobj != null) {
      fieldlist = relaobj.getFieldNames();
      return;
    }
    List temp = (List) this.paramfieldmap.get(DataTools.FIELD_LIST_KEY);
    if (fieldlist == null) {
      fieldlist = new ArrayList(temp);
    } else {
      fieldlist.retainAll(temp);
    }
    
    //根据grid的属性设置过来fieldlist    
    if(!fieldlist.isEmpty() && relaobj != null){
      Map gridPropCols = relaobj.getGridPropCols();
      if(gridPropCols != null){
        List visibleList = new ArrayList();
        Iterator iterator = gridPropCols.entrySet().iterator();
        while(iterator.hasNext()){
          Map.Entry entry = (Map.Entry)iterator.next();
          TableData col = (TableData)entry.getValue();
          if("true".equalsIgnoreCase(col.getFieldValue("isvisible"))){
            visibleList.add(col.getField("name"));
          }
        }
        if(!visibleList.isEmpty()){
          fieldlist.retainAll(visibleList);
        }
      }
    }
  }

  private void initId() {
    if (this.getId() == null || this.getId().length() == 0) {
      this.setId("BoxSet_" + Pub.getUID() + this.getIdsuffix());
    }
  }

  /**
   * 计算字段的行与列; chupp; 20060825
   */
  private void calculateFieldRowCol() {
    rowcolfieldmap = new HashMap();
    invisibleboxlist = new ArrayList();
    maxRows = 0;
    List copyFieldList = new ArrayList(fieldlist);
    // 先计算有明确行列值的字段
    for (Iterator iter = fieldlist.iterator(); iter.hasNext();) {
      String vsField = (String) iter.next();
      Object voObj = paramfieldmap.get(vsField);
      if (voObj == null)
        continue;
      Node voField = (Node) voObj;
      String vsRow = XMLTools.getNodeAttr(voField, "boxsetrow", null);
      String vsCol = XMLTools.getNodeAttr(voField, "boxsetcol", null);
      if (vsRow != null && vsCol != null && Integer.parseInt(vsRow) >= 0
        && Integer.parseInt(vsCol) >= 0) {
        rowcolfieldmap.put(getRCFieldKey(vsRow, vsCol), voField);
        if (maxRows < Integer.parseInt(vsRow))
          maxRows = Integer.parseInt(vsRow);
        copyFieldList.remove(vsField);
      }
    }
    // 没有明确行列值，需要计算行列值的字段
    int viCursorRow = 0;
    int viCursorCol = 0;
    for (Iterator iter = copyFieldList.iterator(); iter.hasNext();) {
      String vsField = (String) iter.next();
      Object voObj = paramfieldmap.get(vsField);
      if (voObj == null) {
        continue;
      }
      Node voField = (Node) voObj;
      String vsRow = XMLTools.getNodeAttr(voField, "boxsetrow", null);
      String vsCol = XMLTools.getNodeAttr(voField, "boxsetcol", null);
      String vsColSpan = XMLTools.getNodeAttr(voField, "boxsetcolspan", null);
      if (RELA_OBJ_TYPE_GRID.equals(relaobjtype)) {
        boolean fieldVisible = Boolean.valueOf(
          XMLTools.getNodeAttr(voField, "isvisible", "true")).booleanValue();
        vsRow = (vsRow == null && !fieldVisible) ? "-1" : vsRow;
      }
      if (vsRow == null || vsRow.trim().equals("") || vsRow.trim().equals("-99")) {
        vsRow = String.valueOf(viCursorRow);
      }
      if (vsCol == null || vsCol.trim().equals("") || vsCol.trim().equals("-99")) {
        vsCol = String.valueOf(viCursorCol);
      }
      if (vsColSpan == null || vsColSpan.trim().equals("")
        || Integer.parseInt(vsColSpan) <= 0) {
        vsColSpan = "1";
      }
      if ((Integer.parseInt(vsRow) < 0 && Integer.parseInt(vsRow) != -99)
        || (Integer.parseInt(vsCol) < 0 && Integer.parseInt(vsCol) != -99)) {
        invisibleboxlist.add(editboxmap.get(vsField));
        continue;
      }
      rowcolfieldmap.put(getRCFieldKey(vsRow, vsCol), voField);
      if (maxRows < Integer.parseInt(vsRow))
        maxRows = Integer.parseInt(vsRow);
      // 计算得到一个没被使用的行列值
      do {
        viCursorCol++;
        if (viCursorCol >= cols) {
          viCursorRow++;
          viCursorCol = 0;
        }
      } while (rowcolfieldmap.containsKey(getRCFieldKey(String.valueOf(viCursorRow),
        String.valueOf(viCursorCol))));
    }
  }

  private void initTableMeta() {
    if (this.isIsfromdb()) {
      this.tablemeta = MetaManager.getTableMeta(tablename);
    } else {
      this.tablemeta = TableMetaBuilder.getDefaultTableMeta(tablename, fieldlist);
    }
    this.tablefieldmap = tablemeta.getFields();
  }

  private String getRCFieldKey(String row, String col) {
    return row + "_" + col;
  }

  private boolean isExistFields() {
    return (fieldlist != null) && (!fieldlist.isEmpty());
  }

  private void makeHTML(Writer out) throws IOException {
    if (!isExistFields())
      return;
    makeInvisibleBox(out);
    makeVisibleBox(out);
    out.write(makeBoxIds());
  }

  private void makeInvisibleBox(Writer out) throws IOException {
    out.write("<div id=\"");
    out.write("InvisibleEditBoxOfBoxSet_Table_" + getId());
    out.write("\" ");
    out.write("style=\"");
    out.write("display:none;");
    out.write("\">\n");
    for (Iterator iter = invisibleboxlist.iterator(); iter.hasNext();) {
      EditBox voBox = (EditBox) iter.next();
      if (voBox == null) {
        continue;
      }
      if (this.isSetDefaultValue){
        voBox.setValueAsDefValue();
      }
      voBox.init();
      voBox.writeHTML(out);
    }
    out.write("</div>\n");
  }

  private void makeVisibleBox(Writer out) throws IOException {
    if (!isExistFields())
      return;
    out.write("<table id=\"");
    out.write("VisibleEditBoxOfBoxSet_Table_" + getId());
    out.write("\" border=\"0\" cellpadding=0 cellspacing=0 ");
    out.write("class='clsFreeTable ");
    out.write(this.getCssclass());
    out.write("' ");
    out.write("style='");
    out.write(this.getStyle());
    out.write(this.getAdjustLayoutTableStyle());
    out.write(";' ");
    out.write(">\n");
    out.write("<colgroup>\n");
    int viEditWidth = 100 / cols;
    for (int i = 0; i < cols; i++) {
      out.write("<col style=\"width:" + capwidthlist.get(i) + "px;\" />\n");
      out.write("<col style=\"width:" + viEditWidth + "%;\" />\n");
    }
    out.write("</colgroup>\n");
    for (int i = 0; i < rows; i++) {
      out.write("<tr class=\"clsFreeRow\">\n");
      int viColSpan = 1;
      for (int j = 0; j < cols; j += viColSpan) {
        String vsKey = getRCFieldKey(String.valueOf(i), String.valueOf(j));
        if (!rowcolfieldmap.containsKey(vsKey)) {
          out.write("<td></td><td></td>\n");
          continue;
        }
        Node voField = (Node) rowcolfieldmap.get(vsKey);
        String vsField = XMLTools.getNodeAttr(voField, "name");
        String vsColSpan = XMLTools.getNodeAttr(voField, "boxsetcolspan", "1");
        if (vsColSpan == null || vsColSpan.trim().equals(""))
          vsColSpan = "1";
        viColSpan = Integer.parseInt(vsColSpan);
        if (j * 2 + viColSpan > cols * 2)
          viColSpan = cols * 2 - j;
        EditBox voBox = (EditBox) editboxmap.get(vsField);
        if (voBox == null){
          continue;
        }
        if (this.isSetDefaultValue){
          voBox.setValueAsDefValue();
        }
        voBox.init();
        out.write(makeCaptionTD(voBox, voField));
        out.write("<td id=\"");
        out.write(makeBoxTDId(voBox));
        out.write("\" colspan=\"");
        out.write(viColSpan);
        out.write("\" style=\"");
        out.write("display:");
        out.write(voBox.isIsvisible() ? "block;" : "none;");
        out.write("\" ");
        out.write(">\n");
        voBox.writeHTML(out);
        out.write("</td>\n");
      }
      out.write("</tr>\n");
    }
    out.write("</table>\n");
  }

  private String makeCaptionTD(EditBox box, Node fieldNode) {
    String vsCaption = XMLTools
      .getNodeAttr(fieldNode, "caption", box.getFieldName());
    Field voTableField = (Field) this.tablefieldmap.get(box.getFieldName());
    String vsCssClass = "clsNormCaption";
    String vsAsterisk = "";
    if (voTableField.isPk()) {
      vsCssClass = "clsKeyCaption";
      vsAsterisk = "<span class='asterisk'>*</span>";
    } else {
      String pageFieldIsNull = XMLTools.getNodeAttr(fieldNode, "isallownull");
      if (StringTools.isEmptyString(pageFieldIsNull)) {
        if (!voTableField.isAllowNull()) {
          vsAsterisk = "<span class='asterisk'>*</span>";
        }
      } else {
        if (!Pub.parseBool(pageFieldIsNull)) {
          vsAsterisk = "<span class='asterisk'>*</span>";
        }
      }
    }
    StringBuffer buf = new StringBuffer();
    buf.append("<td id=\"");
    buf.append(makeCapTDId(box));
    buf.append("\" class=\"");
    buf.append(vsCssClass);
    buf.append("\" style=\"");
    buf.append(XMLTools.getNodeAttr(fieldNode, "style", ""));
    buf.append(";display:");
    buf.append(box.isIsvisible() ? "block;" : "none;");
    buf.append("\" ");
    buf.append(" nowrap ");
    buf.append(">");
    buf.append(vsCaption);
    buf.append(vsAsterisk);
    buf.append("</td>\n");
    return buf.toString();
  }

  private String makeCapTDId(EditBox box) {
    StringBuffer buf = new StringBuffer();
    buf.append("label_");
    buf.append(box.getId());
    return buf.toString();
  }

  private String makeBoxTDId(EditBox box) {
    StringBuffer buf = new StringBuffer();
    buf.append("editbox_");
    buf.append(box.getId());
    return buf.toString();
  }

  private String makeOuterPanel_1() {
    StringBuffer buf = new StringBuffer();
    buf.append("<div id='");
    buf.append(this.getId());
    buf.append("' ");
    buf.append(this.makeAttr());
    buf.append(" class='");
    buf.append(this.getCssclass());
    buf.append("' ");
    buf.append("style='");
    buf.append(this.getStyle());
    buf.append(this.getAdjustOuterPanelStyle());
    buf.append(";' ");
    buf.append("hidefocus='true' ");
    buf.append(">\n");
    return buf.toString();
  }

  private String makeOuterPanel_2() {
    return "</div>\n";
  }

  private String getAdjustOuterPanelStyle() {
    StringBuffer buf = new StringBuffer();
    buf.append(";");
    buf.append("overflow:visible;");
    buf.append("border-width:0px;");
    buf.append("width:;");
    buf.append("height:;");
    return buf.toString();
  }

  private String getAdjustLayoutTableStyle() {
    StringBuffer buf = new StringBuffer();
    buf.append(";");
    buf.append("position:relative;");
    buf.append("text-align:left;");
    buf.append("vertical-align:top;");
    return buf.toString();
  }

  private String makeBoxIds() {
    if (!isExistFields())
      return "";
    StringBuffer buf = new StringBuffer();
    buf.append("<span id=\"");
    buf.append("EditBoxIdSpan_" + getId());
    buf.append("\">\n");
    for (Iterator iter = fieldlist.iterator(); iter.hasNext();) {
      EditBox voBox = (EditBox) editboxmap.get(iter.next());
      buf.append("<span editboxid=\"");
      buf.append(voBox.getId());
      buf.append("\" ></span>\n");
    }
    buf.append("</span>\n");
    return buf.toString();
  }

  private String makeAttr() {
    StringBuffer buf = new StringBuffer();
    buf.append("relaobjid='" + this.relaobjid + "' ");
    buf.append("relaobjtype='" + relaobjtype + "' ");
    buf.append("componame='" + this.componame + "' ");
    buf.append("tablename='" + this.tablename + "' ");
    buf.append("tabindex=" + this.tabindex + " ");
    buf.append("iswritable='" + this.iswritable + "' ");
    buf.append("isreadonly='" + this.isreadonly + "' ");
    buf.append("isautoappear='" + this.isIsautoappear() + "' ");
    buf.append("isfromdb='" + this.isfromdb + "' ");
    return buf.toString();
  }

  private String makeJSAttr() {
    StringBuffer buf = new StringBuffer();
    buf.append(getId() + ".componame='" + this.componame + "';\n");
    buf.append(getId() + ".tablename='" + this.tablename + "';\n");
    buf.append(getId() + ".tabindex=" + this.tabindex + ";\n");
    buf.append(getId() + ".iswritable=" + this.iswritable + ";\n");
    buf.append(getId() + ".isreadonly=" + this.isreadonly + ";\n");
    buf.append(getId() + ".isautoappear=" + this.isIsautoappear() + ";\n");
    buf.append(getId() + ".isfromdb=" + this.isfromdb + ";\n");
    return buf.toString();
  }

  /**
   * 生成 JS;
   *
   * @return
   */
  private void makeJS(Writer out) throws IOException{
    if (!isExistFields())
      return ;
    out.write(makeJSAttr());
    out.write("var ");
    out.write(getScriptVarName());
    out.write(" = PageX.getBoxSet(\"" + tablename + "\", \"");
    out.write(relaobjtype + "\");\n");
    out.write("if (");
    out.write(getScriptVarName());
    out.write(" != null){\n");
    out.write(getScriptVarName());
    out.write(".relaBoxSet(\"");
    out.write(getId());
    out.write("\");\n");
    if (isallowinit) {
      out.write(getScriptVarName());
      out.write(".init();\n");
    }
    out.write("}else{\n");
    out.write("var ");
    out.write(getScriptVarName());
    out.write(" = new BoxSet();\n");
    out.write(getScriptVarName());
    out.write(".make('" + this.getId() + "');\n");
    loopWriteBoxInitScript(new BoxScriptHandler() {
      public void handle(EditBox box, Writer out) throws IOException {
        out.write(getScriptVarName());
        out.write(".addEditBox(");
        out.write(box.getScriptVarName());
        out.write(");\n");
      }
    }, out);
    if (isallowinit) {
      out.write(getScriptVarName());
      out.write(".init();\n");
    }
    // 向 PageX 中注册 Grid 对象;
    out.write("PageX.regCtrlObj(\"");
    out.write(this.getId());
    out.write("\", ");
    out.write(getScriptVarName());
    out.write(");\n");
    out.write("PageX.regBoxSet(\"");
    out.write(tablename);
    out.write("\", \"");
    out.write(relaobjtype);
    out.write("\", ");
    out.write(getScriptVarName());
    out.write(");\n");
    out.write("}\n");
  }

  public String getIdsuffix() {
    return idsuffix;
  }

  public void setIdsuffix(String idsuffix) {
    this.idsuffix = idsuffix;
  }

  public String getRelaobjid() {
    return relaobjid;
  }

  public void setRelaobjid(String relaobjid) {
    this.relaobjid = relaobjid;
  }

  public String getFields() {
    return fields;
  }

  public void setFields(String fields) {
    this.fields = fields;
  }

  public int getRows() {
    return rows;
  }

  public void setRows(int rows) {
    this.rows = rows;
  }

  public int getCols() {
    return cols;
  }

  public void setCols(int cols) {
    this.cols = cols;
  }

  public String getCaptionwidths() {
    return captionwidths;
  }

  public void setCaptionwidths(String captionwidths) {
    this.captionwidths = captionwidths;
  }

  public String getCssclass() {
    return cssclass;
  }

  public void setCssclass(String cssclass) {
    this.cssclass = cssclass;
  }

  public String getStyle() {
    return style;
  }

  public void setStyle(String style) {
    this.style = style;
  }

  public String getRelaobjtype() {
    return relaobjtype;
  }

  public void setRelaobjtype(String relaobjtype) {
    this.relaobjtype = relaobjtype;
  }

  public String getCompoName() {
    return componame;
  }

  public void setCompoName(String componame) {
    this.componame = componame;
  }

  public String getTableName() {
    return tablename;
  }

  public void setTableName(String tablename) {
    this.tablename = tablename;
  }

  public boolean isIsfromdb() {
    return isfromdb;
  }

  public void setIsfromdb(boolean isfromdb) {
    this.isfromdb = isfromdb;
  }

  public boolean isIsvisible() {
    return isvisible;
  }

  public void setIsvisible(boolean isvisible) {
    this.isvisible = isvisible;
  }

  public boolean isIswritable() {
    return iswritable;
  }

  public void setIswritable(boolean iswritable) {
    this.iswritable = iswritable;
  }

  public boolean isIsreadonly() {
    return isreadonly;
  }

  public void setIsreadonly(boolean isreadonly) {
    this.isreadonly = isreadonly;
  }

  public boolean isIsautoappear() {
    return isautoappear;
  }

  public void setIsautoappear(boolean isautoappear) {
    this.isautoappear = isautoappear;
  }

  public boolean isIssynchfieldvisible() {
    return issynchfieldvisible;
  }

  public void setIssynchfieldvisible(boolean issynchfieldvisible) {
    this.issynchfieldvisible = issynchfieldvisible;
  }

  public boolean isIsallowinit() {
    return isallowinit;
  }

  public void setIsallowinit(boolean isallowinit) {
    this.isallowinit = isallowinit;
  }

  public int getTabindex() {
    return tabindex;
  }

  public void setTabindex(int tabindex) {
    this.tabindex = tabindex;
  }

  public String getOninit() {
    return oninit;
  }

  public void setOninit(String oninit) {
    this.oninit = oninit;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getBodyText() {
    return bodyText;
  }

  public void setBodyText(String bodyText) {
    this.bodyText = bodyText;
  }

  public void writeInitScript(Writer out) throws IOException {
    if (!this.initialized){
      init();
    }
    if (this.fieldlist.isEmpty()){
      return;
    }
    loopWriteBoxInitScript(new BoxScriptHandler() {
      public void handle(EditBox box, Writer out) throws IOException {
        box.writeInitScript(out);
      }
    }, out);
    this.makeJS(out);
  }

  private void loopWriteBoxInitScript(BoxScriptHandler handler, Writer out)
    throws IOException {
    for (Iterator iter = invisibleboxlist.iterator(); iter.hasNext();) {
      EditBox voBox = (EditBox) iter.next();
      handler.handle(voBox, out);
    }
    for (int i = 0; i < rows; i++) {
      int viColSpan = 1;
      for (int j = 0; j < cols; j += viColSpan) {
        String vsKey = getRCFieldKey(String.valueOf(i), String.valueOf(j));
        if (!rowcolfieldmap.containsKey(vsKey)) {
          continue;
        }
        Node voField = (Node) rowcolfieldmap.get(vsKey);
        String vsField = XMLTools.getNodeAttr(voField, "name");
        String vsColSpan = XMLTools.getNodeAttr(voField, "boxsetcolspan", "1");
        if (vsColSpan == null || vsColSpan.trim().equals(""))
          vsColSpan = "1";
        viColSpan = Integer.parseInt(vsColSpan);
        if (j * 2 + viColSpan > cols * 2)
          viColSpan = cols * 2 - j;
        EditBox voBox = (EditBox) editboxmap.get(vsField);
        handler.handle(voBox, out);
      }
    }
  }

  public void removeEditBox(EditBox editBox){
    this.removedEditBox.add(editBox);
    this.invisibleboxlist.remove(editBox);
    this.editboxmap.remove(editBox.getFieldName());
    this.fieldlist.remove(editBox.getFieldName());
  }
  
  public List getEditBoxes() {
    List result = new ArrayList();
    for (Iterator iter = this.fieldlist.iterator(); iter.hasNext(); ){
      result.add(editboxmap.get(iter.next()));
    }
    result.removeAll(this.removedEditBox);
    return result;
  }

  public Page getPage() {
    return this.ownerPage;
  }

  public String getScriptVarName() {
    return this.id + "_BoxSetV";
  }

  public void setIsSetDefaultValue(){
    this.isSetDefaultValue = true;
  }
  
  private void initTagInterfaceFieldProp() {
      if(this.getPage() == null || this.getPage().getCurrRequest() == null){
        return;
      }
      
	    HttpServletRequest request = this.getPage().getCurrRequest();
	    Map voPropMap = (Map) request
	        .getAttribute(ITag.TAG_INTERFACE_FIELD_PROP_MAP);
	    if (voPropMap == null)
	      return;
	    Map voTableMap = (Map) voPropMap.get(this.getTableName());
	    if (voTableMap == null)
	      return;
	    for (Iterator iter = this.fieldlist.iterator(); iter.hasNext();) {
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
