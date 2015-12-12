package com.anyi.gp.taglib.components;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.anyi.gp.Pub;
import com.anyi.gp.util.XMLTools;

public class Logic implements Component {

  private String bodyText = null;

  private Page ownerPage = null;

  private String id = null;
  
  private String jsClassName = null;
  
  private Map params = new HashMap();

  public Logic() {
    this.id = "applus_logic_" + Pub.getUID();
  }

  public String getId() {
    return this.id;
  }

  public void setOwnerPage(Page ownerPage) {
    this.ownerPage = ownerPage;
  }

  public void writeHTML(Writer out) throws IOException {
    init();
    out.write((new IncludeUtil(this.ownerPage.getCurrRequest())).make(jsClassName));
  }

  public void writeInitScript(Writer out) throws IOException {
      int viPos = jsClassName.lastIndexOf(".");
      String className = jsClassName.substring(viPos + 1);
      out.write("var voLogicObj= new " + className + "();\n");
      for (Iterator iter = params.keySet().iterator(); iter.hasNext(); ) {
        String paramName = (String)iter.next();
        String paramValue = (String)params.get(paramName);
        out.write("voLogicObj.set");
        out.write(paramName);
        out.write("(\"");
        out.write(paramValue);
        out.write("\");\n");
      }
      out.write("voLogicObj.doLogic();\n");
  }

  private void init() {
    Element root = XMLTools.stringToDocument(getBodyText()).getDocumentElement();
    this.jsClassName = root.getAttribute("classname");
    NodeList p = root.getChildNodes();
    for(int i = 0, len = p.getLength(); i < len; i++){
      Node voField = p.item(i);
      if(voField.getNodeType() != Node.ELEMENT_NODE)
        continue;
      params.put(XMLTools.getNodeAttr(voField, "name"),XMLTools.getNodeAttr(voField, "value"));
    }
  }
  
  protected String getBodyText() {
    return bodyText;
  }

  public void setBodyText(String bodyText) {
    this.bodyText = bodyText;
  }

  public void setId(String id) {
    this.id = id;
  }

}
