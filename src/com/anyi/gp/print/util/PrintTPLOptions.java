package com.anyi.gp.print.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.anyi.gp.util.XMLTools;

/**
 * 打印高级选项类
 * <p>
 * Title: 打印高级选项类
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2002
 * </p>
 * 
 * <p>
 * Company: UFGOV
 * </p>
 * 
 * @author ZHANGYW
 * @version 1.0
 */
public class PrintTPLOptions {
  public PrintTPLOptions() {
  }

  public static List getOptions(String s) {
    List options;
    if ((s == null) || (s.equals("")) || (s.equals("undefined"))) {
      options = null;
    } else {
      options = new ArrayList();
      Document doc = XMLTools.stringToDocument(s);
      NodeList elements = doc.getElementsByTagName("element");
      for (int i = 0; i < elements.getLength(); i++) {
        Node node = elements.item(i);
        NodeList sNode = node.getChildNodes();
        Map map = new HashMap();
        String name = "";
        String value = "";
        for (int j = 0; j < sNode.getLength(); j++) {
          name = sNode.item(j).getNodeName();
          if (sNode.item(j).getFirstChild() == null) {
            value = "";
          } else {
            value = sNode.item(j).getFirstChild().getNodeValue();
          }
          if (value == null || value.equals("null")
              || value.equals("undefined")) {
            value = "";
          }
          map.put(name, value);
        }
        options.add(map);
      }
    }
    return options;
  }
  
	public static boolean isPrintToPrinter(List options, String tplCode){
		boolean printToPrinter = false;
		if(options != null && tplCode != null){
			if(tplCode.indexOf(",") != -1){
				tplCode = tplCode.substring(0, tplCode.indexOf(","));
			}
			Map map = null;
			for(int i = 0; i < options.size(); i++){
				map = (Map)options.get(i);
				if(tplCode.endsWith((String)map.get("tplCode"))){
					break;
				}
			}
			if(map != null && !map.isEmpty()){
				String printDirect = (String)map.get("printDirect");
				if(printDirect != null && printDirect.equalsIgnoreCase("Y")){
					printToPrinter = true;
				}
			}
		}
		return printToPrinter;
	}
}
