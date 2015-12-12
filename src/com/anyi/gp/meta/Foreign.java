/* $Id: Foreign.java,v 1.4 2008/04/19 07:38:25 liuxiaoyong Exp $ */

package com.anyi.gp.meta;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

/**
 * @author leidaohong
 */
public class Foreign implements Serializable{

	public Foreign(){
	}

	/**
	 * @return Returns the tableName.
	 * @uml.property name="tableName"
	 */
	public String getTableName(){
		return tableName;
	}

	/**
	 * @param tableName
	 *          The tableName to set.
	 * @uml.property name="tableName"
	 */
	public void setTableName(String tableName){
		this.tableName = tableName;
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

	public void addField(Field field){
		this.fields.add(field);
	}

  public List getFields(){
    return this.fields;
  }
	/**
	 * @return Returns the effectFields.
	 * @uml.property name="effectFields"
	 */
	public Map getEffectFields(){
		return effectFields;
	}

	public void addEffectField(String sFieldName, String dFieldName){
		effectFields.put(sFieldName, dFieldName);
	}

	/**
	 * @return
	 * @uml.property name="isMultiSel"
	 */
	public boolean isMultiSel(){
		return multiSel;
	}

	/**
	 * @param isMultiSel
	 *          The isMultiSel to set.
	 * @uml.property name="isMultiSel"
	 */
	public void setMultiSel(boolean multi){
		multiSel = multi;
	}

	/**
	 * @return Returns the compoName.
	 * @uml.property name="compoName"
	 */
	public String getCompoName(){
		return this.compoName;
	}

	/**
	 * @param compoName
	 *          The compoName to set.
	 * @uml.property name="compoName"
	 */
	public void setCompoName(String compoName){
		this.compoName = compoName;
	}

	/**
	 * @param isOnlyLeaf
	 *          The isOnlyLeaf to set.
	 * @uml.property name="isOnlyLeaf"
	 */
	public void setOnlyLeaf(boolean only){
		this.onlyLeaf = only;
	}

	public boolean isOnlyLeaf(){
		return onlyLeaf;
	}

	/**
	 * @param tabRefName
	 *          The tabRefName to set.
	 * @uml.property name="tabRefName"
	 */
	public void setTabRefName(String tabRefName){
		this.tabRefName = tabRefName;
	}

	/**
	 * @return Returns the tabRefName.
	 * @uml.property name="tabRefName"
	 */
	public String getTabRefName(){
		return tabRefName;
	}

	// -----------------------------------
	private String tabRefName;

	private boolean multiSel;

	private String tableName;

	private String refName;

	private List fields = new ArrayList();

	private Map effectFields = new HashMap();

	private String compoName;

	private String sqlid = "";
  
	private boolean onlyLeaf = false;

	private static final String PADDING = "   ";

	private static final String SUBPADDING = "    ";

	private String _xml = "";

	public String toXML(){
		if(_xml.length() == 0){
			StringBuffer s = new StringBuffer();
			s.append(PADDING);
			s.append("<foreign");
			s.append(" name=\"").append(getRefName());
			s.append("\" fcomponame=\"").append(getCompoName());
			s.append("\" ftablename=\"").append(getTableName());
			s.append("\" ismultisel=\"").append(isMultiSel());
			s.append("\" tabrefname=\"").append(getTabRefName());
			s.append("\">");
			s.append("\n").append(SUBPADDING);
			s.append("<fields>");
			for(Iterator i = fields.iterator(); i.hasNext();){
				Field field = (Field) i.next();
				s.append("\n").append(SUBPADDING);
				s.append("<field");
				s.append(" name=\"").append(field.getName());
				s.append("\" fname=\"").append(field.getRefField());
				s.append("\" isfk=\"").append(field.isFk());
				s.append("\" />");
			}
			s.append("\n").append(SUBPADDING);
			s.append("</fields>");

			s.append("\n").append(SUBPADDING);
			s.append("<effectfields>");
			for(Iterator i = effectFields.entrySet().iterator(); i.hasNext();){
				Map.Entry entry = (Map.Entry) i.next();
				s.append("\n").append(SUBPADDING);
				s.append("<field");
				s.append(" name=\"").append(entry.getKey());
				s.append("\" fname=\"").append(entry.getValue());
				s.append("\" />");
			}
			s.append("\n").append(SUBPADDING);
			s.append("</effectfields>");

			s.append("\n").append(PADDING);
			s.append("</foreign>");
			_xml = s.toString();
		}
		return _xml;
	}

	public String getSqlid(){
		return sqlid;
	}

	public void setSqlid(String sqlid){
		this.sqlid = sqlid;
	}

	public void initAllrefFields(TableMeta meta){
		// TableMeta meta = MetaManager.getTableMeta(this.getTableName());
		Set fields = meta.getFields().entrySet();
		Iterator iterator = fields.iterator();
		while(iterator.hasNext()){
			Entry entry = (Entry) iterator.next();
			Field field = (Field) entry.getValue();
			String refName = field.getRefName();
			if(refName != null && refName.equals(this.getRefName())){
				this.addField(field);
			}
		}
	}

}
