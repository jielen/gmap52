package com.anyi.gp.domain;

import java.io.IOException;
import java.io.Writer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.anyi.gp.BusinessException;
import com.anyi.gp.Delta;
import com.anyi.gp.Pub;
import com.anyi.gp.TableData;
import com.anyi.gp.bean.RightBean;
import com.anyi.gp.context.ApplusContext;
import com.anyi.gp.core.dao.BaseDao;
import com.anyi.gp.desktop.MenuTreeBuilder;
import com.anyi.gp.desktop.Title;
import com.anyi.gp.desktop.TitleControl;
import com.anyi.gp.desktop.Tree;
import com.anyi.gp.desktop.TreeBuilder;
import com.anyi.gp.pub.DAOFactory;
import com.anyi.gp.pub.DBHelper;
import com.anyi.gp.pub.GeneralFunc;
import com.anyi.gp.pub.TreeViewList;
import com.anyi.gp.util.StringTools;
import com.opensymphony.webwork.ServletActionContext;

/**
 * 权限
 * TODO：可以拆分
 */
public class RightService {
  
  private static final Logger log = Logger.getLogger(RightService.class);
  
  private static final String SQL_DELETE = "gmap-priv.deletePage";
  
  private static final String SQL_DELETE_MENU_WITH_PAGE = "gmap-priv.deleteMenuWithPage";
  
  private static final String SQL_DELETE_COMPO_WITH_MENU = "gmap-priv.deleteMenuCompoWithMenu";

  /**
   * 取用户权限树
   * @param userId
   * @param menuId
   * @param svUserID
   * @return
   */
  public String createUserPrivMenu(String userId, String menuId, String svUserID){
    List funcL = new ArrayList();
    Connection conn = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    String sqlStr = "select compo_id, func_id from as_user_func where user_id = ? "
           + " union select r.compo_id, r.func_id from v_as_role_func r, as_user a, "
           + "       as_emp b, as_emp_position c, as_posi_role d  "
           + "       where a.user_id = b.user_id and b.emp_code = c.emp_code  "
           + "       and c.posi_code = d.posi_code and a.user_id = ? "
           + "       and r.role_id = d.role_id "
           + " union select r.compo_id, r.func_id from v_as_role_func r, "
           + "       as_emp_role e, as_emp f where e.emp_code = f.emp_code "
           + "       and f.user_id = ? and r.role_id = e.role_id ";
    
    try{
      conn = DAOFactory.getInstance().getConnection();
      pst = conn.prepareStatement(sqlStr);
    
      int i = 1;
      pst.setString(i++, userId);
      pst.setString(i++, userId);
      pst.setString(i++, userId);
    
      rs = pst.executeQuery();
      while (rs.next()) {
        funcL.add(rs.getString(1) + "_" + rs.getString(2));
      }
      //rs.close();
      //pst.close();
      //DBHelper.closeConnection(null, pst, rs);
      
      List numL = new ArrayList();
      sqlStr = "select compo_id, func_id, ctrl_field, gran_range, revo_range, is_gran from as_user_num_lim where user_id=? "
        + " union select compo_id, func_id, ctrl_field, gran_range, revo_range,is_gran from as_role_num_lim "
        + " where role_id in (SELECT distinct d.role_id FROM as_user a,as_emp b,as_emp_position c,as_posi_role d "
        + " where a.user_id=b.user_id and b.emp_code=c.emp_code and c.posi_code=d.posi_code and a.user_id=?) ";
      
      pst = conn.prepareStatement(sqlStr);
      int index = 1;
      pst.setString(index++, userId);
      pst.setString(index++, userId);
      
      rs = pst.executeQuery();
      
      while (rs.next()) {
        String tmp1 = rs.getString(1);
        tmp1 = tmp1 + "_" + rs.getString(2);
        numL.add(tmp1); // 部件功能
        numL.add(rs.getString(3)); // 控制字段
        tmp1 = rs.getString(6).toUpperCase();
        numL.add(tmp1);
        if (tmp1.equals("Y")) {
          numL.add(rs.getString(4));
        } else {
          numL.add(rs.getString(5));
        }
      }
      //rs.close();
      //DBHelper.closeConnection(null, pst, rs);
      List fieldL = getTableFieldList(conn, menuId);
      
      return createPrivMenu(conn, menuId, svUserID, funcL, numL, fieldL);
      
    }catch (SQLException ex) {
      throw new RuntimeException("RightService类的createUserPrivMenu方法：" + ex.toString());
    } finally {
      DBHelper.closeConnection(conn, pst, rs);
    }
  }
  
  /**
   * 取角色权限树
   * @param roleId
   * @param menuId
   * @param svUserID
   * @return
   */
  public String createRolePrivMenu(String roleId, String menuId, String svUserID) {

    Connection myConn = null;
    String sqlStr = null;
    ResultSet rs = null;
    PreparedStatement pst = null;
    
    sqlStr = "select compo_id, func_id from v_as_role_func where role_id = ? ";
    try {
      myConn = DAOFactory.getInstance().getConnection();
      List funcL = new ArrayList();
      pst = myConn.prepareStatement(sqlStr);
      
      int j =1;
      pst.setString(j++, roleId);
      
      rs = pst.executeQuery();
      while (rs.next()) {
        funcL.add(rs.getString(1) + "_" + rs.getString(2));
      }
      //rs.close();
      //DBHelper.closeConnection(null, pst, rs);
      List numL = new ArrayList();
      sqlStr = "select compo_id, func_id, ctrl_field, gran_range, revo_range, is_gran from as_role_num_lim where role_id = ? ";
      pst = myConn.prepareStatement(sqlStr);
      pst.setString(1, roleId);
      
      rs = pst.executeQuery();
      while (rs.next()) {
        String tmp1 = rs.getString(1);
        tmp1 = tmp1 + "_" + rs.getString(2);
        numL.add(tmp1); // 部件功能
        numL.add(rs.getString(3)); // 控制字段
        tmp1 = rs.getString(6).toUpperCase();
        
        numL.add(tmp1);
        if (tmp1.equals("Y")) {
          numL.add(rs.getString(4));
        } else {
          numL.add(rs.getString(5));
        }
      }
      //rs.close();
      //DBHelper.closeConnection(null, pst, rs);
      List fieldL = getTableFieldList(myConn, menuId);
      
      return createPrivMenu(myConn, menuId, svUserID, funcL, numL, fieldL);
      
    }catch (SQLException ex) {
       throw new RuntimeException("RightService类的createRolePrivMenu方法：" + ex.toString());
    } finally {
       DBHelper.closeConnection(myConn, pst, rs);
    }
    
  } 
  
  private List getTableFieldList(Connection myConn, String menuId) throws SQLException{
    
    PreparedStatement pst = null;
    ResultSet rs = null;
    List fieldL = new ArrayList();
    String tStr = "", fStr = "", nStr = "", strTableId = "";
    String sqlStr = " select a.tab_id, a.data_item, a.ord_index as ord_index, b.res_na "
      + " from as_tab_col a, as_lang_trans b, v_ap_menu_compo mc, as_compo ac "
      + " where a.data_item = b.res_id and b.lang_id='C' and a.tab_id != 'as_temp' "
      + " and mc.compo_id = ac.compo_id and mc.menu_id = ? and ac.master_tab_id = a.tab_id "
      + " and upper(a.is_save) = 'Y' "//and (a.f_ref_name is not null or a.val_set_id is not null) "
      + " order by tab_id,ord_index";
    
    try{
      pst = myConn.prepareStatement(sqlStr);
      pst.setString(1, menuId);
      rs = pst.executeQuery();
      
      while (rs.next()) {
        strTableId = rs.getString("tab_id");
        String dataItem = rs.getString("data_item");
        String dateItemName = rs.getString("res_na");
        if (!tStr.equals(strTableId)) {
          fieldL.add(tStr);
          fieldL.add(fStr);
          fieldL.add(nStr);
          tStr = strTableId;
          fStr = dataItem + ",";
          nStr = dateItemName + ",";
        } else {
          fStr += dataItem + ",";
          nStr += dateItemName + ",";
        }
      }
    }finally{
      DBHelper.closeConnection(null, pst, rs);
    }

    fieldL.add(tStr);
    fieldL.add(fStr);
    fieldL.add(nStr);
    
    return fieldL;
  }
  
