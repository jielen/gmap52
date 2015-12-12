package com.anyi.gp.core.bean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;

import com.anyi.gp.meta.CompoMeta;
import com.anyi.gp.meta.Field;
import com.anyi.gp.meta.Foreign;
import com.anyi.gp.meta.MetaManager;
import com.anyi.gp.meta.TableMeta;
import com.anyi.gp.pub.DAOFactory;
import com.anyi.gp.pub.DBHelper;
import com.anyi.gp.pub.FieldToHTML;
import com.anyi.gp.pub.GeneralFunc;
import com.anyi.gp.pub.SessionUtils;
import com.anyi.gp.taglib.components.ForeignBox;
import com.anyi.gp.util.StringTools;

/**
 * 
 * 高级搜索pagebean
 * 
 * @author liuxiaoyong
 * 
 */
public class SearchPageBean extends PageRequestInfo {

	String firstField;

	String fieldOptions; // 得到当前HTML 页面的 select 查询字段选项

	int langIndex = 0; // 语言种类序号，0：中文，1：英文，2：其它

	/** 页面标题 */
	private String pageTitle = null;

	/** 页面数据 */
	private String pageData = null;

	/** OK按钮的名称 */
	private String okButtonName = null;

	/** Cancel按钮的名称 */
	private String cancelButtonName = null;

	/** 表格标题——字段名称 */
	private String tableTitleItemName = null;

	/** 表格标题——关系 */
	private String tableTitleRelation = null;

	/** 表格标题——条件 */
	private String tableTitleCondition = null;

	/** 字段列表 */
	private List fieldsList = null;

	/** 部件元数据 */
	protected TableMeta entityMeta = null;

	protected CompoMeta compoMeta = null;
	
	String[][] numCondi = { { "=value", "<>value", ">value", ">=value", "<value", // 4
			"<=value", "is null" // 6
	}, { // 条件的中文表示
			"等于", "不等于", "大于", "大于或等于", "小于", "小于或等于", "为空" // 6
			}, { // 条件的英文表示
			"equal", "not equal", "great than", "great than or equal", "less than", // 4
					"less than or equal", "is null" // 6
			} };

	String[][] textCondi = { { "='value'", "<>'value'", " like '%value%' ", // 2
			" not like '%value%' ", " like 'value%' ", // 4
			" like  '%value' ", "is null" // 5
	}, { // 条件的中文表示
			"等于", "不等于", "包含", "不包含", "字串起始于", "字串结束于", "为空" // 5
			}, { // 条件的英文表示
			"equal", "not equal", "inlcude", "not include", "string start with", // 4
					"string end with", "is null" // 5
			} };

	public SearchPageBean() {
	}
	
	public void setRequest(HttpServletRequest newRequest){
    super.setRequest(newRequest);
  }
	
