package com.anyi.gp.taglib.components;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;

import org.apache.log4j.Logger;

import com.anyi.gp.BusinessException;
import com.anyi.gp.Datum;
import com.anyi.gp.access.PageDataProvider;
import com.anyi.gp.context.ApplusContext;
import com.anyi.gp.meta.Field;
import com.anyi.gp.meta.MetaManager;
import com.anyi.gp.meta.TableMeta;
import com.anyi.gp.pub.DBHelper;
import com.anyi.gp.pub.DataTools;
import com.anyi.gp.pub.RightUtil;
import com.anyi.gp.pub.SessionUtils;
import com.anyi.gp.taglib.ITag;
import com.anyi.gp.taglib.ToolbarTag;
import com.anyi.gp.util.StringTools;
import com.anyi.gp.workflow.WFFactory;
import com.anyi.gp.workflow.WFService;
import com.anyi.gp.workflow.bean.ActivityDefBean;
import com.anyi.gp.workflow.bean.WorkitemBean;
import com.anyi.gp.workflow.util.WFUtil;
import com.kingdrive.workflow.dto.ActionMeta;

public class TableDataPart implements Component {

	private static final Logger logger = Logger.getLogger(TableDataPart.class);

	private int pagesize = -1; // pagesize = -1:分页

	private boolean istablemeta = true;

	private String componame = "";

	private String tablename = "";

	private String physicaltable = "";

	private String sqlid = "";

	private String condition = "";

	private String totalfields = "";

	private boolean issave = false;

	private boolean isdigest = true;

	private String onceautonumfields = "";

	private int fromrow = 0;

	private int torow = 0;

	private List totalfieldlist = new ArrayList();

	private Map paramfieldmap = null;

	private Page ownerPage = null;

	private ServletRequest request = null;

	private String wfStatus = null;
  
  private String sqlCountid = "";
  
  private String sqlSumid="";

	public void writeHTML(Writer out) throws IOException, BusinessException {
		validate();
		init();
		print(out);
	}

	public void init() {
		if (totalfields != null && !totalfields.trim().equals("")) {
			totalfieldlist = StringTools.split(totalfields, ",");
		}
		if (physicaltable == null || physicaltable.length() == 0)
			physicaltable = tablename;
		
		//不得已而为之,补漏用
		if (this.componame == null || "".equals(this.componame)) {
      if(this.ownerPage != null && this.ownerPage.getCurrRequest() != null)
        this.componame = this.ownerPage.getCurrRequest().getParameter("componame");
		}

		condition = StringTools.combCondition(condition, StringTools
				.combCondition(getMainTableCondition(), getRequestCondition()));

		HttpServletRequest request = ownerPage.getCurrRequest();
		paramfieldmap = DataTools.getOuterTableFieldsPool(tablename, request);// 处理外部参数接口
	}

	/**
	 * 加进从外部传来的condition
	 */
	private String getRequestCondition() {
		return (String) this.ownerPage.getCurrRequest().getAttribute(
				Page.TAG_INTERFACE_CONDITION);
	}

	private String getMainTableCondition() {
		HttpServletRequest request = ownerPage.getCurrRequest();
		String compoName = request.getParameter("componame");
		if (compoName != null && compoName.length() > 0) {
			TableMeta tableMeta = MetaManager
					.getTableMetaByCompoName(compoName);
			if (tableMeta.isChildTable(tablename)) {// 加上compo的主键信息
				return request.getParameter("condition");
			}
		}
		return "";
	}

	private void validate() {
		HttpServletRequest request = ownerPage.getCurrRequest();
		String compoName = request.getParameter("componame");
		if (compoName != null && compoName.length() > 0) {
			TableMeta tableMeta = MetaManager
					.getTableMetaByCompoName(compoName);
			if (tableMeta.isChildTable(tablename)) {// 加上compo的主键信息
				componame = compoName;
			}
		}

		if (issave && (componame == null || componame.length() == 0)) {
			throw new RuntimeException("标签TableDataTag（tablename=" + tablename
					+ "）中设置属性issave为true时，必需设置属性componame！");
		}
	}

	public void print(Writer out) throws IOException, BusinessException {
		resetPageSize();
		Datum datum = printTableData(out);
		if (issave) {
			printTableAdditionalTable(datum, out);
		}
		dowfEditPageWork((JspWriter) out, request);
		printWfStatus(out);
		this.ownerPage.putDatum(tablename, datum);
	}

