/** $Id: Field.java,v 1.9 2009/04/17 03:25:23 zhuyulong Exp $ */
package com.anyi.gp.meta;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.w3c.dom.Node;

import com.anyi.gp.pub.SessionUtils;
import com.anyi.gp.util.StringTools;
import com.anyi.gp.util.XMLTools;
import com.opensymphony.webwork.ServletActionContext;

/**
 * @author leidaohong
 */
public class Field implements Serializable{
	/** 数据类型 */
	public final static String DATA_TYPE_TEXT = "TEXT";

	public final static String DATA_TYPE_NUM = "NUM";

	public final static String DATA_TYPE_DATE = "DATE";

	public final static String DATA_TYPE_DATETIME = "DATETIME";

	public final static String DATA_TYPE_BLOB = "BLOB";

	public final static String DATA_TYPE_SEQ = "SEQ";

	public final static String DATA_TYPE_LABEL = "LABEL";

	private boolean fk;

	private boolean pk = false;

	private boolean save = false;

	private boolean list = false;

	private boolean sele = false;

	private boolean pageField = false;

	private boolean allowNull = true; // true允许为空，缺省值

	private boolean used = false;

	private String name = "";

	private String type = "";

	private String defaultValue = "";

	private int maxValue = 0x7FFFFFFF;

	private int minValue = 0 - 0x7FFFFFFF;

	private int decLength;

	private String vscode;

	private int maxLength = 24;

	private int minLength = 0;

	private String refName = null;

	private String refField = "";

	private String alias = null;

	private String vsEffectTable = null;

	private boolean noField = false;

	private String typeField = "";

	private String typeTable = "";

	private boolean noRule = false;

	private String url = null;

	private String dataItemDesc = null;

	private boolean kiloStyle = false;

	private boolean treeView = false; // add by wunianyang

	private boolean onlyLeaf = false; // add by wunianyang

	private boolean seq = false; // add by chupp

	// leidh;加入以下4项;
	private String editBoxType = "TextBox";

	// private boolean isUID = false;
	private boolean rowID = false;

	private int length = 100;

	private String _xml = "";

	public Field(){
	}

	/**
	 * @return Returns the kiloStyle.
	 * @uml.property name="kiloStyle"
	 */
	public boolean getKiloStyle(){
		return kiloStyle;
	}

	/**
	 * @param kiloStyle
	 *          The kiloStyle to set.
	 * @uml.property name="kiloStyle"
	 */
	public void setKiloStyle(boolean kilo){
		this.kiloStyle = kilo;
	}

	/**
	 * @return Returns the url.
	 * @uml.property name="url"
	 */
	public String getUrl(){
		return url;
	}

	/**
	 * @param url
	 *          The url to set.
	 * @uml.property name="url"
	 */
	public void setUrl(String s){
		this.url = s;
	}

	/**
	 * @return Returns the dataItemDesc.
	 * @uml.property name="dataItemDesc"
	 */
	public String getDataItemDesc(){
		return dataItemDesc;
	}

	/**
	 * @param dataItemDesc
	 *          The dataItemDesc to set.
	 * @uml.property name="dataItemDesc"
	 */
	public void setDataItemDesc(String s){
		this.dataItemDesc = s;
	}

	/**
	 * @return Returns the name.
	 * @uml.property name="name"
	 */
	public String getName(){
		return name;
	}

	/**
	 * @param name
	 *          The name to set.
	 * @uml.property name="name"
	 */
	public void setName(String name){
		this.name = name;
	}

	/**
	 * @return Returns the defaultValue.
	 * @uml.property name="defaultValue"
	 */
	public String getDefaultValue(){
		if(defaultValue == null || defaultValue.length() == 0){
			if(getType().equalsIgnoreCase("text")){
				defaultValue = "";
			}else if(getType().equalsIgnoreCase("num")){
				defaultValue = "";
			}else if(getType().equalsIgnoreCase("blob")){
				defaultValue = "";
			}else if(getType().equalsIgnoreCase("date")){
				defaultValue = "";// getCurrentDate();
			}else if(getType().equalsIgnoreCase("datetime")){
				defaultValue = "";// getCurrentDatetime();
			}
		}
		
		final Pattern defExpress = Pattern.compile("==DataTools.getSV\\(\"(\\w+)\"\\)");
		Matcher m = defExpress.matcher(defaultValue);
		if (m.find()){
	     String param = m.group(m.groupCount());
	     HttpServletRequest request = ServletActionContext.getRequest();
	     if(request != null){
	       return SessionUtils.getAttribute(request, param);
	     }
	  }
		return defaultValue;
	}

