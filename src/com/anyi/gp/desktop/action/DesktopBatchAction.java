package com.anyi.gp.desktop.action;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.anyi.gp.core.action.AjaxAction;
import com.anyi.gp.core.dao.BaseDao;

public class DesktopBatchAction extends AjaxAction {

  /**
   * 
   */
  private static final long serialVersionUID = -8418630922341378359L;

  public static final Logger log = Logger.getLogger(DesktopBatchAction.class);

  private String areaId;

  private String areaName;

  private String areaIndex;

  private String areaImg;

  private String displayAmount;

  private String isDisplayRec;

  private String userId;

  private BaseDao dao;

  public String getAreaId() {
    return areaId;
  }

  public void setAreaId(String areaId) {
    this.areaId = areaId;
  }

  public String getAreaImg() {
    return areaImg;
  }

  public void setAreaImg(String areaImg) {
    this.areaImg = areaImg;
  }

  public String getAreaIndex() {
    return areaIndex;
  }

  public void setAreaIndex(String areaIndex) {
    this.areaIndex = areaIndex;
  }

  public String getAreaName() {
    return areaName;
  }

  public void setAreaName(String areaName) {
    this.areaName = areaName;
  }

  public String getDisplayAmount() {
    return displayAmount;
  }

  public void setDisplayAmount(String displayAmount) {
    this.displayAmount = displayAmount;
  }

  public String getIsDisplayRec() {
    return isDisplayRec;
  }

  public void setIsDisplayRec(String isDisplayRec) {
    this.isDisplayRec = isDisplayRec;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public BaseDao getDao() {
    return dao;
  }

  public void setDao(BaseDao dao) {
    this.dao = dao;
  }

  public String doExecute() throws Exception {
    // TCJLODO Auto-generated method stub
    String dataStr = "";
    String flag = "false";

    String sql = "insert into as_desktop (area_id,area_name,user_id,area_index,area_img,display_amount,is_display_rec)"
      + "values (?,?,?,?,?,?,?)";
    List params = new ArrayList();
    params.add(areaId);
    params.add(areaName);
    params.add(userId);
    params.add(areaIndex);
    params.add(areaImg);
    params.add(displayAmount);
    params.add(isDisplayRec);
    try {
      String[] useridParams = userId.split(",");
      for (int i = 0; i < useridParams.length; i++) {
        String sqlKey = "select * from as_desktop where area_id = ? and user_id = ?";
        Object[] temp = new Object[] { areaId, useridParams[i] };
        List isExist = (List) dao.queryForListBySql(sqlKey, temp);
        if (isExist.size() != 0)
          continue;
        params.set(2, useridParams[i]);
        dao.executeBySql(sql, params.toArray());
        flag = "true";
      }
    } catch (Exception ex) {
      dataStr = ex.getMessage();
    }
    this.resultstring = this.wrapResultStr(flag, dataStr);
    return this.SUCCESS;
  }

}
