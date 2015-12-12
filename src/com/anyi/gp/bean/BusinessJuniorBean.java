/**
 * Copyright ? 2004 BeiJing UFGOV Software Co. Ltd.
 * All right reserved.
 * Jun 15, 2005 Powered By chihongfeng
 */
package com.anyi.gp.bean;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.anyi.gp.BusinessException;
import com.anyi.gp.Delta;
import com.anyi.gp.Pub;
import com.anyi.gp.TableData;
import com.anyi.gp.pub.DAOFactory;
import com.anyi.gp.pub.DBHelper;
import com.anyi.gp.pub.TreeSelect;
import com.anyi.gp.util.StringTools;
import com.anyi.gp.workflow.BusinessJuniorExp;

/**
 * @author   leidaohong
 */
public class BusinessJuniorBean extends TreeSelect implements Comparator {

  private String id = null;

  private String name = null;

  private String desc = null;

  private int priority = 0;

  /* 本级单位设置 */
  private String[] selfComs;

  /* 本级组织/机构设置 */
  private String[] selfOrgs;

  /* 本级职位 */
  private String[] selfPositions;

  /* 本级人员设置 */
  private String[] selfUsers;

  /* 下级单位设置 */
  private String[] juniorComs;

  /* 下级组织/机构设置 */
  private String[] juniorOrgs;

  /* 下级职位设置 */
  private String[] juniorPositions;

  /* 下级人员设置 */
  private String[] juniorUsers;

  /* 年度 @from v5.0 */
  private String nd;

  /* 组织上级代码，由运行时决定 */
  public static String SUP_CODE = "0#";

  /* 同级代码，由运行时决定 */
  public static String SAME_CODE = "00#";

  private static final String AND = "@AND@";

  private static final Logger log = Logger.getLogger(BusinessJuniorBean.class);

  private List compoConditions;

  private List sysConditions;

  /**
   *
   */
  public BusinessJuniorBean() {
    super();
    String[] defValue = new String[] { COMMON_CODE };
    this.selfComs = defValue;
    this.selfOrgs = defValue;
    this.selfUsers = defValue;
    this.selfPositions = defValue;
    this.juniorComs = defValue;
    this.juniorOrgs = defValue;
    this.juniorUsers = defValue;
    this.nd = "";
    this.juniorPositions = defValue;
    compoConditions = new ArrayList();
    sysConditions = new ArrayList();

  }

  public BusinessJuniorBean(String name, String desc, int priority, String selfComs,
    String selfOrgs, String selfPositions, String selfUsers, String juniorComs,
    String juniorOrgs, String juniorPositions, String juniorUsers,
    List compoConditions, List sysConditions, String nd) {
    super();
    this.name = name;
    this.desc = desc;
    this.priority = priority;
    this.selfComs = string2Array(selfComs);
    this.selfOrgs = string2Array(selfOrgs);
    this.selfPositions = string2Array(selfPositions);
    this.selfUsers = string2Array(selfUsers);
    this.juniorComs = string2Array(juniorComs);
    this.juniorOrgs = string2Array(juniorOrgs);
    this.juniorPositions = string2Array(juniorPositions);
    this.juniorUsers = string2Array(juniorUsers);
    this.nd = nd;
    this.compoConditions = compoConditions;
    this.sysConditions = sysConditions;
  }

  /*
   * 实现排序
   *
   * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
   */
  public int compare(Object o1, Object o2) {
    BusinessJuniorBean b1 = (BusinessJuniorBean) o1;
    BusinessJuniorBean b2 = (BusinessJuniorBean) o2;
    if (b1.getPriority() == b2.getPriority())
      return 0;
    if (b2.getPriority() < b1.getPriority())
      return -1;
    return 1;
  }

  public boolean needSave() {
    String supCoCode = array2String(this.getSuperComs());
    String supOrgCode = array2String(this.getSuperOrgs());
    String supPosiCode = array2String(this.getSuperPositions());
    String supEmpCode = array2String(this.getSuperUsers());
    if ("#".equals(supCoCode) && "#".equals(supOrgCode) && "#".equals(supPosiCode)
      && "#".equals(supEmpCode)) {
      return false;
    } else {
      return true;
    }
  }

