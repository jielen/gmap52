/**
 * Copyright ? 2004 BeiJing UFGOV Software Co. Ltd.
 * All right reserved.
 * Jun 29, 2005 Powered By chihongfeng
 */
package com.anyi.gp.pub;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.anyi.gp.BusinessException;
import com.anyi.gp.Delta;
import com.anyi.gp.TableData;
import com.anyi.gp.taglib.components.Page;
import com.anyi.gp.util.StringTools;

/**
 * @author   
 */
public class TreeSelect {

  /*单位树结构字串*/
  protected String comsTreeStr = null;

  protected Map companies;

  protected Map organizations;

  protected Map positions;

  protected Map users;

  protected Map compos;

  /*单位设置*/
  private String[] comCollections;

  /*组织/机构设置*/
  private String[] orgCollections;

  /*职位*/
  private String[] positionCollections;

  /*人员设置*/
  private String[] userCollections;

  public static String COMMON_CODE = "#"; //表示对单位或组织或职位代码不做限制，全部单位|组织|职位的意思

  /**
   *
   */
  public TreeSelect() {
    companies = new HashMap();
    organizations = new HashMap();
    positions = new HashMap();
    users = new HashMap();
    compos = new HashMap();
  }

  /**
   * 将单位、机构、职位、人员按树状结构显示，并提供checkbox选择
   * @return
   */
  public String createTree(String rootCaption, String svNd) throws BusinessException {
    return createTree(null, null, null, null, rootCaption, svNd);
  }

  public String createTree(String[] companies, String rootCaption, String svNd)
    throws BusinessException {
    return createTree(companies, null, null, null, rootCaption, svNd);
  }

  public String createTree(String[] companies, String[] organizations,
    String rootCaption, String svNd) throws BusinessException {
    return createTree(companies, organizations, null, null, rootCaption, svNd);
  }

  public String createTree(String[] companies, String[] organizations,
    String[] positions, String rootCaption, String svNd) throws BusinessException {
    return createTree(companies, organizations, positions, null, rootCaption, svNd);
  }

  public String createTree(String[] companies, String[] organizations,
    String[] positions, String[] users, String rootCaption, String svNd)
    throws BusinessException {
    TreeViewList treeView = new TreeViewList();
    String tree = "";
    String coms = cropArray2String(companies);
    String orgs = cropArray2String(organizations);
    String posis = cropArray2String(positions);
    String urs = cropArray2String(users);
    List comsparams = new ArrayList();
    List orgsparams = new ArrayList();
    List posisparams = new ArrayList();
    List uersparams = new ArrayList();
    String comSql = getComsTreeSql(coms, svNd, comsparams);
    String orgSql = getOrgsTreeSql(coms, svNd, orgsparams);
    String posiSql = getPosisTreeSql(coms, orgs, svNd, posisparams);
    String userSql = getUsersTreeSql(coms, orgs, posis, urs, uersparams);
    Delta data = createTreeDelta(comSql, comsparams, orgSql, orgsparams, posiSql,
      posisparams, userSql, uersparams);
    tree = treeView.getTreeWithRoot(data, rootCaption);
    return tree;
  }

  /**
   * 获得指定单位代码的子树
   * @param rootCode
   * @return
   */
  public String getTreeByCoCode(String[] coCode, String svNd)
    throws BusinessException {
    Connection conn = null;
    Delta data = null;
    String tree = "";
    try {
      List params = new ArrayList();
      conn = DAOFactory.getInstance().getConnection();
      String sql = getComsTreeSql(cropArray2String(coCode), svNd, params);
      data = DBHelper.queryToDelta(conn, sql, params.toArray());
      reform(data);
      if (data.size() > 0) {
        TableData td = (TableData) data.get(0);
        String nodeCode = td.getFieldValue("CODE");
        tree = getTreeByRoot(nodeCode, svNd);
      }
    } catch (Exception se) {
      throw new BusinessException(se.getMessage());
    } finally {
      DBHelper.closeConnection(conn);
    }
    return tree;
  }

  /**
   * 创建指定节点的子树
   * @param rootCode
   * @return
   */
  public String getTreeByRoot(String rootCode, String svNd) throws BusinessException {
    TreeViewList treeView = new TreeViewList();
    String tree = "";
    List comsparams = new ArrayList();
    List orgsparams = new ArrayList();
    List posisparams = new ArrayList();
    List uersparams = new ArrayList();
    String comSql = getComsTreeSql(svNd, comsparams);
    String orgSql = getOrgsTreeSql(svNd, orgsparams);
    String posiSql = getPosisTreeSql(svNd, posisparams);
    String userSql = getUsersTreeSql(svNd, uersparams);
    Delta data = createTreeDelta(comSql, comsparams, orgSql, orgsparams, posiSql,
      posisparams, userSql, uersparams);
    tree = treeView.getTreeByRoot(data, rootCode);
    return tree;
  }

