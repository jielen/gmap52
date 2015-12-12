package com.anyi.gp.util;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.anyi.gp.Datum;

public class MapOutputHandler implements DataOutputHandler {
	
	public void printData(Writer out,Datum datum) throws IOException {
		out.write("<" + datum.getName() + ">\n");
		printMetaData(out, datum);
		printListData(out, datum);
		out.write("</" + datum.getName() + ">\n");
	}
	
	public void printMetaData(Writer out, Datum datum) throws IOException {
	  out.write("<meta ");
    String tempValue = "";
	  for (Iterator iter = datum.getMeta().entrySet().iterator(); iter.hasNext(); ){
	    Map.Entry entry = (Map.Entry)iter.next();
      tempValue = StringTools.ifNull(entry.getValue(), "");
	    out.write(entry.getKey() + "=\"" + StringTools.toXMLString(tempValue) + "\" "); 
	  }
    out.write("></meta>\n");
	}
	
	public void printListData(Writer out, Datum datum) throws IOException {
		Iterator iter = datum.getData().iterator();
		out.write("<rowset>\n");
		while (iter.hasNext()) {
			Map data = (Map)iter.next();
			out.write(getDataString(data));
		}
		out.write("</rowset>\n");
	}
	
	private String getDataString(Map map) {
		StringBuffer buffer = new StringBuffer(128);
		buffer.append("<row>\n");
		Iterator data = map.entrySet().iterator();
		String tempValue = "";
		while (data.hasNext()) {
			Entry entry = (Entry)data.next();
			tempValue = StringTools.ifNull(entry.getValue(), "");
			buffer.append("<" + entry.getKey() + ">" + StringTools.toXMLString(tempValue) +  "</" + entry.getKey() + ">\n");
		}
		buffer.append("</row>\n");
		return buffer.toString();
	}

}