  /**
   * 生成权限树
   * @param myConn
   * @param pst
   * @param rs
   * @param menuId
   * @param svUserID
   * @param funcL
   * @return
   * @throws SQLException
   */
  private String createPrivMenu(Connection myConn
    , String menuId, String svUserID, List funcL, List numL, List fieldL) throws SQLException{
    
    Delta menuD = new Delta();
    String tmp1 = null;
    
    String sqlStr = "select 'fwatch' mID, a.compo_id pID, '查看' mName, "
      + "1 ord_index, 'func' TYPE, a.master_tab_id tabID "
      + "from as_compo a, v_ap_menu_compo b, v_as_usr_func c "
      + "where b.compo_id=a.compo_id and b.menu_id = ? "
      + "and a.compo_id=c.compo_id and b.compo_id=c.compo_id and c.user_id = ? "

      + "union "
      + "select 'fquote' mID, a.compo_id pID, '引用' mName,"
      + "998 ord_index, 'func' TYPE, a.master_tab_id tabID "
      + "from as_compo a, v_ap_menu_compo b, v_as_usr_func c "
      + "where b.compo_id=a.compo_id and b.menu_id = ? "
      + "and a.compo_id=c.compo_id and b.compo_id=c.compo_id and c.user_id = ? "

      + "union "
      + "select a.compo_id mID, c.menu_id pID, b.res_na mName, "
      + "c.ord_index, 'compo' TYPE, d.master_tab_id tabID from "
      + "as_compo a,as_lang_trans b,v_ap_menu_compo c,as_compo d, v_as_usr_func e "
      + "where b.res_id=a.compo_id and b.lang_id='C' and "
      + "c.compo_id=a.compo_id and c.menu_id= ? "
      + "and a.compo_id=c.compo_id and d.compo_id=c.compo_id "
      + "and a.compo_id=e.compo_id and d.compo_id=e.compo_id and c.compo_id=e.compo_id "
      + "and e.user_id= ? "

      + " union "
      + "select a.compo_id mID, c.menu_id pID, b.res_na mName, "
      + "c.ord_index, 'compo' TYPE, a.master_tab_id tabID from "
      + "as_compo a,as_lang_trans b,v_ap_menu_compo c, "
      + "v_as_role_func f, as_posi_role g, as_emp_position h, as_emp i "
      + "where b.res_id=a.compo_id and b.lang_id='C' and "
      + "c.compo_id=a.compo_id and c.menu_id= ? "
      + "and a.compo_id=c.compo_id and f.compo_id=f.compo_id "
      + "and c.compo_id=f.compo_id and i.user_id= ? "
      + "and i.emp_code=h.emp_code and h.posi_code=g.posi_code "
      + "and g.role_id=f.role_id "
      
      + " union "
      + "select a.func_id mID, a.compo_id pID, b.res_na mName, "
      + "1 ord_index, 'func' TYPE, c.master_tab_id tabID "
      + "from as_compo_func a, as_lang_trans b, as_compo c, "
      + "v_ap_menu_compo d, as_func e, v_as_usr_func f "
      + "where b.res_id=a.func_id and c.parent_compo is null and "
      + "b.lang_id='C' and a.compo_id=c.compo_id and d.menu_id= ? "
      + "and d.compo_id=a.compo_id and a.func_id=e.func_id "
      + "and (e.is_grant_to_all is null or upper(e.is_grant_to_all)='N') "
      + "and a.compo_id=f.compo_id and c.compo_id=f.compo_id "
      + "and d.compo_id=f.compo_id and f.user_id= ? "
      + "and f.func_id = e.func_id"
      
      + " union "
      + "select a.func_id mID, c.compo_id pID, b.res_na mName, "
      + "1 ord_index, 'func' TYPE, c.master_tab_id tabID "
      + "from as_compo_func a, as_lang_trans b, as_compo c, "
      + "v_ap_menu_compo d, as_func e, v_as_usr_func g "
      + "where b.res_id=a.func_id and c.parent_compo is not null and "
      + "b.lang_id='C' and a.compo_id=c.parent_compo and d.menu_id= ? "
      + "and d.compo_id=c.compo_id and a.func_id=e.func_id "
      + " and (e.is_grant_to_all is null or upper(e.is_grant_to_all)='N')"
      + " and c.compo_id not in "
      + "(select f.compo_id from as_compo_func f where f.compo_id = c.compo_id) "
      + "and a.compo_id=g.compo_id and c.compo_id=g.compo_id "
      + "and d.compo_id=g.compo_id and g.user_id= ? "

      + " union "
      + "select a.func_id mID, c.compo_id pID, b.res_na mName, "
      + "1 ord_index, 'func' TYPE, c.master_tab_id tabID "
      + "from as_compo_func a, as_lang_trans b, as_compo c, "
      + "v_ap_menu_compo d, as_func e, v_as_usr_func g "
      + "where b.res_id=a.func_id and c.parent_compo is not null and "
      + "b.lang_id='C' and a.compo_id=c.compo_id and d.menu_id= ? "
      + "and d.compo_id=c.compo_id and a.func_id=e.func_id "
      + "and (e.is_grant_to_all is null or upper(e.is_grant_to_all)='N')"
      + " and c.compo_id in "
      + "(select f.compo_id from as_compo_func f where f.compo_id = c.compo_id) "
      + "and a.compo_id=g.compo_id and c.compo_id=g.compo_id "
      + "and d.compo_id=g.compo_id and g.user_id= ? "

      + " union "
      + "select a.func_id mID, c.compo_id pID, b.res_na mName, "
      + "1 ord_index, 'func' TYPE, c.master_tab_id tabID "
      + "from as_compo_func a, as_lang_trans b, as_compo c, "
      + "v_ap_menu_compo d, as_func e,  "
      + "v_as_role_func x, as_posi_role z, as_emp gg, as_emp_position hh "
      + "where b.res_id=a.func_id and "
      + "b.lang_id='C' and a.compo_id=c.compo_id and d.menu_id= ? "
      + " and d.compo_id=c.compo_id and a.func_id=e.func_id "
      + "and (e.is_grant_to_all is null or upper(e.is_grant_to_all)='N') "
      + " and c.compo_id in "
      + "(select f.compo_id from as_compo_func f where f.compo_id = c.compo_id) "
      + "and a.compo_id=x.compo_id and c.compo_id=x.compo_id and a.func_id=x.func_id "
      + "and d.compo_id=x.compo_id and x.compo_id=a.compo_id and x.func_id=e.func_id "
      + "and gg.emp_code=hh.emp_code and hh.posi_code=z.posi_code "
      + "and x.func_id=b.res_id "
      + "and z.role_id=x.role_id and gg.user_id= ? "

      + " union "
      + "select 'fwatch' mID, c.compo_id pID, '查看' mName, "
      + "1 ord_index, 'func' TYPE, c.master_tab_id tabID "
      + "from as_compo_func a, as_lang_trans b, as_compo c, "
      + "v_ap_menu_compo d, as_func e,  "
      + "v_as_role_func x, as_posi_role z, as_emp gg, as_emp_position hh "
      + "where b.res_id=a.func_id and "
      + "b.lang_id='C' and a.compo_id=c.compo_id and d.menu_id= ? "
      + " and d.compo_id=c.compo_id and a.func_id=e.func_id "
      + "and (e.is_grant_to_all is null or upper(e.is_grant_to_all)='N') "
      + " and c.compo_id in "
      + "(select f.compo_id from as_compo_func f where f.compo_id = c.compo_id) "
      + "and a.compo_id=x.compo_id and c.compo_id=x.compo_id and a.func_id=x.func_id "
      + "and d.compo_id=x.compo_id and x.compo_id=a.compo_id and x.func_id=e.func_id "
      + "and gg.emp_code=hh.emp_code and hh.posi_code=z.posi_code "
      + "and x.func_id=b.res_id "
      + "and z.role_id=x.role_id and gg.user_id= ? "

      + " union "
      + "select 'fquote' mID, c.compo_id pID, '引用' mName, "
      + "998 ord_index, 'func' TYPE, c.master_tab_id tabID "
      + "from as_compo_func a, as_lang_trans b, as_compo c, "
      + "v_ap_menu_compo d, as_func e,  "
      + "v_as_role_func x, as_posi_role z, as_emp gg, as_emp_position hh "
      + "where b.res_id=a.func_id and "
      + "b.lang_id='C' and a.compo_id=c.compo_id and d.menu_id= ? "
      + " and d.compo_id=c.compo_id and a.func_id=e.func_id "
      + "and (e.is_grant_to_all is null or upper(e.is_grant_to_all)='N') "
      + " and c.compo_id in "
      + "(select f.compo_id from as_compo_func f where f.compo_id = c.compo_id) "
      + "and a.compo_id=x.compo_id and c.compo_id=x.compo_id and a.func_id=x.func_id "
      + "and d.compo_id=x.compo_id and x.compo_id=a.compo_id and x.func_id=e.func_id "
      + "and gg.emp_code=hh.emp_code and hh.posi_code=z.posi_code "
      + "and x.func_id=b.res_id " + "and z.role_id=x.role_id and gg.user_id= ? "

      + "order by pID, ord_index";
    
    PreparedStatement pst = myConn.prepareStatement(sqlStr);
    int j = 1;
    pst.setString(j++, menuId);
    pst.setString(j++, svUserID);
    pst.setString(j++, menuId);
    pst.setString(j++, svUserID);
    pst.setString(j++, menuId);
    pst.setString(j++, svUserID);
    pst.setString(j++, menuId);
    pst.setString(j++, svUserID);
    pst.setString(j++, menuId);
    pst.setString(j++, svUserID);
    pst.setString(j++, menuId);
    pst.setString(j++, svUserID);
    pst.setString(j++, menuId);
    pst.setString(j++, svUserID);
    pst.setString(j++, menuId);
    pst.setString(j++, svUserID);
    pst.setString(j++, menuId);
    pst.setString(j++, svUserID);
    pst.setString(j++, menuId);
    pst.setString(j++, svUserID);
    
    
    ResultSet rs = pst.executeQuery();
    List menuL = new ArrayList();
    while (rs.next()) {
      String type = rs.getString("TYPE").trim();
      if (type.equals("menu") || type.equals("compo")) {
        menuL.add("p:" + rs.getString(1));
        menuL.add(rs.getString(2));
        menuL.add("Y");
      } else {
        tmp1 = rs.getString(2) + "_" + rs.getString(1);
        menuL.add(tmp1);
        menuL.add(rs.getString(2));
        if (funcL.contains(tmp1)) {
          menuL.add("Y");
        } else {
          menuL.add("N");
        }
      }
      menuL.add(type);
    }
    
    //rs.close();
    //DBHelper.closeConnection(null, null, rs);
    for (int i = 0; i < menuL.size(); i += 4) {
      if (menuL.get(i + 3).toString().indexOf("func") != 0) {
        continue;
      }
      if (menuL.get(i + 2).equals("Y")) {
        continue;
      }
      tmp1 = (String) menuL.get(i + 1);
      while (tmp1 != null && tmp1 != "null") {
        j = menuL.indexOf("p:" + tmp1);
        if (j > -1) {
          tmp1 = (String) menuL.get(j + 1);
          if (menuL.get(j + 2).equals("N")) {
            break;
          }
          menuL.set(j + 2, "N");
        } else {
          break;
        }
      }
    }

    rs = pst.executeQuery();

    while (rs.next()) {

      TableData menuTmp = new TableData();
      tmp1 = rs.getString(1);
      if (tmp1 == null) {
        tmp1 = "";
      }
      menuTmp.setField("REALCODE", tmp1);

      tmp1 = rs.getString(6);
      if (tmp1 == null) {
        tmp1 = "";
      }
      if (!tmp1.equals("")) {
        int i = fieldL.indexOf(tmp1);
        if (i > -1) {
          menuTmp.setField("FIELDLIST", fieldL.get(i + 1));
          menuTmp.setField("NAMELIST", fieldL.get(i + 2));
        }
      }
      menuTmp.setField("TABID", tmp1);

      tmp1 = rs.getString(3);
      if (tmp1 == null) {
        tmp1 = "";
      }
      menuTmp.setField("NAME", tmp1);

      tmp1 = rs.getString(2);
      if (tmp1 == null) {
        tmp1 = "";
      }
      if (tmp1.equalsIgnoreCase(menuId)) {
        menuTmp.setField("P_CODE", "");
      } else {
        menuTmp.setField("P_CODE", tmp1);
      }
      if (rs.getString(5).indexOf("func") == 0) {
        menuTmp.setField("CODE", tmp1 + rs.getString(1));
      } else {
        menuTmp.setField("CODE", rs.getString(1));
      }

      menuTmp.setField("TYPE", rs.getString(5).trim());

      if (rs.getString(5).indexOf("func") == 0) {
        tmp1 = rs.getString(2) + "_" + rs.getString(1);
        int i = menuL.indexOf(tmp1);
        menuTmp.setField("IS_CHECKED", menuL.get(i + 2));

        i = numL.indexOf(tmp1);
        if (i > -1) {
          menuTmp.setField("FIELD", numL.get(i + 1));
          menuTmp.setField("GORR", numL.get(i + 2));
          menuTmp.setField("RANGE", numL.get(i + 3));
        } else {
          menuTmp.setField("FIELD", "");
          menuTmp.setField("GORR", "");
          menuTmp.setField("RANGE", "");
        }
      } else {
        menuTmp.setField("FIELD", "");
        menuTmp.setField("GORR", "");
        menuTmp.setField("RANGE", "");
        tmp1 = rs.getString(1);
        int i = menuL.indexOf("p:" + tmp1);
        menuTmp.setField("IS_CHECKED", menuL.get(i + 2));
      }
      menuD.add(menuTmp);
    }
    
    DBHelper.closeConnection(null, pst, rs);
    String result = null;
    String menuName = (String)DBHelper.queryOneValue("select menu_name from v_ap_menu where menu_id = ?", new Object[]{menuId});
    TreeViewList tvl = new TreeViewList();
    Writer writer = null;
    HttpServletResponse response = ServletActionContext.getResponse();
    if(response != null){
      try {
        response.setContentType("text/xml; charset=GBK");
        writer = response.getWriter();
      } catch (IOException e) {
        log.debug(e);
      }
    }
    if(writer != null){
      result = tvl.getTreeWithRoot(menuD, menuName, writer);
    }else{
      result = tvl.getTreeWithRoot(menuD, menuName);
    }
    
    return result;    
  }
  
