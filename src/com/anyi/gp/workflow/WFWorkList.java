/*$Id: WFWorkList.java,v 1.10 2008/06/03 12:48:49 huangcb Exp $*/
package com.anyi.gp.workflow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import com.anyi.gp.BusinessException;
import com.anyi.gp.Delta;
import com.anyi.gp.TableData;
import com.anyi.gp.context.ApplusContext;
import com.anyi.gp.core.dao.BaseDao;
import com.anyi.gp.meta.CompoMeta;
import com.anyi.gp.meta.MetaManager;
import com.anyi.gp.pub.SessionUtils;
import com.anyi.gp.taglib.components.Page;
import com.anyi.gp.util.StringTools;
import com.anyi.gp.workflow.util.FilterWfDataByCondi;
import com.anyi.gp.workflow.util.WFCompoType;
import com.anyi.gp.workflow.util.WFConst;
import com.anyi.gp.workflow.util.WFException;
import com.anyi.gp.workflow.util.WFUtil;
import com.kingdrive.workflow.ConfigureFacade;
import com.kingdrive.workflow.ExecuteFacade;
import com.kingdrive.workflow.business.CurrentTask;
import com.kingdrive.workflow.dto.ActionMeta;
import com.kingdrive.workflow.dto.CurrentTaskMeta;
import com.kingdrive.workflow.dto.InstanceMeta;
import com.kingdrive.workflow.dto.NodeMeta;
import com.kingdrive.workflow.exception.WorkflowException;
import com.kingdrive.workflow.util.DateTime;

public class WFWorkList {

  final org.apache.log4j.Logger logger = org.apache.log4j.Logger
    .getLogger(WFWorkList.class);

  final static String PRV_WF_ACTIVITY_ID = "WF_ACTIVITY_ID";

  static WFService service = WFFactory.getInstance().getService();

  /**
   * ͨ���������ӿ�ȡ���������б��������������б��Ѱ������б�������ʵ���б� ���������б�
   *
   * @param compoName
   *          ��������
   * @param condition
   *          ����
   * @param pageSize
   *          ÿҳ��¼����
   * @param needPage
   *          ��ǰ��Ҫ�ڼ�ҳ����
   * @return Delta
   * @throws WFException
   */
  public Delta getWorklistDelta(String compoName, String condition, int pageSize,
    int needPage, Map paraMap) throws WFException {

    Delta result = new Delta();
    int recLastPage = pageSize;// /last page records
    int pages = 0;// /total pages

    String userId = (String) paraMap.get("userId");
    Delta taskDelta = getAllTypeWorklistDelta(compoName, condition, userId, paraMap); // /???
    /* ����originCompo�������ù������б� */
    String originCompoName = (String) paraMap.get("originCompoName");
    filterWorklistDeltaByCompo(compoName, originCompoName, taskDelta);
    filterWorklistDeltaByCondi(taskDelta, condition);
    int recNo = taskDelta.size();
    recLastPage = recNo % pageSize;
    if (recLastPage == 0) {
      recLastPage = pageSize;
    }

    TableData tmp1 = new TableData("PARAS");
    tmp1.setField("CONDITION", condition); // /????
    pages = recNo / pageSize;
    if (recLastPage != pageSize) {
      pages++;
    }
    if (pages == 0) {
      pages = 1;
    }
    if (needPage > pages) {
      needPage = pages;
    }
    tmp1.setField("PAGES", String.valueOf(pages));
    tmp1.setField("PAGESIZE", String.valueOf(pageSize));
    tmp1.setField("TOTALCOUNTS", String.valueOf(recNo));
    String pageNo = ",";
    if (pages > 0) {
      pageNo = pageNo + "1,";
    }
    if (pages > 1) {
      pageNo = pageNo + "2,";
    }
    if (pages > 2) {
      pageNo = pageNo + "3,";
    }
    if (pages > 5) {
      pageNo = pageNo + String.valueOf(pages - 2) + ",";
    }
    if (pages > 4) {
      pageNo = pageNo + String.valueOf(pages - 1) + ",";
    }
    if (pages > 3) {
      pageNo = pageNo + String.valueOf(pages) + ",";
    }
    if (needPage != 0) {
      String ts = "," + String.valueOf(needPage - 1) + ",";
      if (needPage > 1 && pageNo.indexOf(ts) == -1) {
        pageNo = pageNo + String.valueOf(needPage - 1) + ",";
      }
      ts = "," + String.valueOf(needPage) + ",";
      if (pageNo.indexOf(ts) == -1) {
        pageNo = pageNo + String.valueOf(needPage) + ",";
      }
      ts = "," + String.valueOf(needPage + 1) + ",";
      if (needPage < pages && pageNo.indexOf(ts) == -1) {
        pageNo = pageNo + String.valueOf(needPage + 1) + ",";
      }
    }
    tmp1.setField("PAGENO", pageNo);
    result.add(tmp1);

    getDeltaFromWorklist(result, taskDelta, needPage, pageSize, recLastPage);

    return result;
  }

  /**
   * ����ԭʼ�������˹����б�
   *
   * @param compoName
   * @param originCompoName
   * @param taskDelta
   */
  private void filterWorklistDeltaByCompo(String compoName, String originCompoName,
    Delta taskDelta) {
    if (compoName == null || originCompoName == null || originCompoName == ""
      || compoName.equalsIgnoreCase(originCompoName))
      return;
    if (!(compoName.equalsIgnoreCase(WFCompoType.WF_TODO)
      || compoName.equalsIgnoreCase(WFCompoType.WF_COMPO)
      || compoName.equalsIgnoreCase(WFCompoType.WF_COMPO_OTHER) || compoName
      .equalsIgnoreCase(WFCompoType.WF_DONE)))// �����ַ�ʽ���ù���
      return;
    Iterator iter = taskDelta.iterator();
    while (iter.hasNext()) {
      TableData tableData = (TableData) iter.next();
      String realCompoName = tableData.getFieldValue("COMPO_ID");
      if (realCompoName == null || realCompoName == "")
        continue;// �п����Ƿ�ҳ��Ϣ.
      /* ������ͨ������֧�ֲ����б����� */
      if (!realCompoName.equalsIgnoreCase(originCompoName)) {// ȥ���������
        iter.remove();
      }
    }
  }

