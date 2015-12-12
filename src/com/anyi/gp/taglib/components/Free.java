package com.anyi.gp.taglib.components;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.anyi.gp.Pub;

public class Free implements Container {

  private String compoName = null;
  
  private String tableName = null;
  
  private List editBoxes = new ArrayList();
  
  private Page ownerPage = null;
  
  public String getCompoName() {
    return this.compoName;
  }

  public List getEditBoxes() {
    return this.editBoxes;
  }

  public String getTableName() {
    return this.tableName;
  }

  public String getId() {
    return this.tableName;
  }

  public void setOwnerPage(Page ownerPage) {
    this.ownerPage = ownerPage;
  }
  
  public Page getPage(){
    return this.ownerPage;
  }
  
  public void addEditBox(EditBox box){
    this.editBoxes.add(box);
  }

  public void writeInitScript(Writer out) throws IOException {
    for (Iterator iter = this.editBoxes.iterator(); iter.hasNext(); ){
      EditBox box = (EditBox)iter.next();
      box.writeInitScript(out);
    }
    if (this.getTableName().equals("NOTINTABLE")){
      return;
    }
    String freeId = "Free_" + Pub.getUID();
    out.write("var ");
    out.write(getScriptVarName());
    out.write(" = new Free();\n");
    out.write(getScriptVarName());
    out.write(".id = '");
    out.write(freeId);
    out.write("';\n");
    out.write("PageX.getFreeManager().regFree('");
    out.write(this.tableName);
    out.write("', ");
    out.write(getScriptVarName());
    out.write(");\n");
    for (Iterator iter = this.editBoxes.iterator(); iter.hasNext(); ){
      EditBox box = (EditBox)iter.next();
      out.write(getScriptVarName());
      out.write(".addEditBox(");
      out.write(box.getScriptVarName());
      out.write(");\n");
    }
    out.write(getScriptVarName());
    out.write(".init();\n");
  }

  public void setCompoName(String compoName) {
    this.compoName = compoName;
  }

  public void setTableName(String tableName) {
    this.tableName = tableName;
  }
  
  public String getScriptVarName(){
    return this.tableName + "_FreeV";
  }

}