  /**
   * 保存用户权限
   * @param userId
   * @param delta
   */
  public boolean saveUserPriv(String userId, Delta delta){
    String sqlStr = null;
    TableData entity = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    Connection myConn = null;
    Delta copyDelta= new Delta();
    copyDelta.addAll(delta);

    try {
      myConn = DAOFactory.getInstance().getConnection();
      //myConn.setAutoCommit(false);
      
      while (delta.size() > 0) {
        entity = (TableData) delta.get(0);
        delta.remove(0);
        String compoId = null;
        String pCode = (String) entity.getField("P_CODE");
        if (pCode.length() != 0) {
          compoId = pCode;
        }

        if (entity.getFieldValue("TYPE").indexOf("func") != 0){
          continue;
        }
        
        String ctrlField = (String) entity.getField("REALCODE");
        sqlStr = "select * from  as_user_func where user_id = ? and compo_id = ? and func_id = ? ";
        pst = myConn.prepareStatement(sqlStr);
        
        int i = 1;
        pst.setString(i++, userId);
        pst.setString(i++, compoId);
        pst.setString(i++, ctrlField);
        rs = pst.executeQuery();
        if (!rs.next()) {         
          if (entity.getField("IS_CHECKED").equals("Y")) {          
            sqlStr = "insert into as_user_func(user_id, compo_id, func_id) values (?, ?, ?)";
            
            //DBHelper.closeConnection(null, pst, rs);
            
            pst = myConn.prepareStatement(sqlStr);
            
            i = 1;
            pst.setString(i++, userId);
            pst.setString(i++, compoId);
            pst.setString(i++, ctrlField);
            
            pst.executeUpdate();
          }
          
        } 
        else{
          if (entity.getField("IS_CHECKED").equals("N")) {          
            sqlStr = "delete from  as_user_func where user_id = ? and compo_id = ? and func_id = ?";
            
            //DBHelper.closeConnection(null, pst, rs);
            
            pst = myConn.prepareStatement(sqlStr);
            
            i = 1;
            pst.setString(i++, userId);
            pst.setString(i++, compoId);
            pst.setString(i++, ctrlField);
            
            pst.executeUpdate();
          }
        }
        //DBHelper.closeConnection(null, pst, rs);
      }
      
      //myConn.commit();
      
      saveUserAsCompanyFunc(userId, copyDelta);//对部件AS_COMPANY的级联权限设置
      
    } catch (SQLException ex) {
      throw new RuntimeException("GeneBean的saveUserFunc方法：" + ex.toString());
    } finally {
      DBHelper.closeConnection(myConn, pst, rs);
    }
    
    return true;
  }
  
