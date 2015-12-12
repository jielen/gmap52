package com.anyi.gp.taglib;

import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.JspException;

import org.apache.log4j.Logger;
import org.w3c.dom.Node;

import com.anyi.gp.Delta;
import com.anyi.gp.Pub;
import com.anyi.gp.TableData;
import com.anyi.gp.util.XMLTools;

public class TrackViewTag extends BaseTag implements IChildTag, IVisibleTag{
  private static final Logger log = Logger.getLogger(TrackViewTag.class);

  private static final String ID_SUFFIX = "_TrackView";
  
  static final String TABLE_TRACK_FLOW = "TRACK_FLOW";
  static final String TABLE_TRACK_NODE = "TRACK_NODE";
  
  static final String FLOW_WAY_IN= "in";
  static final String FLOW_WAY_OUT= "out";
  static final String FLOW_WAY_BACK= "back";
  
  private String dataclass= "";
  private boolean isvisible = true;
  private String nodealign= "";
  private String nodetextalign= "";
  private String flowtextalign= "";
  private int nodewidth= 0;
  private int flowwidth= 0;
  private int arrowwidth= 0;
  private String forwardlinecolor= "";
  private String backlinecolor= "";
  private String rootbordercolor= "";
  private String nodebordercolor= "";
  private String flowbordercolor= "";
  private int rootborderwidth= 1;
  private int nodeborderwidth= 1;
  private int flowborderwidth= 0;
  private String cssclass = "";
  private String style = "";
  private String rootid= "";
  private String currentid= "";
  private String flowidfield = "";
  private String flowcapfield = "";
  private String flowwayfield = "";
  private String flowtipfield = "";
  private String flowsournodeidfield = "";
  private String flowdestnodeidfield = "";
  private String nodeidfield = "";
  private String nodecapfield = "";
  private String nodetipfield = "";
  //以上进入 taglib;

  private Delta flowDelta= null;
  private Map nodeMap= null;
  private Map forwardAndBackFlowMap= null;
  private List flowNodeFrameIdList= null; 
  private List nodeIdList= null; 
  private List flowIdList= null;
  private Map usedFlowNodeMap= null;
  
  public TrackViewTag() {
    initFieldsOfTag();
    initFieldsOfNotTag();
  }

  public void release() {
    super.release();
    initFieldsOfTag();
    initFieldsOfNotTag();
  }

  void initFieldsOfTag() {
    dataclass= "";
    isvisible = true;
    nodealign= "top";
    nodetextalign= "left";
    flowtextalign= "center";
    nodewidth= 100;
    flowwidth= 80;
    arrowwidth= 20;
    forwardlinecolor= "black";
    backlinecolor= "blue";
    cssclass = "";
    style = "";
    rootid= "";
    currentid= "";
    flowidfield = "ID";
    flowcapfield = "CAP";
    flowwayfield = "WAY";
    flowtipfield = "TIP";
    flowsournodeidfield= "SOUR_NODE_ID";
    flowdestnodeidfield= "DEST_NODE_ID";
    nodeidfield = "ID";
    nodecapfield = "CAP";
    nodetipfield = "TIP";
    rootbordercolor= "red";
    nodebordercolor= "gray";
    flowbordercolor= "gray";
    rootborderwidth= 1;
    nodeborderwidth= 1;
    flowborderwidth= 0;

    flowDelta= null;
    nodeMap= null;
}

  void initFieldsOfNotTag() {
    flowNodeFrameIdList= null;
    nodeIdList= null; 
    flowIdList= null; 
    forwardAndBackFlowMap= null;
    usedFlowNodeMap= null;
  }

  public int doStartTag() throws JspException {
    return beginX(pageContext.getOut());
  }

  public int doEndTag() throws JspException {
    return endX(pageContext.getOut());
  }

  public int beginX(Writer out) throws JspException {
    return EVAL_BODY_BUFFERED;
  }

  public int endX(Writer out) throws JspException {
    try {
      initFieldsOfNotTag();
      init();
      regObj();
      out.write(make());
      out.write(makeJS());
    } catch (Exception e) {
      String msg = "\nTrackViewTag.endX():\n" + e.getMessage();
      log.debug(msg);
      e.printStackTrace();
      throw new JspException(msg);
    }
    return EVAL_PAGE;
  }

  public String getBodytext() {
    return "";
  }

  public void setBodytext(String text) {
  }

  public void init() {
    ITrackViewData dataObj= newDataClassObj();
    if (dataObj!= null){
      dataObj.prepare(rootid);
      dataObj.prepare(currentid);
      flowDelta= dataObj.getFlowData();
      nodeMap= genNodeMapFromDelta(dataObj.getNodeData());
    }
    checkData();
    forwardAndBackFlowMap= genForwardAndBackFlow();
    flowNodeFrameIdList= new ArrayList();
    nodeIdList= new ArrayList();
    flowIdList= new ArrayList();
    usedFlowNodeMap= new HashMap();
    if (this.getId() == null || this.getId().length() == 0){
      this.setId("PETRI_TRACK_" + Pub.getUID()+ "_"+ ID_SUFFIX);
    }
  }
  
