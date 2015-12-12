package com.anyi.gp.pub;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import com.anyi.gp.TableData;
import com.anyi.gp.core.dao.BaseDao;
import com.anyi.gp.util.XMLTools;

/**
 * 用于平台升级的工具
 * TODO:做成通用的升级工具，升级项可从配置文件中获取
 */
public class UpgradeTools {

  private static final Logger log = Logger.getLogger(UpgradeTools.class);
  
  private static final String upgradeSPName = "SP_AS_UPGRADE";
  
  private BaseDao dao;
  
  public UpgradeTools(BaseDao dao){
    this.dao = dao;
    init();
    upgrade();
  }
  
  /**
   * TODO:升级初始化设置
   *
   */
  private void init(){
    
  }
  
  /**
   * 升级
   *
   */
  private void upgrade(){
    String flag = getUpgradeFlag();//获取升级标志
    if(flag != null && "5.2".equals(flag))
      return;
    
    upgradeUserSession();
    upgradeStoredProcedure();
    
    setUpgradeFlag();//保存升级标志
  }
  
  /**
   * 升级session信息
   *
   */
  private void upgradeUserSession(){
    final String checkSql = " select count(1) from as_user_session where user_id = ? ";
    final String selectSql = " select * from as_user where user_id not in (select user_id from as_user_session)";
    final String insertSql = " insert into as_user_session (USER_ID, SESSION_KEY, SESSION_VALUE) values (?, ?, ?) ";
    
    Object checkObj = dao.queryForObjectBySql(checkSql, new Object[]{"sa"});
    if(checkObj != null){
      if(Integer.parseInt(checkObj.toString()) > 0)
        return;
    }
    
    List test = dao.queryForListBySql(selectSql, null);
    
    for(int j = 0; j < test.size(); j++){
      Map rs = (Map)test.get(j);
      String userId = rs.get("USER_ID").toString();
      Object obj = rs.get("COOKIE");
      if(obj == null) continue;
        
      Document xmlDoc = XMLTools.stringToDocument(obj.toString());
      if(xmlDoc == null) continue;
      TableData data = new TableData(xmlDoc.getDocumentElement());
        
      List fieldNames = data.getFieldNames();
      if(fieldNames == null) continue;
      for(int i = 0; i < fieldNames.size(); i++){
        String name = fieldNames.get(i).toString();
        String value = data.getFieldValue(name);
        
        Object[] params = new Object[]{userId, name, value};
        dao.executeBySql(insertSql, params);
      }
    }
  }
  
  /**
   * v52存储过程升级
   *
   */
  private void upgradeStoredProcedure(){
    DataSource dataSource = dao.getMyDataSource();
    
    Connection conn = null;
    CallableStatement pst = null;
    try {
      conn = dataSource.getConnection();
      pst = conn.prepareCall("{call " + upgradeSPName + " }");
      pst.execute();
    } catch (SQLException e) {
      log.debug(e);
    }finally{
      DBHelper.closeConnection(conn, pst, null);
    }
  }
  
  private void setUpgradeFlag(){
    String sql = " insert into as_option(OPT_ID, CO_CODE, COMPO_ID, TRANS_TYPE, OPT_VAL, IS_SYST_OPT)"
               + " values(?, ?, ?, ?, ?, ?) ";
    dao.executeBySql(sql, new Object[]{"OPT_AS_UPGRADE", "*", "*", "*", "5.2", "Y"});
  }
  
  private String getUpgradeFlag(){
    String sql = " select OPT_VAL from as_option where OPT_ID = ? and CO_CODE = ? and COMPO_ID = ? and TRANS_TYPE = ? ";
    List result = dao.queryForListBySql(sql, new Object[]{"OPT_AS_UPGRADE", "*", "*", "*"});
    if(result != null && !result.isEmpty()){
      Map map = (Map)result.get(0);
      return (String)map.get("OPT_VAL");
    }
    return null;
  }
}
