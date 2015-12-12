// $Id: RightUtil.java,v 1.27 2009/06/10 07:47:55 zhuyulong Exp $
package com.anyi.gp.pub;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.anyi.gp.bean.RightBean;
import com.anyi.gp.context.ApplusContext;
import com.anyi.gp.core.dao.BaseDao;
import com.anyi.gp.desktop.MenuTreeBuilder;
import com.anyi.gp.meta.CompoMeta;
import com.anyi.gp.meta.MetaManager;
import com.anyi.gp.workflow.util.WFUtil;

public class RightUtil {

  private final static Logger log = Logger.getLogger(RightUtil.class);

  public final static String SQL_SELECT_USER_FUNC = "gmap-priv.selectUserFunc";//获取用户的部件权限sql

  public final static String SQL_SELECT_USER_FUNC_SA = "gmap-priv.selectUserFuncBySa";//获取用户的部件权限sql

  public final static String MAIN_TABLE_ALIAS = "MASTER";
  
  public final static String CTRL_FIELD_CONDITION = "SQL_CONDITION";

  /**
   * 用户是否有权限使用某功能
   * @param userId
   * @param funcId
   * @param compoName
   * @param coCode
   * @param orgId
   * @return
   */
  public static boolean isAllowed(Set funcSet, String userId, String funcId, String compoName, String coCode,
    String orgId, String posiCode) {
    if (userId.equalsIgnoreCase("sa")) {
      return true;
    }

    CompoMeta meta = null;
    try {
      meta = MetaManager.getCompoMeta(compoName);
    } catch (RuntimeException e) {
      return false;
    }
    if (meta.isGrantToAll()) {
      return true;
    }

    if (WFUtil.isWorkflowCompoAndImpowerFWatch(funcId, compoName)) {
      return true;
    }

    if (funcSet == null)
      funcSet = getAllowedFuncs(userId, compoName, coCode, orgId, posiCode);
    return funcSet.contains(funcId);
  }

  public static Set getAllowedFuncs(String userId, String compoId, String coCode, String orgId,
    String posiCode) {
    String sqlid = null;
    BaseDao dao = (BaseDao) ApplusContext.getBean("baseDao");
    if ("sa".equals(userId)) {
      sqlid = SQL_SELECT_USER_FUNC_SA;
    } else {
      if (posiCode != null && posiCode.length() != 0) {
        int temp = 0;
        try {
          temp = ((Integer) dao.queryForObject(MenuTreeBuilder.SQL_CHECK_SA_ROLE, posiCode)).intValue();
        } catch (SQLException e) {
          log.error(e);
          throw new RuntimeException(e);
        }
        if (temp > 0) {
          sqlid = SQL_SELECT_USER_FUNC_SA;
        }
      }
    }
    if (sqlid == null) {
      sqlid = SQL_SELECT_USER_FUNC;
    }

    Map params = new HashMap();
    params.put("userId", userId);
    params.put("coCode", coCode == null ? "" : coCode);
    params.put("orgCode", orgId == null ? "" : orgId);
    params.put("posiCode", posiCode == null ? "" : posiCode);
    params.put("compoId", compoId);

    Set returnSet = new HashSet();
    try {
      List result = dao.queryForList(sqlid, params);
      returnSet.addAll(result);
      return returnSet;
    } catch (SQLException e) {
      log.error(e);
      throw new RuntimeException(e);
    }

  }

  /**
   * 获取角色数值权限
   * @param roleId
   * @param funcId
   * @param compoId
   * @return
   */
  public static List getRoleNumLimList(String roleId, String funcId, String compoId) {
    List al = new ArrayList();

    try {
      Map params = new HashMap();
      params.put("ROLE_ID", roleId);
      params.put("FUNC_ID", funcId);
      params.put("COMPO_ID", compoId);

      BaseDao dao = (BaseDao) ApplusContext.getBean("baseDao");
      List result = dao.queryForList(CommonSqlIdConst.GET_ROLE_NUM_LIM, params);

      for (int i = 0; i < result.size(); i++) {
        Map map = (Map) result.get(i);

        RightBean rb = new RightBean();
        rb.setCtrlField((String) map.get("CTRL_FIELD"));
        rb.setGranRange((String) map.get("GRAN_RANGE"));
        rb.setRevoRange((String) map.get("REVO_RANGE"));
        rb.setIsGrant((String) map.get("IS_GRAN"));
        al.add(rb);
      }
    } catch (SQLException se) {
      log.debug(se);
      throw new RuntimeException(se);
    }
    return al;
  }