	private void resetPageSize() {
		if (this.getPagesize() == 0) {
			pagesize = Integer.parseInt(ApplusContext.getEnvironmentConfig()
					.get("pagesize"));
			if (pagesize == 0) {
				pagesize = -1;
			}
		}
	}

	private Datum printTableData(Writer out) throws IOException {
		if (istablemeta) {
			TableMeta voTableData = MetaManager.getTableMeta(tablename);
			out.write(voTableData.toXml(paramfieldmap));
		}
		Datum datum = null;
		boolean isVirtualTable = DBHelper.isVirtualTable(tablename);
		if (isVirtualTable) {
			datum = new Datum();
			datum.setName(tablename);

			List data = new ArrayList();
			TableMeta tableMeta = MetaManager.getTableMeta(tablename);
			List fieldNames = tableMeta.getFieldNames();// TCJLODO 仅仅输出需要的字段
			Map map = new HashMap();
			for (int i = 0; i < fieldNames.size(); i++) {
				map.put(fieldNames.get(i), "");
			}
			data.add(map);
			datum.setData(data);
			datum.addMetaField("pageindex", "1");
			datum.addMetaField("rowcountofpage", "0");
			datum.addMetaField("fromrow", "0");
			datum.addMetaField("torow", "0");
			datum.addMetaField("rowcountofdb", "0");
			datum.pringDataX(out, tablename);

			return datum;
		}

		String userNumLimCondition = "";

		HttpServletRequest request = this.ownerPage.getCurrRequest();
		String pageType = Page.getPage(request).getPageType();

		if (Page.PAGE_TYPE_LIST.equals(pageType)) {
			String userId = SessionUtils.getAttribute(request, "svUserID");
			if (tablename != null
					&& tablename.length() > 0
					&& componame != null
					&& componame.length() > 0
					&& tablename.equals(MetaManager.getCompoMeta(componame)
							.getMasterTable())) {
				userNumLimCondition = RightUtil.getUserNumLimCondition(
						ownerPage.getCurrRequest(), userId, "fwatch",
						componame, null, null);
			}
		}

		int pageIndex = 0;
		boolean isBlank = false;
		PageDataProvider dataProvider = (PageDataProvider) ApplusContext.getBean("pageDataProvider");

		dataProvider.setUserNumLimCondition(userNumLimCondition);

		Map params = new HashMap();
		DBHelper.parseParamsSimpleForSql(condition, params);
		if (condition.indexOf("1=0") >= 0) {
			isBlank = true;
		}

		int totalCount = 0;
		if (!isBlank) {
			totalCount = dataProvider.getTotalCount(this.getSqlCountid(), params);
    }
		if (pagesize > 0) {
			params.put("rownum", pagesize + "");
			params.put("rn", pageIndex + "");
			fromrow = pageIndex;
			torow = pagesize > totalCount ? totalCount : pagesize;
			datum = dataProvider.getPaginationData(pageIndex + 1, totalCount,torow, tablename, sqlid, params, isBlank);
		} else {
			fromrow = pageIndex;
			torow = -1;
			datum = dataProvider.getPageData(pageIndex + 1, totalCount,pagesize, tablename, sqlid, params, isBlank);
		}
		datum.addMetaField("sqlid", sqlid);
		datum.addMetaField("condition", condition);
    datum.addMetaField("sqlCountid", this.sqlCountid);
    datum.addMetaField("sqlSumid", this.sqlSumid);
    datum.addMetaField("userNumLimCondition", userNumLimCondition);
    String digest = DataTools.getDigest(datum, tablename);
    datum.addMetaField("digest", digest);
    
		datum.pringDataX(out, tablename);

		if (totalfieldlist == null || totalfieldlist.size() == 0)
			return datum;

		Map totalData = null;
		if (!isBlank) {
			totalData = dataProvider.getPageTotalData(this.getSqlSumid(), params,totalfieldlist);
		} else {
			totalData = new HashMap();
		}
		printTableTotalData(totalData, out);

		return datum;
	}