	public int init(HttpServletRequest request, JspWriter out) {
		super.setRequest(request);
		this.beanInit();

		if (lang.equals("C")) {
			langIndex = 0;
		}
		if (lang.equals("E")) {
			langIndex = 1;

		}

		// 20040806 setListFieldVisible
		String ignoreFields = "";
		if (request.getAttribute("ignoreFields") != null) {
			ignoreFields = request.getAttribute("ignoreFields").toString();
		}
		int ifi = ignoreFields.indexOf(",");
		while (ifi > -1) {
			String ifName = ignoreFields.substring(0, ifi);
			ignoreFields = ignoreFields.substring(ifi + 1);
			fieldsList.remove(ifName);
			ifi = ignoreFields.indexOf(",");
		}

		try {
			// 得页面字段数据
			int fieldNum = fieldsList.size();
			StringBuffer fields = new StringBuffer();
			// fields 存放着所有字段信息
			fields.append(StringTools.getMargin(2)
					+ "<span id='fieldInfor' style='display:none'>");

			String fieldName, fieldCName, type, size;
			// 用于得到下列字段列表集合,以便调用时方便
			StringBuffer options = new StringBuffer();
			options
					.append("<select id='fieldoptions' name='fieldoptions' onchange='changeField();'>");

			// 以下设置用于处值集
			
			firstField = null;

			int saveFieldsNum = 0;
			for (int i = 0; i < fieldNum; i++) {
				fieldName = (String) fieldsList.get(i);
				fieldCName = resource.getLang(fieldName);
				Field field = entityMeta.getField(fieldName);
				boolean fva = field.isSave();
				if (!fva) { // 为 true 时不保存，=false 时保存
					continue;
				}
				// leidh;20060724;
				if (field.getType() != null
						&& "BLOB".equals(field.getType().toUpperCase())) {
					continue;
				}

				saveFieldsNum++;
				if (firstField == null) {
					firstField = fieldName;

				}
				type = field.getType();
				type = type.toLowerCase();
				size = "" + field.getMaxLength();

				fields.append(StringTools.getMargin(4) + "<span id='" + fieldName
						+ "' " + " cname='" + fieldCName + "' " + " type='" + type + "'"
						+ " size='" + size + "'" + " >\n");

				// 将每个字段的查询输入处理自动加入进来
				fields.append(StringTools.getMargin(6) + "<span id='" + fieldName
						+ "text' >\n ");
				String vsCode = field.getVscode();
				String refCode = field.getRefName();
				if (refCode == null && vsCode != null) {
					fields.append(getValsField(vsCode));
				} else {
					fields.append(getTxtInput(field) + "\n");
				}
				fields.append(StringTools.getMargin(6) + "</span>\n");

				fields.append(StringTools.getMargin(4) + "</span>\n"); // 一个字段处理完成
				// 在得到初始条件的同时，也得到一些附加值，如字段列表
				options.append("<option value='" + fieldName + "'>");
				options.append(fieldCName + "</option>\n");

			}
			// 在字段属性处理完成后，加入文本及数字的输入条件
			fields.append(StringTools.getMargin(4) + getSelectTextCondi());
			fields.append(StringTools.getMargin(4) + getSelectNumCondi());
			fields.append(StringTools.getMargin(2) + "</span>\n"); // 所有字段处理完成

			options.append("</select>"); // 字段列表处理完成
			fieldOptions = options.toString();
			String webVals = getRefHTML();
			if (saveFieldsNum == 0) {
				// //Log.outD("saveFieldNumber is 0");
				return 0;
			}
			out.println(webVals);
			out.println(fields.toString());
			out.println("<span style='display:none' id='firstfield'>");
			out.println(getFirstCondi());
			out.println("</span>");
			out.println("<span style='display:none' id='fieldselect'>");
			out.println(fieldOptions);
			out.println("</span>");
			
		} catch (Exception ex) {
			// //Log.out("SearchPage Bean 136 error:" + ex.toString());
			return 0;
		}

		return 1;

	}

	String getValsField(String vsCode) {
		StringBuffer fields = new StringBuffer();

		fields.append(StringTools.getMargin(8)
				+ "<span id='userinput' name='userinput'>");
		fields.append(StringTools.getMargin(10) + "<select>");

		List valList = GeneralFunc.getValueSet(vsCode, false, null);
		for (int j = 0; j < valList.size(); j++) {
			String[] vals = (String[]) valList.get(j);
			if ((vals[1] == null || vals[1].length() == 0) && vals[0] != null) {
				vals[1] = vals[0];
			}
			fields.append(StringTools.getMargin(12) + "<option value=\"" + vals[0]
					+ "\">" + vals[1] + "</option>\n");
		}
		fields.append(StringTools.getMargin(10) + "</select>");
		fields.append(StringTools.getMargin(8) + "</span>");
		return fields.toString();
	}

	/**
	 * firstCondi=false; //第一个字段条件：false--字串,true--数值或日期 自动得到第一个字段的选项条件
	 * 
	 * @return String
	 */
	public String getFirstCondi() {
		if(firstField == null)
			return "";
		
		StringBuffer result = new StringBuffer();
		String fieldName = firstField;
		Field field = entityMeta.getField(fieldName);
		String type = field.getType();
		type = type.toLowerCase();
		// 得到输入条件，确定是字串或数值类(包括日期)
		if (type.equals("text")) {
			result.append(getSelectTextCondi());
		} else {
			result.append(getSelectNumCondi());
		}
		
		String vsCode = field.getVscode();
		String vsRef = field.getRefName();
		String txtInput;
		if (vsRef == null && vsCode != null) {
			txtInput = getValsField(vsCode);
		} else {
			txtInput = getTxtInput(field);
		}
		result.append(txtInput);
		return result.toString();
	}

