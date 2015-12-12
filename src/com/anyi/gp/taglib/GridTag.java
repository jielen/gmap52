//$Id: GridTag.java,v 1.20 2008/06/02 13:42:21 huangcb Exp $
package com.anyi.gp.taglib;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.anyi.gp.taglib.components.Grid;
import com.anyi.gp.taglib.components.Page;

public class GridTag extends BodyTagSupport implements IChildTag{
  
  private static final long serialVersionUID = 647593497411367187L;
  
  private Grid grid = new Grid();

  public GridTag() {
  }

  public int doStartTag() throws JspException {
    return beginX(pageContext.getOut());
  }

  public int doEndTag() throws JspException {
    Page.addGrid(pageContext.getRequest(), grid);
    try {
      grid.writeHTML(pageContext.getOut());
    } catch (IOException e) {
      throw new JspException(e);
    }
    this.grid = new Grid();
    return EVAL_PAGE;
  }

  public int beginX(Writer out) throws JspException {
    return EVAL_BODY_BUFFERED;
  }

  public void setChildBodyText(String sTagName, String sText) {
    if (sTagName.equals(DataTag.TAG_NAME) == false) {
      return;
    }
    this.grid.setBodyText(sText);
  }

  public void setBoxsetid(String boxsetid) {
    grid.setBoxsetid(boxsetid);
  }

  public void setCardcolcount(int cardcolcount) {
    grid.setCardcolcount(cardcolcount);
  }

  public void setComponame(String componame) {
    grid.setCompoName(componame);
  }

  public void setCssclass(String cssclass) {
    grid.setCssclass(cssclass);
  }

  public void setHeadrowheight(int headrowheight) {
    grid.setHeadrowheight(headrowheight);
  }

  public void setIdsuffix(String idsuffix) {
    grid.setIdsuffix(idsuffix);
  }

  public void setInnerlinecolor(String innerlinecolor) {
    grid.setInnerlinecolor(innerlinecolor);
  }

  public void setIsappendbutton(boolean isappendbutton) {
    grid.setIsappendbutton(isappendbutton);
  }

  public void setIsenterfirstrow(boolean isenterfirstrow) {
    grid.setIsenterfirstrow(isenterfirstrow);
  }

  public void setIsexistcheck(boolean isexistcheck) {
    grid.setIsexistcheck(isexistcheck);
  }

  public void setIsfromdb(boolean isfromdb) {
    grid.setIsfromdb(isfromdb);
  }

  public void setIsinsertbutton(boolean isinsertbutton) {
    grid.setIsinsertbutton(isinsertbutton);
  }

  public void setIslightrow(boolean islightrow) {
    grid.setIslightrow(islightrow);
  }

  public void setIsmultisel(boolean ismultisel) {
    grid.setIsmultisel(ismultisel);
  }

  public void setIsreadonly(boolean isreadonly) {
    grid.setIsreadonly(isreadonly);
  }

  public void setIssavepropbutton(boolean issavepropbutton) {
    grid.setIssavepropbutton(issavepropbutton);
  }

  public void setIsvisible(boolean isvisible) {
    grid.setIsvisible(isvisible);
  }

  public void setIswritable(boolean iswritable) {
    grid.setIswritable(iswritable);
  }

  public void setOninit(String oninit) {
    grid.setOninit(oninit);
  }

  public void setPagesize(int pagesize) {
    grid.setPagesize(pagesize);
  }

  public void setPropfileid(String propfileid) {
    grid.setPropfileid(propfileid);
  }

  public void setRowheight(int rowheight) {
    grid.setRowheight(rowheight);
  }

  public void setStyle(String style) {
    grid.setStyle(style);
  }

  public void setSumbackcolor(String sumbackcolor) {
    grid.setSumbackcolor(sumbackcolor);
  }

  public void setSumcond(String sumcond) {
    grid.setSumcond(sumcond);
  }

  public void setSumdesc(String sumdesc) {
    grid.setSumdesc(sumdesc);
  }

  public void setSumdescfield(String sumdescfield) {
    grid.setSumdescfield(sumdescfield);
  }

  public void setSumfields(String sumfields) {
    grid.setSumfields(sumfields);
  }

  public void setTabindex(int tabindex) {
    grid.setTabindex(tabindex);
  }

  public void setTablename(String tablename) {
    grid.setTableName(tablename);
  }

  public void setTotaldesc(String totaldesc) {
    grid.setTotaldesc(totaldesc);
  }

  public void setTotaldescfield(String totaldescfield) {
    grid.setTotaldescfield(totaldescfield);
  }

  public void setType(String type) {
    grid.setType(type);
  }

  public void setId(String id) {
    grid.setId(id);
  }

  public void setIsdeletebutton(boolean isdeletebutton) {
    grid.setIsdeletebutton(isdeletebutton);
  }

  public void setIsdeletepropbutton(boolean isdeletepropbutton) {
    grid.setIsdeletepropbutton(isdeletepropbutton);
  }

  public void setIsautoappear(boolean isautoappear) {
    grid.setIsautoappear(isautoappear);
  }

}