  /**
   * 保存用户权限
   * @param roleId
   * @param delta
   */
  public boolean saveRolePriv(String roleId, Delta delta){
    String sqlStr = null;
    TableData entity = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    Connection myConn = null;
    Delta copyDelta= new Delta();
    copyDelta.addAll(delta);

    try {
      myConn = DAOFactory.getInstance().getConnection();
      //myConn.setAutoCommit(false);
      
      while (delta.size() > 0) {
        entity = (TableData) delta.get(0);
        delta.remove(0);
        String compoId = null;
        String pCode = (String) entity.getField("P_CODE");
        if (pCode.length() != 0) {
          compoId = pCode;
        }

        if (entity.getFieldValue("TYPE").indexOf("func") != 0){
          continue;
        }
        
        String ctrlField = (String) entity.getField("REALCODE");
        sqlStr = " select * from  v_as_role_func where role_id = ? and compo_id = ? and func_id = ? ";
        pst = myConn.prepareStatement(sqlStr);
        
        int i = 1;
        pst.setString(i++, roleId);
        pst.setString(i++, compoId);
        pst.setString(i++, ctrlField);
        
        rs = pst.executeQuery();
        
        if (!rs.next()){//数据库中不存在                
          if (entity.getField("IS_CHECKED").equals("Y")) {
            sqlStr = " insert into as_role_func(role_id,compo_id,func_id) values (?, ?, ?) ";
            //DBHelper.closeConnection(null, pst, rs);
            pst = myConn.prepareStatement(sqlStr);
            
            int index = 1;
            pst.setString(index++, roleId);
            pst.setString(index++, compoId);
            pst.setString(index++, ctrlField);
            
            pst.executeUpdate();
            
          }
        }
        else{// 数据库中存在
          if (entity.getField("IS_CHECKED").equals("N")) {
            sqlStr = " delete from  as_role_func where role_id = ? and compo_id = ? and func_id = ? ";
            //DBHelper.closeConnection(null, pst, rs);
            pst = myConn.prepareStatement(sqlStr);
            
            int index = 1;
            pst.setString(index++, roleId);
            pst.setString(index++, compoId);
            pst.setString(index++, ctrlField);
            
            pst.executeUpdate();
            
          }
        }
        //DBHelper.closeConnection(null, pst, rs);
      }
      
      //myConn.commit();
      
      saveAsCompanyFunc(roleId, copyDelta);
      
    } catch (SQLException ex) {
      throw new RuntimeException("saveRolePriv的saveRolePriv方法：" + ex.toString());
    } finally {
      DBHelper.closeConnection(myConn, pst, rs);
    }
    
    return true;
  }

  /**
   * 保存对部件AS_COMPANY的用户的级联功能权限设置
   * @param userId 用户id
   * @param delta 前端页面上得到的功能权限设置数据
   */
  private void saveUserAsCompanyFunc(String userId, Delta delta){
    Delta relateMa= getRelateMa(delta);
    if (relateMa.size()> 0)
      saveUserPriv(userId, relateMa);
  }

  /**
   * 保存对部件AS_COMPANY的角色级联功能权限设置
   * @param roleId 角色id
   * @param delta 前端页面上得到的功能权限设置数据
   */
  private void saveAsCompanyFunc(String roleId, Delta delta){
    Delta relateMa= getRelateMa(delta);
    if (relateMa.size()> 0)
      saveRolePriv(roleId, relateMa);
  }

  /**
   * 得到与部件MA_COMPANY有关的功能权限设置数据
   * @param delta 前端页面上得到的功能权限设置数据
   * @return
   */
  private Delta getRelateMa(Delta delta){
    Delta result= new Delta();
    Iterator iter= delta.iterator();
    TableData entity= null;
    String compoId= null, funcId= null, type= null;
    while (iter.hasNext()){
      entity= (TableData)iter.next();
      compoId= (String) entity.getField("P_CODE");
      type= (String)entity.getField("TYPE");
      funcId= (String) entity.getField("REALCODE");
      if (compoId.equals("MA_COMPANY") && type.indexOf("func")>= 0){
        if (funcId.equalsIgnoreCase("fwatch") || funcId.equalsIgnoreCase("fquote")){
          entity.setField("P_CODE","AS_COMPANY");
          result.add(entity);
        }
      }
    }
    return result;
  }
  