  /**
   * �����������˹���������
   *
   * @param taskDelta
   *          ������ȡ�õĹ���������
   * @param condition
   *          ���˵�����
   */
  private void filterWorklistDeltaByCondi(Delta taskDelta, String condition) {
    FilterWfDataByCondi filterWfData = new FilterWfDataByCondi(taskDelta, condition);
    filterWfData.filteredWfData();
  }

  /**
   * ȡ��ĳ�û������д������񣬰���compoid
   *
   * @param userId
   * @return
   * @throws WFException
   */
  public Delta getTodoWorklistDelta(String userId) throws WFException {
    Map paraMap = new HashMap();
    return getAllTypeWorklistDelta(WFCompoType.WF_TODO, null, userId, paraMap);
  }

  public Delta getDoneWorklistDelta(String userId) throws WFException {
    Map paraMap = new HashMap();
    return getAllTypeWorklistDelta(WFCompoType.WF_DONE, null, userId, paraMap);
  }

  /**
   * ȡ�������ˡ��Ѱ����ˡ�����֧�ֹ�������Щ���͵Ĺ����б�
   *
   * @param compoName
   *          ��������
   * @param condition
   *          ����
   * @param userID
   *          �û�
   * @return List
   * @throws WFException
   */
  private Delta getAllTypeWorklistDelta(String compoName, String condition,
    String userId, Map paraMap) throws WFException {
    String startTime = (String) paraMap.get("startTime");
    String endTime = (String) paraMap.get("endTime");

    /* ƽ̨4.0��ʼ����ʹ��templateType */
    // int templateId =
    // Integer.parseInt(paraMap.get(WFConst.WF_TEMPLATE_ID).toString());
    if (null == compoName) {
      compoName = (String) paraMap.get("originCompoName");
    }
    if (WFCompoType.WF_TODO.equals(compoName)) {
      List taskList = this.getTodoListByUser(userId);
      filterDetailsTask(taskList, WFCompoType.WF_TODO);
      return this.getTodoDeltaByUser(compoName, taskList, true);
    }

    if (WFCompoType.WF_DONE.equals(compoName)) {
      List taskList = this.getDoneListByUser(userId, startTime, endTime);
      // filterDetailsTask(taskList,WFCompoType.WF_DONE);
      /* ��ͬ��ʵ�����Ѱ�����ֻ����һ�� */
      return fiterSameInstanceTask(this
        .getDoneDeltaByUser(compoName, taskList, true));// mod by xujh
    }

    if (WFCompoType.WF_COMPO.equals(compoName)
      || WFCompoType.WF_COMPO_OTHER.equals(compoName))
      return this.getCompoTodoAndDoneListByUser(compoName, userId, startTime,
        endTime);

    if (WFCompoType.WF_TEMPLATE.equals(compoName)) {
      return this.getTemplateDelta((String) paraMap.get("originCompoName"));
    }

    if (WFCompoType.WF_ACTION.equals(compoName)) {
      return this.getActionDelta(compoName, paraMap);
    }
    if (WFCompoType.WF_WATCH.equals(compoName)) {
      return this.getInstanceDeltaByTemplate(compoName, (String) paraMap
        .get(WFConst.WF_TEMPLATE_ID));
    }

    if (WFCompoType.AS_WF_INSTANCE_TRACE.equals(compoName)) {
      int processInstanceId = 0;
      String strProcessInstanceId = (String) paraMap
        .get(WFConst.WF_PROCESS_INSTANCE_ID);
      if (strProcessInstanceId != null)
        processInstanceId = Integer.parseInt(strProcessInstanceId);
      return this.getProcessInstanceDeltaById(compoName, processInstanceId);
    }
    // /������֧�ֹ�����
    return null;
  }

  // /ȡAction��Delta
  private Delta getActionDelta(String compoName, Map paraMap) throws WFException {
    Delta result = null;
    // String byType = (String)paraMap.get(WFConst.BY_TYPE);
    // if(byType == null){
    int templateId = 0, activityId = 0;
    String strTemplateId = (String) paraMap.get(WFConst.WF_TEMPLATE_ID);
    String strActivityId = (String) paraMap.get(WFConst.WF_ACTIVITY_ID);
    if (strTemplateId == null || strActivityId == null)
      throw new IllegalArgumentException("WFWorkList���getActionDelta�Ķ�̬����Ϊnull");
    templateId = Integer.parseInt(strTemplateId);
    activityId = Integer.parseInt(strActivityId);
    result = service.getActionDeltaByActivity(compoName, templateId, activityId);
    // }else{
    /*
     * String templateType = (String) paraMap.get(WFConst.WF_TEMPLATE_TYPE_ID);
     * String businessType = (String) paraMap.get(WFConst.WF_BUSINESS_TYPE);
     * result = service.getActionDeltaByActivity(compoName, templateType,
     * businessType); }
     */
    return result;
  }

  // /�ӹ��������б����ɴ������ˡ��Ѱ����˺�֧�ֹ������Ĳ�����Delta
  private void getDeltaFromWorklist(Delta result, Delta taskDelta, int needPage,
    int pageSize, int recLastPage) {
    int tmpRecs = 0;
    int recNo = taskDelta.size();
    Iterator iterator = taskDelta.iterator();

    while (iterator.hasNext()) {
      TableData entity = (TableData) iterator.next();
      tmpRecs++;
      // ȡ��һҳ����
      if (needPage == 0) {
        // ֻ��ȡǰ��ҳ�������ҳ����
        if (tmpRecs > pageSize * 3 && tmpRecs <= recNo - pageSize * 2 - recLastPage) {
          continue;
        }
      } else {// ����ȡ��һҳ
        // ֻ��ȡ����ҳ��ĩ��ҳ���Լ�Ŀ��ҳǰ����ҳ
        if (!(tmpRecs <= pageSize * 3
          || tmpRecs > recNo - pageSize * 2 - recLastPage || (tmpRecs > pageSize
          * (needPage - 2) && tmpRecs <= pageSize * (needPage + 1)))) {
          continue;
        }
      }
      result.add(entity); // ����һ��
    }
  }

  private List getTodoListByUser(String userId) throws WFException {
    List taskList = service.getTodoListByUser(userId);
    return taskList;
  }