  /**
   * 创建树状结构数据
   * @param comSql
   * @param orgSql
   * @param posiSql
   * @param userSql
   * @return
   * @throws BusinessException
   */
  private Delta createTreeDelta(String comSql, List comsparams, String orgSql,
    List orgsparams, String posiSql, List posisparams, String userSql,
    List uersparams) throws BusinessException {
    Connection conn = null;
    Delta data = null;
    try {
      conn = DAOFactory.getInstance().getConnection();
      data = DBHelper.queryToDelta(conn, comSql, comsparams.toArray());
      Delta orgsdata = DBHelper.queryToDelta(conn, orgSql, orgsparams.toArray());
      Delta posisdata = DBHelper.queryToDelta(conn, posiSql, posisparams.toArray());
      Delta userdata = DBHelper.queryToDelta(conn, userSql, uersparams.toArray());
      data.addAll(orgsdata);
      data.addAll(posisdata);
      data.addAll(userdata);
      reform(data);
    } finally {
      DBHelper.closeConnection(conn);
    }
    return data;
  }

  /**
   * 为避免重复节点，重整树结构
   * 将几棵分离的树状数据包连接成一棵树
   * @param data
   */
  protected void reform(Delta data) {
    TableData td = null;
    String prefix = "";
    for (Iterator it = data.iterator(); it.hasNext();) {
      td = (TableData) it.next();
      prefix = StringTools.isEmptyString(td.getFieldValue("PREFIX1")) ? "" : td
        .getFieldValue("PREFIX1");
      prefix += StringTools.isEmptyString(td.getFieldValue("PREFIX2")) ? "" : td
        .getFieldValue("PREFIX2");
      prefix += StringTools.isEmptyString(td.getFieldValue("PREFIX3")) ? "" : td
        .getFieldValue("PREFIX3");
      td.setField("CODE", prefix + td.getFieldValue("CODE"));
      if (StringTools.isEmptyString(td.getFieldValue("P_CODE")))
        td.setField("P_CODE", prefix);
      else
        td.setField("P_CODE", prefix + td.getFieldValue("P_CODE"));
    }
  }

  /**
   * 存入缓存数据
   * @param cache
   * @param data
   */
  protected void buildCache(Map cache, Delta rs) {
    for (int i = 0; i < rs.size(); i++) {
      TableData data = (TableData) rs.get(i);
      cache.put(data.getFieldValue("CODE"), data.getFieldValue("NAME"));
    }
  }

  protected void buildCache(Map cache, String sql) throws BusinessException {
    Connection con = null;
    Statement stmt = null;
    ResultSet rs = null;
    try {
      con = DAOFactory.getInstance().getConnection();
      stmt = con.createStatement();
      rs = stmt.executeQuery(sql);
      while (rs.next()) {
        cache.put(rs.getString(1), rs.getString(2));
      }
    } catch (SQLException se) {
      throw new BusinessException(se.getMessage());
    } finally {
      if (rs != null)
        try {
          rs.close();
        } catch (Exception e) {
        }
      if (stmt != null)
        try {
          stmt.close();
        } catch (Exception e) {
        }
      if (con != null)
        try {
          con.close();
        } catch (Exception e) {
        }
    }
  }

  /**
   * 构造单位树状结构
   */
  public String buildComsTree(String svNd) throws BusinessException {
    if (!StringTools.isEmptyString(comsTreeStr))
      return comsTreeStr;
    TreeViewList treeView = new TreeViewList();
    Connection conn = null;
    try {
      List params = new ArrayList();
      conn = DAOFactory.getInstance().getConnection();
      String comsSql = getComsTreeSql(svNd, params);
      Delta data = DBHelper.queryToDelta(conn, comsSql, params.toArray());
      buildCache(companies, data);
      this.comsTreeStr = treeView.getTreeWithRoot(data, "单位");
    } catch (Exception se) {
      throw new BusinessException(se.getMessage());
    } finally {
      DBHelper.closeConnection(conn);
    }
    return comsTreeStr;
  }