  /**
   * 保存角色的部件字段及关联部件字段的功能权限值
   * @param roleId
   * @param funcId
   * @param compoId
   * @param conditionDelta
   * @return
   */
  public boolean saveRoleFieldRight(String roleId, String funcId, String compoId,
    Delta conditionDelta) {

    Connection conn = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    Delta copyDelta = new Delta();
    copyDelta.addAll(conditionDelta);

    try {
      conn = DAOFactory.getInstance().getConnection();
      //conn.setAutoCommit(false);//TODO
      
      String sql = " select role_id from v_as_role_func where role_id = ? and compo_id = ? and func_id = ? ";
      
      pst = conn.prepareStatement(sql);
      int index = 1;      
      pst.setString(index++, roleId);
      pst.setString(index++, compoId);
      pst.setString(index++, funcId);
      
      rs = pst.executeQuery();
      if(!rs.next()){
        sql = " insert into as_role_func(role_id, compo_id, func_id) values (?, ?, ?) ";
        pst = conn.prepareStatement(sql);
      
        index = 1;      
        pst.setString(index++, roleId);
        pst.setString(index++, compoId);
        pst.setString(index++, funcId);
        pst.executeUpdate(); 
      }
      
      //DBHelper.closeConnection(null, pst, rs);
      sql = " delete from AS_ROLE_NUM_LIM where role_id = ? and compo_id = ? and func_id = ? ";
      pst = conn.prepareStatement(sql);
      
      index = 1;      
      pst.setString(index++, roleId);
      pst.setString(index++, compoId);
      pst.setString(index++, funcId);
      pst.executeUpdate();      
      //DBHelper.closeConnection(null, pst, null);
      
      TableData tmp = null;
      String ctrlField, granRange, revoRange, isGrant, gorr, gorrCompo;
      String noGorr, noGorrCompo;
      boolean needInsert = true;
      String insertSql = " insert into AS_ROLE_NUM_LIM(ROLE_ID,FUNC_ID,COMPO_ID,CTRL_FIELD,GRAN_RANGE,REVO_RANGE,IS_GRAN,IS_RELATION) ";
      
      for (Iterator iter = conditionDelta.iterator(); iter.hasNext();) {
        needInsert = true;
        tmp = (TableData) iter.next();
        ctrlField = tmp.getFieldValue("fieldname");
        isGrant = tmp.getFieldValue("grant");
        gorr = tmp.getFieldValue("gorr");
        gorrCompo = tmp.getFieldValue("gorrCompo");
        noGorr = tmp.getFieldValue("noGorr");
        noGorrCompo = tmp.getFieldValue("noGorrCompo");
        granRange = "";
        revoRange = "";
        if (isGrant.equalsIgnoreCase("0")) {
          if (tmp.getFieldValue("range").length() > 0) {
            granRange = tmp.getFieldValue("range");
          } else {
            needInsert = false;
          }
        } else if (isGrant.equalsIgnoreCase("1")) {
          if (tmp.getFieldValue("range").length() > 0) {
            revoRange = tmp.getFieldValue("range");
          } else {
            needInsert = false;
          }
        }
        if (needInsert) {
          insertSql += " values(?,?,?,?,'" + StringTools.doubleApos(granRange) + "','" 
                    + StringTools.doubleApos(revoRange) + "',?,?) ";
          pst = conn.prepareStatement(insertSql);
          index = 1;
          pst.setString(index++, roleId);
          pst.setString(index++, funcId);
          pst.setString(index++, compoId);
          pst.setString(index++, ctrlField);
          //pst.setString(index++, granRange);
          //pst.setString(index++, revoRange);
          pst.setString(index++, isGrant);
          pst.setString(index++, "N");
          pst.executeUpdate();
          //DBHelper.closeConnection(null, pst, null);
          int i = 0;
          int j = 0;
          int x = 0;
          int y = 0;
          
          // 删除部件字段的没选上的关联字段
          if (noGorr.length() > 0) {
            String tmpNoGorr;
            String tmpNoCompoId;
            while ((j = noGorr.indexOf(',', i)) != -1) {
              y = noGorrCompo.indexOf(",", x);
              tmpNoGorr = noGorr.substring(i, j);
              tmpNoCompoId = noGorrCompo.substring(x, y);
              sql = " delete from AS_ROLE_NUM_LIM where ROLE_ID = ? and FUNC_ID = ? and COMPO_ID = ? "
                  + " and CTRL_FIELD = ? and upper(IS_Relation) = 'Y' ";
              pst = conn.prepareStatement(sql);
              
              index = 1;
              pst.setString(index++, roleId);
              pst.setString(index++, funcId);
              pst.setString(index++, tmpNoCompoId);
              pst.setString(index++, tmpNoGorr);
              
              pst.executeUpdate();
              //DBHelper.closeConnection(null, pst, null);
              i = j + 1;
              x = y + 1;
            }
          }
          // 删除部件字段的选上的关联字段权限值，并插入字段的新的关联值权限
          if (gorr.length() > 0) {
            String tmpGorr;
            String tmpCompoId;
            i = 0;
            j = 0;
            x = 0;
            y = 0;
            while ((j = gorr.indexOf(',', i)) != -1) {
              y = gorrCompo.indexOf(",", x);
              tmpGorr = gorr.substring(i, j);
              tmpCompoId = gorrCompo.substring(x, y);
              
              sql = " delete from as_role_func where role_id = ? and compo_id = ? and func_id = ? ";
              
              pst = conn.prepareStatement(sql);
              index = 1;
              pst.setString(index++, roleId);
              pst.setString(index++, tmpCompoId);
              pst.setString(index++, funcId);
              pst.executeUpdate();
              //DBHelper.closeConnection(null, pst, null);
              
              sql = "insert into as_role_func(role_id, compo_id, func_id) values (?, ?, ?) ";
              
              pst = conn.prepareStatement(sql);
              index = 1;
              pst.setString(index++, roleId);
              pst.setString(index++, tmpCompoId);
              pst.setString(index++, funcId);
              pst.executeUpdate();
              //DBHelper.closeConnection(null, pst, null);
              
              sql = " delete from AS_ROLE_NUM_LIM where ROLE_ID = ? and FUNC_ID = ? and COMPO_ID = ? and CTRL_FIELD = ? ";
              
              pst = conn.prepareStatement(sql);
              index = 1;
              pst.setString(index++, roleId);
              pst.setString(index++, funcId);
              pst.setString(index++, tmpCompoId);
              pst.setString(index++, tmpGorr);
              pst.executeUpdate();              
              //DBHelper.closeConnection(null, pst, null);
              
              pst = conn.prepareStatement(insertSql);
              index = 1;
              pst.setString(index++, roleId);
              pst.setString(index++, funcId);
              pst.setString(index++, tmpCompoId);
              pst.setString(index++, tmpGorr);
              pst.setString(index++, granRange);
              pst.setString(index++, revoRange);
              pst.setString(index++, isGrant);
              pst.setString(index++, "Y");
              pst.executeUpdate();
              //DBHelper.closeConnection(null, pst, null);
              
              i = j + 1;
              x = y + 1;
            }
          }
        } else {

          sql = " select f.role_id,f.func_id,f.compo_id,f.ctrl_field from as_tab_col a, as_lang_trans b, as_foreign_entity c, as_compo d, as_compo e,AS_ROLE_NUM_LIM f "
              + " where f.compo_id = d.compo_id and f.ctrl_field = a.data_item and f.role_id= ? and f.func_id = ? and upper(f.Is_relation)='Y' "
              + " and a.tab_id = d.master_tab_id and a.tab_id = c.tab_id and a.data_item = b.res_id and a.tab_id not like '%temp%' and b.lang_id = 'C' "
              + " and a.f_field = ? and upper(a.is_fpk)='Y' and a.f_ref_name = c.f_ref_name and e.compo_id = ? and e.compo_id = c.f_compo_id ";
          pst = conn.prepareStatement(sql);
          
          index = 1;
          pst.setString(index++, roleId);
          pst.setString(index++, funcId);
          pst.setString(index++, ctrlField);
          pst.setString(index++, compoId);
          
          rs = pst.executeQuery();
          
          List alist = new ArrayList();
          while (rs.next()) {
            RightBean r = new RightBean();
            r.setCompoId(rs.getString("compo_id"));
            r.setRoleId(rs.getString("role_id"));
            r.setFuncId(rs.getString("func_id"));
            r.setCtrlField(rs.getString("ctrl_field"));
            alist.add(r);
          }
          //DBHelper.closeConnection(null, pst, rs);
          
          for (int i = 0; i < alist.size(); i++) {
            RightBean r = (RightBean) alist.get(i);
            sql = " delete from AS_ROLE_NUM_LIM where role_id = ? and func_id = ? and compo_id = ? and ctrl_field = ? ";
            pst = conn.prepareStatement(sql);
            
            index = 1;
            pst.setString(index++, r.getRoleId());
            pst.setString(index++, r.getFuncId());
            pst.setString(index++, r.getCompoId());
            pst.setString(index++, r.getCtrlField());
            
            pst.executeUpdate();
            //DBHelper.closeConnection(null, pst, null);
          }
          
        }
      }
      
      //saveRoleAsCompanyFieldRight(roleId, funcId, compoId, copyDelta);
      
      //conn.commit();
      return true;
    } catch (SQLException se) {
      log.debug(se);
      throw new RuntimeException("类RightService方法saveRoleFieldList()执行错误！"
        + se.toString());
    } finally {
      DBHelper.closeConnection(conn, pst, rs);
    }
  }