  private List getTodoListByProcessInstanceId(int processInstaceId, int isValid)
    throws WFException {
    List taskList = service
      .getTodoListByProcessInstanceId(processInstaceId, isValid);
    return taskList;
  }

  // /�ӹ��������б����ɴ������˵�Delta
  private Delta getTodoDeltaByUser(String compoName, List taskList,
    boolean addWorklistType) throws WFException {
    Delta result = new Delta();
    Iterator iterator = taskList.iterator();

    while (iterator.hasNext()) {
      Object object = iterator.next();
      CurrentTaskMeta curTaskMeta = null;
      if (object instanceof CurrentTaskMeta)
        curTaskMeta = (CurrentTaskMeta) object;
      else
        continue;

      TableData todoEntity = setToDoEntity(compoName, addWorklistType, curTaskMeta);
      result.add(todoEntity); // ����һ��
    }
    Delta resultRever = new Delta();
    for (int i = result.size() - 1; i >= 0; i--) {
      resultRever.add(result.get(i));
    }
    return resultRever;
  }

  private Delta getInstanceDeltaByTemplate(String compoName, String templateId)
    throws WFException {
    Delta result = new Delta();
    List instanceList = null;
    instanceList = service.getInstaceListByTemplate(templateId);

    for (int i = instanceList.size() - 1; i >= 0; i--) {
      InstanceMeta instanceMeta = (InstanceMeta) instanceList.get(i);
      TableData instanceEntity = setInstanceEntity(compoName, instanceMeta);
      result.add(instanceEntity); // ����һ��
    }
    return result;
  }

  private TableData setToDoEntity(String compoName, boolean addWorklistType,
    CurrentTaskMeta curTaskMeta) {
    TableData todoEntity = new TableData(compoName);
    if (addWorklistType) {
      todoEntity.setField(WFConst.WF_WORKLIST_TYPE, "");
      todoEntity.setField(WFConst.WF_WORKLIST_TYPE_IMG_SRC,
        createStatusHTMLImage(curTaskMeta));
    }
    todoEntity.setField(WFConst.WF_TEMPLATE_NAME, curTaskMeta.getTemplateName());
    todoEntity.setField(WFConst.WF_INSTANCE_NAME, curTaskMeta.getInstanceName());
    todoEntity.setField(WFConst.WF_ACTIVITY_NAME, curTaskMeta.getNodeName());

    todoEntity.setField(WFConst.WF_ACTION_NAME, "");
    todoEntity.setField(WFConst.WF_ACTION_EXECUTOR_NAME, "");
    todoEntity.setField(WFConst.WF_ACTION_EXECUTE_TIME, "");
    todoEntity.setField(WFConst.WF_ACTION_DESCRIPTION, "");

    todoEntity.setField(WFConst.WF_INSTANCE_DESCRIPTION, curTaskMeta
      .getInstanceDescription());
    todoEntity.setField(WFConst.WF_INSTANCE_START_TIME, toTimeString(curTaskMeta
      .getInstanceStartTime()));

    todoEntity.setField(WFConst.WF_TASK_OWNER_NAME, curTaskMeta.getOwnerName());
    todoEntity.setField(WFConst.WF_TASK_CREATOR_NAME, curTaskMeta.getCreatorName());
    todoEntity.setField(WFConst.WF_TASK_CREATE_TIME, toTimeString(curTaskMeta
      .getCreateTime()));
    todoEntity.setField(WFConst.WF_LIMIT_TIME, toTimeString(curTaskMeta
      .getLimitExecuteTime()));

    todoEntity.setField(WFConst.WF_TEMPLATE_ID, String.valueOf(curTaskMeta
      .getTemplateId()));
    todoEntity.setField(WFConst.WF_INSTANCE_ID, String.valueOf(curTaskMeta
      .getInstanceId()));
    todoEntity.setField(WFConst.WF_TASK_ID, String.valueOf(curTaskMeta
      .getCurrentTaskId()));
    todoEntity.setField(PRV_WF_ACTIVITY_ID, String.valueOf(curTaskMeta.getNodeId()));

    todoEntity.setField("COMPO_ID", service.findCompoByTask(curTaskMeta
      .getTemplateId(), curTaskMeta.getNodeId()));

    return todoEntity;
  }

  /**
   * ���ݵ�ǰ����Meta��Ϣ��������������״̬ͼƬ��ǣ������ڿͻ�����ʾ��
   * ���� ��ɫ��ʾ�Ѱ� ��ɫ��ʾ���� ��ɫ��ʾ�쵽����ʱ������
   * ��ɫ��ʾ�ѹ��ڴ���
   *
   * @param curTaskMeta
   * @return ����<image ... />��html���
   * @throws BusinessException
   */
  private String createStatusHTMLImage(CurrentTaskMeta curTaskMeta) {
    if (curTaskMeta == null)
      return "";

    String src = "";
    String tip = "";
    int remind_time = -1;
    float hoursLeavings = 0;
    String limitTime = curTaskMeta.getLimitExecuteTime();
    if (limitTime != null && limitTime.length() > 0)
      hoursLeavings = DateTime.getLeavingHours(limitTime);
    try {
      /* ��ȡ�ڵ㶨������ʱ�� */
      NodeMeta node = ConfigureFacade.getNode(curTaskMeta.getNodeId());
      remind_time = node.getRemindExecuteTerm();
    } catch (WorkflowException e) {
      logger.error("���ݽڵ������ȡ�ڵ�meta��Ϣ����");
      e.printStackTrace();
      throw new RuntimeException(e);
    }
    if (limitTime != null && limitTime.length() > 0) {
      if (hoursLeavings < 0) {
        // ��������
        hoursLeavings *= -1;
        Float tmp = new Float(hoursLeavings);
        src += Page.LOCAL_RESOURCE_PATH + "style/img/workflow/red.gif";
        tip += "�������� �����Ѿ�����" + tmp.intValue() + "Сʱ"
          + (int) ((tmp.floatValue() - tmp.intValue()) * 60) + "����";
      } else if (remind_time > 0 && remind_time > hoursLeavings) {
        // ��������
        Float tmp = new Float(hoursLeavings);
        src += Page.LOCAL_RESOURCE_PATH + "style/img/workflow/yellow.gif";
        tip += "�������� ����������ʱ��ֻ��" + tmp.intValue() + "Сʱ"
          + (int) ((tmp.floatValue() - tmp.intValue()) * 60) + "����";

      } else {
        // ��������
        src += Page.LOCAL_RESOURCE_PATH + "style/img/workflow/blue.gif";
        tip += "��������";
      }
    } else {
      // ��������
      src += Page.LOCAL_RESOURCE_PATH + "style/img/workflow/blue.gif";
      tip += "��������";
    }

    return "img src='" + src + "' title='" + tip + "'";
  }

