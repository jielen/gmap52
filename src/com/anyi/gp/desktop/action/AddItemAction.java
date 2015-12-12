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
				resultStr = "DeskTopBean���addItem�������������������,���벿��ʱ������userNameΪ�գ�";
			}
			if (areaID == null) {
				resultStr = "DeskTopBean���addItem�������������������,���벿��ʱ������tempAreaIDΪ�գ�";
			}
			
			//���ﲻ̫����ΪʲôҪת��һ�£���֪��ǰ�洫��������ʲô���д��Ժ�֤����
			if('A'!= areaID.charAt(0))
				areaID = this.areaNametoID(areaID, svUserID);
			
			if (componame == null) {
				resultStr = "DeskTopBean���addItem������������������У����벿��ʱ������compNameΪ�գ�";
			}
			if (getIsShowRec(areaID, svUserID)) {
				if (this.itemsCount(areaID, svUserID) != 0) {
					resultStr = "������Ϊ��ʾ��¼�������Ѿ��м�¼��ʾ��";
				}
			} else {
				flag = true;
				if(!canAddItem(areaID, svUserID))
					resultStr = "�ѳ����������ֵ,���������µ���Ŀ��";
				Map sqlMap = new HashMap();
				sqlMap.put("areaID", areaID);
				sqlMap.put("componame", componame);
				sqlMap.put("userName", svUserID);
				sqlMap.put("menuId", menuId);

				List resultGet = (List) dao.queryForList(SQL_ID, sqlMap);
				if (!resultGet.isEmpty()) {
					flag = false;
					resultStr =  "�Ѿ�����";
				} else {
					dao.insert(INSERT_SQL_ID, sqlMap);
				}
			}
		} catch (SQLException ex) {
			// log.error("Favovite���delete�������������������" + "ɾ������ʱ��SQL���ִ�д���"
			// + ex.toString());
			flag = false;
			resultStr = "DeskTopBean���addItem�������������������" + "���벿��ʱ��SQL���ִ�д���";
		} catch (BusinessException bex) {
			flag = false;
			resultStr = bex.getMessage();
		}
		this.resultstring = this.wrapResultStr(Boolean.toString(flag), resultStr);

		return SUCCESS;
	}

	private String areaNametoID(String areaName, String userID) {
		String returnStr = "";
		int i = areaName.indexOf("����");
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
			throw new RuntimeException("DeskTopBean���areaNametoID�������������������"
					+ "���벿��ʱ��SQL���ִ�д���");
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
				throw new BusinessException("���ݿ���û�У�����ţ�" + areaID + "   �û�����"
						+ userName + " ����Ϣ��");
			}

		} catch (SQLException ex) {
			throw new RuntimeException("DeskTopBean���areaNametoID�������������������"
					+ "���벿��ʱ��SQL���ִ�д���");
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
					"DeskTopBean���itemsAmount�������������������,���벿��ʱ������userNameΪ�գ�", null);
		}
		if (areaID == null) {
			throw new BusinessException(
					"DeskTopBean���itemsAmount�������������������,���벿��ʱ������userNameΪ�գ�", null);
		}

		try {
			List result = (List) dao.queryForList(GET_AMOUNT, sqlMap);
			if (!result.isEmpty()) {
				Map map = (Map) result.get(0);
				returnInt = Integer.parseInt( map.get("AMOUNT").toString());
			}
		} catch (SQLException ex) {
			// log.error("Favovite���delete�������������������" + "ɾ������ʱ��SQL���ִ�д���"
			// + ex.toString());
			throw new RuntimeException("DeskTopBean���addItem�������������������"
					+ "���벿��ʱ��SQL���ִ�д���");
		}
		return returnInt;
	}

	private boolean canAddItem(String areaID, String userName)
			throws BusinessException {

		// ���Ƿ񳬳����ֵ
		int maxI = this.getItemsMax(areaID, userName);
		if (maxI <= this.itemsCount(areaID, userName)) {
			//throw new BusinessException("����������Ϊ" + maxI + ",���������µ���Ŀ��");
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
					"DeskTopBean���getItemsMax�������������������,���벿��ʱ������userNameΪ�գ�", null);
		}
		if (areaID == null) {
			throw new BusinessException(
					"DeskTopBean���getItemsMax�������������������,���벿��ʱ������userNameΪ�գ�", null);
		}

		try {
			List result = (List) dao.queryForList(GET_DISPLAY_AMOUNT, sqlMap);
			if (!result.isEmpty()) {
				Map map = (Map) result.get(0);
				returnInt = Integer.parseInt(map.get("AMOUNT").toString());
			}
		} catch (SQLException ex) {
			// log.error("Favovite���delete�������������������" + "ɾ������ʱ��SQL���ִ�д���"
			// + ex.toString());
			throw new RuntimeException("DeskTopBean���addItem�������������������"
					+ "���벿��ʱ��SQL���ִ�д���");
		}
		return returnInt;
	}

  public void setServletRequest(HttpServletRequest request) {
    this.request = request;
  }
}