	String getTxtInput(Field f) {
		String result = "<span  id='userinput' name='userinput'>";
		result += FieldToHTML.fieldEditToHTML(entityMeta.getName(), f, "", false,
				true, 1)
				+ "";
		result += "</span>\n";
		return result;
	}

	/**
	 * 参考page.java <span id="meta" componame="FA_CARD_STYL"
	 * unique="1038228156224"/> <span id="calls"> </span> <span id="fields"
	 * tablename="FA_CARD_STYL"> <span fieldname="CARD_STYL_CODE"
	 * id="CARD_STYL_CODEMeta" pk="true"></span> <span fieldname="CARD_STYL_NAME"
	 * id="CARD_STYL_NAMEMeta" pk="false"></span> </span> <span id="maintable"
	 * tablename="FA_CARD_STYL"> <span id="FA_CARD_STYLDMeta"
	 * tablename="FA_CARD_STYLD"> </span> </span> <span id="foreigns"> </span>
	 * <span id="status" value="edit"></span> </span> 数据文件请参见
	 * D:\LMAYERP\debug\三层数据结构--数据.txt 具体表示例子
	 * 
	 * 
	 * @return String
	 */
	public String getRefHTML() {
		StringBuffer result = new StringBuffer();
		result.append("   <span id=\"meta\" ");
		result.append(" componame=\"" + compoName + "\"");
		result.append(">");
		result.append(metaTableToString()); // 处理主表标签标识
		result.append(metaForeignToString()); // 处理外键列表标识
		result.append(StringTools.getMargin(1) + "</span>\n");
		return result.toString();
	}

	private String metaTableToString() {
		StringBuffer result = new StringBuffer();
		String mainTableName = entityMeta.getName();
		int dbwhich = DAOFactory.getWhichFactory(); // :0-- oracle,1:other database
		// (sql_server)
		String isOracle = dbwhich == 0 ? "y" : "n";
		result.append(StringTools.getMargin(2) + "<span id=\"maintable\"");
		result.append(" tablename=\"" + mainTableName + "\" ");
		result.append(" isoracle=\"" + isOracle + "\">\n");
		result.append(StringTools.getMargin(2) + "</span>\n");
		return result.toString();
	}

	/**
	 * 得到当前查询页面外键引用的关系标签
	 * 
	 * @return String
	 */

	private String metaForeignToString() {
		StringBuffer result = new StringBuffer();
		result.append(StringTools.getMargin(2) + "<span id=\"foreigns\">\n");
		List refNames = entityMeta.getForeignNames();
		String refName;
		Foreign refEntry = null;
		for (int i = 0; i < refNames.size(); i++) {
			refName = (String) refNames.get(i);
			refEntry = entityMeta.getForeign(refName);
			result.append(StringTools.getMargin(3));
			result.append("<span id=\"" + refName + "Meta\"");
			result.append(" name=\"" + refName + "\"");
			result.append(" tablename=\"" + entityMeta.getName() + "\"");
			result.append(" foreigntablename=\"" + refEntry.getTableName() + "\"");
			result.append(" componame=\"" + refEntry.getCompoName() + "\"");
			result.append(" ismultisel=\"" + refEntry.isMultiSel() + "\"");
			result.append(">\n");
			result.append(StringTools.getMargin(4));
			result.append("<span id=\"" + refName + "condition\"");
//			if (refEntry.getCondition() != null) {
//				result.append(" condition=\"" + refEntry.getCondition() + "\"");
//			}
			result.append("></span>\n");
			result.append(StringTools.getMargin(4));
			result.append("<span id=\"" + refName + "fields\">\n");
			for (Iterator iter3 = refEntry.getFields().iterator(); iter3
					.hasNext();) {
        Field field = (Field) iter3.next();
				//Field field = entityMeta.getField(fieldName);
				result.append(StringTools.getMargin(5) + "<span");
				result.append(" fieldname=\"" + field.getName() + "\"");
				result.append(" alias=\"" + field.getRefField() + "\"");
				result.append(" isFK=\"" + field.isFk() + "\">");
				result.append("</span>\n");
			}
			result.append(StringTools.getMargin(4) + "</span>\n");
			result.append(StringTools.getMargin(4));
			result.append("<span id=\"" + refName + "effectFields\">\n");
			for (Iterator iter3 = refEntry.getEffectFields().entrySet().iterator(); iter3
					.hasNext();) {
				Map.Entry tmp = (Map.Entry) iter3.next();
				String sField = (String) tmp.getKey();
				String dField = (String) tmp.getValue();
				result.append(StringTools.getMargin(5) + "<span");
				result.append(" sfield=\"" + sField + "\"");
				result.append(" dfield=\"" + dField + "\"></span>\n");
			}
			result.append(StringTools.getMargin(4) + "</span>\n");
			result.append(StringTools.getMargin(3) + "</span>\n");
		}
		result.append(StringTools.getMargin(2) + "</span>\n");
		return result.toString();
	}

