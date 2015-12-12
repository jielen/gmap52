package com.anyi.gp.workflow;

import com.anyi.gp.util.StringTools;
import com.anyi.gp.workflow.util.WFCompoType;

/**
 * ƽ̨����ģ����ù�����ģ���ͳһ�ӿ�
 * @author zhangcheng
 *
 */
public class WFFacade {
    /**
     * �Ƿ�ҵ�񲿼�֧�ֹ�����
     */
    public static boolean isCompoSupportWF(String wf_flow_type, String compoName) {
    	//����ǹ������Ĳ������� ���� ֧�ֹ������Ĳ���
    	if(isWorkflowCompo(wf_flow_type, compoName)){
    		return false;
    	}else if (StringTools.isEmptyString(wf_flow_type)){//�����wf_flow_typeΪ��
    		return false;
    	}else {//ֻҪwf_flow_type��Ϊ�ն��Ҳ��ǹ������Ĳ���������֧�ֹ������Ĳ���������true
          return true;
    	}
    }

    /**
     * �Ƿ��ǹ���������
     *
     * @param wf_flow_type
     *          �����������Ƿ��ǹ�������صĲ����ֶ�ֵ
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
      	//��д���������.zhanggh
        if (WFCompoType.AS_WF_INSTANCE_TRACE.equalsIgnoreCase(compoName)
        		||WFCompoType.WF_DONE.equalsIgnoreCase(compoName)
    			||WFCompoType.WF_TODO.equalsIgnoreCase(compoName)
    			||WFCompoType.WF_WATCH.equalsIgnoreCase(compoName)
    			//||WFCompoType.WF_FILTER_COMPO.equalsIgnoreCase(compoName)//
    			//wf_compo�б����Ͳ���Ҫ���������б�ֻ��Ҫҵ�����ݱ�������ݣ���ҵ���Լ�����Ҫ�鿴
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
