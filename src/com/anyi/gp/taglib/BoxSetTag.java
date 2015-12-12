package com.anyi.gp.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.anyi.gp.taglib.components.BoxSet;
import com.anyi.gp.taglib.components.Page;

/**
 * @author leidaohong
 */
public class BoxSetTag extends BodyTagSupport implements IChildTag {
  private static final long serialVersionUID = -4862960939050317498L;

  private BoxSet boxset = new BoxSet();

  public BoxSetTag() {
  }

  public int doStartTag() throws JspException {
    return EVAL_BODY_BUFFERED;
  }

  public int doEndTag() throws JspException {
    Page.addBoxSet(pageContext.getRequest(), boxset);
    try {
      boxset.writeHTML(pageContext.getOut());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    this.boxset = new BoxSet();
    return EVAL_PAGE;
  }


  public void setChildBodyText(String tagName, String text) {
    if (tagName.equals(DataTag.TAG_NAME) == false)
      return;
    boxset.setBodyText("<root>" + text + "</root>");
  }

  public void setCaptionwidths(String captionwidths) {
    boxset.setCaptionwidths(captionwidths);
  }

  public void setCols(int cols) {
    boxset.setCols(cols);
  }

  public void setComponame(String componame) {
    boxset.setCompoName(componame);
  }

  public void setCssclass(String cssclass) {
    boxset.setCssclass(cssclass);
  }

  public void setFields(String fields) {
    boxset.setFields(fields);
  }

  public void setId(String id) {
    boxset.setId(id);
  }

  public void setIdsuffix(String idsuffix) {
    boxset.setIdsuffix(idsuffix);
  }

  public void setIsallowinit(boolean isallowinit) {
    boxset.setIsallowinit(isallowinit);
  }

  public void setIsautoappear(boolean isautoappear) {
    boxset.setIsautoappear(isautoappear);
  }

  public void setIsfromdb(boolean isfromdb) {
    boxset.setIsfromdb(isfromdb);
  }

  public void setIsreadonly(boolean isreadonly) {
    boxset.setIsreadonly(isreadonly);
  }

  public void setIssynchfieldvisible(boolean issynchfieldvisible) {
    boxset.setIssynchfieldvisible(issynchfieldvisible);
  }

  public void setIsvisible(boolean isvisible) {
    boxset.setIsvisible(isvisible);
  }

  public void setIswritable(boolean iswritable) {
    boxset.setIswritable(iswritable);
  }

  public void setOninit(String oninit) {
    boxset.setOninit(oninit);
  }

  public void setRelaobjid(String relaobjid) {
    boxset.setRelaobjid(relaobjid);
  }

  public void setRelaobjtype(String relaobjtype) {
    boxset.setRelaobjtype(relaobjtype);
  }

  public void setRows(int rows) {
    boxset.setRows(rows);
  }

  public void setStyle(String style) {
    boxset.setStyle(style);
  }

  public void setTabindex(int tabindex) {
    boxset.setTabindex(tabindex);
  }

  public void setTablename(String tablename) {
    boxset.setTableName(tablename);
  }

}
