package com.anyi.gp.workflow.action;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.anyi.gp.core.action.AjaxAction;
import com.anyi.gp.pub.DAOFactory;
import com.anyi.gp.pub.DBHelper;
import com.anyi.gp.workflow.util.WFException;

public class GetFirstNodeAction extends AjaxAction {

  /**
   * 
   */
  private static final long serialVersionUID = 5166860638141550580L;

  private String compoId;

  public String getCompoId() {
    return compoId;
  }

  public void setCompoId(String compoId) {
    this.compoId = compoId;
  }

  public String getFirstNode(String compoId){
    String result = "";
    String sql = "SELECT a.default_wf_template, c.next_node_id FROM as_compo a,wf_template b,wf_link c"
      + " WHERE a.default_wf_template=b.template_id AND c.template_id=b.template_id AND c.current_node_id=-1 AND a.compo_id=?";
    Connection conn = null;
    PreparedStatement sta = null;
    ResultSet res = null;
    try {
      conn = DAOFactory.getInstance().getConnection();
      sta = conn.prepareStatement(sql);
      sta.setString(1, compoId);
      res = sta.executeQuery();
      if (res.next())
        result = res.getString(1) + "," + res.getString(2);
    } catch (SQLException ex) {
    	result = ex.getMessage();
    } finally {
      DBHelper.closeConnection(conn, sta, res);
    }
    return result;

  }

  public String doExecute() throws Exception {
    try {
    	resultstring = this.wrapResultStr("true", getFirstNode(compoId));
    } catch (Exception ex) {
      resultstring = this.wrapResultStr("false",ex.getMessage());
    }
    return SUCCESS;
  }

}