  protected String getComsTreeSql(String svNd, List params) {
    return getComsTreeSql(COMMON_CODE, svNd, params);
  }

  protected String getComsTreeSql(String comsCollection, String svNd, List params) {
    String sql = " SELECT CO_CODE AS CODE, PARENT_CO_CODE AS P_CODE, CO_NAME AS NAME, "
      + " '' AS PREFIX1, '' AS PREFIX2,'' PREFIX3, "
      + " 'N' AS IS_CHECKED,'" + Page.LOCAL_RESOURCE_PATH 
      + "style/img/tree/o.png' AS NODE_ICON "
      + " FROM as_company ";
    StringBuffer sb = new StringBuffer(sql);
    if (!(COMMON_CODE).equals(comsCollection) && comsCollection != null) {
      sb.append(" where CO_CODE in (");
      String[] comsList = comsCollection.split(",");
      for (int i = 0; i < comsList.length; i++) {
        sb.append("?, ");
        params.add(comsList[i]);
      }
      sb = new StringBuffer(sb.substring(0, sb.length() - 2));
      sb.append(") and ND = ?");
      params.add(svNd);
    } else {
      sb.append(" where ND = ?");
      params.add(svNd);
    }
    sb.append(" ORDER BY CODE ASC");
    return sb.toString();
  }

  /**
   * 获取机构树状结构
   */
  public String buildOrgsTree(String[] coms, String svNd) throws BusinessException {
    TreeViewList treeView = new TreeViewList();
    Connection conn = null;
    String tree = "";
    try {
      List params = new ArrayList();
      conn = DAOFactory.getInstance().getConnection();
      String cons = cropArray2String(coms);
      String sql = getOrgsTreeSql(cons, svNd, params);
      Delta data = DBHelper.queryToDelta(conn, sql, params.toArray());
      data = filter(data);
      buildCache(organizations, data);
      tree = treeView.getTreeWithRoot(data, "机构");
    } catch (Exception se) {
      throw new BusinessException(se.getMessage());
    } finally {
      DBHelper.closeConnection(conn);
    }
    return tree;
  }

  protected String getOrgsTreeSql(String svNd, List params) {
    return getOrgsTreeSql(COMMON_CODE, svNd, params);
  }

  protected String getOrgsTreeSql(String comsCllection, String svNd, List params) {
    return getOrgsTreeSql(comsCllection, COMMON_CODE, svNd, params);
  }

  protected String getOrgsTreeSql(String comsCllection, String orgsCollection,
    String svNd, List params) {
    String sql = " select distinct ORG_CODE AS CODE, PARENT_ORG_CODE AS P_CODE, ORG_NAME AS NAME, "
      + " CO_CODE AS PREFIX1,'' AS PREFIX2,'' PREFIX3, "
      + " \'N\' AS IS_CHECKED,\'" + Page.LOCAL_RESOURCE_PATH 
      + "style/img/tree/object.png\' AS NODE_ICON from as_org where 1=1 and ND = ? ";
    params.add(svNd);
    if (!(COMMON_CODE).equals(comsCllection) && comsCllection != null) {
      sql += " AND CO_CODE in (";
      String[] coms = comsCllection.split(",");
      for (int i = 0; i < coms.length; i++) {
        sql += "?, ";
        params.add(coms[i]);
      }
      sql = sql.substring(0, sql.length() - 2);
      sql += ")";
    }
    if (!(COMMON_CODE).equals(orgsCollection) && orgsCollection != null) {
      sql += " AND ORG_CODE in (";
      String[] orgs = orgsCollection.split(",");
      for (int i = 0; i < orgs.length; i++) {
        sql += "?, ";
        params.add(orgs[i]);
      }
      sql = sql.substring(0, sql.length() - 2);
      sql += ")";
    }
    return sql;
  }

  /**
   * 过滤树结构结果集，去掉重复的CODE数据
   * @param data
   * @return
   */
  protected Delta filter(Delta data) {
    Set filter = new HashSet();
    List remain = new ArrayList();
    for (Iterator it = data.iterator(); it.hasNext();) {
      TableData td = (TableData) it.next();
      String code = td.getFieldValue("CODE");
      if (filter.contains(code)) {
        continue;
      }
      remain.add(td);
      filter.add(code);
    }
    return (new Delta(remain));
  }