  /**
   * �����Ѱ������״̬ͼƬhtml���
   *
   * @param doneTaskMeta
   * @return
   */
  private String createStatusHTMLImage(ActionMeta doneTaskMeta) {
    return "img src='" + Page.LOCAL_RESOURCE_PATH + "style/img/workflow/green.gif' title='�Ѱ�����'";
  }

  private TableData setInstanceEntity(String compoName, InstanceMeta instaceMeta) {
    TableData instaceEntity = new TableData(compoName);
    instaceEntity.setField(WFConst.WF_INSTANCE_ID, String.valueOf(instaceMeta
      .getInstanceId()));
    instaceEntity.setField(WFConst.WF_INSTANCE_NAME, String.valueOf(instaceMeta
      .getName()));
    instaceEntity.setField(WFConst.WF_TEMPLATE_ID, String.valueOf(instaceMeta
      .getTemplateId()));
    instaceEntity.setField(WFConst.WF_TEMPLATE_NAME, String.valueOf(instaceMeta
      .getTemplateName()));
    instaceEntity.setField(WFConst.WF_INSTANCE_OWNER, String.valueOf(instaceMeta
      .getOwner()));
    instaceEntity.setField(WFConst.WF_INSTANCE_OWNER_NAME, String
      .valueOf(instaceMeta.getOwnerName()));
    instaceEntity.setField(WFConst.WF_INSTANCE_START_TIME, toTimeString(instaceMeta
      .getStartTime()));
    instaceEntity.setField(WFConst.WF_INSTANCE_END_TIME, toTimeString(instaceMeta
      .getEndTime()));
    instaceEntity.setField(WFConst.WF_INSTANCE_DESCRIPTION, String
      .valueOf(instaceMeta.getDescription()));
    switch (instaceMeta.getStatus()) {
    case 1:
      instaceEntity.setField(WFConst.WF_INSTANCE_STATUS, "�");
      break;
    case -1:
      instaceEntity.setField(WFConst.WF_INSTANCE_STATUS, "����");
      break;
    case 9:
      instaceEntity.setField(WFConst.WF_INSTANCE_STATUS, "��������");
      break;
    case -9:
      instaceEntity.setField(WFConst.WF_INSTANCE_STATUS, "��ֹ����");
      break;
    default:
      instaceEntity.setField(WFConst.WF_INSTANCE_STATUS, "");
    }
    return instaceEntity;
  }

  private List getDoneListByUser(String userId, String startTime, String endTime)
    throws WFException {
    List taskList = service.getDoneListByUser(userId, startTime, endTime);
    return taskList;
  }

  private List getDoneListByProcessInstanceId(int processInstanceId, int isValid)
    throws WFException {
    List taskList = service.getDoneListByProcessInstanceId(processInstanceId,
      isValid);
    return taskList;
  }

  public static List getDoneCompoListByUser(String userId) throws WFException {
    List taskList = service.getDoneCompoListByUser(userId);
    return taskList;
  }

  public static List getTodoCompoListByUser(String userId) throws WFException {
    List taskList = service.getTodoCompoListByUser(userId);
    List result = new ArrayList();
    for (int i = 0; i < taskList.size(); i++) {
      CompoMeta meta = MetaManager.getCompoMeta((String) taskList.get(i));
      if (meta.isCompoSupportWF()) {//ȷ�����Ƿ�ҽӹ�����
        result.add(taskList.get(i));
      }
    }
    return result;
  }

  /**
   * û���ҵ������ߣ���ʱע��
   * @param compoName
   * @param taskList
   * @param addWorklistType
   * @return
   * @throws WFException
   */
  public static List getDraftCompoListByUser(String userId) throws BusinessException {
  	String sql = "select draft.compo_id from as_wf_draft draft where draft.user_id=? group by draft.compo_id";
    List result = new ArrayList();
    BaseDao dao = (BaseDao)ApplusContext.getBean("baseDao");
    List rows = dao.queryForListBySql(sql, new String[]{userId});
    String compoName = "";
    for (int i = 0; i < rows.size(); i++) {
    	Map row = (Map)rows.get(i);
    	compoName = (String)row.get("COMPO_ID");
    	CompoMeta meta = MetaManager.getCompoMeta(compoName);
    	if (meta.isCompoSupportWF()) {
      	result.add(compoName);
    	}
    }
    return result;
  }

  // /�ӹ��������б������Ѱ����˵�Delta
  private Delta getDoneDeltaByUser(String compoName, List taskList,
    boolean addWorklistType) throws WFException {
    Delta result = new Delta();
    Iterator iterator = taskList.iterator();

    while (iterator.hasNext()) {
      Object object = iterator.next();
      ActionMeta doneTaskMeta = null;
      if (object instanceof ActionMeta)
        doneTaskMeta = (ActionMeta) object;
      else
        continue;
      TableData doneEntity = setDoneEntity(compoName, addWorklistType, doneTaskMeta);
      if (null != doneEntity) {// ����ֹ��
        result.add(doneEntity); // ����һ��
      }
    }
    Delta resultRever = new Delta();
    for (int i = result.size() - 1; i >= 0; i--) {
      resultRever.add(result.get(i));
    }
    return resultRever;
    // return result;
  }

