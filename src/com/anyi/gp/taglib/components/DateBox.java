package com.anyi.gp.taglib.components;

import com.anyi.gp.Pub;

public class DateBox extends TextBox {
  
  public static final String DATE_TYPE_DATE = "date";

  public static final String DATE_TYPE_DATETIME = "datetime";

  public static final String PICK_TYPE_YEAR = "year";

  public static final String PICK_TYPE_MONTH = "month";

  public static final String PICK_TYPE_DAY = "day";

  private String picktype = PICK_TYPE_DAY;

  //以上进入标记属性;

  private String datetype = DateBox.DATE_TYPE_DATE; 

  //图片资源;
  private String select_button_img = "";

  public DateBox(String boxType){
    this.setBoxtype(boxType);
    if(boxType.equalsIgnoreCase("DatetimeBox")){
    	this.setDatetype(DATE_TYPE_DATETIME);
    }
    picktype = PICK_TYPE_DAY;
  }
  
  public void init() {
    super.init();
    select_button_img = Page.LOCAL_RESOURCE_PATH + Pub.getWebRoot(this.getContainer().getPage().getCurrRequest())
      + "/gp/image/ico/calendar_16x16.gif";
  }

  protected String makeOtherTD() {
    String vsReadOnly = (this.isIsreadonly()) ? " disabled " : "";

    StringBuffer voSBuf = new StringBuffer();
    voSBuf.append("<td width='16px'><img id=\"selectButton\" src=\""
      + select_button_img + "\" tabindex='32766'  align=\"ABSBOTTOM\" ");
    voSBuf.append(vsReadOnly + "></td>\n");
    return voSBuf.toString();
  }

  protected String makeAttr() {
    StringBuffer voSBuf = new StringBuffer();
    voSBuf.append(super.makeAttr());
    voSBuf.append("datetype='" + datetype + "' ");
    voSBuf.append("picktype='" + picktype + "' ");
    return voSBuf.toString();
  }

  public void setPicktype(String picktype) {
    this.picktype = picktype;
  }

public void setDatetype(String datetype) {
	this.datetype = datetype;
}
}