  /**
   * 建立forward流与back流之间的关系;
   * @return
   */
  Map genForwardAndBackFlow(){
    Map inMap= new HashMap();
    Map outMap= new HashMap();
    for (Iterator iter= flowDelta.iterator(); iter.hasNext();){
      TableData row= (TableData)iter.next();
      String way= row.getFieldValue(flowwayfield);
      if (FLOW_WAY_BACK.equals(way)) continue;
      String flowId= row.getFieldValue(flowidfield);
      if (FLOW_WAY_IN.equals(way)){
        String sourNodeId= row.getFieldValue(flowsournodeidfield);
        inMap.put(sourNodeId, flowId);
      }else if (FLOW_WAY_OUT.equals(way)){
        String destNodeId= row.getFieldValue(flowdestnodeidfield);
        outMap.put(destNodeId, flowId);
      }
    }
    Map result= new HashMap();
    for (Iterator iter= flowDelta.iterator(); iter.hasNext();){
      TableData row= (TableData)iter.next();
      String way= row.getFieldValue(flowwayfield);
      if (!FLOW_WAY_BACK.equals(way)) continue;
      String sourNodeId= row.getFieldValue(flowsournodeidfield);
      String destNodeId= row.getFieldValue(flowdestnodeidfield);
      String flowId= null;
      if (inMap.containsKey(sourNodeId)){
        flowId= (String)inMap.get(sourNodeId);
      }else if (outMap.containsKey(destNodeId)){
        flowId= (String)outMap.get(destNodeId);
      }
      if (flowId== null) continue;
      result.put(flowId, row);
    }
    return result;
  }
  
  /**
   * <applus:area> 接口方法的实现;
   */
  public void setTagAttributes(Node tagNode) {
    setId(XMLTools.getNodeAttr(tagNode, "id", id));
    setDataclass(XMLTools.getNodeAttr(tagNode, "dataclass", dataclass));
    setIsvisible(Boolean
        .valueOf(XMLTools.getNodeAttr(tagNode, "isvisible", ""
                + isvisible)).booleanValue());
    setNodealign(XMLTools.getNodeAttr(tagNode, "nodealign", nodealign));
    setNodetextalign(XMLTools.getNodeAttr(tagNode, "nodetextalign", nodetextalign));
    setFlowtextalign(XMLTools.getNodeAttr(tagNode, "flowtextalign", flowtextalign));
    setNodewidth(Integer.parseInt(XMLTools.getNodeAttr(tagNode, "nodewidth", ""+ nodewidth)));
    setCssclass(XMLTools.getNodeAttr(tagNode, "cssclass", cssclass));
    setStyle(XMLTools.getNodeAttr(tagNode, "style", style));
    setRootid(XMLTools.getNodeAttr(tagNode, "rootid", rootid));
    setCurrentid(XMLTools.getNodeAttr(tagNode, "currentid", currentid));
    setFlowidfield(XMLTools.getNodeAttr(tagNode, "flowidfield", flowidfield));
    setFlowcapfield(XMLTools.getNodeAttr(tagNode, "flowcapfield", flowcapfield));
    setFlowwayfield(XMLTools.getNodeAttr(tagNode, "flowwayfield", flowwayfield));
    setFlowtipfield(XMLTools.getNodeAttr(tagNode, "flowtipfield", flowtipfield));
    setFlowsournodeidfield(XMLTools.getNodeAttr(tagNode, "flowsournodeidfield", flowsournodeidfield));
    setFlowdestnodeidfield(XMLTools.getNodeAttr(tagNode, "flowdestnodeidfield", flowdestnodeidfield));
    setNodeidfield(XMLTools.getNodeAttr(tagNode, "nodeidfield", nodeidfield));
    setNodecapfield(XMLTools.getNodeAttr(tagNode, "nodecapfield", nodecapfield));
    setNodetipfield(XMLTools.getNodeAttr(tagNode, "nodetipfield", nodetipfield));
  }

  public boolean isContainer() {
    return false;
  }

  public boolean isAllowChildren() {
    return true;
  }

  public boolean isAllowBody() {
    return false;
  }
  
  void checkData(){
    if (flowDelta== null){
      throw new RuntimeException("\nTrackViewTag.checkData():\n没有发现表["+ TABLE_TRACK_FLOW+ "]的数据或接口["+ dataclass+ ".getFlowData()]提供的数据为空;");
    }
    if (nodeMap== null){
      throw new RuntimeException("\nTrackViewTag.checkData():\n没有发现表["+ TABLE_TRACK_NODE+ "]的数据或接口["+ dataclass+ ".getNodeData()]提供的数据为空;");
    }
  }

  ITrackViewData newDataClassObj(){
    if (dataclass== null || dataclass.trim().equals("")) return null;
    Class objClass = null;
    Object object = null;
    try {
      objClass = Class.forName(dataclass);
    } catch (ClassNotFoundException e) {
      String msg= "\nTrackViewTag.newDataClassObj():\n类没有发现;\nclass name: "+ dataclass+ "\n"+ e.getMessage();
      log.debug(msg);
      throw new RuntimeException(e);
    }
    try {
      object = objClass.newInstance();
    } catch (InstantiationException e) {
      String msg= "\nTrackViewTag.newDataClassObj():\n创建类的实例异常;\nclass name: "+ dataclass+ "\n"+ e.getMessage();
      log.debug(msg);
      throw new RuntimeException(e);
    } catch (IllegalAccessException e) {
      String msg= "\nTrackViewTag.newDataClassObj():\n非法创建类的实例异常;\nclass name: "+ dataclass+ "\n"+ e.getMessage();
      log.debug(msg);
      throw new RuntimeException(e);
    }
    return (ITrackViewData)object;
  }