	/**
	 * 得到数字或日期条件输出选项
	 * 
	 * @return String
	 */
	public String getSelectNumCondi() {
		StringBuffer result = new StringBuffer();
		result
				.append("<select id=condi name=numcondi onchange=\"changeCondi();\"> \n");
		int num = numCondi[0].length;
		for (int i = 0; i < num; i++) {
			result.append("   <option value=\"" + numCondi[0][i] + "\">");
			result.append(numCondi[1 + langIndex][i] + "</option> \n");
		}
		result.append("</select>\n");
		return result.toString();
	}

	/**
	 * 得到字串条件输出选项
	 * 
	 * @return String
	 */
	public String getSelectTextCondi() {
		StringBuffer result = new StringBuffer();
		result
				.append("<select  id=condi name=textcondi onchange=\"changeCondi();\"> \n");
		int num = numCondi[0].length;
		for (int i = 0; i < num; i++) {
			result.append("   <option value=\"" + textCondi[0][i] + "\">");
			result.append(textCondi[1 + langIndex][i] + "</option> \n");
		}
		result.append("</select>");
		return result.toString();
	}

	/**
	 * 得到当前页面所有可选字段列表
	 * 
	 * @return String
	 */
	public String getSelectField() {
		return fieldOptions;

	}

	/**
	 * 初始化Bean
	 */
	protected void beanInit() {
		super.beanInit();
		this.setEntityMeta();
		this.setFieldsList();
	}

  /**
   * 设置部件元数据
   */
  private void setEntityMeta(){
  	compoMeta = MetaManager.getCompoMeta(compoName);
  	tableName = compoMeta.getMasterTable();
    entityMeta = MetaManager.getTableMeta(tableName);
  }
  
	/**
	 * 设置字段列表
	 */
	private void setFieldsList() {
		String pageType = null;
		pageType = (String) request.getAttribute("pageType");
		if (pageType.equalsIgnoreCase("selectPage")) {
			fieldsList = getSelectFields();
		} 
		else {
			fieldsList = getListFields();
		} 
	}

	/**
	 * 页面标题
	 * 
	 * @return 页面标题
	 * @uml.property name="pageTitle"
	 */
	public String getPageTitle() {
		pageTitle = resource.getLang("SEARCHPAGE_TITLE");
		return pageTitle;
	}

	/**
	 * OK按钮的名称
	 * 
	 * @return OK按钮的名称
	 * @uml.property name="okButtonName"
	 */
	public String getOkButtonName() {
		okButtonName = resource.getLang("OK");
		return okButtonName;
	}

	/**
	 * Cancel按钮的名称
	 * 
	 * @return Cancel按钮的名称
	 * @uml.property name="cancelButtonName"
	 */
	public String getCancelButtonName() {
		cancelButtonName = resource.getLang("CANCEL");
		return cancelButtonName;
	}

	/**
	 * 部件字段信息
	 * 
	 * @return 部件字段信息
	 */
	public String getEntityFields() {
		return getEntityFields(fieldsList);
	}

	/**
	 * 表格标题——字段名称
	 * 
	 * @return 表格标题——字段名称
	 * @uml.property name="tableTitleItemName"
	 */
	public String getTableTitleItemName() {
		tableTitleItemName = resource.getLang("SEARCHPAGE_TABTITLE_ITEMNAME");
		return tableTitleItemName;
	}

