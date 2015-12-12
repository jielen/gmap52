package com.kingdrive.workflow.web;

import com.kingdrive.framework.controller.web.SessionManager;
import com.kingdrive.framework.controller.web.action.WebAction;
import com.kingdrive.framework.exception.GeneralException;
import com.kingdrive.workflow.ExecuteFacade;
import com.kingdrive.workflow.exception.WorkflowException;
import com.kingdrive.workflow.util.StringUtil;

public class DocumentWebAction extends WebAction {

  public DocumentWebAction() {
  }

  private String listDocuments() throws GeneralException {
    int instanceId = StringUtil.string2int((String) tps.get(SessionManager.PDS,
        "INSTANCE_ID"));

    java.util.List list;
    try {
      list = ExecuteFacade.getDocumentListByInstance(instanceId);
    } catch (WorkflowException wfe) {
      throw new GeneralException(wfe.toString());
    }
    tps.put(SessionManager.TDS, "doclist", list);
    return null;
  }

  private String removeDocument() throws GeneralException {
    int docId = StringUtil.string2int((String) tps.get(SessionManager.PDS,
        "docid"));
    try {
      ExecuteFacade.removeDocument(docId);
    } catch (WorkflowException wfe) {
      throw new GeneralException(wfe.toString());
    }
    return null;
  }

  public String perform(String event) throws GeneralException {

    int eventId = Integer.parseInt(event);
    switch (eventId) {
    case 0: // '\0'
      return listDocuments();

    case 1: // '\001'
      return removeDocument();

    default:
      throw new GeneralException("Î´Öª¹¦ÄÜ¡£");
    }
  }
}