  /**
   * 数据持久化 存入数据库表 AS_WF_BUSINESS_SUPERIOR
   */
  public void save() throws SQLException, RuntimeException {
    Connection con = null;
    try {
      con = DAOFactory.getInstance().getConnection();

      String junCoCode = array2String(this.getJuniorComs());
      String junOrgCode = array2String(this.getJuniorOrgs());
      String junPosiCode = array2String(this.getJuniorPositions());
      String junEmpCode = array2String(this.getJuniorUsers());
      String supCoCode = array2String(this.getSuperComs());
      String supOrgCode = array2String(this.getSuperOrgs());
      String supPosiCode = array2String(this.getSuperPositions());
      String supEmpCode = array2String(this.getSuperUsers());
      String nd = this.getNd();
      String condition = populateConditions();
      // 查询是否存在重复记录
      List val = new ArrayList();
      val.add(id);
      val.add((SUP_CODE.equals(junCoCode) ? COMMON_CODE : junCoCode));
      val.add(junOrgCode);
      val.add(junPosiCode);
      val.add(junEmpCode);
      val.add(condition);
      val.add((SUP_CODE.equals(junCoCode) ? SUP_CODE : supCoCode));
      val.add(supOrgCode);
      val.add(supPosiCode);
      val.add(supEmpCode);
      val.add(new Integer(Integer.parseInt(nd)));
      String sql = "select * from AS_WF_BUSINESS_SUPERIOR where ID= ?"
        + " or (JUN_CO_CODE= ?" + " and " + " JUN_ORG_CODE= ? and JUN_POSI_CODE= ?"
        + " and JUN_EMP_CODE= ? and SUP_CONDITION= ?" + " and SUP_CO_CODE= ?"
        + " and SUP_ORG_CODE= ? and SUP_POSI_CODE= ?"
        + " and SUP_EMP_CODE= ? and ND= ?" + " )";
      Delta data = DBHelper.queryToDelta(con, sql, val.toArray());
      if (!data.isEmpty()) {
        TableData td = (TableData) data.get(0);
        if (td.getFieldValue("ID").equals(id)) {
          // 是更新操作
          List val_1 = new ArrayList();
          val_1.add(SUP_CODE.equals(junCoCode) ? COMMON_CODE : junCoCode);
          val_1.add(junOrgCode);
          val_1.add(junPosiCode);
          val_1.add(junEmpCode);
          val_1.add(condition);
          val_1.add((SUP_CODE.equals(junCoCode) ? SUP_CODE : supCoCode));
          val_1.add(supOrgCode);
          val_1.add(supPosiCode);
          val_1.add(supEmpCode);
          val_1.add(this.name);
          val_1.add(desc);
          val_1.add(new Integer(priority));
          val_1.add(new Integer(Integer.parseInt(nd)));
          val_1.add(id);
          sql = " UPDATE AS_WF_BUSINESS_SUPERIOR set JUN_CO_CODE= ?"
            + ", JUN_ORG_CODE= ? ,JUN_POSI_CODE= ?"
            + ", JUN_EMP_CODE= ?,SUP_CONDITION= ?" + ", SUP_CO_CODE= ?"
            + ", SUP_ORG_CODE= ?,SUP_POSI_CODE= ?"
            + ", SUP_EMP_CODE= ?,PROJECT_NAME= ?"
            + ", DESCRIPTION= ?, PRIORITY= ?, ND= ?" + " WHERE ID= ?";
          DBHelper.executeUpdate(con, sql, val_1.toArray());
        } else {
          // 存在重复记录
          throw new RuntimeException("已存在重复的定义。");
        }
      } else {
        List val_2 = new ArrayList();
        val_2.add(Pub.getUID());
        val_2.add((SUP_CODE.equals(junCoCode) ? COMMON_CODE : junCoCode));
        val_2.add(junOrgCode);
        val_2.add(junPosiCode);
        val_2.add(junEmpCode);
        val_2.add(condition);
        val_2.add((SUP_CODE.equals(junCoCode) ? SUP_CODE : supCoCode));
        val_2.add(supOrgCode);
        val_2.add(supPosiCode);
        val_2.add(supEmpCode);
        val_2.add(this.name);
        val_2.add(desc);
        val_2.add(new Integer(priority));
        val_2.add(new Integer(Integer.parseInt(nd)));
        sql = "INSERT INTO AS_WF_BUSINESS_SUPERIOR (ID,JUN_CO_CODE,JUN_ORG_CODE,JUN_POSI_CODE,"
          + "JUN_EMP_CODE,SUP_CONDITION,SUP_CO_CODE,SUP_ORG_CODE,SUP_POSI_CODE,"
          + "SUP_EMP_CODE,PROJECT_NAME, DESCRIPTION, PRIORITY,ND) values ("
          + "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";
        DBHelper.executeUpdate(con, sql, val_2.toArray());
      }
    } catch (SQLException se) {
      log.error(se);
      throw new SQLException(se.getMessage());
    } catch (RuntimeException re) {
      throw new RuntimeException(re.getMessage());
    } finally {
      DBHelper.closeConnection(con);
    }
  }

  /**
   * 删除纪录
   *
   * @param id
   */
  public void delete(String id) throws SQLException {
    Connection con = null;
    try {
      con = DAOFactory.getInstance().getConnection();
      String sqlText = "delete from AS_WF_BUSINESS_SUPERIOR where ID=?";
      DBHelper.executeUpdate(con, sqlText, new String[]{id});
    } catch (Exception e) {
      log.error(e);
      throw new SQLException(e.getMessage());
    } finally {
      DBHelper.closeConnection(con);
    }
  }

  /**
   * 查询库表中所有记录
   *
   * @return
   * @throws SQLException
   */
  public static List doSelect(String nd, boolean isId) throws BusinessException {
    Connection con = null;
    List result = new ArrayList();
    try {
      con = DAOFactory.getInstance().getConnection();
      String sqlText = "select * from AS_WF_BUSINESS_SUPERIOR where ND=?";
      Delta delta = DBHelper.queryToDelta(con, sqlText, new String[]{nd});
      for (int i = 0; i < delta.size(); i++) {
        TableData data = (TableData) delta.get(i);
        result.add(delta2Object(data));
      }
    } catch (Exception e) {
      log.error(e);
      throw new BusinessException(e.getMessage());
    } finally {
      DBHelper.closeConnection(con);
    }
    return result;
  }

  /**
   * 查询指定ID的记录
   *
   * @param id
   * @return
   * @throws SQLException
   */
  public static BusinessJuniorBean doSelect(String id) throws BusinessException {
    Connection con = null;
    BusinessJuniorBean bj = null;
    try {
      con = DAOFactory.getInstance().getConnection();
      String sqlText = "select * from AS_WF_BUSINESS_SUPERIOR where ID=?";
      Delta delta = DBHelper.queryToDelta(con, sqlText, new String[]{id});
      if (delta.size() > 0) {
        TableData data = (TableData) delta.get(0);
        bj = delta2Object(data);
        bj.setId(id);
      }
    } catch (Exception e) {
      log.error(e);
      throw new BusinessException(e.getMessage());
    } finally {
      DBHelper.closeConnection(con);
    }
    return bj;
  }