  /**
   * 保存用户的部件字段及关联部件字段的功能权限值
   * 
   * @param userId
   *          用户名。
   * @param funcId
   *          功能名。
   * @param compoId
   *          部件名。
   * @param conditionDelta
   *          字段的权限值列表。
   * @return String
   */
  public boolean saveUserFieldRight(String userId, String funcId, String compoId,
    Delta conditionDelta) {

    Connection conn = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    Delta copyDelta = new Delta();
    copyDelta.addAll(conditionDelta);

    try {
      conn = DAOFactory.getInstance().getConnection();
      //conn.setAutoCommit(false);//TODO
      
      // 删除as_user_func里的数据，保证插入不重复
      String sql = " select user_id from as_user_func where user_id = ? and compo_id = ? and func_id = ? ";
      pst = conn.prepareStatement(sql);
      int index = 1;
      pst.setString(index++, userId);
      pst.setString(index++, compoId);
      pst.setString(index++, funcId);
      
      rs = pst.executeQuery();
      if(!rs.next()){
        sql = " insert into as_user_func(user_id,compo_id,func_id) values (?, ?, ?) ";
        pst = conn.prepareStatement(sql);
        index = 1;
        pst.setString(index++, userId);
        pst.setString(index++, compoId);
        pst.setString(index++, funcId);      
        pst.executeUpdate();
        
      }
      //DBHelper.closeConnection(null, pst, rs);
      
      sql = "delete from AS_USER_NUM_LIM where USER_ID = ? and COMPO_ID = ? and FUNC_ID = ? ";
      pst = conn.prepareStatement(sql);
      index = 1;
      pst.setString(index++, userId);
      pst.setString(index++, compoId);
      pst.setString(index++, funcId);      
      pst.executeUpdate();
      //DBHelper.closeConnection(null, pst,null);
      
      TableData tmp = null;
      String ctrlField, granRange, revoRange, isGrant, gorr, gorrCompo;
      String noGorr, noGorrCompo;
      boolean needInsert = true;
  
      for (Iterator iter = conditionDelta.iterator(); iter.hasNext();) {
    	String insertSql = "insert into AS_USER_NUM_LIM(USER_ID,FUNC_ID,COMPO_ID,CTRL_FIELD,GRAN_RANGE,REVO_RANGE,IS_GRAN,IS_RELATION) ";
        needInsert = true;
        tmp = (TableData) iter.next();
        ctrlField = tmp.getFieldValue("fieldname");
        isGrant = tmp.getFieldValue("grant");
        gorr = tmp.getFieldValue("gorr");
        gorrCompo = tmp.getFieldValue("gorrCompo");
        noGorr = tmp.getFieldValue("noGorr");
        noGorrCompo = tmp.getFieldValue("noGorrCompo");
        granRange = "";
        revoRange = "";

        if (isGrant.equalsIgnoreCase("0")) {
          if (tmp.getFieldValue("range").length() > 0) {
            granRange = tmp.getFieldValue("range");
          } else {
            needInsert = false;
          }
        } else if (isGrant.equalsIgnoreCase("1")) {
          if (tmp.getFieldValue("range").length() > 0) {
            revoRange = tmp.getFieldValue("range");
          } else {
            needInsert = false;
          }
        }
        if (needInsert) {
          insertSql += " values(?,?,?,?,'" + StringTools.doubleApos(granRange) + "','" 
                    + StringTools.doubleApos(revoRange) + "',?,?)";
          pst = conn.prepareStatement(insertSql);
          index = 1;
          pst.setString(index++, userId);
          pst.setString(index++, funcId);
          pst.setString(index++, compoId);
          pst.setString(index++, ctrlField);
          //pst.setString(index++, granRange);
          //pst.setString(index++, revoRange);
          pst.setString(index++, isGrant);
          pst.setString(index++, "N");
          pst.executeUpdate();
          //DBHelper.closeConnection(null, pst,null);
          
          int i = 0;
          int j = 0;
          int x = 0;
          int y = 0;
          // 删除部件字段的没选上的关联字段
          if (noGorr.length() > 0) {
            String tmpNoGorr;
            String tmpNoCompoId;
            while ((j = noGorr.indexOf(',', i)) != -1) {
              y = noGorrCompo.indexOf(",", x);
              tmpNoGorr = noGorr.substring(i, j);
              tmpNoCompoId = noGorrCompo.substring(x, y);
              sql = " delete from AS_USER_NUM_LIM where USER_ID = ? and FUNC_ID = ?  and COMPO_ID = ? " 
                  + " and CTRL_FIELD = ?  and upper(Is_Relation) = 'Y' ";
              
              pst = conn.prepareStatement(sql);
              index = 1;
              pst.setString(index++, userId);
              pst.setString(index++, funcId);
              pst.setString(index++, tmpNoCompoId);
              pst.setString(index++, tmpNoGorr);
              pst.executeUpdate();
              //DBHelper.closeConnection(null, pst,null);
              
              i = j + 1;
              x = y + 1;
            }
          }
          // 删除部件字段的选上的关联字段权限值，并插入字段的新的关联值权限
          if (gorr.length() > 0) {
            String tmpGorr;
            String tmpCompoId;
            i = 0;
            j = 0;
            x = 0;
            y = 0;
            while ((j = gorr.indexOf(',', i)) != -1) {
              y = gorrCompo.indexOf(",", x);
              tmpGorr = gorr.substring(i, j);
              tmpCompoId = gorrCompo.substring(x, y);
              sql = " delete from as_user_func where user_id = ? and compo_id = ? and func_id = ? ";
              
              pst = conn.prepareStatement(sql);
              index = 1;
              pst.setString(index++, userId);
              pst.setString(index++, tmpCompoId);
              pst.setString(index++, funcId);
              pst.executeUpdate();
              //DBHelper.closeConnection(null, pst,null);
              
              sql = " insert into as_user_func(user_id,compo_id,func_id) values(?, ?, ?) ";
              
              pst = conn.prepareStatement(sql);
              index = 1;
              pst.setString(index++, userId);
              pst.setString(index++, tmpCompoId);
              pst.setString(index++, funcId);
              pst.executeUpdate();
              //DBHelper.closeConnection(null, pst,null);
              
              sql = " delete from AS_USER_NUM_LIM where USER_ID = ? and FUNC_ID = ? and COMPO_ID = ? and CTRL_FIELD = ? ";
              pst = conn.prepareStatement(sql);
              index = 1;
              pst.setString(index++, userId);
              pst.setString(index++, funcId);
              pst.setString(index++, tmpCompoId);
              pst.setString(index++, tmpGorr);
              pst.executeUpdate();
              //DBHelper.closeConnection(null, pst,null);
              
              pst = conn.prepareStatement(insertSql);
              index = 1;
              pst.setString(index++, userId);
              pst.setString(index++, funcId);
              pst.setString(index++, tmpCompoId);
              pst.setString(index++, tmpGorr);
              pst.setString(index++, granRange);
              pst.setString(index++, revoRange);
              pst.setString(index++, isGrant);
              pst.setString(index++, "Y");
              pst.executeUpdate();
              //DBHelper.closeConnection(null, pst,null);
              
              i = j + 1;
              x = y + 1;
            }
          }
        } else {
          sql = " select f.user_id,f.func_id,f.compo_id,f.ctrl_field from as_tab_col a, as_lang_trans b, as_foreign_entity c, as_compo d, as_compo e,AS_USER_NUM_LIM f "
              + " where f.compo_id = d.compo_id and f.ctrl_field = a.data_item and f.user_id = ? and f.func_id = ? and upper(f.Is_relation)='Y' "
              + " and a.tab_id = d.master_tab_id and a.tab_id = c.tab_id and a.data_item = b.res_id and a.tab_id not like '%temp%' and b.lang_id = 'C' "
              + " and a.f_field = ? and upper(a.is_fpk)='Y' and a.f_ref_name = c.f_ref_name and e.compo_id = ? and e.compo_id = c.f_compo_id ";
          pst = conn.prepareStatement(sql);
          index = 1;
          pst.setString(index++, userId);
          pst.setString(index++, funcId);
          pst.setString(index++, ctrlField);
          pst.setString(index++, compoId);
          
          rs = pst.executeQuery();
          
          List alist = new ArrayList();
          while (rs.next()) {
            RightBean r = new RightBean();
            r.setCompoId(rs.getString("compo_id"));
            r.setUserId(rs.getString("user_id"));
            r.setFuncId(rs.getString("func_id"));
            r.setCtrlField(rs.getString("ctrl_field"));
            alist.add(r);
          }
          //rs.close();
          //DBHelper.closeConnection(null, pst,rs);
          
          if(alist != null && !alist.isEmpty()){
            for (int i = 0; i < alist.size(); i++) {
              RightBean r = (RightBean) alist.get(i);
              sql = " delete from AS_USER_NUM_LIM where user_id = ? and func_id = ? and compo_id = ? and ctrl_field = ? ";
              pst = conn.prepareStatement(sql);
              index = 1;
              pst.setString(index++, r.getUserId());
              pst.setString(index++, r.getFuncId());
              pst.setString(index++, r.getCompoId());
              pst.setString(index++, r.getCtrlField());
              pst.executeUpdate();
              //DBHelper.closeConnection(null, pst,null);
            }
          }
        }
      }
      
      //saveUserAsCompanyFieldRight(userId, funcId, compoId, copyDelta);
      
      //conn.commit();
      return true;
      
    } catch (SQLException se) {
      log.debug(se);
      throw new RuntimeException("类RightService方法saveRoleFieldList()执行错误！"
        + se.toString());
    } finally {
      DBHelper.closeConnection(conn, pst, rs);
    }
  }
  
