package com.anyi.gp.print.util;

import com.anyi.gp.workflow.WFWorkList;
import com.anyi.gp.workflow.util.WFCompoType;
import com.anyi.gp.workflow.util.WFUtil;

public class PrintWFUtil {

  /**
   * 
   * @param condition
   * @param compoName
   * @param userId
   * @return
   */
  public static String filterWFCompoCondition(String condition, String compoName, String userId) {
    String listType = null;
    if (null != condition && isWFCompo(compoName)) {
      String[] types = new String[2];
      String[] wfTypes = new String[7];
      wfTypes[0] = WFCompoType.WF_FILTER_COMPO_TODO;
      wfTypes[1] = WFCompoType.WF_FILTER_COMPO_DONE;
      wfTypes[2] = WFCompoType.WF_FILTER_COMPO_INVALID;
      wfTypes[3] = WFCompoType.WF_FILTER_COMPO;
      wfTypes[4] = WFCompoType.WF_COMPO_DRAFT;
      wfTypes[5] = WFCompoType.WF_COMPO_OTHER;
      wfTypes[6] = WFCompoType.WF_COMPO;
      for (int i = 0; i < wfTypes.length; i++) {
        types = getListType(condition, wfTypes[i]);
        if (null != types[0]) {
          listType = types[0];
          condition = types[1];
          break;
        }
      }
      condition = WFWorkList.getAllTypeWfFiltedConditionSQL(condition, userId, listType,compoName);
    }
    return condition;
  }

  /**
   * 
   * @param condition
   * @param type
   * @return
   */
  private static String[] getListType(String condition, String type) {
    String[] results = new String[2];
    String condition2 = condition.replaceAll(WFCompoType.WF_COMPO_LIST_TYPE + "=" + type, "");
    if (condition2.length() != condition.length()) {
      results[0] = type;
    }
    results[1] = condition2;
    return results;
  }

  /**
   * 
   * @param entityName
   *          String
   * @return boolean
   */
  public static boolean isWFCompo(String entityName) {
    boolean isWFCompo = false;
    isWFCompo = WFUtil.isCompoSupportWF(entityName);
    /*
     * TableMeta meta = null; if (entityName != null) { meta =
     * MetaPool.getTableMeta(entityName); isWFCompo = meta.isWorkflowCompo(); } /*
     * if(meta != null && (WFWorkList.isCompoSupportWF(meta.getWF_FLOW_TYPE(),
     * entityName) || WFWorkList.isWorkflowCompo(entityName))){ isWFCompo =
     * true; }
     */
    return isWFCompo;
  }
}