  private TableData setDoneEntity(String compoName, boolean addWorklistType,
    ActionMeta doneTaskMeta) {
    TableData doneEntity = new TableData(compoName);
    if (addWorklistType) {
      // �������ֶ���Ϊ����ǰ����ʾ����,�Ѱ�����״̬��ͼƬ�õ�
      doneEntity.setField(WFConst.WF_WORKLIST_TYPE, "");
      doneEntity.setField(WFConst.WF_WORKLIST_TYPE_IMG_SRC,
        createStatusHTMLImage(doneTaskMeta));
    }
    doneEntity.setField(WFConst.WF_TEMPLATE_NAME, doneTaskMeta.getTemplateName());
    doneEntity.setField(WFConst.WF_INSTANCE_NAME, doneTaskMeta.getInstanceName());
    doneEntity.setField(WFConst.WF_ACTIVITY_NAME, doneTaskMeta.getNodeName());

    doneEntity.setField(WFConst.WF_ACTION_NAME, getActionTypeName(doneTaskMeta
      .getActionName()));
    doneEntity.setField(WFConst.WF_ACTION_EXECUTOR_NAME, doneTaskMeta
      .getExecutorName());
    doneEntity.setField(WFConst.WF_ACTION_EXECUTE_TIME, toTimeString(doneTaskMeta
      .getExecuteTime()));
    doneEntity
      .setField(WFConst.WF_ACTION_DESCRIPTION, doneTaskMeta.getDescription());

    doneEntity.setField(WFConst.WF_INSTANCE_DESCRIPTION, doneTaskMeta
      .getInstanceDescription());
    doneEntity.setField(WFConst.WF_INSTANCE_START_TIME, "");

    doneEntity.setField(WFConst.WF_TASK_OWNER_NAME, doneTaskMeta.getOwnerName());
    doneEntity.setField(WFConst.WF_TASK_CREATOR_NAME, "");
    doneEntity.setField(WFConst.WF_TASK_CREATE_TIME, "");
    doneEntity.setField(WFConst.WF_LIMIT_TIME, toTimeString(doneTaskMeta
      .getLimitExecuteTime()));

    doneEntity.setField(WFConst.WF_TEMPLATE_ID, String.valueOf(doneTaskMeta
      .getTemplateId()));
    doneEntity.setField(WFConst.WF_INSTANCE_ID, String.valueOf(doneTaskMeta
      .getInstanceId()));
    doneEntity.setField(WFConst.WF_TASK_ID, "");
    doneEntity
      .setField(PRV_WF_ACTIVITY_ID, String.valueOf(doneTaskMeta.getNodeId()));

    doneEntity.setField("COMPO_ID", service.findCompoByTask(doneTaskMeta
      .getTemplateId(), doneTaskMeta.getNodeId()));
    if (null == service.findCompoByTask(doneTaskMeta.getTemplateId(), doneTaskMeta
      .getNodeId())) {// ����ֹ��
      return null;
    }
    return doneEntity;
  }

  // /�ӹ��������б����ɴ������˵�Delta
  private Delta getTemplateDelta(String compoName) throws WFException {
    Delta result = new Delta();
    List templateList = service.getCompoEnableStartedTemplate(compoName);
    /////List templateList = MetaManager.getTableMetaByCompoName(compoName).getEnableStartTemplateList();
    Iterator iterator = templateList.iterator();

    while (iterator.hasNext()) {
      /*TemplateMeta templateMeta = (TemplateMeta) iterator.next();
      if (templateMeta == null)
        continue;
      TableData templateEntity = new TableData(compoName);
      templateEntity.setField(WFConst.WF_TEMPLATE_NAME, templateMeta.getName());
      templateEntity.setField(WFConst.WF_TEMPLATE_DESCRIPTION, templateMeta
        .getDescription());
      // templateEntity.setField(WFConst.WF_TEMPLATE_TYPE_ID,
      // templateMeta.getTemplateType());
      templateEntity.setField(WFConst.WF_TEMPLATE_ID, String.valueOf(templateMeta
        .getTemplateId()));*/
      //Ϊ����ٶ�,ֻȡ��id,��name��desc����ȡ
      Integer id = (Integer) iterator.next();
      if (id == null)
        continue;
      TableData templateEntity = new TableData(compoName);
      templateEntity.setField(WFConst.WF_TEMPLATE_NAME, id.toString());
      templateEntity.setField(WFConst.WF_TEMPLATE_DESCRIPTION,  id.toString());
      // templateEntity.setField(WFConst.WF_TEMPLATE_TYPE_ID,
      // templateMeta.getTemplateType());
      templateEntity.setField(WFConst.WF_TEMPLATE_ID,  id.toString());

      result.add(templateEntity);
    }
    return result;
  }

  // /ȡ֧�ֹ����������Ĵ������˺��Ѱ����� �ӹ��������б����ɴ�����Ѱ����˵�Delta
  private Delta getCompoTodoAndDoneListByUser(String compoName, String userId,
    String startTime, String endTime) throws WFException {
    List taskList = this.getTodoListByUser(userId);
    Delta compoTodoAndDoneDelta = this.getTodoDeltaByUser(compoName, taskList, true);
    taskList = this.getDoneListByUser(userId, startTime, endTime);
    compoTodoAndDoneDelta.addAll(fiterSameInstanceTask(this.getDoneDeltaByUser(
      compoName, taskList, true)));
    // return fiterTaskList(compoTodoAndDoneDelta);
    return compoTodoAndDoneDelta;
  }

  /**
   * ���˵������б�����ͬʵ��������
   *
   * @param compoTodoAndDoneDelta
   * @return
   */
  private Delta fiterSameInstanceTask(Delta compoTodoAndDoneDelta) {
    Vector tempInsVec = new Vector();
    Delta resultDelta = new Delta();
    Iterator iterator = compoTodoAndDoneDelta.iterator();

    while (iterator.hasNext()) {
      TableData t = (TableData) iterator.next();
      String tmpInstanceId = t.getFieldValue(WFConst.WF_INSTANCE_ID);
      if (!tempInsVec.contains(tmpInstanceId)) {
        tempInsVec.add(tmpInstanceId);
        resultDelta.add(t);
      }
    }
    return resultDelta;

  }

