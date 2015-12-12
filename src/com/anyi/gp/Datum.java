package com.anyi.gp;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.anyi.gp.util.DataOutputHandler;
import com.anyi.gp.util.MapOutputHandler;

public class Datum {
	private String name;
	private List data ;
	private Map meta;
	private Map childDatums;
	private DataOutputHandler outputHandler = null;
	
	public Datum(){
		this.outputHandler = new MapOutputHandler();
		data = new ArrayList();
		meta = new HashMap();
		childDatums = new HashMap();
	}
	
	public Datum(DataOutputHandler outHandler) {
		this.outputHandler = outHandler;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List getData() {
		return data;
	}

	public void setData(List data) {
		this.data = data;
	}

	public Map getMeta() {
		return meta;
	}

	public void setMeta(Map meta) {
		this.meta = meta;
	}
	
	public DataOutputHandler getOutputHandler(){
		return outputHandler;
	}

	public void setOutputHandler(DataOutputHandler outputHandler){
		this.outputHandler = outputHandler;
	}

	public void addMetaField(String field, Object value){
		meta.put(field, value);
	}
	
	public Object getMetaFieldValue(String field){
		return meta.get(field);
	}
	
	public Map getChildDatums(){
		return childDatums;
	}
	
	public void addChildDatum(Datum datum) {
		childDatums.put(datum.getName(), datum);
	}
	
	public Datum getChildDatum(String name) {
		return (Datum)childDatums.get(name);
	}

	public void setChildDatums(Map childDatums){
		this.childDatums = childDatums;
	}
	
	public boolean hasChildDatum(String name) {
		Object child = this.getChildDatum(name);
		if (child == null) return false;
		else return true;
	}

	public void printData(Writer out) throws IOException {
		outputHandler.printData(out, this);
	}

	public boolean isEmpty(){
	  return this.getData().isEmpty();
	}
	
	public void pringDataX(Writer out,String tableName) throws IOException {
		out.write("<xml id=\"TableData_" + name + "_XML\" asynch=\"false\" encoding=\"GBK\" ");
//		out.write("onreadystatechange=\"DataManager_TableDataXML_onreadystatechange('");
//		out.write(tableName);
//		out.write("')\" ");
//    out.write("ondatasetcomplete=\"DataManager_TableDataXML_ondatasetcomplete('");
//    out.write(tableName);
//    out.write("')\" ");
//    out.write("oncellchange=\"DataManager_TableDataXML_oncellchange('");
//    out.write(tableName);
//    out.write("')\" ");
//    out.write("onrowsinserted=\"DataManager_TableDataXML_onrowsinserted('");
//    out.write(tableName);
//    out.write("')\" ");
//    out.write("onrowsdelete=\"DataManager_TableDataXML_onrowsdelete('");
//    out.write(tableName);
//    out.write("')\" ");
//    out.write("onrowenter=\"DataManager_TableDataXML_onrowenter('");
//    out.write(tableName);
//    out.write("')\" ");
//    out.write("onrowexit=\"DataManager_TableDataXML_onrowexit('");
//    out.write(tableName);
//    out.write("')\" ");
		out.write(">\n");
		outputHandler.printData(out, this);
		out.write("</xml>");
	}
}
