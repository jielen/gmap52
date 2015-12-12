package com.kingdrive.workflow.business;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.kingdrive.framework.util.Debug;
import com.kingdrive.workflow.access.StaffBean;
import com.kingdrive.workflow.access.StaffPositionBean;
import com.kingdrive.workflow.access.StaffPositionQuery;
import com.kingdrive.workflow.dto.StaffPositionMeta;
import com.kingdrive.workflow.exception.WorkflowException;
import com.kingdrive.workflow.model.StaffModel;
import com.kingdrive.workflow.model.StaffPositionInfo;
import com.kingdrive.workflow.model.StaffPositionModel;
import com.kingdrive.workflow.util.Sequence;
import com.kingdrive.workflow.util.StringUtil;

public class Staff implements Serializable {

  private String id;

  private String name;

  private transient String password;

  private String description;

  private String email;

  private String status;

  public static final String STAFF_STATUS_ENABLED = "0";

  public static final String STAFF_STATUS_DISABLED = "1";

  public Staff() {
    status = STAFF_STATUS_ENABLED;
  }

  public boolean login(String staffId, String password, boolean encoded) throws WorkflowException {
    Staff staff = getStaff(staffId);
    if (staff.getId() == null) {
      throw new WorkflowException(2200);
    }
    if (!staff.getStatus().equals("0")) {
      throw new WorkflowException(2201);
    }
    if (password == null || password.equals("")) {
      if (staff.getPassword() == null || staff.getPassword().equals("")) {
        return true;
      }
      throw new WorkflowException(2202);
    }
    if (staff.getPassword() == null || staff.getPassword().equals("")) {
      throw new WorkflowException(2202);
    }
    if (encoded) {
      if (password.equals(staff.getPassword())) {
        return true;
      }
      throw new WorkflowException(2202);
    }
    if (StringUtil.encode(password).equals(staff.getPassword())) {
      return true;
    }
    throw new WorkflowException(2202);
  }

