package com.anyi.gp.workflow;

import com.anyi.gp.util.StringTools;
import com.anyi.gp.workflow.util.WFCompoType;

/**
 * 平台其他模块调用工作流模块的统一接口
 * @author zhangcheng
 *
 */
public class WFFacade {
    /**
     * 是否业务部件支持工作流
     */
    public static boolean isCompoSupportWF(String wf_flow_type, String compoName) {
    	//如果是工作流的部件，就 不是 支持工作流的部件
    	if(isWorkflowCompo(wf_flow_type, compoName)){
    		return false;
    	}else if (StringTools.isEmptyString(wf_flow_type)){//如果是wf_flow_type为空
    		return false;
    	}else {//只要wf_flow_type不为空而且不是工作流的部件，就是支持工作流的部件，返回true
          return true;
    	}
    }

    /**
     * 是否是工作流部件
     *
     * @param wf_flow_type
     *          部件描述的是否是工作流相关的部件字段值
     */
    public static boolean isWorkflowCompo(String wf_flow_type, String compoName) {
      if (WFCompoType.AS_WF_INSTANCE_TRACE.equalsIgnoreCase(compoName)
      		||WFCompoType.WF_DONE.equalsIgnoreCase(compoName)
  			||WFCompoType.WF_TODO.equalsIgnoreCase(compoName)
  			||WFCompoType.WF_WATCH.equalsIgnoreCase(compoName)
  			||WFCompoType.WF_TEMPLATE.equalsIgnoreCase(compoName)){
      	return true;
      }
      return false;
    }
    public static boolean isAuthorizedToAll(String action,String compoName) {
          if (action == null || "".equals(action))
            return false;
          if (compoName == null || "".equals(compoName))
            return false;
          if (action.equalsIgnoreCase("fwatch")
              ||(WFCompoType.WF_COMPO.equalsIgnoreCase(compoName))
              ||(WFCompoType.WF_COMPO_OTHER.equalsIgnoreCase(compoName))
              ||(WFCompoType.WF_ACTION.equalsIgnoreCase(compoName))
              ||(WFCompoType.WF_TEMPLATE.equalsIgnoreCase(compoName))
              ||(WFCompoType.AS_WF_INSTANCE_TRACE.equalsIgnoreCase(compoName))){
              return true;
          }
          return false;
        }
    
    public static boolean needWorkflowList(String compoName, String wfListType) {
      	//改写，便于理解.zhanggh
        if (WFCompoType.AS_WF_INSTANCE_TRACE.equalsIgnoreCase(compoName)
        		||WFCompoType.WF_DONE.equalsIgnoreCase(compoName)
    			||WFCompoType.WF_TODO.equalsIgnoreCase(compoName)
    			||WFCompoType.WF_WATCH.equalsIgnoreCase(compoName)
    			//||WFCompoType.WF_FILTER_COMPO.equalsIgnoreCase(compoName)//
    			//wf_compo列表类型不需要工作流的列表，只需要业务数据表里的数据，按业务自己的需要查看
    			//||WFCompoType.WF_COMPO.equalsIgnoreCase(compoName)
                ||WFCompoType.WF_TEMPLATE.equalsIgnoreCase(compoName)
                ||WFCompoType.WF_TODO.equalsIgnoreCase(wfListType)
    			//||WFCompoType.WF_FILTER_COMPO.equalsIgnoreCase(wfListType)//
    			//||WFCompoType.WF_COMPO.equalsIgnoreCase(wfListType)
    			||WFCompoType.WF_DONE.equalsIgnoreCase(wfListType)){
          return true;
        }
        return false;
      }
    public static boolean isNeedHanleWithWF(String wfListType) {
      	return wfListType!=null&&wfListType.startsWith(WFCompoType.WF_FILTER_COMPO);
      }

}