  /**
   * 获取职位树状结构
   */
  public String buildPosisTree(String[] coms, String[] orgs, String svNd)
    throws BusinessException {
    TreeViewList treeView = new TreeViewList();
    Connection conn = null;
    String tree = "";
    try {
      List params = new ArrayList();
      conn = DAOFactory.getInstance().getConnection();
      String ccons = cropArray2String(coms);
      String ocons = cropArray2String(orgs);
      String sql = getPosisTreeSql(ccons, ocons, svNd, params);
      Delta data = DBHelper.queryToDelta(conn, sql, params.toArray());
      data = filter(data);
      buildCache(positions, data);
      tree = treeView.getTreeWithRoot(data, "职位");
    } catch (Exception se) {
      throw new BusinessException(se.getMessage());
    } finally {
      DBHelper.closeConnection(conn);
    }
    return tree;
  }

  /**
   * 构造树结构sql语句
   * @param comsCollection  选定的单位集合
   * @param orgsCollection  选定的机构集合
   * @return
   */
  protected String getPosisTreeSql(String svNd, List params) {
    return getPosisTreeSql(COMMON_CODE, COMMON_CODE, svNd, params);
  }

  protected String getPosisTreeSql(String comsCollection, String svNd, List params) {
    return getPosisTreeSql(comsCollection, COMMON_CODE, svNd, params);
  }

  protected String getPosisTreeSql(String comsCollection, String orgsCollection,
    String svNd, List params) {
    return getPosisTreeSql(comsCollection, orgsCollection, COMMON_CODE, svNd, params);
  }

  protected String getPosisTreeSql(String comsCollection, String orgsCollection,
    String posisCollection, String svNd, List params) {
    String sql = "SELECT distinct AS_POSITION.POSI_CODE AS CODE, '' P_CODE,"
      + "AS_ORG_POSITION.CO_CODE AS PREFIX1,AS_ORG_POSITION.ORG_CODE AS PREFIX2,'' PREFIX3,"
      + " POSI_NAME AS NAME,\'N\' AS IS_CHECKED,\'" + Page.LOCAL_RESOURCE_PATH 
      + "style/img/tree/ou.png\' AS NODE_ICON FROM AS_ORG_POSITION,AS_POSITION WHERE "
      + "AS_POSITION.POSI_CODE=AS_ORG_POSITION.POSI_CODE and ND = ? ";
    params.add(svNd);
    //      限定单位
    if (!(COMMON_CODE).equals(comsCollection) && comsCollection != null) {
      sql += " and AS_ORG_POSITION.CO_CODE in ( ";
      String[] coms = comsCollection.split(",");
      for (int i = 0; i < coms.length; i++) {
        sql += "?, ";
        params.add(coms[i]);
      }
      sql = sql.substring(0, sql.length() - 2);
      sql += ")";
    }
    //        限定机构
    if (!(COMMON_CODE).equals(orgsCollection) && orgsCollection != null) {
      sql += " and AS_ORG_POSITION.ORG_CODE in ( ";
      String[] orgs = orgsCollection.split(",");
      for (int i = 0; i < orgs.length; i++) {
        sql += "?, ";
        params.add(orgs[i]);
      }
      sql = sql.substring(0, sql.length() - 2);
      sql += ")";
    }
    if (!(COMMON_CODE).equals(posisCollection) && posisCollection != null) {
      sql += " and AS_POSITION.POSI_CODE in ( ";
      String[] posis = posisCollection.split(",");
      for (int i = 0; i < posis.length; i++) {
        sql += "?, ";
        params.add(posis[i]);
      }
      sql = sql.substring(0, sql.length() - 2);
      sql += ")";
    }
    return sql;
  }

  /**
   * 获取人员
   */
  public String buildUsersTree(String[] coms, String[] orgs, String[] posis,
    String svNd) throws BusinessException {
    TreeViewList treeView = new TreeViewList();
    Connection conn = null;
    String tree = "";
    try {
      List params = new ArrayList();
      conn = DAOFactory.getInstance().getConnection();
      String ccons = cropArray2String(coms);
      String ocons = cropArray2String(orgs);
      String pcons = cropArray2String(posis);
      String sql = getUsersTreeSql(ccons, ocons, pcons, svNd, params);
      Delta data = DBHelper.queryToDelta(conn, sql, params.toArray());
      //            filter(data);
      buildCache(users, data);
      tree = treeView.getTreeWithRoot(data, "人员");
    } catch (Exception se) {
      throw new BusinessException(se.getMessage());
    } finally {
      DBHelper.closeConnection(conn);
    }
    return tree;
  }

