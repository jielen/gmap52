package com.anyi.gp.core.action;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.anyi.gp.Pub;
import com.anyi.gp.access.CommonService;
import com.anyi.gp.pub.SessionUtils;
import com.anyi.gp.util.StringTools;
import com.opensymphony.webwork.interceptor.ServletRequestAware;

public class PsdModiTimeAction extends AjaxAction implements ServletRequestAware {

  private static final long serialVersionUID = -6108447970693725543L;

  /**
   * @author guohui
   */
  private HttpServletRequest request;

  private CommonService service;

  public CommonService getService() {
    return service;
  }

  public void setService(CommonService service) {
    this.service = service;
  }

  public void setServletRequest(HttpServletRequest request) {
    this.request = request;
  }

  public String doExecute() throws Exception {
    int day = 0;
    String userId = SessionUtils.getAttribute(request, "svUserID");
    
    Map params = new HashMap();
    params.put("user_Id", userId);
    Object rs = service.getPsdModiTime(params);
    
    if (rs != null) {
      Object temp = ((Map)rs).get("MODI_TIME");
      if(temp != null){
        String smodiTime = temp.toString();
        if(smodiTime.trim().length() > 0){
          SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
          java.util.Date currDate = new java.util.Date();
          java.util.Date oldtime = new java.util.Date();
          try {
            oldtime.setTime(formatter.parse(smodiTime).getTime());
          } catch (Exception e) {
            throw new IllegalArgumentException("PsdModiTimeAction类处理密码修改日期转换错误。");
          }
          day = getDays(oldtime, currDate);
        }
      } else { // 若该用户从未设过密码，则时间为0，且记录当前设置时间。
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        java.util.Date currDate = new java.util.Date();
        String nowtime = formatter.format(currDate);
        
        Map param = new HashMap();
        param.put("modi_time", nowtime);
        param.put("user_Id", userId);
        service.updatePsdModiTime(param);
      }
    }
    
    resultstring = StringTools.XML_TYPE;
    resultstring += "\n" + Pub.makeRetString(true, day + "");
    
    return SUCCESS;
  }

  private int getDays(java.util.Date date1, java.util.Date date2) {
    // date2应大于date1
    int days = 0;
    days = (int) ((date2.getTime() - date1.getTime()) / (24 * 60 * 60 * 1000));
    return days;
  }
}
