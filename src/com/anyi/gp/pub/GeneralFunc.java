package com.anyi.gp.pub;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.anyi.gp.Pub;
import com.anyi.gp.TableData;
import com.anyi.gp.bean.FuncBean;
import com.anyi.gp.bean.OptionBean;
import com.anyi.gp.bean.ValBean;
import com.anyi.gp.bean.ValSetBean;
import com.anyi.gp.context.ApplusContext;
import com.anyi.gp.core.dao.BaseDao;
import com.anyi.gp.desktop.Title;
import com.anyi.gp.domain.Group;
import com.anyi.gp.domain.User;
import com.anyi.gp.meta.MetaManager;
import com.anyi.gp.sso.AuthenticationFailedException;
import com.anyi.gp.sso.SessionContext;
import com.anyi.gp.sso.SessionContextBuilder;
import com.anyi.gp.sso.support.SessionContextSupport;
import com.anyi.gp.util.StringTools;

public class GeneralFunc {

  private static final Logger logger = Logger.getLogger(GeneralFunc.class);

  /**
   * 公共方法类不公开构造函数
   */
  private GeneralFunc() {
  }

  /**
   * 根据funcId获取功能
   * 
   * @param funcId
   * @return
   */
  public static FuncBean getFunc(String funcId) {
    if (funcId == null || funcId.length() == 0)
      return null;

    BeanPool beanPool = (BeanPool) ApplusContext.getBean("beanPool");
    FuncBean func = beanPool.getFunction(funcId);
    if (func != null)
      return func;

    BaseDao dao = (BaseDao) ApplusContext.getBean("baseDao");
    Map sqlMap = new HashMap();
    sqlMap.put("funcId", funcId);

    try {
      func = (FuncBean) dao.queryForObject(CommonSqlIdConst.GET_AS_FUNC, sqlMap);
    } catch (SQLException e) {
      logger.debug(e);
      throw new RuntimeException("GeneralFunc类的getFunc方法：" + e.toString());
    }

    return func;
  }

  /**
   * 根据compoId获取系统选项
   * 
   * @param funcId
   * @return
   */
  public static List getOptions(String compoId) {
    if (compoId == null || compoId.length() == 0)
      return null;

    BaseDao dao = (BaseDao) ApplusContext.getBean("baseDao");
    List resultList = null;
    Map sqlMap = new HashMap();
    sqlMap.put("compoId", compoId);

    try {
      resultList = dao.queryForList(CommonSqlIdConst.GET_AS_OPTION, sqlMap);
    } catch (SQLException e) {
      logger.debug(e);
      throw new RuntimeException("GeneralFunc类的getOptions方法：" + e.toString());
    }

    return resultList;
  }

  /**
   * 获取options列表
   * 
   * @param optIdList
   * @param coCode
   * @param compoId
   * @param transType
   * @return
   */
  public static List getOptions(List optIdList, String coCode, String compoId,
    String transType) {
    if (optIdList == null || optIdList.isEmpty())
      return null;
    if (coCode == null || coCode.trim().equals(""))
      coCode = "*";
    if (compoId == null || compoId.trim().equals(""))
      compoId = "*";
    if (transType == null || transType.trim().equals(""))
      transType = "*";

    try {
      StringBuffer voSqlBuf = new StringBuffer();
      List params = new ArrayList();
      String intemp = "";
      for (int i = 0; i < optIdList.size(); i++) {
        params.add(optIdList.get(i));
        intemp += "?,";
      }
      intemp = intemp.substring(0, intemp.length() - 1);

      voSqlBuf.append("select * from AS_OPTION where OPT_ID in (");
      voSqlBuf.append(intemp);
      voSqlBuf.append(") and CO_CODE=? and COMPO_ID=? and TRANS_TYPE=?");
      params.add(coCode);
      params.add(compoId);
      params.add(transType);

      logger.debug("\nDataTools.getOptionsMap():\n" + voSqlBuf.toString());
      BaseDao dao = (BaseDao) ApplusContext.getBean("baseDao");
      return dao.queryForListBySql(voSqlBuf.toString(), params.toArray());

    } catch (Exception e) {
      String msg = "\n" + "GeneralFunc.getOptions():\n" + e.getMessage();
      logger.debug(msg);
      throw new RuntimeException(e);
    }
  }

  /**
   * 从缓存中取option的值
   * @param optId
   * @return
   */
  public static String getOption(String optId) {
    BeanPool beanPool = (BeanPool) ApplusContext.getBean("beanPool");
    OptionBean option = beanPool.getOption(optId);
    String returnValue = option.getOptVal();
    return returnValue == null ? "" : returnValue;
  }

  /**
   * 取单个选项值 与交易类型无关
   * @param optId
   * @param svCoCode
   * @return
   */

