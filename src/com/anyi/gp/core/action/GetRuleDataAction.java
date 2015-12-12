package com.anyi.gp.core.action;

import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.anyi.gp.BusinessException;
import com.anyi.gp.Datum;
import com.anyi.gp.Pub;
import com.anyi.gp.TableData;
import com.anyi.gp.access.CommonService;
import com.anyi.gp.pub.LangResource;
import com.anyi.gp.pub.SessionUtils;
import com.anyi.gp.taglib.components.Grid;
import com.anyi.gp.util.XMLTools;
import com.opensymphony.webwork.interceptor.ServletRequestAware;
import com.opensymphony.webwork.interceptor.ServletResponseAware;
import com.opensymphony.xwork.ActionSupport;

public class GetRuleDataAction extends ActionSupport implements ServletRequestAware, ServletResponseAware {

  private static final long serialVersionUID = 6701339474528257741L;

  private static final String CONTENT_TYPE = "text/html; charset=GBK";
  
  private CommonService service;

  private String param;

  private String ruleID;

  private String fieldsWithKilo;

  private String gridid;

  private String pageName;

  private String areaID;

  private String style;

  private String arrayStyle;

  private String pagesize;

  private HttpServletRequest request;

  private HttpServletResponse response;
  
  public String execute() throws Exception {
      response.setContentType(CONTENT_TYPE);
      Writer writer = response.getWriter();

      try {
          TableData tempParam = new TableData(XMLTools.stringToDocument(param)
                  .getDocumentElement());
          Datum datum = getRuleDataDatum(ruleID, tempParam, fieldsWithKilo);
          writer.write("<xml id=\"TableData_" + getTableName()
                  + "_XML\" asynch=\"false\" encoding=\"GBK\">");

          datum.printData(writer);
          writer.write("</xml>");

          if (datum.getData().size() == 0) {
              return NONE;
          }

          datum.setName(getTableName());

          if (style == null || style.trim().length() < 10) {
              Map data = (Map) datum.getData().get(0);

              Set fieldSet = data.keySet();
              if (fieldSet.isEmpty())
                  return NONE;

              String[] vasStyle = makeReportGridDefaultStyle(fieldSet, data);
              if (vasStyle == null) {
                  return NONE;
              }

              style = vasStyle[0];
              arrayStyle = vasStyle[1];
          }

          makeReportGrid(style, arrayStyle, writer, datum);
      } catch (Exception ex) {
        if(ex instanceof BusinessException)
          writer.write(ex.getMessage());
      }
      return NONE;
  }


  private String getTableName() {
    String vsTableName = areaID;
    if (areaID == null || areaID.trim().length() < 2) {
      vsTableName = gridid;
    }
    return vsTableName;
  }

  private Datum getRuleDataDatum(String ruleID, TableData param,
    String fieldsWithKilo) throws BusinessException {

    Datum datum = null;
    try{
      datum = (Datum)service.invokeMethod(ruleID, "getReportData"
        , new Class[]{TableData.class}, new Object[]{param});
    }catch(Exception e){
      if(e instanceof BusinessException){
        throw (BusinessException)e;
      }
      LOG.debug(e);
      datum = null;
    }
    
    if(datum == null){
      datum = service.getDatum(ruleID, param);
    }
    
    datum.setName(getTableName());
    datum.addMetaField("pageindex", 1 + "");
    datum.addMetaField("fromrow", 1 + "");
    datum.addMetaField("pagesize", 0 + "");
    datum.addMetaField("rowcountofdb", datum.getData().size() + "");

//    if (delta != null) {
//      String[] vasField = StringTools.split2(fieldsWithKilo, ",");
//      if (vasField != null) {
//        List voKiloFields = new ArrayList();
//        for (int i = 0; i < vasField.length; i++) {
//          if (vasField[i] == null)
//            continue;
//          if ("".equals(vasField[i]))
//            continue;
//          voKiloFields.add(vasField[i]);
//        }
//        delta.setKiloFieldList(voKiloFields);
//      }
//    }
    return datum;
  }

