package com.anyi.gp.taglib.components;

import java.io.IOException;
import java.io.Writer;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Node;

import com.anyi.gp.Datum;
import com.anyi.gp.Pub;
import com.anyi.gp.meta.CompoMeta;
import com.anyi.gp.meta.Field;
import com.anyi.gp.meta.MetaManager;
import com.anyi.gp.meta.TableMeta;
import com.anyi.gp.pub.DataTools;
import com.anyi.gp.util.StringTools;
import com.anyi.gp.util.XMLTools;

public class TextBox implements EditBox {

	public static final String OWNER_TYPE_FREE = "free";

	public static final String OWNER_TYPE_GRID = "grid";

	public static final String OWNER_TYPE_BOXSET = "boxset";

	public static final String OWNER_TYPE_SEARCH = "search";

	public static final String DOMID_INPUT = "TextInput";

	public static final String DOMID_FOCUS_BUTTON = "FocusButton";

	public static final int MAX_LEN = 0x7FFFFFFF;

	public static final int MIN_LEN = 0;

	private String id = "";

	private boolean isexact = true;

	private String idsuffix = "_BOX";

	private boolean isfromdb = true;

	private boolean isout = true;

	private String componame = "";

	private boolean isvisible = true;

	private String tablename = "";

	private String fieldname = "";

	private String fieldtype = "TEXT";

	private String groupid = null;

	private int maxlen = MAX_LEN;

	private int minlen = MIN_LEN;

	private boolean isforcereadonly = false;

	private String value = null;

	private String defvalue = null;

	private int tabindex = 0;

	private boolean isallowinput = true;

	private boolean isallownull = false;

	private boolean isreadonly = false;

	private String cssclass = "";

	private String style = "";

	private String oninit = "";

	private boolean isforcedflt = false;

	private String additional = "";

	// 以上进入 taglib;
	// 此Set用以记录哪些属性已经通过jsp标记设置过了，作用是如果这些属性已经通过jsp标记设置过了，
	// 则不再用数据库里的描述属性值覆盖。目前只记录isallownull属性的设置情况。
	private Set tagMarkedAttrs = new HashSet();

	private boolean isSetValueAsDefValue = false;

	private boolean isalone = true;

	private boolean isfreemember = true;

	private String ownertype = OWNER_TYPE_FREE;

	private int inputadjustwidth = 0;

	private String inputtype = "text";

	private String boxtype = "TextBox";

	private Field fieldmeta = null;

	private Node outerField = null;

	private Container container = null;

	private String align = "left";

	private boolean initialized = false;

	public void setContainer(Container container) {
		this.container = container;
	}

	protected Container getContainer() {
		return this.container;
	}

	public void init() {
		if (this.isforcereadonly)
			this.setIsreadonly(true);
		TableMeta tablemeta = null;
		if (this.isIsfromdb()) {
			if (this.tablename != null || this.tablename.length() != 0) {
				tablemeta = MetaManager.getTableMeta(this.tablename);
			} else {
				CompoMeta compoMeta = MetaManager.getCompoMeta(this.componame);
				tablemeta = MetaManager
						.getTableMeta(compoMeta.getMasterTable());
			}
			if (tablemeta == null) {
				throw new RuntimeException("指定表不存在;\ntable name: "
						+ this.tablename);
			}
			this.fieldmeta = tablemeta.getField(this.fieldname);
			if (this.fieldmeta == null) {
				throw new RuntimeException("指定字段不存在;\nfield name: "
						+ this.fieldname);
			}
			initOuterTagInterfaceFieldProp();
		}
		if (this.getId() == null || this.getId().length() == 0) {
			this.setId(this.getTableName() + "_" + this.getFieldName()
					+ this.getIdsuffix());
		}
		initValue();
		this.initialized = true;
	}

	private void initValue() {
		// 判断groupid表示此Box为Search成员,则不能取数据表中的值,而只能是标记中指示的条件值;leidh;20070226;
		if ((!StringTools.isEmptyString(groupid) || this.isSetValueAsDefValue)) {
			if (this.value == null) {
				this.value = this.defvalue;
			}
			return;
		}
		if (this.container.getClass().getName().lastIndexOf("Grid") >= 0){
		  this.value = "";
		  return;
		}
		Datum datum = this.container.getPage().getDatum(tablename);
		if (datum == null)
			return;
		List data = null;
		if (!datum.hasChildDatum(this.tablename)) {
			data = datum.getData();
		} else {
			data = datum.getChildDatum(tablename).getData();
		}
		if (data != null && data.size() > 0) {
			Map row = (Map) data.get(0);
			if (row != null && row.get(fieldname) != null) {
				value = formatDataByType(row.get(fieldname).toString(), this
						.getBoxtype());
			}
		}
	}