  /**
   * 保存用户对部件AS_COMPANY的某个功能的数值权限
   * 
   * @param userId:
   *          用户id
   * @param funcId:
   *          函数功能id
   * @param compoId:
   *          部件id
   * @param conditionDelta:
   *          数值权限数据包
   */
  private void saveUserAsCompanyFieldRight(String userId, String funcId,
    String compoId, Delta conditionDelta) {
    if (compoId.equals("MA_COMPANY")
      && (funcId.equalsIgnoreCase("fwatch") || funcId.equalsIgnoreCase("fquote"))) {

      Delta relateMa = getRelateMa(conditionDelta);
      if (relateMa.size() > 0)
        saveUserFieldRight(userId, funcId, "AS_COMPANY", relateMa);
    }
  }

  /**
   * 保存角色对部件AS_COMPANY的某个功能的数值权限
   * 
   * @param roleId:
   *          角色id
   * @param funcId:
   *          函数功能id
   * @param compoId:
   *          部件id
   * @param conditionDelta:
   *          数值权限数据包
   */
  public void saveRoleAsCompanyFieldRight(String roleId, String funcId,
    String compoId, Delta conditionDelta) {
    if (compoId.equals("MA_COMPANY")
      && (funcId.equalsIgnoreCase("fwatch") || funcId.equalsIgnoreCase("fquote"))) {
      Delta relateMa = getRelateMa(conditionDelta);
      if (relateMa.size() > 0)
        saveRoleFieldRight(roleId, funcId, "AS_COMPANY", relateMa);
    }
  }
  
  /**
   * 职员权限复制
   * @param empCodeS
   * @param empCodeD
   * @param userID
   */
  public boolean empCopy(String empCodeS, String empCodeD, String userID, String nd) {
    Connection conn = null;
    boolean result = true;
    try {
      conn = DAOFactory.getInstance().getConnection();
      String userS = getUsrIdS(empCodeS, conn);

      deleteEmp(empCodeD, userID, nd, conn);
      copyEmp(empCodeS, empCodeD, userS, userID, nd, conn);
    } catch (SQLException e) {
      log.debug(e);
      result = false;
    } finally {
      DBHelper.closeConnection(conn);
    }
    return result;
  }
  
  /**
   * 删除人员权限
   * @param empCodeD
   * @param userID
   * @param conn
   * @throws SQLException
   */
  private void deleteEmp(String empCodeD, String userID, String nd, Connection conn) throws SQLException {
    String sql = " delete from AS_EMP_POSITION where EMP_CODE = ? and nd = ? ";
    DBHelper.executeUpdate(conn, sql, new Object[]{empCodeD, nd});
    
    sql = " delete from AS_EMP_ROLE where EMP_CODE = ? ";
    DBHelper.executeUpdate(conn, sql, new Object[]{empCodeD});
    
    if (userID == null || userID.equals("")) {
      return;
    }
    
    sql = " delete from AS_USER_FUNC where USER_ID = ? ";
    DBHelper.executeUpdate(conn, sql, new Object[]{userID});
    
    sql = " delete from AS_USER_NUM_LIM where USER_ID = ? ";
    DBHelper.executeUpdate(conn, sql, new Object[]{userID});
  }
  
  /**
   * 复制人员权限
   * @param empCodeS
   * @param empCodeD
   * @param userS
   * @param userID
   * @param conn
   * @throws SQLException
   */
  
  private void copyEmp(String empCodeS, String empCodeD, String userS,
    String userID, String nd, Connection conn) throws SQLException {
    
    copyUserGroup(userS, userID, conn);
    copyEmpPosition(empCodeS, empCodeD, nd, conn);
    copyEmpRole(empCodeS, empCodeD, conn);
    if (userS == null || userS.equals("") || userID == null || userID.equals("")) {
      return;
    }
    copyUserFunc(userS, userID, conn);
    copyUserNumLim(userS, userID, conn);
  }
  
  /**
   * 复制用户的用户组：先删除当前用户的用户组，复制目标用户的用户组
   * @param userS
   * @param userD
   * @param conn
   * @throws SQLException
   */
  private void copyUserGroup(String userS, String userD, Connection conn) throws SQLException{
    //String delSql = " delete as_user_group where user_id = ?";
    StringBuffer sb = new StringBuffer();
    sb.append(" insert into as_user_group(user_id, group_id) ");
    sb.append(" select '");
    sb.append(userD);
    sb.append("', group_id ");
    sb.append(" from v_as_user_group where user_id = ? and group_id not in(select group_id from as_user_group where user_id = ?) ");
    
    //DBHelper.executeUpdate(conn, delSql, new Object[]{userS});
    DBHelper.executeUpdate(conn, sb.toString(), new Object[]{userS, userD});
  }
  
  
  private void copyEmpPosition(String empCodeS, String empCodeD, String nd, Connection conn) throws SQLException {
    StringBuffer sb = new StringBuffer();
    sb.append(" insert into AS_EMP_POSITION(EMP_CODE, POSI_CODE, ORG_CODE, CO_CODE, ND) ");
    sb.append(" select '");
    sb.append(empCodeD);
    sb.append("' ,POSI_CODE, ORG_CODE, CO_CODE, ND ");
    sb.append(" from AS_EMP_POSITION where EMP_CODE = ? and nd = ? ");//TODO 加上年度过滤
    
    DBHelper.executeUpdate(conn, sb.toString(), new Object[]{empCodeS, nd});
  }
  
  private void copyEmpRole(String empCodeS, String empCodeD, Connection conn) throws SQLException {
    StringBuffer sb = new StringBuffer();
    sb.append(" insert into AS_EMP_ROLE(EMP_CODE, ROLE_ID) ");
    sb.append(" select '");
    sb.append(empCodeD);
    sb.append("' , ROLE_ID from AS_EMP_ROLE where EMP_CODE = ? ");
    
    DBHelper.executeUpdate(conn, sb.toString(), new Object[]{empCodeS});
  }

  private void copyUserFunc(String userS, String userID, Connection conn) throws SQLException {
    StringBuffer sb = new StringBuffer();
    sb.append(" insert into AS_USER_FUNC(USER_ID, COMPO_ID, FUNC_ID) ");
    sb.append(" select '");
    sb.append(userID);
    sb.append("' , COMPO_ID, FUNC_ID from AS_USER_FUNC where USER_ID = ? ");
    
    DBHelper.executeUpdate(conn, sb.toString(), new Object[]{userS});   
  }

  private void copyUserNumLim(String userS, String userID, Connection conn) throws SQLException {
    StringBuffer sb = new StringBuffer();
    sb.append(" insert into AS_USER_NUM_LIM(USER_ID, COMPO_ID, FUNC_ID, CTRL_FIELD, GRAN_RANGE, REVO_RANGE, IS_GRAN, IS_RELATION) ");
    sb.append(" select '");
    sb.append(userID);
    sb.append("', COMPO_ID, FUNC_ID, CTRL_FIELD, GRAN_RANGE, REVO_RANGE, IS_GRAN, IS_RELATION ");
    sb.append(" from AS_USER_NUM_LIM where USER_ID = ? ");
    
    DBHelper.executeUpdate(conn, sb.toString(), new Object[]{userS});
  }
  