  public static String getOption(String optId, String svCoCode) {
    BaseDao dao = (BaseDao) ApplusContext.getBean("baseDao");
    String resultStr = "";
    Map selectMap = new HashMap();
    OptionBean option = null;
    selectMap.put("optId", optId.toUpperCase());
    selectMap.put("coCode", "*");
    boolean isSys = false;

    try {
      List resultList = dao.queryForList(CommonSqlIdConst.GET_AS_OPTION, selectMap);
      if (null != resultList && resultList.size() > 0) {
        option = (OptionBean) resultList.get(0);
        isSys = option.isSystemOpt();
      }
      if (!isSys) {
        selectMap.put("coCode", svCoCode);
        resultList = dao.queryForList(CommonSqlIdConst.GET_AS_OPTION, selectMap);
        if (null != resultList && resultList.size() > 0)
          option = (OptionBean) resultList.get(0);
      }

      if (option != null)
        resultStr = option.getOptVal();

    } catch (SQLException ex) {
      logger.debug(ex);
      throw new RuntimeException("GeneralFunc类的getOption方法：" + ex.toString());
    }

    return resultStr;
  }

  public static String getOneOption(String optId) {
    BaseDao dao = (BaseDao) ApplusContext.getBean("baseDao");
    String resultStr = "";
    Map selectMap = new HashMap();
    OptionBean option = null;
    selectMap.put("optId", optId.toUpperCase());
    try {
      List resultList = dao.queryForList(CommonSqlIdConst.GET_AS_OPTION, selectMap);
      int isSelect1 = -1;
      int isSelect2 = -1;
      int isSelect3 = -1;
      int isSelect4 = -1;
      if (null != resultList && resultList.size() > 0) {
        for (int i = 0; i < resultList.size(); i++) {
          option = (OptionBean) resultList.get(i);
          if (!"*".equals(option.getCoCode()) && !"*".equals(option.getCompoId())) {
            isSelect1 = i;
            break;
          } else if (!"*".equals(option.getCoCode())
            && "*".equals(option.getCompoId())) {
            isSelect2 = i;
            continue;
          } else if ("*".equals(option.getCoCode())
            && !"*".equals(option.getCompoId())) {
            isSelect3 = i;
            continue;
          } else {
            isSelect4 = i;
            continue;
          }
        }
        if (isSelect1 != -1) {
          option = (OptionBean) resultList.get(isSelect1);
          resultStr = option.getOptVal();
        } else if (isSelect2 != -1) {
          option = (OptionBean) resultList.get(isSelect2);
          resultStr = option.getOptVal();
        } else if (isSelect3 != -1) {
          option = (OptionBean) resultList.get(isSelect3);
          resultStr = option.getOptVal();
        } else if (isSelect4 != -1) {
          option = (OptionBean) resultList.get(isSelect4);
          resultStr = option.getOptVal();
        } else {
          resultStr = "";
        }
      }
    } catch (SQLException ex) {
      logger.debug(ex);
      throw new RuntimeException("GeneralFunc类的getOneOption方法：" + ex.toString());
    }

    return resultStr;
  }

  /**
   * 口令加密与解密
   * 
   * @param passwd
   * @return
   */

  public static String encodePwd(String passwd) {

    String encodeStr = "$#TGDF*FAA&21we@VGXD532w23413!";
    String tempStr = "";
    if (passwd == null) {
      passwd = "";
    }

    int i;
    for (i = 0; i < passwd.length(); i++) {
      tempStr = tempStr + (char) (passwd.charAt(i) ^ encodeStr.charAt(i));
    }

    return tempStr;
  }

  /**
   * 解密用户密码
   * 
   * @param encodedPasswd
   *            String
   * @return String
   */
  public static String recodePwd(String encodedPasswd) {
    String encodeStr = "$#TGDF*FAA&21we@VGXD532w23413!";
    String tempStr = "";
    if (encodedPasswd == null) {
      encodedPasswd = "";
    }

    int i;
    for (i = 0; i < encodedPasswd.length(); i++) {
      char truePass = (char) ~(encodedPasswd.charAt(i) ^ ~encodeStr.charAt(i));
      tempStr = tempStr + truePass;
    }

    return tempStr;
  }

  /**
   * 根据值集ID取得值集的内容
   * 
   * @param code
   * @param isNull
   * @param cond
   * @return
   */
  public static List getValueSet(String valsetId, boolean isNull, String cond) {
    if (valsetId == null || valsetId.length() == 0) {
      throw new RuntimeException("GeneralFunc类的getValueSet方法：参数code为空");
    }

    BaseDao dao = (BaseDao) ApplusContext.getBean("baseDao");
    Map params = new HashMap();
    params.put("VALSET_ID", valsetId);

    List result = new ArrayList();
    if (isNull) {
      // ValBean valBean = new ValBean();
      // valBean.setVal("");
      // valBean.setValId("");
      // result.add(valBean);
      String[] st = new String[] { "", "" };
      result.add(st);
    }

    try {
      ValSetBean valset = (ValSetBean) dao.queryForObject(
        CommonSqlIdConst.GET_AS_VALSET, params);

      if (valset != null) {
        String valSQL = valset.getValSql();
        if ((valSQL != null) && (valSQL.length() > 0)) {
          String sql = valSQL;
          if (cond != null && cond.trim().length() > 0) { // <applus:combobox>中加入
            // condition
            // 属性的支持;
            int viOrderByPos = valSQL.toUpperCase().indexOf("ORDER BY");
            String[] vasSqlPartA = new String[2];
            if (viOrderByPos == -1) {
              vasSqlPartA[0] = valSQL;
              vasSqlPartA[1] = "";
            } else {
              vasSqlPartA[0] = valSQL.substring(0, viOrderByPos);
              vasSqlPartA[1] = valSQL.substring(viOrderByPos);
            }
            int viWherePos = vasSqlPartA[0].toUpperCase().indexOf("WHERE");
            if (viWherePos == -1) {
              sql = vasSqlPartA[0] + " where " + cond + vasSqlPartA[1];
            } else {
              String[] vasSqlPartB = new String[2];
              vasSqlPartB[0] = vasSqlPartA[0].substring(0, viWherePos);
              vasSqlPartB[1] = vasSqlPartA[0].substring(viWherePos
                + "where".length());
              sql = vasSqlPartB[0] + " where (" + vasSqlPartB[1] + ") and (" + cond
                + ")" + vasSqlPartA[1];
            }
          }

          sqlSearchVal(valsetId, sql, result);
        } else {
          directSearchVal(valsetId, result);
        }
      }
    } catch (SQLException ex) {
      throw new RuntimeException("DBTools类的getValueSet方法：" + ex.toString());
    }

    return result;
  }

