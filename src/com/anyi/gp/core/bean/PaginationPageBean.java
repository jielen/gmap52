package com.anyi.gp.core.bean;

public class PaginationPageBean extends PageRequestInfo{

  /** ҳ����� */
  protected String pageTitle;

  /** ���ݱ��ͷ */
  protected String dataTableHeader;

  /** ���ݱ����� */
  protected String dataTableBody;

  /** ������ť������ */
  protected String buttonTable;

  /** ��ҳ��Ϣ����� */
  protected PaginationInfo paginationInfo;

  /** ������ҳ��Ϣ */
  protected String entityPaginationInfo;

  /** ��ҳ������������ */
  protected String paginationControlAreaHTML;

  /** ѡ����еĿ�� */
  protected final int CHECKBOX_COL_WIDTH = 25;

  /** ��ͨ�еĿ�� */
  protected final int COMMON_COL_WIDTH = 150;
  
  /**
   * ��ʼ��bean
   */
  protected void beanInit() {
    super.beanInit();
    //this.setPaginationInfo();�ӳٵ������м���
  }
  
  /**
   * ���÷�ҳ��Ϣ�����
   */
  protected void setPaginationInfo() {
    paginationInfo = new PaginationInfo(request, compoName, lang);
  }

  /**
 * ������ҳ��Ϣ
 * @return   ������ҳ��Ϣ
 * @uml.property   name="entityPaginationInfo"
 */
  public String getEntityPaginationInfo() {
    entityPaginationInfo = paginationInfo.getEntityPaginationInfo();
    return entityPaginationInfo;
  }

  /**
 * ��ҳ������������
 * @return   ��ҳ������������
 * @uml.property   name="paginationControlAreaHTML"
 */
  public String getPaginationControlAreaHTML() {
    paginationControlAreaHTML = paginationInfo.getControlAreaHTML();
    return paginationControlAreaHTML;
  }

  /**
 * �ֶ��б�fieldslist�����ݱ��ͷ
 * @return   �ֶ��б�fieldslist�����ݱ��ͷ
 * @uml.property   name="dataTableHeader"
 */
  public String getDataTableHeader() {

    return dataTableHeader;
  }

  /**
 * �ֶ��б�fieldslist�����ݱ�����
 * @return   �ֶ��б�fieldslist�����ݱ�����
 * @uml.property   name="dataTableBody"
 */
  public String getDataTableBody() {
    return dataTableBody;
  }

  /**
 * ������ť������
 * @return   ������ť������
 * @uml.property   name="buttonTable"
 */
  public String getButtonTable() {
    return buttonTable;
  }

  /**
 * ҳ�����
 * @return   ҳ�����
 * @uml.property   name="pageTitle"
 */
  public String getPageTitle() {
    return pageTitle;
  }
  
}