	private void printTableTotalData(Map totalData, Writer out) {
		if (totalfieldlist == null)
			return;

		try {
			out.write("<xml id=\"TableTotal_" + tablename
					+ "_XML\" asynch=\"false\">\n");
			out.write(DataTools.getDBTotal(tablename, totalfields, totalData));
			out.write("</xml>\n");
		} catch (IOException e) {
			String msg = "\nPageDataTag.printListPageTotal():\n"
					+ e.getMessage();
			logger.debug(msg);
			throw new RuntimeException(msg);
		}
	}

	private void printTableAdditionalTable(Datum datum, Writer out)
			throws IOException {

		TableMeta tableMeta = MetaManager.getTableMeta(tablename);
		out.write("\n<xml id=\"TableAdditionalMeta_" + tablename
				+ "_XML\" asynch=\"false\">\n");
		printDigestData(out, datum, tableMeta);
		printKeyFields(out, tableMeta);
		printFields(out, tableMeta);
		printNotSaveFiels(out, tableMeta);
		out.write("</xml>\n");
	}

	private void printDigestData(Writer out, Datum datum, TableMeta tableMeta)
			throws IOException {
		

		out.write("<table name=\"");
		out.write(tablename);
		out.write("\" componame=\"");
		out.write(componame);
		out.write("\" physicaltable=\"");
		out.write(physicaltable);
		out.write("\" issave=\"");
		out.write(Boolean.toString(issave));
		out.write("\" isdigest=\"");
		out.write(Boolean.toString(isdigest));
		out.write("\" sqlid=\"");
		out.write(sqlid);
		out.write("\" condition=\"");
		out.write(condition);
		out.write("\" fromrow=\"" + fromrow);
		out.write("\" torow=\"" + torow);
		out.write("\" ");
	}

	/**
	 * 表的主键字段信息
	 * 
	 * @param out
	 * @param tableMeta
	 * @throws IOException
	 */
	private void printKeyFields(Writer out, TableMeta tableMeta)
			throws IOException {
		List keyFieldList = tableMeta.getKeyFieldNames();
		StringBuffer voKeyFieldBuf = new StringBuffer();
		if (keyFieldList != null) {
			for (int i = 0; i < keyFieldList.size(); i++) {
				if (i > 0)
					voKeyFieldBuf.append(",");
				voKeyFieldBuf.append(keyFieldList.get(i));
			}
		}
		out.write("keyfields=\"");
		out.write(voKeyFieldBuf.toString());
		out.write("\" ");

	}

	/**
	 * 表字段信息
	 * 
	 * @param out
	 * @param tableMeta
	 * @param voNode
	 * @throws IOException
	 */
	private void printFields(Writer out, TableMeta tableMeta)
			throws IOException {
		List fieldNameList = tableMeta.getFieldNames();
		StringBuffer voNumFieldBuf = new StringBuffer();
		StringBuffer voDateFieldBuf = new StringBuffer();
		StringBuffer voDatetimeFieldBuf = new StringBuffer();

		if (fieldNameList != null) {
			for (int i = 0; i < fieldNameList.size(); i++) {
				Field voField = tableMeta.getField((String) fieldNameList
						.get(i));
				if (Field.DATA_TYPE_NUM.equals(voField.getType().toUpperCase())) {
					if (voNumFieldBuf.length() > 0)
						voNumFieldBuf.append(",");
					voNumFieldBuf.append(fieldNameList.get(i));
				}
				if (Field.DATA_TYPE_DATE
						.equals(voField.getType().toUpperCase())) {
					if (voDateFieldBuf.length() > 0)
						voDateFieldBuf.append(",");
					voDateFieldBuf.append(fieldNameList.get(i));
				}
				if (Field.DATA_TYPE_DATETIME.equals(voField.getType()
						.toUpperCase())) {
					if (voDatetimeFieldBuf.length() > 0)
						voDatetimeFieldBuf.append(",");
					voDatetimeFieldBuf.append(fieldNameList.get(i));
				}
			}
		}
		out.write("numericfields=\"");
		out.write(voNumFieldBuf.toString());
		out.write("\" ");
		out.write("datefields=\"");
		out.write(voDateFieldBuf.toString());
		out.write("\" ");
		out.write("datetimefields=\"");
		out.write(voDatetimeFieldBuf.toString());
		out.write("\" ");
		out.write("onceautonumfields=\"");
		out.write(onceautonumfields);
		out.write("\" ");
		out.write("onceautonums=\"\" ");
	}

