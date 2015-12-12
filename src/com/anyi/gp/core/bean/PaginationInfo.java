package com.anyi.gp.core.bean;

import javax.servlet.http.HttpServletRequest;

import com.anyi.gp.pub.LangResource;
import com.anyi.gp.taglib.components.Page;
import com.anyi.gp.util.StringTools;

public class PaginationInfo {

  /**
   * 空构造体
   */
  public PaginationInfo() {
  }

  /**
   * 构造体
   * @param newRequest 请求
   * @param newEntityName 部件名
   * @param newLang 语言种类
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
   * 设置语言资源对象
   */
  private void setResource() {
    resource = LangResource.getInstance();
  }
  
  /**
   * 设置当前页码
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
   * 设置总页码
   */
  private void setTotalPage() {
    if (request.getAttribute(compoName + "totalPage") != null) {
      totalPage = (String) request.getAttribute(compoName + "totalPage");
    } else {
      totalPage = "0";
    }
  }

  /**
   * 设置总页码
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
   * 设置页面唯一性ID
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
 * 得到部件分页信息
 * @return   XML格式的部件分页信息
 * @uml.property   name="entityPaginationInfo"
 */
  public String getEntityPaginationInfo() {
    entityPaginationInfo = StringTools.getMargin(1)
        + "<span id=\"uniqueID\" value=\"" + pageUniqueID + "\">"
        + "</span>" + NEW_LINE;
    return entityPaginationInfo;
  }

  /**
   * 分页控制区域的HTML代码
   * @return 分页控制区域的HTML代码
   */
  public String getControlAreaHTML() {
    StringBuffer result = new StringBuffer();
    result.append(getTotalCountsHTML());
    result.append(getButtonElementHTML(firstPage, "PAGINATION_FIRST"));
    result.append(getButtonElementHTML(priorPage, "PAGINATION_PRIOR"));
    result.append(getButtonElementHTML(nextPage, "PAGINATION_NEXT"));
    result.append(getButtonElementHTML(lastPage, "PAGINATION_LAST"));
    result.append(getLabelElementHTML(pageInfo, "PAGINATION_PAGE"));
    result.append(jumpToSpecifyHTML()); // 跳转页
    return result.toString();
  }

  /**
   * 按钮元素的HTML代码
   * @param callName 按钮名称
   * @param callNameResID 按钮显示文本的语言代码
   * @return 按钮元素的HTML代码
   */
  private String getButtonElementHTML(String callName, String callNameResID) {
    StringBuffer result = new StringBuffer();
    String title = "快捷键是:";
    if (callName.equals(firstPage)) {
      title += "Home键";
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
      title += "PageUp键";
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
      title += "PageDown键";
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
      title += "End键";
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
   * 标签元素的HTML代码
   * 
   * @param labelName
   *          标签名称
   * @param labelNameResID
   *          标签页
   * @return 标签元素的HTML代码
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
   * “共几条”的HTML代码
   * 
   * @return 标签元素的HTML代码
   */
  private String getTotalCountsHTML() {
    StringBuffer result = new StringBuffer();
    result.append(StringTools.getMargin(7))
        	.append("<span class=\"clsPaginationLabel\">共</span>")
        	.append("<span id=\"totalCountsID\" class=\"clsPaginationLabel\">")
        	.append(totalCount)
        	.append("</span><span class=\"clsPaginationLabel\">条</span>")
        	.append(NEW_LINE);
    return result.toString();
  }

  /**
   * 跳转到指定页的HTML代码 wtm，20040706 malength:最大可跳转页9999，
   * 
   * @return 标签元素的HTML代码
   */
  private String jumpToSpecifyHTML() {
    StringBuffer result = new StringBuffer();
    result.append(StringTools.getMargin(7))
          .append("<span class=\"clsPaginationLabel\">转到第</span>")
          .append("<input type=\"text\" name=\"jumpToPage\" id=\"jumpToPageID\" onKeyPress='jumpToPage_KeyPress()' maxlength=\"4\" size=\"3\">")
          .append("</input>")
          .append("</span><span class=\"clsPaginationLabel\">页</span>")
          .append("<input type=\"button\"  name=\"go\" id=\"goID\" value=\"GO\" onclick=\"go()\" alt=\"开始查找\">")
          .append("</input>")
          .append(NEW_LINE);
    return result.toString();
  }

  /** 换行符 */
  private static final String NEW_LINE = System.getProperty("line.separator");

  /** 首页 */
  private final String firstPage = "firstpage";

  /** 前一页 */
  private final String priorPage = "priorpage";

  /** 下一页 */
  private final String nextPage = "nextpage";

  /** 末页 */
  private final String lastPage = "lastpage";

  /** 页信息 */
  private final String pageInfo = "pageinfo";
  
  /** 部件名 */
  private String compoName;

  /** 语言资源对象 */
  private LangResource resource;

  /** 页面唯一性ID */
  private String pageUniqueID;

  /** 当前页码 */
  private String currentPage;

  /** 总页码 */
  private String totalPage;
  
  /** 总条数 */
  private String totalCount;

  /** 部件分页信息 */
  private String entityPaginationInfo;

  /** 请求对象 */
  private HttpServletRequest request ;

}