	/**
	 * 表格标题——关系
	 * 
	 * @return 表格标题——关系
	 * @uml.property name="tableTitleRelation"
	 */
	public String getTableTitleRelation() {
		tableTitleRelation = resource.getLang("SEARCHPAGE_TABTITLE_RELATION");
		return tableTitleRelation;
	}

	/**
	 * 表格标题——条件
	 * 
	 * @return 表格标题——条件
	 * @uml.property name="tableTitleCondition"
	 */
	public String getTableTitleCondition() {
		tableTitleCondition = resource.getLang("SEARCHPAGE_TABTITLE_CONDITION");
		return tableTitleCondition;
	}

	/**
	 * 页面数据
	 * 
	 * @return 页面数据
	 * @uml.property name="pageData"
	 */
	public String getPageData() {
		StringBuffer result = new StringBuffer();
		for (Iterator iter = fieldsList.iterator(); iter.hasNext();) {
			String fieldName = (String) iter.next();
			String fieldCaption = resource.getLang(fieldName);
			Field field = null;
			field = entityMeta.getField(fieldName);
			boolean isNull = field.isAllowNull();
			if (field == null) {
				continue;
			}
			String fieldType = field.getType();
			if (field.getVscode() != null) {
				fieldType = "ValueSet";
			}
			if (fieldType.equalsIgnoreCase("Text")) {
				result.append(getRowHTML(fieldName, fieldCaption,
						getTextRelation(fieldName), "text", "Text", ""));
			} else if (fieldType.equalsIgnoreCase("Num")) {
				result.append(getRowHTML(fieldName, fieldCaption,
						getQuantityRelation(fieldName), "text", "Num",
						" onkeypress=\"num_KeyPress()\""));
			} else if (fieldType.equalsIgnoreCase("Date")) {
				result.append(getRowHTML(fieldName, fieldCaption,
						getDateRelation(fieldName), "text", "Date",
						" onkeypress=\"date_KeyPress()\""));
			} else if (fieldType.equalsIgnoreCase("Datetime")) {
				result.append(getRowHTML(fieldName, fieldCaption,
						getDatetimeRelation(fieldName), "text", "Datetime",
						" onkeypress=\"datetime_KeyPress()\""));

			} else if (fieldType.equalsIgnoreCase("ValueSet")) {
				String code = null;
				if (field.getVscode() != null) {
					code = field.getVscode();
				}
				result.append(getRowHTML(fieldName, fieldCaption, getValueSetRelation(
						fieldName, code, isNull), "hidden", "ValueSet", ""));
			}
		}
		pageData = result.toString();
		return pageData;
	}

	/**
	 * 获得每行的HTML代码
	 * 
	 * @param field
	 *          字段元素对象
	 * @param fieldName
	 *          字段名
	 * @param fieldCaption
	 *          字段标题
	 * @param fieldRelation
	 *          字段对比关系
	 * @param type
	 *          input元素类型
	 * @param fieldType
	 *          字段类型
	 * @param otherAttribute
	 *          其他属性
	 * @return 获得每行的HTML代码
	 */
	private String getRowHTML(String fieldName, String fieldCaption,
			String fieldRelation, String type, String fieldType, String otherAttribute) {
		StringBuffer rowHTML = new StringBuffer();
		rowHTML.append(StringTools.getMargin(4) + "<tr class=\"clsSearchRow\">"
				+ NEW_LINE);
		rowHTML.append(StringTools.getMargin(5) + "<td nowrap>" + NEW_LINE);
		rowHTML.append(StringTools.getMargin(6) + fieldCaption + "" + NEW_LINE);
		rowHTML.append(StringTools.getMargin(5) + "</td>" + NEW_LINE);
		rowHTML.append(StringTools.getMargin(5) + "<td align=\"center\">"
				+ NEW_LINE);
		rowHTML.append(fieldRelation);
		rowHTML.append(StringTools.getMargin(5) + "</td>" + NEW_LINE);
		rowHTML.append(StringTools.getMargin(5) + "<td>" + NEW_LINE);
		rowHTML.append(StringTools.getMargin(6) + "<input type=\"" + type
				+ "\" id=\"" + fieldName + "ID\" name=\"" + fieldName + "\""
				+ " class=\"clsInput\"" + " fieldType=\"" + fieldType + "\"");
		rowHTML.append(otherAttribute);
		rowHTML.append("></input>" + NEW_LINE);
		rowHTML.append(StringTools.getMargin(5) + "</td>" + NEW_LINE);
		rowHTML.append(StringTools.getMargin(4) + "</tr>" + NEW_LINE);
		return rowHTML.toString();
	}

