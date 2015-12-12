package com.anyi.gp.core.bean;

import javax.servlet.http.HttpServletRequest;

import com.anyi.gp.pub.LangResource;
import com.anyi.gp.taglib.components.Page;
import com.anyi.gp.util.StringTools;

public class PaginationInfo {

  /**
   * �չ�����
   */
  public PaginationInfo() {
  }

  /**
   * ������
   * @param newRequest ����
   * @param newEntityName ������
   * @param newLang ��������
   */
  public PaginationInfo(HttpServletRequest newRequest, String newEntityName, String newLang) {
    request = newRequest;
    compoName = newEntityName;
    setResource();
    setCurrentPage();
    setTotalPage();
    setPageUniqueID();
    setTotalCount();
  }

  /**
   * ����������Դ����
   */
  private void setResource() {
    resource = LangResource.getInstance();
  }
  
  /**
   * ���õ�ǰҳ��
   */
  private void setCurrentPage() {
    if (request.getAttribute(compoName + "currentPage") != null) {
      currentPage = (String) request.getAttribute(compoName + "currentPage");
    } 
    else {
      currentPage = "0";
    }
  }

  /**
   * ������ҳ��
   */
  private void setTotalPage() {
    if (request.getAttribute(compoName + "totalPage") != null) {
      totalPage = (String) request.getAttribute(compoName + "totalPage");
    } else {
      totalPage = "0";
    }
  }

  /**
   * ������ҳ��
   */
  private void setTotalCount() {
    if (request.getAttribute(compoName + "totalCount") != null) {
      this.totalCount = (String) request.getAttribute(compoName
          + "totalCount");
    } else {
      this.totalCount = "0";
    }
  }

  /**
   * ����ҳ��Ψһ��ID
   */
  private void setPageUniqueID() {
    if (request.getAttribute("pageUniqueID") != null) {
      pageUniqueID = (String) request.getAttribute("pageUniqueID");
    } 
    else {
      pageUniqueID = "";
    }
  }

  /**
 * �õ�������ҳ��Ϣ
 * @return   XML��ʽ�Ĳ�����ҳ��Ϣ
 * @uml.property   name="entityPaginationInfo"
 */
  public String getEntityPaginationInfo() {
    entityPaginationInfo = StringTools.getMargin(1)
        + "<span id=\"uniqueID\" value=\"" + pageUniqueID + "\">"
        + "</span>" + NEW_LINE;
    return entityPaginationInfo;
  }

  /**
   * ��ҳ���������HTML����
   * @return ��ҳ���������HTML����
   */
  public String getControlAreaHTML() {
    StringBuffer result = new StringBuffer();
    result.append(getTotalCountsHTML());
    result.append(getButtonElementHTML(firstPage, "PAGINATION_FIRST"));
    result.append(getButtonElementHTML(priorPage, "PAGINATION_PRIOR"));
    result.append(getButtonElementHTML(nextPage, "PAGINATION_NEXT"));
    result.append(getButtonElementHTML(lastPage, "PAGINATION_LAST"));
    result.append(getLabelElementHTML(pageInfo, "PAGINATION_PAGE"));
    result.append(jumpToSpecifyHTML()); // ��תҳ
    return result.toString();
  }

