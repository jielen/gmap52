package com.anyi.gp.meta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.w3c.dom.Node;

public class TableMeta{

	private String name;

	/**
	 * @link aggregation
	 * @associates <{Field}>
	 */
	private Map fields = new HashMap();

	/**
	 * @link aggregation
	 * @associates <{Foreign}>
	 */
	private Map foreigns = new HashMap();

	private List fieldNames = new ArrayList();

	private List listFieldNames = new ArrayList();

	private List selectFieldNames = new ArrayList();

	private List foreignNames = new ArrayList();

	private List keyFieldNames = new ArrayList();

	private List notSaveFieldNames = new ArrayList();
	
  private List saveFieldNames = new ArrayList();
  
	private List refNames = new ArrayList();

	private List treeViewNames = new ArrayList();

	private List kiloFields = new ArrayList();

	private boolean isTable;

	private Map children = new HashMap();

	private List childTableNames = new ArrayList();

	private TableMeta parent;

	public List getSaveFieldNames() {
    return saveFieldNames;
  }

  public void setSaveFieldNames(List saveFieldNames) {
    this.saveFieldNames = saveFieldNames;
  }

  public void addSaveFieldName(String fieldName){
    saveFieldNames.add(fieldName);
  }
  
  public List getFieldNames(){
		return fieldNames;
	}

	public void setFieldNames(List fieldNames){
		this.fieldNames = fieldNames;
	}

	public Map getFields(){
		return fields;
	}

	public void setFields(Map fields){
		this.fields = fields;
	}

	public Map getForeigns(){
		return foreigns;
	}

	public void setForeigns(Map foreigns){
		this.foreigns = foreigns;
	}

	public boolean isTable(){
		return isTable;
	}

	public void setTable(boolean isTable){
		this.isTable = isTable;
	}

	public List getKeyFieldNames(){
		return keyFieldNames;
	}

	public void setKeyFieldNames(List keyFieldNames){
		this.keyFieldNames = keyFieldNames;
	}

	public String getName(){
		return name;
	}

	public void setName(String name){
		this.name = name;
	}

	public List getRefNames(){
		return refNames;
	}

	public void setRefNames(List refNames){
		this.refNames = refNames;
	}

	public List getNoSaveFieldNames(){
		return notSaveFieldNames;
	}

	public void setNotSaveFieldNames(List saveFieldNames){
		this.notSaveFieldNames = saveFieldNames;
	}

	public List getTreeViewNames(){
		return treeViewNames;
	}

	public void setTreeViewNames(List treeViewNames){
		this.treeViewNames = treeViewNames;
	}

	public List getForeignNames(){
		return foreignNames;
	}

	public void setForeignNames(List foreignNames){
		this.foreignNames = foreignNames;
	}

	public List getListFieldNames(){
		return listFieldNames;
	}

	public void setListFieldNames(List listFieldNames){
		this.listFieldNames = listFieldNames;
	}

	public List getSelectFieldNames(){
		return selectFieldNames;
	}

	public void setSelectFieldNames(List selectFieldNames){
		this.selectFieldNames = selectFieldNames;
	}

	public Map getChildren(){
		return children;
	}

	public void setChildren(Map children){
		this.children = children;
	}

	public List getChildTableNames(){
		return childTableNames;
	}

	public void setChildTableNames(List childTableNames){
		this.childTableNames = childTableNames;
	}

	public TableMeta getParent(){
		return parent;
	}

	public void setParent(TableMeta parent){
		this.parent = parent;
	}

	public List getKiloFields(){
		return kiloFields;
	}

	public void setKiloFields(List kiloFields){
		this.kiloFields = kiloFields;
	}

	public void addField(String fName, Field field){
		fields.put(fName, field);
		if(field.isPk())
			keyFieldNames.add(fName);
		if(!field.isSave())
			notSaveFieldNames.add(fName);
    if(field.isSave())
      saveFieldNames.add(fName);
		if(field.isTreeView())
			treeViewNames.add(fName);
		if(field.isList())
			listFieldNames.add(fName);
		if(field.isSele())
			selectFieldNames.add(fName);
		if(field.getKiloStyle())
			kiloFields.add(name);
		fieldNames.add(fName);
	}

	public Field getField(String name){
		return (Field) fields.get(name);
	}

	public void addForeign(String fName, Foreign foreign){
		foreigns.put(fName, foreign);
		foreignNames.add(fName);
	}

	public Foreign getForeign(String name){
		return (Foreign) foreigns.get(name);
	}

	public void addChildTableName(String childTableName){
		childTableNames.add(childTableName);
	}