	private String formatDataByType(String value, String type) {
		String result = value;
		if (type.equals("DatetimeBox")) {
			int position = result.lastIndexOf('.');
			if (position > 0) {
				result = result.substring(0, position);
			}
		}
		return result;
	}

	/**
	 * 获取 table meata 的外部接口参数; leidh; 20060719;
	 * ITag.TAG_OUTER_INTERFACE_FIELD_MAP： Map: key=tablename; value= attributes
	 * document of fields of table;
	 */
	private void initOuterTagInterfaceFieldProp() {
		if (getCompoName() == null || getCompoName().trim().equals(""))
			return;
		if (getTableName() == null || getTableName().trim().equals(""))
			return;
		outerField = DataTools.getOuterTableField(getTableName(),
				this.container.getPage().getCurrRequest(), getFieldName());
		if (!this.tagMarkedAttrs.contains("isAllowNull")) {
			boolean isAllowNull = this.fieldmeta.isAllowNull();
			if (outerField != null) {
				isAllowNull = Pub.parseBool(XMLTools.getNodeAttr(outerField,
						"isallownull", isAllowNull + ""));
			}
			this.isallownull = isAllowNull;
		}
		if (this.getDefvalue() == null && StringTools.isEmptyString(groupid)) {
			this.defvalue = this.fieldmeta.getDefaultValue();
		}
	}

	public void initByField(Node tagNode) {
		if (tagNode == null) {
			return;
		}
		this.setId(XMLTools.getNodeAttr(tagNode, "id", this.id));
		this.setIdsuffix(XMLTools.getNodeAttr(tagNode, "idsuffix",
				this.idsuffix));
		this.setComponame(XMLTools.getNodeAttr(tagNode, "componame",
				this.componame));
		this.setTablename(XMLTools.getNodeAttr(tagNode, "tablename",
				this.tablename));
		this.setFieldname(XMLTools.getNodeAttr(tagNode, "fieldname",
				this.fieldname));
		this.setFieldtype(XMLTools.getNodeAttr(tagNode, "fieldtype",
				this.fieldtype));
		this.setIsfromdb(Boolean.valueOf(
				XMLTools.getNodeAttr(tagNode, "isfromdb", "" + this.isfromdb))
				.booleanValue());
		this.setMinlen(Integer.parseInt(XMLTools.getNodeAttr(tagNode, "minlen",
				"" + this.minlen)));
		this.setMaxlen(Integer.parseInt(XMLTools.getNodeAttr(tagNode, "maxlen",
				"" + this.maxlen)));
		this.setGroupid(XMLTools.getNodeAttr(tagNode, "groupid", this.groupid));
		this.setTabindex(Integer.parseInt(XMLTools.getNodeAttr(tagNode,
				"tabindex", "" + this.tabindex)));
		this.setIsvisible(Boolean
				.valueOf(
						XMLTools.getNodeAttr(tagNode, "isvisible", ""
								+ this.isvisible)).booleanValue());
		this.setIsallowinput(Boolean.valueOf(
				XMLTools.getNodeAttr(tagNode, "isallowinput", ""
						+ this.isallowinput)).booleanValue());
		this.setIsreadonly(Boolean.valueOf(
				XMLTools.getNodeAttr(tagNode, "isreadonly", ""
						+ this.isreadonly)).booleanValue());
		this.setIsforcereadonly(Boolean.valueOf(
				XMLTools.getNodeAttr(tagNode, "isforcereadonly", ""
						+ this.isforcereadonly)).booleanValue());
		String nodeIsAllowNull = XMLTools.getNodeAttr(tagNode, "isallownull",
				null);
		if (nodeIsAllowNull != null) {
			this
					.setIsallownull(Boolean.valueOf(nodeIsAllowNull)
							.booleanValue());
		}
		this.setIsalone(Boolean.valueOf(
				XMLTools.getNodeAttr(tagNode, "isalone", "" + this.isalone))
				.booleanValue());
		this.setIsfreemember(Boolean.valueOf(
				XMLTools.getNodeAttr(tagNode, "isfreemember", ""
						+ this.isfreemember)).booleanValue());
		this.setValue(XMLTools.getNodeAttr(tagNode, "value", this.value));
		this.setDefvalue(XMLTools.getNodeAttr(tagNode, "defvalue",
				this.defvalue));
		this.setCssclass(XMLTools.getNodeAttr(tagNode, "cssclass",
				this.cssclass));
		this.setStyle(XMLTools.getNodeAttr(tagNode, "style", this.style));
		this.setOninit(XMLTools.getNodeAttr(tagNode, "oninit", this.oninit));
		this.setIsforcedflt(Boolean.valueOf(
				XMLTools.getNodeAttr(tagNode, "isforcedflt", "" + isforcedflt))
				.booleanValue());
		this
				.setOwnertype(XMLTools.getNodeAttr(tagNode, "ownertype",
						ownertype));
	}

