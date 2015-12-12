package com.anyi.gp.desktop.action;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.anyi.gp.BusinessException;
import com.anyi.gp.core.action.AjaxAction;
import com.anyi.gp.core.dao.BaseDao;
import com.anyi.gp.pub.SessionUtils;
import com.opensymphony.webwork.interceptor.ServletRequestAware;

public class AddItemAction extends AjaxAction implements ServletRequestAware{

  private static final long serialVersionUID = 6611085609916493858L;

  private static final String SQL_ID = "gmap-common.getAreaId";

	private static final String INSERT_SQL_ID = "gmap-common.insertDeskTopArea";

	private static final String GET_SQL_ID = "gmap-common.getIdByName";

	private static final String GET_IS_DISPLAY = "gmap-common.getDisplay";

	private static final String GET_AMOUNT = "gmap-common.getAmount";

	private static final String GET_DISPLAY_AMOUNT = "gmap-common.getDisplayAmount";

  private HttpServletRequest request;
  
	private String areaID = null;

	private String componame = null;

	private String menuId = null;
	
	private BaseDao dao;
	
	public void setDao(BaseDao dao){
		this.dao = dao;
	}

	public void setAreaID(String tempAreaID) {
		this.areaID = tempAreaID;
	}

	public void setComponame(String componame) {
		this.componame = componame;
	}

	public void setMenuId(String menuId) {
		this.menuId = menuId;
	}

	public String getTempAreaID() {
		return areaID;
	}

	public String getComponame() {
		return componame;
	}

	public String setMenuId() {
		return menuId;
	}
	
	public BaseDao getDao(){
		return dao;
	}


	public String doExecute() {
    String svUserID = SessionUtils.getAttribute(request, "svUserID");
		
		int tPos = menuId.indexOf(componame+"ID");
		if(tPos > 0)
			menuId = menuId.substring(0, tPos);
    
		String resultStr = "";
		boolean flag = false;
		try {
			if (svUserID == null) {
				resultStr = "DeskTopBean类的addItem方法：往桌面的区域中,加入部件时，参数userName为空！";
			}
			if (areaID == null) {
				resultStr = "DeskTopBean类的addItem方法：往桌面的区域中,加入部件时，参数tempAreaID为空！";
			}
			
			//这里不太明白为什么要转换一下，不知道前面传过来的是什么，有待以后考证化简
			if('A'!= areaID.charAt(0))
				areaID = this.areaNametoID(areaID, svUserID);
			
			if (componame == null) {
				resultStr = "DeskTopBean类的addItem方法：往桌面的区域中，加入部件时，参数compName为空！";
			}
			if (getIsShowRec(areaID, svUserID)) {
				if (this.itemsCount(areaID, svUserID) != 0) {
					resultStr = "本区域为显示记录的区域，已经有记录显示！";
				}
			} else {
				flag = true;
				if(!canAddItem(areaID, svUserID))
					resultStr = "已超过区域最大值,不能增加新的条目！";
				Map sqlMap = new HashMap();
				sqlMap.put("areaID", areaID);
				sqlMap.put("componame", componame);
				sqlMap.put("userName", svUserID);
				sqlMap.put("menuId", menuId);

				List resultGet = (List) dao.queryForList(SQL_ID, sqlMap);
				if (!resultGet.isEmpty()) {
					flag = false;
					resultStr =  "已经加入";
				} else {
					dao.insert(INSERT_SQL_ID, sqlMap);
				}
			}
		} catch (SQLException ex) {
			// log.error("Favovite类的delete方法：从桌面的区域中" + "删除部件时，SQL语句执行错误！"
			// + ex.toString());
			flag = false;
			resultStr = "DeskTopBean类的addItem方法：在桌面的区域中" + "加入部件时，SQL语句执行错误！";
		} catch (BusinessException bex) {
			flag = false;
			resultStr = bex.getMessage();
		}
		this.resultstring = this.wrapResultStr(Boolean.toString(flag), resultStr);

		return SUCCESS;
	}