  /**
   * 构造树结构sql语句
   * @param comsCollection
   * @param orgsCollection
   * @param posisCollection
   * @return
   */
  protected String getUsersTreeSql(String svNd, List params) {
    return getUsersTreeSql(COMMON_CODE, COMMON_CODE, COMMON_CODE, svNd, params);
  }

  protected String getUsersTreeSql(String comsCollection, String svNd, List params) {
    return getUsersTreeSql(comsCollection, COMMON_CODE, COMMON_CODE, svNd, params);
  }

  protected String getUsersTreeSql(String comsCollection, String orgsCollection,
    String svNd, List params) {
    return getUsersTreeSql(comsCollection, orgsCollection, COMMON_CODE, svNd, params);
  }

  protected String getUsersTreeSql(String comsCollection, String orgsCollection,
    String posisCollection, String svNd, List params) {
    return getUsersTreeSql(comsCollection, orgsCollection, posisCollection,
      COMMON_CODE, svNd, params);
  }

  protected String getUsersTreeSql(String comsCollection, String orgsCollection,
    String posisCollection, String usersCollection, String svNd, List params) {
    String sql = "SELECT DISTINCT AS_EMP.USER_ID AS CODE, "
      + "AS_EMP_POSITION.CO_CODE PREFIX1,AS_EMP_POSITION.ORG_CODE PREFIX2,AS_EMP_POSITION.POSI_CODE PREFIX3,"
      + "'' AS P_CODE, AS_EMP.EMP_NAME AS NAME,\'N\' AS IS_CHECKED,\'" 
      + Page.LOCAL_RESOURCE_PATH + "style/img/tree/user.png\' AS NODE_ICON FROM AS_EMP_POSITION ,"
      + "AS_EMP where AS_EMP_POSITION.EMP_CODE = AS_EMP.EMP_CODE and ND = ?";
    params.add(svNd);
    //      限定单位
    if (!(COMMON_CODE).equals(comsCollection) && comsCollection != null) {
      sql += " and AS_EMP_POSITION.CO_CODE in (";
      String[] coms = comsCollection.split(",");
      for (int i = 0; i < coms.length; i++) {
        sql += "?, ";
        params.add(coms[i]);
      }
      sql = sql.substring(0, sql.length() - 2);
      sql += ")";
    }
    //        限定机构
    if (!(COMMON_CODE).equals(orgsCollection) && orgsCollection != null) {
      sql += " and AS_EMP_POSITION.ORG_CODE in (";
      String[] orgs = orgsCollection.split(",");
      for (int i = 0; i < orgs.length; i++) {
        sql += "?, ";
        params.add(orgs[i]);
      }
      sql = sql.substring(0, sql.length() - 2);
      sql += ")";
    }
    //      限定职位
    if (!(COMMON_CODE).equals(posisCollection) && posisCollection != null) {
      sql += " and AS_EMP_POSITION.POSI_CODE in (";
      String[] posis = posisCollection.split(",");
      for (int i = 0; i < posis.length; i++) {
        sql += "?, ";
        params.add(posis[i]);
      }
      sql = sql.substring(0, sql.length() - 2);
      sql += ")";
    }
    if (!(COMMON_CODE).equals(usersCollection) && usersCollection != null) {
      sql += " and AS_EMP_POSITION.USER_CODE in (";
      String[] users = usersCollection.split(",");
      for (int i = 0; i < users.length; i++) {
        sql += "?, ";
        params.add(users[i]);
      }
      sql = sql.substring(0, sql.length() - 2);
      sql += ")";
    }
    return sql;
  }

  /**
   * Get the company name from cache
   * @param code
   * @return
   */
  public String getCompanyName(String code, String svNd) throws BusinessException {
    if (companies.isEmpty()) {
      buildComsTree(svNd);
    }
    String ret = (String) companies.get(code);
    if (ret == null)
      ret = code;
    return ret;
  }

  /**
   * @param code
   * @return
   */
  public String[] getCompanyNames(String[] code, String svNd)
    throws BusinessException {
    for (int i = 0; i < code.length; i++) {
      code[i] = getCompanyName(code[i], svNd);
    }
    return code;
  }

  /**
   * @param code
   * @return
   */
  public String getCompanyNames(String code, String svNd) throws BusinessException {
    if (code == null)
      return "";
    return array2String(getCompanyNames(code.split(","), svNd));
  }

