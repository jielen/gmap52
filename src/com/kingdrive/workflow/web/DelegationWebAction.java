package com.kingdrive.workflow.web;

import com.kingdrive.framework.controller.web.SessionManager;
import com.kingdrive.framework.controller.web.action.WebAction;
import com.kingdrive.framework.exception.GeneralException;
import com.kingdrive.workflow.DelegateFacade;
import com.kingdrive.workflow.dto.DelegationMeta;
import com.kingdrive.workflow.exception.WorkflowException;
import com.kingdrive.workflow.util.StringUtil;

public class DelegationWebAction extends WebAction {

  public DelegationWebAction() {
  }

  private String getAvailableDelegations() throws GeneralException {
    String staffId = StringUtil.string2string((String) tps.get(
        SessionManager.SDS, "STAFF_ID"));
    removeOutofdate();
    java.util.List list;
    try {
      list = DelegateFacade.getDelegationList(staffId);
    } catch (WorkflowException wfe) {
      throw new GeneralException(wfe.toString());
    }
    tps.put(SessionManager.TDS, "availabledgs", list);
    return null;
  }

  private String delegate() throws GeneralException {

    String staffId = StringUtil.string2string((String) tps.get(
        SessionManager.SDS, "STAFF_ID"));
    String description = StringUtil.string2string((String) tps.get(
        SessionManager.PDS, "desc"));
    int templateId = StringUtil.string2int((String) tps.get(SessionManager.PDS,
        "templateid"));
    int nodeId = StringUtil.string2int((String) tps.get(SessionManager.PDS,
        "nodeid"));
    String owner = StringUtil.string2string((String) tps.get(
        SessionManager.PDS, "owner"));
    String receiver = StringUtil.string2string((String) tps.get(
        SessionManager.PDS, "receiver"));
    int parentId = StringUtil.string2int((String) tps.get(SessionManager.PDS,
        "parentid"));
    String startTime = StringUtil.string2string((String) tps.get(
        SessionManager.PDS, "starttime"));
    String endTime = StringUtil.string2string((String) tps.get(
        SessionManager.PDS, "endtime"));

    DelegationMeta dto = new DelegationMeta();
    dto.setDescription(description);
    dto.setTemplateId(templateId);
    dto.setNodeId(nodeId);
    dto.setSender(staffId);
    dto.setOwner(owner);
    dto.setReceiver(receiver);
    dto.setParentId(parentId);
    dto.setStartTime(startTime);
    dto.setEndTime(endTime);

    try {
      DelegateFacade.delegate(dto);
    } catch (WorkflowException wfe) {
      throw new GeneralException(wfe.toString());
    }
    return null;
  }

  private String listDelegation() throws GeneralException {
    String staffId = StringUtil.string2string((String) tps.get(
        SessionManager.SDS, "STAFF_ID"));
    java.util.List list;
    try {
      list = DelegateFacade.getDelegationListBySender(staffId);
    } catch (WorkflowException wfe) {
      throw new GeneralException(wfe.toString());
    }
    tps.put(SessionManager.TDS, "delegations", list);

    return null;
  }

  private String cancelDelegate() throws GeneralException {
    int delegationId = StringUtil.string2int((String) tps.get(
        SessionManager.PDS, "dgid"));
    ;

    try {
      DelegateFacade.cancelDelegation(delegationId);
    } catch (WorkflowException wfe) {
      throw new GeneralException(wfe.toString());
    }
    return null;
  }

  private String removeOutofdate() throws GeneralException {
    try {
      DelegateFacade.removeOutOfDate();
    } catch (WorkflowException wfe) {
      throw new GeneralException(wfe.toString());
    }
    return null;
  }

  public String perform(String event) throws GeneralException {

    int eventId = Integer.parseInt(event);
    switch (eventId) {
    case 0: // '\0'
      return getAvailableDelegations();

    case 1: // '\001'
      return delegate();

    case 2: // '\002'
      return listDelegation();

    case 3: // '\003'
      return cancelDelegate();

    case 4: // '\004'
      return removeOutofdate();

    default:
      throw new GeneralException("Î´Öª¹¦ÄÜ¡£");
    }
  }
}
