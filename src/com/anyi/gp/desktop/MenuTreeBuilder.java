package com.anyi.gp.desktop;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.anyi.gp.BusinessException;
import com.anyi.gp.Pub;
import com.anyi.gp.context.ApplusContext;
import com.anyi.gp.core.dao.BaseDao;
import com.anyi.gp.license.LicenseManager;
import com.anyi.gp.license.SpecialProduct;
import com.anyi.gp.pub.DBHelper;

/**
 * ���ɲ˵������������ܵĿ���û��ʹ�õݹ�
 * 
 */
public class MenuTreeBuilder extends AbstractTreeBuilder {

  public static final String SQL_SELECT_MENU = "gmap-menu.selectMenu";//��ȡ�˵��б�sqlid
  
  public static final String SQL_CHECK_SA_ROLE = "gmap-priv.checkSaRoleByPosi";//ͨ��ְλ��鳬���û�
  
  public static final String SQL_SELECT_MENU_COMPO = "gmap-menu.selectMenuCompo";//��ȡ��Ȩ�û�����sqlid
  
  public static final String SQL_SELECT_MENU_COMPO_SA = "gmap-menu.selectMenuCompoBySa";//��ȡsa�û�����sqlid
  
	private static final Logger logger = Logger.getLogger(MenuTreeBuilder.class);

	private Map folderMap = new HashMap();

	public Tree generateTree(Object params) throws BusinessException {
		Tree tree = new Tree();

		Map map = (Map) params;
		boolean isRemoveEmpty = Pub.parseBool(map.get("isRemoveEmpty"));
		boolean isOnlyInMenu = Pub.parseBool(map.get("isOnlyInMenu"));
    map.put("isInMenu", isOnlyInMenu? "Y" : null);
		String rootCode = (String) map.get("rootCode");
		boolean isOnlyMenu = Pub.parseBool(map.get("isOnlyMenu"));

		generateFolder(rootCode);
		if (!isOnlyMenu){
			generateLeaf(map);
    }
    
		Node root = (Node) folderMap.get(rootCode);

		if (isRemoveEmpty) {
			((Folder) root).removeEmptyChild();
		}

		root.setLevel(1);
		tree.setRoot(root);

		return tree;
	}

  /**
   * ���ɲ˵����еĲ���
   * @param params
   * @throws SQLException
   */
  private void generateLeaf(Map params){
    String sqlid = null;
    BaseDao dao = (BaseDao)ApplusContext.getBean("baseDao");
    String userId = (String)params.get("userId");
    if("sa".equals(userId)){
      sqlid = SQL_SELECT_MENU_COMPO_SA;
    }else{
      String posiCode = (String)params.get("posiCode");
      if(posiCode != null && posiCode.length() != 0){
        int temp = 0;
        try {
          temp = ((Integer)dao.queryForObject(SQL_CHECK_SA_ROLE, posiCode)).intValue();
        } catch (SQLException e) {
          logger.error(e);
        }
        if(temp > 0){
          sqlid = SQL_SELECT_MENU_COMPO_SA;
        }
      }
    }
    if(sqlid == null){
      sqlid = SQL_SELECT_MENU_COMPO;
    }
    
    List result = null;
    try {
      result = dao.queryForList(sqlid, params);
    } catch (SQLException e) {
      logger.error(e);
      throw new RuntimeException(e);
    }
    if(result.isEmpty()) return;
    
    LicenseManager licenseManager = (LicenseManager) ApplusContext.getBean("licenseManager");
    List products = null;
    boolean checkProduct = licenseManager.checkProduct();
    if (checkProduct) {
      products = licenseManager.getAllowedProducts();
    }
    
    for(int i = 0; i < result.size(); i++){
      Map map = (Map)result.get(i);
      String menuId = (String)map.get("MENU_ID");
      MenuFolder folder = (MenuFolder) folderMap.get(menuId);
      if (folder == null)
        continue;
      String compoId = (String)map.get("COMPO_ID");        
      if (!checkProduct
          || (checkProduct && checkAllowedProduct(compoId, products))) {
        MenuLeaf leaf = new MenuLeaf();
        leaf.setCode(compoId);
        leaf.setName((String)map.get("COMPO_NAME"));
        leaf.setIsAlwaysNew((String)map.get("IS_ALWAYS_NEW"));
        leaf.setIsGotoEdit((String)map.get("IS_GOTO_EDIT"));
        leaf.setUrl((String)map.get("URL"));
        leaf.setIndex(Pub.parseInt(map.get("ORD_INDEX")));
        leaf.setIsInMenu((String)map.get("IS_IN_MENU"));

        leaf.setParent(folder);
        folder.addChild(leaf); 
      }
    }
  }
  
