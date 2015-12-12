//$Id: RowManagerSupport.java,v 1.31 2009/04/30 07:16:45 liuxiaoyong Exp $
package com.anyi.gp.persistence;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.sql.DataSource;
import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.anyi.gp.BusinessException;
import com.anyi.gp.Datum;
import com.anyi.gp.TableData;
import com.anyi.gp.access.PageDataProvider;
import com.anyi.gp.context.ApplusContext;
import com.anyi.gp.core.action.PageAction;
import com.anyi.gp.license.LicenseManager;
import com.anyi.gp.license.SystemStatus;
import com.anyi.gp.meta.CompoMeta;
import com.anyi.gp.meta.Field;
import com.anyi.gp.meta.MetaManager;
import com.anyi.gp.meta.TableMeta;
import com.anyi.gp.pub.BoolMessage;
import com.anyi.gp.pub.DAOFactory;
import com.anyi.gp.pub.DBHelper;
import com.anyi.gp.pub.DataTools;
import com.anyi.gp.pub.GeneralFunc;
import com.anyi.gp.pub.NumTool;
import com.anyi.gp.pub.ServiceFacade;
import com.anyi.gp.util.StringTools;
import com.anyi.gp.util.XMLTools;
import com.anyi.gp.workflow.util.WFConst;

public class RowManagerSupport implements RowManager {

	private final Logger log = Logger.getLogger(RowManagerSupport.class);

	private final static String AUTO_NUM_TEXT = "�Զ����";

	private DataSource dataSource = null;

	private String data = "";

	private boolean isdigest = true;

	private Map tableMap = null;

	private Set compoNameSet = null;

	private Document dataDoc = null;

	private String doBusiClassName = "";

	private String doBusiParams = "";

	private String userId = null;

	public RowManagerSupport() {
	}

