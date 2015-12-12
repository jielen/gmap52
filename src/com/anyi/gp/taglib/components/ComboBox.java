package com.anyi.gp.taglib.components;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Arrays;

import org.w3c.dom.Node;

import com.anyi.gp.Pub;
import com.anyi.gp.access.DBSupport;
import com.anyi.gp.context.ApplusContext;
import com.anyi.gp.core.dao.BaseDao;
import com.anyi.gp.pub.DAOFactory;
import com.anyi.gp.pub.DBHelper;
import com.anyi.gp.pub.DataTools;
import com.anyi.gp.pub.RightUtil;
import com.anyi.gp.pub.SessionUtils;
import com.anyi.gp.util.XMLTools;

public class ComboBox extends TextBox {

  private String usercond = "";

  private String sqlid = "";

  private String condition = "";

  private String valuesetcode = "";

  //以上变量进入标记.
  private String options = "";

  private List valuesetlist = null;

  private boolean isexistselected = false;

  private String userNumLimCondition = "";

  //图片资源;
  private String select_button_img = "";
  
  public ComboBox() {
    super();
    this.setBoxtype("ComboBox");
  }

  public void init() {
    super.init();
    select_button_img = Page.LOCAL_RESOURCE_PATH
      + Pub.getWebRoot(this.getContainer().getPage().getCurrRequest())
      + "/gp/image/ico/downbutton_16x17_2D.gif";
    this.setMaxlen(MAX_LEN);
    this.setMinlen(MIN_LEN);
    this.setIsallowinput(false);

    if (this.isIsfromdb()) {
      String userId = SessionUtils.getAttribute(this.getContainer().getPage()
        .getCurrRequest(), "svUserID");
      userNumLimCondition = RightUtil.getUserNumLimCondition(
        this.getContainer().getPage().getCurrRequest(), 
        userId, "fwatch", this.getCompoName(), null, this.getFieldName(), "");
      if (sqlid != null && sqlid.trim().length() != 0) {
        initOptionsBySqlid();
      } else {
        initOptionsByDefault();
      }
    }

    if (options.toUpperCase().indexOf(" SELECTED") > 0) {
      isexistselected = true;
    }
  }

  /**
   * 通过sqlid和condition获取值集（加上数值权限）
   *
   */
  private void initOptionsBySqlid() {
    BaseDao dao = (BaseDao) ApplusContext.getBean("baseDao");
    DBSupport support = (DBSupport) ApplusContext.getBean("dbSupport");
    Map paramsMap = support.parseParamsSimpleForSql(condition);
    List newParams = new ArrayList();
    String sql = dao.getSql(sqlid, paramsMap, newParams);
    if (userNumLimCondition != null && userNumLimCondition.length() > 0) {
      sql = support.wrapSqlByCondtion(sql, userNumLimCondition);
    }
    List result = dao.queryForListBySql(sql, newParams.toArray());
    StringBuffer voBuf = new StringBuffer();
    if (this.isIsallownull()) {
      voBuf.append("<option value=\"\"></option>\n");
    }
//    else if ((this.getValue() == null || this.getValue().equals("")) && !this.isIsallownull() && !result.isEmpty()){
//      Object value = ((Map)result.get(0)).get("VAL_ID");
//      if(value != null){
//        this.setValue(value.toString());
//      }
//    }
    for (int i = 0; i < result.size(); i++) {
      Map map = (Map) result.get(i);
      voBuf.append("<option value=\"" + map.get("VAL_ID") + "\"");
      if (map.get("VAL_ID").toString().equalsIgnoreCase(this.getValue())) {
        voBuf.append(" selected");
        this.setValue(map.get("VAL").toString());
      }
      voBuf.append(">");
      voBuf.append(map.get("VAL"));
      voBuf.append("</option>\n");
    }
    this.options = voBuf.toString();
  }