	/**
	 * ���ɲ˵����еĲ˵��� ��ѯrootCode���¼������в˵���ĸ���Ϊ�˵���Ĳ˵��� ��Ҫ�����еĲ˵�����й��˴���
	 * 
	 * @return
	 * @throws BusinessException
	 */
	private void generateFolder(String rootCode) throws BusinessException {
		Node root = new MenuFolder();
		root.setCode(rootCode);
		folderMap.put(rootCode, root);

		try{    
		  BaseDao dao = (BaseDao)ApplusContext.getBean("baseDao");
		  List result = dao.queryForList(SQL_SELECT_MENU, rootCode);
      for(int i = 0; i < result.size(); i++){
        Map map = (Map)result.get(i);
        
        String menuId = (String)map.get("MENU_ID");
        MenuFolder folder = (MenuFolder) folderMap.get(menuId);
        if (folder == null) {
          folder = new MenuFolder();
          folder.setCode(menuId);
        }
        if (folder.getName() == null || folder.getName().length() == 0)
          folder.setName((String)map.get("MENU_NAME"));

        String parentId = (String)map.get("PARENT_ID");
        if (parentId != null && parentId.length() > 0) {
          MenuFolder parent = (MenuFolder) folderMap.get(parentId);
          if (parent == null) {
            parent = new MenuFolder();
            parent.setCode(parentId);
            folderMap.put(parentId, parent);
          }

          folder.setParent(parent);
          parent.addChild(folder);
        }

        folder.setIndex(Pub.parseInt(map.get("ORD_INDEX")));
        folderMap.put(menuId, folder);        
      }
    }catch (SQLException e) {
      logger.error(e);
      throw new BusinessException(e);
    }
	}

	/**
	 * ������
	 * 
	 * @param tree
	 */
	public void saveTree(Tree tree, boolean saveToNew) {
		if (tree != null && tree.getRoot() != null) {
      processMenuTreeNode(tree.getRoot(), tree.getRoot().getCode(), saveToNew);
			saveMenuTreeNode(tree.getRoot());
		}
	}

  /**
   * �����δ�����idת����Ψһֵ
   * @param node
   * @param parentId
   */
  private void processMenuTreeNode(Node node, String parentId, boolean saveToNew){
    if(node == null)
      return ;
    if(node instanceof MenuFolder){
      MenuFolder folder = (MenuFolder)node;
      if(saveToNew){
        folder.cloneOldNode(folder);
      }
      
      if(folder.getParent() != null){
        folder.setCode(Pub.getUID());
        folder.getParent().setCode(parentId);
      }
      else{
        folder.setCode(parentId);
      }
      List children = folder.getChildren();
      if(children != null){
        for(int i = 0; i < children.size(); i++){
          processMenuTreeNode((Node)children.get(i), folder.getCode(), saveToNew);
        }
      }
      
    }else if(node instanceof MenuLeaf){
      MenuLeaf leaf = (MenuLeaf)node;
      if(saveToNew){
        leaf.cloneOldNode(leaf);
      }
      
      leaf.getParent().setCode(parentId);
    }
  }

	/**
	 * ����˵����Ľڵ㣨�ݹ�ʹ�ã�
	 * 
	 * @param node
	 * @param pageIdS
	 * @param pageIdD
	 */
	private void saveMenuTreeNode(Node node) {
		if (node == null)
			return;

		if (node instanceof MenuFolder) {
			MenuFolder folder = (MenuFolder) node;
			saveMenuFolder(folder);

			List children = folder.getChildren();
			if (children != null) {
				for (int i = 0; i < children.size(); i++) {
					saveMenuTreeNode((Node) children.get(i));
				}
			}
		} else if (node instanceof MenuLeaf) {
			MenuLeaf leaf = (MenuLeaf) node;
			saveMenuLeaf(leaf);
		}
	}

	/**
	 * ����˵����м�ڵ㣨ap_menu��
	 * 
	 * @param folder
	 * @param pageIdS
	 * @param pageIdD
	 */
	private void saveMenuFolder(MenuFolder folder) {
    if(folder.getOldNode() != null){
      DBHelper.executeSQL(" delete from ap_menu where menu_id = ? ", new Object[]{folder.getOldNode().getCode()});
    }
    
		String sql = " insert into ap_menu(menu_id, menu_name, ord_index, parent_id) values(?, ?, ?, ?) ";
		String parentId = null;
		Node parent = folder.getParent();
		if (parent != null) {
			parentId = parent.getCode();
		}
		Object[] params = new Object[] { folder.getCode(), folder.getName(),
				folder.getIndex() + "", parentId };

		DBHelper.executeSQL(sql, params);
	}

	/**
	 * ����˵���Ҷ�ӽڵ㣨ap_menu_compo��
	 * 
	 * @param leaf
	 * @param pageIdS
	 * @param pageIdD
	 */
	private void saveMenuLeaf(MenuLeaf leaf) {
    if(leaf.getOldNode() != null){
      DBHelper.executeSQL(" delete from ap_menu_compo where menu_id = ? and compo_id = ? ", 
        new Object[]{leaf.getOldNode().getParent().getCode(), leaf.getOldNode().getCode()});
    }
    
		String sql = " insert into ap_menu_compo(menu_id, compo_id, compo_name, ord_index, is_goto_edit, is_always_new, is_in_menu, url) "
				+ " values(?, ?, ?, ?, ?, ?, ?, ?) ";

		Object[] params = new Object[] { leaf.getParent().getCode(),
				leaf.getCode(), leaf.getName(), leaf.getIndex() + "",
				leaf.getIsGotoEdit(), leaf.getIsAlwaysNew(),
				leaf.getIsInMenu(), leaf.getUrl() };
		DBHelper.executeSQL(sql, params);
	}

	/**
	 * ��鲿���Ƿ����ڹ���Ĳ�Ʒ
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
			//product = SpecialProduct.getProductName(product);
			List realProducts = SpecialProduct.getProductName(allowedProducts);
			if (realProducts.contains(product))
				return true;
		}
		return false;
	}
}