	/**
	 * 文本类型字段的比较关系
	 * 
	 * @param name
	 *          字段名称
	 * @return 文本类型字段的比较关系
	 */
	private String getTextRelation(String name) {
		StringBuffer relation = new StringBuffer();
		relation.append(StringTools.getMargin(6) + "<select id=\"" + name
				+ "IDS\" name=\"" + name + "\" fieldType=\"select\" size=\"1\">"
				+ NEW_LINE);
		relation.append(StringTools.getMargin(7) + "<option value=\"=\" selected>"
				+ resource.getLang("RELATION_TEXT_EQUALS") + "</option>" + NEW_LINE);
		relation.append(StringTools.getMargin(7) + "<option value=\"like\">"
				+ resource.getLang("RELATION_TEXT_INCLUDE") + "</option>" + NEW_LINE);
		relation
				.append(StringTools.getMargin(7) + "<option value=\"not like\">"
						+ resource.getLang("RELATION_TEXT_NOTINCLUDE") + "</option>"
						+ NEW_LINE);
		relation.append(StringTools.getMargin(6) + "</select>" + NEW_LINE);
		return relation.toString();
	}

	/**
	 * 文本类型字段的比较关系
	 * 
	 * @param name
	 *          字段名称
	 * @return 文本类型字段的比较关系
	 */
	private String getQuantityRelation(String name) {
		StringBuffer relation = new StringBuffer();
		relation.append(StringTools.getMargin(6) + "<select id=\"" + name
				+ "IDS\" name=\"" + name + "\" fieldType=\"select\" size=\"1\">"
				+ NEW_LINE);
		relation.append(StringTools.getMargin(7) + "<option value=\">\">"
				+ resource.getLang("RELATION_QUANTITY_GT") + "</option>" + NEW_LINE);
		relation.append(StringTools.getMargin(7) + "<option value=\">=\">"
				+ resource.getLang("RELATION_QUANTITY_GE") + "</option>" + NEW_LINE);
		relation
				.append(StringTools.getMargin(7) + "<option value=\"=\" selected>"
						+ resource.getLang("RELATION_QUANTITY_EQUALS") + "</option>"
						+ NEW_LINE);
		relation.append(StringTools.getMargin(7) + "<option value=\"<=\">"
				+ resource.getLang("RELATION_QUANTITY_LE") + "</option>" + NEW_LINE);
		relation.append(StringTools.getMargin(7) + "<option value=\"<\">"
				+ resource.getLang("RELATION_QUANTITY_LT") + "</option>" + NEW_LINE);
		relation.append(StringTools.getMargin(6) + "</select>" + NEW_LINE);
		return relation.toString();
	}

	/**
	 * 文本类型字段的比较关系
	 * 
	 * @param name
	 *          字段名称
	 * @return 文本类型字段的比较关系
	 */
	private String getDateRelation(String name) {
		StringBuffer relation = new StringBuffer();
		relation.append(StringTools.getMargin(6) + "<select id=\"" + name
				+ "IDS\" name=\"" + name + "\" fieldType=\"select\" size=\"1\">"
				+ NEW_LINE);
		relation.append(StringTools.getMargin(7) + "<option value=\"<\">"
				+ resource.getLang("RELATION_DATE_BT") + "</option>" + NEW_LINE);
		relation.append(StringTools.getMargin(7) + "<option value=\"=\" selected>"
				+ resource.getLang("RELATION_DATE_EQUALS") + "</option>" + NEW_LINE);
		relation.append(StringTools.getMargin(7) + "<option value=\">\">"
				+ resource.getLang("RELATION_DATE_AT") + "</option>" + NEW_LINE);
		relation.append(StringTools.getMargin(6) + "</select>" + NEW_LINE);
		return relation.toString();
	}

	/**
	 * 文本类型字段的比较关系
	 * 
	 * @param name
	 *          字段名称
	 * @return 文本类型字段的比较关系
	 */