  /**
   * 通过val_sql字段提供的sql语句查找值集
   * 
   * @param sql
   * @param result
   */
  private static void sqlSearchVal(String valsetId, String sql, List result) {
    Connection myConn = DAOFactory.getInstance().getConnection();
    Statement smValSet = null;
    ResultSet rsValSet = null;

    try {
      logger.debug("\nDBTools.sqlSearchVal():\n" + sql);
      smValSet = myConn.createStatement();
      rsValSet = smValSet.executeQuery(sql);

      while (rsValSet.next()) {
        // ValBean valBean = new ValBean();
        // valBean.setValSetId(valsetId);
        // valBean.setValId(rsValSet.getString("val_id"));
        String[] st = new String[2];
        st[0] = rsValSet.getString("val_id");
        String val = rsValSet.getString("val");
        if (val == null) {
          val = "";
        }
        st[1] = val;
        result.add(st);

        // valBean.setVal(val);

        // result.add(valBean);
      }
    } catch (SQLException ex) {
      throw new RuntimeException("OracleDML类的sqlSearchVal方法：" + ex.toString());
    } finally {
      DBHelper.closeConnection(myConn, smValSet, rsValSet);
    }
  }

  /**
   * 直接在值集表中查找值集数据
   * 
   * @param code
   * @param result
   */
  private static void directSearchVal(String valsetId, List result) {

    BaseDao dao = (BaseDao) ApplusContext.getBean("baseDao");
    Map params = new HashMap();
    params.put("VALSET_ID", valsetId);

    try {
      List valBeans = dao.queryForList(CommonSqlIdConst.GET_AS_VAL, params);
      // result.addAll(valBeans);
      for (int i = 0; i < valBeans.size(); i++) {
        ValBean valBean = (ValBean) valBeans.get(i);
        String[] st = new String[] { valBean.getValId(), valBean.getVal() };
        result.add(st);
      }
    } catch (SQLException ex) {
      throw new RuntimeException("OracleDML类的directSearchVal方法：" + ex.toString());
    }
  }

  /**
   * 根据编号规则生成编号字段值
   * 
   * @param entityMeta
   *            要编号的表信息
   * @param field
   *            要编号的字段信息
   * @param tableData
   *            当前的数据，生成编号时要用到
   * @return 新生成的编号
   */
  public static String generateNoField(String compoId, TableData tableData) {
    AutoNum auto = (AutoNum) ApplusContext.getBean("autoNum");
    return auto.getNum(compoId, tableData);
  }

  /**
   * 获取单位级次
   * 
   * @param coCode
   * @return
   */
  public static String getCoCodeLevel(String coCode) {
    if (coCode == null || coCode.trim().length() == 0)
      return "";

    String coCodeOptVal = GeneralFunc.getOption("OPT_CO_CODE");
    List splitLevels = StringTools.split(coCodeOptVal, "-");
    int splitLevel = splitLevels.size();
    Map lenAndLevel = new HashMap();
    int levelLen = 0;
    for (int i = 0; i < splitLevel; i++) {
      levelLen = levelLen + Integer.parseInt((String) splitLevels.get(i));
      lenAndLevel.put(String.valueOf(levelLen), String.valueOf(i + 1));
    }
    String coCodeLevelStr = (String) lenAndLevel
      .get(String.valueOf(coCode.length()));

    return coCodeLevelStr;
  }

  /**
   * 获取组织职位信息
   * 
   * @param request
   * @return
   */
  public static String getOrgPosiCode(HttpServletRequest request) {

    String coCode = SessionUtils.getAttribute(request, "svCoCode");
    String orgCode = SessionUtils.getAttribute(request, "svOrgCode");
    String poCode = SessionUtils.getAttribute(request, "svPoCode");
    String nd = SessionUtils.getAttribute(request, "svNd");
    return getOrgPosiCode(coCode, orgCode, poCode, nd);
  }