  /**
   * 从数据库通过描述方式获取值集（加上数值权限）
   *
   */
  private void initOptionsByDefault() {
    if (this.getValuesetlist() == null) {
      String vsValueSetId = this.getFieldmeta().getVscode();
      if (this.getOuterField() != null) {
        vsValueSetId = XMLTools.getNodeAttr(this.getOuterField(), "valuesetcode",
          vsValueSetId);
      }
      if (this.getValuesetcode() != null
        && this.getValuesetcode().trim().length() > 0) {
        vsValueSetId = this.getValuesetcode();
      }
      if (vsValueSetId != null && vsValueSetId.length() > 0) {
        this.setValuesetlist(DataTools.getVS(vsValueSetId, usercond));
        if (this.getValuesetlist() != null) {
          List vals = getHasRightValueSet(vsValueSetId);
          StringBuffer voBuf = new StringBuffer();
          if (this.isIsallownull()) {
            voBuf.append("<option value=\"\"></option>\n");
          }
//          else if ((this.getValue() == null || this.getValue().equals("")) && !this.getValuesetlist().isEmpty()){
//            this.setValue(((String[])this.getValuesetlist().get(0))[0]);
//          }
          for (Iterator iter = this.getValuesetlist().iterator(); iter.hasNext();) {
            String[] vasVal = (String[]) iter.next();
            if (vals == null || vals.contains(vasVal[0])) {
              voBuf.append("<option value=\"" + vasVal[0] + "\"");
              if (vasVal[0].equalsIgnoreCase(this.getValue())) {
                voBuf.append(" selected");
                this.setValue(vasVal[1]);
              }
              voBuf.append(">");
              voBuf.append(vasVal[1]);
              voBuf.append("</option>\n");
            }
          }
          this.options = voBuf.toString();
        }
      }
    }
  }

  /**
   * 得到有权限的ValueSet 的代码集合
   * @param vsValueSetId
   * @return
   */
  private List getHasRightValueSet(String vsValueSetId) {
    Object[] vals = null;
    if (userNumLimCondition != null && userNumLimCondition.length() > 0) {
      userNumLimCondition = userNumLimCondition.replaceAll(this.getFieldName(),
        "VAL_ID");
      String sql = " select VAL_ID from as_val where VALSET_ID = ? and "
        + userNumLimCondition;
      vals = DBHelper.queryOneColumn(DAOFactory.getDataSource(), sql,
        new Object[] { vsValueSetId });
    }
    if (vals == null) {
      return null;
    }
    return Arrays.asList(vals);
  }

  public void initByField(Node tagNode) {
    super.initByField(tagNode);
    this.setSqlid(XMLTools.getNodeAttr(tagNode, "sqlid", this.sqlid));
    this.setCondition(XMLTools.getNodeAttr(tagNode, "condition", this.condition));
  }

  protected String makeFocusBtn() {
    StringBuffer voBuf = new StringBuffer();
    voBuf.append("<input type='button' ");
    voBuf.append("id='" + TextBox.DOMID_FOCUS_BUTTON + "' ");
    voBuf.append("class='clsTextboxFocusBtn'>\n");
    return voBuf.toString();
  }

  protected String makeOtherTD() {
    StringBuffer voSBuf = new StringBuffer();
    voSBuf
      .append("<td id=\"SelectDiv\" width=\"100%\" class=\"clsComboSelectTD\">\n");
    voSBuf.append("<select id=\"Select\" align=\"ABSBOTTOM\" class=\"clsComboSelect\" ");
    voSBuf.append(">");
    voSBuf.append(this.options);
    voSBuf.append("</select>\n");
    voSBuf.append("</td>\n");
    voSBuf.append("<td width=\"16px\"><img type=\"button\" id=\"selectButton\" ");
    voSBuf.append("src=\"");
    voSBuf.append(select_button_img);
    voSBuf.append("\" width=\"16px\" align=\"ABSBOTTOM\" ");
    voSBuf.append("tabindex='32766' ");
    if (this.isIsreadonly()) {
      voSBuf.append("disabled ");
    }
    voSBuf.append("></td>\n");
    return voSBuf.toString();
  }

  protected String makeAttr() {
    StringBuffer voSBuf = new StringBuffer();
    voSBuf.append(super.makeAttr());
    voSBuf.append("isexistselected=\"" + isexistselected + "\" ");
    return voSBuf.toString();
  }

  public void setBodyText(String text) {
    this.options = text;
  }

  private java.util.List getValuesetlist() {
    return valuesetlist;
  }

  public void setValuesetlist(java.util.List valuesetlist) {
    this.valuesetlist = valuesetlist;
  }

  public String getUsercond() {
    return usercond;
  }

  public void setUsercond(String usercond) {
    this.usercond = usercond;
  }

  public String getSqlid() {
    return sqlid;
  }

  public void setSqlid(String sqlid) {
    this.sqlid = sqlid;
  }

  public void setCondition(String condition) {
    this.condition = condition;
  }

  private String getValuesetcode() {
    return valuesetcode;
  }

  public void setValuesetcode(String valuesetcode) {
    this.valuesetcode = valuesetcode;
  }
}