  /**
   * 根据用户获取角色数值权限
   * @param userId
   * @param funcId
   * @param compoId
   * @return
   */
  public static List getRoleNumLimListByUser(String userId, String funcId, String compoId) {
    List al = new ArrayList();
    String sql = " select CTRL_FIELD, GRAN_RANGE, REVO_RANGE, IS_GRAN from AS_ROLE_NUM_LIM "
      + " where FUNC_ID = ? and COMPO_ID = ? and ROLE_ID in (select ROLE_ID from V_AS_USR_ROLE where USER_ID = ?)";
    BaseDao dao = (BaseDao) ApplusContext.getBean("baseDao");
    List result = dao.queryForListBySql(sql, new Object[] { funcId, compoId, userId });
    for (int i = 0; i < result.size(); i++) {
      Map map = (Map) result.get(i);

      RightBean rb = new RightBean();
      rb.setCtrlField((String) map.get("CTRL_FIELD"));
      rb.setGranRange((String) map.get("GRAN_RANGE"));
      rb.setRevoRange((String) map.get("REVO_RANGE"));
      rb.setIsGrant((String) map.get("IS_GRAN"));
      al.add(rb);
    }
    return al;
  }

  /**
   * 获取用户数值权限
   * @param userId
   * @param funcId
   * @param compoId
   * @return
   */
  public static List getUserNumLimList(String userId, String funcId, String compoId) {
    return getUserNumLimList(userId, funcId, compoId, null);
  }

  public static List getUserNumLimList(String userId, String funcId, String compoId, String ctrlField) {
    List al = new ArrayList();

    List result = getUserNumLim(userId, funcId, compoId, ctrlField);

    for (int i = 0; i < result.size(); i++) {
      Map map = (Map) result.get(i);

      RightBean rb = new RightBean();
      rb.setCtrlField((String) map.get("CTRL_FIELD"));
      rb.setGranRange((String) map.get("GRAN_RANGE"));
      rb.setRevoRange((String) map.get("REVO_RANGE"));
      rb.setIsGrant((String) map.get("IS_GRAN"));
      al.add(rb);
    }
    return al;
  }

  public static String getUserNumLimCondition(HttpServletRequest request, String userId, String funcId,
    String compoId, String tableName, String ctrlField) {
    return getUserNumLimCondition(request, userId, funcId, compoId, tableName, ctrlField, MAIN_TABLE_ALIAS);
  }

  public static String getUserNumLimCondition(HttpServletRequest request, String userId, String funcId,
    String compoId, String tableName, String ctrlField, String tableAlias) {
    List result = getUserNumLim(userId, funcId, compoId, ctrlField);
    String userNumLimCondition = getUserNumLimCondition(request, result, tableAlias);
    result = getRoleNumLimByUserId(userId, funcId, compoId, ctrlField);
    String roleNumLimCondition = getUserNumLimCondition(request, result, tableAlias);//用户数值权限和角色数值权限是or的关系
    if (userNumLimCondition == null || userNumLimCondition.length() == 0) {
      userNumLimCondition = roleNumLimCondition;
    } else if (roleNumLimCondition != null && roleNumLimCondition.length() > 0) {
      userNumLimCondition = "((" + userNumLimCondition + ") or (" + roleNumLimCondition + "))";
    }
    return userNumLimCondition;
  }

