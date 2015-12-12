// $Id: FuncBean.java,v 1.4 2008/03/13 14:44:44 liuxiaoyong Exp $

package com.anyi.gp.bean;


/**
 * @author leidaohong
 */
public class FuncBean {

	private String funcId;

	private String funcDesc;

	private String ordIndex;

	private String isGrantToAll;

	private String shortCutKey;

	private String isShift;

	private String isCtrl;

	private String isAlt;

	private Object wfActionId;

	private boolean isBindCommit;

	/**
	 * @return Returns the funcId.
	 * @uml.property name="funcId"
	 */
	public String getFuncId() {
		return funcId;
	}

	/**
	 * @param funcId
	 *            The funcId to set.
	 * @uml.property name="funcId"
	 */
	public void setFuncId(String funcId) {
		this.funcId = funcId;
	}

	/**
	 * @param funcDesc
	 *            The funcDesc to set.
	 * @uml.property name="funcDesc"
	 */
	public void setFuncDesc(String funcDesc) {
		this.funcDesc = funcDesc;
	}

	/**
	 * @return Returns the funcDesc.
	 * @uml.property name="funcDesc"
	 */
	public String getFuncDesc() {
		return funcDesc;
	}

	/**
	 * @param ordIndex
	 *            The ordIndex to set.
	 * @uml.property name="ordIndex"
	 */
	public void setOrdIndex(String ordIndex) {
		this.ordIndex = ordIndex;
	}

	/**
	 * @return Returns the ordIndex.
	 * @uml.property name="ordIndex"
	 */
	public String getOrdIndex() {
		return ordIndex;
	}

	/**
	 * @param isGrantToAll
	 *            The isGrantToAll to set.
	 * @uml.property name="isGrantToAll"
	 */
	public void setIsGrantToAll(String isGrantToAll) {
		this.isGrantToAll = isGrantToAll;
	}

	/**
	 * @return Returns the isGrantToAll.
	 * @uml.property name="isGrantToAll"
	 */
	public String getIsGrantToAll() {
		return isGrantToAll;
	}

	/**
	 * ���ܵĿ�ݼ�
	 * 
	 * @param shortCutKey
	 *            ��ݼ�����ĸ
	 * @uml.property name="shortCutKey"
	 */
	public void setShortCutKey(String shortCutKey) {
		this.shortCutKey = shortCutKey;
	}

	/**
	 * �õ����ܵĿ�ݼ���ĸ
	 * 
	 * @return
	 * @uml.property name="shortCutKey"
	 */
	public String getShortCutKey() {
		return this.shortCutKey;
	}

	/**
	 * �Ƿ���Shift��
	 * 
	 * @param isShift
	 *            �Ƿ���Shift��
	 * @uml.property name="isShift"
	 */
	public void setIsShift(String isShift) {
		this.isShift = isShift;
	}

	/**
	 * �Ƿ���Shift��
	 * 
	 * @return isShift �Ƿ���Shift��
	 * @uml.property name="isShift"
	 */
	public String getIsShift() {
		return this.isShift;
	}

	/**
	 * �Ƿ���Ctrl
	 * 
	 * @param isCtrl
	 *            �Ƿ���Ctrl
	 * @uml.property name="isCtrl"
	 */
	public void setIsCtrl(String isCtrl) {
		this.isCtrl = isCtrl;
	}

	/**
	 * �Ƿ���Ctrl
	 * 
	 * @return �Ƿ���Ctrl
	 * @uml.property name="isCtrl"
	 */
	public String getIsCtrl() {
		return this.isCtrl;
	}

	/**
	 * �Ƿ���Alt
	 * 
	 * @param isAlt
	 *            �Ƿ���Alt
	 * @uml.property name="isAlt"
	 */
	public void setIsAlt(String isAlt) {
		this.isAlt = isAlt;
	}

	/**
	 * �Ƿ���Alt
	 * 
	 * @return �Ƿ���Alt
	 * @uml.property name="isAlt"
	 */
	public String getIsAlt() {
		return this.isAlt;
	}

	/**
	 * ��������������
	 * 
	 * @uml.property name="wfActionId"
	 */
	public Object getWfActionId() {
		return wfActionId;
	}

	/**
	 * @param wfActionId
	 *            The wfActionId to set.
	 * @uml.property name="wfActionId"
	 */
	public void setWfActionId(Object wfActionId) {
		this.wfActionId = wfActionId;
	}

	/**
	 * �Ƿ���ύ������
	 * 
	 * @uml.property name="isBindCommit"
	 */
	public boolean getIsBindCommit() {
		return isBindCommit;
	}

	public void setIsBindCommit(boolean isBindCommit) {
		this.isBindCommit = isBindCommit;
	}
}