  public String make() {
    StringBuffer buf= new StringBuffer();
    buf.append(makeOuterPanel_1());
    buf.append(makeInnerPanel_1("NodeFrame_InnerPanel", 99, 2));
    buf.append(makeFrame());
    buf.append(makeInnerPanel_2());
    buf.append(makeInnerPanel_1("Arrow_InnerPanel", 0, 0));
    buf.append(makeArrows());
    buf.append(makeInnerPanel_2());
    buf.append(TagPub.makeIdListSpan("NodeIdsSpan", nodeIdList, "field"));
    buf.append(TagPub.makeIdListSpan("FlowIdsSpan", flowIdList, "field"));
    buf.append(TagPub.makeIdListSpan("FlowNodeFrameIdsSpan", flowNodeFrameIdList, "field"));
    buf.append(makeOuterPanel_2());
    return buf.toString();
  }

  private String getPageInitMethod(){
    return "_TrackView_"+ getId();
  }

  public String makeJS() {
    String vsScriptID = "Script_" + Pub.getUID();
    StringBuffer buf = new StringBuffer();
    buf
        .append("<script id=\"" + vsScriptID + "\" language=\"javascript\">\n");
    buf.append("function " + getPageInitMethod() + "(){\n");
    
    buf.append("var voBeforeTime = new Date().getTime();\n");
    buf.append("var voTrackView = new TrackView();\n");
    buf.append("var voAfterTime = new Date().getTime();\n");
    buf.append("voTrackView.tNewTime = voAfterTime - voBeforeTime;\n");
    
    buf.append("voBeforeTime = voAfterTime;\n");
    buf.append("voTrackView.make('" + this.getId() + "');\n");
    buf.append("voAfterTime = new Date().getTime();\n");
    buf.append("voTrackView.tMakeTime = voAfterTime - voBeforeTime;\n"); 
    
    buf.append("voBeforeTime = voAfterTime;\n");
    buf.append("voTrackView.init();\n");
    buf.append("voAfterTime = new Date().getTime();\n");
    buf.append("voTrackView.tInitTime = voAfterTime - voBeforeTime;\n");
    
    buf.append("voBeforeTime = voAfterTime;\n");
    buf.append("voTrackView.resize();\n");
    buf.append("voAfterTime = new Date().getTime();\n");
    buf.append("voTrackView.tResizeTime = voAfterTime - voBeforeTime;\n");
    
    // 向 PageX 中注册 TreeView 对象;
    buf.append("PageX.regCtrlObj(\"" + this.getId() + "\", voTrackView);\n");
    buf.append(vsScriptID + ".removeNode(true);\n");
    buf.append("}\n");
    //buf
    //    .append("PageX.regPageInitMethod(" + getPageInitMethod() + ");\n");
    buf.append(getPageInitMethod() + "();");
    buf.append(getPageInitMethod() + "=null;\n");
    buf.append("</script>\n");
    return buf.toString();
  }
  
  String makeRoot(){
    TableData nodeRow= (TableData)nodeMap.get(rootid);
    nodeIdList.add(rootid);
    String currentbordercolor = rootbordercolor;
    if(!rootid.equals(currentid))
      currentbordercolor = nodebordercolor;
    return makeNodeK(getNodeId(rootid), 
        nodeRow.getFieldValue(nodecapfield),
        nodeRow.getFieldValue(nodetipfield),
        makeNodeAttr(nodeRow), 
        nodewidth, nodetextalign, "node", "",
        currentbordercolor, rootborderwidth);
  }
  
  String makeNode(String nodeId){
    TableData nodeRow= (TableData)nodeMap.get(nodeId);
    if (nodeRow== null) return "";
    String nodeTableId= nodeRow.getFieldValue(nodeidfield);
    nodeIdList.add(nodeTableId);
    String currentbordercolor = nodebordercolor;
    if(nodeId.equals(currentid))
      currentbordercolor = rootbordercolor;
    return makeNodeK(getNodeId(nodeTableId), 
        nodeRow.getFieldValue(nodecapfield),
        nodeRow.getFieldValue(nodetipfield),
        makeNodeAttr(nodeRow), 
        nodewidth, nodetextalign, "node", "",
        currentbordercolor, nodeborderwidth);
  }
  
  String makeFlow(TableData row){
    String flowId= row.getFieldValue(flowidfield);
    flowIdList.add(flowId);
    return makeNodeK(getFlowId(flowId), 
        row.getFieldValue(flowcapfield), 
        row.getFieldValue(flowtipfield),
        makeNodeAttr(row), 
        flowwidth, flowtextalign, "flow", 
        row.getFieldValue(flowwayfield),
        flowbordercolor, flowborderwidth);
  }
  
  String getNodeId(String nodeRowId){
    return "node_"+ nodeRowId;
  }
  
  String getFlowId(String flowRowId){
    return "flow_"+ flowRowId;
  }
  
  String getNodeFrameId(){
    return "NodeFrame_"+ Pub.getUID();
  }
  
