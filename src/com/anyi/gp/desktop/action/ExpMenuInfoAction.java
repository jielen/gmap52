package com.anyi.gp.desktop.action;

import java.io.IOException;
import java.io.RandomAccessFile;

import com.anyi.gp.core.action.AjaxAction;

public class ExpMenuInfoAction extends AjaxAction {

  private static final long serialVersionUID = 6214441934992220294L;

  private String root;

  private String memuinfo;

  public String getMemuinfo() {
    return memuinfo;
  }

  public void setMemuinfo(String memuinfo) {
    this.memuinfo = memuinfo;
  }

  public String getRoot() {
    return root;
  }

  public void setRoot(String root) {
    this.root = root;
  }

  public String doExecute() throws Exception {

    String resultStr = "";
    String flag = "false";
    RandomAccessFile doc = null;
    try {
      doc = new RandomAccessFile(root, "rw");
      doc.seek(doc.length());
      doc.write(memuinfo.getBytes());
      flag = "true";
      resultStr = "导出成功!";
    } catch (IOException e) {
       resultStr = e.getMessage();
    }finally{
      if(doc != null)
        doc.close();
    }

    this.resultstring = this.wrapResultStr(flag, resultStr);
     
    return SUCCESS;
  }

}