	public RowManagerSupport(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	/**
	 * ��ʼ��
	 */
	public void init() {
		doBusiClassName = "";
		doBusiParams = "";
		doBusiClassName = "";
		doBusiParams = "";

		dataDoc = XMLTools.stringToDocument(data);
		tableMap = new HashMap();
		compoNameSet = new HashSet();
		NodeList voTableList = null;
		voTableList = XMLTools.selectNodeList(dataDoc.getDocumentElement(),
				"digest//table");

		for (int i = 0; i < voTableList.getLength(); i++) {
			Node voTable = voTableList.item(i);
			String vsTableName = XMLTools.getNodeAttr(voTable, "name", "");
			TableInfo voTableInfo = new TableInfo(dataDoc, vsTableName);
			voTableInfo.init();
			tableMap.put(vsTableName, voTableInfo);
			String compoName = voTableInfo.compoName;
			if (compoName != null && compoName.length() > 0) {// ����TableInfo��ʼ��compoNameSet
				compoNameSet.add(compoName);
			}
		}

		Node voDoBusiNode = null;
		voDoBusiNode = XMLTools.selectSingleNode(dataDoc.getDocumentElement(),
				"dobusinessonsave");
		if (voDoBusiNode != null) {
			doBusiClassName = XMLTools.getNodeAttr(voDoBusiNode, "classname",
					"").trim();
			doBusiParams = XMLTools.textToXml(XMLTools
					.getNodeText(voDoBusiNode));
		}
	}

	/**
	 * ����ǰ��飬��Ҫ���ժҪ��Ϣ
	 */
	public BoolMessage check(String userId) {
		this.userId = userId;

		BoolMessage bm = new BoolMessage();
		bm.setSuccess(false);

		BoolMessage mainTableBM = checkMainTable();
		if (!mainTableBM.isSuccess()) {
			bm.setMessage(mainTableBM.getMessage());
			return bm;
		}

		if (isdigest) {
			BoolMessage digestBM = checkDigest();
			if (!digestBM.isSuccess()) {
				bm.setMessage(digestBM.getMessage());
				return bm;
			}
		}

		bm.setSuccess(true);
		return bm;
	}

	/**
	 * ���ýӿڴ�������ҵ��
	 * 
	 * @return
	 */
	IDoBusinessOnSave newDoBusinessOnSave() {
		if (doBusiClassName == null || doBusiClassName.trim().equals(""))
			return null;

		Class objClass = null;
		Object object = null;

		try {
			objClass = Class.forName(doBusiClassName);
		} catch (ClassNotFoundException e) {
			String msg = "\nRowManager.newDoBusinessOnSave():\n��û�з���;\nclass name: "
					+ doBusiClassName + "\n" + e.getMessage();
			log.debug(msg);
			throw new RuntimeException(e);
		}

		try {
			object = objClass.newInstance();
		} catch (InstantiationException e) {
			String msg = "\nRowManager.newDoBusinessOnSave():\n�������ʵ���쳣;\nclass name: "
					+ doBusiClassName + "\n" + e.getMessage();
			log.debug(msg);
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			String msg = "\nRowManager.newDoBusinessOnSave():\n�Ƿ��������ʵ���쳣;\nclass name: "
					+ doBusiClassName + "\n" + e.getMessage();
			log.debug(msg);
			throw new RuntimeException(e);
		}
		return (IDoBusinessOnSave) object;
	}

	/**
	 * ���ݳ־û����������롢ɾ��������
	 * 
	 * @param userId
	 */
	public BoolMessage save(String userId) throws BusinessException {
		// log.debug("\nRowManager.save():\n" + data);
		BoolMessage bm = new BoolMessage();
		bm.setSuccess(false);
		IDoBusinessOnSave doBusi = null;
		LicenseManager licenseManager = (LicenseManager) ApplusContext
				.getBean("licenseManager");

		if (doBusiClassName != null && !doBusiClassName.trim().equals("")) {
			doBusi = newDoBusinessOnSave();
		}

		if (doBusi != null) {
			doBusi.doBeforeSave(doBusiParams, tableMap);
		}

		Connection conn = null;
		boolean needCheckCompanyCount = false;
		boolean needCheckAccountCount = false;
		try {
			conn = DBHelper.getConnection(dataSource);

			bm.setMessage(autoNum());
			Set voNameSet = tableMap.keySet();

			for (Iterator iter = voNameSet.iterator(); iter.hasNext();) {// �ȴ���delete;
				TableInfo info = (TableInfo) tableMap.get(iter.next());
				if (info.recordList == null)
					continue;
				for (int j = 0; j < info.recordList.getLength(); j++) {
					Node voRecord = info.recordList.item(j);
					String vsAction = XMLTools.getNodeAttr(voRecord, "action",
							"");
					if (ACTION_INSERT.equals(vsAction)) {
					} else if (ACTION_DELETE.equals(vsAction)) {
						deleteData(info, voRecord, conn, userId);
					} else if (ACTION_UPDATE.equals(vsAction)) {
					} else {
						throw new BusinessException("��֧�ֵĶ������͡� action: "
								+ vsAction);
					}
				}
			}

			for (Iterator iter = voNameSet.iterator(); iter.hasNext();) {// �ٴ���insert
																			// update;
				TableInfo info = (TableInfo) tableMap.get(iter.next());
				if (!needCheckCompanyCount)
					needCheckCompanyCount = (info.physicalTable
							.equalsIgnoreCase("MA_COMPANY"));
				if (!needCheckAccountCount)
					needCheckAccountCount = (info.physicalTable
							.equalsIgnoreCase("MA_CO_ACC"));
				if (info.recordList == null)
					continue;
				for (int j = 0; j < info.recordList.getLength(); j++) {
					Node voRecord = info.recordList.item(j);
					String vsAction = XMLTools.getNodeAttr(voRecord, "action",
							"");
					if (ACTION_INSERT.equals(vsAction)) {
						if (!licenseManager.isDemo()) {
							if (needCheckCompanyCount) canInsertCompany(licenseManager);
							if (needCheckAccountCount) canInsertCount(licenseManager);
						}
						bm.setMessage(bm.getMessage()
								+ insertData(info, voRecord, conn, userId));
					} else if (ACTION_DELETE.equals(vsAction)) {
					} else if (ACTION_UPDATE.equals(vsAction)) {
						updateData(info, voRecord, conn);
					} else {
						throw new BusinessException("��֧�ֵĶ������͡� action: "
								+ vsAction);
					}
				}
			}

			bm.setSuccess(true);
			if (doBusi != null) {
				doBusi.doAfterSave(doBusiParams, bm, tableMap);
			}
		} finally {
			DBHelper.closeConnection(conn, null, null);
		}

		return bm;
	}

	/**
	 * �����¼
	 * 
	 * @param tableInfo
	 * @param record
	 * @param stmt
	 * @param userId
	 * @return ���ع������� draft instance id;
	 * @throws BusinessException
	 * @TODO �����������ݲ������ͬһ��connection�У���ʹ��jta����
	 */
	private String insertData(TableInfo tableInfo, Node record,
			Connection conn, String userId) throws BusinessException {
		StringBuffer result = new StringBuffer();
		StringBuffer voBuf = new StringBuffer();

		try {
			Node voNew = XMLTools.selectSingleNode(record, "newvalue");
			if (voNew == null) {
				return "";
			}

			StringBuffer voFieldsBuf = new StringBuffer();
			StringBuffer voValuesBuf = new StringBuffer();
			List params = new ArrayList();

			for (int i = 0; i < voNew.getChildNodes().getLength(); i++) {
				Node voField = voNew.getChildNodes().item(i);
				if (tableInfo.notSaveFieldMap
						.containsKey(voField.getNodeName())) {
					continue;
				}
				if (!tableInfo.fieldNameList.contains(voField.getNodeName()))
					continue;
				if (WFConst.PROCESS_INST_ID_FIELD.equals(voField.getNodeName())) {
					continue;
				}

				voFieldsBuf.append(voField.getNodeName());
				voFieldsBuf.append(", ");
				voValuesBuf.append("?, ");
				params
						.add((castField(tableInfo, voField.getNodeName(),
								StringTools.convertXML(XMLTools
										.getNodeText(voField)))));
			}

			voFieldsBuf = new StringBuffer(voFieldsBuf.substring(0, voFieldsBuf
					.length() - 2));
			voValuesBuf = new StringBuffer(voValuesBuf.substring(0, voValuesBuf
					.length() - 2));

			String compoName = tableInfo.compoName;
			if (compoName != null && compoName.length() > 0) {
				CompoMeta entityMeta = MetaManager.getCompoMeta(compoName);
				String masterTabId = entityMeta.getMasterTable();
				TableMeta tableMeta = entityMeta.getTableMeta();

				if (entityMeta.isCompoSupportWF()) {// ����Ҫ֧��wf
					if (tableMeta.getField(WFConst.PROCESS_INST_ID_FIELD) != null // ���Ҫ�������ֶ�
							&& masterTabId
									.equalsIgnoreCase(tableInfo.physicalTable)) {// ����������
						String titleField = entityMeta.getTitleField();
						String title = "";
						NodeList newvalueNodes = XMLTools.selectSingleNode(
								record, "newvalue").getChildNodes();

						for (int j = 0; j < newvalueNodes.getLength(); j++) {
							if (titleField.equalsIgnoreCase(newvalueNodes.item(
									j).getNodeName())) {
								title = newvalueNodes.item(j).getFirstChild()
										.getNodeValue();
								break;
							}
						}

						ServiceFacade facade = (ServiceFacade) ApplusContext
								.getBean("serviceFacade");
						String instanceId = facade.createDraft(compoName,
								title, masterTabId, userId);
						voFieldsBuf.append(",");
						voFieldsBuf.append(WFConst.PROCESS_INST_ID_FIELD);
						params.add(instanceId);
						voValuesBuf.append(",? ");

						Map wfInstanceMap = new HashMap();// ���빤����ʵ���ŵķ�����Ϣ;
						wfInstanceMap.put(WFConst.PROCESS_INST_ID_FIELD,
								instanceId);
						result.append(makeAutoNumRet(tableInfo, record,
								wfInstanceMap));
					}
				}
			}

			voBuf.append("insert into ");
			voBuf.append(tableInfo.physicalTable);
			voBuf.append("(");
			voBuf.append(voFieldsBuf);
			voBuf.append(") values (");
			voBuf.append(voValuesBuf);
			voBuf.append(")\n");

			log.debug("\nRowManager.insertData():\n" + voBuf.toString());

			DBHelper.executeUpdate(conn, voBuf.toString(), params.toArray());

		} catch (SQLException e) {
			if (DAOFactory.isBusinessException(e)) {
				throw new BusinessException(DAOFactory.translateSQLException(e,
						voBuf.toString()));
			} else {
				log.error(e);
				throw new RuntimeException(e);
			}
		}
		return result.toString();
	}

	/**
	 * ɾ����¼
	 * 
	 * @param tableInfo
	 * @param record
	 * @param conn
	 * @param userId
	 * @throws BusinessException
	 * @@TODO ������������ɾ������ͬһ��connection�У���ʹ��jta����
	 */
	private void deleteData(TableInfo tableInfo, Node record, Connection conn,
			String userId) throws BusinessException {
		Node voOld = XMLTools.selectSingleNode(record, "oldvalue");
		if (voOld == null) {
			throw new RuntimeException("���ݳ����쳣,delete������,oldvalueΪnull.\n"
					+ data);
		}
		List params = new ArrayList();
		String vsWhere = getWhere(tableInfo, voOld, tableInfo.keyFieldMap,
				params);
		if (vsWhere.trim().length() == 0)
			return;

		deleteTable(tableInfo, conn, vsWhere, params);
		deleteInstData(tableInfo, voOld, userId);
	}

	/**
	 * ɾ���������еļ�¼
	 * 
	 * @param tableInfo
	 * @param record
	 * @param userId
	 * @throws BusinessException
	 */
	private void deleteInstData(TableInfo tableInfo, Node record, String userId)
			throws BusinessException {
		String compoName = tableInfo.compoName;
		if (compoName == null || compoName.length() == 0)
			return;

		Node instNode = null;
		CompoMeta entityMeta = MetaManager.getCompoMeta(compoName);
		String masterTabId = entityMeta.getMasterTable();
		TableMeta tableMeta = entityMeta.getTableMeta();

		if (entityMeta.isCompoSupportWF()) {// ����Ҫ֧��wf
			if (tableMeta.getField(WFConst.PROCESS_INST_ID_FIELD) != null // ���Ҫ�������ֶ�
					&& masterTabId.equalsIgnoreCase(tableInfo.physicalTable)) {// ����������
				instNode = XMLTools.selectSingleNode(record,
						WFConst.PROCESS_INST_ID_FIELD);
				if (null == instNode
						|| (null != instNode && null == instNode
								.getFirstChild())) {
					return;// do nothing
				}

				String instId = instNode.getFirstChild().getNodeValue();
				int instanceId = Integer.parseInt(instId);
				ServiceFacade facade = (ServiceFacade) ApplusContext
						.getBean("serviceFacade");
				if (instanceId < -1) {// with draft
					facade.deleteDraft(instId);
				} else if (instanceId > 0) {
					facade.deleteWithWorkflow(instanceId, userId);
				}
			}
		}
	}

	/**
	 * ɾ��ҵ�������
	 * 
	 * @param tableInfo
	 * @param conn
	 * @param where
	 * @param params
	 */
	private void deleteTable(TableInfo tableInfo, Connection conn,
			String where, List params) {
		try {
			if (tableInfo == null)
				return;

			if (tableInfo.childTableNames != null) {
				for (int i = 0; i < tableInfo.childTableNames.size(); i++) {
					String tableName = tableInfo.childTableNames.get(i)
							.toString();
					TableInfo childInfo = (TableInfo) tableMap.get(tableName);
					deleteTable(childInfo, conn, where, params);
				}
			}

			StringBuffer voBuf = new StringBuffer();
			voBuf.append("delete from ");
			voBuf.append(tableInfo.physicalTable);
			voBuf.append(" where ");
			voBuf.append(where);

			DBHelper.executeUpdate(conn, voBuf.toString(), params.toArray());

		} catch (SQLException e) {
			// 1752:ORA-01752: ���ܴ�û��һ����ֵ��������ͼ��ɾ��
			// 1732:ORA-01732: ����ͼ�����ݲ��ݲ����Ƿ�
			String msg = DAOFactory.translateSQLException(e, "");
			if (e.getMessage().indexOf("ORA-01752") >= 0
					|| e.getMessage().indexOf("ORA-01732") >= 0) {
				log.debug(msg);
			} else {
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * ��������
	 * 
	 * @param tableInfo
	 * @param record
	 * @param conn
	 */
	private void updateData(TableInfo tableInfo, Node record, Connection conn) {
		String fldname = "";
		Node voNew = XMLTools.selectSingleNode(record, "newvalue");
		if (voNew == null) {
			throw new RuntimeException("���ݳ����쳣,update������,newvalueΪnull.\n"
					+ data);
		}

		Node voOld = XMLTools.selectSingleNode(record, "oldvalue");
		if (voOld == null) {
			throw new RuntimeException("���ݳ����쳣,update������,oldvalueΪnull.\n"
					+ data);
		}

		List params = new ArrayList();
		StringBuffer voBuf = new StringBuffer();
		voBuf.append("update ");
		voBuf.append(tableInfo.physicalTable);
		voBuf.append(" set \n");

		for (int i = 0; i < voNew.getChildNodes().getLength(); i++) {
			Node voField = voNew.getChildNodes().item(i);
			if ("#text".equals(voField.getNodeName()))
				continue;
			if (tableInfo.notSaveFieldMap.containsKey(voField.getNodeName())) {
				continue;
			}
			if (!tableInfo.fieldNameList.contains(voField.getNodeName()))
				continue;

			fldname = voField.getNodeName();
			voBuf.append(fldname);
			voBuf.append(" = ? , ");

			params.add(castField(tableInfo, voField.getNodeName(), StringTools
					.convertXML(XMLTools.getNodeText(voField))));
		}

		voBuf = new StringBuffer(voBuf.substring(0, voBuf.length() - 2));
		voBuf.append(" where ");
		voBuf.append(getWhere(tableInfo, voOld, tableInfo.keyFieldMap, params));

		log.debug("\nRowManager.updateData():\n" + voBuf.toString());
		try {
			DBHelper.executeUpdate(conn, voBuf.toString(), params.toArray());
		} catch (SQLException e) {
			log.debug(e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * ��ȡwhere����
	 * 
	 * @param tableInfo
	 * @param oldValue
	 * @param keyFieldMap
	 * @param newParams
	 *            �²���
	 * @return where���
	 */
	private String getWhere(TableInfo tableInfo, Node oldValue,
			Map keyFieldMap, List newParams) {
		StringBuffer voBuf = new StringBuffer();
		boolean isFirst = true;

		for (int i = 0; i < oldValue.getChildNodes().getLength(); i++) {
			Node voField = oldValue.getChildNodes().item(i);
			if ("#text".equals(voField.getNodeName()))
				continue;
			if (!keyFieldMap.containsKey(voField.getNodeName())) {
				continue;
			}
			if (!tableInfo.fieldNameList.contains(voField.getNodeName()))
				continue;

			if (!isFirst) {
				voBuf.append(" and ");
			}

			String value = StringTools
					.convertXML(XMLTools.getNodeText(voField));
			voBuf.append(voField.getNodeName());
			voBuf.append(" = ? ");
			newParams.add((castField(tableInfo, voField.getNodeName(), value)));
			isFirst = false;
		}
		return voBuf.toString();
	}

	/**
	 * ���������Ϣ
	 * 
	 * @return
	 */
	private BoolMessage checkMainTable() {
		BoolMessage bm = new BoolMessage();
		bm.setSuccess(false);
		for (Iterator iter = compoNameSet.iterator(); iter.hasNext();) {
			String compoName = (String) iter.next();
			TableInfo mainTable = getMainTableInfo(compoName);
			if (mainTable == null || mainTable.recordList == null
					|| mainTable.recordList.getLength() == 0)
				continue;

			if (mainTable.childTableNames != null
					&& mainTable.childTableNames.size() > 0) {
				if (mainTable.recordList != null
						&& mainTable.recordList.getLength() > 1) {
					bm.setMessage("���ӱ������ֻ�ܱ༭һ������.");
					return bm;
				}
			}
		}
		bm.setSuccess(true);
		return bm;
	}

	/**
	 * ������Ϣ��� ������µ��Ĳ������򲻽���digest��� TODO:
	 */
	private List getCheckDigestCompos() {
		List compos = new ArrayList();
		for (Iterator iter = compoNameSet.iterator(); iter.hasNext();) {
			String compoName = (String) iter.next();
			TableInfo mainTable = getMainTableInfo(compoName);
			if (mainTable == null || mainTable.recordList == null
					|| mainTable.recordList.getLength() == 0)
				continue;

			for (int j = 0; j < mainTable.recordList.getLength(); j++) {
				Node voRecord = mainTable.recordList.item(j);
				String vsAction = XMLTools.getNodeAttr(voRecord, "action", "");
				if (!ACTION_INSERT.equals(vsAction)) {
					compos.add(compoName);
					break;
				}
			}
		}
		return compos;
	}

	/**
	 * ժҪ���
	 * 
	 * @return
	 */
	private BoolMessage checkDigest() {
		BoolMessage bm = new BoolMessage();
		bm.setSuccess(false);
		List checkCompos = getCheckDigestCompos();
		Set voNameSet = tableMap.keySet();
		for (Iterator iter = voNameSet.iterator(); iter.hasNext();) {
			String vsTable = (String) iter.next();
			TableInfo voInfo = (TableInfo) tableMap.get(vsTable);
			if (!checkCompos.contains(voInfo.compoName))
				continue;
			if (!voInfo.isDigest)
				continue;
			if (voInfo.recordList == null || voInfo.recordList.getLength() == 0)
				continue;
			voInfo.currentDigest = getCurDigest(vsTable);
			if (!voInfo.oldDigest.equals(voInfo.currentDigest)) {
				bm.setMessage("����ʹ�õ������ѱ������û����ģ�����ˢ��ҳ�����ݺ����¸��Ĳ����档");
				return bm;
			}
		}
		bm.setSuccess(true);
		return bm;
	}

	/**
	 * ��ȡ��ǰ������ض�sql��ժҪ
	 * 
	 * @param tableName
	 * @return
	 */
	private String getCurDigest(String tableName) {
		if (tableName == null || tableName.trim().equals(""))
			return "";

		TableInfo voInfo = (TableInfo) tableMap.get(tableName);
		if (voInfo == null)
			return "";
		if (!voInfo.isDigest)
			return "";

		PageDataProvider dataProvider = (PageDataProvider) ApplusContext
				.getBean("pageDataProvider");
		Map params = new HashMap();
		DBHelper.parseParamsSimpleForSql(voInfo.condition, params);// ��������
		if (!voInfo.searchType.equalsIgnoreCase(PageAction.ADVANCED_SEARCH)
				&& !voInfo.searchType
						.equalsIgnoreCase(PageAction.ADVANCED_PAGINATION)) {
			DBHelper.parseParamsSimpleForSql(voInfo.searchCond, params);
		}

		// ��ֵ����
		if (userId == null)
			userId = (String) params.get("userid");
		String userNumLimCondition = voInfo.userNumLimCondition;
		// if (voInfo.name.equals(MetaManager.getCompoMeta(voInfo.compoName)
		// .getMasterTable())) {
		// userNumLimCondition =
		// RightUtil.getUserNumLimCondition(ServletActionContext.getRequest(),
		// userId,
		// "fwatch", voInfo.compoName, null, null);
		// }
		if (voInfo.searchType.equalsIgnoreCase(PageAction.ADVANCED_SEARCH)
				|| voInfo.searchType
						.equalsIgnoreCase(PageAction.ADVANCED_PAGINATION)) {// ��������Ϊ�߼�����ʱ�����������������ϣ�
			String conSql = voInfo.searchCond.substring(0, voInfo.searchCond
					.indexOf("/"));
			String keySql = voInfo.searchCond.substring(voInfo.searchCond
					.indexOf("/") + 1);
			DBHelper.parseParamsSimpleForSql(keySql, params);
			if (userNumLimCondition != null && userNumLimCondition.length() > 0)
				userNumLimCondition += " and ";
			userNumLimCondition += conSql;
		}
		dataProvider.setUserNumLimCondition(userNumLimCondition);

		boolean isBlank = false;
		if (voInfo.condition.indexOf("1=0") >= 0)
			isBlank = true;

		Datum datum = null;
		if (voInfo.torow < 0 || voInfo.torow == voInfo.fromrow) {
			datum = dataProvider.getPageData(0, 0, 0, voInfo.name,
					voInfo.sqlid, params, isBlank);
		} else {
			params.put("rownum", voInfo.torow + "");
			params.put("rn", voInfo.fromrow + "");
			datum = dataProvider.getPaginationData(0, 0, 0, voInfo.name,
					voInfo.sqlid, params, isBlank);
		}

		return DataTools.getDigest(datum, voInfo.name);
	}

	/**
	 * �ֶ����͹���
	 * 
	 * @param tableInfo
	 * @param field
	 * @param value
	 * @return
	 */
	private Object castField(TableInfo tableInfo, String field, String value) {
		if (value == null)
			return null;
		else if (tableInfo.dateFieldMap.containsKey(field)) {
			if (value.trim().length() == 0)
				return null;
			return StringTools.toDate(value);
		} else if (tableInfo.datetimeFieldMap.containsKey(field)) {
			if (value.trim().length() == 0)
				return null;
			return StringTools.toTimestamp(value);
		} else if (tableInfo.numFieldMap.containsKey(field)) {
			if ("".equals(value))
				return null;
			else
				return value.replaceAll(",", "");
		} else {
			if (value.trim().length() == 0)
				return "";
			return value;
		}
	}

	/**
	 * ��ȡ�µ�ժҪ
	 */
	public String getNewDigestRet() {
		StringBuffer voBuf = new StringBuffer();
		Set voNameSet = tableMap.keySet();
		for (Iterator iter = voNameSet.iterator(); iter.hasNext();) {
			String vsTable = (String) iter.next();
			TableInfo voInfo = (TableInfo) tableMap.get(vsTable);
			if (voInfo.recordList == null || voInfo.recordList.getLength() == 0) {
				voInfo.currentDigest = voInfo.oldDigest;
				continue;
			}
			voInfo.currentDigest = getCurDigest(vsTable);
		}
		for (Iterator iter = voNameSet.iterator(); iter.hasNext();) {
			String vsTable = (String) iter.next();
			TableInfo voInfo = (TableInfo) tableMap.get(vsTable);
			voBuf.append("<").append(vsTable).append(">");
			voBuf.append(voInfo.currentDigest);
			voBuf.append("</").append(vsTable).append(">\n");
		}
		return voBuf.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.anyi.gp.persistence.IRowManager#setData(java.lang.String)
	 */
	public void setData(String data) {
		this.data = data;
	}

	class TableInfo {

		public String name = "";

		public String physicalTable = "";

		public String compoName = "";

		public boolean isDigest = true;

		public String oldDigest = "";

		public String currentDigest = null;

		public List fieldNameList = null;

		public Map keyFieldMap = null;

		public Map numFieldMap = null;

		public Map notSaveFieldMap = null;

		public Map dateFieldMap = null;

		public Map datetimeFieldMap = null;

		public Map onceAutoNumFieldMap = null;

		public NodeList recordList = null;

		public Node table = null;

		public String parentTableName = null;

		public List childTableNames = null;

		private Document dataDocument = null;

		private String sqlid = null;

		private String condition = null;

		private String searchCond = null;

		private String searchType = null;

		private String userNumLimCondition = null;

		private int fromrow = 0;

		private int torow = 0;

    public List noAutoNumFields;
		public TableInfo(Document doc, String table) {
			dataDocument = doc;
			name = table;
		}

		public void init() {
			table = XMLTools.selectSingleNode(
					dataDocument.getDocumentElement(), "digest//table[@name='"
							+ name + "']");
			physicalTable = XMLTools.getNodeAttr(table, "physicaltable", name);
			if (StringTools.isEmptyString(physicalTable)) {
				physicalTable = name;
			}

			compoName = XMLTools.getNodeAttr(table, "componame", "");
			isDigest = Boolean.valueOf(
					XMLTools.getNodeAttr(table, "isdigest", "true"))
					.booleanValue();
			oldDigest = XMLTools.getNodeAttr(table, "digest", "");

			String vsKeyFields = XMLTools.getNodeAttr(table, "keyfields", "");
			String vsNumFields = XMLTools.getNodeAttr(table, "numericfields",
					"");
			String vsNotSaveFields = XMLTools.getNodeAttr(table,
					"notsavefields", "");
			String vsDateFields = XMLTools.getNodeAttr(table, "datefields", "");
			String vsDatetimeFields = XMLTools.getNodeAttr(table,
					"datetimefields", "");
			String vsOnceAutoNumFields = XMLTools.getNodeAttr(table,
					"onceautonumfields", "");

			sqlid = XMLTools.getNodeAttr(table, "sqlid", "");
			condition = XMLTools.getNodeAttr(table, "condition", "");
			searchCond = XMLTools.getNodeAttr(table, "searchCond", "");
			searchType = XMLTools.getNodeAttr(table, "searchType", "");
			userNumLimCondition = XMLTools.getNodeAttr(table,
					"userNumLimCondition", "");
			fromrow = Integer.parseInt(XMLTools.getNodeAttr(table, "fromrow",
					"0"));
			torow = Integer.parseInt(XMLTools.getNodeAttr(table, "torow", "0"));

			String vsOnceAutoNums = XMLTools.getNodeAttr(table, "onceautonums",
					"");
			keyFieldMap = fieldsToMap(vsKeyFields, null);
			numFieldMap = fieldsToMap(vsNumFields, null);
			notSaveFieldMap = fieldsToMap(vsNotSaveFields, null);
			dateFieldMap = fieldsToMap(vsDateFields, null);
			datetimeFieldMap = fieldsToMap(vsDatetimeFields, null);
			onceAutoNumFieldMap = fieldsToMap(vsOnceAutoNumFields,
					vsOnceAutoNums);
			recordList = XMLTools
					.selectNodeList(dataDocument.getDocumentElement(),
							"table[@name='" + name + "']/record");

      String noAutoNumString = XMLTools.getNodeAttr(table, "noAutoNumFields", "");
      this.noAutoNumFields = Arrays.asList(noAutoNumString.split(","));
			TableMeta tableMeta = MetaManager.getTableMeta(name);
			fieldNameList = tableMeta.getFieldNames();
			childTableNames = tableMeta.getChildTableNames();
			if (tableMeta.getParent() != null)
				parentTableName = tableMeta.getParent().getName();
		}

		private Map fieldsToMap(String fields, String values) {
			Map voMap = new HashMap();
			if (fields == null || fields.trim().equals(""))
				return voMap;
			String[] vasField = StringTools.split2(fields, ",");
			String[] vasValue = null;
			if (values != null && !values.trim().equals("")) {
				vasValue = StringTools.split2(values, ",");
			}
			for (int i = 0; i < vasField.length; i++) {
				if (vasField[i] == null || vasField[i].trim().equals(""))
					continue;
				voMap.put(vasField[i].trim(),
						(vasValue != null && i < vasValue.length) ? vasValue[i]
								: null);
			}
			return voMap;
		}

		public NodeList getAllChilds() {
			try {
				return XPathAPI.selectNodeList(table.getParentNode(),
						"*//table");
			} catch (TransformerException e) {
				log.error(e);
				throw new RuntimeException(e);
			}
		}
	}

	private TableInfo getMainTableInfo(String compoName) {
		String tableName = MetaManager.getCompoMeta(compoName).getMasterTable();
		return (TableInfo) tableMap.get(tableName);
	}

	private TableData newValueToTableData(String tableName, Node newValue) {
		TableData tableData = new TableData(tableName);
		for (int i = 0; i < newValue.getChildNodes().getLength(); i++) {
			Node voField = newValue.getChildNodes().item(i);
			tableData.setField(voField.getNodeName(), StringTools
					.convertXML(XMLTools.getNodeText(voField)));
		}
		return tableData;
	}

	private void synchronizeNewPK(TableInfo mainTable, int index, Map newPK) {
		if (mainTable == null)
			return;
		if (newPK == null)
			return;
		List tableNameList = new ArrayList();
		tableNameList.add(mainTable.name);
		// NodeList voAllChildList = mainTable.getAllChilds();
		// if (voAllChildList != null) {
		// for (int i = 0; i < voAllChildList.getLength(); i++) {
		// Node voTable = voAllChildList.item(i);
		// tableNameList.add(XMLTools.getNodeAttr(voTable, "name", ""));
		// }
		// }
		tableNameList.addAll(MetaManager.getTableMeta(mainTable.name)
				.getAllChildTableNames());

		Set keyFieldSet = newPK.keySet();
		for (Iterator iter = tableNameList.iterator(); iter.hasNext();) {
			TableInfo info = (TableInfo) tableMap.get(iter.next());
			if (info == null || info.recordList == null)
				continue;
			for (int i = 0; i < info.recordList.getLength(); i++) {
				if (i != index && info.name.equals(mainTable.name)) {
					continue;
				}
				Node voRecord = info.recordList.item(i);
				if (voRecord == null)
					continue;
				for (Iterator iterj = keyFieldSet.iterator(); iterj.hasNext();) {
					String vsField = (String) iterj.next();
					Node voNewValueNode = XMLTools.selectSingleNode(voRecord,
							"newvalue");
					Node voField = XMLTools.selectSingleNode(voNewValueNode,
							vsField);
					if (voField != null)
						voNewValueNode.removeChild(voField);
					voField = voRecord.getOwnerDocument()
							.createElement(vsField);
					voNewValueNode.appendChild(voField);
					voField.appendChild(voField.getOwnerDocument()
							.createTextNode((String) newPK.get(vsField)));
				}
			}
		}
	}

	/*
	 * <table name="" rowid="af0001"> <field1>0001</field1> <field2>0001</field2>
	 * </table> <table name="" rowid="af0002"> <field1>0002</field1>
	 * <field2>0002</field2> </table> //
	 */
	private String autoNum() throws BusinessException {
		StringBuffer voBuf = new StringBuffer();
		for (Iterator iter = compoNameSet.iterator(); iter.hasNext();) {
			String compoName = (String) iter.next();
			TableInfo mainTable = getMainTableInfo(compoName);
			CompoMeta entityMeta = MetaManager.getCompoMeta(compoName);
			if (mainTable.recordList == null)
				continue;
			for (int j = 0; j < mainTable.recordList.getLength(); j++) {
				Node voRecord = mainTable.recordList.item(j);
				// ���������,��ζ���µ�,���п�����Ҫ�����Զ����;���򷵻�.
				if (!ACTION_INSERT.equals(XMLTools.getNodeAttr(voRecord,
						"action", ""))) {
					continue;
				}
				Node newValue = null;
				try {
					newValue = XPathAPI.selectSingleNode(voRecord, "newvalue");
				} catch (TransformerException e) {
					log.error(e);
					throw new RuntimeException(e);
				}
				if (newValue == null)
					continue;
				TableData tableData = newValueToTableData(mainTable.name,
						newValue);
				Map newPK = generateValueIfNeeded(mainTable, tableData,
						entityMeta);
				if (newPK != null && newPK.size() > 0) {
					voBuf.append(makeAutoNumRet(mainTable, voRecord, newPK));
					synchronizeNewPK(mainTable, j, newPK);
				}
			}
		}
		return voBuf.toString();
	}

	private String makeAutoNumRet(TableInfo tableInfo, Node record, Map newPK) {
		if (newPK == null)
			return "";
		StringBuffer voBuf = new StringBuffer();
		voBuf.append("<table ");
		voBuf.append("name=\"").append(tableInfo.name).append("\" ");
		voBuf.append("rowid=\"").append(XMLTools.getNodeAttr(record, "rowid"))
				.append("\" ");
		voBuf.append(">\n");

		for (Iterator i = newPK.entrySet().iterator(); i.hasNext();) {
			Entry entry = (Entry) i.next();
			String fieldName = (String) entry.getKey();
			String fieldValue = (String) entry.getValue();
			voBuf.append("<").append(fieldName).append(">");
			voBuf.append(fieldValue);
			voBuf.append("</").append(fieldName).append(">\n");
		}
		voBuf.append("</table>\n");
		return voBuf.toString();
	}

	private Map generateValueIfNeeded(TableInfo tableInfo, TableData tableData,
			CompoMeta entityMeta) {
		Map mapNewPK = new HashMap();
		String newPK = "";
		String noField = entityMeta.getNoField();
		List noRuleFields = entityMeta.getAutoNumFields();
		TableMeta tableMeta = entityMeta.getTableMeta();
		for (Iterator i = tableMeta.getFieldNames().iterator(); i.hasNext();) {
			String fieldName = (String) i.next();
			Field field = tableMeta.getField(fieldName);
			if (null == field || !field.isSave()) {
				continue;
			}
      if(tableInfo.noAutoNumFields == null || tableInfo.noAutoNumFields.contains(fieldName)){
        continue;
      }
			// ��һ�ŵ�����ֻ�����һ�κŵ��Զ�����ֶεĴ���;leidh;20060405;
			if (tableInfo.onceAutoNumFieldMap.containsKey(fieldName)) {
				String num = (String) tableInfo.onceAutoNumFieldMap
						.get(fieldName);
				if (num != null && !num.trim().equals("")
						&& !num.trim().equals(AUTO_NUM_TEXT)) {
					mapNewPK.put(fieldName, num);
					continue;
				}
			}
			if (field.getName().equals(noField)) {
				// ���Զ�����ֶ�
				// TCJLODO: Ӧ��ֻ��һ���Զ����ɵ��ֶ�
				newPK = GeneralFunc.generateNoField(entityMeta.getName(),
						tableData);
				tableData.setField(fieldName, newPK);
				mapNewPK.clear();
				mapNewPK.put(fieldName, newPK);
				break;
			} else if (noRuleFields != null && noRuleFields.contains(fieldName)) {// //(field.isNoRule())
																					// {
				// ���Զ�������ֶ�
				NumTool nt = new NumTool();
				String fieldValue = nt.getNo(entityMeta.getName(), fieldName,
						tableData);
				tableData.setField(fieldName, fieldValue);
				mapNewPK.put(fieldName, fieldValue);
			}
			if (tableInfo.onceAutoNumFieldMap.containsKey(fieldName)) {
				tableInfo.onceAutoNumFieldMap.put(fieldName, mapNewPK
						.get(fieldName));
			}
		}
		return mapNewPK;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.anyi.gp.persistence.IRowManager#isIsdigest()
	 */
	public boolean isIsdigest() {
		return isdigest;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.anyi.gp.persistence.IRowManager#setIsdigest(boolean)
	 */
	public void setIsdigest(boolean isdigest) {
		this.isdigest = isdigest;
	}

	private void canInsertCompany(LicenseManager licenseManager)
			throws BusinessException {
		int sysCompanyCount = new SystemStatus().getCompanyCount();
		int licCompanyCount = licenseManager.getCompanyCount();
		if (licCompanyCount == 0) {
			String count = licenseManager.getCACountFromAnyiserver()[0];
			licCompanyCount = Integer.parseInt(count);
		}
		if (sysCompanyCount >= licCompanyCount)
			throw new BusinessException("���棺���㵥λ(" + sysCompanyCount
					+ ")������Ȩ��ɵ�����������ɾ�����ֻ��㵥λ������ҵ���ܽ��޷�����ʹ�á�");
	}

	private void canInsertCount(LicenseManager licenseManager)
			throws BusinessException {
		int sysAccountCount = new SystemStatus().getAccountCount();
		int licAccountCount = licenseManager.getAccountCount();
		if (licAccountCount == 0) {
			String count = licenseManager.getCACountFromAnyiserver()[1];
			licAccountCount = Integer.parseInt(count);			
		}
		if (sysAccountCount >= licAccountCount)
			throw new BusinessException("���棺����(" + sysAccountCount
					+ ")������Ȩ��ɵ�����������ɾ���������ף�����ҵ���ܽ��޷�����ʹ�á�");
	}
}