  String makeNodeK(String id, String name, String tip, String attrs, 
      int width, String alignH, String nodeType, String flowWay,
      String borderColor, int borderWidth){
    StringBuffer buf= new StringBuffer();
    buf.append("<table id=\"");
    buf.append(id);
    buf.append("\" border=\"1\" borderColor=\"");
    buf.append(borderColor);
    buf.append("\" style=\"width:");
    buf.append(width);
    buf.append("px;text-align:");
    buf.append(alignH);
    buf.append(";border-width:0px;font-size:9pt;");
    buf.append(";\" U_FlowWay=\"");
    buf.append(flowWay);
    buf.append("\" cellspacing=\"0\" ");
    buf.append(attrs);
    buf.append(">\n");
    if ("node".equals(nodeType)){
      buf.append("<tr>\n");
      buf.append("<td style=\"font-size:1px;height:10px;border-width:0px;\">");
      buf.append("</td>\n");
      buf.append("</tr>\n");
    }
    buf.append("<tr>\n");
    
    buf.append("<td style=\"border-width:");
    buf.append(borderWidth);
    buf.append("px;\" ");
    if (tip!= null && !tip.trim().equals("")){
      buf.append("title=\"").append(tip).append("\" ");
    }
    buf.append(">");
    buf.append(name);
    buf.append("</td>\n");

    buf.append("</tr>\n");
    if ("flow".equals(nodeType)){
      buf.append("<tr>\n");
      buf.append("<td style=\"font-size:1px;height:10px;border-width:0px;\">");
      buf.append("</td>\n");
      buf.append("</tr>\n");
    }
    buf.append("</table>\n");
    return buf.toString();
  }
  
  String makeNodeAttr(TableData row){
    StringBuffer buf = new StringBuffer();
    List fieldList= row.getFieldNames();
    for (Iterator iter= fieldList.iterator(); iter.hasNext();){
      String field= (String)iter.next();
      buf.append(" _");
      buf.append(field);
      buf.append("=\"");
      buf.append(XMLTools.getValidStringForXML((String)row.getFieldValue(field)));
      buf.append("\"");
    }
    return buf.toString();
  }
  
  String makeFrame(){
    StringBuffer buf= new StringBuffer();
    buf.append("<table id=\"InnerBaseFrameTable\" style=\"font-size:9pt;width:;\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n");
    buf.append("<tr style=\"display:none;\">\n");
    buf.append("<td width=\"50%\">");
    buf.append("</td>\n");
    buf.append("<td>"+ makeFixedWidthTable(nodewidth)+ "</td>\n");
    buf.append("<td width=\"50%\">");
    buf.append("</td>\n");
    buf.append("</tr>\n");
    buf.append("<tr>\n");
    buf.append("<td style=\"text-align:right;vertical-align:top;\">");
    buf.append(makeLeftNodeFrame(rootid));
    buf.append("</td>\n");
    buf.append("<td style=\"vertical-align:");
    buf.append(nodealign);
    buf.append(";\">");
    buf.append(makeRoot());
    buf.append("</td>\n");
    buf.append("<td style=\"text-align:left;vertical-align:top;\">");
    buf.append(makeRightNodeFrame(rootid));
    buf.append("</td>\n");
    buf.append("</tr>\n");
    buf.append("</table>\n");
    return buf.toString();
  }
  
  String makeLeftNodeFrame(String nodeId){
    StringBuffer buf= new StringBuffer();
    if (nodeId== null) nodeId= "";
    for (Iterator iter= flowDelta.iterator(); iter.hasNext();){
      TableData row= (TableData)iter.next();
      String direct= (String)row.getFieldValue(flowwayfield);
      if (!FLOW_WAY_IN.equals(direct)) continue;
      String destNodeId= (String)row.getFieldValue(flowdestnodeidfield);
      if (!nodeId.equals(destNodeId)) continue;
      
      //在一个trackview中流id必须唯一;否则相同的flowId只有第一个被使用;
      String flowId= (String)row.getFieldValue(flowidfield);
      if (usedFlowNodeMap.containsKey(flowId)) continue;
      usedFlowNodeMap.put(flowId, row);
      
      String sourNodeId= (String)row.getFieldValue(flowsournodeidfield);
      String childNodes= makeLeftNodeFrame(sourNodeId);
      String node= makeNode(sourNodeId);
      StringBuffer flowNodeBuf= new StringBuffer();
      flowNodeBuf.append(makeFlow(row));
      if (forwardAndBackFlowMap.containsKey(flowId)){
        TableData backRow= (TableData)forwardAndBackFlowMap.get(flowId);
        flowNodeBuf.append(makeFlow(backRow));
      }
      buf.append(makeLeftNodeFrame(childNodes, node, flowNodeBuf.toString()));
    }
    return buf.toString();
  }
  