  /**
   * 获取组织职位信息
   * 
   * @param coCode
   * @param orgCode
   * @param poCode
   * @param nd
   * @return
   */
  public static String getOrgPosiCode(String coCode, String orgCode, String poCode,
    String nd) {
    String OrgPosiCode = "";
    if (coCode == null || orgCode == null || poCode == null || nd == null
      || coCode.length() == 0 || orgCode.length() == 0 || poCode.length() == 0
      || nd.length() == 0) {
      return "";
    }
    BaseDao dao = (BaseDao) ApplusContext.getBean("baseDao");
    Map sqlMap = new HashMap();
    sqlMap.put("coCode", coCode);
    sqlMap.put("orgCode", orgCode);
    sqlMap.put("poCode", poCode);
    sqlMap.put("nd", nd);

    try {
      Map resultMap = (Map) dao.queryForObject(CommonSqlIdConst.GET_ORG_POSI_ID,
        sqlMap);
      if (null != resultMap) {
        OrgPosiCode = (String) resultMap.get("ORG_POSI_ID");
      }

    } catch (Exception se) {
      logger.debug(se);
    }
    return OrgPosiCode;
  }

  /**
   * 根据tableName获取CompoMeta列表
   * 
   * @param tableName
   * @return
   */
  public static List getCompoMetaByTable(String tableName) {
    String sql = " select COMPO_ID from as_compo where master_tab_id = ? ";
    Object[] params = new Object[] { tableName };

    BaseDao dao = (BaseDao) ApplusContext.getBean("baseDao");
    List compoNames = dao.queryForListBySql(sql, params);

    if (compoNames == null || compoNames.isEmpty())
      return null;

    List compoMetaList = new ArrayList();
    for (int i = 0; i < compoNames.size(); i++) {
      Map map = (Map) compoNames.get(i);
      compoMetaList.add(MetaManager.getCompoMeta(map.get("compo_id").toString()));
    }

    return compoMetaList;
  }

  /**
   * 获取会计期间
   * 
   * @param iYea
   * @param iMont
   * @param iDa
   * @return
   */
  public static String getPeriod(String iYea, String iMont, String iDa) {

    int iYear = Integer.parseInt(iYea);
    int iMonth = Integer.parseInt(iMont);
    int iDay = Integer.parseInt(iDa);

    String sql = " select PERIOD_ID from ma_acc_calendar_var where "
      + " start_month*100 + start_day = "
      + " (select max(START_MONTH*100 + START_DAY) from ma_acc_calendar_var "
      + " where upper(IS_FOR_ADDJ) = 'N' and (start_month * 100 + start_day) <= ? ) ";
    Connection myConn = null;
    PreparedStatement pst = null;
    ResultSet rsRule = null;
    ResultSet rsRule1 = null;
    int period = 0;

    try {
      myConn = DAOFactory.getInstance().getConnection();
      pst = myConn.prepareStatement(sql);

      int index = 1;
      pst.setInt(index++, iMonth * 100 + iDay);

      rsRule = pst.executeQuery();

      if (rsRule.next()) {
        period = rsRule.getInt(1);
      } else {
        sql = " select START_MONTH from ma_acc_calendar_var where period_id = 1 ";
        pst = myConn.prepareStatement(sql);

        rsRule = pst.executeQuery();
        if (rsRule.next()) {
          if (rsRule.getInt(1) == Integer.parseInt(iMont)) {
            sql = " select max(period_id) from ma_acc_calendar_var ";
            pst = myConn.prepareStatement(sql);

            rsRule = pst.executeQuery();
            if (rsRule.next()) {
              period = rsRule.getInt(1);
            }
          } else {
            period = 1;
          }
        }
      }

      sql = " select year_offset,start_month from ma_acc_calendar_var where period_id = ? ";
      pst = myConn.prepareStatement(sql);
      index = 1;
      pst.setInt(index++, period);

      rsRule1 = pst.executeQuery();
      if (rsRule1.next()) {
        String offset = rsRule1.getString(1);
        if (offset == null) {
          offset = "0";
        }
        if (offset.equals("1")) {
          if (rsRule1.getInt(2) != Integer.parseInt(iMont)) {
            iYear = iYear - 1;
          }
        }
        if (offset.equals("-1")) {
          if (rsRule1.getInt(2) == Integer.parseInt(iMont)) {
            iYear = iYear + 1;
          }
        }
      }
    } catch (SQLException ex) {
      throw new RuntimeException("GeneralFunc类的getPeriod方法：" + ex.toString());
    } finally {
      DBHelper.closeConnection(null, null, rsRule1);
      DBHelper.closeConnection(myConn, pst, rsRule);
    }
    return String.valueOf(period) + "-" + String.valueOf(iYear);
  }

  /**
   * 获取当前日期
   * 
   * @return
   */
  public static String getCurrDate() {
    StringBuffer result = new StringBuffer();
    GregorianCalendar currentDate = new GregorianCalendar();
    result.append(String.valueOf(currentDate.get(Calendar.YEAR)));
    int iMon = currentDate.get(Calendar.MONTH) + 1;
    if (iMon < 10) {
      result.append("-0" + String.valueOf(iMon));
    } else {
      result.append("-" + String.valueOf(iMon));
    }
    int iDay = currentDate.get(Calendar.DATE);
    if (iDay < 10) {
      result.append("-0" + String.valueOf(iDay));
    } else {
      result.append("-" + String.valueOf(iDay));
    }
    return result.toString();
  }