  /**
   * 获取用户数值权限条件字符串
   * @param result
   * @param tableName
   * @return
   */
  public static String getUserNumLimCondition(HttpServletRequest request, List result, String tableName) {

    if (result == null || result.isEmpty())
      return "";

    StringBuffer sb = new StringBuffer();
    sb.append(" ( ");

    for (int i = 0; i < result.size(); i++) {
      Map map = (Map) result.get(i);
      String isGran = (String) map.get("IS_GRAN");
      String ctrlField = (String) map.get("CTRL_FIELD");
      String granRange = (String) map.get("GRAN_RANGE");

      if (tableName != null && tableName.length() > 0)
        ctrlField = tableName + "." + ctrlField;
      ctrlField += " ";

      String flag = "";//反正向标识

      if (!"0".equals(isGran)) {//处理不允许设置
        flag = " not ";
        granRange = (String) map.get("REVO_RANGE");
      }

      /*String[] st = granRange.split(",");//多值选择
      if (st.length > 1) {
         sb.append(processInCondition(ctrlField, flag, request, granRange));
       } 
       else {
         String temp = granRange.toLowerCase();
         if (temp.indexOf("select") >= 0) {//like条件
           sb.append(ctrlField + flag + " in ( " + processSvCondtion(request, granRange) + " ) ");
         } 
         else if (temp.indexOf("like") >= 0) {//select语句
           sb.append(ctrlField + granRange);
         }else if(granRange.indexOf("..") > 0){//'01..09'
           sb.append(processBetweenCondition(ctrlField, flag, granRange));
         }
         else {
           if(granRange.indexOf("@@") >= 0 ){//sv环境变量
             granRange = processSvCondtion(request, granRange);
           }
           if(flag.length() == 0)//单值选择
             sb.append(ctrlField + " = " + granRange);
           else
             sb.append(ctrlField + " <> " + granRange);
         }
       }*/
      //增加CTRL_FIELD对SQL_CONDITION支持 20110918 cjl
      if(ctrlField.equals(RightUtil.CTRL_FIELD_CONDITION) || ctrlField.lastIndexOf(RightUtil.CTRL_FIELD_CONDITION)>=0){
        sb.append(" ").append(processSvCondtion(request, granRange)).append(" )");
        return sb.toString();
      }
      String temp =new String(granRange);
      temp = temp.trim();    
      temp=temp.toLowerCase();
      if (temp.startsWith("select") 
        ||temp.startsWith("(")
        ||temp.startsWith("nvl(")
        ||temp.startsWith("decode(")
        ||temp.startsWith("max(")
        ||temp.startsWith("to_char(")
        ||temp.startsWith("to_date(")) {//like条件
        sb.append(ctrlField + flag + " in ( " + processSvCondtion(request, granRange) + " ) ");
      } else if (temp.startsWith("like") 
        || temp.startsWith("not")
        || temp.startsWith("in (")) {//select语句
        sb.append(ctrlField +" "+ processSvCondtion(request, granRange));
      } else if (granRange.indexOf("..") > 0) {//'01..09'
        sb.append(processBetweenCondition(ctrlField, flag, granRange));
      } else {
        String[] st = granRange.split(",");//多值选择
        if (st.length > 1) {
          sb.append(processInCondition(ctrlField, flag, request, granRange));
        } else {
          if (granRange.indexOf("@@") >= 0) {//sv环境变量
            granRange = processSvCondtion(request, granRange);
          }
          if (flag.length() == 0)//单值选择
            sb.append(ctrlField + " = " + granRange);
          else
            sb.append(ctrlField + " <> " + granRange);
        }
      }
      sb.append(" and ");
    }

    if (sb.toString().endsWith("and "))
      return sb.substring(0, sb.length() - 4) + " ) ";
    else if (" ( ".equals(sb)) {
      return "";
    } else
      return sb.toString();
  }