	public void writeHTML(Writer out) throws IOException {
		if (!this.initialized) {
			init();
		}
		
		out.write(this.makeOuterPanel_1());
		if (this.getBoxtype().equalsIgnoreCase("ImageBox")) {
			out.write(this.makeImageTD());
		}
		out.write(this.makeInputTD());
		out.write(this.makeOtherTD());
		if (hasDefaultValue()) {
			out.write(this.makeDefaultValueTD());
		}
		out.write(this.makeOuterPanel_2());
	}

	protected String makeOtherTD() {
		return "";
	}

	protected String makeImageTD() {
		return "";
	}

	protected String makeInputBox() {
		StringBuffer voBuf = new StringBuffer();
		voBuf.append("<input type='");
		voBuf.append(this.getInputtype());
		voBuf.append("' ");
		voBuf.append("id='");
		voBuf.append(DOMID_INPUT);
		voBuf.append("' ");
		voBuf.append("maxlength='");
		voBuf.append(this.getMaxlen());
		voBuf.append("' ");
		voBuf.append("class='clsTextboxInput' ");
		voBuf.append("align='" + this.align + "' ");
		voBuf.append("tabindex='" + this.getTabindex() + "' ");
		if (this.isIsreadonly() || !this.isIsallowinput()) {
			voBuf.append("readonly ");
			voBuf.append("style='color:black' ");
		}
		if (value == null) {
			voBuf.append(" value='' ");
		} else {
			voBuf.append(" value='" + value + "' ");
		}
		voBuf.append(">\n");
		return voBuf.toString();
	}

	protected String makeFocusBtn() {
		StringBuffer voBuf = new StringBuffer();
		voBuf.append("<input type='button' ");
		voBuf.append("id='" + DOMID_FOCUS_BUTTON + "' ");
		voBuf.append("class='clsTextboxFocusBtn'>\n");
		return voBuf.toString();
	}

	private String makeInputTD() {
		StringBuffer voBuf = new StringBuffer();
		voBuf.append("<td width=\"100%\" id=\"inputTD\" valign=\"bottom\">");
		voBuf.append(makeInputBox());
		voBuf.append("</td>");
		voBuf.append("<td width=\"0\">");
		voBuf.append(makeFocusBtn());
		voBuf.append("</td>");
		return voBuf.toString();
	}

	private String makeOuterPanel_1() {
		StringBuffer voBuf = new StringBuffer();
		voBuf.append("<table  id='" + this.getId()
				+ "'  cellpadding=0 cellspacing=0 ");
		voBuf.append(this.makeAttr());
		voBuf.append("class='clsTextboxOuterpanel' ");
		if (!this.isIsvisible()) {
			voBuf.append("style='display:none' ");
		}
		voBuf.append("hidefocus='true'>\n");
		voBuf.append("<tr valign=\"bottom\">");
		return voBuf.toString();
	}

	private String makeOuterPanel_2() {
		return "</tr></table>\n";
	}

	protected String makeAttr() {
		StringBuffer voBuf = new StringBuffer();
		voBuf.append("componame='" + this.componame + "' ");
		voBuf.append("tablename='" + this.tablename + "' ");
		voBuf.append("fieldname='" + this.fieldname + "' ");
		voBuf.append("fieldtype='" + this.fieldtype + "' ");
		voBuf.append("maxlen=" + this.maxlen + " ");
		voBuf.append("minlen=" + this.minlen + " ");
		voBuf.append("groupid='" + this.groupid + "' ");
		voBuf.append("tabIndex=" + this.tabindex + " ");
		voBuf.append("isallowinput='" + this.isallowinput + "' ");
		voBuf.append("isreadonly='" + this.isreadonly + "' ");
		voBuf.append("isforcereadonly='" + this.isforcereadonly + "' ");
		voBuf.append("isallownull='" + this.isallownull + "' ");
		voBuf.append("inputadjustwidth=" + this.inputadjustwidth + " ");
		voBuf.append("isfromdb='" + this.isfromdb + "' ");
		voBuf.append("isfreemember='" + this.isfreemember + "' ");
		voBuf.append("isforcedflt='" + isforcedflt + "' ");
		voBuf.append("ownertype='" + ownertype + "' ");
		voBuf.append("havedefault='" + hasDefaultValue() + "' ");
		return voBuf.toString();
	}

	private boolean hasDefaultValue() {
		return this.defvalue != null;
	}

	public void setValueAsDefValue() {
		this.isSetValueAsDefValue = true;
	}