	public boolean isChildTable(String childName){
		if(childName == null || childName.length() == 0)
			return false;

		if(childName.equals(name)){
			return true;
		}

		for(int i = 0; i < childTableNames.size(); i++){
			String iChildName = childTableNames.get(i).toString();
			TableMeta iTableMeta = (TableMeta) children.get(iChildName);
			if(iTableMeta.isChildTable(childName))
				return true;
		}

		return false;
	}

	/**
	 * 取得子表的Meta
	 * 
	 * @param childTableName
	 *          is child table name
	 * @param deep
	 *          is depth of search
	 * @return TableMeta
	 * @post $none
	 * @pre $none
	 */
	public TableMeta getChildTable(String childTableName, boolean deep){
		if((childTableName == null) || (childTableName.length() == 0)){
			throw new RuntimeException("TableMeta类的getChildTable方法的参数为空！" + "childTableName:" + childTableName);
		}
		TableMeta result = null;
		if(childTableNames.contains(childTableName)){
			result = (TableMeta) children.get(childTableName);
		}else if(deep){
			for(Iterator iter = children.values().iterator(); iter.hasNext();){
				TableMeta tmp = (TableMeta) iter.next();
				result = tmp.getChildTable(childTableName, deep);
				if(result != null){
					break;
				}
			}
		}
		return result;
	}

	/**
	 * 获得所有子表表名列表
	 * 
	 * @entityMeta 主表
	 * @return List 元素是子表表名字符串，一定不包含主表表名
	 */
	public List getAllChildTableNames(){
		List names = getChildTableNames();
		List allnames = new ArrayList(names);
		for(Iterator i = names.iterator(); i.hasNext();){
			String childTableName = (String) i.next();
			TableMeta childMeta = getChildTable(childTableName, false);
			if(childMeta == null)
				continue;
			for(Iterator j = childMeta.getChildTableNames().iterator(); j.hasNext();){
				String subChildTableName = (String) j.next();
				if(!allnames.contains(subChildTableName)){
					allnames.add(subChildTableName);
				}
			}
		}
		allnames.remove(getName());
		return allnames;
	}

	// 获取指定表名的 TableMeta;
	public TableMeta getTableMeta(String tableName, boolean deep){
		if(tableName.equalsIgnoreCase(this.getName()))
			return this;
		TableMeta childTableMeta = this.getChildTable(tableName, deep);
		return childTableMeta;
	}

	public String getAllTableNames(boolean deep){
		StringBuffer buf = new StringBuffer();
		buf.append(getAllTableNames(this, deep));
		return buf.toString();
	}

	private String getAllTableNames(TableMeta tableMeta, boolean deep){
		//if(_xml.length() == 0){
			StringBuffer buf = new StringBuffer();
			buf.append("<table name=\"");
			buf.append(tableMeta.getName());
			buf.append("\" istable=\"");
			buf.append(isTable);
			buf.append("\" effectfield=\"");
			buf.append("\" issave=\"");
			buf.append("true");
			buf.append("\" >\n");
			if(deep){
				Set entrySet = tableMeta.getChildren().entrySet();
				for(Iterator iter = entrySet.iterator(); iter.hasNext();){
					buf.append(getAllTableNames((TableMeta) ((Entry) iter.next()).getValue(), deep));
				}
			}
			buf.append("</table>\n");
			//_xml = buf.toString();
		//}
		return buf.toString();
	}

	public String toString(){
		return "";
	}

	public String toXml(){
	  return toXml(null);
	}
  
  /**
   * 增加外部接口参数的输出
   * @param outerField
   * @return
   */
  public String toXml(Map paramFieldMap){
    StringBuffer buffer = new StringBuffer();
    buffer.append("<xml id=\"TableMeta_" + name + "_XML\" asynch=\"false\" encoding=\"GBK\">\n");
    buffer.append("<table  istable=\"" + isTable + "\" name=\"" + name + "\">");
    buffer.append("<fields>\n");
    for(int i = 0; i < fieldNames.size(); i++){
      String fName = (String) fieldNames.get(i);
      Field field = (Field) fields.get(fName);
      if(paramFieldMap == null || (Node)paramFieldMap.get(fName) == null)
        buffer.append(field.toXML());
      else
        buffer.append(field.toXML((Node)paramFieldMap.get(fName)));
    }
    buffer.append("</fields>\n");
    buffer.append("<foreigns>\n");
    for(int i = 0; i < foreignNames.size(); i++){
      String fName = (String) foreignNames.get(i);
      Foreign foreign = (Foreign) foreigns.get(fName);
      buffer.append(foreign.toXML());
    }
    buffer.append("</foreigns>\n");
    buffer.append("</table>\n");
    buffer.append("</xml>");
    return buffer.toString();
  }
}
