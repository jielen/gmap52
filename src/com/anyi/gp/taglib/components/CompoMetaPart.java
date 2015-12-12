package com.anyi.gp.taglib.components;

import java.io.IOException;
import java.io.Writer;

import com.anyi.gp.meta.CompoMeta;
import com.anyi.gp.meta.MetaManager;

public class CompoMetaPart implements Component {

  private String name = "";

  private String type = Page.PAGE_TYPE_LIST;

  private boolean ismain = true;

  private Page ownerPage = null;
  
  public CompoMetaPart() {

  }

  public void setOwnerPage(Page ownerPage) {
    this.ownerPage = ownerPage;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public boolean isIsmain() {
    return ismain;
  }

  public void setIsmain(boolean ismain) {
    this.ismain = ismain;
  }

  public void writeHTML(Writer out) throws IOException {
    outCompoMeta(out);
  }

  private void outCompoMeta(Writer out) {
    try {
      out.write(MetaManager.getCompoMeta(this.name).toXml());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
  
  public CompoMeta getCompoMeta(){
    return MetaManager.getCompoMeta(this.name);
  }

  public void writeInitScript(Writer out) throws IOException {
    out.write("DataTools.regCompoMeta(\"" + this.name + "\", CompoMeta_"
      + this.name + "_XML);\n");
    if (this.ismain) {
      out.write("DataTools.oMainCompoMetaXml= CompoMeta_" + this.name + "_XML;\n");
    }
    out.write("PageX.initData('" + this.type + "');\n");
  }

  public String getId() {
    return this.name;
  }

}
