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
 * �߼�����pagebean
 * 
 * @author liuxiaoyong
 * 
 */
public class SearchPageBean extends PageRequestInfo {

	String firstField;

	String fieldOptions; // �õ���ǰHTML ҳ��� select ��ѯ�ֶ�ѡ��

	int langIndex = 0; // ����������ţ�0�����ģ�1��Ӣ�ģ�2������

	/** ҳ����� */
	private String pageTitle = null;

	/** ҳ������ */
	private String pageData = null;

	/** OK��ť������ */
	private String okButtonName = null;

	/** Cancel��ť������ */
	private String cancelButtonName = null;

	/** �����⡪���ֶ����� */
	private String tableTitleItemName = null;

	/** �����⡪����ϵ */
	private String tableTitleRelation = null;

	/** �����⡪������ */
	private String tableTitleCondition = null;

	/** �ֶ��б� */
	private List fieldsList = null;

	/** ����Ԫ���� */
	protected TableMeta entityMeta = null;

	protected CompoMeta compoMeta = null;
	
	String[][] numCondi = { { "=value", "<>value", ">value", ">=value", "<value", // 4
			"<=value", "is null" // 6
	}, { // ���������ı�ʾ
			"����", "������", "����", "���ڻ����", "С��", "С�ڻ����", "Ϊ��" // 6
			}, { // ������Ӣ�ı�ʾ
			"equal", "not equal", "great than", "great than or equal", "less than", // 4
					"less than or equal", "is null" // 6
			} };