  /**
   * 根据条件过滤结果
   *
   * @param junCoCode
   *          下级单位
   * @param junOrgCode
   *          下级组织
   * @param junPosiCode
   *          下级职位
   * @param junior
   *          下级
   * @return
   */
  public static List doFilter(String junCoCode, String nd)
    throws BusinessException {
    return doFilter(junCoCode, null, null, null, nd);
  }

  public static List doFilter(String junCoCode, String junOrgCode, String nd)
    throws BusinessException {
    return doFilter(junCoCode, junOrgCode, null, null, nd);
  }

  public static List doFilter(String junCoCode, String junOrgCode,
    String junPosiCode, String nd) throws BusinessException {
    return doFilter(junCoCode, junOrgCode, junPosiCode, null, nd);
  }

  public static List doFilter(String junCoCode, String junOrgCode,
    String junPosiCode, String junior, String nd) throws BusinessException {

    List all = doSelect(nd, false);
    List result = new ArrayList();
    for (int i = 0; i < all.size(); i++) {
      BusinessJuniorBean bj = (BusinessJuniorBean) all.get(i);
      String[] jc = bj.getJuniorComs();
      String[] jo = bj.getJuniorOrgs();
      String[] jp = bj.getJuniorPositions();
      String[] ju = bj.getJuniorUsers();
      /*
       * if(contains(jc,junCoCode)) { if(StringTools.isEmptyString(junOrgCode))
       * result.add(bj); else if(contains(jo, junOrgCode)) {
       * if(StringTools.isEmptyString(junPosiCode)) result.add(bj); else
       * if(contains(jp, junPosiCode)) { if(StringTools.isEmptyString(junior)) {
       * log.info("doFilter---- junior is empty, why?"); }else if(contains(ju,
       * junior)) result.add(bj); } } }
       */

      if (contains(jc, junCoCode) && contains(jo, junOrgCode)
        && contains(jp, junPosiCode) && contains(ju, junior))
        result.add(bj);
    }
    return result;
  }

  /**
   * 根据查询结果集构造BusinessJuniorBean类
   *
   * @param data
   * @return
   */
  private static BusinessJuniorBean delta2Object(TableData data) {
    String name = data.getFieldValue("PROJECT_NAME");
    String desc = data.getFieldValue("DESCRIPTION");
    String pri = data.getFieldValue("PRIORITY");
    String junCoCode = data.getFieldValue("JUN_CO_CODE");
    String junOrgCode = data.getFieldValue("JUN_ORG_CODE");
    String junPosiCode = data.getFieldValue("JUN_POSI_CODE");
    String junEmpCode = data.getFieldValue("JUN_EMP_CODE");
    String supCoCode = data.getFieldValue("SUP_CO_CODE");
    String supOrgCode = data.getFieldValue("SUP_ORG_CODE");
    String supPosiCode = data.getFieldValue("SUP_POSI_CODE");
    String supEmpCode = data.getFieldValue("SUP_EMP_CODE");
    String conditionsStr = data.getFieldValue("SUP_CONDITION");
    String nd = data.getFieldValue("ND");
    List compoCodition = getCompoConditions(conditionsStr);
    List sysCodition = getSysConditions(conditionsStr);
    BusinessJuniorBean bj = new BusinessJuniorBean(name, desc,
      Integer.parseInt(pri), supCoCode, supOrgCode, supPosiCode, supEmpCode,
      junCoCode, junOrgCode, junPosiCode, junEmpCode, compoCodition, sysCodition, nd);
    bj.setId(data.getFieldValue("ID"));
    return bj;
  }