	/**
	 * @param defaultValue
	 *          The defaultValue to set.
	 * @uml.property name="defaultValue"
	 */
	public void setDefaultValue(String defaultValue){
		this.defaultValue = defaultValue;
		if("-999".equals(defaultValue)){
			setRowID(true);
		}
	}

	/**
	 * 数据类型，对应于 Field.DATA_TYPE_TEXT 等变量
	 * 
	 * @uml.property name="type"
	 */
	public String getType(){
		return type;
	}

	/**
	 * @param type
	 *          The type to set.
	 * @uml.property name="type"
	 */
	public void setType(String type){
		this.type = type;
		if(null != type){
			this.type = type.toUpperCase();
		}
	}

	/**
	 * @return Returns the save.
	 * @uml.property name="save"
	 */
	public boolean isSave(){
		return save;
	}

	/**
	 * @param save
	 *          The save to set.
	 * @uml.property name="save"
	 */
	public void setSave(boolean save){
		this.save = save;
	}

	/**
	 * @return Returns the list.
	 * @uml.property name="list"
	 */
	public boolean isList(){
		return list;
	}

	/**
	 * @param list
	 *          The list to set.
	 * @uml.property name="list"
	 */
	public void setList(boolean list){
		this.list = list;
	}

	/**
	 * @return Returns the select.
	 * @uml.property name="select"
	 */
	public boolean isSele(){
		return sele;
	}

	/**
	 * @param select
	 *          The select to set.
	 * @uml.property name="select"
	 */
	public void setSele(boolean select){
		this.sele = select;
	}

	/**
	 * @return Returns the pageField.
	 * @uml.property name="pageField"
	 */
	public boolean isPageField(){
		return pageField;
	}

	/**
	 * @param pageField
	 *          The pageField to set.
	 * @uml.property name="pageField"
	 */
	public void setPageField(boolean pageField){
		this.pageField = pageField;
	}

	/**
	 * @return Returns the maxValue.
	 * @uml.property name="maxValue"
	 */
	public int getMaxValue(){
		return maxValue;
	}

	/**
	 * @param maxValue
	 *          The maxValue to set.
	 * @uml.property name="maxValue"
	 */
	public void setMaxValue(int maxValue){
		this.maxValue = maxValue;
	}

	/**
	 * @return Returns the minValue.
	 * @uml.property name="minValue"
	 */
	public int getMinValue(){
		return minValue;
	}

	/**
	 * @param minValue
	 *          The minValue to set.
	 * @uml.property name="minValue"
	 */
	public void setMinValue(int minValue){
		this.minValue = minValue;
	}

	/**
	 * @return Returns the decLength.
	 * @uml.property name="decLength"
	 */
	public int getDecLength(){
		return decLength;
	}

	/**
	 * @param decLength
	 *          The decLength to set.
	 * @uml.property name="decLength"
	 */
	public void setDecLength(int decLength){
		this.decLength = decLength;
	}

	/**
	 * @return Returns the vscode.
	 * @uml.property name="vscode"
	 */
	public String getVscode(){
		return vscode;
	}

	/**
	 * @param vscode
	 *          The vscode to set.
	 * @uml.property name="vscode"
	 */
	public void setVscode(String vscode){
		this.vscode = vscode;
	}

	/**
	 * @return Returns the maxLength.
	 * @uml.property name="maxLength"
	 */
	public int getMaxLength(){
		return maxLength;
	}

	/**
	 * @param maxLength
	 *          The maxLength to set.
	 * @uml.property name="maxLength"
	 */
	public void setMaxLength(int maxLength){
		this.maxLength = maxLength;
	}

	/**
	 * @return Returns the minLength.
	 * @uml.property name="minLength"
	 */
	public int getMinLength(){
		return minLength;
	}