  /**
   * ���˵���������������ѻ���������
   *
   * @param list
   * @param Type
   * @return
   */
  private void filterDetailsTask(List list, String Type) {
    Iterator iter = list.iterator();
    CurrentTask taskHandler = new CurrentTask();
    // Instance instanceHandler=new Instance();
    try {
      if (Type.equals(WFCompoType.WF_TODO)) {
        while (iter.hasNext()) {
          CurrentTaskMeta element = (CurrentTaskMeta) iter.next();
          int nTaskType = taskHandler.getTaskType(element.getCurrentTaskId());
          boolean bHideInToDoList = (nTaskType != CurrentTask.TYPE_COLLECTED
            && nTaskType != CurrentTask.TYPE_NORMAL && nTaskType != CurrentTask.TYPE_TOCOLLECT);
          if (bHideInToDoList)
            iter.remove();
        }
      } else if (Type.equals(WFCompoType.WF_DONE)) {
        while (iter.hasNext()) {
          ActionMeta element = (ActionMeta) iter.next();
          // ���˵���������
          boolean bHideInToDoList = (element.getParentInstanceId() != 0);
          if (bHideInToDoList)
            iter.remove();
        }
      }
    } catch (WorkflowException e) {
      throw new RuntimeException(e);
    }

  }

  // /ȡ֧�ֹ����������Ĵ������˺��Ѱ����� �ӹ��������б����ɴ�����Ѱ����˵�Delta
  public Delta getProcessInstanceDeltaById(String compoName, int processInstaceId)
    throws WFException {
    List taskList = this.getTodoListByProcessInstanceId(processInstaceId, 0);
    Delta compoTodoAndDoneDelta = this.getTodoDeltaByUser(compoName, taskList, true);
    taskList = this.getDoneListByProcessInstanceId(processInstaceId, 0);
    compoTodoAndDoneDelta.addAll(this.getDoneDeltaByUser(compoName, taskList, true));
    return compoTodoAndDoneDelta;
  }

  // /��request��ȡ����̬����
  public Map getWFNeededParameterFormRequest(String compoName,
    HttpServletRequest request) throws BusinessException {
    Map paraMap = new HashMap();
    if (request == null)
      return paraMap;
    String startTime = request.getParameter("startTime");
    String endTime = request.getParameter("endTime");
    // String workflowType = this.getWorkflowType(compoName,request);
    String userId = (String)SessionUtils.getAttribute(request,"svUserID");
    String templateId = request.getParameter(WFConst.WF_TEMPLATE_ID);
    String activityId = request.getParameter(WFConst.WF_ACTIVITY_ID);
    String processInstanceId = request.getParameter(WFConst.WF_PROCESS_INSTANCE_ID);
    // String templateType = request.getParameter(WFConst.WF_TEMPLATE_TYPE_ID);
    // String businessType = request.getParameter(WFConst.WF_BUSINESS_TYPE);
    String byType = request.getParameter(WFConst.BY_TYPE);
    /* ���originCompoName�������ȼ��url�������ټ��attribute */
    Object o = request.getParameter("originCompoName");
    // if (o == null)
    // o = request.getAttribute("originCompoName");
    if (o == null)// request�в���ȡ��orginCompoName,�Ĵ�session��ȡ
      o = SessionUtils.getAttribute(request, "originCompoName");
    String originCompoName = null;
    if (o != null)
      originCompoName = o.toString();
    if (!(null == startTime || "".equals(startTime)))// !=""д���Ǵ����
      paraMap.put("startTime", startTime);
    if (!(null == endTime || "".equals(endTime)))
      paraMap.put("endTime", endTime);
    /*
     * if(workflowType != null && workflowType.equals(""))
     * paraMap.put("workflowType",workflowType);
     */
    if (!(null == userId || "".equals(userId)))
      paraMap.put("userId", userId);
    if (!(null == templateId || "".equals(templateId)))
      paraMap.put(WFConst.WF_TEMPLATE_ID, templateId);
    if (!(null == activityId || "".equals(activityId)))
      paraMap.put(WFConst.WF_ACTIVITY_ID, activityId);
    if (!(null == processInstanceId || "".equals(processInstanceId)))
      paraMap.put(WFConst.WF_PROCESS_INSTANCE_ID, processInstanceId);
    /*
     * if(templateType != null && templateType != "")
     * paraMap.put(WFConst.WF_TEMPLATE_TYPE_ID,charsTransform(templateType));
     * if(businessType != null && businessType != "")
     * paraMap.put(WFConst.WF_BUSINESS_TYPE,charsTransform(businessType));
     */
    // if (originCompoName != null && originCompoName != "" &&
    // !(compoName.equalsIgnoreCase(WFCompoType.WF_DONE)
    // ||compoName.equalsIgnoreCase(WFCompoType.WF_TODO)) ){
    if (!(null == originCompoName || "".equals(originCompoName))) {
      paraMap.put("originCompoName", originCompoName);
    }
    if (!(null == byType || "".equals(byType)))
      paraMap.put(WFConst.BY_TYPE, byType);

    return paraMap;
  }

  // /������yyyymmddhhmmss��ʽ��ʱ��ת��Ϊyyyy-mm-dd hh:mm:ss
  public String toTimeString(String inTime) {
    if (inTime == null || inTime.length() == 0) {
      return "";
    }
    StringBuffer outTime = new StringBuffer();
    try {
      outTime.append(inTime.substring(0, 4)).append("-");
      outTime.append(inTime.substring(4, 6)).append("-");
      outTime.append(inTime.substring(6, 8)).append(" ");
      outTime.append(inTime.substring(8, 10)).append(":");
      outTime.append(inTime.substring(10, 12)).append(":");
      outTime.append(inTime.substring(12));
    } catch (IndexOutOfBoundsException e) {
      return inTime;
    }
    return outTime.toString();
  }