  String makeRightNodeFrame(String nodeId){
    StringBuffer buf= new StringBuffer();
    if (nodeId== null) nodeId= "";
    for (Iterator iter= flowDelta.iterator(); iter.hasNext();){
      TableData row= (TableData)iter.next();
      String way= (String)row.getFieldValue(flowwayfield);
      if (!FLOW_WAY_OUT.equals(way)) continue;
      String sourNodeId= (String)row.getFieldValue(flowsournodeidfield);
      if (!nodeId.equals(sourNodeId)) continue;
      
      //在一个trackview中流id必须唯一;否则相同的flowId只有第一个被使用;
      String flowId= (String)row.getFieldValue(flowidfield);
      if (usedFlowNodeMap.containsKey(flowId)) continue;
      usedFlowNodeMap.put(flowId, row);

      String destNodeId= (String)row.getFieldValue(flowdestnodeidfield);
      String childNodes= makeRightNodeFrame(destNodeId);
      String node= makeNode(destNodeId);
      StringBuffer flowNodeBuf= new StringBuffer();
      flowNodeBuf.append(makeFlow(row));
      if (forwardAndBackFlowMap.containsKey(flowId)){
        TableData backRow= (TableData)forwardAndBackFlowMap.get(flowId);
        flowNodeBuf.append(makeFlow(backRow));
      }
      buf.append(makeRightNodeFrame(childNodes, node, flowNodeBuf.toString()));
    }
    return buf.toString();
  }
  
  String makeLeftNodeFrame(String childNodes, String node, String flowNode){
    String frameId= getNodeFrameId();
    StringBuffer buf= new StringBuffer();
    buf.append(makeNodeFrameHead(frameId, "left"));
    buf.append("<tr>\n");
    buf.append("<td id=\"");
    buf.append(frameId);
    buf.append("_ChildNodesCell\" style=\"text-align:right;\" >");
    buf.append(childNodes);
    buf.append("</td>\n");
    buf.append("<td id=\"");
    buf.append(frameId);
    buf.append("_NodeCell\" style=\"vertical-align:");
    buf.append(nodealign);
    buf.append(";\">");
    buf.append(node);
    buf.append("</td>\n");
    buf.append("<td id=\"");
    buf.append(frameId);
    buf.append("_ArrowCell_M\">&nbsp;</td>\n");
    buf.append("<td id=\"");
    buf.append(frameId);
    buf.append("_FlowCell\" style=\"vertical-align:");
    buf.append(nodealign);
    buf.append(";\">");
    buf.append(flowNode);
    buf.append("</td>\n");
    buf.append("<td id=\"");
    buf.append(frameId);
    buf.append("_ArrowCell_O\">&nbsp;</td>\n");
    buf.append("</tr>\n");
    buf.append(makeNodeFrameFoot());
    return buf.toString();
  }
  
  String makeRightNodeFrame(String childNodes, String node, String flowNode){
    String frameId= getNodeFrameId();
    StringBuffer buf= new StringBuffer();
    buf.append(makeNodeFrameHead(frameId, "right"));
    buf.append("<tr>\n");
    buf.append("<td id=\"");
    buf.append(frameId);
    buf.append("_ArrowCell_O\">&nbsp;</td>\n");
    buf.append("<td id=\"");
    buf.append(frameId);
    buf.append("_FlowCell\" style=\"vertical-align:");
    buf.append(nodealign);
    buf.append(";\">");
    buf.append(flowNode);
    buf.append("</td>\n");
    buf.append("<td id=\"");
    buf.append(frameId);
    buf.append("_ArrowCell_M\">&nbsp;</td>\n");
    buf.append("<td id=\"");
    buf.append(frameId);
    buf.append("_NodeCell\" style=\"vertical-align:");
    buf.append(nodealign);
    buf.append(";\">");
    buf.append(node);
    buf.append("</td>\n");
    buf.append("<td id=\"");
    buf.append(frameId);
    buf.append("_ChildNodesCell\" style=\"text-align:left;\" >");
    buf.append(childNodes);
    buf.append("</td>\n");
    buf.append("</tr>\n");
    buf.append(makeNodeFrameFoot());
    return buf.toString();
  }

  String makeNodeFrameHead(String frameId, String pos){
    flowNodeFrameIdList.add(frameId);
    
    StringBuffer buf= new StringBuffer();
    buf.append("<table id=\"");
    buf.append(frameId);
    buf.append("\" U_FramePos=\"");
    buf.append(pos);
    buf.append("\" style=\"font-size:9pt;width:10px;\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n");
    buf.append("<tr style=\"font-size:1px;height:1px;display:;\">\n");
    if ("left".equals(pos)){
      buf.append("<td width=\"0px\"></td>\n");
      buf.append("<td width=\"99%\">&nbsp;</td>\n");
      buf.append("<td>"+ makeFixedWidthTable(arrowwidth)+ "</td>\n");
      buf.append("<td>"+ makeFixedWidthTable(1)+ "</td>\n");
      buf.append("<td>"+ makeFixedWidthTable(arrowwidth)+ "</td>\n");
    }else{
      buf.append("<td>"+ makeFixedWidthTable(arrowwidth)+ "</td>\n");
      buf.append("<td>"+ makeFixedWidthTable(1)+ "</td>\n");
      buf.append("<td>"+ makeFixedWidthTable(arrowwidth)+ "</td>\n");
      buf.append("<td width=\"99%\">&nbsp;</td>\n");
      buf.append("<td width=\"0px\"></td>\n");
    }
    buf.append("</tr>\n");
    return buf.toString();
  }
  
  String makeNodeFrameFoot(){
    StringBuffer buf= new StringBuffer();
    buf.append("</table>\n");
    return buf.toString();
  }
  
  String makeFixedWidthTable(int width){
    return "<div style=\"width:"+ width+ "px;height:1px;\"></div>";
  }
  