	/**
	 * 表中不需要保存字段的信息
	 * 
	 * @param out
	 * @param tableMeta
	 * @throws IOException
	 */
	private void printNotSaveFiels(Writer out, TableMeta tableMeta)
			throws IOException {
		List notSaveFieldList = tableMeta.getNoSaveFieldNames();
		StringBuffer voNotSaveFieldBuf = new StringBuffer();
		if (notSaveFieldList != null) {
			for (int i = 0; i < notSaveFieldList.size(); i++) {
				if (i > 0)
					voNotSaveFieldBuf.append(",");
				voNotSaveFieldBuf.append(notSaveFieldList.get(i));
			}
		}
		out.write("notsavefields=\"");
		out.write(voNotSaveFieldBuf.toString());
		out.write("\" ");
		out.write("/>\n");

	}

	/**
	 * 打印工作流数据
	 * 
	 * @param out
	 * @throws BusinessException
	 * @throws IOException
	 */
	private void printEditPageWFData(Writer out) throws IOException {
		HttpServletRequest request = this.ownerPage.getCurrRequest();

		Map params = new HashMap();
		DBHelper.parseParamsSimpleForSql(condition, params);
		String strInstanceId = (String) params.get("PROCESS_INST_ID");
		String userId = SessionUtils.getAttribute(request, "svUserID");
		if (strInstanceId != null) {
			int instanceId = Integer.parseInt(strInstanceId);
			if (instanceId > 0) {
				String templateId = WFUtil.getTemplateByInsId(strInstanceId);
				WFService wfs = WFFactory.getInstance().getService();
				String wfdataxml;
				try {
					wfdataxml = wfs.getWfdataByProcessInstId(userId,
							templateId, strInstanceId, request);
				} catch (BusinessException e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
				out.write(wfdataxml);
				out.write("\n");
			}
		}
	}

	public void setOwnerPage(Page ownerPage) {
		this.ownerPage = ownerPage;
	}

	public int getPagesize() {
		return pagesize;
	}

	public void setPagesize(int pagesize) {
		this.pagesize = pagesize;
	}

	public boolean isIstablemeta() {
		return istablemeta;
	}

	public void setIstablemeta(boolean istablemeta) {
		this.istablemeta = istablemeta;
	}

	public String getComponame() {
		return componame;
	}

	public void setComponame(String componame) {
		this.componame = componame;
	}

	public String getTablename() {
		return tablename;
	}

	public void setTablename(String tablename) {
		this.tablename = tablename;
	}

	public String getPhysicaltable() {
		return physicaltable;
	}

	public void setPhysicaltable(String physicaltable) {
		this.physicaltable = physicaltable;
	}

	public String getSqlid() {
		return sqlid;
	}

	public void setSqlid(String sqlid) {
		this.sqlid = sqlid;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public String getTotalfields() {
		return totalfields;
	}

	public void setTotalfields(String totalfields) {
		this.totalfields = totalfields;
	}

	public List getTotalfieldlist() {
		return totalfieldlist;
	}

	public boolean isIssave() {
		return issave;
	}

	public void setIssave(boolean issave) {
		this.issave = issave;
	}

	public boolean isIsdigest() {
		return isdigest;
	}

	public void setIsdigest(boolean isdigest) {
		this.isdigest = isdigest;
	}

	public String getOnceautonumfields() {
		return onceautonumfields;
	}

	public void setOnceautonumfields(String onceautonumfields) {
		this.onceautonumfields = onceautonumfields;
	}

	public int getFromrow() {
		return fromrow;
	}

	public void setFromrow(int fromrow) {
		this.fromrow = fromrow;
	}

	public int getTorow() {
		return torow;
	}

	public void setTorow(int torow) {
		this.torow = torow;
	}

	public Map getParamfieldmap() {
		return paramfieldmap;
	}

	public Page getOwnerPage() {
		return ownerPage;
	}

	public void writeInitScript(Writer out) throws IOException {
	}

	public String getId() {
		return this.componame + "_" + this.tablename + "_tabledata_id";
	}

	public ServletRequest getRequest() {
		return request;
	}

	public void setRequest(ServletRequest request) {
		this.request = request;
	}
  

	public String getSqlCountid() {
    String result = sqlCountid==null || sqlCountid.equals("")?sqlid:sqlCountid;
    return result;
  }

  public void setSqlCountid(String sqlCountid) {
    this.sqlCountid = sqlCountid;
  }

  public String getSqlSumid() {
    String result = sqlSumid==null || sqlSumid.equals("")?sqlid:sqlSumid;
    return result;
  }

  public void setSqlSumid(String sqlSumid) {
    this.sqlSumid = sqlSumid;
  }

  /**
	 * 工作流相关处理
	 * 
	 * @param out
	 * @throws BusinessException
	 * @throws IOException
	 */
	private void dowfEditPageWork(JspWriter out, ServletRequest request)
			throws BusinessException, IOException {
		Map params = DBHelper.parseParamsSimpleForSql(condition);
		String strInstanceId = (String) params.get("PROCESS_INST_ID");
		String userId = SessionUtils.getAttribute((HttpServletRequest) request,
				"svUserID");
		if (strInstanceId != null) {
			int instanceId = Integer.parseInt(strInstanceId);
			Object wfDataFlag = request
					.getAttribute("workflow.WFDATAXML_WRITE_FLAG");
			if (instanceId > 0 && wfDataFlag == null) {
				String templateId = WFUtil.getTemplateByInsId(strInstanceId);
				WFService wfs = WFFactory.getInstance().getService();
				String wfdataxml = wfs
						.getWfdataByProcessInstId(userId, templateId,
								strInstanceId, (HttpServletRequest) request);
				out.println(wfdataxml);
				request.setAttribute("workflow.WFDATAXML_WRITE_FLAG",
						Boolean.TRUE);
			}
			setWfDesignFuncAndAccessMap((HttpServletRequest) request);
		}
	}

	private void setWfDesignFuncAndAccessMap(HttpServletRequest request)
			throws BusinessException {
		Map funcMap = null;
		Map accessMap = null;
		ActivityDefBean activityDef = null;
		List funcList = new ArrayList();
		WFService wfs = WFFactory.getInstance().getService();
		try {
			Map params = DBHelper.parseParamsSimpleForSql(condition);
			String userId = SessionUtils.getAttribute(request, "svUserID");
			String strInstanceId = (String) params.get("PROCESS_INST_ID");
			if (strInstanceId != null) {
				Integer instanceId = Integer.valueOf(strInstanceId);
				List todoList = wfs.getTodoListByProcessInst(userId,
						instanceId, 1);
				if (null != todoList && todoList.size() > 0) {
					WorkitemBean workBean = (WorkitemBean) todoList
							.get(todoList.size() - 1);
					int nTemplateId = Integer.parseInt(workBean.getTemplateId()
							.toString());
					int nActivityId = Integer.parseInt(workBean.getActivityId()
							.toString());
					activityDef = WFUtil.findActivityDefBean(true, nTemplateId,
							nActivityId);
					this.wfStatus = "1";
				} else {
					List doneList = wfs.getDoneListByUserAndInstance(userId,
							instanceId.intValue(), 1);
					if ((null != doneList && doneList.size() > 0)) {
						ActionMeta am = (ActionMeta) doneList.get(doneList
								.size() - 1);// 只取最后执行的那个
						int nTemplateId = am.getTemplateId();
						int nActivityId = am.getNodeId();
						activityDef = WFUtil.findActivityDefBean(true,
								nTemplateId, nActivityId);
						this.wfStatus = "2";
					}
				}
				if (instanceId.intValue() < 0) {
					this.wfStatus = "0";
				}
				funcMap = WFUtil.getNodeWfDesignFunc(activityDef);
				accessMap = WFUtil.getNodeAccessMap(activityDef);
				request.setAttribute(ToolbarTag.TAG_INTERFACE_FILTER_FUNC_MAP,
						funcMap);
				request.setAttribute(ITag.TAG_INTERFACE_FIELD_PROP_MAP,
						accessMap);
			}
		} catch (Exception ex) {
			throw new BusinessException(ex);
		}
	}

	private void printWfStatus(Writer out) throws IOException {
		if (this.wfStatus != null) {
			StringBuffer buffer = new StringBuffer();
			buffer.append("<script type='text/javascript'>");
			buffer.append("var wf_status=" + this.wfStatus + ";");
			buffer.append("</script>");
			out.write(buffer.toString());
		}
	}
}