  /**
   * 计算业务上级
   *
   * @param junCoCode
   * @param junOrgCode
   * @param junPosiCOde
   * @param junior
   * @return
   * @throws SQLException 
   */
  public Set fallbackSuperior(String junCoCode, String junOrgCode,
    String junPosiCode, String junior, String nd) throws RuntimeException{
    HashSet userList = new HashSet();
    String table = "AS_EMP_POSITION,AS_EMP";
    String clause = "AS_EMP.EMP_CODE=AS_EMP_POSITION.EMP_CODE";
    String whereClause = "";
    List val = new ArrayList();
    // 单位限制条件
    for (int i = 0; i < selfComs.length; i++) {
      String supCom = selfComs[i];
      if (COMMON_CODE.equals(supCom)) {
        // 全部单位
      } else if (SUP_CODE.equals(supCom)) {
        // junCoCode的组织上级
        table += ",AS_COMPANY";
        whereClause += " OR AS_COMPANY.CO_CODE= ?"
          + " AND AS_EMP_POSITION.CO_CODE=AS_COMPANY.PARENT_CO_CODE";
        val.add(junCoCode);
      } else if (SAME_CODE.equals(supCom)) {
        whereClause += " OR AS_EMP_POSITION.CO_CODE= ?";
        val.add(junCoCode);
      } else {
        whereClause += " OR AS_EMP_POSITION.CO_CODE= ?";
        val.add(supCom);
      }
    }
    if (whereClause.length() != 0) {
      clause += " AND (" + whereClause.substring(" OR".length()) + ")";
      whereClause = "";
    }
    // 组织限制条件
    for (int i = 0; i < selfOrgs.length; i++) {
      String supOrg = selfOrgs[i];
      if (COMMON_CODE.equals(supOrg)) {
        // 全部机构
      } else if (SUP_CODE.equals(supOrg)) {
        // junOrgCode的组织上级
        table += ",AS_ORG";
        whereClause += " OR AS_ORG.ORG_CODE= ?"
          + "' AND AS_EMP_POSITION.ORG_CODE=AS_ORG.PARENT_ORG_CODE";
        val.add(junOrgCode);
      } else if (SAME_CODE.equals(supOrg)) {
        whereClause += " OR AS_EMP_POSITION.ORG_CODE= ?";
        val.add(junOrgCode);
      } else {
        whereClause += " OR AS_EMP_POSITION.ORG_CODE= ?";
        val.add(supOrg);
      }
    }
    if (whereClause.length() != 0) {
      clause += " AND (" + whereClause.substring(" OR".length()) + ")";
      whereClause = "";
    }
    // 职位限制条件
    for (int i = 0; i < selfPositions.length; i++) {
      String supPosi = selfPositions[i];
      if (COMMON_CODE.equals(supPosi)) {
        // 全部机构
      } else if (SUP_CODE.equals(supPosi)) {
        // junPosiCode的组织上级
        table += ",AS_POSITION";
        whereClause += " OR AS_POSITON.POSI_CODE= ?"
          + "' AND AS_EMP_POSITION.POSI_CODE=AS_POSI.PARENT_POSI_CODE";
        val.add(junPosiCode);
      } else if (SAME_CODE.equals(supPosi)) {
        whereClause += " OR AS_EMP_POSITION.POSI_CODE= ?";
        val.add(junPosiCode);
      } else {
        whereClause += " OR AS_EMP_POSITION.POSI_CODE= ?";
        val.add(supPosi);
      }
    }
    if (whereClause.length() != 0) {
      clause += " AND (" + whereClause.substring(" OR".length()) + ")";
      whereClause = "";
    }
    // 人员限制条件
    for (int i = 0; i < selfUsers.length; i++) {
      String supUser = selfUsers[i];
      if (COMMON_CODE.equals(supUser)) {
        // 全部人员
      } else if (SUP_CODE.equals(supUser)) {
        // 人员没有上级 do nothing
      } else if (SAME_CODE.equals(supUser)) {
        // 也不存在这种情况
      } else {
        whereClause += " OR AS_EMP_POSITION.EMP_CODE= ?";
        val.add(supUser);
      }
    }
    if (whereClause.length() != 0) {
      clause += " AND (" + whereClause.substring(" OR".length()) + ")";
      whereClause = "";
    }
    String sql = "select AS_EMP.USER_ID from " + table + " where " + clause;
    log.debug(sql);
    try {

      List userlist = (List)DBHelper.queryToList(sql, val.toArray());
      for(int i=0; i< userlist.size(); i++)
      {
    	Object[] data = (Object[])userlist.get(i);
        userList.add(data[0]); 
      }
    } catch (SQLException se) {
      throw new RuntimeException("查找业务上级出错，sql语句为" + sql + "," + se.getMessage());
    }
    return userList;
  }

  /**
   * 构造条件字串
   *
   * @return
   */
  protected String populateConditions() {
    String cons = "";
    /** 构造部件条件字符串* */
    for (int i = 0; i < compoConditions.size(); i++) {
      String con = compoConditions.get(i).toString();
      if (StringTools.isEmptyString(con))
        continue;
      cons += compoConditions.get(i) + AND;
    }
    /** 构造系统变量条件* */
    for (int i = 0; i < sysConditions.size(); i++) {
      cons += sysConditions.get(i) + AND;
    }
    if (cons.length() > AND.length()) {
      cons = cons.substring(0, cons.length() - AND.length());
    }
    return cons;
  }

  /*
   * （非 Javadoc）
   *
   * @see com.anyi.erp.pub.TreeSelect#getComsTreeSql()
   */
  protected String getComsTreeSql() {
    String sql = "SELECT \'" + SUP_CODE
      + "\' AS CODE, \'\' AS P_CODE, \'运行时下级\' AS NAME from AS_COMPANY"
      + " UNION SELECT \'" + SAME_CODE
      + "\' AS CODE, \'\' AS P_CODE, \'运行时同级\' AS NAME from AS_COMPANY "
      + " UNION (SELECT CO_CODE AS CODE, PARENT_CO_CODE AS P_CODE, CO_NAME AS NAME"
      + " FROM as_company) ORDER BY CODE ASC";
    return sql;
  }

  /*
   * （非 Javadoc）
   * @deprecated 不推荐使用
   * @see com.anyi.erp.pub.TreeSelect#getOrgsTreeSql(java.lang.String)
   */
  protected String getOrgsTreeSql(String comsCollection) {
    String cons = clearSysCode(comsCollection);
    String sql = "select distinct ORG_CODE AS CODE, PARENT_ORG_CODE AS P_CODE, ORG_NAME AS NAME from as_org";
    if (cons != null) {
      sql = "select distinct ORG_CODE AS CODE, PARENT_ORG_CODE AS P_CODE, ORG_NAME AS NAME from as_org where CO_CODE in ("
        + cons + ")";
    }
    sql += " UNION SELECT \'" + SUP_CODE
      + "\' AS CODE, \'\' AS P_CODE, \'运行时下级\' AS NAME"
      + " from AS_ORG UNION SELECT \'" + SAME_CODE
      + "\' AS CODE, \'\' AS P_CODE, \'运行时同级\' AS NAME from AS_ORG";
    return sql;
  }

