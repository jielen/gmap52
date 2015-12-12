package com.anyi.gp.core.action;

import com.anyi.gp.access.PageDataProvider;

public abstract class PageAction extends AjaxAction {

	private static final long serialVersionUID = 1667646539835003458L;

	public static final String SIMPLE_SEARCH = "simpleSearch"; // ������

	public static final String ADVANCED_SEARCH = "advancedSearch"; // �߼�����

	public static final String PAGINATION = "pagination"; // ��ҳ
	
  public static final String ADVANCED_PAGINATION = "advancedPagination";//�߼�������ҳ
  
	protected PageDataProvider provider;

	protected String componame;

	protected String tablename;

	protected String sqlid;

	protected String condition;

	protected String searchCond;

	protected String userid;

	protected String type;//�������ͣ����������߼���������ҳ

	public void setType(String type) {
		this.type = type;
	}

	public void setProvider(PageDataProvider provider) {
		this.provider = provider;
	}

	public void setComponame(String componame) {
		this.componame = componame;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public void setSqlid(String sqlid) {
		this.sqlid = sqlid;
	}

	public void setTablename(String tablename) {
		this.tablename = tablename;
	}

	public String getSearchCond() {
		return searchCond;
	}

	public void setSearchCond(String searchCond) {
		this.searchCond = searchCond;
	}

	public String getCondition() {
		return condition;
	}

	public String getSqlid() {
		return sqlid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

}