  String makeOuterPanel_1() {
    StringBuffer buf= new StringBuffer();
    buf.append("<div id=\"");
    buf.append(getId());
    buf.append("\" ");
    buf.append("class=\"clsTrackViewOuterPanel5 ");
    buf.append(getCssclass());
    buf.append("\" ");
    buf.append("style=\"");
    buf.append(getStyle());
    buf.append(";display:");
    buf.append(isvisible?"":"none");
    buf.append(";overflow:hidden;");
    buf.append("\" ");
    buf.append("hidefocus=\"true\" ");
    buf.append(makeAttr());
    buf.append(">\n");
    return buf.toString();
  }

  String makeOuterPanel_2() {
    return "</div>\n";
  }
  
  String makeInnerPanel_1(String innerId, int zIndex, int padding) {
    StringBuffer buf= new StringBuffer();
    buf.append("<div id=\"");
    buf.append(innerId);
    buf.append("\" ");
    buf.append("style=\"position:absolute;width:100%;height:100%;left:0;top:0;overflow:visible;padding:");
    buf.append(padding);
    buf.append("px;z-index:");
    buf.append(zIndex);
    buf.append(";\" ");
    buf.append("hidefocus=\"true\" ");
    buf.append(">\n");
    return buf.toString();
  }

  String makeInnerPanel_2() {
    return "</div>\n";
  }
  
  String makeArrows(){
    Map backFlowMap= new HashMap();
    for (Iterator iter= forwardAndBackFlowMap.keySet().iterator(); iter.hasNext();){
      TableData row= (TableData)forwardAndBackFlowMap.get(iter.next());
      backFlowMap.put(row.getFieldValue(flowidfield), null);
    }
    StringBuffer buf= new StringBuffer();
    for (Iterator iter= flowIdList.iterator(); iter.hasNext();){
      String flowId= (String)iter.next();
      buf.append(makeArrow(getFlowId(flowId), backFlowMap.containsKey(flowId)?backlinecolor:forwardlinecolor));
    }
    return buf.toString();
  }

  String makeArrow(String flowId, String color){
    StringBuffer buf= new StringBuffer();
    buf.append("<v:line id=\"");
    buf.append(flowId);
    buf.append("_FlowLine1\" strokecolor=\"");
    buf.append(color);
    buf.append("\" />\n");
    buf.append("<v:line id=\"");
    buf.append(flowId);
    buf.append("_FlowLine2\" strokecolor=\"");
    buf.append(color);
    buf.append("\" />\n");
    buf.append("<v:line id=\"");
    buf.append(flowId);
    buf.append("_FlowArrow\" strokecolor=\"");
    buf.append(color);
    buf.append("\" >\n");
    buf.append("<v:stroke startarrow=\"\" endarrow=\"block\" />\n");
    buf.append("</v:line>\n");
    return buf.toString();
  }

  String makeAttr() {
    StringBuffer buf = new StringBuffer();
    buf.append("dataclass=\"" + dataclass + "\" ");
    buf.append("rootid=\"" + rootid + "\" ");
    buf.append("nodewidth=" + nodewidth + " ");
    buf.append("flowwidth=" + flowwidth + " ");
    buf.append("forwardlinecolor=\"" + forwardlinecolor + "\" ");
    buf.append("backlinecolor=\"" + backlinecolor + "\" ");
    buf.append("arrowwidth=" + arrowwidth + " ");
    buf.append("isvisible=\"" + isvisible + "\" ");
    buf.append("nodealign=\"" + nodealign + "\" ");
    buf.append("nodetextalign=\"" + nodetextalign + "\" ");
    buf.append("flowtextalign=\"" + flowtextalign + "\" ");
    buf.append("flowidfield=\"" + flowidfield + "\" ");
    buf.append("flowcapfield=\"" + flowcapfield + "\" ");
    buf.append("flowwayfield=\"" + flowwayfield + "\" ");
    buf.append("flowtipfield=\"" + flowtipfield + "\" ");
    buf.append("flowsournodeidfield=\"" + flowsournodeidfield + "\" ");
    buf.append("flowdestnodeidfield=\"" + flowdestnodeidfield + "\" ");
    buf.append("nodeidfield=\"" + nodeidfield + "\" ");
    buf.append("nodecapfield=\"" + nodecapfield + "\" ");
    buf.append("nodetipfield=\"" + nodetipfield + "\" ");
    return buf.toString();
  }

  public void setChildBodyText(String tagName, String text) {
    if (tagName.equals(DataTag.TAG_NAME) == false) return;
    Delta delta= new Delta();
    delta.parseXml4(text);
    if (TABLE_TRACK_FLOW.equals(delta.getName())){
      flowDelta= delta;
    }if (TABLE_TRACK_NODE.equals(delta.getName())){
      nodeMap= genNodeMapFromDelta(delta);
    }
  }
  
  Map genNodeMapFromDelta(Delta delta){
    Map voMap= new HashMap();
    for (Iterator iter= delta.iterator(); iter.hasNext();){
      TableData row= (TableData)iter.next();
      if (row== null) continue;
      voMap.put(row.getFieldValue(nodeidfield), row);
    }
    return voMap;
  }

  /**
 * @return   Returns the cssclass.
 * @uml.property   name="cssclass"
 */
public String getCssclass() {
	return cssclass;
}

  /**
 * @param cssclass   The cssclass to set.
 * @uml.property   name="cssclass"
 */
public void setCssclass(String cssclass) {
	this.cssclass = cssclass;
}