	private String makeDefaultValueTD() {
		StringBuffer voBuf = new StringBuffer();
		voBuf.append("<td id='BoxDefaultValueSpan' style='display:none;'>");
		if (this.hasDefaultValue()) {
			voBuf.append(StringTools.replaceAll(this.defvalue, "<", "&lt;"));
		}
		voBuf.append("</td>\n");
		return voBuf.toString();
	}

	public void writeInitScript(Writer out) throws IOException {
		if (!this.initialized) {
			init();
		}
		out.write("var ");
		out.write(getScriptVarName());
		out.write(" = new " + this.boxtype + "('");
		out.write(this.getId());
		out.write("');\n");
		out.write(getScriptVarName());
		out.write(".make();\n");
		out.write(getScriptVarName());
		out.write(".init();\n");
		if (this.isIsalone()) {
			out.write("PageX.regCtrlObj(\"");
			out.write(this.getId() + "\", ");
			out.write(getScriptVarName());
			out.write(");\n");
		}
	}

	public String getTableName() {
		return tablename;
	}

	public void setTablename(String tablename) {
		this.tablename = tablename;
	}

	public String getFieldName() {
		return fieldname;
	}

	public void setFieldname(String fieldname) {
		this.fieldname = fieldname;
	}

	protected int getMaxlen() {
		return maxlen;
	}

	public void setMaxlen(int maxlen) {
		this.maxlen = maxlen;
	}

	public void setMinlen(int minlen) {
		this.minlen = minlen;
	}

	public String getGroupId() {
		return groupid;
	}

	public void setGroupid(String groupid) {
		this.groupid = groupid;
	}

	public int getTabindex() {
		return tabindex;
	}

	public void setTabindex(int tabindex) {
		this.tabindex = tabindex;
	}

	protected boolean isIsallowinput() {
		return isallowinput;
	}

	public void setIsallowinput(boolean isallowinput) {
		this.isallowinput = isallowinput;
	}

	protected boolean isIsreadonly() {
		return isreadonly;
	}

	public void setIsreadonly(boolean isreadonly) {
		this.isreadonly = isreadonly;
	}

	public void setCssclass(String cssclass) {
		this.cssclass = cssclass;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	protected String getValue() {
		if (value != null) {
			value = value.replaceAll("\\r\\n", "\\\\r\\\\n");
		}
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setOninit(String oninit) {
		this.oninit = oninit;
	}

	public String getCompoName() {
		return componame;
	}

	public void setComponame(String componame) {
		this.componame = componame;
		if (componame == null || componame.equals(""))
			return;
		CompoMeta compoMeta = MetaManager.getCompoMeta(componame);
		if (compoMeta != null) {
			this.tablename = compoMeta.getMasterTable();
		}
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

	public boolean isIsalone() {
		return isalone;
	}

	public void setIsalone(boolean isalone) {
		this.isalone = isalone;
	}

	protected boolean isIsfromdb() {
		if (this.tablename == null || this.tablename.length() == 0)
			return false;
		if (this.fieldname == null || this.fieldname.length() == 0)
			return false;
		return isfromdb;
	}

	public void setIsfromdb(boolean isfromdb) {
		this.isfromdb = isfromdb;
	}

	public void setFieldtype(String fieldtype) {
		this.fieldtype = fieldtype;
	}

	public void setIsallownull(boolean isallownull) {
		this.tagMarkedAttrs.add("isAllowNull");
		this.isallownull = isallownull;
	}

	public void setIsexact(boolean isexact) {
		this.isexact = isexact;
	}

	public String getDefvalue() {
		return defvalue;
	}

	public void setDefvalue(String defvalue) {
		this.defvalue = defvalue;
	}

	public void setIsforcereadonly(boolean isforcereadonly) {
		this.isforcereadonly = isforcereadonly;
	}

	public void setIsfreemember(boolean isfreemember) {
		this.isfreemember = isfreemember;
	}

	public void setIsforcedflt(boolean isforcedflt) {
		this.isforcedflt = isforcedflt;
	}

	public void setOwnertype(String ownertype) {
		this.ownertype = ownertype;
	}

	public void setAdditional(String additional) {
		this.additional = additional;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBoxtype() {
		return boxtype;
	}

	public void setBoxtype(String boxtype) {
		this.boxtype = boxtype;
	}

	public String getInputtype() {
		return inputtype;
	}

	public void setInputtype(String inputtype) {
		this.inputtype = inputtype;
	}

	public void setIsout(boolean isout) {
		this.isout = isout;
	}

	protected Field getFieldmeta() {
		return fieldmeta;
	}

	protected Node getOuterField() {
		return outerField;
	}

	protected String getCssclass() {
		return cssclass;
	}

	protected String getStyle() {
		return style;
	}

	protected boolean isIsallownull() {
		return isallownull;
	}

	protected void setAlign(String align) {
		this.align = align;
	}

	public String getScriptVarName() {
		return this.id + "_BoxV";
	}
}