  /**
   * Get the Organization name from cache
   * @param code
   * @return
   */
  public String getOrganizationName(String code) throws BusinessException {
    if (organizations.isEmpty()) {
      String sql = "select distinct ORG_CODE AS CODE,ORG_NAME AS NAME from as_org";
      buildCache(organizations, sql);
    }
    String ret = (String) organizations.get(code);
    if (ret == null)
      ret = code;
    return ret;
  }

  /**
   * @param code
   * @return
   */
  public String getOrganizationNames(String code) throws BusinessException {
    if (code == null)
      return "";
    return array2String(getOrganizationNames(code.split(",")));
  }

  public String[] getOrganizationNames(String[] code) throws BusinessException {
    for (int i = 0; i < code.length; i++) {
      code[i] = getOrganizationName(code[i]);
    }
    return code;
  }

  /**
   * Get the position name from cache
   * @param code
   * @return
   */
  public String getPositionName(String code) throws BusinessException {
    if (positions.isEmpty()) {
      String sql = "select distinct POSI_CODE,POSI_NAME from as_position";
      buildCache(positions, sql);
    }
    String ret = (String) positions.get(code);
    if (ret == null)
      ret = code;
    return ret;
  }

  /**
   * @param code
   * @return
   */
  public String getPositionNames(String code) throws BusinessException {
    if (code == null)
      return "";
    return array2String(getPositionNames(code.split(",")));
  }

  /**
   * @param code
   * @return
   */
  public String[] getPositionNames(String[] code) throws BusinessException {
    for (int i = 0; i < code.length; i++) {
      code[i] = getPositionName(code[i]);
    }
    return code;
  }

  /**
   * Get the staff name from cache
   * @param code
   * @return
   */
  public String getUserName(String code) throws BusinessException {
    if (users.isEmpty()) {
      String sql = "select distinct EMP_CODE, EMP_NAME from as_emp";
      buildCache(users, sql);
    }
    String ret = (String) users.get(code);
    if (ret == null)
      ret = code;
    return ret;
  }

  /**
   * @param code
   * @return
   */
  public String[] getUserNames(String[] code) throws BusinessException {
    for (int i = 0; i < code.length; i++) {
      code[i] = getUserName(code[i]);
    }
    return code;
  }

  /**
   * @param code
   * @return
   */
  public String getUserNames(String code) throws BusinessException {
    if (code == null)
      return "";
    return array2String(getUserNames(code.split(",")));
  }

  /**
   * 把字符串数组转换成以逗号分割的字符串,同时去除＃和空字符串
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

  /**
   * 把字串转换成以逗号分割的数组
   * @param str
   * @return
   */
  public String[] string2Array(String str) {
    return StringTools.split2(str, ",");
  }

  public String cropArray2String(String[] arr) {
    StringBuffer ret = new StringBuffer();
    if (arr != null) {
      for (int i = 0; i < arr.length; i++) {
        ret.append(arr[i].trim()).append(",");
      }
      if (ret.length() > 0) {
        return ret.substring(0, ret.length() - 1);
      }
    } else {
      return null;
    }
    return ret.toString();
  }

  /**
   * @return   返回 comCollections。
   * @uml.property   name="comCollections"
   */
  public String[] getComCollections() {
    return comCollections;
  }

  /**
   * @param comCollections   要设置的 comCollections。
   * @uml.property   name="comCollections"
   */
  public void setComCollections(String[] comCollections) {
    this.comCollections = comCollections;
  }

  /**
   * @return   返回 orgCollections。
   * @uml.property   name="orgCollections"
   */
  public String[] getOrgCollections() {
    return orgCollections;
  }

  /**
   * @param orgCollections   要设置的 orgCollections。
   * @uml.property   name="orgCollections"
   */
  public void setOrgCollections(String[] orgCollections) {
    this.orgCollections = orgCollections;
  }

  /**
   * @return   返回 positionCollections。
   * @uml.property   name="positionCollections"
   */
  public String[] getPositionCollections() {
    return positionCollections;
  }

  /**
   * @param positionCollections   要设置的 positionCollections。
   * @uml.property   name="positionCollections"
   */
  public void setPositionCollections(String[] positionCollections) {
    this.positionCollections = positionCollections;
  }

  /**
   * @return   返回 userCollections。
   * @uml.property   name="userCollections"
   */
  public String[] getUserCollections() {
    return userCollections;
  }

  /**
   * @param userCollections   要设置的 userCollections。
   * @uml.property   name="userCollections"
   */
  public void setUserCollections(String[] userCollections) {
    this.userCollections = userCollections;
  }
}
