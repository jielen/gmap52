package com.anyi.gp.core.action;

import java.util.HashMap;
import java.util.Map;

import com.anyi.gp.Datum;
import com.anyi.gp.access.PageDataProvider;
import com.anyi.gp.pub.DBHelper;
import com.anyi.gp.pub.DataTools;

public class GetDigestAction extends AjaxAction{

  private static final long serialVersionUID = -572516251191980629L;

  private String sqlid;
  
  private String condition;
    
  private String componame;
  
  private String tablename;
  
  private PageDataProvider provider;

  public String getComponame() {
    return componame;
  }

  public void setComponame(String componame) {
    this.componame = componame;
  }

  public void setTablename(String tablename) {
    this.tablename = tablename;
  }

  public void setCondition(String condition) {
    this.condition = condition;
  }

  public void setSqlid(String sqlid) {
    this.sqlid = sqlid;
  }

  public void setProvider(PageDataProvider provider) {
    this.provider = provider;
  }

  public String doExecute() throws Exception {
    boolean isBlank = false;
    Map params = new HashMap(); 
    DBHelper.parseParamsSimpleForSql(condition, params);
    if (condition.indexOf("1=0") >= 0) {
      isBlank = true;
    }

    int totalCount = 0;
    if (!isBlank)
      totalCount = provider.getTotalCount(sqlid, params);

    Datum datum = provider.getPageData(1, totalCount, -1, tablename, sqlid, params, isBlank);
    resultstring = DataTools.getDigest(datum, tablename);
    
    return SUCCESS;
  }

}