  public List getStaffList() throws WorkflowException {
    List result = new ArrayList();

    try {
      StaffBean bean = new StaffBean();
      ArrayList list = bean.find();
      Staff staff = null;
      for (int i = 0; i < list.size(); i++, result.add(staff)) {
        staff = wrap((StaffModel) list.get(i));
      }
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
    return result;
  }

  public void create(Staff staff) throws WorkflowException {
    Debug.println("[Staff] staffId=".concat(String.valueOf(staff.getId())));
    if (staff.getId() == null || staff.getId().trim().equals("")) {
      Debug.println("[Staff] fetch sequence");
      staff.setId(String.valueOf(Sequence.fetch(Sequence.SEQ_STAFF)));
    } else {
      Debug.println("[Staff] skip fetch sequence");
    }
    try {
      StaffBean bean = new StaffBean();
      if (bean.insert(unwrap(staff)) != 1)
        throw new WorkflowException(2104);
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
  }

  public void create() throws WorkflowException {
    create(this);
  }

  public Staff getStaff(String staffId)
      throws WorkflowException {
    Staff result = new Staff();
    try {
      StaffBean bean = new StaffBean();
      result = wrap(bean.findByKey(staffId));
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
    return result;
  }

  public boolean load() throws WorkflowException {
    Staff staff = getStaff(id);
    if (staff == null) {
      return false;
    }
    name = staff.getName();
    password = staff.getPassword();
    description = staff.getDescription();
    email = staff.getEmail();
    status = staff.getStatus();
    return true;
  }

  public void delete(String staffId) throws WorkflowException {
    Staff staff = getStaff(staffId);
    if (staff != null) {
      staff.setStatus("1");
      staff.update();
    }
  }

  public void delete() throws WorkflowException {
    delete(id);
  }

  public void updateStaff(Staff staff)
      throws WorkflowException {
    try {
      StaffBean bean = new StaffBean();
      if (bean.update(unwrap(staff)) != 1)
        throw new WorkflowException(2105);
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
  }

  public void update() throws WorkflowException {
    updateStaff(this);
  }

  public boolean equals(Object obj) {
    if (!(obj instanceof Staff))
      return false;
    Staff o = (Staff) obj;
    return o.id == id;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public HashSet getSuperStaffSet(String staffId, String orgPositionId) throws WorkflowException {
    HashSet result = new HashSet();

    try {
      StaffBean bean = new StaffBean();
      ArrayList list = bean.getSuperStaffList(staffId, orgPositionId);
      String staff = null;
      for (int i = 0; i < list.size(); i++, result.add(staff)) {
        staff = ((StaffModel) list.get(i)).getStaffId();
      }
    } catch (SQLException sqle) {
      throw new WorkflowException(1166, sqle.toString());
    }
    //if (result.size() == 0)//找不到时也不必默认给自己
    //  result.add(staffId);
    return result;
  }

  public List getStaffListByExecutor(int nodeId, int responsibility) throws WorkflowException {
    List result = new ArrayList();

    try {
      StaffBean bean = new StaffBean();
      ArrayList list = bean
          .getStaffListByExecutor(nodeId, responsibility);
      Staff staff = null;
      for (int i = 0; i < list.size(); i++, result.add(staff)) {
        staff = wrap((StaffModel) list.get(i));
      }
    } catch (SQLException e) {
      throw new WorkflowException(2008, e.toString());
    }
    return result;
  }

  public List getStaffListByNonExecutor(int nodeId)
      throws WorkflowException {
    List result = new ArrayList();

    try {
      StaffBean bean = new StaffBean();
      ArrayList list = bean.getStaffListByNonExecutor(nodeId);
      Staff staff = null;
      for (int i = 0; i < list.size(); i++, result.add(staff)) {
        staff = wrap((StaffModel) list.get(i));
      }
    } catch (SQLException e) {
      throw new WorkflowException(2008, e.toString());
    }
    return result;
  }

  public void createStaffPosition(String staffId, String positionId,
      String orgId) throws WorkflowException {
    StaffPositionBean bean = new StaffPositionBean();
    try {
      StaffPositionModel model = new StaffPositionModel();
      model.setOrgPositionId(positionId);
      model.setStaffId(staffId);
      bean.insert(model);
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
  }

  public List getStaffPositionList(String staffId)
      throws WorkflowException {
    List result = new ArrayList();
    try {
      StaffPositionQuery query = new StaffPositionQuery();
      ArrayList list = query.getStaffPositionList(staffId);
      StaffPositionMeta meta;
      for (int i = 0; i < list.size(); i++, result.add(meta)) {
        meta = wrapStaffPosition((StaffPositionInfo) list.get(i));
      }
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
    return result;
  }

  public void resetStaffPosition(String staffId, String[] orgPositionId) throws WorkflowException {
    StaffPositionBean bean = new StaffPositionBean();
    try {
      bean.removeByStaff(staffId);
      if (orgPositionId == null) {
        return;
      }
      for (int i = 0; i < orgPositionId.length; i++) {
        StaffPositionModel model = new StaffPositionModel();
        model.setStaffId(staffId);
        model.setOrgPositionId(orgPositionId[i]);
        bean.insert(model);
      }
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
  }

  private StaffPositionMeta wrapStaffPosition(StaffPositionInfo model) {
    StaffPositionMeta meta = new StaffPositionMeta();
    meta.setOrgPositionId(model.getOrgPositionId());
    meta.setStaffId(model.getStaffId());
    meta.setStaffName(model.getStaffName());
    meta.setCompanyId(model.getCompanyId());
    meta.setCompanyName(model.getCompanyName());
    meta.setOrganizationId(model.getOrganizationId());
    meta.setOrganizationName(model.getOrganizationName());
    meta.setPositionId(model.getPositionId());
    meta.setPositionName(model.getPositionName());
    return meta;
  }

  private Staff wrap(StaffModel model) {
    Staff staff = new Staff();
    staff.setId(model.getStaffId());
    staff.setName(model.getName());
    staff.setPassword(model.getPwd());
    staff.setDescription(model.getDescription());
    staff.setEmail(model.getEmail());
    staff.setStatus(model.getStatus());
    return staff;
  }

  private StaffModel unwrap(Staff staff) {
    StaffModel model = new StaffModel();
    if (staff.id != null && !staff.id.equals(""))
      model.setStaffId(staff.getId());
    if (staff.name != null)
      model.setName(staff.getName());
    if (staff.password != null)
      model.setPwd(staff.getPassword());
    if (staff.description != null)
      model.setDescription(staff.getDescription());
    if (staff.email != null)
      model.setEmail(staff.getEmail());
    if (staff.status != null)
      model.setStatus(staff.getStatus());
    return model;
  }
}
