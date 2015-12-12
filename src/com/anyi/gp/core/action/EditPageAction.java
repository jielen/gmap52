package com.anyi.gp.core.action;

import java.io.StringWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.anyi.gp.Datum;
import com.anyi.gp.Delta;
import com.anyi.gp.TableData;
import com.anyi.gp.access.PageDataProvider;
import com.anyi.gp.context.ApplusContext;
import com.anyi.gp.core.dao.BaseDao;
import com.anyi.gp.meta.MetaManager;
import com.anyi.gp.pub.DataTools;

/**
 * 
 * 编辑页面数据获取
 * @author liuxiaoyong
 *
 */
public class EditPageAction extends AjaxAction {

  private static final long serialVersionUID = 3763782265999293592L;

  private String componame;
  
  private String params;

  private BaseDao dao;
  
  private String mastertable;
  
  public void setComponame(String componame) {
    this.componame = componame;
  }
  
  public void setMastertable(String mastertable) {
    this.mastertable = mastertable;
  }

  public String getParams() {
    return params;
  }

  public void setParams(String params) {
    this.params = params;
  }

  public void setDao(BaseDao dao) {
    this.dao = dao;
  }

  public String doExecute() throws Exception {
    Set childDatas = new HashSet();
    Datum datum = null;
    Delta delta = new Delta(params);
    List data = delta.getEntitysList();
    for(int i = 0; i < data.size(); i++){
      TableData tableData = (TableData)data.get(i);
      String tablename = tableData.getFieldValue("tablename");
      String sqlid = tableData.getFieldValue("sqlid");
      String condition = tableData.getFieldValue("condition");
      PageDataProvider dataProvider = (PageDataProvider)ApplusContext.getBean("pageDataProvider");
      
      if(mastertable.equals(tablename)){
        datum = dataProvider.getEditPageData(tablename, sqlid, condition);
        datum.addMetaField("torow", "-1");
        datum.addMetaField("sqlid", sqlid);
        datum.addMetaField("condition", condition);
        datum.setName(tablename);
        datum.addMetaField("digest",DataTools.getDigest(datum,tablename));
      }else{
        Datum tDatum = dataProvider.getEditPageData(tablename, sqlid, condition);
        tDatum.setName(tablename);
        if (datum == null){
          childDatas.add(tDatum);
        }else{
          datum.addChildDatum(tDatum);
        }
      }
    }
    
    if (!childDatas.isEmpty()){
      for (Iterator iter = childDatas.iterator(); iter.hasNext();){
        datum.addChildDatum((Datum)iter.next());
      }
    }
    StringWriter writer = new StringWriter();
    writer.write("<?xml version=\"1.0\" encoding=\"GBK\"?>\n");
    writer.write("<data>\n");
    datum.printData(writer);
    writer.write("</data>");
    resultstring = writer.toString();
    
    return SUCCESS;
  }

  public void before(){
    if(mastertable == null || mastertable.length() == 0){
      mastertable = MetaManager.getCompoMeta(componame).getMasterTable();
    }
  }
}
