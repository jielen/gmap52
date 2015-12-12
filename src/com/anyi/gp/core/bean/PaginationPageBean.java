package com.anyi.gp.core.bean;

public class PaginationPageBean extends PageRequestInfo{

  /** 页面标题 */
  protected String pageTitle;

  /** 数据表表头 */
  protected String dataTableHeader;

  /** 数据表内容 */
  protected String dataTableBody;

  /** 操作按钮表内容 */
  protected String buttonTable;

  /** 分页信息类对象 */
  protected PaginationInfo paginationInfo;

  /** 部件分页信息 */
  protected String entityPaginationInfo;

  /** 分页控制区域内容 */
  protected String paginationControlAreaHTML;

  /** 选择框列的宽度 */
  protected final int CHECKBOX_COL_WIDTH = 25;

  /** 普通列的宽度 */
  protected final int COMMON_COL_WIDTH = 150;
  
  /**
   * 初始化bean
   */
  protected void beanInit() {
    super.beanInit();
    //this.setPaginationInfo();延迟到子类中加载
  }
  
  /**
   * 设置分页信息类对象
   */
  protected void setPaginationInfo() {
    paginationInfo = new PaginationInfo(request, compoName, lang);
  }

  /**
 * 部件分页信息
 * @return   部件分页信息
 * @uml.property   name="entityPaginationInfo"
 */
  public String getEntityPaginationInfo() {
    entityPaginationInfo = paginationInfo.getEntityPaginationInfo();
    return entityPaginationInfo;
  }

  /**
 * 分页控制区域内容
 * @return   分页控制区域内容
 * @uml.property   name="paginationControlAreaHTML"
 */
  public String getPaginationControlAreaHTML() {
    paginationControlAreaHTML = paginationInfo.getControlAreaHTML();
    return paginationControlAreaHTML;
  }

  /**
 * 字段列表fieldslist的数据表表头
 * @return   字段列表fieldslist的数据表表头
 * @uml.property   name="dataTableHeader"
 */
  public String getDataTableHeader() {

    return dataTableHeader;
  }

  /**
 * 字段列表fieldslist的数据表内容
 * @return   字段列表fieldslist的数据表内容
 * @uml.property   name="dataTableBody"
 */
  public String getDataTableBody() {
    return dataTableBody;
  }

  /**
 * 操作按钮表内容
 * @return   操作按钮表内容
 * @uml.property   name="buttonTable"
 */
  public String getButtonTable() {
    return buttonTable;
  }

  /**
 * 页面标题
 * @return   页面标题
 * @uml.property   name="pageTitle"
 */
  public String getPageTitle() {
    return pageTitle;
  }
  
}