  /**
   * 获得系统时间
   */
  public static String getSysTime() {
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    java.util.Date currDate = new java.util.Date();
    String result = formatter.format(currDate);
    return result;
  }

  /**
   * 获取工作路径;
   * 
   * @return
   */
  public static String getWorkPath() {
    String vsWorksPath = ApplusContext.getEnvironmentConfig().get("workpath");
    if (vsWorksPath == null || vsWorksPath.trim().length() == 0) {
      throw new RuntimeException("workpath is null or empty;");
    }
    if (!vsWorksPath.endsWith(File.separator)) {
      vsWorksPath += File.separator;
    }
    // makeDir(vsWorksPath);
    return vsWorksPath;
  }

  public static List getAppNames(ServletContext servletContext) {
    List appNames = (List) servletContext
      .getAttribute(SessionUtils.APP_NAME_LIST_KEY);
    if (appNames == null) {
      appNames = GeneralFunc.getAppNames();
      servletContext.setAttribute(SessionUtils.APP_NAME_LIST_KEY, appNames);
    }
    return appNames;
  }

  /**
   * 获取所有web应用的名称
   * 
   * @param applicationsPath
   * @return
   */
  public static List getAppNames() {
    Connection conn = null;//
    Statement sta = null;
    ResultSet res = null;
    String sql = "select distinct(product_code) from as_product_ver";
    List appNames = new ArrayList();
    try {
      conn = DAOFactory.getInstance().getConnection();
      sta = conn.createStatement();
      res = sta.executeQuery(sql);
      while (res.next()) {
        String appName = res.getString(1);
        appNames.add(appName);
      }
    } catch (SQLException ex) {
      throw new RuntimeException(ex);
    } finally {
      DBHelper.closeConnection(conn, sta, res);
    }
    return appNames;
  }

  /**
   * 获取用户信息
   * 
   * @param userId
   * @return
   */
  public static Map queryUserEmpInfo(String userId) {
    String sql = "select EMP_CODE, EMP_NAME from AS_EMP where USER_ID=?";
    BaseDao dao = (BaseDao) ApplusContext.getBean("baseDao");
    return dao.queryForMapBySql(sql, new String[] { userId });
  }

  public static List getValByValSetId(String valSetId) {
    String sql = "select VAL_ID,VAL from AS_VAL where VALSET_ID = ? order by ORD_INDEX";
    BaseDao dao = (BaseDao) ApplusContext.getBean("baseDao");
    return dao.queryForListBySql(sql, new String[] { valSetId });
  }

  public static List getPosiOrgCoCode(String empCode, String nd) {
    BaseDao dao = (BaseDao) ApplusContext.getBean("baseDao");
    Map sqlMap = new HashMap();
    sqlMap.put("empCode", empCode);
    sqlMap.put("nd", nd);
    try {
      return dao.queryForList(CommonSqlIdConst.GET_POSI_ORG_CO_CODE, sqlMap);
    } catch (Exception se) {
      logger.debug(se);
    }
    return new ArrayList();
  }

  public static List getGroupByUserId(String userId) {
    BaseDao dao = (BaseDao) ApplusContext.getBean("baseDao");
    Map params = new HashMap();
    params.put("userId", userId);
    try {
      return dao.queryForList(CommonSqlIdConst.GET_GROUP_BY_USER_ID, params);
    } catch (SQLException e) {
      logger.debug(e);
    }
    return new ArrayList();
  }

  public static List getGroupByRoleId(String roleId) {
    BaseDao dao = (BaseDao) ApplusContext.getBean("baseDao");
    Map params = new HashMap();
    params.put("roleId", roleId);
    try {
      return dao.queryForList(CommonSqlIdConst.GET_GROUP_BY_ROLE_ID, params);
    } catch (SQLException e) {
      logger.debug(e);
    }
    return new ArrayList();
  }

  public static List getTitleByGroup(List groupList) {
    List titleList = new ArrayList();
    if (groupList == null || groupList.isEmpty())
      return titleList;

    List paramList = new ArrayList();
    StringBuffer sql = new StringBuffer(
      " select GROUP_ID, PAGE_ID, PAGE_TITLE, PAGE_ORDER from v_ap_group_page ");
    for (int i = 0; i < groupList.size(); i++) {
      if (i == 0)
        sql.append(" where group_id in (?,");
      else
        sql.append(" ?,");
      paramList.add(((Group) groupList.get(i)).getId());
    }

    BaseDao dao = (BaseDao) ApplusContext.getBean("baseDao");
    List result = dao.queryForListBySql(sql.substring(0, sql.length() - 1)
      + ") order by page_order ", paramList.toArray());
    for (int i = 0; i < result.size(); i++) {
      Map map = (Map) result.get(i);
      Title title = new Title();
      title.setGroupId((String) map.get("GROUP_ID"));
      title.setTitleId((String) map.get("PAGE_ID"));
      title.setTitleName((String) map.get("PAGE_TITLE"));
      title.setIndex(Pub.parseInt(map.get("PAGE_ORDER")));
      titleList.add(title);
    }

    return titleList;
  }