  /*
   * （非 Javadoc）
   * @deprecated 不推荐使用
   * @see com.anyi.erp.pub.TreeSelect#getPosisTreeSql(java.lang.String,
   *      java.lang.String)
   */
  protected String getPosisTreeSql(String comsCollection, String orgsCollection) {
    String sql = "SELECT distinct AS_POSITION.POSI_CODE AS CODE, '' AS P_CODE,"
      + " POSI_NAME AS NAME FROM AS_ORG_POSITION,AS_POSITION WHERE "
      + "AS_POSITION.POSI_CODE=AS_ORG_POSITION.POSI_CODE";
    String ccons = clearSysCode(comsCollection);
    // 限定单位
    if (ccons != null) {
      sql += " and CO_CODE in (" + ccons + ")";
    }
    String ocons = clearSysCode(orgsCollection);
    // 限定机构
    if (ocons != null) {
      sql += " and ORG_CODE in (" + ocons + ")";
    }
    return sql;
  }

  /*
   * （非 Javadoc）
   *
   * @see com.anyi.erp.pub.TreeSelect#getUsersTreeSql(java.lang.String,
   *      java.lang.String, java.lang.String)
   */
  protected String getUsersTreeSql(String comsCollection, String orgsCollection,
    String posisCollection, String nd) {
    String ccons = clearSysCode(comsCollection);
    String sql = "SELECT DISTINCT AS_EMP_POSITION.EMP_CODE AS CODE, "
      + "'' AS P_CODE, AS_EMP.EMP_NAME AS NAME FROM AS_EMP_POSITION ,"
      + "AS_EMP where AS_EMP_POSITION.EMP_CODE = AS_EMP.EMP_CODE";
    // 限定单位
    if (ccons != null) {
      sql += " and CO_CODE in (" + ccons + ")";
    }
    String ocons = clearSysCode(orgsCollection);
    // 限定机构
    if (ocons != null) {
      sql += " and ORG_CODE in (" + ocons + ")";
    }
    String pcons = clearSysCode(posisCollection);
    // 限定职位
    if (pcons != null) {
      sql += " and POSI_CODE in (" + pcons + ")";
    }
    sql += " and ND='" + nd + "'";
    return sql;
  }