  /**
 * @return   Returns the dataclass.
 * @uml.property   name="dataclass"
 */
public String getDataclass() {
	return dataclass;
}

  /**
 * @param dataclass   The dataclass to set.
 * @uml.property   name="dataclass"
 */
public void setDataclass(String dataclass) {
	this.dataclass = dataclass;
}

  /**
 * @return   Returns the isvisible.
 * @uml.property   name="isvisible"
 */
public boolean isIsvisible() {
	return isvisible;
}

  /**
 * @param isvisible   The isvisible to set.
 * @uml.property   name="isvisible"
 */
public void setIsvisible(boolean isvisible) {
	this.isvisible = isvisible;
}

  /**
 * @return   Returns the style.
 * @uml.property   name="style"
 */
public String getStyle() {
	return style;
}

  /**
 * @param style   The style to set.
 * @uml.property   name="style"
 */
public void setStyle(String style) {
	this.style = style;
}

  /**
 * @return   Returns the flowcapfield.
 * @uml.property   name="flowcapfield"
 */
public String getFlowcapfield() {
	return flowcapfield;
}

  /**
 * @param flowcapfield   The flowcapfield to set.
 * @uml.property   name="flowcapfield"
 */
public void setFlowcapfield(String flowcapfield) {
	this.flowcapfield = flowcapfield;
}

  /**
 * @return   Returns the flowidfield.
 * @uml.property   name="flowidfield"
 */
public String getFlowidfield() {
	return flowidfield;
}

  /**
 * @param flowidfield   The flowidfield to set.
 * @uml.property   name="flowidfield"
 */
public void setFlowidfield(String flowidfield) {
	this.flowidfield = flowidfield;
}

  /**
 * @return   Returns the flowtipfield.
 * @uml.property   name="flowtipfield"
 */
public String getFlowtipfield() {
	return flowtipfield;
}

  /**
 * @param flowtipfield   The flowtipfield to set.
 * @uml.property   name="flowtipfield"
 */
public void setFlowtipfield(String flowtipfield) {
	this.flowtipfield = flowtipfield;
}

  /**
 * @return   Returns the nodecapfield.
 * @uml.property   name="nodecapfield"
 */
public String getNodecapfield() {
	return nodecapfield;
}

  /**
 * @param nodecapfield   The nodecapfield to set.
 * @uml.property   name="nodecapfield"
 */
public void setNodecapfield(String nodecapfield) {
	this.nodecapfield = nodecapfield;
}

  /**
 * @return   Returns the nodeidfield.
 * @uml.property   name="nodeidfield"
 */
public String getNodeidfield() {
	return nodeidfield;
}

  /**
 * @param nodeidfield   The nodeidfield to set.
 * @uml.property   name="nodeidfield"
 */
public void setNodeidfield(String nodeidfield) {
	this.nodeidfield = nodeidfield;
}

  /**
 * @return   Returns the nodetipfield.
 * @uml.property   name="nodetipfield"
 */
public String getNodetipfield() {
	return nodetipfield;
}

  /**
 * @param nodetipfield   The nodetipfield to set.
 * @uml.property   name="nodetipfield"
 */
public void setNodetipfield(String nodetipfield) {
	this.nodetipfield = nodetipfield;
}

  /**
 * @return   Returns the rootid.
 * @uml.property   name="rootid"
 */
public String getRootid() {
	return rootid;
}

public String getCurrentid() {
	return currentid;
}
  /**
 * @param rootid   The rootid to set.
 * @uml.property   name="rootid"
 */
public void setRootid(String rootid) {
	this.rootid = rootid;
}

public void setCurrentid(String currentid) {
	this.currentid = currentid;
}
  /**
 * @return   Returns the flowdestnodeidfield.
 * @uml.property   name="flowdestnodeidfield"
 */
public String getFlowdestnodeidfield() {
	return flowdestnodeidfield;
}

  /**
 * @param flowdestnodeidfield   The flowdestnodeidfield to set.
 * @uml.property   name="flowdestnodeidfield"
 */
public void setFlowdestnodeidfield(String flowdestnodeidfield) {
	this.flowdestnodeidfield = flowdestnodeidfield;
}

  /**
 * @return   Returns the flowsournodeidfield.
 * @uml.property   name="flowsournodeidfield"
 */
public String getFlowsournodeidfield() {
	return flowsournodeidfield;
}

  /**
 * @param flowsournodeidfield   The flowsournodeidfield to set.
 * @uml.property   name="flowsournodeidfield"
 */
public void setFlowsournodeidfield(String flowsournodeidfield) {
	this.flowsournodeidfield = flowsournodeidfield;
}

  /**
 * @return   Returns the nodewidth.
 * @uml.property   name="nodewidth"
 */
public int getNodewidth() {
	return nodewidth;
}

  /**
 * @param nodewidth   The nodewidth to set.
 * @uml.property   name="nodewidth"
 */
public void setNodewidth(int nodewidth) {
	this.nodewidth = nodewidth;
}

  /**
 * @return   Returns the flowwayfield.
 * @uml.property   name="flowwayfield"
 */
public String getFlowwayfield() {
	return flowwayfield;
}