  /**
   * 处理in条件，可能包含between条件
   * @param ctrlField
   * @param flag
   * @param request
   * @param granRange
   * @return
   */
  private static String processInCondition(String ctrlField, String flag, HttpServletRequest request,
    String granRange) {
    StringBuffer sb = new StringBuffer("(");
    List inValueList = new ArrayList();
    List betweenList = new ArrayList();
    String[] st = granRange.split(",");
    for (int i = 0; i < st.length; i++) {
      String stTemp = st[i];
      if (stTemp.indexOf("@@") >= 0) {
        inValueList.add(processSvCondtion(request, stTemp));
      } else if (stTemp.indexOf("..") > 0) {
        betweenList.add(processBetweenCondition(ctrlField, flag, stTemp));
      } else {
        inValueList.add(stTemp);
      }
    }

    if (!inValueList.isEmpty()) {
      sb.append(ctrlField + " " + flag + " in (");
    }
    for (int i = 0; i < inValueList.size(); i++) {
      sb.append(inValueList.get(i) + ",");
    }
    if (!inValueList.isEmpty()) {
      sb = new StringBuffer(sb.substring(0, sb.length() - 1) + ") ");
    }

    for (int i = 0; i < betweenList.size(); i++) {
      String temp = " or ";
      if (flag.length() == 0) {
        if (i == 0 && inValueList.isEmpty())
          temp = " ";
      } else {
        if (i == 0 && inValueList.isEmpty())
          temp = " ";
        else
          temp = " and ";
      }
      sb.append(temp + "(" + betweenList.get(i) + ")");
    }
    sb.append(")");

    return sb.toString();
  }

  /**
   * 处理between条件
   * @param ctrlField
   * @param flag
   * @param granRange
   * @return
   */
  private static String processBetweenCondition(String ctrlField, String flag, String granRange) {
    granRange = granRange.trim();
    int pos = granRange.indexOf("..");
    String startStr = granRange.substring(0, pos).trim();
    String endStr = granRange.substring(pos + 2, granRange.length()).trim();

    if (!startStr.startsWith("'")) {
      startStr = "'" + startStr;
    }
    if (!startStr.endsWith("'")) {
      startStr = startStr + "'";
    }
    if (!endStr.startsWith("'")) {
      endStr = "'" + endStr;
    }
    if (!endStr.endsWith("'")) {
      endStr = endStr + "'";
    }

    return ctrlField + flag + " between " + startStr + " and " + endStr + " ";
  }

  /**
   * 增加session变量条件
   * @param request
   * @param granRange
   * @return
   */
  private static String processSvCondtion(HttpServletRequest request, String granRange) {
    int start = granRange.indexOf("@@");
    if (start < 0) {
      return granRange;
    }

    String svTemp = granRange.substring(start + 2);
    int end = svTemp.indexOf("'");
    if (end > 0) {
      svTemp = svTemp.substring(0, end);
    }

    String temp = granRange;
    if (request != null) {
      temp = temp.replaceAll("@@" + svTemp.trim() + "", SessionUtils.getAttribute(request, svTemp.trim()));
      //增加递归检查，替换全部session变量，20110819 chenjl
      //return  temp; 
      return processSvCondtion(request, temp);
    } else
      return "''";
  }

  /**
   * 获取用户数值权限
   * @param userId
   * @param funcId
   * @param compoId
   * @return
   */
  private static List getUserNumLim(String userId, String funcId, String compoId, String ctrlField) {
    try {
      Map params = new HashMap();
      params.put("USER_ID", userId);
      params.put("FUNC_ID", funcId);
      params.put("COMPO_ID", compoId);
      if (ctrlField != null && ctrlField.length() > 0) {
        params.put("CTRL_FIELD", ctrlField);
      }
      BaseDao dao = (BaseDao) ApplusContext.getBean("baseDao");
      return dao.queryForList(CommonSqlIdConst.GET_USER_NUM_LIM, params);
    } catch (SQLException se) {
      log.debug(se);
      throw new RuntimeException(se);
    }
  }

  public static List getRoleNumLimByUserId(String userId, String funcId, String compoId, String ctrlField) {
    try {
      Map params = new HashMap();
      params.put("USER_ID", userId);
      params.put("FUNC_ID", funcId);
      params.put("COMPO_ID", compoId);
      if (ctrlField != null && ctrlField.length() > 0) {
        params.put("CTRL_FIELD", ctrlField);
      }
      BaseDao dao = (BaseDao) ApplusContext.getBean("baseDao");
      return dao.queryForList(CommonSqlIdConst.GET_ROLE_NUM_LIM_BY_USER, params);
    } catch (SQLException se) {
      log.debug(se);
      throw new RuntimeException(se);
    }
  }

  public static boolean isAllowed(String userId, String funcId, String compoName, String coCode,
    String orgId, String posiCode) {
    return isAllowed(null, userId, funcId, compoName, coCode, orgId, posiCode);
  }
}