  private String getUsrIdS(String empCodeS, Connection conn) throws SQLException {
    String sql = " select USER_ID from AS_EMP where EMP_CODE = ? ";
    return (String)DBHelper.queryOneValue(conn, sql, new Object[]{empCodeS});
  }
  
  /**
   * 角色权限复制
   * @param roleIdS
   * @param roleIdD
   */
  public boolean roleCopy(String roleIdS, String roleIdD) {
    Connection conn = null;
    boolean result = true;
    try {
      conn = DAOFactory.getInstance().getConnection();
      deleteRole(roleIdD, conn);
      if (roleIdS.equalsIgnoreCase("sa")) {
        copyRoleSA(roleIdD, conn);
      } else {
        copyRole(roleIdS, roleIdD, conn);
      }
    } catch (SQLException e) {
      log.debug(e);
      result = false;
    } finally {
      DBHelper.closeConnection(conn);
    }
    return result;
  }
  
  private void deleteRole(String roleIdD, Connection conn) throws SQLException {
    String sql = " delete from AS_ROLE_FUNC where ROLE_ID = ? ";
    DBHelper.executeUpdate(conn, sql, new Object[]{roleIdD});
    
    sql = " delete from AS_ROLE_NUM_LIM where ROLE_ID = ? ";
    DBHelper.executeUpdate(conn, sql, new Object[]{roleIdD});
  }

  private void copyRole(String roleIdS, String roleIdD, Connection conn) throws SQLException {
    StringBuffer sb = new StringBuffer();
    sb.append(" insert into AS_ROLE_FUNC(ROLE_ID, COMPO_ID, FUNC_ID) ");
    sb.append(" select '");
    sb.append(roleIdD);
    sb.append("', COMPO_ID, FUNC_ID from V_AS_ROLE_FUNC where ROLE_ID = ? ");
    DBHelper.executeUpdate(conn, sb.toString(), new Object[]{roleIdS});
    
    sb = new StringBuffer();
    sb.append(" insert into as_role_group(role_id, group_id) ");
    sb.append(" select '");
    sb.append(roleIdD);
    sb.append("', group_id ");
    sb.append(" from as_role_group where role_id = ? and group_id not in(select group_id from as_role_group where role_id = ?) ");
    DBHelper.executeUpdate(conn, sb.toString(), new Object[]{roleIdS, roleIdD});
    
    sb = new StringBuffer();
    sb.append(" insert into AS_ROLE_NUM_LIM(ROLE_ID, COMPO_ID, FUNC_ID, CTRL_FIELD, GRAN_RANGE, REVO_RANGE, IS_GRAN, IS_RELATION) ");
    sb.append(" select '");
    sb.append(roleIdD);
    sb.append("', COMPO_ID, FUNC_ID, CTRL_FIELD, GRAN_RANGE, REVO_RANGE, IS_GRAN, IS_RELATION from AS_ROLE_NUM_LIM where ROLE_ID = ? ");
    DBHelper.executeUpdate(conn, sb.toString(), new Object[]{roleIdS});
  }
  
  /**
   * sa角色的复制
   * @param roleIdD
   * @param conn
   * @throws SQLException
   */
  private void copyRoleSA(String roleIdD, Connection conn) throws SQLException {    
    StringBuffer sb = new StringBuffer();
    sb.append(" insert into AS_ROLE_FUNC(ROLE_ID, COMPO_ID, FUNC_ID) ");
    sb.append(" select ");
    sb.append("'");
    sb.append(roleIdD);
    sb.append("'");
    sb.append(", COMPO_ID, FUNC_ID from V_AS_SA_FUNC where upper(FUNC_ID) <> 'FHELP' ");    
    DBHelper.executeUpdate(conn, sb.toString(), null);
    
    sb = new StringBuffer();
    sb.append(" insert into as_role_group(role_id, group_id) ");
    sb.append(" select '");
    sb.append(roleIdD);
    sb.append("', group_id ");
    sb.append(" from as_role_group where role_id = ? and group_id not in(select group_id from as_role_group where role_id = ?) ");
    DBHelper.executeUpdate(conn, sb.toString(), new Object[]{"sa", roleIdD});
    
  }
  
  /**
   * 用户组复制
   * @param groupIdS:源用户组
   * @param groupIdD:目标用户组
   */
  public void copyGroup(String groupIdS, String groupIdD) throws BusinessException{
    List groupPages = selectGroupPages(groupIdS);
    if(groupPages == null || groupPages.isEmpty())
      return;
    
    List appNames = GeneralFunc.getAppNames();
    boolean isPortal = false;
    if(appNames.contains("portal")){
      isPortal = true;
    }
    
    deletePages(groupIdD, isPortal);
    
    for(int i = 0; i < groupPages.size(); i++){
      Title title = (Title)groupPages.get(i);
      String pageId = copyPages(title, groupIdD, isPortal);
      copyMenuTree(title, pageId);
    } 
  }
  
  /**
   * 获取用户组页列表
   * @param groupId
   * @return
   */
  private List selectGroupPages(String groupId){
    TitleControl control = new TitleControl();
    control.setGroupId(groupId);
    return control.getTitleList();
  }
  
  /**
   * 删除页面连同菜单树
   * @param title
   * @throws SQLException
   */
  private void deletePages(String groupId, boolean isPortal) throws BusinessException{
    List groupPages = selectGroupPages(groupId);
    if(groupPages == null || groupPages.isEmpty())
      return;
    
    for(int i = 0; i < groupPages.size(); i++){
      String pageId = ((Title)groupPages.get(i)).getTitleId();
      if(isPortal){
        String delPortletSql = " delete from ap_page_portlet where page_id = ? ";
        Object[] delPortletParams = new Object[]{pageId};
        DBHelper.executeSQL(delPortletSql, delPortletParams);
      }
      
      BaseDao dao = (BaseDao)ApplusContext.getBean("baseDao");
      try{
        Title title = new Title();
        title.setTitleId(pageId);
        dao.delete(SQL_DELETE_COMPO_WITH_MENU, pageId);
        dao.delete(SQL_DELETE_MENU_WITH_PAGE, pageId);
        dao.delete(SQL_DELETE, title);
      }catch(SQLException e){
        log.debug(e);
        throw new BusinessException("RightService类执行deletePage()方法出错！");
      }
    }
  }
  
  /**
   * 复制页信息(ap_group_page, ap_menu)
   * @param titleS
   * @param groupIdD
   * @return
   */
  private String copyPages(Title titleS, String groupIdD, boolean isPortal){
    String pageId = Pub.getUID();
    String pageSql = " insert into ap_group_page(group_id, page_id, page_order, page_title, page_desc, page_url, COLUMNCOUNT) "
                           + " values (?, ?, ?, ?, ?, ?, ?)";
    String portletSql = " insert into ap_page_portlet(page_id, portlet_id, title, colno) "
                   + " select '" + pageId + "', portlet_id, title, colno "
                   + " from ap_page_portlet where page_id = ? ";
    
    Object[] pageParams = new Object[]{groupIdD, pageId, titleS.getIndex() + "", titleS.getTitleName()
                                , titleS.getTitleDesc(), titleS.getTitleUrl(), titleS.getColCount() + ""};
    Object[] portletParams = new Object[]{titleS.getTitleId()};
    
    DBHelper.executeSQL(pageSql, pageParams);
    if(isPortal){
      DBHelper.executeSQL(portletSql, portletParams);
    }
    return pageId;
  }
  
  /**
   * 复制菜单树
   * @param titleS
   * @param pageIdD
   * @throws BusinessException
   */
  private void copyMenuTree(Title titleS, String pageIdD) throws BusinessException{
    TreeBuilder builder = new MenuTreeBuilder();
    Map params = new HashMap();
    params.put("rootCode", titleS.getTitleId());
    params.put("userId", "sa");
    params.put("isOnlyInMenu", "false");
    params.put("coCode", "");
    params.put("orgCode", "");
    params.put("isRemoveEmpty", "false");
    
    Tree tree = builder.generateTree(params);
    if(tree.getRoot() != null){
      tree.getRoot().setCode(pageIdD);
      builder.saveTree(tree, false);
    }
  }
  
}
