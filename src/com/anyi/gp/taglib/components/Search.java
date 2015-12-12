package com.anyi.gp.taglib.components;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Search implements Container {

  private String groupid = null;

  private String tableName = null;

  private SearchBox searchBox = null;

  private List managedBoxes = new ArrayList();

  private Page ownerPage = null;

  public String getGroupid() {
    return groupid;
  }

  public void setGroupid(String groupid) {
    this.groupid = groupid;
  }

  public String getTableName() {
    return tableName;
  }

  public void setTableName(String tableName) {
    this.tableName = tableName;
  }

  public SearchBox getSearchBox() {
    return searchBox;
  }

  public void setSearchBox(SearchBox search) {
    this.searchBox = search;
  }

  public void addEditBox(EditBox box) {
    this.managedBoxes.add(box);
  }

  public String getCompoName() {
    return null;
  }

  public List getEditBoxes() {
    return this.managedBoxes;
  }

  public String getId() {
    return this.groupid;
  }

  public boolean hasSearchBox() {
    return searchBox != null;
  }

  public void setOwnerPage(Page ownerPage) {
    this.ownerPage = ownerPage;
  }

  public void writeInitScript(Writer out) throws IOException {
    for (Iterator iter = this.managedBoxes.iterator(); iter.hasNext();) {
      EditBox box = (EditBox) iter.next();
      box.writeInitScript(out);
    }
    this.searchBox.writeInitScript(out);
    buildSearchStructure(out);
  }

  private void buildSearchStructure(Writer out) throws IOException {
    out.write("PageX.getFreeManager().oSearchMap.put('" + this.getGroupid() + "',");
    out.write(searchBox.getBoxScriptVariable() + ");\n");
    for(Iterator iter = this.managedBoxes.iterator(); iter.hasNext();){
      EditBox box = (EditBox)iter.next();
      out.write(searchBox.getBoxScriptVariable());
      out.write(".addEditBox(");
      out.write(box.getScriptVarName());
      out.write(");\n");
    }
  }

  public Page getPage() {
    return this.ownerPage;
  }
}