  /**
   * 检查用户登录ip是否有效
   * 
   * @param ip
   * @param userId
   * @return
   */
  public static boolean checkIPValidity(String remoteAddr, String userId) {
    boolean ifIpInclude = true;
    BaseDao dao = (BaseDao) ApplusContext.getBean("baseDao");
    Map params = new HashMap();
    params.put("USER_ID", userId);
    try {
      List data = dao.queryForList(CommonSqlIdConst.GET_IP_BY_USER_ID, params);
      for (int i = 0; i < data.size(); i++) {
        Map result = (Map) data.get(i);
        String grantIp = (String) result.get("GRANT_IP");
        String grantUser = (String) result.get("USER_ID");
        if (!remoteAddr.equalsIgnoreCase(grantIp)) { // IP是否相等
          ifIpInclude = false;
          continue;
        } else {
          if (grantUser == null || grantUser.length() == 0) { // IP相等，无帐号，
            ifIpInclude = true;
            break;
          } else {
            if (!userId.equalsIgnoreCase(grantUser)) { // IP相等，帐号不等
              ifIpInclude = false;
              continue;
            } else { // IP相等，帐号也相等
              ifIpInclude = true;
              break;
            }
          }
        }
      }
    } catch (SQLException e) {
      logger.debug(e);
    }
    return ifIpInclude;
  }

  public static String getCompoTabId(String compoId) {
    String masterTabId = null;
    BaseDao dao = (BaseDao) ApplusContext.getBean("baseDao");
    Map sqlMap = new HashMap();
    sqlMap.put("compoId", compoId);

    try {
      Map resMap = (Map) dao.queryForObject(CommonSqlIdConst.GET_TABID_BY_COMPO_ID,
        sqlMap);
      if (resMap != null)
        masterTabId = (String) resMap.get("MASTER_TAB_ID");
    } catch (SQLException se) {
      logger.debug(se);
    }
    return masterTabId;
  }

  public static List getPreColByTabId(String tabId) {
    if (tabId != null && !tabId.equals("")) {
      BaseDao dao = (BaseDao) ApplusContext.getBean("baseDao");
      Map sqlMap = new HashMap();
      sqlMap.put("tabId", tabId);
      try {
        return dao.queryForList(CommonSqlIdConst.GET_TAB_COL_BY_TAB_ID, sqlMap);
      } catch (SQLException se) {
        logger.debug(se);
      }
    }
    return new ArrayList();
  }

  public static List getValSet() {
    BaseDao dao = (BaseDao) ApplusContext.getBean("baseDao");
    try {
      return dao.queryForList(CommonSqlIdConst.GET_VALSET);
    } catch (SQLException se) {
      logger.debug(se);
    }
    return new ArrayList();
  }

  public static List getPreTabId() {
    BaseDao dao = (BaseDao) ApplusContext.getBean("baseDao");
    try {
      // sqlStr = "select distinct TAB_ID from AS_TAB_COL where
      // upper(IS_PRE)='Y'";
      return dao.queryForList(CommonSqlIdConst.GET_PRE_TAB_ID);
    } catch (SQLException se) {
      logger.debug(se);
    }
    return new ArrayList();
  }

  public static void updatePreCol(String tabId, String dataItem, String dataItemNa,
    String isUsed, String valSetId) {
    if (tabId != null && tabId.length() > 0 && dataItem != null
      && dataItem.length() > 0) {

      BaseDao dao = (BaseDao) ApplusContext.getBean("baseDao");
      Map sqlMap = new HashMap();
      sqlMap.put("dataItem", dataItem);
      sqlMap.put("dataItemNa", dataItemNa);
      sqlMap.put("isUsed", isUsed);
      sqlMap.put("valSetId", valSetId);

      try {
        dao.update(CommonSqlIdConst.UPDATE_AS_TAB_COL, sqlMap);
      } catch (SQLException se) {
        logger.debug(se);
      }
    }
  }

  public static void updateValSetName(String valSetId, String valSetName) {
    if (valSetId != null && valSetId.length() > 0) {
      Map sqlMap = new HashMap();
      sqlMap.put("valSetId", valSetId);
      sqlMap.put("valSetName", valSetName);
      try {
        BaseDao dao = (BaseDao) ApplusContext.getBean("baseDao");
        dao.update(CommonSqlIdConst.UPDATE_VALSET, sqlMap);
      } catch (SQLException se) {
        logger.debug(se);
      }
    }
  }

  /**
   * 将token保存到数据库中，以便session的复制同步（集群环境中）
   * @param token
   * @param userId
   */
  public static void saveTokenToDB(String token, String userId) {
    long currentTime = System.currentTimeMillis();
    Map params = new HashMap();
    params.put("USER_ID", userId);
    params.put("TICKET_ID", token);
    params.put("CURRENT_TIME", currentTime + "");

    BaseDao dao = (BaseDao) ApplusContext.getBean("baseDao");
    try {
      dao.delete(CommonSqlIdConst.DELETE_TICKET, params);
      dao.insert(CommonSqlIdConst.SAVE_TICKET, params);//保存token于数据库中
    } catch (SQLException e) {
      logger.debug(e);
    }
  }

