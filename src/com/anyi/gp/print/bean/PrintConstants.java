package com.anyi.gp.print.bean;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2002</p>
 *
 * <p>Company: </p>
 *
 * @author zhangyw
 * @version 1.0
 */
public interface PrintConstants{

  /**
   * OUT FILE TYPE
   */
  public static final String EXPORT_PDF = "0";

  public static final String EXPORT_XLS = "1";

  public static final String EXPORT_HTML = "2";

  public static final String EXPORT_CSV = "3";

  public static final String EXPORT_XML = "4";

  public static final String EXPORT_RTF = "5";

  public static final String EXPORT_TEXT = "6";


  /**
   * DYNAMIC TPL
   */
  public static final String DYNAMIC_COLUMN_TPL = "1";

  public static final String CONTINUE_PRINT = "Y";

  public static final String NO_SELECTED_ROWS = "NO_SELECTED_ROWS";

  public static final String PRINT_TO_PRINTER = "Y";

  public static final String IS_NOT_PREVIEW = "N";


  /**
   * Data Type
   */
  public static final String DATA_TYPE_DELTA = "Delta";

  public static final String DATA_TYPE_TABLEDATA = "TableData";

  public static final String DATA_TYPE_PAGE_XML = "PageXML";

  public static final String DATA_TYPE_RESULTSET = "ResultSet";

  public static final String DATA_TYPE_DELTA_STRING = "DeltaString";

  public static final String DATA_TYPE_TABLEDATA_STRING = "TableDataString";

  /**
   * Data Tag
   */
  public static final String DATA_TAG_PAGEDATA = "PageData:";

  public static final String DATA_TAG_TABLEDATA = "<entity";

  public static final String DATA_TAG_DELTA = "<delta>";

  public static final String DATA_TAG_TEMPLATE = "<template>";

  public static final String DATA_TAG_XMLDATAS = "<XMLDATAS>";

  public static final String DATA_TAG_XMLDATA = "<XMLDATA>";


  /**
   * Print Type
   */
  public static final String PRINT_TYPE_EDITPAGE_TEMPLATE = "editPage_template";

  public static final String PRINT_TYPE_EDITPAGE_NOTEMPLATE = "editPage_noTemplate";

  public static final String PRINT_TYPE_REPORTPAGE_TEMPLATE = "reportPage_template";

  public static final String PRINT_TYPE_REPORTPAGE_NOTEMPLATE = "reportPage_noTemplate";

  public static final String PRINT_TYPE_LISTPAGE_TEMPLATE = "listPage_template";

  public static final String PRINT_TYPE_LISTPAGE_NOTEMPLATE = "listPage_noTemplate";


  /**
   * Request type
   */
  public static final String REQUEST_TYPE_XMLHTTP = "xmlhttp";


  /**
   * Print Parameter Name
   */
  public static final String PRINT_PARAMETER_TPL_CODE = "tplCode";

  public static final String PRINT_PARAMETER_PRINT_DATA = "printData";
  
  public static final String PRINT_PARAMETER_VALUE_SET = "valueSet";

  public static final String PRINT_PARAMETER_FIXROWCOUNT = "fixRowCount";

  public static final String PRINT_PARAMETER_EXPORT_TYPE = "exportType";

  public static final String PRINT_PARAMETER_COMPO_NAME = "compoName";

  public static final String PRINT_PARAMETER_PAGE_NAME = "pageName";

  public static final String PRINT_PARAMETER_AREA_NAME = "areaName";

  public static final String PRINT_PARAMETER_GRID_ID = "gridId";

  public static final String PRINT_PARAMETER_DYNAMIC_TPL = "dynamicTpl";

  public static final String PRINT_PARAMETER_OPTIONS = "printOptions";

  public static final String PRINT_PARAMETER_PRINTTO_PRINTER = "printToPrinter";

  public static final String PRINT_PARAMETER_REQUEST_TYPE = "requestType";

  public static final String PRINT_PARAMETER_PRINT_HEADDATA = "printHeadData";

  public static final String PRINT_PARAMETER_REQUEST = "request";

  public static final String PRINT_PARAMETER_RESPONSE = "response";

  public static final String PRINT_PARAMETER_NOTEMPLATE_DESIGN = "noTemplateDesign";

  public static final String PRINT_PARAMETER_NOTEMPLATE_PARAMVALUES =
      "noTemplateParaValues";

  public static final String PRINT_PARAMETER_OUT_PARAMETERS = "outParameters";

  public static final String PRINT_PARAMETER_OUT_DATASOURCE = "outDataSource";

  public static final String PRINT_PARAMETER_JREPORT_LIST = "jrReportList";

  public static final String PRINT_PARAMETER_DATA_TYPE = "dataType";

  public static final String PRINT_PARAMETER_DATA = "data";
  
  public static final String PRINT_PARAMETER_SESSION = "sessionMap";

  public static final String PRINT_PARAMETER_PRINT_TYPE = "printType";

  public static final String PRINT_PARAMETER_CHILDTABLE_NAME = "childTableName";

  public static final String PRINT_PARAMETER_CONTINUE_PRINT = "continuePrint";

  public static final String PRINT_PARAMETER_CONTINUE_CONDITION = "continueCondition";
  
  public static final String PRINT_PARAMETER_CONTINUE_RULEID = "continueRuleID";
  
  public static final String PRINT_PARAMETER_IS_SELECT_ROWS = "isSelectRows";

  public static final String PRINT_PARAMETER_LISTPAGE_CONDITION = "listPageCondition";

  public static final String PRINT_PARAMETER_LISTPAGE_KEY_CONDITION ="listPageKeyCondition";
  
  public static final String PRINT_PARAMETER_LISTPAGE_RULEID = "listPageRuleID";

  public static final String PRINT_PARAMETER_LISTPAGE_HEADDATA = "listPageHeadData";
  /**
   * Debug
   */

  public static final String PRINT_DEBUG = "Debug";

  public static final String PRINT_DEBUG_DATA_PATH = "dataPath";

  public static final String PRINT_DEBUG_TEMPLATE_PATH = "templatePath";

  public static final String PRINT_DEBUG_EXPORTFILE_PATH = "exportfilePath";

  public static final String PRINT_DEBUG_EXPORTFILE_NAME = "exportfileName";

  /**
   * Export File
   */

  public static final String PRINT_EXPORT_DESTFILE_NAME = "exportDestFileName";

  /**
   * Servlet Path
   */
  public static final String PRINT_SERVLET_PATH = "printServletPath";

  public static final String PRINT_SERVLET_PATH_EDITPRINT = "/editprint";

  public static final String PRINT_SERVLET_PATH_EDITPRINTNOTPL = "/editprintnotpl";

  public static final String PRINT_SERVLET_PATH_LISTPRINT = "/listprint";

  public static final String PRINT_SERVLET_PATH_LISTPRINTWITHTPL =
      "/listprintwithtpl";

  public static final String PRINT_SERVLET_PATH_SPLITPRINT = "/splitprint";

  public static final String PRINT_SERVLET_PATH_SPLITPRINTNOTPL = "/splitprintnotpl";
}