	private String areaNametoID(String areaName, String userID) {
		String returnStr = "";
		int i = areaName.indexOf("加入");
		areaName = areaName.substring(i + 2);
		Map sqlMap = new HashMap();
		sqlMap.put("areaName", areaName);
		sqlMap.put("userID", userID);
		try {
			List result = (List) dao.queryForList(GET_SQL_ID, sqlMap);
			// if (rs.next()) {
			// returnStr = rs.getString("AREA_ID");
			// }
			if (!result.isEmpty()) {
				Map map = (Map) result.get(0);
				returnStr = (String) map.get("AREA_ID");
			}
		} catch (SQLException ex) {
			throw new RuntimeException("DeskTopBean类的areaNametoID方法：在桌面的区域中"
					+ "加入部件时，SQL语句执行错误！");
		}
		return returnStr;

	}

	private boolean getIsShowRec(String areaID, String userName)
			throws BusinessException {
		boolean returnBln = false;
		Map sqlMap = new HashMap();
		sqlMap.put("areaID", areaID);
		sqlMap.put("userName", userName);

		try {
			List result = (List) dao.queryForList(GET_IS_DISPLAY, sqlMap);
			if (!result.isEmpty()) {
				Map map = (Map) result.get(0);
				String tempStr = (String) map.get("AREA_ID");
				returnBln = tempStr == null ? false : tempStr.equalsIgnoreCase("y");
			} else {
				throw new BusinessException("数据库中没有，区域号：" + areaID + "   用户名："
						+ userName + " 的信息！");
			}

		} catch (SQLException ex) {
			throw new RuntimeException("DeskTopBean类的areaNametoID方法：在桌面的区域中"
					+ "加入部件时，SQL语句执行错误！");
		}
		return returnBln;
	}

	private int itemsCount(String areaID, String svUserID)
			throws BusinessException {
		String userName = svUserID;
		int returnInt = 0;
		Map sqlMap = new HashMap();
		sqlMap.put("areaID", areaID);
		sqlMap.put("userName", userName);

		if (userName == null) {
			throw new BusinessException(
					"DeskTopBean类的itemsAmount方法：往桌面的区域中,加入部件时，参数userName为空！", null);
		}
		if (areaID == null) {
			throw new BusinessException(
					"DeskTopBean类的itemsAmount方法：往桌面的区域中,加入部件时，参数userName为空！", null);
		}

		try {
			List result = (List) dao.queryForList(GET_AMOUNT, sqlMap);
			if (!result.isEmpty()) {
				Map map = (Map) result.get(0);
				returnInt = Integer.parseInt( map.get("AMOUNT").toString());
			}
		} catch (SQLException ex) {
			// log.error("Favovite类的delete方法：从桌面的区域中" + "删除部件时，SQL语句执行错误！"
			// + ex.toString());
			throw new RuntimeException("DeskTopBean类的addItem方法：在桌面的区域中"
					+ "加入部件时，SQL语句执行错误！");
		}
		return returnInt;
	}

	private boolean canAddItem(String areaID, String userName)
			throws BusinessException {

		// 看是否超出最大值
		int maxI = this.getItemsMax(areaID, userName);
		if (maxI <= this.itemsCount(areaID, userName)) {
			//throw new BusinessException("这个区域最大为" + maxI + ",不能增加新的条目！");
			return false;
		}
		return true;
	}

	private int getItemsMax(String areaID, String svUserID)
			throws BusinessException {
		String userName = svUserID;
		Map sqlMap = new HashMap();
		sqlMap.put("areaID", areaID);
		sqlMap.put("userName", userName);

		int returnInt = 0;
		if (userName == null) {
			throw new BusinessException(
					"DeskTopBean类的getItemsMax方法：往桌面的区域中,加入部件时，参数userName为空！", null);
		}
		if (areaID == null) {
			throw new BusinessException(
					"DeskTopBean类的getItemsMax方法：往桌面的区域中,加入部件时，参数userName为空！", null);
		}

		try {
			List result = (List) dao.queryForList(GET_DISPLAY_AMOUNT, sqlMap);
			if (!result.isEmpty()) {
				Map map = (Map) result.get(0);
				returnInt = Integer.parseInt(map.get("AMOUNT").toString());
			}
		} catch (SQLException ex) {
			// log.error("Favovite类的delete方法：从桌面的区域中" + "删除部件时，SQL语句执行错误！"
			// + ex.toString());
			throw new RuntimeException("DeskTopBean类的addItem方法：在桌面的区域中"
					+ "加入部件时，SQL语句执行错误！");
		}
		return returnInt;
	}

  public void setServletRequest(HttpServletRequest request) {
    this.request = request;
  }
}