	private String getDatetimeRelation(String name) {
		StringBuffer relation = new StringBuffer();
		relation.append(StringTools.getMargin(6) + "<select id=\"" + name
				+ "IDS\" name=\"" + name + "\" fieldType=\"select\" size=\"1\">"
				+ NEW_LINE);
		relation.append(StringTools.getMargin(7) + "<option value=\"<\">"
				+ resource.getLang("RELATION_DATETIME_BT") + "</option>" + NEW_LINE);
		relation
				.append(StringTools.getMargin(7) + "<option value=\"=\" selected>"
						+ resource.getLang("RELATION_DATETIME_EQUALS") + "</option>"
						+ NEW_LINE);
		relation.append(StringTools.getMargin(7) + "<option value=\">\">"
				+ resource.getLang("RELATION_DATETIME_AT") + "</option>" + NEW_LINE);
		relation.append(StringTools.getMargin(6) + "</select>" + NEW_LINE);
		return relation.toString();
	}

	/**
	 * 文本类型字段的比较关系
	 * 
	 * @param name
	 *          字段名称
	 * @param code
	 *          值集码
	 * @param isNull
	 *          空
	 * @return 文本类型字段的比较关系
	 */
	private String getValueSetRelation(String name, String code, boolean isNull) {
		StringBuffer relation = new StringBuffer();
		List values = GeneralFunc.getValueSet(code, isNull, null);
		relation.append(StringTools.getMargin(6) + "<select id=\"" + name
				+ "IDS\" name=\"" + name + "\" fieldType=\"select\" size=\"1\""
				+ " onChange=\"searchPage_valueSet_onChange(this)\">" + NEW_LINE);
		relation
				.append(StringTools.getMargin(7) + "<option value=\"  \" selected>"
						+ resource.getLang("RELATION_VALUESET_IGNORE") + "</option>"
						+ NEW_LINE);
		for (int i = 0; i < values.size(); i++) {
			String[] setVal = (String[]) values.get(i);
			String value = StringTools.quot(setVal[0]);
			String caption = StringTools.quot(setVal[1]);
			relation.append(StringTools.getMargin(7) + "<option value=\"" + value
					+ "\">" + value + " " + caption + "</option>" + NEW_LINE);
		}
		relation.append(StringTools.getMargin(6) + "</select>" + NEW_LINE);
		return relation.toString();
	}

  /**
   * 指定字段列表fieldsList的部件字段信息
   *
   * @param fieldsList
   *          字段列表
   * @return fieldsList的部件字段信息
   */
  public String getEntityFields(List fieldsList){
    StringBuffer result = new StringBuffer();
    //弃用WF_TODO和WF_DONE。在列表时也不再需要切换componame
    //String metaCompoName = (String)request.getAttribute("formercomponame");
    
    String metaTableName = this.tableName;
    String wf_flow_type = this.compoMeta.getWfFlowType();
    String printtype = this.compoMeta.getPrintType();
    String parentCompoName = this.compoMeta.getParentName();
    String default_WF_Template = String.valueOf(this.compoMeta.getWfDefTemp());
    boolean template_is_used = this.compoMeta.isWfTempUsed();
    //if(StringTools.isEmptyString(metaCompoName)){
    String metaCompoName = this.compoName;
    /*} else{
      TableMeta tempMeta = MetaPool.getTableMeta(metaCompoName);
      wf_flow_type = tempMeta.getWF_FLOW_TYPE();
      parentCompoName = tempMeta.getParentCompo();
      default_WF_Template = String.valueOf(tempMeta.getDefault_wfTemplate());
      template_is_used = tempMeta.getTemplate_isUsed();
    }*/
    result.append(StringTools.getMargin(1) + "<span id=\"entityMeta\""
                  + " entityName=\"" + metaCompoName + "\" tableName=\""
                  + metaTableName + "\" pageField=\"" + "\""
                  + " dateField=\"" + compoMeta.getDateField() 
                  + "\" valsetField=\"" + compoMeta.getValsetField()
                  + "\" parentCompo=\"" + parentCompoName + "\" wfCompoName=\""
                  + compoName + "\" wftype=\"" + wf_flow_type
                  + "\" defaultTemplate=\"" + default_WF_Template
                  + "\" templateIsUsed=\"" + template_is_used + "\" printtype=\""
                  + printtype + "\">" + NEW_LINE);
    int num = 0;
    for(Iterator iter = fieldsList.iterator(); iter.hasNext(); num++){
      String fieldName = (String)iter.next();
      Field field = null;
      field = entityMeta.getField(fieldName);
      if(field == null){
        continue;
      }
      String fieldType = field.getType();
      if(field.getVscode() != null){
        fieldType = "ValueSet";
      }
      String pk = "false";
      List keyFields = entityMeta.getKeyFieldNames();
      for(int i = 0; i < keyFields.size(); i++){
        if(((String)(keyFields.get(i))).equals(fieldName)){
          pk = "true";
          break;
        }
      }
      String save = "y";
      if(field.isSave()){
        save = "n";
      }
      result.append(StringTools.getMargin(2) + "<field" + " name=\"" + fieldName
                    + "\"" + " no=\"" + num + "\"" + " type=\"" + fieldType + "\""
                    + " save=\"" + save + "\"" + " isKiloStyle=\""
                    + field.getKiloStyle() + "\"" + " pk=\"" + pk + "\" />"
                    + NEW_LINE);
    }
    result.append(StringTools.getMargin(1) + "</span>" + NEW_LINE);
    
    return result.toString();
  }
  