  /**
 * @param flowwayfield   The flowwayfield to set.
 * @uml.property   name="flowwayfield"
 */
public void setFlowwayfield(String flowwayfield) {
	this.flowwayfield = flowwayfield;
}

  /**
 * @return   Returns the arrowwidth.
 * @uml.property   name="arrowwidth"
 */
public int getArrowwidth() {
	return arrowwidth;
}

  /**
 * @param arrowwidth   The arrowwidth to set.
 * @uml.property   name="arrowwidth"
 */
public void setArrowwidth(int arrowwidth) {
	this.arrowwidth = arrowwidth;
}

  /**
 * @return   Returns the flowtextalign.
 * @uml.property   name="flowtextalign"
 */
public String getFlowtextalign() {
	return flowtextalign;
}

  /**
 * @param flowtextalign   The flowtextalign to set.
 * @uml.property   name="flowtextalign"
 */
public void setFlowtextalign(String flowtextalign) {
	this.flowtextalign = flowtextalign;
}

  /**
 * @return   Returns the nodetextalign.
 * @uml.property   name="nodetextalign"
 */
public String getNodetextalign() {
	return nodetextalign;
}

  /**
 * @param nodetextalign   The nodetextalign to set.
 * @uml.property   name="nodetextalign"
 */
public void setNodetextalign(String nodetextalign) {
	this.nodetextalign = nodetextalign;
}

  /**
 * @return   Returns the nodealign.
 * @uml.property   name="nodealign"
 */
public String getNodealign() {
	return nodealign;
}

  /**
 * @param nodealign   The nodealign to set.
 * @uml.property   name="nodealign"
 */
public void setNodealign(String nodealign) {
	this.nodealign = nodealign;
}

  /**
 * @return   Returns the flowbordercolor.
 * @uml.property   name="flowbordercolor"
 */
public String getFlowbordercolor() {
	return flowbordercolor;
}

  /**
 * @param flowbordercolor   The flowbordercolor to set.
 * @uml.property   name="flowbordercolor"
 */
public void setFlowbordercolor(String flowbordercolor) {
	this.flowbordercolor = flowbordercolor;
}

  /**
 * @return   Returns the flowborderwidth.
 * @uml.property   name="flowborderwidth"
 */
public int getFlowborderwidth() {
	return flowborderwidth;
}

  /**
 * @param flowborderwidth   The flowborderwidth to set.
 * @uml.property   name="flowborderwidth"
 */
public void setFlowborderwidth(int flowborderwidth) {
	this.flowborderwidth = flowborderwidth;
}

  /**
 * @return   Returns the nodebordercolor.
 * @uml.property   name="nodebordercolor"
 */
public String getNodebordercolor() {
	return nodebordercolor;
}

  /**
 * @param nodebordercolor   The nodebordercolor to set.
 * @uml.property   name="nodebordercolor"
 */
public void setNodebordercolor(String nodebordercolor) {
	this.nodebordercolor = nodebordercolor;
}

  /**
 * @return   Returns the nodeborderwidth.
 * @uml.property   name="nodeborderwidth"
 */
public int getNodeborderwidth() {
	return nodeborderwidth;
}

  /**
 * @param nodeborderwidth   The nodeborderwidth to set.
 * @uml.property   name="nodeborderwidth"
 */
public void setNodeborderwidth(int nodeborderwidth) {
	this.nodeborderwidth = nodeborderwidth;
}

  /**
 * @return   Returns the rootbordercolor.
 * @uml.property   name="rootbordercolor"
 */
public String getRootbordercolor() {
	return rootbordercolor;
}

  /**
 * @param rootbordercolor   The rootbordercolor to set.
 * @uml.property   name="rootbordercolor"
 */
public void setRootbordercolor(String rootbordercolor) {
	this.rootbordercolor = rootbordercolor;
}

  /**
 * @return   Returns the rootborderwidth.
 * @uml.property   name="rootborderwidth"
 */
public int getRootborderwidth() {
	return rootborderwidth;
}

  /**
 * @param rootborderwidth   The rootborderwidth to set.
 * @uml.property   name="rootborderwidth"
 */
public void setRootborderwidth(int rootborderwidth) {
	this.rootborderwidth = rootborderwidth;
}

  /**
 * @return   Returns the flowwidth.
 * @uml.property   name="flowwidth"
 */
public int getFlowwidth() {
	return flowwidth;
}

  /**
 * @param flowwidth   The flowwidth to set.
 * @uml.property   name="flowwidth"
 */
public void setFlowwidth(int flowwidth) {
	this.flowwidth = flowwidth;
}

  /**
 * @return   Returns the backlinecolor.
 * @uml.property   name="backlinecolor"
 */
public String getBacklinecolor() {
	return backlinecolor;
}

  /**
 * @param backlinecolor   The backlinecolor to set.
 * @uml.property   name="backlinecolor"
 */
public void setBacklinecolor(String backlinecolor) {
	this.backlinecolor = backlinecolor;
}

  /**
 * @return   Returns the forwardlinecolor.
 * @uml.property   name="forwardlinecolor"
 */
public String getForwardlinecolor() {
	return forwardlinecolor;
}

  /**
 * @param forwardlinecolor   The forwardlinecolor to set.
 * @uml.property   name="forwardlinecolor"
 */
public void setForwardlinecolor(String forwardlinecolor) {
	this.forwardlinecolor = forwardlinecolor;
}

}