  /**
   * 获取token信息
   * @param token
   * @param userId
   * @return
   */
  public static List getTokenFromDB(String token, String userId) {
    Map params = new HashMap();
    if (userId != null && userId.length() > 0)
      params.put("USER_ID", userId);
    if (token != null && token.length() > 0)
      params.put("TICKET_ID", token);

    BaseDao dao = (BaseDao) ApplusContext.getBean("baseDao");
    try {
      return dao.queryForList(CommonSqlIdConst.GET_TICKET, params);
    } catch (SQLException e) {
      logger.debug(e);
    }

    return new ArrayList();
  }

  /**
   * 获取登录用户
   * @param userId
   * @return
   */
  public static User getLoginUser(String userId)
    throws AuthenticationFailedException {
    User loginUser = null;
    BaseDao dao = (BaseDao) ApplusContext.getBean("baseDao");
    try {
      loginUser = (User) dao.queryForObject(CommonSqlIdConst.SQL_USER_LOGIN, userId);
    } catch (SQLException e) {
      logger.debug(e);
      throw new AuthenticationFailedException(e.getMessage());
    }
    return loginUser;
  }

  /**
   * 获取搜索方案
   * @param compoId
   * @param userId
   * @return
   */
  public static List getSearchSchema(String compoId, String userId) {
    final String sql = " select * from as_user_sche where compo_id=? and (user_id=? or is_system_sche=?) ";
    BaseDao dao = (BaseDao) ApplusContext.getBean("baseDao");
    return dao.queryForListBySql(sql, new Object[] { compoId, userId, "Y" });
  }

  public static void asySysOption(String option, String value) {
    BeanPool beanPool = (BeanPool) ApplusContext.getBean("beanPool");
    beanPool.asySysOptions(option, value);
  }

  /**
   * 检查用户登录ip是否有效
   * 
   * @param ip
   * @param userId
   * @return
   */
  public static boolean checkIPValidity(List ipList, List macList, String userId) {
    boolean ifIpInclude = true;
    BaseDao dao = (BaseDao) ApplusContext.getBean("baseDao");
    Map params = new HashMap();
    params.put("USER_ID", userId);
    //System.out.println("userId = " + userId);
    try {
      List data = dao.queryForList(CommonSqlIdConst.GET_IP_BY_USER_ID, params);
      for (int i = 0; i < data.size(); i++) {
        Map result = (Map) data.get(i);
        String grantIp = (String) result.get("GRANT_IP");
        if (null == grantIp) {
          grantIp = "";
        }
        String physicalAddress = (String) result.get("PHYSICAL_ADDRESS");
        if (null == physicalAddress) {
          physicalAddress = "";
        }
        String grantUser = (String) result.get("USER_ID");
        String ctrlType = (String) result.get("CTRL_TYPE");
        /*System.out.println("grantIp = " + grantIp);
        System.out.println("physicalAddress = " + physicalAddress);
        System.out.println("grantUser = " + grantUser);
        System.out.println("ctrlType = " + ctrlType);*/
        if ("00".equals(ctrlType)) {
          for (int j = 0; j < ipList.size(); j++) {
            if (!grantIp.equalsIgnoreCase((String) ipList.get(j))) {
              ifIpInclude = false;
              continue;
            } else {
              if (!grantUser.equals(userId) && !"".equals(grantUser)) {
                ifIpInclude = false;
                continue;
              } else {
                ifIpInclude = true;
                break;
              }
            }
          }
        } else if ("01".equals(ctrlType)) {
          for (int j = 0; j < macList.size(); j++) {
            if (!physicalAddress.equalsIgnoreCase((String) macList.get(j))) {
              ifIpInclude = false;
              continue;
            } else {
              if (!grantUser.equals(userId) && !"".equals(grantUser)) {
                ifIpInclude = false;
                continue;
              } else {
                ifIpInclude = true;
                break;
              }
            }
          }
        } else {
          for (int j = 0; j < macList.size(); j++) {
            if (!physicalAddress.equalsIgnoreCase((String) macList.get(j))
              || !grantIp.equalsIgnoreCase((String) ipList.get(j))) {
              ifIpInclude = false;
              continue;
            } else {
              if (!grantUser.equals(userId) && !"".equals(grantUser)) {
                ifIpInclude = false;
                continue;
              } else {
                ifIpInclude = true;
                break;
              }
            }
          }
        }
        if (ifIpInclude) {
          break;
        }
      }
    } catch (SQLException e) {
      logger.debug(e);
    }
    return ifIpInclude;
  }