  /**
   * 生成默认的风格;
   *
   * @param oFieldsList
   * @return
   */
  private String[] makeReportGridDefaultStyle(Set fieldSet, Map data) {
    if (fieldSet == null) {
      return null;
    }
    
    Object voFieldName = null;
    String vsName = "";
    int viCol = 0;
    LangResource resource = LangResource.getInstance();
    StringBuffer voBuf = new StringBuffer();
    voBuf.append("<delta>\n");
    for (Iterator iter = fieldSet.iterator(); iter.hasNext();) {
      voFieldName = iter.next();
      if (voFieldName == null) {
        continue;
      }
      
      vsName = resource.getLang((String) voFieldName);
      voBuf.append("<entity>\n");
      voBuf.append("<field name= 'NAME' value= '" + (String) voFieldName + "'/>\n");
      voBuf.append("<field name= 'ROWS' value= '1'/>\n");
      voBuf.append("<field name= 'COLS' value= '1'/>\n");
      voBuf.append("<field name= 'CAPTION' value= '" + vsName + "'/>\n");
      voBuf.append("<field name= 'TYPE' value= '1'/>\n");
      Object vFieldValue = data.get(voFieldName.toString());
      
      if (isNumber(vFieldValue))
        voBuf.append("<field name= 'ALIGN' value= 'RIGHT'/>\n");
      else
        voBuf.append("<field name= 'ALIGN' value= ''/>\n");
      
      voBuf.append("</entity>\n");
      viCol++;
    }
    
    voBuf.append("</delta>\n");
    if (viCol <= 0) {
      return null;
    }
    
    String[] vasStyle = new String[] { voBuf.toString(), viCol + "*1" };
    return vasStyle;
  }

  private boolean isNumber(Object o) {
    if (o == null)
      return false;
    
    return (o instanceof Number || o instanceof BigInteger
      || o instanceof BigDecimal || o instanceof Integer);
  }

  /**
   * 生成 Report Grid;
   *
   * @param sStyle
   * @param sArrayStyle
   * @param iPageSize
   * @param sTableName
   * @param sGridId
   * @param request
   * @return
   * @throws IOException 
   */
  private void makeReportGrid(String style, String arrayStyle,
    Writer writer, Datum datum) throws IOException {
    
    String tableName = getTableName();
    Grid voGrid = new Grid();
    voGrid.setPagenameforarea(pageName);
    voGrid.setUseridforarea(SessionUtils.getAttribute(request, "svUserID"));
    voGrid.setType(Grid.TYPE_GRID);
    voGrid.setId(gridid);
    voGrid.setIdsuffix("_Grid" + Pub.getUID());
    voGrid.setTableName(tableName);
    voGrid.setPagesize(Pub.parseInt(pagesize));
    voGrid.setReportStyle(style, arrayStyle);
    voGrid.setPosition("relative");
    voGrid.setIsfromdb(false);
    voGrid.setIsregjs(false);
    voGrid.setRequest(request);
    voGrid.setMainData(datum);
    
    voGrid.writeHTML(writer);
  }

  public void setAreaID(String areaID) {
    this.areaID = areaID;
  }

  public void setArrayStyle(String arrayStyle) {
    this.arrayStyle = arrayStyle;
  }

  public void setFieldsWithKilo(String fieldsWithKilo) {
    this.fieldsWithKilo = fieldsWithKilo;
  }

  public void setGridid(String gridid) {
    this.gridid = gridid;
  }

  public void setPageName(String pageName) {
    this.pageName = pageName;
  }

  public void setPagesize(String pagesize) {
    this.pagesize = pagesize;
  }

  public void setParam(String param) {
    this.param = param;
  }

  public void setRuleID(String ruleID) {
    this.ruleID = ruleID;
  }

  public void setService(CommonService service) {
    this.service = service;
  }

  public void setStyle(String style) {
    this.style = style;
  }

  public void setServletRequest(HttpServletRequest request) {
    this.request = request;
  }

  public void setServletResponse(HttpServletResponse response) {
    this.response = response;
  }

}
