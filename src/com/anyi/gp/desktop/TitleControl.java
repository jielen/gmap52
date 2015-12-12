package com.anyi.gp.desktop;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.anyi.gp.BusinessException;
import com.anyi.gp.context.ApplusContext;
import com.anyi.gp.core.dao.BaseDao;
import com.anyi.gp.license.LicenseManager;
import com.anyi.gp.license.SpecialProduct;

public class TitleControl {

  private static final String SQL_SELECT_USER_MENU = "gmap-menu.selectUserMenu";//获取授权用户菜单sqlid

  private static final String SQL_SELECT_USER_PAGE = "gmap-menu.selectGroupPage";//sa用户组对应用户页面sqlid
  
  private static final String SQL_SELECT_USER_PAGE_SA = "gmap-menu.selectGroupPageBySa";//sa用户组对应用户页面sqlid
  
	private static final Logger logger = Logger.getLogger(TitleControl.class);

	private String groupId;

	private String userId;

	private Title defTitle;

	private String coCode;

	private String orgCode;

  private String posiCode;
  
	private int rowNums = 0;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getPosiCode() {
    return posiCode;
  }

  public void setPosiCode(String posiCode) {
    this.posiCode = posiCode;
  }

  /**
	 * 取得默认title
	 * 
	 * @return
	 * @throws BusinessException
	 */
	public Title getDefTitle() throws BusinessException {
		if (defTitle == null || defTitle.getTitleId().length() == 0) {
			List titleList = filterTitle(getTitleList());
			if (!titleList.isEmpty()) {
				defTitle = (Title) titleList.get(0);
			}
		}
		return defTitle;
	}

	public void setDefTitle(Title defTitle) {
		this.defTitle = defTitle;
	}

	public int getRowNums() {
		return rowNums;
	}

	public void setRowNums(int rowNums) {
		this.rowNums = rowNums;
	}

	public String getCoCode() {
		return coCode;
	}

	public void setCoCode(String coCode) {
		this.coCode = coCode;
	}

