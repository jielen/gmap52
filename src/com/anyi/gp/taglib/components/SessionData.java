package com.anyi.gp.taglib.components;

import java.io.IOException;
import java.io.Writer;

import com.anyi.gp.meta.CompoMeta;
import com.anyi.gp.meta.MetaManager;
import com.anyi.gp.pub.DataTools;
import com.anyi.gp.workflow.util.WFUtil;

public class SessionData implements Component {

  private Page ownerPage = null;

  private String componame = "";

  public void setOwnerPage(Page ownerPage) {
    this.ownerPage = ownerPage;
  }

  public void writeHTML(Writer out) throws IOException {
    out.write("<xml id=\"SessionXML\" asynch=\"false\">\n");
    out.write(DataTools
      .getSessionDataXML(componame, this.ownerPage.getCurrRequest()));
    out.write("\n");
    out.write("</xml>\n");
    CompoMeta meta = MetaManager.getCompoMeta(componame);
    if (meta != null && meta.isCompoSupportWF()) {
      String wfSession = WFUtil.getWFSessionXml(this.ownerPage.getCurrRequest());
      out.write(wfSession);
    }
  }

  public String getComponame() {
    return componame;
  }

  public void setComponame(String componame) {
    this.componame = componame;
  }

  public void writeInitScript(Writer out) throws IOException {
  }

  public String getId() {
    return "session_id";
  }

}