  /**
   * 设置列表页面中显示字段的列表
   */
  private List getListFields(){
    List listFields = new ArrayList(entityMeta.getListFieldNames());
    for(int i = 0; i < calls.size(); i++){
      if(listFields.contains(calls.get(i))){
        listFields.remove(calls.get(i));
      }
    }
    List invisiFields= null;
    if (!ForeignBox.COMPO_FOREIGN_FOR_SQL.equals(compoName)){
//      IUser manager = UserManager.get(userId);
//      invisiFields = manager.getUserInvisibleFields(tableName, null, null);
    }
    if (invisiFields!= null) listFields.removeAll(invisiFields);
    
    return listFields;
  }

  /**
   * 设置选择页面中显示字段的列表
   */
  private List getSelectFields(){
  	List result = null;
//  	String tabRefName = request.getParameter("tabRefName");
//  	TableData customForeign = null;
//  	if (tabRefName != null && tabRefName.trim().length() > 0) {
//  		customForeign = this.entityMeta.getCustomForeign(tabRefName);
//  		if (customForeign != null) {
//  			result = customForeign.getFieldNames();
//  		}
//  	}

  	if (result == null || result.size() == 0) {
  		result = this.entityMeta.getSelectFieldNames();
  	} else {
  		List mainFields = this.entityMeta.getFieldNames();
  		result.retainAll(mainFields);
  	}
  	return result;
  }
  
  /**
   * 获取方案名
   * 
   * @param schemaType
   *          String
   * @return String
   */
  public String getSchemaNameHTML(String schemaType) {
    String compoId = compoName;
    String userId = SessionUtils.getAttribute(request, "svUserID");
    if(userId == null)
    	userId = "null";
    StringBuffer sNames = new StringBuffer();
    if (schemaType.equalsIgnoreCase("search"))
      compoId = compoId + "_search";
    sNames
        .append("<select id=\"scheNameOptions\" onChange=\"loadCurrStatSchema()\">");
    sNames.append("<option value=\"\">");
    sNames.append("---选择方案---" + "</option>\n");
    PreparedStatement st = null;
    ResultSet rs = null;
    Connection conn = null;
    String sql = null;
    
    try {
      conn = DAOFactory.getInstance().getConnection();
      sql = "select user_sche_desc from as_user_sche where"
          + " compo_id=? and (user_id=? or is_system_sche=?)";
      st = conn.prepareStatement(sql);
      st.setString(1, compoId);
      st.setString(2, userId);
      st.setString(3, "y");
      rs = st.executeQuery();
      while (rs.next()) {
        String tmpS = rs.getString(1);
        sNames.append("<option value=\"" + tmpS + "\">");
        sNames.append(tmpS + "</option>\n");
      }
    } catch (SQLException ex) {
      throw new RuntimeException("StaSearchPage类的 getSchemaNameHTML 方法：" + sql
          + ex.toString());
    } finally {
      DBHelper.closeConnection(conn, st, rs);
    }
    sNames.append("</select>");
    return sNames.toString();
  }
}