	public String getOrgCode() {
		return orgCode;
	}

	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}

	/**
	 * 获取xml格式的title信息
	 * 
	 * @return
	 */
	public String getTitleXml() {
		StringBuffer result = new StringBuffer();
		result.append("<delta>\n");

		List titleList = getTitleList();
		for (int i = 0; i < titleList.size(); i++) {
			Title title = (Title) titleList.get(i);
			result.append("<entity>\n");
			result.append("<field name=\"page_id\" value=\""
					+ title.getTitleId() + "\"/>\n");
			result.append("<field name=\"page_title\" value=\""
					+ title.getTitleName() + "\"/>\n");
			result.append("<field name=\"page_order\" value=\""
					+ title.getIndex() + "\"/>\n");
			result.append("</entity>\n");
		}
		result.append("</delta>\n");

		return result.toString();
	}

	/**
	 * 获取html格式的title信息 已经根据权限和加密狗过滤了的title信息
	 * 
	 * @return
	 * @throws BusinessException
	 */
	public String getTitleHtml() throws BusinessException {
		StringBuffer result = new StringBuffer();

		int index = 0;
		List titleList = filterTitle(getTitleList());

		if (!titleList.isEmpty())
			setDefTitle((Title) titleList.get(0));

		rowNums = titleList.size() % 9 == 0 ? titleList.size() / 9 - 1
				: titleList.size() / 9;

		for (int i = 0; i < titleList.size(); i++) {
			if (index % 9 == 0) {
				int number = index / 9;

				if (number == 0)
					result.append("<div id=\"topMenu" + number
							+ "\" style=\"display:block\">");
				else
					result.append("<div id=\"topMenu" + number
							+ "\" style=\"display:none\">");

				result
						.append("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
				result.append("<tr>");
			}

			Title title = (Title) titleList.get(i);
			result.append(title.toHtml());

			index++;

			if (index % 9 == 0 && index != titleList.size()) {
				result.append("</tr>");
				result.append("</table>");
				result.append("</div>");
			}

		}

		result.append("</tr>");
		result.append("</table>");
		result.append("</div>");

		return result.toString();
	}

	/**
	 * 获取此用户组所定义的页title信息
	 * 
	 * @return
	 */
	public List getTitleList() {
	  try{
      String sqlid = null;
      if("sa".equals(groupId)){
        sqlid = SQL_SELECT_USER_PAGE_SA;
      }else{
        sqlid = SQL_SELECT_USER_PAGE;
      }
      BaseDao dao = (BaseDao)ApplusContext.getBean("baseDao");
      return dao.queryForList(sqlid, groupId);
		} catch (SQLException e) {
			logger.error(e);
			throw new RuntimeException(e);
		} 
	}

	/**
	 * 过滤title：用户权限、加密狗信息
	 * 
	 * @param titleList
	 * @return
	 * @throws BusinessException
	 */
	private List filterTitle(List titleList) throws BusinessException {
		List result = new ArrayList();
		if (titleList.size() == 0 || userId == null || userId.length() == 0) {
			return result;
		}

		Set userMenuNames = getUserMenuNames();
		if (userMenuNames == null || userMenuNames.isEmpty())
			return result;

		for (int i = 0; i < titleList.size(); i++) {
			Title title = (Title) titleList.get(i);
			TreeBuilder build = new MenuTreeBuilder();
			Map params = new HashMap();
			params.put("rootCode", title.getTitleId());
			params.put("userId", userId);
			params.put("isOnlyInMenu", "true");
			params.put("coCode", coCode == null ? "" : coCode);
			params.put("orgCode", orgCode == null ? "" : orgCode);
			params.put("isRemoveEmpty", "false");
			params.put("isOnlyMenu", "true");
			params.put("posiCode", posiCode);
      
			Tree tree = build.generateTree(params);
			List nodeList = tree.getAllNodes();

			Iterator iterator = userMenuNames.iterator();
			while (iterator.hasNext()) {
				if (nodeList.contains(iterator.next())) {
					result.add(title);
					break;
				}
			}
		}
		LicenseManager licenseManger = (LicenseManager) ApplusContext
				.getBean("licenseManager");
		List products = licenseManger.getAllowedProducts();
		
		if (products == null || products.size() == 0) {
			return result;
		} else {
			return SpecialProduct.filterTitle(result, products);
		}
	}

  /**
   * 获取用户所能操作的菜单列表
   * 
   * @return
   * @throws BusinessException
   */
  public Set getUserMenuNames(){
    String sqlid = null;
    BaseDao dao = (BaseDao)ApplusContext.getBean("baseDao");
    if("sa".equals(userId)){
      sqlid = MenuTreeBuilder.SQL_SELECT_MENU_COMPO_SA;
    }else{
      if(posiCode != null && posiCode.length() != 0){
        int temp = 0;
        try {
          temp = ((Integer)dao.queryForObject(MenuTreeBuilder.SQL_CHECK_SA_ROLE, posiCode)).intValue();
        } catch (SQLException e) {
          logger.error(e);
          throw new RuntimeException(e);
        }
        if(temp > 0){
          sqlid = MenuTreeBuilder.SQL_SELECT_MENU_COMPO_SA;
        }
      }
    }
    if(sqlid == null){
      sqlid = SQL_SELECT_USER_MENU;
    }
    Map params = new HashMap();
    params.put("userId", userId);
    params.put("isInMenu", "Y");
    params.put("coCode", coCode == null ? "" : coCode);
    params.put("orgCode", orgCode == null ? "" : orgCode);
    params.put("posiCode", posiCode);
    
    List result = null;
    try {
      result = dao.queryForList(sqlid, params);
    } catch (SQLException e) {
      logger.error(e);
      throw new RuntimeException(e);
    }
    
    List allowedProducts = null;// 增加加密狗中产品的过滤
    boolean checkProduct = false;
    LicenseManager licenseManger = (LicenseManager) ApplusContext.getBean("licenseManager");
    if (licenseManger.checkProduct()) {
      checkProduct = true;
      allowedProducts = licenseManger.getAllowedProducts();
    }
    
    Set returnSet = new HashSet();
    for(int i = 0; i < result.size(); i++){
      Map map = (Map)result.get(i);
      if ((!checkProduct)|| (checkProduct && checkAllowedProduct(
          (String)map.get("COMPO_ID"), allowedProducts))) {
        returnSet.add((String)map.get("MENU_ID"));
      }
    }
    
    return returnSet;
  }

	/**
	 * 检查部件是否属于购买的产品
	 * 
	 * @param compoId
	 * @param allowedProducts
	 * @return
	 */
	private boolean checkAllowedProduct(String compoId, List allowedProducts) {
		if (compoId == null || compoId.length() == 0 || allowedProducts == null)
			return false;

		String product = null;
		int dotPos = compoId.indexOf("_");
		if (dotPos > 0) {
			product = compoId.substring(0, dotPos);
		}
		if (product != null && product.length() > 0) {
			if (product.equalsIgnoreCase("AS")
					|| product.equalsIgnoreCase("MA")
					|| product.equalsIgnoreCase("WF") || product.equals("DB")) {
				return true;
			}
			List realProducts = SpecialProduct.getProductName(allowedProducts);
			if (realProducts.contains(product))
				return true;
		}
		return false;
	}
}