  /**
   * ��ťԪ�ص�HTML����
   * @param callName ��ť����
   * @param callNameResID ��ť��ʾ�ı������Դ���
   * @return ��ťԪ�ص�HTML����
   */
  private String getButtonElementHTML(String callName, String callNameResID) {
    StringBuffer result = new StringBuffer();
    String title = "��ݼ���:";
    if (callName.equals(firstPage)) {
      title += "Home��";
      result.append(StringTools.getMargin(7))
          .append("<img src=\"" + Page.LOCAL_RESOURCE_PATH + "style/img/tab/lf.gif\">")
          .append("<input type=\"button\" name=\"")
          .append(callName)
          .append("\" id=\"")
          .append(callName)
          .append("ID\" class=\"clsPaginationCall\" value=\"")
          .append(resource.getLang(callNameResID))
          .append("\" title=\"")
          .append(title)
          .append("\" onclick=\"")
          .append(callName)
          .append("()\"> </input>")
          .append(NEW_LINE);
    } 
    else if (callName.equals(priorPage)) {
      title += "PageUp��";
      result.append(StringTools.getMargin(7))
            .append("<img src=\"" + Page.LOCAL_RESOURCE_PATH + "style/img/tab/l.gif\">")
            .append("<input type=\"button\" name=\"")
            .append(callName)
            .append("\" id=\"")
            .append(callName)
            .append("ID\" class=\"clsPaginationCall\" value=\"")
            .append(resource.getLang(callNameResID))
            .append("\" title=\"")
            .append(title)
            .append("\" onclick=\"")
            .append(callName)
            .append("()\"></input>")
            .append(NEW_LINE);
    } 
    else if (callName.equals(nextPage)) {
      title += "PageDown��";
      result.append(StringTools.getMargin(7))
            .append("<img src=\"" + Page.LOCAL_RESOURCE_PATH + "style/img/tab/r.gif\">")
          	.append("<input type=\"button\" name=\"")
          	.append(callName)
          	.append("\" id=\"")
          	.append(callName)
          	.append("ID\" class=\"clsPaginationCall\" value=\"")
          	.append(resource.getLang(callNameResID))
          	.append("\" title=\"")
          	.append(title)
          	.append("\" onclick=\"")
          	.append(callName + "()\"> </input>")
          	.append(NEW_LINE);
    } 
    else if (callName.equals(lastPage)) {
      title += "End��";
      result.append(StringTools.getMargin(7))
          	.append("<img src=\"" + Page.LOCAL_RESOURCE_PATH + "style/img/tab/rl.gif\">")
          	.append("<input type=\"button\" name=\"")
          	.append(callName)
          	.append("\" id=\"")
          	.append(callName)
          	.append("ID\" class=\"clsPaginationCall\" value=\"")
          	.append(resource.getLang(callNameResID))
          	.append("\" title=\"")
          	.append(title)
          	.append("\" onclick=\"")
          	.append(callName + "()\"></input>")
          	.append(NEW_LINE);
    }
    return result.toString();
  }

  /**
   * ��ǩԪ�ص�HTML����
   * 
   * @param labelName
   *          ��ǩ����
   * @param labelNameResID
   *          ��ǩҳ
   * @return ��ǩԪ�ص�HTML����
   */
  private String getLabelElementHTML(String labelName, String labelNameResID) {
    StringBuffer result = new StringBuffer();
    result.append(StringTools.getMargin(7))
    			.append("<span id=\"currentPageID\"")
    			.append("class=\"clsPaginationLabel\">")
    			.append(currentPage)
        	.append("</span> <span>/</span> <span id=\"totalPageID\"")
        	.append("class=\"clsPaginationLabel\">")
        	.append(totalPage)
        	.append("</span> ")
        	.append(NEW_LINE);
    return result.toString();
  }

  /**
   * ������������HTML����
   * 
   * @return ��ǩԪ�ص�HTML����
   */
  private String getTotalCountsHTML() {
    StringBuffer result = new StringBuffer();
    result.append(StringTools.getMargin(7))
        	.append("<span class=\"clsPaginationLabel\">��</span>")
        	.append("<span id=\"totalCountsID\" class=\"clsPaginationLabel\">")
        	.append(totalCount)
        	.append("</span><span class=\"clsPaginationLabel\">��</span>")
        	.append(NEW_LINE);
    return result.toString();
  }

  /**
   * ��ת��ָ��ҳ��HTML���� wtm��20040706 malength:������תҳ9999��
   * 
   * @return ��ǩԪ�ص�HTML����
   */
  private String jumpToSpecifyHTML() {
    StringBuffer result = new StringBuffer();
    result.append(StringTools.getMargin(7))
          .append("<span class=\"clsPaginationLabel\">ת����</span>")
          .append("<input type=\"text\" name=\"jumpToPage\" id=\"jumpToPageID\" onKeyPress='jumpToPage_KeyPress()' maxlength=\"4\" size=\"3\">")
          .append("</input>")
          .append("</span><span class=\"clsPaginationLabel\">ҳ</span>")
          .append("<input type=\"button\"  name=\"go\" id=\"goID\" value=\"GO\" onclick=\"go()\" alt=\"��ʼ����\">")
          .append("</input>")
          .append(NEW_LINE);
    return result.toString();
  }

  /** ���з� */
  private static final String NEW_LINE = System.getProperty("line.separator");

  /** ��ҳ */
  private final String firstPage = "firstpage";

  /** ǰһҳ */
  private final String priorPage = "priorpage";

  /** ��һҳ */
  private final String nextPage = "nextpage";

  /** ĩҳ */
  private final String lastPage = "lastpage";

  /** ҳ��Ϣ */
  private final String pageInfo = "pageinfo";
  
  /** ������ */
  private String compoName;

  /** ������Դ���� */
  private LangResource resource;

  /** ҳ��Ψһ��ID */
  private String pageUniqueID;

  /** ��ǰҳ�� */
  private String currentPage;

  /** ��ҳ�� */
  private String totalPage;
  
  /** ������ */
  private String totalCount;

  /** ������ҳ��Ϣ */
  private String entityPaginationInfo;

  /** ������� */
  private HttpServletRequest request ;

}
