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
	 * 功能的快捷键
	 * 
	 * @param shortCutKey
	 *            快捷键的字母
	 * @uml.property name="shortCutKey"
	 */
	public void setShortCutKey(String shortCutKey) {
		this.shortCutKey = shortCutKey;
	}

	/**
	 * 得到功能的快捷键字母
	 * 
	 * @return
	 * @uml.property name="shortCutKey"
	 */
	public String getShortCutKey() {
		return this.shortCutKey;
	}

	/**
	 * 是否按下Shift键
	 * 
	 * @param isShift
	 *            是否按下Shift键
	 * @uml.property name="isShift"
	 */
	public void setIsShift(String isShift) {
		this.isShift = isShift;
	}

	/**
	 * 是否按下Shift键
	 * 
	 * @return isShift 是否按下Shift键
	 * @uml.property name="isShift"
	 */
	public String getIsShift() {
		return this.isShift;
	}

	/**
	 * 是否按下Ctrl
	 * 
	 * @param isCtrl
	 *            是否按下Ctrl
	 * @uml.property name="isCtrl"
	 */
	public void setIsCtrl(String isCtrl) {
		this.isCtrl = isCtrl;
	}

	/**
	 * 是否按下Ctrl
	 * 
	 * @return 是否按下Ctrl
	 * @uml.property name="isCtrl"
	 */
	public String getIsCtrl() {
		return this.isCtrl;
	}

	/**
	 * 是否按下Alt
	 * 
	 * @param isAlt
	 *            是否按下Alt
	 * @uml.property name="isAlt"
	 */
	public void setIsAlt(String isAlt) {
		this.isAlt = isAlt;
	}

	/**
	 * 是否按下Alt
	 * 
	 * @return 是否按下Alt
	 * @uml.property name="isAlt"
	 */
	public String getIsAlt() {
		return this.isAlt;
	}

	/**
	 * 工作流动作类型
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
	 * 是否绑定提交工作项
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