	/**
	 * @param minLength
	 *          The minLength to set.
	 * @uml.property name="minLength"
	 */
	public void setMinLength(int minLength){
		this.minLength = minLength;
	}

	/**
	 * @return Returns the refName.
	 * @uml.property name="refName"
	 */
	public String getRefName(){
		return refName;
	}

	/**
	 * @param refName
	 *          The refName to set.
	 * @uml.property name="refName"
	 */
	public void setRefName(String refName){
		this.refName = refName;
	}

	/**
	 * @return Returns the alias.
	 * @uml.property name="alias"
	 */
	public String getAlias(){
		return alias;
	}

	/**
	 * @param alias
	 *          The alias to set.
	 * @uml.property name="alias"
	 */
	public void setAlias(String alias){
		this.alias = alias;
	}

	/**
	 * @param vsEffectTable
	 *          The vsEffectTable to set.
	 * @uml.property name="vsEffectTable"
	 */
	public void setVsEffectTable(String tableName){
		vsEffectTable = tableName;
	}

	/**
	 * @return Returns the vsEffectTable.
	 * @uml.property name="vsEffectTable"
	 */
	public String getVsEffectTable(){
		return vsEffectTable;
	}

	/**
	 * @return Returns the typeField.
	 * @uml.property name="typeField"
	 */
	public String getTypeField(){
		return typeField;
	}

	/**
	 * @param typeField
	 *          The typeField to set.
	 * @uml.property name="typeField"
	 */
	public void setTypeField(String typeField){
		this.typeField = typeField;
	}

	/**
	 * @return Returns the typeTable.
	 * @uml.property name="typeTable"
	 */
	public String getTypeTable(){
		return typeTable;
	}

	/**
	 * @param typeTable
	 *          The typeTable to set.
	 * @uml.property name="typeTable"
	 */
	public void setTypeTable(String typeTable){
		this.typeTable = typeTable;
	}

	/**
	 * @param editBoxType
	 *          The editBoxType to set.
	 * @uml.property name="editBoxType"
	 */
	public void setEditBoxType(String editBoxType){
		if(editBoxType == null){
			editBoxType = "";
		}
		this.editBoxType = editBoxType;
	}

	public int getLength(){
		return length;
	}

	public void setLength(int length){
		this.length = length;
	}

	/**
	 * @return Returns the editBoxType.
	 * @uml.property name="editBoxType"
	 */
	public String getEditBoxType(){
		return this.editBoxType;
	}

	public boolean isFk(){
		return fk;
	}

	public void setFk(boolean fk){
		this.fk = fk;
	}

	public boolean isNoField(){
		return noField;
	}

	public void setNoField(boolean noField){
		this.noField = noField;
	}

	public boolean isNoRule(){
		return noRule;
	}

	public void setNoRule(boolean noRule){
		this.noRule = noRule;
	}

	public boolean isOnlyLeaf(){
		return onlyLeaf;
	}

	public void setOnlyLeaf(boolean onlyLeaf){
		this.onlyLeaf = onlyLeaf;
	}

	public boolean isPk(){
		return pk;
	}

	public void setPk(boolean pk){
		this.pk = pk;
	}

	public boolean isRowID(){
		return rowID;
	}

	public void setRowID(boolean rowID){
		this.rowID = rowID;
	}

	public boolean isSeq(){
		return seq;
	}

	public void setSeq(boolean seq){
		this.seq = seq;
	}

	public boolean isTreeView(){
		return treeView;
	}

	public void setTreeView(boolean treeView){
		this.treeView = treeView;
	}

	public boolean isUsed(){
		return used;
	}

	public void setUsed(boolean used){
		this.used = used;
	}

	public boolean isAllowNull(){
		return allowNull;
	}

	public void setAllowNull(boolean allowNull){
		this.allowNull = allowNull;
	}

	public String getRefField(){
		return refField;
	}

	public void setRefField(String refField){
		this.refField = refField;
	}

