package com.anyi.gp.sso.support;

import java.sql.SQLException;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.anyi.gp.Pub;
import com.anyi.gp.core.dao.BaseDao;
import com.anyi.gp.domain.User;
import com.anyi.gp.pub.GeneralFunc;
import com.anyi.gp.sso.SessionContext;
import com.anyi.gp.sso.SessionContextBuilder;
import com.ibatis.sqlmap.client.event.RowHandler;

public class SessionContextBuilderSupport implements SessionContextBuilder {
  
  private static final Logger log = Logger.getLogger(SessionContextBuilderSupport.class);
  
  private static final String SQL_SESSION_CONTEXT = "gmap-user.getSessionContextByUser";
  
  private BaseDao dao;
  
  public BaseDao getDao() {
    return dao;
  }

  public void setDao(BaseDao dao) {
    this.dao = dao;
  }

  public SessionContext getSessionContext(User user) {
    SessionContextSupport sc = new SessionContextSupport(user);
    sc.put("dsKey", user.getDsKey());
    
    try {
      dao.queryWithRowHandler(SQL_SESSION_CONTEXT, user.getUserCode(), new SessionContextRowHandler(sc));
      initSessionContext(sc);
    } catch (SQLException e) {
      log.error(e);
      throw new RuntimeException(e);
    }
    return sc;
  }

  private class SessionContextRowHandler implements RowHandler {

    private SessionContextSupport sc = null;

    SessionContextRowHandler(SessionContextSupport sc) {
      this.sc = sc;
    }

    public void handleRow(Object arg0) {
      Map row = (Map)arg0;
      String key = (String)row.get("SESSION_KEY");
      String value = (String)row.get("SESSION_VALUE");
      sc.put(key, value);
    }
  }
  
  private void initSessionContext(SessionContextSupport sc){
    
    User user = sc.getCurrentUser();
    sc.put("svUserID", user.getUserCode());
    sc.put("svUserName", user.getUserName());
    sc.put("svRealUserID", user.getUserCode());
    sc.put("svRealUserName", user.getUserName());
    
    GregorianCalendar currentDate = new GregorianCalendar();
    
    String svTransDate = Pub.getYear(currentDate) + "-" + Pub.getMonth(currentDate) + "-" + Pub.getDay(currentDate);
    sc.put("svTransDate", svTransDate);
    
    String svSysDate = (String) sc.get("svSysDate");
    if (svSysDate == null || (!svSysDate.equals(svTransDate))) {
      sc.put("svSysDate", svTransDate);
    }
    String fiscalPeriod = GeneralFunc.getPeriod(Pub.getYear(currentDate), Pub.getMonth(currentDate), Pub.getDay(currentDate));
    fiscalPeriod = fiscalPeriod.substring(0, fiscalPeriod.indexOf("-"));
    sc.put("svFiscalPeriod", fiscalPeriod);
    sc.put("svFiscalYear", Pub.getYear(currentDate));
    
    String svStdCurrency = (String) sc.get("svStdCurrency");
    if (svStdCurrency == null) {
      svStdCurrency = GeneralFunc.getOption("opt_std_curr");
      sc.put("svStdCurrency", svStdCurrency);
    }
    
    String svEmpCode = sc.get("svEmpCode");
    if(svEmpCode == null || svEmpCode.length() == 0){
      Map empInfo = GeneralFunc.queryUserEmpInfo(user.getUserCode());
      svEmpCode = (String)empInfo.get("EMP_CODE");
      sc.put("svEmpCode", svEmpCode);
      sc.put("svEmpName", (String)empInfo.get("EMP_NAME"));
    }
    
    String svNd = (String) sc.get("svNd");
    if (svNd == null || (!svNd.equals(Pub.getYear(currentDate)))) {
      svNd = Pub.getYear(currentDate);
      sc.put("svNd", svNd);
    }
    
    List resList = GeneralFunc.getPosiOrgCoCode(svEmpCode, svNd);
    if(resList.size() != 0){
      String coCode = sc.get("svCoCode");
      Map resMap = null;
      if(coCode != null && coCode.length() > 0){
        for(int i = 0; i < resList.size(); i++){
          Map tmpMap = (Map)resList.get(i);
          if(coCode.equals(tmpMap.get("CO_CODE"))){
            resMap = tmpMap;
            break;
          }
        }
      }
      if(resMap == null){//重新设置工作环境变量
        resMap = (Map)resList.get(0);
        sc.put("svCoCode", (String) resMap.get("CO_CODE"));
        sc.put("svOrgCode", (String)resMap.get("ORG_CODE"));
        sc.put("svPoCode", (String)resMap.get("POSI_CODE"));
        sc.put("svPoName", (String)resMap.get("POSI_NAME"));
        sc.put("svOrgName", (String)resMap.get("ORG_NAME"));
        sc.put("svCoName", (String)resMap.get("CO_NAME"));
      }
    }else if(!"sa".equals(user.getUserCode())){
      sc.put("svCoCode", "");
      sc.put("svOrgCode", "");
      sc.put("svPoCode", "");
      sc.put("svPoName", "");
      sc.put("svOrgName", "");
      sc.put("svCoName", "");
    }
    if(null != sc.get("svCoCode") && null != sc.get("svNd")) {
      Map account = GeneralFunc.getDefaultAccountId(sc.get("svCoCode"), sc.get("svNd"));
      if (null != account) {
        String accountId = null == account.get("ACCOUNT_ID") ? "" : account.get("ACCOUNT_ID").toString();
        sc.put("svAccountId", accountId);
      }
    }
  }
}