  public String getActionTypeName(String actionName) {
    if (actionName == null) {
      return null;
    }

    String result = actionName;
    if (actionName.equalsIgnoreCase(ActionMeta.ACTION_TYPE_AUTHORIZE_TASK)) {
      result = "��Ȩ";
    } else if (actionName.equalsIgnoreCase(ActionMeta.ACTION_TYPE_FORWARD_TASK)) {
      result = "�ƽ�";
    } else if (actionName.equalsIgnoreCase(ActionMeta.ACTION_TYPE_CALLBACK_FLOW)) {
      result = "����";
    } else if (actionName.equalsIgnoreCase(ActionMeta.ACTION_TYPE_GIVEBACK_FLOW)) {
      result = "����";
    } else if (actionName.equalsIgnoreCase(ActionMeta.ACTION_TYPE_UNTREAD_FLOW)) {
      result = "���̻���";
    } else if (actionName.equalsIgnoreCase(ActionMeta.ACTION_TYPE_TRANSFER_FLOW)) {
      result = "��ת";
    } else if (actionName.equalsIgnoreCase(ActionMeta.ACTION_TYPE_ACTIVATE_INSTANCE)) {
      result = "ʵ������";
    } else if (actionName
      .equalsIgnoreCase(ActionMeta.ACTION_TYPE_DEACTIVATE_INSTANCE)) {
      result = "ʵ������";
    } else if (actionName
      .equalsIgnoreCase(ActionMeta.ACTION_TYPE_INTERRUPT_INSTANCE)) {
      result = "ʵ����ֹ";
    } else if (actionName.equalsIgnoreCase(ActionMeta.ACTION_TYPE_RESTART_INSTANCE)) {
      result = "ʵ������";
    }
    return result;
  }

//  public static String getWfFilterCondition(String condition, String user,
//    String listType, String compoId) {
//    try {
//      // ������Ǹ��ݴ����Ѱ���ˣ�����ݸò������õ���������
//      if (WFUtil.isNeedHandleWithWF(listType)) {
//        condition = getWfInstFilterCondition(condition, user, listType,
//          compoId);
//      }
//      // ����ǲݸ���ˣ�����Ӳݸ��������
//      if (WFUtil.isNeedHandleWithDraft(listType)) {
//        condition = getWfDraftFilterCondition(condition, user, compoId);
//      }
//    } catch (WFException e) {
//      throw new RuntimeException(e);
//    } catch (BusinessException e) {
//      throw new RuntimeException(e);
//    }
//    return condition;
//  }

  public static String getAllTypeWfFiltedConditionSQL(String condition, String user,
    String listType, String compoId) {
    // ������Ǹ��ݴ����Ѱ���ˣ�����ݸò������õ���������
    if (WFUtil.isNeedHandleWithWF(listType)) {
      condition = getWfInstFilterConditionSQL(condition, user, listType,
        compoId);
    }
    // ����ǲݸ���ˣ�����Ӳݸ��������
    if (WFCompoType.WF_COMPO_DRAFT.equalsIgnoreCase(listType)) {
      condition = getDraftInstFilterConditionSQL(condition, user, compoId);
    }
    return condition;
  }
  public static String getDraftInstFilterConditionSQL(String condition, String user,
    String compoId){
    String instanceSql = "";
    StringBuffer tmpCondition = new StringBuffer();
    instanceSql = getDraftSqlByExecutor(user, compoId);

    if (!StringTools.isEmptyString(instanceSql)) {
      if (!StringTools.isEmptyString(condition)) {
        tmpCondition.append(condition +" and ");
      }
      String mainTable = MetaManager.getTableMeta(compoId).getName();
      tmpCondition.append(mainTable + ".PROCESS_INST_ID IN ( ");
      tmpCondition.append(instanceSql);
      tmpCondition.append(" ) ");
    } else {
      if (!StringTools.isEmptyString(condition)) {
        tmpCondition.append(condition + " and 1=0");
      } else {
        tmpCondition.append(" 1=0");
      }
    }
    return tmpCondition.toString();
  }
//  public static String getWfDraftFilterCondition(String condition, String user,
//    String compoId) throws BusinessException {
//    StringBuffer tmpCondition = new StringBuffer();
//    DML dml = (DML)ApplusContext.getBean("dml");
//    condition = WFUtil.getListTypeFromString(condition)[1];
//    Delta draftDelta = dml.getBigEntities("WF_DRAFT", "user_id='" + user + "'",
//      1000, 0);
//    List instanceList = new ArrayList();
//    String tmpInstance;
//    for (Iterator iter = draftDelta.iterator(); iter.hasNext();) {
//      TableData data = (TableData) iter.next();
//      tmpInstance = Integer.toString(data.getFieldInt("WF_DRAFT_ID"));
//      if (!instanceList.contains(tmpInstance) && !"0".equals(tmpInstance)) {
//        instanceList.add(tmpInstance);
//      }
//    }
//    String instanceSqlStr;
//    if (instanceList.size() > 0) {
//      if (condition != null && condition.length() > 0) {
//        tmpCondition.append(condition + " and ");
//      }
//      instanceSqlStr = getSqlStrByInstanceList(instanceList, compoId);
//      tmpCondition.append(instanceSqlStr);
//    } else {
//      if (condition != null && condition.length() > 0) {
//        tmpCondition.append(condition + " and 1=0");
//      } else {
//        tmpCondition.append(" 1=0");
//      }
//    }
//    return tmpCondition.toString();
//  }

