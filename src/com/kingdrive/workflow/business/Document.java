package com.kingdrive.workflow.business;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.kingdrive.workflow.access.DocumentBean;
import com.kingdrive.workflow.dto.DocumentMeta;
import com.kingdrive.workflow.exception.WorkflowException;
import com.kingdrive.workflow.model.DocumentModel;
import com.kingdrive.workflow.util.DateTime;
import com.kingdrive.workflow.util.Sequence;

public class Document {

  public Document() {
  }

  private DocumentMeta wrap(DocumentModel model) {
    DocumentMeta doc = new DocumentMeta();
    if (model.getDocumentId() != null)
      doc.setId(model.getDocumentId().intValue());
    doc.setName(model.getName());
    doc.setType(model.getType());
    if (model.getInstanceId() != null)
      doc.setInstanceId(model.getInstanceId().intValue());
    doc.setLinkName(model.getLinkName());
    doc.setUploadTime(model.getUploadTime());
    doc.setDescription(model.getDescription());
    return doc;
  }

  private DocumentModel unwrap(DocumentMeta meta) {
    DocumentModel model = new DocumentModel();
    if (meta.getId() != 0)
      model.setDocumentId(meta.getId());
    if (meta.getName() != null)
      model.setName(meta.getName());
    if (meta.getType() != null)
      model.setType(meta.getType());
    if (meta.getInstanceId() != 0)
      model.setInstanceId(meta.getInstanceId());
    if (meta.getLinkName() != null)
      model.setLinkName(meta.getLinkName());
    if (meta.getUploadTime() != null)
      model.setUploadTime(meta.getUploadTime());
    if (meta.getDescription() != null)
      model.setDescription(meta.getDescription());
    return model;
  }

  public void create(int instanceId, String name, String type, String linkName,
      String description, String uploadTime)
      throws WorkflowException {
    // add the document
    DocumentMeta meta = new DocumentMeta();
    meta.setInstanceId(instanceId);
    meta.setName(name);
    meta.setType(type);
    meta.setLinkName(linkName);
    meta.setUploadTime(uploadTime);
    meta.setDescription(description);

    create(meta);
  }

  public void create(DocumentMeta meta)
      throws WorkflowException {
    DocumentBean bean = new DocumentBean();
    try {
      // set the upload time as current system time.
      if (meta.getUploadTime() == null) {
        meta.setUploadTime(DateTime.getSysTime());
      }

      // remove the document
      if (meta.getLinkName() != null) {
        removeByLinkName(meta.getLinkName());
      }

      // set the document id
      meta.setId(Sequence.fetch(Sequence.SEQ_DOCUMENT));

      // add the document
      bean.insert(unwrap(meta));

    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
  }

  public void delete(int docId) throws WorkflowException {
    try {
      DocumentBean bean = new DocumentBean();
      DocumentModel model = bean.findByKey(docId);
      bean.delete(model.getDocumentId().intValue());
      File file = new File(model.getLinkName());
      file.delete();
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
  }

  public List getDocumentListByInstance(int instanceId)
      throws WorkflowException {
    List result = new ArrayList();

    try {
      DocumentBean bean = new DocumentBean();
      ArrayList list = bean.findByInstance(instanceId);
      DocumentMeta document = null;
      for (int i = 0; i < list.size(); i++, result.add(document)) {
        document = wrap((DocumentModel) list.get(i));
      }
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
    return result;
  }

  private void removeByLinkName(String linkName)
      throws WorkflowException {
    DocumentBean bean = new DocumentBean();
    try {
      bean.removeByLinkName(linkName);
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
  }

  public void removeByInstance(int istanceId)
      throws WorkflowException {
    DocumentBean bean = new DocumentBean();
    try {
      bean.removeByInstance(istanceId);
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
  }
}