  /**
   * 获取机构树状结构
   */
  public String getSuperOrgsTree(String svNd) {
    String tree = "";
    try {
      tree = super.buildOrgsTree(selfComs, svNd);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return tree;
  }

  /**
   * 获取下级机构树状结构
   */
  public String getJuniorOrgsTree(String svNd) {
    String tree = "";
    try {
      tree = super.buildOrgsTree(juniorComs, svNd);
    } catch (Exception se) {
      log.error(se);
      throw new RuntimeException(se);
    }
    return tree;
  }

  /**
   * 获取下级职位树状结构
   */
  public String getJuniorPosisTree(String svNd) {
    String tree = "";
    try {
      tree = super.buildPosisTree(juniorComs, juniorOrgs, svNd);
    } catch (Exception se) {
      log.error(se);
      throw new RuntimeException(se);
    }
    return tree;
  }

  /**
   * 获取下级职位树状结构
   */
  public String getSuperPosisTree(String svNd) {
    String tree = "";
    try {
      tree = super.buildPosisTree(selfComs, selfOrgs, svNd);
    } catch (Exception se) {
      log.error(se);
      throw new RuntimeException(se);
    }
    return tree;
  }

  /**
   * 获取本级人员
   */
  public String getSuperUsersTree(String svNd) {
    String tree = "";
    try {
      tree = super.buildUsersTree(selfComs, selfOrgs, selfPositions, svNd);
    } catch (Exception se) {
      log.error(se);
      throw new RuntimeException(se);
    }
    return tree;
  }

  /**
   * 获取下级人员
   */
  public String getJuniorUsersTree(String svNd) {
    String tree = "";
    try {
      tree = super.buildUsersTree(juniorComs, juniorOrgs, juniorPositions, svNd);
    } catch (Exception se) {
      log.error(se);
      throw new RuntimeException(se);
    }
    return tree;
  }

  /**
   * @return   返回 juniorComs。
   * @uml.property   name="juniorComs"
   */
  public String[] getJuniorComs() {
    return juniorComs;
  }

  /**
   * @param juniorComs   要设置的 juniorComs。
   * @uml.property   name="juniorComs"
   */
  public void setJuniorComs(String[] juniorComs) {
    this.juniorComs = convertStringArray(juniorComs);
  }

  /**
   * @return   返回 juniorOrgs。
   * @uml.property   name="juniorOrgs"
   */
  public String[] getJuniorOrgs() {
    return juniorOrgs;
  }

  /**
   * @param juniorOrgs   要设置的 juniorOrgs。
   * @uml.property   name="juniorOrgs"
   */
  public void setJuniorOrgs(String[] juniorOrgs) {
    this.juniorOrgs = convertStringArray(juniorOrgs);
  }

  /**
   * @return   返回 id。
   * @uml.property   name="id"
   */
  public String getId() {
    return id;
  }

  /**
   * @param id   要设置的 id。
   * @uml.property   name="id"
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * @return   返回 juniorPositions。
   * @uml.property   name="juniorPositions"
   */
  public String[] getJuniorPositions() {
    return juniorPositions;
  }

  /**
   * @param juniorPositions   要设置的 juniorPositions。
   * @uml.property   name="juniorPositions"
   */
  public void setJuniorPositions(String[] juniorPositions) {
    this.juniorPositions = convertStringArray(juniorPositions);
  }

  /**
   * @return   返回 juniorUsers。
   * @uml.property   name="juniorUsers"
   */
  public String[] getJuniorUsers() {
    return juniorUsers;
  }

  /**
   * @param juniorUsers   要设置的 juniorUsers。
   * @uml.property   name="juniorUsers"
   */
  public void setJuniorUsers(String[] juniorUsers) {
    this.juniorUsers = convertStringArray(juniorUsers);
  }

  /**
   * @return 返回 selfComs。
   */
  public String[] getSuperComs() {
    return selfComs;
  }

  /**
   * @param selfComs
   *          要设置的 selfComs。
   */
  public void setSuperComs(String[] selfComs) {
    this.selfComs = convertStringArray(selfComs);
  }

  /**
   * @return 返回 selfOrgs。
   */
  public String[] getSuperOrgs() {
    return selfOrgs;
  }

  /**
   * @param selfOrgs
   *          要设置的 selfOrgs。
   */
  public void setSuperOrgs(String[] selfOrgs) {
    this.selfOrgs = convertStringArray(selfOrgs);
  }

  /**
   * @return 返回 selfPositions。
   */
  public String[] getSuperPositions() {
    return selfPositions;
  }

  /**
   * @param selfPositions
   *          要设置的 selfPositions。
   */
  public void setSuperPositions(String[] selfPositions) {
    this.selfPositions = convertStringArray(selfPositions);
  }

  /**
   * @return 返回 selfUsers。
   */
  public String[] getSuperUsers() {

    return selfUsers;
  }

  /**
   * @param sa
   * @return
   *
   * cuiliguo 2006.06.15 转换字符串数组字符集
   */
  private String[] convertStringArray(String[] sa) {
    String[] result = new String[sa.length];
    for (int i = 0; i < sa.length; i++) {
      result[i] = convertString(sa[i]);
    }
    return result;
  }

  /**
   * @param s
   * @return
   *
   * cuiliguo 2006.06.15 转换字符串字符集
   */
  private String convertString(String s) {
    String result = s;
    return result;
  }

  /**
   * @param selfUsers
   *          要设置的 selfUsers。
   */
  public void setSuperUsers(String[] selfUsers) {
    // cuiliguo 2006.06.15 参数中可能包含汉字，需要转换字符集。
    this.selfUsers = convertStringArray(selfUsers);
  }

  /**
   * @return 返回 comsTreeStr。
   */
  public String getComsTreeStr(String svNd) {
    if (comsTreeStr == null)
      try {
        buildComsTree(svNd);
      } catch (Exception e) {
        log.error(e);
        throw new RuntimeException(e);
      }
    return comsTreeStr;
  }

  /**
   * @param comsTreeStr
   *          要设置的 comsTreeStr。
   */
  public void setComsTreeStr(String comsTreeStr) {
    this.comsTreeStr = comsTreeStr;
  }

  /**
   * @return 返回 compos。
   */
  public Map getCompos() {
    if (compos.isEmpty()) {
      Connection con = null;
      Statement stmt = null;
      ResultSet rs = null;
      try {
        con = DAOFactory.getInstance().getConnection();
        stmt = con.createStatement();
        String sql = "select distinct COMPO_ID, RES_NA from AS_COMPO, AS_LANG_TRANS where COMPO_ID=RES_ID and upper(template_is_used)='Y' order by compo_id ";
        rs = stmt.executeQuery(sql);
        while (rs.next()) {
          // 过滤掉系统部件和工作流部件
          if (rs.getString(1).startsWith("AS_") || rs.getString(1).startsWith("WF_"))
            continue;
          compos.put(rs.getString(1), rs.getString(2));
        }
      } catch (SQLException se) {
        log.error(se);
        throw new RuntimeException(se);
      } finally {
        DBHelper.closeConnection(con, stmt, rs);
      }
    }
    return compos;
  }

  /**
   * @param compos
   *          要设置的 compos。
   */
  public void setCompos(Map compos) {
    this.compos = compos;
  }

  /**
   * @return   返回 compoConditions。
   * @uml.property   name="compoConditions"
   */
  public List getCompoConditions() {
    return compoConditions;
  }

  /**
   * @return   返回 nd。
   * @uml.property   name="nd"
   */
  public String getNd() {
    return nd;
  }

  /**
   * @param nd   要设置的 nd。
   * @uml.property   name="nd"
   */
  public void setNd(String nd) {
    this.nd = nd;
  }

  /**
   * @return   返回 desc。
   * @uml.property   name="desc"
   */
  public String getDesc() {
    return desc;
  }

  /**
   * @param desc   要设置的 desc。
   * @uml.property   name="desc"
   */
  public void setDesc(String desc) {
    this.desc = desc;
  }

  /**
   * @return   返回 name。
   * @uml.property   name="name"
   */
  public String getName() {
    return name;
  }

  /**
   * @param name   要设置的 name。
   * @uml.property   name="name"
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * @return   返回 priority。
   * @uml.property   name="priority"
   */
  public int getPriority() {
    return priority;
  }

  /**
   * @param priority
   *          要设置的 priority。
   */
  public void setPriority(String priority) {
    int pri = 0;
    pri = Integer.parseInt(priority);
    this.priority = pri;
  }

  /**
   * 从条件字串中抽取出与部件相关的条件
   *
   * @param cons
   * @return
   */
  public static List getCompoConditions(String cons) {
    List cs = new ArrayList();
    if (cons == null)
      return new ArrayList();
    String[] conArr = cons.split(AND);
    for (int i = 0; i < conArr.length; i++) {
      String con = conArr[i];
      try {
        BusinessJuniorExp exp = new BusinessJuniorExp(con);
        // 如果是部件表达式
        if (exp.isCompoExpression()) {
          String compoAttribute = exp.getPara1();
          int idx = compoAttribute.indexOf(".");
          BusinessJuniorBean b = new BusinessJuniorBean();
          if (idx > 0) {
            CompoCondition cc = b.new CompoCondition(compoAttribute
              .substring(0, idx), compoAttribute.substring(idx + 1),
              exp.getSymbol(), exp.getPara2());
            cs.add(cc);
          } else {
        	  //只是部件名
        	  if (compoAttribute.equals("@COMPOID")) {
        		  CompoCondition cc = b.new CompoCondition(compoAttribute, null, exp.getSymbol(), exp.getPara2());
        		  cs.add(cc);
        	  }
          }
        }
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
    return cs;
  }

  /**
   * @param compoConditions
   *          要设置的 compoConditions。
   */
  public void setCompoConditions(String[] compoCode, String[] fieldCode,
    String[] symbol, String[] val) {
    compoConditions = new ArrayList();
    if (compoCode == null || symbol == null || val == null)
      return;
    if (fieldCode == null)
      fieldCode = new String[] { "" };
    for (int i = 0; i < compoCode.length; i++) {
      String fc = (fieldCode.length <= i) ? "" : fieldCode[i];

      CompoCondition cc = new CompoCondition(compoCode[i], fc, symbol[i], val[i]);
      compoConditions.add(cc);
    }
  }

  /**
   * @param compoConditions
   *          要设置的 compoConditions。
   */
  public void addCompoConditions(String compoCode, String fieldCode, String symbol,
    String val) {
    if (compoCode == null || symbol == null || val == null)
      return;
    if (fieldCode == null)
      fieldCode = "";
    CompoCondition cc = new CompoCondition(compoCode, fieldCode, symbol, val);
    compoConditions.add(cc);
  }

  /**
   * 删除指定索引的条件
   *
   * @param index
   */
  public void removeCompoCondition(int index) {
    if (compoConditions != null && compoConditions.size() > index) {
      compoConditions.remove(index);
    }
  }

  /**
   * 删除最后一条
   */
  public void removeCompoCondition() {
    if (compoConditions != null && !compoConditions.isEmpty())
      compoConditions.remove(compoConditions.size() - 1);
  }

  /**
   * @return   返回 sysConditions。
   * @uml.property   name="sysConditions"
   */
  public List getSysConditions() {
    return sysConditions;
  }

  private static List getSysConditions(String cons) {
    // TCJLODO: 根据条件字串抽取工作流系统表达式
    return new ArrayList();
  }

  /**
   * @param sysConditions
   *          要设置的 sysConditions。
   */
  public void setSysConditions(String[] variableCode, String[] symbol, String[] vals) {
    sysConditions = new ArrayList();
    if (variableCode == null || symbol == null || vals == null)
      return;
    for (int i = 0; i < variableCode.length; i++) {
      SystemCondition sc = new SystemCondition(variableCode[i], symbol[i], vals[i]);
      sysConditions.add(sc);
    }
  }

  /**
   * 删除指定索引的条件
   *
   * @param index
   */
  public void removeSysCondition(int index) {
    if (sysConditions != null && sysConditions.size() > index) {
      sysConditions.remove(index);
    }
  }

  /**
   * 删除最后一条
   */
  public void removeSysCondition() {
    if (sysConditions != null && !sysConditions.isEmpty())
      sysConditions.remove(sysConditions.size() - 1);
  }

  /**
   * 把字符串数组转换成以逗号分割的字符串,同时去除＃和空字符串
   *
   * @param arr
   * @return
   */
  public static String array2String(String[] arr) {

    StringBuffer ret = new StringBuffer();
    if (arr != null) {
      for (int i = 0; i < arr.length; i++) {
        if ("".equals(arr[i]))
          continue;
        if (arr.length > 1 && COMMON_CODE.equals(arr[i]))
          continue;
        ret.append(arr[i].trim()).append(",");
      }
      if (ret.length() > 0) {
        return ret.substring(0, ret.length() - 1);
      }
    }
    return ret.toString();
  }

  private String clearSysCode(String str) {
    if (str == null)
      return null;
    String[] arr = str.split(",");
    StringBuffer ret = new StringBuffer();
    for (int i = 0; i < arr.length; i++) {
      if (("'" + COMMON_CODE + "'").equals(arr[i]))
        continue;
      if (("'" + SUP_CODE + "'").equals(arr[i]))
        continue;
      if (("'" + SAME_CODE + "'").equals(arr[i]))
        continue;
      ret.append(arr[i]).append(",");
    }
    if (ret.length() > 0) {
      return ret.substring(0, ret.length() - 1);
    }
    return null;
  }

  public static boolean contains(String[] arr, String str) {
    if (arr == null)
      return false;
    for (int i = 0; i < arr.length; i++) {
      // 如果是普适代码,说明包括所有
      if (COMMON_CODE.equals(arr[i]))
        return true;
      if (arr[i].equals(str))
        return true;
      if ("".equals(str))
        return true;
    }
    return false;
  }

  public static boolean containsValue(String[] arr, String str) {
    if (arr == null)
      return false;
    for (int i = 0; i < arr.length; i++) {
      if (arr[i].equals(str))
        return true;
    }
    return false;
  }

  /**
   * @author   leidaohong
   */
  public class CompoCondition {
    private String compoCode;

    private String compoName;

    private String fieldCode;

    private String fieldName;

    private String symbol;

    private String val = "";

    /**
     * @param compoCode
     * @param compoName
     * @param fieldCode
     * @param fieldName
     * @param symbol
     * @param val
     */
    public CompoCondition(String compoCode, String compoName, String fieldCode,
      String fieldName, String symbol, String val) {
      super();
      this.compoCode = compoCode;
      this.compoName = compoName;
      this.fieldCode = fieldCode;
      this.fieldName = fieldName;
      this.symbol = symbol;
      this.val = convertString(val);
    }

    /**
     * @param compoCode
     * @param fieldCode
     * @param symbol
     * @param val
     */
    public CompoCondition(String compoCode, String fieldCode, String symbol,
      String val) {
      super();
      this.compoCode = compoCode;
      this.fieldCode = fieldCode;
      this.symbol = symbol;
      this.val = convertString(val);
    }

    public String toString() {
      if (StringTools.isEmptyString(compoCode))
        return "";
      String ret = symbol.concat("(\"").concat(compoCode);
      if (!StringTools.isEmptyString(fieldCode))
        ret = ret.concat(".").concat(fieldCode);
      ret = ret.concat("\",\"").concat(val).concat("\")");
      return ret;
    }

    /**
     * @return   返回 compoCode。
     * @uml.property   name="compoCode"
     */
    public String getCompoCode() {
      return compoCode;
    }

    /**
     * @param compoCode   要设置的 compoCode。
     * @uml.property   name="compoCode"
     */
    public void setCompoCode(String compoCode) {
      this.compoCode = compoCode;
    }

    /**
     * @return   返回 compoName。
     * @uml.property   name="compoName"
     */
    public String getCompoName() {
      return compoName;
    }

    /**
     * @param compoName   要设置的 compoName。
     * @uml.property   name="compoName"
     */
    public void setCompoName(String compoName) {
      this.compoName = compoName;
    }

    /**
     * @return   返回 fieldCode。
     * @uml.property   name="fieldCode"
     */
    public String getFieldCode() {
      return fieldCode;
    }

    /**
     * @param fieldCode   要设置的 fieldCode。
     * @uml.property   name="fieldCode"
     */
    public void setFieldCode(String fieldCode) {
      this.fieldCode = fieldCode;
    }

    /**
     * @return   返回 fieldName。
     * @uml.property   name="fieldName"
     */
    public String getFieldName() {
      return fieldName;
    }

    /**
     * @param fieldName   要设置的 fieldName。
     * @uml.property   name="fieldName"
     */
    public void setFieldName(String fieldName) {
      this.fieldName = fieldName;
    }

    /**
     * @return   返回 symbol。
     * @uml.property   name="symbol"
     */
    public String getSymbol() {
      return symbol;
    }

    /**
     * @param symbol   要设置的 symbol。
     * @uml.property   name="symbol"
     */
    public void setSymbol(String symbol) {
      this.symbol = symbol;
    }

    /**
     * @return   返回 val。
     * @uml.property   name="val"
     */
    public String getVal() {
      return val;
    }

    /**
     * @param val   要设置的 val。
     * @uml.property   name="val"
     */
    public void setVal(String val) {
      this.val = val;
    }

  }

  /**
   * @author   leidaohong
   */
  public class SystemCondition {
    private String variableCode;

    private String variableName;

    private String symbol;

    private String val = "";

    /**
     * @param variableCode
     * @param variableName
     * @param symbol
     * @param val
     */
    public SystemCondition(String variableCode, String variableName, String symbol,
      String val) {
      super();
      this.variableCode = variableCode;
      this.variableName = variableName;
      this.symbol = symbol;
      this.val = convertString(val);
    }

    /**
     * @param variableCode
     * @param symbol
     * @param val
     */
    public SystemCondition(String variableCode, String symbol, String val) {
      super();
      this.variableCode = variableCode;
      this.symbol = symbol;
      this.val = convertString(val);
    }

    public String toString() {
      return symbol.concat("(\"").concat(variableCode).concat("\",\"").concat(val)
        .concat("\")");
    }

    /**
     * @return   返回 symbol。
     * @uml.property   name="symbol"
     */
    public String getSymbol() {
      return symbol;
    }

    /**
     * @param symbol   要设置的 symbol。
     * @uml.property   name="symbol"
     */
    public void setSymbol(String symbol) {
      this.symbol = symbol;
    }

    /**
     * @return   返回 val。
     * @uml.property   name="val"
     */
    public String getVal() {
      return val;
    }

    /**
     * @param val   要设置的 val。
     * @uml.property   name="val"
     */
    public void setVal(String val) {
      this.val = val;
    }

    /**
     * @return   返回 variableCode。
     * @uml.property   name="variableCode"
     */
    public String getVariableCode() {
      return variableCode;
    }

    /**
     * @param variableCode   要设置的 variableCode。
     * @uml.property   name="variableCode"
     */
    public void setVariableCode(String variableCode) {
      this.variableCode = variableCode;
    }

    /**
     * @return   返回 variableName。
     * @uml.property   name="variableName"
     */
    public String getVariableName() {
      return variableName;
    }

    /**
     * @param variableName   要设置的 variableName。
     * @uml.property   name="variableName"
     */
    public void setVariableName(String variableName) {
      this.variableName = variableName;
    }
  }
}