  /**
   * 4���б�ʽ��
   *     WF_FILTER_COMPO_DONE��WF_FILTER_COMPO_TODO
   *     WF_FILTER_COMPO_INVALID��WF_FILTER_COMPO
   *
   */
  public static String getWfInstFilterCondition(String condition, String user,
    String wfListType, String compoId) throws WFException {
    StringBuffer tmpCondition = new StringBuffer();
    WFService service = WFFactory.getInstance().getService();
    condition = WFUtil.getListTypeFromString(condition)[1];
    boolean needInvalidInstance = WFCompoType.WF_FILTER_COMPO_INVALID
      .equalsIgnoreCase(wfListType);
    boolean needDoneInstances = (WFCompoType.WF_FILTER_COMPO_DONE
      .equalsIgnoreCase(wfListType) || WFCompoType.WF_FILTER_COMPO
      .equalsIgnoreCase(wfListType));
    boolean needTodoInstances = (WFCompoType.WF_FILTER_COMPO_TODO
      .equalsIgnoreCase(wfListType) || WFCompoType.WF_FILTER_COMPO
      .equalsIgnoreCase(wfListType));
    List instanceList = new ArrayList();

    String tmpInstance;
    if (needInvalidInstance) {
      instanceList = service.getInvalidInstListByUser(user);
    } else {
      if (needTodoInstances) {
        List todoList = service.getTodoInstListByUser(user);
        for (int i = 0; i < todoList.size(); i++) {
          tmpInstance = (String) todoList.get(i);
          if (!instanceList.contains(tmpInstance)) {
            instanceList.add(tmpInstance);
          }
        }
      }
      if (needDoneInstances) {
        List doneList = service.getDoneInstListByUser(user, null, null);
        for (int i = 0; i < doneList.size(); i++) {
          tmpInstance = (String) doneList.get(i);
          if (!instanceList.contains(tmpInstance)) {
            instanceList.add(tmpInstance);
          }
        }
      }
    }
    if (instanceList.size() > 0) {
      if (condition != null && condition.length() > 0) {
        tmpCondition.append(condition + " and ");
      }
      String instanceSqlStr = getSqlStrByInstanceList(instanceList, compoId);
      tmpCondition.append(instanceSqlStr);
    } else {
      if (condition != null && condition.length() > 0) {
        tmpCondition.append(condition + " and 1=0");
      } else {
        tmpCondition.append(" 1=0");
      }
    }
    return tmpCondition.toString();
  }
  public static String getWfInstFilterConditionSQL(String condition, String user,
    String wfListType, String compoId){
    StringBuffer tmpCondition = new StringBuffer();
    condition = WFUtil.getListTypeFromString(condition)[1];
    boolean needInvalidInstance = WFCompoType.WF_FILTER_COMPO_INVALID
      .equalsIgnoreCase(wfListType);
    boolean needDoneInstances = (WFCompoType.WF_FILTER_COMPO_DONE
      .equalsIgnoreCase(wfListType) || WFCompoType.WF_FILTER_COMPO
      .equalsIgnoreCase(wfListType));
    boolean needTodoInstances = (WFCompoType.WF_FILTER_COMPO_TODO
      .equalsIgnoreCase(wfListType) || WFCompoType.WF_FILTER_COMPO
      .equalsIgnoreCase(wfListType));

    String instanceSql ="";

    instanceSql = getAllTypeSqlByExecutor(user,needInvalidInstance,
      needTodoInstances,needDoneInstances, compoId);

    if (!StringTools.isEmptyString(instanceSql)) {
      if (!StringTools.isEmptyString(condition)) {
        tmpCondition.append(condition +" and ");
      }
      String mainTable = MetaManager.getTableMeta(compoId).getName();
      tmpCondition.append(mainTable + ".PROCESS_INST_ID IN ( ");
      tmpCondition.append(instanceSql);
      tmpCondition.append(" ) ");
    } else {
      if (!StringTools.isEmptyString(condition)) {
        tmpCondition.append(condition + " and 1=0");
      } else {
        tmpCondition.append(" 1=0");
      }
    }
    return tmpCondition.toString();
  }
  public static String getAllTypeSqlByExecutor(String executor,
    boolean isInValid, boolean needTodo,boolean needDone, String compoId){
    String sql = "" ;
    if(!isInValid){
      if(needTodo){
        sql = "select instance_id from v_wf_current_task"
          +" where executor = '" + executor + "' and compo_id='"+ compoId+"' ";
      }
      if(needDone){
        sql = "select instance_id from v_wf_action_history "
          +" where executor = '" + executor + "' and compo_id='"+ compoId+"' ";
      }
      if(needTodo && needDone){
        sql = "select instance_id from v_wf_current_task "
          +" where executor = '" + executor
          + "' and compo_id='"+ compoId+"' union select instance_id from v_wf_action_history "
          + " where executor = '" + executor + "' and compo_id='"+compoId+"'";
      }
    }else{
      sql = "select instance_id from v_wf_current_task_invalid "
        +" where executor = '" + executor
        + "' and compo_id= '"+compoId+"' union select instance_id from v_wf_action_history_invalid "
        + " where executor = '" + executor + "' and compo_id='"+compoId+"'";
    }
    return sql;
  }

  public static String getDraftSqlByExecutor(String executor, String compoId){
    return "select WF_DRAFT_ID from as_wf_draft where user_id='"+executor+"' and compo_id='"+compoId+"'";
  }
  /**
   * ����ʵ���б�ƴ������
   *
   * @param instanceList
   *          ʵ�����б�
   * @return ƴ�ӵ�sql���������
   */
  public static String getSqlStrByInstanceList(List instanceList, String compoId) {
    String result = null;
    StringBuffer sqlStr = new StringBuffer();
    int listLen = instanceList.size();
    List tmp = new ArrayList();
    String mainTable = MetaManager.getTableMeta(compoId).getName();
    sqlStr.append(" ( ");
    while (listLen > 10) {
      tmp = instanceList.subList(0, 10);
      sqlStr.append(mainTable + ".PROCESS_INST_ID");
      sqlStr.append(" IN ( ");
      sqlStr.append(instanceListToSqlStr(tmp));
      sqlStr.append(" ) OR ");
      instanceList = instanceList.subList(10, listLen);
      listLen = instanceList.size();
    }
    if (listLen > 0) {
      sqlStr.append(mainTable + ".PROCESS_INST_ID");
      sqlStr.append(" IN ( ");
      sqlStr.append(instanceListToSqlStr(instanceList));
      sqlStr.append(" )");
      result = sqlStr.toString();
    } else {
      result = sqlStr.toString();
      int orPos = result.lastIndexOf("OR");
      result = result.substring(0, orPos);
    }
    result += " ) ";
    return result;
  }

  public static String instanceListToSqlStr(List instanceList) {
    StringBuffer InstancesSB = new StringBuffer();
    Iterator i = instanceList.iterator();
    while (i.hasNext()) {
      String instanceId = (String) i.next();
      InstancesSB.append("'");
      InstancesSB.append(instanceId);
      if (i.hasNext()) {
        InstancesSB.append("',");
      } else {
        InstancesSB.append("'");
      }
    }
    return InstancesSB.toString();
  }

  /**
   * �ж��Ƿ��Ǹ�user���ֹ�������
   */
  public static boolean isMyInvalidInst(String user, String instanceId)
    throws WorkflowException {
    boolean isInvalidInst = false;
    List resultTodo = new ArrayList();
    List resultDone = new ArrayList();
    resultTodo = ExecuteFacade.getInvalidTodoInstListByExecutor(user);
    if (resultTodo != null && resultTodo.size() > 0) {
      isInvalidInst = resultTodo.contains(instanceId);
    }
    if (!isInvalidInst) {
      resultDone = ExecuteFacade.getInvalidDoneInstListByExecutor(user);
      if (resultDone != null && resultDone.size() > 0) {
        isInvalidInst = resultDone.contains(instanceId);
      }
    }
    return isInvalidInst;
  }

}