	public String toXML(){
		//if(_xml.length() == 0){
			StringBuffer s = new StringBuffer();
			s.append("<field");
			s.append(" name=\"").append(getName());
			s.append("\" type=\"" + getType());
			s.append("\" length=\"" + getMaxLength());
			s.append("\" scale=\"" + this.getDecLength());
			/*
			 * s.append("\" valuesetcode=\"" + ((this.getVscode() == null) ? "" :
			 * StringTools.getValidTagName(this.getVscode()))); //
			 */
			s.append("\" valuesetcode=\"" + ((this.getVscode() == null) ? "" : this.getVscode()));
			s.append("\" foreignname=\"" + ((this.getRefName() == null) ? "" : StringTools.getValidTagName(this.getRefName())));
			s.append("\" ispk=\"" + isPk());
			s.append("\" isallownull=\"" + isAllowNull());
			s.append("\" iskilo=\"" + getKiloStyle());
			s.append("\" issave=\"" + isSave()); // 说明：原来的 isSave 是反的！
			boolean isrowid = "-999".equals(getDefaultValue());
			s.append("\" isrowid=\"" + isrowid);
			if(this.getType().equals(Field.DATA_TYPE_NUM)){
				if(this.getMaxValue() == this.getMinValue() && this.getMaxValue() == 0){
					this.setMaxValue(0x7FFFFFFF);
					this.setMinValue(0 - 0x7FFFFFFF);
				}
				s.append("\" maxvalue=\"" + getMaxValue());
				s.append("\" minvalue=\"" + getMinValue());
			}else{
				s.append("\" maxvalue=\"" + this.getMaxLength());
				s.append("\" minvalue=\"" + this.getMinLength());
			}
			s.append("\">");
			s.append("<default>");
			if(!isrowid){
				if(this.getDefaultValue() != null && this.getDefaultValue().length() > 0){
					s.append(StringTools.toXMLString(StringTools.toString(getDefaultValue())));
				}
			}
			s.append("</default>");
			// s.append("\n");
			s.append("</field>\n");
			_xml = s.toString();
		//}
		return _xml;
	}
  
  /**
   * 增加外部接口参数数据的输出
   * @param outerField
   * @return
   */
  public String toXML(Node outerField){
    //if(_xml.length() == 0){
      StringBuffer s = new StringBuffer();
      s.append("<field");
      s.append(" name=\"").append(getName());
      s.append("\" type=\"" + getType());
      s.append("\" length=\"" + getMaxLength());
      s.append("\" scale=\"" + this.getDecLength());
      s.append("\" valuesetcode=\"" + ((this.getVscode() == null) ? "" : this.getVscode()));
      s.append("\" foreignname=\"" + ((this.getRefName() == null) ? "" : StringTools.getValidTagName(this.getRefName())));
      s.append("\" ispk=\"" + isPk());
      
      if(outerField == null){
        s.append("\" isallownull=\"" + this.isAllowNull());
      }
      else{
        boolean vtIsAllowNull = Boolean.valueOf(XMLTools.getNodeAttr(outerField, "isallownull", this.isAllowNull() + "")).booleanValue();
//        if (vtIsAllowNull) {
//          s.append("\" isallownull=\"" + this.isAllowNull());
//        } else {
//          s.append("\" isallownull=\"" + vtIsAllowNull);
//        }
        s.append("\" isallownull=\"" + vtIsAllowNull);
      }
      
      s.append("\" iskilo=\"" + getKiloStyle());
      s.append("\" issave=\"" + isSave()); 
      boolean isrowid = "-999".equals(getDefaultValue());
      s.append("\" isrowid=\"" + isrowid);
      if(this.getType().equals(Field.DATA_TYPE_NUM)){
        if(this.getMaxValue() == this.getMinValue() && this.getMaxValue() == 0){
          this.setMaxValue(0x7FFFFFFF);
        }
        s.append("\" maxvalue=\"" + getMaxValue());
        s.append("\" minvalue=\"" + getMinValue());
      }else{
        s.append("\" maxvalue=\"" + this.getMaxLength());
        s.append("\" minvalue=\"" + this.getMinLength());
      }
      s.append("\">");
      s.append("<default>");
      if(!isrowid){
        if(this.getDefaultValue() != null && this.getDefaultValue().length() > 0){
          s.append(StringTools.toXMLString(StringTools.toString(getDefaultValue())));
        }
      }
      s.append("</default>");
      s.append("</field>\n");
      //_xml = s.toString();
    //}
    return s.toString();
  }
}