  /**
   * sessionContext的复制，因为classloader的不一致，使用反射的方法调用方式复制
   * @param from
   * @return
   * @throws SecurityException
   * @throws NoSuchMethodException
   * @throws AuthenticationFailedException
   * @throws IllegalArgumentException
   * @throws IllegalAccessException
   * @throws InvocationTargetException
   */
  public static SessionContext copySessionContext(Object from)
    throws SecurityException, NoSuchMethodException, AuthenticationFailedException,
    IllegalArgumentException, IllegalAccessException, InvocationTargetException {
    if (from == null) {
      return null;
    }
    SessionContext to = null;
    
    Method method = from.getClass().getMethod("getCurrentUser", null);
    Object currentUser = method.invoke(from, null);
    
    if (currentUser != null && currentUser instanceof User) {
      to = new SessionContextSupport((User)currentUser);
      
      method = from.getClass().getMethod("getAllPropertyNames", null);
      Set keySet = (Set) method.invoke(from, null);
      
      Method valMethod = from.getClass().getMethod("get",new Class[] { String.class });
      Iterator iterator = keySet.iterator();
      while (iterator.hasNext()) {
        String key = (String) iterator.next();
        String value = (String) valMethod.invoke(from, new Object[] { key });
        ((SessionContextSupport) to).put(key, value);
      }
    }

    return to;
  }

  /**
   * 复制sessionContext将其他应用中的session信息复制到当前的request中去
   * @param request
   * @throws SecurityException
   * @throws IllegalArgumentException
   * @throws NoSuchMethodException
   * @throws AuthenticationFailedException
   * @throws IllegalAccessException
   * @throws InvocationTargetException
   */
  public static SessionContext copySessionContext(HttpServletRequest request)
    throws SecurityException, IllegalArgumentException, NoSuchMethodException,
    AuthenticationFailedException, IllegalAccessException, InvocationTargetException {
    String token = (String) request.getSession().getAttribute(
      SessionUtils.CURRENT_USER_TOKEN);
    ServletContext servletContext = request.getSession().getServletContext();
    SessionContext sessionContext = null;

    if (token == null
      || (sessionContext = (SessionContext) servletContext.getAttribute(token)) == null) {
      token = request.getParameter(SessionUtils.TOKEN);
      if (token != null && token.length() > 0) {
        if (sessionContext == null) {
          List appNames = GeneralFunc.getAppNames(servletContext);
          if (appNames != null) {
            for (int i = 0; i < appNames.size(); i++) {//遍历所有web应用
              String appName = (String) appNames.get(i);
              ServletContext sc = servletContext.getContext("/" + appName);

              if (sc != null) {
                Object tmp = sc.getAttribute(token);
                if (tmp != null) {
                  sessionContext = GeneralFunc.copySessionContext(tmp);
                  break;
                }
              }
            }
          }
        }

        if (sessionContext == null) {
          if (token != null) {
            if (!request.getSession().isNew()) {//非新创建的session---集群下可能有问题
              List tokenList = GeneralFunc.getTokenFromDB(token, null);
              if (!tokenList.isEmpty()) {
                Map topMap = (Map) tokenList.get(0);
                String userId = (String) topMap.get("USER_ID");
                User loginUser = null;
                try {
                  loginUser = GeneralFunc.getLoginUser(userId);
                } catch (AuthenticationFailedException e) {
                  logger.error(e);
                }
                if (loginUser != null) {
                  SessionContextBuilder builder = (SessionContextBuilder) ApplusContext
                    .getBean("sessionBuilder");
                  sessionContext = builder.getSessionContext(loginUser);
                }
              }
            }
          }
        }

        if (sessionContext != null) {
          servletContext.setAttribute(token, sessionContext);
          request.getSession().setAttribute(SessionUtils.CURRENT_USER_TOKEN, token);
        }
      }
    }
    
    return sessionContext;
  }
  public static boolean isCaRole(String userId){
	  BaseDao dao = (BaseDao) ApplusContext.getBean("baseDao");
	  Map params = new HashMap();
	  params.put("userId", userId);
	  List res = null;
	  try {
	    res = dao.queryForList(CommonSqlIdConst.GET_CA_USER_COUNT, params);
	  } catch (SQLException e) {
		logger.error(e);
	  }
	  if (null == res || res.size() == 0) {
		return false;
	  } else {
		return true;
	  }
  }

  public static void saveSessionToDB(String userId, Map session){
    final String updateSql = " update as_user_session set SESSION_VALUE = ? where USER_ID = ? and SESSION_KEY = ? ";
    final String insertSql = " insert into as_user_session(SESSION_VALUE, USER_ID, SESSION_KEY) values(?, ?, ?) ";
    
    Iterator iterator = session.entrySet().iterator();
    while(iterator.hasNext()){
      Map.Entry entry = (Map.Entry)iterator.next();
      Object[] params = new Object[]{entry.getValue(), userId, entry.getKey()};
      int row = DBHelper.executeSQL(updateSql, params);
      if(row == 0){
        DBHelper.executeSQL(insertSql, params);
      }
    }
  }
  public static Map getDefaultAccountId(String coCode, String nd) {
    BaseDao dao = (BaseDao)ApplusContext.getBean("baseDao");
    Map sqlMap = new HashMap();
    sqlMap.put("coCode", coCode);
    sqlMap.put("nd", nd);
    try {
      return (Map)dao.queryForObject("gmap-global.getDataFromMA_CO_ACC", sqlMap);
    } catch (Exception se) {
      logger.debug(se);
    }
    return new HashMap();
  }
}