	String[][] textCondi = { { "='value'", "<>'value'", " like '%value%' ", // 2
			" not like '%value%' ", " like 'value%' ", // 4
			" like  '%value' ", "is null" // 5
	}, { // ���������ı�ʾ
			"����", "������", "����", "������", "�ִ���ʼ��", "�ִ�������", "Ϊ��" // 5
			}, { // ������Ӣ�ı�ʾ
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
			// ��ҳ���ֶ�����
			int fieldNum = fieldsList.size();
			StringBuffer fields = new StringBuffer();
			// fields ����������ֶ���Ϣ
			fields.append(StringTools.getMargin(2)
					+ "<span id='fieldInfor' style='display:none'>");

			String fieldName, fieldCName, type, size;
			// ���ڵõ������ֶ��б���,�Ա����ʱ����
			StringBuffer options = new StringBuffer();
			options
					.append("<select id='fieldoptions' name='fieldoptions' onchange='changeField();'>");

			// �����������ڴ�ֵ��
			
			firstField = null;

			int saveFieldsNum = 0;
			for (int i = 0; i < fieldNum; i++) {
				fieldName = (String) fieldsList.get(i);
				fieldCName = resource.getLang(fieldName);
				Field field = entityMeta.getField(fieldName);
				boolean fva = field.isSave();
				if (!fva) { // Ϊ true ʱ�����棬=false ʱ����
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

				// ��ÿ���ֶεĲ�ѯ���봦���Զ��������
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

				fields.append(StringTools.getMargin(4) + "</span>\n"); // һ���ֶδ������
				// �ڵõ���ʼ������ͬʱ��Ҳ�õ�һЩ����ֵ�����ֶ��б�
				options.append("<option value='" + fieldName + "'>");
				options.append(fieldCName + "</option>\n");

			}
			// ���ֶ����Դ�����ɺ󣬼����ı������ֵ���������
			fields.append(StringTools.getMargin(4) + getSelectTextCondi());
			fields.append(StringTools.getMargin(4) + getSelectNumCondi());
			fields.append(StringTools.getMargin(2) + "</span>\n"); // �����ֶδ������

			options.append("</select>"); // �ֶ��б������
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
	 * firstCondi=false; //��һ���ֶ�������false--�ִ�,true--��ֵ������ �Զ��õ���һ���ֶε�ѡ������
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
		// �õ�����������ȷ�����ִ�����ֵ��(��������)
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
	 * �ο�page.java <span id="meta" componame="FA_CARD_STYL"
	 * unique="1038228156224"/> <span id="calls"> </span> <span id="fields"
	 * tablename="FA_CARD_STYL"> <span fieldname="CARD_STYL_CODE"
	 * id="CARD_STYL_CODEMeta" pk="true"></span> <span fieldname="CARD_STYL_NAME"
	 * id="CARD_STYL_NAMEMeta" pk="false"></span> </span> <span id="maintable"
	 * tablename="FA_CARD_STYL"> <span id="FA_CARD_STYLDMeta"
	 * tablename="FA_CARD_STYLD"> </span> </span> <span id="foreigns"> </span>
	 * <span id="status" value="edit"></span> </span> �����ļ���μ�
	 * D:\LMAYERP\debug\�������ݽṹ--����.txt �����ʾ����
	 * 
	 * 
	 * @return String
	 */
	public String getRefHTML() {
		StringBuffer result = new StringBuffer();
		result.append("   <span id=\"meta\" ");
		result.append(" componame=\"" + compoName + "\"");
		result.append(">");
		result.append(metaTableToString()); // ���������ǩ��ʶ
		result.append(metaForeignToString()); // ��������б��ʶ
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
	 * �õ���ǰ��ѯҳ��������õĹ�ϵ��ǩ
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
	 * �õ����ֻ������������ѡ��
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
	 * �õ��ִ��������ѡ��
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
	 * �õ���ǰҳ�����п�ѡ�ֶ��б�
	 * 
	 * @return String
	 */
	public String getSelectField() {
		return fieldOptions;

	}

	/**
	 * ��ʼ��Bean
	 */
	protected void beanInit() {
		super.beanInit();
		this.setEntityMeta();
		this.setFieldsList();
	}

  /**
   * ���ò���Ԫ����
   */
  private void setEntityMeta(){
  	compoMeta = MetaManager.getCompoMeta(compoName);
  	tableName = compoMeta.getMasterTable();
    entityMeta = MetaManager.getTableMeta(tableName);
  }
  
	/**
	 * �����ֶ��б�
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
	 * ҳ�����
	 * 
	 * @return ҳ�����
	 * @uml.property name="pageTitle"
	 */
	public String getPageTitle() {
		pageTitle = resource.getLang("SEARCHPAGE_TITLE");
		return pageTitle;
	}

	/**
	 * OK��ť������
	 * 
	 * @return OK��ť������
	 * @uml.property name="okButtonName"
	 */
	public String getOkButtonName() {
		okButtonName = resource.getLang("OK");
		return okButtonName;
	}

	/**
	 * Cancel��ť������
	 * 
	 * @return Cancel��ť������
	 * @uml.property name="cancelButtonName"
	 */
	public String getCancelButtonName() {
		cancelButtonName = resource.getLang("CANCEL");
		return cancelButtonName;
	}

	/**
	 * �����ֶ���Ϣ
	 * 
	 * @return �����ֶ���Ϣ
	 */
	public String getEntityFields() {
		return getEntityFields(fieldsList);
	}

	/**
	 * �����⡪���ֶ�����
	 * 
	 * @return �����⡪���ֶ�����
	 * @uml.property name="tableTitleItemName"
	 */
	public String getTableTitleItemName() {
		tableTitleItemName = resource.getLang("SEARCHPAGE_TABTITLE_ITEMNAME");
		return tableTitleItemName;
	}

	/**
	 * �����⡪����ϵ
	 * 
	 * @return �����⡪����ϵ
	 * @uml.property name="tableTitleRelation"
	 */
	public String getTableTitleRelation() {
		tableTitleRelation = resource.getLang("SEARCHPAGE_TABTITLE_RELATION");
		return tableTitleRelation;
	}

	/**
	 * �����⡪������
	 * 
	 * @return �����⡪������
	 * @uml.property name="tableTitleCondition"
	 */
	public String getTableTitleCondition() {
		tableTitleCondition = resource.getLang("SEARCHPAGE_TABTITLE_CONDITION");
		return tableTitleCondition;
	}

	/**
	 * ҳ������
	 * 
	 * @return ҳ������
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
	 * ���ÿ�е�HTML����
	 * 
	 * @param field
	 *          �ֶ�Ԫ�ض���
	 * @param fieldName
	 *          �ֶ���
	 * @param fieldCaption
	 *          �ֶα���
	 * @param fieldRelation
	 *          �ֶζԱȹ�ϵ
	 * @param type
	 *          inputԪ������
	 * @param fieldType
	 *          �ֶ�����
	 * @param otherAttribute
	 *          ��������
	 * @return ���ÿ�е�HTML����
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
	 * �ı������ֶεıȽϹ�ϵ
	 * 
	 * @param name
	 *          �ֶ�����
	 * @return �ı������ֶεıȽϹ�ϵ
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
	 * �ı������ֶεıȽϹ�ϵ
	 * 
	 * @param name
	 *          �ֶ�����
	 * @return �ı������ֶεıȽϹ�ϵ
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
	 * �ı������ֶεıȽϹ�ϵ
	 * 
	 * @param name
	 *          �ֶ�����
	 * @return �ı������ֶεıȽϹ�ϵ
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
	 * �ı������ֶεıȽϹ�ϵ
	 * 
	 * @param name
	 *          �ֶ�����
	 * @return �ı������ֶεıȽϹ�ϵ
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
	 * �ı������ֶεıȽϹ�ϵ
	 * 
	 * @param name
	 *          �ֶ�����
	 * @param code
	 *          ֵ����
	 * @param isNull
	 *          ��
	 * @return �ı������ֶεıȽϹ�ϵ
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
   * ָ���ֶ��б�fieldsList�Ĳ����ֶ���Ϣ
   *
   * @param fieldsList
   *          �ֶ��б�
   * @return fieldsList�Ĳ����ֶ���Ϣ
   */
  public String getEntityFields(List fieldsList){
    StringBuffer result = new StringBuffer();
    //����WF_TODO��WF_DONE�����б�ʱҲ������Ҫ�л�componame
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
   * �����б�ҳ������ʾ�ֶε��б�
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
   * ����ѡ��ҳ������ʾ�ֶε��б�
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
   * ��ȡ������
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
    sNames.append("---ѡ�񷽰�---" + "</option>\n");
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
      throw new RuntimeException("StaSearchPage��� getSchemaNameHTML ������" + sql
          + ex.toString());
    } finally {
      DBHelper.closeConnection(conn, st, rs);
    }
    sNames.append("</select>");
    return sNames.toString();
  }
}
