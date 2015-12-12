package com.anyi.gp.core.action;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.anyi.gp.core.dao.BaseDao;
import com.anyi.gp.pub.SessionUtils;
import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.xwork.ActionSupport;

public class GetAdminPathAction extends ActionSupport {

	private static final long serialVersionUID = 1L;
	private BaseDao baseDAO;
	
	public BaseDao getBaseDAO() {
		return baseDAO;
	}
	public void setBaseDAO(BaseDao baseDAO) {
		this.baseDAO = baseDAO;
	}

	public String execute() throws Exception {
		//String result = "";
		HttpServletRequest request = ServletActionContext.getRequest();
		String userId = SessionUtils.getAttribute(request, "svUserID");
		String sql = "select PATH from as_admin where user_id=?";
		Object[] obj = {userId};
		List adminList = this.getBaseDAO().queryForListBySql(sql, obj);
		if(adminList != null && adminList.size()>0){
			String path = "";
			Map map = null;
			for(int i = 0; i < adminList.size() - 1; i++){
				map = (Map)adminList.get(i);
				path += (String)map.get("PATH") + "&";
			}
			map = (Map)adminList.get(adminList.size() - 1);
			path += (String)map.get("PATH");
			request.setAttribute("svAdminPath", path);
		}
		return "success";
	}

}
